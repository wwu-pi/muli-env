package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class InstructionCoverageListener implements ExecutionPathListener {

    protected List<CoveragePath> paths; // TODO besser Hashmap? Aber dann mit welchem Attribut?
    protected CoveragePath currentPath;

    public InstructionCoverageListener(){
        paths = new ArrayList<CoveragePath>();
        currentPath = new CoveragePath();
    }

    @Override
    public void executedInstruction(Instruction instruction) {
        currentPath.addInstruction(instruction);
    }

    public void reachedEndEvent() {
        paths.add(currentPath);
        currentPath = new CoveragePath();
    }

}
