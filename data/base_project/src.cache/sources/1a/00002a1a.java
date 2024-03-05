package org.apache.harmony.security.x509;

import java.util.Date;
import org.apache.harmony.security.asn1.ASN1Choice;
import org.apache.harmony.security.asn1.ASN1GeneralizedTime;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.ASN1UTCTime;

/* loaded from: Time.class */
public final class Time {
    private static final long JAN_01_2050 = 2524608000000L;
    public static final ASN1Choice ASN1 = new ASN1Choice(new ASN1Type[]{ASN1GeneralizedTime.getInstance(), ASN1UTCTime.getInstance()}) { // from class: org.apache.harmony.security.x509.Time.1
        @Override // org.apache.harmony.security.asn1.ASN1Choice
        public int getIndex(Object object) {
            if (((Date) object).getTime() < Time.JAN_01_2050) {
                return 1;
            }
            return 0;
        }

        @Override // org.apache.harmony.security.asn1.ASN1Choice
        public Object getObjectToEncode(Object object) {
            return object;
        }
    };
}