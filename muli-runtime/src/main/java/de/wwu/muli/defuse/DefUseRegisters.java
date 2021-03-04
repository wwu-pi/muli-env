package de.wwu.muli.defuse;

import java.util.HashSet;
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

    public boolean isVisited(int instruction){
        DefUseRegister r = registers.get(instruction);
        return r.visited;
    }

    public void setRegisters(Map<Integer, DefUseRegister> registers ){
        this.registers = registers;
    }

    public HashSet<Integer> getVisited(){
        HashSet<Integer> output = new HashSet<>();
        for(Map.Entry<Integer, DefUseRegister> entry : registers.entrySet()){
            if(entry.getValue().visited){
                output.add(entry.getKey());
                if(entry.getKey() != -1){
                    entry.getValue().visited = false;
                }
            }
        }
        return output;
    }

    public void joinRegister(DefUseRegisters r){
        for(Map.Entry<Integer, DefUseRegister> entry : registers.entrySet()){
            if(r.registers.get(entry.getKey()).visited){
                setVisited(entry.getKey());
            }
        }
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

    public int getDefUseSize() {
        int out = 0;
        for(Map.Entry<Integer, DefUseRegister> entry : registers.entrySet()){
            DefUseRegister reg = entry.getValue();
            out = out + reg.link.size();
        }
        return out;
    }

    public DefUseRegisters clone(){
        DefUseRegisters r = new DefUseRegisters();
        HashMap<Integer,DefUseRegister> output = new HashMap<>();
        for(Map.Entry<Integer, DefUseRegister> entry : registers.entrySet()){
            output.put(entry.getKey(), entry.getValue().clone());
        }
        r.setRegisters(output);
        return r;
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
