package java.nio;

import java.util.Arrays;

/* loaded from: CharSequenceAdapter.class */
final class CharSequenceAdapter extends CharBuffer {
    final CharSequence sequence;

    static CharSequenceAdapter copy(CharSequenceAdapter other) {
        CharSequenceAdapter buf = new CharSequenceAdapter(other.sequence);
        buf.limit = other.limit;
        buf.position = other.position;
        buf.mark = other.mark;
        return buf;
    }

    CharSequenceAdapter(CharSequence chseq) {
        super(chseq.length());
        this.sequence = chseq;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer asReadOnlyBuffer() {
        return duplicate();
    }

    @Override // java.nio.CharBuffer
    public CharBuffer compact() {
        throw new ReadOnlyBufferException();
    }

    @Override // java.nio.CharBuffer
    public CharBuffer duplicate() {
        return copy(this);
    }

    @Override // java.nio.CharBuffer
    public char get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        CharSequence charSequence = this.sequence;
        int i = this.position;
        this.position = i + 1;
        return charSequence.charAt(i);
    }

    @Override // java.nio.CharBuffer
    public char get(int index) {
        checkIndex(index);
        return this.sequence.charAt(index);
    }

    @Override // java.nio.CharBuffer
    public final CharBuffer get(char[] dst, int dstOffset, int charCount) {
        Arrays.checkOffsetAndCount(dst.length, dstOffset, charCount);
        if (charCount > remaining()) {
            throw new BufferUnderflowException();
        }
        int newPosition = this.position + charCount;
        this.sequence.toString().getChars(this.position, newPosition, dst, dstOffset);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.CharBuffer, java.nio.Buffer
    public boolean isDirect() {
        return false;
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return true;
    }

    @Override // java.nio.CharBuffer
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    char[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(char c) {
        throw new ReadOnlyBufferException();
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(int index, char c) {
        throw new ReadOnlyBufferException();
    }

    @Override // java.nio.CharBuffer
    public final CharBuffer put(char[] src, int srcOffset, int charCount) {
        throw new ReadOnlyBufferException();
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(String src, int start, int end) {
        throw new ReadOnlyBufferException();
    }

    @Override // java.nio.CharBuffer
    public CharBuffer slice() {
        return new CharSequenceAdapter(this.sequence.subSequence(this.position, this.limit));
    }

    @Override // java.nio.CharBuffer, java.lang.CharSequence
    public CharBuffer subSequence(int start, int end) {
        checkStartEndRemaining(start, end);
        CharSequenceAdapter result = copy(this);
        result.position = this.position + start;
        result.limit = this.position + end;
        return result;
    }
}