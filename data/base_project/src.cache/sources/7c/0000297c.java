package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1SetOf.class */
public class ASN1SetOf extends ASN1ValueCollection {
    public ASN1SetOf(ASN1Type type) {
        super(17, type);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readSetOf(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void encodeContent(BerOutputStream out) {
        out.encodeSetOf(this);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final void setEncodingContent(BerOutputStream out) {
        out.getSetOfLength(this);
    }
}