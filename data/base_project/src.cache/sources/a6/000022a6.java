package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ObjectOutput.class */
public interface ObjectOutput extends DataOutput {
    void close() throws IOException;

    void flush() throws IOException;

    @Override // java.io.DataOutput
    void write(byte[] bArr) throws IOException;

    @Override // java.io.DataOutput
    void write(byte[] bArr, int i, int i2) throws IOException;

    @Override // java.io.DataOutput
    void write(int i) throws IOException;

    void writeObject(Object obj) throws IOException;
}