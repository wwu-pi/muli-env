package de.wwu.muli.defuse;

import java.util.Objects;

/**
 * Class representing one instance of a defuse chain containing a variable definition and usage
 */
public class DefUseChain {
    private DefVariable def;
    private UseVariable use;
    private boolean visited;

    public DefUseChain(DefVariable def, UseVariable use) {
        this.def = def;
        this.use = use;
        this.visited = false;
    }

    public DefVariable getDef(){
        return this.def;
    }

    public UseVariable getUse(){
        return this.use;
    }

    public void setVisited(boolean visited){ this.visited=visited;}

    public boolean getVisited() {return this.visited;}

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof DefUseChain)) {
            return false;
        } else {
            DefUseChain chain = (DefUseChain) obj;
            if(this.def.equals(chain.getDef()) && this.use.equals(chain.getUse())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        return Objects.hash(this.getDef(), this.getUse());
    }

    public String toString(){
        String output = "";
        output += "   Def: pc=" + def.getPc() + ", Use: pc=" + use.getPc();
        return output;
    }
}
