package android.support.v4.app;

import android.app.ActivityManager;

/* loaded from: ActivityManagerCompatKitKat.class */
class ActivityManagerCompatKitKat {
    ActivityManagerCompatKitKat() {
    }

    public static boolean isLowRamDevice(ActivityManager activityManager) {
        return activityManager.isLowRamDevice();
    }
}