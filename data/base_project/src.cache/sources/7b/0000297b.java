package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Set.class */
public final class ASN1Set extends ASN1TypeCollection {
    public ASN1Set(ASN1Type[] type) {
        super(17, type);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readSet(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void encodeContent(BerOutputStream out) {
        out.encodeSet(this);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void setEncodingContent(BerOutputStream out) {
        out.getSetLength(this);
    }
}