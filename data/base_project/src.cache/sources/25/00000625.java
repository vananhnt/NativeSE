package android.hardware.camera2;

import android.widget.ExpandableListView;
import gov.nist.core.Separators;

/* loaded from: Rational.class */
public final class Rational {
    private final int mNumerator;
    private final int mDenominator;

    public Rational(int numerator, int denominator) {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        this.mNumerator = numerator;
        this.mDenominator = denominator;
    }

    public int getNumerator() {
        if (this.mDenominator == 0) {
            return 0;
        }
        return this.mNumerator;
    }

    public int getDenominator() {
        return this.mDenominator;
    }

    private boolean isNaN() {
        return this.mDenominator == 0 && this.mNumerator == 0;
    }

    private boolean isInf() {
        return this.mDenominator == 0 && this.mNumerator > 0;
    }

    private boolean isNegInf() {
        return this.mDenominator == 0 && this.mNumerator < 0;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Rational)) {
            Rational other = (Rational) obj;
            if (this.mDenominator == 0 || other.mDenominator == 0) {
                if (isNaN() && other.isNaN()) {
                    return true;
                }
                if (isInf() && other.isInf()) {
                    return true;
                }
                if (isNegInf() && other.isNegInf()) {
                    return true;
                }
                return false;
            } else if (this.mNumerator == other.mNumerator && this.mDenominator == other.mDenominator) {
                return true;
            } else {
                int thisGcd = gcd();
                int otherGcd = other.gcd();
                int thisNumerator = this.mNumerator / thisGcd;
                int thisDenominator = this.mDenominator / thisGcd;
                int otherNumerator = other.mNumerator / otherGcd;
                int otherDenominator = other.mDenominator / otherGcd;
                return thisNumerator == otherNumerator && thisDenominator == otherDenominator;
            }
        }
        return false;
    }

    public String toString() {
        if (isNaN()) {
            return "NaN";
        }
        if (isInf()) {
            return "Infinity";
        }
        if (isNegInf()) {
            return "-Infinity";
        }
        return this.mNumerator + Separators.SLASH + this.mDenominator;
    }

    public float toFloat() {
        return this.mNumerator / this.mDenominator;
    }

    public int hashCode() {
        long asLong = ExpandableListView.PACKED_POSITION_VALUE_NULL & this.mNumerator;
        return Long.valueOf((asLong << 32) | (ExpandableListView.PACKED_POSITION_VALUE_NULL & this.mDenominator)).hashCode();
    }

    public int gcd() {
        int a = this.mNumerator;
        int b = this.mDenominator;
        while (b != 0) {
            int oldB = b;
            b = a % b;
            a = oldB;
        }
        return Math.abs(a);
    }
}