package java.nio;

/* loaded from: ByteBufferAsCharBuffer.class */
final class ByteBufferAsCharBuffer extends CharBuffer {
    private final ByteBuffer byteBuffer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharBuffer asCharBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsCharBuffer(slice);
    }

    private ByteBufferAsCharBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 2);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.effectiveDirectAddress = byteBuffer.effectiveDirectAddress;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer asReadOnlyBuffer() {
        ByteBufferAsCharBuffer buf = new ByteBufferAsCharBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer compact() {
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

    @Override // java.nio.CharBuffer
    public CharBuffer duplicate() {
        ByteBuffer bb = this.byteBuffer.duplicate().order(this.byteBuffer.order());
        ByteBufferAsCharBuffer buf = new ByteBufferAsCharBuffer(bb);
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    @Override // java.nio.CharBuffer
    public char get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getChar(i * 2);
    }

    @Override // java.nio.CharBuffer
    public char get(int index) {
        checkIndex(index);
        return this.byteBuffer.getChar(index * 2);
    }

    @Override // java.nio.CharBuffer
    public CharBuffer get(char[] dst, int dstOffset, int charCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, charCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, charCount);
        }
        this.position += charCount;
        return this;
    }

    @Override // java.nio.CharBuffer, java.nio.Buffer
    public boolean isDirect() {
        return this.byteBuffer.isDirect();
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.byteBuffer.isReadOnly();
    }

    @Override // java.nio.CharBuffer
    public ByteOrder order() {
        return this.byteBuffer.order();
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
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putChar(i * 2, c);
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(int index, char c) {
        checkIndex(index);
        this.byteBuffer.putChar(index * 2, c);
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, charCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, charCount);
        }
        this.position += charCount;
        return this;
    }

    @Override // java.nio.CharBuffer
    public CharBuffer slice() {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        ByteBuffer bb = this.byteBuffer.slice().order(this.byteBuffer.order());
        CharBuffer result = new ByteBufferAsCharBuffer(bb);
        this.byteBuffer.clear();
        return result;
    }

    @Override // java.nio.CharBuffer, java.lang.CharSequence
    public CharBuffer subSequence(int start, int end) {
        checkStartEndRemaining(start, end);
        CharBuffer result = duplicate();
        result.limit(this.position + end);
        result.position(this.position + start);
        return result;
    }
}