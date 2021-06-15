package de.wwu.muli.tcg.concrete_programs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/* 2021-06-14 Renamed from Main to SatPrimes01 : HW */
public class SatPrimes01 {

    // Multiplies two integers n and m
    static int mult(int n, int m) {
        if (m < 0) {
            return mult(n, -m);
        }
        if (m == 0) {
            return 0;
        }
        if (m == 1) {
            return 1;
        }
        return n + mult(n, m - 1);
    }

    // Is n a multiple of m?
    static int multiple_of(int n, int m) {
        if (m < 0) {
            return multiple_of(n, -m);
        }
        if (n < 0) {
            return 0; // 2021-06-14 Commented the following out, it does not terminate : HW :: return multiple_of(-n, m); // false
        }
        if (m == 0) {
            return 0; // false
        }
        if (n == 0) {
            return 1; // true
        }
        return multiple_of(n - m, m);
    }

    // Is n prime?
    static int is_prime(int n) {
        return is_prime_(n, n - 1);
    }

    static int is_prime_(int n, int m) {
        if (n <= 1) {
            return 0; // false
        } else if (n == 2) {
            return 1; // true
        } else {
            if (m <= 1) {
                return 1; // true
            } else {
                if (multiple_of(n, m) == 1) { // 2021-06-14 Adapted the following from : HW :: multiple_of(n, m) == 1
                    return 0; // false
                }
                return is_prime_(n, m - 1);
            }
        }
    }

    @Test
    public void test_is_prime_0() {
        Integer integer0 = 4;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_1() {
        Integer integer0 = 5;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_2() {
        Integer integer0 = 6;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_3() {
        Integer integer0 = 7;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_4() {
        Integer integer0 = 8;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_5() {
        Integer integer0 = 9;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_6() {
        Integer integer0 = 10;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_7() {
        Integer integer0 = 11;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_8() {
        Integer integer0 = 12;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_9() {
        Integer integer0 = 13;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_10() {
        Integer integer0 = 14;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_11() {
        Integer integer0 = 15;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_12() {
        Integer integer0 = 16;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_13() {
        Integer integer0 = 17;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_14() {
        Integer integer0 = 18;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_15() {
        Integer integer0 = 19;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_16() {
        Integer integer0 = 20;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_17() {
        Integer integer0 = 21;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_18() {
        Integer integer0 = 22;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_19() {
        Integer integer0 = 23;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_20() {
        Integer integer0 = 24;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_21() {
        Integer integer0 = 25;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_22() {
        Integer integer0 = 26;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_23() {
        Integer integer0 = 27;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_24() {
        Integer integer0 = 28;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_25() {
        Integer integer0 = 29;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_26() {
        Integer integer0 = 30;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_27() {
        Integer integer0 = 3;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_28() {
        Integer integer0 = 2;
        Integer integer1 = 1;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_is_prime_29() {
        Integer integer0 = 1;
        Integer integer1 = 0;
        assertEquals((int)integer1, SatPrimes01.is_prime(integer0));
    }

    @Test
    public void test_mult_0() {
        Integer integer0 = 1;
        Integer integer1 = 50;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_1() {
        Integer integer0 = 1;
        Integer integer1 = 49;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_2() {
        Integer integer0 = 1;
        Integer integer1 = 48;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_3() {
        Integer integer0 = 1;
        Integer integer1 = 47;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_4() {
        Integer integer0 = 1;
        Integer integer1 = 46;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_5() {
        Integer integer0 = 1;
        Integer integer1 = 45;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_6() {
        Integer integer0 = 1;
        Integer integer1 = 44;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_7() {
        Integer integer0 = 1;
        Integer integer1 = 43;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_8() {
        Integer integer0 = 1;
        Integer integer1 = 42;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_9() {
        Integer integer0 = 1;
        Integer integer1 = 41;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_10() {
        Integer integer0 = 1;
        Integer integer1 = 40;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_11() {
        Integer integer0 = 1;
        Integer integer1 = 39;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_12() {
        Integer integer0 = 1;
        Integer integer1 = 38;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_13() {
        Integer integer0 = 1;
        Integer integer1 = 37;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_14() {
        Integer integer0 = 1;
        Integer integer1 = 36;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_15() {
        Integer integer0 = 1;
        Integer integer1 = 35;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_16() {
        Integer integer0 = 1;
        Integer integer1 = 34;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_17() {
        Integer integer0 = 1;
        Integer integer1 = 33;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_18() {
        Integer integer0 = 1;
        Integer integer1 = 32;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_19() {
        Integer integer0 = 1;
        Integer integer1 = 31;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_20() {
        Integer integer0 = 1;
        Integer integer1 = 30;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_21() {
        Integer integer0 = 1;
        Integer integer1 = 29;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_22() {
        Integer integer0 = 1;
        Integer integer1 = 28;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_23() {
        Integer integer0 = 1;
        Integer integer1 = 27;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_24() {
        Integer integer0 = 1;
        Integer integer1 = 26;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_25() {
        Integer integer0 = 1;
        Integer integer1 = 25;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_26() {
        Integer integer0 = 1;
        Integer integer1 = 24;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_27() {
        Integer integer0 = 1;
        Integer integer1 = 23;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_28() {
        Integer integer0 = 1;
        Integer integer1 = 22;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_29() {
        Integer integer0 = 1;
        Integer integer1 = 21;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_30() {
        Integer integer0 = 1;
        Integer integer1 = 20;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_31() {
        Integer integer0 = 1;
        Integer integer1 = 19;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_32() {
        Integer integer0 = 1;
        Integer integer1 = 18;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_33() {
        Integer integer0 = 1;
        Integer integer1 = 17;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_34() {
        Integer integer0 = 1;
        Integer integer1 = 16;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_35() {
        Integer integer0 = 1;
        Integer integer1 = 15;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_36() {
        Integer integer0 = 1;
        Integer integer1 = 14;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_37() {
        Integer integer0 = 1;
        Integer integer1 = 13;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_38() {
        Integer integer0 = 1;
        Integer integer1 = 12;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_39() {
        Integer integer0 = 1;
        Integer integer1 = 11;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_40() {
        Integer integer0 = 1;
        Integer integer1 = 10;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_41() {
        Integer integer0 = 1;
        Integer integer1 = 9;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_42() {
        Integer integer0 = 1;
        Integer integer1 = 8;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_43() {
        Integer integer0 = 1;
        Integer integer1 = 7;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_44() {
        Integer integer0 = 1;
        Integer integer1 = 6;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_45() {
        Integer integer0 = 1;
        Integer integer1 = 5;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_46() {
        Integer integer0 = 1;
        Integer integer1 = 4;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_47() {
        Integer integer0 = 1;
        Integer integer1 = 3;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_48() {
        Integer integer0 = 1;
        Integer integer1 = 2;
        assertEquals((int)integer1, SatPrimes01.mult(integer0,integer1));
    }

    @Test
    public void test_mult_49() {
        Integer integer0 = 1;
        assertEquals((int)integer0, SatPrimes01.mult(integer0,integer0));
    }
    
}