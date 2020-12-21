package de.wwu.muli.defuse;
import de.wwu.muggl.symbolic.flow.defUseChains.structures.Def;
import de.wwu.muggl.vm.classfile.structures.Method;

import java.util.HashSet;

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

    public void initDefUses(){
        this.defUses = new DefUseChains();
    }

    public void visitDefUse(int instruction){
        visitDef(instruction);
        visitUse(instruction);
    }

    public void visitDef(int instruction){
        if(defs.hasEntry(instruction)) {
            defs.setVisited(instruction);
        }
    }

    public void visitUse(int instruction){
        if(uses.hasEntry(instruction)) {
            DefUseRegister r = uses.registers.get(instruction);
            uses.setVisited(instruction);
            for(int link : r.link.descendingSet()){
                DefUseRegister rDefs = defs.registers.get(link);
                if(rDefs.visited) {
                    DefVariable def = new DefVariable(link);
                    UseVariable use = new UseVariable(instruction);
                    DefUseChain chain = new DefUseChain(def, use);
                    if(!defUses.getDefUseChains().contains(chain)) {
                        defUses.addChain(chain);
                    }
                    break;
                }
            }
        }
    }
}
