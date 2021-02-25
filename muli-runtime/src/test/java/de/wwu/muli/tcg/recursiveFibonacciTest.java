package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muli.defuse.DefUseChain;
import de.wwu.muli.defuse.DefUseChains;
import de.wwu.muli.defuse.DefUseMethod;
import de.wwu.muli.defuse.DefUseRegisters;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.solution.Solution;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class recursiveFibonacciTest {

    @Test
    public final void testVariables() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.Main");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
    }
}
