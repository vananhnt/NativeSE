package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DataInputStream.class */
public class DataInputStream extends FilterInputStream implements DataInput {
    public DataInputStream(InputStream in) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public final int read(byte[] buffer) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read(byte[] buffer, int offset, int length) throws IOException {
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
    @Deprecated
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

    public static final String readUTF(DataInput in) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public final int skipBytes(int count) throws IOException {
        throw new RuntimeException("Stub!");
    }
}