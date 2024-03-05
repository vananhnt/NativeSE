package java.nio;

/* loaded from: FloatArrayBuffer.class */
final class FloatArrayBuffer extends FloatBuffer {
    private final float[] backingArray;
    private final int arrayOffset;
    private final boolean isReadOnly;

    FloatArrayBuffer(float[] array) {
        this(array.length, array, 0, false);
    }

    private FloatArrayBuffer(int capacity, float[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static FloatArrayBuffer copy(FloatArrayBuffer other, int markOfOther, boolean isReadOnly) {
        FloatArrayBuffer buf = new FloatArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer slice() {
        return new FloatArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    float[] protectedArray() {
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

    @Override // java.nio.FloatBuffer
    public final float get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        float[] fArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return fArr[i + i2];
    }

    @Override // java.nio.FloatBuffer
    public final float get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    @Override // java.nio.FloatBuffer
    public final FloatBuffer get(float[] dst, int dstOffset, int floatCount) {
        if (floatCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, floatCount);
        this.position += floatCount;
        return this;
    }

    @Override // java.nio.FloatBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return false;
    }

    @Override // java.nio.FloatBuffer
    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(float c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        float[] fArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        fArr[i + i2] = c;
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(int index, float c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(float[] src, int srcOffset, int floatCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (floatCount > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, floatCount);
        this.position += floatCount;
        return this;
    }
}