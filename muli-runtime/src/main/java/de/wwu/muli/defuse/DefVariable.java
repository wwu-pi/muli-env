package de.wwu.muli.defuse;

import de.wwu.muggl.vm.classfile.structures.Method;

import java.util.Objects;

public class DefVariable {
    public int instructionIndex;
    public int pc;
    public Method method;

    public void setInstructionIndex(int i){
        this.instructionIndex = i;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public int getInstructionIndex() {
        return this.instructionIndex;
    }

    public int getPc() {
        return this.pc;
    }

    public Method getMethod() { return this.method; }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof DefVariable)) {
            return false;
        } else {
            DefVariable def = (DefVariable) obj;
            if (this.instructionIndex == def.getInstructionIndex() && this.pc == def.getPc() && this.method.getFullName().equals(def.getMethod().getFullName())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        return Objects.hash(this.getInstructionIndex(), this.getPc());
    }

}
