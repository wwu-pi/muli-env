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
                int e0 = ((IntConstant) elements[0]).getValue();
                int e1 = ((IntConstant) elements[1]).getValue();
                int e2 = ((IntConstant) elements[2]).getValue();
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
}
