package org.apache.harmony.security.asn1;

/* loaded from: ASN1Primitive.class */
public abstract class ASN1Primitive extends ASN1Type {
    public ASN1Primitive(int tagNumber) {
        super(tagNumber);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final boolean checkTag(int identifier) {
        return this.id == identifier;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeASN(BerOutputStream out) {
        out.encodeTag(this.id);
        encodeContent(out);
    }
}