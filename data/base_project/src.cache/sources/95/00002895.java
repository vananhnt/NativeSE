package javax.xml.transform;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TransformerFactoryConfigurationError.class */
public class TransformerFactoryConfigurationError extends Error {
    private Exception exception;

    public TransformerFactoryConfigurationError() {
        this.exception = null;
    }

    public TransformerFactoryConfigurationError(String msg) {
        super(msg);
        this.exception = null;
    }

    public TransformerFactoryConfigurationError(Exception e) {
        super(e.toString());
        this.exception = e;
    }

    public TransformerFactoryConfigurationError(Exception e, String msg) {
        super(msg);
        this.exception = e;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String message = super.getMessage();
        if (message == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return message;
    }

    public Exception getException() {
        return this.exception;
    }
}