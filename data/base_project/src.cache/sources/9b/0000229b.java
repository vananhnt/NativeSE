package java.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InvalidClassException.class */
public class InvalidClassException extends ObjectStreamException {
    public String classname;

    public InvalidClassException(String detailMessage) {
        throw new RuntimeException("Stub!");
    }

    public InvalidClassException(String className, String detailMessage) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        throw new RuntimeException("Stub!");
    }
}