package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

public class TspTest {

    @Test
    public final void testVariables() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.MainTSP");
        //Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
    }
}