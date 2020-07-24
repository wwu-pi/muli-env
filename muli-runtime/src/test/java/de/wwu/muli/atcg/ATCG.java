package de.wwu.muli.atcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ATCG {
    @Test
    public final void test_runWithoutSideEffects() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.atcg.ATCG");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());

    }
    @Test
    public final void test_runWithSideEffects() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.atcg.ATCGWithSideeffects");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
    }

}
