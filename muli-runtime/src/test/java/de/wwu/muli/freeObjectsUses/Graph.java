package de.wwu.muli.freeObjectsUses;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Graph {

    @Test
    public final void testGraph() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjectsUses.Graph");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(5, leaves.length);
        TestablePrintStreamWrapper.outputStream().resetBuffer(); // To reset output stream
    }
}
