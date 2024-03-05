package android.graphics;

import android.content.res.AssetManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: Movie.class */
public class Movie {
    private final int mNativeMovie;

    public native int width();

    public native int height();

    public native boolean isOpaque();

    public native int duration();

    public native boolean setTime(int i);

    public native void draw(Canvas canvas, float f, float f2, Paint paint);

    private static native Movie nativeDecodeAsset(int i);

    private static native Movie nativeDecodeStream(InputStream inputStream);

    public static native Movie decodeByteArray(byte[] bArr, int i, int i2);

    private static native void nativeDestructor(int i);

    private Movie(int nativeMovie) {
        if (nativeMovie == 0) {
            throw new RuntimeException("native movie creation failed");
        }
        this.mNativeMovie = nativeMovie;
    }

    public void draw(Canvas canvas, float x, float y) {
        draw(canvas, x, y, null);
    }

    public static Movie decodeStream(InputStream is) {
        if (is == null) {
            return null;
        }
        if (is instanceof AssetManager.AssetInputStream) {
            int asset = ((AssetManager.AssetInputStream) is).getAssetInt();
            return nativeDecodeAsset(asset);
        }
        return nativeDecodeStream(is);
    }

    public static Movie decodeFile(String pathName) {
        try {
            InputStream is = new FileInputStream(pathName);
            return decodeTempStream(is);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeMovie);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private static Movie decodeTempStream(InputStream is) {
        Movie moov = null;
        try {
            moov = decodeStream(is);
            is.close();
        } catch (IOException e) {
        }
        return moov;
    }
}