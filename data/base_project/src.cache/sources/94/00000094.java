package android.animation;

/* loaded from: TimeAnimator.class */
public class TimeAnimator extends ValueAnimator {
    private TimeListener mListener;
    private long mPreviousTime = -1;

    /* loaded from: TimeAnimator$TimeListener.class */
    public interface TimeListener {
        void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2);
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        this.mPreviousTime = -1L;
        super.start();
    }

    @Override // android.animation.ValueAnimator
    boolean animationFrame(long currentTime) {
        if (this.mListener != null) {
            long totalTime = currentTime - this.mStartTime;
            long deltaTime = this.mPreviousTime < 0 ? 0L : currentTime - this.mPreviousTime;
            this.mPreviousTime = currentTime;
            this.mListener.onTimeUpdate(this, totalTime, deltaTime);
            return false;
        }
        return false;
    }

    public void setTimeListener(TimeListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void animateValue(float fraction) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.animation.ValueAnimator
    public void initAnimation() {
    }
}