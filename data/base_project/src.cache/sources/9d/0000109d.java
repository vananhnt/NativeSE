package android.support.v4.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/* loaded from: GestureDetectorCompat.class */
public class GestureDetectorCompat {
    private final GestureDetectorCompatImpl mImpl;

    /* loaded from: GestureDetectorCompat$GestureDetectorCompatImpl.class */
    interface GestureDetectorCompatImpl {
        boolean isLongpressEnabled();

        boolean onTouchEvent(MotionEvent motionEvent);

        void setIsLongpressEnabled(boolean z);

        void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener);
    }

    /* loaded from: GestureDetectorCompat$GestureDetectorCompatImplBase.class */
    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl {
        private static final int LONG_PRESS = 2;
        private static final int SHOW_PRESS = 1;
        private static final int TAP = 3;
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        private MotionEvent mCurrentDownEvent;
        private boolean mDeferConfirmSingleTap;
        private GestureDetector.OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        private final GestureDetector.OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        private boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;
        private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
        private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
        private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();

        /* loaded from: GestureDetectorCompat$GestureDetectorCompatImplBase$GestureHandler.class */
        private class GestureHandler extends Handler {
            final GestureDetectorCompatImplBase this$0;

            GestureHandler(GestureDetectorCompatImplBase gestureDetectorCompatImplBase) {
                this.this$0 = gestureDetectorCompatImplBase;
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            GestureHandler(GestureDetectorCompatImplBase gestureDetectorCompatImplBase, Handler handler) {
                super(handler.getLooper());
                this.this$0 = gestureDetectorCompatImplBase;
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        this.this$0.mListener.onShowPress(this.this$0.mCurrentDownEvent);
                        return;
                    case 2:
                        this.this$0.dispatchLongPress();
                        return;
                    case 3:
                        if (this.this$0.mDoubleTapListener != null) {
                            if (this.this$0.mStillDown) {
                                this.this$0.mDeferConfirmSingleTap = true;
                                return;
                            } else {
                                this.this$0.mDoubleTapListener.onSingleTapConfirmed(this.this$0.mCurrentDownEvent);
                                return;
                            }
                        }
                        return;
                    default:
                        throw new RuntimeException("Unknown message " + message);
                }
            }
        }

        public GestureDetectorCompatImplBase(Context context, GestureDetector.OnGestureListener onGestureListener, Handler handler) {
            if (handler != null) {
                this.mHandler = new GestureHandler(this, handler);
            } else {
                this.mHandler = new GestureHandler(this);
            }
            this.mListener = onGestureListener;
            if (onGestureListener instanceof GestureDetector.OnDoubleTapListener) {
                setOnDoubleTapListener((GestureDetector.OnDoubleTapListener) onGestureListener);
            }
            init(context);
        }

        private void cancel() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            this.mIsDoubleTapping = false;
            this.mStillDown = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }

        private void cancelTaps() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mIsDoubleTapping = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }

        private void init(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            if (this.mListener == null) {
                throw new IllegalArgumentException("OnGestureListener must not be null");
            }
            this.mIsLongpressEnabled = true;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
            int scaledDoubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
            this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
            this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
            this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
            this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
        }

        private boolean isConsideredDoubleTap(MotionEvent motionEvent, MotionEvent motionEvent2, MotionEvent motionEvent3) {
            boolean z = false;
            if (this.mAlwaysInBiggerTapRegion && motionEvent3.getEventTime() - motionEvent2.getEventTime() <= DOUBLE_TAP_TIMEOUT) {
                int x = ((int) motionEvent.getX()) - ((int) motionEvent3.getX());
                int y = ((int) motionEvent.getY()) - ((int) motionEvent3.getY());
                if ((x * x) + (y * y) < this.mDoubleTapSlopSquare) {
                    z = true;
                }
                return z;
            }
            return false;
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean isLongpressEnabled() {
            return this.mIsLongpressEnabled;
        }

        /* JADX WARN: Code restructure failed: missing block: B:60:0x0262, code lost:
            if (java.lang.Math.abs(r0) >= 1.0f) goto L58;
         */
        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean onTouchEvent(android.view.MotionEvent r8) {
            /*
                Method dump skipped, instructions count: 1217
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImplBase.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setIsLongpressEnabled(boolean z) {
            this.mIsLongpressEnabled = z;
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
            this.mDoubleTapListener = onDoubleTapListener;
        }
    }

    /* loaded from: GestureDetectorCompat$GestureDetectorCompatImplJellybeanMr2.class */
    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl {
        private final GestureDetector mDetector;

        public GestureDetectorCompatImplJellybeanMr2(Context context, GestureDetector.OnGestureListener onGestureListener, Handler handler) {
            this.mDetector = new GestureDetector(context, onGestureListener, handler);
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean isLongpressEnabled() {
            return this.mDetector.isLongpressEnabled();
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.mDetector.onTouchEvent(motionEvent);
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setIsLongpressEnabled(boolean z) {
            this.mDetector.setIsLongpressEnabled(z);
        }

        @Override // android.support.v4.view.GestureDetectorCompat.GestureDetectorCompatImpl
        public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
            this.mDetector.setOnDoubleTapListener(onDoubleTapListener);
        }
    }

    public GestureDetectorCompat(Context context, GestureDetector.OnGestureListener onGestureListener) {
        this(context, onGestureListener, null);
    }

    public GestureDetectorCompat(Context context, GestureDetector.OnGestureListener onGestureListener, Handler handler) {
        if (Build.VERSION.SDK_INT > 17) {
            this.mImpl = new GestureDetectorCompatImplJellybeanMr2(context, onGestureListener, handler);
        } else {
            this.mImpl = new GestureDetectorCompatImplBase(context, onGestureListener, handler);
        }
    }

    public boolean isLongpressEnabled() {
        return this.mImpl.isLongpressEnabled();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mImpl.onTouchEvent(motionEvent);
    }

    public void setIsLongpressEnabled(boolean z) {
        this.mImpl.setIsLongpressEnabled(z);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.mImpl.setOnDoubleTapListener(onDoubleTapListener);
    }
}