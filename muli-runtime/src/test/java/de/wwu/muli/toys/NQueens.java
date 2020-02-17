package de.wwu.muli.toys;

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

public class NQueens {
    @Test
    public final void test_nqueens() throws InterruptedException, ClassFileException {
         ST[] foundTrees = TestableMuliRunner.runApplication("applications.toys.NQueens");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        //assertEquals("\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
