package java.nio;

/* loaded from: ByteBufferAsShortBuffer.class */
final class ByteBufferAsShortBuffer extends ShortBuffer {
    private final ByteBuffer byteBuffer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ShortBuffer asShortBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsShortBuffer(slice);
    }

    private ByteBufferAsShortBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 2);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.effectiveDirectAddress = byteBuffer.effectiveDirectAddress;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer asReadOnlyBuffer() {
        ByteBufferAsShortBuffer buf = new ByteBufferAsShortBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer compact() {
        if (this.byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        this.byteBuffer.compact();
        this.byteBuffer.clear();
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer duplicate() {
        ByteBuffer bb = this.byteBuffer.duplicate().order(this.byteBuffer.order());
        ByteBufferAsShortBuffer buf = new ByteBufferAsShortBuffer(bb);
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    @Override // java.nio.ShortBuffer
    public short get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getShort(i * 2);
    }

    @Override // java.nio.ShortBuffer
    public short get(int index) {
        checkIndex(index);
        return this.byteBuffer.getShort(index * 2);
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer get(short[] dst, int dstOffset, int shortCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, shortCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, shortCount);
        }
        this.position += shortCount;
        return this;
    }

    @Override // java.nio.ShortBuffer, java.nio.Buffer
    public boolean isDirect() {
        return this.byteBuffer.isDirect();
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.byteBuffer.isReadOnly();
    }

    @Override // java.nio.ShortBuffer
    public ByteOrder order() {
        return this.byteBuffer.order();
    }

    short[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(short c) {
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putShort(i * 2, c);
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(int index, short c) {
        checkIndex(index);
        this.byteBuffer.putShort(index * 2, c);
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer put(short[] src, int srcOffset, int shortCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, shortCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, shortCount);
        }
        this.position += shortCount;
        return this;
    }

    @Override // java.nio.ShortBuffer
    public ShortBuffer slice() {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        ByteBuffer bb = this.byteBuffer.slice().order(this.byteBuffer.order());
        ShortBuffer result = new ByteBufferAsShortBuffer(bb);
        this.byteBuffer.clear();
        return result;
    }
}