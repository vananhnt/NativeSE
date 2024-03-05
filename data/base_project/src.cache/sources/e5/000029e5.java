package org.apache.harmony.security.x509;

import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.DirectoryString;

/* loaded from: EDIPartyName.class */
public final class EDIPartyName {
    private final String nameAssigner;
    private final String partyName;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{new ASN1Explicit(0, DirectoryString.ASN1), new ASN1Explicit(1, DirectoryString.ASN1)}) { // from class: org.apache.harmony.security.x509.EDIPartyName.1
        {
            setOptional(0);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new EDIPartyName((String) values[0], (String) values[1], in.getEncoded());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            EDIPartyName epn = (EDIPartyName) object;
            values[0] = epn.nameAssigner;
            values[1] = epn.partyName;
        }
    };

    private EDIPartyName(String nameAssigner, String partyName, byte[] encoding) {
        this.nameAssigner = nameAssigner;
        this.partyName = partyName;
        this.encoding = encoding;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }
}