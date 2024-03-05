package org.apache.harmony.security.pkcs7;

import java.util.List;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.AttributeTypeAndValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AuthenticatedAttributes.class */
public final class AuthenticatedAttributes {
    private byte[] encoding;
    private final List<AttributeTypeAndValue> authenticatedAttributes;
    public static final ASN1SetOf ASN1 = new ASN1SetOf(AttributeTypeAndValue.ASN1) { // from class: org.apache.harmony.security.pkcs7.AuthenticatedAttributes.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return new AuthenticatedAttributes(in.getEncoded(), (List) in.content);
        }
    };

    private AuthenticatedAttributes(byte[] encoding, List<AttributeTypeAndValue> authenticatedAttributes) {
        this.encoding = encoding;
        this.authenticatedAttributes = authenticatedAttributes;
    }

    public List<AttributeTypeAndValue> getAttributes() {
        return this.authenticatedAttributes;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}