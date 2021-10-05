package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.ArrayList;
import java.util.Map;

public interface ExecutionPathListener {

    public void executedInstruction(Instruction instruction, Frame frame, int pc);

    public void reachedEndEvent();

    public ArrayList<Map<Object, Object>> getResult();

    public Map<String, int[]> getCover(String method, LogicVirtualMachine vm);

    public void setMethodName(String methodName);


}
