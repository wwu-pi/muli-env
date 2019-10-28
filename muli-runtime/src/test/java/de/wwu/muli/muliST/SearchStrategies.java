package de.wwu.muli.muliST;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SearchStrategies {
    @Test
    public final void test_BreadthFirstSearch() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsBFS");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(4, solutions.length);
    }

    @Test
    public final void test_DepthFirstSearch() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsDFS");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(4, solutions.length);
    }

    @Test
    public final void test_IterativeDeepeningSearch() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.ComplicatedCoinsIDD");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] solutions = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(4, solutions.length);
    }
}
