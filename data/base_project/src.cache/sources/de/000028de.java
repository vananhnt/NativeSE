package libcore.io;

/* loaded from: BufferIterator.class */
public abstract class BufferIterator {
    public abstract void seek(int i);

    public abstract void skip(int i);

    public abstract void readByteArray(byte[] bArr, int i, int i2);

    public abstract byte readByte();

    public abstract int readInt();

    public abstract void readIntArray(int[] iArr, int i, int i2);

    public abstract short readShort();
}