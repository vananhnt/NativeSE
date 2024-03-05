package android.net.http;

import com.android.org.conscrypt.TrustManagerImpl;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.X509TrustManager;

/* loaded from: X509TrustManagerExtensions.class */
public class X509TrustManagerExtensions {
    TrustManagerImpl mDelegate;

    public X509TrustManagerExtensions(X509TrustManager tm) throws IllegalArgumentException {
        if (tm instanceof TrustManagerImpl) {
            this.mDelegate = (TrustManagerImpl) tm;
            return;
        }
        throw new IllegalArgumentException("tm is not a supported type of X509TrustManager");
    }

    public List<X509Certificate> checkServerTrusted(X509Certificate[] chain, String authType, String host) throws CertificateException {
        return this.mDelegate.checkServerTrusted(chain, authType, host);
    }
}