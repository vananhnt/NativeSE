package com.android.server.wm;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.StatusBarManager;
import android.app.backup.FullBackup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.WorkSource;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.TimedRemoteCaller;
import android.util.TypedValue;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IApplicationToken;
import android.view.IInputFilter;
import android.view.IMagnificationCallbacks;
import android.view.IOnKeyguardExitResult;
import android.view.IRotationWatcher;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEventReceiver;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowManager;
import android.view.WindowManagerPolicy;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.android.internal.R;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.WindowManagerPolicyThread;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.AttributeCache;
import com.android.server.Watchdog;
import com.android.server.display.DisplayManagerService;
import com.android.server.input.InputManagerService;
import com.android.server.power.PowerManagerService;
import com.android.server.power.ShutdownThread;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    */
/* loaded from: WindowManagerService.class */
public class WindowManagerService extends IWindowManager.Stub implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs, DisplayManagerService.WindowManagerFuncs, DisplayManager.DisplayListener {
    static final String TAG = "WindowManager";
    static final boolean DEBUG = false;
    static final boolean DEBUG_ADD_REMOVE = false;
    static final boolean DEBUG_FOCUS = false;
    static final boolean DEBUG_FOCUS_LIGHT = false;
    static final boolean DEBUG_ANIM = false;
    static final boolean DEBUG_LAYOUT = false;
    static final boolean DEBUG_RESIZE = false;
    static final boolean DEBUG_LAYERS = false;
    static final boolean DEBUG_INPUT = false;
    static final boolean DEBUG_INPUT_METHOD = false;
    static final boolean DEBUG_VISIBILITY = false;
    static final boolean DEBUG_WINDOW_MOVEMENT = false;
    static final boolean DEBUG_TOKEN_MOVEMENT = false;
    static final boolean DEBUG_ORIENTATION = false;
    static final boolean DEBUG_APP_ORIENTATION = false;
    static final boolean DEBUG_CONFIGURATION = false;
    static final boolean DEBUG_APP_TRANSITIONS = false;
    static final boolean DEBUG_STARTING_WINDOW = false;
    static final boolean DEBUG_REORDER = false;
    static final boolean DEBUG_WALLPAPER = false;
    static final boolean DEBUG_WALLPAPER_LIGHT = false;
    static final boolean DEBUG_DRAG = false;
    static final boolean DEBUG_SCREEN_ON = false;
    static final boolean DEBUG_SCREENSHOT = false;
    static final boolean DEBUG_BOOT = false;
    static final boolean DEBUG_LAYOUT_REPEATS = true;
    static final boolean DEBUG_SURFACE_TRACE = false;
    static final boolean DEBUG_WINDOW_TRACE = false;
    static final boolean DEBUG_TASK_MOVEMENT = false;
    static final boolean DEBUG_STACK = false;
    static final boolean SHOW_SURFACE_ALLOC = false;
    static final boolean SHOW_TRANSACTIONS = false;
    static final boolean SHOW_LIGHT_TRANSACTIONS = false;
    static final boolean HIDE_STACK_CRAWLS = true;
    static final int LAYOUT_REPEAT_THRESHOLD = 4;
    static final boolean PROFILE_ORIENTATION = false;
    static final boolean localLOGV = false;
    static final int TYPE_LAYER_MULTIPLIER = 10000;
    static final int TYPE_LAYER_OFFSET = 1000;
    static final int WINDOW_LAYER_MULTIPLIER = 5;
    static final int LAYER_OFFSET_DIM = 1;
    static final int LAYER_OFFSET_BLUR = 2;
    static final int LAYER_OFFSET_FOCUSED_STACK = 1;
    static final int LAYER_OFFSET_THUMBNAIL = 4;
    static final int FREEZE_LAYER = 2000001;
    static final int MASK_LAYER = 2000000;
    static final int MAX_ANIMATION_DURATION = 10000;
    static final int DEFAULT_FADE_IN_OUT_DURATION = 400;
    static final int WINDOW_FREEZE_TIMEOUT_DURATION = 2000;
    static final int STARTING_WINDOW_TIMEOUT_DURATION = 10000;
    static final boolean CUSTOM_SCREEN_ROTATION = true;
    private static final int INPUT_DEVICES_READY_FOR_SAFE_MODE_DETECTION_TIMEOUT_MILLIS = 1000;
    static final long DEFAULT_INPUT_DISPATCHING_TIMEOUT_NANOS = 5000000000L;
    public static final float STACK_WEIGHT_MIN = 0.2f;
    public static final float STACK_WEIGHT_MAX = 0.8f;
    static final int UPDATE_FOCUS_NORMAL = 0;
    static final int UPDATE_FOCUS_WILL_ASSIGN_LAYERS = 1;
    static final int UPDATE_FOCUS_PLACING_SURFACES = 2;
    static final int UPDATE_FOCUS_WILL_PLACE_SURFACES = 3;
    private static final String SYSTEM_SECURE = "ro.secure";
    private static final String SYSTEM_DEBUGGABLE = "ro.debuggable";
    private static final String DENSITY_OVERRIDE = "ro.config.density_override";
    private static final String SIZE_OVERRIDE = "ro.config.size_override";
    private static final int MAX_SCREENSHOT_RETRIES = 3;
    private final KeyguardDisableHandler mKeyguardDisableHandler;
    private final boolean mHeadless;
    final BroadcastReceiver mBroadcastReceiver;
    int mCurrentUserId;
    final Context mContext;
    final boolean mHaveInputMethods;
    final boolean mAllowBootMessages;
    final boolean mLimitedAlphaCompositing;
    final WindowManagerPolicy mPolicy;
    final IActivityManager mActivityManager;
    final IBatteryStats mBatteryStats;
    final AppOpsManager mAppOps;
    final DisplaySettings mDisplaySettings;
    final HashSet<Session> mSessions;
    final HashMap<IBinder, WindowState> mWindowMap;
    final HashMap<IBinder, WindowToken> mTokenMap;
    final ArrayList<AppWindowToken> mFinishedStarting;
    final ArrayList<FakeWindowImpl> mFakeWindows;
    final ArrayList<WindowState> mResizingWindows;
    final ArrayList<WindowState> mPendingRemove;
    WindowState[] mPendingRemoveTmp;
    final ArrayList<WindowState> mDestroySurface;
    ArrayList<WindowState> mLosingFocus;
    ArrayList<WindowState> mForceRemoves;
    ArrayList<Pair<WindowState, IRemoteCallback>> mWaitingForDrawn;
    final ArrayList<WindowState> mRelayoutWhileAnimating;
    WindowState[] mRebuildTmp;
    IInputMethodManager mInputMethodManager;
    DisplayMagnifier mDisplayMagnifier;
    final SurfaceSession mFxSession;
    Watermark mWatermark;
    StrictModeFlash mStrictModeFlash;
    FocusedStackFrame mFocusedStackFrame;
    int mFocusedStackLayer;
    final float[] mTmpFloats;
    final Rect mTmpContentRect;
    boolean mDisplayReady;
    boolean mSafeMode;
    boolean mDisplayEnabled;
    boolean mSystemBooted;
    boolean mForceDisplayEnabled;
    boolean mShowingBootMessages;
    String mLastANRState;
    SparseArray<DisplayContent> mDisplayContents;
    int mRotation;
    int mForcedAppOrientation;
    boolean mAltOrientation;
    ArrayList<IRotationWatcher> mRotationWatchers;
    int mDeferredRotationPauseCount;
    int mSystemDecorLayer;
    final Rect mScreenRect;
    boolean mTraversalScheduled;
    boolean mDisplayFrozen;
    long mDisplayFreezeTime;
    int mLastDisplayFreezeDuration;
    Object mLastFinishedFreezeSource;
    boolean mWaitingForConfig;
    boolean mWindowsFreezingScreen;
    boolean mClientFreezingScreen;
    int mAppsFreezingScreen;
    int mLastWindowForcedOrientation;
    int mLayoutSeq;
    int mLastStatusBarVisibility;
    boolean mFocusMayChange;
    Configuration mCurConfiguration;
    private final PowerManager.WakeLock mScreenFrozenLock;
    final AppTransition mAppTransition;
    boolean mStartingIconInTransition;
    boolean mSkipAppTransitionAnimation;
    final ArrayList<AppWindowToken> mOpeningApps;
    final ArrayList<AppWindowToken> mClosingApps;
    boolean mIsTouchDevice;
    final DisplayMetrics mDisplayMetrics;
    final DisplayMetrics mRealDisplayMetrics;
    final DisplayMetrics mTmpDisplayMetrics;
    final DisplayMetrics mCompatDisplayMetrics;
    final H mH;
    final Choreographer mChoreographer;
    WindowState mCurrentFocus;
    WindowState mLastFocus;
    WindowState mInputMethodTarget;
    boolean mInputMethodTargetWaitingAnim;
    int mInputMethodAnimLayerAdjustment;
    WindowState mInputMethodWindow;
    final ArrayList<WindowState> mInputMethodDialogs;
    boolean mHardKeyboardAvailable;
    boolean mHardKeyboardEnabled;
    OnHardKeyboardStatusChangeListener mHardKeyboardStatusChangeListener;
    final ArrayList<WindowToken> mWallpaperTokens;
    WindowState mWallpaperTarget;
    WindowState mLowerWallpaperTarget;
    WindowState mUpperWallpaperTarget;
    int mWallpaperAnimLayerAdjustment;
    float mLastWallpaperX;
    float mLastWallpaperY;
    float mLastWallpaperXStep;
    float mLastWallpaperYStep;
    WindowState mWaitingOnWallpaper;
    long mLastWallpaperTimeoutTime;
    static final long WALLPAPER_TIMEOUT = 150;
    static final long WALLPAPER_TIMEOUT_RECOVERY = 10000;
    AppWindowToken mFocusedApp;
    PowerManagerService mPowerManager;
    float mWindowAnimationScale;
    float mTransitionAnimationScale;
    float mAnimatorDurationScale;
    final InputManagerService mInputManager;
    final DisplayManagerService mDisplayManagerService;
    final DisplayManager mDisplayManager;
    Session mHoldingScreenOn;
    PowerManager.WakeLock mHoldingScreenWakeLock;
    boolean mTurnOnScreen;
    DragState mDragState;
    int mExitAnimId;
    int mEnterAnimId;
    final LayoutFields mInnerFields;
    boolean mAnimationScheduled;
    private int mTransactionSequence;
    private int mLayoutRepeatCount;
    final WindowAnimator mAnimator;
    SparseArray<Task> mTaskIdToTask;
    SparseArray<TaskStack> mStackIdToStack;
    private final PointerEventDispatcher mPointerEventDispatcher;
    boolean mInTouchMode;
    private ViewServer mViewServer;
    private final ArrayList<WindowChangeListener> mWindowChangeListeners;
    private boolean mWindowsChanged;
    final Configuration mTempConfiguration;
    float mCompatibleScreenScale;
    final boolean mOnlyCore;
    static final int ADJUST_WALLPAPER_LAYERS_CHANGED = 2;
    static final int ADJUST_WALLPAPER_VISIBILITY_CHANGED = 4;
    final InputMonitor mInputMonitor;
    private boolean mEventDispatchingEnabled;
    private boolean mInLayout;

    /* loaded from: WindowManagerService$OnHardKeyboardStatusChangeListener.class */
    public interface OnHardKeyboardStatusChangeListener {
        void onHardKeyboardStatusChange(boolean z, boolean z2);
    }

    /* loaded from: WindowManagerService$WindowChangeListener.class */
    public interface WindowChangeListener {
        void windowsChanged();

        void focusChanged();
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.<init>(android.content.Context, com.android.server.power.PowerManagerService, com.android.server.display.DisplayManagerService, com.android.server.input.InputManagerService, boolean, boolean, boolean):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private WindowManagerService(android.content.Context r1, com.android.server.power.PowerManagerService r2, com.android.server.display.DisplayManagerService r3, com.android.server.input.InputManagerService r4, boolean r5, boolean r6, boolean r7) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.<init>(android.content.Context, com.android.server.power.PowerManagerService, com.android.server.display.DisplayManagerService, com.android.server.input.InputManagerService, boolean, boolean, boolean):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.<init>(android.content.Context, com.android.server.power.PowerManagerService, com.android.server.display.DisplayManagerService, com.android.server.input.InputManagerService, boolean, boolean, boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setTransparentRegionWindow(com.android.server.wm.Session, android.view.IWindow, android.graphics.Region):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void setTransparentRegionWindow(com.android.server.wm.Session r1, android.view.IWindow r2, android.graphics.Region r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setTransparentRegionWindow(com.android.server.wm.Session, android.view.IWindow, android.graphics.Region):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.setTransparentRegionWindow(com.android.server.wm.Session, android.view.IWindow, android.graphics.Region):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setInsetsWindow(com.android.server.wm.Session, android.view.IWindow, int, android.graphics.Rect, android.graphics.Rect, android.graphics.Region):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void setInsetsWindow(com.android.server.wm.Session r1, android.view.IWindow r2, int r3, android.graphics.Rect r4, android.graphics.Rect r5, android.graphics.Region r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setInsetsWindow(com.android.server.wm.Session, android.view.IWindow, int, android.graphics.Rect, android.graphics.Rect, android.graphics.Region):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.setInsetsWindow(com.android.server.wm.Session, android.view.IWindow, int, android.graphics.Rect, android.graphics.Rect, android.graphics.Region):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.performDeferredDestroyWindow(com.android.server.wm.Session, android.view.IWindow):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void performDeferredDestroyWindow(com.android.server.wm.Session r1, android.view.IWindow r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.performDeferredDestroyWindow(com.android.server.wm.Session, android.view.IWindow):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.performDeferredDestroyWindow(com.android.server.wm.Session, android.view.IWindow):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.outOfMemoryWindow(com.android.server.wm.Session, android.view.IWindow):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public boolean outOfMemoryWindow(com.android.server.wm.Session r1, android.view.IWindow r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.outOfMemoryWindow(com.android.server.wm.Session, android.view.IWindow):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.outOfMemoryWindow(com.android.server.wm.Session, android.view.IWindow):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.finishDrawingWindow(com.android.server.wm.Session, android.view.IWindow):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void finishDrawingWindow(com.android.server.wm.Session r1, android.view.IWindow r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.finishDrawingWindow(com.android.server.wm.Session, android.view.IWindow):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.finishDrawingWindow(com.android.server.wm.Session, android.view.IWindow):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.updateOrientationFromAppTokensLocked(boolean):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean updateOrientationFromAppTokensLocked(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.updateOrientationFromAppTokensLocked(boolean):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.updateOrientationFromAppTokensLocked(boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setFocusedStackFrame():void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void setFocusedStackFrame() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.setFocusedStackFrame():void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.setFocusedStackFrame():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.moveTaskToTop(int):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void moveTaskToTop(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.moveTaskToTop(int):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.moveTaskToTop(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.moveTaskToBottom(int):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void moveTaskToBottom(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.moveTaskToBottom(int):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.moveTaskToBottom(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.startFreezingScreen(int, int):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.view.IWindowManager
    public void startFreezingScreen(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.startFreezingScreen(int, int):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.startFreezingScreen(int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.stopFreezingScreen():void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.view.IWindowManager
    public void stopFreezingScreen() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.stopFreezingScreen():void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.stopFreezingScreen():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.showStrictModeViolation(int, int):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    /* JADX INFO: Access modifiers changed from: private */
    public void showStrictModeViolation(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.showStrictModeViolation(int, int):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.showStrictModeViolation(int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.freezeRotation(int):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.view.IWindowManager
    public void freezeRotation(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.freezeRotation(int):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.freezeRotation(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.thawRotation():void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.view.IWindowManager
    public void thawRotation() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.thawRotation():void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.thawRotation():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.updateRotationUncheckedLocked(boolean):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public boolean updateRotationUncheckedLocked(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.updateRotationUncheckedLocked(boolean):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.updateRotationUncheckedLocked(boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerListWindows(java.net.Socket):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean viewServerListWindows(java.net.Socket r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerListWindows(java.net.Socket):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.viewServerListWindows(java.net.Socket):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerGetFocusedWindow(java.net.Socket):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean viewServerGetFocusedWindow(java.net.Socket r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerGetFocusedWindow(java.net.Socket):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.viewServerGetFocusedWindow(java.net.Socket):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerWindowCommand(java.net.Socket, java.lang.String, java.lang.String):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean viewServerWindowCommand(java.net.Socket r1, java.lang.String r2, java.lang.String r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.viewServerWindowCommand(java.net.Socket, java.lang.String, java.lang.String):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.viewServerWindowCommand(java.net.Socket, java.lang.String, java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.prepareDragSurface(android.view.IWindow, android.view.SurfaceSession, int, int, int, android.view.Surface):android.os.IBinder, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    android.os.IBinder prepareDragSurface(android.view.IWindow r1, android.view.SurfaceSession r2, int r3, int r4, int r5, android.view.Surface r6) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.prepareDragSurface(android.view.IWindow, android.view.SurfaceSession, int, int, int, android.view.Surface):android.os.IBinder, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.prepareDragSurface(android.view.IWindow, android.view.SurfaceSession, int, int, int, android.view.Surface):android.os.IBinder");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.performLayoutAndPlaceSurfacesLockedInner(boolean):void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private final void performLayoutAndPlaceSurfacesLockedInner(boolean r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.performLayoutAndPlaceSurfacesLockedInner(boolean):void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.performLayoutAndPlaceSurfacesLockedInner(boolean):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.reclaimSomeSurfaceMemoryLocked(com.android.server.wm.WindowStateAnimator, java.lang.String, boolean):boolean, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    boolean reclaimSomeSurfaceMemoryLocked(com.android.server.wm.WindowStateAnimator r1, java.lang.String r2, boolean r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.reclaimSomeSurfaceMemoryLocked(com.android.server.wm.WindowStateAnimator, java.lang.String, boolean):boolean, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.reclaimSomeSurfaceMemoryLocked(com.android.server.wm.WindowStateAnimator, java.lang.String, boolean):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.createWatermarkInTransaction():void, file: WindowManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void createWatermarkInTransaction() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.createWatermarkInTransaction():void, file: WindowManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.createWatermarkInTransaction():void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WindowManagerService$LayoutFields.class */
    public class LayoutFields {
        static final int SET_UPDATE_ROTATION = 1;
        static final int SET_WALLPAPER_MAY_CHANGE = 2;
        static final int SET_FORCE_HIDING_CHANGED = 4;
        static final int SET_ORIENTATION_CHANGE_COMPLETE = 8;
        static final int SET_TURN_ON_SCREEN = 16;
        static final int SET_WALLPAPER_ACTION_PENDING = 32;
        private static final int DISPLAY_CONTENT_UNKNOWN = 0;
        private static final int DISPLAY_CONTENT_MIRROR = 1;
        private static final int DISPLAY_CONTENT_UNIQUE = 2;
        boolean mWallpaperForceHidingChanged = false;
        boolean mWallpaperMayChange = false;
        boolean mOrientationChangeComplete = true;
        Object mLastWindowFreezeSource = null;
        private Session mHoldScreen = null;
        private boolean mObscured = false;
        private boolean mSyswin = false;
        private float mScreenBrightness = -1.0f;
        private float mButtonBrightness = -1.0f;
        private long mUserActivityTimeout = -1;
        private boolean mUpdateRotation = false;
        boolean mWallpaperActionPending = false;
        private int mDisplayHasContent = 0;

        LayoutFields() {
        }

        static /* synthetic */ Session access$1102(LayoutFields x0, Session x1) {
            x0.mHoldScreen = x1;
            return x1;
        }

        static /* synthetic */ float access$1302(LayoutFields x0, float x1) {
            x0.mScreenBrightness = x1;
            return x1;
        }

        static /* synthetic */ float access$1402(LayoutFields x0, float x1) {
            x0.mButtonBrightness = x1;
            return x1;
        }

        static /* synthetic */ long access$1502(LayoutFields x0, long x1) {
            x0.mUserActivityTimeout = x1;
            return x1;
        }

        static /* synthetic */ boolean access$1202(LayoutFields x0, boolean x1) {
            x0.mSyswin = x1;
            return x1;
        }

        static /* synthetic */ int access$1602(LayoutFields x0, int x1) {
            x0.mDisplayHasContent = x1;
            return x1;
        }

        static /* synthetic */ int access$1600(LayoutFields x0) {
            return x0.mDisplayHasContent;
        }

        static /* synthetic */ boolean access$1702(LayoutFields x0, boolean x1) {
            x0.mObscured = x1;
            return x1;
        }

        static /* synthetic */ boolean access$1700(LayoutFields x0) {
            return x0.mObscured;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WindowManagerService$DragInputEventReceiver.class */
    public final class DragInputEventReceiver extends InputEventReceiver {
        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.DragInputEventReceiver.onInputEvent(android.view.InputEvent):void, file: WindowManagerService$DragInputEventReceiver.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // android.view.InputEventReceiver
        public void onInputEvent(android.view.InputEvent r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowManagerService.DragInputEventReceiver.onInputEvent(android.view.InputEvent):void, file: WindowManagerService$DragInputEventReceiver.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.DragInputEventReceiver.onInputEvent(android.view.InputEvent):void");
        }

        public DragInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }
    }

    public static WindowManagerService main(final Context context, final PowerManagerService pm, final DisplayManagerService dm, final InputManagerService im, Handler wmHandler, final boolean haveInputMethods, final boolean showBootMsgs, final boolean onlyCore) {
        final WindowManagerService[] holder = new WindowManagerService[1];
        wmHandler.runWithScissors(new Runnable() { // from class: com.android.server.wm.WindowManagerService.2
            @Override // java.lang.Runnable
            public void run() {
                holder[0] = new WindowManagerService(context, pm, dm, im, haveInputMethods, showBootMsgs, onlyCore);
            }
        }, 0L);
        return holder[0];
    }

    private void initPolicy(Handler uiHandler) {
        uiHandler.runWithScissors(new Runnable() { // from class: com.android.server.wm.WindowManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                WindowManagerPolicyThread.set(Thread.currentThread(), Looper.myLooper());
                WindowManagerService.this.mPolicy.init(WindowManagerService.this.mContext, WindowManagerService.this, WindowManagerService.this);
                WindowManagerService.this.mAnimator.mAboveUniverseLayer = (WindowManagerService.this.mPolicy.getAboveUniverseLayer() * 10000) + 1000;
            }
        }, 0L);
    }

    public InputMonitor getInputMonitor() {
        return this.mInputMonitor;
    }

    @Override // android.view.IWindowManager.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Window Manager Crash", e);
            }
            throw e;
        }
    }

    private void placeWindowAfter(WindowState pos, WindowState window) {
        WindowList windows = pos.getWindowList();
        int i = windows.indexOf(pos);
        windows.add(i + 1, window);
        this.mWindowsChanged = true;
    }

    private void placeWindowBefore(WindowState pos, WindowState window) {
        WindowList windows = pos.getWindowList();
        int i = windows.indexOf(pos);
        if (i < 0) {
            Slog.w(TAG, "placeWindowBefore: Unable to find " + pos + " in " + windows);
            i = 0;
        }
        windows.add(i, window);
        this.mWindowsChanged = true;
    }

    private int findIdxBasedOnAppTokens(WindowState win) {
        WindowList windows = win.getWindowList();
        for (int j = windows.size() - 1; j >= 0; j--) {
            WindowState wentry = windows.get(j);
            if (wentry.mAppToken == win.mAppToken) {
                return j;
            }
        }
        return -1;
    }

    WindowList getTokenWindowsOnDisplay(WindowToken token, DisplayContent displayContent) {
        WindowList windowList = new WindowList();
        int count = token.windows.size();
        for (int i = 0; i < count; i++) {
            WindowState win = token.windows.get(i);
            if (win.mDisplayContent == displayContent) {
                windowList.add(win);
            }
        }
        return windowList;
    }

    private int indexOfWinInWindowList(WindowState targetWin, WindowList windows) {
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState w = windows.get(i);
            if (w == targetWin) {
                return i;
            }
            if (!w.mChildWindows.isEmpty() && indexOfWinInWindowList(targetWin, w.mChildWindows) >= 0) {
                return i;
            }
        }
        return -1;
    }

    private int addAppWindowToListLocked(WindowState win) {
        int NC;
        int tokenWindowsPos;
        IWindow iWindow = win.mClient;
        WindowToken token = win.mToken;
        DisplayContent displayContent = win.mDisplayContent;
        WindowList windows = win.getWindowList();
        int N = windows.size();
        WindowList tokenWindowList = getTokenWindowsOnDisplay(token, displayContent);
        int windowListPos = tokenWindowList.size();
        if (!tokenWindowList.isEmpty()) {
            if (win.mAttrs.type == 1) {
                WindowState lowestWindow = tokenWindowList.get(0);
                placeWindowBefore(lowestWindow, win);
                tokenWindowsPos = indexOfWinInWindowList(lowestWindow, token.windows);
            } else {
                AppWindowToken atoken = win.mAppToken;
                WindowState lastWindow = tokenWindowList.get(windowListPos - 1);
                if (atoken != null && lastWindow == atoken.startingWindow) {
                    placeWindowBefore(lastWindow, win);
                    tokenWindowsPos = indexOfWinInWindowList(lastWindow, token.windows);
                } else {
                    int newIdx = findIdxBasedOnAppTokens(win);
                    windows.add(newIdx + 1, win);
                    if (newIdx < 0) {
                        tokenWindowsPos = 0;
                    } else {
                        tokenWindowsPos = indexOfWinInWindowList(windows.get(newIdx), token.windows) + 1;
                    }
                    this.mWindowsChanged = true;
                }
            }
            return tokenWindowsPos;
        }
        WindowState pos = null;
        ArrayList<Task> tasks = displayContent.getTasks();
        int tokenNdx = -1;
        int taskNdx = tasks.size() - 1;
        while (taskNdx >= 0) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            tokenNdx = tokens.size() - 1;
            while (true) {
                if (tokenNdx < 0) {
                    break;
                }
                AppWindowToken t = tokens.get(tokenNdx);
                if (t == token) {
                    tokenNdx--;
                    if (tokenNdx < 0) {
                        taskNdx--;
                        if (taskNdx >= 0) {
                            tokenNdx = tasks.get(taskNdx).mAppTokens.size() - 1;
                        }
                    }
                } else {
                    WindowList tokenWindowList2 = getTokenWindowsOnDisplay(t, displayContent);
                    if (!t.sendingToBottom && tokenWindowList2.size() > 0) {
                        pos = tokenWindowList2.get(0);
                    }
                    tokenNdx--;
                }
            }
            if (tokenNdx >= 0) {
                break;
            }
            taskNdx--;
        }
        if (pos != null) {
            WindowToken atoken2 = this.mTokenMap.get(pos.mClient.asBinder());
            if (atoken2 != null) {
                WindowList tokenWindowList3 = getTokenWindowsOnDisplay(atoken2, displayContent);
                if (tokenWindowList3.size() > 0) {
                    WindowState bottom = tokenWindowList3.get(0);
                    if (bottom.mSubLayer < 0) {
                        pos = bottom;
                    }
                }
            }
            placeWindowBefore(pos, win);
            return 0;
        }
        while (taskNdx >= 0) {
            AppTokenList tokens2 = tasks.get(taskNdx).mAppTokens;
            while (true) {
                if (tokenNdx < 0) {
                    break;
                }
                WindowList tokenWindowList4 = getTokenWindowsOnDisplay(tokens2.get(tokenNdx), displayContent);
                int NW = tokenWindowList4.size();
                if (NW <= 0) {
                    tokenNdx--;
                } else {
                    pos = tokenWindowList4.get(NW - 1);
                    break;
                }
            }
            if (tokenNdx >= 0) {
                break;
            }
            taskNdx--;
        }
        if (pos != null) {
            WindowToken atoken3 = this.mTokenMap.get(pos.mClient.asBinder());
            if (atoken3 != null && (NC = atoken3.windows.size()) > 0) {
                WindowState top = atoken3.windows.get(NC - 1);
                if (top.mSubLayer >= 0) {
                    pos = top;
                }
            }
            placeWindowAfter(pos, win);
            return 0;
        }
        int myLayer = win.mBaseLayer;
        int i = 0;
        while (i < N) {
            WindowState w = windows.get(i);
            if (w.mBaseLayer > myLayer) {
                break;
            }
            i++;
        }
        windows.add(i, win);
        this.mWindowsChanged = true;
        return 0;
    }

    private void addFreeWindowToListLocked(WindowState win) {
        WindowList windows = win.getWindowList();
        int myLayer = win.mBaseLayer;
        int i = windows.size() - 1;
        while (i >= 0 && windows.get(i).mBaseLayer > myLayer) {
            i--;
        }
        windows.add(i + 1, win);
        this.mWindowsChanged = true;
    }

    private void addAttachedWindowToListLocked(WindowState win, boolean addToToken) {
        WindowToken token = win.mToken;
        DisplayContent displayContent = win.mDisplayContent;
        WindowState attached = win.mAttachedWindow;
        WindowList tokenWindowList = getTokenWindowsOnDisplay(token, displayContent);
        int NA = tokenWindowList.size();
        int sublayer = win.mSubLayer;
        int largestSublayer = Integer.MIN_VALUE;
        WindowState windowWithLargestSublayer = null;
        int i = 0;
        while (true) {
            if (i >= NA) {
                break;
            }
            WindowState w = tokenWindowList.get(i);
            int wSublayer = w.mSubLayer;
            if (wSublayer >= largestSublayer) {
                largestSublayer = wSublayer;
                windowWithLargestSublayer = w;
            }
            if (sublayer < 0) {
                if (wSublayer < sublayer) {
                    i++;
                } else {
                    if (addToToken) {
                        token.windows.add(i, win);
                    }
                    placeWindowBefore(wSublayer >= 0 ? attached : w, win);
                }
            } else if (wSublayer <= sublayer) {
                i++;
            } else {
                if (addToToken) {
                    token.windows.add(i, win);
                }
                placeWindowBefore(w, win);
            }
        }
        if (i >= NA) {
            if (addToToken) {
                token.windows.add(win);
            }
            if (sublayer < 0) {
                placeWindowBefore(attached, win);
            } else {
                placeWindowAfter(largestSublayer >= 0 ? windowWithLargestSublayer : attached, win);
            }
        }
    }

    private void addWindowToListInOrderLocked(WindowState win, boolean addToToken) {
        if (win.mAttachedWindow == null) {
            WindowToken token = win.mToken;
            int tokenWindowsPos = 0;
            if (token.appWindowToken != null) {
                tokenWindowsPos = addAppWindowToListLocked(win);
            } else {
                addFreeWindowToListLocked(win);
            }
            if (addToToken) {
                token.windows.add(tokenWindowsPos, win);
            }
        } else {
            addAttachedWindowToListLocked(win, addToToken);
        }
        if (win.mAppToken != null && addToToken) {
            win.mAppToken.allAppWindows.add(win);
        }
    }

    static boolean canBeImeTarget(WindowState w) {
        int fl = w.mAttrs.flags & 131080;
        if (fl == 0 || fl == 131080 || w.mAttrs.type == 3) {
            return w.isVisibleOrAdding();
        }
        return false;
    }

    int findDesiredInputMethodWindowIndexLocked(boolean willMove) {
        WindowList windows = getDefaultWindowListLocked();
        WindowState w = null;
        int i = windows.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            WindowState win = windows.get(i);
            if (canBeImeTarget(win)) {
                w = win;
                if (!willMove && w.mAttrs.type == 3 && i > 0) {
                    WindowState wb = windows.get(i - 1);
                    if (wb.mAppToken == w.mAppToken && canBeImeTarget(wb)) {
                        i--;
                        w = wb;
                    }
                }
            } else {
                i--;
            }
        }
        WindowState curTarget = this.mInputMethodTarget;
        if (curTarget != null && curTarget.isDisplayedLw() && curTarget.isClosing() && (w == null || curTarget.mWinAnimator.mAnimLayer > w.mWinAnimator.mAnimLayer)) {
            return windows.indexOf(curTarget) + 1;
        }
        if (willMove && w != null) {
            AppWindowToken token = curTarget == null ? null : curTarget.mAppToken;
            if (token != null) {
                WindowState highestTarget = null;
                int highestPos = 0;
                if (token.mAppAnimator.animating || token.mAppAnimator.animation != null) {
                    WindowList curWindows = curTarget.getWindowList();
                    for (int pos = curWindows.indexOf(curTarget); pos >= 0; pos--) {
                        WindowState win2 = curWindows.get(pos);
                        if (win2.mAppToken != token) {
                            break;
                        }
                        if (!win2.mRemoved && (highestTarget == null || win2.mWinAnimator.mAnimLayer > highestTarget.mWinAnimator.mAnimLayer)) {
                            highestTarget = win2;
                            highestPos = pos;
                        }
                    }
                }
                if (highestTarget != null) {
                    if (this.mAppTransition.isTransitionSet()) {
                        this.mInputMethodTargetWaitingAnim = true;
                        this.mInputMethodTarget = highestTarget;
                        return highestPos + 1;
                    } else if (highestTarget.mWinAnimator.isAnimating() && highestTarget.mWinAnimator.mAnimLayer > w.mWinAnimator.mAnimLayer) {
                        this.mInputMethodTargetWaitingAnim = true;
                        this.mInputMethodTarget = highestTarget;
                        return highestPos + 1;
                    }
                }
            }
        }
        if (w != null) {
            if (willMove) {
                this.mInputMethodTarget = w;
                this.mInputMethodTargetWaitingAnim = false;
                if (w.mAppToken != null) {
                    setInputMethodAnimLayerAdjustment(w.mAppToken.mAppAnimator.animLayerAdjustment);
                } else {
                    setInputMethodAnimLayerAdjustment(0);
                }
            }
            return i + 1;
        } else if (willMove) {
            this.mInputMethodTarget = null;
            setInputMethodAnimLayerAdjustment(0);
            return -1;
        } else {
            return -1;
        }
    }

    void addInputMethodWindowToListLocked(WindowState win) {
        int pos = findDesiredInputMethodWindowIndexLocked(true);
        if (pos >= 0) {
            win.mTargetAppToken = this.mInputMethodTarget.mAppToken;
            getDefaultWindowListLocked().add(pos, win);
            this.mWindowsChanged = true;
            moveInputMethodDialogsLocked(pos + 1);
            return;
        }
        win.mTargetAppToken = null;
        addWindowToListInOrderLocked(win, true);
        moveInputMethodDialogsLocked(pos);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInputMethodAnimLayerAdjustment(int adj) {
        this.mInputMethodAnimLayerAdjustment = adj;
        WindowState imw = this.mInputMethodWindow;
        if (imw != null) {
            imw.mWinAnimator.mAnimLayer = imw.mLayer + adj;
            int wi = imw.mChildWindows.size();
            while (wi > 0) {
                wi--;
                WindowState cw = imw.mChildWindows.get(wi);
                cw.mWinAnimator.mAnimLayer = cw.mLayer + adj;
            }
        }
        int di = this.mInputMethodDialogs.size();
        while (di > 0) {
            di--;
            WindowState imw2 = this.mInputMethodDialogs.get(di);
            imw2.mWinAnimator.mAnimLayer = imw2.mLayer + adj;
        }
    }

    private int tmpRemoveWindowLocked(int interestingPos, WindowState win) {
        WindowList windows = win.getWindowList();
        int wpos = windows.indexOf(win);
        if (wpos >= 0) {
            if (wpos < interestingPos) {
                interestingPos--;
            }
            windows.remove(wpos);
            this.mWindowsChanged = true;
            int NC = win.mChildWindows.size();
            while (NC > 0) {
                NC--;
                WindowState cw = win.mChildWindows.get(NC);
                int cpos = windows.indexOf(cw);
                if (cpos >= 0) {
                    if (cpos < interestingPos) {
                        interestingPos--;
                    }
                    windows.remove(cpos);
                }
            }
        }
        return interestingPos;
    }

    private void reAddWindowToListInOrderLocked(WindowState win) {
        addWindowToListInOrderLocked(win, false);
        WindowList windows = win.getWindowList();
        int wpos = windows.indexOf(win);
        if (wpos >= 0) {
            windows.remove(wpos);
            this.mWindowsChanged = true;
            reAddWindowLocked(wpos, win);
        }
    }

    void logWindowList(WindowList windows, String prefix) {
        int N = windows.size();
        while (N > 0) {
            N--;
            Slog.v(TAG, prefix + Separators.POUND + N + ": " + windows.get(N));
        }
    }

    void moveInputMethodDialogsLocked(int pos) {
        ArrayList<WindowState> dialogs = this.mInputMethodDialogs;
        WindowList windows = getDefaultWindowListLocked();
        int N = dialogs.size();
        for (int i = 0; i < N; i++) {
            pos = tmpRemoveWindowLocked(pos, dialogs.get(i));
        }
        if (pos >= 0) {
            AppWindowToken targetAppToken = this.mInputMethodTarget.mAppToken;
            if (pos < windows.size()) {
                WindowState wp = windows.get(pos);
                if (wp == this.mInputMethodWindow) {
                    pos++;
                }
            }
            for (int i2 = 0; i2 < N; i2++) {
                WindowState win = dialogs.get(i2);
                win.mTargetAppToken = targetAppToken;
                pos = reAddWindowLocked(pos, win);
            }
            return;
        }
        for (int i3 = 0; i3 < N; i3++) {
            WindowState win2 = dialogs.get(i3);
            win2.mTargetAppToken = null;
            reAddWindowToListInOrderLocked(win2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean moveInputMethodWindowsIfNeededLocked(boolean needAssignLayers) {
        WindowState imWin = this.mInputMethodWindow;
        int DN = this.mInputMethodDialogs.size();
        if (imWin == null && DN == 0) {
            return false;
        }
        WindowList windows = getDefaultWindowListLocked();
        int imPos = findDesiredInputMethodWindowIndexLocked(true);
        if (imPos >= 0) {
            int N = windows.size();
            WindowState firstImWin = imPos < N ? windows.get(imPos) : null;
            WindowState baseImWin = imWin != null ? imWin : this.mInputMethodDialogs.get(0);
            if (baseImWin.mChildWindows.size() > 0) {
                WindowState cw = baseImWin.mChildWindows.get(0);
                if (cw.mSubLayer < 0) {
                    baseImWin = cw;
                }
            }
            if (firstImWin == baseImWin) {
                int pos = imPos + 1;
                while (pos < N && windows.get(pos).mIsImWindow) {
                    pos++;
                }
                while (true) {
                    pos++;
                    if (pos >= N || windows.get(pos).mIsImWindow) {
                        break;
                    }
                }
                if (pos >= N) {
                    if (imWin != null) {
                        imWin.mTargetAppToken = this.mInputMethodTarget.mAppToken;
                        return false;
                    }
                    return false;
                }
            }
            if (imWin != null) {
                int imPos2 = tmpRemoveWindowLocked(imPos, imWin);
                imWin.mTargetAppToken = this.mInputMethodTarget.mAppToken;
                reAddWindowLocked(imPos2, imWin);
                if (DN > 0) {
                    moveInputMethodDialogsLocked(imPos2 + 1);
                }
            } else {
                moveInputMethodDialogsLocked(imPos);
            }
        } else if (imWin != null) {
            tmpRemoveWindowLocked(0, imWin);
            imWin.mTargetAppToken = null;
            reAddWindowToListInOrderLocked(imWin);
            if (DN > 0) {
                moveInputMethodDialogsLocked(-1);
            }
        } else {
            moveInputMethodDialogsLocked(-1);
        }
        if (needAssignLayers) {
            assignLayersLocked(windows);
            return true;
        }
        return true;
    }

    final boolean isWallpaperVisible(WindowState wallpaperTarget) {
        return ((wallpaperTarget == null || (wallpaperTarget.mObscured && (wallpaperTarget.mAppToken == null || wallpaperTarget.mAppToken.mAppAnimator.animation == null))) && this.mUpperWallpaperTarget == null && this.mLowerWallpaperTarget == null) ? false : true;
    }

    int adjustWallpaperWindowsLocked() {
        WindowState foundW;
        int type;
        int oldI;
        this.mInnerFields.mWallpaperMayChange = false;
        boolean targetChanged = false;
        DisplayInfo displayInfo = getDefaultDisplayContentLocked().getDisplayInfo();
        int dw = displayInfo.logicalWidth;
        int dh = displayInfo.logicalHeight;
        WindowList windows = getDefaultWindowListLocked();
        int N = windows.size();
        WindowState w = null;
        WindowState foundW2 = null;
        int foundI = 0;
        WindowState topCurW = null;
        int topCurI = 0;
        int windowDetachedI = -1;
        int i = N;
        while (i > 0) {
            i--;
            w = windows.get(i);
            if (w.mAttrs.type == 2013) {
                if (topCurW == null) {
                    topCurW = w;
                    topCurI = i;
                }
            } else {
                topCurW = null;
                if (w == this.mAnimator.mWindowDetachedWallpaper || w.mAppToken == null || !w.mAppToken.hidden || w.mAppToken.mAppAnimator.animation != null) {
                    if ((w.mAttrs.flags & 1048576) != 0 && w.isOnScreen() && (this.mWallpaperTarget == w || w.isDrawFinishedLw())) {
                        foundW2 = w;
                        foundI = i;
                        if (w != this.mWallpaperTarget || !w.mWinAnimator.isAnimating()) {
                            break;
                        }
                    } else if (w == this.mAnimator.mWindowDetachedWallpaper) {
                        windowDetachedI = i;
                    }
                }
            }
        }
        if (foundW2 == null && windowDetachedI >= 0) {
            foundW2 = w;
            foundI = windowDetachedI;
        }
        if (this.mWallpaperTarget != foundW2 && (this.mLowerWallpaperTarget == null || this.mLowerWallpaperTarget != foundW2)) {
            this.mLowerWallpaperTarget = null;
            this.mUpperWallpaperTarget = null;
            WindowState oldW = this.mWallpaperTarget;
            this.mWallpaperTarget = foundW2;
            targetChanged = true;
            if (foundW2 != null && oldW != null) {
                boolean oldAnim = oldW.isAnimatingLw();
                boolean foundAnim = foundW2.isAnimatingLw();
                if (foundAnim && oldAnim && (oldI = windows.indexOf(oldW)) >= 0) {
                    if (foundW2.mAppToken != null && foundW2.mAppToken.hiddenRequested) {
                        this.mWallpaperTarget = oldW;
                        foundW2 = oldW;
                        foundI = oldI;
                    } else if (foundI > oldI) {
                        this.mUpperWallpaperTarget = foundW2;
                        this.mLowerWallpaperTarget = oldW;
                        foundW2 = oldW;
                        foundI = oldI;
                    } else {
                        this.mUpperWallpaperTarget = oldW;
                        this.mLowerWallpaperTarget = foundW2;
                    }
                }
            }
        } else if (this.mLowerWallpaperTarget != null && (!this.mLowerWallpaperTarget.isAnimatingLw() || !this.mUpperWallpaperTarget.isAnimatingLw())) {
            this.mLowerWallpaperTarget = null;
            this.mUpperWallpaperTarget = null;
            this.mWallpaperTarget = foundW2;
            targetChanged = true;
        }
        boolean visible = foundW2 != null;
        if (visible) {
            visible = isWallpaperVisible(foundW2);
            this.mWallpaperAnimLayerAdjustment = (this.mLowerWallpaperTarget != null || foundW2.mAppToken == null) ? 0 : foundW2.mAppToken.mAppAnimator.animLayerAdjustment;
            int maxLayer = (this.mPolicy.getMaxWallpaperLayer() * 10000) + 1000;
            while (foundI > 0) {
                WindowState wb = windows.get(foundI - 1);
                if (wb.mBaseLayer < maxLayer && wb.mAttachedWindow != foundW2 && ((foundW2.mAttachedWindow == null || wb.mAttachedWindow != foundW2.mAttachedWindow) && (wb.mAttrs.type != 3 || foundW2.mToken == null || wb.mToken != foundW2.mToken))) {
                    break;
                }
                foundW2 = wb;
                foundI--;
            }
        }
        if (foundW2 == null && topCurW != null) {
            foundW = topCurW;
            foundI = topCurI + 1;
        } else {
            foundW = foundI > 0 ? windows.get(foundI - 1) : null;
        }
        if (visible) {
            if (this.mWallpaperTarget.mWallpaperX >= 0.0f) {
                this.mLastWallpaperX = this.mWallpaperTarget.mWallpaperX;
                this.mLastWallpaperXStep = this.mWallpaperTarget.mWallpaperXStep;
            }
            if (this.mWallpaperTarget.mWallpaperY >= 0.0f) {
                this.mLastWallpaperY = this.mWallpaperTarget.mWallpaperY;
                this.mLastWallpaperYStep = this.mWallpaperTarget.mWallpaperYStep;
            }
        }
        int changed = 0;
        int curTokenIndex = this.mWallpaperTokens.size();
        while (curTokenIndex > 0) {
            curTokenIndex--;
            WindowToken token = this.mWallpaperTokens.get(curTokenIndex);
            if (token.hidden == visible) {
                changed |= 4;
                token.hidden = !visible;
                getDefaultDisplayContentLocked().layoutNeeded = true;
            }
            int curWallpaperIndex = token.windows.size();
            while (curWallpaperIndex > 0) {
                curWallpaperIndex--;
                WindowState wallpaper = token.windows.get(curWallpaperIndex);
                if (visible) {
                    updateWallpaperOffsetLocked(wallpaper, dw, dh, false);
                }
                dispatchWallpaperVisibility(wallpaper, visible);
                wallpaper.mWinAnimator.mAnimLayer = wallpaper.mLayer + this.mWallpaperAnimLayerAdjustment;
                if (wallpaper == foundW) {
                    foundI--;
                    foundW = foundI > 0 ? windows.get(foundI - 1) : null;
                } else {
                    int oldIndex = windows.indexOf(wallpaper);
                    if (oldIndex >= 0) {
                        windows.remove(oldIndex);
                        this.mWindowsChanged = true;
                        if (oldIndex < foundI) {
                            foundI--;
                        }
                    }
                    int insertionIndex = 0;
                    if (visible && foundW != null && ((type = foundW.mAttrs.type) == 2004 || type == 2029)) {
                        insertionIndex = windows.indexOf(foundW);
                    }
                    windows.add(insertionIndex, wallpaper);
                    this.mWindowsChanged = true;
                    changed |= 2;
                }
            }
        }
        if (targetChanged) {
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWallpaperAnimLayerAdjustmentLocked(int adj) {
        this.mWallpaperAnimLayerAdjustment = adj;
        int curTokenIndex = this.mWallpaperTokens.size();
        while (curTokenIndex > 0) {
            curTokenIndex--;
            WindowToken token = this.mWallpaperTokens.get(curTokenIndex);
            int curWallpaperIndex = token.windows.size();
            while (curWallpaperIndex > 0) {
                curWallpaperIndex--;
                WindowState wallpaper = token.windows.get(curWallpaperIndex);
                wallpaper.mWinAnimator.mAnimLayer = wallpaper.mLayer + adj;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean updateWallpaperOffsetLocked(WindowState wallpaperWin, int dw, int dh, boolean sync) {
        boolean rawChanged = false;
        float wpx = this.mLastWallpaperX >= 0.0f ? this.mLastWallpaperX : 0.5f;
        float wpxs = this.mLastWallpaperXStep >= 0.0f ? this.mLastWallpaperXStep : -1.0f;
        int availw = (wallpaperWin.mFrame.right - wallpaperWin.mFrame.left) - dw;
        int offset = availw > 0 ? -((int) ((availw * wpx) + 0.5f)) : 0;
        boolean changed = wallpaperWin.mXOffset != offset;
        if (changed) {
            wallpaperWin.mXOffset = offset;
        }
        if (wallpaperWin.mWallpaperX != wpx || wallpaperWin.mWallpaperXStep != wpxs) {
            wallpaperWin.mWallpaperX = wpx;
            wallpaperWin.mWallpaperXStep = wpxs;
            rawChanged = true;
        }
        float wpy = this.mLastWallpaperY >= 0.0f ? this.mLastWallpaperY : 0.5f;
        float wpys = this.mLastWallpaperYStep >= 0.0f ? this.mLastWallpaperYStep : -1.0f;
        int availh = (wallpaperWin.mFrame.bottom - wallpaperWin.mFrame.top) - dh;
        int offset2 = availh > 0 ? -((int) ((availh * wpy) + 0.5f)) : 0;
        if (wallpaperWin.mYOffset != offset2) {
            changed = true;
            wallpaperWin.mYOffset = offset2;
        }
        if (wallpaperWin.mWallpaperY != wpy || wallpaperWin.mWallpaperYStep != wpys) {
            wallpaperWin.mWallpaperY = wpy;
            wallpaperWin.mWallpaperYStep = wpys;
            rawChanged = true;
        }
        if (rawChanged && (wallpaperWin.mAttrs.privateFlags & 4) != 0) {
            if (sync) {
                try {
                    this.mWaitingOnWallpaper = wallpaperWin;
                } catch (RemoteException e) {
                }
            }
            wallpaperWin.mClient.dispatchWallpaperOffsets(wallpaperWin.mWallpaperX, wallpaperWin.mWallpaperY, wallpaperWin.mWallpaperXStep, wallpaperWin.mWallpaperYStep, sync);
            if (sync && this.mWaitingOnWallpaper != null) {
                long start = SystemClock.uptimeMillis();
                if (this.mLastWallpaperTimeoutTime + WALLPAPER_TIMEOUT_RECOVERY < start) {
                    try {
                        this.mWindowMap.wait(WALLPAPER_TIMEOUT);
                    } catch (InterruptedException e2) {
                    }
                    if (start + WALLPAPER_TIMEOUT < SystemClock.uptimeMillis()) {
                        Slog.i(TAG, "Timeout waiting for wallpaper to offset: " + wallpaperWin);
                        this.mLastWallpaperTimeoutTime = start;
                    }
                }
                this.mWaitingOnWallpaper = null;
            }
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void wallpaperOffsetsComplete(IBinder window) {
        synchronized (this.mWindowMap) {
            if (this.mWaitingOnWallpaper != null && this.mWaitingOnWallpaper.mClient.asBinder() == window) {
                this.mWaitingOnWallpaper = null;
                this.mWindowMap.notifyAll();
            }
        }
    }

    void updateWallpaperOffsetLocked(WindowState changingTarget, boolean sync) {
        DisplayContent displayContent = changingTarget.mDisplayContent;
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        int dw = displayInfo.logicalWidth;
        int dh = displayInfo.logicalHeight;
        WindowState target = this.mWallpaperTarget;
        if (target != null) {
            if (target.mWallpaperX >= 0.0f) {
                this.mLastWallpaperX = target.mWallpaperX;
            } else if (changingTarget.mWallpaperX >= 0.0f) {
                this.mLastWallpaperX = changingTarget.mWallpaperX;
            }
            if (target.mWallpaperY >= 0.0f) {
                this.mLastWallpaperY = target.mWallpaperY;
            } else if (changingTarget.mWallpaperY >= 0.0f) {
                this.mLastWallpaperY = changingTarget.mWallpaperY;
            }
        }
        int curTokenIndex = this.mWallpaperTokens.size();
        while (curTokenIndex > 0) {
            curTokenIndex--;
            WindowToken token = this.mWallpaperTokens.get(curTokenIndex);
            int curWallpaperIndex = token.windows.size();
            while (curWallpaperIndex > 0) {
                curWallpaperIndex--;
                WindowState wallpaper = token.windows.get(curWallpaperIndex);
                if (updateWallpaperOffsetLocked(wallpaper, dw, dh, sync)) {
                    WindowStateAnimator winAnimator = wallpaper.mWinAnimator;
                    winAnimator.computeShownFrameLocked();
                    winAnimator.setWallpaperOffset(wallpaper.mShownFrame);
                    sync = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchWallpaperVisibility(WindowState wallpaper, boolean visible) {
        if (wallpaper.mWallpaperVisible != visible) {
            wallpaper.mWallpaperVisible = visible;
            try {
                wallpaper.mClient.dispatchAppVisibility(visible);
            } catch (RemoteException e) {
            }
        }
    }

    void updateWallpaperVisibilityLocked() {
        boolean visible = isWallpaperVisible(this.mWallpaperTarget);
        DisplayContent displayContent = this.mWallpaperTarget.mDisplayContent;
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        int dw = displayInfo.logicalWidth;
        int dh = displayInfo.logicalHeight;
        int curTokenIndex = this.mWallpaperTokens.size();
        while (curTokenIndex > 0) {
            curTokenIndex--;
            WindowToken token = this.mWallpaperTokens.get(curTokenIndex);
            if (token.hidden == visible) {
                token.hidden = !visible;
                getDefaultDisplayContentLocked().layoutNeeded = true;
            }
            int curWallpaperIndex = token.windows.size();
            while (curWallpaperIndex > 0) {
                curWallpaperIndex--;
                WindowState wallpaper = token.windows.get(curWallpaperIndex);
                if (visible) {
                    updateWallpaperOffsetLocked(wallpaper, dw, dh, false);
                }
                dispatchWallpaperVisibility(wallpaper, visible);
            }
        }
    }

    public int addWindow(Session session, IWindow client, int seq, WindowManager.LayoutParams attrs, int viewVisibility, int displayId, Rect outContentInsets, InputChannel outInputChannel) {
        int[] appOp = new int[1];
        int res = this.mPolicy.checkAddPermission(attrs, appOp);
        if (res != 0) {
            return res;
        }
        boolean reportNewConfig = false;
        WindowState attachedWindow = null;
        int type = attrs.type;
        synchronized (this.mWindowMap) {
            if (!this.mDisplayReady) {
                throw new IllegalStateException("Display has not been initialialized");
            }
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent == null) {
                Slog.w(TAG, "Attempted to add window to a display that does not exist: " + displayId + ".  Aborting.");
                return -9;
            } else if (!displayContent.hasAccess(session.mUid)) {
                Slog.w(TAG, "Attempted to add window to a display for which the application does not have access: " + displayId + ".  Aborting.");
                return -9;
            } else if (this.mWindowMap.containsKey(client.asBinder())) {
                Slog.w(TAG, "Window " + client + " is already added");
                return -5;
            } else {
                if (type >= 1000 && type <= 1999) {
                    attachedWindow = windowForClientLocked((Session) null, attrs.token, false);
                    if (attachedWindow == null) {
                        Slog.w(TAG, "Attempted to add window with token that is not a window: " + attrs.token + ".  Aborting.");
                        return -2;
                    } else if (attachedWindow.mAttrs.type >= 1000 && attachedWindow.mAttrs.type <= 1999) {
                        Slog.w(TAG, "Attempted to add window with token that is a sub-window: " + attrs.token + ".  Aborting.");
                        return -2;
                    }
                }
                if (type == 2030 && !displayContent.isPrivate()) {
                    Slog.w(TAG, "Attempted to add private presentation window to a non-private display.  Aborting.");
                    return -8;
                }
                boolean addToken = false;
                WindowToken token = this.mTokenMap.get(attrs.token);
                if (token == null) {
                    if (type >= 1 && type <= 99) {
                        Slog.w(TAG, "Attempted to add application window with unknown token " + attrs.token + ".  Aborting.");
                        return -1;
                    } else if (type == 2011) {
                        Slog.w(TAG, "Attempted to add input method window with unknown token " + attrs.token + ".  Aborting.");
                        return -1;
                    } else if (type == 2013) {
                        Slog.w(TAG, "Attempted to add wallpaper window with unknown token " + attrs.token + ".  Aborting.");
                        return -1;
                    } else if (type == 2023) {
                        Slog.w(TAG, "Attempted to add Dream window with unknown token " + attrs.token + ".  Aborting.");
                        return -1;
                    } else {
                        token = new WindowToken(this, attrs.token, -1, false);
                        addToken = true;
                    }
                } else if (type >= 1 && type <= 99) {
                    AppWindowToken atoken = token.appWindowToken;
                    if (atoken == null) {
                        Slog.w(TAG, "Attempted to add window with non-application token " + token + ".  Aborting.");
                        return -3;
                    } else if (atoken.removed) {
                        Slog.w(TAG, "Attempted to add window with exiting application token " + token + ".  Aborting.");
                        return -4;
                    } else if (type == 3 && atoken.firstWindowDrawn) {
                        return -6;
                    }
                } else if (type == 2011) {
                    if (token.windowType != 2011) {
                        Slog.w(TAG, "Attempted to add input method window with bad token " + attrs.token + ".  Aborting.");
                        return -1;
                    }
                } else if (type == 2013) {
                    if (token.windowType != 2013) {
                        Slog.w(TAG, "Attempted to add wallpaper window with bad token " + attrs.token + ".  Aborting.");
                        return -1;
                    }
                } else if (type == 2023 && token.windowType != 2023) {
                    Slog.w(TAG, "Attempted to add Dream window with bad token " + attrs.token + ".  Aborting.");
                    return -1;
                }
                WindowState win = new WindowState(this, session, client, token, attachedWindow, appOp[0], seq, attrs, viewVisibility, displayContent);
                if (win.mDeathRecipient == null) {
                    Slog.w(TAG, "Adding window client " + client.asBinder() + " that is dead, aborting.");
                    return -4;
                }
                this.mPolicy.adjustWindowParamsLw(win.mAttrs);
                win.setShowToOwnerOnlyLocked(this.mPolicy.checkShowToOwnerOnly(attrs));
                int res2 = this.mPolicy.prepareAddWindowLw(win, attrs);
                if (res2 != 0) {
                    return res2;
                }
                if (outInputChannel != null && (attrs.inputFeatures & 2) == 0) {
                    String name = win.makeInputChannelName();
                    InputChannel[] inputChannels = InputChannel.openInputChannelPair(name);
                    win.setInputChannel(inputChannels[0]);
                    inputChannels[1].transferTo(outInputChannel);
                    this.mInputManager.registerInputChannel(win.mInputChannel, win.mInputWindowHandle);
                }
                int res3 = 0;
                long origId = Binder.clearCallingIdentity();
                if (addToken) {
                    this.mTokenMap.put(attrs.token, token);
                }
                win.attach();
                this.mWindowMap.put(client.asBinder(), win);
                if (win.mAppOp != -1 && this.mAppOps.startOpNoThrow(win.mAppOp, win.getOwningUid(), win.getOwningPackage()) != 0) {
                    win.setAppOpVisibilityLw(false);
                }
                if (type == 3 && token.appWindowToken != null) {
                    token.appWindowToken.startingWindow = win;
                    Message m = this.mH.obtainMessage(33, token.appWindowToken);
                    this.mH.sendMessageDelayed(m, WALLPAPER_TIMEOUT_RECOVERY);
                }
                boolean imMayMove = true;
                if (type == 2011) {
                    win.mGivenInsetsPending = true;
                    this.mInputMethodWindow = win;
                    addInputMethodWindowToListLocked(win);
                    imMayMove = false;
                } else if (type == 2012) {
                    this.mInputMethodDialogs.add(win);
                    addWindowToListInOrderLocked(win, true);
                    moveInputMethodDialogsLocked(findDesiredInputMethodWindowIndexLocked(true));
                    imMayMove = false;
                } else {
                    addWindowToListInOrderLocked(win, true);
                    if (type == 2013) {
                        this.mLastWallpaperTimeoutTime = 0L;
                        displayContent.pendingLayoutChanges |= 4;
                    } else if ((attrs.flags & 1048576) != 0) {
                        displayContent.pendingLayoutChanges |= 4;
                    } else if (this.mWallpaperTarget != null && this.mWallpaperTarget.mLayer >= win.mBaseLayer) {
                        displayContent.pendingLayoutChanges |= 4;
                    }
                }
                win.mWinAnimator.mEnterAnimationPending = true;
                if (displayContent.isDefaultDisplay) {
                    this.mPolicy.getContentInsetHintLw(attrs, outContentInsets);
                } else {
                    outContentInsets.setEmpty();
                }
                if (this.mInTouchMode) {
                    res3 = 0 | 1;
                }
                if (win.mAppToken == null || !win.mAppToken.clientHidden) {
                    res3 |= 2;
                }
                this.mInputMonitor.setUpdateInputWindowsNeededLw();
                boolean focusChanged = false;
                if (win.canReceiveKeys()) {
                    focusChanged = updateFocusedWindowLocked(1, false);
                    if (focusChanged) {
                        imMayMove = false;
                    }
                }
                if (imMayMove) {
                    moveInputMethodWindowsIfNeededLocked(false);
                }
                assignLayersLocked(displayContent.getWindowList());
                if (focusChanged) {
                    finishUpdateFocusedWindowAfterAssignLayersLocked(false);
                }
                this.mInputMonitor.updateInputWindowsLw(false);
                if (win.isVisibleOrAdding() && updateOrientationFromAppTokensLocked(false)) {
                    reportNewConfig = true;
                }
                if (reportNewConfig) {
                    sendNewConfiguration();
                }
                Binder.restoreCallingIdentity(origId);
                return res3;
            }
        }
    }

    public void removeWindow(Session session, IWindow client) {
        synchronized (this.mWindowMap) {
            WindowState win = windowForClientLocked(session, client, false);
            if (win == null) {
                return;
            }
            removeWindowLocked(session, win);
        }
    }

    public void removeWindowLocked(Session session, WindowState win) {
        if (win.mAttrs.type == 3) {
            removeStartingWindowTimeout(win.mAppToken);
        }
        long origId = Binder.clearCallingIdentity();
        win.disposeInputChannel();
        boolean wasVisible = false;
        if (win.mHasSurface && okToDisplay()) {
            wasVisible = win.isWinVisibleLw();
            if (wasVisible) {
                int transit = 2;
                if (win.mAttrs.type == 3) {
                    transit = 5;
                }
                if (win.mWinAnimator.applyAnimationLocked(transit, false)) {
                    win.mExiting = true;
                }
                if (this.mDisplayMagnifier != null && win.getDisplayId() == 0) {
                    this.mDisplayMagnifier.onWindowTransitionLocked(win, transit);
                }
            }
            if (win.mExiting || win.mWinAnimator.isAnimating()) {
                win.mExiting = true;
                win.mRemoveOnExit = true;
                win.mDisplayContent.layoutNeeded = true;
                updateFocusedWindowLocked(3, false);
                performLayoutAndPlaceSurfacesLocked();
                if (win.mAppToken != null) {
                    win.mAppToken.updateReportedVisibilityLocked();
                }
                Binder.restoreCallingIdentity(origId);
                return;
            }
        }
        removeWindowInnerLocked(session, win);
        if (wasVisible && updateOrientationFromAppTokensLocked(false)) {
            this.mH.sendEmptyMessage(18);
        }
        updateFocusedWindowLocked(0, true);
        Binder.restoreCallingIdentity(origId);
    }

    private void removeWindowInnerLocked(Session session, WindowState win) {
        if (win.mRemoved) {
            return;
        }
        for (int i = win.mChildWindows.size() - 1; i >= 0; i--) {
            WindowState cwin = win.mChildWindows.get(i);
            Slog.w(TAG, "Force-removing child win " + cwin + " from container " + win);
            removeWindowInnerLocked(cwin.mSession, cwin);
        }
        win.mRemoved = true;
        if (this.mInputMethodTarget == win) {
            moveInputMethodWindowsIfNeededLocked(false);
        }
        this.mPolicy.removeWindowLw(win);
        win.removeLocked();
        this.mWindowMap.remove(win.mClient.asBinder());
        if (win.mAppOp != -1) {
            this.mAppOps.finishOp(win.mAppOp, win.getOwningUid(), win.getOwningPackage());
        }
        WindowList windows = win.getWindowList();
        windows.remove(win);
        this.mPendingRemove.remove(win);
        this.mResizingWindows.remove(win);
        this.mWindowsChanged = true;
        if (this.mInputMethodWindow == win) {
            this.mInputMethodWindow = null;
        } else if (win.mAttrs.type == 2012) {
            this.mInputMethodDialogs.remove(win);
        }
        WindowToken token = win.mToken;
        AppWindowToken atoken = win.mAppToken;
        token.windows.remove(win);
        if (atoken != null) {
            atoken.allAppWindows.remove(win);
        }
        if (token.windows.size() == 0) {
            if (!token.explicit) {
                this.mTokenMap.remove(token.token);
            } else if (atoken != null) {
                atoken.firstWindowDrawn = false;
            }
        }
        if (atoken != null) {
            if (atoken.startingWindow == win) {
                removeStartingWindowTimeout(atoken);
                atoken.startingWindow = null;
            } else if (atoken.allAppWindows.size() == 0 && atoken.startingData != null) {
                atoken.startingData = null;
            } else if (atoken.allAppWindows.size() == 1 && atoken.startingView != null) {
                scheduleRemoveStartingWindow(atoken);
            }
        }
        if (win.mAttrs.type == 2013) {
            this.mLastWallpaperTimeoutTime = 0L;
            getDefaultDisplayContentLocked().pendingLayoutChanges |= 4;
        } else if ((win.mAttrs.flags & 1048576) != 0) {
            getDefaultDisplayContentLocked().pendingLayoutChanges |= 4;
        }
        if (!this.mInLayout) {
            assignLayersLocked(windows);
            win.mDisplayContent.layoutNeeded = true;
            performLayoutAndPlaceSurfacesLocked();
            if (win.mAppToken != null) {
                win.mAppToken.updateReportedVisibilityLocked();
            }
        }
        this.mInputMonitor.updateInputWindowsLw(true);
    }

    public void updateAppOpsState() {
        synchronized (this.mWindowMap) {
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                WindowList windows = this.mDisplayContents.valueAt(displayNdx).getWindowList();
                int numWindows = windows.size();
                for (int winNdx = 0; winNdx < numWindows; winNdx++) {
                    WindowState win = windows.get(winNdx);
                    if (win.mAppOp != -1) {
                        int mode = this.mAppOps.checkOpNoThrow(win.mAppOp, win.getOwningUid(), win.getOwningPackage());
                        win.setAppOpVisibilityLw(mode == 0);
                    }
                }
            }
        }
    }

    static void logSurface(WindowState w, String msg, RuntimeException where) {
        String str = "  SURFACE " + msg + ": " + w;
        if (where != null) {
            Slog.i(TAG, str, where);
        } else {
            Slog.i(TAG, str);
        }
    }

    static void logSurface(SurfaceControl s, String title, String msg, RuntimeException where) {
        String str = "  SURFACE " + s + ": " + msg + " / " + title;
        if (where != null) {
            Slog.i(TAG, str, where);
        } else {
            Slog.i(TAG, str);
        }
    }

    public void getWindowDisplayFrame(Session session, IWindow client, Rect outDisplayFrame) {
        synchronized (this.mWindowMap) {
            WindowState win = windowForClientLocked(session, client, false);
            if (win == null) {
                outDisplayFrame.setEmpty();
            } else {
                outDisplayFrame.set(win.mDisplayFrame);
            }
        }
    }

    public void setWindowWallpaperPositionLocked(WindowState window, float x, float y, float xStep, float yStep) {
        if (window.mWallpaperX != x || window.mWallpaperY != y) {
            window.mWallpaperX = x;
            window.mWallpaperY = y;
            window.mWallpaperXStep = xStep;
            window.mWallpaperYStep = yStep;
            updateWallpaperOffsetLocked(window, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void wallpaperCommandComplete(IBinder window, Bundle result) {
        synchronized (this.mWindowMap) {
            if (this.mWaitingOnWallpaper != null && this.mWaitingOnWallpaper.mClient.asBinder() == window) {
                this.mWaitingOnWallpaper = null;
                this.mWindowMap.notifyAll();
            }
        }
    }

    public Bundle sendWindowWallpaperCommandLocked(WindowState window, String action, int x, int y, int z, Bundle extras, boolean sync) {
        if (window == this.mWallpaperTarget || window == this.mLowerWallpaperTarget || window == this.mUpperWallpaperTarget) {
            int curTokenIndex = this.mWallpaperTokens.size();
            while (curTokenIndex > 0) {
                curTokenIndex--;
                WindowToken token = this.mWallpaperTokens.get(curTokenIndex);
                int curWallpaperIndex = token.windows.size();
                while (curWallpaperIndex > 0) {
                    curWallpaperIndex--;
                    WindowState wallpaper = token.windows.get(curWallpaperIndex);
                    try {
                        wallpaper.mClient.dispatchWallpaperCommand(action, x, y, z, extras, sync);
                        sync = false;
                    } catch (RemoteException e) {
                    }
                }
            }
            if (sync) {
            }
            return null;
        }
        return null;
    }

    public void setUniverseTransformLocked(WindowState window, float alpha, float offx, float offy, float dsdx, float dtdx, float dsdy, float dtdy) {
        Transformation transform = window.mWinAnimator.mUniverseTransform;
        transform.setAlpha(alpha);
        Matrix matrix = transform.getMatrix();
        matrix.getValues(this.mTmpFloats);
        this.mTmpFloats[2] = offx;
        this.mTmpFloats[5] = offy;
        this.mTmpFloats[0] = dsdx;
        this.mTmpFloats[3] = dtdx;
        this.mTmpFloats[1] = dsdy;
        this.mTmpFloats[4] = dtdy;
        matrix.setValues(this.mTmpFloats);
        DisplayInfo displayInfo = window.mDisplayContent.getDisplayInfo();
        RectF dispRect = new RectF(0.0f, 0.0f, displayInfo.logicalWidth, displayInfo.logicalHeight);
        matrix.mapRect(dispRect);
        window.mGivenTouchableRegion.set(0, 0, displayInfo.logicalWidth, displayInfo.logicalHeight);
        window.mGivenTouchableRegion.op((int) dispRect.left, (int) dispRect.top, (int) dispRect.right, (int) dispRect.bottom, Region.Op.DIFFERENCE);
        window.mTouchableInsets = 3;
        window.mDisplayContent.layoutNeeded = true;
        performLayoutAndPlaceSurfacesLocked();
    }

    public void onRectangleOnScreenRequested(IBinder token, Rect rectangle, boolean immediate) {
        WindowState window;
        synchronized (this.mWindowMap) {
            if (this.mDisplayMagnifier != null && (window = this.mWindowMap.get(token)) != null && window.getDisplayId() == 0) {
                this.mDisplayMagnifier.onRectangleOnScreenRequestedLocked(rectangle, immediate);
            }
        }
    }

    public IWindowId getWindowId(IBinder token) {
        IWindowId iWindowId;
        synchronized (this.mWindowMap) {
            WindowState window = this.mWindowMap.get(token);
            iWindowId = window != null ? window.mWindowId : null;
        }
        return iWindowId;
    }

    public int relayoutWindow(Session session, IWindow client, int seq, WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewVisibility, int flags, Rect outFrame, Rect outOverscanInsets, Rect outContentInsets, Rect outVisibleInsets, Configuration outConfig, Surface outSurface) {
        boolean toBeDisplayed = false;
        boolean surfaceChanged = false;
        int systemUiVisibility = 0;
        if (attrs != null) {
            systemUiVisibility = attrs.systemUiVisibility | attrs.subtreeSystemUiVisibility;
            if ((systemUiVisibility & StatusBarManager.DISABLE_MASK) != 0 && this.mContext.checkCallingOrSelfPermission(Manifest.permission.STATUS_BAR) != 0) {
                systemUiVisibility &= -67043329;
            }
        }
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mWindowMap) {
            WindowState win = windowForClientLocked(session, client, false);
            if (win == null) {
                return 0;
            }
            WindowStateAnimator winAnimator = win.mWinAnimator;
            if (win.mRequestedWidth != requestedWidth || win.mRequestedHeight != requestedHeight) {
                win.mLayoutNeeded = true;
                win.mRequestedWidth = requestedWidth;
                win.mRequestedHeight = requestedHeight;
            }
            if (attrs != null && seq == win.mSeq) {
                win.mSystemUiVisibility = systemUiVisibility;
            }
            if (attrs != null) {
                this.mPolicy.adjustWindowParamsLw(attrs);
            }
            winAnimator.mSurfaceDestroyDeferred = (flags & 2) != 0;
            int attrChanges = 0;
            int flagChanges = 0;
            if (attrs != null) {
                if (win.mAttrs.type != attrs.type) {
                    throw new IllegalArgumentException("Window type can not be changed after the window is added.");
                }
                WindowManager.LayoutParams layoutParams = win.mAttrs;
                int i = layoutParams.flags ^ attrs.flags;
                layoutParams.flags = i;
                flagChanges = i;
                attrChanges = win.mAttrs.copyFrom(attrs);
                if ((attrChanges & 16385) != 0) {
                    win.mLayoutNeeded = true;
                }
            }
            win.mEnforceSizeCompat = (win.mAttrs.privateFlags & 128) != 0;
            if ((attrChanges & 128) != 0) {
                winAnimator.mAlpha = attrs.alpha;
            }
            boolean scaledWindow = (win.mAttrs.flags & 16384) != 0;
            if (scaledWindow) {
                win.mHScale = attrs.width != requestedWidth ? attrs.width / requestedWidth : 1.0f;
                win.mVScale = attrs.height != requestedHeight ? attrs.height / requestedHeight : 1.0f;
            } else {
                win.mVScale = 1.0f;
                win.mHScale = 1.0f;
            }
            boolean imMayMove = (flagChanges & 131080) != 0;
            boolean isDefaultDisplay = win.isDefaultDisplay();
            boolean focusMayChange = isDefaultDisplay && !(win.mViewVisibility == viewVisibility && (flagChanges & 8) == 0 && win.mRelayoutCalled);
            boolean wallpaperMayMove = (win.mViewVisibility == viewVisibility || (win.mAttrs.flags & 1048576) == 0) ? false : true;
            boolean wallpaperMayMove2 = wallpaperMayMove | ((flagChanges & 1048576) != 0);
            win.mRelayoutCalled = true;
            int oldVisibility = win.mViewVisibility;
            win.mViewVisibility = viewVisibility;
            if (viewVisibility == 0 && (win.mAppToken == null || !win.mAppToken.clientHidden)) {
                toBeDisplayed = !win.isVisibleLw();
                if (win.mExiting) {
                    winAnimator.cancelExitAnimationForNextAnimationLocked();
                    win.mExiting = false;
                }
                if (win.mDestroying) {
                    win.mDestroying = false;
                    this.mDestroySurface.remove(win);
                }
                if (oldVisibility == 8) {
                    winAnimator.mEnterAnimationPending = true;
                }
                if (toBeDisplayed) {
                    if (win.isDrawnLw() && okToDisplay()) {
                        winAnimator.applyEnterAnimationLocked();
                    }
                    if ((win.mAttrs.flags & 2097152) != 0) {
                        win.mTurnOnScreen = true;
                    }
                    if (win.isConfigChanged()) {
                        outConfig.setTo(this.mCurConfiguration);
                    }
                }
                if ((attrChanges & 8) != 0) {
                    winAnimator.destroySurfaceLocked();
                    toBeDisplayed = true;
                    surfaceChanged = true;
                }
                try {
                    if (!win.mHasSurface) {
                        surfaceChanged = true;
                    }
                    SurfaceControl surfaceControl = winAnimator.createSurfaceLocked();
                    if (surfaceControl != null) {
                        outSurface.copyFrom(surfaceControl);
                    } else {
                        outSurface.release();
                    }
                    if (toBeDisplayed) {
                        focusMayChange = isDefaultDisplay;
                    }
                    if (win.mAttrs.type == 2011 && this.mInputMethodWindow == null) {
                        this.mInputMethodWindow = win;
                        imMayMove = true;
                    }
                    if (win.mAttrs.type == 1 && win.mAppToken != null && win.mAppToken.startingWindow != null) {
                        WindowManager.LayoutParams sa = win.mAppToken.startingWindow.mAttrs;
                        sa.flags = (sa.flags & (-4718594)) | (win.mAttrs.flags & 4718593);
                    }
                } catch (Exception e) {
                    this.mInputMonitor.updateInputWindowsLw(true);
                    Slog.w(TAG, "Exception thrown when creating surface for client " + client + " (" + ((Object) win.mAttrs.getTitle()) + Separators.RPAREN, e);
                    Binder.restoreCallingIdentity(origId);
                    return 0;
                }
            } else {
                winAnimator.mEnterAnimationPending = false;
                if (winAnimator.mSurfaceControl != null && !win.mExiting) {
                    surfaceChanged = true;
                    int transit = 2;
                    if (win.mAttrs.type == 3) {
                        transit = 5;
                    }
                    if (win.isWinVisibleLw() && winAnimator.applyAnimationLocked(transit, false)) {
                        focusMayChange = isDefaultDisplay;
                        win.mExiting = true;
                    } else if (win.mWinAnimator.isAnimating()) {
                        win.mExiting = true;
                    } else if (win == this.mWallpaperTarget) {
                        win.mExiting = true;
                        win.mWinAnimator.mAnimating = true;
                    } else {
                        if (this.mInputMethodWindow == win) {
                            this.mInputMethodWindow = null;
                        }
                        winAnimator.destroySurfaceLocked();
                    }
                    if (this.mDisplayMagnifier != null && win.getDisplayId() == 0) {
                        this.mDisplayMagnifier.onWindowTransitionLocked(win, transit);
                    }
                }
                outSurface.release();
            }
            if (focusMayChange && updateFocusedWindowLocked(3, false)) {
                imMayMove = false;
            }
            if (imMayMove && (moveInputMethodWindowsIfNeededLocked(false) || toBeDisplayed)) {
                assignLayersLocked(win.getWindowList());
            }
            if (wallpaperMayMove2) {
                getDefaultDisplayContentLocked().pendingLayoutChanges |= 4;
            }
            win.mDisplayContent.layoutNeeded = true;
            win.mGivenInsetsPending = (flags & 1) != 0;
            boolean configChanged = updateOrientationFromAppTokensLocked(false);
            performLayoutAndPlaceSurfacesLocked();
            if (toBeDisplayed && win.mIsWallpaper) {
                DisplayInfo displayInfo = getDefaultDisplayInfoLocked();
                updateWallpaperOffsetLocked(win, displayInfo.logicalWidth, displayInfo.logicalHeight, false);
            }
            if (win.mAppToken != null) {
                win.mAppToken.updateReportedVisibilityLocked();
            }
            outFrame.set(win.mCompatFrame);
            outOverscanInsets.set(win.mOverscanInsets);
            outContentInsets.set(win.mContentInsets);
            outVisibleInsets.set(win.mVisibleInsets);
            boolean inTouchMode = this.mInTouchMode;
            boolean animating = this.mAnimator.mAnimating && win.mWinAnimator.isAnimating();
            if (animating && !this.mRelayoutWhileAnimating.contains(win)) {
                this.mRelayoutWhileAnimating.add(win);
            }
            this.mInputMonitor.updateInputWindowsLw(true);
            if (configChanged) {
                sendNewConfiguration();
            }
            Binder.restoreCallingIdentity(origId);
            return (inTouchMode ? 1 : 0) | (toBeDisplayed ? 2 : 0) | (surfaceChanged ? 4 : 0) | (animating ? 8 : 0);
        }
    }

    @Override // android.view.IWindowManager
    public void getWindowFrame(IBinder token, Rect outBounds) {
        if (!checkCallingPermission(Manifest.permission.RETRIEVE_WINDOW_INFO, "getWindowInfo()")) {
            throw new SecurityException("Requires RETRIEVE_WINDOW_INFO permission.");
        }
        synchronized (this.mWindowMap) {
            WindowState windowState = this.mWindowMap.get(token);
            if (windowState != null) {
                outBounds.set(windowState.mFrame);
            } else {
                outBounds.setEmpty();
            }
        }
    }

    @Override // android.view.IWindowManager
    public void setMagnificationSpec(MagnificationSpec spec) {
        if (!checkCallingPermission(Manifest.permission.MAGNIFY_DISPLAY, "setMagnificationSpec()")) {
            throw new SecurityException("Requires MAGNIFY_DISPLAY permission.");
        }
        synchronized (this.mWindowMap) {
            if (this.mDisplayMagnifier != null) {
                this.mDisplayMagnifier.setMagnificationSpecLocked(spec);
            } else {
                throw new IllegalStateException("Magnification callbacks not set!");
            }
        }
        if (Binder.getCallingPid() != Process.myPid()) {
            spec.recycle();
        }
    }

    @Override // android.view.IWindowManager
    public MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder windowToken) {
        if (!checkCallingPermission(Manifest.permission.MAGNIFY_DISPLAY, "getCompatibleMagnificationSpecForWindow()")) {
            throw new SecurityException("Requires MAGNIFY_DISPLAY permission.");
        }
        synchronized (this.mWindowMap) {
            WindowState windowState = this.mWindowMap.get(windowToken);
            if (windowState == null) {
                return null;
            }
            MagnificationSpec spec = null;
            if (this.mDisplayMagnifier != null) {
                spec = this.mDisplayMagnifier.getMagnificationSpecForWindowLocked(windowState);
            }
            if ((spec == null || spec.isNop()) && windowState.mGlobalScale == 1.0f) {
                return null;
            }
            MagnificationSpec spec2 = spec == null ? MagnificationSpec.obtain() : MagnificationSpec.obtain(spec);
            spec2.scale *= windowState.mGlobalScale;
            return spec2;
        }
    }

    @Override // android.view.IWindowManager
    public void setMagnificationCallbacks(IMagnificationCallbacks callbacks) {
        if (!checkCallingPermission(Manifest.permission.MAGNIFY_DISPLAY, "setMagnificationCallbacks()")) {
            throw new SecurityException("Requires MAGNIFY_DISPLAY permission.");
        }
        synchronized (this.mWindowMap) {
            if (this.mDisplayMagnifier == null) {
                this.mDisplayMagnifier = new DisplayMagnifier(this, callbacks);
            } else if (callbacks == null) {
                if (this.mDisplayMagnifier != null) {
                    this.mDisplayMagnifier.destroyLocked();
                    this.mDisplayMagnifier = null;
                }
            } else {
                throw new IllegalStateException("Magnification callbacks already set!");
            }
        }
    }

    private boolean applyAnimationLocked(AppWindowToken atoken, WindowManager.LayoutParams lp, int transit, boolean enter) {
        if (okToDisplay()) {
            DisplayInfo displayInfo = getDefaultDisplayInfoLocked();
            int width = displayInfo.appWidth;
            int height = displayInfo.appHeight;
            Animation a = this.mAppTransition.loadAnimation(lp, transit, enter, width, height);
            if (a != null) {
                atoken.mAppAnimator.setAnimation(a, width, height);
            }
        } else {
            atoken.mAppAnimator.clearAnimation();
        }
        return atoken.mAppAnimator.animation != null;
    }

    public void validateAppTokens(int stackId, List<TaskGroup> tasks) {
        synchronized (this.mWindowMap) {
            int t = tasks.size() - 1;
            if (t < 0) {
                Slog.w(TAG, "validateAppTokens: empty task list");
                return;
            }
            int taskId = tasks.get(0).taskId;
            Task targetTask = this.mTaskIdToTask.get(taskId);
            DisplayContent displayContent = targetTask.getDisplayContent();
            if (displayContent == null) {
                Slog.w(TAG, "validateAppTokens: no Display for taskId=" + taskId);
                return;
            }
            ArrayList<Task> localTasks = this.mStackIdToStack.get(stackId).getTasks();
            int taskNdx = localTasks.size() - 1;
            while (taskNdx >= 0 && t >= 0) {
                AppTokenList localTokens = localTasks.get(taskNdx).mAppTokens;
                TaskGroup task = tasks.get(t);
                List<IApplicationToken> tokens = task.tokens;
                DisplayContent lastDisplayContent = displayContent;
                displayContent = this.mTaskIdToTask.get(taskId).getDisplayContent();
                if (displayContent != lastDisplayContent) {
                    Slog.w(TAG, "validateAppTokens: displayContent changed in TaskGroup list!");
                    return;
                }
                int tokenNdx = localTokens.size() - 1;
                int v = task.tokens.size() - 1;
                while (tokenNdx >= 0 && v >= 0) {
                    AppWindowToken atoken = localTokens.get(tokenNdx);
                    if (atoken.removed) {
                        tokenNdx--;
                    } else if (tokens.get(v) != atoken.token) {
                        break;
                    } else {
                        tokenNdx--;
                        v--;
                    }
                }
                if (tokenNdx >= 0 || v >= 0) {
                    break;
                }
                taskNdx--;
                t--;
            }
            if (taskNdx >= 0 || t >= 0) {
                Slog.w(TAG, "validateAppTokens: Mismatch! ActivityManager=" + tasks);
                Slog.w(TAG, "validateAppTokens: Mismatch! WindowManager=" + localTasks);
                Slog.w(TAG, "validateAppTokens: Mismatch! Callers=" + Debug.getCallers(4));
            }
        }
    }

    public void validateStackOrder(Integer[] remoteStackIds) {
    }

    boolean checkCallingPermission(String permission, String func) {
        if (Binder.getCallingPid() == Process.myPid() || this.mContext.checkCallingPermission(permission) == 0) {
            return true;
        }
        String msg = "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission;
        Slog.w(TAG, msg);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean okToDisplay() {
        return !this.mDisplayFrozen && this.mDisplayEnabled && this.mPolicy.isScreenOnFully();
    }

    AppWindowToken findAppWindowToken(IBinder token) {
        WindowToken wtoken = this.mTokenMap.get(token);
        if (wtoken == null) {
            return null;
        }
        return wtoken.appWindowToken;
    }

    @Override // android.view.IWindowManager
    public void addWindowToken(IBinder token, int type) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "addWindowToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            if (this.mTokenMap.get(token) != null) {
                Slog.w(TAG, "Attempted to add existing input method token: " + token);
                return;
            }
            WindowToken wtoken = new WindowToken(this, token, type, true);
            this.mTokenMap.put(token, wtoken);
            if (type == 2013) {
                this.mWallpaperTokens.add(wtoken);
            }
        }
    }

    @Override // android.view.IWindowManager
    public void removeWindowToken(IBinder token) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "removeWindowToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = null;
            WindowToken wtoken = this.mTokenMap.remove(token);
            if (wtoken != null) {
                boolean delayed = false;
                if (!wtoken.hidden) {
                    int N = wtoken.windows.size();
                    boolean changed = false;
                    for (int i = 0; i < N; i++) {
                        WindowState win = wtoken.windows.get(i);
                        displayContent = win.mDisplayContent;
                        if (win.mWinAnimator.isAnimating()) {
                            delayed = true;
                        }
                        if (win.isVisibleNow()) {
                            win.mWinAnimator.applyAnimationLocked(2, false);
                            if (this.mDisplayMagnifier != null && win.isDefaultDisplay()) {
                                this.mDisplayMagnifier.onWindowTransitionLocked(win, 2);
                            }
                            changed = true;
                            displayContent.layoutNeeded = true;
                        }
                    }
                    wtoken.hidden = true;
                    if (changed) {
                        performLayoutAndPlaceSurfacesLocked();
                        updateFocusedWindowLocked(0, false);
                    }
                    if (delayed) {
                        displayContent.mExitingTokens.add(wtoken);
                    } else if (wtoken.windowType == 2013) {
                        this.mWallpaperTokens.remove(wtoken);
                    }
                }
                this.mInputMonitor.updateInputWindowsLw(true);
            } else {
                Slog.w(TAG, "Attempted to remove non-existing token: " + token);
            }
        }
        Binder.restoreCallingIdentity(origId);
    }

    private Task createTask(int taskId, int stackId, int userId, AppWindowToken atoken) {
        TaskStack stack = this.mStackIdToStack.get(stackId);
        if (stack == null) {
            throw new IllegalArgumentException("addAppToken: invalid stackId=" + stackId);
        }
        Task task = new Task(atoken, stack, userId);
        this.mTaskIdToTask.put(taskId, task);
        stack.addTask(task, true);
        stack.getDisplayContent().moveStack(stack, true);
        return task;
    }

    @Override // android.view.IWindowManager
    public void addAppToken(int addPos, IApplicationToken token, int taskId, int stackId, int requestedOrientation, boolean fullscreen, boolean showWhenLocked, int userId) {
        long inputDispatchingTimeoutNanos;
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "addAppToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        try {
            inputDispatchingTimeoutNanos = token.getKeyDispatchingTimeout() * 1000000;
        } catch (RemoteException ex) {
            Slog.w(TAG, "Could not get dispatching timeout.", ex);
            inputDispatchingTimeoutNanos = 5000000000L;
        }
        synchronized (this.mWindowMap) {
            if (findAppWindowToken(token.asBinder()) != null) {
                Slog.w(TAG, "Attempted to add existing app token: " + token);
                return;
            }
            AppWindowToken atoken = new AppWindowToken(this, token);
            atoken.inputDispatchingTimeoutNanos = inputDispatchingTimeoutNanos;
            atoken.groupId = taskId;
            atoken.appFullscreen = fullscreen;
            atoken.showWhenLocked = showWhenLocked;
            atoken.requestedOrientation = requestedOrientation;
            Task task = this.mTaskIdToTask.get(taskId);
            if (task == null) {
                createTask(taskId, stackId, userId, atoken);
            } else {
                task.addAppToken(addPos, atoken);
            }
            this.mTokenMap.put(token.asBinder(), atoken);
            atoken.hidden = true;
            atoken.hiddenRequested = true;
        }
    }

    @Override // android.view.IWindowManager
    public void setAppGroupId(IBinder token, int groupId) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppGroupId()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken atoken = findAppWindowToken(token);
            if (atoken == null) {
                Slog.w(TAG, "Attempted to set group id of non-existing app token: " + token);
                return;
            }
            Task oldTask = this.mTaskIdToTask.get(atoken.groupId);
            oldTask.removeAppToken(atoken);
            atoken.groupId = groupId;
            Task newTask = this.mTaskIdToTask.get(groupId);
            if (newTask == null) {
                newTask = createTask(groupId, oldTask.mStack.mStackId, oldTask.mUserId, atoken);
            }
            newTask.mAppTokens.add(atoken);
        }
    }

    public int getOrientationFromWindowsLocked() {
        int req;
        if (this.mDisplayFrozen || this.mOpeningApps.size() > 0 || this.mClosingApps.size() > 0) {
            return this.mLastWindowForcedOrientation;
        }
        WindowList windows = getDefaultWindowListLocked();
        int pos = windows.size() - 1;
        while (pos >= 0) {
            WindowState win = windows.get(pos);
            pos--;
            if (win.mAppToken != null) {
                this.mLastWindowForcedOrientation = -1;
                return -1;
            } else if (win.isVisibleLw() && win.mPolicyVisibilityAfterAnim && (req = win.mAttrs.screenOrientation) != -1 && req != 3) {
                this.mLastWindowForcedOrientation = req;
                return req;
            }
        }
        this.mLastWindowForcedOrientation = -1;
        return -1;
    }

    public int getOrientationFromAppTokensLocked() {
        int lastOrientation = -1;
        boolean findingBehind = false;
        boolean lastFullscreen = false;
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        ArrayList<Task> tasks = displayContent.getTasks();
        for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int firstToken = tokens.size() - 1;
            for (int tokenNdx = firstToken; tokenNdx >= 0; tokenNdx--) {
                AppWindowToken atoken = tokens.get(tokenNdx);
                if (findingBehind || atoken.hidden || !atoken.hiddenRequested) {
                    if (tokenNdx == firstToken && lastOrientation != 3 && lastFullscreen) {
                        return lastOrientation;
                    }
                    if (!atoken.hiddenRequested && !atoken.willBeHidden) {
                        if (tokenNdx == 0) {
                            lastOrientation = atoken.requestedOrientation;
                        }
                        int or = atoken.requestedOrientation;
                        lastFullscreen = atoken.appFullscreen;
                        if (lastFullscreen && or != 3) {
                            return or;
                        }
                        if (or != -1 && or != 3) {
                            return or;
                        }
                        findingBehind |= or == 3;
                    }
                }
            }
        }
        return -1;
    }

    @Override // android.view.IWindowManager
    public Configuration updateOrientationFromAppTokens(Configuration currentConfig, IBinder freezeThisOneIfNeeded) {
        Configuration config;
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "updateOrientationFromAppTokens()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        long ident = Binder.clearCallingIdentity();
        synchronized (this.mWindowMap) {
            config = updateOrientationFromAppTokensLocked(currentConfig, freezeThisOneIfNeeded);
        }
        Binder.restoreCallingIdentity(ident);
        return config;
    }

    private Configuration updateOrientationFromAppTokensLocked(Configuration currentConfig, IBinder freezeThisOneIfNeeded) {
        AppWindowToken atoken;
        Configuration config = null;
        if (updateOrientationFromAppTokensLocked(false)) {
            if (freezeThisOneIfNeeded != null && (atoken = findAppWindowToken(freezeThisOneIfNeeded)) != null) {
                startAppFreezingScreenLocked(atoken, 128);
            }
            config = computeNewConfigurationLocked();
        } else if (currentConfig != null) {
            this.mTempConfiguration.setToDefaults();
            this.mTempConfiguration.fontScale = currentConfig.fontScale;
            if (computeScreenConfigurationLocked(this.mTempConfiguration) && currentConfig.diff(this.mTempConfiguration) != 0) {
                this.mWaitingForConfig = true;
                DisplayContent displayContent = getDefaultDisplayContentLocked();
                displayContent.layoutNeeded = true;
                int[] anim = new int[2];
                if (displayContent.isDimming()) {
                    anim[1] = 0;
                    anim[0] = 0;
                } else {
                    this.mPolicy.selectRotationAnimationLw(anim);
                }
                startFreezingDisplayLocked(false, anim[0], anim[1]);
                config = new Configuration(this.mTempConfiguration);
            }
        }
        return config;
    }

    @Override // android.view.IWindowManager
    public void setNewConfiguration(Configuration config) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setNewConfiguration()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            this.mCurConfiguration = new Configuration(config);
            if (this.mWaitingForConfig) {
                this.mWaitingForConfig = false;
                this.mLastFinishedFreezeSource = "new-config";
            }
            performLayoutAndPlaceSurfacesLocked();
        }
    }

    @Override // android.view.IWindowManager
    public void setAppOrientation(IApplicationToken token, int requestedOrientation) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppOrientation()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken atoken = findAppWindowToken(token.asBinder());
            if (atoken == null) {
                Slog.w(TAG, "Attempted to set orientation of non-existing app token: " + token);
            } else {
                atoken.requestedOrientation = requestedOrientation;
            }
        }
    }

    @Override // android.view.IWindowManager
    public int getAppOrientation(IApplicationToken token) {
        synchronized (this.mWindowMap) {
            AppWindowToken wtoken = findAppWindowToken(token.asBinder());
            if (wtoken == null) {
                return -1;
            }
            return wtoken.requestedOrientation;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFocusedStackLayer() {
        this.mFocusedStackLayer = 0;
        if (this.mFocusedApp != null) {
            WindowList windows = this.mFocusedApp.allAppWindows;
            for (int i = windows.size() - 1; i >= 0; i--) {
                WindowState win = windows.get(i);
                int animLayer = win.mWinAnimator.mAnimLayer;
                if (win.mAttachedWindow == null && win.isVisibleLw() && animLayer > this.mFocusedStackLayer) {
                    this.mFocusedStackLayer = animLayer + 1;
                }
            }
        }
        this.mFocusedStackFrame.setLayer(this.mFocusedStackLayer);
    }

    @Override // android.view.IWindowManager
    public void setFocusedApp(IBinder token, boolean moveFocusNow) {
        boolean changed;
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setFocusedApp()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            if (token == null) {
                changed = this.mFocusedApp != null;
                this.mFocusedApp = null;
                if (changed) {
                    this.mInputMonitor.setFocusedAppLw(null);
                }
            } else {
                AppWindowToken newFocus = findAppWindowToken(token);
                if (newFocus == null) {
                    Slog.w(TAG, "Attempted to set focus to non-existing app token: " + token);
                    return;
                }
                changed = this.mFocusedApp != newFocus;
                this.mFocusedApp = newFocus;
                if (changed) {
                    this.mInputMonitor.setFocusedAppLw(newFocus);
                }
            }
            if (moveFocusNow && changed) {
                long origId = Binder.clearCallingIdentity();
                updateFocusedWindowLocked(0, true);
                Binder.restoreCallingIdentity(origId);
            }
        }
    }

    @Override // android.view.IWindowManager
    public void prepareAppTransition(int transit, boolean alwaysKeepCurrent) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "prepareAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            if (okToDisplay()) {
                if (!this.mAppTransition.isTransitionSet() || this.mAppTransition.isTransitionNone()) {
                    this.mAppTransition.setAppTransition(transit);
                } else if (!alwaysKeepCurrent) {
                    if (transit == 4104 && this.mAppTransition.isTransitionEqual(8201)) {
                        this.mAppTransition.setAppTransition(transit);
                    } else if (transit == 4102 && this.mAppTransition.isTransitionEqual(8199)) {
                        this.mAppTransition.setAppTransition(transit);
                    }
                }
                this.mAppTransition.prepare();
                this.mStartingIconInTransition = false;
                this.mSkipAppTransitionAnimation = false;
                this.mH.removeMessages(13);
                this.mH.sendEmptyMessageDelayed(13, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            }
        }
    }

    @Override // android.view.IWindowManager
    public int getPendingAppTransition() {
        return this.mAppTransition.getAppTransition();
    }

    @Override // android.view.IWindowManager
    public void overridePendingAppTransition(String packageName, int enterAnim, int exitAnim, IRemoteCallback startedCallback) {
        synchronized (this.mWindowMap) {
            this.mAppTransition.overridePendingAppTransition(packageName, enterAnim, exitAnim, startedCallback);
        }
    }

    @Override // android.view.IWindowManager
    public void overridePendingAppTransitionScaleUp(int startX, int startY, int startWidth, int startHeight) {
        synchronized (this.mWindowMap) {
            this.mAppTransition.overridePendingAppTransitionScaleUp(startX, startY, startWidth, startHeight);
        }
    }

    @Override // android.view.IWindowManager
    public void overridePendingAppTransitionThumb(Bitmap srcThumb, int startX, int startY, IRemoteCallback startedCallback, boolean scaleUp) {
        synchronized (this.mWindowMap) {
            this.mAppTransition.overridePendingAppTransitionThumb(srcThumb, startX, startY, startedCallback, scaleUp);
        }
    }

    @Override // android.view.IWindowManager
    public void executeAppTransition() {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "executeAppTransition()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            if (this.mAppTransition.isTransitionSet()) {
                this.mAppTransition.setReady();
                long origId = Binder.clearCallingIdentity();
                performLayoutAndPlaceSurfacesLocked();
                Binder.restoreCallingIdentity(origId);
            }
        }
    }

    @Override // android.view.IWindowManager
    public void setAppStartingWindow(IBinder token, String pkg, int theme, CompatibilityInfo compatInfo, CharSequence nonLocalizedLabel, int labelRes, int icon, int logo, int windowFlags, IBinder transferFrom, boolean createIfNeeded) {
        AppWindowToken ttoken;
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppStartingWindow()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken wtoken = findAppWindowToken(token);
            if (wtoken == null) {
                Slog.w(TAG, "Attempted to set icon of non-existing app token: " + token);
            } else if (okToDisplay()) {
                if (wtoken.startingData != null) {
                    return;
                }
                if (transferFrom != null && (ttoken = findAppWindowToken(transferFrom)) != null) {
                    WindowState startingWindow = ttoken.startingWindow;
                    if (startingWindow != null) {
                        if (this.mStartingIconInTransition) {
                            this.mSkipAppTransitionAnimation = true;
                        }
                        long origId = Binder.clearCallingIdentity();
                        wtoken.startingData = ttoken.startingData;
                        wtoken.startingView = ttoken.startingView;
                        wtoken.startingDisplayed = ttoken.startingDisplayed;
                        ttoken.startingDisplayed = false;
                        wtoken.startingWindow = startingWindow;
                        wtoken.reportedVisible = ttoken.reportedVisible;
                        ttoken.startingData = null;
                        ttoken.startingView = null;
                        ttoken.startingWindow = null;
                        ttoken.startingMoved = true;
                        startingWindow.mToken = wtoken;
                        startingWindow.mRootToken = wtoken;
                        startingWindow.mAppToken = wtoken;
                        startingWindow.mWinAnimator.mAppAnimator = wtoken.mAppAnimator;
                        removeStartingWindowTimeout(ttoken);
                        startingWindow.getWindowList().remove(startingWindow);
                        this.mWindowsChanged = true;
                        ttoken.windows.remove(startingWindow);
                        ttoken.allAppWindows.remove(startingWindow);
                        addWindowToListInOrderLocked(startingWindow, true);
                        if (ttoken.allDrawn) {
                            wtoken.allDrawn = true;
                            wtoken.deferClearAllDrawn = ttoken.deferClearAllDrawn;
                        }
                        if (ttoken.firstWindowDrawn) {
                            wtoken.firstWindowDrawn = true;
                        }
                        if (!ttoken.hidden) {
                            wtoken.hidden = false;
                            wtoken.hiddenRequested = false;
                            wtoken.willBeHidden = false;
                        }
                        if (wtoken.clientHidden != ttoken.clientHidden) {
                            wtoken.clientHidden = ttoken.clientHidden;
                            wtoken.sendAppVisibilityToClients();
                        }
                        AppWindowAnimator tAppAnimator = ttoken.mAppAnimator;
                        AppWindowAnimator wAppAnimator = wtoken.mAppAnimator;
                        if (tAppAnimator.animation != null) {
                            wAppAnimator.animation = tAppAnimator.animation;
                            wAppAnimator.animating = tAppAnimator.animating;
                            wAppAnimator.animLayerAdjustment = tAppAnimator.animLayerAdjustment;
                            tAppAnimator.animation = null;
                            tAppAnimator.animLayerAdjustment = 0;
                            wAppAnimator.updateLayers();
                            tAppAnimator.updateLayers();
                        }
                        updateFocusedWindowLocked(3, true);
                        getDefaultDisplayContentLocked().layoutNeeded = true;
                        performLayoutAndPlaceSurfacesLocked();
                        Binder.restoreCallingIdentity(origId);
                        return;
                    } else if (ttoken.startingData != null) {
                        wtoken.startingData = ttoken.startingData;
                        ttoken.startingData = null;
                        ttoken.startingMoved = true;
                        Message m = this.mH.obtainMessage(5, wtoken);
                        this.mH.sendMessageAtFrontOfQueue(m);
                        return;
                    } else {
                        AppWindowAnimator tAppAnimator2 = ttoken.mAppAnimator;
                        AppWindowAnimator wAppAnimator2 = wtoken.mAppAnimator;
                        if (tAppAnimator2.thumbnail != null) {
                            if (wAppAnimator2.thumbnail != null) {
                                wAppAnimator2.thumbnail.destroy();
                            }
                            wAppAnimator2.thumbnail = tAppAnimator2.thumbnail;
                            wAppAnimator2.thumbnailX = tAppAnimator2.thumbnailX;
                            wAppAnimator2.thumbnailY = tAppAnimator2.thumbnailY;
                            wAppAnimator2.thumbnailLayer = tAppAnimator2.thumbnailLayer;
                            wAppAnimator2.thumbnailAnimation = tAppAnimator2.thumbnailAnimation;
                            tAppAnimator2.thumbnail = null;
                        }
                    }
                }
                if (createIfNeeded) {
                    if (theme != 0) {
                        AttributeCache.Entry ent = AttributeCache.instance().get(pkg, theme, R.styleable.Window, this.mCurrentUserId);
                        if (ent == null) {
                            return;
                        }
                        if (ent.array.getBoolean(5, false)) {
                            return;
                        }
                        if (ent.array.getBoolean(4, false)) {
                            return;
                        }
                        if (ent.array.getBoolean(14, false)) {
                            if (this.mWallpaperTarget != null) {
                                return;
                            }
                            windowFlags |= 1048576;
                        }
                    }
                    this.mStartingIconInTransition = true;
                    wtoken.startingData = new StartingData(pkg, theme, compatInfo, nonLocalizedLabel, labelRes, icon, logo, windowFlags);
                    Message m2 = this.mH.obtainMessage(5, wtoken);
                    this.mH.sendMessageAtFrontOfQueue(m2);
                }
            }
        }
    }

    @Override // android.view.IWindowManager
    public void setAppWillBeHidden(IBinder token) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppWillBeHidden()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken wtoken = findAppWindowToken(token);
            if (wtoken == null) {
                Slog.w(TAG, "Attempted to set will be hidden of non-existing app token: " + token);
            } else {
                wtoken.willBeHidden = true;
            }
        }
    }

    public void setAppFullscreen(IBinder token, boolean toOpaque) {
        AppWindowToken atoken = findAppWindowToken(token);
        if (atoken != null) {
            atoken.appFullscreen = toOpaque;
            requestTraversal();
        }
    }

    boolean setTokenVisibilityLocked(AppWindowToken wtoken, WindowManager.LayoutParams lp, boolean visible, int transit, boolean performLayout) {
        boolean delayed = false;
        if (wtoken.clientHidden == visible) {
            wtoken.clientHidden = !visible;
            wtoken.sendAppVisibilityToClients();
        }
        wtoken.willBeHidden = false;
        if (wtoken.hidden == visible) {
            boolean changed = false;
            boolean runningAppAnimation = false;
            if (transit != -1) {
                if (wtoken.mAppAnimator.animation == AppWindowAnimator.sDummyAnimation) {
                    wtoken.mAppAnimator.animation = null;
                }
                if (applyAnimationLocked(wtoken, lp, transit, visible)) {
                    runningAppAnimation = true;
                    delayed = true;
                }
                WindowState window = wtoken.findMainWindow();
                if (window != null && this.mDisplayMagnifier != null && window.getDisplayId() == 0) {
                    this.mDisplayMagnifier.onAppWindowTransitionLocked(window, transit);
                }
                changed = true;
            }
            int N = wtoken.allAppWindows.size();
            for (int i = 0; i < N; i++) {
                WindowState win = wtoken.allAppWindows.get(i);
                if (win != wtoken.startingWindow) {
                    if (visible) {
                        if (!win.isVisibleNow()) {
                            if (!runningAppAnimation) {
                                win.mWinAnimator.applyAnimationLocked(1, true);
                                if (this.mDisplayMagnifier != null && win.getDisplayId() == 0) {
                                    this.mDisplayMagnifier.onWindowTransitionLocked(win, 1);
                                }
                            }
                            changed = true;
                            win.mDisplayContent.layoutNeeded = true;
                        }
                    } else if (win.isVisibleNow()) {
                        if (!runningAppAnimation) {
                            win.mWinAnimator.applyAnimationLocked(2, false);
                            if (this.mDisplayMagnifier != null && win.getDisplayId() == 0) {
                                this.mDisplayMagnifier.onWindowTransitionLocked(win, 2);
                            }
                        }
                        changed = true;
                        win.mDisplayContent.layoutNeeded = true;
                    }
                }
            }
            boolean z = !visible;
            wtoken.hiddenRequested = z;
            wtoken.hidden = z;
            if (!visible) {
                unsetAppFreezingScreenLocked(wtoken, true, true);
            } else {
                WindowState swin = wtoken.startingWindow;
                if (swin != null && !swin.isDrawnLw()) {
                    swin.mPolicyVisibility = false;
                    swin.mPolicyVisibilityAfterAnim = false;
                }
            }
            if (changed) {
                this.mInputMonitor.setUpdateInputWindowsNeededLw();
                if (performLayout) {
                    updateFocusedWindowLocked(3, false);
                    performLayoutAndPlaceSurfacesLocked();
                }
                this.mInputMonitor.updateInputWindowsLw(false);
            }
        }
        if (wtoken.mAppAnimator.animation != null) {
            delayed = true;
        }
        for (int i2 = wtoken.allAppWindows.size() - 1; i2 >= 0 && !delayed; i2--) {
            if (wtoken.allAppWindows.get(i2).mWinAnimator.isWindowAnimating()) {
                delayed = true;
            }
        }
        return delayed;
    }

    @Override // android.view.IWindowManager
    public void setAppVisibility(IBinder token, boolean visible) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppVisibility()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken wtoken = findAppWindowToken(token);
            if (wtoken == null) {
                Slog.w(TAG, "Attempted to set visibility of non-existing app token: " + token);
            } else if (okToDisplay() && this.mAppTransition.isTransitionSet()) {
                if (wtoken.hiddenRequested != visible) {
                    return;
                }
                wtoken.hiddenRequested = !visible;
                if (!wtoken.startingDisplayed) {
                    wtoken.mAppAnimator.setDummyAnimation();
                }
                this.mOpeningApps.remove(wtoken);
                this.mClosingApps.remove(wtoken);
                wtoken.waitingToHide = false;
                wtoken.waitingToShow = false;
                wtoken.inPendingTransaction = true;
                if (visible) {
                    this.mOpeningApps.add(wtoken);
                    wtoken.startingMoved = false;
                    if (wtoken.hidden) {
                        wtoken.allDrawn = false;
                        wtoken.deferClearAllDrawn = false;
                        wtoken.waitingToShow = true;
                        if (wtoken.clientHidden) {
                            wtoken.clientHidden = false;
                            wtoken.sendAppVisibilityToClients();
                        }
                    }
                } else {
                    this.mClosingApps.add(wtoken);
                    if (!wtoken.hidden) {
                        wtoken.waitingToHide = true;
                    }
                }
            } else {
                long origId = Binder.clearCallingIdentity();
                setTokenVisibilityLocked(wtoken, null, visible, -1, true);
                wtoken.updateReportedVisibilityLocked();
                Binder.restoreCallingIdentity(origId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unsetAppFreezingScreenLocked(AppWindowToken wtoken, boolean unfreezeSurfaceNow, boolean force) {
        if (wtoken.mAppAnimator.freezingScreen) {
            int N = wtoken.allAppWindows.size();
            boolean unfrozeWindows = false;
            for (int i = 0; i < N; i++) {
                WindowState w = wtoken.allAppWindows.get(i);
                if (w.mAppFreezing) {
                    w.mAppFreezing = false;
                    if (w.mHasSurface && !w.mOrientationChanging) {
                        w.mOrientationChanging = true;
                        this.mInnerFields.mOrientationChangeComplete = false;
                    }
                    w.mLastFreezeDuration = 0;
                    unfrozeWindows = true;
                    w.mDisplayContent.layoutNeeded = true;
                }
            }
            if (force || unfrozeWindows) {
                wtoken.mAppAnimator.freezingScreen = false;
                wtoken.mAppAnimator.lastFreezeDuration = (int) (SystemClock.elapsedRealtime() - this.mDisplayFreezeTime);
                this.mAppsFreezingScreen--;
                this.mLastFinishedFreezeSource = wtoken;
            }
            if (unfreezeSurfaceNow) {
                if (unfrozeWindows) {
                    performLayoutAndPlaceSurfacesLocked();
                }
                stopFreezingDisplayLocked();
            }
        }
    }

    public void startAppFreezingScreenLocked(AppWindowToken wtoken, int configChanges) {
        if (!wtoken.hiddenRequested) {
            if (!wtoken.mAppAnimator.freezingScreen) {
                wtoken.mAppAnimator.freezingScreen = true;
                wtoken.mAppAnimator.lastFreezeDuration = 0;
                this.mAppsFreezingScreen++;
                if (this.mAppsFreezingScreen == 1) {
                    startFreezingDisplayLocked(false, 0, 0);
                    this.mH.removeMessages(17);
                    this.mH.sendEmptyMessageDelayed(17, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                }
            }
            int N = wtoken.allAppWindows.size();
            for (int i = 0; i < N; i++) {
                WindowState w = wtoken.allAppWindows.get(i);
                w.mAppFreezing = true;
            }
        }
    }

    @Override // android.view.IWindowManager
    public void startAppFreezingScreen(IBinder token, int configChanges) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppFreezingScreen()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            if (configChanges == 0) {
                if (okToDisplay()) {
                    return;
                }
            }
            AppWindowToken wtoken = findAppWindowToken(token);
            if (wtoken == null || wtoken.appToken == null) {
                Slog.w(TAG, "Attempted to freeze screen with non-existing app token: " + wtoken);
                return;
            }
            long origId = Binder.clearCallingIdentity();
            startAppFreezingScreenLocked(wtoken, configChanges);
            Binder.restoreCallingIdentity(origId);
        }
    }

    @Override // android.view.IWindowManager
    public void stopAppFreezingScreen(IBinder token, boolean force) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setAppFreezingScreen()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            AppWindowToken wtoken = findAppWindowToken(token);
            if (wtoken == null || wtoken.appToken == null) {
                return;
            }
            long origId = Binder.clearCallingIdentity();
            unsetAppFreezingScreenLocked(wtoken, true, force);
            Binder.restoreCallingIdentity(origId);
        }
    }

    @Override // android.view.IWindowManager
    public void removeAppToken(IBinder token) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "removeAppToken()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        AppWindowToken wtoken = null;
        AppWindowToken startingToken = null;
        boolean delayed = false;
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mWindowMap) {
            WindowToken basewtoken = this.mTokenMap.remove(token);
            if (basewtoken != null) {
                AppWindowToken appWindowToken = basewtoken.appWindowToken;
                wtoken = appWindowToken;
                if (appWindowToken != null) {
                    delayed = setTokenVisibilityLocked(wtoken, null, false, -1, true);
                    wtoken.inPendingTransaction = false;
                    this.mOpeningApps.remove(wtoken);
                    wtoken.waitingToShow = false;
                    if (this.mClosingApps.contains(wtoken)) {
                        delayed = true;
                    } else if (this.mAppTransition.isTransitionSet()) {
                        this.mClosingApps.add(wtoken);
                        wtoken.waitingToHide = true;
                        delayed = true;
                    }
                    Task task = this.mTaskIdToTask.get(wtoken.groupId);
                    DisplayContent displayContent = task.getDisplayContent();
                    if (delayed) {
                        displayContent.mExitingAppTokens.add(wtoken);
                    } else {
                        wtoken.mAppAnimator.clearAnimation();
                        wtoken.mAppAnimator.animating = false;
                    }
                    if (task.removeAppToken(wtoken)) {
                        this.mTaskIdToTask.delete(wtoken.groupId);
                    }
                    wtoken.removed = true;
                    if (wtoken.startingData != null) {
                        startingToken = wtoken;
                    }
                    unsetAppFreezingScreenLocked(wtoken, true, true);
                    if (this.mFocusedApp == wtoken) {
                        this.mFocusedApp = null;
                        updateFocusedWindowLocked(0, true);
                        this.mInputMonitor.setFocusedAppLw(null);
                    }
                    if (!delayed && wtoken != null) {
                        wtoken.updateReportedVisibilityLocked();
                    }
                }
            }
            Slog.w(TAG, "Attempted to remove non-existing app token: " + token);
            if (!delayed) {
                wtoken.updateReportedVisibilityLocked();
            }
        }
        Binder.restoreCallingIdentity(origId);
        scheduleRemoveStartingWindow(startingToken);
    }

    void removeStartingWindowTimeout(AppWindowToken wtoken) {
        if (wtoken != null) {
            this.mH.removeMessages(33, wtoken);
        }
    }

    void scheduleRemoveStartingWindow(AppWindowToken wtoken) {
        if (wtoken != null && wtoken.startingWindow != null) {
            removeStartingWindowTimeout(wtoken);
            Message m = this.mH.obtainMessage(6, wtoken);
            this.mH.sendMessage(m);
        }
    }

    private boolean tmpRemoveAppWindowsLocked(WindowToken token) {
        int NW = token.windows.size();
        if (NW > 0) {
            this.mWindowsChanged = true;
        }
        for (int i = 0; i < NW; i++) {
            WindowState win = token.windows.get(i);
            win.getWindowList().remove(win);
            int j = win.mChildWindows.size();
            while (j > 0) {
                j--;
                WindowState cwin = win.mChildWindows.get(j);
                cwin.getWindowList().remove(cwin);
            }
        }
        return NW > 0;
    }

    void dumpAppTokensLocked() {
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx);
            Slog.v(TAG, "  Display " + displayContent.getDisplayId());
            ArrayList<Task> tasks = displayContent.getTasks();
            int i = displayContent.numTokens();
            for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
                AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
                for (int tokenNdx = tokens.size() - 1; tokenNdx >= 0; tokenNdx--) {
                    AppWindowToken wtoken = tokens.get(tokenNdx);
                    i--;
                    Slog.v(TAG, "  #" + i + ": " + wtoken.token);
                }
            }
        }
    }

    void dumpWindowsLocked() {
        int i = 0;
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            WindowList windows = this.mDisplayContents.valueAt(displayNdx).getWindowList();
            for (int winNdx = windows.size() - 1; winNdx >= 0; winNdx--) {
                int i2 = i;
                i++;
                Slog.v(TAG, "  #" + i2 + ": " + windows.get(winNdx));
            }
        }
    }

    private int findAppWindowInsertionPointLocked(AppWindowToken target) {
        int taskId = target.groupId;
        Task targetTask = this.mTaskIdToTask.get(taskId);
        if (targetTask == null) {
            Slog.w(TAG, "findAppWindowInsertionPointLocked: no Task for " + target + " taskId=" + taskId);
            return 0;
        }
        DisplayContent displayContent = targetTask.getDisplayContent();
        if (displayContent == null) {
            Slog.w(TAG, "findAppWindowInsertionPointLocked: no DisplayContent for " + target);
            return 0;
        }
        WindowList windows = displayContent.getWindowList();
        int NW = windows.size();
        boolean found = false;
        ArrayList<Task> tasks = displayContent.getTasks();
        for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
            Task task = tasks.get(taskNdx);
            if (found || task.taskId == taskId) {
                AppTokenList tokens = task.mAppTokens;
                for (int tokenNdx = tokens.size() - 1; tokenNdx >= 0; tokenNdx--) {
                    AppWindowToken wtoken = tokens.get(tokenNdx);
                    if (!found && wtoken == target) {
                        found = true;
                    }
                    if (found && !wtoken.sendingToBottom) {
                        for (int i = wtoken.windows.size() - 1; i >= 0; i--) {
                            WindowState win = wtoken.windows.get(i);
                            for (int j = win.mChildWindows.size() - 1; j >= 0; j--) {
                                WindowState cwin = win.mChildWindows.get(j);
                                if (cwin.mSubLayer >= 0) {
                                    for (int pos = NW - 1; pos >= 0; pos--) {
                                        if (windows.get(pos) == cwin) {
                                            return pos + 1;
                                        }
                                    }
                                    continue;
                                }
                            }
                            for (int pos2 = NW - 1; pos2 >= 0; pos2--) {
                                if (windows.get(pos2) == win) {
                                    return pos2 + 1;
                                }
                            }
                        }
                        continue;
                    }
                }
                continue;
            }
        }
        for (int pos3 = NW - 1; pos3 >= 0; pos3--) {
            if (windows.get(pos3).mIsWallpaper) {
                return pos3 + 1;
            }
        }
        return 0;
    }

    private final int reAddWindowLocked(int index, WindowState win) {
        WindowList windows = win.getWindowList();
        int NCW = win.mChildWindows.size();
        boolean added = false;
        for (int j = 0; j < NCW; j++) {
            WindowState cwin = win.mChildWindows.get(j);
            if (!added && cwin.mSubLayer >= 0) {
                win.mRebuilding = false;
                windows.add(index, win);
                index++;
                added = true;
            }
            cwin.mRebuilding = false;
            windows.add(index, cwin);
            index++;
        }
        if (!added) {
            win.mRebuilding = false;
            windows.add(index, win);
            index++;
        }
        this.mWindowsChanged = true;
        return index;
    }

    private final int reAddAppWindowsLocked(DisplayContent displayContent, int index, WindowToken token) {
        int NW = token.windows.size();
        for (int i = 0; i < NW; i++) {
            WindowState win = token.windows.get(i);
            if (win.mDisplayContent == displayContent) {
                index = reAddWindowLocked(index, win);
            }
        }
        return index;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveStackWindowsLocked(TaskStack stack) {
        DisplayContent displayContent = stack.getDisplayContent();
        ArrayList<Task> tasks = stack.getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = numTokens - 1; tokenNdx >= 0; tokenNdx--) {
                tmpRemoveAppWindowsLocked(tokens.get(tokenNdx));
            }
        }
        for (int taskNdx2 = 0; taskNdx2 < numTasks; taskNdx2++) {
            AppTokenList tokens2 = tasks.get(taskNdx2).mAppTokens;
            int pos = findAppWindowInsertionPointLocked(tokens2.get(0));
            int numTokens2 = tokens2.size();
            for (int tokenNdx2 = 0; tokenNdx2 < numTokens2; tokenNdx2++) {
                AppWindowToken wtoken = tokens2.get(tokenNdx2);
                if (wtoken != null) {
                    int newPos = reAddAppWindowsLocked(displayContent, pos, wtoken);
                    if (newPos != pos) {
                        displayContent.layoutNeeded = true;
                    }
                    pos = newPos;
                }
            }
        }
        if (!updateFocusedWindowLocked(3, false)) {
            assignLayersLocked(displayContent.getWindowList());
        }
        this.mInputMonitor.setUpdateInputWindowsNeededLw();
        performLayoutAndPlaceSurfacesLocked();
        this.mInputMonitor.updateInputWindowsLw(false);
    }

    public void createStack(int stackId, int relativeStackBoxId, int position, float weight) {
        synchronized (this.mWindowMap) {
            if (position <= 5 && (weight < 0.2f || weight > 0.8f)) {
                throw new IllegalArgumentException("createStack: weight must be between 0.2 and 0.8, weight=" + weight);
            }
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx);
                TaskStack stack = displayContent.createStack(stackId, relativeStackBoxId, position, weight);
                if (stack != null) {
                    this.mStackIdToStack.put(stackId, stack);
                    displayContent.moveStack(stack, true);
                    performLayoutAndPlaceSurfacesLocked();
                    return;
                }
            }
            Slog.e(TAG, "createStack: Unable to find relativeStackBoxId=" + relativeStackBoxId);
        }
    }

    public int removeStack(int stackId) {
        synchronized (this.mWindowMap) {
            TaskStack stack = this.mStackIdToStack.get(stackId);
            if (stack != null) {
                this.mStackIdToStack.delete(stackId);
                int nextStackId = stack.remove();
                stack.getDisplayContent().layoutNeeded = true;
                requestTraversalLocked();
                return nextStackId;
            }
            return 0;
        }
    }

    public void removeTask(int taskId) {
        synchronized (this.mWindowMap) {
            Task task = this.mTaskIdToTask.get(taskId);
            if (task == null) {
                return;
            }
            TaskStack stack = task.mStack;
            stack.removeTask(task);
            stack.getDisplayContent().layoutNeeded = true;
        }
    }

    public void addTask(int taskId, int stackId, boolean toTop) {
        synchronized (this.mWindowMap) {
            Task task = this.mTaskIdToTask.get(taskId);
            if (task == null) {
                return;
            }
            TaskStack stack = this.mStackIdToStack.get(stackId);
            stack.addTask(task, toTop);
            DisplayContent displayContent = stack.getDisplayContent();
            displayContent.layoutNeeded = true;
            performLayoutAndPlaceSurfacesLocked();
        }
    }

    public void resizeStackBox(int stackBoxId, float weight) {
        if (weight < 0.2f || weight > 0.8f) {
            throw new IllegalArgumentException("resizeStack: weight must be between 0.2 and 0.8, weight=" + weight);
        }
        synchronized (this.mWindowMap) {
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                if (this.mDisplayContents.valueAt(displayNdx).resizeStack(stackBoxId, weight)) {
                    performLayoutAndPlaceSurfacesLocked();
                    return;
                }
            }
            throw new IllegalArgumentException("resizeStack: stackBoxId " + stackBoxId + " not found.");
        }
    }

    public ArrayList<ActivityManager.StackBoxInfo> getStackBoxInfos() {
        ArrayList<ActivityManager.StackBoxInfo> stackBoxInfos;
        synchronized (this.mWindowMap) {
            stackBoxInfos = getDefaultDisplayContentLocked().getStackBoxInfos();
        }
        return stackBoxInfos;
    }

    public Rect getStackBounds(int stackId) {
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            Rect bounds = this.mDisplayContents.valueAt(displayNdx).getStackBounds(stackId);
            if (bounds != null) {
                return bounds;
            }
        }
        return null;
    }

    @Override // android.view.IWindowManager
    public void disableKeyguard(IBinder token, String tag) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DISABLE_KEYGUARD) != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        this.mKeyguardDisableHandler.sendMessage(this.mKeyguardDisableHandler.obtainMessage(1, new Pair(token, tag)));
    }

    @Override // android.view.IWindowManager
    public void reenableKeyguard(IBinder token) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DISABLE_KEYGUARD) != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        this.mKeyguardDisableHandler.sendMessage(this.mKeyguardDisableHandler.obtainMessage(2, token));
    }

    @Override // android.view.IWindowManager
    public void exitKeyguardSecurely(final IOnKeyguardExitResult callback) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DISABLE_KEYGUARD) != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        this.mPolicy.exitKeyguardSecurely(new WindowManagerPolicy.OnKeyguardExitResult() { // from class: com.android.server.wm.WindowManagerService.5
            @Override // android.view.WindowManagerPolicy.OnKeyguardExitResult
            public void onKeyguardExitResult(boolean success) {
                try {
                    callback.onKeyguardExitResult(success);
                } catch (RemoteException e) {
                }
            }
        });
    }

    @Override // android.view.IWindowManager
    public boolean inKeyguardRestrictedInputMode() {
        return this.mPolicy.inKeyguardRestrictedKeyInputMode();
    }

    @Override // android.view.IWindowManager
    public boolean isKeyguardLocked() {
        return this.mPolicy.isKeyguardLocked();
    }

    @Override // android.view.IWindowManager
    public boolean isKeyguardSecure() {
        return this.mPolicy.isKeyguardSecure();
    }

    @Override // android.view.IWindowManager
    public void dismissKeyguard() {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DISABLE_KEYGUARD) != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
        synchronized (this.mWindowMap) {
            this.mPolicy.dismissKeyguardLw();
        }
    }

    @Override // android.view.IWindowManager
    public void closeSystemDialogs(String reason) {
        synchronized (this.mWindowMap) {
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                WindowList windows = this.mDisplayContents.valueAt(displayNdx).getWindowList();
                int numWindows = windows.size();
                for (int winNdx = 0; winNdx < numWindows; winNdx++) {
                    WindowState w = windows.get(winNdx);
                    if (w.mHasSurface) {
                        try {
                            w.mClient.closeSystemDialogs(reason);
                        } catch (RemoteException e) {
                        }
                    }
                }
            }
        }
    }

    static float fixScale(float scale) {
        if (scale < 0.0f) {
            scale = 0.0f;
        } else if (scale > 20.0f) {
            scale = 20.0f;
        }
        return Math.abs(scale);
    }

    @Override // android.view.IWindowManager
    public void setAnimationScale(int which, float scale) {
        if (!checkCallingPermission(Manifest.permission.SET_ANIMATION_SCALE, "setAnimationScale()")) {
            throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
        }
        float scale2 = fixScale(scale);
        switch (which) {
            case 0:
                this.mWindowAnimationScale = scale2;
                break;
            case 1:
                this.mTransitionAnimationScale = scale2;
                break;
            case 2:
                this.mAnimatorDurationScale = scale2;
                break;
        }
        this.mH.sendEmptyMessage(14);
    }

    @Override // android.view.IWindowManager
    public void setAnimationScales(float[] scales) {
        if (!checkCallingPermission(Manifest.permission.SET_ANIMATION_SCALE, "setAnimationScale()")) {
            throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
        }
        if (scales != null) {
            if (scales.length >= 1) {
                this.mWindowAnimationScale = fixScale(scales[0]);
            }
            if (scales.length >= 2) {
                this.mTransitionAnimationScale = fixScale(scales[1]);
            }
            if (scales.length >= 3) {
                setAnimatorDurationScale(fixScale(scales[2]));
            }
        }
        this.mH.sendEmptyMessage(14);
    }

    private void setAnimatorDurationScale(float scale) {
        this.mAnimatorDurationScale = scale;
        ValueAnimator.setDurationScale(scale);
    }

    @Override // android.view.IWindowManager
    public float getAnimationScale(int which) {
        switch (which) {
            case 0:
                return this.mWindowAnimationScale;
            case 1:
                return this.mTransitionAnimationScale;
            case 2:
                return this.mAnimatorDurationScale;
            default:
                return 0.0f;
        }
    }

    @Override // android.view.IWindowManager
    public float[] getAnimationScales() {
        return new float[]{this.mWindowAnimationScale, this.mTransitionAnimationScale, this.mAnimatorDurationScale};
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void registerPointerEventListener(WindowManagerPolicy.PointerEventListener listener) {
        this.mPointerEventDispatcher.registerInputEventListener(listener);
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void unregisterPointerEventListener(WindowManagerPolicy.PointerEventListener listener) {
        this.mPointerEventDispatcher.unregisterInputEventListener(listener);
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public int getLidState() {
        int sw = this.mInputManager.getSwitchState(-1, -256, 0);
        if (sw > 0) {
            return 0;
        }
        if (sw == 0) {
            return 1;
        }
        return -1;
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void switchKeyboardLayout(int deviceId, int direction) {
        this.mInputManager.switchKeyboardLayout(deviceId, direction);
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void shutdown(boolean confirm) {
        ShutdownThread.shutdown(this.mContext, confirm);
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void rebootSafeMode(boolean confirm) {
        ShutdownThread.rebootSafeMode(this.mContext, confirm);
    }

    @Override // android.view.IWindowManager
    public void setInputFilter(IInputFilter filter) {
        if (!checkCallingPermission(Manifest.permission.FILTER_EVENTS, "setInputFilter()")) {
            throw new SecurityException("Requires FILTER_EVENTS permission");
        }
        this.mInputManager.setInputFilter(filter);
    }

    @Override // android.view.IWindowManager
    public void setTouchExplorationEnabled(boolean enabled) {
        this.mPolicy.setTouchExplorationEnabled(enabled);
    }

    public void setCurrentUser(int newUserId) {
        synchronized (this.mWindowMap) {
            int oldUserId = this.mCurrentUserId;
            this.mCurrentUserId = newUserId;
            this.mAppTransition.setCurrentUser(newUserId);
            this.mPolicy.setCurrentUserLw(newUserId);
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx);
                displayContent.switchUserStacks(oldUserId, newUserId);
                rebuildAppWindowListLocked(displayContent);
            }
            performLayoutAndPlaceSurfacesLocked();
        }
    }

    public void enableScreenAfterBoot() {
        synchronized (this.mWindowMap) {
            if (this.mSystemBooted) {
                return;
            }
            this.mSystemBooted = true;
            hideBootMessagesLocked();
            this.mH.sendEmptyMessageDelayed(23, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS);
            this.mPolicy.systemBooted();
            performEnableScreen();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableScreenIfNeededLocked() {
        if (this.mDisplayEnabled) {
            return;
        }
        if (!this.mSystemBooted && !this.mShowingBootMessages) {
            return;
        }
        this.mH.sendEmptyMessage(16);
    }

    public void performBootTimeout() {
        synchronized (this.mWindowMap) {
            if (this.mDisplayEnabled || this.mHeadless) {
                return;
            }
            Slog.w(TAG, "***** BOOT TIMEOUT: forcing display enabled");
            this.mForceDisplayEnabled = true;
            performEnableScreen();
        }
    }

    public void performEnableScreen() {
        synchronized (this.mWindowMap) {
            if (this.mDisplayEnabled) {
                return;
            }
            if (this.mSystemBooted || this.mShowingBootMessages) {
                if (!this.mForceDisplayEnabled) {
                    boolean haveBootMsg = false;
                    boolean haveApp = false;
                    boolean haveWallpaper = false;
                    boolean wallpaperEnabled = this.mContext.getResources().getBoolean(R.bool.config_enableWallpaperService) && !this.mOnlyCore;
                    boolean haveKeyguard = true;
                    WindowList windows = getDefaultWindowListLocked();
                    int N = windows.size();
                    for (int i = 0; i < N; i++) {
                        WindowState w = windows.get(i);
                        if (w.mAttrs.type == 2004) {
                            boolean vis = w.mViewVisibility == 0 && w.mPolicyVisibility;
                            haveKeyguard = !vis;
                        }
                        if (w.isVisibleLw() && !w.mObscured && !w.isDrawnLw()) {
                            return;
                        }
                        if (w.isDrawnLw()) {
                            if (w.mAttrs.type == 2021) {
                                haveBootMsg = true;
                            } else if (w.mAttrs.type == 2) {
                                haveApp = true;
                            } else if (w.mAttrs.type == 2013) {
                                haveWallpaper = true;
                            } else if (w.mAttrs.type == 2004) {
                                haveKeyguard = true;
                            }
                        }
                    }
                    if (!this.mSystemBooted && !haveBootMsg) {
                        return;
                    }
                    if (this.mSystemBooted && ((!haveApp && !haveKeyguard) || (wallpaperEnabled && !haveWallpaper))) {
                        return;
                    }
                }
                this.mDisplayEnabled = true;
                try {
                    IBinder surfaceFlinger = ServiceManager.getService("SurfaceFlinger");
                    if (surfaceFlinger != null) {
                        Parcel data = Parcel.obtain();
                        data.writeInterfaceToken("android.ui.ISurfaceComposer");
                        surfaceFlinger.transact(1, data, null, 0);
                        data.recycle();
                    }
                } catch (RemoteException e) {
                    Slog.e(TAG, "Boot completed: SurfaceFlinger is dead!");
                }
                this.mInputMonitor.setEventDispatchingLw(this.mEventDispatchingEnabled);
                this.mPolicy.enableScreenAfterBoot();
                updateRotationUnchecked(false, false);
            }
        }
    }

    public void showBootMessage(CharSequence msg, boolean always) {
        boolean first = false;
        synchronized (this.mWindowMap) {
            if (this.mAllowBootMessages) {
                if (!this.mShowingBootMessages) {
                    if (!always) {
                        return;
                    }
                    first = true;
                }
                if (this.mSystemBooted) {
                    return;
                }
                this.mShowingBootMessages = true;
                this.mPolicy.showBootMessage(msg, always);
                if (first) {
                    performEnableScreen();
                }
            }
        }
    }

    public void hideBootMessagesLocked() {
        if (this.mShowingBootMessages) {
            this.mShowingBootMessages = false;
            this.mPolicy.hideBootMessages();
        }
    }

    @Override // android.view.IWindowManager
    public void setInTouchMode(boolean mode) {
        synchronized (this.mWindowMap) {
            this.mInTouchMode = mode;
        }
    }

    @Override // android.view.IWindowManager
    public void showStrictModeViolation(boolean on) {
        if (this.mHeadless) {
            return;
        }
        int pid = Binder.getCallingPid();
        this.mH.sendMessage(this.mH.obtainMessage(25, on ? 1 : 0, pid));
    }

    @Override // android.view.IWindowManager
    public void setStrictModeVisualIndicatorPreference(String value) {
        SystemProperties.set(StrictMode.VISUAL_PROPERTY, value);
    }

    @Override // android.view.IWindowManager
    public Bitmap screenshotApplications(IBinder appToken, int displayId, int width, int height, boolean force565) {
        boolean screenshotReady;
        int minLayer;
        int dw;
        int dh;
        WindowState appWin;
        if (!checkCallingPermission(Manifest.permission.READ_FRAME_BUFFER, "screenshotApplications()")) {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
        Bitmap rawss = null;
        int maxLayer = 0;
        Rect frame = new Rect();
        float scale = 0.0f;
        int rot = 0;
        if (appToken == null) {
            screenshotReady = true;
            minLayer = 0;
        } else {
            screenshotReady = false;
            minLayer = Integer.MAX_VALUE;
        }
        int retryCount = 0;
        do {
            int i = retryCount;
            retryCount++;
            if (i > 0) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            }
            synchronized (this.mWindowMap) {
                DisplayContent displayContent = getDisplayContentLocked(displayId);
                if (displayContent == null) {
                    return null;
                }
                DisplayInfo displayInfo = displayContent.getDisplayInfo();
                dw = displayInfo.logicalWidth;
                dh = displayInfo.logicalHeight;
                int aboveAppLayer = (this.mPolicy.windowTypeToLayerLw(2) * 10000) + 1000;
                int aboveAppLayer2 = aboveAppLayer + 10000;
                boolean isImeTarget = (this.mInputMethodTarget == null || this.mInputMethodTarget.mAppToken == null || this.mInputMethodTarget.mAppToken.appToken == null || this.mInputMethodTarget.mAppToken.appToken.asBinder() != appToken) ? false : true;
                boolean including = false;
                appWin = null;
                WindowList windows = displayContent.getWindowList();
                Rect stackBounds = new Rect();
                for (int i2 = windows.size() - 1; i2 >= 0; i2--) {
                    WindowState ws = windows.get(i2);
                    if (ws.mHasSurface && ws.mLayer < aboveAppLayer2) {
                        if (!including && appToken != null && (!ws.mIsImWindow || !isImeTarget)) {
                            if (ws.mAppToken != null && ws.mAppToken.token == appToken) {
                                appWin = ws;
                                stackBounds.set(ws.getStackBounds());
                            }
                        }
                        boolean fullscreen = ws.isFullscreen(dw, dh);
                        including = (ws.mIsImWindow || fullscreen) ? false : true;
                        WindowStateAnimator winAnim = ws.mWinAnimator;
                        if (maxLayer < winAnim.mSurfaceLayer) {
                            maxLayer = winAnim.mSurfaceLayer;
                        }
                        if (minLayer > winAnim.mSurfaceLayer) {
                            minLayer = winAnim.mSurfaceLayer;
                        }
                        if (!ws.mIsWallpaper) {
                            Rect wf = ws.mFrame;
                            Rect cr = ws.mContentInsets;
                            int left = wf.left + cr.left;
                            int top = wf.top + cr.top;
                            int right = wf.right - cr.right;
                            int bottom = wf.bottom - cr.bottom;
                            frame.union(left, top, right, bottom);
                            frame.intersect(stackBounds);
                        }
                        if (ws.mAppToken != null && ws.mAppToken.token == appToken && ws.isDisplayedLw()) {
                            screenshotReady = true;
                        }
                        if (fullscreen) {
                            break;
                        }
                    }
                }
                if (appToken != null && appWin == null) {
                    return null;
                }
                if (screenshotReady) {
                    frame.intersect(0, 0, dw, dh);
                    if (frame.isEmpty() || maxLayer == 0) {
                        return null;
                    }
                    rot = getDefaultDisplayContentLocked().getDisplay().getRotation();
                    int fw = frame.width();
                    int fh = frame.height();
                    scale = Math.max(width / fw, height / fh);
                    dw = (int) (dw * scale);
                    dh = (int) (dh * scale);
                    if (rot == 1 || rot == 3) {
                        dw = dh;
                        dh = dw;
                        rot = rot == 1 ? 3 : 1;
                    }
                    rawss = SurfaceControl.screenshot(dw, dh, minLayer, maxLayer);
                }
                if (screenshotReady) {
                    break;
                }
            }
        } while (retryCount <= 3);
        if (retryCount > 3) {
            Slog.i(TAG, "Screenshot max retries " + retryCount + " of " + appToken + " appWin=" + (appWin == null ? "null" : appWin + " drawState=" + appWin.mWinAnimator.mDrawState));
        }
        if (rawss == null) {
            Slog.w(TAG, "Screenshot failure taking screenshot for (" + dw + "x" + dh + ") to layer " + maxLayer);
            return null;
        }
        Bitmap bm = Bitmap.createBitmap(width, height, force565 ? Bitmap.Config.RGB_565 : rawss.getConfig());
        frame.scale(scale);
        Matrix matrix = new Matrix();
        ScreenRotationAnimation.createRotationMatrix(rot, dw, dh, matrix);
        matrix.postTranslate(-FloatMath.ceil(frame.left), -FloatMath.ceil(frame.top));
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(-16777216);
        canvas.drawBitmap(rawss, matrix, null);
        canvas.setBitmap(null);
        int[] buffer = new int[bm.getWidth() * bm.getHeight()];
        bm.getPixels(buffer, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
        boolean allBlack = true;
        int firstColor = buffer[0];
        int i3 = 0;
        while (true) {
            if (i3 < buffer.length) {
                if (buffer[i3] == firstColor) {
                    i3++;
                } else {
                    allBlack = false;
                    break;
                }
            } else {
                break;
            }
        }
        if (allBlack) {
            Slog.i(TAG, "Screenshot " + appWin + " was monochrome(" + Integer.toHexString(firstColor) + ")! mSurfaceLayer=" + (appWin != null ? Integer.valueOf(appWin.mWinAnimator.mSurfaceLayer) : "null") + " minLayer=" + minLayer + " maxLayer=" + maxLayer);
        }
        rawss.recycle();
        return bm;
    }

    @Override // android.view.IWindowManager
    public void updateRotation(boolean alwaysSendConfiguration, boolean forceRelayout) {
        updateRotationUnchecked(alwaysSendConfiguration, forceRelayout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pauseRotationLocked() {
        this.mDeferredRotationPauseCount++;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resumeRotationLocked() {
        if (this.mDeferredRotationPauseCount > 0) {
            this.mDeferredRotationPauseCount--;
            if (this.mDeferredRotationPauseCount == 0) {
                boolean changed = updateRotationUncheckedLocked(false);
                if (changed) {
                    this.mH.sendEmptyMessage(18);
                }
            }
        }
    }

    public void updateRotationUnchecked(boolean alwaysSendConfiguration, boolean forceRelayout) {
        boolean changed;
        long origId = Binder.clearCallingIdentity();
        synchronized (this.mWindowMap) {
            changed = updateRotationUncheckedLocked(false);
            if (!changed || forceRelayout) {
                getDefaultDisplayContentLocked().layoutNeeded = true;
                performLayoutAndPlaceSurfacesLocked();
            }
        }
        if (changed || alwaysSendConfiguration) {
            sendNewConfiguration();
        }
        Binder.restoreCallingIdentity(origId);
    }

    @Override // android.view.IWindowManager
    public int getRotation() {
        return this.mRotation;
    }

    @Override // android.view.IWindowManager
    public boolean isRotationFrozen() {
        return this.mPolicy.getUserRotationMode() == 1;
    }

    @Override // android.view.IWindowManager
    public int watchRotation(IRotationWatcher watcher) {
        int i;
        final IBinder watcherBinder = watcher.asBinder();
        IBinder.DeathRecipient dr = new IBinder.DeathRecipient() { // from class: com.android.server.wm.WindowManagerService.6
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                synchronized (WindowManagerService.this.mWindowMap) {
                    int i2 = 0;
                    while (i2 < WindowManagerService.this.mRotationWatchers.size()) {
                        if (watcherBinder == WindowManagerService.this.mRotationWatchers.get(i2).asBinder()) {
                            IRotationWatcher removed = WindowManagerService.this.mRotationWatchers.remove(i2);
                            if (removed != null) {
                                removed.asBinder().unlinkToDeath(this, 0);
                            }
                            i2--;
                        }
                        i2++;
                    }
                }
            }
        };
        synchronized (this.mWindowMap) {
            try {
                watcher.asBinder().linkToDeath(dr, 0);
                this.mRotationWatchers.add(watcher);
            } catch (RemoteException e) {
            }
            i = this.mRotation;
        }
        return i;
    }

    @Override // android.view.IWindowManager
    public void removeRotationWatcher(IRotationWatcher watcher) {
        IBinder watcherBinder = watcher.asBinder();
        synchronized (this.mWindowMap) {
            int i = 0;
            while (i < this.mRotationWatchers.size()) {
                if (watcherBinder == this.mRotationWatchers.get(i).asBinder()) {
                    this.mRotationWatchers.remove(i);
                    i--;
                }
                i++;
            }
        }
    }

    @Override // android.view.IWindowManager
    public int getPreferredOptionsPanelGravity() {
        synchronized (this.mWindowMap) {
            int rotation = getRotation();
            DisplayContent displayContent = getDefaultDisplayContentLocked();
            if (displayContent.mInitialDisplayWidth < displayContent.mInitialDisplayHeight) {
                switch (rotation) {
                    case 0:
                    default:
                        return 81;
                    case 1:
                        return 85;
                    case 2:
                        return 81;
                    case 3:
                        return 8388691;
                }
            }
            switch (rotation) {
                case 0:
                default:
                    return 85;
                case 1:
                    return 81;
                case 2:
                    return 8388691;
                case 3:
                    return 81;
            }
        }
    }

    @Override // android.view.IWindowManager
    public boolean startViewServer(int port) {
        if (isSystemSecure() || !checkCallingPermission(Manifest.permission.DUMP, "startViewServer") || port < 1024) {
            return false;
        }
        if (this.mViewServer != null) {
            if (!this.mViewServer.isRunning()) {
                try {
                    return this.mViewServer.start();
                } catch (IOException e) {
                    Slog.w(TAG, "View server did not start");
                    return false;
                }
            }
            return false;
        }
        try {
            this.mViewServer = new ViewServer(this, port);
            return this.mViewServer.start();
        } catch (IOException e2) {
            Slog.w(TAG, "View server did not start");
            return false;
        }
    }

    private boolean isSystemSecure() {
        return "1".equals(SystemProperties.get(SYSTEM_SECURE, "1")) && "0".equals(SystemProperties.get(SYSTEM_DEBUGGABLE, "0"));
    }

    @Override // android.view.IWindowManager
    public boolean stopViewServer() {
        if (!isSystemSecure() && checkCallingPermission(Manifest.permission.DUMP, "stopViewServer") && this.mViewServer != null) {
            return this.mViewServer.stop();
        }
        return false;
    }

    @Override // android.view.IWindowManager
    public boolean isViewServerRunning() {
        return !isSystemSecure() && checkCallingPermission(Manifest.permission.DUMP, "isViewServerRunning") && this.mViewServer != null && this.mViewServer.isRunning();
    }

    public void addWindowChangeListener(WindowChangeListener listener) {
        synchronized (this.mWindowMap) {
            this.mWindowChangeListeners.add(listener);
        }
    }

    public void removeWindowChangeListener(WindowChangeListener listener) {
        synchronized (this.mWindowMap) {
            this.mWindowChangeListeners.remove(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyWindowsChanged() {
        synchronized (this.mWindowMap) {
            if (this.mWindowChangeListeners.isEmpty()) {
                return;
            }
            WindowChangeListener[] windowChangeListeners = (WindowChangeListener[]) this.mWindowChangeListeners.toArray(new WindowChangeListener[this.mWindowChangeListeners.size()]);
            for (WindowChangeListener windowChangeListener : windowChangeListeners) {
                windowChangeListener.windowsChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyFocusChanged() {
        synchronized (this.mWindowMap) {
            if (this.mWindowChangeListeners.isEmpty()) {
                return;
            }
            WindowChangeListener[] windowChangeListeners = (WindowChangeListener[]) this.mWindowChangeListeners.toArray(new WindowChangeListener[this.mWindowChangeListeners.size()]);
            for (WindowChangeListener windowChangeListener : windowChangeListeners) {
                windowChangeListener.focusChanged();
            }
        }
    }

    private WindowState findWindow(int hashCode) {
        if (hashCode == -1) {
            return getFocusedWindow();
        }
        synchronized (this.mWindowMap) {
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                WindowList windows = this.mDisplayContents.valueAt(displayNdx).getWindowList();
                int numWindows = windows.size();
                for (int winNdx = 0; winNdx < numWindows; winNdx++) {
                    WindowState w = windows.get(winNdx);
                    if (System.identityHashCode(w) == hashCode) {
                        return w;
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendNewConfiguration() {
        try {
            this.mActivityManager.updateConfiguration(null);
        } catch (RemoteException e) {
        }
    }

    public Configuration computeNewConfiguration() {
        Configuration config;
        synchronized (this.mWindowMap) {
            config = computeNewConfigurationLocked();
            if (config == null && this.mWaitingForConfig) {
                this.mWaitingForConfig = false;
                this.mLastFinishedFreezeSource = "new-config";
                performLayoutAndPlaceSurfacesLocked();
            }
        }
        return config;
    }

    Configuration computeNewConfigurationLocked() {
        Configuration config = new Configuration();
        config.fontScale = 0.0f;
        if (!computeScreenConfigurationLocked(config)) {
            return null;
        }
        return config;
    }

    private void adjustDisplaySizeRanges(DisplayInfo displayInfo, int rotation, int dw, int dh) {
        int width = this.mPolicy.getConfigDisplayWidth(dw, dh, rotation);
        if (width < displayInfo.smallestNominalAppWidth) {
            displayInfo.smallestNominalAppWidth = width;
        }
        if (width > displayInfo.largestNominalAppWidth) {
            displayInfo.largestNominalAppWidth = width;
        }
        int height = this.mPolicy.getConfigDisplayHeight(dw, dh, rotation);
        if (height < displayInfo.smallestNominalAppHeight) {
            displayInfo.smallestNominalAppHeight = height;
        }
        if (height > displayInfo.largestNominalAppHeight) {
            displayInfo.largestNominalAppHeight = height;
        }
    }

    private int reduceConfigLayout(int curLayout, int rotation, float density, int dw, int dh) {
        int w = this.mPolicy.getNonDecorDisplayWidth(dw, dh, rotation);
        int h = this.mPolicy.getNonDecorDisplayHeight(dw, dh, rotation);
        int longSize = w;
        int shortSize = h;
        if (longSize < shortSize) {
            longSize = shortSize;
            shortSize = longSize;
        }
        return Configuration.reduceScreenLayout(curLayout, (int) (longSize / density), (int) (shortSize / density));
    }

    private void computeSizeRangesAndScreenLayout(DisplayInfo displayInfo, boolean rotated, int dw, int dh, float density, Configuration outConfig) {
        int unrotDw;
        int unrotDh;
        if (rotated) {
            unrotDw = dh;
            unrotDh = dw;
        } else {
            unrotDw = dw;
            unrotDh = dh;
        }
        displayInfo.smallestNominalAppWidth = 1073741824;
        displayInfo.smallestNominalAppHeight = 1073741824;
        displayInfo.largestNominalAppWidth = 0;
        displayInfo.largestNominalAppHeight = 0;
        adjustDisplaySizeRanges(displayInfo, 0, unrotDw, unrotDh);
        adjustDisplaySizeRanges(displayInfo, 1, unrotDh, unrotDw);
        adjustDisplaySizeRanges(displayInfo, 2, unrotDw, unrotDh);
        adjustDisplaySizeRanges(displayInfo, 3, unrotDh, unrotDw);
        int sl = Configuration.resetScreenLayout(outConfig.screenLayout);
        int sl2 = reduceConfigLayout(reduceConfigLayout(reduceConfigLayout(reduceConfigLayout(sl, 0, density, unrotDw, unrotDh), 1, density, unrotDh, unrotDw), 2, density, unrotDw, unrotDh), 3, density, unrotDh, unrotDw);
        outConfig.smallestScreenWidthDp = (int) (displayInfo.smallestNominalAppWidth / density);
        outConfig.screenLayout = sl2;
    }

    private int reduceCompatConfigWidthSize(int curSize, int rotation, DisplayMetrics dm, int dw, int dh) {
        dm.noncompatWidthPixels = this.mPolicy.getNonDecorDisplayWidth(dw, dh, rotation);
        dm.noncompatHeightPixels = this.mPolicy.getNonDecorDisplayHeight(dw, dh, rotation);
        float scale = CompatibilityInfo.computeCompatibleScaling(dm, null);
        int size = (int) (((dm.noncompatWidthPixels / scale) / dm.density) + 0.5f);
        if (curSize == 0 || size < curSize) {
            curSize = size;
        }
        return curSize;
    }

    private int computeCompatSmallestWidth(boolean rotated, DisplayMetrics dm, int dw, int dh) {
        int unrotDw;
        int unrotDh;
        this.mTmpDisplayMetrics.setTo(dm);
        DisplayMetrics tmpDm = this.mTmpDisplayMetrics;
        if (rotated) {
            unrotDw = dh;
            unrotDh = dw;
        } else {
            unrotDw = dw;
            unrotDh = dh;
        }
        int sw = reduceCompatConfigWidthSize(0, 0, tmpDm, unrotDw, unrotDh);
        return reduceCompatConfigWidthSize(reduceCompatConfigWidthSize(reduceCompatConfigWidthSize(sw, 1, tmpDm, unrotDh, unrotDw), 2, tmpDm, unrotDw, unrotDh), 3, tmpDm, unrotDh, unrotDw);
    }

    boolean computeScreenConfigurationLocked(Configuration config) {
        if (!this.mDisplayReady) {
            return false;
        }
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        boolean rotated = this.mRotation == 1 || this.mRotation == 3;
        int realdw = rotated ? displayContent.mBaseDisplayHeight : displayContent.mBaseDisplayWidth;
        int realdh = rotated ? displayContent.mBaseDisplayWidth : displayContent.mBaseDisplayHeight;
        int dw = realdw;
        int dh = realdh;
        if (this.mAltOrientation) {
            if (realdw > realdh) {
                int maxw = (int) (realdh / 1.3f);
                if (maxw < realdw) {
                    dw = maxw;
                }
            } else {
                int maxh = (int) (realdw / 1.3f);
                if (maxh < realdh) {
                    dh = maxh;
                }
            }
        }
        if (config != null) {
            config.orientation = dw <= dh ? 1 : 2;
        }
        int appWidth = this.mPolicy.getNonDecorDisplayWidth(dw, dh, this.mRotation);
        int appHeight = this.mPolicy.getNonDecorDisplayHeight(dw, dh, this.mRotation);
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        synchronized (displayContent.mDisplaySizeLock) {
            displayInfo.rotation = this.mRotation;
            displayInfo.logicalWidth = dw;
            displayInfo.logicalHeight = dh;
            displayInfo.logicalDensityDpi = displayContent.mBaseDisplayDensity;
            displayInfo.appWidth = appWidth;
            displayInfo.appHeight = appHeight;
            displayInfo.getLogicalMetrics(this.mRealDisplayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
            displayInfo.getAppMetrics(this.mDisplayMetrics);
            this.mDisplayManagerService.setDisplayInfoOverrideFromWindowManager(displayContent.getDisplayId(), displayInfo);
        }
        DisplayMetrics dm = this.mDisplayMetrics;
        this.mCompatibleScreenScale = CompatibilityInfo.computeCompatibleScaling(dm, this.mCompatDisplayMetrics);
        if (config != null) {
            config.screenWidthDp = (int) (this.mPolicy.getConfigDisplayWidth(dw, dh, this.mRotation) / dm.density);
            config.screenHeightDp = (int) (this.mPolicy.getConfigDisplayHeight(dw, dh, this.mRotation) / dm.density);
            computeSizeRangesAndScreenLayout(displayInfo, rotated, dw, dh, dm.density, config);
            config.compatScreenWidthDp = (int) (config.screenWidthDp / this.mCompatibleScreenScale);
            config.compatScreenHeightDp = (int) (config.screenHeightDp / this.mCompatibleScreenScale);
            config.compatSmallestScreenWidthDp = computeCompatSmallestWidth(rotated, dm, dw, dh);
            config.densityDpi = displayContent.mBaseDisplayDensity;
            config.touchscreen = 1;
            config.keyboard = 1;
            config.navigation = 1;
            int keyboardPresence = 0;
            int navigationPresence = 0;
            InputDevice[] devices = this.mInputManager.getInputDevices();
            for (InputDevice device : devices) {
                if (!device.isVirtual()) {
                    int sources = device.getSources();
                    int presenceFlag = device.isExternal() ? 2 : 1;
                    if (this.mIsTouchDevice) {
                        if ((sources & 4098) == 4098) {
                            config.touchscreen = 3;
                        }
                    } else {
                        config.touchscreen = 1;
                    }
                    if ((sources & InputDevice.SOURCE_TRACKBALL) == 65540) {
                        config.navigation = 3;
                        navigationPresence |= presenceFlag;
                    } else if ((sources & 513) == 513 && config.navigation == 1) {
                        config.navigation = 2;
                        navigationPresence |= presenceFlag;
                    }
                    if (device.getKeyboardType() == 2) {
                        config.keyboard = 2;
                        keyboardPresence |= presenceFlag;
                    }
                }
            }
            boolean hardKeyboardAvailable = config.keyboard != 1;
            if (hardKeyboardAvailable != this.mHardKeyboardAvailable) {
                this.mHardKeyboardAvailable = hardKeyboardAvailable;
                this.mHardKeyboardEnabled = hardKeyboardAvailable;
                this.mH.removeMessages(22);
                this.mH.sendEmptyMessage(22);
            }
            if (!this.mHardKeyboardEnabled) {
                config.keyboard = 1;
            }
            config.keyboardHidden = 1;
            config.hardKeyboardHidden = 1;
            config.navigationHidden = 1;
            this.mPolicy.adjustConfigurationLw(config, keyboardPresence, navigationPresence);
            return true;
        }
        return true;
    }

    public boolean isHardKeyboardAvailable() {
        boolean z;
        synchronized (this.mWindowMap) {
            z = this.mHardKeyboardAvailable;
        }
        return z;
    }

    public boolean isHardKeyboardEnabled() {
        boolean z;
        synchronized (this.mWindowMap) {
            z = this.mHardKeyboardEnabled;
        }
        return z;
    }

    public void setHardKeyboardEnabled(boolean enabled) {
        synchronized (this.mWindowMap) {
            if (this.mHardKeyboardEnabled != enabled) {
                this.mHardKeyboardEnabled = enabled;
                this.mH.sendEmptyMessage(18);
            }
        }
    }

    public void setOnHardKeyboardStatusChangeListener(OnHardKeyboardStatusChangeListener listener) {
        synchronized (this.mWindowMap) {
            this.mHardKeyboardStatusChangeListener = listener;
        }
    }

    void notifyHardKeyboardStatusChange() {
        OnHardKeyboardStatusChangeListener listener;
        boolean available;
        boolean enabled;
        synchronized (this.mWindowMap) {
            listener = this.mHardKeyboardStatusChangeListener;
            available = this.mHardKeyboardAvailable;
            enabled = this.mHardKeyboardEnabled;
        }
        if (listener != null) {
            listener.onHardKeyboardStatusChange(available, enabled);
        }
    }

    @Override // android.view.IWindowManager
    public void pauseKeyDispatching(IBinder _token) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "pauseKeyDispatching()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            WindowToken token = this.mTokenMap.get(_token);
            if (token != null) {
                this.mInputMonitor.pauseDispatchingLw(token);
            }
        }
    }

    @Override // android.view.IWindowManager
    public void resumeKeyDispatching(IBinder _token) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "resumeKeyDispatching()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            WindowToken token = this.mTokenMap.get(_token);
            if (token != null) {
                this.mInputMonitor.resumeDispatchingLw(token);
            }
        }
    }

    @Override // android.view.IWindowManager
    public void setEventDispatching(boolean enabled) {
        if (!checkCallingPermission(Manifest.permission.MANAGE_APP_TOKENS, "setEventDispatching()")) {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
        synchronized (this.mWindowMap) {
            this.mEventDispatchingEnabled = enabled;
            if (this.mDisplayEnabled) {
                this.mInputMonitor.setEventDispatchingLw(enabled);
            }
            sendScreenStatusToClientsLocked();
        }
    }

    @Override // android.view.IWindowManager
    public IBinder getFocusedWindowToken() {
        if (!checkCallingPermission(Manifest.permission.RETRIEVE_WINDOW_INFO, "getFocusedWindowToken()")) {
            throw new SecurityException("Requires RETRIEVE_WINDOW_INFO permission.");
        }
        synchronized (this.mWindowMap) {
            WindowState windowState = getFocusedWindowLocked();
            if (windowState != null) {
                return windowState.mClient.asBinder();
            }
            return null;
        }
    }

    private WindowState getFocusedWindow() {
        WindowState focusedWindowLocked;
        synchronized (this.mWindowMap) {
            focusedWindowLocked = getFocusedWindowLocked();
        }
        return focusedWindowLocked;
    }

    private WindowState getFocusedWindowLocked() {
        return this.mCurrentFocus;
    }

    public boolean detectSafeMode() {
        if (!this.mInputMonitor.waitForInputDevicesReady(1000L)) {
            Slog.w(TAG, "Devices still not ready after waiting 1000 milliseconds before attempting to detect safe mode.");
        }
        int menuState = this.mInputManager.getKeyCodeState(-1, -256, 82);
        int sState = this.mInputManager.getKeyCodeState(-1, -256, 47);
        int dpadState = this.mInputManager.getKeyCodeState(-1, 513, 23);
        int trackballState = this.mInputManager.getScanCodeState(-1, InputDevice.SOURCE_TRACKBALL, 272);
        int volumeDownState = this.mInputManager.getKeyCodeState(-1, -256, 25);
        this.mSafeMode = menuState > 0 || sState > 0 || dpadState > 0 || trackballState > 0 || volumeDownState > 0;
        try {
            if (SystemProperties.getInt(ShutdownThread.REBOOT_SAFEMODE_PROPERTY, 0) != 0) {
                this.mSafeMode = true;
                SystemProperties.set(ShutdownThread.REBOOT_SAFEMODE_PROPERTY, "");
            }
        } catch (IllegalArgumentException e) {
        }
        if (this.mSafeMode) {
            Log.i(TAG, "SAFE MODE ENABLED (menu=" + menuState + " s=" + sState + " dpad=" + dpadState + " trackball=" + trackballState + Separators.RPAREN);
        } else {
            Log.i(TAG, "SAFE MODE not enabled");
        }
        this.mPolicy.setSafeMode(this.mSafeMode);
        return this.mSafeMode;
    }

    public void displayReady() {
        displayReady(0);
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDefaultDisplayContentLocked();
            readForcedDisplaySizeAndDensityLocked(displayContent);
            this.mDisplayReady = true;
        }
        try {
            this.mActivityManager.updateConfiguration(null);
        } catch (RemoteException e) {
        }
        synchronized (this.mWindowMap) {
            this.mIsTouchDevice = this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
            configureDisplayPolicyLocked(getDefaultDisplayContentLocked());
        }
        try {
            this.mActivityManager.updateConfiguration(null);
        } catch (RemoteException e2) {
        }
    }

    private void displayReady(int displayId) {
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                this.mAnimator.addDisplayLocked(displayId);
                synchronized (displayContent.mDisplaySizeLock) {
                    DisplayInfo displayInfo = displayContent.getDisplayInfo();
                    DisplayInfo newDisplayInfo = this.mDisplayManagerService.getDisplayInfo(displayId);
                    if (newDisplayInfo != null) {
                        displayInfo.copyFrom(newDisplayInfo);
                    }
                    displayContent.mInitialDisplayWidth = displayInfo.logicalWidth;
                    displayContent.mInitialDisplayHeight = displayInfo.logicalHeight;
                    displayContent.mInitialDisplayDensity = displayInfo.logicalDensityDpi;
                    displayContent.mBaseDisplayWidth = displayContent.mInitialDisplayWidth;
                    displayContent.mBaseDisplayHeight = displayContent.mInitialDisplayHeight;
                    displayContent.mBaseDisplayDensity = displayContent.mInitialDisplayDensity;
                    displayContent.mBaseDisplayRect.set(0, 0, displayContent.mBaseDisplayWidth, displayContent.mBaseDisplayHeight);
                }
            }
        }
    }

    public void systemReady() {
        this.mPolicy.systemReady();
    }

    private void sendScreenStatusToClientsLocked() {
        boolean on = this.mPowerManager.isScreenOn();
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            WindowList windows = this.mDisplayContents.valueAt(displayNdx).getWindowList();
            int numWindows = windows.size();
            for (int winNdx = 0; winNdx < numWindows; winNdx++) {
                try {
                    windows.get(winNdx).mClient.dispatchScreenState(on);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WindowManagerService$H.class */
    public final class H extends Handler {
        public static final int REPORT_FOCUS_CHANGE = 2;
        public static final int REPORT_LOSING_FOCUS = 3;
        public static final int DO_TRAVERSAL = 4;
        public static final int ADD_STARTING = 5;
        public static final int REMOVE_STARTING = 6;
        public static final int FINISHED_STARTING = 7;
        public static final int REPORT_APPLICATION_TOKEN_WINDOWS = 8;
        public static final int REPORT_APPLICATION_TOKEN_DRAWN = 9;
        public static final int WINDOW_FREEZE_TIMEOUT = 11;
        public static final int APP_TRANSITION_TIMEOUT = 13;
        public static final int PERSIST_ANIMATION_SCALE = 14;
        public static final int FORCE_GC = 15;
        public static final int ENABLE_SCREEN = 16;
        public static final int APP_FREEZE_TIMEOUT = 17;
        public static final int SEND_NEW_CONFIGURATION = 18;
        public static final int REPORT_WINDOWS_CHANGE = 19;
        public static final int DRAG_START_TIMEOUT = 20;
        public static final int DRAG_END_TIMEOUT = 21;
        public static final int REPORT_HARD_KEYBOARD_STATUS_CHANGE = 22;
        public static final int BOOT_TIMEOUT = 23;
        public static final int WAITING_FOR_DRAWN_TIMEOUT = 24;
        public static final int SHOW_STRICT_MODE_VIOLATION = 25;
        public static final int DO_ANIMATION_CALLBACK = 26;
        public static final int DO_DISPLAY_ADDED = 27;
        public static final int DO_DISPLAY_REMOVED = 28;
        public static final int DO_DISPLAY_CHANGED = 29;
        public static final int CLIENT_FREEZE_TIMEOUT = 30;
        public static final int TAP_OUTSIDE_STACK = 31;
        public static final int NOTIFY_ACTIVITY_DRAWN = 32;
        public static final int REMOVE_STARTING_TIMEOUT = 33;

        H() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:133:0x0378, code lost:
            r11.this$0.mPolicy.removeStartingWindow(r0, r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:134:0x0389, code lost:
            r15 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:135:0x038b, code lost:
            android.util.Slog.w(com.android.server.wm.WindowManagerService.TAG, "Exception when removing starting window", r15);
         */
        @Override // android.os.Handler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public void handleMessage(android.os.Message r12) {
            /*
                Method dump skipped, instructions count: 2149
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.H.handleMessage(android.os.Message):void");
        }
    }

    @Override // android.view.IWindowManager
    public IWindowSession openSession(IInputMethodClient client, IInputContext inputContext) {
        if (client == null) {
            throw new IllegalArgumentException("null client");
        }
        if (inputContext == null) {
            throw new IllegalArgumentException("null inputContext");
        }
        Session session = new Session(this, client, inputContext);
        return session;
    }

    @Override // android.view.IWindowManager
    public boolean inputMethodClientHasFocus(IInputMethodClient client) {
        synchronized (this.mWindowMap) {
            int idx = findDesiredInputMethodWindowIndexLocked(false);
            if (idx > 0) {
                WindowState imFocus = getDefaultWindowListLocked().get(idx - 1);
                if (imFocus != null) {
                    if (imFocus.mAttrs.type == 3 && imFocus.mAppToken != null) {
                        int i = 0;
                        while (true) {
                            if (i >= imFocus.mAppToken.windows.size()) {
                                break;
                            }
                            WindowState w = imFocus.mAppToken.windows.get(i);
                            if (w == imFocus) {
                                i++;
                            } else {
                                Log.i(TAG, "Switching to real app window: " + w);
                                imFocus = w;
                                break;
                            }
                        }
                    }
                    if (imFocus.mSession.mClient != null && imFocus.mSession.mClient.asBinder() == client.asBinder()) {
                        return true;
                    }
                }
            }
            if (this.mCurrentFocus != null && this.mCurrentFocus.mSession.mClient != null && this.mCurrentFocus.mSession.mClient.asBinder() == client.asBinder()) {
                return true;
            }
            return false;
        }
    }

    @Override // android.view.IWindowManager
    public void getInitialDisplaySize(int displayId, Point size) {
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                synchronized (displayContent.mDisplaySizeLock) {
                    size.x = displayContent.mInitialDisplayWidth;
                    size.y = displayContent.mInitialDisplayHeight;
                }
            }
        }
    }

    @Override // android.view.IWindowManager
    public void getBaseDisplaySize(int displayId, Point size) {
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                synchronized (displayContent.mDisplaySizeLock) {
                    size.x = displayContent.mBaseDisplayWidth;
                    size.y = displayContent.mBaseDisplayHeight;
                }
            }
        }
    }

    @Override // android.view.IWindowManager
    public void setForcedDisplaySize(int displayId, int width, int height) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        if (displayId != 0) {
            throw new IllegalArgumentException("Can only set the default display");
        }
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                int width2 = Math.min(Math.max(width, 200), displayContent.mInitialDisplayWidth * 2);
                int height2 = Math.min(Math.max(height, 200), displayContent.mInitialDisplayHeight * 2);
                setForcedDisplaySizeLocked(displayContent, width2, height2);
                Settings.Global.putString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_SIZE_FORCED, width2 + Separators.COMMA + height2);
            }
        }
    }

    private void readForcedDisplaySizeAndDensityLocked(DisplayContent displayContent) {
        int pos;
        String sizeStr = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_SIZE_FORCED);
        if (sizeStr == null || sizeStr.length() == 0) {
            sizeStr = SystemProperties.get(SIZE_OVERRIDE, null);
        }
        if (sizeStr != null && sizeStr.length() > 0 && (pos = sizeStr.indexOf(44)) > 0 && sizeStr.lastIndexOf(44) == pos) {
            try {
                int width = Integer.parseInt(sizeStr.substring(0, pos));
                int height = Integer.parseInt(sizeStr.substring(pos + 1));
                synchronized (displayContent.mDisplaySizeLock) {
                    if (displayContent.mBaseDisplayWidth != width || displayContent.mBaseDisplayHeight != height) {
                        Slog.i(TAG, "FORCED DISPLAY SIZE: " + width + "x" + height);
                        displayContent.mBaseDisplayWidth = width;
                        displayContent.mBaseDisplayHeight = height;
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        String densityStr = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_DENSITY_FORCED);
        if (densityStr == null || densityStr.length() == 0) {
            densityStr = SystemProperties.get(DENSITY_OVERRIDE, null);
        }
        if (densityStr != null && densityStr.length() > 0) {
            try {
                int density = Integer.parseInt(densityStr);
                synchronized (displayContent.mDisplaySizeLock) {
                    if (displayContent.mBaseDisplayDensity != density) {
                        Slog.i(TAG, "FORCED DISPLAY DENSITY: " + density);
                        displayContent.mBaseDisplayDensity = density;
                    }
                }
            } catch (NumberFormatException e2) {
            }
        }
    }

    private void setForcedDisplaySizeLocked(DisplayContent displayContent, int width, int height) {
        Slog.i(TAG, "Using new display size: " + width + "x" + height);
        synchronized (displayContent.mDisplaySizeLock) {
            displayContent.mBaseDisplayWidth = width;
            displayContent.mBaseDisplayHeight = height;
        }
        reconfigureDisplayLocked(displayContent);
    }

    @Override // android.view.IWindowManager
    public void clearForcedDisplaySize(int displayId) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        if (displayId != 0) {
            throw new IllegalArgumentException("Can only set the default display");
        }
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                setForcedDisplaySizeLocked(displayContent, displayContent.mInitialDisplayWidth, displayContent.mInitialDisplayHeight);
                Settings.Global.putString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_SIZE_FORCED, "");
            }
        }
    }

    @Override // android.view.IWindowManager
    public int getInitialDisplayDensity(int displayId) {
        int i;
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                synchronized (displayContent.mDisplaySizeLock) {
                    i = displayContent.mInitialDisplayDensity;
                }
                return i;
            }
            return -1;
        }
    }

    @Override // android.view.IWindowManager
    public int getBaseDisplayDensity(int displayId) {
        int i;
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                synchronized (displayContent.mDisplaySizeLock) {
                    i = displayContent.mBaseDisplayDensity;
                }
                return i;
            }
            return -1;
        }
    }

    @Override // android.view.IWindowManager
    public void setForcedDisplayDensity(int displayId, int density) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        if (displayId != 0) {
            throw new IllegalArgumentException("Can only set the default display");
        }
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                setForcedDisplayDensityLocked(displayContent, density);
                Settings.Global.putString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_DENSITY_FORCED, Integer.toString(density));
            }
        }
    }

    private void setForcedDisplayDensityLocked(DisplayContent displayContent, int density) {
        Slog.i(TAG, "Using new display density: " + density);
        synchronized (displayContent.mDisplaySizeLock) {
            displayContent.mBaseDisplayDensity = density;
        }
        reconfigureDisplayLocked(displayContent);
    }

    @Override // android.view.IWindowManager
    public void clearForcedDisplayDensity(int displayId) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        if (displayId != 0) {
            throw new IllegalArgumentException("Can only set the default display");
        }
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                setForcedDisplayDensityLocked(displayContent, displayContent.mInitialDisplayDensity);
                Settings.Global.putString(this.mContext.getContentResolver(), Settings.Global.DISPLAY_DENSITY_FORCED, "");
            }
        }
    }

    private void reconfigureDisplayLocked(DisplayContent displayContent) {
        configureDisplayPolicyLocked(displayContent);
        displayContent.layoutNeeded = true;
        boolean configChanged = updateOrientationFromAppTokensLocked(false);
        this.mTempConfiguration.setToDefaults();
        this.mTempConfiguration.fontScale = this.mCurConfiguration.fontScale;
        if (computeScreenConfigurationLocked(this.mTempConfiguration) && this.mCurConfiguration.diff(this.mTempConfiguration) != 0) {
            configChanged = true;
        }
        if (configChanged) {
            this.mWaitingForConfig = true;
            startFreezingDisplayLocked(false, 0, 0);
            this.mH.sendEmptyMessage(18);
        }
        performLayoutAndPlaceSurfacesLocked();
    }

    private void configureDisplayPolicyLocked(DisplayContent displayContent) {
        this.mPolicy.setInitialDisplaySize(displayContent.getDisplay(), displayContent.mBaseDisplayWidth, displayContent.mBaseDisplayHeight, displayContent.mBaseDisplayDensity);
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        this.mPolicy.setDisplayOverscan(displayContent.getDisplay(), displayInfo.overscanLeft, displayInfo.overscanTop, displayInfo.overscanRight, displayInfo.overscanBottom);
    }

    @Override // android.view.IWindowManager
    public void setOverscan(int displayId, int left, int top, int right, int bottom) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) != 0) {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
        synchronized (this.mWindowMap) {
            DisplayContent displayContent = getDisplayContentLocked(displayId);
            if (displayContent != null) {
                setOverscanLocked(displayContent, left, top, right, bottom);
            }
        }
    }

    private void setOverscanLocked(DisplayContent displayContent, int left, int top, int right, int bottom) {
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        synchronized (displayContent.mDisplaySizeLock) {
            displayInfo.overscanLeft = left;
            displayInfo.overscanTop = top;
            displayInfo.overscanRight = right;
            displayInfo.overscanBottom = bottom;
        }
        this.mDisplaySettings.setOverscanLocked(displayInfo.name, left, top, right, bottom);
        this.mDisplaySettings.writeSettingsLocked();
        reconfigureDisplayLocked(displayContent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final WindowState windowForClientLocked(Session session, IWindow client, boolean throwOnError) {
        return windowForClientLocked(session, client.asBinder(), throwOnError);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final WindowState windowForClientLocked(Session session, IBinder client, boolean throwOnError) {
        WindowState win = this.mWindowMap.get(client);
        if (win == null) {
            RuntimeException ex = new IllegalArgumentException("Requested window " + client + " does not exist");
            if (throwOnError) {
                throw ex;
            }
            Slog.w(TAG, "Failed looking up window", ex);
            return null;
        } else if (session != null && win.mSession != session) {
            RuntimeException ex2 = new IllegalArgumentException("Requested window " + client + " is in session " + win.mSession + ", not " + session);
            if (throwOnError) {
                throw ex2;
            }
            Slog.w(TAG, "Failed looking up window", ex2);
            return null;
        } else {
            return win;
        }
    }

    final void rebuildAppWindowListLocked() {
        rebuildAppWindowListLocked(getDefaultDisplayContentLocked());
    }

    private void rebuildAppWindowListLocked(DisplayContent displayContent) {
        WindowList windows = displayContent.getWindowList();
        int NW = windows.size();
        int lastBelow = -1;
        int numRemoved = 0;
        if (this.mRebuildTmp.length < NW) {
            this.mRebuildTmp = new WindowState[NW + 10];
        }
        int i = 0;
        while (i < NW) {
            WindowState w = windows.get(i);
            if (w.mAppToken != null) {
                WindowState win = windows.remove(i);
                win.mRebuilding = true;
                this.mRebuildTmp[numRemoved] = win;
                this.mWindowsChanged = true;
                NW--;
                numRemoved++;
            } else {
                if (lastBelow == i - 1 && (w.mAttrs.type == 2013 || w.mAttrs.type == 2025)) {
                    lastBelow = i;
                }
                i++;
            }
        }
        int lastBelow2 = lastBelow + 1;
        int i2 = lastBelow2;
        AppTokenList exitingAppTokens = displayContent.mExitingAppTokens;
        int NT = exitingAppTokens.size();
        for (int j = 0; j < NT; j++) {
            i2 = reAddAppWindowsLocked(displayContent, i2, exitingAppTokens.get(j));
        }
        ArrayList<Task> tasks = displayContent.getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = 0; tokenNdx < numTokens; tokenNdx++) {
                AppWindowToken wtoken = tokens.get(tokenNdx);
                i2 = reAddAppWindowsLocked(displayContent, i2, wtoken);
            }
        }
        int i3 = i2 - lastBelow2;
        if (i3 != numRemoved) {
            Slog.w(TAG, "Rebuild removed " + numRemoved + " windows but added " + i3, new RuntimeException("here").fillInStackTrace());
            for (int i4 = 0; i4 < numRemoved; i4++) {
                WindowState ws = this.mRebuildTmp[i4];
                if (ws.mRebuilding) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new FastPrintWriter((Writer) sw, false, 1024);
                    ws.dump(pw, "", true);
                    pw.flush();
                    Slog.w(TAG, "This window was lost: " + ws);
                    Slog.w(TAG, sw.toString());
                    ws.mWinAnimator.destroySurfaceLocked();
                }
            }
            Slog.w(TAG, "Current app token list:");
            dumpAppTokensLocked();
            Slog.w(TAG, "Final window list:");
            dumpWindowsLocked();
        }
    }

    private final void assignLayersLocked(WindowList windows) {
        int N = windows.size();
        int curBaseLayer = 0;
        int curLayer = 0;
        boolean anyLayerChanged = false;
        for (int i = 0; i < N; i++) {
            WindowState w = windows.get(i);
            WindowStateAnimator winAnimator = w.mWinAnimator;
            boolean layerChanged = false;
            int oldLayer = w.mLayer;
            if (w.mBaseLayer == curBaseLayer || w.mIsImWindow || (i > 0 && w.mIsWallpaper)) {
                curLayer += 5;
                w.mLayer = curLayer;
            } else {
                int i2 = w.mBaseLayer;
                curLayer = i2;
                curBaseLayer = i2;
                w.mLayer = curLayer;
            }
            if (w.mLayer != oldLayer) {
                layerChanged = true;
                anyLayerChanged = true;
            }
            AppWindowToken wtoken = w.mAppToken;
            int oldLayer2 = winAnimator.mAnimLayer;
            if (w.mTargetAppToken != null) {
                winAnimator.mAnimLayer = w.mLayer + w.mTargetAppToken.mAppAnimator.animLayerAdjustment;
            } else if (wtoken != null) {
                winAnimator.mAnimLayer = w.mLayer + wtoken.mAppAnimator.animLayerAdjustment;
            } else {
                winAnimator.mAnimLayer = w.mLayer;
            }
            if (w.mIsImWindow) {
                winAnimator.mAnimLayer += this.mInputMethodAnimLayerAdjustment;
            } else if (w.mIsWallpaper) {
                winAnimator.mAnimLayer += this.mWallpaperAnimLayerAdjustment;
            }
            if (winAnimator.mAnimLayer != oldLayer2) {
                layerChanged = true;
                anyLayerChanged = true;
            }
            if (layerChanged && w.getStack().isDimming(winAnimator)) {
                scheduleAnimationLocked();
            }
        }
        if (this.mDisplayMagnifier != null && anyLayerChanged && windows.get(windows.size() - 1).getDisplayId() == 0) {
            this.mDisplayMagnifier.onWindowLayersChangedLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void performLayoutAndPlaceSurfacesLocked() {
        int loopCount = 6;
        do {
            this.mTraversalScheduled = false;
            performLayoutAndPlaceSurfacesLockedLoop();
            this.mH.removeMessages(4);
            loopCount--;
            if (!this.mTraversalScheduled) {
                break;
            }
        } while (loopCount > 0);
        this.mInnerFields.mWallpaperActionPending = false;
    }

    private final void performLayoutAndPlaceSurfacesLockedLoop() {
        if (this.mInLayout) {
            Slog.w(TAG, "performLayoutAndPlaceSurfacesLocked called while in layout. Callers=" + Debug.getCallers(3));
        } else if (!this.mWaitingForConfig && this.mDisplayReady) {
            Trace.traceBegin(32L, "wmLayout");
            this.mInLayout = true;
            boolean recoveringMemory = false;
            try {
                if (this.mForceRemoves != null) {
                    recoveringMemory = true;
                    for (int i = 0; i < this.mForceRemoves.size(); i++) {
                        WindowState ws = this.mForceRemoves.get(i);
                        Slog.i(TAG, "Force removing: " + ws);
                        removeWindowInnerLocked(ws.mSession, ws);
                    }
                    this.mForceRemoves = null;
                    Slog.w(TAG, "Due to memory failure, waiting a bit for next layout");
                    Object tmp = new Object();
                    synchronized (tmp) {
                        try {
                            tmp.wait(250L);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            } catch (RuntimeException e2) {
                Log.wtf(TAG, "Unhandled exception while force removing for memory", e2);
            }
            try {
                performLayoutAndPlaceSurfacesLockedInner(recoveringMemory);
                this.mInLayout = false;
                if (needsLayout()) {
                    int i2 = this.mLayoutRepeatCount + 1;
                    this.mLayoutRepeatCount = i2;
                    if (i2 < 6) {
                        requestTraversalLocked();
                    } else {
                        Slog.e(TAG, "Performed 6 layouts in a row. Skipping");
                        this.mLayoutRepeatCount = 0;
                    }
                } else {
                    this.mLayoutRepeatCount = 0;
                }
                if (this.mWindowsChanged && !this.mWindowChangeListeners.isEmpty()) {
                    this.mH.removeMessages(19);
                    this.mH.sendEmptyMessage(19);
                }
            } catch (RuntimeException e3) {
                this.mInLayout = false;
                Log.wtf(TAG, "Unhandled exception while laying out windows", e3);
            }
            Trace.traceEnd(32L);
        }
    }

    private final void performLayoutLockedInner(DisplayContent displayContent, boolean initial, boolean updateInputWindows) {
        if (!displayContent.layoutNeeded) {
            return;
        }
        displayContent.layoutNeeded = false;
        WindowList windows = displayContent.getWindowList();
        boolean isDefaultDisplay = displayContent.isDefaultDisplay;
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        int dw = displayInfo.logicalWidth;
        int dh = displayInfo.logicalHeight;
        int NFW = this.mFakeWindows.size();
        for (int i = 0; i < NFW; i++) {
            this.mFakeWindows.get(i).layout(dw, dh);
        }
        int N = windows.size();
        WindowStateAnimator universeBackground = null;
        this.mPolicy.beginLayoutLw(isDefaultDisplay, dw, dh, this.mRotation);
        if (isDefaultDisplay) {
            this.mSystemDecorLayer = this.mPolicy.getSystemDecorLayerLw();
            this.mScreenRect.set(0, 0, dw, dh);
        }
        this.mPolicy.getContentRectLw(this.mTmpContentRect);
        displayContent.setStackBoxSize(this.mTmpContentRect);
        int seq = this.mLayoutSeq + 1;
        if (seq < 0) {
            seq = 0;
        }
        this.mLayoutSeq = seq;
        boolean behindDream = false;
        int topAttached = -1;
        for (int i2 = N - 1; i2 >= 0; i2--) {
            WindowState win = windows.get(i2);
            boolean gone = (behindDream && this.mPolicy.canBeForceHidden(win, win.mAttrs)) || win.isGoneForLayoutLw();
            if (!gone || !win.mHaveFrame || win.mLayoutNeeded || ((win.mAttrs.type == 2004 && win.isConfigChanged()) || win.mAttrs.type == 2025)) {
                if (!win.mLayoutAttached) {
                    if (initial) {
                        win.mContentChanged = false;
                    }
                    if (win.mAttrs.type == 2023) {
                        behindDream = true;
                    }
                    win.mLayoutNeeded = false;
                    win.prelayout();
                    this.mPolicy.layoutWindowLw(win, win.mAttrs, null);
                    win.mLayoutSeq = seq;
                } else if (topAttached < 0) {
                    topAttached = i2;
                }
            }
            if (win.mViewVisibility == 0 && win.mAttrs.type == 2025 && universeBackground == null) {
                universeBackground = win.mWinAnimator;
            }
        }
        if (this.mAnimator.mUniverseBackground != universeBackground) {
            this.mFocusMayChange = true;
            this.mAnimator.mUniverseBackground = universeBackground;
        }
        boolean attachedBehindDream = false;
        for (int i3 = topAttached; i3 >= 0; i3--) {
            WindowState win2 = windows.get(i3);
            if (win2.mLayoutAttached) {
                if ((!attachedBehindDream || !this.mPolicy.canBeForceHidden(win2, win2.mAttrs)) && ((win2.mViewVisibility != 8 && win2.mRelayoutCalled) || !win2.mHaveFrame || win2.mLayoutNeeded)) {
                    if (initial) {
                        win2.mContentChanged = false;
                    }
                    win2.mLayoutNeeded = false;
                    win2.prelayout();
                    this.mPolicy.layoutWindowLw(win2, win2.mAttrs, win2.mAttachedWindow);
                    win2.mLayoutSeq = seq;
                }
            } else if (win2.mAttrs.type == 2023) {
                attachedBehindDream = behindDream;
            }
        }
        this.mInputMonitor.setUpdateInputWindowsNeededLw();
        if (updateInputWindows) {
            this.mInputMonitor.updateInputWindowsLw(false);
        }
        this.mPolicy.finishLayoutLw();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeWindowFreezingScreenIfNeededLocked(WindowState w) {
        if (!okToDisplay()) {
            w.mOrientationChanging = true;
            w.mLastFreezeDuration = 0;
            this.mInnerFields.mOrientationChangeComplete = false;
            if (!this.mWindowsFreezingScreen) {
                this.mWindowsFreezingScreen = true;
                this.mH.removeMessages(11);
                this.mH.sendEmptyMessageDelayed(11, 2000L);
            }
        }
    }

    public int handleAppTransitionReadyLocked(WindowList windows) {
        AppWindowToken lowerWallpaperAppToken;
        AppWindowToken upperWallpaperAppToken;
        AppWindowToken wtoken;
        WindowState ws;
        int changes = 0;
        int NN = this.mOpeningApps.size();
        boolean goodToGo = true;
        if (!this.mDisplayFrozen && !this.mAppTransition.isTimeout()) {
            for (int i = 0; i < NN && goodToGo; i++) {
                AppWindowToken wtoken2 = this.mOpeningApps.get(i);
                if (!wtoken2.allDrawn && !wtoken2.startingDisplayed && !wtoken2.startingMoved) {
                    goodToGo = false;
                }
            }
        }
        if (goodToGo) {
            int transit = this.mAppTransition.getAppTransition();
            if (this.mSkipAppTransitionAnimation) {
                transit = -1;
            }
            this.mAppTransition.goodToGo();
            this.mStartingIconInTransition = false;
            this.mSkipAppTransitionAnimation = false;
            this.mH.removeMessages(13);
            rebuildAppWindowListLocked();
            WindowState oldWallpaper = (this.mWallpaperTarget == null || !this.mWallpaperTarget.mWinAnimator.isAnimating() || this.mWallpaperTarget.mWinAnimator.isDummyAnimation()) ? this.mWallpaperTarget : null;
            this.mInnerFields.mWallpaperMayChange = false;
            WindowManager.LayoutParams animLp = null;
            int bestAnimLayer = -1;
            boolean fullscreenAnim = false;
            boolean openingAppHasWallpaper = false;
            boolean closingAppHasWallpaper = false;
            if (this.mLowerWallpaperTarget == null) {
                upperWallpaperAppToken = null;
                lowerWallpaperAppToken = null;
            } else {
                lowerWallpaperAppToken = this.mLowerWallpaperTarget.mAppToken;
                upperWallpaperAppToken = this.mUpperWallpaperTarget.mAppToken;
            }
            int NC = this.mClosingApps.size();
            int NN2 = NC + this.mOpeningApps.size();
            for (int i2 = 0; i2 < NN2; i2++) {
                if (i2 < NC) {
                    wtoken = this.mClosingApps.get(i2);
                    if (wtoken == lowerWallpaperAppToken || wtoken == upperWallpaperAppToken) {
                        closingAppHasWallpaper = true;
                    }
                } else {
                    wtoken = this.mOpeningApps.get(i2 - NC);
                    if (wtoken == lowerWallpaperAppToken || wtoken == upperWallpaperAppToken) {
                        openingAppHasWallpaper = true;
                    }
                }
                if (wtoken.appFullscreen) {
                    WindowState ws2 = wtoken.findMainWindow();
                    if (ws2 != null) {
                        animLp = ws2.mAttrs;
                        bestAnimLayer = ws2.mLayer;
                        fullscreenAnim = true;
                    }
                } else if (!fullscreenAnim && (ws = wtoken.findMainWindow()) != null && ws.mLayer > bestAnimLayer) {
                    animLp = ws.mAttrs;
                    bestAnimLayer = ws.mLayer;
                }
            }
            if (closingAppHasWallpaper && openingAppHasWallpaper) {
                switch (transit) {
                    case 4102:
                    case AppTransition.TRANSIT_TASK_OPEN /* 4104 */:
                    case AppTransition.TRANSIT_TASK_TO_FRONT /* 4106 */:
                        transit = 4110;
                        break;
                    case 8199:
                    case 8201:
                    case 8203:
                        transit = 8207;
                        break;
                }
            } else if (oldWallpaper != null && !this.mOpeningApps.contains(oldWallpaper.mAppToken)) {
                transit = 8204;
            } else if (this.mWallpaperTarget != null && this.mWallpaperTarget.isVisibleLw()) {
                transit = 4109;
            }
            if (!this.mPolicy.allowAppAnimationsLw()) {
                animLp = null;
            }
            AppWindowToken topOpeningApp = null;
            int topOpeningLayer = 0;
            int NN3 = this.mOpeningApps.size();
            for (int i3 = 0; i3 < NN3; i3++) {
                AppWindowToken wtoken3 = this.mOpeningApps.get(i3);
                AppWindowAnimator appAnimator = wtoken3.mAppAnimator;
                appAnimator.clearThumbnail();
                wtoken3.inPendingTransaction = false;
                appAnimator.animation = null;
                setTokenVisibilityLocked(wtoken3, animLp, true, transit, false);
                wtoken3.updateReportedVisibilityLocked();
                wtoken3.waitingToShow = false;
                appAnimator.mAllAppWinAnimators.clear();
                int N = wtoken3.allAppWindows.size();
                for (int j = 0; j < N; j++) {
                    appAnimator.mAllAppWinAnimators.add(wtoken3.allAppWindows.get(j).mWinAnimator);
                }
                this.mAnimator.mAnimating |= appAnimator.showAllWindowsLocked();
                if (animLp != null) {
                    int layer = -1;
                    for (int j2 = 0; j2 < wtoken3.windows.size(); j2++) {
                        WindowState win = wtoken3.windows.get(j2);
                        if (win.mWinAnimator.mAnimLayer > layer) {
                            layer = win.mWinAnimator.mAnimLayer;
                        }
                    }
                    if (topOpeningApp == null || layer > topOpeningLayer) {
                        topOpeningApp = wtoken3;
                        topOpeningLayer = layer;
                    }
                }
            }
            int NN4 = this.mClosingApps.size();
            for (int i4 = 0; i4 < NN4; i4++) {
                AppWindowToken wtoken4 = this.mClosingApps.get(i4);
                wtoken4.mAppAnimator.clearThumbnail();
                wtoken4.inPendingTransaction = false;
                wtoken4.mAppAnimator.animation = null;
                setTokenVisibilityLocked(wtoken4, animLp, false, transit, false);
                wtoken4.updateReportedVisibilityLocked();
                wtoken4.waitingToHide = false;
                wtoken4.allDrawn = true;
                wtoken4.deferClearAllDrawn = false;
            }
            AppWindowAnimator appAnimator2 = topOpeningApp == null ? null : topOpeningApp.mAppAnimator;
            Bitmap nextAppTransitionThumbnail = this.mAppTransition.getNextAppTransitionThumbnail();
            if (nextAppTransitionThumbnail != null && appAnimator2 != null && appAnimator2.animation != null) {
                Rect dirty = new Rect(0, 0, nextAppTransitionThumbnail.getWidth(), nextAppTransitionThumbnail.getHeight());
                try {
                    DisplayContent displayContent = getDefaultDisplayContentLocked();
                    Display display = displayContent.getDisplay();
                    SurfaceControl surfaceControl = new SurfaceControl(this.mFxSession, "thumbnail anim", dirty.width(), dirty.height(), -3, 4);
                    surfaceControl.setLayerStack(display.getLayerStack());
                    appAnimator2.thumbnail = surfaceControl;
                    Surface drawSurface = new Surface();
                    drawSurface.copyFrom(surfaceControl);
                    Canvas c = drawSurface.lockCanvas(dirty);
                    c.drawBitmap(nextAppTransitionThumbnail, 0.0f, 0.0f, (Paint) null);
                    drawSurface.unlockCanvasAndPost(c);
                    drawSurface.release();
                    appAnimator2.thumbnailLayer = topOpeningLayer;
                    DisplayInfo displayInfo = getDefaultDisplayInfoLocked();
                    Animation anim = this.mAppTransition.createThumbnailAnimationLocked(transit, true, true, displayInfo.appWidth, displayInfo.appHeight);
                    appAnimator2.thumbnailAnimation = anim;
                    anim.restrictDuration(WALLPAPER_TIMEOUT_RECOVERY);
                    anim.scaleCurrentDuration(this.mTransitionAnimationScale);
                    Point p = new Point();
                    this.mAppTransition.getStartingPoint(p);
                    appAnimator2.thumbnailX = p.x;
                    appAnimator2.thumbnailY = p.y;
                } catch (Surface.OutOfResourcesException e) {
                    Slog.e(TAG, "Can't allocate thumbnail/Canvas surface w=" + dirty.width() + " h=" + dirty.height(), e);
                    appAnimator2.clearThumbnail();
                }
            }
            this.mAppTransition.postAnimationCallback();
            this.mAppTransition.clear();
            this.mOpeningApps.clear();
            this.mClosingApps.clear();
            changes = 0 | 3;
            getDefaultDisplayContentLocked().layoutNeeded = true;
            if (windows == getDefaultWindowListLocked() && !moveInputMethodWindowsIfNeededLocked(true)) {
                assignLayersLocked(windows);
            }
            updateFocusedWindowLocked(2, false);
            this.mFocusMayChange = false;
        }
        return changes;
    }

    private int handleAnimatingStoppedAndTransitionLocked() {
        this.mAppTransition.setIdle();
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        ArrayList<Task> tasks = displayContent.getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = 0; tokenNdx < numTokens; tokenNdx++) {
                AppWindowToken wtoken = tokens.get(tokenNdx);
                wtoken.sendingToBottom = false;
            }
        }
        rebuildAppWindowListLocked();
        int changes = 0 | 1;
        moveInputMethodWindowsIfNeededLocked(true);
        this.mInnerFields.mWallpaperMayChange = true;
        this.mFocusMayChange = true;
        return changes;
    }

    private void updateResizingWindows(WindowState w) {
        WindowStateAnimator winAnimator = w.mWinAnimator;
        if (w.mHasSurface && w.mLayoutSeq == this.mLayoutSeq) {
            w.mOverscanInsetsChanged |= !w.mLastOverscanInsets.equals(w.mOverscanInsets);
            w.mContentInsetsChanged |= !w.mLastContentInsets.equals(w.mContentInsets);
            w.mVisibleInsetsChanged |= !w.mLastVisibleInsets.equals(w.mVisibleInsets);
            boolean configChanged = w.isConfigChanged();
            w.mLastFrame.set(w.mFrame);
            if (w.mContentInsetsChanged || w.mVisibleInsetsChanged || winAnimator.mSurfaceResized || configChanged) {
                w.mLastOverscanInsets.set(w.mOverscanInsets);
                w.mLastContentInsets.set(w.mContentInsets);
                w.mLastVisibleInsets.set(w.mVisibleInsets);
                makeWindowFreezingScreenIfNeededLocked(w);
                if (w.mOrientationChanging) {
                    winAnimator.mDrawState = 1;
                    if (w.mAppToken != null) {
                        w.mAppToken.allDrawn = false;
                        w.mAppToken.deferClearAllDrawn = false;
                    }
                }
                if (!this.mResizingWindows.contains(w)) {
                    this.mResizingWindows.add(w);
                }
            } else if (w.mOrientationChanging && w.isDrawnLw()) {
                w.mOrientationChanging = false;
                w.mLastFreezeDuration = (int) (SystemClock.elapsedRealtime() - this.mDisplayFreezeTime);
            }
        }
    }

    private void handleNotObscuredLocked(WindowState w, long currentTime, int innerDw, int innerDh) {
        WindowManager.LayoutParams attrs = w.mAttrs;
        int attrFlags = attrs.flags;
        boolean canBeSeen = w.isDisplayedLw();
        if (w.mHasSurface) {
            if ((attrFlags & 128) != 0) {
                this.mInnerFields.mHoldScreen = w.mSession;
            }
            if (!this.mInnerFields.mSyswin && w.mAttrs.screenBrightness >= 0.0f && this.mInnerFields.mScreenBrightness < 0.0f) {
                this.mInnerFields.mScreenBrightness = w.mAttrs.screenBrightness;
            }
            if (!this.mInnerFields.mSyswin && w.mAttrs.buttonBrightness >= 0.0f && this.mInnerFields.mButtonBrightness < 0.0f) {
                this.mInnerFields.mButtonBrightness = w.mAttrs.buttonBrightness;
            }
            if (!this.mInnerFields.mSyswin && w.mAttrs.userActivityTimeout >= 0 && this.mInnerFields.mUserActivityTimeout < 0) {
                this.mInnerFields.mUserActivityTimeout = w.mAttrs.userActivityTimeout;
            }
            int type = attrs.type;
            if (canBeSeen && (type == 2008 || type == 2028 || type == 2004 || type == 2010)) {
                this.mInnerFields.mSyswin = true;
            }
            if (canBeSeen) {
                if (type == 2023 || type == 2004) {
                    this.mInnerFields.mDisplayHasContent = 1;
                } else if (this.mInnerFields.mDisplayHasContent == 0) {
                    this.mInnerFields.mDisplayHasContent = 2;
                }
            }
        }
        boolean opaqueDrawn = canBeSeen && w.isOpaqueDrawn();
        if (opaqueDrawn && w.isFullscreen(innerDw, innerDh)) {
            this.mInnerFields.mObscured = true;
        }
    }

    private void handleFlagDimBehind(WindowState w, int innerDw, int innerDh) {
        WindowManager.LayoutParams attrs = w.mAttrs;
        if ((attrs.flags & 2) != 0 && w.isDisplayedLw() && !w.mExiting) {
            WindowStateAnimator winAnimator = w.mWinAnimator;
            TaskStack stack = w.getStack();
            stack.setDimmingTag();
            if (!stack.isDimming(winAnimator)) {
                stack.startDimmingIfNeeded(winAnimator);
            }
        }
    }

    private void updateAllDrawnLocked(DisplayContent displayContent) {
        int numInteresting;
        ArrayList<Task> tasks = displayContent.getTasks();
        int numTasks = tasks.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
            int numTokens = tokens.size();
            for (int tokenNdx = 0; tokenNdx < numTokens; tokenNdx++) {
                AppWindowToken wtoken = tokens.get(tokenNdx);
                if (!wtoken.allDrawn && (numInteresting = wtoken.numInterestingWindows) > 0 && wtoken.numDrawnWindows >= numInteresting) {
                    wtoken.allDrawn = true;
                    this.mH.obtainMessage(32, wtoken.token).sendToTarget();
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.WindowManagerService$7  reason: invalid class name */
    /* loaded from: WindowManagerService$7.class */
    class AnonymousClass7 implements Runnable {
        final /* synthetic */ IWindow val$client;
        final /* synthetic */ Rect val$frame;
        final /* synthetic */ Rect val$overscanInsets;
        final /* synthetic */ Rect val$contentInsets;
        final /* synthetic */ Rect val$visibleInsets;
        final /* synthetic */ boolean val$reportDraw;
        final /* synthetic */ Configuration val$newConfig;

        AnonymousClass7(IWindow iWindow, Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) {
            this.val$client = iWindow;
            this.val$frame = rect;
            this.val$overscanInsets = rect2;
            this.val$contentInsets = rect3;
            this.val$visibleInsets = rect4;
            this.val$reportDraw = z;
            this.val$newConfig = configuration;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.val$client.resized(this.val$frame, this.val$overscanInsets, this.val$contentInsets, this.val$visibleInsets, this.val$reportDraw, this.val$newConfig);
            } catch (RemoteException e) {
            }
        }
    }

    private int toBrightnessOverride(float value) {
        return (int) (value * 255.0f);
    }

    void checkDrawnWindowsLocked() {
        if (this.mWaitingForDrawn.size() > 0) {
            for (int j = this.mWaitingForDrawn.size() - 1; j >= 0; j--) {
                Pair<WindowState, IRemoteCallback> pair = this.mWaitingForDrawn.get(j);
                WindowState win = pair.first;
                if (win.mRemoved) {
                    Slog.w(TAG, "Aborted waiting for drawn: " + pair.first);
                    try {
                        pair.second.sendResult(null);
                    } catch (RemoteException e) {
                    }
                    this.mWaitingForDrawn.remove(pair);
                    this.mH.removeMessages(24, pair);
                } else if (win.mWinAnimator.mSurfaceShown) {
                    try {
                        pair.second.sendResult(null);
                    } catch (RemoteException e2) {
                    }
                    this.mWaitingForDrawn.remove(pair);
                    this.mH.removeMessages(24, pair);
                }
            }
        }
    }

    @Override // android.view.IWindowManager
    public boolean waitForWindowDrawn(IBinder token, IRemoteCallback callback) {
        if (token != null && callback != null) {
            synchronized (this.mWindowMap) {
                WindowState win = windowForClientLocked((Session) null, token, true);
                if (win != null) {
                    Pair<WindowState, IRemoteCallback> pair = new Pair<>(win, callback);
                    Message m = this.mH.obtainMessage(24, pair);
                    this.mH.sendMessageDelayed(m, 2000L);
                    this.mWaitingForDrawn.add(pair);
                    checkDrawnWindowsLocked();
                    return true;
                }
                Slog.i(TAG, "waitForWindowDrawn: win null");
                return false;
            }
        }
        return false;
    }

    void setHoldScreenLocked(Session newHoldScreen) {
        boolean hold = newHoldScreen != null;
        if (hold && this.mHoldingScreenOn != newHoldScreen) {
            this.mHoldingScreenWakeLock.setWorkSource(new WorkSource(newHoldScreen.mUid));
        }
        this.mHoldingScreenOn = newHoldScreen;
        boolean state = this.mHoldingScreenWakeLock.isHeld();
        if (hold != state) {
            if (hold) {
                this.mHoldingScreenWakeLock.acquire();
                this.mPolicy.keepScreenOnStartedLw();
                return;
            }
            this.mPolicy.keepScreenOnStoppedLw();
            this.mHoldingScreenWakeLock.release();
        }
    }

    @Override // com.android.server.display.DisplayManagerService.WindowManagerFuncs
    public void requestTraversal() {
        synchronized (this.mWindowMap) {
            requestTraversalLocked();
        }
    }

    void requestTraversalLocked() {
        if (!this.mTraversalScheduled) {
            this.mTraversalScheduled = true;
            this.mH.sendEmptyMessage(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleAnimationLocked() {
        if (!this.mAnimationScheduled) {
            this.mAnimationScheduled = true;
            this.mChoreographer.postCallback(1, this.mAnimator.mAnimationRunnable, null);
        }
    }

    private boolean needsLayout() {
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx);
            if (displayContent.layoutNeeded) {
                return true;
            }
        }
        return false;
    }

    boolean copyAnimToLayoutParamsLocked() {
        boolean doRequest = false;
        int bulkUpdateParams = this.mAnimator.mBulkUpdateParams;
        if ((bulkUpdateParams & 1) != 0) {
            this.mInnerFields.mUpdateRotation = true;
            doRequest = true;
        }
        if ((bulkUpdateParams & 2) != 0) {
            this.mInnerFields.mWallpaperMayChange = true;
            doRequest = true;
        }
        if ((bulkUpdateParams & 4) != 0) {
            this.mInnerFields.mWallpaperForceHidingChanged = true;
            doRequest = true;
        }
        if ((bulkUpdateParams & 8) == 0) {
            this.mInnerFields.mOrientationChangeComplete = false;
        } else {
            this.mInnerFields.mOrientationChangeComplete = true;
            this.mInnerFields.mLastWindowFreezeSource = this.mAnimator.mLastWindowFreezeSource;
            if (this.mWindowsFreezingScreen) {
                doRequest = true;
            }
        }
        if ((bulkUpdateParams & 16) != 0) {
            this.mTurnOnScreen = true;
        }
        if ((bulkUpdateParams & 32) != 0) {
            this.mInnerFields.mWallpaperActionPending = true;
        }
        return doRequest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int adjustAnimationBackground(WindowStateAnimator winAnimator) {
        WindowList windows = winAnimator.mWin.getWindowList();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState testWin = windows.get(i);
            if (testWin.mIsWallpaper && testWin.isVisibleNow()) {
                return testWin.mWinAnimator.mAnimLayer;
            }
        }
        return winAnimator.mAnimLayer;
    }

    private boolean updateFocusedWindowLocked(int mode, boolean updateInputWindows) {
        WindowState newFocus = computeFocusedWindowLocked();
        if (this.mCurrentFocus != newFocus) {
            Trace.traceBegin(32L, "wmUpdateFocus");
            this.mH.removeMessages(2);
            this.mH.sendEmptyMessage(2);
            DisplayContent displayContent = getDefaultDisplayContentLocked();
            boolean imWindowChanged = moveInputMethodWindowsIfNeededLocked((mode == 1 || mode == 3) ? false : true);
            if (imWindowChanged) {
                displayContent.layoutNeeded = true;
                newFocus = computeFocusedWindowLocked();
            }
            WindowState oldFocus = this.mCurrentFocus;
            this.mCurrentFocus = newFocus;
            this.mLosingFocus.remove(newFocus);
            int focusChanged = this.mPolicy.focusChangedLw(oldFocus, newFocus);
            if (imWindowChanged && oldFocus != this.mInputMethodWindow) {
                if (mode == 2) {
                    performLayoutLockedInner(displayContent, true, updateInputWindows);
                    focusChanged &= -2;
                } else if (mode == 3) {
                    assignLayersLocked(displayContent.getWindowList());
                }
            }
            if ((focusChanged & 1) != 0) {
                displayContent.layoutNeeded = true;
                if (mode == 2) {
                    performLayoutLockedInner(displayContent, true, updateInputWindows);
                }
            }
            if (mode != 1) {
                finishUpdateFocusedWindowAfterAssignLayersLocked(updateInputWindows);
            }
            Trace.traceEnd(32L);
            return true;
        }
        return false;
    }

    private void finishUpdateFocusedWindowAfterAssignLayersLocked(boolean updateInputWindows) {
        this.mInputMonitor.setInputFocusLw(this.mCurrentFocus, updateInputWindows);
    }

    private WindowState computeFocusedWindowLocked() {
        if (this.mAnimator.mUniverseBackground != null && this.mAnimator.mUniverseBackground.mWin.canReceiveKeys()) {
            return this.mAnimator.mUniverseBackground.mWin;
        }
        int displayCount = this.mDisplayContents.size();
        for (int i = 0; i < displayCount; i++) {
            DisplayContent displayContent = this.mDisplayContents.valueAt(i);
            WindowState win = findFocusedWindowLocked(displayContent);
            if (win != null) {
                return win;
            }
        }
        return null;
    }

    private WindowState findFocusedWindowLocked(DisplayContent displayContent) {
        AppWindowToken token;
        WindowList windows = displayContent.getWindowList();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState win = windows.get(i);
            AppWindowToken wtoken = win.mAppToken;
            if ((wtoken == null || (!wtoken.removed && !wtoken.sendingToBottom)) && win.canReceiveKeys()) {
                if (wtoken != null && win.mAttrs.type != 3 && this.mFocusedApp != null) {
                    ArrayList<Task> tasks = displayContent.getTasks();
                    for (int taskNdx = tasks.size() - 1; taskNdx >= 0; taskNdx--) {
                        AppTokenList tokens = tasks.get(taskNdx).mAppTokens;
                        int tokenNdx = tokens.size() - 1;
                        while (tokenNdx >= 0 && wtoken != (token = tokens.get(tokenNdx))) {
                            if (this.mFocusedApp == token) {
                                return null;
                            }
                            tokenNdx--;
                        }
                        if (tokenNdx >= 0) {
                            break;
                        }
                    }
                }
                return win;
            }
        }
        return null;
    }

    private void startFreezingDisplayLocked(boolean inTransaction, int exitAnim, int enterAnim) {
        if (this.mDisplayFrozen || !this.mDisplayReady || !this.mPolicy.isScreenOnFully()) {
            return;
        }
        this.mScreenFrozenLock.acquire();
        this.mDisplayFrozen = true;
        this.mDisplayFreezeTime = SystemClock.elapsedRealtime();
        this.mLastFinishedFreezeSource = null;
        this.mInputMonitor.freezeInputDispatchingLw();
        this.mPolicy.setLastInputMethodWindowLw(null, null);
        if (this.mAppTransition.isTransitionSet()) {
            this.mAppTransition.freeze();
        }
        this.mExitAnimId = exitAnim;
        this.mEnterAnimId = enterAnim;
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        int displayId = displayContent.getDisplayId();
        ScreenRotationAnimation screenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(displayId);
        if (screenRotationAnimation != null) {
            screenRotationAnimation.kill();
        }
        this.mAnimator.setScreenRotationAnimationLocked(displayId, new ScreenRotationAnimation(this.mContext, displayContent, this.mFxSession, inTransaction, this.mPolicy.isDefaultOrientationForced()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopFreezingDisplayLocked() {
        if (!this.mDisplayFrozen || this.mWaitingForConfig || this.mAppsFreezingScreen > 0 || this.mWindowsFreezingScreen || this.mClientFreezingScreen) {
            return;
        }
        this.mDisplayFrozen = false;
        this.mLastDisplayFreezeDuration = (int) (SystemClock.elapsedRealtime() - this.mDisplayFreezeTime);
        StringBuilder sb = new StringBuilder(128);
        sb.append("Screen frozen for ");
        TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, sb);
        if (this.mLastFinishedFreezeSource != null) {
            sb.append(" due to ");
            sb.append(this.mLastFinishedFreezeSource);
        }
        Slog.i(TAG, sb.toString());
        this.mH.removeMessages(17);
        this.mH.removeMessages(30);
        boolean updateRotation = false;
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        int displayId = displayContent.getDisplayId();
        ScreenRotationAnimation screenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(displayId);
        if (screenRotationAnimation != null && screenRotationAnimation.hasScreenshot()) {
            DisplayInfo displayInfo = displayContent.getDisplayInfo();
            boolean isDimming = displayContent.isDimming();
            if (!this.mPolicy.validateRotationAnimationLw(this.mExitAnimId, this.mEnterAnimId, isDimming)) {
                this.mEnterAnimId = 0;
                this.mExitAnimId = 0;
            }
            if (screenRotationAnimation.dismiss(this.mFxSession, WALLPAPER_TIMEOUT_RECOVERY, this.mTransitionAnimationScale, displayInfo.logicalWidth, displayInfo.logicalHeight, this.mExitAnimId, this.mEnterAnimId)) {
                scheduleAnimationLocked();
            } else {
                screenRotationAnimation.kill();
                this.mAnimator.setScreenRotationAnimationLocked(displayId, null);
                updateRotation = true;
            }
        } else {
            if (screenRotationAnimation != null) {
                screenRotationAnimation.kill();
                this.mAnimator.setScreenRotationAnimationLocked(displayId, null);
            }
            updateRotation = true;
        }
        this.mInputMonitor.thawInputDispatchingLw();
        boolean configChanged = updateOrientationFromAppTokensLocked(false);
        this.mH.removeMessages(15);
        this.mH.sendEmptyMessageDelayed(15, 2000L);
        this.mScreenFrozenLock.release();
        if (updateRotation) {
            configChanged |= updateRotationUncheckedLocked(false);
        }
        if (configChanged) {
            this.mH.sendEmptyMessage(18);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getPropertyInt(String[] tokens, int index, int defUnits, int defDps, DisplayMetrics dm) {
        String str;
        if (index < tokens.length && (str = tokens[index]) != null && str.length() > 0) {
            try {
                int val = Integer.parseInt(str);
                return val;
            } catch (Exception e) {
            }
        }
        if (defUnits == 0) {
            return defDps;
        }
        int val2 = (int) TypedValue.applyDimension(defUnits, defDps, dm);
        return val2;
    }

    @Override // android.view.IWindowManager
    public void statusBarVisibilityChanged(int visibility) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.STATUS_BAR) != 0) {
            throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
        }
        synchronized (this.mWindowMap) {
            this.mLastStatusBarVisibility = visibility;
            updateStatusBarVisibilityLocked(this.mPolicy.adjustSystemUiVisibilityLw(visibility));
        }
    }

    void updateStatusBarVisibilityLocked(int visibility) {
        this.mInputManager.setSystemUiVisibility(visibility);
        WindowList windows = getDefaultWindowListLocked();
        int N = windows.size();
        for (int i = 0; i < N; i++) {
            WindowState ws = windows.get(i);
            try {
                int curValue = ws.mSystemUiVisibility;
                int diff = (curValue ^ visibility) & 7 & (visibility ^ (-1));
                int newValue = (curValue & (diff ^ (-1))) | (visibility & diff);
                if (newValue != curValue) {
                    ws.mSeq++;
                    ws.mSystemUiVisibility = newValue;
                }
                if (newValue != curValue || ws.mAttrs.hasSystemUiListeners) {
                    ws.mClient.dispatchSystemUiVisibilityChanged(ws.mSeq, visibility, newValue, diff);
                }
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public void reevaluateStatusBarVisibility() {
        synchronized (this.mWindowMap) {
            int visibility = this.mPolicy.adjustSystemUiVisibilityLw(this.mLastStatusBarVisibility);
            updateStatusBarVisibilityLocked(visibility);
            performLayoutAndPlaceSurfacesLocked();
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public WindowManagerPolicy.FakeWindow addFakeWindow(Looper looper, InputEventReceiver.Factory inputEventReceiverFactory, String name, int windowType, int layoutParamsFlags, int layoutParamsPrivateFlags, boolean canReceiveKeys, boolean hasFocus, boolean touchFullscreen) {
        FakeWindowImpl fw;
        synchronized (this.mWindowMap) {
            fw = new FakeWindowImpl(this, looper, inputEventReceiverFactory, name, windowType, layoutParamsFlags, layoutParamsPrivateFlags, canReceiveKeys, hasFocus, touchFullscreen);
            while (0 < this.mFakeWindows.size() && this.mFakeWindows.get(0).mWindowLayer > fw.mWindowLayer) {
            }
            this.mFakeWindows.add(0, fw);
            this.mInputMonitor.updateInputWindowsLw(true);
        }
        return fw;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeFakeWindowLocked(WindowManagerPolicy.FakeWindow window) {
        synchronized (this.mWindowMap) {
            if (this.mFakeWindows.remove(window)) {
                this.mInputMonitor.updateInputWindowsLw(true);
                return true;
            }
            return false;
        }
    }

    public void saveLastInputMethodWindowForTransition() {
        synchronized (this.mWindowMap) {
            getDefaultDisplayContentLocked();
            if (this.mInputMethodWindow != null) {
                this.mPolicy.setLastInputMethodWindowLw(this.mInputMethodWindow, this.mInputMethodTarget);
            }
        }
    }

    @Override // android.view.IWindowManager
    public boolean hasNavigationBar() {
        return this.mPolicy.hasNavigationBar();
    }

    @Override // android.view.IWindowManager
    public void lockNow(Bundle options) {
        this.mPolicy.lockNow(options);
    }

    @Override // android.view.IWindowManager
    public boolean isSafeModeEnabled() {
        return this.mSafeMode;
    }

    void dumpPolicyLocked(PrintWriter pw, String[] args, boolean dumpAll) {
        pw.println("WINDOW MANAGER POLICY STATE (dumpsys window policy)");
        this.mPolicy.dump("    ", pw, args);
    }

    void dumpAnimatorLocked(PrintWriter pw, String[] args, boolean dumpAll) {
        pw.println("WINDOW MANAGER ANIMATOR STATE (dumpsys window animator)");
        this.mAnimator.dumpLocked(pw, "    ", dumpAll);
    }

    void dumpTokensLocked(PrintWriter pw, boolean dumpAll) {
        pw.println("WINDOW MANAGER TOKENS (dumpsys window tokens)");
        if (this.mTokenMap.size() > 0) {
            pw.println("  All tokens:");
            for (WindowToken token : this.mTokenMap.values()) {
                pw.print("  ");
                pw.print(token);
                if (dumpAll) {
                    pw.println(':');
                    token.dump(pw, "    ");
                } else {
                    pw.println();
                }
            }
        }
        if (this.mWallpaperTokens.size() > 0) {
            pw.println();
            pw.println("  Wallpaper tokens:");
            for (int i = this.mWallpaperTokens.size() - 1; i >= 0; i--) {
                WindowToken token2 = this.mWallpaperTokens.get(i);
                pw.print("  Wallpaper #");
                pw.print(i);
                pw.print(' ');
                pw.print(token2);
                if (dumpAll) {
                    pw.println(':');
                    token2.dump(pw, "    ");
                } else {
                    pw.println();
                }
            }
        }
        if (this.mFinishedStarting.size() > 0) {
            pw.println();
            pw.println("  Finishing start of application tokens:");
            for (int i2 = this.mFinishedStarting.size() - 1; i2 >= 0; i2--) {
                WindowToken token3 = this.mFinishedStarting.get(i2);
                pw.print("  Finished Starting #");
                pw.print(i2);
                pw.print(' ');
                pw.print(token3);
                if (dumpAll) {
                    pw.println(':');
                    token3.dump(pw, "    ");
                } else {
                    pw.println();
                }
            }
        }
        if (this.mOpeningApps.size() > 0 || this.mClosingApps.size() > 0) {
            pw.println();
            if (this.mOpeningApps.size() > 0) {
                pw.print("  mOpeningApps=");
                pw.println(this.mOpeningApps);
            }
            if (this.mClosingApps.size() > 0) {
                pw.print("  mClosingApps=");
                pw.println(this.mClosingApps);
            }
        }
    }

    void dumpSessionsLocked(PrintWriter pw, boolean dumpAll) {
        pw.println("WINDOW MANAGER SESSIONS (dumpsys window sessions)");
        if (this.mSessions.size() > 0) {
            Iterator<Session> it = this.mSessions.iterator();
            while (it.hasNext()) {
                Session s = it.next();
                pw.print("  Session ");
                pw.print(s);
                pw.println(':');
                s.dump(pw, "    ");
            }
        }
    }

    void dumpDisplayContentsLocked(PrintWriter pw, boolean dumpAll) {
        pw.println("WINDOW MANAGER DISPLAY CONTENTS (dumpsys window displays)");
        if (this.mDisplayReady) {
            int numDisplays = this.mDisplayContents.size();
            for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx);
                displayContent.dump("  ", pw);
            }
            return;
        }
        pw.println("  NO DISPLAY");
    }

    void dumpWindowsLocked(PrintWriter pw, boolean dumpAll, ArrayList<WindowState> windows) {
        pw.println("WINDOW MANAGER WINDOWS (dumpsys window windows)");
        dumpWindowsNoHeaderLocked(pw, dumpAll, windows);
    }

    void dumpWindowsNoHeaderLocked(PrintWriter pw, boolean dumpAll, ArrayList<WindowState> windows) {
        int numDisplays = this.mDisplayContents.size();
        for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
            WindowList windowList = this.mDisplayContents.valueAt(displayNdx).getWindowList();
            for (int winNdx = windowList.size() - 1; winNdx >= 0; winNdx--) {
                WindowState w = windowList.get(winNdx);
                if (windows == null || windows.contains(w)) {
                    pw.print("  Window #");
                    pw.print(winNdx);
                    pw.print(' ');
                    pw.print(w);
                    pw.println(Separators.COLON);
                    w.dump(pw, "    ", dumpAll || windows != null);
                }
            }
        }
        if (this.mInputMethodDialogs.size() > 0) {
            pw.println();
            pw.println("  Input method dialogs:");
            for (int i = this.mInputMethodDialogs.size() - 1; i >= 0; i--) {
                WindowState w2 = this.mInputMethodDialogs.get(i);
                if (windows == null || windows.contains(w2)) {
                    pw.print("  IM Dialog #");
                    pw.print(i);
                    pw.print(": ");
                    pw.println(w2);
                }
            }
        }
        if (this.mPendingRemove.size() > 0) {
            pw.println();
            pw.println("  Remove pending for:");
            for (int i2 = this.mPendingRemove.size() - 1; i2 >= 0; i2--) {
                WindowState w3 = this.mPendingRemove.get(i2);
                if (windows == null || windows.contains(w3)) {
                    pw.print("  Remove #");
                    pw.print(i2);
                    pw.print(' ');
                    pw.print(w3);
                    if (dumpAll) {
                        pw.println(Separators.COLON);
                        w3.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mForceRemoves != null && this.mForceRemoves.size() > 0) {
            pw.println();
            pw.println("  Windows force removing:");
            for (int i3 = this.mForceRemoves.size() - 1; i3 >= 0; i3--) {
                WindowState w4 = this.mForceRemoves.get(i3);
                pw.print("  Removing #");
                pw.print(i3);
                pw.print(' ');
                pw.print(w4);
                if (dumpAll) {
                    pw.println(Separators.COLON);
                    w4.dump(pw, "    ", true);
                } else {
                    pw.println();
                }
            }
        }
        if (this.mDestroySurface.size() > 0) {
            pw.println();
            pw.println("  Windows waiting to destroy their surface:");
            for (int i4 = this.mDestroySurface.size() - 1; i4 >= 0; i4--) {
                WindowState w5 = this.mDestroySurface.get(i4);
                if (windows == null || windows.contains(w5)) {
                    pw.print("  Destroy #");
                    pw.print(i4);
                    pw.print(' ');
                    pw.print(w5);
                    if (dumpAll) {
                        pw.println(Separators.COLON);
                        w5.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mLosingFocus.size() > 0) {
            pw.println();
            pw.println("  Windows losing focus:");
            for (int i5 = this.mLosingFocus.size() - 1; i5 >= 0; i5--) {
                WindowState w6 = this.mLosingFocus.get(i5);
                if (windows == null || windows.contains(w6)) {
                    pw.print("  Losing #");
                    pw.print(i5);
                    pw.print(' ');
                    pw.print(w6);
                    if (dumpAll) {
                        pw.println(Separators.COLON);
                        w6.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mResizingWindows.size() > 0) {
            pw.println();
            pw.println("  Windows waiting to resize:");
            for (int i6 = this.mResizingWindows.size() - 1; i6 >= 0; i6--) {
                WindowState w7 = this.mResizingWindows.get(i6);
                if (windows == null || windows.contains(w7)) {
                    pw.print("  Resizing #");
                    pw.print(i6);
                    pw.print(' ');
                    pw.print(w7);
                    if (dumpAll) {
                        pw.println(Separators.COLON);
                        w7.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mWaitingForDrawn.size() > 0) {
            pw.println();
            pw.println("  Clients waiting for these windows to be drawn:");
            for (int i7 = this.mWaitingForDrawn.size() - 1; i7 >= 0; i7--) {
                Pair<WindowState, IRemoteCallback> pair = this.mWaitingForDrawn.get(i7);
                pw.print("  Waiting #");
                pw.print(i7);
                pw.print(' ');
                pw.print(pair.first);
                pw.print(": ");
                pw.println(pair.second);
            }
        }
        pw.println();
        pw.print("  mCurConfiguration=");
        pw.println(this.mCurConfiguration);
        pw.print("  mCurrentFocus=");
        pw.println(this.mCurrentFocus);
        if (this.mLastFocus != this.mCurrentFocus) {
            pw.print("  mLastFocus=");
            pw.println(this.mLastFocus);
        }
        pw.print("  mFocusedApp=");
        pw.println(this.mFocusedApp);
        if (this.mInputMethodTarget != null) {
            pw.print("  mInputMethodTarget=");
            pw.println(this.mInputMethodTarget);
        }
        pw.print("  mInTouchMode=");
        pw.print(this.mInTouchMode);
        pw.print(" mLayoutSeq=");
        pw.println(this.mLayoutSeq);
        pw.print("  mLastDisplayFreezeDuration=");
        TimeUtils.formatDuration(this.mLastDisplayFreezeDuration, pw);
        if (this.mLastFinishedFreezeSource != null) {
            pw.print(" due to ");
            pw.print(this.mLastFinishedFreezeSource);
        }
        pw.println();
        if (dumpAll) {
            pw.print("  mSystemDecorLayer=");
            pw.print(this.mSystemDecorLayer);
            pw.print(" mScreenRect=");
            pw.println(this.mScreenRect.toShortString());
            if (this.mLastStatusBarVisibility != 0) {
                pw.print("  mLastStatusBarVisibility=0x");
                pw.println(Integer.toHexString(this.mLastStatusBarVisibility));
            }
            if (this.mInputMethodWindow != null) {
                pw.print("  mInputMethodWindow=");
                pw.println(this.mInputMethodWindow);
            }
            pw.print("  mWallpaperTarget=");
            pw.println(this.mWallpaperTarget);
            if (this.mLowerWallpaperTarget != null || this.mUpperWallpaperTarget != null) {
                pw.print("  mLowerWallpaperTarget=");
                pw.println(this.mLowerWallpaperTarget);
                pw.print("  mUpperWallpaperTarget=");
                pw.println(this.mUpperWallpaperTarget);
            }
            pw.print("  mLastWallpaperX=");
            pw.print(this.mLastWallpaperX);
            pw.print(" mLastWallpaperY=");
            pw.println(this.mLastWallpaperY);
            if (this.mInputMethodAnimLayerAdjustment != 0 || this.mWallpaperAnimLayerAdjustment != 0) {
                pw.print("  mInputMethodAnimLayerAdjustment=");
                pw.print(this.mInputMethodAnimLayerAdjustment);
                pw.print("  mWallpaperAnimLayerAdjustment=");
                pw.println(this.mWallpaperAnimLayerAdjustment);
            }
            pw.print("  mSystemBooted=");
            pw.print(this.mSystemBooted);
            pw.print(" mDisplayEnabled=");
            pw.println(this.mDisplayEnabled);
            if (needsLayout()) {
                pw.print("  layoutNeeded on displays=");
                for (int displayNdx2 = 0; displayNdx2 < numDisplays; displayNdx2++) {
                    DisplayContent displayContent = this.mDisplayContents.valueAt(displayNdx2);
                    if (displayContent.layoutNeeded) {
                        pw.print(displayContent.getDisplayId());
                    }
                }
                pw.println();
            }
            pw.print("  mTransactionSequence=");
            pw.println(this.mTransactionSequence);
            pw.print("  mDisplayFrozen=");
            pw.print(this.mDisplayFrozen);
            pw.print(" windows=");
            pw.print(this.mWindowsFreezingScreen);
            pw.print(" client=");
            pw.print(this.mClientFreezingScreen);
            pw.print(" apps=");
            pw.print(this.mAppsFreezingScreen);
            pw.print(" waitingForConfig=");
            pw.println(this.mWaitingForConfig);
            pw.print("  mRotation=");
            pw.print(this.mRotation);
            pw.print(" mAltOrientation=");
            pw.println(this.mAltOrientation);
            pw.print("  mLastWindowForcedOrientation=");
            pw.print(this.mLastWindowForcedOrientation);
            pw.print(" mForcedAppOrientation=");
            pw.println(this.mForcedAppOrientation);
            pw.print("  mDeferredRotationPauseCount=");
            pw.println(this.mDeferredRotationPauseCount);
            pw.print("  mWindowAnimationScale=");
            pw.print(this.mWindowAnimationScale);
            pw.print(" mTransitionWindowAnimationScale=");
            pw.print(this.mTransitionAnimationScale);
            pw.print(" mAnimatorDurationScale=");
            pw.println(this.mAnimatorDurationScale);
            pw.print("  mTraversalScheduled=");
            pw.println(this.mTraversalScheduled);
            pw.print("  mStartingIconInTransition=");
            pw.print(this.mStartingIconInTransition);
            pw.print(" mSkipAppTransitionAnimation=");
            pw.println(this.mSkipAppTransitionAnimation);
            pw.println("  mLayoutToAnim:");
            this.mAppTransition.dump(pw);
        }
    }

    boolean dumpWindows(PrintWriter pw, String name, String[] args, int opti, boolean dumpAll) {
        WindowList windows = new WindowList();
        if (CalendarContract.CalendarColumns.VISIBLE.equals(name)) {
            synchronized (this.mWindowMap) {
                int numDisplays = this.mDisplayContents.size();
                for (int displayNdx = 0; displayNdx < numDisplays; displayNdx++) {
                    WindowList windowList = this.mDisplayContents.valueAt(displayNdx).getWindowList();
                    for (int winNdx = windowList.size() - 1; winNdx >= 0; winNdx--) {
                        WindowState w = windowList.get(winNdx);
                        if (w.mWinAnimator.mSurfaceShown) {
                            windows.add(w);
                        }
                    }
                }
            }
        } else {
            int objectId = 0;
            try {
                objectId = Integer.parseInt(name, 16);
                name = null;
            } catch (RuntimeException e) {
            }
            synchronized (this.mWindowMap) {
                int numDisplays2 = this.mDisplayContents.size();
                for (int displayNdx2 = 0; displayNdx2 < numDisplays2; displayNdx2++) {
                    WindowList windowList2 = this.mDisplayContents.valueAt(displayNdx2).getWindowList();
                    for (int winNdx2 = windowList2.size() - 1; winNdx2 >= 0; winNdx2--) {
                        WindowState w2 = windowList2.get(winNdx2);
                        if (name != null) {
                            if (w2.mAttrs.getTitle().toString().contains(name)) {
                                windows.add(w2);
                            }
                        } else if (System.identityHashCode(w2) == objectId) {
                            windows.add(w2);
                        }
                    }
                }
            }
        }
        if (windows.size() <= 0) {
            return false;
        }
        synchronized (this.mWindowMap) {
            dumpWindowsLocked(pw, dumpAll, windows);
        }
        return true;
    }

    void dumpLastANRLocked(PrintWriter pw) {
        pw.println("WINDOW MANAGER LAST ANR (dumpsys window lastanr)");
        if (this.mLastANRState == null) {
            pw.println("  <no ANR has occurred since boot>");
        } else {
            pw.println(this.mLastANRState);
        }
    }

    public void saveANRStateLocked(AppWindowToken appWindowToken, WindowState windowState, String reason) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter((Writer) sw, false, 1024);
        pw.println("  ANR time: " + DateFormat.getInstance().format(new Date()));
        if (appWindowToken != null) {
            pw.println("  Application at fault: " + appWindowToken.stringName);
        }
        if (windowState != null) {
            pw.println("  Window at fault: " + ((Object) windowState.mAttrs.getTitle()));
        }
        if (reason != null) {
            pw.println("  Reason: " + reason);
        }
        pw.println();
        dumpWindowsNoHeaderLocked(pw, true, null);
        pw.close();
        this.mLastANRState = sw.toString();
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String opt;
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump WindowManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        boolean dumpAll = false;
        int opti = 0;
        while (opti < args.length && (opt = args[opti]) != null && opt.length() > 0 && opt.charAt(0) == '-') {
            opti++;
            if ("-a".equals(opt)) {
                dumpAll = true;
            } else if ("-h".equals(opt)) {
                pw.println("Window manager dump options:");
                pw.println("  [-a] [-h] [cmd] ...");
                pw.println("  cmd may be one of:");
                pw.println("    l[astanr]: last ANR information");
                pw.println("    p[policy]: policy state");
                pw.println("    a[animator]: animator state");
                pw.println("    s[essions]: active sessions");
                pw.println("    d[isplays]: active display contents");
                pw.println("    t[okens]: token list");
                pw.println("    w[indows]: window list");
                pw.println("  cmd may also be a NAME to dump windows.  NAME may");
                pw.println("    be a partial substring in a window name, a");
                pw.println("    Window hex object identifier, or");
                pw.println("    \"all\" for all windows, or");
                pw.println("    \"visible\" for the visible windows.");
                pw.println("  -a: include all available server state.");
                return;
            } else {
                pw.println("Unknown argument: " + opt + "; use -h for help");
            }
        }
        if (opti < args.length) {
            String cmd = args[opti];
            int opti2 = opti + 1;
            if ("lastanr".equals(cmd) || "l".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpLastANRLocked(pw);
                }
                return;
            } else if ("policy".equals(cmd) || "p".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpPolicyLocked(pw, args, true);
                }
                return;
            } else if ("animator".equals(cmd) || FullBackup.APK_TREE_TOKEN.equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpAnimatorLocked(pw, args, true);
                }
                return;
            } else if ("sessions".equals(cmd) || "s".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpSessionsLocked(pw, true);
                }
                return;
            } else if ("displays".equals(cmd) || "d".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpDisplayContentsLocked(pw, true);
                }
                return;
            } else if ("tokens".equals(cmd) || "t".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpTokensLocked(pw, true);
                }
                return;
            } else if ("windows".equals(cmd) || "w".equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpWindowsLocked(pw, true, null);
                }
                return;
            } else if ("all".equals(cmd) || FullBackup.APK_TREE_TOKEN.equals(cmd)) {
                synchronized (this.mWindowMap) {
                    dumpWindowsLocked(pw, true, null);
                }
                return;
            } else if (!dumpWindows(pw, cmd, args, opti2, dumpAll)) {
                pw.println("Bad window command, or no windows match: " + cmd);
                pw.println("Use -h for help.");
                return;
            } else {
                return;
            }
        }
        synchronized (this.mWindowMap) {
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpLastANRLocked(pw);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpPolicyLocked(pw, args, dumpAll);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpAnimatorLocked(pw, args, dumpAll);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpSessionsLocked(pw, dumpAll);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpDisplayContentsLocked(pw, dumpAll);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpTokensLocked(pw, dumpAll);
            pw.println();
            if (dumpAll) {
                pw.println("-------------------------------------------------------------------------------");
            }
            dumpWindowsLocked(pw, dumpAll, null);
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mWindowMap) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void debugLayoutRepeats(String msg, int pendingLayoutChanges) {
        if (this.mLayoutRepeatCount >= 4) {
            Slog.v(TAG, "Layouts looping: " + msg + ", mPendingLayoutChanges = 0x" + Integer.toHexString(pendingLayoutChanges));
        }
    }

    private DisplayContent newDisplayContentLocked(Display display) {
        DisplayContent displayContent = new DisplayContent(display, this);
        int displayId = display.getDisplayId();
        this.mDisplayContents.put(displayId, displayContent);
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        Rect rect = new Rect();
        this.mDisplaySettings.getOverscanLocked(displayInfo.name, rect);
        synchronized (displayContent.mDisplaySizeLock) {
            displayInfo.overscanLeft = rect.left;
            displayInfo.overscanTop = rect.top;
            displayInfo.overscanRight = rect.right;
            displayInfo.overscanBottom = rect.bottom;
            this.mDisplayManagerService.setDisplayInfoOverrideFromWindowManager(displayId, displayInfo);
        }
        configureDisplayPolicyLocked(displayContent);
        if (displayId == 0) {
            displayContent.mTapDetector = new StackTapPointerEventListener(this, displayContent);
            registerPointerEventListener(displayContent.mTapDetector);
        }
        return displayContent;
    }

    public void createDisplayContentLocked(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("getDisplayContent: display must not be null");
        }
        getDisplayContentLocked(display.getDisplayId());
    }

    public DisplayContent getDisplayContentLocked(int displayId) {
        Display display;
        DisplayContent displayContent = this.mDisplayContents.get(displayId);
        if (displayContent == null && (display = this.mDisplayManager.getDisplay(displayId)) != null) {
            displayContent = newDisplayContentLocked(display);
        }
        return displayContent;
    }

    public DisplayContent getDefaultDisplayContentLocked() {
        return getDisplayContentLocked(0);
    }

    public WindowList getDefaultWindowListLocked() {
        return getDefaultDisplayContentLocked().getWindowList();
    }

    public DisplayInfo getDefaultDisplayInfoLocked() {
        return getDefaultDisplayContentLocked().getDisplayInfo();
    }

    public WindowList getWindowListLocked(Display display) {
        return getWindowListLocked(display.getDisplayId());
    }

    public WindowList getWindowListLocked(int displayId) {
        DisplayContent displayContent = getDisplayContentLocked(displayId);
        if (displayContent != null) {
            return displayContent.getWindowList();
        }
        return null;
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int displayId) {
        this.mH.sendMessage(this.mH.obtainMessage(27, displayId, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayAddedLocked(int displayId) {
        Display display = this.mDisplayManager.getDisplay(displayId);
        if (display != null) {
            createDisplayContentLocked(display);
            displayReady(displayId);
        }
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int displayId) {
        this.mH.sendMessage(this.mH.obtainMessage(28, displayId, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayRemovedLocked(int displayId) {
        DisplayContent displayContent = getDisplayContentLocked(displayId);
        if (displayContent != null) {
            this.mDisplayContents.delete(displayId);
            displayContent.close();
            if (displayId == 0) {
                unregisterPointerEventListener(displayContent.mTapDetector);
            }
            WindowList windows = displayContent.getWindowList();
            while (!windows.isEmpty()) {
                WindowState win = windows.get(windows.size() - 1);
                removeWindowLocked(win.mSession, win);
            }
        }
        this.mAnimator.removeDisplayLocked(displayId);
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int displayId) {
        this.mH.sendMessage(this.mH.obtainMessage(29, displayId, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayChangedLocked(int displayId) {
        DisplayContent displayContent = getDisplayContentLocked(displayId);
        if (displayContent != null) {
            displayContent.updateDisplayInfo();
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowManagerFuncs
    public Object getWindowManagerLock() {
        return this.mWindowMap;
    }
}