package org.apache.harmony.security.x509;

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Oid;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.ObjectIdentifier;

/* loaded from: PolicyInformation.class */
public final class PolicyInformation {
    private final String policyIdentifier;
    private byte[] encoding;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Oid.getInstance(), ASN1Any.getInstance()}) { // from class: org.apache.harmony.security.x509.PolicyInformation.1
        {
            setOptional(1);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new PolicyInformation(ObjectIdentifier.toString((int[]) values[0]));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            PolicyInformation pi = (PolicyInformation) object;
            values[0] = ObjectIdentifier.toIntArray(pi.policyIdentifier);
        }
    };

    public PolicyInformation(String policyIdentifier) {
        this.policyIdentifier = policyIdentifier;
    }

    public String getPolicyIdentifier() {
        return this.policyIdentifier;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public void dumpValue(StringBuilder sb) {
        sb.append("Policy Identifier [").append(this.policyIdentifier).append(']');
    }
}