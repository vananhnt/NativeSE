package android.os;

/* loaded from: NullVibrator.class */
public class NullVibrator extends Vibrator {
    private static final NullVibrator sInstance = new NullVibrator();

    private NullVibrator() {
    }

    public static NullVibrator getInstance() {
        return sInstance;
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        return false;
    }

    @Override // android.os.Vibrator
    public void vibrate(long milliseconds) {
    }

    @Override // android.os.Vibrator
    public void vibrate(long[] pattern, int repeat) {
        if (repeat >= pattern.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int owningUid, String owningPackage, long milliseconds) {
        vibrate(milliseconds);
    }

    @Override // android.os.Vibrator
    public void vibrate(int owningUid, String owningPackage, long[] pattern, int repeat) {
        vibrate(pattern, repeat);
    }

    @Override // android.os.Vibrator
    public void cancel() {
    }
}