package de.wwu.muli.listener;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.symbolic.flow.controlflow.ControlGraph;
import de.wwu.muggl.symbolic.flow.defUseChains.DefUseChainsInitial;
import de.wwu.muli.defuse.DefUseAnalyser;
import de.wwu.muli.vm.LogicVirtualMachine;

public class DefUseListener implements ExecutionPathListener {

    public DefUseListener(LogicVirtualMachine vm){
        //DefUseChainsInitial i = new DefUseChainsInitial(vm.getInitialMethod());
        DefUseAnalyser analyser = new DefUseAnalyser(vm);
        analyser.initializeDUG();
    }

    @Override
    public void executedInstruction(Instruction instruction) {

    }

    @Override
    public void reachedEndEvent() {

    }
}
