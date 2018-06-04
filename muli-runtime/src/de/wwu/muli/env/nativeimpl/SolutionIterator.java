package de.wwu.muli.env.nativeimpl;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.ConversionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.execution.NativeMethodProvider;
import de.wwu.muggl.vm.execution.NativeWrapper;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.solution.ExceptionSolution;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.lang.invoke.MethodType;

/**
 * Provider for native methods of muli-cp's de.wwu.muli.search.SolutionIterator
 * @author Jan C. Dageförde
 */
@SuppressWarnings({"WeakerAccess", "unused"}) // All methods will be called through the NativeWrapper, but static analysis doesn't know this.
public class SolutionIterator extends NativeMethodProvider {
    private static final String handledClassFQ = de.wwu.muli.search.SolutionIterator.class.getCanonicalName();
    private static ClassFile CLASS_SOLUTION = null;
    private static boolean classSolutionIsInitialised = false;

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
                MethodType.methodType(Boolean.class, SolutionIterator.class));
        NativeWrapper.registerNativeMethod(SolutionIterator.class, handledClassFQ, "restoreChoicePointStateNextChoiceVM",
                MethodType.methodType(Boolean.class, Frame.class, Object.class),
                MethodType.methodType(Boolean.class, SolutionIterator.class));
*/
        Globals.getInst().logger.debug("MuliSolutionIterators native method handlers registered");
    }

    public static Objectref wrapSolutionAndFullyBacktrackVM(Frame frame, Object solutionObject) {
        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(frame.getVm());
            Objectref objectref = frame.getVm().getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Result " + solutionObject);
        vm.resetInstructionsExecutedSinceLastSolution();
        Objectref returnValue;
        try {
            final MugglToJavaConversion conversion = new MugglToJavaConversion(frame.getVm());
            returnValue = (Objectref) conversion.toMuggl(new Solution(solutionObject), false);
        } catch (ConversionException e) {
            throw new RuntimeException("Could not create Muggl VM object from Java object", e);
        }
        //System.out.println("solution, " + System.nanoTime());

        // backtracking
        //        // TODO augment!
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false

        return returnValue;
    }

    public static Objectref wrapExceptionAndFullyBacktrackVM(Frame frame, Object solutionException) {
        if (!classSolutionIsInitialised) {
            // Initialise de.wwu.muli.Solution inside the VM, so that areturn's type checks know an initialised class.
            CLASS_SOLUTION.getTheInitializedClass(frame.getVm());
            Objectref objectref = frame.getVm().getAnObjectref(CLASS_SOLUTION);
            classSolutionIsInitialised = true;
        }

        // solutionException is expected to be Objectref (most likely in symbExec) or Throwable (unlikely).
        LogicVirtualMachine vm = (LogicVirtualMachine)frame.getVm();
        Globals.getInst().symbolicExecLogger.debug("Record solution (iterator): Exception " + solutionException);
        vm.resetInstructionsExecutedSinceLastSolution();
        Objectref returnValue;
        try {
            final MugglToJavaConversion conversion = new MugglToJavaConversion(frame.getVm());
            returnValue = (Objectref)  conversion.toMuggl(new ExceptionSolution(solutionException), false);
        } catch (ConversionException e) {
            throw new RuntimeException("Could not create Muggl VM object from Java object", e);
        }

        // backtracking
        // TODO augment!
        vm.getSearchAlgorithm().trackBack(vm);
        // TODO consider special handling / logging if result of trackBack is false

        return returnValue;
    }

    public static Object getVMActiveIterator(Frame frame) {
        return ((LogicVirtualMachine)frame.getVm()).getCurrentSearchRegion();
    }

    public static void setVMActiveIterator(Frame frame, Object activeIterator) {
        ((LogicVirtualMachine)frame.getVm()).setCurrentSearchRegion((Objectref)activeIterator);
    }

}
