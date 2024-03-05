package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DataInput.class */
public interface DataInput {
    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    char readChar() throws IOException;

    double readDouble() throws IOException;

    float readFloat() throws IOException;

    void readFully(byte[] bArr) throws IOException;

    void readFully(byte[] bArr, int i, int i2) throws IOException;

    int readInt() throws IOException;

    String readLine() throws IOException;

    long readLong() throws IOException;

    short readShort() throws IOException;

    int readUnsignedByte() throws IOException;

    int readUnsignedShort() throws IOException;

    String readUTF() throws IOException;

    int skipBytes(int i) throws IOException;
}