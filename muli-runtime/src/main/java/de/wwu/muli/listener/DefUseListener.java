package de.wwu.muli.listener;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.symbolic.flow.controlflow.ControlGraph;
import de.wwu.muggl.symbolic.flow.defUseChains.DefUseChainsInitial;
import de.wwu.muli.defuse.DefUseAnalyser;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.defuse.DefUseRegisters;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.Frame;
import java.util.*;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private Map<Method, DefUseMethod> register;


    public DefUseListener(LogicVirtualMachine vm){
        //DefUseChainsInitial i = new DefUseChainsInitial(vm.getInitialMethod());
        analyser = new DefUseAnalyser(vm);
        analyser.initializeDUG();
        register = analyser.transformDefUse();
    }

    public void executedInstruction(Instruction instruction, Frame frame){

    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(register.containsKey(method)){
            DefUseMethod r = register.get(method);
            r.visitDefUse(pc);
        }
    }

    @Override
    public void reachedEndEvent() {
        System.out.print(2);
    }
}
