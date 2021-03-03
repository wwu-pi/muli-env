package de.wwu.muli.listener;

import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muli.env.nativeimpl.SolutionIterator;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.reflect.Array;
import java.util.*;

public class TcgExecutionListener implements ExecutionListener, TcgListener {

    protected boolean isObjectMethod;
    protected String className;
    protected String methodName;
    protected LinkedHashMap<String, Object> trackInputs;
    protected ExecutionPathListener executionPathListener;
    protected Map<Object, Object> alreadyCloned;
    
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
        if (trackInputs == null
                && method.getName().equals(methodName)
                && method.getClassFile().getName().equals(className)) {
            isObjectMethod = !method.isAccStatic();
            trackInputs = new LinkedHashMap<>();
            String[] parameterNames = method.getParameterNames();
            int noArgs = method.getNumberOfArguments() + (isObjectMethod ? 1 : 0);
            Object[] localVariables = frame.getLocalVariables();
            alreadyCloned = new HashMap<>();
            for (int i = 0; i < noArgs; i++) {
                Object val = localVariables[i];
                if ((localVariables[i] instanceof Objectref && !(localVariables[i] instanceof FreeObjectref))
                    || (localVariables[i] instanceof Arrayref && !(localVariables[i] instanceof FreeArrayref))) {
                    // We only have the information to restore inputs for free references, for concrete references
                    // we directly store the inputs.
                    val = SolutionIterator.cloneVal(val, alreadyCloned);
                }
                trackInputs.put(parameterNames[i], val);
            }
        }
        return instruction;
    }

    @Override
    public LinkedHashMap<String, Object> getInputs() {
        // Propagate the type constraints for copied FreeObjects and the initialized elements for FreeArrays. // TODO Better place for this?
        Map<Object, Object> alreadyCloned;
        if (this.alreadyCloned != null && trackInputs != null) {
           alreadyCloned = new HashMap<>(this.alreadyCloned); // We regard potentially cloned prior elements.
        } else {
            throw new IllegalStateException("The method for which test cases are to be generated was not found: " + methodName);
        }
        LinkedHashMap<String, Object> restoredInputs = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : trackInputs.entrySet()) {
            Object inputClone = SolutionIterator.cloneVal(entry.getValue(), alreadyCloned);
            propagateInformationFromOutputToInput(inputClone, alreadyCloned);
            restoredInputs.put(entry.getKey(), inputClone);
        }

        return restoredInputs;
    }

    private void propagateInformationFromOutputToInput(Object inputCopy, Map<Object, Object> alreadyCloned) {
        if (inputCopy instanceof FreeArrayref) {
            FreeArrayref copy = (FreeArrayref) inputCopy;
            LinkedHashMap<Term, Object> copiedOriginalElements = new LinkedHashMap<>();
            for (Map.Entry<Term, Object> o : copy.getOriginalElements().entrySet()) {
                // We clone again to be sure. If the original values are not used for the current branch, we might change
                // objects which are later used.
                Object copiedOriginal = SolutionIterator.cloneVal(o.getValue(), alreadyCloned);
                // Propagate in case that lazy-initialization markers are anywhere within the array's objects.
                propagateInformationFromOutputToInput(copiedOriginal, alreadyCloned);
                copiedOriginalElements.put(o.getKey(), copiedOriginal);
            }
            // Set to the copied originals:
            copy.setFreeArrayElements(copiedOriginalElements);
        } else if (inputCopy instanceof Arrayref) {
            Arrayref copy = (Arrayref) inputCopy;
            for (int i = 0; i < copy.getLength(); i++) {
                Object element = copy.getElement(i);
                element = SolutionIterator.cloneVal(element, alreadyCloned);
                propagateInformationFromOutputToInput(element, alreadyCloned);
                copy.putElement(i, element);
            }
        } else if (inputCopy instanceof FreeObjectref) {
            FreeObjectref freeObjectInputCopy = (FreeObjectref) inputCopy;

            // We propagate the information of memorized variables to get the initial values.
            Map<Field, Object> memorizedVariables = freeObjectInputCopy.getMemorizedVariables();
            for (Map.Entry<Field, Object> memorized : memorizedVariables.entrySet()) {
                // It might be that a FreeObjectref was concretized to a specialization of the declared superclass.
                // In this case, we must add the initial version of these fields to the copy. The initial value of free fields
                // is stored in the FreeObjectref.memorizedVariables-mapping.
                // We clone the corresponding value to not alter the latter runtime-behavior.
                Object clonedMemorizedVariable = SolutionIterator.cloneVal(memorized.getValue(), alreadyCloned);
                freeObjectInputCopy.putField(memorized.getKey(), clonedMemorizedVariable);
            }
            // The previous steps ensures that the copy has the initial values of fields which were added due to
            // class constraints.
            // Now, for all fields which were free initialized by means of a FreeObjectrefInitialisers.LazyFieldMarker
            // we must replace this LazyFieldMarker by the initial value it was substituted for.
            for (Map.Entry<Field, FreeObjectrefInitialisers.LazyFieldMarker> entry : freeObjectInputCopy.getSubstitutedMarkers().entrySet()) {
                Field field = entry.getKey();
                FreeObjectrefInitialisers.LazyFieldMarker marker = entry.getValue();
                Object val = marker.getSubstituteFor();
                val = SolutionIterator.cloneVal(val, alreadyCloned);
                if (val instanceof FreeObjectref) {
                    propagateInformationFromOutputToInput(val, alreadyCloned);
                }
                freeObjectInputCopy.putField(field, val);
            }
        } else if (inputCopy instanceof Objectref) {
            Objectref copy = (Objectref) inputCopy;
            // Should already be copied (see getInputs()). We simply propagate.
            for (Map.Entry<Field, Object> entry : copy.getFields().entrySet()) {
                propagateInformationFromOutputToInput(entry.getValue(), alreadyCloned);
            }
        }
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
    public boolean[] getCover() {
        return executionPathListener.getCover();
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

    @Override
    public boolean isObjectMethod() {
        return isObjectMethod;
    }
}