package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.ExecutionMode;
import de.wwu.muli.Solution;
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
    private static ClassFile ENUM_EXECUTIONMODE = null;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        ENUM_EXECUTIONMODE = classLoader.getClassAsClassFile(de.wwu.muli.ExecutionMode.class.getCanonicalName());
        registerNatives();
    }

    public static void registerNatives() {
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMExecutionMode",
                MethodType.methodType(Object.class, Frame.class),
                MethodType.methodType(ExecutionMode.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setVMExecutionMode",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, ExecutionMode.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "recordSolutionAndBacktrackVM",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, Object.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "recordExceptionAndBacktrackVM",
                MethodType.methodType(void.class, Frame.class, Throwable.class),
                MethodType.methodType(void.class, Throwable.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMRecordedSolutions",
                MethodType.methodType(Solution[].class, Frame.class),
                MethodType.methodType(Solution[].class));

        // TODO public static native MuliFailException fail();

        // TODO private static native void setVMSymbolicExecutionTreeRoot();
        // TODO private static native void recordExceptionAndBacktrackVM(Throwable exception);

        // TODO private static native Solution getVMRecordedSolutions();

        Globals.getInst().logger.debug("MuliVMControl native method handlers registered");
    }

    public static Object getVMExecutionMode(Frame frame) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        if (Options.getInst().symbolicMode) {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName(de.wwu.muli.ExecutionMode.SYMBOLIC.toString()));
        } else {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName(de.wwu.muli.ExecutionMode.NORMAL.toString()));
        }

    }

    public static void setVMExecutionMode(Frame frame, Object executionMode) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        // parse param and set mode accordingly
        if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName(de.wwu.muli.ExecutionMode.SYMBOLIC.toString()))) {
            Options.getInst().symbolicMode = true;
        } else if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName(de.wwu.muli.ExecutionMode.NORMAL.toString()))) {
            Options.getInst().symbolicMode = false;
        }

    }

    public static void recordSolutionAndBacktrackVM(Frame frame, Object solutionObject) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        Globals.getInst().symbolicExecLogger.debug("Found solution: " + solutionObject);
        vm.saveSolution();

        // backtracking
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false
    }

    public static void recordExceptionAndBacktrackVM(Frame frame, Throwable solutionException) {
        // Actually, vm's `saveSolution` is currently well capable of handling exceptions as well!
        // Let's not duplicate code.
        recordSolutionAndBacktrackVM(frame, solutionException);
    }

    public static Solution[] getVMRecordedSolutions(Frame frame) {
        final ArrayList<Solution> solutions = ((LogicVirtualMachine)frame.getVm()).getSolutions();
        Solution[] result = new Solution[solutions.size()];
        return solutions.toArray(result);
    }
}
