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
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class CheckIndex {

    @Test
    public final void test_checkIndexInBounds() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexInBounds");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
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
                fail("Unexpected leave: " + leave);
            }
        }
        assertEquals(0, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues, leaves.length);
    }

    @Test
    public final void test_checkIndexInBoundsAlt() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexInBoundsAlternative");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
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
                fail("Unexpected leave: " + leave);
            }
        }
        assertEquals(0, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues, leaves.length);
    }

    @Test
    public final void test_checkIndexOutOfBounds() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexOutOfBounds");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesFalses = 0;
        int numberExceptions = 0;
        for (Object leaf : leaves) {
            if (leaf instanceof Fail) {
                numberFails++;
            } else if (leaf instanceof Exception) {
                numberExceptions++;
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberExceptions);
        assertEquals(numberFails + numberValuesFalses + numberExceptions, leaves.length);
    }

    @Test @Ignore // TODO Free indexes not yet implemented for free objects in free arrays
    public final void test_checkObjectFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexOutOfBoundsAndContinuation");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(2, leaves.length);
        assertTrue(leaves[0] instanceof Exception);
        assertTrue(leaves[1] instanceof Value);
    }

    @Test
    public final void test_checkPrimitiveFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees =
                TestableMuliRunner.runApplication("applications.freeArrays.CheckIndexOutOfBoundsAndContinuationConstant");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertTrue(leaves[0] instanceof Exception);
        assertTrue(leaves[1] instanceof Value);
        assertEquals(2, leaves.length);
    }

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
