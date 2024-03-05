package java.lang;

/* loaded from: Double.class */
public final class Double extends Number implements Comparable<Double> {
    public static final double MAX_VALUE = Double.MAX_VALUE;
    public static final double MIN_VALUE = Double.MIN_VALUE;
    public static final double NaN = Double.NaN;
    public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    public static final double MIN_NORMAL = Double.MIN_NORMAL;
    public static final int MAX_EXPONENT = 1023;
    public static final int MIN_EXPONENT = -1022;
    public static final Class<Double> TYPE = null;
    public static final int SIZE = 64;

    public static native long doubleToLongBits(double d);

    public static native long doubleToRawLongBits(double d);

    public static native double longBitsToDouble(long j);

    public Double(double value) {
        throw new RuntimeException("Stub!");
    }

    public Double(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(Double object) {
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

    public static boolean isInfinite(double d) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNaN() {
        throw new RuntimeException("Stub!");
    }

    public static boolean isNaN(double d) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public long longValue() {
        throw new RuntimeException("Stub!");
    }

    public static double parseDouble(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Number
    public short shortValue() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public static String toString(double d) {
        throw new RuntimeException("Stub!");
    }

    public static Double valueOf(String string) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static int compare(double double1, double double2) {
        throw new RuntimeException("Stub!");
    }

    public static Double valueOf(double d) {
        throw new RuntimeException("Stub!");
    }

    public static String toHexString(double d) {
        throw new RuntimeException("Stub!");
    }
}