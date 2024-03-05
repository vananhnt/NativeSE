package java.util.logging;

import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileHandler.class */
public class FileHandler extends StreamHandler {
    public FileHandler() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileHandler(String pattern) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileHandler(String pattern, boolean append) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileHandler(String pattern, int limit, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileHandler(String pattern, int limit, int count, boolean append) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.StreamHandler, java.util.logging.Handler
    public void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.StreamHandler, java.util.logging.Handler
    public synchronized void publish(LogRecord record) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: FileHandler$MeasureOutputStream.class */
    static class MeasureOutputStream extends OutputStream {
        OutputStream wrapped;
        long length;

        public MeasureOutputStream(OutputStream stream, long currentLength) {
            this.wrapped = stream;
            this.length = currentLength;
        }

        public MeasureOutputStream(OutputStream stream) {
            this(stream, 0L);
        }

        @Override // java.io.OutputStream
        public void write(int oneByte) throws IOException {
            this.wrapped.write(oneByte);
            this.length++;
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.wrapped.write(b, off, len);
            this.length += len;
        }

        @Override // java.io.OutputStream, java.io.Closeable
        public void close() throws IOException {
            this.wrapped.close();
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            this.wrapped.flush();
        }

        public long getLength() {
            return this.length;
        }

        public void setLength(long newLength) {
            this.length = newLength;
        }
    }
}