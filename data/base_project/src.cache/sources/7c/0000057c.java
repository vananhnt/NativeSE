package android.graphics;

/* loaded from: SumPathEffect.class */
public class SumPathEffect extends PathEffect {
    private static native int nativeCreate(int i, int i2);

    public SumPathEffect(PathEffect first, PathEffect second) {
        this.native_instance = nativeCreate(first.native_instance, second.native_instance);
    }
}