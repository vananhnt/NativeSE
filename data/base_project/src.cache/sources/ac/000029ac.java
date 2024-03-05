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
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.utils.AlgNameMapper;
import org.apache.harmony.security.x509.Certificate;
import org.apache.harmony.security.x509.Extension;
import org.apache.harmony.security.x509.Extensions;
import org.apache.harmony.security.x509.TBSCertificate;

/* loaded from: X509CertImpl.class */
public final class X509CertImpl extends X509Certificate {
    private static final long serialVersionUID = 2972248729446736154L;
    private final Certificate certificate;
    private final TBSCertificate tbsCert;
    private final Extensions extensions;
    private volatile long notBefore;
    private volatile long notAfter;
    private volatile BigInteger serialNumber;
    private volatile X500Principal issuer;
    private volatile X500Principal subject;
    private volatile byte[] tbsCertificate;
    private volatile byte[] signature;
    private volatile String sigAlgName;
    private volatile String sigAlgOID;
    private volatile byte[] sigAlgParams;
    private volatile boolean nullSigAlgParams;
    private volatile PublicKey publicKey;
    private volatile byte[] encoding;

    public X509CertImpl(InputStream in) throws CertificateException {
        this.notBefore = -1L;
        this.notAfter = -1L;
        try {
            this.certificate = (Certificate) Certificate.ASN1.decode(in);
            this.tbsCert = this.certificate.getTbsCertificate();
            this.extensions = this.tbsCert.getExtensions();
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }

    public X509CertImpl(Certificate certificate) {
        this.notBefore = -1L;
        this.notAfter = -1L;
        this.certificate = certificate;
        this.tbsCert = certificate.getTbsCertificate();
        this.extensions = this.tbsCert.getExtensions();
    }

    public X509CertImpl(byte[] encoding) throws IOException {
        this((Certificate) Certificate.ASN1.decode(encoding));
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        checkValidity(System.currentTimeMillis());
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        checkValidity(date.getTime());
    }

    private void checkValidity(long time) throws CertificateExpiredException, CertificateNotYetValidException {
        if (time < getNotBeforeInternal()) {
            throw new CertificateNotYetValidException("current time: " + new Date(time) + ", validation time: " + new Date(getNotBeforeInternal()));
        }
        if (time > getNotAfterInternal()) {
            throw new CertificateExpiredException("current time: " + new Date(time) + ", expiration time: " + new Date(getNotAfterInternal()));
        }
    }

    @Override // java.security.cert.X509Certificate
    public int getVersion() {
        return this.tbsCert.getVersion() + 1;
    }

    @Override // java.security.cert.X509Certificate
    public BigInteger getSerialNumber() {
        BigInteger result = this.serialNumber;
        if (result == null) {
            BigInteger serialNumber = this.tbsCert.getSerialNumber();
            result = serialNumber;
            this.serialNumber = serialNumber;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public Principal getIssuerDN() {
        return getIssuerX500Principal();
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getIssuerX500Principal() {
        X500Principal result = this.issuer;
        if (result == null) {
            X500Principal x500Principal = this.tbsCert.getIssuer().getX500Principal();
            result = x500Principal;
            this.issuer = x500Principal;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public Principal getSubjectDN() {
        return getSubjectX500Principal();
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getSubjectX500Principal() {
        X500Principal result = this.subject;
        if (result == null) {
            X500Principal x500Principal = this.tbsCert.getSubject().getX500Principal();
            result = x500Principal;
            this.subject = x500Principal;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotBefore() {
        return new Date(getNotBeforeInternal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private long getNotBeforeInternal() {
        long result = this.notBefore;
        if (result == -1) {
            result = this.tbsCert.getValidity().getNotBefore().getTime();
            this.notBefore = this;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotAfter() {
        return new Date(getNotAfterInternal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private long getNotAfterInternal() {
        long result = this.notAfter;
        if (result == -1) {
            result = this.tbsCert.getValidity().getNotAfter().getTime();
            this.notAfter = this;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return (byte[]) getTbsCertificateInternal().clone();
    }

    private byte[] getTbsCertificateInternal() {
        byte[] result = this.tbsCertificate;
        if (result == null) {
            byte[] encoded = this.tbsCert.getEncoded();
            result = encoded;
            this.tbsCertificate = encoded;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSignature() {
        return (byte[]) getSignatureInternal().clone();
    }

    private byte[] getSignatureInternal() {
        byte[] result = this.signature;
        if (result == null) {
            byte[] signatureValue = this.certificate.getSignatureValue();
            result = signatureValue;
            this.signature = signatureValue;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgName() {
        String result = this.sigAlgName;
        if (result == null) {
            String sigAlgOIDLocal = getSigAlgOID();
            result = AlgNameMapper.map2AlgName(sigAlgOIDLocal);
            if (result == null) {
                result = sigAlgOIDLocal;
            }
            this.sigAlgName = result;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgOID() {
        String result = this.sigAlgOID;
        if (result == null) {
            String algorithm = this.tbsCert.getSignature().getAlgorithm();
            result = algorithm;
            this.sigAlgOID = algorithm;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSigAlgParams() {
        if (this.nullSigAlgParams) {
            return null;
        }
        byte[] result = this.sigAlgParams;
        if (result == null) {
            result = this.tbsCert.getSignature().getParameters();
            if (result == null) {
                this.nullSigAlgParams = true;
                return null;
            }
            this.sigAlgParams = result;
        }
        return result;
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getIssuerUniqueID() {
        return this.tbsCert.getIssuerUniqueID();
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getSubjectUniqueID() {
        return this.tbsCert.getSubjectUniqueID();
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getKeyUsage() {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.valueOfKeyUsage();
    }

    @Override // java.security.cert.X509Certificate
    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        if (this.extensions == null) {
            return null;
        }
        try {
            return this.extensions.valueOfExtendedKeyUsage();
        } catch (IOException e) {
            throw new CertificateParsingException(e);
        }
    }

    @Override // java.security.cert.X509Certificate
    public int getBasicConstraints() {
        if (this.extensions == null) {
            return -1;
        }
        return this.extensions.valueOfBasicConstraints();
    }

    @Override // java.security.cert.X509Certificate
    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        if (this.extensions == null) {
            return null;
        }
        try {
            return this.extensions.valueOfSubjectAlternativeName();
        } catch (IOException e) {
            throw new CertificateParsingException(e);
        }
    }

    @Override // java.security.cert.X509Certificate
    public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        if (this.extensions == null) {
            return null;
        }
        try {
            return this.extensions.valueOfIssuerAlternativeName();
        } catch (IOException e) {
            throw new CertificateParsingException(e);
        }
    }

    @Override // java.security.cert.Certificate
    public byte[] getEncoded() throws CertificateEncodingException {
        return (byte[]) getEncodedInternal().clone();
    }

    private byte[] getEncodedInternal() throws CertificateEncodingException {
        byte[] result = this.encoding;
        if (this.encoding == null) {
            byte[] encoded = this.certificate.getEncoded();
            result = encoded;
            this.encoding = encoded;
        }
        return result;
    }

    @Override // java.security.cert.Certificate
    public PublicKey getPublicKey() {
        PublicKey result = this.publicKey;
        if (result == null) {
            PublicKey publicKey = this.tbsCert.getSubjectPublicKeyInfo().getPublicKey();
            result = publicKey;
            this.publicKey = publicKey;
        }
        return result;
    }

    @Override // java.security.cert.Certificate
    public String toString() {
        return this.certificate.toString();
    }

    @Override // java.security.cert.Certificate
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature = Signature.getInstance(getSigAlgName());
        signature.initVerify(key);
        byte[] tbsCertificateLocal = getTbsCertificateInternal();
        signature.update(tbsCertificateLocal, 0, tbsCertificateLocal.length);
        if (!signature.verify(this.certificate.getSignatureValue())) {
            throw new SignatureException("Signature was not verified");
        }
    }

    @Override // java.security.cert.Certificate
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature = Signature.getInstance(getSigAlgName(), sigProvider);
        signature.initVerify(key);
        byte[] tbsCertificateLocal = getTbsCertificateInternal();
        signature.update(tbsCertificateLocal, 0, tbsCertificateLocal.length);
        if (!signature.verify(this.certificate.getSignatureValue())) {
            throw new SignatureException("Signature was not verified");
        }
    }

    @Override // java.security.cert.X509Extension
    public Set<String> getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.getNonCriticalExtensions();
    }

    @Override // java.security.cert.X509Extension
    public Set<String> getCriticalExtensionOIDs() {
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