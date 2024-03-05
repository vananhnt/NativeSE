package android.graphics;

/* loaded from: PaintFlagsDrawFilter.class */
public class PaintFlagsDrawFilter extends DrawFilter {
    public final int clearBits;
    public final int setBits;

    private static native int nativeConstructor(int i, int i2);

    public PaintFlagsDrawFilter(int clearBits, int setBits) {
        this.clearBits = clearBits;
        this.setBits = setBits;
        this.mNativeInt = nativeConstructor(clearBits, setBits);
    }
}