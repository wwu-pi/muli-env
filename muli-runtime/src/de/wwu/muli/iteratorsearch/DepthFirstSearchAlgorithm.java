package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.bytecode.LCmp;
import de.wwu.muggl.instructions.bytecode.Lookupswitch;
import de.wwu.muggl.instructions.general.CompareDouble;
import de.wwu.muggl.instructions.general.CompareFp;
import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.flow.coverage.CGCoverageTrailElement;
import de.wwu.muggl.symbolic.flow.coverage.DUCoverageTrailElement;
import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.generating.GeneratorChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.EquationViolationException;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.SolvingException;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.array.ArrayInitializationChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.fpComparison.DoubleComparisonChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.fpComparison.FloatComparisonChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.longComparison.LongComparisonChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.switching.LookupswitchChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.switching.SwitchingChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.switching.TableswitchChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.*;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muli.iteratorsearch.structures.ConditionalJumpChoicePointDepthFirst;
import de.wwu.muli.iteratorsearch.structures.RootChoicePoint;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.*;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;
import org.apache.log4j.Level;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ListIterator;
import java.util.Stack;

/**
 * This class implements the depth first search algorithm.<br />
 * <br />
 * The depth first algorithm always takes one path through the search tree until
 * a point with no further branching is reached; this most likely is the end of
 * the current program. In this application, the first path to take is always
 * the jumping path. This means, when a conditional jump is encountered, a
 * ChoicePoint is generated and the execution continues at the jump offset.<br />
 * <br />
 * When and leaf of the search tree has been reached and the possibly found
 * solution has been saved, tracking back starts. The algorithm will go back
 * to the last ChoicePoint and check whether its non jumping branch has been
 * visited already already. If it has not yet been visited, execution can be
 * continued at that branch. From this point on, on any further conditional
 * jumps the jumping branch will again be executed first.<br />
 * <br />
 * If tracking back and a ChoicePoint is reached thats non-jumping branch also
 * has been visited, the application tracks back to the parent ChoicePoint,
 * checking if the non-jumping branch has been visited for that ChoicePoint.
 * The algorithm recursively continues in that way. It stops when the topmost
 * ChoicePoint (the ChoicePoint that has no parent) has been reached and it
 * indicates that its non-jumping branch has already been visited.<br />
 * <br />
 * The depth first search algorithm does not need any additional information
 * stored to perform. However, finding solutions might take a lot of time.
 *
 * Modifications have been made in order to account for the fact that backtracking
 * may occur anywhere, not just at the end of the program. -JD 15.03.17
 *
 * TODO rephrase description to account for new mechanism of iterator-based search.
 *
 * @author Jan C. Dageförde, based on work of Tim Majchrzak
 */
public class DepthFirstSearchAlgorithm implements LogicIteratorSearchAlgorithm {
	// Fields
	/**
	 * The total number of branches visited so far.
	 */
	protected long numberOfVisitedBranches;
	/**
	 * Flag to determine if the execution time should be measured.
	 */
	protected boolean measureExecutionTime;
	/**
	 * Temporary field to measure choice point generation time.
	 */
	protected long timeChoicePointGenerationTemp;
	/**
	 * Temporary field to measure backtracking time.
	 */
	protected long timeBacktrackingTemp;
	/**
	 * Temporary field to measure solving time.
	 */
	protected long timeSolvingTemp;
    /**
     * The choice point the search algorithm last branched at.
     */
    protected ChoicePoint currentChoicePoint;
    /**
     * Search tree representation
     */
    private ST searchTree;
    /**
     * Stack that represents the nodes that DFS will check subsequently.
     */
    private Stack<STProxy> nextNodes;
    /**
     * Currently explored subtree.
     */
    private STProxy currentNode;

    /**
	 * Instantiate the depth first search algorithm.
     */
	public DepthFirstSearchAlgorithm() {
		this.numberOfVisitedBranches = 0;
		this.measureExecutionTime = Options.getInst().measureSymbolicExecutionTime;
	}

    /**
     * Get the current ChoicePoint of this search algorithm. It reflects the
     * last node in the searching tree visited.
     * @return The current choice point.
     */
    public ChoicePoint getCurrentChoicePoint() {
        return this.currentChoicePoint;
    }

    @Override
    public Choice getCurrentChoice() {
        if (this.currentNode == null) {
            return null;
        }
        return this.currentNode.getParent();
    }

    public boolean changeToNextChoice(LogicVirtualMachine vm) {
        // Evaluate next subtree.
        if (this.searchTree == null) {
            this.searchTree = new STProxy(vm.getCurrentFrame(), vm.getPc(), null, null);
            this.nextNodes = new Stack<>();
            this.nextNodes.push((STProxy)this.searchTree);
        }

        if (this.nextNodes.empty()) {
            return false;
        }

        final STProxy node = this.nextNodes.pop();

        if (node.isEvaluated()) {
            throw new IllegalStateException("Node must correspond to an unevaluated subtree.");
        }

        if (this.currentNode == null) {
            // We are descending from the root (instead of doing a local descent from an existing choice).
            // This implies that we need to use the inverse trail.
            final Stack<Choice> choices = new Stack<>();
            Choice visit = node.getParent();
            while (visit != null) {
                choices.push(visit);
                visit = visit.getParent();
            }

            StackToTrailWithInverse operandStack;
            StackToTrailWithInverse vmStack;
            while (!choices.empty()) {
                Choice choice = choices.pop();

                // Add constraint.
                if (choice.getSubstitutedSTProxy().getConstraintExpression() != null) {
                    vm.getSolverManager().addConstraint(choice.getSubstitutedSTProxy().getConstraintExpression());
                }

                // Set the correct Frame to be the current Frame.
                vm.setCurrentFrame(choice.getSubstitutedSTProxy().getFrame());

                // If the frame was set to have finished the execution normally, reset that.
                ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

                // Set the pc!
                vm.getCurrentFrame().setPc(choice.getSubstitutedSTProxy().getPc());
                vm.setPC(choice.getSubstitutedSTProxy().getPc());

                // Get the current stacks.
                operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
                vmStack = (StackToTrailWithInverse) vm.getStack();

                // Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
                operandStack.setRestoringMode(true);
                vmStack.setRestoringMode(true);

                // Empty the inverse trail. (Note that the use of Inverse Trail and Trail is the exact opposite of what happens during backtracking.)
                Stack<TrailElement> trail = choice.getInverseTrail();
                Stack<TrailElement> inverseTrail = choice.getTrail();
                while (!trail.empty()) {
                    final TrailElement trailElement = trail.pop();
                    applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
                }

                // Disable the restoring mode.
                operandStack.setRestoringMode(false);
                vmStack.setRestoringMode(false);
            }
        }

        // Add constraint.
        if (node.getConstraintExpression() != null) {
            vm.getSolverManager().addConstraint(node.getConstraintExpression());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                if (!vm.getSolverManager().hasSolution()) {
                    // Constraint system of this subtree is not satisfiable, try next.
                    node.setEvaluationResult(new Fail());
                    trackBackToRoot(vm);
                    return changeToNextChoice(vm);
                }
            } catch (TimeoutException | SolverUnableToDecideException e) {
                // Potentially inconsistent, try next.
                node.setEvaluationResult(new Fail());
                trackBackToRoot(vm);
                return changeToNextChoice(vm);
            }
        }

        // Everything is fine and we need to continue in this subtree.
        // Set the current frame to the subtree's frame.
        vm.setCurrentFrame(node.getFrame());

        // Set the current pc to the subtree's pc!
        vm.getCurrentFrame().setPc(node.getPc());
        vm.setPC(node.getPc());

        this.currentNode = node;
        // Evaluate.
        return true;
    }

    @Override
    public void recordChoice(Choice result) {
        result.setSubstitutedSTProxy(this.currentNode);
        // "Replace" STProxy with its result.
        this.currentNode.setEvaluationResult(result);

        // Push search trees in reverse order so they will be popped from left-to-right.
        ListIterator<STProxy> stIt = result.getSts().listIterator(result.getSts().size());

        while (stIt.hasPrevious()) {
            this.nextNodes.push(stIt.previous());
        }
    }

    @Override
    public void recordValue(Value result) {
        // "Replace" STProxy with its result.
        this.currentNode.setEvaluationResult(result);
        // TODO Consider deleting reference to Frame in STProxy to save some memory.
    }

    @Override
    public void trackBackToRoot(LogicVirtualMachine vm) {
        // Get the current stacks.
        StackToTrailWithInverse operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
        StackToTrailWithInverse vmStack = (StackToTrailWithInverse) vm.getStack();

        // Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
        operandStack.setRestoringMode(true);
        vmStack.setRestoringMode(true);


        // Empty the trail.
        Stack<TrailElement> trail = vm.extractCurrentTrail();
        while (!trail.empty()) {
            final TrailElement trailElement = trail.pop();
            applyTrailElement(trailElement, vm, operandStack, vmStack, null);
        }

        // Set the correct Frame to be the current Frame.
        vm.setCurrentFrame(this.currentNode.getFrame());

        // If the frame was set to have finished the execution normally, reset that.
        ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

        // Set the pc!
        vm.getCurrentFrame().setPc(this.currentNode.getPc());
        vm.setPC(this.currentNode.getPc());

        // Disable the restoring mode.
        operandStack.setRestoringMode(false);
        vmStack.setRestoringMode(false);

        Choice nextChoice = this.currentNode.getParent();
        while (nextChoice != null) {
            // Remove the this choice's constraint from the system.
            vm.getSolverManager().removeConstraint();

            // Get the current stacks.
            operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
            vmStack = (StackToTrailWithInverse) vm.getStack();

            // Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
            operandStack.setRestoringMode(true);
            vmStack.setRestoringMode(true);

            // Empty the trail.
            trail = nextChoice.getTrail();
            Stack<TrailElement> inverseTrail = nextChoice.getInverseTrail();
            while (!trail.empty()) {
                final TrailElement trailElement = trail.pop();
                applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
            }

            // Set the correct Frame to be the current Frame.
            vm.setCurrentFrame(nextChoice.getSubstitutedSTProxy().getFrame());

            // If the frame was set to have finished the execution normally, reset that.
            ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

            // Set the pc!
            vm.getCurrentFrame().setPc(nextChoice.getSubstitutedSTProxy().getPc());
            vm.setPC(nextChoice.getSubstitutedSTProxy().getPc());


            // Disable the restoring mode.
            operandStack.setRestoringMode(false);
            vmStack.setRestoringMode(false);

            nextChoice = nextChoice.getParent();
        }

        // We are at the root.
        this.currentNode = null;
        //vm.getCurrentFrame().setPc(43); // TODO Replace with a reliable position of the next bytecode instruction (see RootChoicePoint)
        //vm.setPC(43);


        // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
        vm.setNextFrameIsAlreadyLoaded(true);
        // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
		vm.setReturnFromCurrentExecution(true);
    }

    @Override
    public boolean trackBackLocallyNextChoice(LogicVirtualMachine vm) {
        throw new NotImplementedException();
    }

    @Override
    public boolean trackBack(LogicVirtualMachine vm) {
        throw new NotImplementedException();
    }

    private void applyTrailElement(TrailElement trailElement, LogicVirtualMachine vm, StackToTrailWithInverse operandStack, StackToTrailWithInverse vmStack, Stack<TrailElement> respectiveInverseStack) {
        // Decide about the action by checking the trail element's type.
        if (trailElement instanceof VmPush) {
            VmPush vmPush = (VmPush) trailElement;
            LogicFrame frame = (LogicFrame)vmPush.getObject();
            vmStack.push(frame);
            if (vmPush.restoreStates()) {
                // Restore states of the frame.
                frame.setPc(vmPush.getPc());
                frame.setMonitor(vmPush.getMonitor());
            }
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new VmPop());
        } else if (trailElement instanceof VmPop) {
            if (vmStack.isEmpty()) {
                if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.WARN))
                    Globals.getInst().symbolicExecLogger.warn("(OP2) Processing the trail lead to a request to "
                            + "pop an element from the empty VM stack. It will be ignored and skipped. "
                            + "However, this hints to a serious problem and should be checked.");
            } else {
                Object fromVm = vmStack.pop();
                // Create inverse trail element and push it.
                if (respectiveInverseStack != null) respectiveInverseStack.push(new VmPush(fromVm));
            }
        } else if (trailElement instanceof FrameChange) {
            Frame formerFrame = vm.getCurrentFrame();                    // TODO consider (because frames might be stored in pc 0, which is not quite correct.) formerFrame.setPc(vm.getPc());
            FrameChange frameChange = (FrameChange) trailElement;
            // There was a change in the frame. Put it as the (temporary) current Frame.
            vm.setCurrentFrame(frameChange.getFrame());
            // Disable the restoring mode for the last Frame's operand stack.
            operandStack.setRestoringMode(false);
            // Set the current operand stack accordingly.
            operandStack = (StackToTrailWithInverse) frameChange.getFrame().getOperandStack();
            // Enable restoring mode for it.
            operandStack.setRestoringMode(true);
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new FrameChange(formerFrame));
        } else if (trailElement instanceof PCChange) {
            int formerPC = vm.getPc();
            PCChange pcChange = (PCChange) trailElement;
            // There was an explicit PC jump, e.g. due to exception handling.
            // Restore the former PC value.
            vm.setPC(pcChange.getPC());
            vm.getCurrentFrame().setPc(pcChange.getPC());
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new PCChange(formerPC));
        } else if (trailElement instanceof Push) {
            Push push = (Push) trailElement;
            vm.getCurrentFrame().getOperandStack().push(push.getObject());
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new Pop());
        } else if (trailElement instanceof Pop) {
            if (vm.getCurrentFrame().getOperandStack().isEmpty()) {
                if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.WARN))
                    Globals.getInst().symbolicExecLogger.warn("(OP1) Processing the trail lead to a request to "
                            + "pop an element from an empty operand stack. It will be ignored and skipped. "
                            + "However, this hints to a serious problem and should be checked.");
            } else {
                Object fromStack = vm.getCurrentFrame().getOperandStack().pop();
                // Create inverse trail element and push it.
                if (respectiveInverseStack != null) respectiveInverseStack.push(new Push(fromStack));
            }
        } else if (trailElement instanceof PopFromFrame) {
            StackToTrailWithInverse otherOperandStack = (StackToTrailWithInverse) ((PopFromFrame) trailElement).getFrame().getOperandStack();
            if (otherOperandStack.isEmpty()) {
                if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.WARN))
                    Globals.getInst().symbolicExecLogger.warn("Processing the trail lead to a request to "
                            + "pop an element from an empty operand stack. It will be ignored and skipped. "
                            + "However, this hints to a serious problem and should be checked.");
            } else {
                boolean previousRestoringMode = otherOperandStack.getRestoringMode();
                otherOperandStack.setRestoringMode(true); // Prevent (bogus) trail elements from being pushed.
                Object formerValue = otherOperandStack.pop();
                otherOperandStack.setRestoringMode(previousRestoringMode);
                // Create inverse trail element and push it.
                if (respectiveInverseStack != null) respectiveInverseStack.push(new PushToFrame(((PopFromFrame) trailElement).getFrame(), formerValue));
            }
        } else if (trailElement instanceof PushToFrame) {
            StackToTrailWithInverse otherOperandStack = (StackToTrailWithInverse) ((PushToFrame) trailElement).getFrame().getOperandStack();
            boolean previousRestoringMode = otherOperandStack.getRestoringMode();
            otherOperandStack.setRestoringMode(true); // Prevent (bogus) trail elements from being pushed.
            otherOperandStack.push(((PushToFrame) trailElement).getValue());
            otherOperandStack.setRestoringMode(previousRestoringMode);
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new PopFromFrame(((PushToFrame) trailElement).getFrame()));
        } else if (trailElement instanceof ArrayRestore) {
            ArrayRestore ar = ((ArrayRestore) trailElement);
            ArrayRestore arInverse = ar.createInverse();
            ar.restore();
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(arInverse);
        } else if (trailElement instanceof Restore) {
            Restore restore = ((Restore) trailElement);
            int restoreTo = restore.getIndex();
            Object formerValue = vm.getCurrentFrame().getLocalVariables()[restoreTo];
            restore.restore(vm.getCurrentFrame());
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(new Restore(restoreTo,formerValue));
        } else if (trailElement instanceof InstanceFieldPut) {
            InstanceFieldPut ifp = ((InstanceFieldPut) trailElement);
            InstanceFieldPut ifpInverse = ifp.createInverseElement();
            ifp.restoreField();
            // Create inverse trail element and push it.
            if (respectiveInverseStack != null) respectiveInverseStack.push(ifpInverse);
        } else if (trailElement instanceof StaticFieldPut) {
            ((StaticFieldPut) trailElement).restoreField();
            // TODO Create inverse trail element and push it.
            if (respectiveInverseStack != null) throw new UnsupportedOperationException("Inverse of StaticFieldPut not implemented");
        } else if (trailElement instanceof DUCoverageTrailElement) {
            ((DUCoverageTrailElement) trailElement).restore();
            // TODO Create inverse trail element and push it.
            if (respectiveInverseStack != null) throw new UnsupportedOperationException("Inverse of DUCoverageTrailElement not implemented");
        } else if (trailElement instanceof CGCoverageTrailElement) {
            ((CGCoverageTrailElement) trailElement).restore();
            // TODO Create inverse trail element and push it.
            if (respectiveInverseStack != null) throw new UnsupportedOperationException("Inverse of CGCoverageTrailElement not implemented");
        } else {
            if (Globals.getInst().symbolicExecLogger.isEnabledFor(Level.WARN))
                Globals.getInst().symbolicExecLogger.warn(
                        "Found an unrecognized object on the trail when trying to restore"
                                + "an old state. It will be ignored and skipped.");
        }
    }

    /**
	 * Generate a new GeneratorChoicePoint or ArrayInitializationChoicePoint for a local variable.
	 * Set it as the current choice point.
	 * 
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @param localVariableIndex The index into the local variable table to store the generated
	 *        array at.
	 * @param generator A variable Generator. May be null to indicate no custom variable generator
	 *        is used.
	 * @throws ConversionException If converting the first provided object failed.
	 * @throws SymbolicExecutionException If a type is encountered that no array can be created for.
	 */
	public void generateNewChoicePoint(
			LogicVirtualMachine vm, int localVariableIndex, Generator generator
			) throws ConversionException, SymbolicExecutionException {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();
		// If a custom generator is present, generate a GeneratorChoicePoint.
		if (generator != null) {
			this.currentChoicePoint = new GeneratorChoicePoint(
					generator,
					vm.getCurrentFrame(),
					localVariableIndex,
					vm.getPc(),
					this.currentChoicePoint
					);
		} else {
			/*
			 * The only reason to invoke this method without a custom variable generator provided is
			 * the request for a ArrayInitializationChoicePoint that uses the built-in array generator.
			 */
			this.currentChoicePoint = new ArrayInitializationChoicePoint(vm.getCurrentFrame(),
					localVariableIndex, vm.getPc(), this.currentChoicePoint);
		}

		// Apply the first value.
		this.currentChoicePoint.applyStateChanges();

		// Count up for the jumping branch.
		this.numberOfVisitedBranches++;
		if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
	}
	
	/**
	 * Generate a new  ArrayInitializationChoicePoint for instruction
	 * <code>anewarray</code>. Set it as the current choice point and mark that the jumping branch
	 * has been visited already (since execution just continues there.)
	 * 
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @param type A String representation of the type.
	 * 
	 * @throws SymbolicExecutionException If a type is encountered that no array can be created for.
	 */
	public void generateNewChoicePoint(LogicVirtualMachine vm, String type)
			throws ExecutionException {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();	
		this.currentChoicePoint = new ArrayInitializationChoicePoint(vm.getCurrentFrame(), vm
				.getPc(), type, this.currentChoicePoint);


		// Apply the first value.
		this.currentChoicePoint.applyStateChanges();

		// Count up for the jumping branch.
		this.numberOfVisitedBranches++;
		if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
	}
	

	/**
	 * Generate a new ConditionalJumpChoicePoint. Set it as the current choice point and mark that
	 * the jumping branch has been visited already (since execution just continues there.)
	 *
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @param instruction The Instruction generating the ChoicePoint.
	 * @param constraintExpression The ConstraintExpression describing the choice at this
	 *        conditional jump Instruction.
	 */
	public void generateNewChoicePoint(LogicVirtualMachine vm,
			GeneralInstructionWithOtherBytes instruction, ConstraintExpression constraintExpression) {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();
		try {
			this.currentChoicePoint = new ConditionalJumpChoicePointDepthFirst(
					vm.getCurrentFrame(),
					vm.getPc(),
					vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
					((JumpConditional) instruction).getJumpTarget(),
					constraintExpression,
					this.currentChoicePoint);

			// Count up for the jumping branch.
			this.numberOfVisitedBranches++;
			if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
		} catch (EquationViolationException e) {
			if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
			// Track back to the last choice point and try its non-jumping branch.
			trackBackLocallyNextChoice(vm);
		} catch (SolvingException e) {
			// Track back to the last choice point and try its non-jumping branch.
            trackBackLocallyNextChoice(vm);
		}
	}

	/**
	 * Generate a new LongComparisonChoicePoint. Set it as the current choice point.
	 *
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @param instruction The Instruction generating the ChoicePoint.
	 * @param leftTerm The term of long variables and constants of the left hand side of the comparison.
	 * @param rightTerm The term of long variables and constants of the right hand side of the comparison.
	 * @throws SymbolicExecutionException If an Exception is thrown during the choice point generation.
	 */
	public void generateNewChoicePoint(LogicVirtualMachine vm, LCmp instruction,
			Term leftTerm, Term rightTerm) throws ExecutionException {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();
		this.currentChoicePoint = new LongComparisonChoicePoint(
				vm.getCurrentFrame(),
				vm.getPc(),
				vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
				leftTerm,
				rightTerm,
				this.currentChoicePoint);

		// Apply the first value.
		this.currentChoicePoint.applyStateChanges();

		// Count up for the jumping branch.
		this.numberOfVisitedBranches++;
		if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
	}

	/**
	 * Generate a new FpComparisonChoicePoint. Set it as the current choice point.
	 *
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @param instruction The Instruction generating the ChoicePoint.
	 * @param less If set to true, the choice point will have the behaviour of dcmpl / fcmpl;
	 *        otherwise, it will behave like dcmpg / fcmpg.
	 * @param leftTerm The term of long variables and constants of the left hand side of the
	 *        comparison.
	 * @param rightTerm The term of long variables and constants of the right hand side of the
	 *        comparison.
	 * @throws SymbolicExecutionException If an Exception is thrown during the choice point
	 *         generation.
	 */
	public void generateNewChoicePoint(LogicVirtualMachine vm, CompareFp instruction,
			boolean less, Term leftTerm, Term rightTerm) throws ExecutionException {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();

		if (instruction instanceof CompareDouble) {
			this.currentChoicePoint = new DoubleComparisonChoicePoint(
					vm.getCurrentFrame(),
					vm.getPc(),
					vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
					less,
					leftTerm,
					rightTerm,
					this.currentChoicePoint);
		} else {
			this.currentChoicePoint = new FloatComparisonChoicePoint(
					vm.getCurrentFrame(),
					vm.getPc(),
					vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
					less,
					leftTerm,
					rightTerm,
					this.currentChoicePoint);
		}

		// Apply the first value.
		this.currentChoicePoint.applyStateChanges();

		// Count up for the jumping branch.
		this.numberOfVisitedBranches++;
		if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
	}

	/**
	 * Generate a new SwitchingComparisonChoicePoint. Set it as the current choice point.
	 *
	 * @param vm The currently executing SymbolicalVirtualMachine.
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
	public void generateNewChoicePoint(LogicVirtualMachine vm, Switch instruction, Term termFromStack,
			IntConstant[] keys, int[] pcs, IntConstant low, IntConstant high)
			throws ExecutionException {
		if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();

		if (instruction instanceof Lookupswitch) {
			this.currentChoicePoint = new LookupswitchChoicePoint(
					vm.getCurrentFrame(),
					vm.getPc(),
					vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
					termFromStack,
					keys,
					pcs,
					this.currentChoicePoint);
		} else {
			this.currentChoicePoint = new TableswitchChoicePoint(
					vm.getCurrentFrame(),
					vm.getPc(),
					vm.getPc() + 1 + instruction.getNumberOfOtherBytes(),
					termFromStack,
					keys,
					pcs,
					low,
					high,
					this.currentChoicePoint);
		}
		// execute side effects that formerly resided in the constructor  
		((SwitchingChoicePoint)this.currentChoicePoint).init(); 

		// Apply the first value.
		this.currentChoicePoint.applyStateChanges();

		// Count up for the jumping branch.
		this.numberOfVisitedBranches++;
		if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
	}

    /**
     * Generate a new  ArrayInitializationChoicePoint for instruction
     * <code>anewarray</code>. Set it as the current choice point and mark that the jumping branch
     * has been visited already (since execution just continues there.)
     *
     * @param vm The currently executing LogicVirtualMachine.
     * @deprecated
     *
     */
    private void generateRootChoicePoint(LogicVirtualMachine vm) {
        if (this.measureExecutionTime) this.timeChoicePointGenerationTemp = System.nanoTime();
        this.currentChoicePoint = new RootChoicePoint(vm.getCurrentFrame(), vm.getPc(), this.currentChoicePoint);

        if (this.measureExecutionTime) vm.increaseTimeChoicePointGeneration(System.nanoTime() - this.timeChoicePointGenerationTemp);
    }

	/**
	 * Return a String representation of this search algorithms name.
	 * @return A String representation of this search algorithms name.
	 */
	public String getName() {
		return "depth first";
	}

	/**
	 * Get the information whether this search algorithm requires a field
	 * value to be stored (at this exakt moment of execution).
	 * @return true, if a choice point has been already generated, false otherwise.
	 */
	public boolean savingFieldValues() {
		if (this.currentChoicePoint == null) return false;
		return true;
	}

	/**
	 * Store a field value for use by the seach algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation Either a InstanceFieldPut or a StaticfieldPut object.
	 */
	public void saveFieldValue(FieldPut valueRepresentation) {
		if (this.currentChoicePoint != null) this.currentChoicePoint.addToTrail(valueRepresentation);
	}

	/**
	 * Get the information whether this search algorithm requires a local
	 * variable value to be stored (at this exakt moment of execution).
	 * @return true, if a choice point has been already generated, false otherwise.
	 */
	public boolean savingLocalVariableValues() {
		if (this.currentChoicePoint == null) return false;
		return true;
	}

	/**
	 * Store a local varable value for use by the seach algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation A Restore object.
	 */
	public void saveLocalVariableValue(Restore valueRepresentation) {
		if (this.currentChoicePoint != null) this.currentChoicePoint.addToTrail(valueRepresentation);
	}

	/**
	 * Get the information whether this search algorithm requires an array
	 * value to be stored (at this exakt moment of execution).
	 * @return true, if a choice point has been already generated, false otherwise.
	 */
	public boolean savingArrayValues() {
		if (this.currentChoicePoint == null) return false;
		return true;
	}

	/**
	 * Store a array value for use by the seach algorithm's tracking back
	 * functionality.
	 * @param valueRepresentation An ArrayRestore object.
	 */
	public void saveArrayValue(ArrayRestore valueRepresentation) {
		if (this.currentChoicePoint != null) this.currentChoicePoint.addToTrail(valueRepresentation);
	}

}
