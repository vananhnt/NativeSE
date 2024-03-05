package java.math;

import java.io.Serializable;
import java.util.Random;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BigInteger.class */
public class BigInteger extends Number implements Comparable<BigInteger>, Serializable {
    public static final BigInteger ZERO = null;
    public static final BigInteger ONE = null;
    public static final BigInteger TEN = null;

    public BigInteger(int numBits, Random random) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger(int bitLength, int certainty, Random unused) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger(String value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger(String value, int radix) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger(int signum, byte[] magnitude) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger(byte[] value) {
        throw new RuntimeException("Stub!");
    }

    public static BigInteger valueOf(long value) {
        throw new RuntimeException("Stub!");
    }

    public byte[] toByteArray() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger abs() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger negate() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger add(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger subtract(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public int signum() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger shiftRight(int n) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger shiftLeft(int n) {
        throw new RuntimeException("Stub!");
    }

    public int bitLength() {
        throw new RuntimeException("Stub!");
    }

    public boolean testBit(int n) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger setBit(int n) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger clearBit(int n) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger flipBit(int n) {
        throw new RuntimeException("Stub!");
    }

    public int getLowestSetBit() {
        throw new RuntimeException("Stub!");
    }

    public int bitCount() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger not() {
        throw new RuntimeException("Stub!");
    }

    public BigInteger and(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger or(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger xor(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger andNot(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public int intValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public long longValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public float floatValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public double doubleValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger min(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger max(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object x) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public String toString(int radix) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger gcd(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger multiply(BigInteger value) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger pow(int exp) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger[] divideAndRemainder(BigInteger divisor) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger divide(BigInteger divisor) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger remainder(BigInteger divisor) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger modInverse(BigInteger m) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger modPow(BigInteger exponent, BigInteger m) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger mod(BigInteger m) {
        throw new RuntimeException("Stub!");
    }

    public boolean isProbablePrime(int certainty) {
        throw new RuntimeException("Stub!");
    }

    public BigInteger nextProbablePrime() {
        throw new RuntimeException("Stub!");
    }

    public static BigInteger probablePrime(int bitLength, Random unused) {
        throw new RuntimeException("Stub!");
    }
}