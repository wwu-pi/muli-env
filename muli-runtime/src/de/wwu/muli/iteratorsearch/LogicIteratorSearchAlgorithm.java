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
import de.wwu.muli.vm.LogicVirtualMachine;

public interface LogicIteratorSearchAlgorithm extends SearchAlgorithm {
    ChoicePoint getCurrentChoicePoint();

	boolean trackBack(LogicVirtualMachine vm);

	void recoverState(LogicVirtualMachine vm);

	void generateNewChoicePoint(LogicVirtualMachine vm, int localVariableIndex, Generator generator)
			throws ConversionException, ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine vm, String type) throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine vm, GeneralInstructionWithOtherBytes instruction,
                                ConstraintExpression constraintExpression);

	void generateNewChoicePoint(LogicVirtualMachine vm, LCmp instruction,
                                Term leftTerm, Term rightTerm)
			throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine vm, CompareFp instruction,
                                boolean less, Term leftTerm, Term rightTerm)
			throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine vm, Switch instruction, Term termFromStack,
                                IntConstant[] keys, int[] pcs, IntConstant low, IntConstant high)
			throws ExecutionException;

}
