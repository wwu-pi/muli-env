package de.wwu.muli.freeObjects;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleFreeObjects {
    @Test
    public final void test_fieldAccessIsFree() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.FieldAccess");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertTrue(Arrays.stream(leaves).allMatch(x -> x instanceof Value));
        String value0 = ((Value<Object>)leaves[0]).value.toString();
        assertTrue("Objectref <" + value0 + "> must encapsulate Integer(2)", value0.endsWith("val: 2)"));
        String value1 = ((Value<Object>)leaves[1]).value.toString();
        assertTrue("Objectref <" + value1 + "> must encapsulate Integer(1)", value1.endsWith("val: 1)"));
    }
    @Test
    public final void test_staticFieldIsNotFree() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.StaticFieldAccess");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        // Expect no branching at all, as variable is not free.
        assertEquals(1, leaves.length);
        assertTrue(Arrays.stream(leaves).allMatch(x -> x instanceof Value));
        String value0 = ((Value<Object>)leaves[0]).value.toString();
        assertTrue("Objectref <" + value0 + "> must encapsulate Integer(5)", value0.endsWith("val: 5)"));
    }

    // Disabled @Test
    public final void test_methodInvocation() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjects.SimpleMethod");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
    }
}
