package android.util;

import android.os.SystemClock;
import java.util.concurrent.TimeoutException;

/* loaded from: TimedRemoteCaller.class */
public abstract class TimedRemoteCaller<T> {
    public static final long DEFAULT_CALL_TIMEOUT_MILLIS = 5000;
    private static final int UNDEFINED_SEQUENCE = -1;
    private final long mCallTimeoutMillis;
    private int mSequenceCounter;
    private T mResult;
    private final Object mLock = new Object();
    private int mReceivedSequence = -1;
    private int mAwaitedSequence = -1;

    public TimedRemoteCaller(long callTimeoutMillis) {
        this.mCallTimeoutMillis = callTimeoutMillis;
    }

    public final int onBeforeRemoteCall() {
        int i;
        synchronized (this.mLock) {
            int i2 = this.mSequenceCounter;
            this.mSequenceCounter = i2 + 1;
            this.mAwaitedSequence = i2;
            i = this.mAwaitedSequence;
        }
        return i;
    }

    public final T getResultTimed(int sequence) throws TimeoutException {
        T result;
        synchronized (this.mLock) {
            boolean success = waitForResultTimedLocked(sequence);
            if (!success) {
                throw new TimeoutException("No reponse for sequence: " + sequence);
            }
            result = this.mResult;
            this.mResult = null;
        }
        return result;
    }

    public final void onRemoteMethodResult(T result, int sequence) {
        synchronized (this.mLock) {
            if (sequence == this.mAwaitedSequence) {
                this.mReceivedSequence = sequence;
                this.mResult = result;
                this.mLock.notifyAll();
            }
        }
    }

    private boolean waitForResultTimedLocked(int sequence) {
        long waitMillis;
        long startMillis = SystemClock.uptimeMillis();
        while (this.mReceivedSequence != sequence) {
            try {
                long elapsedMillis = SystemClock.uptimeMillis() - startMillis;
                waitMillis = this.mCallTimeoutMillis - elapsedMillis;
            } catch (InterruptedException e) {
            }
            if (waitMillis <= 0) {
                return false;
            }
            this.mLock.wait(waitMillis);
        }
        return true;
    }
}