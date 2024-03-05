package android.service.dreams;

import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamService;
import android.util.Slog;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import com.android.internal.policy.PolicyManager;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: DreamService.class */
public class DreamService extends Service implements Window.Callback {
    public static final String DREAM_SERVICE = "dreams";
    public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";
    public static final String DREAM_META_DATA = "android.service.dream";
    private IBinder mWindowToken;
    private Window mWindow;
    private WindowManager mWindowManager;
    private IDreamManager mSandman;
    private boolean mFinished;
    private final String TAG = DreamService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
    private final Handler mHandler = new Handler();
    private boolean mInteractive = false;
    private boolean mLowProfile = true;
    private boolean mFullscreen = false;
    private boolean mScreenBright = true;
    private boolean mDebug = false;

    public void setDebug(boolean dbg) {
        this.mDebug = dbg;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on keyEvent");
            }
            safelyFinish();
            return true;
        } else if (event.getKeyCode() == 4) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on back key");
            }
            safelyFinish();
            return true;
        } else {
            return this.mWindow.superDispatchKeyEvent(event);
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on keyShortcutEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchKeyShortcutEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on touchEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchTouchEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on trackballEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchTrackballEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.v(this.TAG, "Finishing on genericMotionEvent");
            }
            safelyFinish();
            return true;
        }
        return this.mWindow.superDispatchGenericMotionEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.view.Window.Callback
    public void onDetachedFromWindow() {
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    public void setContentView(int layoutResID) {
        getWindow().setContentView(layoutResID);
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().setContentView(view, params);
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().addContentView(view, params);
    }

    public View findViewById(int id) {
        return getWindow().findViewById(id);
    }

    public void setInteractive(boolean interactive) {
        this.mInteractive = interactive;
    }

    public boolean isInteractive() {
        return this.mInteractive;
    }

    public void setLowProfile(boolean lowProfile) {
        this.mLowProfile = lowProfile;
        applySystemUiVisibilityFlags(this.mLowProfile ? 1 : 0, 1);
    }

    public boolean isLowProfile() {
        return getSystemUiVisibilityFlagValue(1, this.mLowProfile);
    }

    public void setFullscreen(boolean fullscreen) {
        this.mFullscreen = fullscreen;
        applyWindowFlags(this.mFullscreen ? 1024 : 0, 1024);
    }

    public boolean isFullscreen() {
        return this.mFullscreen;
    }

    public void setScreenBright(boolean screenBright) {
        this.mScreenBright = screenBright;
        applyWindowFlags(this.mScreenBright ? 128 : 0, 128);
    }

    public boolean isScreenBright() {
        return getWindowFlagValue(128, this.mScreenBright);
    }

    @Override // android.app.Service
    public void onCreate() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onCreate() on thread " + Thread.currentThread().getId());
        }
        super.onCreate();
    }

    public void onDreamingStarted() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStarted()");
        }
    }

    public void onDreamingStopped() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDreamingStopped()");
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mDebug) {
            Slog.v(this.TAG, "onBind() intent = " + intent);
        }
        return new DreamServiceWrapper();
    }

    public final void finish() {
        if (this.mDebug) {
            Slog.v(this.TAG, "finish()");
        }
        finishInternal();
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.mDebug) {
            Slog.v(this.TAG, "onDestroy()");
        }
        detach();
        super.onDestroy();
    }

    private void loadSandman() {
        this.mSandman = IDreamManager.Stub.asInterface(ServiceManager.getService(DREAM_SERVICE));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void detach() {
        if (this.mWindow == null) {
            return;
        }
        try {
            onDreamingStopped();
        } catch (Throwable t) {
            Slog.w(this.TAG, "Crashed in onDreamingStopped()", t);
        }
        if (this.mDebug) {
            Slog.v(this.TAG, "detach(): Removing window from window manager");
        }
        try {
            this.mWindowManager.removeViewImmediate(this.mWindow.getDecorView());
            WindowManagerGlobal.getInstance().closeAll(this.mWindowToken, getClass().getName(), "Dream");
        } catch (Throwable t2) {
            Slog.w(this.TAG, "Crashed removing window view", t2);
        }
        this.mWindow = null;
        this.mWindowToken = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void attach(IBinder windowToken) {
        if (this.mWindowToken != null) {
            Slog.e(this.TAG, "attach() called when already attached with token=" + this.mWindowToken);
            return;
        }
        if (this.mDebug) {
            Slog.v(this.TAG, "Attached on thread " + Thread.currentThread().getId());
        }
        if (this.mSandman == null) {
            loadSandman();
        }
        this.mWindowToken = windowToken;
        this.mWindow = PolicyManager.makeNewWindow(this);
        this.mWindow.setCallback(this);
        this.mWindow.requestFeature(1);
        this.mWindow.setBackgroundDrawable(new ColorDrawable(-16777216));
        this.mWindow.setFormat(-1);
        if (this.mDebug) {
            Slog.v(this.TAG, String.format("Attaching window token: %s to window of type %s", windowToken, Integer.valueOf((int) WindowManager.LayoutParams.TYPE_DREAM)));
        }
        WindowManager.LayoutParams lp = this.mWindow.getAttributes();
        lp.type = WindowManager.LayoutParams.TYPE_DREAM;
        lp.token = windowToken;
        lp.windowAnimations = R.style.Animation_Dream;
        lp.flags |= 4784385 | (this.mFullscreen ? 1024 : 0) | (this.mScreenBright ? 128 : 0);
        this.mWindow.setAttributes(lp);
        if (this.mDebug) {
            Slog.v(this.TAG, "Created and attached window: " + this.mWindow);
        }
        this.mWindow.setWindowManager(null, windowToken, "dream", true);
        this.mWindowManager = this.mWindow.getWindowManager();
        if (this.mDebug) {
            Slog.v(this.TAG, "Window added on thread " + Thread.currentThread().getId());
        }
        try {
            applySystemUiVisibilityFlags(this.mLowProfile ? 1 : 0, 1);
            getWindowManager().addView(this.mWindow.getDecorView(), this.mWindow.getAttributes());
            this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        DreamService.this.onDreamingStarted();
                    } catch (Throwable t) {
                        Slog.w(DreamService.this.TAG, "Crashed in onDreamingStarted()", t);
                        DreamService.this.safelyFinish();
                    }
                }
            });
        } catch (Throwable t) {
            Slog.w(this.TAG, "Crashed adding window view", t);
            safelyFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void safelyFinish() {
        if (this.mDebug) {
            Slog.v(this.TAG, "safelyFinish()");
        }
        try {
            finish();
            if (!this.mFinished) {
                Slog.w(this.TAG, "Bad dream, did not call super.finish()");
                finishInternal();
            }
        } catch (Throwable t) {
            Slog.w(this.TAG, "Crashed in safelyFinish()", t);
            finishInternal();
        }
    }

    private void finishInternal() {
        if (this.mDebug) {
            Slog.v(this.TAG, "finishInternal() mFinished = " + this.mFinished);
        }
        if (this.mFinished) {
            return;
        }
        try {
            this.mFinished = true;
            if (this.mSandman != null) {
                this.mSandman.finishSelf(this.mWindowToken);
            } else {
                Slog.w(this.TAG, "No dream manager found");
            }
            stopSelf();
        } catch (Throwable t) {
            Slog.w(this.TAG, "Crashed in finishInternal()", t);
        }
    }

    private boolean getWindowFlagValue(int flag, boolean defaultValue) {
        return this.mWindow == null ? defaultValue : (this.mWindow.getAttributes().flags & flag) != 0;
    }

    private void applyWindowFlags(int flags, int mask) {
        if (this.mWindow != null) {
            WindowManager.LayoutParams lp = this.mWindow.getAttributes();
            lp.flags = applyFlags(lp.flags, flags, mask);
            this.mWindow.setAttributes(lp);
            this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), lp);
        }
    }

    private boolean getSystemUiVisibilityFlagValue(int flag, boolean defaultValue) {
        View v = this.mWindow == null ? null : this.mWindow.getDecorView();
        return v == null ? defaultValue : (v.getSystemUiVisibility() & flag) != 0;
    }

    private void applySystemUiVisibilityFlags(int flags, int mask) {
        View v = this.mWindow == null ? null : this.mWindow.getDecorView();
        if (v != null) {
            v.setSystemUiVisibility(applyFlags(v.getSystemUiVisibility(), flags, mask));
        }
    }

    private int applyFlags(int oldFlags, int flags, int mask) {
        return (oldFlags & (mask ^ (-1))) | (flags & mask);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.print(this.TAG + ": ");
        if (this.mWindowToken == null) {
            pw.println("stopped");
        } else {
            pw.println("running (token=" + this.mWindowToken + Separators.RPAREN);
        }
        pw.println("  window: " + this.mWindow);
        pw.print("  flags:");
        if (isInteractive()) {
            pw.print(" interactive");
        }
        if (isLowProfile()) {
            pw.print(" lowprofile");
        }
        if (isFullscreen()) {
            pw.print(" fullscreen");
        }
        if (isScreenBright()) {
            pw.print(" bright");
        }
        pw.println();
    }

    /* loaded from: DreamService$DreamServiceWrapper.class */
    private class DreamServiceWrapper extends IDreamService.Stub {
        private DreamServiceWrapper() {
        }

        @Override // android.service.dreams.IDreamService
        public void attach(final IBinder windowToken) {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.attach(windowToken);
                }
            });
        }

        @Override // android.service.dreams.IDreamService
        public void detach() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService.DreamServiceWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    DreamService.this.detach();
                }
            });
        }
    }
}