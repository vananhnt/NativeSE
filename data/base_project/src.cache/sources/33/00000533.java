package android.graphics;

import android.graphics.Shader;

/* loaded from: BitmapShader.class */
public class BitmapShader extends Shader {
    public final Bitmap mBitmap;
    private Shader.TileMode mTileX;
    private Shader.TileMode mTileY;

    private static native int nativeCreate(int i, int i2, int i3);

    private static native int nativePostCreate(int i, int i2, int i3, int i4);

    public BitmapShader(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        this.mBitmap = bitmap;
        this.mTileX = tileX;
        this.mTileY = tileY;
        int b = bitmap.ni();
        this.native_instance = nativeCreate(b, tileX.nativeInt, tileY.nativeInt);
        this.native_shader = nativePostCreate(this.native_instance, b, tileX.nativeInt, tileY.nativeInt);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.Shader
    public Shader copy() {
        BitmapShader copy = new BitmapShader(this.mBitmap, this.mTileX, this.mTileY);
        copyLocalMatrix(copy);
        return copy;
    }
}