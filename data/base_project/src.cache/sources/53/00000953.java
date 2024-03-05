package android.net.http;

import com.android.org.conscrypt.SSLParametersImpl;
import com.android.org.conscrypt.TrustManagerImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.DefaultHostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/* loaded from: CertificateChainValidator.class */
public class CertificateChainValidator {
    private static final CertificateChainValidator sInstance = new CertificateChainValidator();
    private static final DefaultHostnameVerifier sVerifier = new DefaultHostnameVerifier();

    public static CertificateChainValidator getInstance() {
        return sInstance;
    }

    private CertificateChainValidator() {
    }

    public SslError doHandshakeAndValidateServerCertificates(HttpsConnection connection, SSLSocket sslSocket, String domain) throws IOException {
        SSLSession sslSession = sslSocket.getSession();
        if (!sslSession.isValid()) {
            closeSocketThrowException(sslSocket, "failed to perform SSL handshake");
        }
        Certificate[] peerCertificates = sslSocket.getSession().getPeerCertificates();
        if (peerCertificates == null || peerCertificates.length == 0) {
            closeSocketThrowException(sslSocket, "failed to retrieve peer certificates");
        } else if (connection != null && peerCertificates[0] != null) {
            connection.setCertificate(new SslCertificate((X509Certificate) peerCertificates[0]));
        }
        return verifyServerDomainAndCertificates((X509Certificate[]) peerCertificates, domain, "RSA");
    }

    public static SslError verifyServerCertificates(byte[][] certChain, String domain, String authType) throws IOException {
        if (certChain == null || certChain.length == 0) {
            throw new IllegalArgumentException("bad certificate chain");
        }
        X509Certificate[] serverCertificates = new X509Certificate[certChain.length];
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < certChain.length; i++) {
                serverCertificates[i] = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certChain[i]));
            }
            return verifyServerDomainAndCertificates(serverCertificates, domain, authType);
        } catch (CertificateException e) {
            throw new IOException("can't read certificate", e);
        }
    }

    public static void handleTrustStorageUpdate() {
        try {
            TrustManagerImpl defaultTrustManager = SSLParametersImpl.getDefaultTrustManager();
            if (defaultTrustManager instanceof TrustManagerImpl) {
                TrustManagerImpl trustManager = defaultTrustManager;
                trustManager.handleTrustStorageUpdate();
            }
        } catch (KeyManagementException e) {
        }
    }

    private static SslError verifyServerDomainAndCertificates(X509Certificate[] chain, String domain, String authType) throws IOException {
        X509Certificate currCertificate = chain[0];
        if (currCertificate == null) {
            throw new IllegalArgumentException("certificate for this site is null");
        }
        boolean valid = (domain == null || domain.isEmpty() || !sVerifier.verify(domain, currCertificate)) ? false : true;
        if (!valid) {
            return new SslError(2, currCertificate);
        }
        try {
            TrustManagerImpl defaultTrustManager = SSLParametersImpl.getDefaultTrustManager();
            if (defaultTrustManager instanceof TrustManagerImpl) {
                TrustManagerImpl trustManager = defaultTrustManager;
                trustManager.checkServerTrusted(chain, authType, domain);
                return null;
            }
            defaultTrustManager.checkServerTrusted(chain, authType);
            return null;
        } catch (GeneralSecurityException e) {
            return new SslError(3, currCertificate);
        }
    }

    private void closeSocketThrowException(SSLSocket socket, String errorMessage, String defaultErrorMessage) throws IOException {
        closeSocketThrowException(socket, errorMessage != null ? errorMessage : defaultErrorMessage);
    }

    private void closeSocketThrowException(SSLSocket socket, String errorMessage) throws IOException {
        if (socket != null) {
            SSLSession session = socket.getSession();
            if (session != null) {
                session.invalidate();
            }
            socket.close();
        }
        throw new SSLHandshakeException(errorMessage);
    }
}