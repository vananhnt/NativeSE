package android.graphics;

/* loaded from: CornerPathEffect.class */
public class CornerPathEffect extends PathEffect {
    private static native int nativeCreate(float f);

    public CornerPathEffect(float radius) {
        this.native_instance = nativeCreate(radius);
    }
}