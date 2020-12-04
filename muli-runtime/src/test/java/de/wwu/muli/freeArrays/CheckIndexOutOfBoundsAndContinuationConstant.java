package de.wwu.muli.freeArrays;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.*;
import de.wwu.muli.searchtree.Exception;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class CheckIndexOutOfBoundsAndContinuationConstant {
    @Test
    public final void test_checkPrimitiveFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees =
                TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexOutOfBoundsAndContinuationConstant");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertTrue(leaves[0] instanceof Exception);
        assertTrue(leaves[1] instanceof Value);
        assertEquals(2, leaves.length);
    }
}
