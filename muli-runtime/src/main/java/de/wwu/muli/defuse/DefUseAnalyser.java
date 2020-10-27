package de.wwu.muli.defuse;

import de.wwu.muggl.instructions.bytecode.Goto;
import de.wwu.muggl.instructions.general.Load;
import de.wwu.muggl.instructions.general.Store;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.instructions.interfaces.data.VariableDefining;
import de.wwu.muggl.instructions.interfaces.data.VariableUsing;
import de.wwu.muggl.symbolic.flow.controlflow.ControlGraph;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.flow.coverage.DUCoverage;
import de.wwu.muggl.symbolic.flow.defUseChains.DefUseChainsInitial;
import de.wwu.muggl.vm.classfile.Limitations;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.vm.LogicVirtualMachine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

public class DefUseAnalyser {

    private LogicVirtualMachine vm;
    private HashSet<DefVariable> defs;
    private Map<Method, DefUseChains> defUseChains;
    private HashSet<DefUseChain> defUse;
    private HashSet<Instruction> gotos;

    public DefUseAnalyser(LogicVirtualMachine vm) {
        this.vm = vm;
        this.defs = new HashSet<DefVariable>();
        this.defUse = new HashSet<DefUseChain>();
        this.gotos = new HashSet<Instruction>();
        this.defUseChains = new HashMap<Method, DefUseChains>();
    }

    public void initializeDUG(){
        Method m = this.vm.getInitialMethod();
        constructDUGForMethod(m);
    }

    public void constructDUG(){
        Instruction[] in;
        ControlGraph cg;
        String s;
        Variable[] f;
        try{
            Method m = this.vm.getInitialMethod();
            DefUseChains chains = new DefUseChains();
            in = m.getInstructionsAndOtherBytes();
            cg = m.getControlGraph();
            MugglClassLoader classLoader = m.getClassFile().getClassLoader();
            Constant[] constantPool = m.getClassFile().getConstantPool();
            s = cg.toString();
            f = m.getVariables();
            for(int i =0; i<in.length; i++) {
                if(in[i] instanceof Store) {
                    Store defInstruction = (Store) in[i];
                    DefVariable def = new DefVariable();
                    def.setInstructionIndex(defInstruction.getLocalVariableIndex());
                    def.setPc(i);
                    def.setMethod(m);
                    if(!defExists(def)){
                        defs.add(def);
                    }
                } else if(in[i] instanceof Load) {
                    Load useInstruction = (Load) in[i];
                    UseVariable use = new UseVariable();
                    use.setInstructionIndex(useInstruction.getLocalVariableIndex());
                    use.setPc(i);
                    use.setMethod(m);
                    DefVariable def = findDef(use);
                    if(def != null) {
                        DefUseChain chain = new DefUseChain(def, use);
                        //defUse.add(chain);
                        chains.addChain(chain);
                    }
                } else if(in[i] instanceof Goto) {
                    Goto instruction = (Goto) in[i];
                    int index = instruction.getJumpIncrement();
                    index = index + i ;
                    if (index >= Limitations.MAX_CODE_LENGTH) {
                        index -= Limitations.MAX_CODE_LENGTH;
                    }
                    if(!gotos.contains(instruction)) {
                        gotos.add(instruction);
                        i = index -1;
                    }
                } else if (in[i] instanceof JumpInvocation) {
                    Method invokedMethod = ((JumpInvocation) in[i])
                            .getInvokedMethod(constantPool, classLoader);
                }
            }
            System.console();
        } catch(Exception e){

        }

    }

    public void constructDUGForMethod(Method m){
        constructDUGForMethod(m, null);
 //       DefUseChainsInitial d = new DefUseChainsInitial(m);
//        Map<Method, DefUseChains> map = d.getDefUseChainsMapping();
    }

    public void constructDUGForMethod(Method m, List<Map<String, Integer>> indexes){
        try{
            Instruction[] in = m.getInstructionsAndOtherBytes();
            ControlGraph cg = m.getControlGraph();
            MugglClassLoader classLoader = m.getClassFile().getClassLoader();
            Constant[] constantPool = m.getClassFile().getConstantPool();
            for(int i =0; i<in.length; i++) {
                if(in[i] instanceof Store) {
                    Store defInstruction = (Store) in[i];
                    DefVariable def = new DefVariable();
                    def.setInstructionIndex(defInstruction.getLocalVariableIndex());
                    def.setPc(i);
                    def.setMethod(m);
                    if(!defExists(def)){
                        defs.add(def);
                    }
                } else if(in[i] instanceof Load) {
                    Load useInstruction = (Load) in[i];
                    UseVariable use = new UseVariable();
                    use.setInstructionIndex(useInstruction.getLocalVariableIndex());
                    use.setPc(i);
                    use.setMethod(m);
                    if(indexes != null && indexes.size() > useInstruction.getLocalVariableIndex()){
                        use.setIndexOverMethods(indexes.get(useInstruction.getLocalVariableIndex()));
                    } else {
                        use.addMethodIndex(m.getName(), useInstruction.getLocalVariableIndex());
                    }
                    DefVariable def = findDef(use);
                    if(def != null) {
                        DefUseChain chain = new DefUseChain(def, use);
                        defUse.add(chain);
                    }
                } else if(in[i] instanceof Goto) {
                    Goto instruction = (Goto) in[i];
                    int index = instruction.getJumpIncrement();
                    index = index + i ;
                    if (index >= Limitations.MAX_CODE_LENGTH) {
                        index -= Limitations.MAX_CODE_LENGTH;
                    }
                    if(!gotos.contains(instruction)) {
                        gotos.add(instruction);
                        i = index -1;
                    }
                } else if (in[i] instanceof JumpInvocation) {
                    Method invokedMethod = ((JumpInvocation) in[i])
                            .getInvokedMethod(constantPool, classLoader);
                    int numberParameter = invokedMethod.getParameterTypesAsArray().length;
                    List<Map<String, Integer>> paramterIndexes= new ArrayList<Map<String, Integer>>();
                    int c = 0;
                    for(int n = numberParameter; n > 0; n--) {
                        UseVariable use = findUse(i-numberParameter, m.getName());
                        Map<String, Integer> indexesP = use.getIndexOverMethods();
                        indexesP.put(invokedMethod.getName(), c);
                        paramterIndexes.add(c, indexesP);
                        c++;
                    }
                    constructDUGForMethod(invokedMethod, paramterIndexes);
                }
            }
            System.console();
        } catch(Exception e){

        }

    }

    public DefVariable findDef(UseVariable use) {
        DefVariable output = null;
        for(Map.Entry<String, Integer> entry: use.getIndexOverMethods().entrySet()) {
            for(DefVariable def: defs){
                if(def.getInstructionIndex() == entry.getValue() && def.getMethod().getName().equals(entry.getKey())){
                    if(output != null) {
                        if(output.getPc() < def.getPc()){
                            output = def;
                        }
                    } else {
                        output = def;
                    }
                }
            }
        }
        return output;
    }

    public UseVariable findUse(int pc, String method){
        UseVariable output = null;
        for(DefUseChain chain: defUse) {
            UseVariable use = chain.getUse();
            if(pc == use.getPc() && method.equals(use.getMethod().getName())){
                output = use;
            }
        }
        return output;

    }

    public boolean defExists(DefVariable def){
        for(DefVariable d: defs){
            if(def.equals(d)){
                return true;
            }
        }
        return false;
    }
}
