package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.BitSet;
import java.util.Map;

public interface ExecutionPathListener {

    public void executedInstruction(Instruction instruction, Frame frame, int pc);

    public void reachedEndEvent();

    public Map<Object, Object> getResult();

    public boolean[] getCover(String method, LogicVirtualMachine vm);

    public void setMethodName(String methodName);


}
