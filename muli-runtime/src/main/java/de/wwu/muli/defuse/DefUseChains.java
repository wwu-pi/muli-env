package de.wwu.muli.defuse;

import java.util.HashSet;

/**
 * Class collecting defuse chains
 */
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

    public int getChainSize(){
        return defUseChains.size();
    }

    public DefUseChain getDefUseChain(int defPc, int usePc){
        for(DefUseChain chain: defUseChains){
            if(chain.getUse().getPc() == usePc && chain.getDef().getPc() == defPc){
                return chain;
            }
        }
        throw new IllegalStateException("DefUseChain with specified Pcs does not exist.");
    }

    public void mergeChains(DefUseChains chain) {
        HashSet<DefUseChain> mChain = chain.getDefUseChains();
        defUseChains.addAll(mChain);
    }

    public String toString(){
        String output = "";
        for(DefUseChain chain : defUseChains){
            output += "\r\n"+chain.toString();
        }
        return output;
    }
}
