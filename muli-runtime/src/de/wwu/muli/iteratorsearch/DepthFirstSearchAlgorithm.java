package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.MugglException;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.bytecode.LCmp;
import de.wwu.muggl.instructions.bytecode.Lookupswitch;
import de.wwu.muggl.instructions.general.CompareDouble;
import de.wwu.muggl.instructions.general.CompareFp;
import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.solvers.SolverManager;
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
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.STProxy;
import de.wwu.muli.searchtree.Value;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;
import org.apache.log4j.Level;

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
     * Inverse choice point stack.
     */
    final protected Stack<ChoicePoint> inverseChoicePointStack;
    /**
     * The choice point the search algorithm last branched at.
     */
    protected ChoicePoint currentChoicePoint;
    /**
     * Flag that is false until search begins.
     */
    private boolean hasStartedSearch = false;

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
		this.inverseChoicePointStack = new Stack<>();
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
        return this.currentNode.getParent();
    }


    /**
     * Try to track back to the last ChoicePoint thats non jumping branch was not yet visited
     * and continue from there
     *
     * @param vm The currently executing LogicVirtualMachine.
     * @return true, if tracking back was successful and the execution can be continued, false, if
     *         there was no possibility for tracking back and then execution should hence be
     *         stopped.
     */
    public boolean trackBackLocallyNextChoice(LogicVirtualMachine vm) {
        if (this.measureExecutionTime) this.timeBacktrackingTemp = System.nanoTime();
        // Only track back if there ever was a ChoicePoint generated at all. Otherwise, no tracking back is possible.
        if (this.currentChoicePoint == null) return false;

        Globals.getInst().symbolicExecLogger.trace("(LJVM) Attempt local backtracking for the current branch and execute the one corresponding to the next choice.");

        // Get the SolverManager.
        SolverManager solverManager = vm.getSolverManager();

        // Track back until the last choice point that offers another choice.
        if (!trackBackLocally(vm, solverManager)) {
            return false;
        }

        // Change to the next choice.
        try {
            this.currentChoicePoint.changeToNextChoice();
        } catch (MugglException e) {
            if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
            Globals.getInst().symbolicExecLogger
                    .trace("Tracking back was successful, but encountered an Exception when switching "
                            + " to the next choice. Trying to track back further. The root cause it "
                            + e.getClass().getName() + " (" + e.getMessage() + ")");

            return trackBackLocallyNextChoice(vm);
        }

        // Count up for the non-jumping branch.
        this.numberOfVisitedBranches++;

        // Perform operations specific to the constraint system.
        if (this.currentChoicePoint.changesTheConstraintSystem()) {
            // Remove the Constraint and get the new one.
            solverManager.removeConstraint();
            solverManager.addConstraint(this.currentChoicePoint.getConstraintExpression());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                // Try to solve the expression.
                if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
                if (!solverManager.hasSolution()) {
                    if (this.measureExecutionTime) vm.increaseTimeSolvingForBacktracking(System.nanoTime() - this.timeSolvingTemp);
                    if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                    return trackBackLocallyNextChoice(vm);
                }
                if (this.measureExecutionTime) vm.increaseTimeSolvingForBacktracking(System.nanoTime() - this.timeSolvingTemp);
            } catch (SolverUnableToDecideException e) {
                if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
                    Globals.getInst().symbolicExecLogger.trace("Solving lead to a SolverUnableToDecideException with message: " + e.getMessage());
                if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                return trackBackLocallyNextChoice(vm);
            } catch (TimeoutException e) {
                if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
                    Globals.getInst().symbolicExecLogger.trace("Solving lead to a TimeoutException with message: " + e.getMessage());
                if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                return trackBackLocallyNextChoice(vm);
            }
        }

        // Found the choice point to continue, recover the state of it.
        recoverState(vm, RestoreMode.SimpleRestore);

        // Does the choice point require any state specific changes beside those already done?
        if (this.currentChoicePoint.enforcesStateChanges()) this.currentChoicePoint.applyStateChanges();

        // Tracking back was successful.
        if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
            Globals.getInst().symbolicExecLogger.trace("Tracking back was successful. Already visited " + (this.numberOfVisitedBranches - 1) + " branches.");
        if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
        return true;

    }

    /**
     * Try to track back to the last ChoicePoint thats non jumping branch was not yet visited,
     * and then track back to the root of the symbolic execution tree while recording changes
     * to the inverse trail.
     *
     * @param vm The currently executing LogicVirtualMachine.
     * @return true, if tracking back was successful and the execution can be continued, false, if
     *         there was no possibility for tracking back and then execution should hence be
     *         stopped.
     */
    public boolean trackBack(LogicVirtualMachine vm) {
        if (this.measureExecutionTime) this.timeBacktrackingTemp = System.nanoTime();
        // Only track back if there ever was a ChoicePoint generated at all. Otherwise, no tracking back is possible.
        if (this.currentChoicePoint == null) return false;

        Globals.getInst().symbolicExecLogger.trace("(LJVM) Attempt full backtracking for current search region.");

        // Get the SolverManager.
        SolverManager solverManager = vm.getSolverManager();


        // Track back until the last choice point that offers another choice.
        if (!trackBackLocally(vm, solverManager)) { return false; }

        // Old implementation now would have changed to the next choice. Instead, keep on backtracking to the root.

        // For the current choice point, also reset its state and remove its constraint from the constraint stack.
        // Its trail is also not required anymore; it will be changed to its next choice next time!
        recoverState(vm, RestoreMode.SimpleRestore);
        if (this.currentChoicePoint.changesTheConstraintSystem()) solverManager.removeConstraint();

        this.inverseChoicePointStack.push(this.currentChoicePoint);

        while (this.currentChoicePoint.getParent() != null) {
            this.currentChoicePoint = this.currentChoicePoint.getParent();
            // Get back to its original state, and remove the constraint.
            recoverState(vm, RestoreMode.TrailToInverse);
            if (this.currentChoicePoint.changesTheConstraintSystem()) {
                // No need for an inverse constraint stack, because we will always be able to get the constraint from the choice point.
                solverManager.removeConstraint();
            }
            this.inverseChoicePointStack.push(this.currentChoicePoint);
        }

        this.currentChoicePoint = null;

        // Tracking back was successful.
        if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
            Globals.getInst().symbolicExecLogger.trace("Tracking back was successful. Already visited " + (this.numberOfVisitedBranches - 1) + " branches.");
        if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
        return true;
    }

    /**
     * Try to perform backtracking until reaching the next choice point that has another choice.
     * @param vm executing SJVM
     * @param solverManager current contraint solver manager
     * @return false if there is no other choice.
     */
    private boolean trackBackLocally(LogicVirtualMachine vm, SolverManager solverManager) {
        // Since the jump is executed first, find the newest ChoicePoint thats non jumping branch was not visited yet. Restore previous state while doing so.
        // All of this does not need to go on the inverse trail, because this branch will not be executed again. We care about the next one(s).
        while (!this.currentChoicePoint.hasAnotherChoice()) {
            // No further choice for this CP. There is a parent, continue by recovering. Does not need to push to the inverse trail, because this branch
            // will not be executed again. We only care about subsequent one(s).

            // First step: Use the trail of the last choice point to get back to the old state.
            recoverState(vm, RestoreMode.SimpleRestore);

            // Second step: If one has been set, remove the ConstraintExpression from the ConstraintStack of the SolverManager.
            if (this.currentChoicePoint.changesTheConstraintSystem()) solverManager.removeConstraint();

            // Third step: Load its parent. This will also free the memory of the current choice point.
            this.currentChoicePoint = this.currentChoicePoint.getParent();

            if (this.currentChoicePoint == null) {
                // We reached the root of the symbolic execution tree. There are no more choices.
                if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                return trackBackFinishedNoMoreChoices(vm);
            }
        }
        return true;
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

        STProxy node = this.nextNodes.pop();

        if (node.isEvaluated()) {
            throw new IllegalStateException("Node must correspond to an unevaluated subtree.");
        }

        // If required, navigate to the correct position in the search tree (and change VM state accordingly).
        if (this.currentNode != null && node.getParent().getParent() != this.currentNode.getParent()) {
            // TODO is the above condition correct?

            // TODO:
            // Find shortest path to next node.
            // Obtain trail (towards root node),
            // Revert state via trail,
            // Revert constraints,
            // Obtain inverse trail (towards next node),
            // Apply inverse trail,
            // Apply constraints.
        }

        // Add constraint.
        if (node.getConstraintExpression() != null) {
            vm.getSolverManager().addConstraint(node.getConstraintExpression());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                if (!vm.getSolverManager().hasSolution()) {
                    // Constraint system of this subtree is not satisfiable, try next.
                    return changeToNextChoice(vm);
                }
            } catch (TimeoutException | SolverUnableToDecideException e) {
                // Potentially inconsistent, try next.
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


            // Disable the restoring mode.
            operandStack.setRestoringMode(false);
            vmStack.setRestoringMode(false);

            nextChoice = nextChoice.getParent();
        }

        // We are at the root.
        this.currentNode = null;

        // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
        vm.setNextFrameIsAlreadyLoaded(true);
        // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
		vm.setReturnFromCurrentExecution(true);
}


    public boolean changeToNextChoiceOld(LogicVirtualMachine vm) {
        if (this.measureExecutionTime) this.timeBacktrackingTemp = System.nanoTime();
        Globals.getInst().symbolicExecLogger.trace("(LJVM) Attempt replaying the inverse trail for current search region, and selecting the next choice.");

        // If inverse trail is empty, it is the first execution => nothing to replay here.
        // Instead, generate a dummy choice point that marks the root.
        if (this.inverseChoicePointStack.isEmpty()) {
            if (this.hasStartedSearch) {
                return false;
            }
            this.hasStartedSearch = true;
            this.generateRootChoicePoint(vm);
            return true;
        }
        // Sanity check.
        if (!vm.getCurrentFrame().toString().equals("Frame for de.wwu.muli.search.SolutionIterator.tryAdvance(java.util.function.Consumer consumer) at pc 0.")) {
            throw new IllegalStateException("Only expected to be called from within SolutionIterator!!");
        }

        // Get the SolverManager.
        SolverManager solverManager = vm.getSolverManager();

        // Replay the inverse trail.
        while (!this.inverseChoicePointStack.isEmpty()) {
            this.currentChoicePoint = this.inverseChoicePointStack.pop();

            if (this.currentChoicePoint.changesTheConstraintSystem()) {
                // No need for an inverse constraint stack, because we will always be able to get the constraint from the choice point.
                solverManager.addConstraint(this.currentChoicePoint.getConstraintExpression());
            }

            // Apply its state value.
            if (this.currentChoicePoint.enforcesStateChanges()) this.currentChoicePoint.applyStateChanges();

            // Replay its inverse trail before proceeding to the next choice point.
            recoverState(vm, RestoreMode.InverseToTrail);
        }

        // Change to the next choice.
        try {
            this.currentChoicePoint.changeToNextChoice();
        } catch (MugglException e) {
            if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
            Globals.getInst().symbolicExecLogger
                    .trace("Tracking back was successful, but encountered an Exception when switching "
                            + " to the next choice. Trying to track back further. The root cause it "
                            + e.getClass().getName() + " (" + e.getMessage() + ")");

            return trackBackLocallyNextChoice(vm); // TODO evaluate if the mechanisms are compatible here.
        }

        // Check if the constraint system still has a solution.
        if (this.currentChoicePoint.changesTheConstraintSystem()) {
            // Remove the constraint added during replay and add the new one.
            solverManager.removeConstraint();
            solverManager.addConstraint(this.currentChoicePoint.getConstraintExpression());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                // Try to solve the expression.
                if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
                if (!solverManager.hasSolution()) {
                    if (this.measureExecutionTime) vm.increaseTimeSolvingForBacktracking(System.nanoTime() - this.timeSolvingTemp);
                    if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                    return trackBackLocallyNextChoice(vm);
                }
                if (this.measureExecutionTime) vm.increaseTimeSolvingForBacktracking(System.nanoTime() - this.timeSolvingTemp);
            } catch (SolverUnableToDecideException e) {
                if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
                    Globals.getInst().symbolicExecLogger.trace("Solving lead to a SolverUnableToDecideException with message: " + e.getMessage());
                if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                return trackBackLocallyNextChoice(vm);
            } catch (TimeoutException e) {
                if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
                    Globals.getInst().symbolicExecLogger.trace("Solving lead to a TimeoutException with message: " + e.getMessage());
                if (this.measureExecutionTime) vm.increaseTimeBacktracking(System.nanoTime() - this.timeBacktrackingTemp);
                return trackBackLocallyNextChoice(vm);
            }
        }

        // Count up for the next branch (first branches have been counted on CP generation).
        this.numberOfVisitedBranches++;

        // Does the CP require any state specific changes (corresponding to the next choice) besides those already done?
        if (this.currentChoicePoint.enforcesStateChanges()) this.currentChoicePoint.applyStateChanges();

        // Success, continue execution!
        return true;
    }

	/**
	 * This method is called when tracking back failed. It will not change a thing, and just
	 * log that the execution ends here, then return false. This method is intended to be overridden
	 * by inheriting algorithms.
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 * @return false in any case.
	 */
	protected boolean trackBackFinishedNoMoreChoices(LogicVirtualMachine vm) {
		if (Globals.getInst().symbolicExecLogger.isTraceEnabled())
			Globals.getInst().symbolicExecLogger.trace("No more tracking back is possible. Visited " + this.numberOfVisitedBranches + " branches in total.");
		return false;
	}

	/**
	 * Recover that state at the ChoicePoint currentChoicePoint.
	 * @param vm The currently executing SymbolicalVirtualMachine.
	 */
	private void recoverState(LogicVirtualMachine vm, RestoreMode mode) {
		// Get the current stacks.
		StackToTrailWithInverse operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
		StackToTrailWithInverse vmStack = (StackToTrailWithInverse) vm.getStack();

		// Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
		operandStack.setRestoringMode(true);
		vmStack.setRestoringMode(true);

        // All this needs to done before the other procedures iff InverseToTrail;
        // otherwise, in regular backtracking, do it after replaying from the inverse trail.
        if (mode == RestoreMode.InverseToTrail) {
            // Set the correct Frame to be the current Frame.
            vm.setCurrentFrame(this.currentChoicePoint.getFrame());

            // Set the pc!
            vm.getCurrentFrame().setPc(this.currentChoicePoint.getPcNext());
        }

		// If the choice point has a trail, use it to recover the state.
		if (this.currentChoicePoint.hasTrail()) {
            Stack<TrailElement> trail;
            Stack<TrailElement> respectiveInverse = null;
            if (mode == RestoreMode.SimpleRestore) { // No inverse stack used.
                trail = this.currentChoicePoint.getTrail();
            } else if (mode == RestoreMode.TrailToInverse) { // Use regular trail; populate inverse trail.
                trail = this.currentChoicePoint.getTrail();
                respectiveInverse = this.currentChoicePoint.getInverseTrail();
            } else { // Use inverse trail; re-populate regular trail
                trail = this.currentChoicePoint.getInverseTrail();
                respectiveInverse = this.currentChoicePoint.getTrail();
            }

			// Empty the trail.
			while (!trail.empty()) {
				final TrailElement trailElement = trail.pop();
                applyTrailElement(trailElement, vm, operandStack, vmStack, respectiveInverse);
            }
		}


		// All this needs to done before the other procedures iff InverseToTrail;
        // otherwise, in regular backtracking, do it now.
        if (mode != RestoreMode.InverseToTrail) {
            // Set the correct Frame to be the current Frame.
            vm.setCurrentFrame(this.currentChoicePoint.getFrame());

            // If the frame was set to have finished the execution normally, reset that.
            ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

            // Set the pc!
            vm.getCurrentFrame().setPc(this.currentChoicePoint.getPcNext());
        }

		// Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
		vm.setNextFrameIsAlreadyLoaded(true);
		// If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
		vm.setReturnFromCurrentExecution(true);

		// Disable the restoring mode.
		operandStack.setRestoringMode(false);
		vmStack.setRestoringMode(false);
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
