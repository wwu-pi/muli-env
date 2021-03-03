package de.wwu.muli.defuse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Class representing the analyzed variable definitions and usages for each method and incrementally
 * keeps track of the passed defUse chains
 */
public class DefUseChoice {

    private DefUseRegisters defs;
    private DefUseRegisters uses;
    private DefUseChains defuse;
    private boolean[] visited;
    private boolean newInstance;

    public DefUseChoice (DefUseMethod defuse){
        defs = defuse.getDefs().clone();
        uses = defuse.getUses().clone();
        this.defuse = new DefUseChains();
        this.defuse.setDefUseChains(defuse.getDefUses().copyChains());
        visited = new boolean[defuse.getDefUses().getChainSize()];
        newInstance = true;
    }

    public void addDefs(DefUseRegisters registers){
        this.defs.joinRegister(registers);
    }

    public void addUses(DefUseRegisters registers){
        this.uses.joinRegister(registers);
    }

    public void addDefUses(DefUseChains defuse){
        this.defuse.joinVisitedChains(defuse);
    }

    public DefUseRegisters getDefs(){
        return defs;
    }

    public DefUseRegisters getUses(){
        return uses;
    }

    public DefUseChains getDefUse(){
        return defuse;
    }

    public boolean getNewInstance(){return newInstance;}

    public void visitDefUse(int instruction){
        newInstance = false;
        visitDef(instruction);
        visitUse(instruction);
    }

    public boolean[] getVisited(){return visited;}

    public void setVisited(boolean[] visited){
        this.visited = visited;
    }

    /**
     * If it is a definition, it is set to visited. If related usages have also been already passed,
     * the corresponding defuse chain is added.
     * @param instruction instruction pc
     */
    public void visitDef(int instruction){
        if(defs.hasEntry(instruction)) {
            defs.setVisited(instruction);
            DefUseRegister r = defs.getRegister(instruction);
            for(int link : r.link){
                DefUseRegister rUses = uses.getRegister(link);
                if(rUses.visited) {
                    DefUseChain chain = defuse.getDefUseChain(instruction, link);
                    chain.setVisited(true);
                    /*DefVariable def = new DefVariable(instruction);
                    UseVariable use = new UseVariable(link);
                    DefUseChain chain = new DefUseChain(def, use);
                    if(!defUses.getDefUseChains().contains(chain)) {
                        defUses.addChain(chain);
                    }*/
                }
            }
        }
    }

    public void updateDefUse(){
        for (DefUseChain chain: defuse.getDefUseChains()) {
            if(!chain.getVisited() && defs.isVisited(chain.getDef().getPc()) && uses.isVisited(chain.getUse().getPc())){
                DefUseRegister r = uses.getRegister(chain.getUse().getPc());
                if(r.link.size() > 1){
                    boolean setTrue = true;
                    for(int def: r.link.descendingSet()){
                        if(def != chain.getDef().getPc() && defs.isVisited(def)){
                            setTrue = false;
                            break;
                        } else if(def == chain.getDef().getPc()){
                            break;
                        }
                    }
                    if(setTrue){
                        chain.setVisited(true);
                    }
                } else {
                    chain.setVisited(true);
                }

            }
        }
    }

    /**
     * If it is a usage, it is set to visited. If related definitions have also been already passed,
     * the corresponding defuse chain is added.
     * @param instruction instruction pc
     */
    public void visitUse(int instruction){
        if(uses.hasEntry(instruction)) {
            DefUseRegister r = uses.registers.get(instruction);
            uses.setVisited(instruction);
            // only consider the last visited definition
            for(int link : r.link.descendingSet()){
                DefUseRegister rDefs = defs.registers.get(link);
                if(rDefs.visited) {
                    DefUseChain chain = defuse.getDefUseChain(link, instruction);
                    chain.setVisited(true);
                    /*DefVariable def = new DefVariable(link);
                    UseVariable use = new UseVariable(instruction);
                    DefUseChain chain = new DefUseChain(def, use);
                    if(!defUses.getDefUseChains().contains(chain)) {
                        defUses.addChain(chain);
                    }*/
                    break;
                }
            }
        }
    }

    public void updateVisitedArray(){
        //List<UseVariable> visited = new ArrayList<>();
        //List<DefUseChain> visitedTwice = new ArrayList<>();
        int c = 0;
        for(Map.Entry<Integer, DefUseRegister> entry: uses.registers.entrySet()){
            DefUseRegister r = entry.getValue();
            if(r.visited){
                int c1 = c;
                for(int link : r.link.descendingSet()) {
                    DefUseRegister rDefs = defs.registers.get(link);
                    if (rDefs.visited) {
                        visited[c1] = true;
                        c = c + r.link.size();
                        break;
                    }
                    c1++;
                }
            } else {
                c = c + r.link.size();
            }
        }
    }

    public String toString(){
        String output = "";
        output += "Defs: " + defs.toString() + "\r\n";
        output += "Uses: " + uses.toString() + "\r\n";
        return output;
    }
}
