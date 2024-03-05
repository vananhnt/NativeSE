package java.awt.font;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NumericShaper.class */
public final class NumericShaper implements Serializable {
    public static final int EUROPEAN = 1;
    public static final int ARABIC = 2;
    public static final int EASTERN_ARABIC = 4;
    public static final int DEVANAGARI = 8;
    public static final int BENGALI = 16;
    public static final int GURMUKHI = 32;
    public static final int GUJARATI = 64;
    public static final int ORIYA = 128;
    public static final int TAMIL = 256;
    public static final int TELUGU = 512;
    public static final int KANNADA = 1024;
    public static final int MALAYALAM = 2048;
    public static final int THAI = 4096;
    public static final int LAO = 8192;
    public static final int TIBETAN = 16384;
    public static final int MYANMAR = 32768;
    public static final int ETHIOPIC = 65536;
    public static final int KHMER = 131072;
    public static final int MONGOLIAN = 262144;
    public static final int ALL_RANGES = 524287;

    NumericShaper() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public static NumericShaper getContextualShaper(int ranges, int defaultContext) {
        throw new RuntimeException("Stub!");
    }

    public static NumericShaper getContextualShaper(int ranges) {
        throw new RuntimeException("Stub!");
    }

    public int getRanges() {
        throw new RuntimeException("Stub!");
    }

    public static NumericShaper getShaper(int singleRange) {
        throw new RuntimeException("Stub!");
    }

    public boolean isContextual() {
        throw new RuntimeException("Stub!");
    }

    public void shape(char[] text, int start, int count, int context) {
        throw new RuntimeException("Stub!");
    }

    public void shape(char[] text, int start, int count) {
        throw new RuntimeException("Stub!");
    }
}