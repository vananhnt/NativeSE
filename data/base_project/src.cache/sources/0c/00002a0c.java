package org.apache.harmony.security.x509;

import java.util.Date;
import org.apache.harmony.security.asn1.ASN1GeneralizedTime;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: PrivateKeyUsagePeriod.class */
public final class PrivateKeyUsagePeriod {
    private final Date notBeforeDate;
    private final Date notAfterDate;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{new ASN1Implicit(0, ASN1GeneralizedTime.getInstance()), new ASN1Implicit(1, ASN1GeneralizedTime.getInstance())}) { // from class: org.apache.harmony.security.x509.PrivateKeyUsagePeriod.1
        {
            setOptional(0);
            setOptional(1);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new PrivateKeyUsagePeriod((Date) values[0], (Date) values[1], in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            PrivateKeyUsagePeriod pkup = (PrivateKeyUsagePeriod) object;
            values[0] = pkup.notBeforeDate;
            values[1] = pkup.notAfterDate;
        }
    };

    public PrivateKeyUsagePeriod(Date notBeforeDate, Date notAfterDate) {
        this(notBeforeDate, notAfterDate, null);
    }

    private PrivateKeyUsagePeriod(Date notBeforeDate, Date notAfterDate, byte[] encoding) {
        this.notBeforeDate = notBeforeDate;
        this.notAfterDate = notAfterDate;
        this.encoding = encoding;
    }

    public Date getNotBefore() {
        return this.notBeforeDate;
    }

    public Date getNotAfter() {
        return this.notAfterDate;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}