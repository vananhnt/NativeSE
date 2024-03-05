package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/* loaded from: RoundedBitmapDrawable.class */
public abstract class RoundedBitmapDrawable extends Drawable {
    private static final int DEFAULT_PAINT_FLAGS = 6;
    Bitmap mBitmap;
    private int mBitmapHeight;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private float mCornerRadius;
    private int mTargetDensity;
    private int mGravity = 119;
    private Paint mPaint = new Paint(6);
    final Rect mDstRect = new Rect();
    final RectF mDstRectF = new RectF();
    private boolean mApplyGravity = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RoundedBitmapDrawable(Resources resources, Bitmap bitmap) {
        this.mTargetDensity = 160;
        if (resources != null) {
            this.mTargetDensity = resources.getDisplayMetrics().densityDpi;
        }
        this.mBitmap = bitmap;
        if (this.mBitmap != null) {
            computeBitmapSize();
            this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            return;
        }
        this.mBitmapHeight = -1;
        this.mBitmapWidth = -1;
    }

    private void computeBitmapSize() {
        this.mBitmapWidth = this.mBitmap.getScaledWidth(this.mTargetDensity);
        this.mBitmapHeight = this.mBitmap.getScaledHeight(this.mTargetDensity);
    }

    private static boolean isGreaterThanZero(float f) {
        return Float.compare(f, 0.0f) > 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Bitmap bitmap = this.mBitmap;
        if (bitmap == null) {
            return;
        }
        updateDstRect();
        Paint paint = this.mPaint;
        if (paint.getShader() == null) {
            canvas.drawBitmap(bitmap, (Rect) null, this.mDstRect, paint);
            return;
        }
        RectF rectF = this.mDstRectF;
        float f = this.mCornerRadius;
        canvas.drawRoundRect(rectF, f, f, paint);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mPaint.getAlpha();
    }

    public final Bitmap getBitmap() {
        return this.mBitmap;
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public int getGravity() {
        return this.mGravity;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.mGravity != 119) {
            return -3;
        }
        Bitmap bitmap = this.mBitmap;
        int i = -3;
        if (bitmap != null) {
            i = -3;
            if (!bitmap.hasAlpha()) {
                i = -3;
                if (this.mPaint.getAlpha() >= 255) {
                    i = isGreaterThanZero(this.mCornerRadius) ? -3 : -1;
                }
            }
        }
        return i;
    }

    public final Paint getPaint() {
        return this.mPaint;
    }

    void gravityCompatApply(int i, int i2, int i3, Rect rect, Rect rect2) {
        throw new UnsupportedOperationException();
    }

    public boolean hasAntiAlias() {
        return this.mPaint.isAntiAlias();
    }

    public boolean hasMipMap() {
        throw new UnsupportedOperationException();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (i != this.mPaint.getAlpha()) {
            this.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    public void setAntiAlias(boolean z) {
        this.mPaint.setAntiAlias(z);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public void setCornerRadius(float f) {
        if (isGreaterThanZero(f)) {
            this.mPaint.setShader(this.mBitmapShader);
        } else {
            this.mPaint.setShader(null);
        }
        this.mCornerRadius = f;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        this.mPaint.setDither(z);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean z) {
        this.mPaint.setFilterBitmap(z);
        invalidateSelf();
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            this.mGravity = i;
            this.mApplyGravity = true;
            invalidateSelf();
        }
    }

    public void setMipMap(boolean z) {
        throw new UnsupportedOperationException();
    }

    public void setTargetDensity(int i) {
        if (this.mTargetDensity != i) {
            if (i == 0) {
                i = 160;
            }
            this.mTargetDensity = i;
            if (this.mBitmap != null) {
                computeBitmapSize();
            }
            invalidateSelf();
        }
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics displayMetrics) {
        setTargetDensity(displayMetrics.densityDpi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDstRect() {
        if (this.mApplyGravity) {
            gravityCompatApply(this.mGravity, this.mBitmapWidth, this.mBitmapHeight, getBounds(), this.mDstRect);
            this.mDstRectF.set(this.mDstRect);
            this.mApplyGravity = false;
        }
    }
}