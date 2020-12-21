package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;

public interface ExecutionPathListener {

    public void executedInstruction(Instruction instruction, Frame frame, int pc);

    public void reachedEndEvent();


}
