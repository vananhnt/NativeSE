package org.apache.harmony.security.x509;

import java.io.IOException;
import java.util.Date;
import org.apache.harmony.security.asn1.ASN1GeneralizedTime;
import org.apache.harmony.security.asn1.ASN1Type;

/* loaded from: InvalidityDate.class */
public final class InvalidityDate extends ExtensionValue {
    private final Date date;
    public static final ASN1Type ASN1 = ASN1GeneralizedTime.getInstance();

    public InvalidityDate(byte[] encoding) throws IOException {
        super(encoding);
        this.date = (Date) ASN1.decode(encoding);
    }

    public Date getDate() {
        return this.date;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this.date);
        }
        return this.encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("Invalidity Date: [ ").append(this.date).append(" ]\n");
    }
}