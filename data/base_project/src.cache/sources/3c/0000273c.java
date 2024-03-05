package java.util.logging;

import java.io.UnsupportedEncodingException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Handler.class */
public abstract class Handler {
    public abstract void close();

    public abstract void flush();

    public abstract void publish(LogRecord logRecord);

    /* JADX INFO: Access modifiers changed from: protected */
    public Handler() {
        throw new RuntimeException("Stub!");
    }

    public String getEncoding() {
        throw new RuntimeException("Stub!");
    }

    public ErrorManager getErrorManager() {
        throw new RuntimeException("Stub!");
    }

    public Filter getFilter() {
        throw new RuntimeException("Stub!");
    }

    public Formatter getFormatter() {
        throw new RuntimeException("Stub!");
    }

    public Level getLevel() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLoggable(LogRecord record) {
        throw new RuntimeException("Stub!");
    }

    protected void reportError(String msg, Exception ex, int code) {
        throw new RuntimeException("Stub!");
    }

    public void setEncoding(String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public void setErrorManager(ErrorManager newErrorManager) {
        throw new RuntimeException("Stub!");
    }

    public void setFilter(Filter newFilter) {
        throw new RuntimeException("Stub!");
    }

    public void setFormatter(Formatter newFormatter) {
        throw new RuntimeException("Stub!");
    }

    public void setLevel(Level newLevel) {
        throw new RuntimeException("Stub!");
    }
}