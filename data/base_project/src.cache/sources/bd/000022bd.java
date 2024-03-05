package java.io;

import java.nio.channels.FileChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RandomAccessFile.class */
public class RandomAccessFile implements DataInput, DataOutput, Closeable {
    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public RandomAccessFile(String fileName, String mode) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    public final synchronized FileChannel getChannel() {
        throw new RuntimeException("Stub!");
    }

    public final FileDescriptor getFD() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long getFilePointer() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long length() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final boolean readBoolean() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final byte readByte() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final char readChar() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final double readDouble() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final float readFloat() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] dst) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final void readFully(byte[] dst, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final int readInt() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final String readLine() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final long readLong() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final short readShort() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final int readUnsignedByte() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final int readUnsignedShort() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final String readUTF() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void seek(long offset) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setLength(long newLength) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public int skipBytes(int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void write(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void write(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeBoolean(boolean val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeByte(int val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeBytes(String str) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeChar(int val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeChars(String str) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeDouble(double val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeFloat(float val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeInt(int val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeLong(long val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeShort(int val) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public final void writeUTF(String str) throws IOException {
        throw new RuntimeException("Stub!");
    }
}