package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;

public class TcgExecutionListener implements ExecutionListener {

    protected ExecutionPathListener executionPathListener;

    public void setCoverageListener() {
        executionPathListener = new InstructionCoverageListener();
    }

    @Override
    public Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        /*
        TODO If a putfield-bytecode-instruction is executed on a free input object, it should be stored
        so that the original input-object can be generated accordingly.
         */
        return instruction;
    }

    @Override
    public void afterExecuteInstruction(Instruction instruction) {
        /*
         TODO After an instruction was executed, it should be recorded by a DefUseListener or a CoverageListener.
         */
        executionPathListener.executedInstruction(instruction);

    }

    @Override
    public void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, Exception ex) {
        // TODO
    }

    @Override
    public void backtrack() {

    }

    @Override
    public void reachedEndEvent() {
        executionPathListener.reachedEndEvent();
    }
}