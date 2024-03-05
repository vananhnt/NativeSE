package com.android.server.wm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Pools;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.IMagnificationCallbacks;
import android.view.MagnificationSpec;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.WindowManagerPolicy;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.os.SomeArgs;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DisplayMagnifier.class */
public final class DisplayMagnifier {
    private static final String LOG_TAG = DisplayMagnifier.class.getSimpleName();
    private static final boolean DEBUG_WINDOW_TRANSITIONS = false;
    private static final boolean DEBUG_ROTATION = false;
    private static final boolean DEBUG_LAYERS = false;
    private static final boolean DEBUG_RECTANGLE_REQUESTED = false;
    private static final boolean DEBUG_VIEWPORT_WINDOW = false;
    private final Context mContext;
    private final WindowManagerService mWindowManagerService;
    private final Handler mHandler;
    private final IMagnificationCallbacks mCallbacks;
    private final long mLongAnimationDuration;
    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();
    private final Region mTempRegion1 = new Region();
    private final Region mTempRegion2 = new Region();
    private final Region mTempRegion3 = new Region();
    private final Region mTempRegion4 = new Region();
    private final MagnifiedViewport mMagnifedViewport = new MagnifiedViewport();

    static /* synthetic */ IMagnificationCallbacks access$1300(DisplayMagnifier x0) {
        return x0.mCallbacks;
    }

    public DisplayMagnifier(WindowManagerService windowManagerService, IMagnificationCallbacks callbacks) {
        this.mContext = windowManagerService.mContext;
        this.mWindowManagerService = windowManagerService;
        this.mCallbacks = callbacks;
        this.mHandler = new MyHandler(this.mWindowManagerService.mH.getLooper());
        this.mLongAnimationDuration = this.mContext.getResources().getInteger(17694722);
    }

    public void setMagnificationSpecLocked(MagnificationSpec spec) {
        this.mMagnifedViewport.updateMagnificationSpecLocked(spec);
        this.mMagnifedViewport.recomputeBoundsLocked();
        this.mWindowManagerService.scheduleAnimationLocked();
    }

    public void onRectangleOnScreenRequestedLocked(Rect rectangle, boolean immediate) {
        if (!this.mMagnifedViewport.isMagnifyingLocked()) {
            return;
        }
        Rect magnifiedRegionBounds = this.mTempRect2;
        this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked(magnifiedRegionBounds);
        if (magnifiedRegionBounds.contains(rectangle)) {
            return;
        }
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = rectangle.left;
        args.argi2 = rectangle.top;
        args.argi3 = rectangle.right;
        args.argi4 = rectangle.bottom;
        this.mHandler.obtainMessage(2, args).sendToTarget();
    }

    public void onWindowLayersChangedLocked() {
        this.mMagnifedViewport.recomputeBoundsLocked();
        this.mWindowManagerService.scheduleAnimationLocked();
    }

    public void onRotationChangedLocked(DisplayContent displayContent, int rotation) {
        this.mMagnifedViewport.onRotationChangedLocked();
        this.mHandler.sendEmptyMessage(4);
    }

    public void onAppWindowTransitionLocked(WindowState windowState, int transition) {
        boolean magnifying = this.mMagnifedViewport.isMagnifyingLocked();
        if (magnifying) {
            switch (transition) {
                case 4102:
                case AppTransition.TRANSIT_TASK_OPEN /* 4104 */:
                case AppTransition.TRANSIT_TASK_TO_FRONT /* 4106 */:
                case AppTransition.TRANSIT_WALLPAPER_OPEN /* 4109 */:
                case AppTransition.TRANSIT_WALLPAPER_INTRA_OPEN /* 4110 */:
                case 8204:
                    this.mHandler.sendEmptyMessage(3);
                    return;
                default:
                    return;
            }
        }
    }

    public void onWindowTransitionLocked(WindowState windowState, int transition) {
        boolean magnifying = this.mMagnifedViewport.isMagnifyingLocked();
        int type = windowState.mAttrs.type;
        switch (transition) {
            case 1:
            case 3:
                if (magnifying) {
                    switch (type) {
                        case 2:
                        case 1000:
                        case 1001:
                        case 1002:
                        case 1003:
                        case 2001:
                        case 2002:
                        case 2003:
                        case 2005:
                        case 2006:
                        case 2007:
                        case 2008:
                        case 2009:
                        case WindowManager.LayoutParams.TYPE_SYSTEM_ERROR /* 2010 */:
                        case WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY /* 2020 */:
                        case WindowManager.LayoutParams.TYPE_NAVIGATION_BAR_PANEL /* 2024 */:
                        case WindowManager.LayoutParams.TYPE_RECENTS_OVERLAY /* 2028 */:
                            Rect magnifiedRegionBounds = this.mTempRect2;
                            this.mMagnifedViewport.getMagnifiedFrameInContentCoordsLocked(magnifiedRegionBounds);
                            Rect touchableRegionBounds = this.mTempRect1;
                            windowState.getTouchableRegion(this.mTempRegion1);
                            this.mTempRegion1.getBounds(touchableRegionBounds);
                            if (!magnifiedRegionBounds.intersect(touchableRegionBounds)) {
                                try {
                                    this.mCallbacks.onRectangleOnScreenRequested(touchableRegionBounds.left, touchableRegionBounds.top, touchableRegionBounds.right, touchableRegionBounds.bottom);
                                    return;
                                } catch (RemoteException e) {
                                    return;
                                }
                            }
                            return;
                        default:
                            return;
                    }
                }
                return;
            default:
                return;
        }
    }

    public MagnificationSpec getMagnificationSpecForWindowLocked(WindowState windowState) {
        MagnificationSpec spec = this.mMagnifedViewport.getMagnificationSpecLocked();
        if (spec != null && !spec.isNop()) {
            WindowManagerPolicy policy = this.mWindowManagerService.mPolicy;
            int windowType = windowState.mAttrs.type;
            if ((!policy.isTopLevelWindow(windowType) && windowState.mAttachedWindow != null && !policy.canMagnifyWindow(windowType)) || !policy.canMagnifyWindow(windowState.mAttrs.type)) {
                return null;
            }
        }
        return spec;
    }

    public void destroyLocked() {
        this.mMagnifedViewport.destroyWindow();
    }

    public void drawMagnifiedRegionBorderIfNeededLocked() {
        this.mMagnifedViewport.drawWindowIfNeededLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayMagnifier$MagnifiedViewport.class */
    public final class MagnifiedViewport {
        private static final int DEFAUTLT_BORDER_WIDTH_DIP = 5;
        private final SparseArray<WindowStateInfo> mTempWindowStateInfos = new SparseArray<>();
        private final float[] mTempFloats = new float[9];
        private final RectF mTempRectF = new RectF();
        private final Point mTempPoint = new Point();
        private final Matrix mTempMatrix = new Matrix();
        private final Region mMagnifiedBounds = new Region();
        private final Region mOldMagnifiedBounds = new Region();
        private final MagnificationSpec mMagnificationSpec = MagnificationSpec.obtain();
        private final WindowManager mWindowManager;
        private final int mBorderWidth;
        private final int mHalfBorderWidth;
        private final ViewportWindow mWindow;
        private boolean mFullRedrawNeeded;

        public MagnifiedViewport() {
            this.mWindowManager = (WindowManager) DisplayMagnifier.this.mContext.getSystemService(Context.WINDOW_SERVICE);
            this.mBorderWidth = (int) TypedValue.applyDimension(1, 5.0f, DisplayMagnifier.this.mContext.getResources().getDisplayMetrics());
            this.mHalfBorderWidth = ((int) (this.mBorderWidth + 0.5d)) / 2;
            this.mWindow = new ViewportWindow(DisplayMagnifier.this.mContext);
            recomputeBoundsLocked();
        }

        public void updateMagnificationSpecLocked(MagnificationSpec spec) {
            if (spec != null) {
                this.mMagnificationSpec.initialize(spec.scale, spec.offsetX, spec.offsetY);
            } else {
                this.mMagnificationSpec.clear();
            }
            if (!DisplayMagnifier.this.mHandler.hasMessages(5)) {
                setMagnifiedRegionBorderShownLocked(isMagnifyingLocked(), true);
            }
        }

        public void recomputeBoundsLocked() {
            this.mWindowManager.getDefaultDisplay().getRealSize(this.mTempPoint);
            int screenWidth = this.mTempPoint.x;
            int screenHeight = this.mTempPoint.y;
            Region magnifiedBounds = this.mMagnifiedBounds;
            magnifiedBounds.set(0, 0, 0, 0);
            Region availableBounds = DisplayMagnifier.this.mTempRegion1;
            availableBounds.set(0, 0, screenWidth, screenHeight);
            Region nonMagnifiedBounds = DisplayMagnifier.this.mTempRegion4;
            nonMagnifiedBounds.set(0, 0, 0, 0);
            SparseArray<WindowStateInfo> visibleWindows = this.mTempWindowStateInfos;
            visibleWindows.clear();
            getWindowsOnScreenLocked(visibleWindows);
            int visibleWindowCount = visibleWindows.size();
            for (int i = visibleWindowCount - 1; i >= 0; i--) {
                WindowStateInfo info = visibleWindows.valueAt(i);
                if (info.mWindowState.mAttrs.type != 2027) {
                    Region windowBounds = DisplayMagnifier.this.mTempRegion2;
                    Matrix matrix = this.mTempMatrix;
                    populateTransformationMatrix(info.mWindowState, matrix);
                    RectF windowFrame = this.mTempRectF;
                    if (DisplayMagnifier.this.mWindowManagerService.mPolicy.canMagnifyWindow(info.mWindowState.mAttrs.type)) {
                        windowFrame.set(info.mWindowState.mFrame);
                        windowFrame.offset(-windowFrame.left, -windowFrame.top);
                        matrix.mapRect(windowFrame);
                        windowBounds.set((int) windowFrame.left, (int) windowFrame.top, (int) windowFrame.right, (int) windowFrame.bottom);
                        magnifiedBounds.op(windowBounds, Region.Op.UNION);
                        magnifiedBounds.op(availableBounds, Region.Op.INTERSECT);
                    } else {
                        windowFrame.set(info.mTouchableRegion);
                        windowFrame.offset(-info.mWindowState.mFrame.left, -info.mWindowState.mFrame.top);
                        matrix.mapRect(windowFrame);
                        windowBounds.set((int) windowFrame.left, (int) windowFrame.top, (int) windowFrame.right, (int) windowFrame.bottom);
                        nonMagnifiedBounds.op(windowBounds, Region.Op.UNION);
                        windowBounds.op(magnifiedBounds, Region.Op.DIFFERENCE);
                        availableBounds.op(windowBounds, Region.Op.DIFFERENCE);
                    }
                    Region accountedBounds = DisplayMagnifier.this.mTempRegion2;
                    accountedBounds.set(magnifiedBounds);
                    accountedBounds.op(nonMagnifiedBounds, Region.Op.UNION);
                    accountedBounds.op(0, 0, screenWidth, screenHeight, Region.Op.INTERSECT);
                    if (accountedBounds.isRect()) {
                        Rect accountedFrame = DisplayMagnifier.this.mTempRect1;
                        accountedBounds.getBounds(accountedFrame);
                        if (accountedFrame.width() == screenWidth && accountedFrame.height() == screenHeight) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            }
            for (int i2 = visibleWindowCount - 1; i2 >= 0; i2--) {
                visibleWindows.valueAt(i2).recycle();
                visibleWindows.removeAt(i2);
            }
            magnifiedBounds.op(this.mHalfBorderWidth, this.mHalfBorderWidth, screenWidth - this.mHalfBorderWidth, screenHeight - this.mHalfBorderWidth, Region.Op.INTERSECT);
            if (!this.mOldMagnifiedBounds.equals(magnifiedBounds)) {
                Region bounds = Region.obtain();
                bounds.set(magnifiedBounds);
                DisplayMagnifier.this.mHandler.obtainMessage(1, bounds).sendToTarget();
                this.mWindow.setBounds(magnifiedBounds);
                Rect dirtyRect = DisplayMagnifier.this.mTempRect1;
                if (!this.mFullRedrawNeeded) {
                    Region dirtyRegion = DisplayMagnifier.this.mTempRegion3;
                    dirtyRegion.set(magnifiedBounds);
                    dirtyRegion.op(this.mOldMagnifiedBounds, Region.Op.UNION);
                    dirtyRegion.op(nonMagnifiedBounds, Region.Op.INTERSECT);
                    dirtyRegion.getBounds(dirtyRect);
                    this.mWindow.invalidate(dirtyRect);
                } else {
                    this.mFullRedrawNeeded = false;
                    dirtyRect.set(this.mHalfBorderWidth, this.mHalfBorderWidth, screenWidth - this.mHalfBorderWidth, screenHeight - this.mHalfBorderWidth);
                    this.mWindow.invalidate(dirtyRect);
                }
                this.mOldMagnifiedBounds.set(magnifiedBounds);
            }
        }

        private void populateTransformationMatrix(WindowState windowState, Matrix outMatrix) {
            this.mTempFloats[0] = windowState.mWinAnimator.mDsDx;
            this.mTempFloats[3] = windowState.mWinAnimator.mDtDx;
            this.mTempFloats[1] = windowState.mWinAnimator.mDsDy;
            this.mTempFloats[4] = windowState.mWinAnimator.mDtDy;
            this.mTempFloats[2] = windowState.mShownFrame.left;
            this.mTempFloats[5] = windowState.mShownFrame.top;
            this.mTempFloats[6] = 0.0f;
            this.mTempFloats[7] = 0.0f;
            this.mTempFloats[8] = 1.0f;
            outMatrix.setValues(this.mTempFloats);
        }

        private void getWindowsOnScreenLocked(SparseArray<WindowStateInfo> outWindowStates) {
            DisplayContent displayContent = DisplayMagnifier.this.mWindowManagerService.getDefaultDisplayContentLocked();
            WindowList windowList = displayContent.getWindowList();
            int windowCount = windowList.size();
            for (int i = 0; i < windowCount; i++) {
                WindowState windowState = windowList.get(i);
                if ((windowState.isOnScreen() || windowState.mAttrs.type == 2025) && !windowState.mWinAnimator.mEnterAnimationPending) {
                    outWindowStates.put(windowState.mLayer, WindowStateInfo.obtain(windowState));
                }
            }
        }

        public void onRotationChangedLocked() {
            if (isMagnifyingLocked()) {
                setMagnifiedRegionBorderShownLocked(false, false);
                long delay = ((float) DisplayMagnifier.this.mLongAnimationDuration) * DisplayMagnifier.this.mWindowManagerService.mWindowAnimationScale;
                Message message = DisplayMagnifier.this.mHandler.obtainMessage(5);
                DisplayMagnifier.this.mHandler.sendMessageDelayed(message, delay);
            }
            recomputeBoundsLocked();
            this.mWindow.updateSize();
        }

        public void setMagnifiedRegionBorderShownLocked(boolean shown, boolean animate) {
            if (shown) {
                this.mFullRedrawNeeded = true;
                this.mOldMagnifiedBounds.set(0, 0, 0, 0);
            }
            this.mWindow.setShown(shown, animate);
        }

        public void getMagnifiedFrameInContentCoordsLocked(Rect rect) {
            MagnificationSpec spec = this.mMagnificationSpec;
            this.mMagnifiedBounds.getBounds(rect);
            rect.offset((int) (-spec.offsetX), (int) (-spec.offsetY));
            rect.scale(1.0f / spec.scale);
        }

        public boolean isMagnifyingLocked() {
            return this.mMagnificationSpec.scale > 1.0f;
        }

        public MagnificationSpec getMagnificationSpecLocked() {
            return this.mMagnificationSpec;
        }

        public void drawWindowIfNeededLocked() {
            recomputeBoundsLocked();
            this.mWindow.drawIfNeeded();
        }

        public void destroyWindow() {
            this.mWindow.releaseSurface();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: DisplayMagnifier$MagnifiedViewport$ViewportWindow.class */
        public final class ViewportWindow {
            private static final String SURFACE_TITLE = "Magnification Overlay";
            private static final String PROPERTY_NAME_ALPHA = "alpha";
            private static final int MIN_ALPHA = 0;
            private static final int MAX_ALPHA = 255;
            private final ValueAnimator mShowHideFrameAnimator;
            private final SurfaceControl mSurfaceControl;
            private boolean mShown;
            private int mAlpha;
            private boolean mInvalidated;
            private final Region mBounds = new Region();
            private final Rect mDirtyRect = new Rect();
            private final Paint mPaint = new Paint();
            private final Surface mSurface = new Surface();

            public ViewportWindow(Context context) {
                SurfaceControl surfaceControl = null;
                try {
                    MagnifiedViewport.this.mWindowManager.getDefaultDisplay().getRealSize(MagnifiedViewport.this.mTempPoint);
                    surfaceControl = new SurfaceControl(DisplayMagnifier.this.mWindowManagerService.mFxSession, SURFACE_TITLE, MagnifiedViewport.this.mTempPoint.x, MagnifiedViewport.this.mTempPoint.y, -3, 4);
                } catch (Surface.OutOfResourcesException e) {
                }
                this.mSurfaceControl = surfaceControl;
                this.mSurfaceControl.setLayerStack(MagnifiedViewport.this.mWindowManager.getDefaultDisplay().getLayerStack());
                this.mSurfaceControl.setLayer(DisplayMagnifier.this.mWindowManagerService.mPolicy.windowTypeToLayerLw(WindowManager.LayoutParams.TYPE_MAGNIFICATION_OVERLAY) * 10000);
                this.mSurfaceControl.setPosition(0.0f, 0.0f);
                this.mSurface.copyFrom(this.mSurfaceControl);
                TypedValue typedValue = new TypedValue();
                context.getTheme().resolveAttribute(16843664, typedValue, true);
                int borderColor = context.getResources().getColor(typedValue.resourceId);
                this.mPaint.setStyle(Paint.Style.STROKE);
                this.mPaint.setStrokeWidth(MagnifiedViewport.this.mBorderWidth);
                this.mPaint.setColor(borderColor);
                Interpolator interpolator = new DecelerateInterpolator(2.5f);
                long longAnimationDuration = context.getResources().getInteger(17694722);
                this.mShowHideFrameAnimator = ObjectAnimator.ofInt(this, PROPERTY_NAME_ALPHA, 0, 255);
                this.mShowHideFrameAnimator.setInterpolator(interpolator);
                this.mShowHideFrameAnimator.setDuration(longAnimationDuration);
                this.mInvalidated = true;
            }

            public void setShown(boolean shown, boolean animate) {
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    if (this.mShown == shown) {
                        return;
                    }
                    this.mShown = shown;
                    if (animate) {
                        if (this.mShowHideFrameAnimator.isRunning()) {
                            this.mShowHideFrameAnimator.reverse();
                        } else if (shown) {
                            this.mShowHideFrameAnimator.start();
                        } else {
                            this.mShowHideFrameAnimator.reverse();
                        }
                    } else {
                        this.mShowHideFrameAnimator.cancel();
                        if (shown) {
                            setAlpha(255);
                        } else {
                            setAlpha(0);
                        }
                    }
                }
            }

            public int getAlpha() {
                int i;
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    i = this.mAlpha;
                }
                return i;
            }

            public void setAlpha(int alpha) {
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    if (this.mAlpha == alpha) {
                        return;
                    }
                    this.mAlpha = alpha;
                    invalidate(null);
                }
            }

            public void setBounds(Region bounds) {
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    if (this.mBounds.equals(bounds)) {
                        return;
                    }
                    this.mBounds.set(bounds);
                    invalidate(this.mDirtyRect);
                }
            }

            public void updateSize() {
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    MagnifiedViewport.this.mWindowManager.getDefaultDisplay().getRealSize(MagnifiedViewport.this.mTempPoint);
                    this.mSurfaceControl.setSize(MagnifiedViewport.this.mTempPoint.x, MagnifiedViewport.this.mTempPoint.y);
                    invalidate(this.mDirtyRect);
                }
            }

            public void invalidate(Rect dirtyRect) {
                if (dirtyRect != null) {
                    this.mDirtyRect.set(dirtyRect);
                } else {
                    this.mDirtyRect.setEmpty();
                }
                this.mInvalidated = true;
                DisplayMagnifier.this.mWindowManagerService.scheduleAnimationLocked();
            }

            public void drawIfNeeded() {
                synchronized (DisplayMagnifier.this.mWindowManagerService.mWindowMap) {
                    if (this.mInvalidated) {
                        this.mInvalidated = false;
                        Canvas canvas = null;
                        try {
                            if (this.mDirtyRect.isEmpty()) {
                                this.mBounds.getBounds(this.mDirtyRect);
                            }
                            this.mDirtyRect.inset(-MagnifiedViewport.this.mHalfBorderWidth, -MagnifiedViewport.this.mHalfBorderWidth);
                            canvas = this.mSurface.lockCanvas(this.mDirtyRect);
                        } catch (Surface.OutOfResourcesException e) {
                        } catch (IllegalArgumentException e2) {
                        }
                        if (canvas == null) {
                            return;
                        }
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        this.mPaint.setAlpha(this.mAlpha);
                        Path path = this.mBounds.getBoundaryPath();
                        canvas.drawPath(path, this.mPaint);
                        this.mSurface.unlockCanvasAndPost(canvas);
                        if (this.mAlpha > 0) {
                            this.mSurfaceControl.show();
                        } else {
                            this.mSurfaceControl.hide();
                        }
                    }
                }
            }

            public void releaseSurface() {
                this.mSurfaceControl.release();
                this.mSurface.release();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayMagnifier$WindowStateInfo.class */
    public static final class WindowStateInfo {
        private static final int MAX_POOL_SIZE = 30;
        private static final Pools.SimplePool<WindowStateInfo> sPool = new Pools.SimplePool<>(30);
        private static final Region mTempRegion = new Region();
        public WindowState mWindowState;
        public final Rect mTouchableRegion = new Rect();

        private WindowStateInfo() {
        }

        public static WindowStateInfo obtain(WindowState windowState) {
            WindowStateInfo info = sPool.acquire();
            if (info == null) {
                info = new WindowStateInfo();
            }
            info.mWindowState = windowState;
            windowState.getTouchableRegion(mTempRegion);
            mTempRegion.getBounds(info.mTouchableRegion);
            return info;
        }

        public void recycle() {
            this.mWindowState = null;
            this.mTouchableRegion.setEmpty();
            sPool.release(this);
        }
    }

    /* loaded from: DisplayMagnifier$MyHandler.class */
    private class MyHandler extends Handler {
        public static final int MESSAGE_NOTIFY_MAGNIFIED_BOUNDS_CHANGED = 1;
        public static final int MESSAGE_NOTIFY_RECTANGLE_ON_SCREEN_REQUESTED = 2;
        public static final int MESSAGE_NOTIFY_USER_CONTEXT_CHANGED = 3;
        public static final int MESSAGE_NOTIFY_ROTATION_CHANGED = 4;
        public static final int MESSAGE_SHOW_MAGNIFIED_REGION_BOUNDS_IF_NEEDED = 5;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DisplayMagnifier.MyHandler.handleMessage(android.os.Message):void, file: DisplayMagnifier$MyHandler.class
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
        @Override // android.os.Handler
        public void handleMessage(android.os.Message r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DisplayMagnifier.MyHandler.handleMessage(android.os.Message):void, file: DisplayMagnifier$MyHandler.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayMagnifier.MyHandler.handleMessage(android.os.Message):void");
        }

        public MyHandler(Looper looper) {
            super(looper);
        }
    }
}