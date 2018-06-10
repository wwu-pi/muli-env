package de.wwu.muli.iteratorsearch;

import de.wwu.muggl.instructions.bytecode.LCmp;
import de.wwu.muggl.instructions.general.CompareFp;
import de.wwu.muggl.instructions.general.GeneralInstructionWithOtherBytes;
import de.wwu.muggl.instructions.general.Switch;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.ArrayRestore;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.FieldPut;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.Restore;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muli.vm.LogicVirtualMachine;

public class NoSearchAlgorithm implements LogicIteratorSearchAlgorithm {
    private static NoSearchAlgorithm instance = new NoSearchAlgorithm();
    public static NoSearchAlgorithm getInstance() {
        return NoSearchAlgorithm.instance;
    }

    @Override
    public ChoicePoint getCurrentChoicePoint() {
        return null;
    }

    @Override
    public boolean trackBack(LogicVirtualMachine vm) {
        return false;
    }

    @Override
    public boolean trackBackLocallyNextChoice(LogicVirtualMachine vm) {
        return false;
    }

    @Override
    public boolean changeToNextChoice(LogicVirtualMachine vm) {
        throw new IllegalStateException("unexpected state: Trying to change to next choice, but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, int localVariableIndex, Generator generator) throws ConversionException, ExecutionException {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP1), but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, String type) throws ExecutionException {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP2), but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, GeneralInstructionWithOtherBytes instruction, ConstraintExpression constraintExpression) {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP3), but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, LCmp instruction, Term leftTerm, Term rightTerm) throws ExecutionException {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP4), but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, CompareFp instruction, boolean less, Term leftTerm, Term rightTerm) throws ExecutionException {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP5), but no search algorithm initialised.");
    }

    @Override
    public void generateNewChoicePoint(LogicVirtualMachine vm, Switch instruction, Term termFromStack, IntConstant[] keys, int[] pcs, IntConstant low, IntConstant high) throws ExecutionException {
        throw new IllegalStateException("unexpected state: Trying to add a choicepoint (CP6), but no search algorithm initialised.");
    }

    @Override
    public String getName() {
        return "NoSearchAlgorithm";
    }

    @Override
    public boolean savingLocalVariableValues() {
        return false;
    }

    @Override
    public void saveLocalVariableValue(Restore valueRepresentation) {
        throw new IllegalStateException("unexpected state: Trying to save local variable values, but no search algorithm initialised.");
    }

    @Override
    public boolean savingFieldValues() {
        return false;
    }

    @Override
    public void saveFieldValue(FieldPut valueRepresentation) {
        throw new IllegalStateException("unexpected state: Trying to save field values, but no search algorithm initialised.");
    }

    @Override
    public boolean savingArrayValues() {
        return false;
    }

    @Override
    public void saveArrayValue(ArrayRestore valueRepresentation) {
        throw new IllegalStateException("unexpected state: Trying to save array values, but no search algorithm initialised.");
    }
}
