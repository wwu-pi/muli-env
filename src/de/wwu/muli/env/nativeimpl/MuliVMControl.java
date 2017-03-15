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
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.invoke.MethodType;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.Muli
 * @author Jan C. Dageförde
 */
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
            //TODO clear up all choicepoints! no backtracking beyond this point.
            // TODO the preceding TODO is probably nonsense: Collection takes place manually now.
        }

    }

    public static void recordSolutionAndBacktrackVM(Frame frame, Object solutionObject) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        // TODO record solution (possible types?)
        Globals.getInst().symbolicExecLogger.debug("Found solution: " + solutionObject);
        vm.saveSolution();

        // backtracking
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / loggin if result of trackBack is false
    }
}
