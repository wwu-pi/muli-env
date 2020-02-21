package de.wwu.muli.freeObjects;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Checkcast {
    @Test
    public final void test_checkcastNonfree() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.CheckcastNonfree");
        // No non-determinism expected.
        assertEquals(0, foundTrees.length);
        assertEquals("Sit 1 ok.\n" + "Sit 2 ok.\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }
    /*@Test
    public final void test_checkcastNonfreeWithNondeterminism() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.CheckcastNonfreeND");
        assertEquals(2, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        System.out.println(foundTrees[1].toString());
        assertEquals("Sit 1 ok.\n" + "Sit 2 ok.\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }*/

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
