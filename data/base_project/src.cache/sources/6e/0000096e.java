package android.net.http;

import javax.net.ssl.SSLException;

/* compiled from: HttpsConnection.java */
/* loaded from: SSLConnectionClosedByUserException.class */
class SSLConnectionClosedByUserException extends SSLException {
    public SSLConnectionClosedByUserException(String reason) {
        super(reason);
    }
}