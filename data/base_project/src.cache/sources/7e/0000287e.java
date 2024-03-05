package javax.xml.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.namespace.QName;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Duration.class */
public abstract class Duration {
    public abstract int getSign();

    public abstract Number getField(DatatypeConstants.Field field);

    public abstract boolean isSet(DatatypeConstants.Field field);

    public abstract Duration add(Duration duration);

    public abstract void addTo(Calendar calendar);

    public abstract Duration multiply(BigDecimal bigDecimal);

    public abstract Duration negate();

    public abstract Duration normalizeWith(Calendar calendar);

    public abstract int compare(Duration duration);

    public abstract int hashCode();

    public QName getXMLSchemaType() {
        boolean yearSet = isSet(DatatypeConstants.YEARS);
        boolean monthSet = isSet(DatatypeConstants.MONTHS);
        boolean daySet = isSet(DatatypeConstants.DAYS);
        boolean hourSet = isSet(DatatypeConstants.HOURS);
        boolean minuteSet = isSet(DatatypeConstants.MINUTES);
        boolean secondSet = isSet(DatatypeConstants.SECONDS);
        if (yearSet && monthSet && daySet && hourSet && minuteSet && secondSet) {
            return DatatypeConstants.DURATION;
        }
        if (!yearSet && !monthSet && daySet && hourSet && minuteSet && secondSet) {
            return DatatypeConstants.DURATION_DAYTIME;
        }
        if (yearSet && monthSet && !daySet && !hourSet && !minuteSet && !secondSet) {
            return DatatypeConstants.DURATION_YEARMONTH;
        }
        throw new IllegalStateException("javax.xml.datatype.Duration#getXMLSchemaType(): this Duration does not match one of the XML Schema date/time datatypes: year set = " + yearSet + " month set = " + monthSet + " day set = " + daySet + " hour set = " + hourSet + " minute set = " + minuteSet + " second set = " + secondSet);
    }

    public int getYears() {
        return getFieldValueAsInt(DatatypeConstants.YEARS);
    }

    public int getMonths() {
        return getFieldValueAsInt(DatatypeConstants.MONTHS);
    }

    public int getDays() {
        return getFieldValueAsInt(DatatypeConstants.DAYS);
    }

    public int getHours() {
        return getFieldValueAsInt(DatatypeConstants.HOURS);
    }

    public int getMinutes() {
        return getFieldValueAsInt(DatatypeConstants.MINUTES);
    }

    public int getSeconds() {
        return getFieldValueAsInt(DatatypeConstants.SECONDS);
    }

    public long getTimeInMillis(Calendar startInstant) {
        Calendar cal = (Calendar) startInstant.clone();
        addTo(cal);
        return getCalendarTimeInMillis(cal) - getCalendarTimeInMillis(startInstant);
    }

    public long getTimeInMillis(Date startInstant) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(startInstant);
        addTo(cal);
        return getCalendarTimeInMillis(cal) - startInstant.getTime();
    }

    private int getFieldValueAsInt(DatatypeConstants.Field field) {
        Number n = getField(field);
        if (n != null) {
            return n.intValue();
        }
        return 0;
    }

    public void addTo(Date date) {
        if (date == null) {
            throw new NullPointerException("date == null");
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        addTo(cal);
        date.setTime(getCalendarTimeInMillis(cal));
    }

    public Duration subtract(Duration rhs) {
        return add(rhs.negate());
    }

    public Duration multiply(int factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public boolean isLongerThan(Duration duration) {
        return compare(duration) == 1;
    }

    public boolean isShorterThan(Duration duration) {
        return compare(duration) == -1;
    }

    public boolean equals(Object duration) {
        if (duration == this) {
            return true;
        }
        return (duration instanceof Duration) && compare((Duration) duration) == 0;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (getSign() < 0) {
            buf.append('-');
        }
        buf.append('P');
        BigInteger years = (BigInteger) getField(DatatypeConstants.YEARS);
        if (years != null) {
            buf.append(years).append('Y');
        }
        BigInteger months = (BigInteger) getField(DatatypeConstants.MONTHS);
        if (months != null) {
            buf.append(months).append('M');
        }
        BigInteger days = (BigInteger) getField(DatatypeConstants.DAYS);
        if (days != null) {
            buf.append(days).append('D');
        }
        BigInteger hours = (BigInteger) getField(DatatypeConstants.HOURS);
        BigInteger minutes = (BigInteger) getField(DatatypeConstants.MINUTES);
        BigDecimal seconds = (BigDecimal) getField(DatatypeConstants.SECONDS);
        if (hours != null || minutes != null || seconds != null) {
            buf.append('T');
            if (hours != null) {
                buf.append(hours).append('H');
            }
            if (minutes != null) {
                buf.append(minutes).append('M');
            }
            if (seconds != null) {
                buf.append(toString(seconds)).append('S');
            }
        }
        return buf.toString();
    }

    private String toString(BigDecimal bd) {
        StringBuilder buf;
        String intString = bd.unscaledValue().toString();
        int scale = bd.scale();
        if (scale == 0) {
            return intString;
        }
        int insertionPoint = intString.length() - scale;
        if (insertionPoint == 0) {
            return "0." + intString;
        }
        if (insertionPoint > 0) {
            buf = new StringBuilder(intString);
            buf.insert(insertionPoint, '.');
        } else {
            buf = new StringBuilder((3 - insertionPoint) + intString.length());
            buf.append("0.");
            for (int i = 0; i < (-insertionPoint); i++) {
                buf.append('0');
            }
            buf.append(intString);
        }
        return buf.toString();
    }

    private static long getCalendarTimeInMillis(Calendar cal) {
        return cal.getTime().getTime();
    }
}