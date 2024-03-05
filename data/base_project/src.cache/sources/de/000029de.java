package org.apache.harmony.security.x509;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: CertificatePolicies.class */
public final class CertificatePolicies extends ExtensionValue {
    private List<PolicyInformation> policyInformations;
    private byte[] encoding;
    public static final ASN1Type ASN1 = new ASN1SequenceOf(PolicyInformation.ASN1) { // from class: org.apache.harmony.security.x509.CertificatePolicies.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return new CertificatePolicies((List) in.content, in.getEncoded());
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection getValues(Object object) {
            CertificatePolicies cps = (CertificatePolicies) object;
            return cps.policyInformations;
        }
    };

    public CertificatePolicies() {
    }

    public static CertificatePolicies decode(byte[] encoding) throws IOException {
        CertificatePolicies cps = (CertificatePolicies) ASN1.decode(encoding);
        cps.encoding = encoding;
        return cps;
    }

    private CertificatePolicies(List<PolicyInformation> policyInformations, byte[] encoding) {
        this.policyInformations = policyInformations;
        this.encoding = encoding;
    }

    public List<PolicyInformation> getPolicyInformations() {
        return new ArrayList(this.policyInformations);
    }

    public CertificatePolicies addPolicyInformation(PolicyInformation policyInformation) {
        this.encoding = null;
        if (this.policyInformations == null) {
            this.policyInformations = new ArrayList();
        }
        this.policyInformations.add(policyInformation);
        return this;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("CertificatePolicies [\n");
        for (PolicyInformation policyInformation : this.policyInformations) {
            sb.append(prefix);
            sb.append("  ");
            policyInformation.dumpValue(sb);
            sb.append('\n');
        }
        sb.append(prefix).append("]\n");
    }
}