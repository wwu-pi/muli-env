package de.wwu.muli.defuse;

import de.wwu.muggl.instructions.bytecode.ALoad;
import de.wwu.muggl.instructions.bytecode.Goto;
import de.wwu.muggl.instructions.bytecode.Putfield;
import de.wwu.muggl.instructions.general.Load;
import de.wwu.muggl.instructions.general.Store;
import de.wwu.muggl.instructions.general.Astore;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.instructions.interfaces.data.VariableDefining;
import de.wwu.muggl.instructions.interfaces.data.VariableUsing;
import de.wwu.muggl.symbolic.flow.controlflow.ControlGraph;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.flow.coverage.DUCoverage;
import de.wwu.muggl.symbolic.flow.defUseChains.DefUseChainsInitial;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.Limitations;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.vm.LogicVirtualMachine;


import java.util.*;

public class DefUseAnalyser {

    private LogicVirtualMachine vm;
    //private HashSet<DefVariable> defs;
    public Map<Method, DefUseChains> defUseChains;
    //private HashSet<DefUseChain> defUse;
    private HashSet<Instruction> gotos;
    private HashSet<Instruction> jumpInstructions;
    private HashSet<Method> invokedMethods;

    public DefUseAnalyser(LogicVirtualMachine vm) {
        this.vm = vm;
        //this.defs = new HashSet<DefVariable>();
        //this.defUse = new HashSet<DefUseChain>();
        this.gotos = new HashSet<Instruction>();
        this.jumpInstructions = new HashSet<Instruction>();
        this.invokedMethods = new HashSet<Method>();
        this.defUseChains = new HashMap<Method, DefUseChains>();
    }

    public void initializeDUG(){
        Method m = this.vm.getInitialMethod();
        constructDUGForMethod(m);
        System.out.println("hallo");
    }

    public void constructDUGForMethod(Method m){
        constructDUGForMethod(m, null);
  //      DefUseChainsInitial d = new DefUseChainsInitial(m);
        //Map<Method, DefUseChains> map = d.getDefUseChainsMapping();
    }

    public void constructDUGForMethod(Method m, List<Map<String, Integer>> indexes){
        try{
            Instruction[] in = m.getInstructionsAndOtherBytes();
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
                    Store defInstruction = (Store) in[i];
                    DefVariable def = new DefVariable();
                    def.setInstructionIndex(defInstruction.getLocalVariableIndex());
                    def.setPc(i);
                    def.setMethod(m);
                    if(!defExists(def, defs)) {
                        defs.add(def);
                    }
                } else if(in[i] instanceof Astore || in[i] instanceof Putfield) {
                    // Special case von def -> Wert in Array schreiben
                    int back = i-1;
                    while(back>=0) {
                        if(in[back] instanceof JumpInvocation) {
                            Method invokedMethod = ((JumpInvocation) in[back])
                                    .getInvokedMethod(constantPool, classLoader);
                            int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                            back = back - numberParameter - 1;
                            continue;
                        } else if(in[back] instanceof ALoad){
                            ALoad loadInstruction = (ALoad) in[back];
                            DefVariable def = new DefVariable();
                            def.setInstructionIndex(loadInstruction.getLocalVariableIndex());
                            def.setPc(i);
                            def.setMethod(m);
                            if(!defExists(def, defs)) {
                                defs.add(def);
                            }
                            break;
                        } else {
                            back--;
                        }
                    }
                } else if(in[i] instanceof Load) {
                    // Usage of Variable
                    // Todo eigentlich wird nur geladen, überprüfen ob auch benutzt wird?
                    Load useInstruction = (Load) in[i];
                    if(useInstruction instanceof ALoad){
                        if(isNotUseLoad(in, i)){
                            continue;
                        }
                    }
                    UseVariable use = new UseVariable();
                    use.setInstructionIndex(useInstruction.getLocalVariableIndex());
                    use.setPc(i);
                    use.setMethod(m);
                    if(indexes != null && indexes.size() > useInstruction.getLocalVariableIndex()){
                        use.setIndexOverMethods(indexes.get(useInstruction.getLocalVariableIndex()));
                    } else {
                        use.addMethodIndex(m.getName(), useInstruction.getLocalVariableIndex());
                    }
                    DefVariable def = findDef(use, defs);
                    if(def != null) {
                        DefUseChain chain = new DefUseChain(def, use);
                        defUse.addChain(chain);
                    }
                } else if(in[i] instanceof JumpConditional) {
                    JumpConditional jumpInstruction = (JumpConditional) in[i];
                    Instruction jump = (Instruction) jumpInstruction;
                    if(jumpInstructions.contains(jump)) {
                        continue;
                    } else {
                        jumpInstructions.add(jump);
                    }
                    int endIf = jumpInstruction.getJumpTarget();
                    DefUseChains defUseIf = processConditionalJump(i+1, m, defs);
                    i = endIf -1;
                    if(defUseChains.containsKey(m)) {
                        DefUseChains chain = defUseChains.get(m);
                        chain.mergeChains(defUseIf);
                        defUseChains.put(m, chain);
                    } else {
                        defUseChains.put(m, defUseIf);
                    }
                } else if(in[i] instanceof Goto) {
                    // Sprung zu einer anderen Instruction
                    Goto instruction = (Goto) in[i];
                    int index = instruction.getJumpIncrement();
                    index = index + i ;
                    if (index >= Limitations.MAX_CODE_LENGTH) {
                        index -= Limitations.MAX_CODE_LENGTH;
                    }
                    // Sichergehen dass nur einmal zurück gesprungen wird -> keine endlos-Schleife
                    if(!gotos.contains(instruction)) {
                        gotos.add(instruction);
                        i = index -1;
                    }
                } else if (in[i] instanceof JumpInvocation) {
                    // neue Methodenaufruf
                    JumpInvocation jump = (JumpInvocation) in[i];
                    Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
                    int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                    List<Map<String, Integer>> paramterIndexes= new ArrayList<Map<String, Integer>>();
                    int c = 0;
                    /*for(int n = numberParameter; n > 0; n--) {
                        UseVariable use = findUse(i-numberParameter, m.getName(), defUse);
                        Map<String, Integer> indexesP = use.getIndexOverMethods();
                        indexesP.put(invokedMethod.getName(), c);
                        paramterIndexes.add(c, indexesP);
                        c++;
                    }*/
                    if(!invokedMethods.contains(invokedMethod)) {
                        invokedMethods.add(invokedMethod);
                        constructDUGForMethod(invokedMethod, paramterIndexes);

                    }
                }
            }
            if(defUseChains.containsKey(m)) {
                DefUseChains chain = defUseChains.get(m);
                chain.mergeChains(defUse);
                defUseChains.put(m, chain);
            } else {
                defUseChains.put(m, defUse);
            }
        } catch(Exception e){

        }

    }

    public DefUseChains processConditionalJump(int start, Method m, HashSet<DefVariable> defs) {
        try {
            HashSet<DefVariable> condDefs = new HashSet<DefVariable>();
            MugglClassLoader classLoader = m.getClassFile().getClassLoader();
            Constant[] constantPool = m.getClassFile().getConstantPool();
            DefUseChains defUse = new DefUseChains();
            Instruction[] in = m.getInstructionsAndOtherBytes();
            for (int i = start; i < in.length; i++) {
                if (in[i] instanceof Store) {
                    // Definition of Variable
                    Store defInstruction = (Store) in[i];
                    DefVariable def = new DefVariable();
                    def.setInstructionIndex(defInstruction.getLocalVariableIndex());
                    def.setPc(i);
                    def.setMethod(m);
                    if (!defExists(def, condDefs)) {
                        condDefs.add(def);
                    }
                } else if (in[i] instanceof Astore || in[i] instanceof Putfield) {
                    // Special case von def -> Wert in Array schreiben
                    Astore arrayInstruction = (Astore) in[i];
                    int back = i - 1;
                    while (back > 0) {
                        if (in[back] instanceof JumpInvocation) {
                            Method invokedMethod = null;
                            invokedMethod = ((JumpInvocation) in[back])
                                        .getInvokedMethod(constantPool, classLoader);
                            int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                            back = back - numberParameter - 1;
                        } else if (in[back] instanceof ALoad) {
                            ALoad loadInstruction = (ALoad) in[back];
                            DefVariable def = new DefVariable();
                            def.setInstructionIndex(loadInstruction.getLocalVariableIndex());
                            def.setPc(i);
                            def.setMethod(m);
                            if (!defExists(def, condDefs)) {
                                condDefs.add(def);
                            }
                            break;
                        } else {
                            back--;
                        }
                    }
                } else if (in[i] instanceof Load) {
                    // Usage of Variable
                    // Todo eigentlich wird nur geladen, überprüfen ob auch benutzt wird?
                    Load useInstruction = (Load) in[i];
                    if (useInstruction instanceof ALoad) {
                        if (isNotUseLoad(in, i)) {
                            continue;
                        }
                    }
                    UseVariable use = new UseVariable();
                    use.setInstructionIndex(useInstruction.getLocalVariableIndex());
                    use.setPc(i);
                    use.setMethod(m);
                    //if(indexes != null && indexes.size() > useInstruction.getLocalVariableIndex()){
                    //    use.setIndexOverMethods(indexes.get(useInstruction.getLocalVariableIndex()));
                    //} else {
                    //    use.addMethodIndex(m.getName(), useInstruction.getLocalVariableIndex());
                    //}
                    DefVariable def = findDef(use, condDefs);
                    if (def == null) {
                        def = findDef(use, defs);
                    }
                    if (def != null) {
                        DefUseChain chain = new DefUseChain(def, use);
                        defUse.addChain(chain);
                    }
                } else if (in[i] instanceof JumpConditional) {
                    JumpConditional jumpInstruction = (JumpConditional) in[i];
                    Instruction jump = (Instruction) jumpInstruction;
                    if(jumpInstructions.contains(jump)) {
                        continue;
                    } else {
                        jumpInstructions.add(jump);
                    }
                    int endIf = jumpInstruction.getJumpTarget();
                    i = endIf - 1;
                    DefUseChains defUseIf = processConditionalJump(i+1, m, defs);
                    if(defUseChains.containsKey(m)) {
                        DefUseChains chain = defUseChains.get(m);
                        chain.mergeChains(defUseIf);
                        defUseChains.put(m, chain);
                    } else {
                        defUseChains.put(m, defUseIf);
                    }
                } else if (in[i] instanceof Goto) {
                    // Sprung zu einer anderen Instruction
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
                } else if (in[i] instanceof JumpInvocation) {
                    // neue Methodenaufruf
                    JumpInvocation jump = (JumpInvocation) in[i];
                    Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
                    List<Map<String, Integer>> paramterIndexes = new ArrayList<Map<String, Integer>>();
                    if (!invokedMethods.contains(invokedMethod)) {
                        invokedMethods.add(invokedMethod);
                        constructDUGForMethod(invokedMethod, paramterIndexes);
                     }
                }
            }
            return defUse;
        } catch(Exception e) {
            return null;
        }
    }

    public DefVariable findDef(UseVariable use, HashSet<DefVariable> defs) {
        DefVariable output = null;
        //for(Map.Entry<String, Integer> entry: use.getIndexOverMethods().entrySet()) {
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
           // }
        }
        return output;
    }

    public UseVariable findUse(int pc, String method, DefUseChains chains){
        UseVariable output = null;
        for(DefUseChain chain: chains.getDefUseChains()) {
            UseVariable use = chain.getUse();
            if(pc == use.getPc() && method.equals(use.getMethod().getName())){
                output = use;
            }
        }
        return output;

    }

    public boolean defExists(DefVariable def, HashSet<DefVariable> defs){
        for(DefVariable d: defs){
            if(def.equals(d)){
                return true;
            }
        }
        return false;
    }

    public boolean isNotUseLoad(Instruction[] in, int index){
        for(int i = index + 1; i < in.length; i++) {
            if(in[i] instanceof ALoad) {
                return false;
            } else if(in[i] instanceof Astore) {
                return true;
            } else if(in[i] instanceof Putfield){
                return true;
            }
        }
        return false;
    }

    public Map<Method, DefUseMethod> transformDefUse(){
        Map<Method, DefUseMethod> output = new HashMap<>();
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
                    DefUseRegister def = new DefUseRegister(useIndex, false);
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
