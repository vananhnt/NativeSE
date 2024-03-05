package libcore.io;

/* loaded from: NioBufferIterator.class */
public final class NioBufferIterator extends BufferIterator {
    private final long address;
    private final int size;
    private final boolean swap;
    private int position;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NioBufferIterator(long address, int size, boolean swap) {
        this.address = address;
        this.size = size;
        this.swap = swap;
    }

    @Override // libcore.io.BufferIterator
    public void seek(int offset) {
        this.position = offset;
    }

    @Override // libcore.io.BufferIterator
    public void skip(int byteCount) {
        this.position += byteCount;
    }

    @Override // libcore.io.BufferIterator
    public void readByteArray(byte[] dst, int dstOffset, int byteCount) {
        Memory.peekByteArray(this.address + this.position, dst, dstOffset, byteCount);
        this.position += byteCount;
    }

    @Override // libcore.io.BufferIterator
    public byte readByte() {
        byte result = Memory.peekByte(this.address + this.position);
        this.position++;
        return result;
    }

    @Override // libcore.io.BufferIterator
    public int readInt() {
        int result = Memory.peekInt(this.address + this.position, this.swap);
        this.position += 4;
        return result;
    }

    @Override // libcore.io.BufferIterator
    public void readIntArray(int[] dst, int dstOffset, int intCount) {
        Memory.peekIntArray(this.address + this.position, dst, dstOffset, intCount, this.swap);
        this.position += 4 * intCount;
    }

    @Override // libcore.io.BufferIterator
    public short readShort() {
        short result = Memory.peekShort(this.address + this.position, this.swap);
        this.position += 2;
        return result;
    }
}