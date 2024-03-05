package android.text.format;

import android.content.Context;
import android.net.wifi.BatchedScanSettings;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import com.android.internal.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.ICU;
import libcore.icu.LocaleData;

/* loaded from: DateFormat.class */
public class DateFormat {
    @Deprecated
    public static final char QUOTE = '\'';
    @Deprecated
    public static final char AM_PM = 'a';
    @Deprecated
    public static final char CAPITAL_AM_PM = 'A';
    @Deprecated
    public static final char DATE = 'd';
    @Deprecated
    public static final char DAY = 'E';
    @Deprecated
    public static final char HOUR = 'h';
    @Deprecated
    public static final char HOUR_OF_DAY = 'k';
    @Deprecated
    public static final char MINUTE = 'm';
    @Deprecated
    public static final char MONTH = 'M';
    @Deprecated
    public static final char STANDALONE_MONTH = 'L';
    @Deprecated
    public static final char SECONDS = 's';
    @Deprecated
    public static final char TIME_ZONE = 'z';
    @Deprecated
    public static final char YEAR = 'y';
    private static final Object sLocaleLock = new Object();
    private static Locale sIs24HourLocale;
    private static boolean sIs24Hour;

    public static boolean is24HourFormat(Context context) {
        String value;
        String value2 = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);
        if (value2 == null) {
            Locale locale = context.getResources().getConfiguration().locale;
            synchronized (sLocaleLock) {
                if (sIs24HourLocale != null && sIs24HourLocale.equals(locale)) {
                    return sIs24Hour;
                }
                java.text.DateFormat natural = java.text.DateFormat.getTimeInstance(1, locale);
                if (natural instanceof SimpleDateFormat) {
                    SimpleDateFormat sdf = (SimpleDateFormat) natural;
                    String pattern = sdf.toPattern();
                    if (pattern.indexOf(72) >= 0) {
                        value = "24";
                    } else {
                        value = "12";
                    }
                } else {
                    value = "12";
                }
                synchronized (sLocaleLock) {
                    sIs24HourLocale = locale;
                    sIs24Hour = value.equals("24");
                }
                return sIs24Hour;
            }
        }
        return value2.equals("24");
    }

    public static String getBestDateTimePattern(Locale locale, String skeleton) {
        return ICU.getBestDateTimePattern(skeleton, locale.toString());
    }

    public static java.text.DateFormat getTimeFormat(Context context) {
        return new SimpleDateFormat(getTimeFormatString(context));
    }

    public static String getTimeFormatString(Context context) {
        LocaleData d = LocaleData.get(context.getResources().getConfiguration().locale);
        return is24HourFormat(context) ? d.timeFormat24 : d.timeFormat12;
    }

    public static java.text.DateFormat getDateFormat(Context context) {
        String value = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
        return getDateFormatForSetting(context, value);
    }

    public static java.text.DateFormat getDateFormatForSetting(Context context, String value) {
        String format = getDateFormatStringForSetting(context, value);
        return new SimpleDateFormat(format);
    }

    private static String getDateFormatStringForSetting(Context context, String value) {
        String value2;
        if (value != null) {
            int month = value.indexOf(77);
            int day = value.indexOf(100);
            int year = value.indexOf(121);
            if (month >= 0 && day >= 0 && year >= 0) {
                String template = context.getString(R.string.numeric_date_template);
                if (year < month && year < day) {
                    if (month < day) {
                        value2 = String.format(template, "yyyy", "MM", "dd");
                    } else {
                        value2 = String.format(template, "yyyy", "dd", "MM");
                    }
                } else if (month < day) {
                    if (day < year) {
                        value2 = String.format(template, "MM", "dd", "yyyy");
                    } else {
                        value2 = String.format(template, "MM", "yyyy", "dd");
                    }
                } else if (month < year) {
                    value2 = String.format(template, "dd", "MM", "yyyy");
                } else {
                    value2 = String.format(template, "dd", "yyyy", "MM");
                }
                return value2;
            }
        }
        LocaleData d = LocaleData.get(context.getResources().getConfiguration().locale);
        return d.shortDateFormat4;
    }

    public static java.text.DateFormat getLongDateFormat(Context context) {
        return java.text.DateFormat.getDateInstance(1);
    }

    public static java.text.DateFormat getMediumDateFormat(Context context) {
        return java.text.DateFormat.getDateInstance(2);
    }

    public static char[] getDateFormatOrder(Context context) {
        return ICU.getDateFormatOrder(getDateFormatString(context));
    }

    private static String getDateFormatString(Context context) {
        String value = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
        return getDateFormatStringForSetting(context, value);
    }

    public static CharSequence format(CharSequence inFormat, long inTimeInMillis) {
        return format(inFormat, new Date(inTimeInMillis));
    }

    public static CharSequence format(CharSequence inFormat, Date inDate) {
        Calendar c = new GregorianCalendar();
        c.setTime(inDate);
        return format(inFormat, c);
    }

    public static boolean hasSeconds(CharSequence inFormat) {
        return hasDesignator(inFormat, 's');
    }

    public static boolean hasDesignator(CharSequence inFormat, char designator) {
        if (inFormat == null) {
            return false;
        }
        int length = inFormat.length();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                int count = 1;
                int c = inFormat.charAt(i2);
                if (c == 39) {
                    count = skipQuotedText(inFormat, i2, length);
                } else if (c == designator) {
                    return true;
                }
                i = i2 + count;
            } else {
                return false;
            }
        }
    }

    private static int skipQuotedText(CharSequence s, int i, int len) {
        if (i + 1 < len && s.charAt(i + 1) == '\'') {
            return 2;
        }
        int count = 1;
        int i2 = i + 1;
        while (i2 < len) {
            char c = s.charAt(i2);
            if (c == '\'') {
                count++;
                if (i2 + 1 >= len || s.charAt(i2 + 1) != '\'') {
                    break;
                }
                i2++;
            } else {
                i2++;
                count++;
            }
        }
        return count;
    }

    public static CharSequence format(CharSequence inFormat, Calendar inDate) {
        String replacement;
        SpannableStringBuilder s = new SpannableStringBuilder(inFormat);
        LocaleData localeData = LocaleData.get(Locale.getDefault());
        int len = inFormat.length();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < len) {
                int count = 1;
                int c = s.charAt(i2);
                if (c == 39) {
                    count = appendQuotedText(s, i2, len);
                    len = s.length();
                } else {
                    while (i2 + count < len && s.charAt(i2 + count) == c) {
                        count++;
                    }
                    switch (c) {
                        case 65:
                        case 97:
                            replacement = localeData.amPm[inDate.get(9) - 0];
                            break;
                        case 66:
                        case 67:
                        case 68:
                        case 70:
                        case 71:
                        case 73:
                        case 74:
                        case 78:
                        case 79:
                        case 80:
                        case 81:
                        case 82:
                        case 83:
                        case 84:
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        case 90:
                        case 91:
                        case 92:
                        case 93:
                        case 94:
                        case 95:
                        case 96:
                        case 98:
                        case 101:
                        case 102:
                        case 103:
                        case 105:
                        case 106:
                        case 108:
                        case 110:
                        case 111:
                        case 112:
                        case 113:
                        case 114:
                        case 116:
                        case 117:
                        case 118:
                        case 119:
                        case 120:
                        default:
                            replacement = null;
                            break;
                        case 69:
                        case 99:
                            replacement = getDayOfWeekString(localeData, inDate.get(7), count, c);
                            break;
                        case 72:
                        case 107:
                            replacement = zeroPad(inDate.get(11), count);
                            break;
                        case 75:
                        case 104:
                            int hour = inDate.get(10);
                            if (c == 104 && hour == 0) {
                                hour = 12;
                            }
                            replacement = zeroPad(hour, count);
                            break;
                        case 76:
                        case 77:
                            replacement = getMonthString(localeData, inDate.get(2), count, c);
                            break;
                        case 100:
                            replacement = zeroPad(inDate.get(5), count);
                            break;
                        case 109:
                            replacement = zeroPad(inDate.get(12), count);
                            break;
                        case 115:
                            replacement = zeroPad(inDate.get(13), count);
                            break;
                        case 121:
                            replacement = getYearString(inDate.get(1), count);
                            break;
                        case 122:
                            replacement = getTimeZoneString(inDate, count);
                            break;
                    }
                    if (replacement != null) {
                        s.replace(i2, i2 + count, (CharSequence) replacement);
                        count = replacement.length();
                        len = s.length();
                    }
                }
                i = i2 + count;
            } else if (inFormat instanceof Spanned) {
                return new SpannedString(s);
            } else {
                return s.toString();
            }
        }
    }

    private static String getDayOfWeekString(LocaleData ld, int day, int count, int kind) {
        boolean standalone = kind == 99;
        return count == 5 ? standalone ? ld.tinyStandAloneWeekdayNames[day] : ld.tinyWeekdayNames[day] : count == 4 ? standalone ? ld.longStandAloneWeekdayNames[day] : ld.longWeekdayNames[day] : standalone ? ld.shortStandAloneWeekdayNames[day] : ld.shortWeekdayNames[day];
    }

    private static String getMonthString(LocaleData ld, int month, int count, int kind) {
        boolean standalone = kind == 76;
        if (count == 5) {
            return standalone ? ld.tinyStandAloneMonthNames[month] : ld.tinyMonthNames[month];
        } else if (count == 4) {
            return standalone ? ld.longStandAloneMonthNames[month] : ld.longMonthNames[month];
        } else if (count == 3) {
            return standalone ? ld.shortStandAloneMonthNames[month] : ld.shortMonthNames[month];
        } else {
            return zeroPad(month + 1, count);
        }
    }

    private static String getTimeZoneString(Calendar inDate, int count) {
        TimeZone tz = inDate.getTimeZone();
        if (count < 2) {
            return formatZoneOffset(inDate.get(16) + inDate.get(15), count);
        }
        boolean dst = inDate.get(16) != 0;
        return tz.getDisplayName(dst, 0);
    }

    private static String formatZoneOffset(int offset, int count) {
        int offset2 = offset / 1000;
        StringBuilder tb = new StringBuilder();
        if (offset2 < 0) {
            tb.insert(0, "-");
            offset2 = -offset2;
        } else {
            tb.insert(0, "+");
        }
        int hours = offset2 / BatchedScanSettings.MAX_INTERVAL_SEC;
        int minutes = (offset2 % BatchedScanSettings.MAX_INTERVAL_SEC) / 60;
        tb.append(zeroPad(hours, 2));
        tb.append(zeroPad(minutes, 2));
        return tb.toString();
    }

    private static String getYearString(int year, int count) {
        return count <= 2 ? zeroPad(year % 100, 2) : String.format(Locale.getDefault(), "%d", Integer.valueOf(year));
    }

    private static int appendQuotedText(SpannableStringBuilder s, int i, int len) {
        if (i + 1 < len && s.charAt(i + 1) == '\'') {
            s.delete(i, i + 1);
            return 1;
        }
        int count = 0;
        s.delete(i, i + 1);
        int len2 = len - 1;
        while (i < len2) {
            char c = s.charAt(i);
            if (c == '\'') {
                if (i + 1 < len2 && s.charAt(i + 1) == '\'') {
                    s.delete(i, i + 1);
                    len2--;
                    count++;
                    i++;
                } else {
                    s.delete(i, i + 1);
                    break;
                }
            } else {
                i++;
                count++;
            }
        }
        return count;
    }

    private static String zeroPad(int inValue, int inMinDigits) {
        return String.format(Locale.getDefault(), "%0" + inMinDigits + "d", Integer.valueOf(inValue));
    }
}