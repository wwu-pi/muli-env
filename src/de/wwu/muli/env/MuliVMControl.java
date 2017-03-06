package de.wwu.muli.env;

import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.ExecutionMode;

import java.lang.invoke.MethodType;

/**
 * Created by j_dage01 on 06.03.17.
 */
public class MuliVMControl extends NativeMethodProvider {
    private static final String handledClassFQ = "de.wwu.muli.Muli";
    private static ClassFile ENUM_EXECUTIONMODE = null;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        ENUM_EXECUTIONMODE = classLoader.getClassAsClassFile("de.wwu.muli.ExecutionMode");
        registerNatives();
    }

    public static void registerNatives() {
        //TODO check types here: Object vs Objectref -- not quite sure how this works out.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMExecutionMode",
                MethodType.methodType(Object.class, Frame.class),
                MethodType.methodType(Object.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setVMExecutionMode",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, ExecutionMode.class));
        System.out.println("MuliVMControl native method handlers registered");
    }

    public static Object getVMExecutionMode(Frame frame) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        if (Options.getInst().symbolicMode) {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName("SYMBOLIC"));
        } else {
            return ic.getField(ENUM_EXECUTIONMODE.getFieldByName("NORMAL"));
        }

    }

    public static void setVMExecutionMode(Frame frame, Object executionMode) {
        InitializedClass ic = ENUM_EXECUTIONMODE.getTheInitializedClass(frame.getVm());

        // parse param and set mode accordingly
        if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName("SYMBOLIC"))) {
            Options.getInst().symbolicMode = true;
        } else if (executionMode == ic.getField(ENUM_EXECUTIONMODE.getFieldByName("NORMAL"))) {
            Options.getInst().symbolicMode = false;
            //TODO clear up all choicepoints! no backtracking beyond this point.
            // TODO the preceding TODO is probably nonsense: Collection takes place manually now.
        }
    }
}
