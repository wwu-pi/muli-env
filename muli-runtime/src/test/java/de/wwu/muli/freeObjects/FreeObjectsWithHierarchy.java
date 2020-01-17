package de.wwu.muli.freeObjects;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FreeObjectsWithHierarchy {
    @Test
    public final void test_methodImplementingInterface() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.InheritedMethod");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // A binary choice for getArea, then one binary choice each for true/false -> One for Rectangle, one for Square, two failing.
        assertEquals(4, leaves.length);
        assertEquals(2, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        List<Object> collect = Arrays.stream(leaves).filter(x -> x instanceof Value).map(v -> ((Value) v).value).collect(Collectors.toList());
        // Show that the free object was interpreted with the correct type.
        assertTrue(collect.get(0).toString().endsWith(" val: applications.freeObjects.pojo.Rectangle)"));
        assertTrue(collect.get(1).toString().endsWith(" val: applications.freeObjects.pojo.Square)"));
    }
    @Test
    public final void test_methodImplementingAbstractClass() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.InheritedAbstractMethod");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // A binary choice for getArea, then one binary choice each for true/false -> One for Rectangle, one for Square, two failing.
        assertEquals(4, leaves.length);
        assertEquals(2, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        List<Object> collect = Arrays.stream(leaves).filter(x -> x instanceof Value).map(v -> ((Value) v).value).collect(Collectors.toList());
        // Show that the free object was interpreted with the correct type.
        assertTrue(collect.get(0).toString().endsWith(" val: applications.freeObjects.pojo2.Rectangle)"));
        assertTrue(collect.get(1).toString().endsWith(" val: applications.freeObjects.pojo2.Square)"));
    }
}
