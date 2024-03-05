package android.graphics;

/* loaded from: SweepGradient.class */
public class SweepGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
    private int mType;
    private float mCx;
    private float mCy;
    private int[] mColors;
    private float[] mPositions;
    private int mColor0;
    private int mColor1;

    private static native int nativeCreate1(float f, float f2, int[] iArr, float[] fArr);

    private static native int nativeCreate2(float f, float f2, int i, int i2);

    private static native int nativePostCreate1(int i, float f, float f2, int[] iArr, float[] fArr);

    private static native int nativePostCreate2(int i, float f, float f2, int i2, int i3);

    public SweepGradient(float cx, float cy, int[] colors, float[] positions) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (positions != null && colors.length != positions.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mCx = cx;
        this.mCy = cy;
        this.mColors = colors;
        this.mPositions = positions;
        this.native_instance = nativeCreate1(cx, cy, colors, positions);
        this.native_shader = nativePostCreate1(this.native_instance, cx, cy, colors, positions);
    }

    public SweepGradient(float cx, float cy, int color0, int color1) {
        this.mType = 2;
        this.mCx = cx;
        this.mCy = cy;
        this.mColor0 = color0;
        this.mColor1 = color1;
        this.native_instance = nativeCreate2(cx, cy, color0, color1);
        this.native_shader = nativePostCreate2(this.native_instance, cx, cy, color0, color1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.Shader
    public Shader copy() {
        SweepGradient copy;
        switch (this.mType) {
            case 1:
                copy = new SweepGradient(this.mCx, this.mCy, (int[]) this.mColors.clone(), this.mPositions != null ? (float[]) this.mPositions.clone() : null);
                break;
            case 2:
                copy = new SweepGradient(this.mCx, this.mCy, this.mColor0, this.mColor1);
                break;
            default:
                throw new IllegalArgumentException("SweepGradient should be created with either colors and positions or start color and end color");
        }
        copyLocalMatrix(copy);
        return copy;
    }
}