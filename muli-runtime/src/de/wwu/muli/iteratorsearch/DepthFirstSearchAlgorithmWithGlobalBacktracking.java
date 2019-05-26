package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.*;
import de.wwu.muli.iteratorsearch.structures.StackToTrailWithInverse;
import de.wwu.muli.searchtree.*;
import de.wwu.muli.vm.LogicFrame;
import de.wwu.muli.vm.LogicVirtualMachine;

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
 * may occur anywhere, not just at the end of the program. -JD 15.03.19
 *
 * Major modifications in order to incorporate the ST structure. -JD w. Finn Teegen, 26.05.19
 *
 * TODO rephrase description to account for new mechanism of iterator-based search and ST structure.
 *
 * @author Jan C. Dageförde and Finn Teegen
 *
 */
public class DepthFirstSearchAlgorithmWithGlobalBacktracking extends AbstractSearchAlgorithm {
    /**
     * Stack that represents the nodes that DFS will check subsequently.
     */
    protected Stack<UnevaluatedST> nextNodes;

    /**
	 * Instantiate the depth first search algorithm.
     */
	public DepthFirstSearchAlgorithmWithGlobalBacktracking() {
        super();
    }

    public boolean takeNextDecision(LogicVirtualMachine vm) {
        // Evaluate next subtree.
        if (this.searchTree == null) {
            this.searchTree = new UnevaluatedST(vm.getCurrentFrame(), vm.getPc(), null, null);
            this.nextNodes = new Stack<>();
            this.nextNodes.push((UnevaluatedST)this.searchTree);
        }

        if (this.nextNodes.empty()) {
            return false;
        }

        final UnevaluatedST node = this.nextNodes.pop();

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
                    applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
                }

                // Disable the restoring mode.
                operandStack.setRestoringMode(false);
                vmStack.setRestoringMode(false);

                // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
                vm.setNextFrameIsAlreadyLoaded(true);
                // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
                vm.setReturnFromCurrentExecution(true);
            }
        }

        this.currentNode = node;

        // Add constraint.
        if (node.getConstraintExpression() != null) {
            vm.getSolverManager().addConstraint(node.getConstraintExpression());

            // Check if the new branch can be visited at all, or if it causes an equation violation.
            try {
                if (!vm.getSolverManager().hasSolution()) {
                    // Constraint system of this subtree is not satisfiable, try next.
                    node.setEvaluationResult(new Fail());
                    return trackBackAndTakeNextDecision(vm);
                }
            } catch (TimeoutException | SolverUnableToDecideException e) {
                // Potentially inconsistent, try next.
                node.setEvaluationResult(new Fail());
                return trackBackAndTakeNextDecision(vm);
            }
        }

        // Everything is fine and we need to continue in this subtree.
        // Set the current frame to the subtree's frame.
        vm.setCurrentFrame(node.getFrame());

        // Set the current pc to the subtree's pc!
        vm.getCurrentFrame().setPc(node.getPc());
        vm.setPC(node.getPc());

        // Evaluate.
        return true;
    }

    @Override
    public void trackBackToRoot(LogicVirtualMachine vm) {
        // Perform local backtracking to revert all effects since the last choice.
        trackBackTheActiveTrail(vm);

        // Now track back until the root using individual trails from the choices along the path.
        trackBackUntil(null, true, vm);

        // We are at the root.
        this.currentNode = null;

        // Signalize to the virtual machine that no Frame has to be popped but execution can be resumed with the current Frame.
        vm.setNextFrameIsAlreadyLoaded(true);
        // If this tracking back is done while executing a Frame, also signalize to the vm to not continue executing it.
		vm.setReturnFromCurrentExecution(true);
    }

    @Override
    public boolean trackBackAndTakeNextDecision(LogicVirtualMachine vm) {
        trackBackToRoot(vm);
        return takeNextDecision(vm);
    }

    @Override
    public void recordChoice(Choice result) {
        // "Replace" the originating UnevaluatedST with its result.
        super.recordChoice(result);

        // Push search trees in reverse order so they will be popped from left-to-right.
        ListIterator<UnevaluatedST> stIt = result.getSts().listIterator(result.getSts().size());

        while (stIt.hasPrevious()) {
            this.nextNodes.push(stIt.previous());
        }
    }

    protected void trackBackUntil(Choice until, boolean constructForwardTrail, LogicVirtualMachine vm) {
        Choice nextChoice = this.currentNode.getParent();
        while (nextChoice != until) {
            // Remove the this choice's constraint from the system.
            vm.getSolverManager().removeConstraint();

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
                applyTrailElement(trailElement, vm, operandStack, vmStack, inverseTrail);
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
        return "depth first (global backtracking)";
    }

}
