package android.graphics;

import android.graphics.Shader;

/* loaded from: RadialGradient.class */
public class RadialGradient extends Shader {
    private static final int TYPE_COLORS_AND_POSITIONS = 1;
    private static final int TYPE_COLOR_CENTER_AND_COLOR_EDGE = 2;
    private int mType;
    private float mX;
    private float mY;
    private float mRadius;
    private int[] mColors;
    private float[] mPositions;
    private int mColor0;
    private int mColor1;
    private Shader.TileMode mTileMode;

    private static native int nativeCreate1(float f, float f2, float f3, int[] iArr, float[] fArr, int i);

    private static native int nativeCreate2(float f, float f2, float f3, int i, int i2, int i3);

    private static native int nativePostCreate1(int i, float f, float f2, float f3, int[] iArr, float[] fArr, int i2);

    private static native int nativePostCreate2(int i, float f, float f2, float f3, int i2, int i3, int i4);

    public RadialGradient(float x, float y, float radius, int[] colors, float[] positions, Shader.TileMode tile) {
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        if (colors.length < 2) {
            throw new IllegalArgumentException("needs >= 2 number of colors");
        }
        if (positions != null && colors.length != positions.length) {
            throw new IllegalArgumentException("color and position arrays must be of equal length");
        }
        this.mType = 1;
        this.mX = x;
        this.mY = y;
        this.mRadius = radius;
        this.mColors = colors;
        this.mPositions = positions;
        this.mTileMode = tile;
        this.native_instance = nativeCreate1(x, y, radius, colors, positions, tile.nativeInt);
        this.native_shader = nativePostCreate1(this.native_instance, x, y, radius, colors, positions, tile.nativeInt);
    }

    public RadialGradient(float x, float y, float radius, int color0, int color1, Shader.TileMode tile) {
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        this.mType = 2;
        this.mX = x;
        this.mY = y;
        this.mRadius = radius;
        this.mColor0 = color0;
        this.mColor1 = color1;
        this.mTileMode = tile;
        this.native_instance = nativeCreate2(x, y, radius, color0, color1, tile.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, x, y, radius, color0, color1, tile.nativeInt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.Shader
    public Shader copy() {
        RadialGradient copy;
        switch (this.mType) {
            case 1:
                copy = new RadialGradient(this.mX, this.mY, this.mRadius, (int[]) this.mColors.clone(), this.mPositions != null ? (float[]) this.mPositions.clone() : null, this.mTileMode);
                break;
            case 2:
                copy = new RadialGradient(this.mX, this.mY, this.mRadius, this.mColor0, this.mColor1, this.mTileMode);
                break;
            default:
                throw new IllegalArgumentException("RadialGradient should be created with either colors and positions or center color and edge color");
        }
        copyLocalMatrix(copy);
        return copy;
    }
}