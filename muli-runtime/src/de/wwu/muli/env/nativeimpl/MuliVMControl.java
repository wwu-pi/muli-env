package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.ExecutionMode;
import de.wwu.muli.solution.MuliFailException;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.invoke.MethodType;
import java.util.ArrayList;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.Muli
 * @author Jan C. Dageförde
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // All methods will be called through the NativeWrapper, but static analysis doesn't know this.
public class MuliVMControl extends NativeMethodProvider {
    private static final String handledClassFQ = de.wwu.muli.Muli.class.getCanonicalName();
    private static ClassFile CLASS_SOLUTION = null;
    private static ClassFile ENUM_EXECUTIONMODE = null;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        ENUM_EXECUTIONMODE = classLoader.getClassAsClassFile(ExecutionMode.class.getCanonicalName());
        CLASS_SOLUTION = classLoader.getClassAsClassFile(Solution.class.getCanonicalName());
        registerNatives();
    }

    public static void registerNatives() {
        // Execution mode
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMExecutionMode",
                MethodType.methodType(Object.class, Frame.class),
                MethodType.methodType(ExecutionMode.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setVMExecutionMode",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, ExecutionMode.class));

        // Solutions - store and retrieve
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "recordSolutionAndBacktrackVM",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, Object.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "recordExceptionAndBacktrackVM",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, Throwable.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMRecordedSolutions",
                MethodType.methodType(Arrayref.class, Frame.class),
                MethodType.methodType(Solution[].class));

        // `fail' construct
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "fail",
                MethodType.methodType(void.class, Frame.class),
                MethodType.methodType(MuliFailException.class));

        // `label' operator
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "label",
                MethodType.methodType(void.class, Frame.class),
                MethodType.methodType(void.class));

        Globals.getInst().logger.debug("MuliVMControl native method handlers registered");
    }

    public static Object getVMExecutionMode(Frame frame) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        if (Options.getInst().symbolicMode) {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName(ExecutionMode.SYMBOLIC.toString()));
        } else {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName(ExecutionMode.NORMAL.toString()));
        }

    }

    public static void setVMExecutionMode(Frame frame, Object executionMode) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        // parse param and set mode accordingly
        if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName(ExecutionMode.SYMBOLIC.toString()))) {
            Options.getInst().symbolicMode = true;
        } else if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName(ExecutionMode.NORMAL.toString()))) {
            Options.getInst().symbolicMode = false;
        }

    }

    public static void recordSolutionAndBacktrackVM(Frame frame, Object solutionObject) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        Globals.getInst().symbolicExecLogger.debug("Record solution: Result " + solutionObject);
        vm.saveSolutionObject(solutionObject);
        //System.out.println("solution, " + System.nanoTime());

        // backtracking
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false
    }

    public static void recordExceptionAndBacktrackVM(Frame frame, Object solutionException) {
        // solutionException is expected to be Objectref (most likely in symbExec) or Throwable (unlikely).
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        Globals.getInst().symbolicExecLogger.debug("Record solution: Exception " + solutionException);
        vm.saveSolutionException(solutionException);

        // backtracking
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false
    }

    public static Arrayref getVMRecordedSolutions(Frame frame) {
        // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
        CLASS_SOLUTION.getTheInitializedClass(frame.getVm());
        Objectref objectref = frame.getVm().getAnObjectref(CLASS_SOLUTION);

        // Retrieve solutions from VM and pack them into an array(ref).
        final ArrayList<Solution> solutions = ((LogicVirtualMachine)frame.getVm()).getSolutions();
        final int solutioncount = solutions.size();
        final Arrayref returnvalue = new Arrayref(objectref, solutioncount);
        final MugglToJavaConversion conversion = new MugglToJavaConversion(frame.getVm());
        for (int i = 0; i < solutioncount; i++) {
            try {
                returnvalue.putElement(i, conversion.toMuggl(solutions.get(i), false));
            } catch (ConversionException e) {
                throw new RuntimeException("Could not create Muggl VM object from Java object", e);
            }
        }
        return returnvalue;
    }

    public static void fail(Frame frame) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();

        // backtracking
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false
        // TODO is behaviour different in streams?
    }
    public static void label(Frame frame) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        try {
            vm.getSolverManager().getSolution();
        } catch (Exception e) {
            Globals.getInst().solverLogger.error("Labeling exception: " + e.getMessage());
        }

    }
}
