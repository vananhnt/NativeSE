package javax.xml.xpath;

import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPathException.class */
public class XPathException extends Exception {
    private final Throwable cause;
    private static final long serialVersionUID = -1837080260374986980L;

    public XPathException(String message) {
        super(message);
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        this.cause = null;
    }

    public XPathException(Throwable cause) {
        super(cause == null ? null : cause.toString());
        this.cause = cause;
        if (cause == null) {
            throw new NullPointerException("cause == null");
        }
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream s) {
        if (getCause() != null) {
            getCause().printStackTrace(s);
            s.println("--------------- linked to ------------------");
        }
        super.printStackTrace(s);
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter s) {
        if (getCause() != null) {
            getCause().printStackTrace(s);
            s.println("--------------- linked to ------------------");
        }
        super.printStackTrace(s);
    }
}