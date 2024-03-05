package com.android.server.wm;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.IApplicationToken;
import android.view.IWindow;
import android.view.IWindowFocusObserver;
import android.view.IWindowId;
import android.view.InputChannel;
import android.view.WindowManager;
import android.view.WindowManagerPolicy;
import com.android.server.input.InputWindowHandle;
import gov.nist.core.Separators;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WindowState.class */
public final class WindowState implements WindowManagerPolicy.WindowState {
    static final String TAG = "WindowState";
    final WindowManagerService mService;
    final WindowManagerPolicy mPolicy;
    final Context mContext;
    final Session mSession;
    final IWindow mClient;
    final int mAppOp;
    final int mOwnerUid;
    WindowToken mToken;
    WindowToken mRootToken;
    AppWindowToken mAppToken;
    AppWindowToken mTargetAppToken;
    final DeathRecipient mDeathRecipient;
    final WindowState mAttachedWindow;
    final int mBaseLayer;
    final int mSubLayer;
    final boolean mLayoutAttached;
    final boolean mIsImWindow;
    final boolean mIsWallpaper;
    final boolean mIsFloatingLayer;
    int mSeq;
    boolean mEnforceSizeCompat;
    int mViewVisibility;
    int mSystemUiVisibility;
    boolean mAppFreezing;
    boolean mAttachedHidden;
    boolean mWallpaperVisible;
    RemoteCallbackList<IWindowFocusObserver> mFocusCallbacks;
    int mRequestedWidth;
    int mRequestedHeight;
    int mLastRequestedWidth;
    int mLastRequestedHeight;
    int mLayer;
    boolean mHaveFrame;
    boolean mObscured;
    boolean mTurnOnScreen;
    private boolean mConfigHasChanged;
    boolean mVisibleInsetsChanged;
    boolean mContentInsetsChanged;
    boolean mOverscanInsetsChanged;
    boolean mGivenInsetsPending;
    boolean mContentChanged;
    int mXOffset;
    int mYOffset;
    boolean mRelayoutCalled;
    boolean mLayoutNeeded;
    boolean mExiting;
    boolean mDestroying;
    boolean mRemoveOnExit;
    boolean mOrientationChanging;
    int mLastFreezeDuration;
    boolean mRemoved;
    boolean mRebuilding;
    final InputWindowHandle mInputWindowHandle;
    InputChannel mInputChannel;
    String mStringNameCache;
    CharSequence mLastTitle;
    boolean mWasExiting;
    final WindowStateAnimator mWinAnimator;
    DisplayContent mDisplayContent;
    private boolean mShowToOwnerOnly;
    final WindowManager.LayoutParams mAttrs = new WindowManager.LayoutParams();
    final WindowList mChildWindows = new WindowList();
    boolean mPolicyVisibility = true;
    boolean mPolicyVisibilityAfterAnim = true;
    boolean mAppOpVisibility = true;
    int mLayoutSeq = -1;
    Configuration mConfiguration = null;
    final RectF mShownFrame = new RectF();
    final Rect mVisibleInsets = new Rect();
    final Rect mLastVisibleInsets = new Rect();
    final Rect mContentInsets = new Rect();
    final Rect mLastContentInsets = new Rect();
    final Rect mOverscanInsets = new Rect();
    final Rect mLastOverscanInsets = new Rect();
    final Rect mGivenContentInsets = new Rect();
    final Rect mGivenVisibleInsets = new Rect();
    final Region mGivenTouchableRegion = new Region();
    int mTouchableInsets = 0;
    final Rect mSystemDecorRect = new Rect();
    final Rect mLastSystemDecorRect = new Rect();
    float mGlobalScale = 1.0f;
    float mInvGlobalScale = 1.0f;
    float mHScale = 1.0f;
    float mVScale = 1.0f;
    float mLastHScale = 1.0f;
    float mLastVScale = 1.0f;
    final Matrix mTmpMatrix = new Matrix();
    final Rect mFrame = new Rect();
    final Rect mLastFrame = new Rect();
    final Rect mCompatFrame = new Rect();
    final Rect mContainingFrame = new Rect();
    final Rect mDisplayFrame = new Rect();
    final Rect mOverscanFrame = new Rect();
    final Rect mContentFrame = new Rect();
    final Rect mParentFrame = new Rect();
    final Rect mVisibleFrame = new Rect();
    final Rect mDecorFrame = new Rect();
    float mWallpaperX = -1.0f;
    float mWallpaperY = -1.0f;
    float mWallpaperXStep = -1.0f;
    float mWallpaperYStep = -1.0f;
    boolean mHasSurface = false;
    boolean mUnderStatusBar = true;
    final IWindowId mWindowId = new IWindowId.Stub() { // from class: com.android.server.wm.WindowState.1
        @Override // android.view.IWindowId
        public void registerFocusObserver(IWindowFocusObserver observer) {
            WindowState.this.registerFocusObserver(observer);
        }

        @Override // android.view.IWindowId
        public void unregisterFocusObserver(IWindowFocusObserver observer) {
            WindowState.this.unregisterFocusObserver(observer);
        }

        @Override // android.view.IWindowId
        public boolean isFocused() {
            return WindowState.this.isFocused();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowState(WindowManagerService service, Session s, IWindow c, WindowToken token, WindowState attachedWindow, int appOp, int seq, WindowManager.LayoutParams a, int viewVisibility, DisplayContent displayContent) {
        WindowState appWin;
        WindowToken appToken;
        this.mService = service;
        this.mSession = s;
        this.mClient = c;
        this.mAppOp = appOp;
        this.mToken = token;
        this.mOwnerUid = s.mUid;
        this.mAttrs.copyFrom(a);
        this.mViewVisibility = viewVisibility;
        this.mDisplayContent = displayContent;
        this.mPolicy = this.mService.mPolicy;
        this.mContext = this.mService.mContext;
        DeathRecipient deathRecipient = new DeathRecipient();
        this.mSeq = seq;
        this.mEnforceSizeCompat = (this.mAttrs.privateFlags & 128) != 0;
        try {
            c.asBinder().linkToDeath(deathRecipient, 0);
            this.mDeathRecipient = deathRecipient;
            if (this.mAttrs.type >= 1000 && this.mAttrs.type <= 1999) {
                this.mBaseLayer = (this.mPolicy.windowTypeToLayerLw(attachedWindow.mAttrs.type) * 10000) + 1000;
                this.mSubLayer = this.mPolicy.subWindowTypeToLayerLw(a.type);
                this.mAttachedWindow = attachedWindow;
                int children_size = this.mAttachedWindow.mChildWindows.size();
                if (children_size == 0) {
                    this.mAttachedWindow.mChildWindows.add(this);
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= children_size) {
                            break;
                        }
                        WindowState child = this.mAttachedWindow.mChildWindows.get(i);
                        if (this.mSubLayer < child.mSubLayer) {
                            this.mAttachedWindow.mChildWindows.add(i, this);
                            break;
                        } else if (this.mSubLayer > child.mSubLayer || this.mBaseLayer > child.mBaseLayer) {
                            i++;
                        } else {
                            this.mAttachedWindow.mChildWindows.add(i, this);
                            break;
                        }
                    }
                    if (children_size == this.mAttachedWindow.mChildWindows.size()) {
                        this.mAttachedWindow.mChildWindows.add(this);
                    }
                }
                this.mLayoutAttached = this.mAttrs.type != 1003;
                this.mIsImWindow = attachedWindow.mAttrs.type == 2011 || attachedWindow.mAttrs.type == 2012;
                this.mIsWallpaper = attachedWindow.mAttrs.type == 2013;
                this.mIsFloatingLayer = this.mIsImWindow || this.mIsWallpaper;
            } else {
                this.mBaseLayer = (this.mPolicy.windowTypeToLayerLw(a.type) * 10000) + 1000;
                this.mSubLayer = 0;
                this.mAttachedWindow = null;
                this.mLayoutAttached = false;
                this.mIsImWindow = this.mAttrs.type == 2011 || this.mAttrs.type == 2012;
                this.mIsWallpaper = this.mAttrs.type == 2013;
                this.mIsFloatingLayer = this.mIsImWindow || this.mIsWallpaper;
            }
            WindowState windowState = this;
            while (true) {
                appWin = windowState;
                if (appWin.mAttachedWindow == null) {
                    break;
                }
                windowState = appWin.mAttachedWindow;
            }
            WindowToken windowToken = appWin.mToken;
            while (true) {
                appToken = windowToken;
                if (appToken.appWindowToken != null) {
                    break;
                }
                WindowToken parent = this.mService.mTokenMap.get(appToken.token);
                if (parent == null || appToken == parent) {
                    break;
                }
                windowToken = parent;
            }
            this.mRootToken = appToken;
            this.mAppToken = appToken.appWindowToken;
            this.mWinAnimator = new WindowStateAnimator(this);
            this.mWinAnimator.mAlpha = a.alpha;
            this.mRequestedWidth = 0;
            this.mRequestedHeight = 0;
            this.mLastRequestedWidth = 0;
            this.mLastRequestedHeight = 0;
            this.mXOffset = 0;
            this.mYOffset = 0;
            this.mLayer = 0;
            this.mInputWindowHandle = new InputWindowHandle(this.mAppToken != null ? this.mAppToken.mInputApplicationHandle : null, this, displayContent.getDisplayId());
        } catch (RemoteException e) {
            this.mDeathRecipient = null;
            this.mAttachedWindow = null;
            this.mLayoutAttached = false;
            this.mIsImWindow = false;
            this.mIsWallpaper = false;
            this.mIsFloatingLayer = false;
            this.mBaseLayer = 0;
            this.mSubLayer = 0;
            this.mInputWindowHandle = null;
            this.mWinAnimator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attach() {
        this.mSession.windowAddedLocked();
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public int getOwningUid() {
        return this.mOwnerUid;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public String getOwningPackage() {
        return this.mAttrs.packageName;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public void computeFrameLw(Rect pf, Rect df, Rect of, Rect cf, Rect vf, Rect dcf) {
        int w;
        int h;
        float x;
        float y;
        this.mHaveFrame = true;
        TaskStack stack = this.mAppToken != null ? getStack() : null;
        if (stack != null && stack.hasSibling()) {
            this.mContainingFrame.set(getStackBounds(stack));
            if (this.mUnderStatusBar) {
                this.mContainingFrame.top = pf.top;
            }
        } else {
            this.mContainingFrame.set(pf);
        }
        this.mDisplayFrame.set(df);
        int pw = this.mContainingFrame.width();
        int ph = this.mContainingFrame.height();
        if ((this.mAttrs.flags & 16384) != 0) {
            if (this.mAttrs.width < 0) {
                w = pw;
            } else if (this.mEnforceSizeCompat) {
                w = (int) ((this.mAttrs.width * this.mGlobalScale) + 0.5f);
            } else {
                w = this.mAttrs.width;
            }
            if (this.mAttrs.height < 0) {
                h = ph;
            } else if (this.mEnforceSizeCompat) {
                h = (int) ((this.mAttrs.height * this.mGlobalScale) + 0.5f);
            } else {
                h = this.mAttrs.height;
            }
        } else {
            if (this.mAttrs.width == -1) {
                w = pw;
            } else if (this.mEnforceSizeCompat) {
                w = (int) ((this.mRequestedWidth * this.mGlobalScale) + 0.5f);
            } else {
                w = this.mRequestedWidth;
            }
            if (this.mAttrs.height == -1) {
                h = ph;
            } else if (this.mEnforceSizeCompat) {
                h = (int) ((this.mRequestedHeight * this.mGlobalScale) + 0.5f);
            } else {
                h = this.mRequestedHeight;
            }
        }
        if (!this.mParentFrame.equals(pf)) {
            this.mParentFrame.set(pf);
            this.mContentChanged = true;
        }
        if (this.mRequestedWidth != this.mLastRequestedWidth || this.mRequestedHeight != this.mLastRequestedHeight) {
            this.mLastRequestedWidth = this.mRequestedWidth;
            this.mLastRequestedHeight = this.mRequestedHeight;
            this.mContentChanged = true;
        }
        this.mOverscanFrame.set(of);
        this.mContentFrame.set(cf);
        this.mVisibleFrame.set(vf);
        this.mDecorFrame.set(dcf);
        int fw = this.mFrame.width();
        int fh = this.mFrame.height();
        if (this.mEnforceSizeCompat) {
            x = this.mAttrs.x * this.mGlobalScale;
            y = this.mAttrs.y * this.mGlobalScale;
        } else {
            x = this.mAttrs.x;
            y = this.mAttrs.y;
        }
        Gravity.apply(this.mAttrs.gravity, w, h, this.mContainingFrame, (int) (x + (this.mAttrs.horizontalMargin * pw)), (int) (y + (this.mAttrs.verticalMargin * ph)), this.mFrame);
        Gravity.applyDisplay(this.mAttrs.gravity, df, this.mFrame);
        this.mContentFrame.set(Math.max(this.mContentFrame.left, this.mFrame.left), Math.max(this.mContentFrame.top, this.mFrame.top), Math.min(this.mContentFrame.right, this.mFrame.right), Math.min(this.mContentFrame.bottom, this.mFrame.bottom));
        this.mVisibleFrame.set(Math.max(this.mVisibleFrame.left, this.mFrame.left), Math.max(this.mVisibleFrame.top, this.mFrame.top), Math.min(this.mVisibleFrame.right, this.mFrame.right), Math.min(this.mVisibleFrame.bottom, this.mFrame.bottom));
        this.mOverscanInsets.set(Math.max(this.mOverscanFrame.left - this.mFrame.left, 0), Math.max(this.mOverscanFrame.top - this.mFrame.top, 0), Math.max(this.mFrame.right - this.mOverscanFrame.right, 0), Math.max(this.mFrame.bottom - this.mOverscanFrame.bottom, 0));
        this.mContentInsets.set(this.mContentFrame.left - this.mFrame.left, this.mContentFrame.top - this.mFrame.top, this.mFrame.right - this.mContentFrame.right, this.mFrame.bottom - this.mContentFrame.bottom);
        this.mVisibleInsets.set(this.mVisibleFrame.left - this.mFrame.left, this.mVisibleFrame.top - this.mFrame.top, this.mFrame.right - this.mVisibleFrame.right, this.mFrame.bottom - this.mVisibleFrame.bottom);
        this.mCompatFrame.set(this.mFrame);
        if (this.mEnforceSizeCompat) {
            this.mOverscanInsets.scale(this.mInvGlobalScale);
            this.mContentInsets.scale(this.mInvGlobalScale);
            this.mVisibleInsets.scale(this.mInvGlobalScale);
            this.mCompatFrame.scale(this.mInvGlobalScale);
        }
        if (this.mIsWallpaper) {
            if (fw != this.mFrame.width() || fh != this.mFrame.height()) {
                DisplayInfo displayInfo = this.mDisplayContent.getDisplayInfo();
                this.mService.updateWallpaperOffsetLocked(this, displayInfo.logicalWidth, displayInfo.logicalHeight, false);
            }
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getFrameLw() {
        return this.mFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public RectF getShownFrameLw() {
        return this.mShownFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getDisplayFrameLw() {
        return this.mDisplayFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getOverscanFrameLw() {
        return this.mOverscanFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getContentFrameLw() {
        return this.mContentFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getVisibleFrameLw() {
        return this.mVisibleFrame;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean getGivenInsetsPendingLw() {
        return this.mGivenInsetsPending;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getGivenContentInsetsLw() {
        return this.mGivenContentInsets;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public Rect getGivenVisibleInsetsLw() {
        return this.mGivenVisibleInsets;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public WindowManager.LayoutParams getAttrs() {
        return this.mAttrs;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean getNeedsMenuLw(WindowManagerPolicy.WindowState bottom) {
        int index = -1;
        WindowState ws = this;
        WindowList windows = getWindowList();
        while ((ws.mAttrs.privateFlags & 8) == 0) {
            if (ws == bottom) {
                return false;
            }
            if (index < 0) {
                index = windows.indexOf(ws);
            }
            index--;
            if (index < 0) {
                return false;
            }
            ws = windows.get(index);
        }
        return (ws.mAttrs.flags & 1073741824) != 0;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public int getSurfaceLayer() {
        return this.mLayer;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public IApplicationToken getAppToken() {
        if (this.mAppToken != null) {
            return this.mAppToken.appToken;
        }
        return null;
    }

    public int getDisplayId() {
        return this.mDisplayContent.getDisplayId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskStack getStack() {
        Task task;
        AppWindowToken wtoken = this.mAppToken == null ? this.mService.mFocusedApp : this.mAppToken;
        if (wtoken != null && (task = this.mService.mTaskIdToTask.get(wtoken.groupId)) != null) {
            return task.mStack;
        }
        return this.mDisplayContent.getHomeStack();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect getStackBounds() {
        return getStackBounds(getStack());
    }

    private Rect getStackBounds(TaskStack stack) {
        if (stack != null) {
            return stack.mStackBox.mBounds;
        }
        return this.mFrame;
    }

    public long getInputDispatchingTimeoutNanos() {
        if (this.mAppToken != null) {
            return this.mAppToken.inputDispatchingTimeoutNanos;
        }
        return 5000000000L;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean hasAppShownWindows() {
        return this.mAppToken != null && (this.mAppToken.firstWindowDrawn || this.mAppToken.startingDisplayed);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isIdentityMatrix(float dsdx, float dtdx, float dsdy, float dtdy) {
        return dsdx >= 0.99999f && dsdx <= 1.00001f && dtdy >= 0.99999f && dtdy <= 1.00001f && dtdx >= -1.0E-6f && dtdx <= 1.0E-6f && dsdy >= -1.0E-6f && dsdy <= 1.0E-6f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prelayout() {
        if (this.mEnforceSizeCompat) {
            this.mGlobalScale = this.mService.mCompatibleScreenScale;
            this.mInvGlobalScale = 1.0f / this.mGlobalScale;
            return;
        }
        this.mInvGlobalScale = 1.0f;
        this.mGlobalScale = 1.0f;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isVisibleLw() {
        AppWindowToken atoken = this.mAppToken;
        return this.mHasSurface && this.mPolicyVisibility && !this.mAttachedHidden && !((atoken != null && atoken.hiddenRequested) || this.mExiting || this.mDestroying);
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isVisibleOrBehindKeyguardLw() {
        if (this.mRootToken.waitingToShow && this.mService.mAppTransition.isTransitionSet()) {
            return false;
        }
        AppWindowToken atoken = this.mAppToken;
        boolean animating = atoken != null ? atoken.mAppAnimator.animation != null : false;
        return (!this.mHasSurface || this.mDestroying || this.mExiting || (atoken != null ? atoken.hiddenRequested : !this.mPolicyVisibility) || ((this.mAttachedHidden || this.mViewVisibility != 0 || this.mRootToken.hidden) && this.mWinAnimator.mAnimation == null && !animating)) ? false : true;
    }

    public boolean isWinVisibleLw() {
        AppWindowToken atoken = this.mAppToken;
        return this.mHasSurface && this.mPolicyVisibility && !this.mAttachedHidden && !((atoken != null && atoken.hiddenRequested && !atoken.mAppAnimator.animating) || this.mExiting || this.mDestroying);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVisibleNow() {
        return (!this.mHasSurface || !this.mPolicyVisibility || this.mAttachedHidden || this.mRootToken.hidden || this.mExiting || this.mDestroying) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPotentialDragTarget() {
        return (!isVisibleNow() || this.mRemoved || this.mInputChannel == null || this.mInputWindowHandle == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVisibleOrAdding() {
        AppWindowToken atoken = this.mAppToken;
        return (this.mHasSurface || (!this.mRelayoutCalled && this.mViewVisibility == 0)) && this.mPolicyVisibility && !this.mAttachedHidden && !((atoken != null && atoken.hiddenRequested) || this.mExiting || this.mDestroying);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOnScreen() {
        if (!this.mHasSurface || !this.mPolicyVisibility || this.mDestroying) {
            return false;
        }
        AppWindowToken atoken = this.mAppToken;
        return atoken != null ? ((this.mAttachedHidden || atoken.hiddenRequested) && this.mWinAnimator.mAnimation == null && atoken.mAppAnimator.animation == null) ? false : true : (this.mAttachedHidden && this.mWinAnimator.mAnimation == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isReadyForDisplay() {
        return ((this.mRootToken.waitingToShow && this.mService.mAppTransition.isTransitionSet()) || !this.mHasSurface || !this.mPolicyVisibility || this.mDestroying || ((this.mAttachedHidden || this.mViewVisibility != 0 || this.mRootToken.hidden) && this.mWinAnimator.mAnimation == null && (this.mAppToken == null || this.mAppToken.mAppAnimator.animation == null))) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isReadyForDisplayIgnoringKeyguard() {
        if (this.mRootToken.waitingToShow && this.mService.mAppTransition.isTransitionSet()) {
            return false;
        }
        AppWindowToken atoken = this.mAppToken;
        return (atoken != null || this.mPolicyVisibility) && this.mHasSurface && !this.mDestroying && !((this.mAttachedHidden || this.mViewVisibility != 0 || this.mRootToken.hidden) && this.mWinAnimator.mAnimation == null && (atoken == null || atoken.mAppAnimator.animation == null || this.mWinAnimator.isDummyAnimation()));
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isDisplayedLw() {
        AppWindowToken atoken = this.mAppToken;
        return isDrawnLw() && this.mPolicyVisibility && ((!this.mAttachedHidden && (atoken == null || !atoken.hiddenRequested)) || this.mWinAnimator.mAnimating || !(atoken == null || atoken.mAppAnimator.animation == null));
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isAnimatingLw() {
        return (this.mWinAnimator.mAnimation == null && (this.mAppToken == null || this.mAppToken.mAppAnimator.animation == null)) ? false : true;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isGoneForLayoutLw() {
        AppWindowToken atoken = this.mAppToken;
        return this.mViewVisibility == 8 || !this.mRelayoutCalled || (atoken == null && this.mRootToken.hidden) || ((atoken != null && (atoken.hiddenRequested || atoken.hidden)) || this.mAttachedHidden || ((this.mExiting && !isAnimatingLw()) || this.mDestroying));
    }

    public boolean isDrawFinishedLw() {
        return this.mHasSurface && !this.mDestroying && (this.mWinAnimator.mDrawState == 2 || this.mWinAnimator.mDrawState == 3 || this.mWinAnimator.mDrawState == 4);
    }

    public boolean isDrawnLw() {
        return this.mHasSurface && !this.mDestroying && (this.mWinAnimator.mDrawState == 3 || this.mWinAnimator.mDrawState == 4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOpaqueDrawn() {
        return (this.mAttrs.format == -1 || this.mAttrs.type == 2013) && isDrawnLw() && this.mWinAnimator.mAnimation == null && (this.mAppToken == null || this.mAppToken.mAppAnimator.animation == null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldAnimateMove() {
        return this.mContentChanged && !this.mExiting && !this.mWinAnimator.mLastHidden && this.mService.okToDisplay() && !(this.mFrame.top == this.mLastFrame.top && this.mFrame.left == this.mLastFrame.left) && (this.mAttrs.privateFlags & 64) == 0 && (this.mAttachedWindow == null || !this.mAttachedWindow.shouldAnimateMove());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFullscreen(int screenWidth, int screenHeight) {
        return this.mFrame.left <= 0 && this.mFrame.top <= 0 && this.mFrame.right >= screenWidth && this.mFrame.bottom >= screenHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isConfigChanged() {
        boolean configChanged = this.mConfiguration != this.mService.mCurConfiguration && (this.mConfiguration == null || this.mConfiguration.diff(this.mService.mCurConfiguration) != 0);
        if (this.mAttrs.type == 2004) {
            this.mConfigHasChanged |= configChanged;
            configChanged = this.mConfigHasChanged;
        }
        return configChanged;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeLocked() {
        disposeInputChannel();
        if (this.mAttachedWindow != null) {
            this.mAttachedWindow.mChildWindows.remove(this);
        }
        this.mWinAnimator.destroyDeferredSurfaceLocked();
        this.mWinAnimator.destroySurfaceLocked();
        this.mSession.windowRemovedLocked();
        try {
            this.mClient.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
        } catch (RuntimeException e) {
        }
    }

    void setConfiguration(Configuration newConfig) {
        this.mConfiguration = newConfig;
        this.mConfigHasChanged = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInputChannel(InputChannel inputChannel) {
        if (this.mInputChannel != null) {
            throw new IllegalStateException("Window already has an input channel.");
        }
        this.mInputChannel = inputChannel;
        this.mInputWindowHandle.inputChannel = inputChannel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disposeInputChannel() {
        if (this.mInputChannel != null) {
            this.mService.mInputManager.unregisterInputChannel(this.mInputChannel);
            this.mInputChannel.dispose();
            this.mInputChannel = null;
        }
        this.mInputWindowHandle.inputChannel = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WindowState$DeathRecipient.class */
    public class DeathRecipient implements IBinder.DeathRecipient {
        private DeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            try {
                synchronized (WindowState.this.mService.mWindowMap) {
                    WindowState win = WindowState.this.mService.windowForClientLocked(WindowState.this.mSession, WindowState.this.mClient, false);
                    Slog.i(WindowState.TAG, "WIN DEATH: " + win);
                    if (win != null) {
                        WindowState.this.mService.removeWindowLocked(WindowState.this.mSession, win);
                    } else if (WindowState.this.mHasSurface) {
                        Slog.e(WindowState.TAG, "!!! LEAK !!! Window removed but surface still valid.");
                        WindowState.this.mService.removeWindowLocked(WindowState.this.mSession, WindowState.this);
                    }
                }
            } catch (IllegalArgumentException e) {
            }
        }
    }

    public final boolean canReceiveKeys() {
        return isVisibleOrAdding() && this.mViewVisibility == 0 && (this.mAttrs.flags & 8) == 0;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean hasDrawnLw() {
        return this.mWinAnimator.mDrawState == 4;
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean showLw(boolean doAnimation) {
        return showLw(doAnimation, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showLw(boolean doAnimation, boolean requestAnim) {
        if (isHiddenFromUserLocked()) {
            Slog.w(TAG, "current user violation " + this.mService.mCurrentUserId + " trying to display " + this + ", type " + this.mAttrs.type + ", belonging to " + this.mOwnerUid);
            return false;
        } else if (!this.mAppOpVisibility) {
            return false;
        } else {
            if (this.mPolicyVisibility && this.mPolicyVisibilityAfterAnim) {
                return false;
            }
            if (doAnimation) {
                if (!this.mService.okToDisplay()) {
                    doAnimation = false;
                } else if (this.mPolicyVisibility && this.mWinAnimator.mAnimation == null) {
                    doAnimation = false;
                }
            }
            this.mPolicyVisibility = true;
            this.mPolicyVisibilityAfterAnim = true;
            if (doAnimation) {
                this.mWinAnimator.applyAnimationLocked(1, true);
            }
            if (requestAnim) {
                this.mService.scheduleAnimationLocked();
                return true;
            }
            return true;
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean hideLw(boolean doAnimation) {
        return hideLw(doAnimation, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hideLw(boolean doAnimation, boolean requestAnim) {
        if (doAnimation && !this.mService.okToDisplay()) {
            doAnimation = false;
        }
        boolean current = doAnimation ? this.mPolicyVisibilityAfterAnim : this.mPolicyVisibility;
        if (!current) {
            return false;
        }
        if (doAnimation) {
            this.mWinAnimator.applyAnimationLocked(2, false);
            if (this.mWinAnimator.mAnimation == null) {
                doAnimation = false;
            }
        }
        if (doAnimation) {
            this.mPolicyVisibilityAfterAnim = false;
        } else {
            this.mPolicyVisibilityAfterAnim = false;
            this.mPolicyVisibility = false;
            this.mService.enableScreenIfNeededLocked();
            if (this.mService.mCurrentFocus == this) {
                this.mService.mFocusMayChange = true;
            }
        }
        if (requestAnim) {
            this.mService.scheduleAnimationLocked();
            return true;
        }
        return true;
    }

    public void setAppOpVisibilityLw(boolean state) {
        if (this.mAppOpVisibility != state) {
            this.mAppOpVisibility = state;
            if (state) {
                showLw(true, true);
            } else {
                hideLw(true, true);
            }
        }
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isAlive() {
        return this.mClient.asBinder().isBinderAlive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isClosing() {
        return this.mExiting || this.mService.mClosingApps.contains(this.mAppToken);
    }

    @Override // android.view.WindowManagerPolicy.WindowState
    public boolean isDefaultDisplay() {
        return this.mDisplayContent.isDefaultDisplay;
    }

    public void setShowToOwnerOnlyLocked(boolean showToOwnerOnly) {
        this.mShowToOwnerOnly = showToOwnerOnly;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHiddenFromUserLocked() {
        WindowState win;
        WindowState windowState = this;
        while (true) {
            win = windowState;
            if (win.mAttachedWindow == null) {
                break;
            }
            windowState = win.mAttachedWindow;
        }
        if (win.mAttrs.type < 2000 && win.mAppToken != null && win.mAppToken.showWhenLocked) {
            DisplayInfo displayInfo = win.mDisplayContent.getDisplayInfo();
            if (win.mFrame.left <= 0 && win.mFrame.top <= 0 && win.mFrame.right >= displayInfo.appWidth && win.mFrame.bottom >= displayInfo.appHeight) {
                return false;
            }
        }
        return win.mShowToOwnerOnly && UserHandle.getUserId(win.mOwnerUid) != this.mService.mCurrentUserId;
    }

    private static void applyInsets(Region outRegion, Rect frame, Rect inset) {
        outRegion.set(frame.left + inset.left, frame.top + inset.top, frame.right - inset.right, frame.bottom - inset.bottom);
    }

    public void getTouchableRegion(Region outRegion) {
        Rect frame = this.mFrame;
        switch (this.mTouchableInsets) {
            case 0:
            default:
                outRegion.set(frame);
                return;
            case 1:
                applyInsets(outRegion, frame, this.mGivenContentInsets);
                return;
            case 2:
                applyInsets(outRegion, frame, this.mGivenVisibleInsets);
                return;
            case 3:
                Region givenTouchableRegion = this.mGivenTouchableRegion;
                outRegion.set(givenTouchableRegion);
                outRegion.translate(frame.left, frame.top);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowList getWindowList() {
        return this.mDisplayContent.getWindowList();
    }

    public void reportFocusChangedSerialized(boolean focused, boolean inTouchMode) {
        try {
            this.mClient.windowFocusChanged(focused, inTouchMode);
        } catch (RemoteException e) {
        }
        if (this.mFocusCallbacks != null) {
            int N = this.mFocusCallbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IWindowFocusObserver obs = this.mFocusCallbacks.getBroadcastItem(i);
                if (focused) {
                    try {
                        obs.focusGained(this.mWindowId.asBinder());
                    } catch (RemoteException e2) {
                    }
                } else {
                    obs.focusLost(this.mWindowId.asBinder());
                }
            }
            this.mFocusCallbacks.finishBroadcast();
        }
    }

    public void registerFocusObserver(IWindowFocusObserver observer) {
        synchronized (this.mService.mWindowMap) {
            if (this.mFocusCallbacks == null) {
                this.mFocusCallbacks = new RemoteCallbackList<>();
            }
            this.mFocusCallbacks.register(observer);
        }
    }

    public void unregisterFocusObserver(IWindowFocusObserver observer) {
        synchronized (this.mService.mWindowMap) {
            if (this.mFocusCallbacks != null) {
                this.mFocusCallbacks.unregister(observer);
            }
        }
    }

    public boolean isFocused() {
        boolean z;
        synchronized (this.mService.mWindowMap) {
            z = this.mService.mCurrentFocus == this;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        pw.print(prefix);
        pw.print("mDisplayId=");
        pw.print(this.mDisplayContent.getDisplayId());
        pw.print(" mSession=");
        pw.print(this.mSession);
        pw.print(" mClient=");
        pw.println(this.mClient.asBinder());
        pw.print(prefix);
        pw.print("mOwnerUid=");
        pw.print(this.mOwnerUid);
        pw.print(" mShowToOwnerOnly=");
        pw.print(this.mShowToOwnerOnly);
        pw.print(" package=");
        pw.print(this.mAttrs.packageName);
        pw.print(" appop=");
        pw.println(AppOpsManager.opToName(this.mAppOp));
        pw.print(prefix);
        pw.print("mAttrs=");
        pw.println(this.mAttrs);
        pw.print(prefix);
        pw.print("Requested w=");
        pw.print(this.mRequestedWidth);
        pw.print(" h=");
        pw.print(this.mRequestedHeight);
        pw.print(" mLayoutSeq=");
        pw.println(this.mLayoutSeq);
        if (this.mRequestedWidth != this.mLastRequestedWidth || this.mRequestedHeight != this.mLastRequestedHeight) {
            pw.print(prefix);
            pw.print("LastRequested w=");
            pw.print(this.mLastRequestedWidth);
            pw.print(" h=");
            pw.println(this.mLastRequestedHeight);
        }
        if (this.mAttachedWindow != null || this.mLayoutAttached) {
            pw.print(prefix);
            pw.print("mAttachedWindow=");
            pw.print(this.mAttachedWindow);
            pw.print(" mLayoutAttached=");
            pw.println(this.mLayoutAttached);
        }
        if (this.mIsImWindow || this.mIsWallpaper || this.mIsFloatingLayer) {
            pw.print(prefix);
            pw.print("mIsImWindow=");
            pw.print(this.mIsImWindow);
            pw.print(" mIsWallpaper=");
            pw.print(this.mIsWallpaper);
            pw.print(" mIsFloatingLayer=");
            pw.print(this.mIsFloatingLayer);
            pw.print(" mWallpaperVisible=");
            pw.println(this.mWallpaperVisible);
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mBaseLayer=");
            pw.print(this.mBaseLayer);
            pw.print(" mSubLayer=");
            pw.print(this.mSubLayer);
            pw.print(" mAnimLayer=");
            pw.print(this.mLayer);
            pw.print("+");
            pw.print(this.mTargetAppToken != null ? this.mTargetAppToken.mAppAnimator.animLayerAdjustment : this.mAppToken != null ? this.mAppToken.mAppAnimator.animLayerAdjustment : 0);
            pw.print(Separators.EQUALS);
            pw.print(this.mWinAnimator.mAnimLayer);
            pw.print(" mLastLayer=");
            pw.println(this.mWinAnimator.mLastLayer);
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mToken=");
            pw.println(this.mToken);
            pw.print(prefix);
            pw.print("mRootToken=");
            pw.println(this.mRootToken);
            if (this.mAppToken != null) {
                pw.print(prefix);
                pw.print("mAppToken=");
                pw.println(this.mAppToken);
            }
            if (this.mTargetAppToken != null) {
                pw.print(prefix);
                pw.print("mTargetAppToken=");
                pw.println(this.mTargetAppToken);
            }
            pw.print(prefix);
            pw.print("mViewVisibility=0x");
            pw.print(Integer.toHexString(this.mViewVisibility));
            pw.print(" mHaveFrame=");
            pw.print(this.mHaveFrame);
            pw.print(" mObscured=");
            pw.println(this.mObscured);
            pw.print(prefix);
            pw.print("mSeq=");
            pw.print(this.mSeq);
            pw.print(" mSystemUiVisibility=0x");
            pw.println(Integer.toHexString(this.mSystemUiVisibility));
        }
        if (!this.mPolicyVisibility || !this.mPolicyVisibilityAfterAnim || !this.mAppOpVisibility || this.mAttachedHidden) {
            pw.print(prefix);
            pw.print("mPolicyVisibility=");
            pw.print(this.mPolicyVisibility);
            pw.print(" mPolicyVisibilityAfterAnim=");
            pw.print(this.mPolicyVisibilityAfterAnim);
            pw.print(" mAppOpVisibility=");
            pw.print(this.mAppOpVisibility);
            pw.print(" mAttachedHidden=");
            pw.println(this.mAttachedHidden);
        }
        if (!this.mRelayoutCalled || this.mLayoutNeeded) {
            pw.print(prefix);
            pw.print("mRelayoutCalled=");
            pw.print(this.mRelayoutCalled);
            pw.print(" mLayoutNeeded=");
            pw.println(this.mLayoutNeeded);
        }
        if (this.mXOffset != 0 || this.mYOffset != 0) {
            pw.print(prefix);
            pw.print("Offsets x=");
            pw.print(this.mXOffset);
            pw.print(" y=");
            pw.println(this.mYOffset);
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mGivenContentInsets=");
            this.mGivenContentInsets.printShortString(pw);
            pw.print(" mGivenVisibleInsets=");
            this.mGivenVisibleInsets.printShortString(pw);
            pw.println();
            if (this.mTouchableInsets != 0 || this.mGivenInsetsPending) {
                pw.print(prefix);
                pw.print("mTouchableInsets=");
                pw.print(this.mTouchableInsets);
                pw.print(" mGivenInsetsPending=");
                pw.println(this.mGivenInsetsPending);
                Region region = new Region();
                getTouchableRegion(region);
                pw.print(prefix);
                pw.print("touchable region=");
                pw.println(region);
            }
            pw.print(prefix);
            pw.print("mConfiguration=");
            pw.println(this.mConfiguration);
        }
        pw.print(prefix);
        pw.print("mHasSurface=");
        pw.print(this.mHasSurface);
        pw.print(" mShownFrame=");
        this.mShownFrame.printShortString(pw);
        pw.print(" isReadyForDisplay()=");
        pw.println(isReadyForDisplay());
        if (dumpAll) {
            pw.print(prefix);
            pw.print("mFrame=");
            this.mFrame.printShortString(pw);
            pw.print(" last=");
            this.mLastFrame.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("mSystemDecorRect=");
            this.mSystemDecorRect.printShortString(pw);
            pw.print(" last=");
            this.mLastSystemDecorRect.printShortString(pw);
            pw.println();
        }
        if (this.mEnforceSizeCompat) {
            pw.print(prefix);
            pw.print("mCompatFrame=");
            this.mCompatFrame.printShortString(pw);
            pw.println();
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print("Frames: containing=");
            this.mContainingFrame.printShortString(pw);
            pw.print(" parent=");
            this.mParentFrame.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("    display=");
            this.mDisplayFrame.printShortString(pw);
            pw.print(" overscan=");
            this.mOverscanFrame.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("    content=");
            this.mContentFrame.printShortString(pw);
            pw.print(" visible=");
            this.mVisibleFrame.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("    decor=");
            this.mDecorFrame.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("Cur insets: overscan=");
            this.mOverscanInsets.printShortString(pw);
            pw.print(" content=");
            this.mContentInsets.printShortString(pw);
            pw.print(" visible=");
            this.mVisibleInsets.printShortString(pw);
            pw.println();
            pw.print(prefix);
            pw.print("Lst insets: overscan=");
            this.mLastOverscanInsets.printShortString(pw);
            pw.print(" content=");
            this.mLastContentInsets.printShortString(pw);
            pw.print(" visible=");
            this.mLastVisibleInsets.printShortString(pw);
            pw.println();
        }
        pw.print(prefix);
        pw.print(this.mWinAnimator);
        pw.println(Separators.COLON);
        this.mWinAnimator.dump(pw, prefix + "  ", dumpAll);
        if (this.mExiting || this.mRemoveOnExit || this.mDestroying || this.mRemoved) {
            pw.print(prefix);
            pw.print("mExiting=");
            pw.print(this.mExiting);
            pw.print(" mRemoveOnExit=");
            pw.print(this.mRemoveOnExit);
            pw.print(" mDestroying=");
            pw.print(this.mDestroying);
            pw.print(" mRemoved=");
            pw.println(this.mRemoved);
        }
        if (this.mOrientationChanging || this.mAppFreezing || this.mTurnOnScreen) {
            pw.print(prefix);
            pw.print("mOrientationChanging=");
            pw.print(this.mOrientationChanging);
            pw.print(" mAppFreezing=");
            pw.print(this.mAppFreezing);
            pw.print(" mTurnOnScreen=");
            pw.println(this.mTurnOnScreen);
        }
        if (this.mLastFreezeDuration != 0) {
            pw.print(prefix);
            pw.print("mLastFreezeDuration=");
            TimeUtils.formatDuration(this.mLastFreezeDuration, pw);
            pw.println();
        }
        if (this.mHScale != 1.0f || this.mVScale != 1.0f) {
            pw.print(prefix);
            pw.print("mHScale=");
            pw.print(this.mHScale);
            pw.print(" mVScale=");
            pw.println(this.mVScale);
        }
        if (this.mWallpaperX != -1.0f || this.mWallpaperY != -1.0f) {
            pw.print(prefix);
            pw.print("mWallpaperX=");
            pw.print(this.mWallpaperX);
            pw.print(" mWallpaperY=");
            pw.println(this.mWallpaperY);
        }
        if (this.mWallpaperXStep != -1.0f || this.mWallpaperYStep != -1.0f) {
            pw.print(prefix);
            pw.print("mWallpaperXStep=");
            pw.print(this.mWallpaperXStep);
            pw.print(" mWallpaperYStep=");
            pw.println(this.mWallpaperYStep);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String makeInputChannelName() {
        return Integer.toHexString(System.identityHashCode(this)) + Separators.SP + ((Object) this.mAttrs.getTitle());
    }

    public String toString() {
        CharSequence title = this.mAttrs.getTitle();
        if (title == null || title.length() <= 0) {
            title = this.mAttrs.packageName;
        }
        if (this.mStringNameCache == null || this.mLastTitle != title || this.mWasExiting != this.mExiting) {
            this.mLastTitle = title;
            this.mWasExiting = this.mExiting;
            this.mStringNameCache = "Window{" + Integer.toHexString(System.identityHashCode(this)) + " u" + UserHandle.getUserId(this.mSession.mUid) + Separators.SP + ((Object) this.mLastTitle) + (this.mExiting ? " EXITING}" : "}");
        }
        return this.mStringNameCache;
    }
}