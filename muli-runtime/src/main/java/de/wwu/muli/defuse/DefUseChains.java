package de.wwu.muli.defuse;

import java.util.HashSet;

public class DefUseChains {

    private HashSet<DefUseChain> defUseChains = new HashSet<DefUseChain>();

    public HashSet<DefUseChain>  getDefUseChains() {
        return defUseChains;
    }

    public void setDefUseChains(HashSet<DefUseChain> defUseChains){
        this.defUseChains = defUseChains;
    }

    public void addChain(DefUseChain chain){
        defUseChains.add(chain);
    }
}
