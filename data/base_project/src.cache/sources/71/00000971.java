package android.net.http;

import java.security.cert.X509Certificate;

/* loaded from: SslError.class */
public class SslError {
    public static final int SSL_NOTYETVALID = 0;
    public static final int SSL_EXPIRED = 1;
    public static final int SSL_IDMISMATCH = 2;
    public static final int SSL_UNTRUSTED = 3;
    public static final int SSL_DATE_INVALID = 4;
    public static final int SSL_INVALID = 5;
    @Deprecated
    public static final int SSL_MAX_ERROR = 6;
    int mErrors;
    final SslCertificate mCertificate;
    final String mUrl;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !SslError.class.desiredAssertionStatus();
    }

    @Deprecated
    public SslError(int error, SslCertificate certificate) {
        this(error, certificate, "");
    }

    @Deprecated
    public SslError(int error, X509Certificate certificate) {
        this(error, certificate, "");
    }

    public SslError(int error, SslCertificate certificate, String url) {
        if (!$assertionsDisabled && certificate == null) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && url == null) {
            throw new AssertionError();
        }
        addError(error);
        this.mCertificate = certificate;
        this.mUrl = url;
    }

    public SslError(int error, X509Certificate certificate, String url) {
        this(error, new SslCertificate(certificate), url);
    }

    public static SslError SslErrorFromChromiumErrorCode(int error, SslCertificate cert, String url) {
        if ($assertionsDisabled || (error >= -299 && error <= -200)) {
            if (error == -200) {
                return new SslError(2, cert, url);
            }
            if (error == -201) {
                return new SslError(4, cert, url);
            }
            if (error == -202) {
                return new SslError(3, cert, url);
            }
            return new SslError(5, cert, url);
        }
        throw new AssertionError();
    }

    public SslCertificate getCertificate() {
        return this.mCertificate;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public boolean addError(int error) {
        boolean rval = 0 <= error && error < 6;
        if (rval) {
            this.mErrors |= 1 << error;
        }
        return rval;
    }

    public boolean hasError(int error) {
        boolean rval = 0 <= error && error < 6;
        if (rval) {
            rval = (this.mErrors & (1 << error)) != 0;
        }
        return rval;
    }

    public int getPrimaryError() {
        if (this.mErrors != 0) {
            for (int error = 5; error >= 0; error--) {
                if ((this.mErrors & (1 << error)) != 0) {
                    return error;
                }
            }
            if ($assertionsDisabled) {
                return -1;
            }
            throw new AssertionError();
        }
        return -1;
    }

    public String toString() {
        return "primary error: " + getPrimaryError() + " certificate: " + getCertificate() + " on URL: " + getUrl();
    }
}