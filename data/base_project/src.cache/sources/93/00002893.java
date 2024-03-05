package javax.xml.transform;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TransformerException.class */
public class TransformerException extends Exception {
    private static final long serialVersionUID = 975798773772956428L;
    SourceLocator locator;
    Throwable containedException;

    public SourceLocator getLocator() {
        return this.locator;
    }

    public void setLocator(SourceLocator location) {
        this.locator = location;
    }

    public Throwable getException() {
        return this.containedException;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        if (this.containedException == this) {
            return null;
        }
        return this.containedException;
    }

    @Override // java.lang.Throwable
    public synchronized Throwable initCause(Throwable cause) {
        if (this.containedException != null) {
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (cause == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.containedException = cause;
        return this;
    }

    public TransformerException(String message) {
        super(message);
        this.containedException = null;
        this.locator = null;
    }

    public TransformerException(Throwable e) {
        super(e.toString());
        this.containedException = e;
        this.locator = null;
    }

    public TransformerException(String message, Throwable e) {
        super((message == null || message.length() == 0) ? e.toString() : message);
        this.containedException = e;
        this.locator = null;
    }

    public TransformerException(String message, SourceLocator locator) {
        super(message);
        this.containedException = null;
        this.locator = locator;
    }

    public TransformerException(String message, SourceLocator locator, Throwable e) {
        super(message);
        this.containedException = e;
        this.locator = locator;
    }

    public String getMessageAndLocation() {
        StringBuilder sbuffer = new StringBuilder();
        String message = super.getMessage();
        if (null != message) {
            sbuffer.append(message);
        }
        if (null != this.locator) {
            String systemID = this.locator.getSystemId();
            int line = this.locator.getLineNumber();
            int column = this.locator.getColumnNumber();
            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }
            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }
            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }
        }
        return sbuffer.toString();
    }

    public String getLocationAsString() {
        if (null != this.locator) {
            StringBuilder sbuffer = new StringBuilder();
            String systemID = this.locator.getSystemId();
            int line = this.locator.getLineNumber();
            int column = this.locator.getColumnNumber();
            if (null != systemID) {
                sbuffer.append("; SystemID: ");
                sbuffer.append(systemID);
            }
            if (0 != line) {
                sbuffer.append("; Line#: ");
                sbuffer.append(line);
            }
            if (0 != column) {
                sbuffer.append("; Column#: ");
                sbuffer.append(column);
            }
            return sbuffer.toString();
        }
        return null;
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(new PrintWriter((OutputStream) System.err, true));
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream s) {
        printStackTrace(new PrintWriter(s));
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter s) {
        if (s == null) {
            s = new PrintWriter((OutputStream) System.err, true);
        }
        try {
            String locInfo = getLocationAsString();
            if (null != locInfo) {
                s.println(locInfo);
            }
            super.printStackTrace(s);
        } catch (Throwable th) {
        }
    }
}