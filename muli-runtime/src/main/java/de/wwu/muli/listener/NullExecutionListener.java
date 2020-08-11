package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;

public class NullExecutionListener implements ExecutionListener {

    @Override
    public Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        return instruction;
    }

    @Override
    public void afterExecuteInstruction(Instruction instruction, Method method, Frame frame) {}

    @Override
    public void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, Exception ex) {}

    @Override
    public void backtrack() {}
}