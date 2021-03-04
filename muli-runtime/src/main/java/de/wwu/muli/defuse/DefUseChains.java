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

    public void joinVisitedChains(DefUseChains defuse){
        Iterator<DefUseChain> it = defUseChains.iterator();
        Iterator<DefUseChain> it2 = defuse.defUseChains.iterator();
        while(it.hasNext() && it2.hasNext()){
            DefUseChain chain = it.next();
            DefUseChain copy = it2.next();
            if(copy.getVisited()){
                chain.setVisited(copy.getVisited());
            }
        }
    }

    public String toString(){
        String output = "";
        for(DefUseChain chain : defUseChains){
            output += "\r\n"+chain.toString();
        }
        return output;
    }
}
