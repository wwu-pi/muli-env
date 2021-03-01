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
    private Map<Object, Object> register;
    private Map<Choice, DefUseChoice> choices;
    private String methodName;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        try {
            analyser.initializeDUG();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO Better exception treatment.
        }
        register = analyser.transformDefUse();
        choices = new HashMap<>();
        //defUseChains = analyser.defUseChains;
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(methodName.equals(method.getName())){
            DefUseMethod r = (DefUseMethod) register.get(method.getName());
            Choice ch = ((LogicVirtualMachine)frame.getVm()).getCurrentChoice();
            if(ch == null){
                r.visitDefUse(pc);
                return;
            }
            Method choiceMethod = ((UnevaluatedST) ch.getSts().get(0)).getFrame().getMethod();
            if(methodName.equals(choiceMethod.getName()) && !choices.containsKey(ch)){
                DefUseChoice defuse = new DefUseChoice(r);
                Choice p = ch.getParent();
                while(p != null){
                    Method parentChoiceMethod = ((UnevaluatedST) p.getSts().get(0)).getFrame().getMethod();
                    if(!methodName.equals(parentChoiceMethod.getName())){
                        break;
                    }
                    DefUseChoice defUseParent = choices.get(p);
                    defuse.addDefs(defUseParent.getDefs());
                    defuse.addUses(defUseParent.getUses());
                    p = p.getParent();
                }
                choices.put(ch, defuse);
            }
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

    public boolean[] getCover(String methodName, LogicVirtualMachine vm){
        ArrayList<Boolean> result = new ArrayList<>();
        DefUseMethod r = (DefUseMethod) register.get(methodName);
        DefUseChoice defuseEnd = new DefUseChoice(r);
        Choice choice = vm.getCurrentChoice();
        if(choices.containsKey(choice)){
            DefUseChoice defUse = choices.get(choice);
            defuseEnd.addDefs(defUse.getDefs());
            defuseEnd.addUses(defUse.getUses());
        }
        HashSet<Integer> defs = defuseEnd.getDefs();
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
                for (DefUseChain chain: chains.getDefUseChains()) {
                    if(chain.getVisited() && chain.getUse().equals(actual.getUse()) && !chain.getDef().equals(actual.getDef())){
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
