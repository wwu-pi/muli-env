package de.wwu.muli;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muli.env.MuliRunner;
import de.wwu.muli.env.nativeimpl.SolutionIterator;

public class ExampleRunner {
    public static void main(String[] args) throws InitializationException, ClassFileException {
        SolutionIterator.labelSolutions = true;
        MuliRunner.main(new String[]{"mulist.Partition3"});
    }
}
