package de.wwu.muli.defuse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import de.wwu.muggl.vm.classfile.structures.Method;

public class UseVariable {
    public int instructionIndex;
    public int pc;
    public Map<String, Integer> indexOverMethods = new HashMap<String, Integer>();
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

    public void addMethodIndex(String name, int i) {
        indexOverMethods.put(name, i);
    }

    public Map<String, Integer> getIndexOverMethods(){
        return this.indexOverMethods;
    }

    public void setIndexOverMethods(Map<String, Integer> indexOverMethods){
        this.indexOverMethods = indexOverMethods;
    }

    public Boolean moreThanOneMethod() {
        return indexOverMethods.size() > 1;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof UseVariable)) {
            return false;
        } else {
            UseVariable use = (UseVariable) obj;
            if (this.instructionIndex == use.getInstructionIndex() && this.pc == use.getPc() && this.method.getFullName().equals(use.getMethod().getFullName())) {
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
