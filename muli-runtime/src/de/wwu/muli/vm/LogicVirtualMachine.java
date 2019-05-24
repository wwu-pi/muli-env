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
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.*;
import de.wwu.muggl.symbolic.structures.Loop;
import de.wwu.muggl.vm.Application;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.Limitations;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeFreeVariables;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeLocalVariableTable;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.FreeVariable;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.LocalVariableTable;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.iteratorsearch.LogicIteratorSearchAlgorithm;
import de.wwu.muli.iteratorsearch.NoSearchAlgorithm;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.solution.ExceptionSolution;
import de.wwu.muli.solution.Solution;
import org.apache.log4j.Level;

import java.util.*;

/**
 * This concrete class represents a virtual machine for the logic execution of java bytecode. It
 * inherits functionality from the symbolic virtual machine but overrides some of it in order to
 * prevent undesired effects such as the generation of test cases.
 * 
 * @author Jan C. Dageförde
 * @version 1.0.0, 2016-09-09
 */
public class LogicVirtualMachine extends VirtualMachine implements SearchingVM {
	// The Solver Manager.
	private SolverManager			solverManager;

	// Solution related fields.
	private ArrayList<Solution> solutions;

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

	/*
	 * Fields to indicate if and if yes, why the execution did not finish without matching an
	 * abortion criterion.
	 */
	private boolean					abortionCriterionMatched;
	private String					abortionCriterionMatchedMessage;
	private boolean					maximumLoopsReached;

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
		this.solutions = succeededSVM.solutions;
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
		this.solutions = new ArrayList<>();
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
		this.abortionCriterionMatched = false;
		this.maximumLoopsReached = false;
		this.abortionCriterionMatchedMessage = null;
		this.searchStrategies = new HashMap<>();
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
			if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.INFO))
				Globals.getInst().symbolicExecLogger
						.info("The virtual machine was halted with an InterruptionException. "
								+ e);


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
        } else {
            throw new IllegalStateException("Instruction " + instruction + " returned an unsupported result type: " + result);
        }
    }

    /**
	 * Since execution reached a backtracking point, store the found solution for later retrieval.
	 */
	public void saveSolutionObject(Object solution) {
        // TODO maybe remove from VM.
		// Reset the instruction-between-solutions counter.
		this.instructionsExecutedSinceLastSolution = 0;

		// Add the solution.
		this.solutions.add(new Solution(solution));
	}

	/**
	 * Since execution reached a backtracking point, store the found exception for later retrieval.
	 */
	public void saveSolutionException(Object solution) {
        // TODO maybe remove from VM.
		// Reset the instruction-between-solutions counter.
		this.instructionsExecutedSinceLastSolution = 0;

		// Add the solution.
		this.solutions.add(new ExceptionSolution(solution));
	}

    /**
     * SolutionIterator recorded and returned a solution, so reset the counter.
     */
    public void resetInstructionsExecutedSinceLastSolution() {
        // Reset the instruction-between-solutions counter.
        this.instructionsExecutedSinceLastSolution = 0;
    }

	/**
	 * Retrieves solutions that were found and stored during execution.
	 */
	public ArrayList<Solution> getSolutions() {
		return this.solutions;
	}

	/**
	 * Create a new frame and check whether its local variables should be initialized to logic
	 * variables.
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

		/*
		 * Check which local variables are annotated and replace undefined local variables by logic
		 * variables.
		 */
		AttributeFreeVariables freeVariablesAttribute = null;
		for (Attribute attribute : method.getAttributes()) {
			if (attribute.getStructureName().equals("attribute_free_variables")) {
				freeVariablesAttribute = (AttributeFreeVariables)attribute;
				break;
			} 
		}
		
		if (freeVariablesAttribute != null && freeVariablesAttribute.getFreeVariables().length > 0) {
			AttributeLocalVariableTable localVariablesTableAttribute = null;
			for (Attribute attribute : method.getCodeAttribute().getAttributes()) {
				if (attribute.getStructureName().equals("attribute_local_variable_table")) {
					localVariablesTableAttribute = (AttributeLocalVariableTable)attribute;
					break;
				}
			}
			
			for (FreeVariable freeVariable : freeVariablesAttribute.getFreeVariables()) {
				// find type of free variable
				final int freeVariableIndex = freeVariable.getIndex();
				// Note to future selves: "Parameters" are not "Variables" are not "Local Variables". Or sometimes they are.
				// Anyway, do NOT trust method names of frame.method! Instead, we better determine the type ourselves.
				String type = null;
				assert localVariablesTableAttribute != null;
				for (LocalVariableTable localVariable : localVariablesTableAttribute.getLocalVariableTable())
				{
					if (localVariable.getIndex() != freeVariableIndex) {
						continue;
					}
					type = localVariable.getClassFile().getConstantPool()[localVariable.getDescriptorIndex()].getStringValue();
					break; // Do not look any further.
					
				}
				if (type == null) {
					throw new IllegalStateException("Class File contains FreeVariable declaration for a non-existing variable");
				}
				
				// Convert string type to expression type.
				byte expressionType;
				switch(type) {
				case "B":
					expressionType = Expression.BYTE;
					break;
				case "C":
					expressionType = Expression.CHAR;
					break;
				case "D":
					expressionType = Expression.DOUBLE;
					break;
				case "I":
					expressionType = Expression.INT;
					break;
				case "F":
					expressionType = Expression.FLOAT;
					break;
				case "J":
					expressionType = Expression.LONG;
					break;
				case "S":
					expressionType = Expression.SHORT;
					break;
				case "Z":
					expressionType = Expression.BOOLEAN;
					break;
				default:
					// TODO invent better exception type
					throw new IllegalStateException("Free variables of non-primitive types are not supported");	
				}
				
				// Put correct logic variable for field.
				if (expressionType == Expression.BOOLEAN) {
					frame.setLocalVariable(freeVariableIndex, 
							new BooleanVariable(freeVariable.getName())
							);
				} else {
					frame.setLocalVariable(freeVariableIndex, 
							new NumericVariable(freeVariable.getName(), expressionType, false)
							);
				}
			}
		}
		// Return it.
		return frame;
	}

	/**
	 * Check if the current instruction is the constituting element of a loop. In that case,
	 * increase the counter for the passes of the loop. If the counter has reached the limit, return
	 * false. This will signalize, that execution should be aborted and backtracking started.
	 * Otherwise, true is returned, which signalizes that execution should continue.
	 * 
	 * @param instruction The currently executed instruction.
	 * @param oldPc The pc before this instruction was executed.
	 * @return true, if execution should continue, false, if execution should be aborted.
	 */
	private boolean checkForLoops(Instruction instruction, int oldPc) {
		// Only do further operations if the instruction is actually a conditional jump.
		if (instruction instanceof JumpConditional) {
			Iterator<Loop> iterator = ((LogicFrame) this.currentFrame).getLoops().iterator();
			while (iterator.hasNext()) {
				Loop loop = iterator.next();
				// Is there a matching entry?
				if (loop.getFrom() == oldPc) {
					int newPC = this.pc;
					// Jumped too far?
					if (newPC >= Limitations.MAX_CODE_LENGTH) {
						newPC -= Limitations.MAX_CODE_LENGTH;
					}
					// Only increase if really jumping.
					if (newPC != oldPc + 1 + instruction.getNumberOfOtherBytes()) loop.incCount();
					// End the check here. If the count is now greater or equal than the maximum
					// loops to take, return false. Return true otherwise.
					return !loop.isCountGreaterEqual(Options.getInst().maximumLoopsToTake);
				}
			}
		}
		// Everything went all right. Return true, execution can be continued.
		return true;
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
	public Objectref getAnObjectref(ClassFile classFile) {
		// Get and check the initialized class.
		InitializedClass initializedClass = classFile.getTheInitializedClass(this);
		
		// Get the object reference.
		Objectref objectref = initializedClass.getANewInstance();

		/*
		 * Check which fields are annotated and replace undefined fields by logic variables.
		 */
		for (Field field : classFile.getFields()) {
			for (Attribute attribute : field.getAttributes()) {
				if (!attribute.getStructureName().equals("attribute_free_field")) {
					continue;
				}
				if (!objectref.hasValueFor(field)) {
					String typeString = field.getType();
					byte type;
					switch (typeString) {
					case "char":
					case "java.lang.Character":
						type = Expression.CHAR;
						break;
					case "boolean":
					case "java.lang.Boolean":
						type = Expression.BOOLEAN;
						break;
					case "byte":
					case "java.lang.Byte":
						type = Expression.BYTE;
						break;
					case "double":
					case "java.lang.Double":
						type = Expression.DOUBLE;
						break;
					case "int":
					case "java.lang.Integer":
						type = Expression.INT;
						break;
					case "float":
					case "java.lang.Float":
						type = Expression.FLOAT;
						break;
					case "long":
					case "java.lang.Long":
						type = Expression.LONG;
						break;
					case "short":
					case "java.lang.Short":
						type = Expression.SHORT;
						break;
					default:
						// TODO invent better exception type
						throw new IllegalStateException("Free variables of non-primitive types are not supported");	
					}
					
					// Put correct logic variable for field.
					if (type == Expression.BOOLEAN) {
						objectref.putField(field, new BooleanVariable(field.getName()));
					} else {
						objectref.putField(field, new NumericVariable(field.getName(), type, false));
					}

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
			if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.WARN))
				Globals.getInst().symbolicExecLogger
						.warn("Shutting down the SolverManager failed.");
		} finally {
			super.finalize();
		}
	}

	/**
	 * Return an array of long elements with the times of the execution measurement. If it is
	 * disabled, an array of zero length will be returned.
	 * 
	 * The array has six entries: 0 - Time spent on the execution of instructions. 1 - Time spent on
	 * the detection of loops. 2 - Time spent on the coverage checking. 3 - Time spent on the
	 * generation of choice points. 4 - Time spent on the solving of constraints. 5 - Time spent on
	 * backtracking. 6 - Time spent on the generation of solutions.
	 * 
	 * The time spent on the execution of instructions has already been reduced by the time spent on
	 * the generation of choice points (including solving), which is a sub task.
	 * 
	 * The time is in milliseconds and can directly be used for displaying issues.
	 * 
	 * @return An array of long elements with the times of the execution measurement.
	 */
	public long[] getExecutionTimeInformation() {
		if (!this.measureExecutionTime) return new long[0];
		return new long[]{
				(this.timeExecutionInstruction - this.timeChoicePointGeneration) / NANOS_MILLIS,
				0, this.timeCoverageChecking / NANOS_MILLIS,
				(this.timeChoicePointGeneration - this.timeSolvingChoicePoints) / NANOS_MILLIS,
				(this.timeSolvingChoicePoints + this.timeSolvingBacktracking) / NANOS_MILLIS,
				(this.timeBacktracking - this.timeSolvingBacktracking) / NANOS_MILLIS,
				this.timeSolutionGeneration / NANOS_MILLIS };
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
	 * Increase the time spent on coverage checking by the supplied increment. Lower the instruction
	 * execution time by that value.
	 * 
	 * @param increment The time needed for coverage checking.
	 */
	public void increaseTimeCoverageChecking(long increment) {
		this.timeCoverageChecking += increment;
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
	 * Increase the time spent on backtracking by the supplied increment.
	 * 
	 * @param increment The time needed for a backtracking action.
	 */
	public void increaseTimeBacktracking(long increment) {
		this.timeBacktracking += increment;
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
	 * Increase the time spent on solving for backtracking by the supplied increment.
	 * 
	 * @param increment The time needed for a solving action.
	 */
	public void increaseTimeSolvingForBacktracking(long increment) {
		this.timeSolvingBacktracking += increment;
	}

	/**
	 * Getter for the finalized field.
	 * 
	 * @return true, if this symbolic virtual machine has been finalized, false otherwise.
	 */
	public boolean isFinalized() {
		return this.finalized;
	}

	/**
	 * Getter for the information, whether execution finished as an abortion criterion was matched.
	 * 
	 * @return true, if an abortion criterion was matched, false otherwise.
	 */
	public boolean getAbortionCriterionMatched() {
		return this.abortionCriterionMatched;
	}

	/**
	 * Setter for the information, whether execution finished as an abortion criterion was matched.
	 * 
	 * @param abortionCriterionMatched Information, whether execution finished as an abortion
	 *        criterion was matched.
	 */
	public void setAbortionCriterionMatched(boolean abortionCriterionMatched) {
		this.abortionCriterionMatched = abortionCriterionMatched;
	}

	/**
	 * Getter for the message stored if the execution finished as an abortion criterion was matched.
	 * 
	 * @return The abortion criterion message, or null, if there is no such message stored.
	 */
	public String getAbortionCriterionMatchedMessage() {
		return this.abortionCriterionMatchedMessage;
	}

	/**
	 * Setter for the message stored if the execution finished as an abortion criterion was matched.
	 * 
	 * @param abortionCriterionMatchedMessage The abortion criterion message.
	 */
	public void setAbortionCriterionMatchedMessage(String abortionCriterionMatchedMessage) {
		this.abortionCriterionMatchedMessage = abortionCriterionMatchedMessage;
	}

	/**
	 * Getter for the information, whether the maximum number of loops were reached and no further
	 * deepening was done at least one time.
	 * 
	 * @return true, if the maximum number of loops were reached, false otherwise.
	 */
	public boolean getMaximumLoopsReached() {
		return this.maximumLoopsReached;
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
    }

    public void recordSearchStarted() {
        this.searchStarted = System.nanoTime();
    }

    public void recordSearchEnded() {
        long searchTime =  System.nanoTime() - this.searchStarted;
        //System.out.println("Time spent searching: " + searchTime + " ns.");
    }
}
