package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseChain;
import de.wwu.muli.defuse.DefUseRegisters;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TestExecution {

    @Test
    public final void testDefUseIf() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.Test");
        Map<Object, Object> defUse = TestableMuliRunner.getCoverageMap();
        // number of invoked methods
        assertEquals(4, defUse.size());
        for(Object m: defUse.keySet()){
            DefUseMethod defuseMethod = (DefUseMethod) defUse.get(m);
            Method method = (Method) m;
            // Methods for the class invokation, no significant def use chains
            if(method.getFullName().contains("init")){
                assertTrue(defuseMethod.getDefUses().getChainSize() < 2);
            }
            // helper method increment
            if(method.getFullName().contains("increment")){
                // parsed variable definitions based on bytecode
                DefUseRegisters defs = defuseMethod.getDefs();
                assertEquals(2, defs.getRegisterSize());
                Integer[] defPcs = {-1,3};
                Integer [] defsPcsActual = defs.registers.keySet().toArray(new Integer[defs.getRegisterSize()]);
                assertArrayEquals(defPcs, defsPcsActual);
                assertEquals("\r\n   PC: -1; Visited: true; Links:0\r\n   PC: 3; Visited: true; Links:4",
                        defs.toString());
                // parsed variable usages based on bytecode
                DefUseRegisters uses = defuseMethod.getUses();
                assertEquals(2, uses.getRegisterSize());
                Integer[] usePcs = {0,4};
                Integer [] usePcsActual = uses.registers.keySet().toArray(new Integer[uses.getRegisterSize()]);
                assertArrayEquals(usePcs, usePcsActual);
                assertEquals("\r\n   PC: 0; Visited: true; Links:-1\r\n   PC: 4; Visited: true; Links:3",
                        uses.toString());
                // defUse chains which were visited
                DefUseChains chains = defuseMethod.getDefUses();
                assertEquals(2, chains.getChainSize());
                for(DefUseChain chain: chains.getDefUseChains()){
                    if(chain.getDef().getPc() == -1){
                        assertEquals(0, chain.getUse().getPc());
                    }
                    if(chain.getDef().getPc() == 3){
                        assertEquals(4, chain.getUse().getPc());
                    }
                }
            }
            // if method
            if(method.getFullName().contains("ifTest")){
                // parsed variable definitions based on bytecode
                DefUseRegisters defs = defuseMethod.getDefs();
                assertEquals(4, defs.getRegisterSize());
                Integer[] defPcs = {23,7,11,28};
                Integer [] defsPcsActual = defs.registers.keySet().toArray(new Integer[defs.getRegisterSize()]);
                assertArrayEquals(defPcs, defsPcsActual);
                assertEquals("\r\n   PC: 23; Visited: true; Links:29\r\n   PC: 7; Visited: true; Links:17"
                        + "\r\n   PC: 11; Visited: true; Links:12,19\r\n   PC: 28; Visited: true; Links:29",
                        defs.toString());
                // parsed variable usages based on bytecode
                DefUseRegisters uses = defuseMethod.getUses();
                assertEquals(4, uses.getRegisterSize());
                Integer[] usePcs = {17,19,12,29};
                Integer [] usePcsActual = uses.registers.keySet().toArray(new Integer[uses.getRegisterSize()]);
                assertArrayEquals(usePcs, usePcsActual);
                assertEquals("\r\n   PC: 17; Visited: true; Links:7\r\n   PC: 19; Visited: true; Links:11"
                                + "\r\n   PC: 12; Visited: true; Links:11\r\n   PC: 29; Visited: true; Links:23,28",
                        uses.toString());
                // defUse chains which were visited
                DefUseChains chains = defuseMethod.getDefUses();
                assertEquals(5, chains.getChainSize());
                for(DefUseChain chain: chains.getDefUseChains()){
                    if(chain.getDef().getPc() == 11){
                        try {
                            assertEquals(12, chain.getUse().getPc());
                        } catch(AssertionError a){
                            assertEquals(19, chain.getUse().getPc());
                        }
                    }
                    if(chain.getDef().getPc() == 23){
                        assertEquals(29, chain.getUse().getPc());
                    }
                    if(chain.getDef().getPc() == 28){
                        assertEquals(29, chain.getUse().getPc());
                    }
                    if(chain.getDef().getPc() == 7){
                        assertEquals(17, chain.getUse().getPc());
                    }
                }
            }
            System.out.println("Method: "+method.getFullName());
            System.out.println(defuseMethod.toString());
        }
    }

    @Test
    public final void testDefUseWhile() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.TestWhile");
        Map<Object, Object> defUse = TestableMuliRunner.getCoverageMap();
        // number of invoked methods
        assertEquals(1, defUse.size());
        for(Object m: defUse.keySet()){
            DefUseMethod defuseMethod = (DefUseMethod) defUse.get(m);
            Method method = (Method) m;
            // while method
            if(method.getFullName().contains("whileTest")){
                // parsed variable definitions based on bytecode
                DefUseRegisters defs = defuseMethod.getDefs();
                assertEquals(3, defs.getRegisterSize());
                Integer[] defPcs = {33,22,11};
                Integer [] defsPcsActual = defs.registers.keySet().toArray(new Integer[defs.getRegisterSize()]);
                assertArrayEquals(defPcs, defsPcsActual);
                assertEquals("\r\n   PC: 33; Visited: false; Links:12,23,37\r\n   PC: 22; Visited: true; Links:23,37"
                                + "\r\n   PC: 11; Visited: true; Links:12,37",
                        defs.toString());
                // parsed variable usages based on bytecode
                DefUseRegisters uses = defuseMethod.getUses();
                assertEquals(3, uses.getRegisterSize());
                Integer[] usePcs = {37,23,12};
                Integer [] usePcsActual = uses.registers.keySet().toArray(new Integer[uses.getRegisterSize()]);
                assertArrayEquals(usePcs, usePcsActual);
                assertEquals("\r\n   PC: 37; Visited: true; Links:11,22,33\r\n   PC: 23; Visited: true; Links:22,33"
                                + "\r\n   PC: 12; Visited: true; Links:11,33",
                        uses.toString());
                // defUse chains which were visited
                DefUseChains chains = defuseMethod.getDefUses();
                assertEquals(3, chains.getChainSize());
                for(DefUseChain chain: chains.getDefUseChains()){
                    if(chain.getDef().getPc() == 22){
                        try {
                            assertEquals(23, chain.getUse().getPc());
                        } catch(AssertionError a){
                            assertEquals(37, chain.getUse().getPc());
                        }
                    }
                    if(chain.getDef().getPc() == 11){
                        assertEquals(12, chain.getUse().getPc());
                    }
                }
            }
            System.out.println("Method: "+method.getFullName());
            System.out.println(defuseMethod.toString());
        }
    }
}
