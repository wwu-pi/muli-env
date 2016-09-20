package de.wwu.muli.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

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
		super(classLoader, initialClassName, method);
		ClassFile classFile = this.classLoader.getClassAsClassFile(initialClassName);
		this.virtualMachine = new LogicVirtualMachine(this, this.classLoader, classFile, method);
		if (Globals.getInst().logger.isDebugEnabled())
			Globals.getInst().logger.debug("Application set up for logic execution.");
	}

	/**
	 * Basic constructor for initialization without an existing class loader.
	 * @param classPathEntries A String array of class path entries.
	 * @param initialClassName The class that is to be executed initially.
	 * @param initialMethodNumber The methods number in the class file that is to be executed first.
	 * @throws ClassFileException Thrown on fatal errors loading or parsing a class file.
	 * @throws InitializationException If initialization of auxiliary classes fails.
	 */
	private Application(
			String[] classPathEntries,
			String initialClassName,
			int initialMethodNumber
			) throws ClassFileException, InitializationException {
		super(classPathEntries, initialClassName, initialMethodNumber);
		throw new UnsupportedOperationException("Constructor not supported by muli: Not useful.");
	}
}
