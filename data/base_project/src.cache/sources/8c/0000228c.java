package java.io;

import java.nio.channels.FileChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileOutputStream.class */
public class FileOutputStream extends OutputStream implements Closeable {
    public FileOutputStream(File file) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public FileOutputStream(File file, boolean append) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public FileOutputStream(FileDescriptor fd) {
        throw new RuntimeException("Stub!");
    }

    public FileOutputStream(String path) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public FileOutputStream(String path, boolean append) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream, java.io.Closeable
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

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        throw new RuntimeException("Stub!");
    }
}