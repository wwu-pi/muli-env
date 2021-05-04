package de.wwu.muli.freeObjectsUses;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

public class QueenSolutions {

    @Test
    public final void testGetSolutions() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjectsUses.NQueens");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        TestablePrintStreamWrapper.outputStream().resetBuffer(); // To reset output stream
    }
}
