package de.wwu.muli.copying;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayCopying {

    @Test
    public void test_arrayLabeling0() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ArrayPaths");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());
        assertEquals(0, exceptions.size());
        // Java doing Java things
        Set<Value> values = (Set<Value>) (Object) Arrays.stream(leaves).filter(x -> x instanceof Value).collect(Collectors.toSet());
        assertEquals(3, values.size());
        boolean foundFirst = false, foundSecond = false, foundThird = false;
        for (Value val : values) {
            Arrayref result = (Arrayref) val.value;
            if (result == null) {
                foundFirst = true;
            } else {
                Object[] elements = result.getRawElements();
                int e0 = (Integer) elements[0];
                int e1 = (Integer) elements[1];
                int e2 = (Integer) elements[2];
                if (e0 == 1 && e1 == 2 && e2 == 5) {
                    foundSecond = true;
                }
                if (e0 == 1 && e1 == 2 && e2 == 6) {
                    foundThird = true;
                }
            }
        }
        assertTrue(foundFirst);
        assertTrue(foundSecond);
        assertTrue(foundThird);
    }

    @Test
    public void test_arrayLabeling1() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.FreeArrayPaths");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());
        assertEquals(0, exceptions.size());
        Set<Value> values = (Set<Value>) (Object) Arrays.stream(leaves).filter(x -> x instanceof Value).collect(Collectors.toSet());
        assertEquals(2, values.size());
        boolean foundFirst = false, foundSecond = false;
        for (Value val : values) {
            Arrayref ar = (Arrayref) val.value;
            int v0 = (Integer) ar.getElement(0);
            int v1 = (Integer) ar.getElement(1);
            if (v0 == 3 && v1 == 2) {
                foundFirst = true;
            } else if (v0 == -5 && v1 == 5) {
                foundSecond = true;
            }
        }
        assertTrue(foundFirst);
        assertTrue(foundSecond);
    }
}
