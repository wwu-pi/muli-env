package de.wwu.muli.muliST;

import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muli.env.TestableMuliRunner;
import org.junit.Test;

public class SimpleCoinApplications {

    @Test
    public final void test_FailCoin() throws InterruptedException, ClassFileException {
        TestableMuliRunner.runApplication("applications.muliST.FailCoin");
    }
}
