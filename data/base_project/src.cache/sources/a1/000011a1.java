package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: MaterialProgressDrawable.class */
public class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final int ANIMATION_DURATION = 1333;
    private static final int ARROW_HEIGHT = 5;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final float ARROW_OFFSET_ANGLE = 5.0f;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final float CENTER_RADIUS = 8.75f;
    private static final float CENTER_RADIUS_LARGE = 12.5f;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    static final int DEFAULT = 1;
    static final int LARGE = 0;
    private static final float MAX_PROGRESS_ARC = 0.8f;
    private static final float NUM_POINTS = 5.0f;
    private static final float STROKE_WIDTH = 2.5f;
    private static final float STROKE_WIDTH_LARGE = 3.0f;
    private Animation mAnimation;
    private Animation mFinishAnimation;
    private double mHeight;
    private View mParent;
    private Resources mResources;
    private float mRotation;
    private float mRotationCount;
    private double mWidth;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator END_CURVE_INTERPOLATOR = new EndCurveInterpolator();
    private static final Interpolator START_CURVE_INTERPOLATOR = new StartCurveInterpolator();
    private static final Interpolator EASE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private final int[] COLORS = {-16777216};
    private final ArrayList<Animation> mAnimators = new ArrayList<>();
    private final Drawable.Callback mCallback = new Drawable.Callback(this) { // from class: android.support.v4.widget.MaterialProgressDrawable.5
        final MaterialProgressDrawable this$0;

        {
            this.this$0 = this;
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable drawable) {
            this.this$0.invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
            this.this$0.scheduleSelf(runnable, j);
        }

        @Override // android.graphics.drawable.Drawable.Callback
        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
            this.this$0.unscheduleSelf(runnable);
        }
    };
    private final Ring mRing = new Ring(this.mCallback);

    /* loaded from: MaterialProgressDrawable$EndCurveInterpolator.class */
    private static class EndCurveInterpolator extends AccelerateDecelerateInterpolator {
        private EndCurveInterpolator() {
        }

        @Override // android.view.animation.AccelerateDecelerateInterpolator, android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            return super.getInterpolation(Math.max(0.0f, (f - 0.5f) * 2.0f));
        }
    }

    @Retention(RetentionPolicy.CLASS)
    @IntDef({0, 1})
    /* loaded from: MaterialProgressDrawable$ProgressDrawableSize.class */
    public @interface ProgressDrawableSize {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: MaterialProgressDrawable$Ring.class */
    public static class Ring {
        private int mAlpha;
        private Path mArrow;
        private int mArrowHeight;
        private float mArrowScale;
        private int mArrowWidth;
        private int mBackgroundColor;
        private final Drawable.Callback mCallback;
        private int mColorIndex;
        private int[] mColors;
        private double mRingCenterRadius;
        private boolean mShowArrow;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private float mStartingStartTrim;
        private final RectF mTempBounds = new RectF();
        private final Paint mPaint = new Paint();
        private final Paint mArrowPaint = new Paint();
        private float mStartTrim = 0.0f;
        private float mEndTrim = 0.0f;
        private float mRotation = 0.0f;
        private float mStrokeWidth = 5.0f;
        private float mStrokeInset = MaterialProgressDrawable.STROKE_WIDTH;
        private final Paint mCirclePaint = new Paint();

        public Ring(Drawable.Callback callback) {
            this.mCallback = callback;
            this.mPaint.setStrokeCap(Paint.Cap.SQUARE);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mArrowPaint.setStyle(Paint.Style.FILL);
            this.mArrowPaint.setAntiAlias(true);
        }

        private void drawTriangle(Canvas canvas, float f, float f2, Rect rect) {
            if (this.mShowArrow) {
                Path path = this.mArrow;
                if (path == null) {
                    this.mArrow = new Path();
                    this.mArrow.setFillType(Path.FillType.EVEN_ODD);
                } else {
                    path.reset();
                }
                float f3 = ((int) this.mStrokeInset) / 2;
                float f4 = this.mArrowScale;
                float cos = (float) ((this.mRingCenterRadius * Math.cos(0.0d)) + rect.exactCenterX());
                float sin = (float) ((this.mRingCenterRadius * Math.sin(0.0d)) + rect.exactCenterY());
                this.mArrow.moveTo(0.0f, 0.0f);
                this.mArrow.lineTo(this.mArrowWidth * this.mArrowScale, 0.0f);
                Path path2 = this.mArrow;
                float f5 = this.mArrowWidth;
                float f6 = this.mArrowScale;
                path2.lineTo((f5 * f6) / 2.0f, this.mArrowHeight * f6);
                this.mArrow.offset(cos - (f3 * f4), sin);
                this.mArrow.close();
                this.mArrowPaint.setColor(this.mColors[this.mColorIndex]);
                canvas.rotate((f + f2) - 5.0f, rect.exactCenterX(), rect.exactCenterY());
                canvas.drawPath(this.mArrow, this.mArrowPaint);
            }
        }

        private void invalidateSelf() {
            this.mCallback.invalidateDrawable(null);
        }

        public void draw(Canvas canvas, Rect rect) {
            RectF rectF = this.mTempBounds;
            rectF.set(rect);
            float f = this.mStrokeInset;
            rectF.inset(f, f);
            float f2 = this.mStartTrim;
            float f3 = this.mRotation;
            float f4 = (f2 + f3) * 360.0f;
            float f5 = ((this.mEndTrim + f3) * 360.0f) - f4;
            this.mPaint.setColor(this.mColors[this.mColorIndex]);
            canvas.drawArc(rectF, f4, f5, false, this.mPaint);
            drawTriangle(canvas, f4, f5, rect);
            if (this.mAlpha < 255) {
                this.mCirclePaint.setColor(this.mBackgroundColor);
                this.mCirclePaint.setAlpha(255 - this.mAlpha);
                canvas.drawCircle(rect.exactCenterX(), rect.exactCenterY(), rect.width() / 2, this.mCirclePaint);
            }
        }

        public int getAlpha() {
            return this.mAlpha;
        }

        public double getCenterRadius() {
            return this.mRingCenterRadius;
        }

        public float getEndTrim() {
            return this.mEndTrim;
        }

        public float getInsets() {
            return this.mStrokeInset;
        }

        public float getRotation() {
            return this.mRotation;
        }

        public float getStartTrim() {
            return this.mStartTrim;
        }

        public float getStartingEndTrim() {
            return this.mStartingEndTrim;
        }

        public float getStartingRotation() {
            return this.mStartingRotation;
        }

        public float getStartingStartTrim() {
            return this.mStartingStartTrim;
        }

        public float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        public void goToNextColor() {
            this.mColorIndex = (this.mColorIndex + 1) % this.mColors.length;
        }

        public void resetOriginals() {
            this.mStartingStartTrim = 0.0f;
            this.mStartingEndTrim = 0.0f;
            this.mStartingRotation = 0.0f;
            setStartTrim(0.0f);
            setEndTrim(0.0f);
            setRotation(0.0f);
        }

        public void setAlpha(int i) {
            this.mAlpha = i;
        }

        public void setArrowDimensions(float f, float f2) {
            this.mArrowWidth = (int) f;
            this.mArrowHeight = (int) f2;
        }

        public void setArrowScale(float f) {
            if (f != this.mArrowScale) {
                this.mArrowScale = f;
                invalidateSelf();
            }
        }

        public void setBackgroundColor(int i) {
            this.mBackgroundColor = i;
        }

        public void setCenterRadius(double d) {
            this.mRingCenterRadius = d;
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        public void setColorIndex(int i) {
            this.mColorIndex = i;
        }

        public void setColors(@NonNull int[] iArr) {
            this.mColors = iArr;
            setColorIndex(0);
        }

        public void setEndTrim(float f) {
            this.mEndTrim = f;
            invalidateSelf();
        }

        public void setInsets(int i, int i2) {
            float min = Math.min(i, i2);
            double d = this.mRingCenterRadius;
            this.mStrokeInset = (d <= 0.0d || min < 0.0f) ? (float) Math.ceil(this.mStrokeWidth / 2.0f) : (float) ((min / 2.0f) - d);
        }

        public void setRotation(float f) {
            this.mRotation = f;
            invalidateSelf();
        }

        public void setShowArrow(boolean z) {
            if (this.mShowArrow != z) {
                this.mShowArrow = z;
                invalidateSelf();
            }
        }

        public void setStartTrim(float f) {
            this.mStartTrim = f;
            invalidateSelf();
        }

        public void setStrokeWidth(float f) {
            this.mStrokeWidth = f;
            this.mPaint.setStrokeWidth(f);
            invalidateSelf();
        }

        public void storeOriginals() {
            this.mStartingStartTrim = this.mStartTrim;
            this.mStartingEndTrim = this.mEndTrim;
            this.mStartingRotation = this.mRotation;
        }
    }

    /* loaded from: MaterialProgressDrawable$StartCurveInterpolator.class */
    private static class StartCurveInterpolator extends AccelerateDecelerateInterpolator {
        private StartCurveInterpolator() {
        }

        @Override // android.view.animation.AccelerateDecelerateInterpolator, android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            return super.getInterpolation(Math.min(1.0f, 2.0f * f));
        }
    }

    public MaterialProgressDrawable(Context context, View view) {
        this.mParent = view;
        this.mResources = context.getResources();
        this.mRing.setColors(this.COLORS);
        updateSizes(1);
        setupAnimators();
    }

    private float getRotation() {
        return this.mRotation;
    }

    private void setSizeParameters(double d, double d2, double d3, double d4, float f, float f2) {
        Ring ring = this.mRing;
        float f3 = this.mResources.getDisplayMetrics().density;
        this.mWidth = f3 * d;
        this.mHeight = f3 * d2;
        ring.setStrokeWidth(((float) d4) * f3);
        ring.setCenterRadius(f3 * d3);
        ring.setColorIndex(0);
        ring.setArrowDimensions(f * f3, f2 * f3);
        ring.setInsets((int) this.mWidth, (int) this.mHeight);
    }

    private void setupAnimators() {
        Ring ring = this.mRing;
        Animation animation = new Animation(this, ring) { // from class: android.support.v4.widget.MaterialProgressDrawable.1
            final MaterialProgressDrawable this$0;
            final Ring val$ring;

            {
                this.this$0 = this;
                this.val$ring = ring;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                float floor = (float) (Math.floor(this.val$ring.getStartingRotation() / 0.8f) + 1.0d);
                this.val$ring.setStartTrim(this.val$ring.getStartingStartTrim() + ((this.val$ring.getStartingEndTrim() - this.val$ring.getStartingStartTrim()) * f));
                this.val$ring.setRotation(this.val$ring.getStartingRotation() + ((floor - this.val$ring.getStartingRotation()) * f));
                this.val$ring.setArrowScale(1.0f - f);
            }
        };
        animation.setInterpolator(EASE_INTERPOLATOR);
        animation.setDuration(666L);
        animation.setAnimationListener(new Animation.AnimationListener(this, ring) { // from class: android.support.v4.widget.MaterialProgressDrawable.2
            final MaterialProgressDrawable this$0;
            final Ring val$ring;

            {
                this.this$0 = this;
                this.val$ring = ring;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation2) {
                this.val$ring.goToNextColor();
                this.val$ring.storeOriginals();
                this.val$ring.setShowArrow(false);
                this.this$0.mParent.startAnimation(this.this$0.mAnimation);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation2) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation2) {
            }
        });
        Animation animation2 = new Animation(this, ring) { // from class: android.support.v4.widget.MaterialProgressDrawable.3
            final MaterialProgressDrawable this$0;
            final Ring val$ring;

            {
                this.this$0 = this;
                this.val$ring = ring;
            }

            @Override // android.view.animation.Animation
            public void applyTransformation(float f, Transformation transformation) {
                float radians = (float) Math.toRadians(this.val$ring.getStrokeWidth() / (this.val$ring.getCenterRadius() * 6.283185307179586d));
                float startingEndTrim = this.val$ring.getStartingEndTrim();
                float startingStartTrim = this.val$ring.getStartingStartTrim();
                float startingRotation = this.val$ring.getStartingRotation();
                this.val$ring.setEndTrim((MaterialProgressDrawable.START_CURVE_INTERPOLATOR.getInterpolation(f) * (0.8f - radians)) + startingEndTrim);
                this.val$ring.setStartTrim((MaterialProgressDrawable.END_CURVE_INTERPOLATOR.getInterpolation(f) * 0.8f) + startingStartTrim);
                this.val$ring.setRotation((0.25f * f) + startingRotation);
                this.this$0.setRotation((144.0f * f) + ((this.this$0.mRotationCount / 5.0f) * 720.0f));
            }
        };
        animation2.setRepeatCount(-1);
        animation2.setRepeatMode(1);
        animation2.setInterpolator(LINEAR_INTERPOLATOR);
        animation2.setDuration(1333L);
        animation2.setAnimationListener(new Animation.AnimationListener(this, ring) { // from class: android.support.v4.widget.MaterialProgressDrawable.4
            final MaterialProgressDrawable this$0;
            final Ring val$ring;

            {
                this.this$0 = this;
                this.val$ring = ring;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation3) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation3) {
                this.val$ring.storeOriginals();
                this.val$ring.goToNextColor();
                Ring ring2 = this.val$ring;
                ring2.setStartTrim(ring2.getEndTrim());
                MaterialProgressDrawable materialProgressDrawable = this.this$0;
                materialProgressDrawable.mRotationCount = (materialProgressDrawable.mRotationCount + 1.0f) % 5.0f;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation3) {
                this.this$0.mRotationCount = 0.0f;
            }
        });
        this.mFinishAnimation = animation;
        this.mAnimation = animation2;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int save = canvas.save();
        canvas.rotate(this.mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        this.mRing.draw(canvas, bounds);
        canvas.restoreToCount(save);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mRing.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return (int) this.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return (int) this.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        ArrayList<Animation> arrayList = this.mAnimators;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Animation animation = arrayList.get(i);
            if (animation.hasStarted() && !animation.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mRing.setAlpha(i);
    }

    public void setArrowScale(float f) {
        this.mRing.setArrowScale(f);
    }

    public void setBackgroundColor(int i) {
        this.mRing.setBackgroundColor(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mRing.setColorFilter(colorFilter);
    }

    public void setColorSchemeColors(int... iArr) {
        this.mRing.setColors(iArr);
        this.mRing.setColorIndex(0);
    }

    public void setProgressRotation(float f) {
        this.mRing.setRotation(f);
    }

    void setRotation(float f) {
        this.mRotation = f;
        invalidateSelf();
    }

    public void setStartEndTrim(float f, float f2) {
        this.mRing.setStartTrim(f);
        this.mRing.setEndTrim(f2);
    }

    public void showArrow(boolean z) {
        this.mRing.setShowArrow(z);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        this.mAnimation.reset();
        this.mRing.storeOriginals();
        if (this.mRing.getEndTrim() != this.mRing.getStartTrim()) {
            this.mParent.startAnimation(this.mFinishAnimation);
            return;
        }
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
        this.mParent.startAnimation(this.mAnimation);
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mParent.clearAnimation();
        setRotation(0.0f);
        this.mRing.setShowArrow(false);
        this.mRing.setColorIndex(0);
        this.mRing.resetOriginals();
    }

    public void updateSizes(@ProgressDrawableSize int i) {
        if (i == 0) {
            setSizeParameters(56.0d, 56.0d, 12.5d, 3.0d, 12.0f, 6.0f);
        } else {
            setSizeParameters(40.0d, 40.0d, 8.75d, 2.5d, 10.0f, 5.0f);
        }
    }
}