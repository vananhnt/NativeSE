package java.math;

/* loaded from: Logical.class */
class Logical {
    private Logical() {
    }

    static BigInteger not(BigInteger val) {
        int i;
        if (val.sign == 0) {
            return BigInteger.MINUS_ONE;
        }
        if (val.equals(BigInteger.MINUS_ONE)) {
            return BigInteger.ZERO;
        }
        int[] resDigits = new int[val.numberLength + 1];
        if (val.sign > 0) {
            if (val.digits[val.numberLength - 1] != -1) {
                i = 0;
                while (val.digits[i] == -1) {
                    i++;
                }
            } else {
                i = 0;
                while (i < val.numberLength && val.digits[i] == -1) {
                    i++;
                }
                if (i == val.numberLength) {
                    resDigits[i] = 1;
                    return new BigInteger(-val.sign, i + 1, resDigits);
                }
            }
        } else {
            i = 0;
            while (val.digits[i] == 0) {
                resDigits[i] = -1;
                i++;
            }
        }
        resDigits[i] = val.digits[i] + val.sign;
        while (true) {
            i++;
            if (i < val.numberLength) {
                resDigits[i] = val.digits[i];
            } else {
                return new BigInteger(-val.sign, i, resDigits);
            }
        }
    }

    static BigInteger and(BigInteger val, BigInteger that) {
        if (that.sign == 0 || val.sign == 0) {
            return BigInteger.ZERO;
        }
        if (that.equals(BigInteger.MINUS_ONE)) {
            return val;
        }
        if (val.equals(BigInteger.MINUS_ONE)) {
            return that;
        }
        if (val.sign > 0) {
            if (that.sign > 0) {
                return andPositive(val, that);
            }
            return andDiffSigns(val, that);
        } else if (that.sign > 0) {
            return andDiffSigns(that, val);
        } else {
            if (val.numberLength > that.numberLength) {
                return andNegative(val, that);
            }
            return andNegative(that, val);
        }
    }

    static BigInteger andPositive(BigInteger val, BigInteger that) {
        int resLength = Math.min(val.numberLength, that.numberLength);
        int i = Math.max(val.getFirstNonzeroDigit(), that.getFirstNonzeroDigit());
        if (i >= resLength) {
            return BigInteger.ZERO;
        }
        int[] resDigits = new int[resLength];
        while (i < resLength) {
            resDigits[i] = val.digits[i] & that.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger andDiffSigns(BigInteger positive, BigInteger negative) {
        int iPos = positive.getFirstNonzeroDigit();
        int iNeg = negative.getFirstNonzeroDigit();
        if (iNeg >= positive.numberLength) {
            return BigInteger.ZERO;
        }
        int resLength = positive.numberLength;
        int[] resDigits = new int[resLength];
        int i = Math.max(iPos, iNeg);
        if (i == iNeg) {
            resDigits[i] = (-negative.digits[i]) & positive.digits[i];
            i++;
        }
        int limit = Math.min(negative.numberLength, positive.numberLength);
        while (i < limit) {
            resDigits[i] = (negative.digits[i] ^ (-1)) & positive.digits[i];
            i++;
        }
        if (i >= negative.numberLength) {
            while (i < positive.numberLength) {
                resDigits[i] = positive.digits[i];
                i++;
            }
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger andNegative(BigInteger longer, BigInteger shorter) {
        int digit;
        int iLonger = longer.getFirstNonzeroDigit();
        int iShorter = shorter.getFirstNonzeroDigit();
        if (iLonger >= shorter.numberLength) {
            return longer;
        }
        int i = Math.max(iShorter, iLonger);
        if (iShorter > iLonger) {
            digit = (-shorter.digits[i]) & (longer.digits[i] ^ (-1));
        } else if (iShorter < iLonger) {
            digit = (shorter.digits[i] ^ (-1)) & (-longer.digits[i]);
        } else {
            digit = (-shorter.digits[i]) & (-longer.digits[i]);
        }
        if (digit == 0) {
            while (true) {
                i++;
                if (i >= shorter.numberLength) {
                    break;
                }
                int i2 = (longer.digits[i] | shorter.digits[i]) ^ (-1);
                digit = i2;
                if (i2 != 0) {
                    break;
                }
            }
            if (digit == 0) {
                while (i < longer.numberLength) {
                    int i3 = longer.digits[i] ^ (-1);
                    digit = i3;
                    if (i3 != 0) {
                        break;
                    }
                    i++;
                }
                if (digit == 0) {
                    int resLength = longer.numberLength + 1;
                    int[] resDigits = new int[resLength];
                    resDigits[resLength - 1] = 1;
                    return new BigInteger(-1, resLength, resDigits);
                }
            }
        }
        int resLength2 = longer.numberLength;
        int[] resDigits2 = new int[resLength2];
        resDigits2[i] = -digit;
        while (true) {
            i++;
            if (i >= shorter.numberLength) {
                break;
            }
            resDigits2[i] = longer.digits[i] | shorter.digits[i];
        }
        while (i < longer.numberLength) {
            resDigits2[i] = longer.digits[i];
            i++;
        }
        return new BigInteger(-1, resLength2, resDigits2);
    }

    static BigInteger andNot(BigInteger val, BigInteger that) {
        if (that.sign == 0) {
            return val;
        }
        if (val.sign == 0) {
            return BigInteger.ZERO;
        }
        if (val.equals(BigInteger.MINUS_ONE)) {
            return that.not();
        }
        if (that.equals(BigInteger.MINUS_ONE)) {
            return BigInteger.ZERO;
        }
        if (val.sign > 0) {
            if (that.sign > 0) {
                return andNotPositive(val, that);
            }
            return andNotPositiveNegative(val, that);
        } else if (that.sign > 0) {
            return andNotNegativePositive(val, that);
        } else {
            return andNotNegative(val, that);
        }
    }

    static BigInteger andNotPositive(BigInteger val, BigInteger that) {
        int[] resDigits = new int[val.numberLength];
        int limit = Math.min(val.numberLength, that.numberLength);
        int i = val.getFirstNonzeroDigit();
        while (i < limit) {
            resDigits[i] = val.digits[i] & (that.digits[i] ^ (-1));
            i++;
        }
        while (i < val.numberLength) {
            resDigits[i] = val.digits[i];
            i++;
        }
        return new BigInteger(1, val.numberLength, resDigits);
    }

    static BigInteger andNotPositiveNegative(BigInteger positive, BigInteger negative) {
        int iNeg = negative.getFirstNonzeroDigit();
        int iPos = positive.getFirstNonzeroDigit();
        if (iNeg >= positive.numberLength) {
            return positive;
        }
        int resLength = Math.min(positive.numberLength, negative.numberLength);
        int[] resDigits = new int[resLength];
        int i = iPos;
        while (i < iNeg) {
            resDigits[i] = positive.digits[i];
            i++;
        }
        if (i == iNeg) {
            resDigits[i] = positive.digits[i] & (negative.digits[i] - 1);
            i++;
        }
        while (i < resLength) {
            resDigits[i] = positive.digits[i] & negative.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger andNotNegativePositive(BigInteger negative, BigInteger positive) {
        int[] resDigits;
        int iNeg = negative.getFirstNonzeroDigit();
        int iPos = positive.getFirstNonzeroDigit();
        if (iNeg >= positive.numberLength) {
            return negative;
        }
        int resLength = Math.max(negative.numberLength, positive.numberLength);
        int i = iNeg;
        if (iPos > iNeg) {
            resDigits = new int[resLength];
            int limit = Math.min(negative.numberLength, iPos);
            while (i < limit) {
                resDigits[i] = negative.digits[i];
                i++;
            }
            if (i == negative.numberLength) {
                i = iPos;
                while (i < positive.numberLength) {
                    resDigits[i] = positive.digits[i];
                    i++;
                }
            }
        } else {
            int digit = (-negative.digits[i]) & (positive.digits[i] ^ (-1));
            if (digit == 0) {
                int limit2 = Math.min(positive.numberLength, negative.numberLength);
                while (true) {
                    i++;
                    if (i >= limit2) {
                        break;
                    }
                    int i2 = (negative.digits[i] | positive.digits[i]) ^ (-1);
                    digit = i2;
                    if (i2 != 0) {
                        break;
                    }
                }
                if (digit == 0) {
                    while (i < positive.numberLength) {
                        int i3 = positive.digits[i] ^ (-1);
                        digit = i3;
                        if (i3 != 0) {
                            break;
                        }
                        i++;
                    }
                    while (i < negative.numberLength) {
                        int i4 = negative.digits[i] ^ (-1);
                        digit = i4;
                        if (i4 != 0) {
                            break;
                        }
                        i++;
                    }
                    if (digit == 0) {
                        int resLength2 = resLength + 1;
                        int[] resDigits2 = new int[resLength2];
                        resDigits2[resLength2 - 1] = 1;
                        return new BigInteger(-1, resLength2, resDigits2);
                    }
                }
            }
            resDigits = new int[resLength];
            resDigits[i] = -digit;
            i++;
        }
        int limit3 = Math.min(positive.numberLength, negative.numberLength);
        while (i < limit3) {
            resDigits[i] = negative.digits[i] | positive.digits[i];
            i++;
        }
        while (i < negative.numberLength) {
            resDigits[i] = negative.digits[i];
            i++;
        }
        while (i < positive.numberLength) {
            resDigits[i] = positive.digits[i];
            i++;
        }
        return new BigInteger(-1, resLength, resDigits);
    }

    static BigInteger andNotNegative(BigInteger val, BigInteger that) {
        int iVal = val.getFirstNonzeroDigit();
        int iThat = that.getFirstNonzeroDigit();
        if (iVal >= that.numberLength) {
            return BigInteger.ZERO;
        }
        int resLength = that.numberLength;
        int[] resDigits = new int[resLength];
        int i = iVal;
        if (iVal < iThat) {
            resDigits[i] = -val.digits[i];
            int limit = Math.min(val.numberLength, iThat);
            while (true) {
                i++;
                if (i >= limit) {
                    break;
                }
                resDigits[i] = val.digits[i] ^ (-1);
            }
            if (i == val.numberLength) {
                while (i < iThat) {
                    resDigits[i] = -1;
                    i++;
                }
                resDigits[i] = that.digits[i] - 1;
            } else {
                resDigits[i] = (val.digits[i] ^ (-1)) & (that.digits[i] - 1);
            }
        } else if (iThat < iVal) {
            resDigits[i] = (-val.digits[i]) & that.digits[i];
        } else {
            resDigits[i] = (-val.digits[i]) & (that.digits[i] - 1);
        }
        int limit2 = Math.min(val.numberLength, that.numberLength);
        while (true) {
            i++;
            if (i >= limit2) {
                break;
            }
            resDigits[i] = (val.digits[i] ^ (-1)) & that.digits[i];
        }
        while (i < that.numberLength) {
            resDigits[i] = that.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger or(BigInteger val, BigInteger that) {
        if (that.equals(BigInteger.MINUS_ONE) || val.equals(BigInteger.MINUS_ONE)) {
            return BigInteger.MINUS_ONE;
        }
        if (that.sign == 0) {
            return val;
        }
        if (val.sign == 0) {
            return that;
        }
        if (val.sign > 0) {
            if (that.sign > 0) {
                if (val.numberLength > that.numberLength) {
                    return orPositive(val, that);
                }
                return orPositive(that, val);
            }
            return orDiffSigns(val, that);
        } else if (that.sign > 0) {
            return orDiffSigns(that, val);
        } else {
            if (that.getFirstNonzeroDigit() > val.getFirstNonzeroDigit()) {
                return orNegative(that, val);
            }
            return orNegative(val, that);
        }
    }

    static BigInteger orPositive(BigInteger longer, BigInteger shorter) {
        int resLength = longer.numberLength;
        int[] resDigits = new int[resLength];
        int i = 0;
        while (i < shorter.numberLength) {
            resDigits[i] = longer.digits[i] | shorter.digits[i];
            i++;
        }
        while (i < resLength) {
            resDigits[i] = longer.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger orNegative(BigInteger val, BigInteger that) {
        int i;
        int iThat = that.getFirstNonzeroDigit();
        int iVal = val.getFirstNonzeroDigit();
        if (iVal >= that.numberLength) {
            return that;
        }
        if (iThat >= val.numberLength) {
            return val;
        }
        int resLength = Math.min(val.numberLength, that.numberLength);
        int[] resDigits = new int[resLength];
        if (iThat == iVal) {
            resDigits[iVal] = -((-val.digits[iVal]) | (-that.digits[iVal]));
            i = iVal;
        } else {
            i = iThat;
            while (i < iVal) {
                resDigits[i] = that.digits[i];
                i++;
            }
            resDigits[i] = that.digits[i] & (val.digits[i] - 1);
        }
        while (true) {
            i++;
            if (i < resLength) {
                resDigits[i] = val.digits[i] & that.digits[i];
            } else {
                return new BigInteger(-1, resLength, resDigits);
            }
        }
    }

    static BigInteger orDiffSigns(BigInteger positive, BigInteger negative) {
        int i;
        int iNeg = negative.getFirstNonzeroDigit();
        int iPos = positive.getFirstNonzeroDigit();
        if (iPos >= negative.numberLength) {
            return negative;
        }
        int resLength = negative.numberLength;
        int[] resDigits = new int[resLength];
        if (iNeg < iPos) {
            i = iNeg;
            while (i < iPos) {
                resDigits[i] = negative.digits[i];
                i++;
            }
        } else if (iPos < iNeg) {
            int i2 = iPos;
            resDigits[i2] = -positive.digits[i2];
            int limit = Math.min(positive.numberLength, iNeg);
            while (true) {
                i2++;
                if (i2 >= limit) {
                    break;
                }
                resDigits[i2] = positive.digits[i2] ^ (-1);
            }
            if (i2 != positive.numberLength) {
                resDigits[i2] = ((-negative.digits[i2]) | positive.digits[i2]) ^ (-1);
            } else {
                while (i2 < iNeg) {
                    resDigits[i2] = -1;
                    i2++;
                }
                resDigits[i2] = negative.digits[i2] - 1;
            }
            i = i2 + 1;
        } else {
            resDigits[iPos] = -((-negative.digits[iPos]) | positive.digits[iPos]);
            i = iPos + 1;
        }
        int limit2 = Math.min(negative.numberLength, positive.numberLength);
        while (i < limit2) {
            resDigits[i] = negative.digits[i] & (positive.digits[i] ^ (-1));
            i++;
        }
        while (i < negative.numberLength) {
            resDigits[i] = negative.digits[i];
            i++;
        }
        return new BigInteger(-1, resLength, resDigits);
    }

    static BigInteger xor(BigInteger val, BigInteger that) {
        if (that.sign == 0) {
            return val;
        }
        if (val.sign == 0) {
            return that;
        }
        if (that.equals(BigInteger.MINUS_ONE)) {
            return val.not();
        }
        if (val.equals(BigInteger.MINUS_ONE)) {
            return that.not();
        }
        if (val.sign > 0) {
            if (that.sign > 0) {
                if (val.numberLength > that.numberLength) {
                    return xorPositive(val, that);
                }
                return xorPositive(that, val);
            }
            return xorDiffSigns(val, that);
        } else if (that.sign > 0) {
            return xorDiffSigns(that, val);
        } else {
            if (that.getFirstNonzeroDigit() > val.getFirstNonzeroDigit()) {
                return xorNegative(that, val);
            }
            return xorNegative(val, that);
        }
    }

    static BigInteger xorPositive(BigInteger longer, BigInteger shorter) {
        int resLength = longer.numberLength;
        int[] resDigits = new int[resLength];
        int i = Math.min(longer.getFirstNonzeroDigit(), shorter.getFirstNonzeroDigit());
        while (i < shorter.numberLength) {
            resDigits[i] = longer.digits[i] ^ shorter.digits[i];
            i++;
        }
        while (i < longer.numberLength) {
            resDigits[i] = longer.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger xorNegative(BigInteger val, BigInteger that) {
        int resLength = Math.max(val.numberLength, that.numberLength);
        int[] resDigits = new int[resLength];
        int iVal = val.getFirstNonzeroDigit();
        int iThat = that.getFirstNonzeroDigit();
        int i = iThat;
        if (iVal == iThat) {
            resDigits[i] = (-val.digits[i]) ^ (-that.digits[i]);
        } else {
            resDigits[i] = -that.digits[i];
            int limit = Math.min(that.numberLength, iVal);
            while (true) {
                i++;
                if (i >= limit) {
                    break;
                }
                resDigits[i] = that.digits[i] ^ (-1);
            }
            if (i == that.numberLength) {
                while (i < iVal) {
                    resDigits[i] = -1;
                    i++;
                }
                resDigits[i] = val.digits[i] - 1;
            } else {
                resDigits[i] = (-val.digits[i]) ^ (that.digits[i] ^ (-1));
            }
        }
        int limit2 = Math.min(val.numberLength, that.numberLength);
        while (true) {
            i++;
            if (i >= limit2) {
                break;
            }
            resDigits[i] = val.digits[i] ^ that.digits[i];
        }
        while (i < val.numberLength) {
            resDigits[i] = val.digits[i];
            i++;
        }
        while (i < that.numberLength) {
            resDigits[i] = that.digits[i];
            i++;
        }
        return new BigInteger(1, resLength, resDigits);
    }

    static BigInteger xorDiffSigns(BigInteger positive, BigInteger negative) {
        int[] resDigits;
        int i;
        int resLength = Math.max(negative.numberLength, positive.numberLength);
        int iNeg = negative.getFirstNonzeroDigit();
        int iPos = positive.getFirstNonzeroDigit();
        if (iNeg < iPos) {
            resDigits = new int[resLength];
            i = iNeg;
            resDigits[i] = negative.digits[i];
            int limit = Math.min(negative.numberLength, iPos);
            while (true) {
                i++;
                if (i >= limit) {
                    break;
                }
                resDigits[i] = negative.digits[i];
            }
            if (i == negative.numberLength) {
                while (i < positive.numberLength) {
                    resDigits[i] = positive.digits[i];
                    i++;
                }
            }
        } else if (iPos < iNeg) {
            resDigits = new int[resLength];
            i = iPos;
            resDigits[i] = -positive.digits[i];
            int limit2 = Math.min(positive.numberLength, iNeg);
            while (true) {
                i++;
                if (i >= limit2) {
                    break;
                }
                resDigits[i] = positive.digits[i] ^ (-1);
            }
            if (i == iNeg) {
                resDigits[i] = (positive.digits[i] ^ (-negative.digits[i])) ^ (-1);
                i++;
            } else {
                while (i < iNeg) {
                    resDigits[i] = -1;
                    i++;
                }
                while (i < negative.numberLength) {
                    resDigits[i] = negative.digits[i];
                    i++;
                }
            }
        } else {
            int i2 = iNeg;
            int digit = positive.digits[i2] ^ (-negative.digits[i2]);
            if (digit == 0) {
                int limit3 = Math.min(positive.numberLength, negative.numberLength);
                while (true) {
                    i2++;
                    if (i2 >= limit3) {
                        break;
                    }
                    int i3 = positive.digits[i2] ^ (negative.digits[i2] ^ (-1));
                    digit = i3;
                    if (i3 != 0) {
                        break;
                    }
                }
                if (digit == 0) {
                    while (i2 < positive.numberLength) {
                        int i4 = positive.digits[i2] ^ (-1);
                        digit = i4;
                        if (i4 != 0) {
                            break;
                        }
                        i2++;
                    }
                    while (i2 < negative.numberLength) {
                        int i5 = negative.digits[i2] ^ (-1);
                        digit = i5;
                        if (i5 != 0) {
                            break;
                        }
                        i2++;
                    }
                    if (digit == 0) {
                        int resLength2 = resLength + 1;
                        int[] resDigits2 = new int[resLength2];
                        resDigits2[resLength2 - 1] = 1;
                        return new BigInteger(-1, resLength2, resDigits2);
                    }
                }
            }
            resDigits = new int[resLength];
            resDigits[i2] = -digit;
            i = i2 + 1;
        }
        int limit4 = Math.min(negative.numberLength, positive.numberLength);
        while (i < limit4) {
            resDigits[i] = ((negative.digits[i] ^ (-1)) ^ positive.digits[i]) ^ (-1);
            i++;
        }
        while (i < positive.numberLength) {
            resDigits[i] = positive.digits[i];
            i++;
        }
        while (i < negative.numberLength) {
            resDigits[i] = negative.digits[i];
            i++;
        }
        return new BigInteger(-1, resLength, resDigits);
    }
}