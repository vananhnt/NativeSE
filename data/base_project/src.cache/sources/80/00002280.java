package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DataOutputStream.class */
public class DataOutputStream extends FilterOutputStream implements DataOutput {
    protected int written;

    public DataOutputStream(OutputStream out) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
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