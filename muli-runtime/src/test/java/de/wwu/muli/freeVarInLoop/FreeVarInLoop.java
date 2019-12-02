package de.wwu.muli.freeVarInLoop;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FreeVarInLoop {

    @Test
    public final void test_variablesInLoopIterationsAreDistinct() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeVarInLoop.LoopVars");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
    }

}
