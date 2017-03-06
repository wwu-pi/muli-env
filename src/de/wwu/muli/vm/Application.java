package de.wwu.muli.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muli.env.MuliVMControl;

/**
 * An Application is the top level element of any execution. it instantiates and holds the reference to the
 * Muli logic virtual machine and the class loader. It will start the execution in a new thread and
 * provide access to the results of the execution.
 *
 * @author Jan C. Dageförde
 * @version 1.0.0, 2016-09-09
 */
public class Application extends de.wwu.muggl.vm.Application {
	/**
	 * Basic constructor.
	 * @param classLoader The main classLoader to use.
	 * @param initialClassName The class that is to be executed initially.
	 * @param method The method that is to be executed initially. It must be a method of the class initialClassName.
	 * @throws ClassFileException Thrown on fatal errors loading or parsing a class file.
	 * @throws InitializationException If initialization of auxiliary classes fails.
	 */
	public Application(
			MugglClassLoader classLoader,
			String initialClassName,
			Method method
			) throws ClassFileException, InitializationException {
		this.classLoader = classLoader;
		ClassFile classFile = this.classLoader.getClassAsClassFile(initialClassName);
		this.virtualMachine = new LogicVirtualMachine(this, this.classLoader, classFile, method);
		MuliVMControl.initialiseAndRegister(this.classLoader);
		Globals.getInst().logger.debug("Application set up for logic execution.");
	}

	/**
	 * Unused constructor - just to prevent superconstructors from running.
	 * @throws IllegalAccessError On any access.
	 */
	@SuppressWarnings("unused")
	private Application(
			String[] classPathEntries,
			String initialClassName,
			int initialMethodNumber
			) throws ClassFileException, InitializationException {
		throw new IllegalAccessError("Constructor not supported by muli: Not useful.");
	}
}
