package java.security.cert;

import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: X509CRLEntry.class */
public abstract class X509CRLEntry implements X509Extension {
    public abstract byte[] getEncoded() throws CRLException;

    public abstract BigInteger getSerialNumber();

    public abstract Date getRevocationDate();

    public abstract boolean hasExtensions();

    public abstract String toString();

    public X509CRLEntry() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public X500Principal getCertificateIssuer() {
        throw new RuntimeException("Stub!");
    }
}