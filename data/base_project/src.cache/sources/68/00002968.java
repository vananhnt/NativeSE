package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Any.class */
public final class ASN1Any extends ASN1Type {
    private static final ASN1Any ASN1 = new ASN1Any();

    public ASN1Any() {
        super(0);
    }

    public static ASN1Any getInstance() {
        return ASN1;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final boolean checkTag(int identifier) {
        return true;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readContent();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object getDecodedObject(BerInputStream in) throws IOException {
        byte[] bytesEncoded = new byte[in.offset - in.tagOffset];
        System.arraycopy(in.buffer, in.tagOffset, bytesEncoded, 0, bytesEncoded.length);
        return bytesEncoded;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeASN(BerOutputStream out) {
        out.encodeANY();
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        out.encodeANY();
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        out.length = ((byte[]) out.content).length;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public int getEncodedLength(BerOutputStream out) {
        return out.length;
    }
}