package org.apache.harmony.security.pkcs8;

import java.util.List;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.AttributeTypeAndValue;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

/* loaded from: PrivateKeyInfo.class */
public final class PrivateKeyInfo {
    private final int version;
    private final AlgorithmIdentifier privateKeyAlgorithm;
    private final byte[] privateKey;
    private final List<?> attributes;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Integer.getInstance(), AlgorithmIdentifier.ASN1, ASN1OctetString.getInstance(), new ASN1Implicit(0, new ASN1SetOf(AttributeTypeAndValue.ASN1))}) { // from class: org.apache.harmony.security.pkcs8.PrivateKeyInfo.1
        {
            setOptional(3);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new PrivateKeyInfo(ASN1Integer.toIntValue(values[0]), (AlgorithmIdentifier) values[1], (byte[]) values[2], (List) values[3], in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
            values[0] = ASN1Integer.fromIntValue(privateKeyInfo.version);
            values[1] = privateKeyInfo.privateKeyAlgorithm;
            values[2] = privateKeyInfo.privateKey;
            values[3] = privateKeyInfo.attributes;
        }
    };

    public PrivateKeyInfo(int version, AlgorithmIdentifier privateKeyAlgorithm, byte[] privateKey, List attributes) {
        this.version = version;
        this.privateKeyAlgorithm = privateKeyAlgorithm;
        this.privateKey = privateKey;
        this.attributes = attributes;
    }

    private PrivateKeyInfo(int version, AlgorithmIdentifier privateKeyAlgorithm, byte[] privateKey, List attributes, byte[] encoding) {
        this(version, privateKeyAlgorithm, privateKey, attributes);
        this.encoding = encoding;
    }

    public int getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.privateKeyAlgorithm;
    }

    public List getAttributes() {
        return this.attributes;
    }

    public byte[] getPrivateKey() {
        return this.privateKey;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}