package de.wwu.muli.defuse;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Class collecting defuse chains
 */
public class DefUseChains {

    private HashSet<DefUseChain> defUseChains = new HashSet<DefUseChain>();

    public HashSet<DefUseChain>  getDefUseChains() {
        return defUseChains;
    }

    public HashSet<DefUseChain> copyChains(){
        HashSet<DefUseChain> copy = new HashSet<DefUseChain>();
        for(DefUseChain chain : defUseChains){
            DefUseChain defCopy = (DefUseChain) chain.clone();
            copy.add(defCopy);
        }
        return copy;
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

    public void mergeChains(DefUseChains chain) {
        HashSet<DefUseChain> mChain = chain.getDefUseChains();
        defUseChains.addAll(mChain);
    }

    public boolean containsChain(int def, int use){
        for(DefUseChain chain : defUseChains){
            if(chain.getDef().getPc() == def && chain.getUse().getPc() == use){
                return true;
            }
        }
        return false;
    }

    public String toString(){
        String output = "";
        for(DefUseChain chain : defUseChains){
            output += "\r\n"+chain.toString();
        }
        return output;
    }
}
