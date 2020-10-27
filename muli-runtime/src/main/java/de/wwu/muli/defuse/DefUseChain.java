package de.wwu.muli.defuse;

import java.util.Objects;

public class DefUseChain {
    private DefVariable def;
    private UseVariable use;

    public DefUseChain(DefVariable def, UseVariable use) {
        this.def = def;
        this.use = use;
    }

    public DefVariable getDef(){
        return this.def;
    }

    public UseVariable getUse(){
        return this.use;
    }

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
}
