package android.graphics;

/* loaded from: DashPathEffect.class */
public class DashPathEffect extends PathEffect {
    private static native int nativeCreate(float[] fArr, float f);

    public DashPathEffect(float[] intervals, float phase) {
        if (intervals.length < 2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.native_instance = nativeCreate(intervals, phase);
    }
}