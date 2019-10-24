package de.wwu.muli.muliST;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleCoinApplications {

    @Test
    public final void test_FailCoin() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.muliST.FailCoin");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, leaves.length);
    }
}
