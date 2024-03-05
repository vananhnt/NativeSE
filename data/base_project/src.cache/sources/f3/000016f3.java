package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* loaded from: AbsSeekBar.class */
public abstract class AbsSeekBar extends ProgressBar {
    private Drawable mThumb;
    private int mThumbOffset;
    float mTouchProgressOffset;
    boolean mIsUserSeekable;
    private int mKeyProgressIncrement;
    private static final int NO_ALPHA = 255;
    private float mDisabledAlpha;
    private int mScaledTouchSlop;
    private float mTouchDownX;
    private boolean mIsDragging;

    public AbsSeekBar(Context context) {
        super(context);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
    }

    public AbsSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsUserSeekable = true;
        this.mKeyProgressIncrement = 1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyle, 0);
        Drawable thumb = a.getDrawable(0);
        setThumb(thumb);
        int thumbOffset = a.getDimensionPixelOffset(1, getThumbOffset());
        setThumbOffset(thumbOffset);
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.Theme, 0, 0);
        this.mDisabledAlpha = a2.getFloat(3, 0.5f);
        a2.recycle();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setThumb(Drawable thumb) {
        boolean needUpdate;
        if (this.mThumb != null && thumb != this.mThumb) {
            this.mThumb.setCallback(null);
            needUpdate = true;
        } else {
            needUpdate = false;
        }
        if (thumb != null) {
            thumb.setCallback(this);
            if (canResolveLayoutDirection()) {
                thumb.setLayoutDirection(getLayoutDirection());
            }
            this.mThumbOffset = thumb.getIntrinsicWidth() / 2;
            if (needUpdate && (thumb.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth() || thumb.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight())) {
                requestLayout();
            }
        }
        this.mThumb = thumb;
        invalidate();
        if (needUpdate) {
            updateThumbPos(getWidth(), getHeight());
            if (thumb != null && thumb.isStateful()) {
                int[] state = getDrawableState();
                thumb.setState(state);
            }
        }
    }

    public Drawable getThumb() {
        return this.mThumb;
    }

    public int getThumbOffset() {
        return this.mThumbOffset;
    }

    public void setThumbOffset(int thumbOffset) {
        this.mThumbOffset = thumbOffset;
        invalidate();
    }

    public void setKeyProgressIncrement(int increment) {
        this.mKeyProgressIncrement = increment < 0 ? -increment : increment;
    }

    public int getKeyProgressIncrement() {
        return this.mKeyProgressIncrement;
    }

    @Override // android.widget.ProgressBar
    public synchronized void setMax(int max) {
        super.setMax(max);
        if (this.mKeyProgressIncrement == 0 || getMax() / this.mKeyProgressIncrement > 20) {
            setKeyProgressIncrement(Math.max(1, Math.round(getMax() / 20.0f)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return who == this.mThumb || super.verifyDrawable(who);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mThumb != null) {
            this.mThumb.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (255.0f * this.mDisabledAlpha));
        }
        if (this.mThumb != null && this.mThumb.isStateful()) {
            int[] state = getDrawableState();
            this.mThumb.setState(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.ProgressBar
    public void onProgressRefresh(float scale, boolean fromUser) {
        super.onProgressRefresh(scale, fromUser);
        Drawable thumb = this.mThumb;
        if (thumb != null) {
            setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateThumbPos(w, h);
    }

    private void updateThumbPos(int w, int h) {
        Drawable d = getCurrentDrawable();
        Drawable thumb = this.mThumb;
        int thumbHeight = thumb == null ? 0 : thumb.getIntrinsicHeight();
        int trackHeight = Math.min(this.mMaxHeight, (h - this.mPaddingTop) - this.mPaddingBottom);
        int max = getMax();
        float scale = max > 0 ? getProgress() / max : 0.0f;
        if (thumbHeight > trackHeight) {
            if (thumb != null) {
                setThumbPos(w, thumb, scale, 0);
            }
            int gapForCenteringTrack = (thumbHeight - trackHeight) / 2;
            if (d != null) {
                d.setBounds(0, gapForCenteringTrack, (w - this.mPaddingRight) - this.mPaddingLeft, ((h - this.mPaddingBottom) - gapForCenteringTrack) - this.mPaddingTop);
                return;
            }
            return;
        }
        if (d != null) {
            d.setBounds(0, 0, (w - this.mPaddingRight) - this.mPaddingLeft, (h - this.mPaddingBottom) - this.mPaddingTop);
        }
        int gap = (trackHeight - thumbHeight) / 2;
        if (thumb != null) {
            setThumbPos(w, thumb, scale, gap);
        }
    }

    private void setThumbPos(int w, Drawable thumb, float scale, int gap) {
        int topBound;
        int bottomBound;
        int available = (w - this.mPaddingLeft) - this.mPaddingRight;
        int thumbWidth = thumb.getIntrinsicWidth();
        int thumbHeight = thumb.getIntrinsicHeight();
        int available2 = (available - thumbWidth) + (this.mThumbOffset * 2);
        int thumbPos = (int) (scale * available2);
        if (gap == Integer.MIN_VALUE) {
            Rect oldBounds = thumb.getBounds();
            topBound = oldBounds.top;
            bottomBound = oldBounds.bottom;
        } else {
            topBound = gap;
            bottomBound = gap + thumbHeight;
        }
        int left = (isLayoutRtl() && this.mMirrorForRtl) ? available2 - thumbPos : thumbPos;
        thumb.setBounds(left, topBound, left + thumbWidth, bottomBound);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onResolveDrawables(int layoutDirection) {
        super.onResolveDrawables(layoutDirection);
        if (this.mThumb != null) {
            this.mThumb.setLayoutDirection(layoutDirection);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mThumb != null) {
            canvas.save();
            canvas.translate(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
            this.mThumb.draw(canvas);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ProgressBar, android.view.View
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getCurrentDrawable();
        int thumbHeight = this.mThumb == null ? 0 : this.mThumb.getIntrinsicHeight();
        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, d.getIntrinsicWidth()));
            int dh2 = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, d.getIntrinsicHeight()));
            dh = Math.max(thumbHeight, dh2);
        }
        setMeasuredDimension(resolveSizeAndState(dw + this.mPaddingLeft + this.mPaddingRight, widthMeasureSpec, 0), resolveSizeAndState(dh + this.mPaddingTop + this.mPaddingBottom, heightMeasureSpec, 0));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mIsUserSeekable || !isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                if (isInScrollingContainer()) {
                    this.mTouchDownX = event.getX();
                    return true;
                }
                setPressed(true);
                if (this.mThumb != null) {
                    invalidate(this.mThumb.getBounds());
                }
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();
                return true;
            case 1:
                if (this.mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                invalidate();
                return true;
            case 2:
                if (this.mIsDragging) {
                    trackTouchEvent(event);
                    return true;
                }
                float x = event.getX();
                if (Math.abs(x - this.mTouchDownX) > this.mScaledTouchSlop) {
                    setPressed(true);
                    if (this.mThumb != null) {
                        invalidate(this.mThumb.getBounds());
                    }
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    attemptClaimDrag();
                    return true;
                }
                return true;
            case 3:
                if (this.mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate();
                return true;
            default:
                return true;
        }
    }

    private void trackTouchEvent(MotionEvent event) {
        float scale;
        int width = getWidth();
        int available = (width - this.mPaddingLeft) - this.mPaddingRight;
        int x = (int) event.getX();
        float progress = 0.0f;
        if (isLayoutRtl() && this.mMirrorForRtl) {
            if (x > width - this.mPaddingRight) {
                scale = 0.0f;
            } else if (x < this.mPaddingLeft) {
                scale = 1.0f;
            } else {
                scale = ((available - x) + this.mPaddingLeft) / available;
                progress = this.mTouchProgressOffset;
            }
        } else if (x < this.mPaddingLeft) {
            scale = 0.0f;
        } else if (x > width - this.mPaddingRight) {
            scale = 1.0f;
        } else {
            scale = (x - this.mPaddingLeft) / available;
            progress = this.mTouchProgressOffset;
        }
        int max = getMax();
        setProgress((int) (progress + (scale * max)), true);
    }

    private void attemptClaimDrag() {
        if (this.mParent != null) {
            this.mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onKeyChange() {
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            int progress = getProgress();
            switch (keyCode) {
                case 21:
                    if (progress > 0) {
                        setProgress(progress - this.mKeyProgressIncrement, true);
                        onKeyChange();
                        return true;
                    }
                    break;
                case 22:
                    if (progress < getMax()) {
                        setProgress(progress + this.mKeyProgressIncrement, true);
                        onKeyChange();
                        return true;
                    }
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AbsSeekBar.class.getName());
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AbsSeekBar.class.getName());
        if (isEnabled()) {
            int progress = getProgress();
            if (progress > 0) {
                info.addAction(8192);
            }
            if (progress < getMax()) {
                info.addAction(4096);
            }
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        if (!isEnabled()) {
            return false;
        }
        int progress = getProgress();
        int increment = Math.max(1, Math.round(getMax() / 5.0f));
        switch (action) {
            case 4096:
                if (progress >= getMax()) {
                    return false;
                }
                setProgress(progress + increment, true);
                onKeyChange();
                return true;
            case 8192:
                if (progress <= 0) {
                    return false;
                }
                setProgress(progress - increment, true);
                onKeyChange();
                return true;
            default:
                return false;
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        int max = getMax();
        float scale = max > 0 ? getProgress() / max : 0.0f;
        Drawable thumb = this.mThumb;
        if (thumb != null) {
            setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);
            invalidate();
        }
    }
}