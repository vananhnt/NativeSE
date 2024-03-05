package java.math;

import android.os.UserHandle;

/* loaded from: Multiplication.class */
class Multiplication {
    static final int[] tenPows = {1, 10, 100, 1000, 10000, UserHandle.PER_USER_RANGE, 1000000, 10000000, 100000000, 1000000000};
    static final int[] fivePows = {1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125};
    static final BigInteger[] bigTenPows = new BigInteger[32];
    static final BigInteger[] bigFivePows = new BigInteger[32];

    private Multiplication() {
    }

    static {
        long fivePow = 1;
        int i = 0;
        while (i <= 18) {
            bigFivePows[i] = BigInteger.valueOf(fivePow);
            bigTenPows[i] = BigInteger.valueOf(fivePow << i);
            fivePow *= 5;
            i++;
        }
        while (i < bigTenPows.length) {
            bigFivePows[i] = bigFivePows[i - 1].multiply(bigFivePows[1]);
            bigTenPows[i] = bigTenPows[i - 1].multiply(BigInteger.TEN);
            i++;
        }
    }

    static BigInteger multiplyByPositiveInt(BigInteger val, int factor) {
        BigInt bi = val.getBigInt().copy();
        bi.multiplyByPositiveInt(factor);
        return new BigInteger(bi);
    }

    static BigInteger multiplyByTenPow(BigInteger val, long exp) {
        return exp < ((long) tenPows.length) ? multiplyByPositiveInt(val, tenPows[(int) exp]) : val.multiply(powerOf10(exp));
    }

    static BigInteger powerOf10(long exp) {
        int intExp = (int) exp;
        if (exp < bigTenPows.length) {
            return bigTenPows[intExp];
        }
        if (exp <= 50) {
            return BigInteger.TEN.pow(intExp);
        }
        if (exp <= 1000) {
            return bigFivePows[1].pow(intExp).shiftLeft(intExp);
        }
        long byteArraySize = 1 + ((long) (exp / 2.4082399653118496d));
        if (byteArraySize > Runtime.getRuntime().freeMemory()) {
            throw new ArithmeticException();
        }
        if (exp <= 2147483647L) {
            return bigFivePows[1].pow(intExp).shiftLeft(intExp);
        }
        BigInteger powerOfFive = bigFivePows[1].pow(Integer.MAX_VALUE);
        BigInteger res = powerOfFive;
        int intExp2 = (int) (exp % 2147483647L);
        for (long longExp = exp - 2147483647L; longExp > 2147483647L; longExp -= 2147483647L) {
            res = res.multiply(powerOfFive);
        }
        BigInteger res2 = res.multiply(bigFivePows[1].pow(intExp2)).shiftLeft(Integer.MAX_VALUE);
        long j = exp;
        while (true) {
            long longExp2 = j - 2147483647L;
            if (longExp2 > 2147483647L) {
                res2 = res2.shiftLeft(Integer.MAX_VALUE);
                j = longExp2;
            } else {
                return res2.shiftLeft(intExp2);
            }
        }
    }

    static BigInteger multiplyByFivePow(BigInteger val, int exp) {
        if (exp < fivePows.length) {
            return multiplyByPositiveInt(val, fivePows[exp]);
        }
        if (exp < bigFivePows.length) {
            return val.multiply(bigFivePows[exp]);
        }
        return val.multiply(bigFivePows[1].pow(exp));
    }
}