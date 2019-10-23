package de.wwu.muli.env;

import java.util.Arrays;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;

import de.wwu.muli.vm.Application;
import de.wwu.muggl.common.TimeSupport;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class MuliRunner {

	private static final String MAIN_METHOD_NAME = "main";
	private static final String MAIN_METHOD_DESCRIPTOR = "([Ljava/lang/String;)V";
	private static final int THREAD_SLEEP_TIME = 50;
	protected final Application app;
    protected final MugglClassLoader classLoader;
	private boolean isRunning;

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			printUsage();
			return;
		}
		
		// The following is inspired by de.wwu.muggl.ui.gui.support.ExecutionRunner.run()
		// Initialize the Application.
		MuliRunner runner = null;
		try {
			runner = new MuliRunner(args);
		} catch (ClassFileException | InitializationException e) {
			throw new RuntimeException(e);
		}
		
		// Enter the main execution loop.
		try {
			final long timeStarted = System.currentTimeMillis();
			// Execute
			runner.startApplication();

            waitUntilExecutionFinishes(runner);

            // Finished the execution.
			final long milliSecondsRun = System.currentTimeMillis() - timeStarted;
			Globals.getInst().execLogger.info("Total running time: " + TimeSupport.computeRunningTime(milliSecondsRun, true));
			
			// Exit successfully, unless an exception occured
			if (runner.app.getVirtualMachine().getThrewAnUncaughtException()) {
				Object exception = runner.app.getVirtualMachine().getReturnedObject();
				if (exception == null) {
					throw new IllegalStateException("VM says that an uncaught exception was thrown, but no exception found. Terminating immediately.");
				} else if (exception instanceof NoExceptionHandlerFoundException) {
					// unwrap actual (maybe symbolic) exception  
					String[] uncaught = ((NoExceptionHandlerFoundException)exception).getUncaughtThrowableNameAndMessage();
					System.err.println("Unhandled exception: " + uncaught[0] + "; Message: " + uncaught[1]);
				} else if (exception instanceof Throwable) {
					throw new RuntimeException((Throwable)exception);
				}
			}

			// Exit the app runner thread.
			runner.app.abortExecution();

		} catch (InterruptedException e) {
			// Just give out a message and then abort.
			System.out.println("Muli Error: InterruptedException");
			throw new RuntimeException(e);
		}

	}

    protected static void waitUntilExecutionFinishes(MuliRunner runner) throws InterruptedException {
        // The first sleep should be shorter.
        boolean firstSleep = true;
        while (runner.isRunning()) {
            // Sleep for the desired time.
            if (firstSleep) {
                Thread.sleep(Globals.SAFETY_SLEEP_DELAY);
                firstSleep = false;
            } else {
                // Save the time sleeping started.
                final long sleepStarted = System.currentTimeMillis();
                int sleepFor = THREAD_SLEEP_TIME;
                final int maximumSleepingSlice = Globals.SAFETY_SLEEP_DELAY;
                // Continue to sleep until we slept long enough.
                while (sleepFor > 0) {
                    // Has the execution finished?
                    if (runner.getExecutionFinished())
                        break;
                    // And now sleep.
                    try {
                        /*
                         * Determine if the sleeping period is shorter than
                         * the maximum time to sleep before checking if the
                         * execution has finished. If the sleeping time is
                         * high and execution finishes in the meanwhile,
                         * there would be no output until sleeping is
                         * finished. So this is checked more frequently with
                         * higher sleeping times. It does not consume a lot
                         * more cpu (actually its not really appreciable),
                         * but the user will almost immediately get informed
                         * if the execution finished.
                         */
                        int sleepingSlice = Math.min(sleepFor, maximumSleepingSlice);
                        Thread.sleep(sleepingSlice);
                        // Sleeping finished as expected. So decrease
                        // the needed sleeping time by the minimum sleep
                        // delay.
                        sleepFor -= sleepingSlice;
                    } catch (InterruptedException e) {
                        // Sleeping was interrupted as the time to sleep
                        // was changed. Set it to the new time, but drop
                        // the time we slept already.
                        sleepFor = THREAD_SLEEP_TIME - (int) (System.currentTimeMillis() - sleepStarted);
                    }
                }
            }
        }
    }


    protected void startApplication() {
		this.app.start();
		this.isRunning = true;
	}


    protected boolean isRunning() {
		return this.isRunning;
	}

    protected MuliRunner(String[] args) throws ClassFileException, InitializationException {
	    this(args, new MugglClassLoader(new String[]{ "./system-classes/", "./examples/"}));
    }

    protected MuliRunner(String[] args, MugglClassLoader classLoader) throws ClassFileException, InitializationException {
		assert (args != null);
		assert (args.length > 0);
		
		// Initially, symbolic mode is false. During execution this flag might change.
		Options.getInst().symbolicMode = false;
		Options.getInst().logicMode = true;
		Options.getInst().actualCliPrinting = true;
		
		Globals.getInst().changeLogLevel(Level.INFO);// DEV: DEBUG // PROD: INFO
		Globals.getInst().execLogger.setLevel(Level.ERROR); // DEV: comment this line // PROD: remove comment
		Globals.getInst().parserLogger.setLevel(Level.WARN); // DEV: INFO // PROD: WARN

		
		// Accept class
		final String className = args[0];
		
		// Extract arguments
		String[] newArgs;
		if (args.length > 1) {
			newArgs = Arrays.copyOfRange(args, 1, args.length); 
		} else {
			newArgs = new String[0];
		}
		// TODO: remove args that control (Muli/Muggl) VM instead of program.
		
        // TODO: Remove fake cp; Enable more classpaths from -cp arg
        this.classLoader = classLoader;
		
		// Find main method
		final ClassFile classFile = this.classLoader.getClassAsClassFile(className);
		final Method mainMethod = classFile.getMethodByNameAndDescriptor(MAIN_METHOD_NAME, MAIN_METHOD_DESCRIPTOR);

		// Pass newArgs to invoked main method.
		mainMethod.setPredefinedParameters(new Object[] { newArgs });
		app = new Application(this.classLoader, className, mainMethod);
		
		this.isRunning = false;
		
	}
	
	private boolean getExecutionFinished() {
		// Continue only if the virtual machine executed by the Application has not changed.
		if (!this.app.getVmIsInitializing()) {
			// Find out, if the execution has finished.
			if (this.app.getExecutionFinished()) {
				this.isRunning = false;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Finalize the ExecutionRunner by finalizing the Application.
	 */
	@Override
	public void finalize() {
		try {
			// Finalize and clean up the Application.
			//if (this.app != null) {
			//	this.app.finalizeApplication();
			//	this.app.finalize();
			//}
		} finally {
			try {
				super.finalize();
			} catch (Throwable t) {
				// Log it, but do nothing.
				Globals.getInst().guiLogger.warn("Finalizing the Muli runner failed.");
			}
		}
	}

	private static void printUsage() {
		System.out.println("USAGE: java " + MuliRunner.class.getName() + " CLASS [ARG1 [ARG 2 ...]]");
		
	}

}
