package de.wwu.muli.listener;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.symbolic.flow.controlflow.ControlGraph;
import de.wwu.muggl.symbolic.flow.defUseChains.DefUseChainsInitial;
import de.wwu.muli.defuse.DefUseAnalyser;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseRegisters;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private DefUseRegisters defs;
    private DefUseRegisters uses;
    private Boolean[] defUses;

    public DefUseListener(LogicVirtualMachine vm){
        //DefUseChainsInitial i = new DefUseChainsInitial(vm.getInitialMethod());
        analyser = new DefUseAnalyser(vm);
        analyser.initializeDUG();
    }

    @Override
    public void executedInstruction(Instruction instruction) {

    }

    public void executedInstruction(Instruction instruction, Method method){
        DefUseChains chain = analyser.defUseChains.get(method);
        defUses = new Boolean[chain.getDefUseChains().size()];

    }

    @Override
    public void reachedEndEvent() {

    }
}
