package android.view;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.ActivityManagerNative;
import android.content.ClipDescription;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Choreographer;
import android.view.HardwareRenderer;
import android.view.IWindow;
import android.view.InputDevice;
import android.view.InputQueue;
import android.view.KeyCharacterMap;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import com.android.internal.R;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.PolicyManager;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import gov.nist.javax.sip.parser.TokenNames;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

/* loaded from: ViewRootImpl.class */
public final class ViewRootImpl implements ViewParent, View.AttachInfo.Callbacks, HardwareRenderer.HardwareDrawCallbacks {
    private static final String TAG = "ViewRootImpl";
    private static final boolean DBG = false;
    private static final boolean LOCAL_LOGV = false;
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_DIALOG = false;
    private static final boolean DEBUG_INPUT_RESIZE = false;
    private static final boolean DEBUG_ORIENTATION = false;
    private static final boolean DEBUG_TRACKBALL = false;
    private static final boolean DEBUG_IMF = false;
    private static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_FPS = false;
    private static final boolean DEBUG_INPUT_PROCESSING = false;
    private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
    private static final String PROPERTY_MEDIA_DISABLED = "config.disable_media";
    static final int MAX_TRACKBALL_DELAY = 250;
    final Context mContext;
    final IWindowSession mWindowSession;
    final Display mDisplay;
    final String mBasePackageName;
    final Thread mThread;
    final WindowLeaked mLocation;
    final W mWindow;
    final int mTargetSdkVersion;
    int mSeq;
    View mView;
    View mAccessibilityFocusedHost;
    AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
    int mViewVisibility;
    SurfaceHolder.Callback2 mSurfaceHolderCallback;
    BaseSurfaceHolder mSurfaceHolder;
    boolean mIsCreating;
    boolean mDrawingAllowed;
    final Region mTransparentRegion;
    final Region mPreviousTransparentRegion;
    int mWidth;
    int mHeight;
    Rect mDirty;
    boolean mIsAnimating;
    CompatibilityInfo.Translator mTranslator;
    final View.AttachInfo mAttachInfo;
    InputChannel mInputChannel;
    InputQueue.Callback mInputQueueCallback;
    InputQueue mInputQueue;
    FallbackEventHandler mFallbackEventHandler;
    Choreographer mChoreographer;
    final Rect mTempRect;
    final Rect mVisRect;
    boolean mTraversalScheduled;
    int mTraversalBarrier;
    boolean mWillDrawSoon;
    boolean mIsInTraversal;
    boolean mFitSystemWindowsRequested;
    boolean mLayoutRequested;
    boolean mFirst;
    boolean mReportNextDraw;
    boolean mFullRedrawNeeded;
    boolean mNewSurfaceNeeded;
    boolean mHasHadWindowFocus;
    boolean mLastWasImTarget;
    boolean mWindowsAnimating;
    boolean mDrawDuringWindowsAnimating;
    boolean mIsDrawing;
    int mLastSystemUiVisibility;
    int mClientWindowLayoutFlags;
    boolean mLastOverscanRequested;
    private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
    private QueuedInputEvent mQueuedInputEventPool;
    private int mQueuedInputEventPoolSize;
    QueuedInputEvent mPendingInputEventHead;
    QueuedInputEvent mPendingInputEventTail;
    int mPendingInputEventCount;
    boolean mProcessInputEventsScheduled;
    InputStage mFirstInputStage;
    InputStage mFirstPostImeInputStage;
    boolean mFlipControllerFallbackKeys;
    boolean mAdded;
    boolean mAddedTouchMode;
    final DisplayAdjustments mDisplayAdjustments;
    final Rect mWinFrame;
    boolean mScrollMayChange;
    int mSoftInputMode;
    WeakReference<View> mLastScrolledFocus;
    int mScrollY;
    int mCurScrollY;
    Scroller mScroller;
    HardwareLayer mResizeBuffer;
    long mResizeBufferStartTime;
    int mResizeBufferDuration;
    private ArrayList<LayoutTransition> mPendingTransitions;
    final ViewConfiguration mViewConfiguration;
    ClipDescription mDragDescription;
    View mCurrentDragView;
    volatile Object mLocalDragState;
    private boolean mProfileRendering;
    private Choreographer.FrameCallback mRenderProfiler;
    private boolean mRenderProfilingEnabled;
    private boolean mMediaDisabled;
    private int mFpsNumFrames;
    AudioManager mAudioManager;
    final AccessibilityManager mAccessibilityManager;
    AccessibilityInteractionController mAccessibilityInteractionController;
    AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager;
    SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
    HashSet<View> mTempHashSet;
    private final int mDensity;
    private final int mNoncompatDensity;
    private int mViewLayoutDirectionInitial;
    private boolean mRemoved;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private boolean mProfile;
    int mHardwareYOffset;
    int mResizeAlpha;
    final Paint mResizePaint;
    private static final int MSG_INVALIDATE = 1;
    private static final int MSG_INVALIDATE_RECT = 2;
    private static final int MSG_DIE = 3;
    private static final int MSG_RESIZED = 4;
    private static final int MSG_RESIZED_REPORT = 5;
    private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
    private static final int MSG_DISPATCH_INPUT_EVENT = 7;
    private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
    private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
    private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
    private static final int MSG_FINISH_INPUT_CONNECTION = 12;
    private static final int MSG_CHECK_FOCUS = 13;
    private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
    private static final int MSG_DISPATCH_DRAG_EVENT = 15;
    private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
    private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
    private static final int MSG_UPDATE_CONFIGURATION = 18;
    private static final int MSG_PROCESS_INPUT_EVENTS = 19;
    private static final int MSG_DISPATCH_SCREEN_STATE = 20;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
    private static final int MSG_DISPATCH_DONE_ANIMATING = 22;
    private static final int MSG_INVALIDATE_WORLD = 23;
    private static final int MSG_WINDOW_MOVED = 24;
    private static final int MSG_FLUSH_LAYER_UPDATES = 25;
    final ViewRootHandler mHandler;
    final TraversalRunnable mTraversalRunnable;
    WindowInputEventReceiver mInputEventReceiver;
    final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
    boolean mConsumeBatchedInputScheduled;
    final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
    private final SurfaceHolder mHolder;
    static final ThreadLocal<RunQueue> sRunQueues = new ThreadLocal<>();
    static final ArrayList<Runnable> sFirstDrawHandlers = new ArrayList<>();
    static boolean sFirstDrawComplete = false;
    static final ArrayList<ComponentCallbacks> sConfigCallbacks = new ArrayList<>();
    static final Interpolator mResizeInterpolator = new AccelerateDecelerateInterpolator();
    final int[] mTmpLocation = new int[2];
    final TypedValue mTmpValue = new TypedValue();
    final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();
    boolean mAppVisible = true;
    int mOrigWindowType = -1;
    boolean mStopped = false;
    boolean mLastInCompatMode = false;
    final Rect mCurrentDirty = new Rect();
    String mPendingInputEventQueueLengthCounterName = "pq";
    boolean mWindowAttributesChanged = false;
    int mWindowAttributesChangesFlag = 0;
    private final Surface mSurface = new Surface();
    final Rect mPendingOverscanInsets = new Rect();
    final Rect mPendingVisibleInsets = new Rect();
    final Rect mPendingContentInsets = new Rect();
    final ViewTreeObserver.InternalInsetsInfo mLastGivenInsets = new ViewTreeObserver.InternalInsetsInfo();
    final Rect mFitSystemWindowsInsets = new Rect();
    final Configuration mLastConfiguration = new Configuration();
    final Configuration mPendingConfiguration = new Configuration();
    final PointF mDragPoint = new PointF();
    final PointF mLastTouchPoint = new PointF();
    private long mFpsStartTime = -1;
    private long mFpsPrevTime = -1;
    private final ArrayList<DisplayList> mDisplayLists = new ArrayList<>();
    private boolean mInLayout = false;
    ArrayList<View> mLayoutRequesters = new ArrayList<>();
    boolean mHandlingLayoutInLayoutRequest = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$SystemUiVisibilityInfo.class */
    public static final class SystemUiVisibilityInfo {
        int seq;
        int globalVisibility;
        int localValue;
        int localChanges;

        SystemUiVisibilityInfo() {
        }
    }

    public ViewRootImpl(Context context, Display display) {
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mProfile = false;
        this.mResizePaint = new Paint();
        this.mHandler = new ViewRootHandler();
        this.mTraversalRunnable = new TraversalRunnable();
        this.mConsumedBatchedInputRunnable = new ConsumeBatchedInputRunnable();
        this.mInvalidateOnAnimationRunnable = new InvalidateOnAnimationRunnable();
        this.mHolder = new SurfaceHolder() { // from class: android.view.ViewRootImpl.5
            @Override // android.view.SurfaceHolder
            public Surface getSurface() {
                return ViewRootImpl.this.mSurface;
            }

            @Override // android.view.SurfaceHolder
            public boolean isCreating() {
                return false;
            }

            @Override // android.view.SurfaceHolder
            public void addCallback(SurfaceHolder.Callback callback) {
            }

            @Override // android.view.SurfaceHolder
            public void removeCallback(SurfaceHolder.Callback callback) {
            }

            @Override // android.view.SurfaceHolder
            public void setFixedSize(int width, int height) {
            }

            @Override // android.view.SurfaceHolder
            public void setSizeFromLayout() {
            }

            @Override // android.view.SurfaceHolder
            public void setFormat(int format) {
            }

            @Override // android.view.SurfaceHolder
            public void setType(int type) {
            }

            @Override // android.view.SurfaceHolder
            public void setKeepScreenOn(boolean screenOn) {
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas() {
                return null;
            }

            @Override // android.view.SurfaceHolder
            public Canvas lockCanvas(Rect dirty) {
                return null;
            }

            @Override // android.view.SurfaceHolder
            public void unlockCanvasAndPost(Canvas canvas) {
            }

            @Override // android.view.SurfaceHolder
            public Rect getSurfaceFrame() {
                return null;
            }
        };
        this.mContext = context;
        this.mWindowSession = WindowManagerGlobal.getWindowSession();
        this.mDisplay = display;
        this.mBasePackageName = context.getBasePackageName();
        this.mDisplayAdjustments = display.getDisplayAdjustments();
        this.mThread = Thread.currentThread();
        this.mLocation = new WindowLeaked(null);
        this.mLocation.fillInStackTrace();
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDirty = new Rect();
        this.mTempRect = new Rect();
        this.mVisRect = new Rect();
        this.mWinFrame = new Rect();
        this.mWindow = new W(this);
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mViewVisibility = 8;
        this.mTransparentRegion = new Region();
        this.mPreviousTransparentRegion = new Region();
        this.mFirst = true;
        this.mAdded = false;
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mAccessibilityInteractionConnectionManager = new AccessibilityInteractionConnectionManager();
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        this.mAttachInfo = new View.AttachInfo(this.mWindowSession, this.mWindow, display, this, this.mHandler, this);
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNoncompatDensity = context.getResources().getDisplayMetrics().noncompatDensityDpi;
        this.mFallbackEventHandler = PolicyManager.makeNewFallbackEventHandler(context);
        this.mChoreographer = Choreographer.getInstance();
        this.mFlipControllerFallbackKeys = context.getResources().getBoolean(R.bool.flip_controller_fallback_keys);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mAttachInfo.mScreenOn = powerManager.isScreenOn();
        loadSystemProperties();
    }

    public static void addFirstDrawHandler(Runnable callback) {
        synchronized (sFirstDrawHandlers) {
            if (!sFirstDrawComplete) {
                sFirstDrawHandlers.add(callback);
            }
        }
    }

    public static void addConfigCallback(ComponentCallbacks callback) {
        synchronized (sConfigCallbacks) {
            sConfigCallbacks.add(callback);
        }
    }

    public void profile() {
        this.mProfile = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isInTouchMode() {
        IWindowSession windowSession = WindowManagerGlobal.peekWindowSession();
        if (windowSession != null) {
            try {
                return windowSession.getInTouchMode();
            } catch (RemoteException e) {
                return false;
            }
        }
        return false;
    }

    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
        synchronized (this) {
            if (this.mView == null) {
                this.mView = view;
                this.mViewLayoutDirectionInitial = this.mView.getRawLayoutDirection();
                this.mFallbackEventHandler.setView(view);
                this.mWindowAttributes.copyFrom(attrs);
                if (this.mWindowAttributes.packageName == null) {
                    this.mWindowAttributes.packageName = this.mBasePackageName;
                }
                WindowManager.LayoutParams attrs2 = this.mWindowAttributes;
                this.mClientWindowLayoutFlags = attrs2.flags;
                setAccessibilityFocus(null, null);
                if (view instanceof RootViewSurfaceTaker) {
                    this.mSurfaceHolderCallback = ((RootViewSurfaceTaker) view).willYouTakeTheSurface();
                    if (this.mSurfaceHolderCallback != null) {
                        this.mSurfaceHolder = new TakenSurfaceHolder();
                        this.mSurfaceHolder.setFormat(0);
                    }
                }
                CompatibilityInfo compatibilityInfo = this.mDisplayAdjustments.getCompatibilityInfo();
                this.mTranslator = compatibilityInfo.getTranslator();
                this.mDisplayAdjustments.setActivityToken(attrs2.token);
                if (this.mSurfaceHolder == null) {
                    enableHardwareAcceleration(attrs2);
                }
                boolean restore = false;
                if (this.mTranslator != null) {
                    this.mSurface.setCompatibilityTranslator(this.mTranslator);
                    restore = true;
                    attrs2.backup();
                    this.mTranslator.translateWindowLayout(attrs2);
                }
                if (!compatibilityInfo.supportsScreen()) {
                    attrs2.privateFlags |= 128;
                    this.mLastInCompatMode = true;
                }
                this.mSoftInputMode = attrs2.softInputMode;
                this.mWindowAttributesChanged = true;
                this.mWindowAttributesChangesFlag = -1;
                this.mAttachInfo.mRootView = view;
                this.mAttachInfo.mScalingRequired = this.mTranslator != null;
                this.mAttachInfo.mApplicationScale = this.mTranslator == null ? 1.0f : this.mTranslator.applicationScale;
                if (panelParentView != null) {
                    this.mAttachInfo.mPanelParentWindowToken = panelParentView.getApplicationWindowToken();
                }
                this.mAdded = true;
                requestLayout();
                if ((this.mWindowAttributes.inputFeatures & 2) == 0) {
                    this.mInputChannel = new InputChannel();
                }
                try {
                    this.mOrigWindowType = this.mWindowAttributes.type;
                    this.mAttachInfo.mRecomputeGlobalAttributes = true;
                    collectViewAttributes();
                    int res = this.mWindowSession.addToDisplay(this.mWindow, this.mSeq, this.mWindowAttributes, getHostVisibility(), this.mDisplay.getDisplayId(), this.mAttachInfo.mContentInsets, this.mInputChannel);
                    if (restore) {
                        attrs2.restore();
                    }
                    if (this.mTranslator != null) {
                        this.mTranslator.translateRectInScreenToAppWindow(this.mAttachInfo.mContentInsets);
                    }
                    this.mPendingOverscanInsets.set(0, 0, 0, 0);
                    this.mPendingContentInsets.set(this.mAttachInfo.mContentInsets);
                    this.mPendingVisibleInsets.set(0, 0, 0, 0);
                    if (res < 0) {
                        this.mAttachInfo.mRootView = null;
                        this.mAdded = false;
                        this.mFallbackEventHandler.setView(null);
                        unscheduleTraversals();
                        setAccessibilityFocus(null, null);
                        switch (res) {
                            case -9:
                                throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified display can not be found");
                            case -8:
                                throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- permission denied for this window type");
                            case -7:
                                throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- another window of this type already exists");
                            case -6:
                                return;
                            case -5:
                                throw new WindowManager.BadTokenException("Unable to add window -- window " + this.mWindow + " has already been added");
                            case -4:
                                throw new WindowManager.BadTokenException("Unable to add window -- app for token " + attrs2.token + " is exiting");
                            case -3:
                                throw new WindowManager.BadTokenException("Unable to add window -- token " + attrs2.token + " is not for an application");
                            case -2:
                            case -1:
                                throw new WindowManager.BadTokenException("Unable to add window -- token " + attrs2.token + " is not valid; is your activity running?");
                            default:
                                throw new RuntimeException("Unable to add window -- unknown error code " + res);
                        }
                    }
                    if (view instanceof RootViewSurfaceTaker) {
                        this.mInputQueueCallback = ((RootViewSurfaceTaker) view).willYouTakeTheInputQueue();
                    }
                    if (this.mInputChannel != null) {
                        if (this.mInputQueueCallback != null) {
                            this.mInputQueue = new InputQueue();
                            this.mInputQueueCallback.onInputQueueCreated(this.mInputQueue);
                        }
                        this.mInputEventReceiver = new WindowInputEventReceiver(this.mInputChannel, Looper.myLooper());
                    }
                    view.assignParent(this);
                    this.mAddedTouchMode = (res & 1) != 0;
                    this.mAppVisible = (res & 2) != 0;
                    if (this.mAccessibilityManager.isEnabled()) {
                        this.mAccessibilityInteractionConnectionManager.ensureConnection();
                    }
                    if (view.getImportantForAccessibility() == 0) {
                        view.setImportantForAccessibility(1);
                    }
                    CharSequence counterSuffix = attrs2.getTitle();
                    InputStage syntheticInputStage = new SyntheticInputStage();
                    InputStage viewPostImeStage = new ViewPostImeInputStage(syntheticInputStage);
                    InputStage nativePostImeStage = new NativePostImeInputStage(viewPostImeStage, "aq:native-post-ime:" + ((Object) counterSuffix));
                    InputStage earlyPostImeStage = new EarlyPostImeInputStage(nativePostImeStage);
                    InputStage imeStage = new ImeInputStage(earlyPostImeStage, "aq:ime:" + ((Object) counterSuffix));
                    InputStage viewPreImeStage = new ViewPreImeInputStage(imeStage);
                    InputStage nativePreImeStage = new NativePreImeInputStage(viewPreImeStage, "aq:native-pre-ime:" + ((Object) counterSuffix));
                    this.mFirstInputStage = nativePreImeStage;
                    this.mFirstPostImeInputStage = earlyPostImeStage;
                    this.mPendingInputEventQueueLengthCounterName = "aq:pending:" + ((Object) counterSuffix);
                } catch (RemoteException e) {
                    this.mAdded = false;
                    this.mView = null;
                    this.mAttachInfo.mRootView = null;
                    this.mInputChannel = null;
                    this.mFallbackEventHandler.setView(null);
                    unscheduleTraversals();
                    setAccessibilityFocus(null, null);
                    throw new RuntimeException("Adding window failed", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInLocalFocusMode() {
        return (this.mWindowAttributes.flags & 268435456) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroyHardwareResources() {
        invalidateDisplayLists();
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.destroyHardwareResources(this.mView);
            this.mAttachInfo.mHardwareRenderer.destroy(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroyHardwareLayers() {
        if (this.mThread != Thread.currentThread()) {
            if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                HardwareRenderer.trimMemory(60);
                return;
            }
            return;
        }
        invalidateDisplayLists();
        if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            this.mAttachInfo.mHardwareRenderer.destroyLayers(this.mView);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pushHardwareLayerUpdate(HardwareLayer layer) {
        if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            this.mAttachInfo.mHardwareRenderer.pushLayerUpdate(layer);
        }
    }

    void flushHardwareLayerUpdates() {
        if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled() && this.mAttachInfo.mHardwareRenderer.validate()) {
            this.mAttachInfo.mHardwareRenderer.flushLayerUpdates();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchFlushHardwareLayerUpdates() {
        this.mHandler.removeMessages(25);
        this.mHandler.sendMessageAtFrontOfQueue(this.mHandler.obtainMessage(25));
    }

    public boolean attachFunctor(int functor) {
        if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
            return this.mAttachInfo.mHardwareRenderer.attachFunctor(this.mAttachInfo, functor);
        }
        return false;
    }

    public void detachFunctor(int functor) {
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.detachFunctor(functor);
        }
    }

    private void enableHardwareAcceleration(WindowManager.LayoutParams attrs) {
        this.mAttachInfo.mHardwareAccelerated = false;
        this.mAttachInfo.mHardwareAccelerationRequested = false;
        if (this.mTranslator != null) {
            return;
        }
        boolean hardwareAccelerated = (attrs.flags & 16777216) != 0;
        if (!hardwareAccelerated || !HardwareRenderer.isAvailable()) {
            return;
        }
        boolean fakeHwAccelerated = (attrs.privateFlags & 1) != 0;
        boolean forceHwAccelerated = (attrs.privateFlags & 2) != 0;
        if (HardwareRenderer.sRendererDisabled && (!HardwareRenderer.sSystemRendererDisabled || !forceHwAccelerated)) {
            if (fakeHwAccelerated) {
                this.mAttachInfo.mHardwareAccelerationRequested = true;
            }
        } else if (!HardwareRenderer.sSystemRendererDisabled && Looper.getMainLooper() != Looper.myLooper()) {
            Log.w("HardwareRenderer", "Attempting to initialize hardware acceleration outside of the main thread, aborting");
        } else {
            if (this.mAttachInfo.mHardwareRenderer != null) {
                this.mAttachInfo.mHardwareRenderer.destroy(true);
            }
            boolean translucent = attrs.format != -1;
            this.mAttachInfo.mHardwareRenderer = HardwareRenderer.createGlRenderer(2, translucent);
            if (this.mAttachInfo.mHardwareRenderer != null) {
                this.mAttachInfo.mHardwareRenderer.setName(attrs.getTitle().toString());
                View.AttachInfo attachInfo = this.mAttachInfo;
                this.mAttachInfo.mHardwareAccelerationRequested = true;
                attachInfo.mHardwareAccelerated = true;
            }
        }
    }

    public View getView() {
        return this.mView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final WindowLeaked getLocation() {
        return this.mLocation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLayoutParams(WindowManager.LayoutParams attrs, boolean newView) {
        synchronized (this) {
            int oldSoftInputMode = this.mWindowAttributes.softInputMode;
            this.mClientWindowLayoutFlags = attrs.flags;
            int compatibleWindowFlag = this.mWindowAttributes.privateFlags & 128;
            attrs.systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
            attrs.subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
            this.mWindowAttributesChangesFlag = this.mWindowAttributes.copyFrom(attrs);
            if ((this.mWindowAttributesChangesFlag & 524288) != 0) {
                this.mAttachInfo.mRecomputeGlobalAttributes = true;
            }
            if (this.mWindowAttributes.packageName == null) {
                this.mWindowAttributes.packageName = this.mBasePackageName;
            }
            this.mWindowAttributes.privateFlags |= compatibleWindowFlag;
            applyKeepScreenOnFlag(this.mWindowAttributes);
            if (newView) {
                this.mSoftInputMode = attrs.softInputMode;
                requestLayout();
            }
            if ((attrs.softInputMode & 240) == 0) {
                this.mWindowAttributes.softInputMode = (this.mWindowAttributes.softInputMode & (-241)) | (oldSoftInputMode & 240);
            }
            this.mWindowAttributesChanged = true;
            scheduleTraversals();
        }
    }

    void handleAppVisibility(boolean visible) {
        if (this.mAppVisible != visible) {
            this.mAppVisible = visible;
            scheduleTraversals();
        }
    }

    void handleGetNewSurface() {
        this.mNewSurfaceNeeded = true;
        this.mFullRedrawNeeded = true;
        scheduleTraversals();
    }

    void handleScreenStateChange(boolean on) {
        if (on != this.mAttachInfo.mScreenOn) {
            this.mAttachInfo.mScreenOn = on;
            if (this.mView != null) {
                this.mView.dispatchScreenStateChanged(on ? 1 : 0);
            }
            if (on) {
                this.mFullRedrawNeeded = true;
                scheduleTraversals();
            }
        }
    }

    @Override // android.view.ViewParent
    public void requestFitSystemWindows() {
        checkThread();
        this.mFitSystemWindowsRequested = true;
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void requestLayout() {
        if (!this.mHandlingLayoutInLayoutRequest) {
            checkThread();
            this.mLayoutRequested = true;
            scheduleTraversals();
        }
    }

    @Override // android.view.ViewParent
    public boolean isLayoutRequested() {
        return this.mLayoutRequested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
        scheduleTraversals();
    }

    void invalidateWorld(View view) {
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                invalidateWorld(parent.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewParent
    public void invalidateChild(View child, Rect dirty) {
        invalidateChildInParent(null, dirty);
    }

    @Override // android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        checkThread();
        if (dirty == null) {
            invalidate();
            return null;
        } else if (dirty.isEmpty() && !this.mIsAnimating) {
            return null;
        } else {
            if (this.mCurScrollY != 0 || this.mTranslator != null) {
                this.mTempRect.set(dirty);
                dirty = this.mTempRect;
                if (this.mCurScrollY != 0) {
                    dirty.offset(0, -this.mCurScrollY);
                }
                if (this.mTranslator != null) {
                    this.mTranslator.translateRectInAppWindowToScreen(dirty);
                }
                if (this.mAttachInfo.mScalingRequired) {
                    dirty.inset(-1, -1);
                }
            }
            Rect localDirty = this.mDirty;
            if (!localDirty.isEmpty() && !localDirty.contains(dirty)) {
                this.mAttachInfo.mSetIgnoreDirtyState = true;
                this.mAttachInfo.mIgnoreDirtyState = true;
            }
            localDirty.union(dirty.left, dirty.top, dirty.right, dirty.bottom);
            float appScale = this.mAttachInfo.mApplicationScale;
            boolean intersected = localDirty.intersect(0, 0, (int) ((this.mWidth * appScale) + 0.5f), (int) ((this.mHeight * appScale) + 0.5f));
            if (!intersected) {
                localDirty.setEmpty();
            }
            if (this.mWillDrawSoon) {
                return null;
            }
            if (intersected || this.mIsAnimating) {
                scheduleTraversals();
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStopped(boolean stopped) {
        if (this.mStopped != stopped) {
            this.mStopped = stopped;
            if (!stopped) {
                scheduleTraversals();
            }
        }
    }

    @Override // android.view.ViewParent
    public ViewParent getParent() {
        return null;
    }

    @Override // android.view.ViewParent
    public boolean getChildVisibleRect(View child, Rect r, Point offset) {
        if (child != this.mView) {
            throw new RuntimeException("child is not mine, honest!");
        }
        return r.intersect(0, 0, this.mWidth, this.mHeight);
    }

    @Override // android.view.ViewParent
    public void bringChildToFront(View child) {
    }

    int getHostVisibility() {
        if (this.mAppVisible) {
            return this.mView.getVisibility();
        }
        return 8;
    }

    void disposeResizeBuffer() {
        if (this.mResizeBuffer != null && this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.safelyRun(new Runnable() { // from class: android.view.ViewRootImpl.1
                @Override // java.lang.Runnable
                public void run() {
                    ViewRootImpl.this.mResizeBuffer.destroy();
                    ViewRootImpl.this.mResizeBuffer = null;
                }
            });
        }
    }

    public void requestTransitionStart(LayoutTransition transition) {
        if (this.mPendingTransitions == null || !this.mPendingTransitions.contains(transition)) {
            if (this.mPendingTransitions == null) {
                this.mPendingTransitions = new ArrayList<>();
            }
            this.mPendingTransitions.add(transition);
        }
    }

    void scheduleTraversals() {
        if (!this.mTraversalScheduled) {
            this.mTraversalScheduled = true;
            this.mTraversalBarrier = this.mHandler.getLooper().postSyncBarrier();
            this.mChoreographer.postCallback(2, this.mTraversalRunnable, null);
            scheduleConsumeBatchedInput();
        }
    }

    void unscheduleTraversals() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            this.mChoreographer.removeCallbacks(2, this.mTraversalRunnable, null);
        }
    }

    void doTraversal() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            if (this.mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            Trace.traceBegin(8L, "performTraversals");
            try {
                performTraversals();
                Trace.traceEnd(8L);
                if (this.mProfile) {
                    Debug.stopMethodTracing();
                    this.mProfile = false;
                }
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }
    }

    private void applyKeepScreenOnFlag(WindowManager.LayoutParams params) {
        if (this.mAttachInfo.mKeepScreenOn) {
            params.flags |= 128;
        } else {
            params.flags = (params.flags & (-129)) | (this.mClientWindowLayoutFlags & 128);
        }
    }

    private boolean collectViewAttributes() {
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo.mRecomputeGlobalAttributes) {
            attachInfo.mRecomputeGlobalAttributes = false;
            boolean oldScreenOn = attachInfo.mKeepScreenOn;
            attachInfo.mKeepScreenOn = false;
            attachInfo.mSystemUiVisibility = 0;
            attachInfo.mHasSystemUiListeners = false;
            this.mView.dispatchCollectViewAttributes(attachInfo, 0);
            attachInfo.mSystemUiVisibility &= attachInfo.mDisabledSystemUiVisibility ^ (-1);
            WindowManager.LayoutParams params = this.mWindowAttributes;
            attachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(params);
            if (attachInfo.mKeepScreenOn != oldScreenOn || attachInfo.mSystemUiVisibility != params.subtreeSystemUiVisibility || attachInfo.mHasSystemUiListeners != params.hasSystemUiListeners) {
                applyKeepScreenOnFlag(params);
                params.subtreeSystemUiVisibility = attachInfo.mSystemUiVisibility;
                params.hasSystemUiListeners = attachInfo.mHasSystemUiListeners;
                this.mView.dispatchWindowSystemUiVisiblityChanged(attachInfo.mSystemUiVisibility);
                return true;
            }
            return false;
        }
        return false;
    }

    private int getImpliedSystemUiVisibility(WindowManager.LayoutParams params) {
        int vis = 0;
        if ((params.flags & 67108864) != 0) {
            vis = 0 | 1280;
        }
        if ((params.flags & 134217728) != 0) {
            vis |= 768;
        }
        return vis;
    }

    private boolean measureHierarchy(View host, WindowManager.LayoutParams lp, Resources res, int desiredWindowWidth, int desiredWindowHeight) {
        boolean windowSizeMayChange = false;
        boolean goodMeasure = false;
        if (lp.width == -2) {
            DisplayMetrics packageMetrics = res.getDisplayMetrics();
            res.getValue(R.dimen.config_prefDialogWidth, this.mTmpValue, true);
            int baseSize = 0;
            if (this.mTmpValue.type == 5) {
                baseSize = (int) this.mTmpValue.getDimension(packageMetrics);
            }
            if (baseSize != 0 && desiredWindowWidth > baseSize) {
                int childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
                int childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                if ((host.getMeasuredWidthAndState() & 16777216) == 0) {
                    goodMeasure = true;
                } else {
                    int childWidthMeasureSpec2 = getRootMeasureSpec((baseSize + desiredWindowWidth) / 2, lp.width);
                    performMeasure(childWidthMeasureSpec2, childHeightMeasureSpec);
                    if ((host.getMeasuredWidthAndState() & 16777216) == 0) {
                        goodMeasure = true;
                    }
                }
            }
        }
        if (!goodMeasure) {
            int childWidthMeasureSpec3 = getRootMeasureSpec(desiredWindowWidth, lp.width);
            performMeasure(childWidthMeasureSpec3, getRootMeasureSpec(desiredWindowHeight, lp.height));
            if (this.mWidth != host.getMeasuredWidth() || this.mHeight != host.getMeasuredHeight()) {
                windowSizeMayChange = true;
            }
        }
        return windowSizeMayChange;
    }

    /* JADX WARN: Removed duplicated region for block: B:336:0x0945  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x096a  */
    /* JADX WARN: Removed duplicated region for block: B:381:0x0af9  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x0b44  */
    /* JADX WARN: Removed duplicated region for block: B:394:0x0b63  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void performTraversals() {
        /*
            Method dump skipped, instructions count: 3986
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.performTraversals():void");
    }

    private void handleOutOfResourcesException(Surface.OutOfResourcesException e) {
        Log.e(TAG, "OutOfResourcesException initializing HW surface", e);
        try {
            if (!this.mWindowSession.outOfMemory(this.mWindow) && Process.myUid() != 1000) {
                Slog.w(TAG, "No processes killed for memory; killing self");
                Process.killProcess(Process.myPid());
            }
        } catch (RemoteException e2) {
        }
        this.mLayoutRequested = true;
    }

    private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        Trace.traceBegin(8L, "measure");
        try {
            this.mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            Trace.traceEnd(8L);
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInLayout() {
        return this.mInLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean requestLayoutDuringLayout(View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            return true;
        }
        if (!this.mLayoutRequesters.contains(view)) {
            this.mLayoutRequesters.add(view);
        }
        if (!this.mHandlingLayoutInLayoutRequest) {
            return true;
        }
        return false;
    }

    private void performLayout(WindowManager.LayoutParams lp, int desiredWindowWidth, int desiredWindowHeight) {
        ArrayList<View> validLayoutRequesters;
        this.mLayoutRequested = false;
        this.mScrollMayChange = true;
        this.mInLayout = true;
        View host = this.mView;
        Trace.traceBegin(8L, "layout");
        try {
            host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
            this.mInLayout = false;
            int numViewsRequestingLayout = this.mLayoutRequesters.size();
            if (numViewsRequestingLayout > 0 && (validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, false)) != null) {
                this.mHandlingLayoutInLayoutRequest = true;
                int numValidRequests = validLayoutRequesters.size();
                for (int i = 0; i < numValidRequests; i++) {
                    View view = validLayoutRequesters.get(i);
                    Log.w("View", "requestLayout() improperly called by " + view + " during layout: running second layout pass");
                    view.requestLayout();
                }
                measureHierarchy(host, lp, this.mView.getContext().getResources(), desiredWindowWidth, desiredWindowHeight);
                this.mInLayout = true;
                host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
                this.mHandlingLayoutInLayoutRequest = false;
                final ArrayList<View> validLayoutRequesters2 = getValidLayoutRequesters(this.mLayoutRequesters, true);
                if (validLayoutRequesters2 != null) {
                    getRunQueue().post(new Runnable() { // from class: android.view.ViewRootImpl.2
                        @Override // java.lang.Runnable
                        public void run() {
                            int numValidRequests2 = validLayoutRequesters2.size();
                            for (int i2 = 0; i2 < numValidRequests2; i2++) {
                                View view2 = (View) validLayoutRequesters2.get(i2);
                                Log.w("View", "requestLayout() improperly called by " + view2 + " during second layout pass: posting in next frame");
                                view2.requestLayout();
                            }
                        }
                    });
                }
            }
            this.mInLayout = false;
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private ArrayList<View> getValidLayoutRequesters(ArrayList<View> layoutRequesters, boolean secondLayoutRequests) {
        int numViewsRequestingLayout = layoutRequesters.size();
        ArrayList<View> validLayoutRequesters = null;
        for (int i = 0; i < numViewsRequestingLayout; i++) {
            View view = layoutRequesters.get(i);
            if (view != null && view.mAttachInfo != null && view.mParent != null && (secondLayoutRequests || (view.mPrivateFlags & 4096) == 4096)) {
                boolean gone = false;
                View view2 = view;
                while (true) {
                    View parent = view2;
                    if (parent == null) {
                        break;
                    } else if ((parent.mViewFlags & 12) == 8) {
                        gone = true;
                        break;
                    } else if (parent.mParent instanceof View) {
                        view2 = (View) parent.mParent;
                    } else {
                        view2 = null;
                    }
                }
                if (!gone) {
                    if (validLayoutRequesters == null) {
                        validLayoutRequesters = new ArrayList<>();
                    }
                    validLayoutRequesters.add(view);
                }
            }
        }
        if (!secondLayoutRequests) {
            for (int i2 = 0; i2 < numViewsRequestingLayout; i2++) {
                View view3 = layoutRequesters.get(i2);
                while (true) {
                    View view4 = view3;
                    if (view4 != null && (view4.mPrivateFlags & 4096) != 0) {
                        view4.mPrivateFlags &= -4097;
                        if (view4.mParent instanceof View) {
                            view3 = (View) view4.mParent;
                        } else {
                            view3 = null;
                        }
                    }
                }
            }
        }
        layoutRequesters.clear();
        return validLayoutRequesters;
    }

    @Override // android.view.ViewParent
    public void requestTransparentRegion(View child) {
        checkThread();
        if (this.mView == child) {
            this.mView.mPrivateFlags |= 512;
            this.mWindowAttributesChanged = true;
            this.mWindowAttributesChangesFlag = 0;
            requestLayout();
        }
    }

    private static int getRootMeasureSpec(int windowSize, int rootDimension) {
        int measureSpec;
        switch (rootDimension) {
            case -2:
                measureSpec = View.MeasureSpec.makeMeasureSpec(windowSize, Integer.MIN_VALUE);
                break;
            case -1:
                measureSpec = View.MeasureSpec.makeMeasureSpec(windowSize, 1073741824);
                break;
            default:
                measureSpec = View.MeasureSpec.makeMeasureSpec(rootDimension, 1073741824);
                break;
        }
        return measureSpec;
    }

    @Override // android.view.HardwareRenderer.HardwareDrawCallbacks
    public void onHardwarePreDraw(HardwareCanvas canvas) {
        canvas.translate(0.0f, -this.mHardwareYOffset);
    }

    @Override // android.view.HardwareRenderer.HardwareDrawCallbacks
    public void onHardwarePostDraw(HardwareCanvas canvas) {
        if (this.mResizeBuffer != null) {
            this.mResizePaint.setAlpha(this.mResizeAlpha);
            canvas.drawHardwareLayer(this.mResizeBuffer, 0.0f, this.mHardwareYOffset, this.mResizePaint);
        }
        drawAccessibilityFocusedDrawableIfNeeded(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void outputDisplayList(View view) {
        DisplayList displayList;
        if (this.mAttachInfo != null && this.mAttachInfo.mHardwareCanvas != null && (displayList = view.getDisplayList()) != null) {
            this.mAttachInfo.mHardwareCanvas.outputDisplayList(displayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void profileRendering(boolean enabled) {
        if (this.mProfileRendering) {
            this.mRenderProfilingEnabled = enabled;
            if (this.mRenderProfiler != null) {
                this.mChoreographer.removeFrameCallback(this.mRenderProfiler);
            }
            if (this.mRenderProfilingEnabled) {
                if (this.mRenderProfiler == null) {
                    this.mRenderProfiler = new Choreographer.FrameCallback() { // from class: android.view.ViewRootImpl.3
                        @Override // android.view.Choreographer.FrameCallback
                        public void doFrame(long frameTimeNanos) {
                            ViewRootImpl.this.mDirty.set(0, 0, ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight);
                            ViewRootImpl.this.scheduleTraversals();
                            if (ViewRootImpl.this.mRenderProfilingEnabled) {
                                ViewRootImpl.this.mChoreographer.postFrameCallback(ViewRootImpl.this.mRenderProfiler);
                            }
                        }
                    };
                }
                this.mChoreographer.postFrameCallback(this.mRenderProfiler);
                return;
            }
            this.mRenderProfiler = null;
        }
    }

    private void trackFPS() {
        long nowTime = System.currentTimeMillis();
        if (this.mFpsStartTime < 0) {
            this.mFpsPrevTime = nowTime;
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
            return;
        }
        this.mFpsNumFrames++;
        String thisHash = Integer.toHexString(System.identityHashCode(this));
        long frameTime = nowTime - this.mFpsPrevTime;
        long totalTime = nowTime - this.mFpsStartTime;
        Log.v(TAG, "0x" + thisHash + "\tFrame time:\t" + frameTime);
        this.mFpsPrevTime = nowTime;
        if (totalTime > 1000) {
            float fps = (this.mFpsNumFrames * 1000.0f) / ((float) totalTime);
            Log.v(TAG, "0x" + thisHash + "\tFPS:\t" + fps);
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
        }
    }

    private void performDraw() {
        if (!this.mAttachInfo.mScreenOn && !this.mReportNextDraw) {
            return;
        }
        boolean fullRedrawNeeded = this.mFullRedrawNeeded;
        this.mFullRedrawNeeded = false;
        this.mIsDrawing = true;
        Trace.traceBegin(8L, "draw");
        try {
            draw(fullRedrawNeeded);
            this.mIsDrawing = false;
            Trace.traceEnd(8L);
            if (this.mReportNextDraw) {
                this.mReportNextDraw = false;
                if (this.mSurfaceHolder != null && this.mSurface.isValid()) {
                    this.mSurfaceHolderCallback.surfaceRedrawNeeded(this.mSurfaceHolder);
                    SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                    if (callbacks != null) {
                        for (SurfaceHolder.Callback c : callbacks) {
                            if (c instanceof SurfaceHolder.Callback2) {
                                ((SurfaceHolder.Callback2) c).surfaceRedrawNeeded(this.mSurfaceHolder);
                            }
                        }
                    }
                }
                try {
                    this.mWindowSession.finishDrawing(this.mWindow);
                } catch (RemoteException e) {
                }
            }
        } catch (Throwable th) {
            this.mIsDrawing = false;
            Trace.traceEnd(8L);
            throw th;
        }
    }

    private void draw(boolean fullRedrawNeeded) {
        int yoff;
        Surface surface = this.mSurface;
        if (!surface.isValid()) {
            return;
        }
        if (!sFirstDrawComplete) {
            synchronized (sFirstDrawHandlers) {
                sFirstDrawComplete = true;
                int count = sFirstDrawHandlers.size();
                for (int i = 0; i < count; i++) {
                    this.mHandler.post(sFirstDrawHandlers.get(i));
                }
            }
        }
        scrollToRectOrFocus(null, false);
        View.AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo.mViewScrollChanged) {
            attachInfo.mViewScrollChanged = false;
            attachInfo.mTreeObserver.dispatchOnScrollChanged();
        }
        boolean animating = this.mScroller != null && this.mScroller.computeScrollOffset();
        if (animating) {
            yoff = this.mScroller.getCurrY();
        } else {
            yoff = this.mScrollY;
        }
        if (this.mCurScrollY != yoff) {
            this.mCurScrollY = yoff;
            fullRedrawNeeded = true;
        }
        float appScale = attachInfo.mApplicationScale;
        boolean scalingRequired = attachInfo.mScalingRequired;
        int resizeAlpha = 0;
        if (this.mResizeBuffer != null) {
            long deltaTime = SystemClock.uptimeMillis() - this.mResizeBufferStartTime;
            if (deltaTime < this.mResizeBufferDuration) {
                float amt = ((float) deltaTime) / this.mResizeBufferDuration;
                animating = true;
                resizeAlpha = 255 - ((int) (mResizeInterpolator.getInterpolation(amt) * 255.0f));
            } else {
                disposeResizeBuffer();
            }
        }
        Rect dirty = this.mDirty;
        if (this.mSurfaceHolder != null) {
            dirty.setEmpty();
            if (animating) {
                if (this.mScroller != null) {
                    this.mScroller.abortAnimation();
                }
                disposeResizeBuffer();
                return;
            }
            return;
        }
        if (fullRedrawNeeded) {
            attachInfo.mIgnoreDirtyState = true;
            dirty.set(0, 0, (int) ((this.mWidth * appScale) + 0.5f), (int) ((this.mHeight * appScale) + 0.5f));
        }
        invalidateDisplayLists();
        attachInfo.mTreeObserver.dispatchOnDraw();
        if (!dirty.isEmpty() || this.mIsAnimating) {
            if (attachInfo.mHardwareRenderer != null && attachInfo.mHardwareRenderer.isEnabled()) {
                this.mIsAnimating = false;
                this.mHardwareYOffset = yoff;
                this.mResizeAlpha = resizeAlpha;
                this.mCurrentDirty.set(dirty);
                dirty.setEmpty();
                attachInfo.mHardwareRenderer.draw(this.mView, attachInfo, this, animating ? null : this.mCurrentDirty);
            } else if (attachInfo.mHardwareRenderer != null && !attachInfo.mHardwareRenderer.isEnabled() && attachInfo.mHardwareRenderer.isRequested()) {
                try {
                    attachInfo.mHardwareRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mHolder.getSurface());
                    this.mFullRedrawNeeded = true;
                    scheduleTraversals();
                    return;
                } catch (Surface.OutOfResourcesException e) {
                    handleOutOfResourcesException(e);
                    return;
                }
            } else if (!drawSoftware(surface, attachInfo, yoff, scalingRequired, dirty)) {
                return;
            }
        }
        if (animating) {
            this.mFullRedrawNeeded = true;
            scheduleTraversals();
        }
    }

    private boolean drawSoftware(Surface surface, View.AttachInfo attachInfo, int yoff, boolean scalingRequired, Rect dirty) {
        try {
            int left = dirty.left;
            int top = dirty.top;
            int right = dirty.right;
            int bottom = dirty.bottom;
            Canvas canvas = this.mSurface.lockCanvas(dirty);
            if (left != dirty.left || top != dirty.top || right != dirty.right || bottom != dirty.bottom) {
                attachInfo.mIgnoreDirtyState = true;
            }
            canvas.setDensity(this.mDensity);
            try {
                if (!canvas.isOpaque() || yoff != 0) {
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                }
                dirty.setEmpty();
                this.mIsAnimating = false;
                attachInfo.mDrawingTime = SystemClock.uptimeMillis();
                this.mView.mPrivateFlags |= 32;
                canvas.translate(0.0f, -yoff);
                if (this.mTranslator != null) {
                    this.mTranslator.translateCanvas(canvas);
                }
                canvas.setScreenDensity(scalingRequired ? this.mNoncompatDensity : 0);
                attachInfo.mSetIgnoreDirtyState = false;
                this.mView.draw(canvas);
                drawAccessibilityFocusedDrawableIfNeeded(canvas);
                if (!attachInfo.mSetIgnoreDirtyState) {
                    attachInfo.mIgnoreDirtyState = false;
                }
                try {
                    surface.unlockCanvasAndPost(canvas);
                    return true;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Could not unlock surface", e);
                    this.mLayoutRequested = true;
                    return false;
                }
            } catch (Throwable th) {
                try {
                    surface.unlockCanvasAndPost(canvas);
                    throw th;
                } catch (IllegalArgumentException e2) {
                    Log.e(TAG, "Could not unlock surface", e2);
                    this.mLayoutRequested = true;
                    return false;
                }
            }
        } catch (Surface.OutOfResourcesException e3) {
            handleOutOfResourcesException(e3);
            return false;
        } catch (IllegalArgumentException e4) {
            Log.e(TAG, "Could not lock surface", e4);
            this.mLayoutRequested = true;
            return false;
        }
    }

    private void drawAccessibilityFocusedDrawableIfNeeded(Canvas canvas) {
        Drawable drawable;
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mView.mContext);
        if (!manager.isEnabled() || !manager.isTouchExplorationEnabled() || this.mAccessibilityFocusedHost == null || this.mAccessibilityFocusedHost.mAttachInfo == null || (drawable = getAccessibilityFocusedDrawable()) == null) {
            return;
        }
        AccessibilityNodeProvider provider = this.mAccessibilityFocusedHost.getAccessibilityNodeProvider();
        Rect bounds = this.mView.mAttachInfo.mTmpInvalRect;
        if (provider == null) {
            this.mAccessibilityFocusedHost.getBoundsOnScreen(bounds);
        } else if (this.mAccessibilityFocusedVirtualView == null) {
            return;
        } else {
            this.mAccessibilityFocusedVirtualView.getBoundsInScreen(bounds);
        }
        bounds.offset(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
        bounds.intersect(0, 0, this.mAttachInfo.mViewRootImpl.mWidth, this.mAttachInfo.mViewRootImpl.mHeight);
        drawable.setBounds(bounds);
        drawable.draw(canvas);
    }

    private Drawable getAccessibilityFocusedDrawable() {
        if (this.mAttachInfo != null) {
            if (this.mAttachInfo.mAccessibilityFocusDrawable == null) {
                TypedValue value = new TypedValue();
                boolean resolved = this.mView.mContext.getTheme().resolveAttribute(R.attr.accessibilityFocusedDrawable, value, true);
                if (resolved) {
                    this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getResources().getDrawable(value.resourceId);
                }
            }
            return this.mAttachInfo.mAccessibilityFocusDrawable;
        }
        return null;
    }

    void invalidateDisplayLists() {
        ArrayList<DisplayList> displayLists = this.mDisplayLists;
        int count = displayLists.size();
        for (int i = 0; i < count; i++) {
            DisplayList displayList = displayLists.get(i);
            if (displayList.isDirty()) {
                displayList.reset();
            }
        }
        displayLists.clear();
    }

    public void setDrawDuringWindowsAnimating(boolean value) {
        this.mDrawDuringWindowsAnimating = value;
        if (value) {
            handleDispatchDoneAnimating();
        }
    }

    boolean scrollToRectOrFocus(Rect rectangle, boolean immediate) {
        View.AttachInfo attachInfo = this.mAttachInfo;
        Rect ci = attachInfo.mContentInsets;
        Rect vi = attachInfo.mVisibleInsets;
        int scrollY = 0;
        boolean handled = false;
        if (vi.left > ci.left || vi.top > ci.top || vi.right > ci.right || vi.bottom > ci.bottom) {
            scrollY = this.mScrollY;
            View focus = this.mView.findFocus();
            if (focus == null) {
                return false;
            }
            View lastScrolledFocus = this.mLastScrolledFocus != null ? this.mLastScrolledFocus.get() : null;
            if (focus != lastScrolledFocus) {
                rectangle = null;
            }
            if (focus != lastScrolledFocus || this.mScrollMayChange || rectangle != null) {
                this.mLastScrolledFocus = new WeakReference<>(focus);
                this.mScrollMayChange = false;
                if (focus.getGlobalVisibleRect(this.mVisRect, null)) {
                    if (rectangle == null) {
                        focus.getFocusedRect(this.mTempRect);
                        if (this.mView instanceof ViewGroup) {
                            ((ViewGroup) this.mView).offsetDescendantRectToMyCoords(focus, this.mTempRect);
                        }
                    } else {
                        this.mTempRect.set(rectangle);
                    }
                    if (this.mTempRect.intersect(this.mVisRect)) {
                        if (this.mTempRect.height() <= (this.mView.getHeight() - vi.top) - vi.bottom) {
                            if (this.mTempRect.top - scrollY < vi.top) {
                                scrollY -= vi.top - (this.mTempRect.top - scrollY);
                            } else if (this.mTempRect.bottom - scrollY > this.mView.getHeight() - vi.bottom) {
                                scrollY += (this.mTempRect.bottom - scrollY) - (this.mView.getHeight() - vi.bottom);
                            }
                        }
                        handled = true;
                    }
                }
            }
        }
        if (scrollY != this.mScrollY) {
            if (!immediate && this.mResizeBuffer == null) {
                if (this.mScroller == null) {
                    this.mScroller = new Scroller(this.mView.getContext());
                }
                this.mScroller.startScroll(0, this.mScrollY, 0, scrollY - this.mScrollY);
            } else if (this.mScroller != null) {
                this.mScroller.abortAnimation();
            }
            this.mScrollY = scrollY;
        }
        return handled;
    }

    public View getAccessibilityFocusedHost() {
        return this.mAccessibilityFocusedHost;
    }

    public AccessibilityNodeInfo getAccessibilityFocusedVirtualView() {
        return this.mAccessibilityFocusedVirtualView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAccessibilityFocus(View view, AccessibilityNodeInfo node) {
        if (this.mAccessibilityFocusedVirtualView != null) {
            AccessibilityNodeInfo focusNode = this.mAccessibilityFocusedVirtualView;
            View focusHost = this.mAccessibilityFocusedHost;
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            focusHost.clearAccessibilityFocusNoCallbacks();
            AccessibilityNodeProvider provider = focusHost.getAccessibilityNodeProvider();
            if (provider != null) {
                focusNode.getBoundsInParent(this.mTempRect);
                focusHost.invalidate(this.mTempRect);
                int virtualNodeId = AccessibilityNodeInfo.getVirtualDescendantId(focusNode.getSourceNodeId());
                provider.performAction(virtualNodeId, 128, null);
            }
            focusNode.recycle();
        }
        if (this.mAccessibilityFocusedHost != null) {
            this.mAccessibilityFocusedHost.clearAccessibilityFocusNoCallbacks();
        }
        this.mAccessibilityFocusedHost = view;
        this.mAccessibilityFocusedVirtualView = node;
    }

    @Override // android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void clearChildFocus(View child) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public ViewParent getParentForAccessibility() {
        return null;
    }

    @Override // android.view.ViewParent
    public void focusableViewAvailable(View v) {
        checkThread();
        if (this.mView != null) {
            if (!this.mView.hasFocus()) {
                v.requestFocus();
                return;
            }
            View focused = this.mView.findFocus();
            if (focused instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) focused;
                if (group.getDescendantFocusability() == 262144 && isViewDescendantOf(v, focused)) {
                    v.requestFocus();
                }
            }
        }
    }

    @Override // android.view.ViewParent
    public void recomputeViewAttributes(View child) {
        checkThread();
        if (this.mView == child) {
            this.mAttachInfo.mRecomputeGlobalAttributes = true;
            if (!this.mWillDrawSoon) {
                scheduleTraversals();
            }
        }
    }

    void dispatchDetachedFromWindow() {
        if (this.mView != null && this.mView.mAttachInfo != null) {
            if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                this.mAttachInfo.mHardwareRenderer.validate();
            }
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);
            this.mView.dispatchDetachedFromWindow();
        }
        this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        removeSendWindowContentChangedCallback();
        destroyHardwareRenderer();
        setAccessibilityFocus(null, null);
        this.mView.assignParent(null);
        this.mView = null;
        this.mAttachInfo.mRootView = null;
        this.mAttachInfo.mSurface = null;
        this.mSurface.release();
        if (this.mInputQueueCallback != null && this.mInputQueue != null) {
            this.mInputQueueCallback.onInputQueueDestroyed(this.mInputQueue);
            this.mInputQueue.dispose();
            this.mInputQueueCallback = null;
            this.mInputQueue = null;
        }
        if (this.mInputEventReceiver != null) {
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        try {
            this.mWindowSession.remove(this.mWindow);
        } catch (RemoteException e) {
        }
        if (this.mInputChannel != null) {
            this.mInputChannel.dispose();
            this.mInputChannel = null;
        }
        unscheduleTraversals();
    }

    void updateConfiguration(Configuration config, boolean force) {
        CompatibilityInfo ci = this.mDisplayAdjustments.getCompatibilityInfo();
        if (!ci.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            config = new Configuration(config);
            ci.applyToConfiguration(this.mNoncompatDensity, config);
        }
        synchronized (sConfigCallbacks) {
            for (int i = sConfigCallbacks.size() - 1; i >= 0; i--) {
                sConfigCallbacks.get(i).onConfigurationChanged(config);
            }
        }
        if (this.mView != null) {
            Configuration config2 = this.mView.getResources().getConfiguration();
            if (force || this.mLastConfiguration.diff(config2) != 0) {
                int lastLayoutDirection = this.mLastConfiguration.getLayoutDirection();
                int currentLayoutDirection = config2.getLayoutDirection();
                this.mLastConfiguration.setTo(config2);
                if (lastLayoutDirection != currentLayoutDirection && this.mViewLayoutDirectionInitial == 2) {
                    this.mView.setLayoutDirection(currentLayoutDirection);
                }
                this.mView.dispatchConfigurationChanged(config2);
            }
        }
        this.mFlipControllerFallbackKeys = this.mContext.getResources().getBoolean(R.bool.flip_controller_fallback_keys);
    }

    public static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                forceLayout(group.getChildAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$ViewRootHandler.class */
    public final class ViewRootHandler extends Handler {
        ViewRootHandler() {
        }

        @Override // android.os.Handler
        public String getMessageName(Message message) {
            switch (message.what) {
                case 1:
                    return "MSG_INVALIDATE";
                case 2:
                    return "MSG_INVALIDATE_RECT";
                case 3:
                    return "MSG_DIE";
                case 4:
                    return "MSG_RESIZED";
                case 5:
                    return "MSG_RESIZED_REPORT";
                case 6:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case 7:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case 8:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case 9:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case 10:
                case 23:
                default:
                    return super.getMessageName(message);
                case 11:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case 12:
                    return "MSG_FINISH_INPUT_CONNECTION";
                case 13:
                    return "MSG_CHECK_FOCUS";
                case 14:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case 15:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case 16:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case 17:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case 18:
                    return "MSG_UPDATE_CONFIGURATION";
                case 19:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case 20:
                    return "MSG_DISPATCH_SCREEN_STATE";
                case 21:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case 22:
                    return "MSG_DISPATCH_DONE_ANIMATING";
                case 24:
                    return "MSG_WINDOW_MOVED";
                case 25:
                    return "MSG_FLUSH_LAYER_UPDATES";
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ((View) msg.obj).invalidate();
                    return;
                case 2:
                    View.AttachInfo.InvalidateInfo info = (View.AttachInfo.InvalidateInfo) msg.obj;
                    info.target.invalidate(info.left, info.top, info.right, info.bottom);
                    info.recycle();
                    return;
                case 3:
                    ViewRootImpl.this.doDie();
                    return;
                case 4:
                    SomeArgs args = (SomeArgs) msg.obj;
                    if (ViewRootImpl.this.mWinFrame.equals(args.arg1) && ViewRootImpl.this.mPendingOverscanInsets.equals(args.arg5) && ViewRootImpl.this.mPendingContentInsets.equals(args.arg2) && ViewRootImpl.this.mPendingVisibleInsets.equals(args.arg3) && args.arg4 == null) {
                        return;
                    }
                    break;
                case 5:
                    break;
                case 6:
                    if (ViewRootImpl.this.mAdded) {
                        boolean hasWindowFocus = msg.arg1 != 0;
                        ViewRootImpl.this.mAttachInfo.mHasWindowFocus = hasWindowFocus;
                        ViewRootImpl.this.profileRendering(hasWindowFocus);
                        if (hasWindowFocus) {
                            boolean inTouchMode = msg.arg2 != 0;
                            ViewRootImpl.this.ensureTouchModeLocally(inTouchMode);
                            if (ViewRootImpl.this.mAttachInfo.mHardwareRenderer != null && ViewRootImpl.this.mSurface.isValid()) {
                                ViewRootImpl.this.mFullRedrawNeeded = true;
                                try {
                                    ViewRootImpl.this.mAttachInfo.mHardwareRenderer.initializeIfNeeded(ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight, ViewRootImpl.this.mHolder.getSurface());
                                } catch (Surface.OutOfResourcesException e) {
                                    Log.e(ViewRootImpl.TAG, "OutOfResourcesException locking surface", e);
                                    try {
                                        if (!ViewRootImpl.this.mWindowSession.outOfMemory(ViewRootImpl.this.mWindow)) {
                                            Slog.w(ViewRootImpl.TAG, "No processes killed for memory; killing self");
                                            Process.killProcess(Process.myPid());
                                        }
                                    } catch (RemoteException e2) {
                                    }
                                    sendMessageDelayed(obtainMessage(msg.what, msg.arg1, msg.arg2), 500L);
                                    return;
                                }
                            }
                        }
                        ViewRootImpl.this.mLastWasImTarget = WindowManager.LayoutParams.mayUseInputMethod(ViewRootImpl.this.mWindowAttributes.flags);
                        InputMethodManager imm = InputMethodManager.peekInstance();
                        if (ViewRootImpl.this.mView != null) {
                            if (hasWindowFocus && imm != null && ViewRootImpl.this.mLastWasImTarget && !ViewRootImpl.this.isInLocalFocusMode()) {
                                imm.startGettingWindowFocus(ViewRootImpl.this.mView);
                            }
                            ViewRootImpl.this.mAttachInfo.mKeyDispatchState.reset();
                            ViewRootImpl.this.mView.dispatchWindowFocusChanged(hasWindowFocus);
                            ViewRootImpl.this.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(hasWindowFocus);
                        }
                        if (hasWindowFocus) {
                            if (imm != null && ViewRootImpl.this.mLastWasImTarget && !ViewRootImpl.this.isInLocalFocusMode()) {
                                imm.onWindowFocus(ViewRootImpl.this.mView, ViewRootImpl.this.mView.findFocus(), ViewRootImpl.this.mWindowAttributes.softInputMode, !ViewRootImpl.this.mHasHadWindowFocus, ViewRootImpl.this.mWindowAttributes.flags);
                            }
                            ViewRootImpl.this.mWindowAttributes.softInputMode &= -257;
                            ((WindowManager.LayoutParams) ViewRootImpl.this.mView.getLayoutParams()).softInputMode &= -257;
                            ViewRootImpl.this.mHasHadWindowFocus = true;
                        }
                        ViewRootImpl.this.setAccessibilityFocus(null, null);
                        if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mAccessibilityManager.isEnabled() && hasWindowFocus) {
                            ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                            return;
                        }
                        return;
                    }
                    return;
                case 7:
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) msg.obj, null, 0, true);
                    return;
                case 8:
                    ViewRootImpl.this.handleAppVisibility(msg.arg1 != 0);
                    return;
                case 9:
                    ViewRootImpl.this.handleGetNewSurface();
                    return;
                case 10:
                default:
                    return;
                case 11:
                    KeyEvent event = (KeyEvent) msg.obj;
                    if ((event.getFlags() & 8) != 0) {
                        event = KeyEvent.changeFlags(event, event.getFlags() & (-9));
                    }
                    ViewRootImpl.this.enqueueInputEvent(event, null, 1, true);
                    return;
                case 12:
                    InputMethodManager imm2 = InputMethodManager.peekInstance();
                    if (imm2 != null) {
                        imm2.reportFinishInputConnection((InputConnection) msg.obj);
                        return;
                    }
                    return;
                case 13:
                    InputMethodManager imm3 = InputMethodManager.peekInstance();
                    if (imm3 != null) {
                        imm3.checkFocus();
                        return;
                    }
                    return;
                case 14:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.mView.onCloseSystemDialogs((String) msg.obj);
                        return;
                    }
                    return;
                case 15:
                case 16:
                    DragEvent event2 = (DragEvent) msg.obj;
                    event2.mLocalState = ViewRootImpl.this.mLocalDragState;
                    ViewRootImpl.this.handleDragEvent(event2);
                    return;
                case 17:
                    ViewRootImpl.this.handleDispatchSystemUiVisibilityChanged((SystemUiVisibilityInfo) msg.obj);
                    return;
                case 18:
                    Configuration config = (Configuration) msg.obj;
                    if (config.isOtherSeqNewer(ViewRootImpl.this.mLastConfiguration)) {
                        config = ViewRootImpl.this.mLastConfiguration;
                    }
                    ViewRootImpl.this.updateConfiguration(config, false);
                    return;
                case 19:
                    ViewRootImpl.this.mProcessInputEventsScheduled = false;
                    ViewRootImpl.this.doProcessInputEvents();
                    return;
                case 20:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.handleScreenStateChange(msg.arg1 == 1);
                        return;
                    }
                    return;
                case 21:
                    ViewRootImpl.this.setAccessibilityFocus(null, null);
                    return;
                case 22:
                    ViewRootImpl.this.handleDispatchDoneAnimating();
                    return;
                case 23:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.invalidateWorld(ViewRootImpl.this.mView);
                        return;
                    }
                    return;
                case 24:
                    if (ViewRootImpl.this.mAdded) {
                        int w = ViewRootImpl.this.mWinFrame.width();
                        int h = ViewRootImpl.this.mWinFrame.height();
                        int l = msg.arg1;
                        int t = msg.arg2;
                        ViewRootImpl.this.mWinFrame.left = l;
                        ViewRootImpl.this.mWinFrame.right = l + w;
                        ViewRootImpl.this.mWinFrame.top = t;
                        ViewRootImpl.this.mWinFrame.bottom = t + h;
                        if (ViewRootImpl.this.mView != null) {
                            ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
                        }
                        ViewRootImpl.this.requestLayout();
                        return;
                    }
                    return;
                case 25:
                    ViewRootImpl.this.flushHardwareLayerUpdates();
                    return;
            }
            if (ViewRootImpl.this.mAdded) {
                SomeArgs args2 = (SomeArgs) msg.obj;
                Configuration config2 = (Configuration) args2.arg4;
                if (config2 != null) {
                    ViewRootImpl.this.updateConfiguration(config2, false);
                }
                ViewRootImpl.this.mWinFrame.set((Rect) args2.arg1);
                ViewRootImpl.this.mPendingOverscanInsets.set((Rect) args2.arg5);
                ViewRootImpl.this.mPendingContentInsets.set((Rect) args2.arg2);
                ViewRootImpl.this.mPendingVisibleInsets.set((Rect) args2.arg3);
                args2.recycle();
                if (msg.what == 5) {
                    ViewRootImpl.this.mReportNextDraw = true;
                }
                if (ViewRootImpl.this.mView != null) {
                    ViewRootImpl.forceLayout(ViewRootImpl.this.mView);
                }
                ViewRootImpl.this.requestLayout();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean ensureTouchMode(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return false;
        }
        try {
            if (!isInLocalFocusMode()) {
                this.mWindowSession.setInTouchMode(inTouchMode);
            }
            return ensureTouchModeLocally(inTouchMode);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean ensureTouchModeLocally(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return false;
        }
        this.mAttachInfo.mInTouchMode = inTouchMode;
        this.mAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(inTouchMode);
        return inTouchMode ? enterTouchMode() : leaveTouchMode();
    }

    private boolean enterTouchMode() {
        View focused;
        if (this.mView != null && this.mView.hasFocus() && (focused = this.mView.findFocus()) != null && !focused.isFocusableInTouchMode()) {
            ViewGroup ancestorToTakeFocus = findAncestorToTakeFocusInTouchMode(focused);
            if (ancestorToTakeFocus != null) {
                return ancestorToTakeFocus.requestFocus();
            }
            focused.clearFocusInternal(true, false);
            return true;
        }
        return false;
    }

    private static ViewGroup findAncestorToTakeFocusInTouchMode(View focused) {
        ViewParent parent = focused.getParent();
        while (true) {
            ViewParent parent2 = parent;
            if (parent2 instanceof ViewGroup) {
                ViewGroup vgParent = (ViewGroup) parent2;
                if (vgParent.getDescendantFocusability() == 262144 && vgParent.isFocusableInTouchMode()) {
                    return vgParent;
                }
                if (vgParent.isRootNamespace()) {
                    return null;
                }
                parent = vgParent.getParent();
            } else {
                return null;
            }
        }
    }

    private boolean leaveTouchMode() {
        if (this.mView != null) {
            if (this.mView.hasFocus()) {
                View focusedView = this.mView.findFocus();
                if (!(focusedView instanceof ViewGroup) || ((ViewGroup) focusedView).getDescendantFocusability() != 262144) {
                    return false;
                }
            }
            View focused = focusSearch(null, 130);
            if (focused != null) {
                return focused.requestFocus(130);
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$InputStage.class */
    public abstract class InputStage {
        private final InputStage mNext;
        protected static final int FORWARD = 0;
        protected static final int FINISH_HANDLED = 1;
        protected static final int FINISH_NOT_HANDLED = 2;

        public InputStage(InputStage next) {
            this.mNext = next;
        }

        public final void deliver(QueuedInputEvent q) {
            if ((q.mFlags & 4) != 0) {
                forward(q);
            } else if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                Slog.w(ViewRootImpl.TAG, "Dropping event due to root view being removed: " + q.mEvent);
                finish(q, false);
            } else if (!ViewRootImpl.this.mAttachInfo.mHasWindowFocus && !q.mEvent.isFromSource(2) && !ViewRootImpl.isTerminalInputEvent(q.mEvent)) {
                Slog.w(ViewRootImpl.TAG, "Dropping event due to no window focus: " + q.mEvent);
                finish(q, false);
            } else {
                apply(q, onProcess(q));
            }
        }

        protected void finish(QueuedInputEvent q, boolean handled) {
            q.mFlags |= 4;
            if (handled) {
                q.mFlags |= 8;
            }
            forward(q);
        }

        protected void forward(QueuedInputEvent q) {
            onDeliverToNext(q);
        }

        protected void apply(QueuedInputEvent q, int result) {
            if (result == 0) {
                forward(q);
            } else if (result == 1) {
                finish(q, true);
            } else if (result == 2) {
                finish(q, false);
            } else {
                throw new IllegalArgumentException("Invalid result: " + result);
            }
        }

        protected int onProcess(QueuedInputEvent q) {
            return 0;
        }

        protected void onDeliverToNext(QueuedInputEvent q) {
            if (this.mNext == null) {
                ViewRootImpl.this.finishInputEvent(q);
            } else {
                this.mNext.deliver(q);
            }
        }
    }

    /* loaded from: ViewRootImpl$AsyncInputStage.class */
    abstract class AsyncInputStage extends InputStage {
        private final String mTraceCounter;
        private QueuedInputEvent mQueueHead;
        private QueuedInputEvent mQueueTail;
        private int mQueueLength;
        protected static final int DEFER = 3;

        public AsyncInputStage(InputStage next, String traceCounter) {
            super(next);
            this.mTraceCounter = traceCounter;
        }

        protected void defer(QueuedInputEvent q) {
            q.mFlags |= 2;
            enqueue(q);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void forward(QueuedInputEvent q) {
            q.mFlags &= -3;
            QueuedInputEvent curr = this.mQueueHead;
            if (curr == null) {
                super.forward(q);
                return;
            }
            int deviceId = q.mEvent.getDeviceId();
            QueuedInputEvent prev = null;
            boolean blocked = false;
            while (curr != null && curr != q) {
                if (!blocked && deviceId == curr.mEvent.getDeviceId()) {
                    blocked = true;
                }
                prev = curr;
                curr = curr.mNext;
            }
            if (blocked) {
                if (curr == null) {
                    enqueue(q);
                    return;
                }
                return;
            }
            if (curr != null) {
                curr = curr.mNext;
                dequeue(q, prev);
            }
            super.forward(q);
            while (curr != null) {
                if (deviceId == curr.mEvent.getDeviceId()) {
                    if ((curr.mFlags & 2) == 0) {
                        QueuedInputEvent next = curr.mNext;
                        dequeue(curr, prev);
                        super.forward(curr);
                        curr = next;
                    } else {
                        return;
                    }
                } else {
                    prev = curr;
                    curr = curr.mNext;
                }
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void apply(QueuedInputEvent q, int result) {
            if (result == 3) {
                defer(q);
            } else {
                super.apply(q, result);
            }
        }

        private void enqueue(QueuedInputEvent q) {
            if (this.mQueueTail == null) {
                this.mQueueHead = q;
                this.mQueueTail = q;
            } else {
                this.mQueueTail.mNext = q;
                this.mQueueTail = q;
            }
            this.mQueueLength++;
            Trace.traceCounter(4L, this.mTraceCounter, this.mQueueLength);
        }

        private void dequeue(QueuedInputEvent q, QueuedInputEvent prev) {
            if (prev == null) {
                this.mQueueHead = q.mNext;
            } else {
                prev.mNext = q.mNext;
            }
            if (this.mQueueTail == q) {
                this.mQueueTail = prev;
            }
            q.mNext = null;
            this.mQueueLength--;
            Trace.traceCounter(4L, this.mTraceCounter, this.mQueueLength);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$NativePreImeInputStage.class */
    public final class NativePreImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePreImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (ViewRootImpl.this.mInputQueue != null && (q.mEvent instanceof KeyEvent)) {
                ViewRootImpl.this.mInputQueue.sendInputEvent(q.mEvent, q, true, this);
                return 3;
            }
            return 0;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$ViewPreImeInputStage.class */
    public final class ViewPreImeInputStage extends InputStage {
        public ViewPreImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (ViewRootImpl.this.mView.dispatchKeyEventPreIme(event)) {
                return 1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$ImeInputStage.class */
    public final class ImeInputStage extends AsyncInputStage implements InputMethodManager.FinishedInputEventCallback {
        public ImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            InputMethodManager imm;
            if (ViewRootImpl.this.mLastWasImTarget && !ViewRootImpl.this.isInLocalFocusMode() && (imm = InputMethodManager.peekInstance()) != null) {
                InputEvent event = q.mEvent;
                int result = imm.dispatchInputEvent(event, q, this, ViewRootImpl.this.mHandler);
                if (result == 1) {
                    return 1;
                }
                if (result == 0) {
                    return 2;
                }
                return 3;
            }
            return 0;
        }

        @Override // android.view.inputmethod.InputMethodManager.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$EarlyPostImeInputStage.class */
    public final class EarlyPostImeInputStage extends InputStage {
        public EarlyPostImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            int source = q.mEvent.getSource();
            if ((source & 2) != 0) {
                return processPointerEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (ViewRootImpl.this.checkForLeavingTouchModeAndConsume(event)) {
                return 1;
            }
            ViewRootImpl.this.mFallbackEventHandler.preDispatchKeyEvent(event);
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            if (ViewRootImpl.this.mTranslator != null) {
                ViewRootImpl.this.mTranslator.translateEventInScreenToAppWindow(event);
            }
            int action = event.getAction();
            if (action == 0 || action == 8) {
                ViewRootImpl.this.ensureTouchMode(true);
            }
            if (ViewRootImpl.this.mCurScrollY != 0) {
                event.offsetLocation(0.0f, ViewRootImpl.this.mCurScrollY);
            }
            if (event.isTouchEvent()) {
                ViewRootImpl.this.mLastTouchPoint.x = event.getRawX();
                ViewRootImpl.this.mLastTouchPoint.y = event.getRawY();
                return 0;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$NativePostImeInputStage.class */
    public final class NativePostImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePostImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (ViewRootImpl.this.mInputQueue != null) {
                ViewRootImpl.this.mInputQueue.sendInputEvent(q.mEvent, q, false, this);
                return 3;
            }
            return 0;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$ViewPostImeInputStage.class */
    public final class ViewPostImeInputStage extends InputStage {
        public ViewPostImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            ViewRootImpl.this.handleDispatchDoneAnimating();
            int source = q.mEvent.getSource();
            if ((source & 2) != 0) {
                return processPointerEvent(q);
            }
            if ((source & 4) != 0) {
                return processTrackballEvent(q);
            }
            return processGenericMotionEvent(q);
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (event.getAction() != 1) {
                ViewRootImpl.this.handleDispatchDoneAnimating();
            }
            if (ViewRootImpl.this.mView.dispatchKeyEvent(event)) {
                return 1;
            }
            if ((event.getAction() == 0 && event.isCtrlPressed() && event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(event.getKeyCode()) && ViewRootImpl.this.mView.dispatchKeyShortcutEvent(event)) || ViewRootImpl.this.mFallbackEventHandler.dispatchKeyEvent(event)) {
                return 1;
            }
            if (event.getAction() == 0) {
                int direction = 0;
                switch (event.getKeyCode()) {
                    case 19:
                        if (event.hasNoModifiers()) {
                            direction = 33;
                            break;
                        }
                        break;
                    case 20:
                        if (event.hasNoModifiers()) {
                            direction = 130;
                            break;
                        }
                        break;
                    case 21:
                        if (event.hasNoModifiers()) {
                            direction = 17;
                            break;
                        }
                        break;
                    case 22:
                        if (event.hasNoModifiers()) {
                            direction = 66;
                            break;
                        }
                        break;
                    case 61:
                        if (event.hasNoModifiers()) {
                            direction = 2;
                            break;
                        } else if (event.hasModifiers(1)) {
                            direction = 1;
                            break;
                        }
                        break;
                }
                if (direction != 0) {
                    View focused = ViewRootImpl.this.mView.findFocus();
                    if (focused != null) {
                        View v = focused.focusSearch(direction);
                        if (v != null && v != focused) {
                            focused.getFocusedRect(ViewRootImpl.this.mTempRect);
                            if (ViewRootImpl.this.mView instanceof ViewGroup) {
                                ((ViewGroup) ViewRootImpl.this.mView).offsetDescendantRectToMyCoords(focused, ViewRootImpl.this.mTempRect);
                                ((ViewGroup) ViewRootImpl.this.mView).offsetRectIntoDescendantCoords(v, ViewRootImpl.this.mTempRect);
                            }
                            if (v.requestFocus(direction, ViewRootImpl.this.mTempRect)) {
                                ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                                return 1;
                            }
                        }
                        if (ViewRootImpl.this.mView.dispatchUnhandledMove(focused, direction)) {
                            return 1;
                        }
                        return 0;
                    }
                    View v2 = ViewRootImpl.this.focusSearch(null, direction);
                    if (v2 != null && v2.requestFocus(direction)) {
                        return 1;
                    }
                    return 0;
                }
                return 0;
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            if (ViewRootImpl.this.mView.dispatchPointerEvent(event)) {
                return 1;
            }
            return 0;
        }

        private int processTrackballEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            if (ViewRootImpl.this.mView.dispatchTrackballEvent(event)) {
                return 1;
            }
            return 0;
        }

        private int processGenericMotionEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            if (ViewRootImpl.this.mView.dispatchGenericMotionEvent(event)) {
                return 1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$SyntheticInputStage.class */
    public final class SyntheticInputStage extends InputStage {
        private final SyntheticTrackballHandler mTrackball;
        private final SyntheticJoystickHandler mJoystick;
        private final SyntheticTouchNavigationHandler mTouchNavigation;
        private final SyntheticKeyHandler mKeys;

        public SyntheticInputStage() {
            super(null);
            this.mTrackball = new SyntheticTrackballHandler();
            this.mJoystick = new SyntheticJoystickHandler();
            this.mTouchNavigation = new SyntheticTouchNavigationHandler();
            this.mKeys = new SyntheticKeyHandler();
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            q.mFlags |= 16;
            if (q.mEvent instanceof MotionEvent) {
                MotionEvent event = (MotionEvent) q.mEvent;
                int source = event.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.process(event);
                    return 1;
                } else if ((source & 16) != 0) {
                    this.mJoystick.process(event);
                    return 1;
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.process(event);
                    return 1;
                } else {
                    return 0;
                }
            } else if ((q.mEvent instanceof KeyEvent) && this.mKeys.process((KeyEvent) q.mEvent)) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent q) {
            if ((q.mFlags & 16) == 0 && (q.mEvent instanceof MotionEvent)) {
                MotionEvent event = (MotionEvent) q.mEvent;
                int source = event.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.cancel(event);
                } else if ((source & 16) != 0) {
                    this.mJoystick.cancel(event);
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.cancel(event);
                }
            }
            super.onDeliverToNext(q);
        }
    }

    /* loaded from: ViewRootImpl$SyntheticTrackballHandler.class */
    final class SyntheticTrackballHandler {
        private final TrackballAxis mX = new TrackballAxis();
        private final TrackballAxis mY = new TrackballAxis();
        private long mLastTime;

        SyntheticTrackballHandler() {
        }

        public void process(MotionEvent event) {
            long curTime = SystemClock.uptimeMillis();
            if (this.mLastTime + 250 < curTime) {
                this.mX.reset(0);
                this.mY.reset(0);
                this.mLastTime = curTime;
            }
            int action = event.getAction();
            int metaState = event.getMetaState();
            switch (action) {
                case 0:
                    this.mX.reset(2);
                    this.mY.reset(2);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 0, 23, 0, metaState, -1, 0, 1024, 257));
                    break;
                case 1:
                    this.mX.reset(2);
                    this.mY.reset(2);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 1, 23, 0, metaState, -1, 0, 1024, 257));
                    break;
            }
            float xOff = this.mX.collect(event.getX(), event.getEventTime(), TokenNames.X);
            float yOff = this.mY.collect(event.getY(), event.getEventTime(), "Y");
            int keycode = 0;
            int movement = 0;
            float accel = 1.0f;
            if (xOff > yOff) {
                movement = this.mX.generate();
                if (movement != 0) {
                    keycode = movement > 0 ? 22 : 21;
                    accel = this.mX.acceleration;
                    this.mY.reset(2);
                }
            } else if (yOff > 0.0f) {
                movement = this.mY.generate();
                if (movement != 0) {
                    keycode = movement > 0 ? 20 : 19;
                    accel = this.mY.acceleration;
                    this.mX.reset(2);
                }
            }
            if (keycode != 0) {
                if (movement < 0) {
                    movement = -movement;
                }
                int accelMovement = (int) (movement * accel);
                if (accelMovement > movement) {
                    movement--;
                    int repeatCount = accelMovement - movement;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 2, keycode, repeatCount, metaState, -1, 0, 1024, 257));
                }
                while (movement > 0) {
                    movement--;
                    curTime = SystemClock.uptimeMillis();
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 0, keycode, 0, metaState, -1, 0, 1024, 257));
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 1, keycode, 0, metaState, -1, 0, 1024, 257));
                }
                this.mLastTime = curTime;
            }
        }

        public void cancel(MotionEvent event) {
            this.mLastTime = -2147483648L;
            if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mAdded) {
                ViewRootImpl.this.ensureTouchMode(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$TrackballAxis.class */
    public static final class TrackballAxis {
        static final float MAX_ACCELERATION = 20.0f;
        static final long FAST_MOVE_TIME = 150;
        static final float ACCEL_MOVE_SCALING_FACTOR = 0.025f;
        static final float FIRST_MOVEMENT_THRESHOLD = 0.5f;
        static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0f;
        static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0f;
        float position;
        float acceleration = 1.0f;
        long lastMoveTime = 0;
        int step;
        int dir;
        int nonAccelMovement;

        TrackballAxis() {
        }

        void reset(int _step) {
            this.position = 0.0f;
            this.acceleration = 1.0f;
            this.lastMoveTime = 0L;
            this.step = _step;
            this.dir = 0;
        }

        float collect(float off, long time, String axis) {
            long normTime;
            if (off > 0.0f) {
                normTime = off * 150.0f;
                if (this.dir < 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = 1;
            } else if (off < 0.0f) {
                normTime = (-off) * 150.0f;
                if (this.dir > 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = -1;
            } else {
                normTime = 0;
            }
            if (normTime > 0) {
                long delta = time - this.lastMoveTime;
                this.lastMoveTime = time;
                float acc = this.acceleration;
                if (delta < normTime) {
                    float scale = ((float) (normTime - delta)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale > 1.0f) {
                        acc *= scale;
                    }
                    this.acceleration = acc < MAX_ACCELERATION ? acc : MAX_ACCELERATION;
                } else {
                    float scale2 = ((float) (delta - normTime)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale2 > 1.0f) {
                        acc /= scale2;
                    }
                    this.acceleration = acc > 1.0f ? acc : 1.0f;
                }
            }
            this.position += off;
            return Math.abs(this.position);
        }

        int generate() {
            int movement = 0;
            this.nonAccelMovement = 0;
            while (true) {
                int dir = this.position >= 0.0f ? 1 : -1;
                switch (this.step) {
                    case 0:
                        if (Math.abs(this.position) < FIRST_MOVEMENT_THRESHOLD) {
                            return movement;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.step = 1;
                        break;
                    case 1:
                        if (Math.abs(this.position) < SECOND_CUMULATIVE_MOVEMENT_THRESHOLD) {
                            return movement;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.position -= SECOND_CUMULATIVE_MOVEMENT_THRESHOLD * dir;
                        this.step = 2;
                        break;
                    default:
                        if (Math.abs(this.position) < 1.0f) {
                            return movement;
                        }
                        movement += dir;
                        this.position -= dir * 1.0f;
                        float acc = this.acceleration * 1.1f;
                        this.acceleration = acc < MAX_ACCELERATION ? acc : this.acceleration;
                        break;
                }
            }
        }
    }

    /* loaded from: ViewRootImpl$SyntheticJoystickHandler.class */
    final class SyntheticJoystickHandler extends Handler {
        private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
        private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
        private int mLastXDirection;
        private int mLastYDirection;
        private int mLastXKeyCode;
        private int mLastYKeyCode;

        public SyntheticJoystickHandler() {
            super(true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                case 2:
                    KeyEvent oldEvent = (KeyEvent) msg.obj;
                    KeyEvent e = KeyEvent.changeTimeRepeat(oldEvent, SystemClock.uptimeMillis(), oldEvent.getRepeatCount() + 1);
                    if (ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                        ViewRootImpl.this.enqueueInputEvent(e);
                        Message m = obtainMessage(msg.what, e);
                        m.setAsynchronous(true);
                        sendMessageDelayed(m, ViewConfiguration.getKeyRepeatDelay());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void process(MotionEvent event) {
            update(event, true);
        }

        public void cancel(MotionEvent event) {
            update(event, false);
        }

        private void update(MotionEvent event, boolean synthesizeNewKeys) {
            long time = event.getEventTime();
            int metaState = event.getMetaState();
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            int xDirection = joystickAxisValueToDirection(event.getAxisValue(15));
            if (xDirection == 0) {
                xDirection = joystickAxisValueToDirection(event.getX());
            }
            int yDirection = joystickAxisValueToDirection(event.getAxisValue(16));
            if (yDirection == 0) {
                yDirection = joystickAxisValueToDirection(event.getY());
            }
            if (xDirection != this.mLastXDirection) {
                if (this.mLastXKeyCode != 0) {
                    removeMessages(1);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(time, time, 1, this.mLastXKeyCode, 0, metaState, deviceId, 0, 1024, source));
                    this.mLastXKeyCode = 0;
                }
                this.mLastXDirection = xDirection;
                if (xDirection != 0 && synthesizeNewKeys) {
                    this.mLastXKeyCode = xDirection > 0 ? 22 : 21;
                    KeyEvent e = new KeyEvent(time, time, 0, this.mLastXKeyCode, 0, metaState, deviceId, 0, 1024, source);
                    ViewRootImpl.this.enqueueInputEvent(e);
                    Message m = obtainMessage(1, e);
                    m.setAsynchronous(true);
                    sendMessageDelayed(m, ViewConfiguration.getKeyRepeatTimeout());
                }
            }
            if (yDirection != this.mLastYDirection) {
                if (this.mLastYKeyCode != 0) {
                    removeMessages(2);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(time, time, 1, this.mLastYKeyCode, 0, metaState, deviceId, 0, 1024, source));
                    this.mLastYKeyCode = 0;
                }
                this.mLastYDirection = yDirection;
                if (yDirection != 0 && synthesizeNewKeys) {
                    this.mLastYKeyCode = yDirection > 0 ? 20 : 19;
                    KeyEvent e2 = new KeyEvent(time, time, 0, this.mLastYKeyCode, 0, metaState, deviceId, 0, 1024, source);
                    ViewRootImpl.this.enqueueInputEvent(e2);
                    Message m2 = obtainMessage(2, e2);
                    m2.setAsynchronous(true);
                    sendMessageDelayed(m2, ViewConfiguration.getKeyRepeatTimeout());
                }
            }
        }

        private int joystickAxisValueToDirection(float value) {
            if (value >= 0.5f) {
                return 1;
            }
            if (value <= -0.5f) {
                return -1;
            }
            return 0;
        }
    }

    /* loaded from: ViewRootImpl$SyntheticTouchNavigationHandler.class */
    final class SyntheticTouchNavigationHandler extends Handler {
        private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
        private static final boolean LOCAL_DEBUG = false;
        private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0f;
        private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0f;
        private static final int TICK_DISTANCE_MILLIMETERS = 12;
        private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0f;
        private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0f;
        private static final float FLING_TICK_DECAY = 0.8f;
        private int mCurrentDeviceId;
        private int mCurrentSource;
        private boolean mCurrentDeviceSupported;
        private float mConfigTickDistance;
        private float mConfigMinFlingVelocity;
        private float mConfigMaxFlingVelocity;
        private VelocityTracker mVelocityTracker;
        private int mActivePointerId;
        private long mStartTime;
        private float mStartX;
        private float mStartY;
        private float mLastX;
        private float mLastY;
        private float mAccumulatedX;
        private float mAccumulatedY;
        private boolean mConsumedMovement;
        private long mPendingKeyDownTime;
        private int mPendingKeyCode;
        private int mPendingKeyRepeatCount;
        private int mPendingKeyMetaState;
        private boolean mFlinging;
        private float mFlingVelocity;
        private long mLastConfirmKeyTime;
        private final Runnable mFlingRunnable;

        static /* synthetic */ float access$1432(SyntheticTouchNavigationHandler x0, float x1) {
            float f = x0.mFlingVelocity * x1;
            x0.mFlingVelocity = f;
            return f;
        }

        public SyntheticTouchNavigationHandler() {
            super(true);
            this.mCurrentDeviceId = -1;
            this.mActivePointerId = -1;
            this.mPendingKeyCode = 0;
            this.mLastConfirmKeyTime = Long.MAX_VALUE;
            this.mFlingRunnable = new Runnable() { // from class: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    long time = SystemClock.uptimeMillis();
                    SyntheticTouchNavigationHandler.this.sendKeyDownOrRepeat(time, SyntheticTouchNavigationHandler.this.mPendingKeyCode, SyntheticTouchNavigationHandler.this.mPendingKeyMetaState);
                    SyntheticTouchNavigationHandler.access$1432(SyntheticTouchNavigationHandler.this, 0.8f);
                    if (!SyntheticTouchNavigationHandler.this.postFling(time)) {
                        SyntheticTouchNavigationHandler.this.mFlinging = false;
                        SyntheticTouchNavigationHandler.this.finishKeys(time);
                    }
                }
            };
        }

        public void process(MotionEvent event) {
            long time = event.getEventTime();
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            if (this.mCurrentDeviceId != deviceId || this.mCurrentSource != source) {
                finishKeys(time);
                finishTracking(time);
                this.mCurrentDeviceId = deviceId;
                this.mCurrentSource = source;
                this.mCurrentDeviceSupported = false;
                InputDevice device = event.getDevice();
                if (device != null) {
                    InputDevice.MotionRange xRange = device.getMotionRange(0);
                    InputDevice.MotionRange yRange = device.getMotionRange(1);
                    if (xRange != null && yRange != null) {
                        this.mCurrentDeviceSupported = true;
                        float xRes = xRange.getResolution();
                        if (xRes <= 0.0f) {
                            xRes = xRange.getRange() / 48.0f;
                        }
                        float yRes = yRange.getResolution();
                        if (yRes <= 0.0f) {
                            yRes = yRange.getRange() / 48.0f;
                        }
                        float nominalRes = (xRes + yRes) * 0.5f;
                        this.mConfigTickDistance = 12.0f * nominalRes;
                        this.mConfigMinFlingVelocity = MIN_FLING_VELOCITY_TICKS_PER_SECOND * this.mConfigTickDistance;
                        this.mConfigMaxFlingVelocity = MAX_FLING_VELOCITY_TICKS_PER_SECOND * this.mConfigTickDistance;
                    }
                }
            }
            if (!this.mCurrentDeviceSupported) {
                return;
            }
            int action = event.getActionMasked();
            switch (action) {
                case 0:
                    boolean caughtFling = this.mFlinging;
                    finishKeys(time);
                    finishTracking(time);
                    this.mActivePointerId = event.getPointerId(0);
                    this.mVelocityTracker = VelocityTracker.obtain();
                    this.mVelocityTracker.addMovement(event);
                    this.mStartTime = time;
                    this.mStartX = event.getX();
                    this.mStartY = event.getY();
                    this.mLastX = this.mStartX;
                    this.mLastY = this.mStartY;
                    this.mAccumulatedX = 0.0f;
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = caughtFling;
                    return;
                case 1:
                case 2:
                    if (this.mActivePointerId >= 0) {
                        int index = event.findPointerIndex(this.mActivePointerId);
                        if (index < 0) {
                            finishKeys(time);
                            finishTracking(time);
                            return;
                        }
                        this.mVelocityTracker.addMovement(event);
                        float x = event.getX(index);
                        float y = event.getY(index);
                        this.mAccumulatedX += x - this.mLastX;
                        this.mAccumulatedY += y - this.mLastY;
                        this.mLastX = x;
                        this.mLastY = y;
                        int metaState = event.getMetaState();
                        consumeAccumulatedMovement(time, metaState);
                        if (action == 1) {
                            if (this.mConsumedMovement && this.mPendingKeyCode != 0) {
                                this.mVelocityTracker.computeCurrentVelocity(1000, this.mConfigMaxFlingVelocity);
                                float vx = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                                float vy = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                                if (!startFling(time, vx, vy)) {
                                    finishKeys(time);
                                }
                            }
                            finishTracking(time);
                            return;
                        }
                        return;
                    }
                    return;
                case 3:
                    finishKeys(time);
                    finishTracking(time);
                    return;
                default:
                    return;
            }
        }

        public void cancel(MotionEvent event) {
            if (this.mCurrentDeviceId == event.getDeviceId() && this.mCurrentSource == event.getSource()) {
                long time = event.getEventTime();
                finishKeys(time);
                finishTracking(time);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishKeys(long time) {
            cancelFling();
            sendKeyUp(time);
        }

        private void finishTracking(long time) {
            if (this.mActivePointerId >= 0) {
                this.mActivePointerId = -1;
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }

        private void consumeAccumulatedMovement(long time, int metaState) {
            float absX = Math.abs(this.mAccumulatedX);
            float absY = Math.abs(this.mAccumulatedY);
            if (absX >= absY) {
                if (absX >= this.mConfigTickDistance) {
                    this.mAccumulatedX = consumeAccumulatedMovement(time, metaState, this.mAccumulatedX, 21, 22);
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = true;
                }
            } else if (absY >= this.mConfigTickDistance) {
                this.mAccumulatedY = consumeAccumulatedMovement(time, metaState, this.mAccumulatedY, 19, 20);
                this.mAccumulatedX = 0.0f;
                this.mConsumedMovement = true;
            }
        }

        private float consumeAccumulatedMovement(long time, int metaState, float accumulator, int negativeKeyCode, int positiveKeyCode) {
            while (accumulator <= (-this.mConfigTickDistance)) {
                sendKeyDownOrRepeat(time, negativeKeyCode, metaState);
                accumulator += this.mConfigTickDistance;
            }
            while (accumulator >= this.mConfigTickDistance) {
                sendKeyDownOrRepeat(time, positiveKeyCode, metaState);
                accumulator -= this.mConfigTickDistance;
            }
            return accumulator;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendKeyDownOrRepeat(long time, int keyCode, int metaState) {
            if (this.mPendingKeyCode != keyCode) {
                sendKeyUp(time);
                this.mPendingKeyDownTime = time;
                this.mPendingKeyCode = keyCode;
                this.mPendingKeyRepeatCount = 0;
            } else {
                this.mPendingKeyRepeatCount++;
            }
            this.mPendingKeyMetaState = metaState;
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, 1024, this.mCurrentSource));
        }

        private void sendKeyUp(long time) {
            if (this.mPendingKeyCode != 0) {
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, 1, this.mPendingKeyCode, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, 1024, this.mCurrentSource));
                this.mPendingKeyCode = 0;
            }
        }

        private boolean startFling(long time, float vx, float vy) {
            switch (this.mPendingKeyCode) {
                case 19:
                    if ((-vy) >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vy;
                        break;
                    } else {
                        return false;
                    }
                case 20:
                    if (vy >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vy;
                        break;
                    } else {
                        return false;
                    }
                    break;
                case 21:
                    if ((-vx) >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vx;
                        break;
                    } else {
                        return false;
                    }
                    break;
                case 22:
                    if (vx >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vx;
                        break;
                    } else {
                        return false;
                    }
                    break;
            }
            this.mFlinging = postFling(time);
            return this.mFlinging;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean postFling(long time) {
            if (this.mFlingVelocity >= this.mConfigMinFlingVelocity) {
                long delay = (this.mConfigTickDistance / this.mFlingVelocity) * 1000.0f;
                postAtTime(this.mFlingRunnable, time + delay);
                return true;
            }
            return false;
        }

        private void cancelFling() {
            if (this.mFlinging) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlinging = false;
            }
        }
    }

    /* loaded from: ViewRootImpl$SyntheticKeyHandler.class */
    final class SyntheticKeyHandler {
        SyntheticKeyHandler() {
        }

        public boolean process(KeyEvent event) {
            int keyCode;
            switch (event.getKeyCode()) {
                case 96:
                case 98:
                case 99:
                case 101:
                    keyCode = ViewRootImpl.this.mFlipControllerFallbackKeys ? 4 : 23;
                    break;
                case 97:
                case 100:
                    keyCode = ViewRootImpl.this.mFlipControllerFallbackKeys ? 23 : 4;
                    break;
                case 102:
                case 103:
                case 104:
                case 105:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                case 121:
                case 122:
                case 123:
                case 124:
                case 125:
                case 126:
                case 127:
                case 128:
                case 129:
                case 130:
                case 131:
                case 132:
                case 133:
                case 134:
                case 135:
                case 136:
                case 137:
                case 138:
                case 139:
                case 140:
                case 141:
                case 142:
                case 143:
                case 144:
                case 145:
                case 146:
                case 147:
                case 148:
                case 149:
                case 150:
                case 151:
                case 152:
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 167:
                case 168:
                case 169:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                case 178:
                case 179:
                case 180:
                case 181:
                case 182:
                case 183:
                case 184:
                case 185:
                case 186:
                case 187:
                default:
                    return false;
                case 106:
                case 107:
                case 108:
                case 188:
                case 189:
                case 190:
                case 191:
                case 192:
                case 193:
                case 194:
                case 195:
                case 196:
                case 197:
                case 198:
                case 199:
                case 200:
                case 201:
                case 202:
                case 203:
                    keyCode = 23;
                    break;
                case 109:
                case 110:
                    return false;
            }
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(), keyCode, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(), event.getScanCode(), event.getFlags() | 1024, event.getSource()));
            return true;
        }
    }

    private static boolean isNavigationKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 61:
            case 62:
            case 66:
            case 92:
            case 93:
            case 122:
            case 123:
                return true;
            default:
                return false;
        }
    }

    private static boolean isTypingKey(KeyEvent keyEvent) {
        return keyEvent.getUnicodeChar() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkForLeavingTouchModeAndConsume(KeyEvent event) {
        if (!this.mAttachInfo.mInTouchMode) {
            return false;
        }
        int action = event.getAction();
        if ((action != 0 && action != 2) || (event.getFlags() & 4) != 0) {
            return false;
        }
        if (isNavigationKey(event)) {
            return ensureTouchMode(false);
        }
        if (isTypingKey(event)) {
            ensureTouchMode(false);
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLocalDragState(Object obj) {
        this.mLocalDragState = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDragEvent(DragEvent event) {
        if (this.mView != null && this.mAdded) {
            int what = event.mAction;
            if (what == 6) {
                this.mView.dispatchDragEvent(event);
            } else {
                if (what == 1) {
                    this.mCurrentDragView = null;
                    this.mDragDescription = event.mClipDescription;
                } else {
                    event.mClipDescription = this.mDragDescription;
                }
                if (what == 2 || what == 3) {
                    this.mDragPoint.set(event.mX, event.mY);
                    if (this.mTranslator != null) {
                        this.mTranslator.translatePointInScreenToAppWindow(this.mDragPoint);
                    }
                    if (this.mCurScrollY != 0) {
                        this.mDragPoint.offset(0.0f, this.mCurScrollY);
                    }
                    event.mX = this.mDragPoint.x;
                    event.mY = this.mDragPoint.y;
                }
                View prevDragView = this.mCurrentDragView;
                boolean result = this.mView.dispatchDragEvent(event);
                if (prevDragView != this.mCurrentDragView) {
                    if (prevDragView != null) {
                        try {
                            this.mWindowSession.dragRecipientExited(this.mWindow);
                        } catch (RemoteException e) {
                            Slog.e(TAG, "Unable to note drag target change");
                        }
                    }
                    if (this.mCurrentDragView != null) {
                        this.mWindowSession.dragRecipientEntered(this.mWindow);
                    }
                }
                if (what == 3) {
                    this.mDragDescription = null;
                    try {
                        Log.i(TAG, "Reporting drop result: " + result);
                        this.mWindowSession.reportDropResult(this.mWindow, result);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Unable to report drop result");
                    }
                }
                if (what == 4) {
                    setLocalDragState(null);
                }
            }
        }
        event.recycle();
    }

    public void handleDispatchSystemUiVisibilityChanged(SystemUiVisibilityInfo args) {
        int visibility;
        if (this.mSeq != args.seq) {
            this.mSeq = args.seq;
            this.mAttachInfo.mForceReportNewAttributes = true;
            scheduleTraversals();
        }
        if (this.mView == null) {
            return;
        }
        if (args.localChanges != 0) {
            this.mView.updateLocalSystemUiVisibility(args.localValue, args.localChanges);
        }
        if (this.mAttachInfo != null && (visibility = args.globalVisibility & 7) != this.mAttachInfo.mGlobalSystemUiVisibility) {
            this.mAttachInfo.mGlobalSystemUiVisibility = visibility;
            this.mView.dispatchSystemUiVisibilityChanged(visibility);
        }
    }

    public void handleDispatchDoneAnimating() {
        if (this.mWindowsAnimating) {
            this.mWindowsAnimating = false;
            if (!this.mDirty.isEmpty() || this.mIsAnimating || this.mFullRedrawNeeded) {
                scheduleTraversals();
            }
        }
    }

    public void getLastTouchPoint(Point outLocation) {
        outLocation.x = (int) this.mLastTouchPoint.x;
        outLocation.y = (int) this.mLastTouchPoint.y;
    }

    public void setDragFocus(View newDragTarget) {
        if (this.mCurrentDragView != newDragTarget) {
            this.mCurrentDragView = newDragTarget;
        }
    }

    private AudioManager getAudioManager() {
        if (this.mView == null) {
            throw new IllegalStateException("getAudioManager called when there is no mView");
        }
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mView.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        return this.mAudioManager;
    }

    public AccessibilityInteractionController getAccessibilityInteractionController() {
        if (this.mView == null) {
            throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
        }
        if (this.mAccessibilityInteractionController == null) {
            this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
        }
        return this.mAccessibilityInteractionController;
    }

    private int relayoutWindow(WindowManager.LayoutParams params, int viewVisibility, boolean insetsPending) throws RemoteException {
        float appScale = this.mAttachInfo.mApplicationScale;
        boolean restore = false;
        if (params != null && this.mTranslator != null) {
            restore = true;
            params.backup();
            this.mTranslator.translateWindowLayout(params);
        }
        if (params != null) {
        }
        this.mPendingConfiguration.seq = 0;
        if (params != null && this.mOrigWindowType != params.type && this.mTargetSdkVersion < 14) {
            Slog.w(TAG, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
            params.type = this.mOrigWindowType;
        }
        int relayoutResult = this.mWindowSession.relayout(this.mWindow, this.mSeq, params, (int) ((this.mView.getMeasuredWidth() * appScale) + 0.5f), (int) ((this.mView.getMeasuredHeight() * appScale) + 0.5f), viewVisibility, insetsPending ? 1 : 0, this.mWinFrame, this.mPendingOverscanInsets, this.mPendingContentInsets, this.mPendingVisibleInsets, this.mPendingConfiguration, this.mSurface);
        if (restore) {
            params.restore();
        }
        if (this.mTranslator != null) {
            this.mTranslator.translateRectInScreenToAppWinFrame(this.mWinFrame);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingOverscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingContentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingVisibleInsets);
        }
        return relayoutResult;
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public void playSoundEffect(int effectId) {
        checkThread();
        if (this.mMediaDisabled) {
            return;
        }
        try {
            AudioManager audioManager = getAudioManager();
            switch (effectId) {
                case 0:
                    audioManager.playSoundEffect(0);
                    return;
                case 1:
                    audioManager.playSoundEffect(3);
                    return;
                case 2:
                    audioManager.playSoundEffect(1);
                    return;
                case 3:
                    audioManager.playSoundEffect(4);
                    return;
                case 4:
                    audioManager.playSoundEffect(2);
                    return;
                default:
                    throw new IllegalArgumentException("unknown effect id " + effectId + " not defined in " + SoundEffectConstants.class.getCanonicalName());
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "FATAL EXCEPTION when attempting to play sound effect: " + e);
            e.printStackTrace();
        }
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public boolean performHapticFeedback(int effectId, boolean always) {
        try {
            return this.mWindowSession.performHapticFeedback(this.mWindow, effectId, always);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.ViewParent
    public View focusSearch(View focused, int direction) {
        checkThread();
        if (!(this.mView instanceof ViewGroup)) {
            return null;
        }
        return FocusFinder.getInstance().findNextFocus((ViewGroup) this.mView, focused, direction);
    }

    public void debug() {
        this.mView.debug();
    }

    public void dumpGfxInfo(int[] info) {
        info[1] = 0;
        info[0] = 0;
        if (this.mView != null) {
            getGfxInfo(this.mView, info);
        }
    }

    private static void getGfxInfo(View view, int[] info) {
        DisplayList displayList = view.mDisplayList;
        info[0] = info[0] + 1;
        if (displayList != null) {
            info[1] = info[1] + displayList.getSize();
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                getGfxInfo(group.getChildAt(i), info);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean die(boolean immediate) {
        if (immediate && !this.mIsInTraversal) {
            doDie();
            return false;
        }
        if (!this.mIsDrawing) {
            destroyHardwareRenderer();
        } else {
            Log.e(TAG, "Attempting to destroy the window while drawing!\n  window=" + this + ", title=" + ((Object) this.mWindowAttributes.getTitle()));
        }
        this.mHandler.sendEmptyMessage(3);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doDie() {
        checkThread();
        synchronized (this) {
            if (this.mRemoved) {
                return;
            }
            this.mRemoved = true;
            if (this.mAdded) {
                dispatchDetachedFromWindow();
            }
            if (this.mAdded && !this.mFirst) {
                invalidateDisplayLists();
                destroyHardwareRenderer();
                if (this.mView != null) {
                    int viewVisibility = this.mView.getVisibility();
                    boolean viewVisibilityChanged = this.mViewVisibility != viewVisibility;
                    if (this.mWindowAttributesChanged || viewVisibilityChanged) {
                        try {
                            if ((relayoutWindow(this.mWindowAttributes, viewVisibility, false) & 2) != 0) {
                                this.mWindowSession.finishDrawing(this.mWindow);
                            }
                        } catch (RemoteException e) {
                        }
                    }
                    this.mSurface.release();
                }
            }
            this.mAdded = false;
            WindowManagerGlobal.getInstance().doRemoveView(this);
        }
    }

    public void requestUpdateConfiguration(Configuration config) {
        Message msg = this.mHandler.obtainMessage(18, config);
        this.mHandler.sendMessage(msg);
    }

    public void loadSystemProperties() {
        this.mHandler.post(new Runnable() { // from class: android.view.ViewRootImpl.4
            @Override // java.lang.Runnable
            public void run() {
                ViewRootImpl.this.mProfileRendering = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_PROFILE_RENDERING, false);
                ViewRootImpl.this.profileRendering(ViewRootImpl.this.mAttachInfo.mHasWindowFocus);
                ViewRootImpl.this.mMediaDisabled = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_MEDIA_DISABLED, false);
                if (ViewRootImpl.this.mAttachInfo.mHardwareRenderer != null && ViewRootImpl.this.mAttachInfo.mHardwareRenderer.loadSystemProperties(ViewRootImpl.this.mHolder.getSurface())) {
                    ViewRootImpl.this.invalidate();
                }
                boolean layout = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
                if (layout != ViewRootImpl.this.mAttachInfo.mDebugLayout) {
                    ViewRootImpl.this.mAttachInfo.mDebugLayout = layout;
                    if (!ViewRootImpl.this.mHandler.hasMessages(23)) {
                        ViewRootImpl.this.mHandler.sendEmptyMessageDelayed(23, 200L);
                    }
                }
            }
        });
    }

    private void destroyHardwareRenderer() {
        View.AttachInfo attachInfo = this.mAttachInfo;
        HardwareRenderer hardwareRenderer = attachInfo.mHardwareRenderer;
        if (hardwareRenderer != null) {
            if (this.mView != null) {
                hardwareRenderer.destroyHardwareResources(this.mView);
            }
            hardwareRenderer.destroy(true);
            hardwareRenderer.setRequested(false);
            attachInfo.mHardwareRenderer = null;
            attachInfo.mHardwareAccelerated = false;
        }
    }

    public void dispatchFinishInputConnection(InputConnection connection) {
        Message msg = this.mHandler.obtainMessage(12, connection);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchResized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, boolean reportDraw, Configuration newConfig) {
        Message msg = this.mHandler.obtainMessage(reportDraw ? 5 : 4);
        if (this.mTranslator != null) {
            this.mTranslator.translateRectInScreenToAppWindow(frame);
            this.mTranslator.translateRectInScreenToAppWindow(overscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(contentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(visibleInsets);
        }
        SomeArgs args = SomeArgs.obtain();
        boolean sameProcessCall = Binder.getCallingPid() == Process.myPid();
        args.arg1 = sameProcessCall ? new Rect(frame) : frame;
        args.arg2 = sameProcessCall ? new Rect(contentInsets) : contentInsets;
        args.arg3 = sameProcessCall ? new Rect(visibleInsets) : visibleInsets;
        args.arg4 = (!sameProcessCall || newConfig == null) ? newConfig : new Configuration(newConfig);
        args.arg5 = sameProcessCall ? new Rect(overscanInsets) : overscanInsets;
        msg.obj = args;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchMoved(int newX, int newY) {
        if (this.mTranslator != null) {
            PointF point = new PointF(newX, newY);
            this.mTranslator.translatePointInScreenToAppWindow(point);
            newX = (int) (point.x + 0.5d);
            newY = (int) (point.y + 0.5d);
        }
        Message msg = this.mHandler.obtainMessage(24, newX, newY);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ViewRootImpl$QueuedInputEvent.class */
    public static final class QueuedInputEvent {
        public static final int FLAG_DELIVER_POST_IME = 1;
        public static final int FLAG_DEFERRED = 2;
        public static final int FLAG_FINISHED = 4;
        public static final int FLAG_FINISHED_HANDLED = 8;
        public static final int FLAG_RESYNTHESIZED = 16;
        public QueuedInputEvent mNext;
        public InputEvent mEvent;
        public InputEventReceiver mReceiver;
        public int mFlags;

        private QueuedInputEvent() {
        }

        public boolean shouldSkipIme() {
            if ((this.mFlags & 1) != 0) {
                return true;
            }
            return (this.mEvent instanceof MotionEvent) && this.mEvent.isFromSource(2);
        }
    }

    private QueuedInputEvent obtainQueuedInputEvent(InputEvent event, InputEventReceiver receiver, int flags) {
        QueuedInputEvent q = this.mQueuedInputEventPool;
        if (q != null) {
            this.mQueuedInputEventPoolSize--;
            this.mQueuedInputEventPool = q.mNext;
            q.mNext = null;
        } else {
            q = new QueuedInputEvent();
        }
        q.mEvent = event;
        q.mReceiver = receiver;
        q.mFlags = flags;
        return q;
    }

    private void recycleQueuedInputEvent(QueuedInputEvent q) {
        q.mEvent = null;
        q.mReceiver = null;
        if (this.mQueuedInputEventPoolSize < 10) {
            this.mQueuedInputEventPoolSize++;
            q.mNext = this.mQueuedInputEventPool;
            this.mQueuedInputEventPool = q;
        }
    }

    void enqueueInputEvent(InputEvent event) {
        enqueueInputEvent(event, null, 0, false);
    }

    void enqueueInputEvent(InputEvent event, InputEventReceiver receiver, int flags, boolean processImmediately) {
        QueuedInputEvent q = obtainQueuedInputEvent(event, receiver, flags);
        QueuedInputEvent last = this.mPendingInputEventTail;
        if (last == null) {
            this.mPendingInputEventHead = q;
            this.mPendingInputEventTail = q;
        } else {
            last.mNext = q;
            this.mPendingInputEventTail = q;
        }
        this.mPendingInputEventCount++;
        Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
        if (processImmediately) {
            doProcessInputEvents();
        } else {
            scheduleProcessInputEvents();
        }
    }

    private void scheduleProcessInputEvents() {
        if (!this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = true;
            Message msg = this.mHandler.obtainMessage(19);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    void doProcessInputEvents() {
        while (this.mPendingInputEventHead != null) {
            QueuedInputEvent q = this.mPendingInputEventHead;
            this.mPendingInputEventHead = q.mNext;
            if (this.mPendingInputEventHead == null) {
                this.mPendingInputEventTail = null;
            }
            q.mNext = null;
            this.mPendingInputEventCount--;
            Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
            deliverInputEvent(q);
        }
        if (this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = false;
            this.mHandler.removeMessages(19);
        }
    }

    private void deliverInputEvent(QueuedInputEvent q) {
        Trace.traceBegin(8L, "deliverInputEvent");
        try {
            if (this.mInputEventConsistencyVerifier != null) {
                this.mInputEventConsistencyVerifier.onInputEvent(q.mEvent, 0);
            }
            InputStage stage = q.shouldSkipIme() ? this.mFirstPostImeInputStage : this.mFirstInputStage;
            if (stage != null) {
                stage.deliver(q);
            } else {
                finishInputEvent(q);
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishInputEvent(QueuedInputEvent q) {
        if (q.mReceiver != null) {
            boolean handled = (q.mFlags & 8) != 0;
            q.mReceiver.finishInputEvent(q.mEvent, handled);
        } else {
            q.mEvent.recycleIfNeededAfterDispatch();
        }
        recycleQueuedInputEvent(q);
    }

    static boolean isTerminalInputEvent(InputEvent event) {
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            return keyEvent.getAction() == 1;
        }
        MotionEvent motionEvent = (MotionEvent) event;
        int action = motionEvent.getAction();
        return action == 1 || action == 3 || action == 10;
    }

    void scheduleConsumeBatchedInput() {
        if (!this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = true;
            this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void unscheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void doConsumeBatchedInput(long frameTimeNanos) {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            if (this.mInputEventReceiver != null) {
                this.mInputEventReceiver.consumeBatchedInputEvents(frameTimeNanos);
            }
            doProcessInputEvents();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$TraversalRunnable.class */
    public final class TraversalRunnable implements Runnable {
        TraversalRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doTraversal();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$WindowInputEventReceiver.class */
    public final class WindowInputEventReceiver extends InputEventReceiver {
        public WindowInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventReceiver
        public void onInputEvent(InputEvent event) {
            ViewRootImpl.this.enqueueInputEvent(event, this, 0, true);
        }

        @Override // android.view.InputEventReceiver
        public void onBatchedInputEventPending() {
            ViewRootImpl.this.scheduleConsumeBatchedInput();
        }

        @Override // android.view.InputEventReceiver
        public void dispose() {
            ViewRootImpl.this.unscheduleConsumeBatchedInput();
            super.dispose();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$ConsumeBatchedInputRunnable.class */
    public final class ConsumeBatchedInputRunnable implements Runnable {
        ConsumeBatchedInputRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doConsumeBatchedInput(ViewRootImpl.this.mChoreographer.getFrameTimeNanos());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$InvalidateOnAnimationRunnable.class */
    public final class InvalidateOnAnimationRunnable implements Runnable {
        private boolean mPosted;
        private final ArrayList<View> mViews = new ArrayList<>();
        private final ArrayList<View.AttachInfo.InvalidateInfo> mViewRects = new ArrayList<>();
        private View[] mTempViews;
        private View.AttachInfo.InvalidateInfo[] mTempViewRects;

        InvalidateOnAnimationRunnable() {
        }

        public void addView(View view) {
            synchronized (this) {
                this.mViews.add(view);
                postIfNeededLocked();
            }
        }

        public void addViewRect(View.AttachInfo.InvalidateInfo info) {
            synchronized (this) {
                this.mViewRects.add(info);
                postIfNeededLocked();
            }
        }

        public void removeView(View view) {
            synchronized (this) {
                this.mViews.remove(view);
                int i = this.mViewRects.size();
                while (true) {
                    int i2 = i;
                    i--;
                    if (i2 <= 0) {
                        break;
                    }
                    View.AttachInfo.InvalidateInfo info = this.mViewRects.get(i);
                    if (info.target == view) {
                        this.mViewRects.remove(i);
                        info.recycle();
                    }
                }
                if (this.mPosted && this.mViews.isEmpty() && this.mViewRects.isEmpty()) {
                    ViewRootImpl.this.mChoreographer.removeCallbacks(1, this, null);
                    this.mPosted = false;
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int viewCount;
            int viewRectCount;
            synchronized (this) {
                this.mPosted = false;
                viewCount = this.mViews.size();
                if (viewCount != 0) {
                    this.mTempViews = (View[]) this.mViews.toArray(this.mTempViews != null ? this.mTempViews : new View[viewCount]);
                    this.mViews.clear();
                }
                viewRectCount = this.mViewRects.size();
                if (viewRectCount != 0) {
                    this.mTempViewRects = (View.AttachInfo.InvalidateInfo[]) this.mViewRects.toArray(this.mTempViewRects != null ? this.mTempViewRects : new View.AttachInfo.InvalidateInfo[viewRectCount]);
                    this.mViewRects.clear();
                }
            }
            for (int i = 0; i < viewCount; i++) {
                this.mTempViews[i].invalidate();
                this.mTempViews[i] = null;
            }
            for (int i2 = 0; i2 < viewRectCount; i2++) {
                View.AttachInfo.InvalidateInfo info = this.mTempViewRects[i2];
                info.target.invalidate(info.left, info.top, info.right, info.bottom);
                info.recycle();
            }
        }

        private void postIfNeededLocked() {
            if (!this.mPosted) {
                ViewRootImpl.this.mChoreographer.postCallback(1, this, null);
                this.mPosted = true;
            }
        }
    }

    public void dispatchInvalidateDelayed(View view, long delayMilliseconds) {
        Message msg = this.mHandler.obtainMessage(1, view);
        this.mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }

    public void dispatchInvalidateRectDelayed(View.AttachInfo.InvalidateInfo info, long delayMilliseconds) {
        Message msg = this.mHandler.obtainMessage(2, info);
        this.mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }

    public void dispatchInvalidateOnAnimation(View view) {
        this.mInvalidateOnAnimationRunnable.addView(view);
    }

    public void dispatchInvalidateRectOnAnimation(View.AttachInfo.InvalidateInfo info) {
        this.mInvalidateOnAnimationRunnable.addViewRect(info);
    }

    public void enqueueDisplayList(DisplayList displayList) {
        this.mDisplayLists.add(displayList);
    }

    public void cancelInvalidate(View view) {
        this.mHandler.removeMessages(1, view);
        this.mHandler.removeMessages(2, view);
        this.mInvalidateOnAnimationRunnable.removeView(view);
    }

    public void dispatchInputEvent(InputEvent event) {
        Message msg = this.mHandler.obtainMessage(7, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchKeyFromIme(KeyEvent event) {
        Message msg = this.mHandler.obtainMessage(11, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchUnhandledKey(KeyEvent event) {
        if ((event.getFlags() & 1024) == 0) {
            KeyCharacterMap kcm = event.getKeyCharacterMap();
            int keyCode = event.getKeyCode();
            int metaState = event.getMetaState();
            KeyCharacterMap.FallbackAction fallbackAction = kcm.getFallbackAction(keyCode, metaState);
            if (fallbackAction != null) {
                int flags = event.getFlags() | 1024;
                KeyEvent fallbackEvent = KeyEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), fallbackAction.keyCode, event.getRepeatCount(), fallbackAction.metaState, event.getDeviceId(), event.getScanCode(), flags, event.getSource(), null);
                fallbackAction.recycle();
                dispatchInputEvent(fallbackEvent);
            }
        }
    }

    public void dispatchAppVisibility(boolean visible) {
        Message msg = this.mHandler.obtainMessage(8);
        msg.arg1 = visible ? 1 : 0;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchScreenStateChange(boolean on) {
        Message msg = this.mHandler.obtainMessage(20);
        msg.arg1 = on ? 1 : 0;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchGetNewSurface() {
        Message msg = this.mHandler.obtainMessage(9);
        this.mHandler.sendMessage(msg);
    }

    public void windowFocusChanged(boolean hasFocus, boolean inTouchMode) {
        Message msg = Message.obtain();
        msg.what = 6;
        msg.arg1 = hasFocus ? 1 : 0;
        msg.arg2 = inTouchMode ? 1 : 0;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchCloseSystemDialogs(String reason) {
        Message msg = Message.obtain();
        msg.what = 14;
        msg.obj = reason;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchDragEvent(DragEvent event) {
        int what;
        if (event.getAction() == 2) {
            what = 16;
            this.mHandler.removeMessages(16);
        } else {
            what = 15;
        }
        Message msg = this.mHandler.obtainMessage(what, event);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility, int localValue, int localChanges) {
        SystemUiVisibilityInfo args = new SystemUiVisibilityInfo();
        args.seq = seq;
        args.globalVisibility = globalVisibility;
        args.localValue = localValue;
        args.localChanges = localChanges;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(17, args));
    }

    public void dispatchDoneAnimating() {
        this.mHandler.sendEmptyMessage(22);
    }

    public void dispatchCheckFocus() {
        if (!this.mHandler.hasMessages(13)) {
            this.mHandler.sendEmptyMessage(13);
        }
    }

    private void postSendWindowContentChangedCallback(View source, int changeType) {
        if (this.mSendWindowContentChangedAccessibilityEvent == null) {
            this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent();
        }
        this.mSendWindowContentChangedAccessibilityEvent.runOrPost(source, changeType);
    }

    private void removeSendWindowContentChangedCallback() {
        if (this.mSendWindowContentChangedAccessibilityEvent != null) {
            this.mHandler.removeCallbacks(this.mSendWindowContentChangedAccessibilityEvent);
        }
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View originalView) {
        return false;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.ViewParent
    public void createContextMenu(ContextMenu menu) {
    }

    @Override // android.view.ViewParent
    public void childDrawableStateChanged(View child) {
    }

    @Override // android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        AccessibilityNodeProvider provider;
        if (this.mView == null) {
            return false;
        }
        int eventType = event.getEventType();
        switch (eventType) {
            case 32768:
                long sourceNodeId = event.getSourceNodeId();
                int accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(sourceNodeId);
                View source = this.mView.findViewByAccessibilityId(accessibilityViewId);
                if (source != null && (provider = source.getAccessibilityNodeProvider()) != null) {
                    AccessibilityNodeInfo node = provider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(sourceNodeId));
                    setAccessibilityFocus(source, node);
                    break;
                }
                break;
            case 65536:
                int accessibilityViewId2 = AccessibilityNodeInfo.getAccessibilityViewId(event.getSourceNodeId());
                View source2 = this.mView.findViewByAccessibilityId(accessibilityViewId2);
                if (source2 != null && source2.getAccessibilityNodeProvider() != null) {
                    setAccessibilityFocus(null, null);
                    break;
                }
                break;
        }
        this.mAccessibilityManager.sendAccessibilityEvent(event);
        return true;
    }

    @Override // android.view.ViewParent
    public void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType) {
        postSendWindowContentChangedCallback(source, changeType);
    }

    @Override // android.view.ViewParent
    public boolean canResolveLayoutDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isLayoutDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getLayoutDirection() {
        return 0;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getTextDirection() {
        return 1;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextAlignment() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextAlignmentResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getTextAlignment() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getCommonPredecessor(View first, View second) {
        if (this.mAttachInfo != null) {
            if (this.mTempHashSet == null) {
                this.mTempHashSet = new HashSet<>();
            }
            HashSet<View> seen = this.mTempHashSet;
            seen.clear();
            View view = first;
            while (true) {
                View firstCurrent = view;
                if (firstCurrent == null) {
                    break;
                }
                seen.add(firstCurrent);
                ViewParent firstCurrentParent = firstCurrent.mParent;
                if (firstCurrentParent instanceof View) {
                    view = (View) firstCurrentParent;
                } else {
                    view = null;
                }
            }
            View view2 = second;
            while (true) {
                View secondCurrent = view2;
                if (secondCurrent != null) {
                    if (seen.contains(secondCurrent)) {
                        seen.clear();
                        return secondCurrent;
                    }
                    ViewParent secondCurrentParent = secondCurrent.mParent;
                    if (secondCurrentParent instanceof View) {
                        view2 = (View) secondCurrentParent;
                    } else {
                        view2 = null;
                    }
                } else {
                    seen.clear();
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    void checkThread() {
        if (this.mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
        }
    }

    @Override // android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override // android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        boolean scrolled = scrollToRectOrFocus(rectangle, immediate);
        if (rectangle != null) {
            this.mTempRect.set(rectangle);
            this.mTempRect.offset(0, -this.mCurScrollY);
            this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
            try {
                this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect, immediate);
            } catch (RemoteException e) {
            }
        }
        return scrolled;
    }

    @Override // android.view.ViewParent
    public void childHasTransientStateChanged(View child, boolean hasTransientState) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void changeCanvasOpacity(boolean opaque) {
        Log.d(TAG, "changeCanvasOpacity: opaque=" + opaque);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$TakenSurfaceHolder.class */
    public class TakenSurfaceHolder extends BaseSurfaceHolder {
        TakenSurfaceHolder() {
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public boolean onAllowLockCanvas() {
            return ViewRootImpl.this.mDrawingAllowed;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onRelayoutContainer() {
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFormat(int format) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceFormat(format);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setType(int type) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceType(type);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onUpdateSurface() {
            throw new IllegalStateException("Shouldn't be here");
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return ViewRootImpl.this.mIsCreating;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFixedSize(int width, int height) {
            throw new UnsupportedOperationException("Currently only support sizing from layout");
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(boolean screenOn) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceKeepScreenOn(screenOn);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$W.class */
    public static class W extends IWindow.Stub {
        private final WeakReference<ViewRootImpl> mViewAncestor;
        private final IWindowSession mWindowSession;

        W(ViewRootImpl viewAncestor) {
            this.mViewAncestor = new WeakReference<>(viewAncestor);
            this.mWindowSession = viewAncestor.mWindowSession;
        }

        @Override // android.view.IWindow
        public void resized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, boolean reportDraw, Configuration newConfig) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchResized(frame, overscanInsets, contentInsets, visibleInsets, reportDraw, newConfig);
            }
        }

        @Override // android.view.IWindow
        public void moved(int newX, int newY) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchMoved(newX, newY);
            }
        }

        @Override // android.view.IWindow
        public void dispatchAppVisibility(boolean visible) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchAppVisibility(visible);
            }
        }

        @Override // android.view.IWindow
        public void dispatchScreenState(boolean on) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchScreenStateChange(on);
            }
        }

        @Override // android.view.IWindow
        public void dispatchGetNewSurface() {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchGetNewSurface();
            }
        }

        @Override // android.view.IWindow
        public void windowFocusChanged(boolean hasFocus, boolean inTouchMode) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.windowFocusChanged(hasFocus, inTouchMode);
            }
        }

        private static int checkCallingPermission(String permission) {
            try {
                return ActivityManagerNative.getDefault().checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid());
            } catch (RemoteException e) {
                return -1;
            }
        }

        @Override // android.view.IWindow
        public void executeCommand(String command, String parameters, ParcelFileDescriptor out) {
            View view;
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor == null || (view = viewAncestor.mView) == null) {
                return;
            }
            if (checkCallingPermission(Manifest.permission.DUMP) != 0) {
                throw new SecurityException("Insufficient permissions to invoke executeCommand() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            }
            OutputStream clientStream = null;
            try {
                try {
                    clientStream = new ParcelFileDescriptor.AutoCloseOutputStream(out);
                    ViewDebug.dispatchCommand(view, command, parameters, clientStream);
                    if (clientStream != null) {
                        try {
                            clientStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable th) {
                    if (clientStream != null) {
                        try {
                            clientStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e3) {
                e3.printStackTrace();
                if (clientStream != null) {
                    try {
                        clientStream.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }

        @Override // android.view.IWindow
        public void closeSystemDialogs(String reason) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchCloseSystemDialogs(reason);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperOffsetsComplete(asBinder());
                } catch (RemoteException e) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
                } catch (RemoteException e) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchDragEvent(DragEvent event) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchDragEvent(event);
            }
        }

        @Override // android.view.IWindow
        public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility, int localValue, int localChanges) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchSystemUiVisibilityChanged(seq, globalVisibility, localValue, localChanges);
            }
        }

        @Override // android.view.IWindow
        public void doneAnimating() {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchDoneAnimating();
            }
        }
    }

    /* loaded from: ViewRootImpl$CalledFromWrongThreadException.class */
    public static final class CalledFromWrongThreadException extends AndroidRuntimeException {
        public CalledFromWrongThreadException(String msg) {
            super(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RunQueue getRunQueue() {
        RunQueue rq = sRunQueues.get();
        if (rq != null) {
            return rq;
        }
        RunQueue rq2 = new RunQueue();
        sRunQueues.set(rq2);
        return rq2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$RunQueue.class */
    public static final class RunQueue {
        private final ArrayList<HandlerAction> mActions = new ArrayList<>();

        RunQueue() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void post(Runnable action) {
            postDelayed(action, 0L);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void postDelayed(Runnable action, long delayMillis) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = action;
            handlerAction.delay = delayMillis;
            synchronized (this.mActions) {
                this.mActions.add(handlerAction);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void removeCallbacks(Runnable action) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = action;
            synchronized (this.mActions) {
                ArrayList<HandlerAction> actions = this.mActions;
                while (actions.remove(handlerAction)) {
                }
            }
        }

        void executeActions(Handler handler) {
            synchronized (this.mActions) {
                ArrayList<HandlerAction> actions = this.mActions;
                int count = actions.size();
                for (int i = 0; i < count; i++) {
                    HandlerAction handlerAction = actions.get(i);
                    handler.postDelayed(handlerAction.action, handlerAction.delay);
                }
                actions.clear();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ViewRootImpl$RunQueue$HandlerAction.class */
        public static class HandlerAction {
            Runnable action;
            long delay;

            private HandlerAction() {
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                HandlerAction that = (HandlerAction) o;
                return this.action == null ? that.action == null : this.action.equals(that.action);
            }

            public int hashCode() {
                int result = this.action != null ? this.action.hashCode() : 0;
                return (31 * result) + ((int) (this.delay ^ (this.delay >>> 32)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$AccessibilityInteractionConnectionManager.class */
    public final class AccessibilityInteractionConnectionManager implements AccessibilityManager.AccessibilityStateChangeListener {
        AccessibilityInteractionConnectionManager() {
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean enabled) {
            if (enabled) {
                ensureConnection();
                if (ViewRootImpl.this.mAttachInfo != null && ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                    ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                    View focusedView = ViewRootImpl.this.mView.findFocus();
                    if (focusedView != null && focusedView != ViewRootImpl.this.mView) {
                        focusedView.sendAccessibilityEvent(8);
                        return;
                    }
                    return;
                }
                return;
            }
            ensureNoConnection();
            ViewRootImpl.this.mHandler.obtainMessage(21).sendToTarget();
        }

        public void ensureConnection() {
            if (ViewRootImpl.this.mAttachInfo != null) {
                boolean registered = ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1;
                if (!registered) {
                    ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = ViewRootImpl.this.mAccessibilityManager.addAccessibilityInteractionConnection(ViewRootImpl.this.mWindow, new AccessibilityInteractionConnection(ViewRootImpl.this));
                }
            }
        }

        public void ensureNoConnection() {
            boolean registered = ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1;
            if (registered) {
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = -1;
                ViewRootImpl.this.mAccessibilityManager.removeAccessibilityInteractionConnection(ViewRootImpl.this.mWindow);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ViewRootImpl$AccessibilityInteractionConnection.class */
    public static final class AccessibilityInteractionConnection extends IAccessibilityInteractionConnection.Stub {
        private final WeakReference<ViewRootImpl> mViewRootImpl;

        AccessibilityInteractionConnection(ViewRootImpl viewRootImpl) {
            this.mViewRootImpl = new WeakReference<>(viewRootImpl);
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfoByAccessibilityId(long accessibilityNodeId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfoByAccessibilityIdClientThread(accessibilityNodeId, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfosResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void performAccessibilityAction(long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interogatingPid, long interrogatingTid) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().performAccessibilityActionClientThread(accessibilityNodeId, action, arguments, interactionId, callback, flags, interogatingPid, interrogatingTid);
                return;
            }
            try {
                callback.setPerformAccessibilityActionResult(false, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByViewId(long accessibilityNodeId, String viewId, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByViewIdClientThread(accessibilityNodeId, viewId, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByText(long accessibilityNodeId, String text, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByTextClientThread(accessibilityNodeId, text, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfosResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findFocus(long accessibilityNodeId, int focusType, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findFocusClientThread(accessibilityNodeId, focusType, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void focusSearch(long accessibilityNodeId, int direction, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().focusSearchClientThread(accessibilityNodeId, direction, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ViewRootImpl$SendWindowContentChangedAccessibilityEvent.class */
    public class SendWindowContentChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        public View mSource;
        public long mLastEventTimeMillis;

        private SendWindowContentChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AccessibilityManager.getInstance(ViewRootImpl.this.mContext).isEnabled()) {
                this.mLastEventTimeMillis = SystemClock.uptimeMillis();
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.setEventType(2048);
                event.setContentChangeTypes(this.mChangeTypes);
                this.mSource.sendAccessibilityEventUnchecked(event);
            } else {
                this.mLastEventTimeMillis = 0L;
            }
            this.mSource.resetSubtreeAccessibilityStateChanged();
            this.mSource = null;
            this.mChangeTypes = 0;
        }

        public void runOrPost(View source, int changeType) {
            if (this.mSource != null) {
                View predecessor = ViewRootImpl.this.getCommonPredecessor(this.mSource, source);
                this.mSource = predecessor != null ? predecessor : source;
                this.mChangeTypes |= changeType;
                return;
            }
            this.mSource = source;
            this.mChangeTypes = changeType;
            long timeSinceLastMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
            long minEventIntevalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            if (timeSinceLastMillis >= minEventIntevalMillis) {
                this.mSource.removeCallbacks(this);
                run();
                return;
            }
            this.mSource.postDelayed(this, minEventIntevalMillis - timeSinceLastMillis);
        }
    }
}