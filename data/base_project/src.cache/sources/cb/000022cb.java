package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: WriteAbortedException.class */
public class WriteAbortedException extends ObjectStreamException {
    public Exception detail;

    public WriteAbortedException(String detailMessage, Exception rootCause) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        throw new RuntimeException("Stub!");
    }
}