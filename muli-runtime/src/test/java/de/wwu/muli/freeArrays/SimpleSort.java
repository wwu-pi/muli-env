package de.wwu.muli.freeArrays;

import de.wwu.muggl.configuration.Defaults;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Arrayref;
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
        Arrayref sortedArray = (Arrayref) ((Value) leaves[0]).value;
        assertEquals(10, sortedArray.getLength());
        assertEquals(IntConstant.getInstance(1),  sortedArray.getElement(0));
        assertEquals(IntConstant.getInstance(5),  sortedArray.getElement(1));
        assertEquals(IntConstant.getInstance(8),  sortedArray.getElement(2));
        assertEquals(IntConstant.getInstance(12), sortedArray.getElement(3));
        assertEquals(IntConstant.getInstance(17), sortedArray.getElement(4));
        assertEquals(IntConstant.getInstance(27), sortedArray.getElement(5));
        assertEquals(IntConstant.getInstance(39), sortedArray.getElement(6));
        assertEquals(IntConstant.getInstance(42), sortedArray.getElement(7));
        assertEquals(IntConstant.getInstance(56), sortedArray.getElement(8));
        assertEquals(IntConstant.getInstance(78), sortedArray.getElement(9));
        System.out.println(sortedArray);
    }

    @After
    public void tearDown() {
        Defaults.EXCEPTION_IF_FREE_ARRAY_INDEX_OOB = true;
    }
}
