package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: NinePatchDrawable.class */
public class NinePatchDrawable extends Drawable {
    private static final boolean DEFAULT_DITHER = false;
    private NinePatchState mNinePatchState;
    private NinePatch mNinePatch;
    private Rect mPadding;
    private Insets mOpticalInsets;
    private Paint mPaint;
    private boolean mMutated;
    private int mTargetDensity;
    private int mBitmapWidth;
    private int mBitmapHeight;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NinePatchDrawable() {
        this.mOpticalInsets = Insets.NONE;
        this.mTargetDensity = 160;
    }

    @Deprecated
    public NinePatchDrawable(Bitmap bitmap, byte[] chunk, Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), padding), (Resources) null);
    }

    public NinePatchDrawable(Resources res, Bitmap bitmap, byte[] chunk, Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), padding), res);
        this.mNinePatchState.mTargetDensity = this.mTargetDensity;
    }

    public NinePatchDrawable(Resources res, Bitmap bitmap, byte[] chunk, Rect padding, Rect opticalInsets, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), padding, opticalInsets), res);
        this.mNinePatchState.mTargetDensity = this.mTargetDensity;
    }

    @Deprecated
    public NinePatchDrawable(NinePatch patch) {
        this(new NinePatchState(patch, new Rect()), (Resources) null);
    }

    public NinePatchDrawable(Resources res, NinePatch patch) {
        this(new NinePatchState(patch, new Rect()), res);
        this.mNinePatchState.mTargetDensity = this.mTargetDensity;
    }

    private void setNinePatchState(NinePatchState state, Resources res) {
        this.mNinePatchState = state;
        this.mNinePatch = state.mNinePatch;
        this.mPadding = state.mPadding;
        this.mTargetDensity = res != null ? res.getDisplayMetrics().densityDpi : state.mTargetDensity;
        if (state.mDither) {
            setDither(state.mDither);
        }
        setAutoMirrored(state.mAutoMirrored);
        if (this.mNinePatch != null) {
            computeBitmapSize();
        }
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics metrics) {
        setTargetDensity(metrics.densityDpi);
    }

    public void setTargetDensity(int density) {
        if (density != this.mTargetDensity) {
            this.mTargetDensity = density == 0 ? 160 : density;
            if (this.mNinePatch != null) {
                computeBitmapSize();
            }
            invalidateSelf();
        }
    }

    private static Insets scaleFromDensity(Insets insets, int sdensity, int tdensity) {
        int left = Bitmap.scaleFromDensity(insets.left, sdensity, tdensity);
        int top = Bitmap.scaleFromDensity(insets.top, sdensity, tdensity);
        int right = Bitmap.scaleFromDensity(insets.right, sdensity, tdensity);
        int bottom = Bitmap.scaleFromDensity(insets.bottom, sdensity, tdensity);
        return Insets.of(left, top, right, bottom);
    }

    private void computeBitmapSize() {
        int sdensity = this.mNinePatch.getDensity();
        int tdensity = this.mTargetDensity;
        if (sdensity == tdensity) {
            this.mBitmapWidth = this.mNinePatch.getWidth();
            this.mBitmapHeight = this.mNinePatch.getHeight();
            this.mOpticalInsets = this.mNinePatchState.mOpticalInsets;
            return;
        }
        this.mBitmapWidth = Bitmap.scaleFromDensity(this.mNinePatch.getWidth(), sdensity, tdensity);
        this.mBitmapHeight = Bitmap.scaleFromDensity(this.mNinePatch.getHeight(), sdensity, tdensity);
        if (this.mNinePatchState.mPadding != null && this.mPadding != null) {
            Rect dest = this.mPadding;
            Rect src = this.mNinePatchState.mPadding;
            if (dest == src) {
                Rect rect = new Rect(src);
                dest = rect;
                this.mPadding = rect;
            }
            dest.left = Bitmap.scaleFromDensity(src.left, sdensity, tdensity);
            dest.top = Bitmap.scaleFromDensity(src.top, sdensity, tdensity);
            dest.right = Bitmap.scaleFromDensity(src.right, sdensity, tdensity);
            dest.bottom = Bitmap.scaleFromDensity(src.bottom, sdensity, tdensity);
        }
        this.mOpticalInsets = scaleFromDensity(this.mNinePatchState.mOpticalInsets, sdensity, tdensity);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        boolean needsMirroring = needsMirroring();
        if (needsMirroring) {
            canvas.save();
            canvas.translate(bounds.right - bounds.left, 0.0f);
            canvas.scale(-1.0f, 1.0f);
        }
        this.mNinePatch.draw(canvas, bounds, this.mPaint);
        if (needsMirroring) {
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mNinePatchState.mChangingConfigurations;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        if (needsMirroring()) {
            padding.set(this.mPadding.right, this.mPadding.top, this.mPadding.left, this.mPadding.bottom);
            return true;
        }
        padding.set(this.mPadding);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        if (needsMirroring()) {
            return Insets.of(this.mOpticalInsets.right, this.mOpticalInsets.top, this.mOpticalInsets.right, this.mOpticalInsets.bottom);
        }
        return this.mOpticalInsets;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.mPaint == null && alpha == 255) {
            return;
        }
        getPaint().setAlpha(alpha);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        if (this.mPaint == null) {
            return 255;
        }
        return getPaint().getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        if (this.mPaint == null && cf == null) {
            return;
        }
        getPaint().setColorFilter(cf);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        if (this.mPaint == null && !dither) {
            return;
        }
        getPaint().setDither(dither);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        this.mNinePatchState.mAutoMirrored = mirrored;
    }

    private boolean needsMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return this.mNinePatchState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean filter) {
        getPaint().setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.NinePatchDrawable);
        int id = a.getResourceId(0, 0);
        if (id == 0) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": <nine-patch> requires a valid src attribute");
        }
        boolean dither = a.getBoolean(1, false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (dither) {
            options.inDither = false;
        }
        options.inScreenDensity = r.getDisplayMetrics().noncompatDensityDpi;
        Rect padding = new Rect();
        Rect opticalInsets = new Rect();
        Bitmap bitmap = null;
        try {
            TypedValue value = new TypedValue();
            InputStream is = r.openRawResource(id, value);
            bitmap = BitmapFactory.decodeResourceStream(r, value, is, padding, options);
            is.close();
        } catch (IOException e) {
        }
        if (bitmap == null) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": <nine-patch> requires a valid src attribute");
        }
        if (bitmap.getNinePatchChunk() == null) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": <nine-patch> requires a valid 9-patch source image");
        }
        boolean automirrored = a.getBoolean(2, false);
        setNinePatchState(new NinePatchState(new NinePatch(bitmap, bitmap.getNinePatchChunk()), padding, opticalInsets, dither, automirrored), r);
        this.mNinePatchState.mTargetDensity = this.mTargetDensity;
        a.recycle();
    }

    public Paint getPaint() {
        if (this.mPaint == null) {
            this.mPaint = new Paint();
            this.mPaint.setDither(false);
        }
        return this.mPaint;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return this.mBitmapWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.mBitmapHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return (this.mNinePatch.hasAlpha() || (this.mPaint != null && this.mPaint.getAlpha() < 255)) ? -3 : -1;
    }

    @Override // android.graphics.drawable.Drawable
    public Region getTransparentRegion() {
        return this.mNinePatch.getTransparentRegion(getBounds());
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mNinePatchState.mChangingConfigurations = getChangingConfigurations();
        return this.mNinePatchState;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mNinePatchState = new NinePatchState(this.mNinePatchState);
            this.mNinePatch = this.mNinePatchState.mNinePatch;
            this.mMutated = true;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: NinePatchDrawable$NinePatchState.class */
    public static final class NinePatchState extends Drawable.ConstantState {
        final NinePatch mNinePatch;
        final Rect mPadding;
        final Insets mOpticalInsets;
        final boolean mDither;
        int mChangingConfigurations;
        int mTargetDensity;
        boolean mAutoMirrored;

        NinePatchState(NinePatch ninePatch, Rect padding) {
            this(ninePatch, padding, new Rect(), false, false);
        }

        NinePatchState(NinePatch ninePatch, Rect padding, Rect opticalInsets) {
            this(ninePatch, padding, opticalInsets, false, false);
        }

        NinePatchState(NinePatch ninePatch, Rect rect, Rect opticalInsets, boolean dither, boolean autoMirror) {
            this.mTargetDensity = 160;
            this.mNinePatch = ninePatch;
            this.mPadding = rect;
            this.mOpticalInsets = Insets.of(opticalInsets);
            this.mDither = dither;
            this.mAutoMirrored = autoMirror;
        }

        NinePatchState(NinePatchState state) {
            this.mTargetDensity = 160;
            this.mNinePatch = state.mNinePatch;
            this.mPadding = state.mPadding;
            this.mOpticalInsets = state.mOpticalInsets;
            this.mDither = state.mDither;
            this.mChangingConfigurations = state.mChangingConfigurations;
            this.mTargetDensity = state.mTargetDensity;
            this.mAutoMirrored = state.mAutoMirrored;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Bitmap getBitmap() {
            return this.mNinePatch.getBitmap();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new NinePatchDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new NinePatchDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    private NinePatchDrawable(NinePatchState state, Resources res) {
        this.mOpticalInsets = Insets.NONE;
        this.mTargetDensity = 160;
        setNinePatchState(state, res);
    }
}