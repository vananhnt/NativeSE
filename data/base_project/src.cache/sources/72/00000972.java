package android.net.http;

import android.os.SystemClock;

/* loaded from: Timer.class */
class Timer {
    private long mStart;
    private long mLast;

    public Timer() {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mLast = uptimeMillis;
        this.mStart = uptimeMillis;
    }

    public void mark(String message) {
        long now = SystemClock.uptimeMillis();
        this.mLast = now;
    }
}