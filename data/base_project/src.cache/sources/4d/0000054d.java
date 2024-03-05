package android.graphics;

import android.graphics.Shader;

/* loaded from: LinearGradient.class */
public class LinearGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_START_AND_COLOR_END = 2;
    private int mType;
    private float mX0;
    private float mY0;
    private float mX1;
    private float mY1;
    private int[] mColors;
    private float[] mPositions;
    private int mColor0;
    private int mColor1;
    private Shader.TileMode mTileMode;

    private native int nativeCreate1(float f, float f2, float f3, float f4, int[] iArr, float[] fArr, int i);

    private native int nativeCreate2(float f, float f2, float f3, float f4, int i, int i2, int i3);

    private native int nativePostCreate1(int i, float f, float f2, float f3, float f4, int[] iArr, float[] fArr, int i2);

    private native int nativePostCreate2(int i, float f, float f2, float f3, float f4, int i2, int i3, int i4);

    public LinearGradient(float x0, float y0, float x1, float y1, int[] colors, float[] positions, Shader.TileMode tile) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (positions != null && colors.length != positions.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mX0 = x0;
        this.mY0 = y0;
        this.mX1 = x1;
        this.mY1 = y1;
        this.mColors = colors;
        this.mPositions = positions;
        this.mTileMode = tile;
        this.native_instance = nativeCreate1(x0, y0, x1, y1, colors, positions, tile.nativeInt);
        this.native_shader = nativePostCreate1(this.native_instance, x0, y0, x1, y1, colors, positions, tile.nativeInt);
    }

    public LinearGradient(float x0, float y0, float x1, float y1, int color0, int color1, Shader.TileMode tile) {
        this.mType = 2;
        this.mX0 = x0;
        this.mY0 = y0;
        this.mX1 = x1;
        this.mY1 = y1;
        this.mColor0 = color0;
        this.mColor1 = color1;
        this.mTileMode = tile;
        this.native_instance = nativeCreate2(x0, y0, x1, y1, color0, color1, tile.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, x0, y0, x1, y1, color0, color1, tile.nativeInt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.Shader
    public Shader copy() {
        LinearGradient copy;
        switch (this.mType) {
            case 1:
                copy = new LinearGradient(this.mX0, this.mY0, this.mX1, this.mY1, (int[]) this.mColors.clone(), this.mPositions != null ? (float[]) this.mPositions.clone() : null, this.mTileMode);
                break;
            case 2:
                copy = new LinearGradient(this.mX0, this.mY0, this.mX1, this.mY1, this.mColor0, this.mColor1, this.mTileMode);
                break;
            default:
                throw new IllegalArgumentException("LinearGradient should be created with either colors and positions or start color and end color");
        }
        copyLocalMatrix(copy);
        return copy;
    }
}