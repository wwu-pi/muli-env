package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.FreeArrayref;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muli.env.nativeimpl.SolutionIterator;
import de.wwu.muli.vm.LogicVirtualMachine;
import de.wwu.muli.defuse.DefUseMethod;
import sun.awt.image.ImageWatched;

import java.util.*;

public class TcgExecutionListener implements ExecutionListener, TcgListener {

    protected String className;
    protected String methodName;
    protected LinkedHashMap<String, Object> inputs = null; // names to inputs (cloned)
    protected Map<Object, Object> alreadyCloned;
    protected ExecutionPathListener executionPathListener;

    public void setCoverageListener() {
        executionPathListener = new InstructionCoverageListener();
    }

    public void setDefUseListener(LogicVirtualMachine vm) {
        if (executionPathListener == null) {
            executionPathListener = new DefUseListener(vm);
        }
    }

    public Map<Object, Object> getResult(){
        if (executionPathListener == null) {
            return new HashMap<>();
        }
        return executionPathListener.getResult();
    }

    @Override
    public void setMethod(String methodName) {
        this.className = methodName.substring(0, methodName.lastIndexOf('.'));
        this.methodName = methodName.substring(methodName.lastIndexOf('.') + 1);
    }

    @Override
    public Instruction beforeExecuteInstruction(Instruction instruction, Method method, Frame frame) {
        /*
        TODO If a putfield-bytecode-instruction is executed on a free input object, it should be stored
        so that the original input-object can be generated accordingly.
         */
        if (inputs == null
                && method.getName().equals(methodName)
                && method.getClassFile().getName().equals(className)) {
            inputs = new LinkedHashMap<>();
            String[] parameterNames = method.getParameterNames();
            int noArgs = method.getNumberOfArguments();
            Object[] localVariables = frame.getLocalVariables();
            alreadyCloned = new HashMap<>();
            for (int i = 0; i < noArgs; i++) { // TODO If input- and output-objects are the same, their identities do not match after this. However, for a static definition of inputs and outputs this is not fixable.
                // TODO As soon as free objects are initialized lazily (with placeholders to avoid endless initialization in case of circular dependencies),
                //  these values must be inserted here as well. Use a dedicated structure keeping track of these placeholders.
                inputs.put(parameterNames[i], SolutionIterator.cloneVal(localVariables[i], alreadyCloned)); // TODO Refactor: Clone-utility
            }
        }
        return instruction;
    }

    @Override
    public LinkedHashMap<String, Object> getInputs() {
        // Propagate the type constraints for copied FreeObjects and the initialized elements for FreeArrays. // TODO Better place for this?
        for (Map.Entry<Object, Object> copyMatching : alreadyCloned.entrySet()) {
            if (copyMatching.getKey() instanceof FreeObjectref) {
                FreeObjectref original = (FreeObjectref) copyMatching.getKey();
                FreeObjectref copy = (FreeObjectref) copyMatching.getValue();
                copy.getPossibleTypes().clear();
                copy.getPossibleTypes().addAll(original.getPossibleTypes());
                copy.getDisallowedTypes().clear();
                copy.getDisallowedTypes().addAll(original.getDisallowedTypes());
                HashMap<Field, Object> originalFields = original.getFields();
                HashMap<Field, Object> copyFields = copy.getFields();
                for (Map.Entry<Field, Object> newInitializedForSubclass : originalFields.entrySet()) {
                    if (!copyFields.containsKey(newInitializedForSubclass.getKey())) {
                        Field newField = newInitializedForSubclass.getKey();
                        copyFields.put(newField, original.getCachedVariables().get(newField));
                    }
                }
            } else if (copyMatching.getKey() instanceof FreeArrayref) {
                FreeArrayref original = (FreeArrayref) copyMatching.getKey();
                FreeArrayref copy = (FreeArrayref) copyMatching.getValue();
                // TODO Get oldest available Arrayref
            }
        }

        return inputs;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public BitSet getCover() {
        return null; // TODO
    }

    public void afterExecuteInstruction(Instruction instruction, Frame frame, int pc) {
        /*
         TODO After an instruction was executed, it should be recorded by a DefUseListener or a CoverageListener.
         */
        if(executionPathListener != null) {
            executionPathListener.executedInstruction(instruction, frame, pc);
        }

    }

    @Override
    public void treatExceptionDuringInstruction(Instruction instruction, Method method, Frame frame, Exception ex) {
        // TODO
    }

    @Override
    public void backtrack() {

    }

    @Override
    public void reachedEndEvent() {
        if (executionPathListener != null) {
            executionPathListener.reachedEndEvent();
        }
    }
}