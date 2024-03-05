package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;

/* loaded from: DrawableCompatKitKat.class */
class DrawableCompatKitKat {
    DrawableCompatKitKat() {
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        return drawable.isAutoMirrored();
    }

    public static void setAutoMirrored(Drawable drawable, boolean z) {
        drawable.setAutoMirrored(z);
    }
}