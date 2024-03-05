package android.graphics;

/* loaded from: PathDashPathEffect.class */
public class PathDashPathEffect extends PathEffect {
    private static native int nativeCreate(int i, float f, float f2, int i2);

    /* loaded from: PathDashPathEffect$Style.class */
    public enum Style {
        TRANSLATE(0),
        ROTATE(1),
        MORPH(2);
        
        int native_style;

        Style(int value) {
            this.native_style = value;
        }
    }

    public PathDashPathEffect(Path shape, float advance, float phase, Style style) {
        this.native_instance = nativeCreate(shape.ni(), advance, phase, style.native_style);
    }
}