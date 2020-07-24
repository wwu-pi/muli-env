package de.wwu.muli.lists;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.execution.nativeWrapping.TestablePrintStreamWrapper;
import de.wwu.muli.env.LazyDFSIterator;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.searchtree.Value;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TruckOrderAssignments {
    @Test
    public final void test_executeAplication() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.lists.Application");
        assertEquals(1, foundTrees.length);

    }

}
