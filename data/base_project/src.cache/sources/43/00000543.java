package android.graphics;

/* loaded from: DiscretePathEffect.class */
public class DiscretePathEffect extends PathEffect {
    private static native int nativeCreate(float f, float f2);

    public DiscretePathEffect(float segmentLength, float deviation) {
        this.native_instance = nativeCreate(segmentLength, deviation);
    }
}