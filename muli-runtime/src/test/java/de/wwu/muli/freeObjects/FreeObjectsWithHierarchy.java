package de.wwu.muli.freeObjects;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FreeObjectsWithHierarchy {
    // Disabled @Test
    public final void test_methodInSubclasses() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.InheritedMethod");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // One for Rectangle, one for Square, one failing.
        assertEquals(3, leaves.length);
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
    }
}
