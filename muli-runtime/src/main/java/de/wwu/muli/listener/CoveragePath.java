package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class CoveragePath {
    protected Map<Instruction, Integer> instructions = new HashMap<Instruction, Integer>();
    protected BitSet bitInstructions = new BitSet();

    public void addInstruction(Instruction instruction){
        int id = instructions.size();
        instructions.put(instruction, id);
        bitInstructions.set(id);
    }
}
