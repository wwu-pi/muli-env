package de.wwu.muli.defuse;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.bytecode.ALoad;
import de.wwu.muggl.instructions.bytecode.Putfield;
import de.wwu.muggl.instructions.general.Astore;
import de.wwu.muggl.instructions.general.Load;
import de.wwu.muggl.instructions.general.Store;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

import java.util.*;

/**
 * Class representing the analyzed variable definitions and usages for each method and incrementally
 * keeps track of the passed defUse chains
 */
public class DefUseChoice {

    private ArrayDeque<DefVariable>  defs;
    private DefUseChains defuse;
    private boolean newInstance;
    private boolean initialDefs;

    public DefUseChoice (){
        this.defuse = new DefUseChains();
        newInstance = true;
        initialDefs = false;
        defs = new ArrayDeque<>();
    }

    /**
     * Set initial definitions for the method parameters.
     * @param m method
     */
    public void setInitialDefs(Method m){
        int parameters = m.getNumberOfParameters();
        for (int k = 0; k < parameters; k++){
            DefVariable def = new DefVariable();
            def.setInstructionIndex(k);
            def.setPc(-1);
            def.setMethod(m);
            if(defExists(def, defs)) {
                defs.remove(def);
            }
            defs.add(def);
        }
        initialDefs = true;
    }

    public void addDefs(ArrayDeque<DefVariable> defs){
        for(DefVariable d:defs){
            if(!defExists(d, this.defs)){
                this.defs.add(d);
            }
        }
    }

    public void addDefUses(DefUseChains defuse){
        this.defuse.mergeChains(defuse);
    }

    public ArrayDeque<DefVariable> getDefs(){
        return defs;
    }

    public DefUseChains getDefUse(){
        return defuse;
    }

    public boolean getNewInstance(){return newInstance;}
    public boolean getInitialDefs(){return initialDefs;}
    public void setNewInstance(boolean newInstance){this.newInstance=newInstance;}
    public void setBoolInitialDefs(boolean initialDefs){this.initialDefs=initialDefs;}

    public void visitDefUse(Instruction instruction, int i, Method m){
        newInstance = false;
        if(instruction instanceof Store) {
            visitDef(instruction, i, m);
        } else if(instruction instanceof Astore || instruction instanceof Putfield) {
            // Special case of def -> write value in array or field of object
            try {
                getObjectDefs(i, m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(instruction instanceof Load) {
        // Usage of Variable
            UseVariable use = null;
            try {
                use = visitUse(instruction, i, m);
            } catch (Exception e) {
                e.printStackTrace();
            }
            DefVariable def = findDef(use, defs);
            if(def != null) {
                DefUseChain chain = new DefUseChain(def, use);
                defuse.addChain(chain);
            }
        }
    }

    /**
     * Define the Variable Definition and add it to definitions
     * @param instruction current instruction for definition
     * @param i current pc
     * @param m current method
     */
    public void visitDef(Instruction instruction, int i, Method m){
        Store defInstruction = (Store) instruction;
        DefVariable def = new DefVariable();
        def.setInstructionIndex(defInstruction.getLocalVariableIndex());
        def.setPc(i);
        def.setMethod(m);
        if (defExists(def, defs)) {
            defs.remove(def);
        }
        defs.addFirst(def);
    }

    /**
     * Define variable usage if it is not related to an object definition
     * @param instruction current instruction for usage
     * @param i pc of current instruction
     * @param m current method
     * @return use variable
     */
    public UseVariable visitUse(Instruction instruction, int i, Method m) throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        Instruction[] in = m.getInstructionsAndOtherBytes();
        Load useInstruction = (Load) instruction;
        if (useInstruction instanceof ALoad) {
            if (isNotUseLoad(in, i, constantPool, classLoader)) {
                return null;
            }
        }
        UseVariable use = new UseVariable();
        use.setInstructionIndex(useInstruction.getLocalVariableIndex());
        use.setPc(i);
        use.setMethod(m);
        return use;
    }

    /**
     * Processes Objects and Array definitions. Because Astore does not give any reference to the
     * object which is stored but gets the reference from the stack, a workaround is implemented to
     * find the reference when it is pushed to the stack. Method invokations and their pushed parameters
     * are considered.
     * @param i pc of current instruction
     * @param m current method
     */
    protected void getObjectDefs(int i, Method m) throws ExecutionException, ClassFileException, InvalidInstructionInitialisationException {
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        Instruction[] in = m.getInstructionsAndOtherBytes();
        int back = i - 1;
        while (back >= 0) {
            if (in[back] instanceof JumpInvocation) {
                Method invokedMethod = ((JumpInvocation) in[back])
                        .getInvokedMethod(constantPool, classLoader);
                int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                back = back - numberParameter - 1;
                continue;
            } else if (in[back] instanceof ALoad) {
                ALoad loadInstruction = (ALoad) in[back];
                DefVariable def = new DefVariable();
                def.setInstructionIndex(loadInstruction.getLocalVariableIndex());
                def.setPc(i);
                def.setMethod(m);
                if (defExists(def, defs)) {
                    defs.remove(def);
                }
                defs.addFirst(def);
                break;
            } else {
                back--;
            }
        }
    }

    public String toString(){
        String output = "";
        output += "Defs: " + defs.toString() + "\r\n";
        //output += "Uses: " + uses.toString() + "\r\n";
        return output;
    }

    /***
     * Find variable definition for a given variable usage. Returns the definition with the
     * highest pc, i.e. the latest definition.
     * @param use variable usage
     * @param defs set of definitions
     * @return variable definition
     */
    public DefVariable findDef(UseVariable use, ArrayDeque<DefVariable> defs) {
        DefVariable output = null;
        if(use == null){
            return output;
        }
        for(DefVariable def: defs){
            if(def.getInstructionIndex() == use.getInstructionIndex() && def.getMethod().getName().equals(use.getMethod().getName())) {
                output = def;
                return output;
            }
        }
        return output;
    }

    /**
     * Check if variable definition exists
     * @param def variable definition
     * @param defs set of variable defitions
     * @return boolean
     */
    public boolean defExists(DefVariable def, ArrayDeque<DefVariable> defs){
        for(DefVariable d: defs){
            if(def.equals(d)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the ALoad instruction is part of a Store instruction and not a simple
     * variable usage
     * @param in instruction array
     * @param index current pc of ALoad instruction
     * @return boolean
     */
    public boolean isNotUseLoad(Instruction[] in, int index, Constant[] constantPool, MugglClassLoader classLoader) throws ExecutionException, ClassFileException {
        for(int i = index + 1; i < in.length; i++) {
            if(in[i] instanceof ALoad) {
                if(!isMethodInvLoad(in, i, constantPool, classLoader)){
                    return false;
                }
            } else if(in[i] instanceof Astore) {
                return true;
            } else if(in[i] instanceof Putfield){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the a Load instruction is part of a method invocation.
     * @param in instruction array
     * @param index index of the Load instruction
     * @param constantPool
     * @param classLoader
     * @return boolean
     */
    public boolean isMethodInvLoad(Instruction[] in, int index, Constant[] constantPool, MugglClassLoader classLoader) throws ExecutionException, ClassFileException {
        int loads = 1;
        for(int i = index + 1; i < in.length; i++) {
            if(in[i] instanceof Load) {
                loads++;
            } else if(in[i] instanceof JumpInvocation){
                JumpInvocation jump = (JumpInvocation) in[i];
                Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
                int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                return loads <= numberParameter;
            }
        }
        return false;
    }
}
