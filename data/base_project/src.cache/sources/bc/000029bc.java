package org.apache.harmony.security.provider.crypto;

import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: ThreeIntegerSequence.class */
class ThreeIntegerSequence {
    byte[] p;
    byte[] q;
    byte[] g;
    private byte[] encoding = null;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Integer.getInstance(), ASN1Integer.getInstance(), ASN1Integer.getInstance()}) { // from class: org.apache.harmony.security.provider.crypto.ThreeIntegerSequence.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new ThreeIntegerSequence((byte[]) values[0], (byte[]) values[1], (byte[]) values[2]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            ThreeIntegerSequence mySeq = (ThreeIntegerSequence) object;
            values[0] = mySeq.p;
            values[1] = mySeq.q;
            values[2] = mySeq.g;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThreeIntegerSequence(byte[] p, byte[] q, byte[] g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}