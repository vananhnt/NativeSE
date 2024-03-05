package org.apache.harmony.security.x509.tsp;

import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;

/* loaded from: TimeStampResp.class */
public class TimeStampResp {
    private final PKIStatusInfo status;
    private final ContentInfo timeStampToken;
    public static final ASN1Sequence ASN1 = new ASN1Sequence(new ASN1Type[]{PKIStatusInfo.ASN1, ContentInfo.ASN1}) { // from class: org.apache.harmony.security.x509.tsp.TimeStampResp.1
        {
            setOptional(1);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1Type
        public Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new TimeStampResp((PKIStatusInfo) values[0], (ContentInfo) values[1]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.harmony.security.asn1.ASN1TypeCollection
        public void getValues(Object object, Object[] values) {
            TimeStampResp resp = (TimeStampResp) object;
            values[0] = resp.status;
            values[1] = resp.timeStampToken;
        }
    };

    public TimeStampResp(PKIStatusInfo status, ContentInfo timeStampToken) {
        this.status = status;
        this.timeStampToken = timeStampToken;
    }

    public String toString() {
        return "-- TimeStampResp:\nstatus:  " + this.status + "\ntimeStampToken:  " + this.timeStampToken + "\n-- TimeStampResp End\n";
    }

    public PKIStatusInfo getStatus() {
        return this.status;
    }

    public ContentInfo getTimeStampToken() {
        return this.timeStampToken;
    }
}