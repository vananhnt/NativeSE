package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;

/* loaded from: PointerIcon.class */
public final class PointerIcon implements Parcelable {
    private static final String TAG = "PointerIcon";
    public static final int STYLE_CUSTOM = -1;
    public static final int STYLE_NULL = 0;
    public static final int STYLE_ARROW = 1000;
    public static final int STYLE_SPOT_HOVER = 2000;
    public static final int STYLE_SPOT_TOUCH = 2001;
    public static final int STYLE_SPOT_ANCHOR = 2002;
    private static final int STYLE_OEM_FIRST = 10000;
    private static final int STYLE_DEFAULT = 1000;
    private final int mStyle;
    private int mSystemIconResourceId;
    private Bitmap mBitmap;
    private float mHotSpotX;
    private float mHotSpotY;
    private static final PointerIcon gNullIcon = new PointerIcon(0);
    public static final Parcelable.Creator<PointerIcon> CREATOR = new Parcelable.Creator<PointerIcon>() { // from class: android.view.PointerIcon.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PointerIcon createFromParcel(Parcel in) {
            int style = in.readInt();
            if (style == 0) {
                return PointerIcon.getNullIcon();
            }
            int systemIconResourceId = in.readInt();
            if (systemIconResourceId != 0) {
                PointerIcon icon = new PointerIcon(style);
                icon.mSystemIconResourceId = systemIconResourceId;
                return icon;
            }
            Bitmap bitmap = Bitmap.CREATOR.createFromParcel(in);
            float hotSpotX = in.readFloat();
            float hotSpotY = in.readFloat();
            return PointerIcon.createCustomIcon(bitmap, hotSpotX, hotSpotY);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PointerIcon[] newArray(int size) {
            return new PointerIcon[size];
        }
    };

    private PointerIcon(int style) {
        this.mStyle = style;
    }

    public static PointerIcon getNullIcon() {
        return gNullIcon;
    }

    public static PointerIcon getDefaultIcon(Context context) {
        return getSystemIcon(context, 1000);
    }

    public static PointerIcon getSystemIcon(Context context, int style) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (style == 0) {
            return gNullIcon;
        }
        int styleIndex = getSystemIconStyleIndex(style);
        if (styleIndex == 0) {
            styleIndex = getSystemIconStyleIndex(1000);
        }
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.Pointer, R.attr.pointerStyle, 0);
        int resourceId = a.getResourceId(styleIndex, -1);
        a.recycle();
        if (resourceId == -1) {
            Log.w(TAG, "Missing theme resources for pointer icon style " + style);
            return style == 1000 ? gNullIcon : getSystemIcon(context, 1000);
        }
        PointerIcon icon = new PointerIcon(style);
        if ((resourceId & (-16777216)) == 16777216) {
            icon.mSystemIconResourceId = resourceId;
        } else {
            icon.loadResource(context.getResources(), resourceId);
        }
        return icon;
    }

    public static PointerIcon createCustomIcon(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap must not be null");
        }
        validateHotSpot(bitmap, hotSpotX, hotSpotY);
        PointerIcon icon = new PointerIcon(-1);
        icon.mBitmap = bitmap;
        icon.mHotSpotX = hotSpotX;
        icon.mHotSpotY = hotSpotY;
        return icon;
    }

    public static PointerIcon loadCustomIcon(Resources resources, int resourceId) {
        if (resources == null) {
            throw new IllegalArgumentException("resources must not be null");
        }
        PointerIcon icon = new PointerIcon(-1);
        icon.loadResource(resources, resourceId);
        return icon;
    }

    public PointerIcon load(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (this.mSystemIconResourceId == 0 || this.mBitmap != null) {
            return this;
        }
        PointerIcon result = new PointerIcon(this.mStyle);
        result.mSystemIconResourceId = this.mSystemIconResourceId;
        result.loadResource(context.getResources(), this.mSystemIconResourceId);
        return result;
    }

    public boolean isNullIcon() {
        return this.mStyle == 0;
    }

    public boolean isLoaded() {
        return this.mBitmap != null || this.mStyle == 0;
    }

    public int getStyle() {
        return this.mStyle;
    }

    public Bitmap getBitmap() {
        throwIfIconIsNotLoaded();
        return this.mBitmap;
    }

    public float getHotSpotX() {
        throwIfIconIsNotLoaded();
        return this.mHotSpotX;
    }

    public float getHotSpotY() {
        throwIfIconIsNotLoaded();
        return this.mHotSpotY;
    }

    private void throwIfIconIsNotLoaded() {
        if (!isLoaded()) {
            throw new IllegalStateException("The icon is not loaded.");
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStyle);
        if (this.mStyle != 0) {
            out.writeInt(this.mSystemIconResourceId);
            if (this.mSystemIconResourceId == 0) {
                this.mBitmap.writeToParcel(out, flags);
                out.writeFloat(this.mHotSpotX);
                out.writeFloat(this.mHotSpotY);
            }
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof PointerIcon)) {
            return false;
        }
        PointerIcon otherIcon = (PointerIcon) other;
        if (this.mStyle != otherIcon.mStyle || this.mSystemIconResourceId != otherIcon.mSystemIconResourceId) {
            return false;
        }
        if (this.mSystemIconResourceId == 0) {
            if (this.mBitmap != otherIcon.mBitmap || this.mHotSpotX != otherIcon.mHotSpotX || this.mHotSpotY != otherIcon.mHotSpotY) {
                return false;
            }
            return true;
        }
        return true;
    }

    private void loadResource(Resources resources, int resourceId) {
        XmlResourceParser parser = resources.getXml(resourceId);
        try {
            try {
                XmlUtils.beginDocument(parser, "pointer-icon");
                TypedArray a = resources.obtainAttributes(parser, R.styleable.PointerIcon);
                int bitmapRes = a.getResourceId(0, 0);
                float hotSpotX = a.getFloat(1, 0.0f);
                float hotSpotY = a.getFloat(2, 0.0f);
                a.recycle();
                parser.close();
                if (bitmapRes == 0) {
                    throw new IllegalArgumentException("<pointer-icon> is missing bitmap attribute.");
                }
                Drawable drawable = resources.getDrawable(bitmapRes);
                if (!(drawable instanceof BitmapDrawable)) {
                    throw new IllegalArgumentException("<pointer-icon> bitmap attribute must refer to a bitmap drawable.");
                }
                this.mBitmap = ((BitmapDrawable) drawable).getBitmap();
                this.mHotSpotX = hotSpotX;
                this.mHotSpotY = hotSpotY;
            } catch (Exception ex) {
                throw new IllegalArgumentException("Exception parsing pointer icon resource.", ex);
            }
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    private static void validateHotSpot(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (hotSpotX < 0.0f || hotSpotX >= bitmap.getWidth()) {
            throw new IllegalArgumentException("x hotspot lies outside of the bitmap area");
        }
        if (hotSpotY < 0.0f || hotSpotY >= bitmap.getHeight()) {
            throw new IllegalArgumentException("y hotspot lies outside of the bitmap area");
        }
    }

    private static int getSystemIconStyleIndex(int style) {
        switch (style) {
            case 1000:
                return 0;
            case 2000:
                return 1;
            case 2001:
                return 2;
            case 2002:
                return 3;
            default:
                return 0;
        }
    }
}