package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.util.*;

public class InstructionCoverageListener implements ExecutionPathListener {

    protected List<CoveragePath> paths; // TODO besser Hashmap? Aber dann mit welchem Attribut?
    protected CoveragePath currentPath;

    public InstructionCoverageListener(){
        paths = new ArrayList<CoveragePath>();
        currentPath = new CoveragePath();
    }

    @Override
    public void executedInstruction(Instruction instruction, Frame frame, int pc) {
        currentPath.addInstruction(instruction);
    }

    public void reachedEndEvent() {
        paths.add(currentPath);
        currentPath = new CoveragePath();
    }

    public ArrayList<Map<Object, Object>> getResult(){
        throw new IllegalStateException("Not yet implemented.");
    }

    public Map<String, Object> getCover(String method, LogicVirtualMachine vm) {return null;}

    @Override
    public void setMethodName(String methodName) {

    }

}
