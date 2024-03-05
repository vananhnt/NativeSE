package java.nio;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharBuffer.class */
public abstract class CharBuffer extends Buffer implements Comparable<CharBuffer>, CharSequence, Appendable, Readable {
    public abstract CharBuffer asReadOnlyBuffer();

    public abstract CharBuffer compact();

    public abstract CharBuffer duplicate();

    public abstract char get();

    public abstract char get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract CharBuffer put(char c);

    public abstract CharBuffer put(int i, char c);

    public abstract CharBuffer slice();

    public abstract CharSequence subSequence(int i, int i2);

    CharBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static CharBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static CharBuffer wrap(char[] array) {
        throw new RuntimeException("Stub!");
    }

    public static CharBuffer wrap(char[] array, int start, int charCount) {
        throw new RuntimeException("Stub!");
    }

    public static CharBuffer wrap(CharSequence chseq) {
        throw new RuntimeException("Stub!");
    }

    public static CharBuffer wrap(CharSequence cs, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final char[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.CharSequence
    public final char charAt(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(CharBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public CharBuffer get(char[] dst) {
        throw new RuntimeException("Stub!");
    }

    public CharBuffer get(char[] dst, int dstOffset, int charCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.CharSequence
    public final int length() {
        throw new RuntimeException("Stub!");
    }

    public final CharBuffer put(char[] src) {
        throw new RuntimeException("Stub!");
    }

    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        throw new RuntimeException("Stub!");
    }

    public CharBuffer put(CharBuffer src) {
        throw new RuntimeException("Stub!");
    }

    public final CharBuffer put(String str) {
        throw new RuntimeException("Stub!");
    }

    public CharBuffer put(String str, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer, java.lang.CharSequence
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public CharBuffer append(char c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public CharBuffer append(CharSequence csq) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Appendable
    public CharBuffer append(CharSequence csq, int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Readable
    public int read(CharBuffer target) throws IOException {
        throw new RuntimeException("Stub!");
    }
}