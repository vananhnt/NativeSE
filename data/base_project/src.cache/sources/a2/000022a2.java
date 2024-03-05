package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ObjectInputStream.class */
public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: ObjectInputStream$GetField.class */
    public static abstract class GetField {
        public abstract ObjectStreamClass getObjectStreamClass();

        public abstract boolean defaulted(String str) throws IOException, IllegalArgumentException;

        public abstract boolean get(String str, boolean z) throws IOException, IllegalArgumentException;

        public abstract char get(String str, char c) throws IOException, IllegalArgumentException;

        public abstract byte get(String str, byte b) throws IOException, IllegalArgumentException;

        public abstract short get(String str, short s) throws IOException, IllegalArgumentException;

        public abstract int get(String str, int i) throws IOException, IllegalArgumentException;

        public abstract long get(String str, long j) throws IOException, IllegalArgumentException;

        public abstract float get(String str, float f) throws IOException, IllegalArgumentException;

        public abstract double get(String str, double d) throws IOException, IllegalArgumentException;

        public abstract Object get(String str, Object obj) throws IOException, IllegalArgumentException;

        public GetField() {
            throw new RuntimeException("Stub!");
        }
    }

    protected ObjectInputStream() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public ObjectInputStream(InputStream input) throws StreamCorruptedException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void defaultReadObject() throws IOException, ClassNotFoundException, NotActiveException {
        throw new RuntimeException("Stub!");
    }

    protected boolean enableResolveObject(boolean enable) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public boolean readBoolean() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public byte readByte() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public char readChar() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public double readDouble() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public GetField readFields() throws IOException, ClassNotFoundException, NotActiveException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public float readFloat() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public void readFully(byte[] dst) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public void readFully(byte[] dst, int offset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public int readInt() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    @Deprecated
    public String readLine() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public long readLong() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    protected Class<?> resolveProxyClass(String[] interfaceNames) throws IOException, ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.ObjectInput
    public final Object readObject() throws OptionalDataException, ClassNotFoundException, IOException {
        throw new RuntimeException("Stub!");
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    protected Object readObjectOverride() throws OptionalDataException, ClassNotFoundException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public short readShort() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void readStreamHeader() throws IOException, StreamCorruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public int readUnsignedByte() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public int readUnsignedShort() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public String readUTF() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void registerValidation(ObjectInputValidation object, int priority) throws NotActiveException, InvalidObjectException {
        throw new RuntimeException("Stub!");
    }

    protected Class<?> resolveClass(ObjectStreamClass osClass) throws IOException, ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    protected Object resolveObject(Object object) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.DataInput
    public int skipBytes(int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ObjectInputStream$InputValidationDesc.class */
    static class InputValidationDesc {
        ObjectInputValidation validator;
        int priority;

        InputValidationDesc() {
        }
    }
}