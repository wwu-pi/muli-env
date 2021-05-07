package de.wwu.muli.iteratorsearch;

import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.Stack;

/**
 * This class implements the iterative deepening DFS search algorithm along the ST
 * search tree structure.
 * <br />
 *
 * @author Jan C. Dageförde
 *
 */
public class IterativeDeepeningDFS extends IterativeDeepeningDFSNaive {


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

        Choice trackBackTo = null;
        if (this.currentNode != null) {
            trackBackTo = findCommonAncestor(this.currentNode, node);

            if (this.freshChoiceRecorded != null) {
                Choice from = this.freshChoiceRecorded;
                this.freshChoiceRecorded = null;

                // There is no active trail, but track back from the last recorded choice upwards. We will need to come back to this choice later.
                trackBackUntil(trackBackTo, from, false, true, vm);
            } else {
                // We were at a leaf node, to which we won't need to descend anymore.
                trackBackTheActiveTrail(vm);

                // Now track back until the root using individual trails from the choices along the path.
                trackBackUntil(trackBackTo, this.currentNode.getParent(), true, true, vm);
            }
            vm.setReturnFromCurrentExecution(true);
            vm.setNextFrameIsAlreadyLoaded(true);
        }

        return navigateTo(trackBackTo, node, vm);
    }

    @Override
    protected void trackBackUntil(Choice until, Choice from, boolean firstChoicesDecisionWasApplied, boolean constructForwardTrail, LogicVirtualMachine vm) {
        // First perform backtracking as in naive BFS.
        super.trackBackUntil(until, from, firstChoicesDecisionWasApplied, constructForwardTrail, vm);

        // If until is not null, i.e. a choice, remove the constraint that resulted from that choice's last decision.
        // This is necessary so that we can subsequently take the next decision (whose constraint likely contradicts the former one).
        if (until != null) {
            vm.getSolverManager().removeConstraint();
        }
    }

    @Override
    public boolean trackBackAndTakeNextDecision(LogicVirtualMachine vm) {
        if (this.nextNodes.isEmpty() && this.nextNodeStack.isEmpty()) {
            trackBackTheActiveTrail(vm);
            trackBackToRoot(vm);
            return false;
        }

        return takeNextDecision(vm);
    }

    /**
     * Return a String representation of this search algorithms name.
     * @return A String representation of this search algorithms name.
     */
    public String getName() {
        return "iterative deepening";
    }
}
