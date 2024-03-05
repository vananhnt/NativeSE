package libcore.net.http;

import android.text.format.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: HttpDate.class */
public final class HttpDate {
    private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() { // from class: libcore.net.http.HttpDate.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public DateFormat initialValue() {
            DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            rfc1123.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
            return rfc1123;
        }
    };
    private static final String[] BROWSER_COMPATIBLE_DATE_FORMATS = {"EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM d yyyy HH:mm:ss z"};

    public static Date parse(String value) {
        try {
            return STANDARD_DATE_FORMAT.get().parse(value);
        } catch (ParseException e) {
            String[] arr$ = BROWSER_COMPATIBLE_DATE_FORMATS;
            for (String formatString : arr$) {
                try {
                    return new SimpleDateFormat(formatString, Locale.US).parse(value);
                } catch (ParseException e2) {
                }
            }
            return null;
        }
    }

    public static String format(Date value) {
        return STANDARD_DATE_FORMAT.get().format(value);
    }
}