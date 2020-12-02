package de.wwu.muli.defuse;

import java.util.Map;
import java.util.HashMap;

public class DefUseRegisters {
    public Map<Integer, DefUseRegister> registers = new HashMap<>();

    public void addRegister(DefUseRegister r, int i) {
        registers.put(i, r);
    }

    public boolean isVisited(int i){
        DefUseRegister r = registers.get(i);
        return r.visited;
    }

    public void setVisited(int instruction){
        for(Map.Entry<Integer, DefUseRegister> pair : registers.entrySet()) {
            DefUseRegister r = pair.getValue();
            if(r.instructionIndex == instruction){
                r.visited = true;
            }
        }
    }
}
