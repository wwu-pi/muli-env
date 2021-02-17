package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import java.util.Arrays;

public class TestTCGAbsValueObjectMethod {

    @Test
    public void testTcgWithObjectMethod() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.TCGAbsValueObjectMethod");
        System.out.println(Arrays.toString(foundTrees));
    }
}
