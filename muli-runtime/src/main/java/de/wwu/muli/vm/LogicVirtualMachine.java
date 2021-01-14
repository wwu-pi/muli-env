package de.wwu.muli.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.bytecode.LCmp;
import de.wwu.muggl.instructions.bytecode.Newarray;
import de.wwu.muggl.instructions.general.CompareFp;
import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Load;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.*;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeLocalVariableTable;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.LocalVariableTable;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.iteratorsearch.LogicIteratorSearchAlgorithm;
import de.wwu.muli.iteratorsearch.NoSearchAlgorithm;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.listener.ExecutionListener;
import de.wwu.muli.listener.NullExecutionListener;
import de.wwu.muli.listener.TcgExecutionListener;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * This concrete class represents a virtual machine for the logic execution of java bytecode. It
 * inherits functionality from the symbolic virtual machine but overrides some of it in order to
 * prevent undesired effects such as the generation of test cases.
 * 
 * @author Jan C. Dageförde
 * @version 1.0.0, 2016-09-09
 */
public class LogicVirtualMachine extends SearchingVM {
	// The Solver Manager.
	private SolverManager			solverManager;

	// Fields for the execution time measured.
	private boolean					measureExecutionTime;
	private long					timeExecutionInstruction;
	private long					timeCoverageChecking;
	private long					timeChoicePointGeneration;
	private long					timeBacktracking;
	private long					timeSolvingChoicePoints;
	private long					timeSolvingBacktracking;
	private long					timeSolutionGeneration;

	// Fields for counting the instructions executed since the last solution was found.
	private int						maximumInstructionsBeforeFindingANewSolution;
	private boolean					onlyCountChoicePointGeneratingInstructions;
	private int						instructionsExecutedSinceLastSolution;

	// Constant.
	private static final long		NANOS_MILLIS	= 1000000;

    /**
     *
     */
    private Stack<TrailElement> currentTrail = new Stack<>();


	// Map search region instantiations (i.e. their iterators) to their respective search strategies (each strategy maintaining its choice points).
	// Actual type: HashMap<SolutionIterator, SearchStrategy>.
    private HashMap<Objectref, LogicIteratorSearchAlgorithm> searchStrategies;

    // At most one search region instantiation, i.e. the corresponding iterator, is active in the VM at the same time. Store which one.
    private Objectref currentSearchRegion = null;
    private long searchStarted;
    private long overallExecutionTime = -1;

    public void startMeasuringOverallTime() {
    	if (overallExecutionTime == -1) {
    		overallExecutionTime = System.nanoTime();
		}
	}

	public long getMeasuredTimeSoFar() {
    	return System.nanoTime() - overallExecutionTime;
	}

	/**
	 * A listener which is invoked before and after instructions are executed and if an exception
	 * is thrown during the execution of the instruction.
	 */
	protected TcgExecutionListener executionListener;

    /**
	 * Special constructor, which sets the search algorithm and initializes the other fields. It is
	 * used to start a "fresh" symbolic virtual machine during execution and start from scratch. The
	 * virtual machine specified is succeeded by it. The solutions found in the earlier execution
	 * and old statistical records are imported. If also the measuring of execution time is enabled,
	 * the old execution times are imported.
	 * 
	 * @param succeededSVM The SymbolicalVirtualMachine that is succeeded by this one.
	 * @throws InitializationException If initialization of auxiliary classes fails.
	 * @throws NullPointerException If succeededSVM is null.
	 */
	public LogicVirtualMachine(LogicVirtualMachine succeededSVM) throws InitializationException {
		this(succeededSVM.getApplication(), succeededSVM.getClassLoader(), succeededSVM
				.getClassFile(), succeededSVM.getInitialMethod());
		
		// Import outcomes of the former execution.
		this.executedFrames = succeededSVM.getExecutedFrames();
		this.executedInstructions = succeededSVM.getExecutedInstructions();
		if (Options.getInst().measureSymbolicExecutionTime) {
			long[] executionTimes = succeededSVM.getNanoExecutionTimeInformation();
			this.timeExecutionInstruction = executionTimes[0];
			this.timeCoverageChecking = executionTimes[2];
			this.timeChoicePointGeneration = executionTimes[3];
			this.timeBacktracking = executionTimes[4];
			this.timeSolvingChoicePoints = executionTimes[5];
			this.timeSolvingBacktracking = executionTimes[6];
			this.timeSolutionGeneration = executionTimes[7];
		}
		this.searchStrategies = succeededSVM.searchStrategies;
		this.currentSearchRegion = succeededSVM.currentSearchRegion;
	}

	/**
	 * Main constructor, which initializes the additional fields. It has to be invoked by any public
	 * constructor.
	 * 
	 * @param application The application this virtual machine is used by.
	 * @param classLoader The main ClassLoader to use.
	 * @param classFile The classFile to start execution with.
	 * @param initialMethod The Method to start execution with. This Method has to be a method of
	 *        the supplied classFile.
	 * @throws InitializationException If initialization of auxiliary classes fails.
	 */
	public LogicVirtualMachine(Application application, MugglClassLoader classLoader,
			ClassFile classFile, Method initialMethod)
			throws InitializationException {
		super(application, classLoader, classFile, initialMethod);
		Options options = Options.getInst();
		try {
			this.solverManager = (SolverManager) Class.forName(options.solverManager).newInstance();
		} catch (InstantiationException e) {
			throw new InitializationException("Solver manager of class " + options.solverManager + " cannot be instantiated.");
		} catch (IllegalAccessException e) {
			throw new InitializationException("Solver manager of class " + options.solverManager + " cannot be accessed.");
		} catch (ClassNotFoundException e) {
			throw new InitializationException("Solver manager of class " + options.solverManager + " does not exist.");
		}
		this.stack = new StackToTrailWithInverse(true, this);
		this.measureExecutionTime = options.measureSymbolicExecutionTime;
		this.timeExecutionInstruction = 0;
		this.timeCoverageChecking = 0;
		this.timeChoicePointGeneration = 0;
		this.timeBacktracking = 0;
		this.timeSolvingChoicePoints = 0;
		this.timeSolvingBacktracking = 0;
		this.timeSolutionGeneration = 0;
		this.instructionsExecutedSinceLastSolution = 0;
		this.maximumInstructionsBeforeFindingANewSolution = options.maxInstrBeforeFindingANewSolution;
		this.onlyCountChoicePointGeneratingInstructions = options.onlyCountChoicePointGeneratingInst;
		this.searchStrategies = new HashMap<>();
		this.executionListener = new TcgExecutionListener();
	}

	public ExecutionListener getExecutionListener() {
		return executionListener;
	}

    public Object labelSolutionObject(Object solutionObject) {
        // Label found solution.
        de.wwu.muggl.solvers.Solution solution;
        try {
            solution = this.getSolverManager().getSolution();
            if (solutionObject instanceof Objectref) {
                Objectref solutionObject2 = this.getAnObjectref(((Objectref) solutionObject).getInitializedClass().getClassFile());
                HashMap<Field, Object> fields = ((Objectref) solutionObject).getFields();
                HashMap<Field, Object> fields2 = ((Objectref) solutionObject2).getFields();
                fields.entrySet().forEach((entry) -> {
                    if (entry.getValue() instanceof Term) {
                        Term value = (Term) entry.getValue();
                        Term simplified = value.insert(solution, false);
                        Object newValue = simplified;
                        if (simplified.isConstant()) {
                            newValue = ((NumericConstant) simplified).getIntValue();
                        }
                        fields2.put(entry.getKey(), newValue);
                    } else {
                        fields2.put(entry.getKey(), entry.getValue());
                    }
                });
                solutionObject = solutionObject2;
            } else if (solutionObject instanceof Arrayref) {

                Arrayref ar = (Arrayref) solutionObject;
                Object[] elements = ar.getRawElements();
                Arrayref result = new Arrayref(ar);
                boolean substitutedPrimitives = false;
                boolean simplificationOfTermsSuccessful = true;
                for (int i = 0; i < elements.length; i++) {
                    Object newValue = elements[i];
                    if (elements[i] instanceof Term) {
                        Term value = (Term) elements[i];
                        Term simplified = value.insert(solution, false);

                        if (simplified.isConstant()) {
                        	substitutedPrimitives = true;
                            newValue = ((NumericConstant) simplified).getIntValue();
                        } else {
                        	simplificationOfTermsSuccessful = false;
                            newValue = simplified;
                        }
                    }
                    result.putElement(i, newValue);
                }
                replaceReferenceValueOfArray(result, substitutedPrimitives, simplificationOfTermsSuccessful);
                solutionObject = result;
            }
        } catch (TimeoutException | SolverUnableToDecideException e) {
            throw new RuntimeException(e);
        }

        return solutionObject;
    }

    protected void replaceReferenceValueOfArray(Arrayref result,
												boolean substitutedPrimitives,
												boolean simplificationOfTermsSuccessful) {
		if (substitutedPrimitives) {
			if (!simplificationOfTermsSuccessful) {
				throw new RuntimeException("Simplification failed.");
			}
			try {
				InitializedClass newClass =
						new InitializedClass(getClassLoader().getClassAsClassFile("java.lang.Integer"), this);
				Objectref newReferenceValue = new Objectref(newClass, true);
				result.setReferenceValue(newReferenceValue);
			} catch (ClassFileException e) {
				throw new RuntimeException(e);
			}
		}
	}

    /**
	 * The main loop on the SymbolicalVirtualMachine executes the VirtualMachines main loop; it then
	 * saves the found solutions, checks weather a tracking back is possible and continues from that
	 * point on, again using the normal VirtualMachines main loop. It keeps doing so, until tracking
	 * back is no longer possible.
	 * 
	 * @param visualStartingFrame If step by step execution is enabled, there will be no stepping
	 *        until this frame has been reached.
	 * @throws ExecutionException An ExecutionExeption is thrown on any fatal errors during
	 *         execution.
	 * @throws InterruptedException Thrown to signal the manual end of the step by step execution.
	 * @throws InvalidInstructionInitialisationException Any fatal problems with the parsing and the
	 *         initialization will lead to this exception.
	 * @throws OutOfMemoryError If the virtual machine runs out for memory, this error is thrown.
	 * @throws StackOverflowError If the operand stack of a frame or the virtual machine stack
	 *         exceeds its element limit, this Error is thrown.
	 */
	@Override
	protected void runMainLoop(Frame visualStartingFrame) throws ExecutionException,
			InterruptedException, InvalidInstructionInitialisationException {
		boolean firstRun = true;

		try {
			// Run the program.
			if (firstRun) {
				super.runMainLoop(visualStartingFrame);
				firstRun = false;
			} else {
				super.runMainLoop(this.currentFrame);
			}
		} catch (NoExceptionHandlerFoundException e) {
			/*
			 * The only exception reaching this point and not halting the virtual machine is the
			 * NoExceptionHandlerFoundException. It indicates that an Exception was thrown by
			 * the executed program which would stop the execution of it. At this point is means
			 * that a set of parameters was found that will result in the application throwing
			 * an uncaught Exception. This forms a solution!
			 */
			this.threwAnUncaughtException = true;
			// The return object is used to store the NoExceptionHandlerFoundException
			// containing the uncaught throwable as there is no returned value anyway.
			this.returnedObject = e;
		} catch (InterruptedException e) {
			// Mark that the actual execution has finished.
			this.application.executionHasFinished();

			// Logging.
            Globals.getInst().symbolicExecLogger.info("The virtual machine was halted with an InterruptionException. " + e);

			// Rethrow.
			throw e;
		}
	}

	/**
	 * This concrete method executes the given instruction symbolically.
	 * 
	 * @param instruction The instruction that is to be executed.
	 * @throws ExecutionException Thrown on any fatal errors during symbolic execution.
	 */
	@Override
	protected void executeInstruction(Instruction instruction) throws ExecutionException {
        Optional<ST> st = instruction.executeMuli(this, this.currentFrame);
        afterExecuteInstruction(instruction, this.currentFrame, this.pc);

        if (!st.isPresent()) {
            return;
        }

        if (!Options.getInst().symbolicMode) {
            throw new IllegalStateException("Tried non-deterministic branching outside of encapsulated search.");
        }

        ST result = st.get();
        if (result instanceof Choice) {
            this.getSearchAlgorithm().recordChoice((Choice)result);
            this.getSearchAlgorithm().takeNextDecision(this);
        } else if (result instanceof Fail) {
            this.getSearchAlgorithm().recordFail((Fail) result);
            this.getSearchAlgorithm().takeNextDecision(this); // TODO Might need additional backtracking?! Check interface.
        } else {
            throw new IllegalStateException("Instruction " + instruction + " returned an unsupported result type: " + result);
        }
    }

    public void reachedEndEvent() {
		executionListener.reachedEndEvent();
	}

	protected Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
		return executionListener.beforeExecuteInstruction(instruction, method, frame);
	}

	protected void afterExecuteInstruction(Instruction instruction, Frame frame, int pc) {
		executionListener.afterExecuteInstruction(instruction, frame, pc);
	}

	protected void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, ExecutionException e) {
		executionListener.treatExceptionDuringInstruction(instruction, method, frame, e);
	}

    /**
     * SolutionIterator recorded and returned a solution, so reset the counter.
     */
    public void resetInstructionsExecutedSinceLastSolution() {
        // Reset the instruction-between-solutions counter.
        this.instructionsExecutedSinceLastSolution = 0;
    }

	/**
	 * Create a new frame.
     * In contrast to earlier versions (Muli < 1.8), do NOT check whether its local variables should be initialized to logic
	 * variables. Instead of the previous implicit approach, which was insufficient e. g. for variables that are scoped to a loop,
     * the compiler produces explicit bytecode when initialization is required.
	 * 
	 * @param invokedBy The frame this frame is invoked by. Might be null.
	 * @param method The Method that this frame holds.
	 * @param arguments The arguments that will be stored in the local variables prior to execution.
	 * @return The new frame.
	 * @throws ExecutionException On any fatal error that happens during execution and is not coped
	 *         by one of the other Exceptions.
	 */
	@Override
	protected Frame createFrame(Frame invokedBy, Method method, Object[] arguments) throws ExecutionException {
		LogicFrame frame = new LogicFrame(invokedBy, this, method, method.getClassFile()
				.getConstantPool(), arguments);
		frame.setOperandStack(new StackToTrailWithInverse(false, this));

		// Return it.
		return frame;
	}

    public void storeRepresentationForFreeVariable(Frame frame, int freeVariableIndex) {
        AttributeLocalVariableTable localVariablesTableAttribute = null;
        for (Attribute attribute : frame.getMethod().getCodeAttribute().getAttributes()) {
            if (attribute.getStructureName().equals("attribute_local_variable_table")) {
                localVariablesTableAttribute = (AttributeLocalVariableTable)attribute;
                break;
            }
        }

        // Note to future selves: "Parameters" are not "Variables" are not "Local Variables". Or sometimes they are.
        // Anyway, do NOT trust method names of frame.method! Instead, we better determine the type ourselves.
        String type = null;
        String name = null;
        assert localVariablesTableAttribute != null;
        for (LocalVariableTable localVariable : localVariablesTableAttribute.getLocalVariableTable())
        {
            if (localVariable.getIndex() != freeVariableIndex) {
                continue;
            }
            name = localVariable.getName();
            type = localVariable.getClassFile().getConstantPool()[localVariable.getDescriptorIndex()].getStringValue();
            break; // Do not look any further.

        }
        if (type == null) {
            throw new IllegalStateException("Trying to create a free variable for a non-existing variable! Aborting.");
        }
        Object freeVariableRepresentation = FreeObjectrefInitialisers.createRepresentationForFreeVariableOrField(this, frame.getMethod().getClassFile(), type, name);

        // Put logic variable into field.
        frame.setLocalVariable(freeVariableIndex, freeVariableRepresentation);
    }

    /**
	 * Generate a new choice point.
	 * 
	 * @param instruction The instruction that wants to generate the choice points.
	 * @param constraintExpression The ConstraintExpression describing the choice a if it is
	 *        conditional jump Instruction. May be null.

	 * @throws SymbolicExecutionException If the instruction supplied is no conditional jump, no
	 *         load instruction or if an Exception is thrown during the choice point generation.
	 */
	public void generateNewChoicePoint(GeneralInstructionWithOtherBytes instruction,
			ConstraintExpression constraintExpression)
			throws SymbolicExecutionException {
        if (this.getSearchAlgorithm() == null) {
            throw new IllegalStateException("unexpected state: Trying to add a choicepoint, but no search algorithm initialised.");
        }

		// Counting the instructions before a new solution is found?
		if (this.maximumInstructionsBeforeFindingANewSolution != -1) {
			if (this.onlyCountChoicePointGeneratingInstructions)
				this.instructionsExecutedSinceLastSolution++;
		}

		// Check if it is a suitable instruction.
		if (instruction instanceof JumpConditional) { // Conditional jump found.
			this.getSearchAlgorithm().generateNewChoicePoint(this, instruction, constraintExpression);
		} else {
			throw new SymbolicExecutionException(
					"Only conditional jump instructions might attempt to generate a choice point using this method.");
		}
	}
	
	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction The instruction that wants to generate the choice points.
	 * @param generator A variable Generator. May be null to indicate no custom variable generator
	 *        is used.
	 * @param type A String representation of the type.
	 * @throws SymbolicExecutionException If the instruction supplied is no conditional jump, no
	 *         load instruction or if an Exception is thrown during the choice point generation.
	 */
	public void generateNewChoicePoint(GeneralInstructionWithOtherBytes instruction,
			Generator generator, String type)
			throws ExecutionException {
        if (this.getSearchAlgorithm() == null) {
            throw new IllegalStateException("unexpected state: Trying to add a choicepoint, but no search algorithm initialised.");
        }

		// Counting the instructions before a new solution is found?
		if (this.maximumInstructionsBeforeFindingANewSolution != -1) {
			if (this.onlyCountChoicePointGeneratingInstructions)
				this.instructionsExecutedSinceLastSolution++;
		}

		// Check if it is a suitable instruction.
		if (instruction instanceof Load) {
			try {
				this.getSearchAlgorithm().generateNewChoicePoint(this, ((Load) instruction)
						.getLocalVariableIndex(), generator);
			} catch (ConversionException e) {
				throw new SymbolicExecutionException(
						"An object provided by a generator required conversion to Muggl, but conversion failed: "
								+ e.getClass().getName() + " (" + e.getMessage() + ")");
			}
		} else if (instruction instanceof Newarray) {
			this.getSearchAlgorithm().generateNewChoicePoint(this, type);
		} else {
			throw new SymbolicExecutionException(
					"Only loading instructions or newarray might attempt to generate a choice point using this method.");
		}
	}

	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction A lcmp instruction.
	 * @param leftTerm The term of long variables and constants of the left hand side of the
	 *        comparison.
	 * @param rightTerm The term of long variables and constants of the right hand side of the
	 *        comparison.
	 * @throws ExecutionException If an Exception is thrown during the choice point
	 *         generation.
	 */
	public void generateNewChoicePoint(LCmp instruction, Term leftTerm, Term rightTerm)
			throws ExecutionException {
        if (this.getSearchAlgorithm() == null) {
            throw new IllegalStateException("unexpected state: Trying to add a choicepoint, but no search algorithm initialised.");
        }

		// Counting the instructions before a new solution is found?
		if (this.maximumInstructionsBeforeFindingANewSolution != -1) {
			if (this.onlyCountChoicePointGeneratingInstructions)
				this.instructionsExecutedSinceLastSolution++;
		}

		// Create the choice point.
		this.getSearchAlgorithm().generateNewChoicePoint(this, instruction, leftTerm, rightTerm);
	}

	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction A CompareFp instruction.
	 * @param less If set to true, the choice point will have the behaviour of dcmpl / fcmpl;
	 *        otherwise, it will behave like dcmpg / fcmpg.
	 * @param leftTerm The term of long variables and constants of the left hand side of the
	 *        comparison.
	 * @param rightTerm The term of long variables and constants of the right hand side of the
	 *        comparison.
	 * @throws ExecutionException If an Exception is thrown during the choice point
	 *         generation.
	 */
	public void generateNewChoicePoint(CompareFp instruction, boolean less, Term leftTerm,
			Term rightTerm) throws ExecutionException {
        if (this.getSearchAlgorithm() == null) {
            throw new IllegalStateException("unexpected state: Trying to add a choicepoint, but no search algorithm initialised.");
        }

		// Counting the instructions before a new solution is found?
		if (this.maximumInstructionsBeforeFindingANewSolution != -1) {
			if (this.onlyCountChoicePointGeneratingInstructions)
				this.instructionsExecutedSinceLastSolution++;
		}

		// Create the choice point.
		this.getSearchAlgorithm().generateNewChoicePoint(this, instruction, less, leftTerm, rightTerm);
	}

	/**
	 * Generate a new choice point.
	 * 
	 * @param instruction The Instruction generating the ChoicePoint.
	 * @param termFromStack The term term that was on top of the stack. Using the non symbolic
	 *        execution, this would be the key for the switch.
	 * @param keys The possible keys.
	 * @param pcs The possible jump targets.
	 * @param low The "low" boundary of the tableswitch instruction; or null, if the choice point is
	 *        generated for a lookupswitch instruction.
	 * @param high The "high" boundary of the tableswitch instruction; or null, if the choice point
	 *        is generated for a lookupswitch instruction.
	 * @throws IllegalArgumentException If the number of keys is not equal to the number of jump
	 *         targets or if there are no choices at all.
	 * @throws NullPointerException If either of the specified arrays is null, or if the instruction
	 *         is tableswitch and at least one of the boundaries is null.
	 * @throws ExecutionException If an Exception is thrown during the choice point
	 *         generation.
	 */
	public void generateNewChoicePoint(Switch instruction, Term termFromStack, IntConstant[] keys,
			int[] pcs, IntConstant low, IntConstant high) throws ExecutionException {
	    if (this.getSearchAlgorithm() == null) {
	        throw new IllegalStateException("unexpected state: Trying to add a choicepoint, but no search algorithm initialised.");
        }
		// Counting the instructions before a new solution is found?
		if (this.maximumInstructionsBeforeFindingANewSolution != -1) {
			if (this.onlyCountChoicePointGeneratingInstructions)
				this.instructionsExecutedSinceLastSolution++;
		}

		// Create the choice point.
		this.getSearchAlgorithm().generateNewChoicePoint(this, instruction, termFromStack, keys, pcs,
				low, high);
	}

    /**
     * Generate an instance of Objectref for the specified ClassFile and check whether its fields
     * should be initialized to logic variables.
     *
     * @param classFile The class file to get an object reference for.
     * @return A new instance of objectref for this ClassFile.
     * @throws ExceptionInInitializerError If class initialization fails.
     */
    @Override
    public FreeObjectref getAFreeObjectref(ClassFile classFile) {
        // Get and check the initialized class.
        InitializedClass initializedClass = classFile.getTheInitializedClass(this);

        // Get the object reference.
        FreeObjectref objectref = initializedClass.getANewFreeObject();

        // Check which fields are annotated and replace undefined fields by logic variables.
        for (Field field : classFile.getFields()) {
            for (Attribute attribute : field.getAttributes()) {
                if (!attribute.getStructureName().equals("attribute_free_field")) {
                    continue;
                }
                if (!objectref.hasValueFor(field)) {
                    String type = field.getDescriptor();
                    Object representation = FreeObjectrefInitialisers.createRepresentationForFreeVariableOrField(this, classFile, type, field.getName());
                    objectref.putField(field, representation);
                }
                break;

            }
        }

        // Return the object reference.
        return objectref;
    }

	@Override
	public Objectref getAPrimitiveWrapperObjectref(ClassFile classFile) throws PrimitiveWrappingImpossibleException {
		return classFile.getAPrimitiveWrapperObjectref(this);
	}
	
	/**
	 * Generate an instance of Objectref for the specified ClassFile and check whether its fields
	 * should be initialized to logic variables.
	 * 
	 * @param classFile The class file to get an object reference for.
	 * @return A new instance of objectref for this ClassFile.
	 * @throws ExceptionInInitializerError If class initialization fails.
	 */
	@Override
	public Objectref getAnObjectref(ClassFile classFile) {
		// Get and check the initialized class.
		InitializedClass initializedClass = classFile.getTheInitializedClass(this);
		
		// Get the object reference.
		Objectref objectref = initializedClass.getANewInstance();

		// Check which fields are annotated and replace undefined fields by logic variables.
		for (Field field : classFile.getFields()) {
			for (Attribute attribute : field.getAttributes()) {
				if (!attribute.getStructureName().equals("attribute_free_field")) {
					continue;
				}
				if (!objectref.hasValueFor(field)) {
					String type = field.getDescriptor();
                    Object representation = FreeObjectrefInitialisers.createRepresentationForFreeVariableOrField(this, classFile, type, field.getName());
                    objectref.putField(field, representation);
				}
				break;
				
			}
		}
		
		// Return the object reference.
		return objectref;
	}
	
	/**
	 * Sets nextFrameIsAlreadyLoaded to true.
     * @param nextFrameIsAlreadyLoaded Whether `currentFrame` is ready for execution right away.
     */
	public void setNextFrameIsAlreadyLoaded(boolean nextFrameIsAlreadyLoaded) {
		this.nextFrameIsAlreadyLoaded = nextFrameIsAlreadyLoaded;
	}

	/**
	 * Make sure the virtual machine will not continue execution.
	 */
	public void abortExecution() {
		this.returnFromCurrentExecution = true;
		this.nextFrameIsAlreadyLoaded = false;
		this.stack.clear();
	}

	/**
	 * Interrupt the execution.
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		if (!isInterrupted()) {
			// Invoke the interruption implementation of java.lang.Thread.
			super.interrupt();
		}
	}

	/**
	 * Getter for the SolverManager.
	 * 
	 * @return The SolverManager of this SymbolicalVirtualMachine.
	 */
	public SolverManager getSolverManager() {
		return this.solverManager;
	}

	/**
	 * Finalize the SymbolicalVirtualMachine.
	 */
	@Override
	public void finalize() {
		try {
			this.finalized = true;
			this.solverManager.finalize();
		} catch (Throwable t) {
			// Log it, but do nothing.
            Globals.getInst().symbolicExecLogger.warn("Shutting down the SolverManager failed.");
		} finally {
			super.finalize();
		}
		reachedEndEvent();
	}

	/**
	 * Return an array of long elements with the times of the execution measurement. There will be
	 * no preprocessing of the elements, hence it is only meant for internal (private) use.
	 * 
	 * @return An array of long elements with the times of the execution measurement.
	 */
	private long[] getNanoExecutionTimeInformation() {
		return new long[]{ this.timeExecutionInstruction, 0,
				this.timeCoverageChecking, this.timeChoicePointGeneration,
				this.timeSolvingChoicePoints, this.timeSolvingBacktracking, this.timeBacktracking,
				this.timeSolutionGeneration };
	}

	/**
	 * Increase the time spent on choice point generation by the supplied increment. Lower the
	 * instruction execution time by that value.
	 * 
	 * @param increment The time needed for a choice point generation.
	 */
	@Override
	public void increaseTimeChoicePointGeneration(long increment) {
		this.timeChoicePointGeneration += increment;
	}

	/**
	 * Increase the time spent on solving for the generation of choice points by the supplied
	 * increment.
	 * 
	 * @param increment The time needed for a solving action.
	 */
	@Override
	public void increaseTimeSolvingForChoicePointGeneration(long increment) {
		this.timeSolvingChoicePoints += increment;
	}

	/**
	 * Change the current frame and put that information onto the trail, if there is any.
	 * 
	 * @param frame The frame to become the current frame.
	 */
	@Override
	public void changeCurrentFrame(Frame frame) {
		// Create a FrameChange trail element (conditionally, see method).
		this.addToTrail(new FrameChange(this.currentFrame));

		// Invoke the super implementation to change the frame.
		super.changeCurrentFrame(frame);
	}

	/**
	 * Change the pc at the current Frame and put information about the previous pc onto the
	 * trail, if there is any.
	 *
	 * @param pc the next pc
	 */
	@Override
	public void changeCurrentPC(int pc) {
		// Create a PCChange trail element (conditionally, see method).
        this.addToTrail(new PCChange(this.pc));

		super.setPC(pc);
	}

    public void setSearchStrategy(Objectref iterator, LogicIteratorSearchAlgorithm searchStrategy) {
        this.searchStrategies.put(iterator, searchStrategy);
    }

    public LogicIteratorSearchAlgorithm getSearchStrategyForIterator(Objectref iterator) {
        return searchStrategies.get(iterator);
    }

    /**
     * Get the search algorithm corresponding to the currently active search region.
     *
     * @return The SearchAlgorithm, or null if there is no current search algorithm (i.e. no active search region).
     */
    public LogicIteratorSearchAlgorithm getSearchAlgorithm() {
        if (this.currentSearchRegion == null) {
            return NoSearchAlgorithm.getInstance();
        }
        return this.getSearchStrategyForIterator(this.currentSearchRegion);
    }
    /**
     * Get the current choicepoint, i.e. the most recent choicepoint in the currently active search region.
     * @return
     */
    @Deprecated
    public ChoicePoint getCurrentChoicePoint() {
        LogicIteratorSearchAlgorithm algorithm = searchStrategies.get(currentSearchRegion);
        if (algorithm == null) {
            return null;
        }
        return algorithm.getCurrentChoicePoint();
    }

    /**
     * Get the current choicepoint, i.e. the most recent choicepoint in the currently active search region.
     * @return
     */
    public Choice getCurrentChoice() {
        LogicIteratorSearchAlgorithm algorithm = searchStrategies.get(currentSearchRegion);
        if (algorithm == null) {
            return null;
        }
        return algorithm.getCurrentChoice();
    }

    /**
     * Obtain current trail and reset it for further execution
     * @return Trail stack
     */
    @Override
    public Stack<TrailElement> extractCurrentTrail() {
        Stack<TrailElement> trail = this.currentTrail;
        this.currentTrail = new Stack<>();
        return trail;
    }

    @Override
    public void addToTrail(TrailElement element) {
        if (isInSearch()) {
            this.currentTrail.push(element);
        }
    }

    @Override
    public boolean isInSearch() {
        return (
                !(this.getSearchAlgorithm() instanceof NoSearchAlgorithm)
                && this.getSearchAlgorithm().isActivelySearching()
        );
    }

    /**
     * Store a field value for use by the seach algorithm's tracking back
     * functionality.
     * @param valueRepresentation Either a InstanceFieldPut or a StaticfieldPut object.
     */
    @Override
    public void saveFieldValue(FieldPut valueRepresentation) {
        addToTrail(valueRepresentation);
    }

    /**
     * Store a local varable value for use by the seach algorithm's tracking back
     * functionality.
     * @param valueRepresentation A Restore object.
     */
    @Override
    public void saveLocalVariableValue(Restore valueRepresentation) {
        this.addToTrail(valueRepresentation);
    }

    /**
     * Store a array value for use by the seach algorithm's tracking back
     * functionality.
     * @param valueRepresentation An ArrayRestore object.
     */
    @Override
    public void saveArrayValue(ArrayRestore valueRepresentation) {
        this.addToTrail(valueRepresentation);
    }

    public Objectref getCurrentSearchRegion() {
        return currentSearchRegion;
    }

    public void setCurrentSearchRegion(Objectref currentSearchRegion) {
        this.currentSearchRegion = currentSearchRegion;
        this.executionListener.setDefUseListener(this);
    }

    public void recordSearchStarted() {
        this.searchStarted = System.nanoTime();
    }

    public long recordSearchEnded() {
        return System.nanoTime() - this.searchStarted;
        //System.out.println("Time spent searching: " + searchTime + " ns.");
    }

    public List<ST> getAllSearchTreesDebug() {
        return this.searchStrategies.values().stream().map(LogicIteratorSearchAlgorithm::getSearchTreeDebug).collect(Collectors.toList());
    }
}
