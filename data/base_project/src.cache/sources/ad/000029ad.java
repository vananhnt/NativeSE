package org.apache.harmony.security.provider.cert;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Oid;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;
import org.apache.harmony.security.pkcs7.SignedData;
import org.apache.harmony.security.x509.Certificate;

/* loaded from: X509CertPathImpl.class */
public class X509CertPathImpl extends CertPath {
    private static final long serialVersionUID = 7989755106209515436L;
    private final List<X509Certificate> certificates;
    private byte[] pkiPathEncoding;
    private byte[] pkcs7Encoding;
    static final List<String> encodings = Collections.unmodifiableList(Arrays.asList(Encoding.PKI_PATH.apiName, Encoding.PKCS7.apiName));
    public static final ASN1SequenceOf ASN1 = new ASN1SequenceOf(ASN1Any.getInstance()) { // from class: org.apache.harmony.security.provider.cert.X509CertPathImpl.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            List<byte[]> encodedCerts = (List) in.content;
            int size = encodedCerts.size();
            List<X509Certificate> certificates = new ArrayList<>(size);
            for (int i = size - 1; i >= 0; i--) {
                certificates.add(new X509CertImpl((Certificate) Certificate.ASN1.decode(encodedCerts.get(i))));
            }
            return new X509CertPathImpl(certificates, Encoding.PKI_PATH);
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection<byte[]> getValues(Object object) {
            X509CertPathImpl cp = (X509CertPathImpl) object;
            if (cp.certificates != null) {
                int size = cp.certificates.size();
                List<byte[]> encodings2 = new ArrayList<>(size);
                try {
                    for (int i = size - 1; i >= 0; i--) {
                        encodings2.add(((X509Certificate) cp.certificates.get(i)).getEncoded());
                    }
                    return encodings2;
                } catch (CertificateEncodingException e) {
                    throw new IllegalArgumentException("Encoding error occurred", e);
                }
            }
            return Collections.emptyList();
        }
    };
    private static final ASN1Sequence ASN1_SIGNED_DATA = new ASN1Sequence(new ASN1Type[]{ASN1Any.getInstance(), new ASN1Implicit(0, ASN1), ASN1Any.getInstance()}) { // from class: org.apache.harmony.security.provider.cert.X509CertPathImpl.2
        private final byte[] PRECALCULATED_HEAD = {2, 1, 1, 49, 0, 48, 3, 6, 1, 0};
        private final byte[] SIGNERS_INFO = {49, 0};

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            values[0] = this.PRECALCULATED_HEAD;
            values[1] = object;
            values[2] = this.SIGNERS_INFO;
        }

        @Override // org.apache.harmony.security.asn1.ASN1Sequence, org.apache.harmony.security.asn1.ASN1Type
        public Object decode(BerInputStream in) throws IOException {
            throw new RuntimeException("Invalid use of encoder for PKCS#7 SignedData object");
        }
    };
    private static final ASN1Sequence PKCS7_SIGNED_DATA_OBJECT = new ASN1Sequence(new ASN1Type[]{ASN1Any.getInstance(), new ASN1Explicit(0, ASN1_SIGNED_DATA)}) { // from class: org.apache.harmony.security.provider.cert.X509CertPathImpl.3
        private final byte[] SIGNED_DATA_OID = ASN1Oid.getInstance().encode(ContentInfo.SIGNED_DATA);

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            values[0] = this.SIGNED_DATA_OID;
            values[1] = object;
        }

        @Override // org.apache.harmony.security.asn1.ASN1Sequence, org.apache.harmony.security.asn1.ASN1Type
        public Object decode(BerInputStream in) throws IOException {
            throw new RuntimeException("Invalid use of encoder for PKCS#7 SignedData object");
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: X509CertPathImpl$Encoding.class */
    public enum Encoding {
        PKI_PATH("PkiPath"),
        PKCS7("PKCS7");
        
        private final String apiName;

        Encoding(String apiName) {
            this.apiName = apiName;
        }

        static Encoding findByApiName(String apiName) throws CertificateEncodingException {
            Encoding[] arr$ = values();
            for (Encoding element : arr$) {
                if (element.apiName.equals(apiName)) {
                    return element;
                }
            }
            return null;
        }
    }

    public X509CertPathImpl(List<? extends java.security.cert.Certificate> certs) throws CertificateException {
        super("X.509");
        int size = certs.size();
        this.certificates = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            java.security.cert.Certificate cert = certs.get(i);
            if (!(cert instanceof X509Certificate)) {
                throw new CertificateException("Certificate " + i + " is not an X.509 certificate");
            }
            this.certificates.add((X509Certificate) cert);
        }
    }

    private X509CertPathImpl(List<X509Certificate> certs, Encoding type) {
        super("X.509");
        this.certificates = certs;
    }

    private static X509CertPathImpl getCertPathFromContentInfo(ContentInfo contentInfo) throws CertificateException {
        SignedData sd = contentInfo.getSignedData();
        if (sd == null) {
            throw new CertificateException("Incorrect PKCS7 encoded form: missing signed data");
        }
        List<Certificate> certs = sd.getCertificates();
        if (certs == null) {
            certs = Collections.emptyList();
        }
        List<X509Certificate> result = new ArrayList<>(certs.size());
        for (Certificate cert : certs) {
            result.add(new X509CertImpl(cert));
        }
        return new X509CertPathImpl(result, Encoding.PKCS7);
    }

    public static X509CertPathImpl getInstance(InputStream in) throws CertificateException {
        try {
            return (X509CertPathImpl) ASN1.decode(in);
        } catch (IOException e) {
            throw new CertificateException("Failed to decode CertPath", e);
        }
    }

    public static X509CertPathImpl getInstance(InputStream in, String encoding) throws CertificateException {
        try {
            Encoding encType = Encoding.findByApiName(encoding);
            if (encType == null) {
                throw new CertificateException("Unsupported encoding: " + encoding);
            }
            switch (encType) {
                case PKI_PATH:
                    return (X509CertPathImpl) ASN1.decode(in);
                case PKCS7:
                    return getCertPathFromContentInfo((ContentInfo) ContentInfo.ASN1.decode(in));
                default:
                    throw new CertificateException("Unsupported encoding: " + encoding);
            }
        } catch (IOException e) {
            throw new CertificateException("Failed to decode CertPath", e);
        }
    }

    public static X509CertPathImpl getInstance(byte[] in) throws CertificateException {
        try {
            return (X509CertPathImpl) ASN1.decode(in);
        } catch (IOException e) {
            throw new CertificateException("Failed to decode CertPath", e);
        }
    }

    public static X509CertPathImpl getInstance(byte[] in, String encoding) throws CertificateException {
        try {
            Encoding encType = Encoding.findByApiName(encoding);
            if (encType == null) {
                throw new CertificateException("Unsupported encoding: " + encoding);
            }
            switch (encType) {
                case PKI_PATH:
                    return (X509CertPathImpl) ASN1.decode(in);
                case PKCS7:
                    return getCertPathFromContentInfo((ContentInfo) ContentInfo.ASN1.decode(in));
                default:
                    throw new CertificateException("Unsupported encoding: " + encoding);
            }
        } catch (IOException e) {
            throw new CertificateException("Failed to decode CertPath", e);
        }
    }

    @Override // java.security.cert.CertPath
    public List<X509Certificate> getCertificates() {
        return Collections.unmodifiableList(this.certificates);
    }

    @Override // java.security.cert.CertPath
    public byte[] getEncoded() throws CertificateEncodingException {
        return getEncoded(Encoding.PKI_PATH);
    }

    private byte[] getEncoded(Encoding encoding) throws CertificateEncodingException {
        switch (encoding) {
            case PKI_PATH:
                if (this.pkiPathEncoding == null) {
                    this.pkiPathEncoding = ASN1.encode(this);
                }
                return (byte[]) this.pkiPathEncoding.clone();
            case PKCS7:
                if (this.pkcs7Encoding == null) {
                    this.pkcs7Encoding = PKCS7_SIGNED_DATA_OBJECT.encode(this);
                }
                return (byte[]) this.pkcs7Encoding.clone();
            default:
                throw new CertificateEncodingException("Unsupported encoding: " + encoding);
        }
    }

    @Override // java.security.cert.CertPath
    public byte[] getEncoded(String encoding) throws CertificateEncodingException {
        Encoding encType = Encoding.findByApiName(encoding);
        if (encType == null) {
            throw new CertificateEncodingException("Unsupported encoding: " + encoding);
        }
        return getEncoded(encType);
    }

    @Override // java.security.cert.CertPath
    public Iterator<String> getEncodings() {
        return encodings.iterator();
    }
}