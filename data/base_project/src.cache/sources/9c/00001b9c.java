package com.android.internal.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

@RemoteViews.RemoteView
/* loaded from: TextProgressBar.class */
public class TextProgressBar extends RelativeLayout implements Chronometer.OnChronometerTickListener {
    public static final String TAG = "TextProgressBar";
    static final int CHRONOMETER_ID = 16908308;
    static final int PROGRESSBAR_ID = 16908301;
    Chronometer mChronometer;
    ProgressBar mProgressBar;
    long mDurationBase;
    int mDuration;
    boolean mChronometerFollow;
    int mChronometerGravity;

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChronometer = null;
        this.mProgressBar = null;
        this.mDurationBase = -1L;
        this.mDuration = -1;
        this.mChronometerFollow = false;
        this.mChronometerGravity = 0;
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChronometer = null;
        this.mProgressBar = null;
        this.mDurationBase = -1L;
        this.mDuration = -1;
        this.mChronometerFollow = false;
        this.mChronometerGravity = 0;
    }

    public TextProgressBar(Context context) {
        super(context);
        this.mChronometer = null;
        this.mProgressBar = null;
        this.mDurationBase = -1L;
        this.mDuration = -1;
        this.mChronometerFollow = false;
        this.mChronometerGravity = 0;
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        int childId = child.getId();
        if (childId == 16908308 && (child instanceof Chronometer)) {
            this.mChronometer = (Chronometer) child;
            this.mChronometer.setOnChronometerTickListener(this);
            this.mChronometerFollow = params.width == -2;
            this.mChronometerGravity = this.mChronometer.getGravity() & 8388615;
        } else if (childId == 16908301 && (child instanceof ProgressBar)) {
            this.mProgressBar = (ProgressBar) child;
        }
    }

    @RemotableViewMethod
    public void setDurationBase(long durationBase) {
        this.mDurationBase = durationBase;
        if (this.mProgressBar == null || this.mChronometer == null) {
            throw new RuntimeException("Expecting child ProgressBar with id 'android.R.id.progress' and Chronometer id 'android.R.id.text1'");
        }
        this.mDuration = (int) (durationBase - this.mChronometer.getBase());
        if (this.mDuration <= 0) {
            this.mDuration = 1;
        }
        this.mProgressBar.setMax(this.mDuration);
    }

    @Override // android.widget.Chronometer.OnChronometerTickListener
    public void onChronometerTick(Chronometer chronometer) {
        if (this.mProgressBar == null) {
            throw new RuntimeException("Expecting child ProgressBar with id 'android.R.id.progress'");
        }
        long now = SystemClock.elapsedRealtime();
        if (now >= this.mDurationBase) {
            this.mChronometer.stop();
        }
        int remaining = (int) (this.mDurationBase - now);
        this.mProgressBar.setProgress(this.mDuration - remaining);
        if (this.mChronometerFollow) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mProgressBar.getLayoutParams();
            int contentWidth = this.mProgressBar.getWidth() - (params.leftMargin + params.rightMargin);
            int leadingEdge = ((contentWidth * this.mProgressBar.getProgress()) / this.mProgressBar.getMax()) + params.leftMargin;
            int adjustLeft = 0;
            int textWidth = this.mChronometer.getWidth();
            if (this.mChronometerGravity == 8388613) {
                adjustLeft = -textWidth;
            } else if (this.mChronometerGravity == 1) {
                adjustLeft = -(textWidth / 2);
            }
            int leadingEdge2 = leadingEdge + adjustLeft;
            int rightLimit = (contentWidth - params.rightMargin) - textWidth;
            if (leadingEdge2 < params.leftMargin) {
                leadingEdge2 = params.leftMargin;
            } else if (leadingEdge2 > rightLimit) {
                leadingEdge2 = rightLimit;
            }
            ((RelativeLayout.LayoutParams) this.mChronometer.getLayoutParams()).leftMargin = leadingEdge2;
            this.mChronometer.requestLayout();
        }
    }
}