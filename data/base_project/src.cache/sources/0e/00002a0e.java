package org.apache.harmony.security.x509;

import java.io.IOException;
import org.apache.harmony.security.asn1.ASN1Enumerated;
import org.apache.harmony.security.asn1.ASN1Type;

/* loaded from: ReasonCode.class */
public final class ReasonCode extends ExtensionValue {
    public static final byte UNSPECIFIED = 0;
    public static final byte KEY_COMPROMISE = 1;
    public static final byte CA_COMPROMISE = 2;
    public static final byte AFFILIATION_CHANGED = 3;
    public static final byte SUPERSEDED = 4;
    public static final byte CESSATION_OF_OPERATION = 5;
    public static final byte CERTIFICATE_HOLD = 6;
    public static final byte REMOVE_FROM_CRL = 8;
    public static final byte PRIVILEGE_WITHDRAWN = 9;
    public static final byte AA_COMPROMISE = 10;
    private final byte code;
    public static final ASN1Type ASN1 = ASN1Enumerated.getInstance();

    public ReasonCode(byte[] encoding) throws IOException {
        super(encoding);
        this.code = ((byte[]) ASN1.decode(encoding))[0];
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(new byte[]{this.code});
        }
        return this.encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("Reason Code: [ ");
        switch (this.code) {
            case 0:
                sb.append("unspecified");
                break;
            case 1:
                sb.append("keyCompromise");
                break;
            case 2:
                sb.append("cACompromise");
                break;
            case 3:
                sb.append("affiliationChanged");
                break;
            case 4:
                sb.append("superseded");
                break;
            case 5:
                sb.append("cessationOfOperation");
                break;
            case 6:
                sb.append("certificateHold");
                break;
            case 8:
                sb.append("removeFromCRL");
                break;
            case 9:
                sb.append("privilegeWithdrawn");
                break;
            case 10:
                sb.append("aACompromise");
                break;
        }
        sb.append(" ]\n");
    }
}