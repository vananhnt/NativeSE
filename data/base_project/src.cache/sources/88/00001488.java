package android.util;

import java.util.Random;

/* loaded from: MathUtils.class */
public final class MathUtils {
    private static final Random sRandom = new Random();
    private static final float DEG_TO_RAD = 0.017453292f;
    private static final float RAD_TO_DEG = 57.295784f;

    private MathUtils() {
    }

    public static float abs(float v) {
        return v > 0.0f ? v : -v;
    }

    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : amount > high ? high : amount;
    }

    public static long constrain(long amount, long low, long high) {
        return amount < low ? low : amount > high ? high : amount;
    }

    public static float constrain(float amount, float low, float high) {
        return amount < low ? low : amount > high ? high : amount;
    }

    public static float log(float a) {
        return (float) Math.log(a);
    }

    public static float exp(float a) {
        return (float) Math.exp(a);
    }

    public static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static float max(int a, int b) {
        return a > b ? a : b;
    }

    public static float max(float a, float b, float c) {
        return a > b ? a > c ? a : c : b > c ? b : c;
    }

    public static float max(int a, int b, int c) {
        if (a > b) {
            return a > c ? a : c;
        }
        return b > c ? b : c;
    }

    public static float min(float a, float b) {
        return a < b ? a : b;
    }

    public static float min(int a, int b) {
        return a < b ? a : b;
    }

    public static float min(float a, float b, float c) {
        return a < b ? a < c ? a : c : b < c ? b : c;
    }

    public static float min(int a, int b, int c) {
        if (a < b) {
            return a < c ? a : c;
        }
        return b < c ? b : c;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public static float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
        float x = x2 - x1;
        float y = y2 - y1;
        float z = z2 - z1;
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public static float mag(float a, float b) {
        return (float) Math.sqrt((a * a) + (b * b));
    }

    public static float mag(float a, float b, float c) {
        return (float) Math.sqrt((a * a) + (b * b) + (c * c));
    }

    public static float sq(float v) {
        return v * v;
    }

    public static float radians(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static float degrees(float radians) {
        return radians * RAD_TO_DEG;
    }

    public static float acos(float value) {
        return (float) Math.acos(value);
    }

    public static float asin(float value) {
        return (float) Math.asin(value);
    }

    public static float atan(float value) {
        return (float) Math.atan(value);
    }

    public static float atan2(float a, float b) {
        return (float) Math.atan2(a, b);
    }

    public static float tan(float angle) {
        return (float) Math.tan(angle);
    }

    public static float lerp(float start, float stop, float amount) {
        return start + ((stop - start) * amount);
    }

    public static float norm(float start, float stop, float value) {
        return (value - start) / (stop - start);
    }

    public static float map(float minStart, float minStop, float maxStart, float maxStop, float value) {
        return maxStart + ((maxStart - maxStop) * ((value - minStart) / (minStop - minStart)));
    }

    public static int random(int howbig) {
        return (int) (sRandom.nextFloat() * howbig);
    }

    public static int random(int howsmall, int howbig) {
        return howsmall >= howbig ? howsmall : (int) ((sRandom.nextFloat() * (howbig - howsmall)) + howsmall);
    }

    public static float random(float howbig) {
        return sRandom.nextFloat() * howbig;
    }

    public static float random(float howsmall, float howbig) {
        return howsmall >= howbig ? howsmall : (sRandom.nextFloat() * (howbig - howsmall)) + howsmall;
    }

    public static void randomSeed(long seed) {
        sRandom.setSeed(seed);
    }
}