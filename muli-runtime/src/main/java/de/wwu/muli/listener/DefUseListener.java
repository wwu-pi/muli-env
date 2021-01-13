package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muli.defuse.DefUseAnalyser;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.Frame;
import java.util.*;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private Map<Object, Object> register;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        analyser.initializeDUG();
        register = analyser.transformDefUse();
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
}
