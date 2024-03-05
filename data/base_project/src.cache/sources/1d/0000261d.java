package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SimpleTimeZone.class */
public class SimpleTimeZone extends TimeZone {
    public static final int UTC_TIME = 2;
    public static final int STANDARD_TIME = 1;
    public static final int WALL_TIME = 0;

    public SimpleTimeZone(int offset, String name) {
        throw new RuntimeException("Stub!");
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime) {
        throw new RuntimeException("Stub!");
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime, int daylightSavings) {
        throw new RuntimeException("Stub!");
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode, int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode, int daylightSavings) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public int getDSTSavings() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int time) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public int getOffset(long time) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public int getRawOffset() {
        throw new RuntimeException("Stub!");
    }

    public synchronized int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public boolean hasSameRules(TimeZone zone) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public boolean inDaylightTime(Date time) {
        throw new RuntimeException("Stub!");
    }

    public void setDSTSavings(int milliseconds) {
        throw new RuntimeException("Stub!");
    }

    public void setEndRule(int month, int dayOfMonth, int time) {
        throw new RuntimeException("Stub!");
    }

    public void setEndRule(int month, int day, int dayOfWeek, int time) {
        throw new RuntimeException("Stub!");
    }

    public void setEndRule(int month, int day, int dayOfWeek, int time, boolean after) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public void setRawOffset(int offset) {
        throw new RuntimeException("Stub!");
    }

    public void setStartRule(int month, int dayOfMonth, int time) {
        throw new RuntimeException("Stub!");
    }

    public void setStartRule(int month, int day, int dayOfWeek, int time) {
        throw new RuntimeException("Stub!");
    }

    public void setStartRule(int month, int day, int dayOfWeek, int time, boolean after) {
        throw new RuntimeException("Stub!");
    }

    public void setStartYear(int year) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.TimeZone
    public boolean useDaylightTime() {
        throw new RuntimeException("Stub!");
    }
}