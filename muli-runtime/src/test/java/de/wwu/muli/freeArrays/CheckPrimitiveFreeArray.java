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
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CheckPrimitiveFreeArray {

    @Test
    public final void test_checkPrimitiveFreeArray() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckPrimitiveFreeArray");
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
        // Returns true for lengths 1 and 2, one time and one >further< time correspondingly.
        assertEquals(2, numberValuesTrues);
        // Returns false for lengths 0, 1, and 2 one time each.
        assertEquals(3, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues, leaves.length);
    }

    @Test
    public final void test_checkPrimitiveFreeArrayFreeIndex() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckPrimitiveFreeArrayFreeIndex");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
        int numberExceptions = 0;
        for (Object leaf : leaves) {
            if (leaf instanceof Value) {
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
            } else if (leaf instanceof Exception) {
                numberExceptions++;
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberExceptions);
        assertEquals(1, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberValuesFalses + numberValuesTrues + numberExceptions, leaves.length);
    }

    @Test
    public final void test_checkPrimitiveFreeArrayFreeIndexEnsureRestricted() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckPrimitiveFreeArrayFreeIndexEnsureRestricted");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
        int numberExceptions = 0;
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
            } else if (leaf instanceof Exception) {
                numberExceptions++;
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberExceptions);
        assertEquals(0, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues + numberExceptions, leaves.length);
    }

    @Test
    public final void test_checkPrimitiveFreeArrayFreeIndexFreeValue() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckPrimitiveFreeArrayFreeIndexFreeValue");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
        int numberValuesFalses = 0;
        int numberExceptions = 0;
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
            } else if (leaf instanceof Exception) {
                numberExceptions++;
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberExceptions);
        assertEquals(1, numberValuesTrues);
        assertEquals(1, numberValuesFalses);
        assertEquals(numberFails + numberValuesFalses + numberValuesTrues + numberExceptions, leaves.length);
    }

    @Test
    public final void test_checkPrimitiveFreeArrayFreeIndexFreeValueWithStore() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.freeArrays.CheckPrimitiveFreeArrayFreeIndexFreeValueWithStore");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        int numberFails = 0;
        int numberValuesTrues = 0;
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
                            fail("False is not expected: " + booleanValue);
                        }
                    } catch (java.lang.Exception e) { fail("Unexpected exception: " + e); }
                } else {
                    fail("Unknown value: " + val);
                }
            } else {
                fail("Unexpected leaf: " + leaf);
            }
        }
        assertEquals(1, numberValuesTrues);
        assertEquals(numberFails + numberValuesTrues, leaves.length);
    }
}
