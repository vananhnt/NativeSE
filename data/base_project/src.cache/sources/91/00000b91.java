package android.os;

/* loaded from: SystemClock.class */
public final class SystemClock {
    public static native boolean setCurrentTimeMillis(long j);

    public static native long uptimeMillis();

    public static native long elapsedRealtime();

    public static native long elapsedRealtimeNanos();

    public static native long currentThreadTimeMillis();

    public static native long currentThreadTimeMicro();

    public static native long currentTimeMicro();

    private SystemClock() {
    }

    public static void sleep(long ms) {
        long start = uptimeMillis();
        long duration = ms;
        boolean interrupted = false;
        do {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            duration = (start + ms) - uptimeMillis();
        } while (duration > 0);
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}