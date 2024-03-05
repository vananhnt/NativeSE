package com.android.server.wm;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Debug;
import android.provider.BrowserContract;
import android.util.Slog;
import android.view.DisplayInfo;
import android.view.HardwareRenderer;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowManagerPolicy;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WindowStateAnimator.class */
public class WindowStateAnimator {
    static final String TAG = "WindowStateAnimator";
    final WindowManagerService mService;
    final WindowState mWin;
    final WindowStateAnimator mAttachedWinAnimator;
    final WindowAnimator mAnimator;
    AppWindowAnimator mAppAnimator;
    final Session mSession;
    final WindowManagerPolicy mPolicy;
    final Context mContext;
    final boolean mIsWallpaper;
    boolean mAnimating;
    boolean mLocalAnimating;
    Animation mAnimation;
    boolean mAnimationIsEntrance;
    boolean mHasTransformation;
    boolean mHasLocalTransformation;
    boolean mWasAnimating;
    int mAnimLayer;
    int mLastLayer;
    SurfaceControl mSurfaceControl;
    SurfaceControl mPendingDestroySurface;
    boolean mSurfaceResized;
    boolean mSurfaceDestroyDeferred;
    int mAnimDw;
    int mAnimDh;
    boolean mHaveMatrix;
    boolean mSurfaceShown;
    float mSurfaceX;
    float mSurfaceY;
    float mSurfaceW;
    float mSurfaceH;
    int mSurfaceLayer;
    float mSurfaceAlpha;
    boolean mEnterAnimationPending;
    static final int NO_SURFACE = 0;
    static final int DRAW_PENDING = 1;
    static final int COMMIT_DRAW_PENDING = 2;
    static final int READY_TO_SHOW = 3;
    static final int HAS_DRAWN = 4;
    int mDrawState;
    boolean mLastHidden;
    int mAttrFlags;
    int mAttrType;
    final int mLayerStack;
    final Transformation mUniverseTransform = new Transformation();
    final Transformation mTransformation = new Transformation();
    float mShownAlpha = 0.0f;
    float mAlpha = 0.0f;
    float mLastAlpha = 0.0f;
    float mDsDx = 1.0f;
    float mDtDx = 0.0f;
    float mDsDy = 0.0f;
    float mDtDy = 1.0f;
    float mLastDsDx = 1.0f;
    float mLastDtDx = 0.0f;
    float mLastDsDy = 0.0f;
    float mLastDtDy = 1.0f;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.createSurfaceLocked():android.view.SurfaceControl, file: WindowStateAnimator.class
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
    android.view.SurfaceControl createSurfaceLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.createSurfaceLocked():android.view.SurfaceControl, file: WindowStateAnimator.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowStateAnimator.createSurfaceLocked():android.view.SurfaceControl");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.setTransparentRegionHintLocked(android.graphics.Region):void, file: WindowStateAnimator.class
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
    void setTransparentRegionHintLocked(android.graphics.Region r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.setTransparentRegionHintLocked(android.graphics.Region):void, file: WindowStateAnimator.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowStateAnimator.setTransparentRegionHintLocked(android.graphics.Region):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.setWallpaperOffset(android.graphics.RectF):void, file: WindowStateAnimator.class
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
    void setWallpaperOffset(android.graphics.RectF r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.WindowStateAnimator.setWallpaperOffset(android.graphics.RectF):void, file: WindowStateAnimator.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowStateAnimator.setWallpaperOffset(android.graphics.RectF):void");
    }

    static String drawStateToString(int state) {
        switch (state) {
            case 0:
                return "NO_SURFACE";
            case 1:
                return "DRAW_PENDING";
            case 2:
                return "COMMIT_DRAW_PENDING";
            case 3:
                return "READY_TO_SHOW";
            case 4:
                return "HAS_DRAWN";
            default:
                return Integer.toString(state);
        }
    }

    public WindowStateAnimator(WindowState win) {
        WindowManagerService service = win.mService;
        this.mService = service;
        this.mAnimator = service.mAnimator;
        this.mPolicy = service.mPolicy;
        this.mContext = service.mContext;
        DisplayInfo displayInfo = win.mDisplayContent.getDisplayInfo();
        this.mAnimDw = displayInfo.appWidth;
        this.mAnimDh = displayInfo.appHeight;
        this.mWin = win;
        this.mAttachedWinAnimator = win.mAttachedWindow == null ? null : win.mAttachedWindow.mWinAnimator;
        this.mAppAnimator = win.mAppToken == null ? null : win.mAppToken.mAppAnimator;
        this.mSession = win.mSession;
        this.mAttrFlags = win.mAttrs.flags;
        this.mAttrType = win.mAttrs.type;
        this.mIsWallpaper = win.mIsWallpaper;
        this.mLayerStack = win.mDisplayContent.getDisplay().getLayerStack();
    }

    public void setAnimation(Animation anim) {
        this.mAnimating = false;
        this.mLocalAnimating = false;
        this.mAnimation = anim;
        this.mAnimation.restrictDuration(10000L);
        this.mAnimation.scaleCurrentDuration(this.mService.mWindowAnimationScale);
        this.mTransformation.clear();
        this.mTransformation.setAlpha(this.mLastHidden ? 0.0f : 1.0f);
        this.mHasLocalTransformation = true;
    }

    public void clearAnimation() {
        if (this.mAnimation != null) {
            this.mAnimating = true;
            this.mLocalAnimating = false;
            this.mAnimation.cancel();
            this.mAnimation = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAnimating() {
        return this.mAnimation != null || !(this.mAttachedWinAnimator == null || this.mAttachedWinAnimator.mAnimation == null) || (this.mAppAnimator != null && (this.mAppAnimator.animation != null || this.mAppAnimator.mAppToken.inPendingTransaction));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDummyAnimation() {
        return this.mAppAnimator != null && this.mAppAnimator.animation == AppWindowAnimator.sDummyAnimation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isWindowAnimating() {
        return this.mAnimation != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelExitAnimationForNextAnimationLocked() {
        if (this.mAnimation != null) {
            this.mAnimation.cancel();
            this.mAnimation = null;
            this.mLocalAnimating = false;
            destroySurfaceLocked();
        }
    }

    private boolean stepAnimation(long currentTime) {
        if (this.mAnimation == null || !this.mLocalAnimating) {
            return false;
        }
        this.mTransformation.clear();
        boolean more = this.mAnimation.getTransformation(currentTime, this.mTransformation);
        return more;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean stepAnimationLocked(long currentTime) {
        this.mWasAnimating = this.mAnimating;
        if (this.mService.okToDisplay()) {
            if (this.mWin.isDrawnLw() && this.mAnimation != null) {
                this.mHasTransformation = true;
                this.mHasLocalTransformation = true;
                if (!this.mLocalAnimating) {
                    this.mAnimation.initialize(this.mWin.mFrame.width(), this.mWin.mFrame.height(), this.mAnimDw, this.mAnimDh);
                    DisplayInfo displayInfo = this.mWin.mDisplayContent.getDisplayInfo();
                    this.mAnimDw = displayInfo.appWidth;
                    this.mAnimDh = displayInfo.appHeight;
                    this.mAnimation.setStartTime(currentTime);
                    this.mLocalAnimating = true;
                    this.mAnimating = true;
                }
                if (this.mAnimation != null && this.mLocalAnimating && stepAnimation(currentTime)) {
                    return true;
                }
            }
            this.mHasLocalTransformation = false;
            if ((!this.mLocalAnimating || this.mAnimationIsEntrance) && this.mAppAnimator != null && this.mAppAnimator.animation != null) {
                this.mAnimating = true;
                this.mHasTransformation = true;
                this.mTransformation.clear();
                return false;
            } else if (this.mHasTransformation) {
                this.mAnimating = true;
            } else if (isAnimating()) {
                this.mAnimating = true;
            }
        } else if (this.mAnimation != null) {
            this.mAnimating = true;
        }
        if (!this.mAnimating && !this.mLocalAnimating) {
            return false;
        }
        this.mAnimating = false;
        this.mLocalAnimating = false;
        if (this.mAnimation != null) {
            this.mAnimation.cancel();
            this.mAnimation = null;
        }
        if (this.mAnimator.mWindowDetachedWallpaper == this.mWin) {
            this.mAnimator.mWindowDetachedWallpaper = null;
        }
        this.mAnimLayer = this.mWin.mLayer;
        if (this.mWin.mIsImWindow) {
            this.mAnimLayer += this.mService.mInputMethodAnimLayerAdjustment;
        } else if (this.mIsWallpaper) {
            this.mAnimLayer += this.mService.mWallpaperAnimLayerAdjustment;
        }
        this.mHasTransformation = false;
        this.mHasLocalTransformation = false;
        if (this.mWin.mPolicyVisibility != this.mWin.mPolicyVisibilityAfterAnim) {
            this.mWin.mPolicyVisibility = this.mWin.mPolicyVisibilityAfterAnim;
            this.mWin.mDisplayContent.layoutNeeded = true;
            if (!this.mWin.mPolicyVisibility) {
                if (this.mService.mCurrentFocus == this.mWin) {
                    this.mService.mFocusMayChange = true;
                }
                this.mService.enableScreenIfNeededLocked();
            }
        }
        this.mTransformation.clear();
        if (this.mDrawState == 4 && this.mWin.mAttrs.type == 3 && this.mWin.mAppToken != null && this.mWin.mAppToken.firstWindowDrawn && this.mWin.mAppToken.startingData != null) {
            this.mService.mFinishedStarting.add(this.mWin.mAppToken);
            this.mService.mH.sendEmptyMessage(7);
        }
        finishExit();
        int displayId = this.mWin.mDisplayContent.getDisplayId();
        this.mAnimator.setPendingLayoutChanges(displayId, 8);
        this.mService.debugLayoutRepeats(TAG, this.mAnimator.getPendingLayoutChanges(displayId));
        if (this.mWin.mAppToken != null) {
            this.mWin.mAppToken.updateReportedVisibilityLocked();
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishExit() {
        int N = this.mWin.mChildWindows.size();
        for (int i = 0; i < N; i++) {
            this.mWin.mChildWindows.get(i).mWinAnimator.finishExit();
        }
        if (!this.mWin.mExiting || isWindowAnimating()) {
            return;
        }
        if (this.mSurfaceControl != null) {
            this.mService.mDestroySurface.add(this.mWin);
            this.mWin.mDestroying = true;
            hide();
        }
        this.mWin.mExiting = false;
        if (this.mWin.mRemoveOnExit) {
            this.mService.mPendingRemove.add(this.mWin);
            this.mWin.mRemoveOnExit = false;
        }
        this.mAnimator.hideWallpapersLocked(this.mWin);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hide() {
        if (!this.mLastHidden) {
            this.mLastHidden = true;
            if (this.mSurfaceControl != null) {
                this.mSurfaceShown = false;
                try {
                    this.mSurfaceControl.hide();
                } catch (RuntimeException e) {
                    Slog.w(TAG, "Exception hiding surface in " + this.mWin);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean finishDrawingLocked() {
        if (this.mDrawState == 1) {
            this.mDrawState = 2;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean commitFinishDrawingLocked(long currentTime) {
        if (this.mDrawState != 2) {
            return false;
        }
        this.mDrawState = 3;
        boolean starting = this.mWin.mAttrs.type == 3;
        AppWindowToken atoken = this.mWin.mAppToken;
        if (atoken == null || atoken.allDrawn || starting) {
            performShowLocked();
            return true;
        }
        return true;
    }

    /* loaded from: WindowStateAnimator$SurfaceTrace.class */
    static class SurfaceTrace extends SurfaceControl {
        private static final String SURFACE_TAG = "SurfaceTrace";
        static final ArrayList<SurfaceTrace> sSurfaces = new ArrayList<>();
        private float mSurfaceTraceAlpha;
        private int mLayer;
        private final PointF mPosition;
        private final Point mSize;
        private final Rect mWindowCrop;
        private boolean mShown;
        private int mLayerStack;
        private final String mName;

        public SurfaceTrace(SurfaceSession s, String name, int w, int h, int format, int flags) throws Surface.OutOfResourcesException {
            super(s, name, w, h, format, flags);
            this.mSurfaceTraceAlpha = 0.0f;
            this.mPosition = new PointF();
            this.mSize = new Point();
            this.mWindowCrop = new Rect();
            this.mShown = false;
            this.mName = name != null ? name : "Not named";
            this.mSize.set(w, h);
            Slog.v(SURFACE_TAG, "ctor: " + this + ". Called by " + Debug.getCallers(3));
        }

        @Override // android.view.SurfaceControl
        public void setAlpha(float alpha) {
            if (this.mSurfaceTraceAlpha != alpha) {
                Slog.v(SURFACE_TAG, "setAlpha(" + alpha + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mSurfaceTraceAlpha = alpha;
            }
            super.setAlpha(alpha);
        }

        @Override // android.view.SurfaceControl
        public void setLayer(int zorder) {
            if (zorder != this.mLayer) {
                Slog.v(SURFACE_TAG, "setLayer(" + zorder + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mLayer = zorder;
            }
            super.setLayer(zorder);
            sSurfaces.remove(this);
            int i = sSurfaces.size() - 1;
            while (i >= 0) {
                SurfaceTrace s = sSurfaces.get(i);
                if (s.mLayer < zorder) {
                    break;
                }
                i--;
            }
            sSurfaces.add(i + 1, this);
        }

        @Override // android.view.SurfaceControl
        public void setPosition(float x, float y) {
            if (x != this.mPosition.x || y != this.mPosition.y) {
                Slog.v(SURFACE_TAG, "setPosition(" + x + Separators.COMMA + y + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mPosition.set(x, y);
            }
            super.setPosition(x, y);
        }

        @Override // android.view.SurfaceControl
        public void setSize(int w, int h) {
            if (w != this.mSize.x || h != this.mSize.y) {
                Slog.v(SURFACE_TAG, "setSize(" + w + Separators.COMMA + h + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mSize.set(w, h);
            }
            super.setSize(w, h);
        }

        @Override // android.view.SurfaceControl
        public void setWindowCrop(Rect crop) {
            if (crop != null && !crop.equals(this.mWindowCrop)) {
                Slog.v(SURFACE_TAG, "setWindowCrop(" + crop.toShortString() + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mWindowCrop.set(crop);
            }
            super.setWindowCrop(crop);
        }

        @Override // android.view.SurfaceControl
        public void setLayerStack(int layerStack) {
            if (layerStack != this.mLayerStack) {
                Slog.v(SURFACE_TAG, "setLayerStack(" + layerStack + "): OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mLayerStack = layerStack;
            }
            super.setLayerStack(layerStack);
        }

        @Override // android.view.SurfaceControl
        public void hide() {
            if (this.mShown) {
                Slog.v(SURFACE_TAG, "hide: OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mShown = false;
            }
            super.hide();
        }

        @Override // android.view.SurfaceControl
        public void show() {
            if (!this.mShown) {
                Slog.v(SURFACE_TAG, "show: OLD:" + this + ". Called by " + Debug.getCallers(3));
                this.mShown = true;
            }
            super.show();
        }

        @Override // android.view.SurfaceControl
        public void destroy() {
            super.destroy();
            Slog.v(SURFACE_TAG, "destroy: " + this + ". Called by " + Debug.getCallers(3));
            sSurfaces.remove(this);
        }

        @Override // android.view.SurfaceControl
        public void release() {
            super.release();
            Slog.v(SURFACE_TAG, "release: " + this + ". Called by " + Debug.getCallers(3));
            sSurfaces.remove(this);
        }

        static void dumpAllSurfaces() {
            int N = sSurfaces.size();
            for (int i = 0; i < N; i++) {
                Slog.i(WindowStateAnimator.TAG, "SurfaceDump: " + sSurfaces.get(i));
            }
        }

        @Override // android.view.SurfaceControl
        public String toString() {
            return "Surface " + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.mName + " (" + this.mLayerStack + "): shown=" + this.mShown + " layer=" + this.mLayer + " alpha=" + this.mSurfaceTraceAlpha + Separators.SP + this.mPosition.x + Separators.COMMA + this.mPosition.y + Separators.SP + this.mSize.x + "x" + this.mSize.y + " crop=" + this.mWindowCrop.toShortString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroySurfaceLocked() {
        if (this.mWin.mAppToken != null && this.mWin == this.mWin.mAppToken.startingWindow) {
            this.mWin.mAppToken.startingDisplayed = false;
        }
        if (this.mSurfaceControl != null) {
            int i = this.mWin.mChildWindows.size();
            while (i > 0) {
                i--;
                WindowState c = this.mWin.mChildWindows.get(i);
                c.mAttachedHidden = true;
            }
            try {
                if (this.mSurfaceDestroyDeferred) {
                    if (this.mSurfaceControl != null && this.mPendingDestroySurface != this.mSurfaceControl) {
                        if (this.mPendingDestroySurface != null) {
                            this.mPendingDestroySurface.destroy();
                        }
                        this.mPendingDestroySurface = this.mSurfaceControl;
                    }
                } else {
                    this.mSurfaceControl.destroy();
                }
                this.mAnimator.hideWallpapersLocked(this.mWin);
            } catch (RuntimeException e) {
                Slog.w(TAG, "Exception thrown when destroying Window " + this + " surface " + this.mSurfaceControl + " session " + this.mSession + ": " + e.toString());
            }
            this.mSurfaceShown = false;
            this.mSurfaceControl = null;
            this.mWin.mHasSurface = false;
            this.mDrawState = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroyDeferredSurfaceLocked() {
        try {
            if (this.mPendingDestroySurface != null) {
                this.mPendingDestroySurface.destroy();
                this.mAnimator.hideWallpapersLocked(this.mWin);
            }
        } catch (RuntimeException e) {
            Slog.w(TAG, "Exception thrown when destroying Window " + this + " surface " + this.mPendingDestroySurface + " session " + this.mSession + ": " + e.toString());
        }
        this.mSurfaceDestroyDeferred = false;
        this.mPendingDestroySurface = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void computeShownFrameLocked() {
        MagnificationSpec spec;
        boolean selfTransformation = this.mHasLocalTransformation;
        Transformation attachedTransformation = (this.mAttachedWinAnimator == null || !this.mAttachedWinAnimator.mHasLocalTransformation) ? null : this.mAttachedWinAnimator.mTransformation;
        Transformation appTransformation = (this.mAppAnimator == null || !this.mAppAnimator.hasTransformation) ? null : this.mAppAnimator.transformation;
        if (this.mIsWallpaper && this.mService.mLowerWallpaperTarget == null && this.mService.mWallpaperTarget != null) {
            WindowStateAnimator wallpaperAnimator = this.mService.mWallpaperTarget.mWinAnimator;
            if (wallpaperAnimator.mHasLocalTransformation && wallpaperAnimator.mAnimation != null && !wallpaperAnimator.mAnimation.getDetachWallpaper()) {
                attachedTransformation = wallpaperAnimator.mTransformation;
            }
            AppWindowAnimator wpAppAnimator = this.mAnimator.getWallpaperAppAnimator();
            if (wpAppAnimator != null && wpAppAnimator.hasTransformation && wpAppAnimator.animation != null && !wpAppAnimator.animation.getDetachWallpaper()) {
                appTransformation = wpAppAnimator.transformation;
            }
        }
        int displayId = this.mWin.getDisplayId();
        ScreenRotationAnimation screenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(displayId);
        boolean screenAnimation = screenRotationAnimation != null && screenRotationAnimation.isAnimating();
        if (selfTransformation || attachedTransformation != null || appTransformation != null || screenAnimation) {
            Rect frame = this.mWin.mFrame;
            float[] tmpFloats = this.mService.mTmpFloats;
            Matrix tmpMatrix = this.mWin.mTmpMatrix;
            if (screenAnimation && screenRotationAnimation.isRotating()) {
                float w = frame.width();
                float h = frame.height();
                if (w >= 1.0f && h >= 1.0f) {
                    tmpMatrix.setScale(1.0f + (2.0f / w), 1.0f + (2.0f / h), w / 2.0f, h / 2.0f);
                } else {
                    tmpMatrix.reset();
                }
            } else {
                tmpMatrix.reset();
            }
            tmpMatrix.postScale(this.mWin.mGlobalScale, this.mWin.mGlobalScale);
            if (selfTransformation) {
                tmpMatrix.postConcat(this.mTransformation.getMatrix());
            }
            tmpMatrix.postTranslate(frame.left + this.mWin.mXOffset, frame.top + this.mWin.mYOffset);
            if (attachedTransformation != null) {
                tmpMatrix.postConcat(attachedTransformation.getMatrix());
            }
            if (appTransformation != null) {
                tmpMatrix.postConcat(appTransformation.getMatrix());
            }
            if (this.mAnimator.mUniverseBackground != null) {
                tmpMatrix.postConcat(this.mAnimator.mUniverseBackground.mUniverseTransform.getMatrix());
            }
            if (screenAnimation) {
                tmpMatrix.postConcat(screenRotationAnimation.getEnterTransformation().getMatrix());
            }
            if (this.mService.mDisplayMagnifier != null && this.mWin.getDisplayId() == 0 && (spec = this.mService.mDisplayMagnifier.getMagnificationSpecForWindowLocked(this.mWin)) != null && !spec.isNop()) {
                tmpMatrix.postScale(spec.scale, spec.scale);
                tmpMatrix.postTranslate(spec.offsetX, spec.offsetY);
            }
            this.mHaveMatrix = true;
            tmpMatrix.getValues(tmpFloats);
            this.mDsDx = tmpFloats[0];
            this.mDtDx = tmpFloats[3];
            this.mDsDy = tmpFloats[1];
            this.mDtDy = tmpFloats[4];
            float x = tmpFloats[2];
            float y = tmpFloats[5];
            this.mWin.mShownFrame.set(x, y, x + frame.width(), y + frame.height());
            this.mShownAlpha = this.mAlpha;
            if (!this.mService.mLimitedAlphaCompositing || !PixelFormat.formatHasAlpha(this.mWin.mAttrs.format) || (this.mWin.isIdentityMatrix(this.mDsDx, this.mDtDx, this.mDsDy, this.mDtDy) && x == frame.left && y == frame.top)) {
                if (selfTransformation) {
                    this.mShownAlpha *= this.mTransformation.getAlpha();
                }
                if (attachedTransformation != null) {
                    this.mShownAlpha *= attachedTransformation.getAlpha();
                }
                if (appTransformation != null) {
                    this.mShownAlpha *= appTransformation.getAlpha();
                }
                if (this.mAnimator.mUniverseBackground != null) {
                    this.mShownAlpha *= this.mAnimator.mUniverseBackground.mUniverseTransform.getAlpha();
                }
                if (screenAnimation) {
                    this.mShownAlpha *= screenRotationAnimation.getEnterTransformation().getAlpha();
                }
            }
        } else if (this.mIsWallpaper && this.mService.mInnerFields.mWallpaperActionPending) {
        } else {
            boolean applyUniverseTransformation = (this.mAnimator.mUniverseBackground == null || this.mWin.mAttrs.type == 2025 || this.mWin.mBaseLayer >= this.mAnimator.mAboveUniverseLayer) ? false : true;
            MagnificationSpec spec2 = null;
            if (this.mService.mDisplayMagnifier != null && this.mWin.getDisplayId() == 0) {
                spec2 = this.mService.mDisplayMagnifier.getMagnificationSpecForWindowLocked(this.mWin);
            }
            if (applyUniverseTransformation || spec2 != null) {
                Rect frame2 = this.mWin.mFrame;
                float[] tmpFloats2 = this.mService.mTmpFloats;
                Matrix tmpMatrix2 = this.mWin.mTmpMatrix;
                tmpMatrix2.setScale(this.mWin.mGlobalScale, this.mWin.mGlobalScale);
                tmpMatrix2.postTranslate(frame2.left + this.mWin.mXOffset, frame2.top + this.mWin.mYOffset);
                if (applyUniverseTransformation) {
                    tmpMatrix2.postConcat(this.mAnimator.mUniverseBackground.mUniverseTransform.getMatrix());
                }
                if (spec2 != null && !spec2.isNop()) {
                    tmpMatrix2.postScale(spec2.scale, spec2.scale);
                    tmpMatrix2.postTranslate(spec2.offsetX, spec2.offsetY);
                }
                tmpMatrix2.getValues(tmpFloats2);
                this.mHaveMatrix = true;
                this.mDsDx = tmpFloats2[0];
                this.mDtDx = tmpFloats2[3];
                this.mDsDy = tmpFloats2[1];
                this.mDtDy = tmpFloats2[4];
                float x2 = tmpFloats2[2];
                float y2 = tmpFloats2[5];
                this.mWin.mShownFrame.set(x2, y2, x2 + frame2.width(), y2 + frame2.height());
                this.mShownAlpha = this.mAlpha;
                if (applyUniverseTransformation) {
                    this.mShownAlpha *= this.mAnimator.mUniverseBackground.mUniverseTransform.getAlpha();
                    return;
                }
                return;
            }
            this.mWin.mShownFrame.set(this.mWin.mFrame);
            if (this.mWin.mXOffset != 0 || this.mWin.mYOffset != 0) {
                this.mWin.mShownFrame.offset(this.mWin.mXOffset, this.mWin.mYOffset);
            }
            this.mShownAlpha = this.mAlpha;
            this.mHaveMatrix = false;
            this.mDsDx = this.mWin.mGlobalScale;
            this.mDtDx = 0.0f;
            this.mDsDy = 0.0f;
            this.mDtDy = this.mWin.mGlobalScale;
        }
    }

    void applyDecorRect(Rect decorRect) {
        WindowState w = this.mWin;
        int offX = w.mXOffset + w.mFrame.left;
        int offY = w.mYOffset + w.mFrame.top;
        w.mSystemDecorRect.set(0, 0, w.mFrame.width(), w.mFrame.height());
        w.mSystemDecorRect.intersect(decorRect.left - offX, decorRect.top - offY, decorRect.right - offX, decorRect.bottom - offY);
        if (w.mEnforceSizeCompat && w.mInvGlobalScale != 1.0f) {
            float scale = w.mInvGlobalScale;
            w.mSystemDecorRect.left = (int) ((w.mSystemDecorRect.left * scale) - 0.5f);
            w.mSystemDecorRect.top = (int) ((w.mSystemDecorRect.top * scale) - 0.5f);
            w.mSystemDecorRect.right = (int) (((w.mSystemDecorRect.right + 1) * scale) - 0.5f);
            w.mSystemDecorRect.bottom = (int) (((w.mSystemDecorRect.bottom + 1) * scale) - 0.5f);
        }
    }

    void updateSurfaceWindowCrop(boolean recoveringMemory) {
        WindowState w = this.mWin;
        DisplayInfo displayInfo = w.mDisplayContent.getDisplayInfo();
        if ((w.mAttrs.flags & 16384) != 0) {
            w.mSystemDecorRect.set(0, 0, w.mRequestedWidth, w.mRequestedHeight);
        } else if (!w.isDefaultDisplay()) {
            w.mSystemDecorRect.set(0, 0, w.mCompatFrame.width(), w.mCompatFrame.height());
            w.mSystemDecorRect.intersect(-w.mCompatFrame.left, -w.mCompatFrame.top, displayInfo.logicalWidth - w.mCompatFrame.left, displayInfo.logicalHeight - w.mCompatFrame.top);
        } else if (w.mLayer >= this.mService.mSystemDecorLayer) {
            if (this.mAnimator.mUniverseBackground == null) {
                w.mSystemDecorRect.set(0, 0, w.mCompatFrame.width(), w.mCompatFrame.height());
            } else {
                applyDecorRect(this.mService.mScreenRect);
            }
        } else if (w.mAttrs.type == 2025 || w.mDecorFrame.isEmpty()) {
            w.mSystemDecorRect.set(0, 0, w.mCompatFrame.width(), w.mCompatFrame.height());
        } else {
            applyDecorRect(w.mDecorFrame);
        }
        if (!w.mSystemDecorRect.equals(w.mLastSystemDecorRect)) {
            w.mLastSystemDecorRect.set(w.mSystemDecorRect);
            try {
                this.mSurfaceControl.setWindowCrop(w.mSystemDecorRect);
            } catch (RuntimeException e) {
                Slog.w(TAG, "Error setting crop surface of " + w + " crop=" + w.mSystemDecorRect.toShortString(), e);
                if (!recoveringMemory) {
                    this.mService.reclaimSomeSurfaceMemoryLocked(this, "crop", true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSurfaceBoundariesLocked(boolean recoveringMemory) {
        int width;
        int height;
        WindowState w = this.mWin;
        if ((w.mAttrs.flags & 16384) != 0) {
            width = w.mRequestedWidth;
            height = w.mRequestedHeight;
        } else {
            width = w.mCompatFrame.width();
            height = w.mCompatFrame.height();
        }
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        boolean surfaceResized = (this.mSurfaceW == ((float) width) && this.mSurfaceH == ((float) height)) ? false : true;
        if (surfaceResized) {
            this.mSurfaceW = width;
            this.mSurfaceH = height;
        }
        float left = w.mShownFrame.left;
        float top = w.mShownFrame.top;
        if (this.mSurfaceX != left || this.mSurfaceY != top) {
            try {
                this.mSurfaceX = left;
                this.mSurfaceY = top;
                this.mSurfaceControl.setPosition(left, top);
            } catch (RuntimeException e) {
                Slog.w(TAG, "Error positioning surface of " + w + " pos=(" + left + Separators.COMMA + top + Separators.RPAREN, e);
                if (!recoveringMemory) {
                    this.mService.reclaimSomeSurfaceMemoryLocked(this, BrowserContract.Bookmarks.POSITION, true);
                }
            }
        }
        if (surfaceResized) {
            try {
                this.mSurfaceResized = true;
                this.mSurfaceControl.setSize(width, height);
                int displayId = w.mDisplayContent.getDisplayId();
                this.mAnimator.setPendingLayoutChanges(displayId, 4);
                if ((w.mAttrs.flags & 2) != 0) {
                    w.getStack().startDimmingIfNeeded(this);
                }
            } catch (RuntimeException e2) {
                Slog.e(TAG, "Error resizing surface of " + w + " size=(" + width + "x" + height + Separators.RPAREN, e2);
                if (!recoveringMemory) {
                    this.mService.reclaimSomeSurfaceMemoryLocked(this, "size", true);
                }
            }
        }
        updateSurfaceWindowCrop(recoveringMemory);
    }

    public void prepareSurfaceLocked(boolean recoveringMemory) {
        WindowState w = this.mWin;
        if (this.mSurfaceControl == null) {
            if (w.mOrientationChanging) {
                w.mOrientationChanging = false;
                return;
            }
            return;
        }
        boolean displayed = false;
        computeShownFrameLocked();
        setSurfaceBoundariesLocked(recoveringMemory);
        if (this.mIsWallpaper && !this.mWin.mWallpaperVisible) {
            hide();
        } else if (w.mAttachedHidden || !w.isOnScreen()) {
            hide();
            this.mAnimator.hideWallpapersLocked(w);
            if (w.mOrientationChanging) {
                w.mOrientationChanging = false;
            }
        } else if (this.mLastLayer != this.mAnimLayer || this.mLastAlpha != this.mShownAlpha || this.mLastDsDx != this.mDsDx || this.mLastDtDx != this.mDtDx || this.mLastDsDy != this.mDsDy || this.mLastDtDy != this.mDtDy || w.mLastHScale != w.mHScale || w.mLastVScale != w.mVScale || this.mLastHidden) {
            displayed = true;
            this.mLastAlpha = this.mShownAlpha;
            this.mLastLayer = this.mAnimLayer;
            this.mLastDsDx = this.mDsDx;
            this.mLastDtDx = this.mDtDx;
            this.mLastDsDy = this.mDsDy;
            this.mLastDtDy = this.mDtDy;
            w.mLastHScale = w.mHScale;
            w.mLastVScale = w.mVScale;
            if (this.mSurfaceControl != null) {
                try {
                    this.mSurfaceAlpha = this.mShownAlpha;
                    this.mSurfaceControl.setAlpha(this.mShownAlpha);
                    this.mSurfaceLayer = this.mAnimLayer;
                    this.mSurfaceControl.setLayer(this.mAnimLayer);
                    this.mSurfaceControl.setMatrix(this.mDsDx * w.mHScale, this.mDtDx * w.mVScale, this.mDsDy * w.mHScale, this.mDtDy * w.mVScale);
                    if (this.mLastHidden && this.mDrawState == 4) {
                        if (showSurfaceRobustlyLocked()) {
                            this.mLastHidden = false;
                            if (this.mIsWallpaper) {
                                this.mService.dispatchWallpaperVisibility(w, true);
                            }
                            this.mAnimator.setPendingLayoutChanges(w.getDisplayId(), 8);
                        } else {
                            w.mOrientationChanging = false;
                        }
                    }
                    if (this.mSurfaceControl != null) {
                        w.mToken.hasVisible = true;
                    }
                } catch (RuntimeException e) {
                    Slog.w(TAG, "Error updating surface in " + w, e);
                    if (!recoveringMemory) {
                        this.mService.reclaimSomeSurfaceMemoryLocked(this, "update", true);
                    }
                }
            }
        } else {
            displayed = true;
        }
        if (displayed) {
            if (w.mOrientationChanging) {
                if (!w.isDrawnLw()) {
                    this.mAnimator.mBulkUpdateParams &= -9;
                    this.mAnimator.mLastWindowFreezeSource = w;
                } else {
                    w.mOrientationChanging = false;
                }
            }
            w.mToken.hasVisible = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean performShowLocked() {
        if (this.mWin.isHiddenFromUserLocked()) {
            Slog.w(TAG, "current user violation " + this.mService.mCurrentUserId + " trying to display " + this + ", type " + this.mWin.mAttrs.type + ", belonging to " + this.mWin.mOwnerUid);
            return false;
        } else if (this.mDrawState == 3 && this.mWin.isReadyForDisplayIgnoringKeyguard()) {
            this.mService.enableScreenIfNeededLocked();
            applyEnterAnimationLocked();
            this.mLastAlpha = -1.0f;
            this.mDrawState = 4;
            this.mService.scheduleAnimationLocked();
            int i = this.mWin.mChildWindows.size();
            while (i > 0) {
                i--;
                WindowState c = this.mWin.mChildWindows.get(i);
                if (c.mAttachedHidden) {
                    c.mAttachedHidden = false;
                    if (c.mWinAnimator.mSurfaceControl != null) {
                        c.mWinAnimator.performShowLocked();
                        c.mDisplayContent.layoutNeeded = true;
                    }
                }
            }
            if (this.mWin.mAttrs.type != 3 && this.mWin.mAppToken != null) {
                this.mWin.mAppToken.firstWindowDrawn = true;
                if (this.mWin.mAppToken.startingData != null) {
                    clearAnimation();
                    this.mService.mFinishedStarting.add(this.mWin.mAppToken);
                    this.mService.mH.sendEmptyMessage(7);
                }
                this.mWin.mAppToken.updateReportedVisibilityLocked();
                return true;
            }
            return true;
        } else {
            return false;
        }
    }

    boolean showSurfaceRobustlyLocked() {
        try {
            if (this.mSurfaceControl != null) {
                this.mSurfaceShown = true;
                this.mSurfaceControl.show();
                if (this.mWin.mTurnOnScreen) {
                    this.mWin.mTurnOnScreen = false;
                    this.mAnimator.mBulkUpdateParams |= 16;
                    return true;
                }
                return true;
            }
            return true;
        } catch (RuntimeException e) {
            Slog.w(TAG, "Failure showing surface " + this.mSurfaceControl + " in " + this.mWin, e);
            this.mService.reclaimSomeSurfaceMemoryLocked(this, HardwareRenderer.OVERDRAW_PROPERTY_SHOW, true);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyEnterAnimationLocked() {
        int transit;
        if (this.mEnterAnimationPending) {
            this.mEnterAnimationPending = false;
            transit = 1;
        } else {
            transit = 3;
        }
        applyAnimationLocked(transit, true);
        if (this.mService.mDisplayMagnifier != null && this.mWin.getDisplayId() == 0) {
            this.mService.mDisplayMagnifier.onWindowTransitionLocked(this.mWin, transit);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean applyAnimationLocked(int transit, boolean isEntrance) {
        if (this.mLocalAnimating && this.mAnimationIsEntrance == isEntrance) {
            return true;
        }
        if (this.mService.okToDisplay()) {
            int anim = this.mPolicy.selectAnimationLw(this.mWin, transit);
            int attr = -1;
            Animation a = null;
            if (anim != 0) {
                a = anim != -1 ? AnimationUtils.loadAnimation(this.mContext, anim) : null;
            } else {
                switch (transit) {
                    case 1:
                        attr = 0;
                        break;
                    case 2:
                        attr = 1;
                        break;
                    case 3:
                        attr = 2;
                        break;
                    case 4:
                        attr = 3;
                        break;
                }
                if (attr >= 0) {
                    a = this.mService.mAppTransition.loadAnimation(this.mWin.mAttrs, attr);
                }
            }
            if (a != null) {
                setAnimation(a);
                this.mAnimationIsEntrance = isEntrance;
            }
        } else {
            clearAnimation();
        }
        return this.mAnimation != null;
    }

    public void dump(PrintWriter pw, String prefix, boolean dumpAll) {
        if (this.mAnimating || this.mLocalAnimating || this.mAnimationIsEntrance || this.mAnimation != null) {
            pw.print(prefix);
            pw.print("mAnimating=");
            pw.print(this.mAnimating);
            pw.print(" mLocalAnimating=");
            pw.print(this.mLocalAnimating);
            pw.print(" mAnimationIsEntrance=");
            pw.print(this.mAnimationIsEntrance);
            pw.print(" mAnimation=");
            pw.println(this.mAnimation);
        }
        if (this.mHasTransformation || this.mHasLocalTransformation) {
            pw.print(prefix);
            pw.print("XForm: has=");
            pw.print(this.mHasTransformation);
            pw.print(" hasLocal=");
            pw.print(this.mHasLocalTransformation);
            pw.print(Separators.SP);
            this.mTransformation.printShortString(pw);
            pw.println();
        }
        if (this.mSurfaceControl != null) {
            if (dumpAll) {
                pw.print(prefix);
                pw.print("mSurface=");
                pw.println(this.mSurfaceControl);
                pw.print(prefix);
                pw.print("mDrawState=");
                pw.print(drawStateToString(this.mDrawState));
                pw.print(" mLastHidden=");
                pw.println(this.mLastHidden);
            }
            pw.print(prefix);
            pw.print("Surface: shown=");
            pw.print(this.mSurfaceShown);
            pw.print(" layer=");
            pw.print(this.mSurfaceLayer);
            pw.print(" alpha=");
            pw.print(this.mSurfaceAlpha);
            pw.print(" rect=(");
            pw.print(this.mSurfaceX);
            pw.print(Separators.COMMA);
            pw.print(this.mSurfaceY);
            pw.print(") ");
            pw.print(this.mSurfaceW);
            pw.print(" x ");
            pw.println(this.mSurfaceH);
        }
        if (this.mPendingDestroySurface != null) {
            pw.print(prefix);
            pw.print("mPendingDestroySurface=");
            pw.println(this.mPendingDestroySurface);
        }
        if (this.mSurfaceResized || this.mSurfaceDestroyDeferred) {
            pw.print(prefix);
            pw.print("mSurfaceResized=");
            pw.print(this.mSurfaceResized);
            pw.print(" mSurfaceDestroyDeferred=");
            pw.println(this.mSurfaceDestroyDeferred);
        }
        if (this.mWin.mAttrs.type == 2025) {
            pw.print(prefix);
            pw.print("mUniverseTransform=");
            this.mUniverseTransform.printShortString(pw);
            pw.println();
        }
        if (this.mShownAlpha != 1.0f || this.mAlpha != 1.0f || this.mLastAlpha != 1.0f) {
            pw.print(prefix);
            pw.print("mShownAlpha=");
            pw.print(this.mShownAlpha);
            pw.print(" mAlpha=");
            pw.print(this.mAlpha);
            pw.print(" mLastAlpha=");
            pw.println(this.mLastAlpha);
        }
        if (this.mHaveMatrix || this.mWin.mGlobalScale != 1.0f) {
            pw.print(prefix);
            pw.print("mGlobalScale=");
            pw.print(this.mWin.mGlobalScale);
            pw.print(" mDsDx=");
            pw.print(this.mDsDx);
            pw.print(" mDtDx=");
            pw.print(this.mDtDx);
            pw.print(" mDsDy=");
            pw.print(this.mDsDy);
            pw.print(" mDtDy=");
            pw.println(this.mDtDy);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("WindowStateAnimator{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.mWin.mAttrs.getTitle());
        sb.append('}');
        return sb.toString();
    }
}