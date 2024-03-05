package org.apache.harmony.security.asn1;

import android.text.format.Time;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: ASN1UTCTime.class */
public final class ASN1UTCTime extends ASN1Time {
    public static final int UTC_HM = 11;
    public static final int UTC_HMS = 13;
    public static final int UTC_LOCAL_HM = 15;
    public static final int UTC_LOCAL_HMS = 17;
    private static final ASN1UTCTime ASN1 = new ASN1UTCTime();
    private static final String UTC_PATTERN = "yyMMddHHmmss'Z'";

    public ASN1UTCTime() {
        super(23);
    }

    public static ASN1UTCTime getInstance() {
        return ASN1;
    }

    @Override // org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
    public Object decode(BerInputStream in) throws IOException {
        in.readUTCTime();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    @Override // org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
    public void encodeContent(BerOutputStream out) {
        out.encodeUTCTime();
    }

    @Override // org.apache.harmony.security.asn1.ASN1StringType, org.apache.harmony.security.asn1.ASN1Type
    public void setEncodingContent(BerOutputStream out) {
        SimpleDateFormat sdf = new SimpleDateFormat(UTC_PATTERN, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        out.content = sdf.format(out.content).getBytes(StandardCharsets.UTF_8);
        out.length = ((byte[]) out.content).length;
    }
}