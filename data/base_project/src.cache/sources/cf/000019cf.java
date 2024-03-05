package com.android.internal.http;

import android.text.format.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: HttpDateTime.class */
public final class HttpDateTime {
    private static final String HTTP_DATE_RFC_REGEXP = "([0-9]{1,2})[- ]([A-Za-z]{3,9})[- ]([0-9]{2,4})[ ]([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])";
    private static final Pattern HTTP_DATE_RFC_PATTERN = Pattern.compile(HTTP_DATE_RFC_REGEXP);
    private static final String HTTP_DATE_ANSIC_REGEXP = "[ ]([A-Za-z]{3,9})[ ]+([0-9]{1,2})[ ]([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])[ ]([0-9]{2,4})";
    private static final Pattern HTTP_DATE_ANSIC_PATTERN = Pattern.compile(HTTP_DATE_ANSIC_REGEXP);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: HttpDateTime$TimeOfDay.class */
    public static class TimeOfDay {
        int hour;
        int minute;
        int second;

        TimeOfDay(int h, int m, int s) {
            this.hour = h;
            this.minute = m;
            this.second = s;
        }
    }

    public static long parse(String timeString) throws IllegalArgumentException {
        int month;
        int date;
        TimeOfDay timeOfDay;
        int year;
        Matcher rfcMatcher = HTTP_DATE_RFC_PATTERN.matcher(timeString);
        if (rfcMatcher.find()) {
            date = getDate(rfcMatcher.group(1));
            month = getMonth(rfcMatcher.group(2));
            year = getYear(rfcMatcher.group(3));
            timeOfDay = getTime(rfcMatcher.group(4));
        } else {
            Matcher ansicMatcher = HTTP_DATE_ANSIC_PATTERN.matcher(timeString);
            if (ansicMatcher.find()) {
                month = getMonth(ansicMatcher.group(1));
                date = getDate(ansicMatcher.group(2));
                timeOfDay = getTime(ansicMatcher.group(3));
                year = getYear(ansicMatcher.group(4));
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (year >= 2038) {
            year = 2038;
            month = 0;
            date = 1;
        }
        Time time = new Time(Time.TIMEZONE_UTC);
        time.set(timeOfDay.second, timeOfDay.minute, timeOfDay.hour, date, month, year);
        return time.toMillis(false);
    }

    private static int getDate(String dateString) {
        if (dateString.length() == 2) {
            return ((dateString.charAt(0) - '0') * 10) + (dateString.charAt(1) - '0');
        }
        return dateString.charAt(0) - '0';
    }

    private static int getMonth(String monthString) {
        int hash = ((Character.toLowerCase(monthString.charAt(0)) + Character.toLowerCase(monthString.charAt(1))) + Character.toLowerCase(monthString.charAt(2))) - 291;
        switch (hash) {
            case 9:
                return 11;
            case 10:
                return 1;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 23:
            case 24:
            case 25:
            case 27:
            case 28:
            case 30:
            case 31:
            case 33:
            case 34:
            case 38:
            case 39:
            case 41:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            default:
                throw new IllegalArgumentException();
            case 22:
                return 0;
            case 26:
                return 7;
            case 29:
                return 2;
            case 32:
                return 3;
            case 35:
                return 9;
            case 36:
                return 4;
            case 37:
                return 8;
            case 40:
                return 6;
            case 42:
                return 5;
            case 48:
                return 10;
        }
    }

    private static int getYear(String yearString) {
        if (yearString.length() == 2) {
            int year = ((yearString.charAt(0) - '0') * 10) + (yearString.charAt(1) - '0');
            if (year >= 70) {
                return year + 1900;
            }
            return year + 2000;
        } else if (yearString.length() == 3) {
            return ((yearString.charAt(0) - '0') * 100) + ((yearString.charAt(1) - '0') * 10) + (yearString.charAt(2) - '0') + 1900;
        } else {
            if (yearString.length() == 4) {
                return ((yearString.charAt(0) - '0') * 1000) + ((yearString.charAt(1) - '0') * 100) + ((yearString.charAt(2) - '0') * 10) + (yearString.charAt(3) - '0');
            }
            return 1970;
        }
    }

    private static TimeOfDay getTime(String timeString) {
        int i = 0 + 1;
        int hour = timeString.charAt(0) - '0';
        if (timeString.charAt(i) != ':') {
            i++;
            hour = (hour * 10) + (timeString.charAt(i) - '0');
        }
        int i2 = i + 1;
        int i3 = i2 + 1;
        int minute = ((timeString.charAt(i2) - '0') * 10) + (timeString.charAt(i3) - '0');
        int i4 = i3 + 1 + 1;
        int i5 = i4 + 1;
        int i6 = i5 + 1;
        int second = ((timeString.charAt(i4) - '0') * 10) + (timeString.charAt(i5) - '0');
        return new TimeOfDay(hour, minute, second);
    }
}