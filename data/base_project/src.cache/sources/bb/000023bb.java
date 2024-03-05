package java.nio;

/* loaded from: ByteBufferAsFloatBuffer.class */
final class ByteBufferAsFloatBuffer extends FloatBuffer {
    private final ByteBuffer byteBuffer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FloatBuffer asFloatBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsFloatBuffer(slice);
    }

    ByteBufferAsFloatBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 4);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.effectiveDirectAddress = byteBuffer.effectiveDirectAddress;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer asReadOnlyBuffer() {
        ByteBufferAsFloatBuffer buf = new ByteBufferAsFloatBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer compact() {
        if (this.byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        this.byteBuffer.compact();
        this.byteBuffer.clear();
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer duplicate() {
        ByteBuffer bb = this.byteBuffer.duplicate().order(this.byteBuffer.order());
        ByteBufferAsFloatBuffer buf = new ByteBufferAsFloatBuffer(bb);
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    @Override // java.nio.FloatBuffer
    public float get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getFloat(i * 4);
    }

    @Override // java.nio.FloatBuffer
    public float get(int index) {
        checkIndex(index);
        return this.byteBuffer.getFloat(index * 4);
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer get(float[] dst, int dstOffset, int floatCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, floatCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, floatCount);
        }
        this.position += floatCount;
        return this;
    }

    @Override // java.nio.FloatBuffer, java.nio.Buffer
    public boolean isDirect() {
        return this.byteBuffer.isDirect();
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.byteBuffer.isReadOnly();
    }

    @Override // java.nio.FloatBuffer
    public ByteOrder order() {
        return this.byteBuffer.order();
    }

    float[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(float c) {
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putFloat(i * 4, c);
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(int index, float c) {
        checkIndex(index);
        this.byteBuffer.putFloat(index * 4, c);
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer put(float[] src, int srcOffset, int floatCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, floatCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, floatCount);
        }
        this.position += floatCount;
        return this;
    }

    @Override // java.nio.FloatBuffer
    public FloatBuffer slice() {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        ByteBuffer bb = this.byteBuffer.slice().order(this.byteBuffer.order());
        FloatBuffer result = new ByteBufferAsFloatBuffer(bb);
        this.byteBuffer.clear();
        return result;
    }
}