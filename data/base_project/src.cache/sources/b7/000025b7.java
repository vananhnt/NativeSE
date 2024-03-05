package java.util;

/* loaded from: Grego.class */
class Grego {
    public static final long MIN_MILLIS = -184303902528000000L;
    public static final long MAX_MILLIS = 183882168921600000L;
    public static final int MILLIS_PER_SECOND = 1000;
    public static final int MILLIS_PER_MINUTE = 60000;
    public static final int MILLIS_PER_HOUR = 3600000;
    public static final int MILLIS_PER_DAY = 86400000;
    private static final int JULIAN_1_CE = 1721426;
    private static final int JULIAN_1970_CE = 2440588;
    private static final int[] MONTH_LENGTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] DAYS_BEFORE = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    Grego() {
    }

    public static final boolean isLeapYear(int year) {
        return (year & 3) == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public static final int monthLength(int year, int month) {
        return MONTH_LENGTH[month + (isLeapYear(year) ? 12 : 0)];
    }

    public static final int previousMonthLength(int year, int month) {
        if (month > 0) {
            return monthLength(year, month - 1);
        }
        return 31;
    }

    public static long fieldsToDay(int year, int month, int dom) {
        int y = year - 1;
        long julian = (((((365 * y) + floorDivide(y, 4L)) + 1721423) + floorDivide(y, 400L)) - floorDivide(y, 100L)) + 2 + DAYS_BEFORE[month + (isLeapYear(year) ? 12 : 0)] + dom;
        return julian - 2440588;
    }

    public static int dayOfWeek(long day) {
        long[] remainder = new long[1];
        floorDivide(day + 5, 7L, remainder);
        int dayOfWeek = (int) remainder[0];
        return dayOfWeek == 0 ? 7 : dayOfWeek;
    }

    public static int[] dayToFields(long day, int[] fields) {
        if (fields == null || fields.length < 5) {
            fields = new int[5];
        }
        long day2 = day + 719162;
        long[] rem = new long[1];
        long n400 = floorDivide(day2, 146097L, rem);
        long n100 = floorDivide(rem[0], 36524L, rem);
        long n4 = floorDivide(rem[0], 1461L, rem);
        long n1 = floorDivide(rem[0], 365L, rem);
        int year = (int) ((400 * n400) + (100 * n100) + (4 * n4) + n1);
        int dayOfYear = (int) rem[0];
        if (n100 == 4 || n1 == 4) {
            dayOfYear = 365;
        } else {
            year++;
        }
        boolean isLeap = isLeapYear(year);
        int correction = 0;
        int march1 = isLeap ? 60 : 59;
        if (dayOfYear >= march1) {
            correction = isLeap ? 1 : 2;
        }
        int month = ((12 * (dayOfYear + correction)) + 6) / 367;
        int dayOfMonth = (dayOfYear - DAYS_BEFORE[isLeap ? month + 12 : month]) + 1;
        int dayOfWeek = (int) ((day2 + 2) % 7);
        if (dayOfWeek < 1) {
            dayOfWeek += 7;
        }
        fields[0] = year;
        fields[1] = month;
        fields[2] = dayOfMonth;
        fields[3] = dayOfWeek;
        fields[4] = dayOfYear + 1;
        return fields;
    }

    public static int[] timeToFields(long time, int[] fields) {
        if (fields == null || fields.length < 6) {
            fields = new int[6];
        }
        long[] remainder = new long[1];
        long day = floorDivide(time, 86400000L, remainder);
        dayToFields(day, fields);
        fields[5] = (int) remainder[0];
        return fields;
    }

    public static long floorDivide(long numerator, long denominator) {
        return numerator >= 0 ? numerator / denominator : ((numerator + 1) / denominator) - 1;
    }

    private static long floorDivide(long numerator, long denominator, long[] remainder) {
        if (numerator >= 0) {
            remainder[0] = numerator % denominator;
            return numerator / denominator;
        }
        long quotient = ((numerator + 1) / denominator) - 1;
        remainder[0] = numerator - (quotient * denominator);
        return quotient;
    }

    public static int getDayOfWeekInMonth(int year, int month, int dayOfMonth) {
        int weekInMonth = (dayOfMonth + 6) / 7;
        if (weekInMonth == 4) {
            if (dayOfMonth + 7 > monthLength(year, month)) {
                weekInMonth = -1;
            }
        } else if (weekInMonth == 5) {
            weekInMonth = -1;
        }
        return weekInMonth;
    }
}