package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.ArrayDeque;
import java.util.ListIterator;
import java.util.Stack;

/**
 * This class implements the breadth first search algorithm along the ST
 * search tree structure.<br />
 * <br />
 *
 * @author Jan C. Dageförde and Finn Teegen
 *
 */
public class BreadthFirstSearch extends AbstractSearchAlgorithm {
    /**
     * Queue that represents the nodes that BFS will check.
     */
    protected ArrayDeque<UnevaluatedST> nextNodes;
    protected Choice freshChoiceRecorded = null;

    /**
	 * Instantiate the depth first search algorithm.
     */
	public BreadthFirstSearch() {
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
            this.nextNodes = new ArrayDeque<>();
            // Add to the end of the queue.
            this.nextNodes.add((UnevaluatedST)this.searchTree);
        }

        if (this.nextNodes.isEmpty()) {
            return false;
        }

        // Get next node from the beginning of the queue.
        final UnevaluatedST node = this.nextNodes.remove();

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
            if (choice.getSubstitutedUnevaluatedST().getConstraintExpression() != null) {
                vm.getSolverManager().addConstraint(choice.getSubstitutedUnevaluatedST().getConstraintExpression());
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
        // Add new choices to the end of the queue.
        this.nextNodes.addAll(result.getSts());
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
        return "breadth-first search (naïve)";
    }

}
