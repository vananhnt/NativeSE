package java.nio;

/* loaded from: IntArrayBuffer.class */
final class IntArrayBuffer extends IntBuffer {
    private final int[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    IntArrayBuffer(int[] array) {
        this(array.length, array, 0, false);
    }

    private IntArrayBuffer(int capacity, int[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static IntArrayBuffer copy(IntArrayBuffer other, int markOfOther, boolean isReadOnly) {
        IntArrayBuffer buf = new IntArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.IntBuffer
    public IntBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.IntBuffer
    public IntBuffer slice() {
        return new IntArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    int[] protectedArray() {
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

    @Override // java.nio.IntBuffer
    public final int get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        int[] iArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return iArr[i + i2];
    }

    @Override // java.nio.IntBuffer
    public final int get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.IntBuffer
    public final IntBuffer get(int[] dst, int dstOffset, int intCount) {
        if (intCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, intCount);
        this.position += intCount;
        return this;
    }

    @Override // java.nio.IntBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.IntBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        int[] iArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        iArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int index, int c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int[] src, int srcOffset, int intCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (intCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, intCount);
        this.position += intCount;
        return this;
    }
}