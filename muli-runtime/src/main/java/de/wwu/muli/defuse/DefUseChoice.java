package de.wwu.muli.defuse;

import java.util.HashSet;

/**
 * Class representing the analyzed variable definitions and usages for each method and incrementally
 * keeps track of the passed defUse chains
 */
public class DefUseChoice {

    private HashSet<Integer> defs;
    private HashSet<Integer> uses;

    public DefUseChoice (DefUseMethod defuse){
        DefUseRegisters def = defuse.getDefs();
        DefUseRegisters use = defuse.getUses();
        defs = def.getVisited();
        uses = use.getVisited();
    }

    public void addDefs(HashSet<Integer> defs){
        this.defs.addAll(defs);
    }

    public void addUses(HashSet<Integer> uses){
        this.uses.addAll(uses);
    }

    public HashSet<Integer> getDefs(){
        return defs;
    }

    public HashSet<Integer> getUses(){
        return uses;
    }

    public String toString(){
        String output = "";
        output += "Defs: " + defs.toString() + "\r\n";
        output += "Uses: " + uses.toString() + "\r\n";
        return output;
    }
}
