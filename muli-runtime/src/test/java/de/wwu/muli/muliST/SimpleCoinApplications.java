package de.wwu.muli.muliST;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleCoinApplications {

    @Test
    public final void test_FailCoin() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.FailCoin");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Fail).count());
    }

    @Test
    public final void test_ComplicatedCoins() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsBFS");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(7, leaves.length);
        assertEquals(4, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        assertEquals(3, Arrays.stream(leaves).filter(x -> x instanceof Fail).count());
    }

    @Test
    public final void test_ExceptionsCanBeSolutions() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ExceptionCoin");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertTrue(Arrays.stream(leaves).anyMatch(x -> x instanceof Exception));
    }

    @Test
    public final void test_NoNonDeterminism() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.NoCoin");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, leaves.length);
    }

    @Test
    public final void test_NoSolution() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.NoSolution");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, leaves.length);
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Fail).count());
    }
}
