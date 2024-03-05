package org.apache.harmony.security.x509;

import java.io.IOException;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BerOutputStream;

/* loaded from: ReasonFlags.class */
public final class ReasonFlags {
    private final boolean[] flags;
    static final String[] REASONS = {"unused", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "privilegeWithdrawn", "aACompromise"};
    public static final ASN1BitString ASN1 = new ASN1BitString.ASN1NamedBitList(REASONS.length) { // from class: org.apache.harmony.security.x509.ReasonFlags.1
        @Override // org.apache.harmony.security.asn1.ASN1BitString.ASN1NamedBitList, org.apache.harmony.security.asn1.ASN1BitString, org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            return new ReasonFlags((boolean[]) super.getDecodedObject(in));
        }

        @Override // org.apache.harmony.security.asn1.ASN1BitString.ASN1NamedBitList, org.apache.harmony.security.asn1.ASN1BitString, org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
        public void setEncodingContent(BerOutputStream out) {
            out.content = ((ReasonFlags) out.content).flags;
            super.setEncodingContent(out);
        }
    };

    public ReasonFlags(boolean[] flags) {
        this.flags = flags;
    }

    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix);
        sb.append("ReasonFlags [\n");
        for (int i = 0; i < this.flags.length; i++) {
            if (this.flags[i]) {
                sb.append(prefix).append("  ").append(REASONS[i]).append('\n');
            }
        }
        sb.append(prefix);
        sb.append("]\n");
    }
}