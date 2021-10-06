package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import java.util.Arrays;

public class TestTCGAbsValueStatic {

    @Test
    public void testTcg() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.TCGAbsValueStatic");
        //System.out.println(Arrays.toString(foundTrees));
    }
}
