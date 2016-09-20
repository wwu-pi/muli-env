package de.wwu.muli.env;

import java.util.Arrays;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;

import de.wwu.muli.vm.Application;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class MuliRunner {

	private static final String MAIN_METHOD_NAME = "main";
	private static final String MAIN_METHOD_DESCRIPTOR = "([Ljava/lang/String;)V";
	private Application app;

	public static void main(String[] args) {
		assert (args != null);
		if (args.length == 0) {
			printUsage();
			return;
		}
		
		try {
			MuliRunner runner = new MuliRunner(args);
			runner.executeClass();
			
			
		} catch (ClassFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void executeClass() {

		// Execute
this.app.start();
		
	}

	private MuliRunner(String[] args) throws ClassFileException, InitializationException {
		assert (args != null);
		assert (args.length > 0);
		
		Options.getInst().symbolicMode = true;
		Options.getInst().logicMode = true;
		
		Globals.getInst().changeLogLevel(Level.TRACE);
		Globals.getInst().execLogger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		// Accept class
		String className = args[0];
		
		// Extract arguments
		String[] newArgs;
		if (args.length > 1) {
			newArgs = Arrays.copyOfRange(args, 1, args.length); 
		} else {
			newArgs = new String[0];
		}
		// TODO: remove args that control VM instead of program.
		
		// Instantiate class loader
		MugglClassLoader classLoader = new MugglClassLoader(new String[]{"res/testfiles"});
		// TODO: Enable more classpaths from -cp arg
		
		// Find main method
		ClassFile classFile = classLoader.getClassAsClassFile(className);
		Method mainMethod = classFile.getMethodByNameAndDescriptor(MAIN_METHOD_NAME, MAIN_METHOD_DESCRIPTOR);


		app = new Application(classLoader, className, mainMethod);
		
	}

	private static void printUsage() {
		System.out.println("USAGE: java " + MuliRunner.class.getName() + " CLASS [ARG1 [ARG 2 ...]]");
		
	}

}
