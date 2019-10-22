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
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.*;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;
import org.apache.log4j.Level;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Stack;

public abstract class AbstractSearchAlgorithm implements LogicIteratorSearchAlgorithm {
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
     * The choice point the search algorithm last branched at.
     */
    protected ChoicePoint currentChoicePoint;
    /**
     * Currently explored subtree.
     */
    protected UnevaluatedST currentNode;
    /**
     * Search tree representation
     */
    protected ST searchTree;

    public AbstractSearchAlgorithm() {
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

    public boolean isActivelySearching() {
        return this.currentNode != null;
    }

    protected Choice findCommonAncestor(UnevaluatedST currentNode, UnevaluatedST nextNode) {
        if (nextNode == null || currentNode == null) {
            throw new IllegalArgumentException();
        }

        // First, create a set of the parents of nextNode.
        // In BFS, the path of nextNode can be assumed to be shorter or equal to that of the current node, so this likely results in the smaller set.
        Choice nextNodesParent = nextNode.getParent();
        HashSet<Choice> choicesUntilNextNode = new HashSet<>();
        while (nextNodesParent != null) {
            choicesUntilNextNode.add(nextNodesParent);
            nextNodesParent = nextNodesParent.getParent();
        }

        // Traverse upwards starting from the current node and check against the set.
        Choice currentNodesParent = currentNode.getParent();
        while (currentNodesParent != null) {
            // Is this the common ancestor?
            if (choicesUntilNextNode.contains(currentNodesParent)) {
                return currentNodesParent;
            }

            // Try upwards.
            currentNodesParent = currentNodesParent.getParent();
        }

        // No choice is the common ancestor -> Track back to root and start from there.
        // This only happens while we are initially evaluating the root node.
        return null;
    }

    @Override
    public boolean trackBackAndTakeNextDecision(LogicVirtualMachine vm) {
        trackBackToRoot(vm);
        return takeNextDecision(vm);
    }

    /**
     * Activates the selected subtree by imposing its constraint and setting current frame / pc accordingly.
     * If the resulting constraint system is infeasible, switch to the next possible decision immediately.
     * Otherwise, return false.
     *
     * Precondition: The state of the VM is already consistent with the one at which this subtree's parent
     * choice originated.
     *
     * @param subtree Selected subtree
     * @param vm VM
     * @return true if there is another path that can be evaluated (and VM state is set accordingly); false if there is no path.
     */
    protected boolean implementNextDecisionAndDescendIntoSubtree(UnevaluatedST subtree, LogicVirtualMachine vm) {
        this.currentNode = subtree;

        // Add constraint.
        if (subtree.getConstraintExpression().isPresent()) {
            vm.getSolverManager().addConstraint((ConstraintExpression)subtree.getConstraintExpression().get());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                if (!vm.getSolverManager().hasSolution()) {
                    // Constraint system of this subtree is not satisfiable, try next.
                    subtree.setEvaluationResult(new Fail());
                    return trackBackAndTakeNextDecision(vm);
                }
            } catch (TimeoutException | SolverUnableToDecideException e) {
                // Potentially inconsistent, try next.
                subtree.setEvaluationResult(new Fail());
                return trackBackAndTakeNextDecision(vm);
            }
        }

        // Everything is fine and we need to continue in this subtree.
        // Set the current frame to the subtree's frame.
        vm.setCurrentFrame(subtree.getFrame());

        // Set the current pc to the subtree's pc!
        vm.getCurrentFrame().setPc(subtree.getPc());
        vm.setPC(subtree.getPc());

        // Evaluate.
        return true;
    }

    @Override
    public void recordChoice(Choice result) {
        // "Replace" UnevaluatedST with its result (make this known to both).
        result.setSubstitutedUnevaluatedST(this.currentNode);
        this.currentNode.setEvaluationResult(result);

        // Now, add this choice to the next nodes, according to the chosen search strategy.
        // This needs to be implemented in a subclass.
    }

    @Override
    public void recordValue(Value result) {
        // "Replace" UnevaluatedST with its result.
        this.currentNode.setEvaluationResult(result);
    }

    @Override
    public void recordException(de.wwu.muli.searchtree.Exception result) {
        // "Replace" UnevaluatedST with its result.
        this.currentNode.setEvaluationResult(result);
    }

    @Override
    public void recordFail(Fail result) {
        // "Replace" UnevaluatedST with its result.
        this.currentNode.setEvaluationResult(result);
    }

    /**
     *
     * @param trailElement
     * @param vm
     * @param operandStack
     * @param vmStack
     * @param respectiveInverseStack
     * @return operand stack -- this is only different from the {@param operandStack} if the frame has changed because of the application of this trail element.
     */
    protected StackToTrailWithInverse applyTrailElement(final TrailElement trailElement,
                                                        final LogicVirtualMachine vm,
                                                        StackToTrailWithInverse operandStack,
                                                        final StackToTrailWithInverse vmStack,
                                                        final Stack<TrailElement> respectiveInverseStack) {
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

        return operandStack;
    }

    protected void trackBackTheActiveTrail(LogicVirtualMachine vm) {
        StackToTrailWithInverse operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
        StackToTrailWithInverse vmStack = (StackToTrailWithInverse) vm.getStack();

        // Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
        operandStack.setRestoringMode(true);
        vmStack.setRestoringMode(true);

        // Empty the active trail.
        Stack<TrailElement> trail = vm.extractCurrentTrail();
        while (!trail.empty()) {
            final TrailElement trailElement = trail.pop();
            operandStack = applyTrailElement(trailElement, vm, operandStack, vmStack, null);
        }

        // Set the VM's current frame to the one specified in this node.
        vm.setCurrentFrame(this.currentNode.getFrame());

        // If the frame was set to have finished the execution normally, reset that.
        ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

        // Set the pc!
        vm.getCurrentFrame().setPc(this.currentNode.getPc());
        vm.setPC(this.currentNode.getPc());

        // Disable the restoring mode.
        operandStack.setRestoringMode(false);
        vmStack.setRestoringMode(false);
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

    @Override @Deprecated
    public boolean trackBackLocallyNextChoice(LogicVirtualMachine vm) {
        throw new NotImplementedException();
    }

    @Override @Deprecated
    public boolean trackBack(LogicVirtualMachine vm) {
        throw new NotImplementedException();
    }
}
