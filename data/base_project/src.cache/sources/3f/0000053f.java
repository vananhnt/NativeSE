package android.graphics;

/* loaded from: ComposePathEffect.class */
public class ComposePathEffect extends PathEffect {
    private static native int nativeCreate(int i, int i2);

    public ComposePathEffect(PathEffect outerpe, PathEffect innerpe) {
        this.native_instance = nativeCreate(outerpe.native_instance, innerpe.native_instance);
    }
}