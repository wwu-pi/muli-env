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
	private final Application app;
	private boolean isRunning;

	public static void main(String[] args) {
		assert (args != null);
		if (args.length == 0) {
			printUsage();
			return;
		}
		
		// The following is inspired by de.wwu.muggl.ui.gui.support.ExecutionRunner.run()
		// Initialize the Application.

		MuliRunner runner = null;
		try {
			runner = new MuliRunner(args);
		} catch (ClassFileException | InitializationException e) {
			e.printStackTrace();
			return;
		}
		
		// Enter the main execution loop.
		try {
			final long timeStarted = System.currentTimeMillis();
			// Execute
			runner.startApplication();

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
					int sleepFor = 50; // TODO make configurable (was:
										// this.sleepFor)
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
							sleepFor = 50 - (int) (System.currentTimeMillis() - sleepStarted); // TODO
																								// make
																								// 50
																								// configurable
																								// (was:
																								// this.sleepFor)
						}
					}
				}
			}

			// Finished the execution.
			final long milliSecondsRun = System.currentTimeMillis() - timeStarted;
			Globals.getInst().execLogger.info("Total running time: " + TimeSupport.computeRunningTime(milliSecondsRun, true));
			
			// If the application terminated with an exception, output it. 
			if (runner.app.getVirtualMachine().getThrewAnUncaughtException()) {
				Object exception = runner.app.getVirtualMachine().getReturnedObject();
				if (exception == null) {
					throw new IllegalStateException("VM says that an uncaught exception was thrown, but no exception found. Terminating immediately.");
				} else if (exception instanceof NoExceptionHandlerFoundException) {
					// unwrap actual (maybe symbolic) exception  
					String[] uncaught = ((NoExceptionHandlerFoundException)exception).getUncaughtThrowableNameAndMessage();
					System.err.println("Unhandled exception: " + uncaught[0] + "; Message: " + uncaught[1]);
				} else if (exception instanceof Throwable) {
					// print regular stack trace.
					((Throwable)exception).printStackTrace();
				}
			}
			

		} catch (InterruptedException e) {
			// Just give out a message and then abort.
			System.out.println("Error: InterruptedException");
		}

	}


	private void startApplication() {
		this.app.start();
		this.isRunning = true;
	}


	private boolean isRunning() {
		return this.isRunning;
	}


	private MuliRunner(String[] args) throws ClassFileException, InitializationException {
		assert (args != null);
		assert (args.length > 0);
		
		// Initially, symbolic mode is false. During execution this flag might change.
		Options.getInst().symbolicMode = false;
		Options.getInst().logicMode = true;
		Options.getInst().actualCliPrinting = true;
		
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().execLogger.addAppender(new ConsoleAppender(new SimpleLayout(), "System.err"));
		//Globals.getInst().logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
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
		
		// Instantiate class loader
		final MugglClassLoader classLoader = new MugglClassLoader(new String[]{"res"});
		// TODO: Enable more classpaths from -cp arg
		
		// Find main method
		final ClassFile classFile = classLoader.getClassAsClassFile(className);
		final Method mainMethod = classFile.getMethodByNameAndDescriptor(MAIN_METHOD_NAME, MAIN_METHOD_DESCRIPTOR);
		// TODO pass newArgs to invoked main method


		app = new Application(classLoader, className, mainMethod);
		
		this.isRunning = false;
		
	}
	
	private boolean getExecutionFinished() {
		// Continue only if the virtual machine executed by the Application has not changed.
		if (!this.app.getVmIsInitializing()) {
			// Find out, if the execution has finished.
			if (this.app.getExecutionFinished()) {
				this.isRunning = false;
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
			if (this.app != null) {
				synchronized (this.app) {
					boolean forceCleanup;
					try {
						/*
						 * Force cleanup if an error occurred. Some errors or special circumstances might
						 * have the finalizer of the Application instance run and hence the cleanup
						 * invoked. This might be done too early, though, leaving much memory occupied.
						 * Running the clean up again will have great effect in that cases.
						 */
						forceCleanup = this.app.getVirtualMachine().errorOccured();
					} catch (NullPointerException e) {
						/*
						 * There is no virtual machine present any more. This means that the
						 * finalizer has been run already or there as another serious problem.
						 * It hence is a good idea to run cleanup again.
						 */
						forceCleanup = true;
					}
					this.app.cleanUp(forceCleanup);
				}
			}
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
