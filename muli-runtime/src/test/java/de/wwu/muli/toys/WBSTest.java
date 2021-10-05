package de.wwu.muli.toys;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

public class WBSTest {

    @Test
    public final void testWBS() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.toys.WBS");
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
    }

}
