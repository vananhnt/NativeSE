package org.apache.harmony.security.asn1;

import java.io.IOException;

/* loaded from: ASN1Implicit.class */
public final class ASN1Implicit extends ASN1Type {
    private static final int TAGGING_PRIMITIVE = 0;
    private static final int TAGGING_CONSTRUCTED = 1;
    private static final int TAGGING_STRING = 2;
    private final ASN1Type type;
    private final int taggingType;

    public ASN1Implicit(int tagNumber, ASN1Type type) {
        super(128, tagNumber);
        if ((type instanceof ASN1Choice) || (type instanceof ASN1Any)) {
            throw new IllegalArgumentException("Implicit tagging can not be used for ASN.1 ANY or CHOICE type");
        }
        this.type = type;
        if (type.checkTag(type.id)) {
            if (type.checkTag(type.constrId)) {
                this.taggingType = 2;
                return;
            } else {
                this.taggingType = 0;
                return;
            }
        }
        this.taggingType = 1;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final boolean checkTag(int identifier) {
        switch (this.taggingType) {
            case 0:
                return this.id == identifier;
            case 1:
                return this.constrId == identifier;
            default:
                return this.id == identifier || this.constrId == identifier;
        }
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        if (!checkTag(in.tag)) {
            throw new ASN1Exception("ASN.1 implicitly tagged type expected at [" + in.tagOffset + "]. Expected tag: " + Integer.toHexString(this.id) + ", but got " + Integer.toHexString(in.tag));
        }
        if (this.id == in.tag) {
            in.tag = this.type.id;
        } else {
            in.tag = this.type.constrId;
        }
        in.content = this.type.decode(in);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeASN(BerOutputStream out) {
        if (this.taggingType == 1) {
            out.encodeTag(this.constrId);
        } else {
            out.encodeTag(this.id);
        }
        encodeContent(out);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        this.type.encodeContent(out);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        this.type.setEncodingContent(out);
    }
}