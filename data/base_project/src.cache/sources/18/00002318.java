package java.lang;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Locale;

/* loaded from: String.class */
public final class String implements Serializable, Comparable<String>, CharSequence {
    public static final Comparator<String> CASE_INSENSITIVE_ORDER = null;

    @Override // java.lang.CharSequence
    public native char charAt(int i);

    @Override // java.lang.Comparable
    public native int compareTo(String str);

    public native boolean equals(Object obj);

    public native String intern();

    public native boolean isEmpty();

    @Override // java.lang.CharSequence
    public native int length();

    public String() {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public String(byte[] data, int high) {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data, int offset, int byteCount) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public String(byte[] data, int high, int offset, int byteCount) {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data, int offset, int byteCount, String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data, String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data, int offset, int byteCount, Charset charset) {
        throw new RuntimeException("Stub!");
    }

    public String(byte[] data, Charset charset) {
        throw new RuntimeException("Stub!");
    }

    public String(char[] data) {
        throw new RuntimeException("Stub!");
    }

    public String(char[] data, int offset, int charCount) {
        throw new RuntimeException("Stub!");
    }

    public String(String toCopy) {
        throw new RuntimeException("Stub!");
    }

    public String(StringBuffer stringBuffer) {
        throw new RuntimeException("Stub!");
    }

    public String(int[] codePoints, int offset, int count) {
        throw new RuntimeException("Stub!");
    }

    public String(StringBuilder stringBuilder) {
        throw new RuntimeException("Stub!");
    }

    public int compareToIgnoreCase(String string) {
        throw new RuntimeException("Stub!");
    }

    public String concat(String string) {
        throw new RuntimeException("Stub!");
    }

    public static String copyValueOf(char[] data) {
        throw new RuntimeException("Stub!");
    }

    public static String copyValueOf(char[] data, int start, int length) {
        throw new RuntimeException("Stub!");
    }

    public boolean endsWith(String suffix) {
        throw new RuntimeException("Stub!");
    }

    public boolean equalsIgnoreCase(String string) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public void getBytes(int start, int end, byte[] data, int index) {
        throw new RuntimeException("Stub!");
    }

    public byte[] getBytes() {
        throw new RuntimeException("Stub!");
    }

    public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public byte[] getBytes(Charset charset) {
        throw new RuntimeException("Stub!");
    }

    public void getChars(int start, int end, char[] buffer, int index) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public int indexOf(int c) {
        throw new RuntimeException("Stub!");
    }

    public int indexOf(int c, int start) {
        throw new RuntimeException("Stub!");
    }

    public int indexOf(String string) {
        throw new RuntimeException("Stub!");
    }

    public int indexOf(String subString, int start) {
        throw new RuntimeException("Stub!");
    }

    public int lastIndexOf(int c) {
        throw new RuntimeException("Stub!");
    }

    public int lastIndexOf(int c, int start) {
        throw new RuntimeException("Stub!");
    }

    public int lastIndexOf(String string) {
        throw new RuntimeException("Stub!");
    }

    public int lastIndexOf(String subString, int start) {
        throw new RuntimeException("Stub!");
    }

    public boolean regionMatches(int thisStart, String string, int start, int length) {
        throw new RuntimeException("Stub!");
    }

    public boolean regionMatches(boolean ignoreCase, int thisStart, String string, int start, int length) {
        throw new RuntimeException("Stub!");
    }

    public String replace(char oldChar, char newChar) {
        throw new RuntimeException("Stub!");
    }

    public String replace(CharSequence target, CharSequence replacement) {
        throw new RuntimeException("Stub!");
    }

    public boolean startsWith(String prefix) {
        throw new RuntimeException("Stub!");
    }

    public boolean startsWith(String prefix, int start) {
        throw new RuntimeException("Stub!");
    }

    public String substring(int start) {
        throw new RuntimeException("Stub!");
    }

    public String substring(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public char[] toCharArray() {
        throw new RuntimeException("Stub!");
    }

    public String toLowerCase() {
        throw new RuntimeException("Stub!");
    }

    public String toLowerCase(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.CharSequence
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public String toUpperCase() {
        throw new RuntimeException("Stub!");
    }

    public String toUpperCase(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public String trim() {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(char[] data) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(char[] data, int start, int length) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(char value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(double value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(float value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(int value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(long value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(Object value) {
        throw new RuntimeException("Stub!");
    }

    public static String valueOf(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public boolean contentEquals(StringBuffer strbuf) {
        throw new RuntimeException("Stub!");
    }

    public boolean contentEquals(CharSequence cs) {
        throw new RuntimeException("Stub!");
    }

    public boolean matches(String regularExpression) {
        throw new RuntimeException("Stub!");
    }

    public String replaceAll(String regularExpression, String replacement) {
        throw new RuntimeException("Stub!");
    }

    public String replaceFirst(String regularExpression, String replacement) {
        throw new RuntimeException("Stub!");
    }

    public String[] split(String regularExpression) {
        throw new RuntimeException("Stub!");
    }

    public String[] split(String regularExpression, int limit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public int codePointAt(int index) {
        throw new RuntimeException("Stub!");
    }

    public int codePointBefore(int index) {
        throw new RuntimeException("Stub!");
    }

    public int codePointCount(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public boolean contains(CharSequence cs) {
        throw new RuntimeException("Stub!");
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        throw new RuntimeException("Stub!");
    }

    public static String format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public static String format(Locale locale, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }
}