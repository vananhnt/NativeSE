package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import java.io.InputStream;

/* loaded from: RoundedBitmapDrawableFactory.class */
public class RoundedBitmapDrawableFactory {
    private static final String TAG = "RoundedBitmapDrawableFactory";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RoundedBitmapDrawableFactory$DefaultRoundedBitmapDrawable.class */
    public static class DefaultRoundedBitmapDrawable extends RoundedBitmapDrawable {
        DefaultRoundedBitmapDrawable(Resources resources, Bitmap bitmap) {
            super(resources, bitmap);
        }

        @Override // android.support.v4.graphics.drawable.RoundedBitmapDrawable
        void gravityCompatApply(int i, int i2, int i3, Rect rect, Rect rect2) {
            GravityCompat.apply(i, i2, i3, rect, rect2, 0);
        }

        @Override // android.support.v4.graphics.drawable.RoundedBitmapDrawable
        public boolean hasMipMap() {
            return this.mBitmap != null && BitmapCompat.hasMipMap(this.mBitmap);
        }

        @Override // android.support.v4.graphics.drawable.RoundedBitmapDrawable
        public void setMipMap(boolean z) {
            if (this.mBitmap != null) {
                BitmapCompat.setHasMipMap(this.mBitmap, z);
                invalidateSelf();
            }
        }
    }

    public static RoundedBitmapDrawable create(Resources resources, Bitmap bitmap) {
        return Build.VERSION.SDK_INT >= 21 ? new RoundedBitmapDrawable21(resources, bitmap) : new DefaultRoundedBitmapDrawable(resources, bitmap);
    }

    public static RoundedBitmapDrawable create(Resources resources, InputStream inputStream) {
        RoundedBitmapDrawable create = create(resources, BitmapFactory.decodeStream(inputStream));
        if (create.getBitmap() == null) {
            Log.w(TAG, "BitmapDrawable cannot decode " + inputStream);
        }
        return create;
    }

    public static RoundedBitmapDrawable create(Resources resources, String str) {
        RoundedBitmapDrawable create = create(resources, BitmapFactory.decodeFile(str));
        if (create.getBitmap() == null) {
            Log.w(TAG, "BitmapDrawable cannot decode " + str);
        }
        return create;
    }
}