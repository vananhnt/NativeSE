package java.nio;

/* loaded from: ByteBufferAsIntBuffer.class */
final class ByteBufferAsIntBuffer extends IntBuffer {
    private final ByteBuffer byteBuffer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IntBuffer asIntBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsIntBuffer(slice);
    }

    private ByteBufferAsIntBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 4);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.effectiveDirectAddress = byteBuffer.effectiveDirectAddress;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer asReadOnlyBuffer() {
        ByteBufferAsIntBuffer buf = new ByteBufferAsIntBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer compact() {
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

    @Override // java.nio.IntBuffer
    public IntBuffer duplicate() {
        ByteBuffer bb = this.byteBuffer.duplicate().order(this.byteBuffer.order());
        ByteBufferAsIntBuffer buf = new ByteBufferAsIntBuffer(bb);
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    @Override // java.nio.IntBuffer
    public int get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getInt(i * 4);
    }

    @Override // java.nio.IntBuffer
    public int get(int index) {
        checkIndex(index);
        return this.byteBuffer.getInt(index * 4);
    }

    @Override // java.nio.IntBuffer
    public IntBuffer get(int[] dst, int dstOffset, int intCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, intCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, intCount);
        }
        this.position += intCount;
        return this;
    }

    @Override // java.nio.IntBuffer, java.nio.Buffer
    public boolean isDirect() {
        return this.byteBuffer.isDirect();
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.byteBuffer.isReadOnly();
    }

    @Override // java.nio.IntBuffer
    public ByteOrder order() {
        return this.byteBuffer.order();
    }

    int[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int c) {
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putInt(i * 4, c);
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int index, int c) {
        checkIndex(index);
        this.byteBuffer.putInt(index * 4, c);
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer put(int[] src, int srcOffset, int intCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, intCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, intCount);
        }
        this.position += intCount;
        return this;
    }

    @Override // java.nio.IntBuffer
    public IntBuffer slice() {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        ByteBuffer bb = this.byteBuffer.slice().order(this.byteBuffer.order());
        IntBuffer result = new ByteBufferAsIntBuffer(bb);
        this.byteBuffer.clear();
        return result;
    }
}