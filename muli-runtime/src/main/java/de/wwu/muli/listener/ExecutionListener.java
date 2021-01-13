package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muli.defuse.DefUseMethod;

import java.util.Map;

public interface ExecutionListener {

    Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame);

    void afterExecuteInstruction(Instruction instruction, Frame frame, int pc);

    void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, Exception ex);

    void setCoverageListener();

    void reachedEndEvent();

    void backtrack(); // TODO Parametrization, where should be used?

    Map<Object, Object> getResult();

}