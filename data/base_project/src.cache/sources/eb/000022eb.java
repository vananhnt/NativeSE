package java.lang;

/* loaded from: Float.class */
public final class Float extends Number implements Comparable<Float> {
    public static final float MAX_VALUE = Float.MAX_VALUE;
    public static final float MIN_VALUE = Float.MIN_VALUE;
    public static final float NaN = Float.NaN;
    public static final float POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
    public static final float NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;
    public static final float MIN_NORMAL = Float.MIN_NORMAL;
    public static final int MAX_EXPONENT = 127;
    public static final int MIN_EXPONENT = -126;
    public static final Class<Float> TYPE = null;
    public static final int SIZE = 32;

    public static native int floatToIntBits(float f);

    public static native int floatToRawIntBits(float f);

    public static native float intBitsToFloat(int i);

    public Float(float value) {
        throw new RuntimeException("Stub!");
    }

    public Float(double value) {
        throw new RuntimeException("Stub!");
    }

    public Float(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(Float object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public byte byteValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public double doubleValue() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public float floatValue() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public int intValue() {
        throw new RuntimeException("Stub!");
    }

    public boolean isInfinite() {
        throw new RuntimeException("Stub!");
    }

    public static boolean isInfinite(float f) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNaN() {
        throw new RuntimeException("Stub!");
    }

    public static boolean isNaN(float f) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public long longValue() {
        throw new RuntimeException("Stub!");
    }

    public static float parseFloat(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public short shortValue() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public static String toString(float f) {
        throw new RuntimeException("Stub!");
    }

    public static Float valueOf(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static int compare(float float1, float float2) {
        throw new RuntimeException("Stub!");
    }

    public static Float valueOf(float f) {
        throw new RuntimeException("Stub!");
    }

    public static String toHexString(float f) {
        throw new RuntimeException("Stub!");
    }
}