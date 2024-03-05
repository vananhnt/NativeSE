package android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemProperties;
import android.view.ActionMode;
import android.view.InputQueue;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import gov.nist.core.Separators;

/* loaded from: Window.class */
public abstract class Window {
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_PROGRESS = 2;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_MAX = 10;
    public static final int PROGRESS_VISIBILITY_ON = -1;
    public static final int PROGRESS_VISIBILITY_OFF = -2;
    public static final int PROGRESS_INDETERMINATE_ON = -3;
    public static final int PROGRESS_INDETERMINATE_OFF = -4;
    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_END = 10000;
    public static final int PROGRESS_SECONDARY_START = 20000;
    public static final int PROGRESS_SECONDARY_END = 30000;
    protected static final int DEFAULT_FEATURES = 65;
    public static final int ID_ANDROID_CONTENT = 16908290;
    private static final String PROPERTY_HARDWARE_UI = "persist.sys.ui.hw";
    private final Context mContext;
    private TypedArray mWindowStyle;
    private Callback mCallback;
    private WindowManager mWindowManager;
    private IBinder mAppToken;
    private String mAppName;
    private boolean mHardwareAccelerated;
    private Window mContainer;
    private Window mActiveChild;
    private boolean mDestroyed;
    private boolean mIsActive = false;
    private boolean mHasChildren = false;
    private boolean mCloseOnTouchOutside = false;
    private boolean mSetCloseOnTouchOutside = false;
    private int mForcedWindowFlags = 0;
    private int mFeatures = 65;
    private int mLocalFeatures = 65;
    private boolean mHaveWindowFormat = false;
    private boolean mHaveDimAmount = false;
    private int mDefaultWindowFormat = -1;
    private boolean mHasSoftInputMode = false;
    private final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();

    /* loaded from: Window$Callback.class */
    public interface Callback {
        boolean dispatchKeyEvent(KeyEvent keyEvent);

        boolean dispatchKeyShortcutEvent(KeyEvent keyEvent);

        boolean dispatchTouchEvent(MotionEvent motionEvent);

        boolean dispatchTrackballEvent(MotionEvent motionEvent);

        boolean dispatchGenericMotionEvent(MotionEvent motionEvent);

        boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        View onCreatePanelView(int i);

        boolean onCreatePanelMenu(int i, Menu menu);

        boolean onPreparePanel(int i, View view, Menu menu);

        boolean onMenuOpened(int i, Menu menu);

        boolean onMenuItemSelected(int i, MenuItem menuItem);

        void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams);

        void onContentChanged();

        void onWindowFocusChanged(boolean z);

        void onAttachedToWindow();

        void onDetachedFromWindow();

        void onPanelClosed(int i, Menu menu);

        boolean onSearchRequested();

        ActionMode onWindowStartingActionMode(ActionMode.Callback callback);

        void onActionModeStarted(ActionMode actionMode);

        void onActionModeFinished(ActionMode actionMode);
    }

    public abstract void takeSurface(SurfaceHolder.Callback2 callback2);

    public abstract void takeInputQueue(InputQueue.Callback callback);

    public abstract boolean isFloating();

    public abstract void alwaysReadCloseOnTouchAttr();

    public abstract void setContentView(int i);

    public abstract void setContentView(View view);

    public abstract void setContentView(View view, ViewGroup.LayoutParams layoutParams);

    public abstract void addContentView(View view, ViewGroup.LayoutParams layoutParams);

    public abstract View getCurrentFocus();

    public abstract LayoutInflater getLayoutInflater();

    public abstract void setTitle(CharSequence charSequence);

    public abstract void setTitleColor(int i);

    public abstract void openPanel(int i, KeyEvent keyEvent);

    public abstract void closePanel(int i);

    public abstract void togglePanel(int i, KeyEvent keyEvent);

    public abstract void invalidatePanelMenu(int i);

    public abstract boolean performPanelShortcut(int i, int i2, KeyEvent keyEvent, int i3);

    public abstract boolean performPanelIdentifierAction(int i, int i2, int i3);

    public abstract void closeAllPanels();

    public abstract boolean performContextMenuIdentifierAction(int i, int i2);

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void setBackgroundDrawable(Drawable drawable);

    public abstract void setFeatureDrawableResource(int i, int i2);

    public abstract void setFeatureDrawableUri(int i, Uri uri);

    public abstract void setFeatureDrawable(int i, Drawable drawable);

    public abstract void setFeatureDrawableAlpha(int i, int i2);

    public abstract void setFeatureInt(int i, int i2);

    public abstract void takeKeyEvents(boolean z);

    public abstract boolean superDispatchKeyEvent(KeyEvent keyEvent);

    public abstract boolean superDispatchKeyShortcutEvent(KeyEvent keyEvent);

    public abstract boolean superDispatchTouchEvent(MotionEvent motionEvent);

    public abstract boolean superDispatchTrackballEvent(MotionEvent motionEvent);

    public abstract boolean superDispatchGenericMotionEvent(MotionEvent motionEvent);

    public abstract View getDecorView();

    public abstract View peekDecorView();

    public abstract Bundle saveHierarchyState();

    public abstract void restoreHierarchyState(Bundle bundle);

    protected abstract void onActive();

    public abstract void setChildDrawable(int i, Drawable drawable);

    public abstract void setChildInt(int i, int i2);

    public abstract boolean isShortcutKey(int i, KeyEvent keyEvent);

    public abstract void setVolumeControlStream(int i);

    public abstract int getVolumeControlStream();

    public Window(Context context) {
        this.mContext = context;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final TypedArray getWindowStyle() {
        TypedArray typedArray;
        synchronized (this) {
            if (this.mWindowStyle == null) {
                this.mWindowStyle = this.mContext.obtainStyledAttributes(R.styleable.Window);
            }
            typedArray = this.mWindowStyle;
        }
        return typedArray;
    }

    public void setContainer(Window container) {
        this.mContainer = container;
        if (container != null) {
            this.mFeatures |= 2;
            this.mLocalFeatures |= 2;
            container.mHasChildren = true;
        }
    }

    public final Window getContainer() {
        return this.mContainer;
    }

    public final boolean hasChildren() {
        return this.mHasChildren;
    }

    public final void destroy() {
        this.mDestroyed = true;
    }

    public final boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void setWindowManager(WindowManager wm, IBinder appToken, String appName) {
        setWindowManager(wm, appToken, appName, false);
    }

    public void setWindowManager(WindowManager wm, IBinder appToken, String appName, boolean hardwareAccelerated) {
        this.mAppToken = appToken;
        this.mAppName = appName;
        this.mHardwareAccelerated = hardwareAccelerated || SystemProperties.getBoolean(PROPERTY_HARDWARE_UI, false);
        if (wm == null) {
            wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        this.mWindowManager = ((WindowManagerImpl) wm).createLocalWindowManager(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustLayoutParamsForSubWindow(WindowManager.LayoutParams wp) {
        String title;
        View decor;
        CharSequence curTitle = wp.getTitle();
        if (wp.type >= 1000 && wp.type <= 1999) {
            if (wp.token == null && (decor = peekDecorView()) != null) {
                wp.token = decor.getWindowToken();
            }
            if (curTitle == null || curTitle.length() == 0) {
                if (wp.type == 1001) {
                    title = "Media";
                } else if (wp.type == 1004) {
                    title = "MediaOvr";
                } else if (wp.type == 1000) {
                    title = "Panel";
                } else if (wp.type == 1002) {
                    title = "SubPanel";
                } else if (wp.type == 1003) {
                    title = "AtchDlg";
                } else {
                    title = Integer.toString(wp.type);
                }
                if (this.mAppName != null) {
                    title = title + Separators.COLON + this.mAppName;
                }
                wp.setTitle(title);
            }
        } else {
            if (wp.token == null) {
                wp.token = this.mContainer == null ? this.mAppToken : this.mContainer.mAppToken;
            }
            if ((curTitle == null || curTitle.length() == 0) && this.mAppName != null) {
                wp.setTitle(this.mAppName);
            }
        }
        if (wp.packageName == null) {
            wp.packageName = this.mContext.getPackageName();
        }
        if (this.mHardwareAccelerated) {
            wp.flags |= 16777216;
        }
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public final Callback getCallback() {
        return this.mCallback;
    }

    public void setLayout(int width, int height) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.width = width;
        attrs.height = height;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setGravity(int gravity) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.gravity = gravity;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setType(int type) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.type = type;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setFormat(int format) {
        WindowManager.LayoutParams attrs = getAttributes();
        if (format != 0) {
            attrs.format = format;
            this.mHaveWindowFormat = true;
        } else {
            attrs.format = this.mDefaultWindowFormat;
            this.mHaveWindowFormat = false;
        }
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setWindowAnimations(int resId) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.windowAnimations = resId;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setSoftInputMode(int mode) {
        WindowManager.LayoutParams attrs = getAttributes();
        if (mode != 0) {
            attrs.softInputMode = mode;
            this.mHasSoftInputMode = true;
        } else {
            this.mHasSoftInputMode = false;
        }
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void addFlags(int flags) {
        setFlags(flags, flags);
    }

    public void addPrivateFlags(int flags) {
        setPrivateFlags(flags, flags);
    }

    public void clearFlags(int flags) {
        setFlags(0, flags);
    }

    public void setFlags(int flags, int mask) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.flags = (attrs.flags & (mask ^ (-1))) | (flags & mask);
        if ((mask & 1073741824) != 0) {
            attrs.privateFlags |= 8;
        }
        this.mForcedWindowFlags |= mask;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    private void setPrivateFlags(int flags, int mask) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.privateFlags = (attrs.privateFlags & (mask ^ (-1))) | (flags & mask);
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setDimAmount(float amount) {
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.dimAmount = amount;
        this.mHaveDimAmount = true;
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(attrs);
        }
    }

    public void setAttributes(WindowManager.LayoutParams a) {
        this.mWindowAttributes.copyFrom(a);
        if (this.mCallback != null) {
            this.mCallback.onWindowAttributesChanged(this.mWindowAttributes);
        }
    }

    public final WindowManager.LayoutParams getAttributes() {
        return this.mWindowAttributes;
    }

    protected final int getForcedWindowFlags() {
        return this.mForcedWindowFlags;
    }

    protected final boolean hasSoftInputMode() {
        return this.mHasSoftInputMode;
    }

    public void setCloseOnTouchOutside(boolean close) {
        this.mCloseOnTouchOutside = close;
        this.mSetCloseOnTouchOutside = true;
    }

    public void setCloseOnTouchOutsideIfNotSet(boolean close) {
        if (!this.mSetCloseOnTouchOutside) {
            this.mCloseOnTouchOutside = close;
            this.mSetCloseOnTouchOutside = true;
        }
    }

    public boolean shouldCloseOnTouch(Context context, MotionEvent event) {
        if (this.mCloseOnTouchOutside && event.getAction() == 0 && isOutOfBounds(context, event) && peekDecorView() != null) {
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        View decorView = getDecorView();
        return x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop;
    }

    public boolean requestFeature(int featureId) {
        int flag = 1 << featureId;
        this.mFeatures |= flag;
        this.mLocalFeatures |= this.mContainer != null ? flag & (this.mContainer.mFeatures ^ (-1)) : flag;
        return (this.mFeatures & flag) != 0;
    }

    protected void removeFeature(int featureId) {
        int flag = 1 << featureId;
        this.mFeatures &= flag ^ (-1);
        this.mLocalFeatures &= (this.mContainer != null ? flag & (this.mContainer.mFeatures ^ (-1)) : flag) ^ (-1);
    }

    public final void makeActive() {
        if (this.mContainer != null) {
            if (this.mContainer.mActiveChild != null) {
                this.mContainer.mActiveChild.mIsActive = false;
            }
            this.mContainer.mActiveChild = this;
        }
        this.mIsActive = true;
        onActive();
    }

    public final boolean isActive() {
        return this.mIsActive;
    }

    public View findViewById(int id) {
        return getDecorView().findViewById(id);
    }

    public void setBackgroundDrawableResource(int resid) {
        setBackgroundDrawable(this.mContext.getResources().getDrawable(resid));
    }

    protected final int getFeatures() {
        return this.mFeatures;
    }

    public boolean hasFeature(int feature) {
        return (getFeatures() & (1 << feature)) != 0;
    }

    protected final int getLocalFeatures() {
        return this.mLocalFeatures;
    }

    protected void setDefaultWindowFormat(int format) {
        this.mDefaultWindowFormat = format;
        if (!this.mHaveWindowFormat) {
            WindowManager.LayoutParams attrs = getAttributes();
            attrs.format = format;
            if (this.mCallback != null) {
                this.mCallback.onWindowAttributesChanged(attrs);
            }
        }
    }

    protected boolean haveDimAmount() {
        return this.mHaveDimAmount;
    }

    public void setUiOptions(int uiOptions) {
    }

    public void setUiOptions(int uiOptions, int mask) {
    }

    public void setIcon(int resId) {
    }

    public void setDefaultIcon(int resId) {
    }

    public void setLogo(int resId) {
    }

    public void setDefaultLogo(int resId) {
    }

    public void setLocalFocus(boolean hasFocus, boolean inTouchMode) {
    }

    public void injectInputEvent(InputEvent event) {
    }
}