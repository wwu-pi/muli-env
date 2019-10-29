package de.wwu.muli.muliST;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FiniteInfinity {

    @Test
    public final void test_StreamsCanTerminateEarly() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.NonTerminatingCoin");
        assertEquals(1, foundTrees.length);
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(5, solutions.length);
        assertEquals("5\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
