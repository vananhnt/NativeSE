package com.android.internal.util;

/* loaded from: FastMath.class */
public class FastMath {
    public static int round(float value) {
        long lx = value * 1.6777216E7f;
        return (int) ((lx + 8388608) >> 24);
    }
}