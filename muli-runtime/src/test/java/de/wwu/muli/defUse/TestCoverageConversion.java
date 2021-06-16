package de.wwu.muli.defUse;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.testsetreducer.SimpleForwardsTestSetReducer;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.BitSet;

public class TestCoverageConversion {

    @Test
    public void testCoverageConversion(){
        String method1 = "method1";
        int[] m1c1 = {1,6,29,111};
        String method2 = "method2";
        int[] m2c1 = {3, 29, 111};
        int[] m1c2 = {1, 6,29,111, 200};
        int[] m2c2 = {1, 29, 100};
        String method3 = "method3";
        int[] m3c1 = {1};
        Map<String, Object> coverage1 = new HashMap();
        coverage1.put(method1, m1c1);
        coverage1.put(method2, m2c1);
        TestCase tc1 = new TestCase<>(null, null,"defuse.testCoverageConversion", null, coverage1);
        Map<String, Object> coverage2 = new HashMap();
        coverage2.put(method1, m1c2);
        coverage2.put(method2, m2c2);
        coverage2.put(method3, m3c1);
        TestCase tc2 = new TestCase<>(null, null,"defuse.testCoverageConversion", null, coverage2);
        Set<TestCase<?>> testCases = new HashSet<>();
        testCases.add(tc1);
        testCases.add(tc2);
        SimpleForwardsTestSetReducer reducer = new SimpleForwardsTestSetReducer();
        Map<String, Integer> lengthMap = reducer.getLengthMap(testCases);
        int output1 = lengthMap.get("method1");
        assertEquals(200, output1);
        int output2 = lengthMap.get("method2");
        assertEquals(111, output2);
        int output3 = lengthMap.get("method3");
        assertEquals(1, output3);

        BitSet cover = tc1.getCover(lengthMap);
        int[] l = {1,6,29,111,203,229,311};
        BitSet expected = new BitSet();
        for(int i=0; i<l.length; i++){
            expected.set(l[i]);
        }
        assertEquals(expected, cover);

        BitSet cover2 = tc2.getCover(lengthMap);
        int[] l2 = {1,6,29,111,200,201,229,300,312};
        BitSet expected2 = new BitSet();
        for(int i=0; i<l2.length; i++){
            expected2.set(l2[i]);
        }
        assertEquals(expected2, cover2);

    }
}
