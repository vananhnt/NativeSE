package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.os.BatteryManager;
import android.os.Trace;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: Drawable.class */
public abstract class Drawable {
    private static final Rect ZERO_BOUNDS_RECT = new Rect();
    private int[] mStateSet = StateSet.WILD_CARD;
    private int mLevel = 0;
    private int mChangingConfigurations = 0;
    private Rect mBounds = ZERO_BOUNDS_RECT;
    private WeakReference<Callback> mCallback = null;
    private boolean mVisible = true;
    private int mLayoutDirection;

    /* loaded from: Drawable$Callback.class */
    public interface Callback {
        void invalidateDrawable(Drawable drawable);

        void scheduleDrawable(Drawable drawable, Runnable runnable, long j);

        void unscheduleDrawable(Drawable drawable, Runnable runnable);
    }

    public abstract void draw(Canvas canvas);

    public abstract void setAlpha(int i);

    public abstract void setColorFilter(ColorFilter colorFilter);

    public abstract int getOpacity();

    public void setBounds(int left, int top, int right, int bottom) {
        Rect oldBounds = this.mBounds;
        if (oldBounds == ZERO_BOUNDS_RECT) {
            Rect rect = new Rect();
            this.mBounds = rect;
            oldBounds = rect;
        }
        if (oldBounds.left != left || oldBounds.top != top || oldBounds.right != right || oldBounds.bottom != bottom) {
            if (!oldBounds.isEmpty()) {
                invalidateSelf();
            }
            this.mBounds.set(left, top, right, bottom);
            onBoundsChange(this.mBounds);
        }
    }

    public void setBounds(Rect bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public final void copyBounds(Rect bounds) {
        bounds.set(this.mBounds);
    }

    public final Rect copyBounds() {
        return new Rect(this.mBounds);
    }

    public final Rect getBounds() {
        if (this.mBounds == ZERO_BOUNDS_RECT) {
            this.mBounds = new Rect();
        }
        return this.mBounds;
    }

    public void setChangingConfigurations(int configs) {
        this.mChangingConfigurations = configs;
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public void setDither(boolean dither) {
    }

    public void setFilterBitmap(boolean filter) {
    }

    public final void setCallback(Callback cb) {
        this.mCallback = new WeakReference<>(cb);
    }

    public Callback getCallback() {
        if (this.mCallback != null) {
            return this.mCallback.get();
        }
        return null;
    }

    public void invalidateSelf() {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleSelf(Runnable what, long when) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleSelf(Runnable what) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public int getLayoutDirection() {
        return this.mLayoutDirection;
    }

    public void setLayoutDirection(int layoutDirection) {
        if (getLayoutDirection() != layoutDirection) {
            this.mLayoutDirection = layoutDirection;
        }
    }

    public int getAlpha() {
        return 255;
    }

    public void setXfermode(Xfermode mode) {
    }

    public void setColorFilter(int color, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    public void clearColorFilter() {
        setColorFilter(null);
    }

    public boolean isStateful() {
        return false;
    }

    public boolean setState(int[] stateSet) {
        if (!Arrays.equals(this.mStateSet, stateSet)) {
            this.mStateSet = stateSet;
            return onStateChange(stateSet);
        }
        return false;
    }

    public int[] getState() {
        return this.mStateSet;
    }

    public void jumpToCurrentState() {
    }

    public Drawable getCurrent() {
        return this;
    }

    public final boolean setLevel(int level) {
        if (this.mLevel != level) {
            this.mLevel = level;
            return onLevelChange(level);
        }
        return false;
    }

    public final int getLevel() {
        return this.mLevel;
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = this.mVisible != visible;
        if (changed) {
            this.mVisible = visible;
            invalidateSelf();
        }
        return changed;
    }

    public final boolean isVisible() {
        return this.mVisible;
    }

    public void setAutoMirrored(boolean mirrored) {
    }

    public boolean isAutoMirrored() {
        return false;
    }

    public static int resolveOpacity(int op1, int op2) {
        if (op1 == op2) {
            return op1;
        }
        if (op1 == 0 || op2 == 0) {
            return 0;
        }
        if (op1 == -3 || op2 == -3) {
            return -3;
        }
        if (op1 == -2 || op2 == -2) {
            return -2;
        }
        return -1;
    }

    public Region getTransparentRegion() {
        return null;
    }

    protected boolean onStateChange(int[] state) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean onLevelChange(int level) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
    }

    public int getIntrinsicWidth() {
        return -1;
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    public int getMinimumWidth() {
        int intrinsicWidth = getIntrinsicWidth();
        if (intrinsicWidth > 0) {
            return intrinsicWidth;
        }
        return 0;
    }

    public int getMinimumHeight() {
        int intrinsicHeight = getIntrinsicHeight();
        if (intrinsicHeight > 0) {
            return intrinsicHeight;
        }
        return 0;
    }

    public boolean getPadding(Rect padding) {
        padding.set(0, 0, 0, 0);
        return false;
    }

    public Insets getOpticalInsets() {
        return Insets.NONE;
    }

    public Drawable mutate() {
        return this;
    }

    public static Drawable createFromStream(InputStream is, String srcName) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            Drawable createFromResourceStream = createFromResourceStream(null, null, is, srcName, null);
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            return createFromResourceStream;
        } catch (Throwable th) {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            throw th;
        }
    }

    public static Drawable createFromResourceStream(Resources res, TypedValue value, InputStream is, String srcName) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            Drawable createFromResourceStream = createFromResourceStream(res, value, is, srcName, null);
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            return createFromResourceStream;
        } catch (Throwable th) {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            throw th;
        }
    }

    public static Drawable createFromResourceStream(Resources res, TypedValue value, InputStream is, String srcName, BitmapFactory.Options opts) {
        if (is == null) {
            return null;
        }
        Rect pad = new Rect();
        if (opts == null) {
            opts = new BitmapFactory.Options();
        }
        opts.inScreenDensity = res != null ? res.getDisplayMetrics().noncompatDensityDpi : DisplayMetrics.DENSITY_DEVICE;
        Bitmap bm = BitmapFactory.decodeResourceStream(res, value, is, pad, opts);
        if (bm != null) {
            byte[] np = bm.getNinePatchChunk();
            if (np == null || !NinePatch.isNinePatchChunk(np)) {
                np = null;
                pad = null;
            }
            int[] layoutBounds = bm.getLayoutBounds();
            Rect layoutBoundsRect = null;
            if (layoutBounds != null) {
                layoutBoundsRect = new Rect(layoutBounds[0], layoutBounds[1], layoutBounds[2], layoutBounds[3]);
            }
            return drawableFromBitmap(res, bm, np, pad, layoutBoundsRect, srcName);
        }
        return null;
    }

    public static Drawable createFromXml(Resources r, XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        Drawable drawable = createFromXmlInner(r, parser, attrs);
        if (drawable == null) {
            throw new RuntimeException("Unknown initial tag: " + parser.getName());
        }
        return drawable;
    }

    public static Drawable createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        Drawable drawable;
        String name = parser.getName();
        if (name.equals("selector")) {
            drawable = new StateListDrawable();
        } else if (name.equals("level-list")) {
            drawable = new LevelListDrawable();
        } else if (name.equals("layer-list")) {
            drawable = new LayerDrawable();
        } else if (name.equals("transition")) {
            drawable = new TransitionDrawable();
        } else if (name.equals(CalendarContract.ColorsColumns.COLOR)) {
            drawable = new ColorDrawable();
        } else if (name.equals("shape")) {
            drawable = new GradientDrawable();
        } else if (name.equals(BatteryManager.EXTRA_SCALE)) {
            drawable = new ScaleDrawable();
        } else if (name.equals("clip")) {
            drawable = new ClipDrawable();
        } else if (name.equals("rotate")) {
            drawable = new RotateDrawable();
        } else if (name.equals("animated-rotate")) {
            drawable = new AnimatedRotateDrawable();
        } else if (name.equals("animation-list")) {
            drawable = new AnimationDrawable();
        } else if (name.equals("inset")) {
            drawable = new InsetDrawable();
        } else if (name.equals("bitmap")) {
            drawable = new BitmapDrawable(r);
            if (r != null) {
                ((BitmapDrawable) drawable).setTargetDensity(r.getDisplayMetrics());
            }
        } else if (name.equals("nine-patch")) {
            drawable = new NinePatchDrawable();
            if (r != null) {
                ((NinePatchDrawable) drawable).setTargetDensity(r.getDisplayMetrics());
            }
        } else {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid drawable tag " + name);
        }
        drawable.inflate(r, parser, attrs);
        return drawable;
    }

    public static Drawable createFromPath(String pathName) {
        if (pathName == null) {
            return null;
        }
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, pathName);
        try {
            Bitmap bm = BitmapFactory.decodeFile(pathName);
            if (bm != null) {
                Drawable drawableFromBitmap = drawableFromBitmap(null, bm, null, null, null, pathName);
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                return drawableFromBitmap;
            }
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            return null;
        } catch (Throwable th) {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            throw th;
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        TypedArray a = r.obtainAttributes(attrs, R.styleable.Drawable);
        inflateWithAttributes(r, parser, a, 0);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void inflateWithAttributes(Resources r, XmlPullParser parser, TypedArray attrs, int visibleAttr) throws XmlPullParserException, IOException {
        this.mVisible = attrs.getBoolean(visibleAttr, this.mVisible);
    }

    /* loaded from: Drawable$ConstantState.class */
    public static abstract class ConstantState {
        public abstract Drawable newDrawable();

        public abstract int getChangingConfigurations();

        public Drawable newDrawable(Resources res) {
            return newDrawable();
        }

        public Bitmap getBitmap() {
            return null;
        }
    }

    public ConstantState getConstantState() {
        return null;
    }

    private static Drawable drawableFromBitmap(Resources res, Bitmap bm, byte[] np, Rect pad, Rect layoutBounds, String srcName) {
        if (np != null) {
            return new NinePatchDrawable(res, bm, np, pad, layoutBounds, srcName);
        }
        return new BitmapDrawable(res, bm);
    }
}