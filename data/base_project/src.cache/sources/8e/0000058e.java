package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: BitmapDrawable.class */
public class BitmapDrawable extends Drawable {
    private static final int DEFAULT_PAINT_FLAGS = 6;
    private BitmapState mBitmapState;
    private Bitmap mBitmap;
    private int mTargetDensity;
    private final Rect mDstRect;
    private boolean mApplyGravity;
    private boolean mMutated;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private Matrix mMirrorMatrix;

    @Deprecated
    public BitmapDrawable() {
        this.mDstRect = new Rect();
        this.mBitmapState = new BitmapState((Bitmap) null);
    }

    @Deprecated
    public BitmapDrawable(Resources res) {
        this.mDstRect = new Rect();
        this.mBitmapState = new BitmapState((Bitmap) null);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
    }

    @Deprecated
    public BitmapDrawable(Bitmap bitmap) {
        this(new BitmapState(bitmap), (Resources) null);
    }

    public BitmapDrawable(Resources res, Bitmap bitmap) {
        this(new BitmapState(bitmap), res);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
    }

    @Deprecated
    public BitmapDrawable(String filepath) {
        this(new BitmapState(BitmapFactory.decodeFile(filepath)), (Resources) null);
        if (this.mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filepath);
        }
    }

    public BitmapDrawable(Resources res, String filepath) {
        this(new BitmapState(BitmapFactory.decodeFile(filepath)), (Resources) null);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
        if (this.mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filepath);
        }
    }

    @Deprecated
    public BitmapDrawable(InputStream is) {
        this(new BitmapState(BitmapFactory.decodeStream(is)), (Resources) null);
        if (this.mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + is);
        }
    }

    public BitmapDrawable(Resources res, InputStream is) {
        this(new BitmapState(BitmapFactory.decodeStream(is)), (Resources) null);
        this.mBitmapState.mTargetDensity = this.mTargetDensity;
        if (this.mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + is);
        }
    }

    public final Paint getPaint() {
        return this.mBitmapState.mPaint;
    }

    public final Bitmap getBitmap() {
        return this.mBitmap;
    }

    private void computeBitmapSize() {
        this.mBitmapWidth = this.mBitmap.getScaledWidth(this.mTargetDensity);
        this.mBitmapHeight = this.mBitmap.getScaledHeight(this.mTargetDensity);
    }

    private void setBitmap(Bitmap bitmap) {
        if (bitmap != this.mBitmap) {
            this.mBitmap = bitmap;
            if (bitmap != null) {
                computeBitmapSize();
            } else {
                this.mBitmapHeight = -1;
                this.mBitmapWidth = -1;
            }
            invalidateSelf();
        }
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics metrics) {
        setTargetDensity(metrics.densityDpi);
    }

    public void setTargetDensity(int density) {
        if (this.mTargetDensity != density) {
            this.mTargetDensity = density == 0 ? 160 : density;
            if (this.mBitmap != null) {
                computeBitmapSize();
            }
            invalidateSelf();
        }
    }

    public int getGravity() {
        return this.mBitmapState.mGravity;
    }

    public void setGravity(int gravity) {
        if (this.mBitmapState.mGravity != gravity) {
            this.mBitmapState.mGravity = gravity;
            this.mApplyGravity = true;
            invalidateSelf();
        }
    }

    public void setMipMap(boolean mipMap) {
        if (this.mBitmapState.mBitmap != null) {
            this.mBitmapState.mBitmap.setHasMipMap(mipMap);
            invalidateSelf();
        }
    }

    public boolean hasMipMap() {
        return this.mBitmapState.mBitmap != null && this.mBitmapState.mBitmap.hasMipMap();
    }

    public void setAntiAlias(boolean aa) {
        this.mBitmapState.mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    public boolean hasAntiAlias() {
        return this.mBitmapState.mPaint.isAntiAlias();
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean filter) {
        this.mBitmapState.mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        this.mBitmapState.mPaint.setDither(dither);
        invalidateSelf();
    }

    public Shader.TileMode getTileModeX() {
        return this.mBitmapState.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return this.mBitmapState.mTileModeY;
    }

    public void setTileModeX(Shader.TileMode mode) {
        setTileModeXY(mode, this.mBitmapState.mTileModeY);
    }

    public final void setTileModeY(Shader.TileMode mode) {
        setTileModeXY(this.mBitmapState.mTileModeX, mode);
    }

    public void setTileModeXY(Shader.TileMode xmode, Shader.TileMode ymode) {
        BitmapState state = this.mBitmapState;
        if (state.mTileModeX != xmode || state.mTileModeY != ymode) {
            state.mTileModeX = xmode;
            state.mTileModeY = ymode;
            state.mRebuildShader = true;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAutoMirrored(boolean mirrored) {
        if (this.mBitmapState.mAutoMirrored != mirrored) {
            this.mBitmapState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        return this.mBitmapState.mAutoMirrored;
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mBitmapState.mChangingConfigurations;
    }

    private boolean needMirroring() {
        return isAutoMirrored() && getLayoutDirection() == 1;
    }

    private void updateMirrorMatrix(float dx) {
        if (this.mMirrorMatrix == null) {
            this.mMirrorMatrix = new Matrix();
        }
        this.mMirrorMatrix.setTranslate(dx, 0.0f);
        this.mMirrorMatrix.preScale(-1.0f, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mApplyGravity = true;
        Shader shader = this.mBitmapState.mPaint.getShader();
        if (shader != null) {
            if (needMirroring()) {
                updateMirrorMatrix(bounds.right - bounds.left);
                shader.setLocalMatrix(this.mMirrorMatrix);
            } else if (this.mMirrorMatrix != null) {
                this.mMirrorMatrix = null;
                shader.setLocalMatrix(Matrix.IDENTITY_MATRIX);
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            BitmapState state = this.mBitmapState;
            if (state.mRebuildShader) {
                Shader.TileMode tmx = state.mTileModeX;
                Shader.TileMode tmy = state.mTileModeY;
                if (tmx == null && tmy == null) {
                    state.mPaint.setShader(null);
                } else {
                    state.mPaint.setShader(new BitmapShader(bitmap, tmx == null ? Shader.TileMode.CLAMP : tmx, tmy == null ? Shader.TileMode.CLAMP : tmy));
                }
                state.mRebuildShader = false;
                copyBounds(this.mDstRect);
            }
            Shader shader = state.mPaint.getShader();
            boolean needMirroring = needMirroring();
            if (shader == null) {
                if (this.mApplyGravity) {
                    int layoutDirection = getLayoutDirection();
                    Gravity.apply(state.mGravity, this.mBitmapWidth, this.mBitmapHeight, getBounds(), this.mDstRect, layoutDirection);
                    this.mApplyGravity = false;
                }
                if (needMirroring) {
                    canvas.save();
                    canvas.translate(this.mDstRect.right - this.mDstRect.left, 0.0f);
                    canvas.scale(-1.0f, 1.0f);
                }
                canvas.drawBitmap(bitmap, (Rect) null, this.mDstRect, state.mPaint);
                if (needMirroring) {
                    canvas.restore();
                    return;
                }
                return;
            }
            if (this.mApplyGravity) {
                copyBounds(this.mDstRect);
                this.mApplyGravity = false;
            }
            if (needMirroring) {
                updateMirrorMatrix(this.mDstRect.right - this.mDstRect.left);
                shader.setLocalMatrix(this.mMirrorMatrix);
            } else if (this.mMirrorMatrix != null) {
                this.mMirrorMatrix = null;
                shader.setLocalMatrix(Matrix.IDENTITY_MATRIX);
            }
            canvas.drawRect(this.mDstRect, state.mPaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        int oldAlpha = this.mBitmapState.mPaint.getAlpha();
        if (alpha != oldAlpha) {
            this.mBitmapState.mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mBitmapState.mPaint.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.mBitmapState.mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setXfermode(Xfermode xfermode) {
        this.mBitmapState.mPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mBitmapState = new BitmapState(this.mBitmapState);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        TypedArray a = r.obtainAttributes(attrs, R.styleable.BitmapDrawable);
        int id = a.getResourceId(1, 0);
        if (id == 0) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": <bitmap> requires a valid src attribute");
        }
        Bitmap bitmap = BitmapFactory.decodeResource(r, id);
        if (bitmap == null) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": <bitmap> requires a valid src attribute");
        }
        this.mBitmapState.mBitmap = bitmap;
        setBitmap(bitmap);
        setTargetDensity(r.getDisplayMetrics());
        setMipMap(a.getBoolean(6, bitmap.hasMipMap()));
        setAutoMirrored(a.getBoolean(7, false));
        Paint paint = this.mBitmapState.mPaint;
        paint.setAntiAlias(a.getBoolean(2, paint.isAntiAlias()));
        paint.setFilterBitmap(a.getBoolean(3, paint.isFilterBitmap()));
        paint.setDither(a.getBoolean(4, paint.isDither()));
        setGravity(a.getInt(0, 119));
        int tileMode = a.getInt(5, -1);
        if (tileMode != -1) {
            switch (tileMode) {
                case 0:
                    setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    break;
                case 1:
                    setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                    break;
                case 2:
                    setTileModeXY(Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
                    break;
            }
        }
        a.recycle();
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
    public int getOpacity() {
        Bitmap bm;
        return (this.mBitmapState.mGravity == 119 && (bm = this.mBitmap) != null && !bm.hasAlpha() && this.mBitmapState.mPaint.getAlpha() >= 255) ? -1 : -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        this.mBitmapState.mChangingConfigurations = getChangingConfigurations();
        return this.mBitmapState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BitmapDrawable$BitmapState.class */
    public static final class BitmapState extends Drawable.ConstantState {
        Bitmap mBitmap;
        int mChangingConfigurations;
        int mGravity;
        Paint mPaint;
        Shader.TileMode mTileModeX;
        Shader.TileMode mTileModeY;
        int mTargetDensity;
        boolean mRebuildShader;
        boolean mAutoMirrored;

        BitmapState(Bitmap bitmap) {
            this.mGravity = 119;
            this.mPaint = new Paint(6);
            this.mTileModeX = null;
            this.mTileModeY = null;
            this.mTargetDensity = 160;
            this.mBitmap = bitmap;
        }

        BitmapState(BitmapState bitmapState) {
            this(bitmapState.mBitmap);
            this.mChangingConfigurations = bitmapState.mChangingConfigurations;
            this.mGravity = bitmapState.mGravity;
            this.mTileModeX = bitmapState.mTileModeX;
            this.mTileModeY = bitmapState.mTileModeY;
            this.mTargetDensity = bitmapState.mTargetDensity;
            this.mPaint = new Paint(bitmapState.mPaint);
            this.mRebuildShader = bitmapState.mRebuildShader;
            this.mAutoMirrored = bitmapState.mAutoMirrored;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Bitmap getBitmap() {
            return this.mBitmap;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new BitmapDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new BitmapDrawable(this, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    private BitmapDrawable(BitmapState state, Resources res) {
        this.mDstRect = new Rect();
        this.mBitmapState = state;
        if (res != null) {
            this.mTargetDensity = res.getDisplayMetrics().densityDpi;
        } else {
            this.mTargetDensity = state.mTargetDensity;
        }
        setBitmap(state != null ? state.mBitmap : null);
    }
}