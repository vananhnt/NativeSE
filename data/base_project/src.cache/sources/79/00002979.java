package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Sequence.class */
public class ASN1Sequence extends ASN1TypeCollection {
    public ASN1Sequence(ASN1Type[] type) {
        super(16, type);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readSequence(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void encodeContent(BerOutputStream out) {
        out.encodeSequence(this);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void setEncodingContent(BerOutputStream out) {
        out.getSequenceLength(this);
    }
}