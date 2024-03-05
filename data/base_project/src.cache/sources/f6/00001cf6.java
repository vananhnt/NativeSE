package com.android.server;

import android.util.FloatMath;

/* loaded from: TwilightCalculator.class */
public class TwilightCalculator {
    public static final int DAY = 0;
    public static final int NIGHT = 1;
    private static final float DEGREES_TO_RADIANS = 0.017453292f;
    private static final float J0 = 9.0E-4f;
    private static final float ALTIDUTE_CORRECTION_CIVIL_TWILIGHT = -0.10471976f;
    private static final float C1 = 0.0334196f;
    private static final float C2 = 3.49066E-4f;
    private static final float C3 = 5.236E-6f;
    private static final float OBLIQUITY = 0.4092797f;
    private static final long UTC_2000 = 946728000000L;
    public long mSunset;
    public long mSunrise;
    public int mState;

    public void calculateTwilight(long time, double latiude, double longitude) {
        float daysSince2000 = ((float) (time - UTC_2000)) / 8.64E7f;
        float meanAnomaly = 6.24006f + (daysSince2000 * 0.01720197f);
        float trueAnomaly = meanAnomaly + (C1 * FloatMath.sin(meanAnomaly)) + (C2 * FloatMath.sin(2.0f * meanAnomaly)) + (C3 * FloatMath.sin(3.0f * meanAnomaly));
        float solarLng = trueAnomaly + 1.7965931f + 3.1415927f;
        double arcLongitude = (-longitude) / 360.0d;
        float n = (float) Math.round((daysSince2000 - J0) - arcLongitude);
        double solarTransitJ2000 = n + J0 + arcLongitude + (0.0053f * FloatMath.sin(meanAnomaly)) + ((-0.0069f) * FloatMath.sin(2.0f * solarLng));
        double solarDec = Math.asin(FloatMath.sin(solarLng) * FloatMath.sin(OBLIQUITY));
        double latRad = latiude * 0.01745329238474369d;
        double cosHourAngle = (FloatMath.sin(ALTIDUTE_CORRECTION_CIVIL_TWILIGHT) - (Math.sin(latRad) * Math.sin(solarDec))) / (Math.cos(latRad) * Math.cos(solarDec));
        if (cosHourAngle >= 1.0d) {
            this.mState = 1;
            this.mSunset = -1L;
            this.mSunrise = -1L;
        } else if (cosHourAngle <= -1.0d) {
            this.mState = 0;
            this.mSunset = -1L;
            this.mSunrise = -1L;
        } else {
            float hourAngle = (float) (Math.acos(cosHourAngle) / 6.283185307179586d);
            this.mSunset = Math.round((solarTransitJ2000 + hourAngle) * 8.64E7d) + UTC_2000;
            this.mSunrise = Math.round((solarTransitJ2000 - hourAngle) * 8.64E7d) + UTC_2000;
            if (this.mSunrise < time && this.mSunset > time) {
                this.mState = 0;
            } else {
                this.mState = 1;
            }
        }
    }
}