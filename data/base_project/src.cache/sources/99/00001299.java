package android.support.v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/* loaded from: ProgressBarCompat.class */
public class ProgressBarCompat extends View {
    private static final int ANIMATION_RESOLUTION = 200;
    private static final int MAX_LEVEL = 10000;
    private static final int[] android_R_styleable_ProgressBar = {16843062, 16843063, 16843064, 16843065, 16843066, 16843067, 16843068, 16843069, 16843070, 16843071, 16843039, 16843072, 16843040, 16843073};
    private AlphaAnimation mAnimation;
    private int mBehavior;
    private Drawable mCurrentDrawable;
    private int mDuration;
    private boolean mInDrawing;
    private boolean mIndeterminate;
    private Drawable mIndeterminateDrawable;
    private Interpolator mInterpolator;
    private long mLastDrawTime;
    private int mMax;
    int mMaxHeight;
    int mMaxWidth;
    int mMinHeight;
    int mMinWidth;
    private boolean mNoInvalidate;
    private boolean mOnlyIndeterminate;
    private int mProgress;
    private Drawable mProgressDrawable;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    Bitmap mSampleTile;
    private int mSecondaryProgress;
    private boolean mShouldStartAnimationDrawable;
    private Transformation mTransformation;
    private long mUiThreadId;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ProgressBarCompat$RefreshProgressRunnable.class */
    public class RefreshProgressRunnable implements Runnable {
        private boolean mFromUser;
        private int mId;
        private int mProgress;
        final ProgressBarCompat this$0;

        RefreshProgressRunnable(ProgressBarCompat progressBarCompat, int i, int i2, boolean z) {
            this.this$0 = progressBarCompat;
            this.mId = i;
            this.mProgress = i2;
            this.mFromUser = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.doRefreshProgress(this.mId, this.mProgress, this.mFromUser, true);
            this.this$0.mRefreshProgressRunnable = this;
        }

        public void setup(int i, int i2, boolean z) {
            this.mId = i;
            this.mProgress = i2;
            this.mFromUser = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ProgressBarCompat$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.support.v7.internal.widget.ProgressBarCompat.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int progress;
        int secondaryProgress;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.progress = parcel.readInt();
            this.secondaryProgress = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.progress);
            parcel.writeInt(this.secondaryProgress);
        }
    }

    public ProgressBarCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mUiThreadId = Thread.currentThread().getId();
        initProgressBar();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, android_R_styleable_ProgressBar, i, i2);
        this.mNoInvalidate = true;
        setMax(obtainStyledAttributes.getInt(0, this.mMax));
        setProgress(obtainStyledAttributes.getInt(1, this.mProgress));
        setSecondaryProgress(obtainStyledAttributes.getInt(2, this.mSecondaryProgress));
        boolean z = obtainStyledAttributes.getBoolean(3, this.mIndeterminate);
        this.mOnlyIndeterminate = obtainStyledAttributes.getBoolean(4, this.mOnlyIndeterminate);
        Drawable drawable = obtainStyledAttributes.getDrawable(5);
        if (drawable != null) {
            setIndeterminateDrawable(tileifyIndeterminate(drawable));
        }
        Drawable drawable2 = obtainStyledAttributes.getDrawable(6);
        if (drawable2 != null) {
            setProgressDrawable(tileify(drawable2, false));
        }
        this.mDuration = obtainStyledAttributes.getInt(7, this.mDuration);
        this.mBehavior = obtainStyledAttributes.getInt(8, this.mBehavior);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(9, this.mMinWidth);
        this.mMaxWidth = obtainStyledAttributes.getDimensionPixelSize(10, this.mMaxWidth);
        this.mMinHeight = obtainStyledAttributes.getDimensionPixelSize(11, this.mMinHeight);
        this.mMaxHeight = obtainStyledAttributes.getDimensionPixelSize(12, this.mMaxHeight);
        int resourceId = obtainStyledAttributes.getResourceId(13, 17432587);
        if (resourceId > 0) {
            setInterpolator(context, resourceId);
        }
        obtainStyledAttributes.recycle();
        this.mNoInvalidate = false;
        setIndeterminate(this.mOnlyIndeterminate ? true : z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRefreshProgress(int i, int i2, boolean z, boolean z2) {
        synchronized (this) {
            float f = this.mMax > 0 ? i2 / this.mMax : 0.0f;
            Drawable drawable = this.mCurrentDrawable;
            if (drawable != null) {
                Drawable drawable2 = null;
                if (drawable instanceof LayerDrawable) {
                    drawable2 = ((LayerDrawable) drawable).findDrawableByLayerId(i);
                }
                int i3 = (int) (10000.0f * f);
                if (drawable2 == null) {
                    drawable2 = drawable;
                }
                drawable2.setLevel(i3);
            } else {
                invalidate();
            }
        }
    }

    private void initProgressBar() {
        this.mMax = 100;
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mIndeterminate = false;
        this.mOnlyIndeterminate = false;
        this.mDuration = 4000;
        this.mBehavior = 1;
        this.mMinWidth = 24;
        this.mMaxWidth = 48;
        this.mMinHeight = 24;
        this.mMaxHeight = 48;
    }

    private void refreshProgress(int i, int i2, boolean z) {
        RefreshProgressRunnable refreshProgressRunnable;
        synchronized (this) {
            if (this.mUiThreadId == Thread.currentThread().getId()) {
                doRefreshProgress(i, i2, z, true);
            } else {
                if (this.mRefreshProgressRunnable != null) {
                    refreshProgressRunnable = this.mRefreshProgressRunnable;
                    this.mRefreshProgressRunnable = null;
                    refreshProgressRunnable.setup(i, i2, z);
                } else {
                    refreshProgressRunnable = new RefreshProgressRunnable(this, i, i2, z);
                }
                post(refreshProgressRunnable);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v17, types: [android.graphics.drawable.ClipDrawable] */
    private Drawable tileify(Drawable drawable, boolean z) {
        if (!(drawable instanceof LayerDrawable)) {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (this.mSampleTile == null) {
                    this.mSampleTile = bitmap;
                }
                ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
                shapeDrawable.getPaint().setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP));
                if (z) {
                    shapeDrawable = new ClipDrawable(shapeDrawable, 3, 1);
                }
                return shapeDrawable;
            }
            return drawable;
        }
        LayerDrawable layerDrawable = (LayerDrawable) drawable;
        int numberOfLayers = layerDrawable.getNumberOfLayers();
        Drawable[] drawableArr = new Drawable[numberOfLayers];
        for (int i = 0; i < numberOfLayers; i++) {
            int id = layerDrawable.getId(i);
            drawableArr[i] = tileify(layerDrawable.getDrawable(i), id == 16908301 || id == 16908303);
        }
        LayerDrawable layerDrawable2 = new LayerDrawable(drawableArr);
        for (int i2 = 0; i2 < numberOfLayers; i2++) {
            layerDrawable2.setId(i2, layerDrawable.getId(i2));
        }
        return layerDrawable2;
    }

    private Drawable tileifyIndeterminate(Drawable drawable) {
        boolean z = drawable instanceof AnimationDrawable;
        AnimationDrawable animationDrawable = drawable;
        if (z) {
            AnimationDrawable animationDrawable2 = (AnimationDrawable) drawable;
            int numberOfFrames = animationDrawable2.getNumberOfFrames();
            AnimationDrawable animationDrawable3 = new AnimationDrawable();
            animationDrawable3.setOneShot(animationDrawable2.isOneShot());
            for (int i = 0; i < numberOfFrames; i++) {
                Drawable tileify = tileify(animationDrawable2.getFrame(i), true);
                tileify.setLevel(10000);
                animationDrawable3.addFrame(tileify, animationDrawable2.getDuration(i));
            }
            animationDrawable3.setLevel(10000);
            animationDrawable = animationDrawable3;
        }
        return animationDrawable;
    }

    private void updateDrawableBounds(int i, int i2) {
        int i3;
        int i4;
        int paddingRight = (i - getPaddingRight()) - getPaddingLeft();
        int paddingBottom = (i2 - getPaddingBottom()) - getPaddingTop();
        int i5 = 0;
        int i6 = 0;
        Drawable drawable = this.mIndeterminateDrawable;
        if (drawable != null) {
            if (!this.mOnlyIndeterminate || (drawable instanceof AnimationDrawable)) {
                i3 = paddingBottom;
                i4 = paddingRight;
            } else {
                float intrinsicWidth = drawable.getIntrinsicWidth() / this.mIndeterminateDrawable.getIntrinsicHeight();
                float f = i / i2;
                if (intrinsicWidth == f) {
                    i4 = paddingRight;
                    i3 = paddingBottom;
                } else if (f > intrinsicWidth) {
                    int i7 = (int) (i2 * intrinsicWidth);
                    int i8 = (i - i7) / 2;
                    i4 = i7 + i8;
                    i3 = paddingBottom;
                    i6 = i8;
                } else {
                    int i9 = (int) (i * (1.0f / intrinsicWidth));
                    i5 = (i2 - i9) / 2;
                    i3 = i5 + i9;
                    i4 = paddingRight;
                }
            }
            this.mIndeterminateDrawable.setBounds(i6, i5, i4, i3);
        } else {
            i3 = paddingBottom;
            i4 = paddingRight;
        }
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, i4, i3);
        }
    }

    private void updateDrawableState() {
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null && drawable.isStateful()) {
            this.mProgressDrawable.setState(drawableState);
        }
        Drawable drawable2 = this.mIndeterminateDrawable;
        if (drawable2 == null || !drawable2.isStateful()) {
            return;
        }
        this.mIndeterminateDrawable.setState(drawableState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    Shape getDrawableShape() {
        return new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null);
    }

    public Drawable getIndeterminateDrawable() {
        return this.mIndeterminateDrawable;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public int getMax() {
        int i;
        synchronized (this) {
            i = this.mMax;
        }
        return i;
    }

    public int getProgress() {
        int i;
        synchronized (this) {
            i = this.mIndeterminate ? 0 : this.mProgress;
        }
        return i;
    }

    public Drawable getProgressDrawable() {
        return this.mProgressDrawable;
    }

    public int getSecondaryProgress() {
        int i;
        synchronized (this) {
            i = this.mIndeterminate ? 0 : this.mSecondaryProgress;
        }
        return i;
    }

    public final void incrementProgressBy(int i) {
        synchronized (this) {
            setProgress(this.mProgress + i);
        }
    }

    public final void incrementSecondaryProgressBy(int i) {
        synchronized (this) {
            setSecondaryProgress(this.mSecondaryProgress + i);
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (this.mInDrawing) {
            return;
        }
        if (!verifyDrawable(drawable)) {
            super.invalidateDrawable(drawable);
            return;
        }
        Rect bounds = drawable.getBounds();
        int scrollX = getScrollX() + getPaddingLeft();
        int scrollY = getScrollY() + getPaddingTop();
        invalidate(bounds.left + scrollX, bounds.top + scrollY, bounds.right + scrollX, bounds.bottom + scrollY);
    }

    public boolean isIndeterminate() {
        boolean z;
        synchronized (this) {
            z = this.mIndeterminate;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIndeterminate) {
            startAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        if (this.mIndeterminate) {
            stopAnimation();
        }
        RefreshProgressRunnable refreshProgressRunnable = this.mRefreshProgressRunnable;
        if (refreshProgressRunnable != null) {
            removeCallbacks(refreshProgressRunnable);
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        synchronized (this) {
            super.onDraw(canvas);
            Drawable drawable = this.mCurrentDrawable;
            if (drawable != null) {
                canvas.save();
                canvas.translate(getPaddingLeft(), getPaddingTop());
                long drawingTime = getDrawingTime();
                if (this.mAnimation != null) {
                    this.mAnimation.getTransformation(drawingTime, this.mTransformation);
                    float alpha = this.mTransformation.getAlpha();
                    this.mInDrawing = true;
                    drawable.setLevel((int) (10000.0f * alpha));
                    this.mInDrawing = false;
                    if (SystemClock.uptimeMillis() - this.mLastDrawTime >= 200) {
                        this.mLastDrawTime = SystemClock.uptimeMillis();
                        postInvalidateDelayed(200L);
                    }
                }
                drawable.draw(canvas);
                canvas.restore();
                if (this.mShouldStartAnimationDrawable && (drawable instanceof Animatable)) {
                    ((Animatable) drawable).start();
                    this.mShouldStartAnimationDrawable = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        synchronized (this) {
            Drawable drawable = this.mCurrentDrawable;
            int i3 = 0;
            int i4 = 0;
            if (drawable != null) {
                i3 = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, drawable.getIntrinsicWidth()));
                i4 = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, drawable.getIntrinsicHeight()));
            }
            updateDrawableState();
            setMeasuredDimension(resolveSize(i3 + getPaddingLeft() + getPaddingRight(), i), resolveSize(i4 + getPaddingTop() + getPaddingBottom(), i2));
        }
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setProgress(savedState.progress);
        setSecondaryProgress(savedState.secondaryProgress);
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.progress = this.mProgress;
        savedState.secondaryProgress = this.mSecondaryProgress;
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        updateDrawableBounds(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View view, int i) {
        if (Build.VERSION.SDK_INT >= 8) {
            super.onVisibilityChanged(view, i);
        }
        if (this.mIndeterminate) {
            if (i == 8 || i == 4) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }
    }

    @Override // android.view.View
    public void postInvalidate() {
        if (this.mNoInvalidate) {
            return;
        }
        super.postInvalidate();
    }

    public void setIndeterminate(boolean z) {
        synchronized (this) {
            if ((!this.mOnlyIndeterminate || !this.mIndeterminate) && z != this.mIndeterminate) {
                this.mIndeterminate = z;
                if (z) {
                    this.mCurrentDrawable = this.mIndeterminateDrawable;
                    startAnimation();
                } else {
                    this.mCurrentDrawable = this.mProgressDrawable;
                    stopAnimation();
                }
            }
        }
    }

    public void setIndeterminateDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(this);
        }
        this.mIndeterminateDrawable = drawable;
        if (this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            postInvalidate();
        }
    }

    public void setInterpolator(Context context, int i) {
        setInterpolator(AnimationUtils.loadInterpolator(context, i));
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void setMax(int i) {
        synchronized (this) {
            if (i < 0) {
                i = 0;
            }
            if (i != this.mMax) {
                this.mMax = i;
                postInvalidate();
                if (this.mProgress > i) {
                    this.mProgress = i;
                }
                refreshProgress(16908301, this.mProgress, false);
            }
        }
    }

    public void setProgress(int i) {
        synchronized (this) {
            setProgress(i, false);
        }
    }

    void setProgress(int i, boolean z) {
        synchronized (this) {
            if (this.mIndeterminate) {
                return;
            }
            if (i < 0) {
                i = 0;
            }
            if (i > this.mMax) {
                i = this.mMax;
            }
            if (i != this.mProgress) {
                this.mProgress = i;
                refreshProgress(16908301, this.mProgress, z);
            }
        }
    }

    public void setProgressDrawable(Drawable drawable) {
        boolean z;
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 == null || drawable == drawable2) {
            z = false;
        } else {
            drawable2.setCallback(null);
            z = true;
        }
        if (drawable != null) {
            drawable.setCallback(this);
            int minimumHeight = drawable.getMinimumHeight();
            if (this.mMaxHeight < minimumHeight) {
                this.mMaxHeight = minimumHeight;
                requestLayout();
            }
        }
        this.mProgressDrawable = drawable;
        if (!this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            postInvalidate();
        }
        if (z) {
            updateDrawableBounds(getWidth(), getHeight());
            updateDrawableState();
            doRefreshProgress(16908301, this.mProgress, false, false);
            doRefreshProgress(16908303, this.mSecondaryProgress, false, false);
        }
    }

    public void setSecondaryProgress(int i) {
        synchronized (this) {
            if (this.mIndeterminate) {
                return;
            }
            if (i < 0) {
                i = 0;
            }
            if (i > this.mMax) {
                i = this.mMax;
            }
            if (i != this.mSecondaryProgress) {
                this.mSecondaryProgress = i;
                refreshProgress(16908303, this.mSecondaryProgress, false);
            }
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        if (getVisibility() != i) {
            super.setVisibility(i);
            if (this.mIndeterminate) {
                if (i == 8 || i == 4) {
                    stopAnimation();
                } else {
                    startAnimation();
                }
            }
        }
    }

    void startAnimation() {
        if (getVisibility() != 0) {
            return;
        }
        if (this.mIndeterminateDrawable instanceof Animatable) {
            this.mShouldStartAnimationDrawable = true;
            this.mAnimation = null;
        } else {
            if (this.mInterpolator == null) {
                this.mInterpolator = new LinearInterpolator();
            }
            this.mTransformation = new Transformation();
            this.mAnimation = new AlphaAnimation(0.0f, 1.0f);
            this.mAnimation.setRepeatMode(this.mBehavior);
            this.mAnimation.setRepeatCount(-1);
            this.mAnimation.setDuration(this.mDuration);
            this.mAnimation.setInterpolator(this.mInterpolator);
            this.mAnimation.setStartTime(-1L);
        }
        postInvalidate();
    }

    void stopAnimation() {
        this.mAnimation = null;
        this.mTransformation = null;
        Drawable drawable = this.mIndeterminateDrawable;
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
            this.mShouldStartAnimationDrawable = false;
        }
        postInvalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mProgressDrawable || drawable == this.mIndeterminateDrawable || super.verifyDrawable(drawable);
    }
}