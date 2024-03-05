package java.nio;

/* loaded from: CharArrayBuffer.class */
final class CharArrayBuffer extends CharBuffer {
    private final char[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    CharArrayBuffer(char[] array) {
        this(array.length, array, 0, false);
    }

    private CharArrayBuffer(int capacity, char[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static CharArrayBuffer copy(CharArrayBuffer other, int markOfOther, boolean isReadOnly) {
        CharArrayBuffer buf = new CharArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.CharBuffer
    public CharBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.CharBuffer
    public CharBuffer slice() {
        return new CharArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    char[] protectedArray() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        return this.backingArray;
    }

    int protectedArrayOffset() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        return this.arrayOffset;
    }

    boolean protectedHasArray() {
        if (this.isReadOnly) {
            return false;
        }
        return true;
    }

    @Override // java.nio.CharBuffer
    public final char get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        char[] cArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return cArr[i + i2];
    }

    @Override // java.nio.CharBuffer
    public final char get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.CharBuffer
    public final CharBuffer get(char[] dst, int srcOffset, int charCount) {
        if (charCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, srcOffset, charCount);
        this.position += charCount;
        return this;
    }

    @Override // java.nio.CharBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.CharBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.CharBuffer, java.lang.CharSequence
    public final CharBuffer subSequence(int start, int end) {
        checkStartEndRemaining(start, end);
        CharBuffer result = duplicate();
        result.limit(this.position + end);
        result.position(this.position + start);
        return result;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(char c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        char[] cArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        cArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(int index, char c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (charCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, charCount);
        this.position += charCount;
        return this;
    }

    @Override // java.nio.CharBuffer, java.nio.Buffer, java.lang.CharSequence
    public final String toString() {
        return String.copyValueOf(this.backingArray, this.arrayOffset + this.position, remaining());
    }
}