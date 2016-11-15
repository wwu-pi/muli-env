package de.wwu.muli.env.search;

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

public interface LogicSearchAlgorithm extends SearchAlgorithm {
	ChoicePoint getCurrentChoicePoint();

	boolean trackBack(LogicVirtualMachine arg0);

	void recoverState(LogicVirtualMachine arg0);

	void generateNewChoicePoint(LogicVirtualMachine arg0, int arg1, Generator arg2)
			throws ConversionException, ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine arg0, String arg1) throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine arg0, GeneralInstructionWithOtherBytes arg1,
			ConstraintExpression arg2);

	void generateNewChoicePoint(LogicVirtualMachine arg0, LCmp arg1, Term arg2, Term arg3)
			throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine arg0, CompareFp arg1, boolean arg2, Term arg3, Term arg4)
			throws ExecutionException;

	void generateNewChoicePoint(LogicVirtualMachine arg0, Switch arg1, Term arg2, IntConstant[] arg3, int[] arg4,
			IntConstant arg5, IntConstant arg6) throws ExecutionException;

}
