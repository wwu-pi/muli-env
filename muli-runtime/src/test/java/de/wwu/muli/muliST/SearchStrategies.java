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

public class SearchStrategies {
    @Test
    public final void test_BreadthFirstSearchF2C() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.FlipTwoCoinsBFS");
        assertEquals(1, foundTrees.length);
        //System.out.println(foundTrees[0].toString());
        /* Expected tree is
            - Choice
                - Choice
                    - Value Objectref java.lang.Boolean (id: 10410 val: true)
                    - Fail
                - Value Objectref java.lang.Boolean (id: 10438 val: false)
         */
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(2, solutions.length);
        assertEquals("false\ntrue\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Test
    public final void test_DepthFirstSearchF2C() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.FlipTwoCoinsDFS");
        assertEquals(1, foundTrees.length);
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(2, solutions.length);
        assertEquals("true\nfalse\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Test
    public final void test_IterativeDeepeningSearchF2C() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.FlipTwoCoinsIDD");
        assertEquals(1, foundTrees.length);
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(2, solutions.length);
        assertEquals("true\nfalse\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }
    @Test
    public final void test_BreadthFirstSearchCC() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsBFS");
        assertEquals(1, foundTrees.length);
        //System.out.println(foundTrees[0].toString());
        /* Expected tree is
            - Choice
                - Choice
                    - Choice
                        - Value Objectref java.lang.Boolean (id: 10414 val: false)
                        - Fail
                    - Choice
                        - Value Objectref java.lang.Boolean (id: 10441 val: false)
                        - Fail
                - Choice
                    - Value Objectref java.lang.Boolean (id: 10444 val: false)
                    - Choice
                        - Value Objectref java.lang.Boolean (id: 10447 val: true)
                        - Fail
         */
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(4, solutions.length);
        assertEquals("false\nfalse\nfalse\ntrue\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Test
    public final void test_DepthFirstSearchCC() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsDFS");
        assertEquals(1, foundTrees.length);
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(4, solutions.length);
        assertEquals("false\nfalse\nfalse\ntrue\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Test
    public final void test_IterativeDeepeningSearchCC() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsIDD");
        assertEquals(1, foundTrees.length);
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(4, solutions.length);
        assertEquals("false\nfalse\nfalse\ntrue\n", TestablePrintStreamWrapper.outputStream().getBufferContents());
    }

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
