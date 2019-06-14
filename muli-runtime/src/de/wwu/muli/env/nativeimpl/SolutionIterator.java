package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.iteratorsearch.LogicIteratorSearchAlgorithm;
import de.wwu.muli.iteratorsearch.NoSearchAlgorithm;
import de.wwu.muli.searchtree.Value;
import de.wwu.muli.solution.ExceptionSolution;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.invoke.MethodType;
import java.util.HashMap;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.search.SolutionIterator
 * @author Jan C. Dageförde
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // All methods will be called through the NativeWrapper, but static analysis doesn't know this.
public class SolutionIterator extends NativeMethodProvider {
    private static final String handledClassFQ = de.wwu.muli.search.SolutionIterator.class.getCanonicalName();
    private static ClassFile CLASS_SOLUTION = null;
    private static boolean classSolutionIsInitialised = false;
    private static boolean labelSolutions = true;
    private static int solutionCounter = 0;
    private static long totalSearchTime = 0L;
    private static long totalSolutionCount = 0L;
    private static boolean abortEarly = true;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        CLASS_SOLUTION = classLoader.getClassAsClassFile(Solution.class.getCanonicalName());
        registerNatives();
    }

    public static void registerNatives() {
        // Solutions - store and retrieve.
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "wrapSolutionAndFullyBacktrackVM",
                MethodType.methodType(Objectref.class, Frame.class, Object.class),
                MethodType.methodType(Solution.class, Object.class));
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "wrapExceptionAndFullyBacktrackVM",
                MethodType.methodType(Objectref.class, Frame.class, Object.class),
                MethodType.methodType(Solution.class, Throwable.class));

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

    public static Objectref wrapSolutionAndFullyBacktrackVM(Frame frame, Object solutionObject) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(vm);
            Objectref objectref = vm.getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Result " + solutionObject);
        vm.resetInstructionsExecutedSinceLastSolution();
        SolutionIterator.totalSearchTime += vm.recordSearchEnded();
        SolutionIterator.totalSolutionCount++;

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
            returnValue = (Objectref) conversion.toMuggl(new Solution(solutionObject), false);
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

    public static Objectref wrapExceptionAndFullyBacktrackVM(Frame frame, Object solutionException) {
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(vm);
            Objectref objectref = vm.getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        // solutionException is expected to be Objectref (most likely in symbExec) or Throwable (unlikely).
        Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Exception " + solutionException);
        vm.resetInstructionsExecutedSinceLastSolution();
        SolutionIterator.totalSearchTime += vm.recordSearchEnded();
        SolutionIterator.totalSolutionCount++;

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
            returnValue = (Objectref)  conversion.toMuggl(new ExceptionSolution(solutionException), false);
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
            // Label found solution.
            de.wwu.muggl.solvers.Solution solution;
            try {
                solution = vm.getSolverManager().getSolution();
                if (solutionObject instanceof Objectref) {
                    Objectref solutionObject2 = vm.getAnObjectref(((Objectref) solutionObject).getInitializedClass().getClassFile());
                    HashMap<Field, Object> fields = ((Objectref) solutionObject).getFields();
                    HashMap<Field, Object> fields2 = ((Objectref) solutionObject2).getFields();
                    fields.entrySet().forEach((entry) -> {
                        if (entry.getValue() instanceof Term) {
                            Term value = (Term) entry.getValue();
                            Term simplified = value.insert(solution, false);
                            Object newValue = simplified;
                            if (simplified.isConstant()) {
                                newValue = ((NumericConstant) simplified).getIntValue();
                            }
                            fields2.put(entry.getKey(), newValue);
                        } else {
                            fields2.put(entry.getKey(), entry.getValue());
                        }
                    });
                    solutionObject = solutionObject2;
                } else if (solutionObject instanceof Arrayref) {
                    Arrayref ar = (Arrayref) solutionObject;
                    Object[] elements = ar.getRawElements();
                    Arrayref result = new Arrayref(vm.getClassLoader().getClassAsClassFile("java.lang.Integer")
                            .getAPrimitiveWrapperObjectref(vm), elements.length);
                    for (int i = 0; i < elements.length; i++) {
                        Object newValue = elements[i];
                        if (elements[i] instanceof Term) {
                            Term value = (Term) elements[i];
                            Term simplified = value.insert(solution, false);

                            if (simplified.isConstant()) {
                                newValue = ((NumericConstant) simplified).getIntValue();
                            } else {
                                newValue = simplified;
                            }
                        }
                        result.putElement(i, newValue);
                    }
                    solutionObject = result;
                }
            } catch (TimeoutException | SolverUnableToDecideException | ClassFileException | PrimitiveWrappingImpossibleException e) {
                throw new RuntimeException(e);
            }
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
        LogicIteratorSearchAlgorithm currentIteratorSearchAlgorithm = ((LogicVirtualMachine) frame.getVm()).getSearchAlgorithm();
        if (currentIteratorSearchAlgorithm instanceof NoSearchAlgorithm) {
            throw new IllegalStateException("Must be inside an active search region, set by setVMActiveIterator.");
        }
        boolean hasAnotherChoice = currentIteratorSearchAlgorithm.takeNextDecision(((LogicVirtualMachine) frame.getVm()));
        if (hasAnotherChoice) {
            ((LogicVirtualMachine) frame.getVm()).recordSearchStarted();
        }
        // Might be false if search space was fully explored.
        return hasAnotherChoice;
    }

    private static void maybeAbortEarly(LogicVirtualMachine vm) {
        // 5000000000L = 5 seconds.
        if (abortEarly && totalSearchTime >= 5000000000L) {
            // Only for evaluation purposes.
            vm.getApplication().abortExecution();
            throw new RuntimeException("Search ends after 5 seconds. Total no. of solutions found: " + totalSolutionCount);
        }
    }
}
