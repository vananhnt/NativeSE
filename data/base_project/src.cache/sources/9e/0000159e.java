package android.view;

import android.app.AppGlobals;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import com.android.internal.R;

/* loaded from: ViewConfiguration.class */
public class ViewConfiguration {
    private static final int SCROLL_BAR_SIZE = 10;
    private static final int SCROLL_BAR_FADE_DURATION = 250;
    private static final int SCROLL_BAR_DEFAULT_DELAY = 300;
    private static final int FADING_EDGE_LENGTH = 12;
    private static final int PRESSED_STATE_DURATION = 64;
    private static final int DEFAULT_LONG_PRESS_TIMEOUT = 500;
    private static final int KEY_REPEAT_DELAY = 50;
    private static final int GLOBAL_ACTIONS_KEY_TIMEOUT = 500;
    private static final int TAP_TIMEOUT = 180;
    private static final int JUMP_TAP_TIMEOUT = 500;
    private static final int DOUBLE_TAP_TIMEOUT = 300;
    private static final int DOUBLE_TAP_MIN_TIME = 40;
    private static final int HOVER_TAP_TIMEOUT = 150;
    private static final int HOVER_TAP_SLOP = 20;
    private static final int ZOOM_CONTROLS_TIMEOUT = 3000;
    private static final int EDGE_SLOP = 12;
    private static final int TOUCH_SLOP = 8;
    private static final int DOUBLE_TAP_TOUCH_SLOP = 8;
    private static final int PAGING_TOUCH_SLOP = 16;
    private static final int DOUBLE_TAP_SLOP = 100;
    private static final int WINDOW_TOUCH_SLOP = 16;
    private static final int MINIMUM_FLING_VELOCITY = 50;
    private static final int MAXIMUM_FLING_VELOCITY = 8000;
    private static final long SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS = 100;
    @Deprecated
    private static final int MAXIMUM_DRAWING_CACHE_SIZE = 1536000;
    private static final float SCROLL_FRICTION = 0.015f;
    private static final int OVERSCROLL_DISTANCE = 0;
    private static final int OVERFLING_DISTANCE = 6;
    private final int mEdgeSlop;
    private final int mFadingEdgeLength;
    private final int mMinimumFlingVelocity;
    private final int mMaximumFlingVelocity;
    private final int mScrollbarSize;
    private final int mTouchSlop;
    private final int mDoubleTapTouchSlop;
    private final int mPagingTouchSlop;
    private final int mDoubleTapSlop;
    private final int mWindowTouchSlop;
    private final int mMaximumDrawingCacheSize;
    private final int mOverscrollDistance;
    private final int mOverflingDistance;
    private final boolean mFadingMarqueeEnabled;
    private boolean sHasPermanentMenuKey;
    private boolean sHasPermanentMenuKeySet;
    static final SparseArray<ViewConfiguration> sConfigurations = new SparseArray<>(2);

    @Deprecated
    public ViewConfiguration() {
        this.mEdgeSlop = 12;
        this.mFadingEdgeLength = 12;
        this.mMinimumFlingVelocity = 50;
        this.mMaximumFlingVelocity = 8000;
        this.mScrollbarSize = 10;
        this.mTouchSlop = 8;
        this.mDoubleTapTouchSlop = 8;
        this.mPagingTouchSlop = 16;
        this.mDoubleTapSlop = 100;
        this.mWindowTouchSlop = 16;
        this.mMaximumDrawingCacheSize = MAXIMUM_DRAWING_CACHE_SIZE;
        this.mOverscrollDistance = 0;
        this.mOverflingDistance = 6;
        this.mFadingMarqueeEnabled = true;
    }

    private ViewConfiguration(Context context) {
        float sizeAndDensity;
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        float density = metrics.density;
        if (config.isLayoutSizeAtLeast(4)) {
            sizeAndDensity = density * 1.5f;
        } else {
            sizeAndDensity = density;
        }
        this.mEdgeSlop = (int) ((sizeAndDensity * 12.0f) + 0.5f);
        this.mFadingEdgeLength = (int) ((sizeAndDensity * 12.0f) + 0.5f);
        this.mMinimumFlingVelocity = (int) ((density * 50.0f) + 0.5f);
        this.mMaximumFlingVelocity = (int) ((density * 8000.0f) + 0.5f);
        this.mScrollbarSize = (int) ((density * 10.0f) + 0.5f);
        this.mDoubleTapSlop = (int) ((sizeAndDensity * 100.0f) + 0.5f);
        this.mWindowTouchSlop = (int) ((sizeAndDensity * 16.0f) + 0.5f);
        WindowManager win = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = win.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        this.mMaximumDrawingCacheSize = 4 * size.x * size.y;
        this.mOverscrollDistance = (int) ((sizeAndDensity * 0.0f) + 0.5f);
        this.mOverflingDistance = (int) ((sizeAndDensity * 6.0f) + 0.5f);
        if (!this.sHasPermanentMenuKeySet) {
            IWindowManager wm = WindowManagerGlobal.getWindowManagerService();
            try {
                this.sHasPermanentMenuKey = !wm.hasNavigationBar();
                this.sHasPermanentMenuKeySet = true;
            } catch (RemoteException e) {
                this.sHasPermanentMenuKey = false;
            }
        }
        this.mFadingMarqueeEnabled = res.getBoolean(R.bool.config_ui_enableFadingMarquee);
        this.mTouchSlop = res.getDimensionPixelSize(R.dimen.config_viewConfigurationTouchSlop);
        this.mPagingTouchSlop = this.mTouchSlop * 2;
        this.mDoubleTapTouchSlop = this.mTouchSlop;
    }

    public static ViewConfiguration get(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int density = (int) (100.0f * metrics.density);
        ViewConfiguration configuration = sConfigurations.get(density);
        if (configuration == null) {
            configuration = new ViewConfiguration(context);
            sConfigurations.put(density, configuration);
        }
        return configuration;
    }

    @Deprecated
    public static int getScrollBarSize() {
        return 10;
    }

    public int getScaledScrollBarSize() {
        return this.mScrollbarSize;
    }

    public static int getScrollBarFadeDuration() {
        return 250;
    }

    public static int getScrollDefaultDelay() {
        return 300;
    }

    @Deprecated
    public static int getFadingEdgeLength() {
        return 12;
    }

    public int getScaledFadingEdgeLength() {
        return this.mFadingEdgeLength;
    }

    public static int getPressedStateDuration() {
        return 64;
    }

    public static int getLongPressTimeout() {
        return AppGlobals.getIntCoreSetting(Settings.Secure.LONG_PRESS_TIMEOUT, 500);
    }

    public static int getKeyRepeatTimeout() {
        return getLongPressTimeout();
    }

    public static int getKeyRepeatDelay() {
        return 50;
    }

    public static int getTapTimeout() {
        return 180;
    }

    public static int getJumpTapTimeout() {
        return 500;
    }

    public static int getDoubleTapTimeout() {
        return 300;
    }

    public static int getDoubleTapMinTime() {
        return 40;
    }

    public static int getHoverTapTimeout() {
        return 150;
    }

    public static int getHoverTapSlop() {
        return 20;
    }

    @Deprecated
    public static int getEdgeSlop() {
        return 12;
    }

    public int getScaledEdgeSlop() {
        return this.mEdgeSlop;
    }

    @Deprecated
    public static int getTouchSlop() {
        return 8;
    }

    public int getScaledTouchSlop() {
        return this.mTouchSlop;
    }

    public int getScaledDoubleTapTouchSlop() {
        return this.mDoubleTapTouchSlop;
    }

    public int getScaledPagingTouchSlop() {
        return this.mPagingTouchSlop;
    }

    @Deprecated
    public static int getDoubleTapSlop() {
        return 100;
    }

    public int getScaledDoubleTapSlop() {
        return this.mDoubleTapSlop;
    }

    public static long getSendRecurringAccessibilityEventsInterval() {
        return SEND_RECURRING_ACCESSIBILITY_EVENTS_INTERVAL_MILLIS;
    }

    @Deprecated
    public static int getWindowTouchSlop() {
        return 16;
    }

    public int getScaledWindowTouchSlop() {
        return this.mWindowTouchSlop;
    }

    @Deprecated
    public static int getMinimumFlingVelocity() {
        return 50;
    }

    public int getScaledMinimumFlingVelocity() {
        return this.mMinimumFlingVelocity;
    }

    @Deprecated
    public static int getMaximumFlingVelocity() {
        return 8000;
    }

    public int getScaledMaximumFlingVelocity() {
        return this.mMaximumFlingVelocity;
    }

    @Deprecated
    public static int getMaximumDrawingCacheSize() {
        return MAXIMUM_DRAWING_CACHE_SIZE;
    }

    public int getScaledMaximumDrawingCacheSize() {
        return this.mMaximumDrawingCacheSize;
    }

    public int getScaledOverscrollDistance() {
        return this.mOverscrollDistance;
    }

    public int getScaledOverflingDistance() {
        return this.mOverflingDistance;
    }

    public static long getZoomControlsTimeout() {
        return 3000L;
    }

    public static long getGlobalActionKeyTimeout() {
        return 500L;
    }

    public static float getScrollFriction() {
        return SCROLL_FRICTION;
    }

    public boolean hasPermanentMenuKey() {
        return this.sHasPermanentMenuKey;
    }

    public boolean isFadingMarqueeEnabled() {
        return this.mFadingMarqueeEnabled;
    }
}