package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;

public class SimpleTCGExecutionListener implements ExecutionListener {

    @Override
    public Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        return instruction; // TODO
    }

    @Override
    public void afterExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        // TODO
    }

    @Override
    public void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, Exception ex) {
        // TODO
    }
}