package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1SequenceOf.class */
public class ASN1SequenceOf extends ASN1ValueCollection {
    public ASN1SequenceOf(ASN1Type type) {
        super(16, type);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readSequenceOf(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void encodeContent(BerOutputStream out) {
        out.encodeSequenceOf(this);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void setEncodingContent(BerOutputStream out) {
        out.getSequenceOfLength(this);
    }
}