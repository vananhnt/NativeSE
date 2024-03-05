package org.apache.harmony.security.pkcs7;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.AttributeTypeAndValue;
import org.apache.harmony.security.x501.Name;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

/* loaded from: SignerInfo.class */
public final class SignerInfo {
    private final int version;
    private final X500Principal issuer;
    private final BigInteger serialNumber;
    private final AlgorithmIdentifier digestAlgorithm;
    private final AuthenticatedAttributes authenticatedAttributes;
    private final AlgorithmIdentifier digestEncryptionAlgorithm;
    private final byte[] encryptedDigest;
    private final List<?> unauthenticatedAttributes;
    public static final ASN1Sequence ISSUER_AND_SERIAL_NUMBER = new ASN1Sequence(new ASN1Type[]{Name.ASN1, ASN1Integer.getInstance()}) { // from class: org.apache.harmony.security.pkcs7.SignerInfo.1
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            Object[] issAndSerial = (Object[]) object;
            values[0] = issAndSerial[0];
            values[1] = issAndSerial[1];
        }
    };
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Integer.getInstance(), ISSUER_AND_SERIAL_NUMBER, AlgorithmIdentifier.ASN1, new ASN1Implicit(0, AuthenticatedAttributes.ASN1), AlgorithmIdentifier.ASN1, ASN1OctetString.getInstance(), new ASN1Implicit(1, new ASN1SetOf(AttributeTypeAndValue.ASN1))}) { // from class: org.apache.harmony.security.pkcs7.SignerInfo.2
        {
            setOptional(3);
            setOptional(6);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            SignerInfo si = (SignerInfo) object;
            byte[] bArr = new byte[1];
            bArr[0] = (byte) si.version;
            values[0] = bArr;
            try {
                Object[] objArr = new Object[2];
                objArr[0] = new Name(si.issuer.getName());
                objArr[1] = si.serialNumber.toByteArray();
                values[1] = objArr;
                values[2] = si.digestAlgorithm;
                values[3] = si.authenticatedAttributes;
                values[4] = si.digestEncryptionAlgorithm;
                values[5] = si.encryptedDigest;
                values[6] = si.unauthenticatedAttributes;
            } catch (IOException e) {
                throw new RuntimeException("Failed to encode issuer name", e);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new SignerInfo(ASN1Integer.toIntValue(values[0]), (Object[]) values[1], (AlgorithmIdentifier) values[2], (AuthenticatedAttributes) values[3], (AlgorithmIdentifier) values[4], (byte[]) values[5], (List) values[6]);
        }
    };

    private SignerInfo(int version, Object[] issuerAndSerialNumber, AlgorithmIdentifier digestAlgorithm, AuthenticatedAttributes authenticatedAttributes, AlgorithmIdentifier digestEncryptionAlgorithm, byte[] encryptedDigest, List<?> unauthenticatedAttributes) {
        this.version = version;
        this.issuer = ((Name) issuerAndSerialNumber[0]).getX500Principal();
        this.serialNumber = ASN1Integer.toBigIntegerValue(issuerAndSerialNumber[1]);
        this.digestAlgorithm = digestAlgorithm;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }

    public X500Principal getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm.getAlgorithm();
    }

    public String getDigestAlgorithmName() {
        return this.digestAlgorithm.getAlgorithmName();
    }

    public String getDigestEncryptionAlgorithm() {
        return this.digestEncryptionAlgorithm.getAlgorithm();
    }

    public String getDigestEncryptionAlgorithmName() {
        return this.digestEncryptionAlgorithm.getAlgorithmName();
    }

    public List<AttributeTypeAndValue> getAuthenticatedAttributes() {
        if (this.authenticatedAttributes == null) {
            return null;
        }
        return this.authenticatedAttributes.getAttributes();
    }

    public byte[] getEncodedAuthenticatedAttributes() {
        if (this.authenticatedAttributes == null) {
            return null;
        }
        return AuthenticatedAttributes.ASN1.encode(this.authenticatedAttributes.getAttributes());
    }

    public byte[] getEncryptedDigest() {
        return this.encryptedDigest;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("-- SignerInfo:");
        res.append("\n version : ");
        res.append(this.version);
        res.append("\nissuerAndSerialNumber:  ");
        res.append(this.issuer);
        res.append("   ");
        res.append(this.serialNumber);
        res.append("\ndigestAlgorithm:  ");
        res.append(this.digestAlgorithm.toString());
        res.append("\nauthenticatedAttributes:  ");
        if (this.authenticatedAttributes != null) {
            res.append(this.authenticatedAttributes.toString());
        }
        res.append("\ndigestEncryptionAlgorithm: ");
        res.append(this.digestEncryptionAlgorithm.toString());
        res.append("\nunauthenticatedAttributes: ");
        if (this.unauthenticatedAttributes != null) {
            res.append(this.unauthenticatedAttributes.toString());
        }
        res.append("\n-- SignerInfo End\n");
        return res.toString();
    }
}