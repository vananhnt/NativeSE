package java.nio;

import java.nio.channels.FileChannel;
import libcore.io.Memory;

/* loaded from: DirectByteBuffer.class */
class DirectByteBuffer extends MappedByteBuffer {
    protected final int offset;
    private final boolean isReadOnly;

    /* JADX INFO: Access modifiers changed from: protected */
    public DirectByteBuffer(MemoryBlock block, int capacity, int offset, boolean isReadOnly, FileChannel.MapMode mapMode) {
        super(block, capacity, mapMode);
        long baseSize = block.getSize();
        if (baseSize >= 0 && capacity + offset > baseSize) {
            throw new IllegalArgumentException("capacity + offset > baseSize");
        }
        this.effectiveDirectAddress = block.toLong() + offset;
        this.offset = offset;
        this.isReadOnly = isReadOnly;
    }

    DirectByteBuffer(long address, int capacity) {
        this(MemoryBlock.wrapFromJni(address, capacity), capacity, 0, false, null);
    }

    private static DirectByteBuffer copy(DirectByteBuffer other, int markOfOther, boolean isReadOnly) {
        DirectByteBuffer buf = new DirectByteBuffer(other.block, other.capacity(), other.offset, isReadOnly, other.mapMode);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        Memory.memmove(this, 0, this, this.position, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer slice() {
        return new DirectByteBuffer(this.block, remaining(), this.offset + this.position, this.isReadOnly, this.mapMode);
    }

    @Override // java.nio.Buffer
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    byte[] protectedArray() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        byte[] array = this.block.array();
        if (array == null) {
            throw new UnsupportedOperationException();
        }
        return array;
    }

    int protectedArrayOffset() {
        protectedArray();
        return this.offset;
    }

    boolean protectedHasArray() {
        return (this.isReadOnly || this.block.array() == null) ? false : true;
    }

    @Override // java.nio.ByteBuffer
    public final ByteBuffer get(byte[] dst, int dstOffset, int byteCount) {
        checkGetBounds(1, dst.length, dstOffset, byteCount);
        this.block.peekByteArray(this.offset + this.position, dst, dstOffset, byteCount);
        this.position += byteCount;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(char[] dst, int dstOffset, int charCount) {
        int byteCount = checkGetBounds(2, dst.length, dstOffset, charCount);
        this.block.peekCharArray(this.offset + this.position, dst, dstOffset, charCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(double[] dst, int dstOffset, int doubleCount) {
        int byteCount = checkGetBounds(8, dst.length, dstOffset, doubleCount);
        this.block.peekDoubleArray(this.offset + this.position, dst, dstOffset, doubleCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(float[] dst, int dstOffset, int floatCount) {
        int byteCount = checkGetBounds(4, dst.length, dstOffset, floatCount);
        this.block.peekFloatArray(this.offset + this.position, dst, dstOffset, floatCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(int[] dst, int dstOffset, int intCount) {
        int byteCount = checkGetBounds(4, dst.length, dstOffset, intCount);
        this.block.peekIntArray(this.offset + this.position, dst, dstOffset, intCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(long[] dst, int dstOffset, int longCount) {
        int byteCount = checkGetBounds(8, dst.length, dstOffset, longCount);
        this.block.peekLongArray(this.offset + this.position, dst, dstOffset, longCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void get(short[] dst, int dstOffset, int shortCount) {
        int byteCount = checkGetBounds(2, dst.length, dstOffset, shortCount);
        this.block.peekShortArray(this.offset + this.position, dst, dstOffset, shortCount, this.order.needsSwap);
        this.position += byteCount;
    }

    @Override // java.nio.ByteBuffer
    public final byte get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        MemoryBlock memoryBlock = this.block;
        int i = this.offset;
        int i2 = this.position;
        this.position = i2 + 1;
        return memoryBlock.peekByte(i + i2);
    }

    @Override // java.nio.ByteBuffer
    public final byte get(int index) {
        checkIndex(index);
        return this.block.peekByte(this.offset + index);
    }

    @Override // java.nio.ByteBuffer
    public final char getChar() {
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        char result = (char) this.block.peekShort(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final char getChar(int index) {
        checkIndex(index, 2);
        return (char) this.block.peekShort(this.offset + index, this.order);
    }

    @Override // java.nio.ByteBuffer
    public final double getDouble() {
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        double result = Double.longBitsToDouble(this.block.peekLong(this.offset + this.position, this.order));
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final double getDouble(int index) {
        checkIndex(index, 8);
        return Double.longBitsToDouble(this.block.peekLong(this.offset + index, this.order));
    }

    @Override // java.nio.ByteBuffer
    public final float getFloat() {
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        float result = Float.intBitsToFloat(this.block.peekInt(this.offset + this.position, this.order));
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final float getFloat(int index) {
        checkIndex(index, 4);
        return Float.intBitsToFloat(this.block.peekInt(this.offset + index, this.order));
    }

    @Override // java.nio.ByteBuffer
    public final int getInt() {
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        int result = this.block.peekInt(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final int getInt(int index) {
        checkIndex(index, 4);
        return this.block.peekInt(this.offset + index, this.order);
    }

    @Override // java.nio.ByteBuffer
    public final long getLong() {
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        long result = this.block.peekLong(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final long getLong(int index) {
        checkIndex(index, 8);
        return this.block.peekLong(this.offset + index, this.order);
    }

    @Override // java.nio.ByteBuffer
    public final short getShort() {
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        short result = this.block.peekShort(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    @Override // java.nio.ByteBuffer
    public final short getShort(int index) {
        checkIndex(index, 2);
        return this.block.peekShort(this.offset + index, this.order);
    }

    @Override // java.nio.ByteBuffer, java.nio.Buffer
    public final boolean isDirect() {
        return true;
    }

    public final void free() {
        this.block.free();
    }

    @Override // java.nio.ByteBuffer
    public final CharBuffer asCharBuffer() {
        return ByteBufferAsCharBuffer.asCharBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public final DoubleBuffer asDoubleBuffer() {
        return ByteBufferAsDoubleBuffer.asDoubleBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public final FloatBuffer asFloatBuffer() {
        return ByteBufferAsFloatBuffer.asFloatBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public final IntBuffer asIntBuffer() {
        return ByteBufferAsIntBuffer.asIntBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public final LongBuffer asLongBuffer() {
        return ByteBufferAsLongBuffer.asLongBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public final ShortBuffer asShortBuffer() {
        return ByteBufferAsShortBuffer.asShortBuffer(this);
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer put(byte value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        MemoryBlock memoryBlock = this.block;
        int i = this.offset;
        int i2 = this.position;
        this.position = i2 + 1;
        memoryBlock.pokeByte(i + i2, value);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer put(int index, byte value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.block.pokeByte(this.offset + index, value);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkPutBounds(1, src.length, srcOffset, byteCount);
        this.block.pokeByteArray(this.offset + this.position, src, srcOffset, byteCount);
        this.position += byteCount;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(char[] src, int srcOffset, int charCount) {
        int byteCount = checkPutBounds(2, src.length, srcOffset, charCount);
        this.block.pokeCharArray(this.offset + this.position, src, srcOffset, charCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(double[] src, int srcOffset, int doubleCount) {
        int byteCount = checkPutBounds(8, src.length, srcOffset, doubleCount);
        this.block.pokeDoubleArray(this.offset + this.position, src, srcOffset, doubleCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(float[] src, int srcOffset, int floatCount) {
        int byteCount = checkPutBounds(4, src.length, srcOffset, floatCount);
        this.block.pokeFloatArray(this.offset + this.position, src, srcOffset, floatCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(int[] src, int srcOffset, int intCount) {
        int byteCount = checkPutBounds(4, src.length, srcOffset, intCount);
        this.block.pokeIntArray(this.offset + this.position, src, srcOffset, intCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(long[] src, int srcOffset, int longCount) {
        int byteCount = checkPutBounds(8, src.length, srcOffset, longCount);
        this.block.pokeLongArray(this.offset + this.position, src, srcOffset, longCount, this.order.needsSwap);
        this.position += byteCount;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void put(short[] src, int srcOffset, int shortCount) {
        int byteCount = checkPutBounds(2, src.length, srcOffset, shortCount);
        this.block.pokeShortArray(this.offset + this.position, src, srcOffset, shortCount, this.order.needsSwap);
        this.position += byteCount;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putChar(char value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(this.offset + this.position, (short) value, this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putChar(int index, char value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        this.block.pokeShort(this.offset + index, (short) value, this.order);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putDouble(double value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(this.offset + this.position, Double.doubleToRawLongBits(value), this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putDouble(int index, double value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 8);
        this.block.pokeLong(this.offset + index, Double.doubleToRawLongBits(value), this.order);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putFloat(float value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(this.offset + this.position, Float.floatToRawIntBits(value), this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putFloat(int index, float value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 4);
        this.block.pokeInt(this.offset + index, Float.floatToRawIntBits(value), this.order);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putInt(int value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putInt(int index, int value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 4);
        this.block.pokeInt(this.offset + index, value, this.order);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putLong(long value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putLong(int index, long value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 8);
        this.block.pokeLong(this.offset + index, value, this.order);
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putShort(short value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    @Override // java.nio.ByteBuffer
    public ByteBuffer putShort(int index, short value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        this.block.pokeShort(this.offset + index, value, this.order);
        return this;
    }
}