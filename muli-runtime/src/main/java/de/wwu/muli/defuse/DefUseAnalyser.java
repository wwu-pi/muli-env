package de.wwu.muli.defuse;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.bytecode.ALoad;
import de.wwu.muggl.instructions.bytecode.Goto;
import de.wwu.muggl.instructions.bytecode.Putfield;
import de.wwu.muggl.instructions.general.Load;
import de.wwu.muggl.instructions.general.Store;
import de.wwu.muggl.instructions.general.Astore;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.Limitations;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.Muli;
import de.wwu.muli.vm.LogicVirtualMachine;


import java.util.*;

public class DefUseAnalyser {

    private LogicVirtualMachine vm;
    public Map<Method, DefUseChains> defUseChains;
    private HashSet<Instruction> gotos;
    private HashSet<Instruction> jumpInstructions;
    private HashSet<Method> invokedMethods;

    public DefUseAnalyser(LogicVirtualMachine vm) {
        this.vm = vm;
        this.gotos = new HashSet<Instruction>();
        this.jumpInstructions = new HashSet<Instruction>();
        this.invokedMethods = new HashSet<Method>();
        this.defUseChains = new HashMap<Method, DefUseChains>();
    }

    public void initializeDUG() throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        HashMap<Field, Object> d = this.vm.getCurrentSearchRegion().getFields();
        ClassFile ref = null;
        for(Field field : d.keySet()){
            if(field.getName().equals("searchRegion")){
                ref = ((Objectref) d.get(field)).getInitializedClass().getClassFile();
            }
        }
        Method method = ref.getMethodByNameAndDescriptor("get", "()Ljava/lang/Object;");
        Instruction[] instructions = method.getInstructionsAndOtherBytes();
        JumpInvocation jump = null;
        for(Instruction in : instructions){
            if(in instanceof JumpInvocation){
                jump = (JumpInvocation) (in);
                break;
            }
        }
        if(jump == null){
            throw new IllegalStateException();
        }
        MugglClassLoader classLoader = method.getClassFile().getClassLoader();
        Constant[] constantPool = method.getClassFile().getConstantPool();
        Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
        constructDUGForMethod(invokedMethod);
    }

    public void constructDUGForMethod(Method m) throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        Instruction[] in = m.getInstructionsAndOtherBytes();
        if (in == null) { // No input parameters.
            return;
        }
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        DefUseChains defUse = new DefUseChains();
        HashSet<DefVariable> defs = new HashSet<DefVariable>();
        int parameters = m.getNumberOfParameters();
        for (int k = 0; k < parameters; k++){
            DefVariable def = new DefVariable();
            def.setInstructionIndex(k);
            def.setPc(-1);
            def.setMethod(m);
            if(!defExists(def, defs)) {
                defs.add(def);
            }
        }
        for(int i =0; i<in.length; i++) {
            if(in[i] instanceof Store) {
                // Definition of Variable
                getDefVariable(in, i, m, defs);
            } else if(in[i] instanceof Astore || in[i] instanceof Putfield) {
                // Special case of def -> write value in array or field of object
                getObjectDefs(in, i, defs, constantPool, classLoader, m);
            } else if(in[i] instanceof Load) {
                // Usage of Variable
                UseVariable use = getUseVariable(in, i, m, constantPool, classLoader);
                DefVariable def = findDef(use, defs);
                if(def != null) {
                    DefUseChain chain = new DefUseChain(def, use);
                    defUse.addChain(chain);
                }
            } else if(in[i] instanceof JumpConditional) {
                // process conditional Jumps recursively
                i = getConditionalJump(in, i, m, defs);
            } else if(in[i] instanceof Goto) {
                // Jump to different Instruction
                i = getGotoInstr(in, i);
            } else if (in[i] instanceof JumpInvocation) {
                // new method invokation
                getMethodInvok(in, i, constantPool, classLoader, m, defUse);
            }
        }
        if(defUseChains.containsKey(m)) {
            DefUseChains chain = defUseChains.get(m);
            chain.mergeChains(defUse);
            defUseChains.put(m, chain);
        } else {
            defUseChains.put(m, defUse);
        }

    }

    public DefUseChains processConditionalJump(int start, Method m, HashSet<DefVariable> defs) throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        HashSet<DefVariable> condDefs = new HashSet<DefVariable>();
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        DefUseChains defUse = new DefUseChains();
        Instruction[] in = m.getInstructionsAndOtherBytes();
        for (int i = start; i < in.length; i++) {
            if (in[i] instanceof Store) {
                // Definition of Variable
                getDefVariable(in, i, m, condDefs);
            } else if (in[i] instanceof Astore || in[i] instanceof Putfield) {
                // Special case von def -> Wert in Array schreiben
                getObjectDefs(in, i, condDefs, constantPool, classLoader, m);
            } else if (in[i] instanceof Load) {
                // Usage of Variable
                UseVariable use = getUseVariable(in, i, m, constantPool, classLoader);
                DefVariable def = findDef(use, condDefs);
                if (def == null) {
                    def = findDef(use, defs);
                }
                if (def != null) {
                    DefUseChain chain = new DefUseChain(def, use);
                    defUse.addChain(chain);
                }
            } else if (in[i] instanceof JumpConditional) {
                // process conditional Jumps recursively
                HashSet<DefVariable> allDefs = new HashSet<DefVariable>();
                allDefs.addAll(defs);
                allDefs.addAll(condDefs);
                i = getConditionalJump(in, i, m, allDefs);
            } else if (in[i] instanceof Goto) {
                // Jump to different Instruction
                i = getGotoInstr(in, i);
            } else if (in[i] instanceof JumpInvocation) {
                // new method invokation
                getMethodInvok(in, i, constantPool, classLoader, m, defUse);
            }
        }
        return defUse;
    }

    /***
     * Find variable definition for a given variable usage. Returns the definition with the
     * highest pc, i.e. the latest definition.
     * @param use variable usage
     * @param defs set of definitions
     * @return variable definition
     */
    public DefVariable findDef(UseVariable use, HashSet<DefVariable> defs) {
        DefVariable output = null;
        if(use == null){
            return output;
        }
        for(DefVariable def: defs){
            if(def.getInstructionIndex() == use.getInstructionIndex() && def.getMethod().getName().equals(use.getMethod().getName())){
                if(output != null) {
                    if(output.getPc() < def.getPc()){
                        output = def;
                    }
                } else {
                    output = def;
                }
            }
        }
        return output;
    }

    public UseVariable findUse(int pc, String method, DefUseChains chains){
        UseVariable output = null;
        for(int i = pc; i >= 0; i--){
            for(DefUseChain chain: chains.getDefUseChains()) {
                UseVariable use = chain.getUse();
                if(i == use.getPc() && method.equals(use.getMethod().getName())){
                    output = use;
                    break;
                }
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
    public boolean defExists(DefVariable def, HashSet<DefVariable> defs){
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

    /**
     * Define the Variable Definition and add it to definitions
     * @param in instruction array
     * @param i current pc
     * @param m current method
     * @param defs set of variable definitions
     */
    protected void getDefVariable(Instruction[] in, int i, Method m, HashSet<DefVariable> defs){
        Store defInstruction = (Store) in[i];
        DefVariable def = new DefVariable();
        def.setInstructionIndex(defInstruction.getLocalVariableIndex());
        def.setPc(i);
        def.setMethod(m);
        if (!defExists(def, defs)) {
            defs.add(def);
        }
    }

    /**
     * Define variable usage if it is not related to an object definition
     * @param in instruction array
     * @param i pc of current instruction
     * @param m current method
     * @return use variable
     */
    protected UseVariable getUseVariable(Instruction[] in, int i, Method m,  Constant[] constantPool, MugglClassLoader classLoader) throws ExecutionException, ClassFileException {
        // Todo eigentlich wird nur geladen, überprüfen ob auch benutzt wird?
        Load useInstruction = (Load) in[i];
        if (useInstruction instanceof ALoad) {
            if (isNotUseLoad(in, i, constantPool, classLoader)) {
                return null;
            }
        }
        UseVariable use = new UseVariable();
        use.setInstructionIndex(useInstruction.getLocalVariableIndex());
        use.setPc(i);
        use.setMethod(m);

        use.addMethodIndex(m.getFullName(), useInstruction.getLocalVariableIndex());
        return use;
    }

    /**
     * Process conditional jump instruction. If jump was already considered, it is ignored.
     * Otherwise the defUse chains beginning at the conditional section are recursively processed
     * with a new method invocation and the pc jumps to the end of the conditional section.
     * @param in instruction array
     * @param i pc of current instruction
     * @param m current method
     * @param defs previous variable definitions
     * @return new pc after jump
     */
    protected int getConditionalJump(Instruction[] in, int i, Method m, HashSet<DefVariable> defs) throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        JumpConditional jumpInstruction = (JumpConditional) in[i];
        Instruction jump = (Instruction) jumpInstruction;
        // jump was already considered
        if(jumpInstructions.contains(jump)) {
            return i;
        } else {
            jumpInstructions.add(jump);
        }
        int endIf = jumpInstruction.getJumpTarget();
        DefUseChains defUseIf = processConditionalJump(i+1, m, defs);
        i = endIf -1;
        // Merge defuse chains with and without condition
        if(defUseChains.containsKey(m)) {
            DefUseChains chain = defUseChains.get(m);
            chain.mergeChains(defUseIf);
            defUseChains.put(m, chain);
        } else {
            defUseChains.put(m, defUseIf);
        }
        return i;
    }

    /**
     * Process goto instructions. Calculates new pc and if the instruction was not visited before,
     * sets it as new pc
     * @param in instruction array
     * @param i pc of current instruction
     * @return new pc after jump
     */
    protected int getGotoInstr(Instruction[] in, int i){
        Goto instruction = (Goto) in[i];
        int index = instruction.getJumpIncrement();
        index = index + i;
        if (index >= Limitations.MAX_CODE_LENGTH) {
            index -= Limitations.MAX_CODE_LENGTH;
        }
        // Sichergehen dass nur einmal zurück gesprungen wird -> keine endlos-Schleife
        if (!gotos.contains(instruction)) {
            gotos.add(instruction);
            i = index - 1;
        }
        return i;
    }

    /**
     * Process method invocations. Method is loaded and recursively processed.
     * @param in instruction array
     * @param i pc of current instruction
     * @param constantPool constant pool of class
     * @param classLoader class loader of class
     */
    protected void getMethodInvok(Instruction[] in, int i, Constant[] constantPool, MugglClassLoader classLoader,
                                  Method m, DefUseChains defUse) throws InvalidInstructionInitialisationException, ExecutionException, ClassFileException {
        JumpInvocation jump = (JumpInvocation) in[i];
        Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
        if(invokedMethod.getClassFile().getName().startsWith("java.")){
            return;
        }
        List<Map<String, Integer>> paramterIndexes = new ArrayList<Map<String, Integer>>();
        int c = 1;
        int numberParameter = invokedMethod.getParameterTypesAsArray().length;
        for(int n = numberParameter; n > 0; n--) {
            UseVariable use = findUse(i-numberParameter, m.getName(), defUse);
            Map<String, Integer> indexesP = use.getIndexOverMethods();
            indexesP.put(invokedMethod.getFullName(), c);
            //paramterIndexes.add(c, indexesP);
            c++;
        }
        if (!invokedMethods.contains(invokedMethod)) {
            invokedMethods.add(invokedMethod);
            constructDUGForMethod(invokedMethod);
        }
    }

    /**
     * Processes Objects and Array definitions. Because Astore does not give any reference to the
     * object which is stored but gets the reference from the stack, a workaround is implemented to
     * find the reference when it is pushed to the stack. Method invokations and their pushed parameters
     * are considered.
     * @param in instruction array
     * @param i pc of current instruction
     * @param defs set of variable definitions
     * @param constantPool array of class constants
     * @param classLoader class loader
     * @param m current method
     */
    protected void getObjectDefs(Instruction[] in, int i, HashSet<DefVariable> defs, Constant[] constantPool,
                                 MugglClassLoader classLoader, Method m) throws ExecutionException, ClassFileException {
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
                if (!defExists(def, defs)) {
                    defs.add(def);
                }
                break;
            } else {
                back--;
            }
        }
    }

    /**
     * Transform the defuse chains in defs and uses as needed.
     * @return
     */
    public Map<Object, Object> transformDefUse(){
        Map<Object, Object> output = new HashMap<>();
        for(Map.Entry<Method, DefUseChains> pair : defUseChains.entrySet()){
            Method method = pair.getKey();
            DefUseChains chains = pair.getValue();
            DefUseMethod dum = new DefUseMethod();
            DefUseRegisters uses = new DefUseRegisters();
            DefUseRegisters defs = new DefUseRegisters();
            for(DefUseChain chain : chains.getDefUseChains()) {
                int useIndex = chain.getUse().getPc();
                int defIndex = chain.getDef().getPc();
                if(!uses.hasEntry(useIndex)){
                    DefUseRegister use = new DefUseRegister(defIndex, false);
                    uses.addRegister(use, useIndex);
                } else {
                    uses.addLink(useIndex, defIndex);
                }

                if(!defs.hasEntry(defIndex)){
                    DefUseRegister def;
                    if(defIndex == -1){
                        def = new DefUseRegister(useIndex, true);
                    } else {
                        def = new DefUseRegister(useIndex, false);
                    }
                    defs.addRegister(def, defIndex);
                } else {
                    defs.addLink(defIndex, useIndex);
                }
            }
            dum.initDefUses();
            dum.setDefs(defs);
            dum.setUses(uses);
            output.put(method, dum);
        }
        return output;
    }
}
