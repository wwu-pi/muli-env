package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;

public interface ExecutionPathListener {

    public void executedInstruction(Instruction instruction);

    public void reachedSolutionEvent();


}
