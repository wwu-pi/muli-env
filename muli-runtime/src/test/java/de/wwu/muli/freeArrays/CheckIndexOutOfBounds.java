package de.wwu.muli.freeArrays;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckIndexOutOfBounds {

    @Test
    public final void test_checkIndexOutOfBounds() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexOutOfBounds");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesFalses = 0;
        int numberExceptions = 0;
        for (Object leaf : leaves) {
            if (leaf instanceof Fail) {
                numberFails++;
            } else if (leaf instanceof Exception) {
                numberExceptions++;
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberExceptions);
        assertEquals(numberFails + numberValuesFalses + numberExceptions, leaves.length);
    }
}
