package org.apache.harmony.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.utils.Array;

/* loaded from: AuthorityKeyIdentifier.class */
public final class AuthorityKeyIdentifier extends ExtensionValue {
    private final byte[] keyIdentifier;
    private final GeneralNames authorityCertIssuer;
    private final BigInteger authorityCertSerialNumber;
    public static final ASN1Type ASN1 = new ASN1Sequence(new ASN1Type[]{new ASN1Implicit(0, ASN1OctetString.getInstance()), new ASN1Implicit(1, GeneralNames.ASN1), new ASN1Implicit(2, ASN1Integer.getInstance())}) { // from class: org.apache.harmony.security.x509.AuthorityKeyIdentifier.1
        {
            setOptional(0);
            setOptional(1);
            setOptional(2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            Object[] values = (Object[]) in.content;
            byte[] bytes = (byte[]) values[2];
            BigInteger authorityCertSerialNumber = null;
            if (bytes != null) {
                authorityCertSerialNumber = new BigInteger(bytes);
            }
            return new AuthorityKeyIdentifier((byte[]) values[0], (GeneralNames) values[1], authorityCertSerialNumber);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            AuthorityKeyIdentifier akid = (AuthorityKeyIdentifier) object;
            values[0] = akid.keyIdentifier;
            values[1] = akid.authorityCertIssuer;
            if (akid.authorityCertSerialNumber != null) {
                values[2] = akid.authorityCertSerialNumber.toByteArray();
            }
        }
    };

    public AuthorityKeyIdentifier(byte[] keyIdentifier, GeneralNames authorityCertIssuer, BigInteger authorityCertSerialNumber) {
        this.keyIdentifier = keyIdentifier;
        this.authorityCertIssuer = authorityCertIssuer;
        this.authorityCertSerialNumber = authorityCertSerialNumber;
    }

    public static AuthorityKeyIdentifier decode(byte[] encoding) throws IOException {
        AuthorityKeyIdentifier aki = (AuthorityKeyIdentifier) ASN1.decode(encoding);
        aki.encoding = encoding;
        return aki;
    }

    public byte[] getKeyIdentifier() {
        return this.keyIdentifier;
    }

    public GeneralNames getAuthorityCertIssuer() {
        return this.authorityCertIssuer;
    }

    public BigInteger getAuthorityCertSerialNumber() {
        return this.authorityCertSerialNumber;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("AuthorityKeyIdentifier [\n");
        if (this.keyIdentifier != null) {
            sb.append(prefix).append("  keyIdentifier:\n");
            sb.append(Array.toString(this.keyIdentifier, prefix + "    "));
        }
        if (this.authorityCertIssuer != null) {
            sb.append(prefix).append("  authorityCertIssuer: [\n");
            this.authorityCertIssuer.dumpValue(sb, prefix + "    ");
            sb.append(prefix).append("  ]\n");
        }
        if (this.authorityCertSerialNumber != null) {
            sb.append(prefix).append("  authorityCertSerialNumber: ");
            sb.append(this.authorityCertSerialNumber).append('\n');
        }
        sb.append(prefix).append("]\n");
    }
}