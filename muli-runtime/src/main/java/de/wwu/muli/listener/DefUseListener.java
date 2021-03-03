package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.symbolic.flow.defUseChains.structures.Def;
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
        //defUseChains = analyser.defUseChains;
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
                //defusechoice.addUses(defUseParent.getUses());
                //defusechoice.addDefUses(defUseParent.getDefUse());
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
                //defusechoice.addUses(defUseParent.getUses());
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

    public boolean[] getCover(String methodName, LogicVirtualMachine vm){
        ArrayList<Boolean> result = new ArrayList<>();
        DefUseMethod r = register;
        Choice choice = vm.getCurrentChoice();
        if(choices.containsKey(choice)){
            DefUseChoice defUse = choices.get(choice);
            defusechoice.addDefs(defUse.getDefs());
            defusechoice.addUses(defUse.getUses());
            defusechoice.addDefUses(defUse.getDefUse());
            defusechoice.updateDefUse();
            /*boolean[] visited = defUse.getVisited();
            defusechoice.setVisited(Arrays.copyOf(visited, visited.length));
            defusechoice.updateVisitedArray();*/
        }
        boolean[] asArray = new boolean[defusechoice.getDefUse().getChainSize()];
        DefUseChain[] chainArray = defusechoice.getDefUse().getDefUseChains().toArray(new DefUseChain[asArray.length]);
        for (int i = 0; i < asArray.length; i++) {
            asArray[i] = chainArray[i].getVisited();
        }
        defusechoice = new DefUseChoice(register);
        return asArray;
        /*HashSet<Integer> defs = defuseEnd.getDefs();
        HashSet<Integer> uses = defuseEnd.getUses();
        DefUseChains chains = r.getDefUses();
        int size = generateDefUseChains(chains, defs, uses);

        boolean[] asArray = new boolean[size];
        DefUseChain[] chainArray = chains.getDefUseChains().toArray(new DefUseChain[size]);
        for (int i = 0; i < size; i++) {
            asArray[i] = chainArray[i].getVisited();
        }
        register = analyser.transformDefUse();
        return asArray;
        /*for(Map.Entry<Object, Object> entry : register.entrySet()){
            Method method = (Method) entry.getKey();
            if(method.getName().equals(methodeName)) {
                DefUseMethod defuse = (DefUseMethod) entry.getValue();
                DefUseChains chains = defuse.getDefUses();
                for (DefUseChain chain: chains.getDefUseChains()) {
                    result.add(chain.getVisited());
                }
                boolean[] asArray = new boolean[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    asArray[i] = result.get(i);
                }
                register = analyser.transformDefUse();
                return asArray;
            }
        }*/
        //return null;
    }

    protected int generateDefUseChains(DefUseChains chains, HashSet<Integer> defs, HashSet<Integer> uses){
        int size = 0;
        List<UseVariable> visited = new ArrayList<UseVariable>();
        List<DefUseChain> visitedTwice = new ArrayList<DefUseChain>();
        for (DefUseChain chain: chains.getDefUseChains()) {
            if(defs.contains(chain.getDef().getPc()) && uses.contains(chain.getUse().getPc())){
                if(visited.contains(chain.getUse())) {
                    visitedTwice.add(chain);
                }
                chain.setVisited(true);
                visited.add(chain.getUse());
            }
        }
        if(visitedTwice.size() != 0){
            for(DefUseChain t: visitedTwice){
                DefUseChain actual = t;
                if(actual.getDef().getPc() > actual.getUse().getPc()){
                    continue;
                }
                for (DefUseChain chain: chains.getDefUseChains()) {
                    if(chain.getVisited() && chain.getUse().equals(actual.getUse()) &&
                            !chain.getDef().equals(actual.getDef()) && chain.getDef().getPc() < chain.getUse().getPc()){
                        if(chain.getDef().getPc() > actual.getDef().getPc()){
                            actual.setVisited(false);
                            actual = chain;
                        } else {
                            chain.setVisited(false);
                        }
                    }
                }
            }

        }
        return chains.getChainSize();
    }
}
