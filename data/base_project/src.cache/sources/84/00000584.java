package android.graphics;

import android.content.res.AssetManager;
import android.util.SparseArray;
import java.io.File;

/* loaded from: Typeface.class */
public class Typeface {
    int native_instance;
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = 3;
    private int mStyle;
    private static final SparseArray<SparseArray<Typeface>> sTypefaceCache = new SparseArray<>(3);
    public static final Typeface DEFAULT = create((String) null, 0);
    public static final Typeface DEFAULT_BOLD = create((String) null, 1);
    public static final Typeface SANS_SERIF = create("sans-serif", 0);
    public static final Typeface SERIF = create("serif", 0);
    public static final Typeface MONOSPACE = create("monospace", 0);
    static Typeface[] sDefaults = {DEFAULT, DEFAULT_BOLD, create((String) null, 2), create((String) null, 3)};

    private static native int nativeCreate(String str, int i);

    private static native int nativeCreateFromTypeface(int i, int i2);

    private static native void nativeUnref(int i);

    private static native int nativeGetStyle(int i);

    private static native int nativeCreateFromAsset(AssetManager assetManager, String str);

    private static native int nativeCreateFromFile(String str);

    public int getStyle() {
        return this.mStyle;
    }

    public final boolean isBold() {
        return (this.mStyle & 1) != 0;
    }

    public final boolean isItalic() {
        return (this.mStyle & 2) != 0;
    }

    public static Typeface create(String familyName, int style) {
        return new Typeface(nativeCreate(familyName, style));
    }

    public static Typeface create(Typeface family, int style) {
        Typeface typeface;
        int ni = 0;
        if (family != null) {
            if (family.mStyle == style) {
                return family;
            }
            ni = family.native_instance;
        }
        SparseArray<Typeface> styles = sTypefaceCache.get(ni);
        if (styles != null && (typeface = styles.get(style)) != null) {
            return typeface;
        }
        Typeface typeface2 = new Typeface(nativeCreateFromTypeface(ni, style));
        if (styles == null) {
            styles = new SparseArray<>(4);
            sTypefaceCache.put(ni, styles);
        }
        styles.put(style, typeface2);
        return typeface2;
    }

    public static Typeface defaultFromStyle(int style) {
        return sDefaults[style];
    }

    public static Typeface createFromAsset(AssetManager mgr, String path) {
        return new Typeface(nativeCreateFromAsset(mgr, path));
    }

    public static Typeface createFromFile(File path) {
        return new Typeface(nativeCreateFromFile(path.getAbsolutePath()));
    }

    public static Typeface createFromFile(String path) {
        return new Typeface(nativeCreateFromFile(path));
    }

    private Typeface(int ni) {
        this.mStyle = 0;
        if (ni == 0) {
            throw new RuntimeException("native typeface cannot be made");
        }
        this.native_instance = ni;
        this.mStyle = nativeGetStyle(ni);
    }

    protected void finalize() throws Throwable {
        try {
            nativeUnref(this.native_instance);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Typeface typeface = (Typeface) o;
        return this.mStyle == typeface.mStyle && this.native_instance == typeface.native_instance;
    }

    public int hashCode() {
        int result = this.native_instance;
        return (31 * result) + this.mStyle;
    }
}