package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.ListIterator;
import java.util.Stack;

/**
 * This class implements the iterative deepening DFS search algorithm along the ST
 * search tree structure. It works naïvely, i.e., it always traverses via the root.<br />
 * <br />
 *
 * @author Jan C. Dageförde
 *
 */
public class IterativeDeepeningDFSNaive extends AbstractSearchAlgorithm {
    /**
     * This holds the current depth up to which we will descend a tree.
     */
    protected int currentMaximumDepth;
    /**
     * The depth is incremented by this constant.
     */
    protected static final int deepnessIncrement = 5;
    /**
     * For a given maximum depth of n = `currentMaximumDepth', this
     * stack represents the nodes up to a depth of n that DFS will check subsequently.
     */
    protected Stack<UnevaluatedST> nextNodes;
    /**
     * For a given maximum depth of n = `currentMaximumDepth', this
     * stack represents nodes in depth n or greater that DFS will check after `nextNodes' is empty.
     */
    protected Stack<UnevaluatedST> nextNodeStack;
    protected Choice freshChoiceRecorded = null;

    /**
	 * Instantiate the depth first search algorithm.
     */
	public IterativeDeepeningDFSNaive() {
        super();
    }

    /**
     * Take the next possible decision and put the VM into a state in which it can follow the decision's path.
     *
     * @param vm VM
     * @return true if there is another path that can be evaluated (and VM state is set accordingly); false if there is no path.
     */
    public boolean takeNextDecision(LogicVirtualMachine vm) {
        // Evaluate next subtree.
        if (this.searchTree == null) {
            this.searchTree = new UnevaluatedST(vm.getCurrentFrame(), vm.getPc(), null, null);
            this.nextNodes = new Stack<>();
            this.nextNodeStack = new Stack<>();
            this.currentMaximumDepth = IterativeDeepeningDFSNaive.deepnessIncrement;
            // Add to the end of the queue.
            this.nextNodes.push((UnevaluatedST)this.searchTree);
        }

        if (this.nextNodes.empty()) {
            if (this.nextNodeStack.empty()) {
                return false;
            }

            this.currentMaximumDepth += IterativeDeepeningDFSNaive.deepnessIncrement;
            this.nextNodes = this.nextNodeStack;
            this.nextNodeStack = new Stack<>();
        }

        // Get next node from the beginning of the queue.
        final UnevaluatedST node = this.nextNodes.pop();

        if (node.isEvaluated()) {
            throw new IllegalStateException("Node must correspond to an unevaluated subtree.");
        }

        if (this.currentNode != null) {
            trackBackToRoot(vm);
        }

        return navigateTo(null, node, vm);
    }

    protected boolean navigateTo(Choice from, UnevaluatedST node, LogicVirtualMachine vm) {
        // We need to use the inverse trail to get from the current node (from) until the next node.
        final Stack<Choice> choices = new Stack<>();
        Choice visit = node.getParent();
        // Only visit nodes upwards until the `from' node (where null is the root).
        while (visit != from) {
            choices.push(visit);
            visit = visit.getParent();
        }

        StackToTrailWithInverse operandStack;
        StackToTrailWithInverse vmStack;
        while (!choices.empty()) {
            Choice choice = choices.pop();

            // Add constraint.
            if (choice.getSubstitutedUnevaluatedST().getConstraintExpression().isPresent()) {
                vm.getSolverManager().addConstraint((ConstraintExpression)choice.getSubstitutedUnevaluatedST().getConstraintExpression().get());
            }

            // Set the correct Frame to be the current Frame.
            vm.setCurrentFrame(choice.getSubstitutedUnevaluatedST().getFrame());

            // If the frame was set to have finished the execution normally, reset that.
            ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

            // Set the pc!
            vm.getCurrentFrame().setPc(choice.getSubstitutedUnevaluatedST().getPc());
            vm.setPC(choice.getSubstitutedUnevaluatedST().getPc());

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
                operandStack = applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
            }

            // Disable the restoring mode.
            operandStack.setRestoringMode(false);
            vmStack.setRestoringMode(false);

            // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
            vm.setNextFrameIsAlreadyLoaded(true);
            // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
            vm.setReturnFromCurrentExecution(true);
        }

        // Now activate the next subtree by imposing its constraint and setting current frame / pc accordingly.
        // If the resulting constraint system is infeasible, switch to the next possible decision immediately.
        // Otherwise, return false.
        return implementNextDecisionAndDescendIntoSubtree(node, vm);
    }

    @Override
    public void trackBackToRoot(LogicVirtualMachine vm) {
        // Perform local backtracking to revert all effects since the last choice - unless we have just recorded a fresh choice.
        if (this.freshChoiceRecorded != null) {
            Choice from = this.freshChoiceRecorded;
            this.freshChoiceRecorded = null;

            // There is no active trail, but track back from the last recorded choice upwards. We will need to come back to this choice later.
            trackBackUntil(null, from, false, true, vm);
        } else {
            // We were at a value node, to which we won't need to descend anymore.
            trackBackTheActiveTrail(vm);

            // Now track back until the root using individual trails from the choices along the path.
            trackBackUntil(null, this.currentNode.getParent(), true,true, vm);
        }

        // We are at the root.
        this.currentNode = null;

        // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
        vm.setNextFrameIsAlreadyLoaded(true);
        // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
		vm.setReturnFromCurrentExecution(true);
    }

    @Override
    public void recordChoice(Choice result) {
        // "Replace" the originating UnevaluatedST with its result.
        super.recordChoice(result);
        // In BFS, after a Choice is recorded, backtracking will follow in order to get to another node on the same level. Therefore, record this field to prepare backtracking.
        this.freshChoiceRecorded = result;

        Stack<UnevaluatedST> pushTo;
        if (depthOfChoice(result) < this.currentMaximumDepth) {
            pushTo = this.nextNodes;
        } else {
            pushTo = this.nextNodeStack;
        }

        // Push search trees in reverse order so they will be popped from left-to-right.
        ListIterator<UnevaluatedST> stIt = result.getSts().listIterator(result.getSts().size());

        while (stIt.hasPrevious()) {
            pushTo.push(stIt.previous());
        }
    }

    private int depthOfChoice(Choice result) {
        int depth = 0;
        Choice visit = result;
        while (visit.getParent() != null) {
            depth++;
            visit = visit.getParent();
        }
        return depth;
    }

    protected void trackBackUntil(Choice until, Choice from, boolean firstChoicesDecisionWasApplied, boolean constructForwardTrail, LogicVirtualMachine vm) {
        Choice nextChoice = from;
        while (nextChoice != until) {
            // Skip the following only for the first choice, and only if its decision has not been applied yet.
            if (nextChoice != from || firstChoicesDecisionWasApplied) {
                // Remove the this choice's constraint from the system.
                vm.getSolverManager().removeConstraint();
            }

            // Get the current stacks.
            StackToTrailWithInverse operandStack = (StackToTrailWithInverse) vm.getCurrentFrame().getOperandStack();
            StackToTrailWithInverse vmStack = (StackToTrailWithInverse) vm.getStack();

            // Set the StackToTrailWithInverse instances to restoring mode. Otherwise the recovery will be added to the trail, which will lead to weird behavior.
            operandStack.setRestoringMode(true);
            vmStack.setRestoringMode(true);

            // Empty the trail.
            Stack<TrailElement> trail = nextChoice.getTrail();
            // Optionally, create a forward trail using inverses of the previous trail.
            // The forward trail is only needed if there ever is a chance that we will go down this path later.
            Stack<TrailElement> inverseTrail = constructForwardTrail ? nextChoice.getInverseTrail() : null;
            while (!trail.empty()) {
                final TrailElement trailElement = trail.pop();
                operandStack = applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
            }

            // Set the correct Frame to be the current Frame.
            vm.setCurrentFrame(nextChoice.getSubstitutedUnevaluatedST().getFrame());

            // If the frame was set to have finished the execution normally, reset that.
            ((LogicFrame) vm.getCurrentFrame()).resetExecutionFinishedNormally();

            // Set the pc!
            vm.getCurrentFrame().setPc(nextChoice.getSubstitutedUnevaluatedST().getPc());
            vm.setPC(nextChoice.getSubstitutedUnevaluatedST().getPc());


            // Disable the restoring mode.
            operandStack.setRestoringMode(false);
            vmStack.setRestoringMode(false);

            nextChoice = nextChoice.getParent();
        }
    }

    /**
     * Return a String representation of this search algorithms name.
     * @return A String representation of this search algorithms name.
     */
    public String getName() {
        return "iterative deepening (naïve)";
    }

}
