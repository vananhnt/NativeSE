package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ObjectInput.class */
public interface ObjectInput extends DataInput {
    int available() throws IOException;

    void close() throws IOException;

    int read() throws IOException;

    int read(byte[] bArr) throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;

    Object readObject() throws ClassNotFoundException, IOException;

    long skip(long j) throws IOException;
}