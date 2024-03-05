package org.apache.commons.logging;

import gov.nist.core.Separators;

/* loaded from: LogConfigurationException.class */
public class LogConfigurationException extends RuntimeException {
    protected Throwable cause;

    public LogConfigurationException() {
        this.cause = null;
    }

    public LogConfigurationException(String message) {
        super(message);
        this.cause = null;
    }

    public LogConfigurationException(Throwable cause) {
        this(cause == null ? null : cause.toString(), cause);
    }

    public LogConfigurationException(String message, Throwable cause) {
        super(message + " (Caused by " + cause + Separators.RPAREN);
        this.cause = null;
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}