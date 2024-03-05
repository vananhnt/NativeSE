package org.apache.harmony.security.pkcs10;

import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

/* loaded from: CertificationRequest.class */
public final class CertificationRequest {
    private CertificationRequestInfo info;
    private AlgorithmIdentifier algId;
    private byte[] signature;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{CertificationRequestInfo.ASN1, AlgorithmIdentifier.ASN1, ASN1BitString.getInstance()}) { // from class: org.apache.harmony.security.pkcs10.CertificationRequest.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new CertificationRequest((CertificationRequestInfo) values[0], (AlgorithmIdentifier) values[1], ((BitString) values[2]).bytes, in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            CertificationRequest certReq = (CertificationRequest) object;
            values[0] = certReq.info;
            values[1] = certReq.algId;
            values[2] = new BitString(certReq.signature, 0);
        }
    };

    public CertificationRequest(CertificationRequestInfo info, AlgorithmIdentifier algId, byte[] signature) {
        this.info = info;
        this.algId = algId;
        this.signature = (byte[]) signature.clone();
    }

    private CertificationRequest(CertificationRequestInfo info, AlgorithmIdentifier algId, byte[] signature, byte[] encoding) {
        this(info, algId, signature);
        this.encoding = encoding;
    }

    public CertificationRequestInfo getInfo() {
        return this.info;
    }

    public byte[] getSignature() {
        byte[] result = new byte[this.signature.length];
        System.arraycopy(this.signature, 0, result, 0, this.signature.length);
        return result;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}