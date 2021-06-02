package de.wwu.muli.defUse;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.defuse.DefUseChoice;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseChain;
import de.wwu.muli.defuse.DefUseRegisters;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class TestExecution {

    @Test
    public final void testDefUseIf() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.defUse.Test");
        ArrayList defUse = TestableMuliRunner.getCoverageMap();
        // number of invoked methods
        assertEquals(3, defUse.size());
        Map<Object, Object> map1 = (Map<Object, Object>) defUse.get(0);
        assertEquals(2, map1.size());
        assertTrue(map1.containsKey("<init>"));
        DefUseChains defuse = ((DefUseChoice) map1.get("<init>")).getDefUse();
        assertEquals(1, defuse.getChainSize());
        assertTrue(defuse.containsChain(-1,0));
        assertTrue(map1.containsKey("ifTest"));
        DefUseChains defuse2 = ((DefUseChoice) map1.get("ifTest")).getDefUse();
        assertEquals(2, defuse2.getChainSize());
        assertTrue(defuse2.containsChain(-1,8));
        assertTrue(defuse2.containsChain(24,25));

        Map<Object, Object> map2 = (Map<Object, Object>) defUse.get(1);
        assertEquals(3, map2.size());
        assertTrue(map2.containsKey("<init>"));
        DefUseChains defuse3 = ((DefUseChoice) map2.get("<init>")).getDefUse();
        assertEquals(1, defuse3.getChainSize());
        assertTrue(defuse3.containsChain(-1,0));
        assertTrue(map2.containsKey("ifTest"));
        DefUseChains defuse4 = ((DefUseChoice) map2.get("ifTest")).getDefUse();
        assertEquals(4, defuse4.getChainSize());
        assertTrue(defuse4.containsChain(-1,8));
        assertTrue(defuse4.containsChain(7,13));
        assertTrue(defuse4.containsChain(-1,15));
        assertTrue(defuse4.containsChain(19,25));
        assertTrue(map2.containsKey("increment"));
        DefUseChains defuse5 = ((DefUseChoice) map2.get("increment")).getDefUse();
        assertEquals(2, defuse5.getChainSize());
        assertTrue(defuse5.containsChain(-1,0));
        assertTrue(defuse5.containsChain(-1,11));

        Map<Object, Object> map3 = (Map<Object, Object>) defUse.get(2);
        assertEquals(3, map3.size());
        assertTrue(map3.containsKey("<init>"));
        DefUseChains defuse6 = ((DefUseChoice) map3.get("<init>")).getDefUse();
        assertEquals(1, defuse6.getChainSize());
        assertTrue(defuse6.containsChain(-1,0));
        assertTrue(map3.containsKey("ifTest"));
        DefUseChains defuse7 = ((DefUseChoice) map3.get("ifTest")).getDefUse();
        assertEquals(4, defuse7.getChainSize());
        assertTrue(defuse7.containsChain(-1,8));
        assertTrue(defuse7.containsChain(7,13));
        assertTrue(defuse7.containsChain(-1,15));
        assertTrue(defuse7.containsChain(19,25));
        assertTrue(map3.containsKey("increment"));
        DefUseChains defuse8 = ((DefUseChoice) map3.get("increment")).getDefUse();
        assertEquals(3, defuse8.getChainSize());
        assertTrue(defuse8.containsChain(-1,0));
        assertTrue(defuse8.containsChain(-1,5));
        assertTrue(defuse8.containsChain(8,9));
    }

    @Test
    public final void testDefUseWhile() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.defUse.TestWhile");
        ArrayList defUse = TestableMuliRunner.getCoverageMap();
        // number of invoked methods
        assertEquals(3, defUse.size());
        Map<Object, Object> map1 = (Map<Object, Object>) defUse.get(0);
        assertEquals(1, map1.size());
        for(Object m: map1.keySet()){
            String method = (String) m;
            assertEquals("whileTest", m);
            DefUseChains defuse = ((DefUseChoice) map1.get(m)).getDefUse();
            assertEquals(2, defuse.getChainSize());
            assertTrue(defuse.containsChain(-1,0));
            assertTrue(defuse.containsChain(-1,25));
        }
        Map<Object, Object> map2 = (Map<Object, Object>) defUse.get(1);
        assertEquals(1, map2.size());
        for(Object m: map2.keySet()){
            String method = (String) m;
            assertEquals("whileTest", m);
            DefUseChains defuse = ((DefUseChoice) map2.get(m)).getDefUse();
            assertEquals(4, defuse.getChainSize());
            assertTrue(defuse.containsChain(-1,0));
            assertTrue(defuse.containsChain(10,11));
            assertTrue(defuse.containsChain(10,25));
            assertTrue(defuse.containsChain(10,0));
        }
        Map<Object, Object> map3 = (Map<Object, Object>) defUse.get(2);
        assertEquals(1, map3.size());
        for(Object m: map3.keySet()){
            String method = (String) m;
            assertEquals("whileTest", m);
            DefUseChains defuse = ((DefUseChoice) map3.get(m)).getDefUse();
            assertEquals(4, defuse.getChainSize());
            assertTrue(defuse.containsChain(-1,0));
            assertTrue(defuse.containsChain(10,11));
            assertTrue(defuse.containsChain(21,25));
            assertTrue(defuse.containsChain(21,0));
        }
    }

    @Test @Ignore
    public final void testVariables() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.TestVariables");
    }
}
