package org.apache.harmony.security.x509;

import java.util.Date;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: Validity.class */
public final class Validity {
    private final Date notBefore;
    private final Date notAfter;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{Time.ASN1, Time.ASN1}) { // from class: org.apache.harmony.security.x509.Validity.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new Validity((Date) values[0], (Date) values[1]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            Validity validity = (Validity) object;
            values[0] = validity.notBefore;
            values[1] = validity.notAfter;
        }
    };

    public Validity(Date notBefore, Date notAfter) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public Date getNotBefore() {
        return this.notBefore;
    }

    public Date getNotAfter() {
        return this.notAfter;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}