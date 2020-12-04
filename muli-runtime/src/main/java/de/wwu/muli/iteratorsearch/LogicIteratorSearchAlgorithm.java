package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.instructions.bytecode.LCmp;
import de.wwu.muggl.instructions.general.CompareFp;
import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.search.SearchAlgorithm;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import de.wwu.muli.vm.LogicVirtualMachine;

public interface LogicIteratorSearchAlgorithm extends SearchAlgorithm {
    @Deprecated
    ChoicePoint getCurrentChoicePoint();

    Choice getCurrentChoice();

    boolean isActivelySearching();

    void recordChoice(Choice result);

    void recordValue(Value result);

    void recordException(de.wwu.muli.searchtree.Exception exception);

    void recordFail(Fail result);

    void trackBackToRoot(LogicVirtualMachine vm);

    boolean trackBackAndTakeNextDecision(LogicVirtualMachine vm);

    boolean takeNextDecision(LogicVirtualMachine vm);

    @Deprecated // Use Choice-objects instead
    boolean trackBackLocallyNextChoice(LogicVirtualMachine vm);

    @Deprecated // Use Choice-objects instead
    boolean trackBack(LogicVirtualMachine vm);

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, int localVariableIndex, Generator generator)
			throws ConversionException, ExecutionException;

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, String type) throws ExecutionException;

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, GeneralInstructionWithOtherBytes instruction,
                                ConstraintExpression constraintExpression);

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, LCmp instruction,
                                Term leftTerm, Term rightTerm)
			throws ExecutionException;

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, CompareFp instruction,
                                boolean less, Term leftTerm, Term rightTerm)
			throws ExecutionException;

    @Deprecated // Use Choice-objects instead
	void generateNewChoicePoint(LogicVirtualMachine vm, Switch instruction, Term termFromStack,
                                IntConstant[] keys, int[] pcs, IntConstant low, IntConstant high)
			throws ExecutionException;

    public ST getSearchTreeDebug();
}
