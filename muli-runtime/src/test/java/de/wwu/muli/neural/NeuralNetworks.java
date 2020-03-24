package de.wwu.muli.neural;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class NeuralNetworks {
    //@Test
    public final void /*test_*/nngenerator() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.neural.NNGenerator");
        assertEquals(1, foundTrees.length);
        System.out.println(foundTrees[0].toString());
    }
}
