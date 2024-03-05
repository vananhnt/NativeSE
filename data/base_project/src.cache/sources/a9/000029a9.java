package org.apache.harmony.security.provider.cert;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.utils.AlgNameMapper;
import org.apache.harmony.security.x509.CertificateList;
import org.apache.harmony.security.x509.Extension;
import org.apache.harmony.security.x509.Extensions;
import org.apache.harmony.security.x509.TBSCertList;

/* loaded from: X509CRLImpl.class */
public class X509CRLImpl extends X509CRL {
    private final CertificateList crl;
    private final TBSCertList tbsCertList;
    private byte[] tbsCertListEncoding;
    private final Extensions extensions;
    private X500Principal issuer;
    private ArrayList entries;
    private int entriesSize;
    private byte[] signature;
    private String sigAlgOID;
    private String sigAlgName;
    private byte[] sigAlgParams;
    private byte[] encoding;
    private boolean nullSigAlgParams;
    private boolean entriesRetrieved;
    private boolean isIndirectCRL;
    private int nonIndirectEntriesSize;

    public X509CRLImpl(CertificateList crl) {
        this.crl = crl;
        this.tbsCertList = crl.getTbsCertList();
        this.extensions = this.tbsCertList.getCrlExtensions();
    }

    public X509CRLImpl(InputStream in) throws CRLException {
        try {
            this.crl = (CertificateList) CertificateList.ASN1.decode(in);
            this.tbsCertList = this.crl.getTbsCertList();
            this.extensions = this.tbsCertList.getCrlExtensions();
        } catch (IOException e) {
            throw new CRLException(e);
        }
    }

    public X509CRLImpl(byte[] encoding) throws IOException {
        this((CertificateList) CertificateList.ASN1.decode(encoding));
    }

    @Override // java.security.cert.X509CRL
    public byte[] getEncoded() throws CRLException {
        if (this.encoding == null) {
            this.encoding = this.crl.getEncoded();
        }
        byte[] result = new byte[this.encoding.length];
        System.arraycopy(this.encoding, 0, result, 0, this.encoding.length);
        return result;
    }

    @Override // java.security.cert.X509CRL
    public int getVersion() {
        return this.tbsCertList.getVersion();
    }

    @Override // java.security.cert.X509CRL
    public Principal getIssuerDN() {
        if (this.issuer == null) {
            this.issuer = this.tbsCertList.getIssuer().getX500Principal();
        }
        return this.issuer;
    }

    @Override // java.security.cert.X509CRL
    public X500Principal getIssuerX500Principal() {
        if (this.issuer == null) {
            this.issuer = this.tbsCertList.getIssuer().getX500Principal();
        }
        return this.issuer;
    }

    @Override // java.security.cert.X509CRL
    public Date getThisUpdate() {
        return this.tbsCertList.getThisUpdate();
    }

    @Override // java.security.cert.X509CRL
    public Date getNextUpdate() {
        return this.tbsCertList.getNextUpdate();
    }

    private void retrieveEntries() {
        this.entriesRetrieved = true;
        List rcerts = this.tbsCertList.getRevokedCertificates();
        if (rcerts == null) {
            return;
        }
        this.entriesSize = rcerts.size();
        this.entries = new ArrayList(this.entriesSize);
        X500Principal rcertIssuer = null;
        for (int i = 0; i < this.entriesSize; i++) {
            TBSCertList.RevokedCertificate rcert = rcerts.get(i);
            X500Principal iss = rcert.getIssuer();
            if (iss != null) {
                rcertIssuer = iss;
                this.isIndirectCRL = true;
                this.nonIndirectEntriesSize = i;
            }
            this.entries.add(new X509CRLEntryImpl(rcert, rcertIssuer));
        }
    }

    @Override // java.security.cert.X509CRL
    public X509CRLEntry getRevokedCertificate(X509Certificate certificate) {
        if (certificate == null) {
            throw new NullPointerException("certificate == null");
        }
        if (!this.entriesRetrieved) {
            retrieveEntries();
        }
        if (this.entries == null) {
            return null;
        }
        BigInteger serialN = certificate.getSerialNumber();
        if (this.isIndirectCRL) {
            X500Principal certIssuer = certificate.getIssuerX500Principal();
            if (certIssuer.equals(getIssuerX500Principal())) {
                certIssuer = null;
            }
            for (int i = 0; i < this.entriesSize; i++) {
                X509CRLEntry entry = (X509CRLEntry) this.entries.get(i);
                if (serialN.equals(entry.getSerialNumber())) {
                    X500Principal iss = entry.getCertificateIssuer();
                    if (certIssuer != null) {
                        if (certIssuer.equals(iss)) {
                            return entry;
                        }
                    } else if (iss == null) {
                        return entry;
                    }
                }
            }
            return null;
        }
        for (int i2 = 0; i2 < this.entriesSize; i2++) {
            X509CRLEntry entry2 = (X509CRLEntry) this.entries.get(i2);
            if (serialN.equals(entry2.getSerialNumber())) {
                return entry2;
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public X509CRLEntry getRevokedCertificate(BigInteger serialNumber) {
        if (!this.entriesRetrieved) {
            retrieveEntries();
        }
        if (this.entries == null) {
            return null;
        }
        for (int i = 0; i < this.nonIndirectEntriesSize; i++) {
            X509CRLEntry entry = (X509CRLEntry) this.entries.get(i);
            if (serialNumber.equals(entry.getSerialNumber())) {
                return entry;
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public Set<? extends X509CRLEntry> getRevokedCertificates() {
        if (!this.entriesRetrieved) {
            retrieveEntries();
        }
        if (this.entries == null) {
            return null;
        }
        return new HashSet(this.entries);
    }

    @Override // java.security.cert.X509CRL
    public byte[] getTBSCertList() throws CRLException {
        if (this.tbsCertListEncoding == null) {
            this.tbsCertListEncoding = this.tbsCertList.getEncoded();
        }
        byte[] result = new byte[this.tbsCertListEncoding.length];
        System.arraycopy(this.tbsCertListEncoding, 0, result, 0, this.tbsCertListEncoding.length);
        return result;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSignature() {
        if (this.signature == null) {
            this.signature = this.crl.getSignatureValue();
        }
        byte[] result = new byte[this.signature.length];
        System.arraycopy(this.signature, 0, result, 0, this.signature.length);
        return result;
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgName() {
        if (this.sigAlgOID == null) {
            this.sigAlgOID = this.tbsCertList.getSignature().getAlgorithm();
            this.sigAlgName = AlgNameMapper.map2AlgName(this.sigAlgOID);
            if (this.sigAlgName == null) {
                this.sigAlgName = this.sigAlgOID;
            }
        }
        return this.sigAlgName;
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgOID() {
        if (this.sigAlgOID == null) {
            this.sigAlgOID = this.tbsCertList.getSignature().getAlgorithm();
            this.sigAlgName = AlgNameMapper.map2AlgName(this.sigAlgOID);
            if (this.sigAlgName == null) {
                this.sigAlgName = this.sigAlgOID;
            }
        }
        return this.sigAlgOID;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSigAlgParams() {
        if (this.nullSigAlgParams) {
            return null;
        }
        if (this.sigAlgParams == null) {
            this.sigAlgParams = this.tbsCertList.getSignature().getParameters();
            if (this.sigAlgParams == null) {
                this.nullSigAlgParams = true;
                return null;
            }
        }
        return this.sigAlgParams;
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature = Signature.getInstance(getSigAlgName());
        signature.initVerify(key);
        byte[] tbsEncoding = this.tbsCertList.getEncoded();
        signature.update(tbsEncoding, 0, tbsEncoding.length);
        if (!signature.verify(this.crl.getSignatureValue())) {
            throw new SignatureException("Signature was not verified");
        }
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key, String sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature = Signature.getInstance(getSigAlgName(), sigProvider);
        signature.initVerify(key);
        byte[] tbsEncoding = this.tbsCertList.getEncoded();
        signature.update(tbsEncoding, 0, tbsEncoding.length);
        if (!signature.verify(this.crl.getSignatureValue())) {
            throw new SignatureException("Signature was not verified");
        }
    }

    @Override // java.security.cert.CRL
    public boolean isRevoked(Certificate cert) {
        return (cert instanceof X509Certificate) && getRevokedCertificate((X509Certificate) cert) != null;
    }

    @Override // java.security.cert.CRL
    public String toString() {
        return this.crl.toString();
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