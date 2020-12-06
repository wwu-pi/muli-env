package de.wwu.muli.freeArrays;

import de.wwu.muggl.configuration.Defaults;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.FreeArrayref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleSort {

    @Before
    public void setup() {
        // We do this to simplify the search procedure.
        // If disabled, there will be more leaves. Each additional
        // leave will be of type Exception.
        Defaults.EXCEPTION_IF_FREE_ARRAY_INDEX_OOB = false;
    }

    @Test
    public final void test_checkSortAlgorithm() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.SimpleSort");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, leaves.length);
        FreeArrayref sortedArray = (FreeArrayref) ((Value) leaves[0]).value;
        assertEquals(sortedArray.getLength(), sortedArray.getFreeArrayElements().size());
        assertEquals(1,  sortedArray.getFreeArrayElement(0));
        assertEquals(5,  sortedArray.getFreeArrayElement(1));
        assertEquals(8,  sortedArray.getFreeArrayElement(2));
        assertEquals(12, sortedArray.getFreeArrayElement(3));
        assertEquals(17, sortedArray.getFreeArrayElement(4));
        assertEquals(27, sortedArray.getFreeArrayElement(5));
        assertEquals(39, sortedArray.getFreeArrayElement(6));
        assertEquals(42, sortedArray.getFreeArrayElement(7));
        assertEquals(56, sortedArray.getFreeArrayElement(8));
        assertEquals(78, sortedArray.getFreeArrayElement(9));
        System.out.println(sortedArray);
    }

    @After
    public void tearDown() {
        Defaults.EXCEPTION_IF_FREE_ARRAY_INDEX_OOB = true;
    }
}
