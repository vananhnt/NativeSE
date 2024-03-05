package org.apache.harmony.security.x509;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

/* loaded from: CRLDistributionPoints.class */
public final class CRLDistributionPoints extends ExtensionValue {
    private List<DistributionPoint> distributionPoints;
    private byte[] encoding;
    public static final ASN1Type ASN1 = new ASN1SequenceOf(DistributionPoint.ASN1) { // from class: org.apache.harmony.security.x509.CRLDistributionPoints.1
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            return new CRLDistributionPoints((List) in.content, in.getEncoded());
        }

        @Override // org.apache.harmony.security.asn1.ASN1ValueCollection
        public Collection<?> getValues(Object object) {
            CRLDistributionPoints dps = (CRLDistributionPoints) object;
            return dps.distributionPoints;
        }
    };

    private CRLDistributionPoints(List<DistributionPoint> distributionPoints, byte[] encoding) {
        if (distributionPoints == null || distributionPoints.size() == 0) {
            throw new IllegalArgumentException("distributionPoints are empty");
        }
        this.distributionPoints = distributionPoints;
        this.encoding = encoding;
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public static CRLDistributionPoints decode(byte[] encoding) throws IOException {
        return (CRLDistributionPoints) ASN1.decode(encoding);
    }

    @Override // org.apache.harmony.security.x509.ExtensionValue
    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("CRL Distribution Points: [\n");
        int number = 0;
        for (DistributionPoint distributionPoint : this.distributionPoints) {
            number++;
            sb.append(prefix).append("  [").append(number).append("]\n");
            distributionPoint.dumpValue(sb, prefix + "  ");
        }
        sb.append(prefix).append("]\n");
    }
}