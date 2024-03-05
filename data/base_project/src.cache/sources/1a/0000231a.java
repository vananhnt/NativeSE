package java.lang;

import java.io.Serializable;

/* loaded from: StringBuilder.class */
public final class StringBuilder extends AbstractStringBuilder implements Appendable, CharSequence, Serializable {
    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int offsetByCodePoints(int x0, int x1) {
        return super.offsetByCodePoints(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int codePointCount(int x0, int x1) {
        return super.codePointCount(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int codePointBefore(int x0) {
        return super.codePointBefore(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int codePointAt(int x0) {
        return super.codePointAt(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ void trimToSize() {
        super.trimToSize();
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int lastIndexOf(String x0, int x1) {
        return super.lastIndexOf(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int lastIndexOf(String x0) {
        return super.lastIndexOf(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int indexOf(String x0, int x1) {
        return super.indexOf(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int indexOf(String x0) {
        return super.indexOf(x0);
    }

    @Override // java.lang.AbstractStringBuilder, java.lang.CharSequence
    public /* bridge */ /* synthetic */ CharSequence subSequence(int x0, int x1) {
        return super.subSequence(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ String substring(int x0, int x1) {
        return super.substring(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ String substring(int x0) {
        return super.substring(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ void setLength(int x0) {
        super.setLength(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ void setCharAt(int x0, char x1) {
        super.setCharAt(x0, x1);
    }

    @Override // java.lang.AbstractStringBuilder, java.lang.CharSequence
    public /* bridge */ /* synthetic */ int length() {
        return super.length();
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ void getChars(int x0, int x1, char[] x2, int x3) {
        super.getChars(x0, x1, x2, x3);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ void ensureCapacity(int x0) {
        super.ensureCapacity(x0);
    }

    @Override // java.lang.AbstractStringBuilder, java.lang.CharSequence
    public /* bridge */ /* synthetic */ char charAt(int x0) {
        return super.charAt(x0);
    }

    @Override // java.lang.AbstractStringBuilder
    public /* bridge */ /* synthetic */ int capacity() {
        return super.capacity();
    }

    public StringBuilder() {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder(CharSequence seq) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder(String str) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(boolean b) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public StringBuilder append(char c) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(int i) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(long l) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(float f) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(double d) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(String str) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(StringBuffer sb) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(char[] chars) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder append(char[] str, int offset, int len) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public StringBuilder append(CharSequence csq) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public StringBuilder append(CharSequence csq, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder appendCodePoint(int codePoint) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder delete(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder deleteCharAt(int index) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, boolean b) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, char c) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, int i) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, long l) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, float f) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, double d) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, Object obj) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, String str) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, char[] ch) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, char[] str, int strOffset, int strLen) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, CharSequence s) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder insert(int offset, CharSequence s, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder replace(int start, int end, String string) {
        throw new RuntimeException("Stub!");
    }

    public StringBuilder reverse() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.AbstractStringBuilder, java.lang.CharSequence
    public String toString() {
        throw new RuntimeException("Stub!");
    }
}