package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

public class SatPrimes01Test {

    @Test
    public final void testSatPrimes01() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.SatPrimes01");
        //Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        //System.out.println("Done");
    }
}
