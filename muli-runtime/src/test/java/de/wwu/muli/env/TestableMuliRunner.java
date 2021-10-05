package de.wwu.muli.env;

import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.vm.LogicVirtualMachine;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.fail;

public class TestableMuliRunner extends MuliRunner {
    private final static Path resourcesRoot;
    private static ArrayList<Map<Object, Object>> coverageMap = null;

    static {
        // Statically determine the path to precompiled resources (i.e., testable artifacts).
        // Resolve path of this class.
        URL resource = TestableMuliRunner.class.getClassLoader().getResource(TestableMuliRunner.class.getName().replace(".", "/") + ".class");
        Path thisClassLocation = Paths.get(resource.getFile());
        // Use it to determine classes root and resources root. Testable artifacts are in resources root.
        Path classesRoot = thisClassLocation.getParent().getParent().getParent().getParent().getParent();
        resourcesRoot = classesRoot.resolveSibling("resources");
    }

    public TestableMuliRunner(String[] args) throws ClassFileException, InitializationException {
        super(args, new MugglClassLoader(new String[]{"./system-classes/", resourcesRoot.toString()}));
    }

    /**
     * You are not supposed to call this! This class is intended to be used by unit tests only. In unit tests, use runApplication instead.
     * @param args
     */
    public static void main(String[] args) {
        throw new UnsupportedOperationException();
    }

    /**
     * Helps executing a single program.
     *
     * Shall print Errors on stack trace and raise according exceptions or otherwise finish gracefully.
     *
     * @author Jan C. Dageförde (2019), based on work by Max Schulze
     *
     * @return
     */
    public static ST[] runApplication(final String classFileName, final String[] args) throws ClassFileException, InterruptedException {
        Options.getInst().symbolicMode = false;
        Options.getInst().actualCliPrinting = true;
        Options.getInst().isUnitTest = true;

        // Initialize the Application.
        TestableMuliRunner runner = null;
        try {
            // Combine classFileName and args into a single array.
            String[] newargs = new String[args.length + 1];
            newargs[0] = classFileName;
            System.arraycopy(args, 0, newargs, 1, args.length);

            runner = new TestableMuliRunner(newargs);
        } catch (ClassFileException | InitializationException e) {
            throw new RuntimeException(e);
        }
        runner.startApplication();
        waitUntilExecutionFinishes(runner);

        // Find out if execution finished successfully.
        if (runner.app.errorOccured()) {
            // There was an error.
            fail("Execution did not finish successfully. The reason is:\n" + runner.app.fetchError());
        } else {
            if (runner.app.getVirtualMachine().getThrewAnUncaughtException()) {
                Object returnedObject = runner.app.getReturnedObject();
                if (returnedObject instanceof NoExceptionHandlerFoundException) {
                    returnedObject = ((NoExceptionHandlerFoundException)returnedObject).getUncaughtThrowable();
                }
                Objectref objectref = (Objectref)returnedObject;

                ClassFile throwableClassFile = objectref.getInitializedClass().getClassFile();
                Field detailMessageField = throwableClassFile.getFieldByName("detailMessage", true);
                ClassFile stringClassFile = runner.app.getVirtualMachine().getClassLoader()
                        .getClassAsClassFile("java.lang.String");
                Field stringValueField = stringClassFile.getFieldByNameAndDescriptor("value", "[C");
                Objectref stringObjectref = (Objectref) objectref.getField(detailMessageField);
                String message;
                if (stringObjectref == null) {
                    message = "null";
                } else {
                    Arrayref arrayref = (Arrayref) stringObjectref.getField(stringValueField);

                    // Convert it.
                    char[] characters = new char[arrayref.getLength()];
                    for (int a = 0; a < arrayref.getLength(); a++) {
                        characters[a] = (Character) arrayref.getElement(a);
                    }
                    message = new String(characters);
                }

                fail("Uncaught exception, no suitable exception handler: "
                        + throwableClassFile.getName().replace("/", ".") + " (" + message + ")");

            }
        }
        LogicVirtualMachine virtualMachine = (LogicVirtualMachine)runner.app.getVirtualMachine();
        System.out.println("Total time: " + virtualMachine.getMeasuredTimeSoFar());
        coverageMap = virtualMachine.getExecutionListener().getResult();
        ST[] allSearchTrees = virtualMachine.getAllSearchTreesDebug().toArray(new ST[0]);

        // Exit the app runner thread.
        runner.app.abortExecution();
        return allSearchTrees;
    }

    public static ST[] runApplication(final String classFileName) throws ClassFileException, InterruptedException {
        return runApplication(classFileName, new String[]{});
    }

    static public ArrayList<Map<Object, Object>> getCoverageMap(){
        return coverageMap;
    }

}
