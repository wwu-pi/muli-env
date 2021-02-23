package de.wwu.muli.defuse;

/**
 * Class representing the analyzed variable definitions and usages for each method and incrementally
 * keeps track of the passed defUse chains
 */
public class DefUseMethod {

    private DefUseRegisters defs;
    private DefUseRegisters uses;
    private DefUseChains defUses;

    public void setDefs(DefUseRegisters defs){
        this.defs = defs;
    }

    public void setUses(DefUseRegisters uses){
        this.uses = uses;
    }

    public void setDefUses(DefUseChains chains) {this.defUses = chains;}

    public void initDefUses(){
        this.defUses = new DefUseChains();
    }

    public DefUseChains getDefUses(){
        return defUses;
    }

    public DefUseRegisters getDefs(){
        return defs;
    }

    public DefUseRegisters getUses(){
        return uses;
    }

    public void visitDefUse(int instruction){
        visitDef(instruction);
        visitUse(instruction);
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
                    DefUseChain chain = defUses.getDefUseChain(instruction, link);
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
                    DefUseChain chain = defUses.getDefUseChain(link, instruction);
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

    public String toString(){
        String output = "";
        output += "Defs: " + defs.toString() + "\r\n";
        output += "Uses: " + uses.toString() + "\r\n";
        output += "DefUse: " + defUses.toString();
        return output;
    }
}
