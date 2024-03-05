package libcore.io;

import gov.nist.core.Separators;
import java.net.UnknownHostException;

/* loaded from: GaiException.class */
public final class GaiException extends RuntimeException {
    private final String functionName;
    public final int error;

    public GaiException(String functionName, int error) {
        this.functionName = functionName;
        this.error = error;
    }

    public GaiException(String functionName, int error, Throwable cause) {
        super(cause);
        this.functionName = functionName;
        this.error = error;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String gaiName = OsConstants.gaiName(this.error);
        if (gaiName == null) {
            gaiName = "GAI_ error " + this.error;
        }
        String description = Libcore.os.gai_strerror(this.error);
        return this.functionName + " failed: " + gaiName + " (" + description + Separators.RPAREN;
    }

    public UnknownHostException rethrowAsUnknownHostException(String detailMessage) throws UnknownHostException {
        UnknownHostException newException = new UnknownHostException(detailMessage);
        newException.initCause(this);
        throw newException;
    }

    public UnknownHostException rethrowAsUnknownHostException() throws UnknownHostException {
        throw rethrowAsUnknownHostException(getMessage());
    }
}