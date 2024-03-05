package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TimeZone.class */
public abstract class TimeZone implements Serializable, Cloneable {
    public static final int SHORT = 0;
    public static final int LONG = 1;

    public abstract int getOffset(int i, int i2, int i3, int i4, int i5, int i6);

    public abstract int getRawOffset();

    public abstract boolean inDaylightTime(Date date);

    public abstract void setRawOffset(int i);

    public abstract boolean useDaylightTime();

    public TimeZone() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized String[] getAvailableIDs() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized String[] getAvailableIDs(int offsetMillis) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized TimeZone getDefault() {
        throw new RuntimeException("Stub!");
    }

    public final String getDisplayName() {
        throw new RuntimeException("Stub!");
    }

    public final String getDisplayName(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public final String getDisplayName(boolean daylightTime, int style) {
        throw new RuntimeException("Stub!");
    }

    public String getDisplayName(boolean daylightTime, int style, Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public String getID() {
        throw new RuntimeException("Stub!");
    }

    public int getDSTSavings() {
        throw new RuntimeException("Stub!");
    }

    public int getOffset(long time) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized TimeZone getTimeZone(String id) {
        throw new RuntimeException("Stub!");
    }

    public boolean hasSameRules(TimeZone timeZone) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized void setDefault(TimeZone timeZone) {
        throw new RuntimeException("Stub!");
    }

    public void setID(String id) {
        throw new RuntimeException("Stub!");
    }
}