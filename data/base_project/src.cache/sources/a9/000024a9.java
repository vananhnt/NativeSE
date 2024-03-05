package java.security.cert;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Certificate.class */
public abstract class Certificate implements Serializable {
    public abstract byte[] getEncoded() throws CertificateEncodingException;

    public abstract void verify(PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

    public abstract void verify(PublicKey publicKey, String str) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

    public abstract String toString();

    public abstract PublicKey getPublicKey();

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Certificate$CertificateRep.class */
    protected static class CertificateRep implements Serializable {
        protected CertificateRep(String type, byte[] data) {
            throw new RuntimeException("Stub!");
        }

        protected Object readResolve() throws ObjectStreamException {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Certificate(String type) {
        throw new RuntimeException("Stub!");
    }

    public final String getType() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    protected Object writeReplace() throws ObjectStreamException {
        throw new RuntimeException("Stub!");
    }
}