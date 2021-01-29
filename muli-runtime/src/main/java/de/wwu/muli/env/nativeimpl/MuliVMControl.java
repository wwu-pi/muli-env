package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.general.Logic;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.ExecutionMode;
import de.wwu.muli.SearchStrategy;
import de.wwu.muli.iteratorsearch.*;
import de.wwu.muli.listener.TcgListener;
import de.wwu.muli.search.NoFurtherSolutionsIndicator;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.solution.MuliFailException;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.io.*;
import java.lang.invoke.MethodType;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.Muli
 * @author Jan C. Dageförde
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // All methods will be called through the NativeWrapper, but static analysis doesn't know this.
public class MuliVMControl extends NativeMethodProvider {
    private static final String handledClassFQ = de.wwu.muli.Muli.class.getCanonicalName();
    private static ClassFile CLASS_SOLUTION = null;
    private static ClassFile ENUM_EXECUTIONMODE = null;
    private static ClassFile ENUM_SEARCH_STRATEGY = null;

    private static int failCounter = 0;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        ENUM_EXECUTIONMODE = classLoader.getClassAsClassFile(ExecutionMode.class.getCanonicalName());
        ENUM_SEARCH_STRATEGY = classLoader.getClassAsClassFile(SearchStrategy.class.getCanonicalName());
        CLASS_SOLUTION = classLoader.getClassAsClassFile(Solution.class.getCanonicalName());
        registerNatives();
    }

    public static void registerNatives() {
        // Execution mode.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "getVMExecutionMode",
                MethodType.methodType(Object.class, Frame.class),
                MethodType.methodType(ExecutionMode.class));
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setVMExecutionMode",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, ExecutionMode.class));

        // Search region iterator registration.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setSearchStrategyVM",
                MethodType.methodType(void.class, Frame.class, Object.class, Object.class),
                MethodType.methodType(void.class, de.wwu.muli.search.SolutionIterator.class, SearchStrategy.class));

        // `fail' operation.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "fail",
                MethodType.methodType(void.class, Frame.class),
                MethodType.methodType(MuliFailException.class));

        // `label' operation.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "label",
                MethodType.methodType(Object.class, Frame.class, Object.class),
                MethodType.methodType(Object.class, Object.class));

        // `label' operation.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "label",
                MethodType.methodType(void.class, Frame.class),
                MethodType.methodType(void.class));

        // `executeOnShell' operation.
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "executeOnShell",
                MethodType.methodType(Object.class, Frame.class, Object.class, Object.class, Object.class, Object.class, Object.class),
                MethodType.methodType(String.class, String.class, String.class, String.class, String.class, String.class));

        // set method name for test case generation
        NativeWrapper.registerNativeMethod(MuliVMControl.class, handledClassFQ, "setMethodForTCG",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, String.class));

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

    public static void setSearchStrategyVM(Frame frame, Object iterator, Object searchStrategy) {
        LogicVirtualMachine vm = (LogicVirtualMachine) frame.getVm();
        InitializedClass ic = ENUM_SEARCH_STRATEGY.getTheInitializedClass(vm);

        // parse param and set mode accordingly
        if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.IterativeDeepening.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new IterativeDeepeningDFS());
        } else if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.IterativeDeepeningNaive.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new IterativeDeepeningDFSNaive());
        } else if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.DepthFirstSearch.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new DepthFirstSearchAlgorithmWithLocalBacktracking());
        } else if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.DepthFirstSearchNaive.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new DepthFirstSearchAlgorithmWithGlobalBacktracking());
        } else if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.BreadthFirstSearch.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new BreadthFirstSearchWithLocalBacktracking());
        } else if (searchStrategy == ic.getField(ENUM_SEARCH_STRATEGY.getFieldByName(SearchStrategy.BreadthFirstSearchNaive.toString()))) {
            vm.setSearchStrategy((Objectref)iterator, new BreadthFirstSearch());
        }

        Globals.getInst().symbolicExecLogger.debug("Registered search strategy successfully.");

    }

    public static void setMethodForTCG(Frame frame, Object methodName) {
        LogicVirtualMachine vm = (LogicVirtualMachine) frame.getVm();
        ((TcgListener) vm.getExecutionListener()).setMethod((String) SolutionIterator.getValFromObjectref((Objectref) methodName)); // TODO refactor.
    }

    public static void fail(Frame frame) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();

        vm.reachedEndEvent();

        // Record Fail for this node, then do backtracking to next branch immediately.
        vm.getSearchAlgorithm().recordFail(new Fail());
        boolean hasNextChoice = vm.getSearchAlgorithm().trackBackAndTakeNextDecision((LogicVirtualMachine) frame.getVm());

        if (!hasNextChoice) {
            // Special handling if result is false, i.e. no choices are left.
            // Needs to be communicated to surrounding VM, however, fail() does not usually have a return
            // value and we cannot artificially throw exceptions here. Regardless, the next Muli CP computation
            // expects to be able to ASTORE something. Therefore we just push a fake "solution" to the operand stack.
            Object returnValue;
            try {
                final MugglToJavaConversion conversion = new MugglToJavaConversion(frame.getVm());
                returnValue = (Objectref) conversion.toMuggl(new NoFurtherSolutionsIndicator(), false);
            } catch (ConversionException e) {
                throw new RuntimeException("Could not create fake solution for surrounding program", e);
            }

            // Current frame has changed as a result of backtracking! Push value to correct opstack; likely tryAdvance().
            frame.getVm().getCurrentFrame().getOperandStack().push(returnValue);

            // tryAdvance of SolutionIterator will now handle this case.
        }


    }

    public static void label(Frame frame) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        try {
            vm.getSolverManager().getSolution();
        } catch (Exception e) {
            Globals.getInst().solverLogger.error("Labeling exception: " + e.getMessage());
        }
    }

    public static Object label(Frame frame, Object o) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        try {
            return vm.labelSolutionObject(o);
        } catch (Exception e) {
            Globals.getInst().solverLogger.error("Labeling exception: " + e.getMessage());
            return null; // TODO Throw a runtime exception instead.
        }
    }

    public static Object executeOnShell(Frame frame, Object cmd, Object pathToTemp, Object prefix, Object suffix, Object script) {
        String cmdStr = cmd instanceof Objectref ? NativeWrapper.stringObjectrefToString((Objectref) cmd) : (String) cmd;
        String pathToTempStr = pathToTemp instanceof Objectref ? NativeWrapper.stringObjectrefToString((Objectref) pathToTemp) : (String) pathToTemp;
        String prefixStr = prefix instanceof Objectref ? NativeWrapper.stringObjectrefToString((Objectref) prefix) : (String) prefix;
        String suffixStr = suffix instanceof Objectref ? NativeWrapper.stringObjectrefToString((Objectref) suffix) : (String) suffix;
        String scriptStr = script instanceof Objectref ? NativeWrapper.stringObjectrefToString((Objectref) script) : (String) script;

        final File path = new File(pathToTempStr);
        final File tempFile;
        try {
            tempFile = File.createTempFile(prefixStr, suffixStr, path);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        try (PrintWriter out = new PrintWriter(tempFile)) {
            out.println(scriptStr);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        String tempFileName = tempFile.getName();

        try {
            Process p = Runtime.getRuntime().exec(cmdStr + " " + pathToTempStr + tempFileName);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String s;
            while ((s = stdInput.readLine()) != null) {
                output.append(s);
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
}
