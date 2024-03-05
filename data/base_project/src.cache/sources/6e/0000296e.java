package org.apache.harmony.security.asn1;

/* loaded from: ASN1Constructed.class */
public abstract class ASN1Constructed extends ASN1Type {
    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Constructed(int tagNumber) {
        super(0, tagNumber);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Constructed(int tagClass, int tagNumber) {
        super(tagClass, tagNumber);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final boolean checkTag(int identifier) {
        return this.constrId == identifier;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeASN(BerOutputStream out) {
        out.encodeTag(this.constrId);
        encodeContent(out);
    }
}