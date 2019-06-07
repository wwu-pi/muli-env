package de.wwu.muli.iteratorsearch;

import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.ArrayDeque;
import java.util.HashSet;

/**
 * This class implements the breadth first search algorithm along the ST
 * search tree structure.<br />
 * <br />
 *
 * @author Jan C. Dageförde and Finn Teegen
 *
 */
public class BreadthFirstSearchWithLocalBacktracking extends BreadthFirstSearch {
    /**
	 * Instantiate the depth first search algorithm.
     */
	public BreadthFirstSearchWithLocalBacktracking() {
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
        }

        return navigateTo(trackBackTo, node, vm);
    }

    private Choice findCommonAncestor(UnevaluatedST currentNode, UnevaluatedST nextNode) {
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
        if (this.nextNodes.isEmpty()) {
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
        return "breadth-first search";
    }

}
