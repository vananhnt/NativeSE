package java.nio;

/* loaded from: ShortArrayBuffer.class */
final class ShortArrayBuffer extends ShortBuffer {
    private final short[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    ShortArrayBuffer(short[] array) {
        this(array.length, array, 0, false);
    }

    private ShortArrayBuffer(int capacity, short[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static ShortArrayBuffer copy(ShortArrayBuffer other, int markOfOther, boolean isReadOnly) {
        ShortArrayBuffer buf = new ShortArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer slice() {
        return new ShortArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    short[] protectedArray() {
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

    @Override // java.nio.ShortBuffer
    public final short get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        short[] sArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return sArr[i + i2];
    }

    @Override // java.nio.ShortBuffer
    public final short get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.ShortBuffer
    public final ShortBuffer get(short[] dst, int dstOffset, int shortCount) {
        if (shortCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, shortCount);
        this.position += shortCount;
        return this;
    }

    @Override // java.nio.ShortBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.ShortBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(short c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        short[] sArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        sArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(int index, short c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(short[] src, int srcOffset, int shortCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (shortCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, shortCount);
        this.position += shortCount;
        return this;
    }
}