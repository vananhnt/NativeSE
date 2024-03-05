package java.io;

import java.nio.channels.FileChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileInputStream.class */
public class FileInputStream extends InputStream implements Closeable {
    public FileInputStream(File file) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public FileInputStream(FileDescriptor fd) {
        throw new RuntimeException("Stub!");
    }

    public FileInputStream(String path) throws FileNotFoundException {
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

    protected void finalize() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileChannel getChannel() {
        throw new RuntimeException("Stub!");
    }

    public final FileDescriptor getFD() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.InputStream
    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}