package org.apache.http.impl.cookie;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DateUtils.class */
public final class DateUtils {
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final TimeZone GMT = null;

    DateUtils() {
        throw new RuntimeException("Stub!");
    }

    public static Date parseDate(String dateValue) throws DateParseException {
        throw new RuntimeException("Stub!");
    }

    public static Date parseDate(String dateValue, String[] dateFormats) throws DateParseException {
        throw new RuntimeException("Stub!");
    }

    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) throws DateParseException {
        throw new RuntimeException("Stub!");
    }

    public static String formatDate(Date date) {
        throw new RuntimeException("Stub!");
    }

    public static String formatDate(Date date, String pattern) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: DateUtils$DateFormatHolder.class */
    static final class DateFormatHolder {
        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() { // from class: org.apache.http.impl.cookie.DateUtils.DateFormatHolder.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public SoftReference<Map<String, SimpleDateFormat>> initialValue() {
                return new SoftReference<>(new HashMap());
            }
        };

        DateFormatHolder() {
        }

        public static SimpleDateFormat formatFor(String pattern) {
            SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
            Map<String, SimpleDateFormat> formats = ref.get();
            if (formats == null) {
                formats = new HashMap<>();
                THREADLOCAL_FORMATS.set(new SoftReference<>(formats));
            }
            SimpleDateFormat format = formats.get(pattern);
            if (format == null) {
                format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                formats.put(pattern, format);
            }
            return format;
        }
    }
}