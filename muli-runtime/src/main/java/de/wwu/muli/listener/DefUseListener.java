package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muli.defuse.DefUseAnalyser;
import de.wwu.muli.defuse.DefUseChain;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.Frame;
import java.util.*;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private Map<Object, Object> register;
    private Map<Method, DefUseChains> defUseChains;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        try {
            analyser.initializeDUG();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO Better exception treatment.
        }
        register = analyser.transformDefUse();
        defUseChains = analyser.defUseChains;
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(register.containsKey(method)){
            DefUseMethod r = (DefUseMethod) register.get(method);
            r.visitDefUse(pc);
        }
    }

    @Override
    public void reachedEndEvent() {
    }

    @Override
    public Map<Object, Object> getResult(){
        return register;
    }

    public boolean[] getCover(){
        ArrayList<Boolean> result = new ArrayList<>();
        for(Map.Entry<Object, Object> entry : register.entrySet()){
            DefUseMethod defuse = (DefUseMethod) entry.getValue();
            DefUseChains chains = defuse.getDefUses();
            for (DefUseChain chain: chains.getDefUseChains()) {
                result.add(chain.getVisited());
            }

        }
        boolean[] asArray = new boolean[result.size()];
        for (int i = 0; i < result.size(); i++) {
            asArray[i] = result.get(i);
        }
        return asArray;
    }
}
