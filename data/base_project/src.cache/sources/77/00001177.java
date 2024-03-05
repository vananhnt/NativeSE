package android.support.v4.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/* loaded from: ContentLoadingProgressBar.class */
public class ContentLoadingProgressBar extends ProgressBar {
    private static final int MIN_DELAY = 500;
    private static final int MIN_SHOW_TIME = 500;
    private final Runnable mDelayedHide;
    private final Runnable mDelayedShow;
    private boolean mDismissed;
    private boolean mPostedHide;
    private boolean mPostedShow;
    private long mStartTime;

    public ContentLoadingProgressBar(Context context) {
        this(context, null);
    }

    public ContentLoadingProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.mStartTime = -1L;
        this.mPostedHide = false;
        this.mPostedShow = false;
        this.mDismissed = false;
        this.mDelayedHide = new Runnable(this) { // from class: android.support.v4.widget.ContentLoadingProgressBar.1
            final ContentLoadingProgressBar this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mPostedHide = false;
                this.this$0.mStartTime = -1L;
                this.this$0.setVisibility(8);
            }
        };
        this.mDelayedShow = new Runnable(this) { // from class: android.support.v4.widget.ContentLoadingProgressBar.2
            final ContentLoadingProgressBar this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mPostedShow = false;
                if (this.this$0.mDismissed) {
                    return;
                }
                this.this$0.mStartTime = System.currentTimeMillis();
                this.this$0.setVisibility(0);
            }
        };
    }

    private void removeCallbacks() {
        removeCallbacks(this.mDelayedHide);
        removeCallbacks(this.mDelayedShow);
    }

    public void hide() {
        this.mDismissed = true;
        removeCallbacks(this.mDelayedShow);
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mStartTime;
        long j2 = currentTimeMillis - j;
        if (j2 >= 500 || j == -1) {
            setVisibility(8);
        } else if (this.mPostedHide) {
        } else {
            postDelayed(this.mDelayedHide, 500 - j2);
            this.mPostedHide = true;
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
    }

    @Override // android.widget.ProgressBar, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }

    public void show() {
        this.mStartTime = -1L;
        this.mDismissed = false;
        removeCallbacks(this.mDelayedHide);
        if (this.mPostedShow) {
            return;
        }
        postDelayed(this.mDelayedShow, 500L);
        this.mPostedShow = true;
    }
}