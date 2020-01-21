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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexImplementationHierarchy {
    @Test
    public final void test_severalImplementationsInHierarchy() throws InterruptedException, ClassFileException {
        // Testing WFLP2018, S. 3.2.
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.SeveralImplementations");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // There are three implementations for m() along the hierarchy.
        assertEquals(3, leaves.length);
        // Show that the free object was interpreted with the correct types.
        assertEquals("I am A\nI am D\nI am B\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
