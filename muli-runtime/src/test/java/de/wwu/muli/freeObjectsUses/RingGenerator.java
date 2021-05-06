package de.wwu.muli.freeObjectsUses;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RingGenerator {

    @Test
    public final void testRingGenerator() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeObjectsUses.RingGenerator");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(5, leaves.length);
        for (int i = 0; i < leaves.length; i++) { // This test makes an assumption on the traversing order
            FreeObjectref first = (FreeObjectref) ((Value) leaves[i]).value;
            FreeObjectref next = first;
            int j = 0;
            Set<Object> ringFreeObjects = new HashSet<>();
            ringFreeObjects.add(first);
            boolean seen = false;
            do {
                next = (FreeObjectref) next.getField(first.getFieldForName("o"));
                if (ringFreeObjects.contains(next)) {
                    seen = true;
                } else {
                    ringFreeObjects.add(next);
                }
                j++;
            } while (first != next);
            assertTrue(seen);
        }
    }

}
