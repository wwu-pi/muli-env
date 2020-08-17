package de.wwu.muli;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muli.env.MuliRunner;

public class ExampleRunner {
    public static void main(String[] args) throws InitializationException, ClassFileException {
        MuliRunner.main(new String[]{"mulist.Partition3"});
    }
}
