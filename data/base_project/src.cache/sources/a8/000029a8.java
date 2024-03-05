package org.apache.harmony.security.provider.cert;

import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.x509.Extension;
import org.apache.harmony.security.x509.Extensions;
import org.apache.harmony.security.x509.TBSCertList;

/* loaded from: X509CRLEntryImpl.class */
public class X509CRLEntryImpl extends X509CRLEntry {
    private final TBSCertList.RevokedCertificate rcert;
    private final Extensions extensions;
    private final X500Principal issuer;
    private byte[] encoding;

    public X509CRLEntryImpl(TBSCertList.RevokedCertificate rcert, X500Principal issuer) {
        this.rcert = rcert;
        this.extensions = rcert.getCrlEntryExtensions();
        this.issuer = issuer;
    }

    @Override // java.security.cert.X509CRLEntry
    public byte[] getEncoded() throws CRLException {
        if (this.encoding == null) {
            this.encoding = this.rcert.getEncoded();
        }
        byte[] result = new byte[this.encoding.length];
        System.arraycopy(this.encoding, 0, result, 0, this.encoding.length);
        return result;
    }

    @Override // java.security.cert.X509CRLEntry
    public BigInteger getSerialNumber() {
        return this.rcert.getUserCertificate();
    }

    @Override // java.security.cert.X509CRLEntry
    public X500Principal getCertificateIssuer() {
        return this.issuer;
    }

    @Override // java.security.cert.X509CRLEntry
    public Date getRevocationDate() {
        return this.rcert.getRevocationDate();
    }

    @Override // java.security.cert.X509CRLEntry
    public boolean hasExtensions() {
        return (this.extensions == null || this.extensions.size() == 0) ? false : true;
    }

    @Override // java.security.cert.X509CRLEntry
    public String toString() {
        return "X509CRLEntryImpl: " + this.rcert.toString();
    }

    @Override // java.security.cert.X509Extension
    public Set getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.getNonCriticalExtensions();
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.getCriticalExtensions();
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        Extension ext;
        if (this.extensions == null || (ext = this.extensions.getExtensionByOID(oid)) == null) {
            return null;
        }
        return ext.getRawExtnValue();
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        if (this.extensions == null) {
            return false;
        }
        return this.extensions.hasUnsupportedCritical();
    }
}