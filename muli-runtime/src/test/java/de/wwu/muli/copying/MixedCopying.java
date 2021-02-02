package de.wwu.muli.copying;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MixedCopying {
    @Test
    public void test_mixedLabeling0() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ObjectInputs");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(7, leaves.length);
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());
        assertEquals(0, exceptions.size());
        Set<Integer> resultValues = new HashSet<>();
        for (Object leaf : leaves) {
            int val = ((Integer) ((Objectref) ((Value) leaf).value).getFields().values().toArray(new Object[0])[0]);
            assertTrue(0 < val && val < 8);
            resultValues.add(val);
        }
        assertEquals(7, resultValues.size());

    }
    @Test
    public void test_mixedLabeling1() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ArrayInputAndOutput");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(3, leaves.length);
    }

    @Test
    public void test_mixedLabeling2() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ObjectInputAndOutput");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(6, leaves.length);
    }

    @Test
    public void test_mixedLabeling3() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.NestedObjects");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).filter(x -> x instanceof Value).toArray();
        assertEquals(3, leaves.length);
    }

}
