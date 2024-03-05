package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/* loaded from: ASN1StringType.class */
public abstract class ASN1StringType extends ASN1Type {
    public static final ASN1StringType BMPSTRING = new ASN1StringType(30) { // from class: org.apache.harmony.security.asn1.ASN1StringType.1
    };
    public static final ASN1StringType IA5STRING = new ASN1StringType(22) { // from class: org.apache.harmony.security.asn1.ASN1StringType.2
    };
    public static final ASN1StringType GENERALSTRING = new ASN1StringType(27) { // from class: org.apache.harmony.security.asn1.ASN1StringType.3
    };
    public static final ASN1StringType PRINTABLESTRING = new ASN1StringType(19) { // from class: org.apache.harmony.security.asn1.ASN1StringType.4
    };
    public static final ASN1StringType TELETEXSTRING = new ASN1StringUTF8Type(20) { // from class: org.apache.harmony.security.asn1.ASN1StringType.5
    };
    public static final ASN1StringType UNIVERSALSTRING = new ASN1StringType(28) { // from class: org.apache.harmony.security.asn1.ASN1StringType.6
    };
    public static final ASN1StringType UTF8STRING = new ASN1StringUTF8Type(12) { // from class: org.apache.harmony.security.asn1.ASN1StringType.7
    };

    /* loaded from: ASN1StringType$ASN1StringUTF8Type.class */
    private static class ASN1StringUTF8Type extends ASN1StringType {
        public ASN1StringUTF8Type(int tagNumber) {
            super(tagNumber);
        }

        @Override // org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            return new String(in.buffer, in.contentOffset, in.length, StandardCharsets.UTF_8);
        }

        @Override // org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
        public void setEncodingContent(BerOutputStream out) {
            byte[] bytes = ((String) out.content).getBytes(StandardCharsets.UTF_8);
            out.content = bytes;
            out.length = bytes.length;
        }
    }

    public ASN1StringType(int tagNumber) {
        super(tagNumber);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public final boolean checkTag(int identifier) {
        return this.id == identifier || this.constrId == identifier;
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readString(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public Object getDecodedObject(BerInputStream in) throws IOException {
        return new String(in.buffer, in.contentOffset, in.length, StandardCharsets.ISO_8859_1);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeASN(BerOutputStream out) {
        out.encodeTag(this.id);
        encodeContent(out);
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        out.encodeString();
    }

    @Override // org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        byte[] bytes = ((String) out.content).getBytes(StandardCharsets.UTF_8);
        out.content = bytes;
        out.length = bytes.length;
    }
}