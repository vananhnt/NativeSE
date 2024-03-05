package java.nio;

/* loaded from: DoubleArrayBuffer.class */
final class DoubleArrayBuffer extends DoubleBuffer {
    private final double[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    DoubleArrayBuffer(double[] array) {
        this(array.length, array, 0, false);
    }

    private DoubleArrayBuffer(int capacity, double[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static DoubleArrayBuffer copy(DoubleArrayBuffer other, int markOfOther, boolean isReadOnly) {
        DoubleArrayBuffer buf = new DoubleArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer slice() {
        return new DoubleArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    double[] protectedArray() {
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

    @Override // java.nio.DoubleBuffer
    public final double get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        double[] dArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return dArr[i + i2];
    }

    @Override // java.nio.DoubleBuffer
    public final double get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.DoubleBuffer
    public final DoubleBuffer get(double[] dst, int dstOffset, int doubleCount) {
        if (doubleCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, doubleCount);
        this.position += doubleCount;
        return this;
    }

    @Override // java.nio.DoubleBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.DoubleBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer put(double c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        double[] dArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        dArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer put(int index, double c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.DoubleBuffer
    public DoubleBuffer put(double[] src, int srcOffset, int doubleCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (doubleCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, doubleCount);
        this.position += doubleCount;
        return this;
    }
}