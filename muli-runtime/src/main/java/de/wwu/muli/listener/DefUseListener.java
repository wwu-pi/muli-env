package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.loading.MugglClassLoader;
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
    //private DefUseChoice defusechoice;
    private Map<Choice, DefUseMethodMap> choices;
    private String methodName;
    private HashSet<String> relevantMethods;
    private DefUseMethodMap defUseMap;
    private ArrayList<Map<Object, Object>> testableResult;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        try {
            //analyser.initializeDUG();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO Better exception treatment.
        }
        choices = new HashMap<>();
        defUseMap = new DefUseMethodMap();
        relevantMethods = new HashSet<>();
        testableResult = new ArrayList<>();
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
        //register = analyser.transformDefUse(methodName);
        DefUseChoice defusechoice = new DefUseChoice();
        defUseMap.put(methodName, defusechoice);
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(methodName.equals(method.getName()) || relevantMethods.contains(method.getClassFile().getName()+method.getFullName())){
            if(instruction instanceof JumpInvocation){
                registerNewMethod(instruction, method);
            }
            DefUseChoice defusechoice = defUseMap.get(method.getName());
            if(defusechoice.getDefs().size() == 0 && !defusechoice.getInitialDefs()){
                defusechoice.setInitialDefs(method);
            }
            Choice ch = ((LogicVirtualMachine)frame.getVm()).getCurrentChoice();
            if(ch == null){
                defusechoice.visitDefUse(instruction, pc, method);
                return;
            }
            Method choiceMethod = ((UnevaluatedST) ch.getSts().get(0)).getFrame().getMethod();
            if(defusechoice.getNewInstance() && choices.containsKey(ch) && choiceMethod.equals(method)) {
                DefUseMethodMap map = choices.get(ch);
                DefUseChoice defUseParent = map.get(method.getName());
                defusechoice.addDefs(defUseParent.getDefs());
            }
            if(method.getName().equals(choiceMethod.getName()) && !choices.containsKey(ch)){
                Choice p = ch.getParent();
                if(p!=null) {
                    Method parentChoiceMethod = ((UnevaluatedST) p.getSts().get(0)).getFrame().getMethod();
                    if (methodName.equals(parentChoiceMethod.getName()) || relevantMethods.contains(parentChoiceMethod.getClassFile().getName()+parentChoiceMethod.getFullName())) {
                        DefUseMethodMap map = choices.get(p);
                        for(Map.Entry<String, DefUseChoice> entry : map.entrySet()) {
                            DefUseChoice defUseParent = entry.getValue();
                            String m = entry.getKey();
                            DefUseChoice defUse = defUseMap.get(m);
                            defUse.addDefs(defUseParent.getDefs());
                            defUse.addDefUses(defUseParent.getDefUse());
                        }
                    }
                }
                choices.put(ch, defUseMap);
                defUseMap = new DefUseMethodMap();
                //DefUseChoice defUseParent = defUseMap.get(method.getName());
                defusechoice = new DefUseChoice();
                DefUseMethodMap map = choices.get(ch);
                for(Map.Entry<String, DefUseChoice> entry : map.entrySet()) {
                    DefUseChoice newDefuse = new DefUseChoice();
                    DefUseChoice defuseParent = entry.getValue();
                    String m = entry.getKey();
                    newDefuse.addDefs(defuseParent.getDefs());
                    defUseMap.put(m, newDefuse);
                    if(m.equals(method.getName())){
                        defusechoice = newDefuse;
                    }
                }
            }
            defusechoice.visitDefUse(instruction, pc, method);
        }
    }

    @Override
    public void reachedEndEvent() {
    }

    @Override
    public ArrayList<Map<Object, Object>> getResult(){
        return testableResult;
    }

    public Map<String, Object> getCover(String methodName, LogicVirtualMachine vm) {
        Choice choice = vm.getCurrentChoice();
        Map<String, Object> result = new HashMap<>();
        //DefUseChoice defusechoice = defUseMap.get(methodName);
        Map<Object, Object> testOutput = new HashMap<>();
        DefUseMethodMap map = new DefUseMethodMap();
        if (choices.containsKey(choice)) {
            map = choices.get(choice);
        } else if(choices.containsKey(choice.getParent())){
            map = choices.get(choice.getParent());
        }
        for(Map.Entry<String, DefUseChoice> entry : defUseMap.entrySet()) {
            DefUseChoice defusechoice = entry.getValue();
            String method = entry.getKey();
            DefUseChoice defUse = map.get(method);
            if(defUse != null){
                defusechoice.addDefs(defUse.getDefs());
                defusechoice.addDefUses(defUse.getDefUse());
            }

            testOutput.put(method, defusechoice);
            int[] asArray = new int[defusechoice.getDefUse().getChainSize()];
            DefUseChain[] chainArray = defusechoice.getDefUse().getDefUseChains().toArray(new DefUseChain[asArray.length]);
            int max = 0;
            for (int i = 0; i < asArray.length; i++) {
                int value = pair(chainArray[i].getDef().getPc(), chainArray[i].getUse().getPc());
                asArray[i] = value;
                //if(value > max){
                //    max = value;
                //}
            }
            Arrays.sort(asArray);
            //boolean[] part_result = new boolean[max+1];
            //for (int i = 0; i < asArray.length; i++) {
            //    part_result[asArray[i]] = true;
            //}
            defusechoice = new DefUseChoice();
            defUseMap.put(method, defusechoice);
            result.put(method, asArray);
        }
        testableResult.add(testOutput);
        return result;
    }

    public int pair(int a, int b){
        a++;
        b++;
        int result = (int) (0.5 * (a + b) * (a + b + 1) + b);
        return result;
    }

    public void registerNewMethod(Instruction instruction, Method m){
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        JumpInvocation jump = (JumpInvocation) instruction;
        try {
            Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
            if(!invokedMethod.getName().equals(methodName) && !relevantMethods.contains(invokedMethod.getClassFile().getName()+invokedMethod.getFullName())){
                if(!invokedMethod.getClassFile().getName().startsWith("java.")) {
                    relevantMethods.add(invokedMethod.getClassFile().getName()+invokedMethod.getFullName());
                    defUseMap.put(invokedMethod.getName(), new DefUseChoice());
                }
            }
        } catch (Exception e) {
            // Frame method does not match instruction method

        }
    }
}
