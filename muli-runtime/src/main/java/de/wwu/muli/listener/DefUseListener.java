package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpInvocation;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.defuse.*;
import de.wwu.muli.searchtree.UnevaluatedST;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.Frame;
import java.util.*;
import de.wwu.muli.searchtree.Choice;
import java.util.stream.Stream;

public class DefUseListener implements ExecutionPathListener {

    private DefUseAnalyser analyser;
    private DefUseMethod register;
    //private DefUseChoice defusechoice;
    private Map<Choice, DefUseChoice> choices;
    private String methodName;
    private HashSet<String> relevantMethods;
    private Map<String, DefUseChoice> defUseMap;


    public DefUseListener(LogicVirtualMachine vm){
        analyser = new DefUseAnalyser(vm);
        try {
            //analyser.initializeDUG();
        } catch (Exception e) {
            throw new IllegalStateException(e); // TODO Better exception treatment.
        }
        choices = new HashMap<>();
        defUseMap = new HashMap<>();
        relevantMethods = new HashSet<>();
    }

    public void setMethodName(String methodName){
        this.methodName = methodName;
        //register = analyser.transformDefUse(methodName);
        DefUseChoice defusechoice = new DefUseChoice();
        defUseMap.put(methodName, defusechoice);
    }

    public void executedInstruction(Instruction instruction, Frame frame, int pc){
        Method method = frame.getMethod();
        if(methodName.equals(method.getName()) || relevantMethods.contains(method.getFullName())){
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
                        //defusechoice.addUses(defUseParent.getUses());
                        defusechoice.addDefUses(defUseParent.getDefUse());
                        //defusechoice.updateDefUse();
                    }
                }
                choices.put(ch, defusechoice);
                defusechoice = new DefUseChoice();
                defUseMap.put(methodName, defusechoice);
                DefUseChoice defUseParent = choices.get(ch);
                defusechoice.addDefs(defUseParent.getDefs());
            }
            defusechoice.visitDefUse(instruction, pc, method);
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
        Choice choice = vm.getCurrentChoice();
        boolean[] result = new boolean[]{};
        //DefUseChoice defusechoice = defUseMap.get(methodName);
        for(Map.Entry<String, DefUseChoice> entry : defUseMap.entrySet()) {
            DefUseChoice defusechoice = entry.getValue();
            String method = entry.getKey();
            if (choices.containsKey(choice)) {
                DefUseChoice defUse = choices.get(choice);
                defusechoice.addDefs(defUse.getDefs());
                defusechoice.addDefUses(defUse.getDefUse());
            } else if (choices.containsKey(choice.getParent())){
                DefUseChoice defUse = choices.get(choice.getParent());
                defusechoice.addDefs(defUse.getDefs());
                defusechoice.addDefUses(defUse.getDefUse());
            } else {
                throw new IllegalStateException("Final choice does not exist");
            }
            int[] asArray = new int[defusechoice.getDefUse().getChainSize()];
            DefUseChain[] chainArray = defusechoice.getDefUse().getDefUseChains().toArray(new DefUseChain[asArray.length]);
            int max = 0;
            for (int i = 0; i < asArray.length; i++) {
                int value = pair(chainArray[i].getDef().getPc(), chainArray[i].getUse().getPc());
                asArray[i] = value;
                if(value > max){
                    max = value;
                }
            }
            boolean[] int_result = new boolean[max+1];
            for (int i = 0; i < asArray.length; i++) {
                int_result[asArray[i]] = true;
            }
            defusechoice = new DefUseChoice();
            defUseMap.put(method, defusechoice);
            result = booleanConcat(result, int_result);
        }
        return result;
    }

    public int pair(int a, int b){
        a++;
        b++;
        int result = (int) (0.5 * (a + b) * (a + b + 1) + b);
        return result;
    }

/*    public boolean isInvokedBy(Frame frame){
        Frame currentFrame = frame;
        String methodName = currentFrame.getMethod().getClassFile().getName();
        if (methodName.startsWith("java.") || notRelevantMethods.contains(methodName)){
            return false;
        } else if(relevantMethods.contains(methodName)){
            return true;
        }
        while(!currentFrame.getMethod().getName().equals(this.methodName)){
            currentFrame = currentFrame.getInvokedBy();
            if(currentFrame == null){
                notRelevantMethods.add(frame.getMethod().getName());
                return false;
            }

        }
        relevantMethods.add(frame.getMethod().getName());
        return true;
    }*/

    public void registerNewMethod(Instruction instruction, Method m){
        MugglClassLoader classLoader = m.getClassFile().getClassLoader();
        Constant[] constantPool = m.getClassFile().getConstantPool();
        JumpInvocation jump = (JumpInvocation) instruction;
        try {
            Method invokedMethod = jump.getInvokedMethod(constantPool, classLoader);
            if(!relevantMethods.contains(invokedMethod.getFullName())){
                relevantMethods.add(invokedMethod.getFullName());
                defUseMap.put(invokedMethod.getName(), new DefUseChoice());
            }
        } catch (Exception e) {
            // Frame method does not match instruction method

        }
    }

    public boolean[] booleanConcat(boolean[] a, boolean[]b){
        boolean[] r = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }


}
