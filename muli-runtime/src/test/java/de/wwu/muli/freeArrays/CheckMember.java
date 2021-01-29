package de.wwu.muli.freeArrays;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CheckMember {
    @Test
    public final void test_checkMember() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckMember");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberNotFound = 0;
        Set<Integer> values = new HashSet<>();
        for (Object leave : leaves) {
            if (leave instanceof Fail) {
                numberFails++;
            } else if (leave instanceof Value) {
                Value val = (Value) leave;
                if (val.value instanceof Objectref) {
                    try {
                        Objectref booleanRef = ((Objectref) val.value);
                        HashMap<Field, Object> fields = booleanRef.getFields();
                        Collection<Object> valAsSet = fields.values();
                        if (valAsSet.size() != 1) {
                            throw new IllegalStateException("Should not occur.");
                        }
                        Integer value = (Integer) valAsSet.toArray()[0];
                        if (value == -1) {
                            numberNotFound++;
                        }
                        values.add(value);
                    } catch (java.lang.Exception e) {
                        fail("Unexpected exception: " + e);
                    }

                } else {
                    fail("Unknown value: " + val);
                }
            } else {
                fail("Unexpected leave: " + leave);
            }
        }
        assertTrue(values.contains(0));
        assertTrue(values.contains(1));
        assertTrue(values.contains(2));
        assertTrue(values.contains(-1));
        assertEquals(4, numberNotFound);
        assertEquals(4, values.size());
        assertEquals(numberFails + values.size() + (numberNotFound - 1), leaves.length);
    }
}
