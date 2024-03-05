package android.view;

import android.hardware.display.DisplayManagerGlobal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;

/* loaded from: Choreographer.class */
public final class Choreographer {
    private static final String TAG = "Choreographer";
    private static final boolean DEBUG = false;
    private static final long NANOS_PER_MS = 1000000;
    private static final int MSG_DO_FRAME = 0;
    private static final int MSG_DO_SCHEDULE_VSYNC = 1;
    private static final int MSG_DO_SCHEDULE_CALLBACK = 2;
    private final Object mLock;
    private final Looper mLooper;
    private final FrameHandler mHandler;
    private final FrameDisplayEventReceiver mDisplayEventReceiver;
    private CallbackRecord mCallbackPool;
    private final CallbackQueue[] mCallbackQueues;
    private boolean mFrameScheduled;
    private boolean mCallbacksRunning;
    private long mLastFrameTimeNanos;
    private long mFrameIntervalNanos;
    public static final int CALLBACK_INPUT = 0;
    public static final int CALLBACK_ANIMATION = 1;
    public static final int CALLBACK_TRAVERSAL = 2;
    private static final int CALLBACK_LAST = 2;
    private static final long DEFAULT_FRAME_DELAY = 10;
    private static volatile long sFrameDelay = DEFAULT_FRAME_DELAY;
    private static final ThreadLocal<Choreographer> sThreadInstance = new ThreadLocal<Choreographer>() { // from class: android.view.Choreographer.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Choreographer initialValue() {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                throw new IllegalStateException("The current thread must have a looper!");
            }
            return new Choreographer(looper);
        }
    };
    private static final boolean USE_VSYNC = SystemProperties.getBoolean("debug.choreographer.vsync", true);
    private static final boolean USE_FRAME_TIME = SystemProperties.getBoolean("debug.choreographer.frametime", true);
    private static final int SKIPPED_FRAME_WARNING_LIMIT = SystemProperties.getInt("debug.choreographer.skipwarning", 30);
    private static final Object FRAME_CALLBACK_TOKEN = new Object() { // from class: android.view.Choreographer.2
        public String toString() {
            return "FRAME_CALLBACK_TOKEN";
        }
    };

    /* loaded from: Choreographer$FrameCallback.class */
    public interface FrameCallback {
        void doFrame(long j);
    }

    private Choreographer(Looper looper) {
        this.mLock = new Object();
        this.mLooper = looper;
        this.mHandler = new FrameHandler(looper);
        this.mDisplayEventReceiver = USE_VSYNC ? new FrameDisplayEventReceiver(looper) : null;
        this.mLastFrameTimeNanos = Long.MIN_VALUE;
        this.mFrameIntervalNanos = 1.0E9f / getRefreshRate();
        this.mCallbackQueues = new CallbackQueue[3];
        for (int i = 0; i <= 2; i++) {
            this.mCallbackQueues[i] = new CallbackQueue();
        }
    }

    private static float getRefreshRate() {
        DisplayInfo di = DisplayManagerGlobal.getInstance().getDisplayInfo(0);
        return di.refreshRate;
    }

    public static Choreographer getInstance() {
        return sThreadInstance.get();
    }

    public static long getFrameDelay() {
        return sFrameDelay;
    }

    public static void setFrameDelay(long frameDelay) {
        sFrameDelay = frameDelay;
    }

    public static long subtractFrameDelay(long delayMillis) {
        long frameDelay = sFrameDelay;
        if (delayMillis <= frameDelay) {
            return 0L;
        }
        return delayMillis - frameDelay;
    }

    public void postCallback(int callbackType, Runnable action, Object token) {
        postCallbackDelayed(callbackType, action, token, 0L);
    }

    public void postCallbackDelayed(int callbackType, Runnable action, Object token, long delayMillis) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (callbackType < 0 || callbackType > 2) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        postCallbackDelayedInternal(callbackType, action, token, delayMillis);
    }

    private void postCallbackDelayedInternal(int callbackType, Object action, Object token, long delayMillis) {
        synchronized (this.mLock) {
            long now = SystemClock.uptimeMillis();
            long dueTime = now + delayMillis;
            this.mCallbackQueues[callbackType].addCallbackLocked(dueTime, action, token);
            if (dueTime <= now) {
                scheduleFrameLocked(now);
            } else {
                Message msg = this.mHandler.obtainMessage(2, action);
                msg.arg1 = callbackType;
                msg.setAsynchronous(true);
                this.mHandler.sendMessageAtTime(msg, dueTime);
            }
        }
    }

    public void removeCallbacks(int callbackType, Runnable action, Object token) {
        if (callbackType < 0 || callbackType > 2) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        removeCallbacksInternal(callbackType, action, token);
    }

    private void removeCallbacksInternal(int callbackType, Object action, Object token) {
        synchronized (this.mLock) {
            this.mCallbackQueues[callbackType].removeCallbacksLocked(action, token);
            if (action != null && token == null) {
                this.mHandler.removeMessages(2, action);
            }
        }
    }

    public void postFrameCallback(FrameCallback callback) {
        postFrameCallbackDelayed(callback, 0L);
    }

    public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        postCallbackDelayedInternal(1, callback, FRAME_CALLBACK_TOKEN, delayMillis);
    }

    public void removeFrameCallback(FrameCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        removeCallbacksInternal(1, callback, FRAME_CALLBACK_TOKEN);
    }

    public long getFrameTime() {
        return getFrameTimeNanos() / NANOS_PER_MS;
    }

    public long getFrameTimeNanos() {
        long nanoTime;
        synchronized (this.mLock) {
            if (!this.mCallbacksRunning) {
                throw new IllegalStateException("This method must only be called as part of a callback while a frame is in progress.");
            }
            nanoTime = USE_FRAME_TIME ? this.mLastFrameTimeNanos : System.nanoTime();
        }
        return nanoTime;
    }

    private void scheduleFrameLocked(long now) {
        if (!this.mFrameScheduled) {
            this.mFrameScheduled = true;
            if (USE_VSYNC) {
                if (isRunningOnLooperThreadLocked()) {
                    scheduleVsyncLocked();
                    return;
                }
                Message msg = this.mHandler.obtainMessage(1);
                msg.setAsynchronous(true);
                this.mHandler.sendMessageAtFrontOfQueue(msg);
                return;
            }
            long nextFrameTime = Math.max((this.mLastFrameTimeNanos / NANOS_PER_MS) + sFrameDelay, now);
            Message msg2 = this.mHandler.obtainMessage(0);
            msg2.setAsynchronous(true);
            this.mHandler.sendMessageAtTime(msg2, nextFrameTime);
        }
    }

    void doFrame(long frameTimeNanos, int frame) {
        synchronized (this.mLock) {
            if (this.mFrameScheduled) {
                long startNanos = System.nanoTime();
                long jitterNanos = startNanos - frameTimeNanos;
                if (jitterNanos >= this.mFrameIntervalNanos) {
                    long skippedFrames = jitterNanos / this.mFrameIntervalNanos;
                    if (skippedFrames >= SKIPPED_FRAME_WARNING_LIMIT) {
                        Log.i(TAG, "Skipped " + skippedFrames + " frames!  The application may be doing too much work on its main thread.");
                    }
                    long lastFrameOffset = jitterNanos % this.mFrameIntervalNanos;
                    frameTimeNanos = startNanos - lastFrameOffset;
                }
                if (frameTimeNanos < this.mLastFrameTimeNanos) {
                    scheduleVsyncLocked();
                    return;
                }
                this.mFrameScheduled = false;
                this.mLastFrameTimeNanos = frameTimeNanos;
                doCallbacks(0, frameTimeNanos);
                doCallbacks(1, frameTimeNanos);
                doCallbacks(2, frameTimeNanos);
            }
        }
    }

    void doCallbacks(int callbackType, long frameTimeNanos) {
        synchronized (this.mLock) {
            long now = SystemClock.uptimeMillis();
            CallbackRecord callbacks = this.mCallbackQueues[callbackType].extractDueCallbacksLocked(now);
            if (callbacks == null) {
                return;
            }
            this.mCallbacksRunning = true;
            for (CallbackRecord c = callbacks; c != null; c = c.next) {
                try {
                    c.run(frameTimeNanos);
                } catch (Throwable th) {
                    synchronized (this.mLock) {
                        this.mCallbacksRunning = false;
                        do {
                            CallbackRecord next = callbacks.next;
                            recycleCallbackLocked(callbacks);
                            callbacks = next;
                        } while (callbacks != null);
                        throw th;
                    }
                }
            }
            synchronized (this.mLock) {
                this.mCallbacksRunning = false;
                do {
                    CallbackRecord next2 = callbacks.next;
                    recycleCallbackLocked(callbacks);
                    callbacks = next2;
                } while (callbacks != null);
            }
        }
    }

    void doScheduleVsync() {
        synchronized (this.mLock) {
            if (this.mFrameScheduled) {
                scheduleVsyncLocked();
            }
        }
    }

    void doScheduleCallback(int callbackType) {
        synchronized (this.mLock) {
            if (!this.mFrameScheduled) {
                long now = SystemClock.uptimeMillis();
                if (this.mCallbackQueues[callbackType].hasDueCallbacksLocked(now)) {
                    scheduleFrameLocked(now);
                }
            }
        }
    }

    private void scheduleVsyncLocked() {
        this.mDisplayEventReceiver.scheduleVsync();
    }

    private boolean isRunningOnLooperThreadLocked() {
        return Looper.myLooper() == this.mLooper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CallbackRecord obtainCallbackLocked(long dueTime, Object action, Object token) {
        CallbackRecord callback = this.mCallbackPool;
        if (callback == null) {
            callback = new CallbackRecord();
        } else {
            this.mCallbackPool = callback.next;
            callback.next = null;
        }
        callback.dueTime = dueTime;
        callback.action = action;
        callback.token = token;
        return callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recycleCallbackLocked(CallbackRecord callback) {
        callback.action = null;
        callback.token = null;
        callback.next = this.mCallbackPool;
        this.mCallbackPool = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Choreographer$FrameHandler.class */
    public final class FrameHandler extends Handler {
        public FrameHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Choreographer.this.doFrame(System.nanoTime(), 0);
                    return;
                case 1:
                    Choreographer.this.doScheduleVsync();
                    return;
                case 2:
                    Choreographer.this.doScheduleCallback(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Choreographer$FrameDisplayEventReceiver.class */
    public final class FrameDisplayEventReceiver extends DisplayEventReceiver implements Runnable {
        private boolean mHavePendingVsync;
        private long mTimestampNanos;
        private int mFrame;

        public FrameDisplayEventReceiver(Looper looper) {
            super(looper);
        }

        @Override // android.view.DisplayEventReceiver
        public void onVsync(long timestampNanos, int builtInDisplayId, int frame) {
            if (builtInDisplayId != 0) {
                Log.d(Choreographer.TAG, "Received vsync from secondary display, but we don't support this case yet.  Choreographer needs a way to explicitly request vsync for a specific display to ensure it doesn't lose track of its scheduled vsync.");
                scheduleVsync();
                return;
            }
            long now = System.nanoTime();
            if (timestampNanos > now) {
                Log.w(Choreographer.TAG, "Frame time is " + (((float) (timestampNanos - now)) * 1.0E-6f) + " ms in the future!  Check that graphics HAL is generating vsync timestamps using the correct timebase.");
                timestampNanos = now;
            }
            if (this.mHavePendingVsync) {
                Log.w(Choreographer.TAG, "Already have a pending vsync event.  There should only be one at a time.");
            } else {
                this.mHavePendingVsync = true;
            }
            this.mTimestampNanos = timestampNanos;
            this.mFrame = frame;
            Message msg = Message.obtain(Choreographer.this.mHandler, this);
            msg.setAsynchronous(true);
            Choreographer.this.mHandler.sendMessageAtTime(msg, timestampNanos / Choreographer.NANOS_PER_MS);
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHavePendingVsync = false;
            Choreographer.this.doFrame(this.mTimestampNanos, this.mFrame);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Choreographer$CallbackRecord.class */
    public static final class CallbackRecord {
        public CallbackRecord next;
        public long dueTime;
        public Object action;
        public Object token;

        private CallbackRecord() {
        }

        public void run(long frameTimeNanos) {
            if (this.token == Choreographer.FRAME_CALLBACK_TOKEN) {
                ((FrameCallback) this.action).doFrame(frameTimeNanos);
            } else {
                ((Runnable) this.action).run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Choreographer$CallbackQueue.class */
    public final class CallbackQueue {
        private CallbackRecord mHead;

        private CallbackQueue() {
        }

        public boolean hasDueCallbacksLocked(long now) {
            return this.mHead != null && this.mHead.dueTime <= now;
        }

        public CallbackRecord extractDueCallbacksLocked(long now) {
            CallbackRecord next;
            CallbackRecord callbacks = this.mHead;
            if (callbacks == null || callbacks.dueTime > now) {
                return null;
            }
            CallbackRecord last = callbacks;
            CallbackRecord callbackRecord = last.next;
            while (true) {
                next = callbackRecord;
                if (next == null) {
                    break;
                } else if (next.dueTime > now) {
                    last.next = null;
                    break;
                } else {
                    last = next;
                    callbackRecord = next.next;
                }
            }
            this.mHead = next;
            return callbacks;
        }

        public void addCallbackLocked(long dueTime, Object action, Object token) {
            CallbackRecord callback = Choreographer.this.obtainCallbackLocked(dueTime, action, token);
            CallbackRecord entry = this.mHead;
            if (entry == null) {
                this.mHead = callback;
            } else if (dueTime < entry.dueTime) {
                callback.next = entry;
                this.mHead = callback;
            } else {
                while (true) {
                    if (entry.next == null) {
                        break;
                    } else if (dueTime < entry.next.dueTime) {
                        callback.next = entry.next;
                        break;
                    } else {
                        entry = entry.next;
                    }
                }
                entry.next = callback;
            }
        }

        public void removeCallbacksLocked(Object action, Object token) {
            CallbackRecord predecessor = null;
            CallbackRecord callbackRecord = this.mHead;
            while (true) {
                CallbackRecord callback = callbackRecord;
                if (callback != null) {
                    CallbackRecord next = callback.next;
                    if ((action == null || callback.action == action) && (token == null || callback.token == token)) {
                        if (predecessor != null) {
                            predecessor.next = next;
                        } else {
                            this.mHead = next;
                        }
                        Choreographer.this.recycleCallbackLocked(callback);
                    } else {
                        predecessor = callback;
                    }
                    callbackRecord = next;
                } else {
                    return;
                }
            }
        }
    }
}