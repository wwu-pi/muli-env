package de.wwu.muli.toys;

import de.wwu.muggl.configuration.Defaults;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.env.nativeimpl.SolutionIterator;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class Taxicab {

    @SuppressWarnings("ConstantConditions")
    @Before
    public void checkSolvable() {
        // Z3 currently fails with "smt tactic failed to show goal to be sat/unsat (incomplete (theory arithmetic))"
        Assume.assumeFalse(Defaults.SOLVER_MANAGER.equals(Defaults.Z3_MANAGER));
    }

    @Test
    public final void test_hardy_ramanujan_number() throws InterruptedException, ClassFileException {
        boolean previousConfigLabelling = SolutionIterator.labelSolutions;
        SolutionIterator.labelSolutions = true;
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.toys.Taxicab");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        assertEquals(1, Arrays.stream(leaves).filter(x -> x instanceof Value).count());
        assertEquals("1729\n", TestablePrintStreamWrapper.outputStream().getBufferContents());

        // Reset.
        SolutionIterator.labelSolutions = previousConfigLabelling;
    }

    @Before
    public void clearStreamsBeforeTests() {
        TestablePrintStreamWrapper.outputStream().resetBuffer();
        TestablePrintStreamWrapper.errorStream().resetBuffer();
    }
}
