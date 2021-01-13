package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muli.defuse.DefUseMethod;
import java.util.Map;

public class TcgExecutionListener implements ExecutionListener {

    protected ExecutionPathListener executionPathListener;

    public void setCoverageListener() {
        executionPathListener = new InstructionCoverageListener();
    }

    public void setDefUseListener(LogicVirtualMachine vm) {
        if(executionPathListener == null) {
            executionPathListener = new DefUseListener(vm);
        }
    }

    public Map<Object, Object> getResult(){
        return executionPathListener.getResult();
    }

    @Override
    public Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        /*
        TODO If a putfield-bytecode-instruction is executed on a free input object, it should be stored
        so that the original input-object can be generated accordingly.
         */
        return instruction;
    }


    public void afterExecuteInstruction(Instruction instruction, Frame frame, int pc) {
        /*
         TODO After an instruction was executed, it should be recorded by a DefUseListener or a CoverageListener.
         */
        if(executionPathListener != null) {
            executionPathListener.executedInstruction(instruction, frame, pc);
        }

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