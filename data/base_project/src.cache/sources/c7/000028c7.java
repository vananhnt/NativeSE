package libcore.icu;

import android.text.format.Time;
import gov.nist.core.Separators;
import gov.nist.javax.sip.parser.TokenNames;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.util.BasicLruCache;

/* loaded from: DateIntervalFormat.class */
public final class DateIntervalFormat {
    public static final int FORMAT_SHOW_TIME = 1;
    public static final int FORMAT_SHOW_WEEKDAY = 2;
    public static final int FORMAT_SHOW_YEAR = 4;
    public static final int FORMAT_NO_YEAR = 8;
    public static final int FORMAT_SHOW_DATE = 16;
    public static final int FORMAT_NO_MONTH_DAY = 32;
    public static final int FORMAT_12HOUR = 64;
    public static final int FORMAT_24HOUR = 128;
    public static final int FORMAT_UTC = 8192;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 32768;
    public static final int FORMAT_ABBREV_MONTH = 65536;
    public static final int FORMAT_NUMERIC_DATE = 131072;
    public static final int FORMAT_ABBREV_ALL = 524288;
    private static final int DAY_IN_MS = 86400000;
    private static final int EPOCH_JULIAN_DAY = 2440588;
    private static final FormatterCache CACHED_FORMATTERS = new FormatterCache();

    private static native long createDateIntervalFormat(String str, String str2, String str3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void destroyDateIntervalFormat(long j);

    private static native String formatDateInterval(long j, long j2, long j3);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: DateIntervalFormat$FormatterCache.class */
    public static class FormatterCache extends BasicLruCache<String, Long> {
        FormatterCache() {
            super(8);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // libcore.util.BasicLruCache
        public void entryEvicted(String key, Long value) {
            DateIntervalFormat.destroyDateIntervalFormat(value.longValue());
        }
    }

    private DateIntervalFormat() {
    }

    public static String formatDateRange(long startMs, long endMs, int flags, String olsonId) {
        if ((flags & 8192) != 0) {
            olsonId = Time.TIMEZONE_UTC;
        }
        TimeZone tz = olsonId != null ? TimeZone.getTimeZone(olsonId) : TimeZone.getDefault();
        return formatDateRange(Locale.getDefault(), tz, startMs, endMs, flags);
    }

    public static String formatDateRange(Locale locale, TimeZone tz, long startMs, long endMs, int flags) {
        Calendar endCalendar;
        String formatDateInterval;
        Calendar startCalendar = Calendar.getInstance(tz);
        startCalendar.setTimeInMillis(startMs);
        if (startMs == endMs) {
            endCalendar = startCalendar;
        } else {
            endCalendar = Calendar.getInstance(tz);
            endCalendar.setTimeInMillis(endMs);
        }
        boolean endsAtMidnight = isMidnight(endCalendar);
        if (startMs != endMs && endsAtMidnight && ((flags & 1) == 0 || julianDay(startCalendar) == julianDay(endCalendar))) {
            endCalendar.roll(5, false);
            endMs -= 86400000;
        }
        String skeleton = toSkeleton(startCalendar, endCalendar, flags);
        synchronized (CACHED_FORMATTERS) {
            formatDateInterval = formatDateInterval(getFormatter(skeleton, locale.toString(), tz.getID()), startMs, endMs);
        }
        return formatDateInterval;
    }

    private static long getFormatter(String skeleton, String localeName, String tzName) {
        String key = skeleton + Separators.HT + localeName + Separators.HT + tzName;
        Long formatter = CACHED_FORMATTERS.get(key);
        if (formatter != null) {
            return formatter.longValue();
        }
        long address = createDateIntervalFormat(skeleton, localeName, tzName);
        CACHED_FORMATTERS.put(key, Long.valueOf(address));
        return address;
    }

    private static String toSkeleton(Calendar startCalendar, Calendar endCalendar, int flags) {
        if ((flags & 524288) != 0) {
            flags |= 114688;
        }
        String monthPart = "MMMM";
        if ((flags & 131072) != 0) {
            monthPart = TokenNames.M;
        } else if ((flags & 65536) != 0) {
            monthPart = "MMM";
        }
        String weekPart = "EEEE";
        if ((flags & 32768) != 0) {
            weekPart = "EEE";
        }
        String timePart = "j";
        if ((flags & 128) != 0) {
            timePart = "H";
        } else if ((flags & 64) != 0) {
            timePart = "h";
        }
        if ((flags & 16384) == 0 || (flags & 128) != 0) {
            timePart = timePart + "m";
        } else if (!onTheHour(startCalendar) || !onTheHour(endCalendar)) {
            timePart = timePart + "m";
        }
        if (fallOnDifferentDates(startCalendar, endCalendar)) {
            flags |= 16;
        }
        if (fallInSameMonth(startCalendar, endCalendar) && (flags & 32) != 0) {
            flags = flags & (-3) & (-2);
        }
        if ((flags & 19) == 0) {
            flags |= 16;
        }
        if ((flags & 16) != 0 && (flags & 4) == 0 && (flags & 8) == 0 && (!fallInSameYear(startCalendar, endCalendar) || !isThisYear(startCalendar))) {
            flags |= 4;
        }
        StringBuilder builder = new StringBuilder();
        if ((flags & 48) != 0) {
            if ((flags & 4) != 0) {
                builder.append("y");
            }
            builder.append(monthPart);
            if ((flags & 32) == 0) {
                builder.append("d");
            }
        }
        if ((flags & 2) != 0) {
            builder.append(weekPart);
        }
        if ((flags & 1) != 0) {
            builder.append(timePart);
        }
        return builder.toString();
    }

    private static boolean isMidnight(Calendar c) {
        return c.get(11) == 0 && c.get(12) == 0 && c.get(13) == 0 && c.get(14) == 0;
    }

    private static boolean onTheHour(Calendar c) {
        return c.get(12) == 0 && c.get(13) == 0;
    }

    private static boolean fallOnDifferentDates(Calendar c1, Calendar c2) {
        return (c1.get(1) == c2.get(1) && c1.get(2) == c2.get(2) && c1.get(5) == c2.get(5)) ? false : true;
    }

    private static boolean fallInSameMonth(Calendar c1, Calendar c2) {
        return c1.get(2) == c2.get(2);
    }

    private static boolean fallInSameYear(Calendar c1, Calendar c2) {
        return c1.get(1) == c2.get(1);
    }

    private static boolean isThisYear(Calendar c) {
        Calendar now = Calendar.getInstance(c.getTimeZone());
        return c.get(1) == now.get(1);
    }

    private static int julianDay(Calendar c) {
        long utcMs = c.get(14) + c.get(15);
        return ((int) (utcMs / 86400000)) + 2440588;
    }
}