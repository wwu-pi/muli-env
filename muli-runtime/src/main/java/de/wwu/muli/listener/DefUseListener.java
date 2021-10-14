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

    private Map<Choice, DefUseMethodMap> choices;
    private String methodName;
    private HashSet<String> relevantMethods;
    private DefUseMethodMap defUseMap;
    private ArrayList<Map<Object, Object>> testableResult;


    public DefUseListener(LogicVirtualMachine vm){
        choices = new HashMap<>();
        defUseMap = new DefUseMethodMap();
        relevantMethods = new HashSet<>();
        testableResult = new ArrayList<>();
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
        DefUseChoice defusechoice = new DefUseChoice();
        defUseMap.put(methodName, defusechoice);
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        // if the current instruction is within the search space
        if(methodName.equals(method.getName()) || relevantMethods.contains(method.getClassFile().getName()+method.getFullName())){
            if(instruction instanceof JumpInvocation){
                // is invoked method within the search space
                registerNewMethod(instruction, method);
            }
            DefUseChoice defusechoice = defUseMap.get(method.getName());

            Choice ch = ((LogicVirtualMachine)frame.getVm()).getCurrentChoice();
            if(ch == null){
                // if there has not been a choice yet, proceed with DefUse analysis
                if(defusechoice.getDefs().size() == 0 && !defusechoice.getInitialDefs()){
                    defusechoice.setInitialDefs(method);
                }
                defusechoice.visitDefUse(instruction, pc, method);
                return;
            }
            Method choiceMethod = ((UnevaluatedST) ch.getSts().get(0)).getFrame().getMethod();
            if(defusechoice.getNewInstance() && choices.containsKey(ch) && choiceMethod.equals(method)) {
                DefUseMethodMap map = choices.get(ch);
                DefUseChoice defUseParent = map.get(method.getName());
                defusechoice.addDefs(defUseParent.getDefs());
                defusechoice.setNewInstance(false);
            }
            // if for the current choice no defuses have been saved yet, save them together with the defuses of the parent choice
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
                // save current DefUseChoice
                choices.put(ch, defUseMap);
                // initialize new DefUseChoice and add previous definitions
                defUseMap = new DefUseMethodMap();
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
            // initialize definitions if this has not already been done
            if(defusechoice.getDefs().size() == 0 && !defusechoice.getInitialDefs()){
                defusechoice.setInitialDefs(method);
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

    public Map<String, int[]> getCover(String methodName, LogicVirtualMachine vm) {
        Choice choice = vm.getCurrentChoice();
        Map<String, int[]> result = new HashMap<>();
        Map<Object, Object> testOutput = new HashMap<>();
        DefUseMethodMap map = new DefUseMethodMap();
        // retrieve last DefUseChoice Object
        if (choices.containsKey(choice)) {
            map = choices.get(choice);
        } else {
            while(choice.getParent() != null) {
                if(choices.containsKey(choice.getParent())){
                    map = choices.get(choice.getParent());
                    break;
                } else {
                    choice = choice.getParent();
                }
            }
        }
        // for each method, save defuses as an array where the index is a unique value based on the def and use pc
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
            for (int i = 0; i < asArray.length; i++) {
                int value = pair(chainArray[i].getDef().getPc(), chainArray[i].getUse().getPc());
                asArray[i] = value;
            }
            Arrays.sort(asArray);
            defusechoice = new DefUseChoice();
            defUseMap.put(method, defusechoice);
            result.put(method, asArray);
        }
        testableResult.add(testOutput);
        return result;
    }

    /**
     * Calculate a reproducible and unique number for two given numbers
     * @param a int
     * @param b int
     * @return index
     */
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
