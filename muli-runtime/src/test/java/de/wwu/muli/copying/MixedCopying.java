package de.wwu.muli.copying;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.Exception;
import de.wwu.muli.searchtree.ST;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class MixedCopying {
    @Test
    public void test_mixedLabeling0() throws InterruptedException, ClassFileException { /// TODO Also FreeArrays as input and output!
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.copying.ObjectInputs");
        assertEquals(1, foundTrees.length);
        Object[] leaves = LazyDFSIterator.stream(foundTrees[0]).toArray();
        Set<Object> exceptions = Arrays.stream(leaves).filter(x -> x instanceof Exception).collect(Collectors.toSet());

    }
}
