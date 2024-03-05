package android.os;

/* loaded from: Vibrator.class */
public abstract class Vibrator {
    public abstract boolean hasVibrator();

    public abstract void vibrate(long j);

    public abstract void vibrate(long[] jArr, int i);

    public abstract void vibrate(int i, String str, long j);

    public abstract void vibrate(int i, String str, long[] jArr, int i2);

    public abstract void cancel();
}