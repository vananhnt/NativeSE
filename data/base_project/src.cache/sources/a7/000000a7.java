package android.app;

import android.app.Instrumentation;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.UserHandle;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.webkit.WebView;
import com.android.internal.R;
import com.android.internal.app.ActionBarImpl;
import com.android.internal.policy.PolicyManager;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: Activity.class */
public class Activity extends ContextThemeWrapper implements LayoutInflater.Factory2, Window.Callback, KeyEvent.Callback, View.OnCreateContextMenuListener, ComponentCallbacks2 {
    private static final String TAG = "Activity";
    private static final boolean DEBUG_LIFECYCLE = false;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;
    public static final int RESULT_FIRST_USER = 1;
    static final String FRAGMENTS_TAG = "android:fragments";
    private static final String WINDOW_HIERARCHY_TAG = "android:viewHierarchyState";
    private static final String SAVED_DIALOG_IDS_KEY = "android:savedDialogIds";
    private static final String SAVED_DIALOGS_TAG = "android:savedDialogs";
    private static final String SAVED_DIALOG_KEY_PREFIX = "android:dialog_";
    private static final String SAVED_DIALOG_ARGS_KEY_PREFIX = "android:dialog_args_";
    private SparseArray<ManagedDialog> mManagedDialogs;
    private Instrumentation mInstrumentation;
    private IBinder mToken;
    private int mIdent;
    String mEmbeddedID;
    private Application mApplication;
    Intent mIntent;
    private ComponentName mComponent;
    ActivityInfo mActivityInfo;
    ActivityThread mMainThread;
    Activity mParent;
    boolean mCalled;
    boolean mCheckedForLoaderManager;
    boolean mLoadersStarted;
    boolean mResumed;
    private boolean mStopped;
    boolean mFinished;
    boolean mStartedActivity;
    private boolean mDestroyed;
    int mConfigChangeFlags;
    Configuration mCurrentConfig;
    private SearchManager mSearchManager;
    private MenuInflater mMenuInflater;
    NonConfigurationInstances mLastNonConfigurationInstances;
    private Window mWindow;
    private WindowManager mWindowManager;
    private boolean mEnableDefaultActionBarUp;
    private CharSequence mTitle;
    ArrayMap<String, LoaderManagerImpl> mAllLoaderManagers;
    LoaderManagerImpl mLoaderManager;
    private TranslucentConversionListener mTranslucentCallback;
    private boolean mChangeCanvasToTranslucent;
    protected static final int[] FOCUSED_STATE_SET = {16842908};
    private Thread mUiThread;
    public static final int DEFAULT_KEYS_DISABLE = 0;
    public static final int DEFAULT_KEYS_DIALER = 1;
    public static final int DEFAULT_KEYS_SHORTCUT = 2;
    public static final int DEFAULT_KEYS_SEARCH_LOCAL = 3;
    public static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;
    private boolean mDoReportFullyDrawn = true;
    boolean mTemporaryPause = false;
    boolean mChangingConfigurations = false;
    View mDecor = null;
    boolean mWindowAdded = false;
    boolean mVisibleFromServer = false;
    boolean mVisibleFromClient = true;
    ActionBarImpl mActionBar = null;
    private int mTitleColor = 0;
    final FragmentManagerImpl mFragments = new FragmentManagerImpl();
    final FragmentContainer mContainer = new FragmentContainer() { // from class: android.app.Activity.1
        @Override // android.app.FragmentContainer
        public View findViewById(int id) {
            return Activity.this.findViewById(id);
        }
    };
    private final ArrayList<ManagedCursor> mManagedCursors = new ArrayList<>();
    int mResultCode = 0;
    Intent mResultData = null;
    private boolean mTitleReady = false;
    private int mDefaultKeyMode = 0;
    private SpannableStringBuilder mDefaultKeySsb = null;
    private final Object mInstanceTracker = StrictMode.trackActivity(this);
    final Handler mHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Activity$NonConfigurationInstances.class */
    public static final class NonConfigurationInstances {
        Object activity;
        HashMap<String, Object> children;
        ArrayList<Fragment> fragments;
        ArrayMap<String, LoaderManagerImpl> loaders;
    }

    /* loaded from: Activity$TranslucentConversionListener.class */
    public interface TranslucentConversionListener {
        void onTranslucentConversionComplete(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Activity$ManagedDialog.class */
    public static class ManagedDialog {
        Dialog mDialog;
        Bundle mArgs;

        private ManagedDialog() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Activity$ManagedCursor.class */
    public static final class ManagedCursor {
        private final Cursor mCursor;
        private boolean mReleased = false;
        private boolean mUpdated = false;

        ManagedCursor(Cursor cursor) {
            this.mCursor = cursor;
        }
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public void setIntent(Intent newIntent) {
        this.mIntent = newIntent;
    }

    public final Application getApplication() {
        return this.mApplication;
    }

    public final boolean isChild() {
        return this.mParent != null;
    }

    public final Activity getParent() {
        return this.mParent;
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    public LoaderManager getLoaderManager() {
        if (this.mLoaderManager != null) {
            return this.mLoaderManager;
        }
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
        return this.mLoaderManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderManagerImpl getLoaderManager(String who, boolean started, boolean create) {
        if (this.mAllLoaderManagers == null) {
            this.mAllLoaderManagers = new ArrayMap<>();
        }
        LoaderManagerImpl lm = this.mAllLoaderManagers.get(who);
        if (lm == null) {
            if (create) {
                lm = new LoaderManagerImpl(who, this, started);
                this.mAllLoaderManagers.put(who, lm);
            }
        } else {
            lm.updateActivity(this);
        }
        return lm;
    }

    public View getCurrentFocus() {
        if (this.mWindow != null) {
            return this.mWindow.getCurrentFocus();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        if (this.mLastNonConfigurationInstances != null) {
            this.mAllLoaderManagers = this.mLastNonConfigurationInstances.loaders;
        }
        if (this.mActivityInfo.parentActivityName != null) {
            if (this.mActionBar == null) {
                this.mEnableDefaultActionBarUp = true;
            } else {
                this.mActionBar.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            this.mFragments.restoreAllState(p, this.mLastNonConfigurationInstances != null ? this.mLastNonConfigurationInstances.fragments : null);
        }
        this.mFragments.dispatchCreate();
        getApplication().dispatchActivityCreated(this, savedInstanceState);
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestoreInstanceState(Bundle savedInstanceState) {
        onRestoreInstanceState(savedInstanceState);
        restoreManagedDialogs(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle windowState;
        if (this.mWindow != null && (windowState = savedInstanceState.getBundle(WINDOW_HIERARCHY_TAG)) != null) {
            this.mWindow.restoreHierarchyState(windowState);
        }
    }

    private void restoreManagedDialogs(Bundle savedInstanceState) {
        Bundle b = savedInstanceState.getBundle(SAVED_DIALOGS_TAG);
        if (b == null) {
            return;
        }
        int[] ids = b.getIntArray(SAVED_DIALOG_IDS_KEY);
        int numDialogs = ids.length;
        this.mManagedDialogs = new SparseArray<>(numDialogs);
        for (int i : ids) {
            Integer dialogId = Integer.valueOf(i);
            Bundle dialogState = b.getBundle(savedDialogKeyFor(dialogId.intValue()));
            if (dialogState != null) {
                ManagedDialog md = new ManagedDialog();
                md.mArgs = b.getBundle(savedDialogArgsKeyFor(dialogId.intValue()));
                md.mDialog = createDialog(dialogId, dialogState, md.mArgs);
                if (md.mDialog != null) {
                    this.mManagedDialogs.put(dialogId.intValue(), md);
                    onPrepareDialog(dialogId.intValue(), md.mDialog, md.mArgs);
                    md.mDialog.onRestoreInstanceState(dialogState);
                }
            }
        }
    }

    private Dialog createDialog(Integer dialogId, Bundle state, Bundle args) {
        Dialog dialog = onCreateDialog(dialogId.intValue(), args);
        if (dialog == null) {
            return null;
        }
        dialog.dispatchOnCreate(state);
        return dialog;
    }

    private static String savedDialogKeyFor(int key) {
        return SAVED_DIALOG_KEY_PREFIX + key;
    }

    private static String savedDialogArgsKeyFor(int key) {
        return SAVED_DIALOG_ARGS_KEY_PREFIX + key;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        if (!isChild()) {
            this.mTitleReady = true;
            onTitleChanged(getTitle(), getTitleColor());
        }
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStart() {
        this.mCalled = true;
        if (!this.mLoadersStarted) {
            this.mLoadersStarted = true;
            if (this.mLoaderManager != null) {
                this.mLoaderManager.doStart();
            } else if (!this.mCheckedForLoaderManager) {
                this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
            }
            this.mCheckedForLoaderManager = true;
        }
        getApplication().dispatchActivityStarted(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestart() {
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onResume() {
        getApplication().dispatchActivityResumed(this);
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostResume() {
        Window win = getWindow();
        if (win != null) {
            win.makeActive();
        }
        if (this.mActionBar != null) {
            this.mActionBar.setShowHideAnimationEnabled(true);
        }
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performSaveInstanceState(Bundle outState) {
        onSaveInstanceState(outState);
        saveManagedDialogs(outState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(WINDOW_HIERARCHY_TAG, this.mWindow.saveHierarchyState());
        Parcelable p = this.mFragments.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
        getApplication().dispatchActivitySaveInstanceState(this, outState);
    }

    private void saveManagedDialogs(Bundle outState) {
        int numDialogs;
        if (this.mManagedDialogs == null || (numDialogs = this.mManagedDialogs.size()) == 0) {
            return;
        }
        Bundle dialogState = new Bundle();
        int[] ids = new int[this.mManagedDialogs.size()];
        for (int i = 0; i < numDialogs; i++) {
            int key = this.mManagedDialogs.keyAt(i);
            ids[i] = key;
            ManagedDialog md = this.mManagedDialogs.valueAt(i);
            dialogState.putBundle(savedDialogKeyFor(key), md.mDialog.onSaveInstanceState());
            if (md.mArgs != null) {
                dialogState.putBundle(savedDialogArgsKeyFor(key), md.mArgs);
            }
        }
        dialogState.putIntArray(SAVED_DIALOG_IDS_KEY, ids);
        outState.putBundle(SAVED_DIALOGS_TAG, dialogState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPause() {
        getApplication().dispatchActivityPaused(this);
        this.mCalled = true;
    }

    protected void onUserLeaveHint() {
    }

    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return false;
    }

    public CharSequence onCreateDescription() {
        return null;
    }

    public void onProvideAssistData(Bundle data) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStop() {
        if (this.mActionBar != null) {
            this.mActionBar.setShowHideAnimationEnabled(false);
        }
        getApplication().dispatchActivityStopped(this);
        this.mTranslucentCallback = null;
        this.mCalled = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDestroy() {
        this.mCalled = true;
        if (this.mManagedDialogs != null) {
            int numDialogs = this.mManagedDialogs.size();
            for (int i = 0; i < numDialogs; i++) {
                ManagedDialog md = this.mManagedDialogs.valueAt(i);
                if (md.mDialog.isShowing()) {
                    md.mDialog.dismiss();
                }
            }
            this.mManagedDialogs = null;
        }
        synchronized (this.mManagedCursors) {
            int numCursors = this.mManagedCursors.size();
            for (int i2 = 0; i2 < numCursors; i2++) {
                ManagedCursor c = this.mManagedCursors.get(i2);
                if (c != null) {
                    c.mCursor.close();
                }
            }
            this.mManagedCursors.clear();
        }
        if (this.mSearchManager != null) {
            this.mSearchManager.stopSearch();
        }
        getApplication().dispatchActivityDestroyed(this);
    }

    public void reportFullyDrawn() {
        if (this.mDoReportFullyDrawn) {
            this.mDoReportFullyDrawn = false;
            try {
                ActivityManagerNative.getDefault().reportActivityFullyDrawn(this.mToken);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        this.mCalled = true;
        this.mFragments.dispatchConfigurationChanged(newConfig);
        if (this.mWindow != null) {
            this.mWindow.onConfigurationChanged(newConfig);
        }
        if (this.mActionBar != null) {
            this.mActionBar.onConfigurationChanged(newConfig);
        }
    }

    public int getChangingConfigurations() {
        return this.mConfigChangeFlags;
    }

    @Deprecated
    public Object getLastNonConfigurationInstance() {
        if (this.mLastNonConfigurationInstances != null) {
            return this.mLastNonConfigurationInstances.activity;
        }
        return null;
    }

    public Object onRetainNonConfigurationInstance() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashMap<String, Object> getLastNonConfigurationChildInstances() {
        if (this.mLastNonConfigurationInstances != null) {
            return this.mLastNonConfigurationInstances.children;
        }
        return null;
    }

    HashMap<String, Object> onRetainNonConfigurationChildInstances() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NonConfigurationInstances retainNonConfigurationInstances() {
        Object activity = onRetainNonConfigurationInstance();
        HashMap<String, Object> children = onRetainNonConfigurationChildInstances();
        ArrayList<Fragment> fragments = this.mFragments.retainNonConfig();
        boolean retainLoaders = false;
        if (this.mAllLoaderManagers != null) {
            int N = this.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaders = new LoaderManagerImpl[N];
            for (int i = N - 1; i >= 0; i--) {
                loaders[i] = this.mAllLoaderManagers.valueAt(i);
            }
            for (int i2 = 0; i2 < N; i2++) {
                LoaderManagerImpl lm = loaders[i2];
                if (lm.mRetaining) {
                    retainLoaders = true;
                } else {
                    lm.doDestroy();
                    this.mAllLoaderManagers.remove(lm.mWho);
                }
            }
        }
        if (activity == null && children == null && fragments == null && !retainLoaders) {
            return null;
        }
        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.activity = activity;
        nci.children = children;
        nci.fragments = fragments;
        nci.loaders = this.mAllLoaderManagers;
        return nci;
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        this.mCalled = true;
        this.mFragments.dispatchLowMemory();
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        this.mCalled = true;
        this.mFragments.dispatchTrimMemory(level);
    }

    public FragmentManager getFragmentManager() {
        return this.mFragments;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateFragment(String who) {
        LoaderManagerImpl lm;
        if (this.mAllLoaderManagers != null && (lm = this.mAllLoaderManagers.get(who)) != null && !lm.mRetaining) {
            lm.doDestroy();
            this.mAllLoaderManagers.remove(who);
        }
    }

    public void onAttachFragment(Fragment fragment) {
    }

    @Deprecated
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String sortOrder) {
        Cursor c = getContentResolver().query(uri, projection, selection, null, sortOrder);
        if (c != null) {
            startManagingCursor(c);
        }
        return c;
    }

    @Deprecated
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (c != null) {
            startManagingCursor(c);
        }
        return c;
    }

    @Deprecated
    public void startManagingCursor(Cursor c) {
        synchronized (this.mManagedCursors) {
            this.mManagedCursors.add(new ManagedCursor(c));
        }
    }

    @Deprecated
    public void stopManagingCursor(Cursor c) {
        synchronized (this.mManagedCursors) {
            int N = this.mManagedCursors.size();
            int i = 0;
            while (true) {
                if (i >= N) {
                    break;
                }
                ManagedCursor mc = this.mManagedCursors.get(i);
                if (mc.mCursor != c) {
                    i++;
                } else {
                    this.mManagedCursors.remove(i);
                    break;
                }
            }
        }
    }

    @Deprecated
    public void setPersistent(boolean isPersistent) {
    }

    public View findViewById(int id) {
        return getWindow().findViewById(id);
    }

    public ActionBar getActionBar() {
        initActionBar();
        return this.mActionBar;
    }

    private void initActionBar() {
        Window window = getWindow();
        window.getDecorView();
        if (isChild() || !window.hasFeature(8) || this.mActionBar != null) {
            return;
        }
        this.mActionBar = new ActionBarImpl(this);
        this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
        this.mWindow.setDefaultIcon(this.mActivityInfo.getIconResource());
        this.mWindow.setDefaultLogo(this.mActivityInfo.getLogoResource());
    }

    public void setContentView(int layoutResID) {
        getWindow().setContentView(layoutResID);
        initActionBar();
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
        initActionBar();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().setContentView(view, params);
        initActionBar();
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().addContentView(view, params);
        initActionBar();
    }

    public void setFinishOnTouchOutside(boolean finish) {
        this.mWindow.setCloseOnTouchOutside(finish);
    }

    public final void setDefaultKeyMode(int mode) {
        this.mDefaultKeyMode = mode;
        switch (mode) {
            case 0:
            case 2:
                this.mDefaultKeySsb = null;
                return;
            case 1:
            case 3:
            case 4:
                this.mDefaultKeySsb = new SpannableStringBuilder();
                Selection.setSelection(this.mDefaultKeySsb, 0);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled;
        if (keyCode == 4) {
            if (getApplicationInfo().targetSdkVersion >= 5) {
                event.startTracking();
                return true;
            }
            onBackPressed();
            return true;
        } else if (this.mDefaultKeyMode == 0) {
            return false;
        } else {
            if (this.mDefaultKeyMode == 2) {
                if (getWindow().performPanelShortcut(0, keyCode, event, 2)) {
                    return true;
                }
                return false;
            }
            boolean clearSpannable = false;
            if (event.getRepeatCount() != 0 || event.isSystem()) {
                clearSpannable = true;
                handled = false;
            } else {
                handled = TextKeyListener.getInstance().onKeyDown(null, this.mDefaultKeySsb, keyCode, event);
                if (handled && this.mDefaultKeySsb.length() > 0) {
                    String str = this.mDefaultKeySsb.toString();
                    clearSpannable = true;
                    switch (this.mDefaultKeyMode) {
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(WebView.SCHEME_TEL + str));
                            intent.addFlags(268435456);
                            startActivity(intent);
                            break;
                        case 3:
                            startSearch(str, false, null, false);
                            break;
                        case 4:
                            startSearch(str, false, null, true);
                            break;
                    }
                }
            }
            if (clearSpannable) {
                this.mDefaultKeySsb.clear();
                this.mDefaultKeySsb.clearSpans();
                Selection.setSelection(this.mDefaultKeySsb, 0);
            }
            return handled;
        }
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getApplicationInfo().targetSdkVersion >= 5 && keyCode == 4 && event.isTracking() && !event.isCanceled()) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    public void onBackPressed() {
        if (!this.mFragments.popBackStackImmediate()) {
            finish();
        }
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mWindow.shouldCloseOnTouch(this, event)) {
            finish();
            return true;
        }
        return false;
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public void onUserInteraction() {
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        View decor;
        if (this.mParent == null && (decor = this.mDecor) != null && decor.getParent() != null) {
            getWindowManager().updateViewLayout(decor, params);
        }
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

    public boolean hasWindowFocus() {
        View d;
        Window w = getWindow();
        if (w != null && (d = w.getDecorView()) != null) {
            return d.hasWindowFocus();
        }
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        onUserInteraction();
        Window win = getWindow();
        if (win.superDispatchKeyEvent(event)) {
            return true;
        }
        View decor = this.mDecor;
        if (decor == null) {
            decor = win.getDecorView();
        }
        return event.dispatch(this, decor != null ? decor.getKeyDispatcherState() : null, this);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        onUserInteraction();
        if (getWindow().superDispatchKeyShortcutEvent(event)) {
            return true;
        }
        return onKeyShortcut(event.getKeyCode(), event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        onUserInteraction();
        if (getWindow().superDispatchTrackballEvent(ev)) {
            return true;
        }
        return onTrackballEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        onUserInteraction();
        if (getWindow().superDispatchGenericMotionEvent(ev)) {
            return true;
        }
        return onGenericMotionEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.setClassName(getClass().getName());
        event.setPackageName(getPackageName());
        ViewGroup.LayoutParams params = getWindow().getAttributes();
        boolean isFullScreen = params.width == -1 && params.height == -1;
        event.setFullScreen(isFullScreen);
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            event.getText().add(title);
            return true;
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == 0) {
            boolean show = onCreateOptionsMenu(menu);
            return show | this.mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
        }
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == 0 && menu != null) {
            boolean goforit = onPrepareOptionsMenu(menu);
            return goforit | this.mFragments.dispatchPrepareOptionsMenu(menu);
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == 8) {
            initActionBar();
            if (this.mActionBar != null) {
                this.mActionBar.dispatchMenuVisibilityChanged(true);
                return true;
            }
            Log.e(TAG, "Tried to open action bar menu with no action bar");
            return true;
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        CharSequence titleCondensed = item.getTitleCondensed();
        switch (featureId) {
            case 0:
                if (titleCondensed != null) {
                    EventLog.writeEvent(50000, 0, titleCondensed.toString());
                }
                if (onOptionsItemSelected(item) || this.mFragments.dispatchOptionsItemSelected(item)) {
                    return true;
                }
                if (item.getItemId() == 16908332 && this.mActionBar != null && (this.mActionBar.getDisplayOptions() & 4) != 0) {
                    if (this.mParent == null) {
                        return onNavigateUp();
                    }
                    return this.mParent.onNavigateUpFromChild(this);
                }
                return false;
            case 6:
                if (titleCondensed != null) {
                    EventLog.writeEvent(50000, 1, titleCondensed.toString());
                }
                if (onContextItemSelected(item)) {
                    return true;
                }
                return this.mFragments.dispatchContextItemSelected(item);
            default:
                return false;
        }
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        switch (featureId) {
            case 0:
                this.mFragments.dispatchOptionsMenuClosed(menu);
                onOptionsMenuClosed(menu);
                return;
            case 6:
                onContextMenuClosed(menu);
                return;
            case 8:
                initActionBar();
                this.mActionBar.dispatchMenuVisibilityChanged(false);
                return;
            default:
                return;
        }
    }

    public void invalidateOptionsMenu() {
        this.mWindow.invalidatePanelMenu(0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mParent != null) {
            return this.mParent.onCreateOptionsMenu(menu);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.mParent != null) {
            return this.mParent.onPrepareOptionsMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mParent != null) {
            return this.mParent.onOptionsItemSelected(item);
        }
        return false;
    }

    public boolean onNavigateUp() {
        Intent upIntent = getParentActivityIntent();
        if (upIntent != null) {
            if (this.mActivityInfo.taskAffinity == null) {
                finish();
                return true;
            } else if (shouldUpRecreateTask(upIntent)) {
                TaskStackBuilder b = TaskStackBuilder.create(this);
                onCreateNavigateUpTaskStack(b);
                onPrepareNavigateUpTaskStack(b);
                b.startActivities();
                if (this.mResultCode != 0 || this.mResultData != null) {
                    Log.i(TAG, "onNavigateUp only finishing topmost activity to return a result");
                    finish();
                    return true;
                }
                finishAffinity();
                return true;
            } else {
                navigateUpTo(upIntent);
                return true;
            }
        }
        return false;
    }

    public boolean onNavigateUpFromChild(Activity child) {
        return onNavigateUp();
    }

    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        builder.addParentStack(this);
    }

    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
    }

    public void onOptionsMenuClosed(Menu menu) {
        if (this.mParent != null) {
            this.mParent.onOptionsMenuClosed(menu);
        }
    }

    public void openOptionsMenu() {
        this.mWindow.openPanel(0, null);
    }

    public void closeOptionsMenu() {
        this.mWindow.closePanel(0);
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    public void registerForContextMenu(View view) {
        view.setOnCreateContextMenuListener(this);
    }

    public void unregisterForContextMenu(View view) {
        view.setOnCreateContextMenuListener(null);
    }

    public void openContextMenu(View view) {
        view.showContextMenu();
    }

    public void closeContextMenu() {
        this.mWindow.closePanel(6);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (this.mParent != null) {
            return this.mParent.onContextItemSelected(item);
        }
        return false;
    }

    public void onContextMenuClosed(Menu menu) {
        if (this.mParent != null) {
            this.mParent.onContextMenuClosed(menu);
        }
    }

    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return null;
    }

    @Deprecated
    protected Dialog onCreateDialog(int id, Bundle args) {
        return onCreateDialog(id);
    }

    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog) {
        dialog.setOwnerActivity(this);
    }

    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        onPrepareDialog(id, dialog);
    }

    @Deprecated
    public final void showDialog(int id) {
        showDialog(id, null);
    }

    @Deprecated
    public final boolean showDialog(int id, Bundle args) {
        if (this.mManagedDialogs == null) {
            this.mManagedDialogs = new SparseArray<>();
        }
        ManagedDialog md = this.mManagedDialogs.get(id);
        if (md == null) {
            md = new ManagedDialog();
            md.mDialog = createDialog(Integer.valueOf(id), null, args);
            if (md.mDialog == null) {
                return false;
            }
            this.mManagedDialogs.put(id, md);
        }
        md.mArgs = args;
        onPrepareDialog(id, md.mDialog, args);
        md.mDialog.show();
        return true;
    }

    @Deprecated
    public final void dismissDialog(int id) {
        if (this.mManagedDialogs == null) {
            throw missingDialog(id);
        }
        ManagedDialog md = this.mManagedDialogs.get(id);
        if (md == null) {
            throw missingDialog(id);
        }
        md.mDialog.dismiss();
    }

    private IllegalArgumentException missingDialog(int id) {
        return new IllegalArgumentException("no dialog with id " + id + " was ever shown via Activity#showDialog");
    }

    @Deprecated
    public final void removeDialog(int id) {
        ManagedDialog md;
        if (this.mManagedDialogs != null && (md = this.mManagedDialogs.get(id)) != null) {
            md.mDialog.dismiss();
            this.mManagedDialogs.remove(id);
        }
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        startSearch(null, false, null, false);
        return true;
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        ensureSearchManager();
        this.mSearchManager.startSearch(initialQuery, selectInitialQuery, getComponentName(), appSearchData, globalSearch);
    }

    public void triggerSearch(String query, Bundle appSearchData) {
        ensureSearchManager();
        this.mSearchManager.triggerSearch(query, getComponentName(), appSearchData);
    }

    public void takeKeyEvents(boolean get) {
        getWindow().takeKeyEvents(get);
    }

    public final boolean requestWindowFeature(int featureId) {
        return getWindow().requestFeature(featureId);
    }

    public final void setFeatureDrawableResource(int featureId, int resId) {
        getWindow().setFeatureDrawableResource(featureId, resId);
    }

    public final void setFeatureDrawableUri(int featureId, Uri uri) {
        getWindow().setFeatureDrawableUri(featureId, uri);
    }

    public final void setFeatureDrawable(int featureId, Drawable drawable) {
        getWindow().setFeatureDrawable(featureId, drawable);
    }

    public final void setFeatureDrawableAlpha(int featureId, int alpha) {
        getWindow().setFeatureDrawableAlpha(featureId, alpha);
    }

    public LayoutInflater getLayoutInflater() {
        return getWindow().getLayoutInflater();
    }

    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            initActionBar();
            if (this.mActionBar != null) {
                this.mMenuInflater = new MenuInflater(this.mActionBar.getThemedContext(), this);
            } else {
                this.mMenuInflater = new MenuInflater(this);
            }
        }
        return this.mMenuInflater;
    }

    @Override // android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        if (this.mParent == null) {
            super.onApplyThemeResource(theme, resid, first);
            return;
        }
        try {
            theme.setTo(this.mParent.getTheme());
        } catch (Exception e) {
        }
        theme.applyStyle(resid, false);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (this.mParent == null) {
            Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, intent, requestCode, options);
            if (ar != null) {
                this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
            }
            if (requestCode >= 0) {
                this.mStartedActivity = true;
            }
            View decor = this.mWindow != null ? this.mWindow.peekDecorView() : null;
            if (decor != null) {
                decor.cancelPendingInputEvents();
            }
        } else if (options != null) {
            this.mParent.startActivityFromChild(this, intent, requestCode, options);
        } else {
            this.mParent.startActivityFromChild(this, intent, requestCode);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivityAsUser(Intent intent, UserHandle user) {
        startActivityAsUser(intent, null, user);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        if (this.mParent != null) {
            throw new RuntimeException("Called be called from a child");
        }
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, intent, -1, options, user);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, -1, ar.getResultCode(), ar.getResultData());
        }
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (this.mParent == null) {
            startIntentSenderForResultInner(intent, requestCode, fillInIntent, flagsMask, flagsValues, this, options);
        } else if (options != null) {
            this.mParent.startIntentSenderFromChild(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            this.mParent.startIntentSenderFromChild(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    private void startIntentSenderForResultInner(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, Activity activity, Bundle options) throws IntentSender.SendIntentException {
        String resolvedType = null;
        if (fillInIntent != null) {
            try {
                fillInIntent.migrateExtraStreamToClipData();
                fillInIntent.prepareToLeaveProcess();
                resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
            } catch (RemoteException e) {
            }
        }
        int result = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), intent, fillInIntent, resolvedType, this.mToken, activity.mEmbeddedID, requestCode, flagsMask, flagsValues, options);
        if (result == -6) {
            throw new IntentSender.SendIntentException();
        }
        Instrumentation.checkStartActivityResult(result, null);
        if (requestCode >= 0) {
            this.mStartedActivity = true;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        if (options != null) {
            startActivityForResult(intent, -1, options);
        } else {
            startActivityForResult(intent, -1);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents, Bundle options) {
        this.mInstrumentation.execStartActivities(this, this.mMainThread.getApplicationThread(), this.mToken, this, intents, options);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (options != null) {
            startIntentSenderForResult(intent, -1, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            startIntentSenderForResult(intent, -1, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public boolean startActivityIfNeeded(Intent intent, int requestCode) {
        return startActivityIfNeeded(intent, requestCode, null);
    }

    public boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options) {
        if (this.mParent == null) {
            int result = 1;
            try {
                intent.migrateExtraStreamToClipData();
                intent.prepareToLeaveProcess();
                result = ActivityManagerNative.getDefault().startActivity(this.mMainThread.getApplicationThread(), getBasePackageName(), intent, intent.resolveTypeIfNeeded(getContentResolver()), this.mToken, this.mEmbeddedID, requestCode, 1, null, null, options);
            } catch (RemoteException e) {
            }
            Instrumentation.checkStartActivityResult(result, intent);
            if (requestCode >= 0) {
                this.mStartedActivity = true;
            }
            return result != 1;
        }
        throw new UnsupportedOperationException("startActivityIfNeeded can only be called from a top-level activity");
    }

    public boolean startNextMatchingActivity(Intent intent) {
        return startNextMatchingActivity(intent, null);
    }

    public boolean startNextMatchingActivity(Intent intent, Bundle options) {
        if (this.mParent == null) {
            try {
                intent.migrateExtraStreamToClipData();
                intent.prepareToLeaveProcess();
                return ActivityManagerNative.getDefault().startNextMatchingActivity(this.mToken, intent, options);
            } catch (RemoteException e) {
                return false;
            }
        }
        throw new UnsupportedOperationException("startNextMatchingActivity can only be called from a top-level activity");
    }

    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
        startActivityFromChild(child, intent, requestCode, null);
    }

    public void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) {
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, child, intent, requestCode, options);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, child.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
        }
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        startActivityFromFragment(fragment, intent, requestCode, null);
    }

    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, fragment, intent, requestCode, options);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, fragment.mWho, requestCode, ar.getResultCode(), ar.getResultData());
        }
    }

    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        startIntentSenderForResultInner(intent, requestCode, fillInIntent, flagsMask, flagsValues, child, options);
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        try {
            ActivityManagerNative.getDefault().overridePendingTransition(this.mToken, getPackageName(), enterAnim, exitAnim);
        } catch (RemoteException e) {
        }
    }

    public final void setResult(int resultCode) {
        synchronized (this) {
            this.mResultCode = resultCode;
            this.mResultData = null;
        }
    }

    public final void setResult(int resultCode, Intent data) {
        synchronized (this) {
            this.mResultCode = resultCode;
            this.mResultData = data;
        }
    }

    public String getCallingPackage() {
        try {
            return ActivityManagerNative.getDefault().getCallingPackage(this.mToken);
        } catch (RemoteException e) {
            return null;
        }
    }

    public ComponentName getCallingActivity() {
        try {
            return ActivityManagerNative.getDefault().getCallingActivity(this.mToken);
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setVisible(boolean visible) {
        if (this.mVisibleFromClient != visible) {
            this.mVisibleFromClient = visible;
            if (this.mVisibleFromServer) {
                if (!visible) {
                    this.mDecor.setVisibility(4);
                } else {
                    makeVisible();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeVisible() {
        if (!this.mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(this.mDecor, getWindow().getAttributes());
            this.mWindowAdded = true;
        }
        this.mDecor.setVisibility(0);
    }

    public boolean isFinishing() {
        return this.mFinished;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public boolean isChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public void recreate() {
        if (this.mParent != null) {
            throw new IllegalStateException("Can only be called on top-level activity");
        }
        if (Looper.myLooper() != this.mMainThread.getLooper()) {
            throw new IllegalStateException("Must be called from main thread");
        }
        this.mMainThread.requestRelaunchActivity(this.mToken, null, null, 0, false, null, false);
    }

    public void finish() {
        int resultCode;
        Intent resultData;
        if (this.mParent == null) {
            synchronized (this) {
                resultCode = this.mResultCode;
                resultData = this.mResultData;
            }
            if (resultData != null) {
                try {
                    resultData.prepareToLeaveProcess();
                } catch (RemoteException e) {
                    return;
                }
            }
            if (ActivityManagerNative.getDefault().finishActivity(this.mToken, resultCode, resultData)) {
                this.mFinished = true;
            }
            return;
        }
        this.mParent.finishFromChild(this);
    }

    public void finishAffinity() {
        if (this.mParent != null) {
            throw new IllegalStateException("Can not be called from an embedded activity");
        }
        if (this.mResultCode != 0 || this.mResultData != null) {
            throw new IllegalStateException("Can not be called to deliver a result");
        }
        try {
            if (ActivityManagerNative.getDefault().finishActivityAffinity(this.mToken)) {
                this.mFinished = true;
            }
        } catch (RemoteException e) {
        }
    }

    public void finishFromChild(Activity child) {
        finish();
    }

    public void finishActivity(int requestCode) {
        if (this.mParent == null) {
            try {
                ActivityManagerNative.getDefault().finishSubActivity(this.mToken, this.mEmbeddedID, requestCode);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mParent.finishActivityFromChild(this, requestCode);
    }

    public void finishActivityFromChild(Activity child, int requestCode) {
        try {
            ActivityManagerNative.getDefault().finishSubActivity(this.mToken, child.mEmbeddedID, requestCode);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public PendingIntent createPendingResult(int requestCode, Intent data, int flags) {
        String packageName = getPackageName();
        try {
            data.prepareToLeaveProcess();
            IIntentSender target = ActivityManagerNative.getDefault().getIntentSender(3, packageName, this.mParent == null ? this.mToken : this.mParent.mToken, this.mEmbeddedID, requestCode, new Intent[]{data}, null, flags, null, UserHandle.myUserId());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (this.mParent == null) {
            try {
                ActivityManagerNative.getDefault().setRequestedOrientation(this.mToken, requestedOrientation);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mParent.setRequestedOrientation(requestedOrientation);
    }

    public int getRequestedOrientation() {
        if (this.mParent == null) {
            try {
                return ActivityManagerNative.getDefault().getRequestedOrientation(this.mToken);
            } catch (RemoteException e) {
                return -1;
            }
        }
        return this.mParent.getRequestedOrientation();
    }

    public int getTaskId() {
        try {
            return ActivityManagerNative.getDefault().getTaskForActivity(this.mToken, false);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean isTaskRoot() {
        try {
            return ActivityManagerNative.getDefault().getTaskForActivity(this.mToken, true) >= 0;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean moveTaskToBack(boolean nonRoot) {
        try {
            return ActivityManagerNative.getDefault().moveActivityTaskToBack(this.mToken, nonRoot);
        } catch (RemoteException e) {
            return false;
        }
    }

    public String getLocalClassName() {
        String pkg = getPackageName();
        String cls = this.mComponent.getClassName();
        int packageLen = pkg.length();
        if (!cls.startsWith(pkg) || cls.length() <= packageLen || cls.charAt(packageLen) != '.') {
            return cls;
        }
        return cls.substring(packageLen + 1);
    }

    public ComponentName getComponentName() {
        return this.mComponent;
    }

    public SharedPreferences getPreferences(int mode) {
        return getSharedPreferences(getLocalClassName(), mode);
    }

    private void ensureSearchManager() {
        if (this.mSearchManager != null) {
            return;
        }
        this.mSearchManager = new SearchManager(this, null);
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            throw new IllegalStateException("System services not available to Activities before onCreate()");
        }
        if (Context.WINDOW_SERVICE.equals(name)) {
            return this.mWindowManager;
        }
        if ("search".equals(name)) {
            ensureSearchManager();
            return this.mSearchManager;
        }
        return super.getSystemService(name);
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        onTitleChanged(title, this.mTitleColor);
        if (this.mParent != null) {
            this.mParent.onChildTitleChanged(this, title);
        }
    }

    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    public void setTitleColor(int textColor) {
        this.mTitleColor = textColor;
        onTitleChanged(this.mTitle, textColor);
    }

    public final CharSequence getTitle() {
        return this.mTitle;
    }

    public final int getTitleColor() {
        return this.mTitleColor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTitleChanged(CharSequence title, int color) {
        Window win;
        if (this.mTitleReady && (win = getWindow()) != null) {
            win.setTitle(title);
            if (color != 0) {
                win.setTitleColor(color);
            }
        }
    }

    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
    }

    public final void setProgressBarVisibility(boolean visible) {
        getWindow().setFeatureInt(2, visible ? -1 : -2);
    }

    public final void setProgressBarIndeterminateVisibility(boolean visible) {
        getWindow().setFeatureInt(5, visible ? -1 : -2);
    }

    public final void setProgressBarIndeterminate(boolean indeterminate) {
        getWindow().setFeatureInt(2, indeterminate ? -3 : -4);
    }

    public final void setProgress(int progress) {
        getWindow().setFeatureInt(2, progress + 0);
    }

    public final void setSecondaryProgress(int secondaryProgress) {
        getWindow().setFeatureInt(2, secondaryProgress + Window.PROGRESS_SECONDARY_START);
    }

    public final void setVolumeControlStream(int streamType) {
        getWindow().setVolumeControlStream(streamType);
    }

    public final int getVolumeControlStream() {
        return getWindow().getVolumeControlStream();
    }

    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != this.mUiThread) {
            this.mHandler.post(action);
        } else {
            action.run();
        }
    }

    @Override // android.view.LayoutInflater.Factory
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override // android.view.LayoutInflater.Factory2
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!"fragment".equals(name)) {
            return onCreateView(name, context, attrs);
        }
        String fname = attrs.getAttributeValue(null, "class");
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Fragment);
        if (fname == null) {
            fname = a.getString(0);
        }
        int id = a.getResourceId(1, -1);
        String tag = a.getString(2);
        a.recycle();
        int containerId = parent != null ? parent.getId() : 0;
        if (containerId == -1 && id == -1 && tag == null) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
        }
        Fragment fragment = id != -1 ? this.mFragments.findFragmentById(id) : null;
        if (fragment == null && tag != null) {
            fragment = this.mFragments.findFragmentByTag(tag);
        }
        if (fragment == null && containerId != -1) {
            fragment = this.mFragments.findFragmentById(containerId);
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "onCreateView: id=0x" + Integer.toHexString(id) + " fname=" + fname + " existing=" + fragment);
        }
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fname);
            fragment.mFromLayout = true;
            fragment.mFragmentId = id != 0 ? id : containerId;
            fragment.mContainerId = containerId;
            fragment.mTag = tag;
            fragment.mInLayout = true;
            fragment.mFragmentManager = this.mFragments;
            fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
            this.mFragments.addFragment(fragment, true);
        } else if (fragment.mInLayout) {
            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(id) + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId) + " with another fragment for " + fname);
        } else {
            fragment.mInLayout = true;
            if (!fragment.mRetaining) {
                fragment.onInflate(this, attrs, fragment.mSavedFragmentState);
            }
            this.mFragments.moveToState(fragment);
        }
        if (fragment.mView == null) {
            throw new IllegalStateException("Fragment " + fname + " did not create a view.");
        }
        if (id != 0) {
            fragment.mView.setId(id);
        }
        if (fragment.mView.getTag() == null) {
            fragment.mView.setTag(tag);
        }
        return fragment.mView;
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        dumpInner(prefix, fd, writer, args);
    }

    void dumpInner(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.print("Local Activity ");
        writer.print(Integer.toHexString(System.identityHashCode(this)));
        writer.println(" State:");
        String innerPrefix = prefix + "  ";
        writer.print(innerPrefix);
        writer.print("mResumed=");
        writer.print(this.mResumed);
        writer.print(" mStopped=");
        writer.print(this.mStopped);
        writer.print(" mFinished=");
        writer.println(this.mFinished);
        writer.print(innerPrefix);
        writer.print("mLoadersStarted=");
        writer.println(this.mLoadersStarted);
        writer.print(innerPrefix);
        writer.print("mChangingConfigurations=");
        writer.println(this.mChangingConfigurations);
        writer.print(innerPrefix);
        writer.print("mCurrentConfig=");
        writer.println(this.mCurrentConfig);
        if (this.mLoaderManager != null) {
            writer.print(prefix);
            writer.print("Loader Manager ");
            writer.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
            writer.println(Separators.COLON);
            this.mLoaderManager.dump(prefix + "  ", fd, writer, args);
        }
        this.mFragments.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.println("View Hierarchy:");
        dumpViewHierarchy(prefix + "  ", writer, getWindow().getDecorView());
    }

    private void dumpViewHierarchy(String prefix, PrintWriter writer, View view) {
        ViewGroup grp;
        int N;
        writer.print(prefix);
        if (view == null) {
            writer.println("null");
            return;
        }
        writer.println(view.toString());
        if (!(view instanceof ViewGroup) || (N = (grp = (ViewGroup) view).getChildCount()) <= 0) {
            return;
        }
        String prefix2 = prefix + "  ";
        for (int i = 0; i < N; i++) {
            dumpViewHierarchy(prefix2, writer, grp.getChildAt(i));
        }
    }

    public boolean isImmersive() {
        try {
            return ActivityManagerNative.getDefault().isImmersive(this.mToken);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void convertFromTranslucent() {
        try {
            this.mTranslucentCallback = null;
            if (ActivityManagerNative.getDefault().convertFromTranslucent(this.mToken)) {
                WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, true);
            }
        } catch (RemoteException e) {
        }
    }

    public void convertToTranslucent(TranslucentConversionListener callback) {
        try {
            this.mTranslucentCallback = callback;
            this.mChangeCanvasToTranslucent = ActivityManagerNative.getDefault().convertToTranslucent(this.mToken);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTranslucentConversionComplete(boolean drawComplete) {
        if (this.mTranslucentCallback != null) {
            this.mTranslucentCallback.onTranslucentConversionComplete(drawComplete);
            this.mTranslucentCallback = null;
        }
        if (this.mChangeCanvasToTranslucent) {
            WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, false);
        }
    }

    public void setImmersive(boolean i) {
        try {
            ActivityManagerNative.getDefault().setImmersive(this.mToken, i);
        } catch (RemoteException e) {
        }
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mWindow.getDecorView().startActionMode(callback);
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        initActionBar();
        if (this.mActionBar != null) {
            return this.mActionBar.startActionMode(callback);
        }
        return null;
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
    }

    public boolean shouldUpRecreateTask(Intent targetIntent) {
        try {
            PackageManager pm = getPackageManager();
            ComponentName cn = targetIntent.getComponent();
            if (cn == null) {
                cn = targetIntent.resolveActivity(pm);
            }
            ActivityInfo info = pm.getActivityInfo(cn, 0);
            if (info.taskAffinity == null) {
                return false;
            }
            return !ActivityManagerNative.getDefault().targetTaskAffinityMatchesActivity(this.mToken, info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (RemoteException e2) {
            return false;
        }
    }

    public boolean navigateUpTo(Intent upIntent) {
        int resultCode;
        Intent resultData;
        if (this.mParent == null) {
            if (upIntent.getComponent() == null) {
                ComponentName destInfo = upIntent.resolveActivity(getPackageManager());
                if (destInfo == null) {
                    return false;
                }
                upIntent = new Intent(upIntent);
                upIntent.setComponent(destInfo);
            }
            synchronized (this) {
                resultCode = this.mResultCode;
                resultData = this.mResultData;
            }
            if (resultData != null) {
                resultData.prepareToLeaveProcess();
            }
            try {
                upIntent.prepareToLeaveProcess();
                return ActivityManagerNative.getDefault().navigateUpTo(this.mToken, upIntent, resultCode, resultData);
            } catch (RemoteException e) {
                return false;
            }
        }
        return this.mParent.navigateUpToFromChild(this, upIntent);
    }

    public boolean navigateUpToFromChild(Activity child, Intent upIntent) {
        return navigateUpTo(upIntent);
    }

    public Intent getParentActivityIntent() {
        String parentName = this.mActivityInfo.parentActivityName;
        if (TextUtils.isEmpty(parentName)) {
            return null;
        }
        ComponentName target = new ComponentName(this, parentName);
        try {
            ActivityInfo parentInfo = getPackageManager().getActivityInfo(target, 0);
            String parentActivity = parentInfo.parentActivityName;
            Intent parentIntent = parentActivity == null ? Intent.makeMainActivity(target) : new Intent().setComponent(target);
            return parentIntent;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getParentActivityIntent: bad parentActivityName '" + parentName + "' in manifest");
            return null;
        }
    }

    final void setParent(Activity parent) {
        this.mParent = parent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attach(Context context, ActivityThread aThread, Instrumentation instr, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, NonConfigurationInstances lastNonConfigurationInstances, Configuration config) {
        attach(context, aThread, instr, token, 0, application, intent, info, title, parent, id, lastNonConfigurationInstances, config);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attach(Context context, ActivityThread aThread, Instrumentation instr, IBinder token, int ident, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, NonConfigurationInstances lastNonConfigurationInstances, Configuration config) {
        attachBaseContext(context);
        this.mFragments.attachActivity(this, this.mContainer, null);
        this.mWindow = PolicyManager.makeNewWindow(this);
        this.mWindow.setCallback(this);
        this.mWindow.getLayoutInflater().setPrivateFactory(this);
        if (info.softInputMode != 0) {
            this.mWindow.setSoftInputMode(info.softInputMode);
        }
        if (info.uiOptions != 0) {
            this.mWindow.setUiOptions(info.uiOptions);
        }
        this.mUiThread = Thread.currentThread();
        this.mMainThread = aThread;
        this.mInstrumentation = instr;
        this.mToken = token;
        this.mIdent = ident;
        this.mApplication = application;
        this.mIntent = intent;
        this.mComponent = intent.getComponent();
        this.mActivityInfo = info;
        this.mTitle = title;
        this.mParent = parent;
        this.mEmbeddedID = id;
        this.mLastNonConfigurationInstances = lastNonConfigurationInstances;
        this.mWindow.setWindowManager((WindowManager) context.getSystemService(Context.WINDOW_SERVICE), this.mToken, this.mComponent.flattenToString(), (info.flags & 512) != 0);
        if (this.mParent != null) {
            this.mWindow.setContainer(this.mParent.getWindow());
        }
        this.mWindowManager = this.mWindow.getWindowManager();
        this.mCurrentConfig = config;
    }

    public final IBinder getActivityToken() {
        return this.mParent != null ? this.mParent.getActivityToken() : this.mToken;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performCreate(Bundle icicle) {
        onCreate(icicle);
        this.mVisibleFromClient = !this.mWindow.getWindowStyle().getBoolean(10, false);
        this.mFragments.dispatchActivityCreated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performStart() {
        this.mFragments.noteStateNotSaved();
        this.mCalled = false;
        this.mFragments.execPendingActions();
        this.mInstrumentation.callActivityOnStart(this);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStart()");
        }
        this.mFragments.dispatchStart();
        if (this.mAllLoaderManagers != null) {
            int N = this.mAllLoaderManagers.size();
            LoaderManagerImpl[] loaders = new LoaderManagerImpl[N];
            for (int i = N - 1; i >= 0; i--) {
                loaders[i] = this.mAllLoaderManagers.valueAt(i);
            }
            for (int i2 = 0; i2 < N; i2++) {
                LoaderManagerImpl lm = loaders[i2];
                lm.finishRetain();
                lm.doReportStart();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestart() {
        this.mFragments.noteStateNotSaved();
        if (this.mStopped) {
            this.mStopped = false;
            if (this.mToken != null && this.mParent == null) {
                WindowManagerGlobal.getInstance().setStoppedState(this.mToken, false);
            }
            synchronized (this.mManagedCursors) {
                int N = this.mManagedCursors.size();
                for (int i = 0; i < N; i++) {
                    ManagedCursor mc = this.mManagedCursors.get(i);
                    if (mc.mReleased || mc.mUpdated) {
                        if (mc.mCursor.requery() || getApplicationInfo().targetSdkVersion < 14) {
                            mc.mReleased = false;
                            mc.mUpdated = false;
                        } else {
                            throw new IllegalStateException("trying to requery an already closed cursor  " + mc.mCursor);
                        }
                    }
                }
            }
            this.mCalled = false;
            this.mInstrumentation.callActivityOnRestart(this);
            if (!this.mCalled) {
                throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onRestart()");
            }
            performStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performResume() {
        performRestart();
        this.mFragments.execPendingActions();
        this.mLastNonConfigurationInstances = null;
        this.mCalled = false;
        this.mInstrumentation.callActivityOnResume(this);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onResume()");
        }
        this.mCalled = false;
        this.mFragments.dispatchResume();
        this.mFragments.execPendingActions();
        onPostResume();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPostResume()");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performPause() {
        this.mDoReportFullyDrawn = false;
        this.mFragments.dispatchPause();
        this.mCalled = false;
        onPause();
        this.mResumed = false;
        if (!this.mCalled && getApplicationInfo().targetSdkVersion >= 9) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPause()");
        }
        this.mResumed = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performUserLeaving() {
        onUserInteraction();
        onUserLeaveHint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performStop() {
        this.mDoReportFullyDrawn = false;
        if (this.mLoadersStarted) {
            this.mLoadersStarted = false;
            if (this.mLoaderManager != null) {
                if (!this.mChangingConfigurations) {
                    this.mLoaderManager.doStop();
                } else {
                    this.mLoaderManager.doRetain();
                }
            }
        }
        if (!this.mStopped) {
            if (this.mWindow != null) {
                this.mWindow.closeAllPanels();
            }
            if (this.mToken != null && this.mParent == null) {
                WindowManagerGlobal.getInstance().setStoppedState(this.mToken, true);
            }
            this.mFragments.dispatchStop();
            this.mCalled = false;
            this.mInstrumentation.callActivityOnStop(this);
            if (!this.mCalled) {
                throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStop()");
            }
            synchronized (this.mManagedCursors) {
                int N = this.mManagedCursors.size();
                for (int i = 0; i < N; i++) {
                    ManagedCursor mc = this.mManagedCursors.get(i);
                    if (!mc.mReleased) {
                        mc.mCursor.deactivate();
                        mc.mReleased = true;
                    }
                }
            }
            this.mStopped = true;
        }
        this.mResumed = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performDestroy() {
        this.mDestroyed = true;
        this.mWindow.destroy();
        this.mFragments.dispatchDestroy();
        onDestroy();
        if (this.mLoaderManager != null) {
            this.mLoaderManager.doDestroy();
        }
    }

    public final boolean isResumed() {
        return this.mResumed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityResult(String who, int requestCode, int resultCode, Intent data) {
        this.mFragments.noteStateNotSaved();
        if (who == null) {
            onActivityResult(requestCode, resultCode, data);
            return;
        }
        Fragment frag = this.mFragments.findFragmentByWho(who);
        if (frag != null) {
            frag.onActivityResult(requestCode, resultCode, data);
        }
    }
}