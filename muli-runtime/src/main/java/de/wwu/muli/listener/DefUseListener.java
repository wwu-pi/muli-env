package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muli.defuse.*;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.Frame;
import java.util.*;
import de.wwu.muli.searchtree.Choice;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private DefUseMethod register;
    private DefUseChoice defusechoice;
    private Map<Choice, DefUseChoice> choices;
    private String methodName;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        try {
            analyser.initializeDUG();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO Better exception treatment.
        }
        choices = new HashMap<>();
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
        register = analyser.transformDefUse(methodName);
        defusechoice = new DefUseChoice(register);
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(methodName.equals(method.getName())){
            Choice ch = ((LogicVirtualMachine)frame.getVm()).getCurrentChoice();
            if(ch == null){
                defusechoice.visitDefUse(pc);
                return;
            }
            Method choiceMethod = ((UnevaluatedST) ch.getSts().get(0)).getFrame().getMethod();
            if(defusechoice.getNewInstance() && choices.containsKey(ch)) {
                DefUseChoice defUseParent = choices.get(ch);
                defusechoice.addDefs(defUseParent.getDefs());
            }
            if(methodName.equals(choiceMethod.getName()) && !choices.containsKey(ch)){
                Choice p = ch.getParent();
                if(p!=null) {
                    Method parentChoiceMethod = ((UnevaluatedST) p.getSts().get(0)).getFrame().getMethod();
                    if (methodName.equals(parentChoiceMethod.getName())) {
                        DefUseChoice defUseParent = choices.get(p);
                        defusechoice.addDefs(defUseParent.getDefs());
                        defusechoice.addUses(defUseParent.getUses());
                        defusechoice.addDefUses(defUseParent.getDefUse());
                        defusechoice.updateDefUse();
                    }
                }
                choices.put(ch, defusechoice);
                defusechoice = new DefUseChoice(register);
                DefUseChoice defUseParent = choices.get(ch);
                defusechoice.addDefs(defUseParent.getDefs());
            }
            defusechoice.visitDefUse(pc);
        }
    }

    @Override
    public void reachedEndEvent() {
    }

    @Override
    public Map<Object, Object> getResult(){
        return null;
    }

    public boolean[] getCover(String methodName, LogicVirtualMachine vm) {
        ArrayList<Boolean> result = new ArrayList<>();
        DefUseMethod r = register;
        Choice choice = vm.getCurrentChoice();
        if (choices.containsKey(choice)) {
            DefUseChoice defUse = choices.get(choice);
            defusechoice.addDefs(defUse.getDefs());
            defusechoice.addUses(defUse.getUses());
            defusechoice.addDefUses(defUse.getDefUse());
            defusechoice.updateDefUse();
        }
        boolean[] asArray = new boolean[defusechoice.getDefUse().getChainSize()];
        DefUseChain[] chainArray = defusechoice.getDefUse().getDefUseChains().toArray(new DefUseChain[asArray.length]);
        for (int i = 0; i < asArray.length; i++) {
            asArray[i] = chainArray[i].getVisited();
        }
        defusechoice = new DefUseChoice(register);
        return asArray;
    }
}
