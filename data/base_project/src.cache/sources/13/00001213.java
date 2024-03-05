package android.support.v7.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R;

/* loaded from: DrawerArrowDrawable.class */
abstract class DrawerArrowDrawable extends Drawable {
    private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45.0d);
    private final float mBarGap;
    private final float mBarSize;
    private final float mBarThickness;
    private final float mMiddleArrowSize;
    private float mProgress;
    private final int mSize;
    private final boolean mSpin;
    private final float mTopBottomArrowSize;
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private boolean mVerticalMirror = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawerArrowDrawable(Context context) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, R.attr.drawerArrowStyle, R.style.Base_Widget_AppCompat_DrawerArrowToggle);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(obtainStyledAttributes.getColor(R.styleable.DrawerArrowToggle_color, 0));
        this.mSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
        this.mBarSize = obtainStyledAttributes.getDimension(R.styleable.DrawerArrowToggle_barSize, 0.0f);
        this.mTopBottomArrowSize = obtainStyledAttributes.getDimension(R.styleable.DrawerArrowToggle_topBottomBarArrowSize, 0.0f);
        this.mBarThickness = obtainStyledAttributes.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0f);
        this.mBarGap = obtainStyledAttributes.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0f);
        this.mSpin = obtainStyledAttributes.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true);
        this.mMiddleArrowSize = obtainStyledAttributes.getDimension(R.styleable.DrawerArrowToggle_middleBarArrowSize, 0.0f);
        obtainStyledAttributes.recycle();
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.SQUARE);
        this.mPaint.setStrokeWidth(this.mBarThickness);
    }

    private static float lerp(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        boolean isLayoutRtl = isLayoutRtl();
        float lerp = lerp(this.mBarSize, this.mTopBottomArrowSize, this.mProgress);
        float lerp2 = lerp(this.mBarSize, this.mMiddleArrowSize, this.mProgress);
        float lerp3 = lerp(0.0f, this.mBarThickness / 2.0f, this.mProgress);
        float lerp4 = lerp(0.0f, ARROW_HEAD_ANGLE, this.mProgress);
        float lerp5 = lerp(isLayoutRtl ? 0.0f : -180.0f, isLayoutRtl ? 180.0f : 0.0f, this.mProgress);
        float lerp6 = lerp(this.mBarGap + this.mBarThickness, 0.0f, this.mProgress);
        this.mPath.rewind();
        float f = (-lerp2) / 2.0f;
        this.mPath.moveTo(f + lerp3, 0.0f);
        this.mPath.rLineTo(lerp2 - lerp3, 0.0f);
        float round = (float) Math.round(lerp * Math.cos(lerp4));
        float round2 = (float) Math.round(lerp * Math.sin(lerp4));
        this.mPath.moveTo(f, lerp6);
        this.mPath.rLineTo(round, round2);
        this.mPath.moveTo(f, -lerp6);
        this.mPath.rLineTo(round, -round2);
        this.mPath.moveTo(0.0f, 0.0f);
        this.mPath.close();
        canvas.save();
        if (this.mSpin) {
            canvas.rotate(lerp5 * (this.mVerticalMirror ^ isLayoutRtl ? -1 : 1), bounds.centerX(), bounds.centerY());
        } else if (isLayoutRtl) {
            canvas.rotate(180.0f, bounds.centerX(), bounds.centerY());
        }
        canvas.translate(bounds.centerX(), bounds.centerY());
        canvas.drawPath(this.mPath, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mSize;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mSize;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public float getProgress() {
        return this.mProgress;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isAutoMirrored() {
        return true;
    }

    abstract boolean isLayoutRtl();

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void setProgress(float f) {
        this.mProgress = f;
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setVerticalMirror(boolean z) {
        this.mVerticalMirror = z;
    }
}