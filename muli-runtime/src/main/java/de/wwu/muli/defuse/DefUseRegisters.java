package de.wwu.muli.defuse;

import java.util.Map;
import java.util.HashMap;

/**
 * Class to enclose all definitions or usages for one method
 */
public class DefUseRegisters {
    public Map<Integer, DefUseRegister> registers = new HashMap<>();

    public void addRegister(DefUseRegister r, int index) {
        registers.put(index, r);
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

    public DefUseRegister getRegister(int instruction){
        return registers.get(instruction);
    }

    public int getRegisterSize(){
        return registers.size();
    }

    public String toString(){
        String output = "";
        for(Integer i : registers.keySet()){
            output += "\r\n   PC: " + i;
            output += "; " + registers.get(i).toString();
        }
        return output;
    }
}
