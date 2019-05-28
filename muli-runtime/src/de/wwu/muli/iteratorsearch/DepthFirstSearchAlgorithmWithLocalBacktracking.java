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
 */
public class DepthFirstSearchAlgorithmWithLocalBacktracking extends DepthFirstSearchAlgorithmWithGlobalBacktracking {
    /**
	 * Instantiate the depth first search algorithm.
     */
	public DepthFirstSearchAlgorithmWithLocalBacktracking() {
        super();
	}

    @Override
    public boolean trackBackAndTakeNextDecision(LogicVirtualMachine vm) {
        if (this.nextNodes.empty()) {
            return false;
        }

        final UnevaluatedST node = this.nextNodes.pop();

        if (node.isEvaluated()) {
            throw new IllegalStateException("Node must correspond to an unevaluated subtree.");
        }

        Choice correspondingChoice = node.getParent();

        // Perform local backtracking to revert all effects since the last choice.
        trackBackTheActiveTrail(vm);

        // Now track back until the selected choice using individual trails from the choices along the path.
        trackBackUntil(correspondingChoice, false, vm);

        // Remove this choice's previous constraint from the system.
        vm.getSolverManager().removeConstraint();

        // Now activate the next subtree by imposing its constraint and setting current frame / pc accordingly.
        // If the resulting constraint system is infeasible, switch to the next possible decision immediately.
        // Otherwise, return false.
        return implementNextDecisionAndDescendIntoSubtree(node, vm);
    }

    /**
	 * Return a String representation of this search algorithms name.
	 * @return A String representation of this search algorithms name.
	 */
	@Override
	public String getName() {
		return "depth-first (local backtracking)";
	}

}
