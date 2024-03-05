package android.widget;

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
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pools;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.ArrayList;

@RemoteViews.RemoteView
/* loaded from: ProgressBar.class */
public class ProgressBar extends View {
    private static final int MAX_LEVEL = 10000;
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
    int mMinWidth;
    int mMaxWidth;
    int mMinHeight;
    int mMaxHeight;
    private int mProgress;
    private int mSecondaryProgress;
    private int mMax;
    private int mBehavior;
    private int mDuration;
    private boolean mIndeterminate;
    private boolean mOnlyIndeterminate;
    private Transformation mTransformation;
    private AlphaAnimation mAnimation;
    private boolean mHasAnimation;
    private Drawable mIndeterminateDrawable;
    private Drawable mProgressDrawable;
    private Drawable mCurrentDrawable;
    Bitmap mSampleTile;
    private boolean mNoInvalidate;
    private Interpolator mInterpolator;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private long mUiThreadId;
    private boolean mShouldStartAnimationDrawable;
    private boolean mInDrawing;
    private boolean mAttached;
    private boolean mRefreshIsPosted;
    boolean mMirrorForRtl;
    private final ArrayList<RefreshData> mRefreshData;
    private AccessibilityEventSender mAccessibilityEventSender;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 16842871);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle, int styleRes) {
        super(context, attrs, defStyle);
        this.mMirrorForRtl = false;
        this.mRefreshData = new ArrayList<>();
        this.mUiThreadId = Thread.currentThread().getId();
        initProgressBar();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyle, styleRes);
        this.mNoInvalidate = true;
        Drawable drawable = a.getDrawable(8);
        if (drawable != null) {
            setProgressDrawable(tileify(drawable, false));
        }
        this.mDuration = a.getInt(9, this.mDuration);
        this.mMinWidth = a.getDimensionPixelSize(11, this.mMinWidth);
        this.mMaxWidth = a.getDimensionPixelSize(0, this.mMaxWidth);
        this.mMinHeight = a.getDimensionPixelSize(12, this.mMinHeight);
        this.mMaxHeight = a.getDimensionPixelSize(1, this.mMaxHeight);
        this.mBehavior = a.getInt(10, this.mBehavior);
        int resID = a.getResourceId(13, 17432587);
        if (resID > 0) {
            setInterpolator(context, resID);
        }
        setMax(a.getInt(2, this.mMax));
        setProgress(a.getInt(3, this.mProgress));
        setSecondaryProgress(a.getInt(4, this.mSecondaryProgress));
        Drawable drawable2 = a.getDrawable(7);
        if (drawable2 != null) {
            setIndeterminateDrawable(tileifyIndeterminate(drawable2));
        }
        this.mOnlyIndeterminate = a.getBoolean(6, this.mOnlyIndeterminate);
        this.mNoInvalidate = false;
        setIndeterminate(this.mOnlyIndeterminate || a.getBoolean(5, this.mIndeterminate));
        this.mMirrorForRtl = a.getBoolean(15, this.mMirrorForRtl);
        a.recycle();
    }

    private Drawable tileify(Drawable drawable, boolean clip) {
        if (drawable instanceof LayerDrawable) {
            LayerDrawable background = (LayerDrawable) drawable;
            int N = background.getNumberOfLayers();
            Drawable[] outDrawables = new Drawable[N];
            for (int i = 0; i < N; i++) {
                int id = background.getId(i);
                outDrawables[i] = tileify(background.getDrawable(i), id == 16908301 || id == 16908303);
            }
            LayerDrawable newBg = new LayerDrawable(outDrawables);
            for (int i2 = 0; i2 < N; i2++) {
                newBg.setId(i2, background.getId(i2));
            }
            return newBg;
        } else if (drawable instanceof StateListDrawable) {
            StateListDrawable in = (StateListDrawable) drawable;
            StateListDrawable out = new StateListDrawable();
            int numStates = in.getStateCount();
            for (int i3 = 0; i3 < numStates; i3++) {
                out.addState(in.getStateSet(i3), tileify(in.getStateDrawable(i3), clip));
            }
            return out;
        } else if (drawable instanceof BitmapDrawable) {
            Bitmap tileBitmap = ((BitmapDrawable) drawable).getBitmap();
            if (this.mSampleTile == null) {
                this.mSampleTile = tileBitmap;
            }
            ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
            BitmapShader bitmapShader = new BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
            shapeDrawable.getPaint().setShader(bitmapShader);
            return clip ? new ClipDrawable(shapeDrawable, 3, 1) : shapeDrawable;
        } else {
            return drawable;
        }
    }

    Shape getDrawableShape() {
        float[] roundedCorners = {5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f};
        return new RoundRectShape(roundedCorners, null, null);
    }

    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable background = (AnimationDrawable) drawable;
            int N = background.getNumberOfFrames();
            AnimationDrawable newBg = new AnimationDrawable();
            newBg.setOneShot(background.isOneShot());
            for (int i = 0; i < N; i++) {
                Drawable frame = tileify(background.getFrame(i), true);
                frame.setLevel(10000);
                newBg.addFrame(frame, background.getDuration(i));
            }
            newBg.setLevel(10000);
            drawable = newBg;
        }
        return drawable;
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

    @ViewDebug.ExportedProperty(category = "progress")
    public synchronized boolean isIndeterminate() {
        return this.mIndeterminate;
    }

    @RemotableViewMethod
    public synchronized void setIndeterminate(boolean indeterminate) {
        if ((!this.mOnlyIndeterminate || !this.mIndeterminate) && indeterminate != this.mIndeterminate) {
            this.mIndeterminate = indeterminate;
            if (indeterminate) {
                this.mCurrentDrawable = this.mIndeterminateDrawable;
                startAnimation();
                return;
            }
            this.mCurrentDrawable = this.mProgressDrawable;
            stopAnimation();
        }
    }

    public Drawable getIndeterminateDrawable() {
        return this.mIndeterminateDrawable;
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (d != null) {
            d.setCallback(this);
        }
        this.mIndeterminateDrawable = d;
        if (this.mIndeterminateDrawable != null && canResolveLayoutDirection()) {
            this.mIndeterminateDrawable.setLayoutDirection(getLayoutDirection());
        }
        if (this.mIndeterminate) {
            this.mCurrentDrawable = d;
            postInvalidate();
        }
    }

    public Drawable getProgressDrawable() {
        return this.mProgressDrawable;
    }

    public void setProgressDrawable(Drawable d) {
        boolean needUpdate;
        if (this.mProgressDrawable != null && d != this.mProgressDrawable) {
            this.mProgressDrawable.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }
        if (d != null) {
            d.setCallback(this);
            if (canResolveLayoutDirection()) {
                d.setLayoutDirection(getLayoutDirection());
            }
            int drawableHeight = d.getMinimumHeight();
            if (this.mMaxHeight < drawableHeight) {
                this.mMaxHeight = drawableHeight;
                requestLayout();
            }
        }
        this.mProgressDrawable = d;
        if (!this.mIndeterminate) {
            this.mCurrentDrawable = d;
            postInvalidate();
        }
        if (needUpdate) {
            updateDrawableBounds(getWidth(), getHeight());
            updateDrawableState();
            doRefreshProgress(16908301, this.mProgress, false, false);
            doRefreshProgress(16908303, this.mSecondaryProgress, false, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Drawable getCurrentDrawable() {
        return this.mCurrentDrawable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean verifyDrawable(Drawable who) {
        return who == this.mProgressDrawable || who == this.mIndeterminateDrawable || super.verifyDrawable(who);
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mProgressDrawable != null) {
            this.mProgressDrawable.jumpToCurrentState();
        }
        if (this.mIndeterminateDrawable != null) {
            this.mIndeterminateDrawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public void onResolveDrawables(int layoutDirection) {
        Drawable d = this.mCurrentDrawable;
        if (d != null) {
            d.setLayoutDirection(layoutDirection);
        }
        if (this.mIndeterminateDrawable != null) {
            this.mIndeterminateDrawable.setLayoutDirection(layoutDirection);
        }
        if (this.mProgressDrawable != null) {
            this.mProgressDrawable.setLayoutDirection(layoutDirection);
        }
    }

    @Override // android.view.View
    public void postInvalidate() {
        if (!this.mNoInvalidate) {
            super.postInvalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ProgressBar$RefreshProgressRunnable.class */
    public class RefreshProgressRunnable implements Runnable {
        private RefreshProgressRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ProgressBar.this) {
                int count = ProgressBar.this.mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    RefreshData rd = (RefreshData) ProgressBar.this.mRefreshData.get(i);
                    ProgressBar.this.doRefreshProgress(rd.id, rd.progress, rd.fromUser, true);
                    rd.recycle();
                }
                ProgressBar.this.mRefreshData.clear();
                ProgressBar.this.mRefreshIsPosted = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ProgressBar$RefreshData.class */
    public static class RefreshData {
        private static final int POOL_MAX = 24;
        private static final Pools.SynchronizedPool<RefreshData> sPool = new Pools.SynchronizedPool<>(24);
        public int id;
        public int progress;
        public boolean fromUser;

        private RefreshData() {
        }

        public static RefreshData obtain(int id, int progress, boolean fromUser) {
            RefreshData rd = sPool.acquire();
            if (rd == null) {
                rd = new RefreshData();
            }
            rd.id = id;
            rd.progress = progress;
            rd.fromUser = fromUser;
            return rd;
        }

        public void recycle() {
            sPool.release(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void doRefreshProgress(int id, int progress, boolean fromUser, boolean callBackToApp) {
        float scale = this.mMax > 0 ? progress / this.mMax : 0.0f;
        Drawable d = this.mCurrentDrawable;
        if (d != null) {
            Drawable progressDrawable = null;
            if (d instanceof LayerDrawable) {
                progressDrawable = ((LayerDrawable) d).findDrawableByLayerId(id);
                if (progressDrawable != null && canResolveLayoutDirection()) {
                    progressDrawable.setLayoutDirection(getLayoutDirection());
                }
            }
            int level = (int) (scale * 10000.0f);
            (progressDrawable != null ? progressDrawable : d).setLevel(level);
        } else {
            invalidate();
        }
        if (callBackToApp && id == 16908301) {
            onProgressRefresh(scale, fromUser);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onProgressRefresh(float scale, boolean fromUser) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            scheduleAccessibilityEventSender();
        }
    }

    private synchronized void refreshProgress(int id, int progress, boolean fromUser) {
        if (this.mUiThreadId == Thread.currentThread().getId()) {
            doRefreshProgress(id, progress, fromUser, true);
            return;
        }
        if (this.mRefreshProgressRunnable == null) {
            this.mRefreshProgressRunnable = new RefreshProgressRunnable();
        }
        RefreshData rd = RefreshData.obtain(id, progress, fromUser);
        this.mRefreshData.add(rd);
        if (this.mAttached && !this.mRefreshIsPosted) {
            post(this.mRefreshProgressRunnable);
            this.mRefreshIsPosted = true;
        }
    }

    @RemotableViewMethod
    public synchronized void setProgress(int progress) {
        setProgress(progress, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @RemotableViewMethod
    public synchronized void setProgress(int progress, boolean fromUser) {
        if (this.mIndeterminate) {
            return;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress > this.mMax) {
            progress = this.mMax;
        }
        if (progress != this.mProgress) {
            this.mProgress = progress;
            refreshProgress(16908301, this.mProgress, fromUser);
        }
    }

    @RemotableViewMethod
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        if (this.mIndeterminate) {
            return;
        }
        if (secondaryProgress < 0) {
            secondaryProgress = 0;
        }
        if (secondaryProgress > this.mMax) {
            secondaryProgress = this.mMax;
        }
        if (secondaryProgress != this.mSecondaryProgress) {
            this.mSecondaryProgress = secondaryProgress;
            refreshProgress(16908303, this.mSecondaryProgress, false);
        }
    }

    @ViewDebug.ExportedProperty(category = "progress")
    public synchronized int getProgress() {
        if (this.mIndeterminate) {
            return 0;
        }
        return this.mProgress;
    }

    @ViewDebug.ExportedProperty(category = "progress")
    public synchronized int getSecondaryProgress() {
        if (this.mIndeterminate) {
            return 0;
        }
        return this.mSecondaryProgress;
    }

    @ViewDebug.ExportedProperty(category = "progress")
    public synchronized int getMax() {
        return this.mMax;
    }

    @RemotableViewMethod
    public synchronized void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        if (max != this.mMax) {
            this.mMax = max;
            postInvalidate();
            if (this.mProgress > max) {
                this.mProgress = max;
            }
            refreshProgress(16908301, this.mProgress, false);
        }
    }

    public final synchronized void incrementProgressBy(int diff) {
        setProgress(this.mProgress + diff);
    }

    public final synchronized void incrementSecondaryProgressBy(int diff) {
        setSecondaryProgress(this.mSecondaryProgress + diff);
    }

    void startAnimation() {
        if (getVisibility() != 0) {
            return;
        }
        if (this.mIndeterminateDrawable instanceof Animatable) {
            this.mShouldStartAnimationDrawable = true;
            this.mHasAnimation = false;
        } else {
            this.mHasAnimation = true;
            if (this.mInterpolator == null) {
                this.mInterpolator = new LinearInterpolator();
            }
            if (this.mTransformation == null) {
                this.mTransformation = new Transformation();
            } else {
                this.mTransformation.clear();
            }
            if (this.mAnimation == null) {
                this.mAnimation = new AlphaAnimation(0.0f, 1.0f);
            } else {
                this.mAnimation.reset();
            }
            this.mAnimation.setRepeatMode(this.mBehavior);
            this.mAnimation.setRepeatCount(-1);
            this.mAnimation.setDuration(this.mDuration);
            this.mAnimation.setInterpolator(this.mInterpolator);
            this.mAnimation.setStartTime(-1L);
        }
        postInvalidate();
    }

    void stopAnimation() {
        this.mHasAnimation = false;
        if (this.mIndeterminateDrawable instanceof Animatable) {
            ((Animatable) this.mIndeterminateDrawable).stop();
            this.mShouldStartAnimationDrawable = false;
        }
        postInvalidate();
    }

    public void setInterpolator(Context context, int resID) {
        setInterpolator(AnimationUtils.loadInterpolator(context, resID));
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (this.mIndeterminate) {
                if (v == 8 || v == 4) {
                    stopAnimation();
                } else {
                    startAnimation();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.mIndeterminate) {
            if (visibility == 8 || visibility == 4) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable dr) {
        if (!this.mInDrawing) {
            if (verifyDrawable(dr)) {
                Rect dirty = dr.getBounds();
                invalidate(dirty.left + this.mScrollX, dirty.top + this.mScrollY, dirty.right + this.mScrollX, dirty.bottom + this.mScrollY);
                return;
            }
            super.invalidateDrawable(dr);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateDrawableBounds(w, h);
    }

    private void updateDrawableBounds(int w, int h) {
        int w2 = w - (this.mPaddingRight + this.mPaddingLeft);
        int h2 = h - (this.mPaddingTop + this.mPaddingBottom);
        int right = w2;
        int bottom = h2;
        int top = 0;
        int left = 0;
        if (this.mIndeterminateDrawable != null) {
            if (this.mOnlyIndeterminate && !(this.mIndeterminateDrawable instanceof AnimationDrawable)) {
                int intrinsicWidth = this.mIndeterminateDrawable.getIntrinsicWidth();
                int intrinsicHeight = this.mIndeterminateDrawable.getIntrinsicHeight();
                float intrinsicAspect = intrinsicWidth / intrinsicHeight;
                float boundAspect = w2 / h2;
                if (intrinsicAspect != boundAspect) {
                    if (boundAspect > intrinsicAspect) {
                        int width = (int) (h2 * intrinsicAspect);
                        left = (w2 - width) / 2;
                        right = left + width;
                    } else {
                        int height = (int) (w2 * (1.0f / intrinsicAspect));
                        top = (h2 - height) / 2;
                        bottom = top + height;
                    }
                }
            }
            if (isLayoutRtl() && this.mMirrorForRtl) {
                int tempLeft = left;
                left = w2 - right;
                right = w2 - tempLeft;
            }
            this.mIndeterminateDrawable.setBounds(left, top, right, bottom);
        }
        if (this.mProgressDrawable != null) {
            this.mProgressDrawable.setBounds(0, 0, right, bottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable d = this.mCurrentDrawable;
        if (d != null) {
            canvas.save();
            if (isLayoutRtl() && this.mMirrorForRtl) {
                canvas.translate(getWidth() - this.mPaddingRight, this.mPaddingTop);
                canvas.scale(-1.0f, 1.0f);
            } else {
                canvas.translate(this.mPaddingLeft, this.mPaddingTop);
            }
            long time = getDrawingTime();
            if (this.mHasAnimation) {
                this.mAnimation.getTransformation(time, this.mTransformation);
                float scale = this.mTransformation.getAlpha();
                try {
                    this.mInDrawing = true;
                    d.setLevel((int) (scale * 10000.0f));
                    this.mInDrawing = false;
                    postInvalidateOnAnimation();
                } catch (Throwable th) {
                    this.mInDrawing = false;
                    throw th;
                }
            }
            d.draw(canvas);
            canvas.restore();
            if (this.mShouldStartAnimationDrawable && (d instanceof Animatable)) {
                ((Animatable) d).start();
                this.mShouldStartAnimationDrawable = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = this.mCurrentDrawable;
        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, d.getIntrinsicWidth()));
            dh = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, d.getIntrinsicHeight()));
        }
        updateDrawableState();
        setMeasuredDimension(resolveSizeAndState(dw + this.mPaddingLeft + this.mPaddingRight, widthMeasureSpec, 0), resolveSizeAndState(dh + this.mPaddingTop + this.mPaddingBottom, heightMeasureSpec, 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    private void updateDrawableState() {
        int[] state = getDrawableState();
        if (this.mProgressDrawable != null && this.mProgressDrawable.isStateful()) {
            this.mProgressDrawable.setState(state);
        }
        if (this.mIndeterminateDrawable != null && this.mIndeterminateDrawable.isStateful()) {
            this.mIndeterminateDrawable.setState(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ProgressBar$SavedState.class */
    public static class SavedState extends View.BaseSavedState {
        int progress;
        int secondaryProgress;
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.ProgressBar.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.progress = in.readInt();
            this.secondaryProgress = in.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.progress);
            out.writeInt(this.secondaryProgress);
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = this.mProgress;
        ss.secondaryProgress = this.mSecondaryProgress;
        return ss;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(ss.progress);
        setSecondaryProgress(ss.secondaryProgress);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIndeterminate) {
            startAnimation();
        }
        if (this.mRefreshData != null) {
            synchronized (this) {
                int count = this.mRefreshData.size();
                for (int i = 0; i < count; i++) {
                    RefreshData rd = this.mRefreshData.get(i);
                    doRefreshProgress(rd.id, rd.progress, rd.fromUser, true);
                    rd.recycle();
                }
                this.mRefreshData.clear();
            }
        }
        this.mAttached = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        if (this.mIndeterminate) {
            stopAnimation();
        }
        if (this.mRefreshProgressRunnable != null) {
            removeCallbacks(this.mRefreshProgressRunnable);
        }
        if (this.mRefreshProgressRunnable != null && this.mRefreshIsPosted) {
            removeCallbacks(this.mRefreshProgressRunnable);
        }
        if (this.mAccessibilityEventSender != null) {
            removeCallbacks(this.mAccessibilityEventSender);
        }
        super.onDetachedFromWindow();
        this.mAttached = false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ProgressBar.class.getName());
        event.setItemCount(this.mMax);
        event.setCurrentItemIndex(this.mProgress);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ProgressBar.class.getName());
    }

    private void scheduleAccessibilityEventSender() {
        if (this.mAccessibilityEventSender == null) {
            this.mAccessibilityEventSender = new AccessibilityEventSender();
        } else {
            removeCallbacks(this.mAccessibilityEventSender);
        }
        postDelayed(this.mAccessibilityEventSender, 200L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ProgressBar$AccessibilityEventSender.class */
    public class AccessibilityEventSender implements Runnable {
        private AccessibilityEventSender() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ProgressBar.this.sendAccessibilityEvent(4);
        }
    }
}