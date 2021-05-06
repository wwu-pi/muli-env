package de.wwu.muli.freeObjectsUses;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Logistics {
    @Test
    public final void testLogistics() throws InterruptedException, ClassFileException {
        long startTime = System.nanoTime();
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjectsUses.Logistics");
        long endTime = System.nanoTime();
        System.out.println("Needed time: " + (endTime - startTime));
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int solutionCount = 0;
        for (Object l : leaves) {
            if (l instanceof Value) {
                solutionCount++;
            }
        }
        assertEquals(1, solutionCount);
        TestablePrintStreamWrapper.outputStream().resetBuffer(); // To reset output stream
    }
}
