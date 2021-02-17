package de.wwu.muli.tcg;

import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import de.wwu.muli.searchtree.ST;
import de.wwu.muli.vm.LogicVirtualMachine;
import org.junit.Test;

import java.util.Arrays;

public class TestTCGBooleanCounter {

    @Test
    public void testTcgBooleanCounter() throws InterruptedException, ClassFileException {
        ST[] foundTrees = TestableMuliRunner.runApplication("applications.tcg.TCGBooleanCounter");
        System.out.println(Arrays.toString(foundTrees));
        System.out.println(LogicVirtualMachine.frameAndInstruction);
        System.out.println("=======================================================");
        System.out.println(LogicVirtualMachine.operandStack);
        System.out.println("=======================================================");
        System.out.println(LogicVirtualMachine.vmFrameStack);
    }
}
