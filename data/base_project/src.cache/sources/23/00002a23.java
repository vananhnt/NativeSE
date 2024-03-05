package org.apache.harmony.security.x509.tsp;

import java.math.BigInteger;
import java.util.List;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;

/* loaded from: PKIStatusInfo.class */
public class PKIStatusInfo {
    private final PKIStatus status;
    private final List statusString;
    private final PKIFailureInfo failInfo;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{ASN1Integer.getInstance(), new ASN1SequenceOf(ASN1StringType.UTF8STRING), ASN1BitString.getInstance()}) { // from class: org.apache.harmony.security.x509.tsp.PKIStatusInfo.1
        {
            setOptional(1);
            setOptional(2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            PKIStatusInfo psi = (PKIStatusInfo) object;
            values[0] = BigInteger.valueOf(psi.status.getStatus()).toByteArray();
            values[1] = psi.statusString;
            if (psi.failInfo != null) {
                boolean[] failInfoBoolArray = new boolean[PKIFailureInfo.getMaxValue()];
                failInfoBoolArray[psi.failInfo.getValue()] = true;
                values[2] = new BitString(failInfoBoolArray);
                return;
            }
            values[2] = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            int failInfoValue = -1;
            if (values[2] != null) {
                boolean[] failInfoBoolArray = ((BitString) values[2]).toBooleanArray();
                int i = 0;
                while (true) {
                    if (i >= failInfoBoolArray.length) {
                        break;
                    } else if (!failInfoBoolArray[i]) {
                        i++;
                    } else {
                        failInfoValue = i;
                        break;
                    }
                }
            }
            return new PKIStatusInfo(PKIStatus.getInstance(ASN1Integer.toIntValue(values[0])), (List) values[1], PKIFailureInfo.getInstance(failInfoValue));
        }
    };

    public PKIStatusInfo(PKIStatus pKIStatus, List statusString, PKIFailureInfo failInfo) {
        this.status = pKIStatus;
        this.statusString = statusString;
        this.failInfo = failInfo;
    }

    public String toString() {
        return "-- PKIStatusInfo:\nPKIStatus : " + this.status + "\nstatusString:  " + this.statusString + "\nfailInfo:  " + this.failInfo + "\n-- PKIStatusInfo End\n";
    }

    public PKIFailureInfo getFailInfo() {
        return this.failInfo;
    }

    public PKIStatus getStatus() {
        return this.status;
    }

    public List getStatusString() {
        return this.statusString;
    }
}