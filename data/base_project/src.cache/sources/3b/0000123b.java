package android.support.v7.internal.view;

import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: ViewPropertyAnimatorCompatSet.class */
public class ViewPropertyAnimatorCompatSet {
    private Interpolator mInterpolator;
    private boolean mIsStarted;
    private ViewPropertyAnimatorListener mListener;
    private long mDuration = -1;
    private final ViewPropertyAnimatorListenerAdapter mProxyListener = new ViewPropertyAnimatorListenerAdapter(this) { // from class: android.support.v7.internal.view.ViewPropertyAnimatorCompatSet.1
        final ViewPropertyAnimatorCompatSet this$0;
        private boolean mProxyStarted = false;
        private int mProxyEndCount = 0;

        {
            this.this$0 = this;
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationEnd(View view) {
            int i = this.mProxyEndCount + 1;
            this.mProxyEndCount = i;
            if (i == this.this$0.mAnimators.size()) {
                if (this.this$0.mListener != null) {
                    this.this$0.mListener.onAnimationEnd(null);
                }
                onEnd();
            }
        }

        @Override // android.support.v4.view.ViewPropertyAnimatorListenerAdapter, android.support.v4.view.ViewPropertyAnimatorListener
        public void onAnimationStart(View view) {
            if (this.mProxyStarted) {
                return;
            }
            this.mProxyStarted = true;
            if (this.this$0.mListener != null) {
                this.this$0.mListener.onAnimationStart(null);
            }
        }

        void onEnd() {
            this.mProxyEndCount = 0;
            this.mProxyStarted = false;
            this.this$0.onAnimationsEnded();
        }
    };
    private final ArrayList<ViewPropertyAnimatorCompat> mAnimators = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationsEnded() {
        this.mIsStarted = false;
    }

    public void cancel() {
        if (this.mIsStarted) {
            Iterator<ViewPropertyAnimatorCompat> it = this.mAnimators.iterator();
            while (it.hasNext()) {
                it.next().cancel();
            }
            this.mIsStarted = false;
        }
    }

    public ViewPropertyAnimatorCompatSet play(ViewPropertyAnimatorCompat viewPropertyAnimatorCompat) {
        if (!this.mIsStarted) {
            this.mAnimators.add(viewPropertyAnimatorCompat);
        }
        return this;
    }

    public ViewPropertyAnimatorCompatSet setDuration(long j) {
        if (!this.mIsStarted) {
            this.mDuration = j;
        }
        return this;
    }

    public ViewPropertyAnimatorCompatSet setInterpolator(Interpolator interpolator) {
        if (!this.mIsStarted) {
            this.mInterpolator = interpolator;
        }
        return this;
    }

    public ViewPropertyAnimatorCompatSet setListener(ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        if (!this.mIsStarted) {
            this.mListener = viewPropertyAnimatorListener;
        }
        return this;
    }

    public void start() {
        if (this.mIsStarted) {
            return;
        }
        Iterator<ViewPropertyAnimatorCompat> it = this.mAnimators.iterator();
        while (it.hasNext()) {
            ViewPropertyAnimatorCompat next = it.next();
            long j = this.mDuration;
            if (j >= 0) {
                next.setDuration(j);
            }
            Interpolator interpolator = this.mInterpolator;
            if (interpolator != null) {
                next.setInterpolator(interpolator);
            }
            if (this.mListener != null) {
                next.setListener(this.mProxyListener);
            }
            next.start();
        }
        this.mIsStarted = true;
    }
}