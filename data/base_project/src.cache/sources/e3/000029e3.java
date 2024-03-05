package org.apache.harmony.security.x509;

import java.io.IOException;
import org.apache.harmony.security.asn1.ASN1Choice;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.Name;

/* loaded from: DistributionPointName.class */
public final class DistributionPointName {
    private final GeneralNames fullName;
    private final Name nameRelativeToCRLIssuer;
    public static final ASN1Choice ASN1 = new ASN1Choice(new ASN1Type[]{new ASN1Implicit(0, GeneralNames.ASN1), new ASN1Implicit(1, Name.ASN1_RDN)}) { // from class: org.apache.harmony.security.x509.DistributionPointName.1
        @Override // org.apache.harmony.security.asn1.ASN1Choice
        public int getIndex(Object object) {
            DistributionPointName dpn = (DistributionPointName) object;
            return dpn.fullName == null ? 1 : 0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) throws IOException {
            DistributionPointName result;
            if (in.choiceIndex == 0) {
                result = new DistributionPointName((GeneralNames) in.content);
            } else {
                result = new DistributionPointName((Name) in.content);
            }
            return result;
        }

        @Override // org.apache.harmony.security.asn1.ASN1Choice
        public Object getObjectToEncode(Object object) {
            DistributionPointName dpn = (DistributionPointName) object;
            return dpn.fullName == null ? dpn.nameRelativeToCRLIssuer : dpn.fullName;
        }
    };

    public DistributionPointName(GeneralNames fullName) {
        this.fullName = fullName;
        this.nameRelativeToCRLIssuer = null;
    }

    public DistributionPointName(Name nameRelativeToCRLIssuer) {
        this.fullName = null;
        this.nameRelativeToCRLIssuer = nameRelativeToCRLIssuer;
    }

    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix);
        sb.append("Distribution Point Name: [\n");
        if (this.fullName != null) {
            this.fullName.dumpValue(sb, prefix + "  ");
        } else {
            sb.append(prefix);
            sb.append("  ");
            sb.append(this.nameRelativeToCRLIssuer.getName("RFC2253"));
        }
        sb.append(prefix);
        sb.append("]\n");
    }
}