package java.nio;

/* loaded from: LongArrayBuffer.class */
final class LongArrayBuffer extends LongBuffer {
    private final long[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    LongArrayBuffer(long[] array) {
        this(array.length, array, 0, false);
    }

    private LongArrayBuffer(int capacity, long[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static LongArrayBuffer copy(LongArrayBuffer other, int markOfOther, boolean isReadOnly) {
        LongArrayBuffer buf = new LongArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.LongBuffer
    public LongBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.LongBuffer
    public LongBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.LongBuffer
    public LongBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.LongBuffer
    public LongBuffer slice() {
        return new LongArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    long[] protectedArray() {
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

    @Override // java.nio.LongBuffer
    public final long get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        long[] jArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return jArr[i + i2];
    }

    @Override // java.nio.LongBuffer
    public final long get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.LongBuffer
    public final LongBuffer get(long[] dst, int dstOffset, int longCount) {
        if (longCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, longCount);
        this.position += longCount;
        return this;
    }

    @Override // java.nio.LongBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.LongBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.LongBuffer
    public LongBuffer put(long c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        long[] jArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        jArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.LongBuffer
    public LongBuffer put(int index, long c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.LongBuffer
    public LongBuffer put(long[] src, int srcOffset, int longCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (longCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, longCount);
        this.position += longCount;
        return this;
    }
}