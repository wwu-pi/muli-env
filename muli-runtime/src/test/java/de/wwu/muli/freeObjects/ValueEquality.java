package de.wwu.muli.freeObjects;

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
import static org.junit.Assert.assertTrue;

public class ValueEquality {
    @Test
    public final void test_nondeterministicInvoke() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.EqualsNondeterministicInvoke");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // There are three leaves for invalid types, two leaves for matching type but mismatched fields, and one leaf where both objects are equal.
        assertEquals(6, leaves.length);
        int sum = Arrays.stream(leaves).filter(leaf -> leaf instanceof Value).mapToInt(leaf -> (((Value) leaf).value.toString().endsWith(": true)")) ? 1 : 0).sum();
        // Only one leaf should be true.
        assertEquals(1, sum);
    }
    @Test
    public final void test_nondeterministicBody() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.EqualsNondeterministicBody");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(4, leaves.length);
    }
}
