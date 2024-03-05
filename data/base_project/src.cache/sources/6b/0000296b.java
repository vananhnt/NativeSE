package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Boolean.class */
public final class ASN1Boolean extends ASN1Primitive {
    private static final ASN1Boolean ASN1 = new ASN1Boolean();

    public ASN1Boolean() {
        super(1);
    }

    public static ASN1Boolean getInstance() {
        return ASN1;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readBoolean();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object getDecodedObject(BerInputStream in) throws IOException {
        if (in.buffer[in.contentOffset] == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        out.encodeBoolean();
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        out.length = 1;
    }
}