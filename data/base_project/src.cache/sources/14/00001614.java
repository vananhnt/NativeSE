package android.view;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.InputEventReceiver;
import android.view.WindowManager;
import android.view.animation.Animation;
import java.io.PrintWriter;

/* loaded from: WindowManagerPolicy.class */
public interface WindowManagerPolicy {
    public static final int FLAG_WAKE = 1;
    public static final int FLAG_WAKE_DROPPED = 2;
    public static final int FLAG_SHIFT = 4;
    public static final int FLAG_CAPS_LOCK = 8;
    public static final int FLAG_ALT = 16;
    public static final int FLAG_ALT_GR = 32;
    public static final int FLAG_MENU = 64;
    public static final int FLAG_LAUNCHER = 128;
    public static final int FLAG_VIRTUAL = 256;
    public static final int FLAG_INJECTED = 16777216;
    public static final int FLAG_TRUSTED = 33554432;
    public static final int FLAG_FILTERED = 67108864;
    public static final int FLAG_DISABLE_KEY_REPEAT = 134217728;
    public static final int FLAG_WOKE_HERE = 268435456;
    public static final int FLAG_BRIGHT_HERE = 536870912;
    public static final int FLAG_PASS_TO_USER = 1073741824;
    public static final int PRESENCE_INTERNAL = 1;
    public static final int PRESENCE_EXTERNAL = 2;
    public static final boolean WATCH_POINTER = false;
    public static final String ACTION_HDMI_PLUGGED = "android.intent.action.HDMI_PLUGGED";
    public static final String EXTRA_HDMI_PLUGGED_STATE = "state";
    public static final int ACTION_PASS_TO_USER = 1;
    public static final int ACTION_WAKE_UP = 2;
    public static final int ACTION_GO_TO_SLEEP = 4;
    public static final int TRANSIT_ENTER = 1;
    public static final int TRANSIT_EXIT = 2;
    public static final int TRANSIT_SHOW = 3;
    public static final int TRANSIT_HIDE = 4;
    public static final int TRANSIT_PREVIEW_DONE = 5;
    public static final int OFF_BECAUSE_OF_ADMIN = 1;
    public static final int OFF_BECAUSE_OF_USER = 2;
    public static final int OFF_BECAUSE_OF_TIMEOUT = 3;
    public static final int OFF_BECAUSE_OF_PROX_SENSOR = 4;
    public static final int USER_ROTATION_FREE = 0;
    public static final int USER_ROTATION_LOCKED = 1;
    public static final int FINISH_LAYOUT_REDO_LAYOUT = 1;
    public static final int FINISH_LAYOUT_REDO_CONFIG = 2;
    public static final int FINISH_LAYOUT_REDO_WALLPAPER = 4;
    public static final int FINISH_LAYOUT_REDO_ANIM = 8;

    /* loaded from: WindowManagerPolicy$FakeWindow.class */
    public interface FakeWindow {
        void dismiss();
    }

    /* loaded from: WindowManagerPolicy$OnKeyguardExitResult.class */
    public interface OnKeyguardExitResult {
        void onKeyguardExitResult(boolean z);
    }

    /* loaded from: WindowManagerPolicy$PointerEventListener.class */
    public interface PointerEventListener {
        void onPointerEvent(MotionEvent motionEvent);
    }

    /* loaded from: WindowManagerPolicy$ScreenOnListener.class */
    public interface ScreenOnListener {
        void onScreenOn();
    }

    /* loaded from: WindowManagerPolicy$WindowManagerFuncs.class */
    public interface WindowManagerFuncs {
        public static final int LID_ABSENT = -1;
        public static final int LID_CLOSED = 0;
        public static final int LID_OPEN = 1;

        void reevaluateStatusBarVisibility();

        FakeWindow addFakeWindow(Looper looper, InputEventReceiver.Factory factory, String str, int i, int i2, int i3, boolean z, boolean z2, boolean z3);

        int getLidState();

        void switchKeyboardLayout(int i, int i2);

        void shutdown(boolean z);

        void rebootSafeMode(boolean z);

        Object getWindowManagerLock();

        void registerPointerEventListener(PointerEventListener pointerEventListener);

        void unregisterPointerEventListener(PointerEventListener pointerEventListener);
    }

    /* loaded from: WindowManagerPolicy$WindowState.class */
    public interface WindowState {
        int getOwningUid();

        String getOwningPackage();

        void computeFrameLw(Rect rect, Rect rect2, Rect rect3, Rect rect4, Rect rect5, Rect rect6);

        Rect getFrameLw();

        RectF getShownFrameLw();

        Rect getDisplayFrameLw();

        Rect getOverscanFrameLw();

        Rect getContentFrameLw();

        Rect getVisibleFrameLw();

        boolean getGivenInsetsPendingLw();

        Rect getGivenContentInsetsLw();

        Rect getGivenVisibleInsetsLw();

        WindowManager.LayoutParams getAttrs();

        boolean getNeedsMenuLw(WindowState windowState);

        int getSystemUiVisibility();

        int getSurfaceLayer();

        IApplicationToken getAppToken();

        boolean hasAppShownWindows();

        boolean isVisibleLw();

        boolean isVisibleOrBehindKeyguardLw();

        boolean isDisplayedLw();

        boolean isAnimatingLw();

        boolean isGoneForLayoutLw();

        boolean hasDrawnLw();

        boolean hideLw(boolean z);

        boolean showLw(boolean z);

        boolean isAlive();

        boolean isDefaultDisplay();
    }

    void init(Context context, IWindowManager iWindowManager, WindowManagerFuncs windowManagerFuncs);

    boolean isDefaultOrientationForced();

    void setInitialDisplaySize(Display display, int i, int i2, int i3);

    void setDisplayOverscan(Display display, int i, int i2, int i3, int i4);

    int checkAddPermission(WindowManager.LayoutParams layoutParams, int[] iArr);

    boolean checkShowToOwnerOnly(WindowManager.LayoutParams layoutParams);

    void adjustWindowParamsLw(WindowManager.LayoutParams layoutParams);

    void adjustConfigurationLw(Configuration configuration, int i, int i2);

    int windowTypeToLayerLw(int i);

    int subWindowTypeToLayerLw(int i);

    int getMaxWallpaperLayer();

    int getAboveUniverseLayer();

    int getNonDecorDisplayWidth(int i, int i2, int i3);

    int getNonDecorDisplayHeight(int i, int i2, int i3);

    int getConfigDisplayWidth(int i, int i2, int i3);

    int getConfigDisplayHeight(int i, int i2, int i3);

    boolean doesForceHide(WindowState windowState, WindowManager.LayoutParams layoutParams);

    boolean canBeForceHidden(WindowState windowState, WindowManager.LayoutParams layoutParams);

    View addStartingWindow(IBinder iBinder, String str, int i, CompatibilityInfo compatibilityInfo, CharSequence charSequence, int i2, int i3, int i4, int i5);

    void removeStartingWindow(IBinder iBinder, View view);

    int prepareAddWindowLw(WindowState windowState, WindowManager.LayoutParams layoutParams);

    void removeWindowLw(WindowState windowState);

    int selectAnimationLw(WindowState windowState, int i);

    void selectRotationAnimationLw(int[] iArr);

    boolean validateRotationAnimationLw(int i, int i2, boolean z);

    Animation createForceHideEnterAnimation(boolean z);

    int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i, boolean z);

    int interceptMotionBeforeQueueingWhenScreenOff(int i);

    long interceptKeyBeforeDispatching(WindowState windowState, KeyEvent keyEvent, int i);

    KeyEvent dispatchUnhandledKey(WindowState windowState, KeyEvent keyEvent, int i);

    void beginLayoutLw(boolean z, int i, int i2, int i3);

    int getSystemDecorLayerLw();

    void getContentRectLw(Rect rect);

    void layoutWindowLw(WindowState windowState, WindowManager.LayoutParams layoutParams, WindowState windowState2);

    void getContentInsetHintLw(WindowManager.LayoutParams layoutParams, Rect rect);

    void finishLayoutLw();

    void beginPostLayoutPolicyLw(int i, int i2);

    void applyPostLayoutPolicyLw(WindowState windowState, WindowManager.LayoutParams layoutParams);

    int finishPostLayoutPolicyLw();

    boolean allowAppAnimationsLw();

    int focusChangedLw(WindowState windowState, WindowState windowState2);

    void screenTurnedOff(int i);

    void screenTurningOn(ScreenOnListener screenOnListener);

    boolean isScreenOnEarly();

    boolean isScreenOnFully();

    void notifyLidSwitchChanged(long j, boolean z);

    void enableKeyguard(boolean z);

    void exitKeyguardSecurely(OnKeyguardExitResult onKeyguardExitResult);

    boolean isKeyguardLocked();

    boolean isKeyguardSecure();

    boolean inKeyguardRestrictedKeyInputMode();

    void dismissKeyguardLw();

    int rotationForOrientationLw(int i, int i2);

    boolean rotationHasCompatibleMetricsLw(int i, int i2);

    void setRotationLw(int i);

    void setSafeMode(boolean z);

    void systemReady();

    void systemBooted();

    void showBootMessage(CharSequence charSequence, boolean z);

    void hideBootMessages();

    void userActivity();

    void enableScreenAfterBoot();

    void setCurrentOrientationLw(int i);

    boolean performHapticFeedbackLw(WindowState windowState, int i, boolean z);

    void keepScreenOnStartedLw();

    void keepScreenOnStoppedLw();

    int getUserRotationMode();

    void setUserRotationMode(int i, int i2);

    int adjustSystemUiVisibilityLw(int i);

    boolean hasNavigationBar();

    void lockNow(Bundle bundle);

    void setLastInputMethodWindowLw(WindowState windowState, WindowState windowState2);

    void setCurrentUserLw(int i);

    void dump(String str, PrintWriter printWriter, String[] strArr);

    boolean canMagnifyWindow(int i);

    boolean isTopLevelWindow(int i);

    void setTouchExplorationEnabled(boolean z);
}