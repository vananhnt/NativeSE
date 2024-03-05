package org.apache.harmony.security.x509.tsp;

import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

/* loaded from: MessageImprint.class */
public class MessageImprint {
    private final AlgorithmIdentifier algId;
    private final byte[] hashedMessage;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{AlgorithmIdentifier.ASN1, ASN1OctetString.getInstance()}) { // from class: org.apache.harmony.security.x509.tsp.MessageImprint.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new MessageImprint((AlgorithmIdentifier) values[0], (byte[]) values[1]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            MessageImprint mi = (MessageImprint) object;
            values[0] = mi.algId;
            values[1] = mi.hashedMessage;
        }
    };

    public MessageImprint(AlgorithmIdentifier algId, byte[] hashedMessage) {
        this.algId = algId;
        this.hashedMessage = hashedMessage;
    }
}