package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: OutputStreamWriter.class */
public class OutputStreamWriter extends Writer {
    public OutputStreamWriter(OutputStream out) {
        throw new RuntimeException("Stub!");
    }

    public OutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public OutputStreamWriter(OutputStream out, Charset cs) {
        throw new RuntimeException("Stub!");
    }

    public OutputStreamWriter(OutputStream out, CharsetEncoder charsetEncoder) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getEncoding() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(char[] buffer, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(int oneChar) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Writer
    public void write(String str, int offset, int count) throws IOException {
        throw new RuntimeException("Stub!");
    }
}