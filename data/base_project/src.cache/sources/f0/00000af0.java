package android.os;

/* loaded from: CountDownTimer.class */
public abstract class CountDownTimer {
    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mStopTimeInFuture;
    private static final int MSG = 1;
    private Handler mHandler = new Handler() { // from class: android.os.CountDownTimer.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            synchronized (CountDownTimer.this) {
                long millisLeft = CountDownTimer.this.mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0) {
                    CountDownTimer.this.onFinish();
                } else if (millisLeft < CountDownTimer.this.mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(1), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    CountDownTimer.this.onTick(millisLeft);
                    long delay = (lastTickStart + CountDownTimer.this.mCountdownInterval) - SystemClock.elapsedRealtime();
                    while (delay < 0) {
                        delay += CountDownTimer.this.mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(1), delay);
                }
            }
        }
    };

    public abstract void onTick(long j);

    public abstract void onFinish();

    public CountDownTimer(long millisInFuture, long countDownInterval) {
        this.mMillisInFuture = millisInFuture;
        this.mCountdownInterval = countDownInterval;
    }

    public final void cancel() {
        this.mHandler.removeMessages(1);
    }

    public final synchronized CountDownTimer start() {
        if (this.mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        this.mStopTimeInFuture = SystemClock.elapsedRealtime() + this.mMillisInFuture;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
        return this;
    }
}