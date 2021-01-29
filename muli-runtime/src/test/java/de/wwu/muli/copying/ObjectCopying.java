package de.wwu.muli.copying;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muggl.vm.initialization.FreeObjectref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ObjectCopying {

    @Test // TODO We probably should make this less complex.
    public void test_objectLabeling0() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ObjectPaths");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());
        assertEquals(0, exceptions.size());
        // Java doing Java things
        Set<Value> values = (Set<Value>) (Object) Arrays.stream(leaves).filter(x -> x instanceof Value).collect(Collectors.toSet());
        assertEquals(4, values.size());
        boolean foundFirst = false, foundSecond = false, foundThird = false, foundFourth = false;
        int[][] vals = new int[4][3];
        int i = 0;
        // First extract the values
        for (Value val : values) {
            Objectref checkclass = (Objectref) val.value;
            Map<Field, Object> fields = checkclass.getFields();
            for (Map.Entry<Field, Object> entry : fields.entrySet()) {
                if (entry.getKey().getName().equals("val")) {
                    int v0 = ((IntConstant) entry.getValue()).getIntValue();
                    vals[i][0] = v0;
                } else if (entry.getKey().getName().equals("nestedClass")) {
                    Objectref nc = (Objectref) entry.getValue();
                    Map<Field, Object> nfs = nc.getFields();
                    Field valField = nfs.keySet().stream().filter(f -> f.getName().equals("val")).collect(Collectors.toList()).get(0);
                    int v1 = ((IntConstant) nfs.get(valField)).getIntValue();
                    vals[i][1] = v1;
                } else {
                    Objectref nc = (Objectref) entry.getValue();
                    Map<Field, Object> nfs = nc.getFields();
                    Field valField = nfs.keySet().stream().filter(f -> f.getName().equals("val")).collect(Collectors.toList()).get(0);
                    int v2 = ((IntConstant) nfs.get(valField)).getIntValue();
                    vals[i][2] = v2;
                }
            }
            i++;
        }
        // Check if each result is given.
        for (int[] oneResult : vals) {
            if (oneResult[0] > 2 && oneResult[1] <=5 && oneResult[2] >= 3) {
                foundFirst = true;
            } else if (oneResult[0] == 2 && oneResult[1] <= 4 && oneResult[2] >= 3) {
                foundSecond = true;
            } else if (oneResult[0] == 2 && oneResult[1] == 5 && oneResult[2] > 3) {
                foundThird = true;
            } else if (oneResult[0] == 2 && oneResult[1] == 5 && oneResult[2] == 3) {
                foundFourth = true;
            }
        }

        assertTrue(foundFirst);
        assertTrue(foundSecond);
        assertTrue(foundThird);
        assertTrue(foundFourth);
    }

    @Test
    public void test_objectLabeling1() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.FreeObjectPaths");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());
        assertEquals(0, exceptions.size());
        // Java doing Java things
        Set<Value> values = (Set<Value>) (Object) Arrays.stream(leaves).filter(x -> x instanceof Value).collect(Collectors.toSet());
        assertEquals(3, values.stream().filter(x -> x.value!=null).count());
        boolean foundFirst = false, foundSecond = false, foundThird = false, foundNull = false;
        for (Value val : values) {
            FreeObjectref a = (FreeObjectref) val.value;
            if (a == null) {
                foundNull = true;
                continue;
            }
            Map<Field, Object> fields = a.getFields();
            Set<String> possibleTypes = a.getPossibleTypes();
            assertEquals(1, possibleTypes.size());
            if (possibleTypes.contains("applications.copying.pojo.A")) {
                fields.keySet().stream().forEach(f -> {
                    if (f.getName().equals("val")) {
                        assertEquals(5, ((IntConstant) fields.get(f)).getIntValue());
                    }
                });
                foundFirst = true;
            } else if (possibleTypes.contains("applications.copying.pojo.B")) {
                fields.keySet().stream().forEach(f -> {
                    if (f.getName().equals("val")) {
                        assertEquals(8, ((IntConstant) fields.get(f)).getIntValue());
                    } else if (f.getName().equals("bval")) {
                        assertEquals(6, ((IntConstant) fields.get(f)).getIntValue());
                    }
                });
                foundSecond = true;
            } else if (possibleTypes.contains("applications.copying.pojo.C")) {
                fields.keySet().stream().forEach(f -> {
                    if (f.getName().equals("val")) {
                        assertEquals(10, ((IntConstant) fields.get(f)).getIntValue());
                    } else if (f.getName().equals("bval")) {
                        assertEquals(9, ((IntConstant) fields.get(f)).getIntValue());
                    } else if (f.getName().equals("cval")) {
                        assertEquals(7, ((IntConstant) fields.get(f)).getIntValue());
                    }
                });
                foundThird = true;
            } else {
                fail("Illegal value: " + a);
            }
        }
        assertTrue(foundFirst);
        assertTrue(foundSecond);
        assertTrue(foundThird);
        assertTrue(foundNull);

    }
}
