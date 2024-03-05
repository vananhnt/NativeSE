package java.util.logging;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Formatter.class */
public abstract class Formatter {
    public abstract String format(LogRecord logRecord);

    /* JADX INFO: Access modifiers changed from: protected */
    public Formatter() {
        throw new RuntimeException("Stub!");
    }

    public String formatMessage(LogRecord r) {
        throw new RuntimeException("Stub!");
    }

    public String getHead(Handler h) {
        throw new RuntimeException("Stub!");
    }

    public String getTail(Handler h) {
        throw new RuntimeException("Stub!");
    }
}