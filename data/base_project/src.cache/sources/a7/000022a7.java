package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ObjectOutputStream.class */
public class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ObjectOutputStream$PutField.class */
    public static abstract class PutField {
        public abstract void put(String str, boolean z);

        public abstract void put(String str, char c);

        public abstract void put(String str, byte b);

        public abstract void put(String str, short s);

        public abstract void put(String str, int i);

        public abstract void put(String str, long j);

        public abstract void put(String str, float f);

        public abstract void put(String str, double d);

        public abstract void put(String str, Object obj);

        @Deprecated
        public abstract void write(ObjectOutput objectOutput) throws IOException;

        public PutField() {
            throw new RuntimeException("Stub!");
        }
    }

    protected ObjectOutputStream() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ObjectOutputStream(OutputStream output) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void annotateClass(Class<?> aClass) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void annotateProxyClass(Class<?> aClass) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void defaultWriteObject() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void drain() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected boolean enableReplaceObject(boolean enable) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public PutField putFields() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected Object replaceObject(Object object) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void reset() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void useProtocolVersion(int version) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(int value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeBoolean(boolean value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeByte(int value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeBytes(String value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeChar(int value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeChars(String value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeDouble(double value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void writeFields() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeFloat(float value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeInt(int value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeLong(long value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void writeClassDescriptor(ObjectStreamClass classDesc) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.ObjectOutput
    public final void writeObject(Object object) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void writeUnshared(Object object) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void writeObjectOverride(Object object) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeShort(int value) throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void writeStreamHeader() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataOutput
    public void writeUTF(String value) throws IOException {
        throw new RuntimeException("Stub!");
    }
}