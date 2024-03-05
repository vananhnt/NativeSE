package javax.security.cert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateFactory;
import java.util.Date;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509Certificate.class */
public abstract class X509Certificate extends Certificate {
    private static Constructor constructor;

    public abstract void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract int getVersion();

    public abstract BigInteger getSerialNumber();

    public abstract Principal getIssuerDN();

    public abstract Principal getSubjectDN();

    public abstract Date getNotBefore();

    public abstract Date getNotAfter();

    public abstract String getSigAlgName();

    public abstract String getSigAlgOID();

    public abstract byte[] getSigAlgParams();

    static {
        try {
            String classname = Security.getProperty("cert.provider.x509v1");
            Class cl = Class.forName(classname);
            constructor = cl.getConstructor(InputStream.class);
        } catch (Throwable th) {
        }
    }

    public static final X509Certificate getInstance(InputStream inStream) throws CertificateException {
        if (inStream == null) {
            throw new CertificateException("inStream == null");
        }
        if (constructor != null) {
            try {
                return (X509Certificate) constructor.newInstance(inStream);
            } catch (Throwable e) {
                throw new CertificateException(e.getMessage());
            }
        }
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) cf.generateCertificate(inStream);
            return new X509Certificate() { // from class: javax.security.cert.X509Certificate.1
                @Override // javax.security.cert.Certificate
                public byte[] getEncoded() throws CertificateEncodingException {
                    try {
                        return java.security.cert.X509Certificate.this.getEncoded();
                    } catch (java.security.cert.CertificateEncodingException e2) {
                        throw new CertificateEncodingException(e2.getMessage());
                    }
                }

                @Override // javax.security.cert.Certificate
                public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
                    try {
                        java.security.cert.X509Certificate.this.verify(key);
                    } catch (java.security.cert.CertificateException e2) {
                        throw new CertificateException(e2.getMessage());
                    }
                }

                @Override // javax.security.cert.Certificate
                public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
                    try {
                        java.security.cert.X509Certificate.this.verify(key, sigProvider);
                    } catch (java.security.cert.CertificateException e2) {
                        throw new CertificateException(e2.getMessage());
                    }
                }

                @Override // javax.security.cert.Certificate
                public String toString() {
                    return java.security.cert.X509Certificate.this.toString();
                }

                @Override // javax.security.cert.Certificate
                public PublicKey getPublicKey() {
                    return java.security.cert.X509Certificate.this.getPublicKey();
                }

                @Override // javax.security.cert.X509Certificate
                public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
                    try {
                        java.security.cert.X509Certificate.this.checkValidity();
                    } catch (java.security.cert.CertificateExpiredException e2) {
                        throw new CertificateExpiredException(e2.getMessage());
                    } catch (java.security.cert.CertificateNotYetValidException e3) {
                        throw new CertificateNotYetValidException(e3.getMessage());
                    }
                }

                @Override // javax.security.cert.X509Certificate
                public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
                    try {
                        java.security.cert.X509Certificate.this.checkValidity(date);
                    } catch (java.security.cert.CertificateExpiredException e2) {
                        throw new CertificateExpiredException(e2.getMessage());
                    } catch (java.security.cert.CertificateNotYetValidException e3) {
                        throw new CertificateNotYetValidException(e3.getMessage());
                    }
                }

                @Override // javax.security.cert.X509Certificate
                public int getVersion() {
                    return 2;
                }

                @Override // javax.security.cert.X509Certificate
                public BigInteger getSerialNumber() {
                    return java.security.cert.X509Certificate.this.getSerialNumber();
                }

                @Override // javax.security.cert.X509Certificate
                public Principal getIssuerDN() {
                    return java.security.cert.X509Certificate.this.getIssuerDN();
                }

                @Override // javax.security.cert.X509Certificate
                public Principal getSubjectDN() {
                    return java.security.cert.X509Certificate.this.getSubjectDN();
                }

                @Override // javax.security.cert.X509Certificate
                public Date getNotBefore() {
                    return java.security.cert.X509Certificate.this.getNotBefore();
                }

                @Override // javax.security.cert.X509Certificate
                public Date getNotAfter() {
                    return java.security.cert.X509Certificate.this.getNotAfter();
                }

                @Override // javax.security.cert.X509Certificate
                public String getSigAlgName() {
                    return java.security.cert.X509Certificate.this.getSigAlgName();
                }

                @Override // javax.security.cert.X509Certificate
                public String getSigAlgOID() {
                    return java.security.cert.X509Certificate.this.getSigAlgOID();
                }

                @Override // javax.security.cert.X509Certificate
                public byte[] getSigAlgParams() {
                    return java.security.cert.X509Certificate.this.getSigAlgParams();
                }
            };
        } catch (java.security.cert.CertificateException e2) {
            throw new CertificateException(e2.getMessage());
        }
    }

    public static final X509Certificate getInstance(byte[] certData) throws CertificateException {
        if (certData == null) {
            throw new CertificateException("certData == null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(certData);
        return getInstance(bais);
    }
}