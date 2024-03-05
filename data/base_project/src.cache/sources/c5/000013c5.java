package android.text.format;

import android.content.res.Resources;
import android.net.wifi.BatchedScanSettings;
import com.android.internal.R;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.LocaleData;

/* loaded from: Time.class */
public class Time {
    private static final String Y_M_D_T_H_M_S_000 = "%Y-%m-%dT%H:%M:%S.000";
    private static final String Y_M_D_T_H_M_S_000_Z = "%Y-%m-%dT%H:%M:%S.000Z";
    private static final String Y_M_D = "%Y-%m-%d";
    public static final String TIMEZONE_UTC = "UTC";
    public static final int EPOCH_JULIAN_DAY = 2440588;
    public static final int MONDAY_BEFORE_JULIAN_EPOCH = 2440585;
    public boolean allDay;
    public int second;
    public int minute;
    public int hour;
    public int monthDay;
    public int month;
    public int year;
    public int weekDay;
    public int yearDay;
    public int isDst;
    public long gmtoff;
    public String timezone;
    public static final int SECOND = 1;
    public static final int MINUTE = 2;
    public static final int HOUR = 3;
    public static final int MONTH_DAY = 4;
    public static final int MONTH = 5;
    public static final int YEAR = 6;
    public static final int WEEK_DAY = 7;
    public static final int YEAR_DAY = 8;
    public static final int WEEK_NUM = 9;
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    private static Locale sLocale;
    private static String[] sShortMonths;
    private static String[] sLongMonths;
    private static String[] sLongStandaloneMonths;
    private static String[] sShortWeekdays;
    private static String[] sLongWeekdays;
    private static String sTimeOnlyFormat;
    private static String sDateOnlyFormat;
    private static String sDateTimeFormat;
    private static String sAm;
    private static String sPm;
    private static char sZeroDigit;
    private static String sDateCommand = "%a %b %e %H:%M:%S %Z %Y";
    private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] sThursdayOffset = {-3, 3, 2, 1, 0, -1, -2};

    public native long normalize(boolean z);

    public native void switchTimezone(String str);

    private static native int nativeCompare(Time time, Time time2);

    private native String format1(String str);

    public native String toString();

    private native boolean nativeParse(String str);

    private native boolean nativeParse3339(String str);

    public native void setToNow();

    public native long toMillis(boolean z);

    public native void set(long j);

    public native String format2445();

    public Time(String timezone) {
        if (timezone == null) {
            throw new NullPointerException("timezone is null!");
        }
        this.timezone = timezone;
        this.year = 1970;
        this.monthDay = 1;
        this.isDst = -1;
    }

    public Time() {
        this(TimeZone.getDefault().getID());
    }

    public Time(Time other) {
        set(other);
    }

    public int getActualMaximum(int field) {
        switch (field) {
            case 1:
                return 59;
            case 2:
                return 59;
            case 3:
                return 23;
            case 4:
                int n = DAYS_PER_MONTH[this.month];
                if (n != 28) {
                    return n;
                }
                int y = this.year;
                return (y % 4 != 0 || (y % 100 == 0 && y % 400 != 0)) ? 28 : 29;
            case 5:
                return 11;
            case 6:
                return 2037;
            case 7:
                return 6;
            case 8:
                int y2 = this.year;
                return (y2 % 4 != 0 || (y2 % 100 == 0 && y2 % 400 != 0)) ? 364 : 365;
            case 9:
                throw new RuntimeException("WEEK_NUM not implemented");
            default:
                throw new RuntimeException("bad field=" + field);
        }
    }

    public void clear(String timezone) {
        if (timezone == null) {
            throw new NullPointerException("timezone is null!");
        }
        this.timezone = timezone;
        this.allDay = false;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = 0;
        this.month = 0;
        this.year = 0;
        this.weekDay = 0;
        this.yearDay = 0;
        this.gmtoff = 0L;
        this.isDst = -1;
    }

    public static int compare(Time a, Time b) {
        if (a == null) {
            throw new NullPointerException("a == null");
        }
        if (b == null) {
            throw new NullPointerException("b == null");
        }
        return nativeCompare(a, b);
    }

    public String format(String format) {
        String str;
        synchronized (Time.class) {
            Locale locale = Locale.getDefault();
            if (sLocale == null || locale == null || !locale.equals(sLocale)) {
                LocaleData localeData = LocaleData.get(locale);
                sAm = localeData.amPm[0];
                sPm = localeData.amPm[1];
                sZeroDigit = localeData.zeroDigit;
                sShortMonths = localeData.shortMonthNames;
                sLongMonths = localeData.longMonthNames;
                sLongStandaloneMonths = localeData.longStandAloneMonthNames;
                sShortWeekdays = localeData.shortWeekdayNames;
                sLongWeekdays = localeData.longWeekdayNames;
                Resources r = Resources.getSystem();
                sTimeOnlyFormat = r.getString(R.string.time_of_day);
                sDateOnlyFormat = r.getString(R.string.month_day_year);
                sDateTimeFormat = r.getString(R.string.date_and_time);
                sLocale = locale;
            }
            String result = format1(format);
            if (sZeroDigit != '0') {
                result = localizeDigits(result);
            }
            str = result;
        }
        return str;
    }

    private String localizeDigits(String s) {
        int length = s.length();
        int offsetToLocalizedDigits = sZeroDigit - '0';
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ch = (char) (ch + offsetToLocalizedDigits);
            }
            result.append(ch);
        }
        return result.toString();
    }

    public boolean parse(String s) {
        if (s == null) {
            throw new NullPointerException("time string is null");
        }
        if (nativeParse(s)) {
            this.timezone = TIMEZONE_UTC;
            return true;
        }
        return false;
    }

    public boolean parse3339(String s) {
        if (s == null) {
            throw new NullPointerException("time string is null");
        }
        if (nativeParse3339(s)) {
            this.timezone = TIMEZONE_UTC;
            return true;
        }
        return false;
    }

    public static String getCurrentTimezone() {
        return TimeZone.getDefault().getID();
    }

    public void set(Time that) {
        this.timezone = that.timezone;
        this.allDay = that.allDay;
        this.second = that.second;
        this.minute = that.minute;
        this.hour = that.hour;
        this.monthDay = that.monthDay;
        this.month = that.month;
        this.year = that.year;
        this.weekDay = that.weekDay;
        this.yearDay = that.yearDay;
        this.isDst = that.isDst;
        this.gmtoff = that.gmtoff;
    }

    public void set(int second, int minute, int hour, int monthDay, int month, int year) {
        this.allDay = false;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public void set(int monthDay, int month, int year) {
        this.allDay = true;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public boolean before(Time that) {
        return compare(this, that) < 0;
    }

    public boolean after(Time that) {
        return compare(this, that) > 0;
    }

    public int getWeekNumber() {
        int closestThursday = this.yearDay + sThursdayOffset[this.weekDay];
        if (closestThursday >= 0 && closestThursday <= 364) {
            return (closestThursday / 7) + 1;
        }
        Time temp = new Time(this);
        temp.monthDay += sThursdayOffset[this.weekDay];
        temp.normalize(true);
        return (temp.yearDay / 7) + 1;
    }

    public String format3339(boolean allDay) {
        if (allDay) {
            return format(Y_M_D);
        }
        if (TIMEZONE_UTC.equals(this.timezone)) {
            return format(Y_M_D_T_H_M_S_000_Z);
        }
        String base = format(Y_M_D_T_H_M_S_000);
        String sign = this.gmtoff < 0 ? "-" : "+";
        int offset = (int) Math.abs(this.gmtoff);
        int minutes = (offset % BatchedScanSettings.MAX_INTERVAL_SEC) / 60;
        int hours = offset / BatchedScanSettings.MAX_INTERVAL_SEC;
        return String.format(Locale.US, "%s%s%02d:%02d", base, sign, Integer.valueOf(hours), Integer.valueOf(minutes));
    }

    public static boolean isEpoch(Time time) {
        long millis = time.toMillis(true);
        return getJulianDay(millis, 0L) == 2440588;
    }

    public static int getJulianDay(long millis, long gmtoff) {
        long offsetMillis = gmtoff * 1000;
        long julianDay = (millis + offsetMillis) / 86400000;
        return ((int) julianDay) + EPOCH_JULIAN_DAY;
    }

    public long setJulianDay(int julianDay) {
        long millis = (julianDay - EPOCH_JULIAN_DAY) * 86400000;
        set(millis);
        int approximateDay = getJulianDay(millis, this.gmtoff);
        int diff = julianDay - approximateDay;
        this.monthDay += diff;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        return normalize(true);
    }

    public static int getWeeksSinceEpochFromJulianDay(int julianDay, int firstDayOfWeek) {
        int diff = 4 - firstDayOfWeek;
        if (diff < 0) {
            diff += 7;
        }
        int refDay = EPOCH_JULIAN_DAY - diff;
        return (julianDay - refDay) / 7;
    }

    public static int getJulianMondayFromWeeksSinceEpoch(int week) {
        return MONDAY_BEFORE_JULIAN_EPOCH + (week * 7);
    }
}