package com.android.server.accessibility;

import android.content.Context;
import android.os.PowerManager;
import android.util.Pools;
import android.view.Choreographer;
import android.view.InputEvent;
import android.view.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AccessibilityInputFilter.class */
public class AccessibilityInputFilter extends InputFilter implements EventStreamTransformation {
    private static final String TAG = AccessibilityInputFilter.class.getSimpleName();
    private static final boolean DEBUG = false;
    static final int FLAG_FEATURE_SCREEN_MAGNIFIER = 1;
    static final int FLAG_FEATURE_TOUCH_EXPLORATION = 2;
    static final int FLAG_FEATURE_FILTER_KEY_EVENTS = 4;
    private final Runnable mProcessBatchedEventsRunnable;
    private final Context mContext;
    private final PowerManager mPm;
    private final AccessibilityManagerService mAms;
    private final Choreographer mChoreographer;
    private int mCurrentTouchDeviceId;
    private boolean mInstalled;
    private int mEnabledFeatures;
    private TouchExplorer mTouchExplorer;
    private ScreenMagnifier mScreenMagnifier;
    private EventStreamTransformation mEventHandler;
    private MotionEventHolder mEventQueue;
    private boolean mMotionEventSequenceStarted;
    private boolean mHoverEventSequenceStarted;
    private boolean mKeyEventSequenceStarted;
    private boolean mFilterKeyEvents;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityInputFilter(Context context, AccessibilityManagerService service) {
        super(context.getMainLooper());
        this.mProcessBatchedEventsRunnable = new Runnable() { // from class: com.android.server.accessibility.AccessibilityInputFilter.1
            @Override // java.lang.Runnable
            public void run() {
                long frameTimeNanos = AccessibilityInputFilter.this.mChoreographer.getFrameTimeNanos();
                AccessibilityInputFilter.this.processBatchedEvents(frameTimeNanos);
                if (AccessibilityInputFilter.this.mEventQueue != null) {
                    AccessibilityInputFilter.this.scheduleProcessBatchedEvents();
                }
            }
        };
        this.mContext = context;
        this.mAms = service;
        this.mPm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mChoreographer = Choreographer.getInstance();
    }

    @Override // android.view.InputFilter
    public void onInstalled() {
        this.mInstalled = true;
        disableFeatures();
        enableFeatures();
        super.onInstalled();
    }

    @Override // android.view.InputFilter
    public void onUninstalled() {
        this.mInstalled = false;
        disableFeatures();
        super.onUninstalled();
    }

    @Override // android.view.InputFilter
    public void onInputEvent(InputEvent event, int policyFlags) {
        if ((event instanceof MotionEvent) && event.isFromSource(4098)) {
            MotionEvent motionEvent = (MotionEvent) event;
            onMotionEvent(motionEvent, policyFlags);
        } else if ((event instanceof KeyEvent) && event.isFromSource(257)) {
            KeyEvent keyEvent = (KeyEvent) event;
            onKeyEvent(keyEvent, policyFlags);
        } else {
            super.onInputEvent(event, policyFlags);
        }
    }

    private void onMotionEvent(MotionEvent event, int policyFlags) {
        if (this.mEventHandler == null) {
            super.onInputEvent(event, policyFlags);
        } else if ((policyFlags & 1073741824) == 0) {
            this.mMotionEventSequenceStarted = false;
            this.mHoverEventSequenceStarted = false;
            this.mEventHandler.clear();
            super.onInputEvent(event, policyFlags);
        } else {
            int deviceId = event.getDeviceId();
            if (this.mCurrentTouchDeviceId != deviceId) {
                this.mCurrentTouchDeviceId = deviceId;
                this.mMotionEventSequenceStarted = false;
                this.mHoverEventSequenceStarted = false;
                this.mEventHandler.clear();
            }
            if (this.mCurrentTouchDeviceId < 0) {
                super.onInputEvent(event, policyFlags);
            } else if (event.getActionMasked() == 8) {
                super.onInputEvent(event, policyFlags);
            } else {
                if (event.isTouchEvent()) {
                    if (!this.mMotionEventSequenceStarted) {
                        if (event.getActionMasked() != 0) {
                            return;
                        }
                        this.mMotionEventSequenceStarted = true;
                    }
                } else if (!this.mHoverEventSequenceStarted) {
                    if (event.getActionMasked() != 9) {
                        return;
                    }
                    this.mHoverEventSequenceStarted = true;
                }
                batchMotionEvent(event, policyFlags);
            }
        }
    }

    private void onKeyEvent(KeyEvent event, int policyFlags) {
        if (!this.mFilterKeyEvents) {
            super.onInputEvent(event, policyFlags);
        } else if ((policyFlags & 1073741824) == 0) {
            this.mKeyEventSequenceStarted = false;
            super.onInputEvent(event, policyFlags);
        } else {
            if (!this.mKeyEventSequenceStarted) {
                if (event.getAction() != 0) {
                    return;
                }
                this.mKeyEventSequenceStarted = true;
            }
            this.mAms.notifyKeyEvent(event, policyFlags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleProcessBatchedEvents() {
        this.mChoreographer.postCallback(0, this.mProcessBatchedEventsRunnable, null);
    }

    private void batchMotionEvent(MotionEvent event, int policyFlags) {
        if (this.mEventQueue == null) {
            this.mEventQueue = MotionEventHolder.obtain(event, policyFlags);
            scheduleProcessBatchedEvents();
        } else if (this.mEventQueue.event.addBatch(event)) {
        } else {
            MotionEventHolder holder = MotionEventHolder.obtain(event, policyFlags);
            holder.next = this.mEventQueue;
            this.mEventQueue.previous = holder;
            this.mEventQueue = holder;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processBatchedEvents(long frameNanos) {
        MotionEventHolder current;
        MotionEventHolder motionEventHolder = this.mEventQueue;
        while (true) {
            current = motionEventHolder;
            if (current.next == null) {
                break;
            }
            motionEventHolder = current.next;
        }
        while (current != null) {
            if (current.event.getEventTimeNano() >= frameNanos) {
                current.next = null;
                return;
            }
            handleMotionEvent(current.event, current.policyFlags);
            MotionEventHolder prior = current;
            current = current.previous;
            prior.recycle();
        }
        this.mEventQueue = null;
    }

    private void handleMotionEvent(MotionEvent event, int policyFlags) {
        if (this.mEventHandler != null) {
            this.mPm.userActivity(event.getEventTime(), false);
            MotionEvent transformedEvent = MotionEvent.obtain(event);
            this.mEventHandler.onMotionEvent(transformedEvent, event, policyFlags);
            transformedEvent.recycle();
        }
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onMotionEvent(MotionEvent transformedEvent, MotionEvent rawEvent, int policyFlags) {
        sendInputEvent(transformedEvent, policyFlags);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void setNext(EventStreamTransformation sink) {
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void clear() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnabledFeatures(int enabledFeatures) {
        if (this.mEnabledFeatures == enabledFeatures) {
            return;
        }
        if (this.mInstalled) {
            disableFeatures();
        }
        this.mEnabledFeatures = enabledFeatures;
        if (this.mInstalled) {
            enableFeatures();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyAccessibilityEvent(AccessibilityEvent event) {
        if (this.mEventHandler != null) {
            this.mEventHandler.onAccessibilityEvent(event);
        }
    }

    private void enableFeatures() {
        this.mMotionEventSequenceStarted = false;
        this.mHoverEventSequenceStarted = false;
        if ((this.mEnabledFeatures & 1) != 0) {
            ScreenMagnifier screenMagnifier = new ScreenMagnifier(this.mContext, 0, this.mAms);
            this.mScreenMagnifier = screenMagnifier;
            this.mEventHandler = screenMagnifier;
            this.mEventHandler.setNext(this);
        }
        if ((this.mEnabledFeatures & 2) != 0) {
            this.mTouchExplorer = new TouchExplorer(this.mContext, this.mAms);
            this.mTouchExplorer.setNext(this);
            if (this.mEventHandler != null) {
                this.mEventHandler.setNext(this.mTouchExplorer);
            } else {
                this.mEventHandler = this.mTouchExplorer;
            }
        }
        if ((this.mEnabledFeatures & 4) != 0) {
            this.mFilterKeyEvents = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableFeatures() {
        if (this.mTouchExplorer != null) {
            this.mTouchExplorer.clear();
            this.mTouchExplorer.onDestroy();
            this.mTouchExplorer = null;
        }
        if (this.mScreenMagnifier != null) {
            this.mScreenMagnifier.clear();
            this.mScreenMagnifier.onDestroy();
            this.mScreenMagnifier = null;
        }
        this.mEventHandler = null;
        this.mKeyEventSequenceStarted = false;
        this.mMotionEventSequenceStarted = false;
        this.mHoverEventSequenceStarted = false;
        this.mFilterKeyEvents = false;
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onDestroy() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AccessibilityInputFilter$MotionEventHolder.class */
    public static class MotionEventHolder {
        private static final int MAX_POOL_SIZE = 32;
        private static final Pools.SimplePool<MotionEventHolder> sPool = new Pools.SimplePool<>(32);
        public int policyFlags;
        public MotionEvent event;
        public MotionEventHolder next;
        public MotionEventHolder previous;

        private MotionEventHolder() {
        }

        public static MotionEventHolder obtain(MotionEvent event, int policyFlags) {
            MotionEventHolder holder = sPool.acquire();
            if (holder == null) {
                holder = new MotionEventHolder();
            }
            holder.event = MotionEvent.obtain(event);
            holder.policyFlags = policyFlags;
            return holder;
        }

        public void recycle() {
            this.event.recycle();
            this.event = null;
            this.policyFlags = 0;
            this.next = null;
            this.previous = null;
            sPool.release(this);
        }
    }
}