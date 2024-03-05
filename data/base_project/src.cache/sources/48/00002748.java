package java.util.logging;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StreamHandler.class */
public class StreamHandler extends Handler {
    public StreamHandler() {
        throw new RuntimeException("Stub!");
    }

    public StreamHandler(OutputStream os, Formatter formatter) {
        throw new RuntimeException("Stub!");
    }

    protected void setOutputStream(OutputStream os) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.Handler
    public void setEncoding(String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.Handler
    public void close() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.Handler
    public void flush() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.Handler
    public synchronized void publish(LogRecord record) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.logging.Handler
    public boolean isLoggable(LogRecord record) {
        throw new RuntimeException("Stub!");
    }
}