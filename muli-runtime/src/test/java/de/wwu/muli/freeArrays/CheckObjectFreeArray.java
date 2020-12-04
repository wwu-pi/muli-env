package de.wwu.muli.freeArrays;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CheckObjectFreeArray {

    @Test
    public final void test_checkObjectFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckObjectFreeArray");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
        for (Object leaf : leaves) {
            if (leaf instanceof Fail) {
                numberFails++;
            } else if (leaf instanceof Value) {
                Value val = (Value) leaf;
                if (val.value instanceof Objectref) {
                    try {
                        Objectref booleanRef = ((Objectref) val.value);
                        HashMap<Field, Object> fields = booleanRef.getFields();
                        Collection<Object> valAsSet = fields.values();
                        if (valAsSet.size() != 1) {
                            throw new IllegalStateException("Should not occur.");
                        }
                        Integer booleanValue = (Integer) valAsSet.toArray()[0];
                        if (booleanValue == 1) {
                            numberValuesTrues++;
                        } else {
                            numberValuesFalses++;
                        }
                    } catch (java.lang.Exception e) { fail("Unexpected exception: " + e); }
                } else {
                    fail("Unknown value: " + val);
                }
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(2, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues, leaves.length);
    }
}
