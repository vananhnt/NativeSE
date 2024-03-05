package org.apache.harmony.security.x509;

import org.apache.harmony.security.asn1.ASN1Oid;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.ObjectIdentifier;

/* loaded from: AccessDescription.class */
public final class AccessDescription {
    private final String accessMethod;
    private final GeneralName accessLocation;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Oid.getInstance(), GeneralName.ASN1}) { // from class: org.apache.harmony.security.x509.AccessDescription.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new AccessDescription(ObjectIdentifier.toString((int[]) values[0]), (GeneralName) values[1], in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            AccessDescription ad = (AccessDescription) object;
            values[0] = ObjectIdentifier.toIntArray(ad.accessMethod);
            values[1] = ad.accessLocation;
        }
    };

    private AccessDescription(String accessMethod, GeneralName accessLocation, byte[] encoding) {
        this.accessMethod = accessMethod;
        this.accessLocation = accessLocation;
        this.encoding = encoding;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public String toString() {
        return "\n-- AccessDescription:\naccessMethod:  " + this.accessMethod + "\naccessLocation:  " + this.accessLocation + "\n-- AccessDescription END\n";
    }
}