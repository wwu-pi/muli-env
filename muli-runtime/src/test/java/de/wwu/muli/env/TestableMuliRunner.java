package de.wwu.muli.env;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muli.vm.Application;

public class TestableMuliRunner extends MuliRunner {
    public TestableMuliRunner(String[] args) throws ClassFileException, InitializationException {
        super(args);
    }

    @Override
    public void startApplication() {
        super.startApplication();
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    public Application getApp() {
        return super.app;
    }

    public static void main(String[] args) {
        // You are not supposed to call this! This class is intended to be used by unit tests only.
        throw new UnsupportedOperationException();
    }
}
