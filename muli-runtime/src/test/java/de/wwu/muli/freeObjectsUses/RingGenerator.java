package de.wwu.muli.freeObjectsUses;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RingGenerator {

    @Test
    public final void testRingGenerator() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjectsUses.RingGenerator");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(5, leaves.length);
        for (int i = 0; i < leaves.length; i++) {
            FreeObjectref first = (FreeObjectref) ((Value) leaves[i]).value;
            FreeObjectref next = first;
            int j = 0;
            do {
                next = (FreeObjectref) next.getField(first.getFieldForName("o"));
                j++;
            } while (first != next);
            assertEquals(i+1, j);
        }
        assertEquals("next was called ---" +
                "next was called next was called ---" +
                "next was called next was called next was called ---" +
                "next was called next was called next was called next was called ---" +
                "next was called next was called next was called next was called next was called ---", TestablePrintStreamWrapper.outputStream().getBufferContents());
        TestablePrintStreamWrapper.outputStream().resetBuffer();
    }

}
