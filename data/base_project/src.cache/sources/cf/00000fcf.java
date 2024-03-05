package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* loaded from: DrawableCompat.class */
public class DrawableCompat {
    static final DrawableImpl IMPL;

    /* loaded from: DrawableCompat$BaseDrawableImpl.class */
    static class BaseDrawableImpl implements DrawableImpl {
        BaseDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public boolean isAutoMirrored(Drawable drawable) {
            return false;
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void jumpToCurrentState(Drawable drawable) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setAutoMirrored(Drawable drawable, boolean z) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspot(Drawable drawable, float f, float f2) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTint(Drawable drawable, int i) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintList(Drawable drawable, ColorStateList colorStateList) {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
        }
    }

    /* loaded from: DrawableCompat$DrawableImpl.class */
    interface DrawableImpl {
        boolean isAutoMirrored(Drawable drawable);

        void jumpToCurrentState(Drawable drawable);

        void setAutoMirrored(Drawable drawable, boolean z);

        void setHotspot(Drawable drawable, float f, float f2);

        void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4);

        void setTint(Drawable drawable, int i);

        void setTintList(Drawable drawable, ColorStateList colorStateList);

        void setTintMode(Drawable drawable, PorterDuff.Mode mode);
    }

    /* loaded from: DrawableCompat$HoneycombDrawableImpl.class */
    static class HoneycombDrawableImpl extends BaseDrawableImpl {
        HoneycombDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void jumpToCurrentState(Drawable drawable) {
            DrawableCompatHoneycomb.jumpToCurrentState(drawable);
        }
    }

    /* loaded from: DrawableCompat$KitKatDrawableImpl.class */
    static class KitKatDrawableImpl extends HoneycombDrawableImpl {
        KitKatDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public boolean isAutoMirrored(Drawable drawable) {
            return DrawableCompatKitKat.isAutoMirrored(drawable);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setAutoMirrored(Drawable drawable, boolean z) {
            DrawableCompatKitKat.setAutoMirrored(drawable, z);
        }
    }

    /* loaded from: DrawableCompat$LDrawableImpl.class */
    static class LDrawableImpl extends KitKatDrawableImpl {
        LDrawableImpl() {
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspot(Drawable drawable, float f, float f2) {
            DrawableCompatL.setHotspot(drawable, f, f2);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
            DrawableCompatL.setHotspotBounds(drawable, i, i2, i3, i4);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTint(Drawable drawable, int i) {
            DrawableCompatL.setTint(drawable, i);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintList(Drawable drawable, ColorStateList colorStateList) {
            DrawableCompatL.setTintList(drawable, colorStateList);
        }

        @Override // android.support.v4.graphics.drawable.DrawableCompat.BaseDrawableImpl, android.support.v4.graphics.drawable.DrawableCompat.DrawableImpl
        public void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
            DrawableCompatL.setTintMode(drawable, mode);
        }
    }

    static {
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            IMPL = new LDrawableImpl();
        } else if (i >= 19) {
            IMPL = new KitKatDrawableImpl();
        } else if (i >= 11) {
            IMPL = new HoneycombDrawableImpl();
        } else {
            IMPL = new BaseDrawableImpl();
        }
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        return IMPL.isAutoMirrored(drawable);
    }

    public static void jumpToCurrentState(Drawable drawable) {
        IMPL.jumpToCurrentState(drawable);
    }

    public static void setAutoMirrored(Drawable drawable, boolean z) {
        IMPL.setAutoMirrored(drawable, z);
    }

    public static void setHotspot(Drawable drawable, float f, float f2) {
        IMPL.setHotspot(drawable, f, f2);
    }

    public static void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        IMPL.setHotspotBounds(drawable, i, i2, i3, i4);
    }

    public static void setTint(Drawable drawable, int i) {
        IMPL.setTint(drawable, i);
    }

    public static void setTintList(Drawable drawable, ColorStateList colorStateList) {
        IMPL.setTintList(drawable, colorStateList);
    }

    public static void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
        IMPL.setTintMode(drawable, mode);
    }
}