package de.wwu.muli.tcg;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.ArrayList;

public class BellmanFordTest {

    @Test // Not working
    public final void testVariables() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.MainBellman");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        ArrayList<Object> solutions = new ArrayList<Object>();

    }
}
