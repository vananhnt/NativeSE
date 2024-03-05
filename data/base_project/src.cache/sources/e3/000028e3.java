package libcore.io;

import gov.nist.core.Separators;
import java.io.IOException;
import java.net.SocketException;

/* loaded from: ErrnoException.class */
public final class ErrnoException extends Exception {
    private final String functionName;
    public final int errno;

    public ErrnoException(String functionName, int errno) {
        this.functionName = functionName;
        this.errno = errno;
    }

    public ErrnoException(String functionName, int errno, Throwable cause) {
        super(cause);
        this.functionName = functionName;
        this.errno = errno;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String errnoName = OsConstants.errnoName(this.errno);
        if (errnoName == null) {
            errnoName = "errno " + this.errno;
        }
        String description = Libcore.os.strerror(this.errno);
        return this.functionName + " failed: " + errnoName + " (" + description + Separators.RPAREN;
    }

    public IOException rethrowAsIOException() throws IOException {
        IOException newException = new IOException(getMessage());
        newException.initCause(this);
        throw newException;
    }

    public SocketException rethrowAsSocketException() throws SocketException {
        throw new SocketException(getMessage(), this);
    }
}