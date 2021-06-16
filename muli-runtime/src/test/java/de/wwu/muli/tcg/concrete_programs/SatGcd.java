package de.wwu.muli.tcg.concrete_programs;

/*
 * Origin of the benchmark:
 *     license: MIT (see /java/jayhorn-recursive/LICENSE)
 *     repo: https://github.com/jayhorn/cav_experiments.git
 *     branch: master
 *     root directory: benchmarks/recursive
 * The benchmark was taken from the repo: 24 January 2018
 */

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/* 2021-06-14 Taken and adjusted from https://github.com/sosy-lab/sv-benchmarks/blob/master/java/jayhorn-recursive/SatGcd/Main.java
    : HW */
/* 2021-06-14 Renamed from Main to SatGcd : HW */
public class SatGcd {

    // Compute the greatest common denominator using Euclid's algorithm
    static int gcd(int y1, int y2) {
        if (y1 <= 0 || y2 <= 0) {
            return 0;
        }
        if (y1 == y2) {
            return y1;
        }
        if (y1 > y2) {
            return gcd(y1 - y2, y2);
        }
        return gcd(y1, y2 - y1);
    }

    @Test
    public void test_gcd_0() {
        Integer integer0 = 1;
        Integer integer1 = 30;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_1() {
        Integer integer0 = 1;
        Integer integer1 = 29;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_2() {
        Integer integer0 = 1;
        Integer integer1 = 28;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_3() {
        Integer integer0 = 1;
        Integer integer1 = 27;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_4() {
        Integer integer0 = 1;
        Integer integer1 = 26;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_5() {
        Integer integer0 = 1;
        Integer integer1 = 25;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_6() {
        Integer integer0 = 1;
        Integer integer1 = 24;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_7() {
        Integer integer0 = 1;
        Integer integer1 = 23;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_8() {
        Integer integer0 = 1;
        Integer integer1 = 22;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_9() {
        Integer integer0 = 1;
        Integer integer1 = 21;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_10() {
        Integer integer0 = 1;
        Integer integer1 = 20;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_11() {
        Integer integer0 = 1;
        Integer integer1 = 19;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_12() {
        Integer integer0 = 1;
        Integer integer1 = 18;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_13() {
        Integer integer0 = 1;
        Integer integer1 = 17;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_14() {
        Integer integer0 = 1;
        Integer integer1 = 16;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_15() {
        Integer integer0 = 2;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_16() {
        Integer integer0 = 1;
        Integer integer1 = 15;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_17() {
        Integer integer0 = 2;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_18() {
        Integer integer0 = 1;
        Integer integer1 = 14;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_19() {
        Integer integer0 = 2;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_20() {
        Integer integer0 = 1;
        Integer integer1 = 13;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_21() {
        Integer integer0 = 2;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_22() {
        Integer integer0 = 1;
        Integer integer1 = 12;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_23() {
        Integer integer0 = 2;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_24() {
        Integer integer0 = 1;
        Integer integer1 = 11;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_25() {
        Integer integer0 = 3;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_26() {
        Integer integer0 = 3;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_27() {
        Integer integer0 = 2;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_28() {
        Integer integer0 = 2;
        Integer integer1 = 20;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_29() {
        Integer integer0 = 3;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_30() {
        Integer integer0 = 3;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_31() {
        Integer integer0 = 2;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_32() {
        Integer integer0 = 1;
        Integer integer1 = 9;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_33() {
        Integer integer0 = 3;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_34() {
        Integer integer0 = 4;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_35() {
        Integer integer0 = 3;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_36() {
        Integer integer0 = 4;
        Integer integer1 = 30;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_37() {
        Integer integer0 = 1;
        Integer integer1 = 8;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_38() {
        Integer integer0 = 4;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_39() {
        Integer integer0 = 3;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_40() {
        Integer integer0 = 4;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_41() {
        Integer integer0 = 3;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_42() {
        Integer integer0 = 4;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_43() {
        Integer integer0 = 2;
        Integer integer1 = 14;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_44() {
        Integer integer0 = 5;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_45() {
        Integer integer0 = 4;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_46() {
        Integer integer0 = 5;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_47() {
        Integer integer0 = 3;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_48() {
        Integer integer0 = 5;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_49() {
        Integer integer0 = 5;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_50() {
        Integer integer0 = 4;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_51() {
        Integer integer0 = 3;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_52() {
        Integer integer0 = 4;
        Integer integer1 = 22;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_53() {
        Integer integer0 = 1;
        Integer integer1 = 6;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_54() {
        Integer integer0 = 6;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_55() {
        Integer integer0 = 5;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_56() {
        Integer integer0 = 4;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_57() {
        Integer integer0 = 5;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_58() {
        Integer integer0 = 3;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_59() {
        Integer integer0 = 5;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_60() {
        Integer integer0 = 7;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_61() {
        Integer integer0 = 7;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_62() {
        Integer integer0 = 6;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_63() {
        Integer integer0 = 5;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_64() {
        Integer integer0 = 4;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_65() {
        Integer integer0 = 6;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_66() {
        Integer integer0 = 4;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_67() {
        Integer integer0 = 1;
        Integer integer1 = 5;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_68() {
        Integer integer0 = 7;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_69() {
        Integer integer0 = 6;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_70() {
        Integer integer0 = 5;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_71() {
        Integer integer0 = 7;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_72() {
        Integer integer0 = 8;
        Integer integer1 = 30;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_73() {
        Integer integer0 = 8;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_74() {
        Integer integer0 = 7;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_75() {
        Integer integer0 = 5;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_76() {
        Integer integer0 = 3;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_77() {
        Integer integer0 = 7;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_78() {
        Integer integer0 = 8;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_79() {
        Integer integer0 = 5;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_80() {
        Integer integer0 = 7;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_81() {
        Integer integer0 = 9;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_82() {
        Integer integer0 = 9;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_83() {
        Integer integer0 = 8;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_84() {
        Integer integer0 = 7;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_85() {
        Integer integer0 = 6;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_86() {
        Integer integer0 = 5;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_87() {
        Integer integer0 = 8;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_88() {
        Integer integer0 = 6;
        Integer integer1 = 20;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_89() {
        Integer integer0 = 2;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_90() {
        Integer integer0 = 1;
        Integer integer1 = 4;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_91() {
        Integer integer0 = 10;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_92() {
        Integer integer0 = 9;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_93() {
        Integer integer0 = 8;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_94() {
        Integer integer0 = 7;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_95() {
        Integer integer0 = 6;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_96() {
        Integer integer0 = 9;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_97() {
        Integer integer0 = 5;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_98() {
        Integer integer0 = 11;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_99() {
        Integer integer0 = 10;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_100() {
        Integer integer0 = 7;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_101() {
        Integer integer0 = 4;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_102() {
        Integer integer0 = 11;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_103() {
        Integer integer0 = 8;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_104() {
        Integer integer0 = 11;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_105() {
        Integer integer0 = 9;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_106() {
        Integer integer0 = 7;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_107() {
        Integer integer0 = 5;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_108() {
        Integer integer0 = 6;
        Integer integer1 = 16;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_109() {
        Integer integer0 = 11;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_110() {
        Integer integer0 = 9;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_111() {
        Integer integer0 = 12;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_112() {
        Integer integer0 = 7;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_113() {
        Integer integer0 = 11;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_114() {
        Integer integer0 = 8;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_115() {
        Integer integer0 = 5;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_116() {
        Integer integer0 = 13;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_117() {
        Integer integer0 = 10;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_118() {
        Integer integer0 = 11;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_119() {
        Integer integer0 = 7;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_120() {
        Integer integer0 = 13;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_121() {
        Integer integer0 = 9;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_122() {
        Integer integer0 = 11;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_123() {
        Integer integer0 = 13;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_124() {
        Integer integer0 = 14;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_125() {
        Integer integer0 = 13;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_126() {
        Integer integer0 = 12;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_127() {
        Integer integer0 = 11;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_128() {
        Integer integer0 = 10;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_129() {
        Integer integer0 = 9;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_130() {
        Integer integer0 = 8;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_131() {
        Integer integer0 = 14;
        Integer integer1 = 30;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_132() {
        Integer integer0 = 6;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_133() {
        Integer integer0 = 5;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_134() {
        Integer integer0 = 8;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_135() {
        Integer integer0 = 6;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_136() {
        Integer integer0 = 4;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_137() {
        Integer integer0 = 1;
        Integer integer1 = 3;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_138() {
        Integer integer0 = 15;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_139() {
        Integer integer0 = 14;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_140() {
        Integer integer0 = 13;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_141() {
        Integer integer0 = 12;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_142() {
        Integer integer0 = 11;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_143() {
        Integer integer0 = 10;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_144() {
        Integer integer0 = 9;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_145() {
        Integer integer0 = 15;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_146() {
        Integer integer0 = 16;
        Integer integer1 = 30;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_147() {
        Integer integer0 = 13;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_148() {
        Integer integer0 = 14;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_149() {
        Integer integer0 = 16;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_150() {
        Integer integer0 = 11;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_151() {
        Integer integer0 = 12;
        Integer integer1 = 22;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_152() {
        Integer integer0 = 14;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_153() {
        Integer integer0 = 17;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_154() {
        Integer integer0 = 13;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_155() {
        Integer integer0 = 9;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_156() {
        Integer integer0 = 5;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_157() {
        Integer integer0 = 15;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_158() {
        Integer integer0 = 11;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_159() {
        Integer integer0 = 17;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_160() {
        Integer integer0 = 16;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_161() {
        Integer integer0 = 13;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_162() {
        Integer integer0 = 10;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_163() {
        Integer integer0 = 14;
        Integer integer1 = 24;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_164() {
        Integer integer0 = 4;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_165() {
        Integer integer0 = 17;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_166() {
        Integer integer0 = 14;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_167() {
        Integer integer0 = 11;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_168() {
        Integer integer0 = 18;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_169() {
        Integer integer0 = 13;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_170() {
        Integer integer0 = 16;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_171() {
        Integer integer0 = 17;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_172() {
        Integer integer0 = 19;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_173() {
        Integer integer0 = 12;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_174() {
        Integer integer0 = 16;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_175() {
        Integer integer0 = 19;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_176() {
        Integer integer0 = 17;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_177() {
        Integer integer0 = 15;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_178() {
        Integer integer0 = 13;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_179() {
        Integer integer0 = 11;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_180() {
        Integer integer0 = 9;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_181() {
        Integer integer0 = 7;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_182() {
        Integer integer0 = 5;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_183() {
        Integer integer0 = 6;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_184() {
        Integer integer0 = 19;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_185() {
        Integer integer0 = 17;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_186() {
        Integer integer0 = 15;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_187() {
        Integer integer0 = 13;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_188() {
        Integer integer0 = 20;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_189() {
        Integer integer0 = 11;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_190() {
        Integer integer0 = 16;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_191() {
        Integer integer0 = 9;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_192() {
        Integer integer0 = 19;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_193() {
        Integer integer0 = 17;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_194() {
        Integer integer0 = 12;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_195() {
        Integer integer0 = 14;
        Integer integer1 = 20;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_196() {
        Integer integer0 = 18;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_197() {
        Integer integer0 = 21;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_198() {
        Integer integer0 = 13;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_199() {
        Integer integer0 = 19;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_200() {
        Integer integer0 = 20;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_201() {
        Integer integer0 = 17;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_202() {
        Integer integer0 = 14;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_203() {
        Integer integer0 = 11;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_204() {
        Integer integer0 = 8;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_205() {
        Integer integer0 = 5;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_206() {
        Integer integer0 = 22;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_207() {
        Integer integer0 = 19;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_208() {
        Integer integer0 = 16;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_209() {
        Integer integer0 = 23;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_210() {
        Integer integer0 = 13;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_211() {
        Integer integer0 = 17;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_212() {
        Integer integer0 = 10;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_213() {
        Integer integer0 = 18;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_214() {
        Integer integer0 = 23;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_215() {
        Integer integer0 = 19;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_216() {
        Integer integer0 = 15;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_217() {
        Integer integer0 = 22;
        Integer integer1 = 28;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_218() {
        Integer integer0 = 14;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_219() {
        Integer integer0 = 21;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_220() {
        Integer integer0 = 17;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_221() {
        Integer integer0 = 22;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_222() {
        Integer integer0 = 13;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_223() {
        Integer integer0 = 23;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_224() {
        Integer integer0 = 24;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_225() {
        Integer integer0 = 19;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_226() {
        Integer integer0 = 14;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_227() {
        Integer integer0 = 9;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_228() {
        Integer integer0 = 21;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_229() {
        Integer integer0 = 16;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_230() {
        Integer integer0 = 23;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_231() {
        Integer integer0 = 17;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_232() {
        Integer integer0 = 22;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_233() {
        Integer integer0 = 25;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_234() {
        Integer integer0 = 19;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_235() {
        Integer integer0 = 20;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_236() {
        Integer integer0 = 26;
        Integer integer1 = 30;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_237() {
        Integer integer0 = 22;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_238() {
        Integer integer0 = 23;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_239() {
        Integer integer0 = 15;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_240() {
        Integer integer0 = 25;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_241() {
        Integer integer0 = 26;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_242() {
        Integer integer0 = 17;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_243() {
        Integer integer0 = 19;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_244() {
        Integer integer0 = 21;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_245() {
        Integer integer0 = 23;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_246() {
        Integer integer0 = 25;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_247() {
        Integer integer0 = 27;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_248() {
        Integer integer0 = 29;
        Integer integer1 = 30;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_249() {
        Integer integer0 = 28;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_250() {
        Integer integer0 = 27;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_251() {
        Integer integer0 = 26;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_252() {
        Integer integer0 = 25;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_253() {
        Integer integer0 = 24;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_254() {
        Integer integer0 = 23;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_255() {
        Integer integer0 = 22;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_256() {
        Integer integer0 = 21;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_257() {
        Integer integer0 = 20;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_258() {
        Integer integer0 = 19;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_259() {
        Integer integer0 = 18;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_260() {
        Integer integer0 = 17;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_261() {
        Integer integer0 = 16;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_262() {
        Integer integer0 = 15;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_263() {
        Integer integer0 = 14;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_264() {
        Integer integer0 = 13;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_265() {
        Integer integer0 = 12;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_266() {
        Integer integer0 = 22;
        Integer integer1 = 24;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_267() {
        Integer integer0 = 20;
        Integer integer1 = 22;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_268() {
        Integer integer0 = 9;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_269() {
        Integer integer0 = 16;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_270() {
        Integer integer0 = 7;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_271() {
        Integer integer0 = 12;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_272() {
        Integer integer0 = 10;
        Integer integer1 = 12;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_273() {
        Integer integer0 = 4;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_274() {
        Integer integer0 = 3;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_275() {
        Integer integer0 = 4;
        Integer integer1 = 6;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_276() {
        Integer integer0 = 1;
        Integer integer1 = 2;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_277() {
        Integer integer0 = 30;
        Integer integer1 = 29;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_278() {
        Integer integer0 = 29;
        Integer integer1 = 28;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_279() {
        Integer integer0 = 28;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_280() {
        Integer integer0 = 27;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_281() {
        Integer integer0 = 26;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_282() {
        Integer integer0 = 25;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_283() {
        Integer integer0 = 24;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_284() {
        Integer integer0 = 23;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_285() {
        Integer integer0 = 22;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_286() {
        Integer integer0 = 21;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_287() {
        Integer integer0 = 20;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_288() {
        Integer integer0 = 19;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_289() {
        Integer integer0 = 18;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_290() {
        Integer integer0 = 17;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_291() {
        Integer integer0 = 16;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_292() {
        Integer integer0 = 29;
        Integer integer1 = 27;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_293() {
        Integer integer0 = 30;
        Integer integer1 = 28;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_294() {
        Integer integer0 = 27;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_295() {
        Integer integer0 = 28;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_296() {
        Integer integer0 = 25;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_297() {
        Integer integer0 = 26;
        Integer integer1 = 24;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_298() {
        Integer integer0 = 23;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_299() {
        Integer integer0 = 12;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_300() {
        Integer integer0 = 21;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_301() {
        Integer integer0 = 11;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_302() {
        Integer integer0 = 29;
        Integer integer1 = 26;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_303() {
        Integer integer0 = 28;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_304() {
        Integer integer0 = 19;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_305() {
        Integer integer0 = 10;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_306() {
        Integer integer0 = 26;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_307() {
        Integer integer0 = 25;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_308() {
        Integer integer0 = 17;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_309() {
        Integer integer0 = 9;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_310() {
        Integer integer0 = 23;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_311() {
        Integer integer0 = 29;
        Integer integer1 = 25;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_312() {
        Integer integer0 = 22;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_313() {
        Integer integer0 = 30;
        Integer integer1 = 26;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_314() {
        Integer integer0 = 16;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_315() {
        Integer integer0 = 27;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_316() {
        Integer integer0 = 20;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_317() {
        Integer integer0 = 25;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_318() {
        Integer integer0 = 19;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_319() {
        Integer integer0 = 26;
        Integer integer1 = 22;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_320() {
        Integer integer0 = 14;
        Integer integer1 = 12;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_321() {
        Integer integer0 = 29;
        Integer integer1 = 24;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_322() {
        Integer integer0 = 23;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_323() {
        Integer integer0 = 28;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_324() {
        Integer integer0 = 17;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_325() {
        Integer integer0 = 27;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_326() {
        Integer integer0 = 26;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_327() {
        Integer integer0 = 21;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_328() {
        Integer integer0 = 16;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_329() {
        Integer integer0 = 22;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_330() {
        Integer integer0 = 6;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_331() {
        Integer integer0 = 29;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_332() {
        Integer integer0 = 24;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_333() {
        Integer integer0 = 19;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_334() {
        Integer integer0 = 23;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_335() {
        Integer integer0 = 14;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_336() {
        Integer integer0 = 22;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_337() {
        Integer integer0 = 30;
        Integer integer1 = 23;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_338() {
        Integer integer0 = 29;
        Integer integer1 = 22;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_339() {
        Integer integer0 = 25;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_340() {
        Integer integer0 = 21;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_341() {
        Integer integer0 = 17;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_342() {
        Integer integer0 = 26;
        Integer integer1 = 20;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_343() {
        Integer integer0 = 18;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_344() {
        Integer integer0 = 5;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_345() {
        Integer integer0 = 27;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_346() {
        Integer integer0 = 23;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_347() {
        Integer integer0 = 19;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_348() {
        Integer integer0 = 26;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_349() {
        Integer integer0 = 30;
        Integer integer1 = 22;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_350() {
        Integer integer0 = 29;
        Integer integer1 = 21;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_351() {
        Integer integer0 = 25;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_352() {
        Integer integer0 = 18;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_353() {
        Integer integer0 = 22;
        Integer integer1 = 16;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_354() {
        Integer integer0 = 24;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_355() {
        Integer integer0 = 27;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_356() {
        Integer integer0 = 17;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_357() {
        Integer integer0 = 23;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_358() {
        Integer integer0 = 29;
        Integer integer1 = 20;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_359() {
        Integer integer0 = 28;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_360() {
        Integer integer0 = 25;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_361() {
        Integer integer0 = 22;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_362() {
        Integer integer0 = 19;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_363() {
        Integer integer0 = 16;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_364() {
        Integer integer0 = 13;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_365() {
        Integer integer0 = 20;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_366() {
        Integer integer0 = 14;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_367() {
        Integer integer0 = 8;
        Integer integer1 = 6;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_368() {
        Integer integer0 = 29;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_369() {
        Integer integer0 = 26;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_370() {
        Integer integer0 = 23;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_371() {
        Integer integer0 = 20;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_372() {
        Integer integer0 = 17;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_373() {
        Integer integer0 = 25;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_374() {
        Integer integer0 = 28;
        Integer integer1 = 18;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_375() {
        Integer integer0 = 30;
        Integer integer1 = 19;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_376() {
        Integer integer0 = 27;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_377() {
        Integer integer0 = 19;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_378() {
        Integer integer0 = 22;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_379() {
        Integer integer0 = 29;
        Integer integer1 = 18;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_380() {
        Integer integer0 = 21;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_381() {
        Integer integer0 = 28;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_382() {
        Integer integer0 = 23;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_383() {
        Integer integer0 = 18;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_384() {
        Integer integer0 = 13;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_385() {
        Integer integer0 = 16;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_386() {
        Integer integer0 = 27;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_387() {
        Integer integer0 = 22;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_388() {
        Integer integer0 = 29;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_389() {
        Integer integer0 = 17;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_390() {
        Integer integer0 = 26;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_391() {
        Integer integer0 = 19;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_392() {
        Integer integer0 = 24;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_393() {
        Integer integer0 = 30;
        Integer integer1 = 17;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_394() {
        Integer integer0 = 23;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_395() {
        Integer integer0 = 25;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_396() {
        Integer integer0 = 16;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_397() {
        Integer integer0 = 29;
        Integer integer1 = 16;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_398() {
        Integer integer0 = 20;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_399() {
        Integer integer0 = 24;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_400() {
        Integer integer0 = 28;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_401() {
        Integer integer0 = 29;
        Integer integer1 = 15;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_402() {
        Integer integer0 = 27;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_403() {
        Integer integer0 = 25;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_404() {
        Integer integer0 = 23;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_405() {
        Integer integer0 = 21;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_406() {
        Integer integer0 = 19;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_407() {
        Integer integer0 = 17;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_408() {
        Integer integer0 = 30;
        Integer integer1 = 16;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_409() {
        Integer integer0 = 26;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_410() {
        Integer integer0 = 11;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_411() {
        Integer integer0 = 18;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_412() {
        Integer integer0 = 7;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_413() {
        Integer integer0 = 5;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_414() {
        Integer integer0 = 3;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_415() {
        Integer integer0 = 29;
        Integer integer1 = 14;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_416() {
        Integer integer0 = 27;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_417() {
        Integer integer0 = 25;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_418() {
        Integer integer0 = 23;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_419() {
        Integer integer0 = 21;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_420() {
        Integer integer0 = 19;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_421() {
        Integer integer0 = 17;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_422() {
        Integer integer0 = 28;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_423() {
        Integer integer0 = 30;
        Integer integer1 = 14;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_424() {
        Integer integer0 = 24;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_425() {
        Integer integer0 = 13;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_426() {
        Integer integer0 = 29;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_427() {
        Integer integer0 = 20;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_428() {
        Integer integer0 = 22;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_429() {
        Integer integer0 = 25;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_430() {
        Integer integer0 = 30;
        Integer integer1 = 13;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_431() {
        Integer integer0 = 23;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_432() {
        Integer integer0 = 16;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_433() {
        Integer integer0 = 9;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_434() {
        Integer integer0 = 26;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_435() {
        Integer integer0 = 19;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_436() {
        Integer integer0 = 29;
        Integer integer1 = 12;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_437() {
        Integer integer0 = 27;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_438() {
        Integer integer0 = 22;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_439() {
        Integer integer0 = 17;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_440() {
        Integer integer0 = 12;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_441() {
        Integer integer0 = 14;
        Integer integer1 = 6;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_442() {
        Integer integer0 = 28;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_443() {
        Integer integer0 = 23;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_444() {
        Integer integer0 = 18;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_445() {
        Integer integer0 = 29;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_446() {
        Integer integer0 = 21;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_447() {
        Integer integer0 = 13;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_448() {
        Integer integer0 = 27;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_449() {
        Integer integer0 = 30;
        Integer integer1 = 11;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_450() {
        Integer integer0 = 19;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_451() {
        Integer integer0 = 25;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_452() {
        Integer integer0 = 29;
        Integer integer1 = 10;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_453() {
        Integer integer0 = 26;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_454() {
        Integer integer0 = 23;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_455() {
        Integer integer0 = 20;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_456() {
        Integer integer0 = 17;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_457() {
        Integer integer0 = 28;
        Integer integer1 = 10;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_458() {
        Integer integer0 = 11;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_459() {
        Integer integer0 = 8;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_460() {
        Integer integer0 = 5;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_461() {
        Integer integer0 = 28;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_462() {
        Integer integer0 = 25;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_463() {
        Integer integer0 = 22;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_464() {
        Integer integer0 = 19;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_465() {
        Integer integer0 = 29;
        Integer integer1 = 9;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_466() {
        Integer integer0 = 16;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_467() {
        Integer integer0 = 23;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_468() {
        Integer integer0 = 13;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_469() {
        Integer integer0 = 27;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_470() {
        Integer integer0 = 24;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_471() {
        Integer integer0 = 17;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_472() {
        Integer integer0 = 20;
        Integer integer1 = 6;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_473() {
        Integer integer0 = 25;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_474() {
        Integer integer0 = 29;
        Integer integer1 = 8;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_475() {
        Integer integer0 = 18;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_476() {
        Integer integer0 = 26;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_477() {
        Integer integer0 = 27;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_478() {
        Integer integer0 = 23;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_479() {
        Integer integer0 = 19;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_480() {
        Integer integer0 = 30;
        Integer integer1 = 8;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_481() {
        Integer integer0 = 11;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_482() {
        Integer integer0 = 14;
        Integer integer1 = 4;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_483() {
        Integer integer0 = 29;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_484() {
        Integer integer0 = 25;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_485() {
        Integer integer0 = 21;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_486() {
        Integer integer0 = 30;
        Integer integer1 = 7;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_487() {
        Integer integer0 = 17;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_488() {
        Integer integer0 = 22;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_489() {
        Integer integer0 = 13;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_490() {
        Integer integer0 = 23;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_491() {
        Integer integer0 = 29;
        Integer integer1 = 6;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_492() {
        Integer integer0 = 24;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_493() {
        Integer integer0 = 19;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_494() {
        Integer integer0 = 14;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_495() {
        Integer integer0 = 9;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_496() {
        Integer integer0 = 26;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_497() {
        Integer integer0 = 21;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_498() {
        Integer integer0 = 27;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_499() {
        Integer integer0 = 16;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_500() {
        Integer integer0 = 28;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_501() {
        Integer integer0 = 29;
        Integer integer1 = 5;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_502() {
        Integer integer0 = 23;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_503() {
        Integer integer0 = 17;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_504() {
        Integer integer0 = 11;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_505() {
        Integer integer0 = 25;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_506() {
        Integer integer0 = 19;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_507() {
        Integer integer0 = 27;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_508() {
        Integer integer0 = 20;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_509() {
        Integer integer0 = 26;
        Integer integer1 = 4;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_510() {
        Integer integer0 = 29;
        Integer integer1 = 4;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_511() {
        Integer integer0 = 22;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_512() {
        Integer integer0 = 23;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_513() {
        Integer integer0 = 30;
        Integer integer1 = 4;
        Integer integer2 = 2;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_514() {
        Integer integer0 = 25;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_515() {
        Integer integer0 = 26;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_516() {
        Integer integer0 = 17;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_517() {
        Integer integer0 = 28;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_518() {
        Integer integer0 = 29;
        Integer integer1 = 3;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_519() {
        Integer integer0 = 19;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_520() {
        Integer integer0 = 21;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_521() {
        Integer integer0 = 23;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_522() {
        Integer integer0 = 25;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_523() {
        Integer integer0 = 27;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_524() {
        Integer integer0 = 29;
        Integer integer1 = 2;
        Integer integer2 = 1;
        assertEquals((int)integer2, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_525() {
        Integer integer0 = 30;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_526() {
        Integer integer0 = 29;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_527() {
        Integer integer0 = 28;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_528() {
        Integer integer0 = 27;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_529() {
        Integer integer0 = 26;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_530() {
        Integer integer0 = 25;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_531() {
        Integer integer0 = 24;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_532() {
        Integer integer0 = 23;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_533() {
        Integer integer0 = 22;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_534() {
        Integer integer0 = 21;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_535() {
        Integer integer0 = 20;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_536() {
        Integer integer0 = 19;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_537() {
        Integer integer0 = 18;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_538() {
        Integer integer0 = 17;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_539() {
        Integer integer0 = 16;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_540() {
        Integer integer0 = 30;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_541() {
        Integer integer0 = 14;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_542() {
        Integer integer0 = 26;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_543() {
        Integer integer0 = 12;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_544() {
        Integer integer0 = 22;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_545() {
        Integer integer0 = 20;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_546() {
        Integer integer0 = 18;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_547() {
        Integer integer0 = 8;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_548() {
        Integer integer0 = 7;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_549() {
        Integer integer0 = 6;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_550() {
        Integer integer0 = 10;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_551() {
        Integer integer0 = 8;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_552() {
        Integer integer0 = 3;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_553() {
        Integer integer0 = 2;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatGcd.gcd(integer0,integer1));
    }

    @Test
    public void test_gcd_554() {
        Integer integer0 = 2;
        assertEquals((int)integer0, SatGcd.gcd(integer0,integer0));
    }

}
