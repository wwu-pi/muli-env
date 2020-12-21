package de.wwu.muli.defuse;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class DefUseRegisters {
    public Map<Integer, DefUseRegister> registers = new HashMap<>();

    public void addRegister(DefUseRegister r, int index) {
        registers.put(index, r);
    }

    public Boolean isVisited(int i){
        for(Map.Entry<Integer, DefUseRegister> pair : registers.entrySet()) {
            DefUseRegister r = pair.getValue();
            if(r.link.contains(i)) {
                return r.visited;
            }
        }
        return null;
    }

    public void setVisited(int instruction){
        DefUseRegister r = registers.get(instruction);
        r.visited = true;
    }

    public boolean hasEntry(int index) {
        return registers.containsKey(index);
    }

    public void addLink(int instruction, int link){
        DefUseRegister r = registers.get(instruction);
        r.addLink(link);
    }
}
