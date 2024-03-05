package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DataOutput.class */
public interface DataOutput {
    void write(byte[] bArr) throws IOException;

    void write(byte[] bArr, int i, int i2) throws IOException;

    void write(int i) throws IOException;

    void writeBoolean(boolean z) throws IOException;

    void writeByte(int i) throws IOException;

    void writeBytes(String str) throws IOException;

    void writeChar(int i) throws IOException;

    void writeChars(String str) throws IOException;

    void writeDouble(double d) throws IOException;

    void writeFloat(float f) throws IOException;

    void writeInt(int i) throws IOException;

    void writeLong(long j) throws IOException;

    void writeShort(int i) throws IOException;

    void writeUTF(String str) throws IOException;
}