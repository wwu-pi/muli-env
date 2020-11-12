package de.wwu.muli.freeArrays;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CheckObjectFreeArray {

    @Test
    public final void test_checkPrimitiveFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckObjectFreeArray");
        assertEquals(3, foundTrees.length);
    }
}
