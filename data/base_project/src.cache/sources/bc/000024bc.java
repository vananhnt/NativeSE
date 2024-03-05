package java.security.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509CRL.class */
public abstract class X509CRL extends CRL implements X509Extension {
    public abstract byte[] getEncoded() throws CRLException;

    public abstract void verify(PublicKey publicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

    public abstract void verify(PublicKey publicKey, String str) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

    public abstract int getVersion();

    public abstract Principal getIssuerDN();

    public abstract Date getThisUpdate();

    public abstract Date getNextUpdate();

    public abstract X509CRLEntry getRevokedCertificate(BigInteger bigInteger);

    public abstract Set<? extends X509CRLEntry> getRevokedCertificates();

    public abstract byte[] getTBSCertList() throws CRLException;

    public abstract byte[] getSignature();

    public abstract String getSigAlgName();

    public abstract String getSigAlgOID();

    public abstract byte[] getSigAlgParams();

    /* JADX INFO: Access modifiers changed from: protected */
    public X509CRL() {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public X500Principal getIssuerX500Principal() {
        throw new RuntimeException("Stub!");
    }

    public X509CRLEntry getRevokedCertificate(X509Certificate certificate) {
        throw new RuntimeException("Stub!");
    }
}