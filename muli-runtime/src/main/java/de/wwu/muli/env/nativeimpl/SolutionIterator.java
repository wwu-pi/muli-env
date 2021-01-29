package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.iteratorsearch.LogicIteratorSearchAlgorithm;
import de.wwu.muli.iteratorsearch.NoSearchAlgorithm;
import de.wwu.muli.listener.TcgListener;
import de.wwu.muli.searchtree.Value;
import de.wwu.muli.solution.ExceptionSolution;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.invoke.MethodType;
import java.util.*;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.search.SolutionIterator
 * @author Jan C. Dageförde
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // All methods will be called through the NativeWrapper, but static analysis doesn't know this.
public class SolutionIterator extends NativeMethodProvider {
    private static final String handledClassFQ = de.wwu.muli.search.SolutionIterator.class.getCanonicalName();
    private static ClassFile CLASS_SOLUTION = null;
    private static boolean classSolutionIsInitialised = false;
    public static boolean labelSolutions = true;
    private static int solutionCounter = 0;
    private static long totalSearchTime = 0L;
    private static long totalSolutionCount = 0L;
    private static boolean abortEarly = false;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        CLASS_SOLUTION = classLoader.getClassAsClassFile(Solution.class.getCanonicalName());
        registerNatives();
        LogicVirtualMachine vm =  ((LogicVirtualMachine) VirtualMachine.getLatestVM());
        vm.startMeasuringOverallTime();
    }

    public static void registerNatives() {
        // Solutions - store and retrieve.
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "wrapSolutionAndFullyBacktrackVM",
                MethodType.methodType(Objectref.class, Frame.class, Object.class, Object.class, Object.class),
                MethodType.methodType(Solution.class, Object.class, Boolean.class, String.class));
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "wrapExceptionAndFullyBacktrackVM",
                MethodType.methodType(Objectref.class, Frame.class, Object.class, Object.class, Object.class),
                MethodType.methodType(Solution.class, Throwable.class, Boolean.class, String.class));

        // Active search region / corresponding iterator.
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "getVMActiveIterator",
                MethodType.methodType(Object.class, Frame.class),
                MethodType.methodType(de.wwu.muli.search.SolutionIterator.class));
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "setVMActiveIterator",
                MethodType.methodType(void.class, Frame.class, Object.class),
                MethodType.methodType(void.class, de.wwu.muli.search.SolutionIterator.class));

        // Choice point navigation.
        /*NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "choicePointHasAdditionalChoiceVM",
                MethodType.methodType(Boolean.class, Frame.class, Object.class),
                MethodType.methodType(Boolean.class, SolutionIterator.class));*/
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "replayInverseTrailForNextChoiceVM",
                MethodType.methodType(boolean.class, Frame.class),
                MethodType.methodType(boolean.class));

        Globals.getInst().logger.debug("MuliSolutionIterators native method handlers registered");
    }

    public static Object getValFromObjectref(Objectref val) {
        for (Map.Entry<Field, Object> entry : val.getFields().entrySet()) {
            if (!entry.getKey().getName().equals("value")) {
                throw new IllegalStateException("Should be an Objectref of Boolean which only has the field value.");
            }
            if (entry.getKey().getType().equals("char[]")) {
                Object[] chars = ((Arrayref) entry.getValue()).getRawElements();
                return Arrays.toString(chars)
                        .replaceAll(",", "")
                        .replaceAll("]", "")
                        .replaceAll("\\[", "")
                        .replaceAll(" ", ""); // TODO Refactor.
            } else {
                return ((Integer) entry.getValue()) == 1;
            }
        }
        throw new IllegalStateException("Should not occur.");
    }

    public static Objectref wrapSolutionAndFullyBacktrackVM(Frame frame, Object solutionObject, Object generateTest, Object methodToTest) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        boolean getTest = (Boolean) getValFromObjectref((Objectref) generateTest);
        String method = null;
        if (getTest) {
            method = (String) getValFromObjectref((Objectref) methodToTest);
        }

        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(vm);
            Objectref objectref = vm.getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        if (Globals.getInst().symbolicExecLogger.isDebugEnabled()) {
            Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Result " + solutionObject);
        }
        vm.resetInstructionsExecutedSinceLastSolution();
        long timeSpent = vm.recordSearchEnded();
        // System.out.print(timeSpent + ",");
        SolutionIterator.totalSearchTime += timeSpent;
        SolutionIterator.totalSolutionCount++;

        // We clone first to not alter the objects used after backtracking during labelling
        solutionObject = cloneSolution(solutionObject);

        // Label solution if enabled.
        solutionObject = maybeLabel(vm, solutionObject);

        // Store Value node in ST.
        Value val = new Value(solutionObject);
        vm.getSearchAlgorithm().recordValue(val);

        // Maybe abort after an amount of time has elapsed -- only for evaluation purposes.
        maybeAbortEarly(vm);

        // Wrap and return.
        Objectref returnValue;
        try {
            final MugglToJavaConversion conversion = new MugglToJavaConversion(vm);
            Solution solution;
            if (getTest) {
                TcgListener tcgListener = (TcgListener) vm.getExecutionListener();
                LinkedHashMap<String, Object> inputs = copyAndLabelEach(vm, tcgListener.getInputs());

                solution = new Solution(solutionObject, inputs, tcgListener.getClassName(), tcgListener.getMethodName(), tcgListener.getCover());
            } else {
                solution = new Solution(solutionObject);
            }
            returnValue = (Objectref) conversion.toMuggl(solution, false);



            vm.reachedEndEvent();
            // TODO Add ListenerData...issue: def-use-chains and achievable coverage only known after all test cases are accumulated
            // TODO A stream of test cases would only make sense without test case reduction
        } catch (ConversionException e) {
            throw new RuntimeException("Could not create Muggl VM object from Java object", e);
        }
        // Backtracking.
        int pcBeforeBacktracking = vm.getPc();
        vm.getSearchAlgorithm().trackBackToRoot(vm);

        // Make sure next frame (which is the reincarnation of tryAdvance) will continue at the same PC as we left off (instead of at the pc of the root, which would result in an infinite loop).
        int nextPc;
        try {
            nextPc = pcBeforeBacktracking + 1 + vm.getCurrentFrame().getMethod().getInstructionsAndOtherBytes()[pcBeforeBacktracking].getNumberOfOtherBytes();
        } catch (InvalidInstructionInitialisationException e) {
            throw new RuntimeException(e);
        }
        vm.getCurrentFrame().setPc(nextPc);

        return returnValue;
    }

    protected static LinkedHashMap<String, Object> copyAndLabelEach(LogicVirtualMachine vm, LinkedHashMap<String, Object> toCopy) {
        LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
        Map<Object, Object> alreadyCopied = new HashMap<>();
        for (Map.Entry<String, Object> entry : toCopy.entrySet()) {
            Object copiedVal = cloneVal(entry.getValue(), alreadyCopied);
            Object copiedAndLabelledVal = maybeLabel(vm, copiedVal);
            copy.put(entry.getKey(), copiedAndLabelledVal);
        }

        return copy;
    }

    protected static Object cloneSolution(Object solutionObject) {
        // Try cloning the solution in order to prevent its contents from being backtracked later on.
        return cloneVal(solutionObject, new HashMap<>());
    }

    public static Object cloneVal(Object val, Map<Object, Object> alreadyCloned) {
        if (val == null) {
            return null;
        }
        Object alreadyInResults = alreadyCloned.get(val);
        if (alreadyInResults != null) {
            return alreadyInResults;
        }
        // We add new values in the cloneObjectref, cloneArrayref, and clonePrimitive methods
        // to avoid circular dependencies. The "hull", e.g., an empty Objectref, is immediately added.
        if (val instanceof Objectref) {
            return cloneObjectref((Objectref) val, alreadyCloned);
        } else if (val instanceof Arrayref) {
            return cloneArrayref((Arrayref) val, alreadyCloned);
        } else { // Is primitive
            return clonePrimitive(val, alreadyCloned);
        }
    }

    protected static Objectref cloneObjectref(Objectref o, Map<Object, Object> alreadyCloned) {
        Objectref result;
        if (o instanceof FreeObjectref) {
            result = new FreeObjectref((FreeObjectref) o);
        } else {
            result = VirtualMachine.getLatestVM().getAnObjectref(o.getInitializedClass().getClassFile());
        }
        alreadyCloned.put(o, result);
        for (Map.Entry<Field, Object> entry : o.getFields().entrySet()) {
            Object val = entry.getValue();
            Object clonedVal = cloneVal(val, alreadyCloned);
            result.putField(entry.getKey(), clonedVal);
        }
        return result;
    }

    protected static Arrayref cloneArrayref(Arrayref a, Map<Object, Object> alreadyCloned) {
        if (a instanceof FreeArrayref) {
            FreeArrayref fa = new FreeArrayref((FreeArrayref) a);
            alreadyCloned.put(a, fa);
            Term copiedLengthTerm = (Term) cloneVal(fa.getLengthTerm(), alreadyCloned);
            Map<Term, Object> copiedElements = new HashMap<>();
            for (Map.Entry<Term, Object> entry : fa.getFreeArrayElements().entrySet()) {
                Term key = (Term) cloneVal(entry.getKey(), alreadyCloned);
                Object val = cloneVal(entry.getValue(), alreadyCloned);
                copiedElements.put(key, val);
            }
            fa.setFreeArrayElements(copiedElements);
            fa.setLengthTerm(copiedLengthTerm);
            return fa;
        }

        Arrayref result;
        if (a instanceof ModifieableArrayref) {
            result = new ModifieableArrayref((ModifieableArrayref) a);
        } else {
            result = new Arrayref(a);
        }
        alreadyCloned.put(a, result);
        for (int i = 0; i < a.getLength(); i++) {
            Object clonedVal = cloneVal(a.getElement(i), alreadyCloned);
            result.putElement(i, clonedVal);
        }
        return result;
    }

    protected static Object clonePrimitive(Object p, Map<Object, Object> alreadyCloned) {
        if (p instanceof Number || p instanceof Term) {
            alreadyCloned.put(p, p);
            return p;
        } else {
            throw new IllegalStateException("Not supported: " + p.getClass());
        }
    }

    public static Objectref wrapExceptionAndFullyBacktrackVM(Frame frame, Object solutionException, Object wrapInputs, Object generateTest) { // TODO wrap inputs
        LogicVirtualMachine vm = (LogicVirtualMachine) frame.getVm();
        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(vm);
            Objectref objectref = vm.getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        // solutionException is expected to be Objectref (most likely in symbExec) or Throwable (unlikely).
        if (Globals.getInst().symbolicExecLogger.isDebugEnabled()) {
            Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Exception " + solutionException);
        }
        vm.resetInstructionsExecutedSinceLastSolution();
        SolutionIterator.totalSearchTime += vm.recordSearchEnded();
        SolutionIterator.totalSolutionCount++;

        // We clone first to not alter the objects used after backtracking during labelling
        solutionException = cloneSolution(solutionException);

        // Label solution if enabled.
        solutionException = maybeLabel(vm, solutionException);

        // Store Exception node in ST.
        de.wwu.muli.searchtree.Exception exception = new de.wwu.muli.searchtree.Exception(solutionException);
        vm.getSearchAlgorithm().recordException(exception);

        // Maybe abort after an amount of time has elapsed -- only for evaluation purposes.
        maybeAbortEarly(vm);

        Objectref returnValue;
        try {
            final MugglToJavaConversion conversion = new MugglToJavaConversion(vm);
            returnValue = (Objectref) conversion.toMuggl(new ExceptionSolution(solutionException), false);
        } catch (ConversionException e) {
            throw new RuntimeException("Could not create Muggl VM object from Java object", e);
        }

        // Backtracking.
        int pcBeforeBacktracking = vm.getPc();
        vm.getSearchAlgorithm().trackBackToRoot(vm);
        // TODO consider special handling / logging if result of trackBack is false

        // Make sure next frame (which is the reincarnation of tryAdvance) will continue at the same PC as we left of (instead of at the root choice point).
        int nextPc;
        try {
            nextPc = pcBeforeBacktracking + 1 + vm.getCurrentFrame().getMethod().getInstructionsAndOtherBytes()[pcBeforeBacktracking].getNumberOfOtherBytes();
        } catch (InvalidInstructionInitialisationException e) {
            throw new RuntimeException(e);
        }
        vm.getCurrentFrame().setPc(nextPc);

        return returnValue;
    }

    private static Object maybeLabel(LogicVirtualMachine vm, Object solutionObject) {
        if (labelSolutions) {
            return vm.labelSolutionObject(solutionObject);
        }

        return solutionObject;
    }

    public static Object getVMActiveIterator(Frame frame) {
        return ((LogicVirtualMachine)frame.getVm()).getCurrentSearchRegion();
    }

    public static void setVMActiveIterator(Frame frame, Object activeIterator) {
        ((LogicVirtualMachine)frame.getVm()).setCurrentSearchRegion((Objectref)activeIterator);
    }

    public static boolean replayInverseTrailForNextChoiceVM(Frame frame) {
        LogicVirtualMachine vm = ((LogicVirtualMachine) frame.getVm());
        LogicIteratorSearchAlgorithm currentIteratorSearchAlgorithm = vm.getSearchAlgorithm();
        if (currentIteratorSearchAlgorithm instanceof NoSearchAlgorithm) {
            throw new IllegalStateException("Must be inside an active search region, set by setVMActiveIterator.");
        }
        boolean hasAnotherChoice = currentIteratorSearchAlgorithm.takeNextDecision(vm);
        if (hasAnotherChoice) {
            vm.recordSearchStarted();
        }
        // Might be false if search space was fully explored.
        return hasAnotherChoice;
    }

    private static void maybeAbortEarly(LogicVirtualMachine vm) {
        // 5000000000L = 5 seconds.
        if (abortEarly && totalSearchTime >= 10_000_000_000L) {
            // Only for evaluation purposes.
            vm.getApplication().abortExecution();
            throw new RuntimeException("Search ended after 10 seconds. Total no. of solutions found: " + totalSolutionCount);
        }
    }
}