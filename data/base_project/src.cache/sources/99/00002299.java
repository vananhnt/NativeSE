package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InputStreamReader.class */
public class InputStreamReader extends Reader {
    public InputStreamReader(InputStream in) {
        throw new RuntimeException("Stub!");
    }

    public InputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        throw new RuntimeException("Stub!");
    }

    public InputStreamReader(InputStream in, Charset charset) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader, java.io.Closeable
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public String getEncoding() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public int read(char[] buffer, int offset, int length) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        throw new RuntimeException("Stub!");
    }
}