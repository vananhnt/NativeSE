package com.android.webview.chromium;

import android.app.ActivityThread;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebIconDatabase;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.webkit.WebViewFactoryProvider;
import android.webkit.WebViewProvider;
import com.android.org.chromium.android_webview.AwBrowserContext;
import com.android.org.chromium.android_webview.AwBrowserProcess;
import com.android.org.chromium.android_webview.AwContents;
import com.android.org.chromium.android_webview.AwCookieManager;
import com.android.org.chromium.android_webview.AwDevToolsServer;
import com.android.org.chromium.android_webview.AwQuotaManagerBridge;
import com.android.org.chromium.android_webview.AwSettings;
import com.android.org.chromium.base.PathService;
import com.android.org.chromium.base.ThreadUtils;
import com.android.org.chromium.content.app.ContentMain;
import com.android.org.chromium.content.app.LibraryLoader;
import com.android.org.chromium.content.browser.ContentViewStatics;
import com.android.org.chromium.content.browser.ResourceExtractor;
import com.android.org.chromium.content.common.CommandLine;
import com.android.org.chromium.content.common.ProcessInitException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: WebViewChromiumFactoryProvider.class */
public class WebViewChromiumFactoryProvider implements WebViewFactoryProvider {
    private static final String CHROMIUM_PREFS_NAME = "WebViewChromiumPrefs";
    private static final String COMMAND_LINE_FILE = "/data/local/tmp/webview-command-line";
    private AwBrowserContext mBrowserContext;
    private WebViewFactoryProvider.Statics mStaticMethods;
    private GeolocationPermissionsAdapter mGeolocationPermissions;
    private CookieManagerAdapter mCookieManager;
    private WebIconDatabaseAdapter mWebIconDatabase;
    private WebStorageAdapter mWebStorage;
    private WebViewDatabaseAdapter mWebViewDatabase;
    private AwDevToolsServer mDevToolsServer;
    private boolean mStarted;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final Object mLock = new Object();
    private ArrayList<WeakReference<WebViewChromium>> mWebViewsToStart = new ArrayList<>();

    static {
        $assertionsDisabled = !WebViewChromiumFactoryProvider.class.desiredAssertionStatus();
    }

    public WebViewChromiumFactoryProvider() {
        AwBrowserProcess.loadLibrary();
        System.loadLibrary("webviewchromium_plat_support");
        ThreadUtils.setWillOverrideUiThread();
    }

    private void initPlatSupportLibrary() {
        DrawGLFunctor.setChromiumAwDrawGLFunction(AwContents.getAwDrawGLFunction());
        AwContents.setAwDrawSWFunctionTable(GraphicsUtils.getDrawSWFunctionTable());
        AwContents.setAwDrawGLFunctionTable(GraphicsUtils.getDrawGLFunctionTable());
    }

    private void ensureChromiumStartedLocked(boolean onMainThread) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.mLock)) {
            throw new AssertionError();
        }
        if (this.mStarted) {
            return;
        }
        Looper looper = !onMainThread ? Looper.myLooper() : Looper.getMainLooper();
        Log.v("WebViewChromium", "Binding Chromium to the " + (onMainThread ? "main" : "background") + " looper " + looper);
        ThreadUtils.setUiThread(looper);
        if (ThreadUtils.runningOnUiThread()) {
            startChromiumLocked();
            return;
        }
        ThreadUtils.postOnUiThread(new Runnable() { // from class: com.android.webview.chromium.WebViewChromiumFactoryProvider.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (WebViewChromiumFactoryProvider.this.mLock) {
                    WebViewChromiumFactoryProvider.this.startChromiumLocked();
                }
            }
        });
        while (!this.mStarted) {
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startChromiumLocked() {
        if (!$assertionsDisabled && (!Thread.holdsLock(this.mLock) || !ThreadUtils.runningOnUiThread())) {
            throw new AssertionError();
        }
        this.mLock.notifyAll();
        if (this.mStarted) {
            return;
        }
        if (Build.IS_DEBUGGABLE) {
            CommandLine.initFromFile(COMMAND_LINE_FILE);
        } else {
            CommandLine.init((String[]) null);
        }
        CommandLine cl = CommandLine.getInstance();
        cl.appendSwitch("enable-dcheck");
        if (!cl.hasSwitch("disable-webview-gl-mode")) {
            cl.appendSwitch("testing-webview-gl-mode");
        }
        Context context = ActivityThread.currentApplication();
        if (context.getApplicationInfo().targetSdkVersion < 19) {
            cl.appendSwitch("enable-webview-classic-workarounds");
        }
        ResourceExtractor.setMandatoryPaksToExtract(new String[]{""});
        try {
            LibraryLoader.ensureInitialized();
            PathService.override(3, "/system/lib/");
            PathService.override(3003, "/system/framework/webview/paks");
            AwBrowserProcess.start(ActivityThread.currentApplication());
            initPlatSupportLibrary();
            if (Build.IS_DEBUGGABLE) {
                setWebContentsDebuggingEnabled(true);
            }
            this.mStarted = true;
            Iterator i$ = this.mWebViewsToStart.iterator();
            while (i$.hasNext()) {
                WeakReference<WebViewChromium> wvc = i$.next();
                WebViewChromium w = wvc.get();
                if (w != null) {
                    w.startYourEngine();
                }
            }
            this.mWebViewsToStart.clear();
            this.mWebViewsToStart = null;
        } catch (ProcessInitException e) {
            throw new RuntimeException("Error initializing WebView library", e);
        }
    }

    @Override // android.webkit.WebViewFactoryProvider
    public WebViewFactoryProvider.Statics getStatics() {
        synchronized (this.mLock) {
            if (this.mStaticMethods == null) {
                ensureChromiumStartedLocked(true);
                this.mStaticMethods = new WebViewFactoryProvider.Statics() { // from class: com.android.webview.chromium.WebViewChromiumFactoryProvider.2
                    @Override // android.webkit.WebViewFactoryProvider.Statics
                    public String findAddress(String addr) {
                        return ContentViewStatics.findAddress(addr);
                    }

                    @Override // android.webkit.WebViewFactoryProvider.Statics
                    public void setPlatformNotificationsEnabled(boolean enable) {
                    }

                    @Override // android.webkit.WebViewFactoryProvider.Statics
                    public String getDefaultUserAgent(Context context) {
                        return AwSettings.getDefaultUserAgent();
                    }

                    @Override // android.webkit.WebViewFactoryProvider.Statics
                    public void setWebContentsDebuggingEnabled(boolean enable) {
                        if (!Build.IS_DEBUGGABLE) {
                            WebViewChromiumFactoryProvider.this.setWebContentsDebuggingEnabled(enable);
                        }
                    }
                };
            }
        }
        return this.mStaticMethods;
    }

    @Override // android.webkit.WebViewFactoryProvider
    public WebViewProvider createWebView(WebView webView, WebView.PrivateAccess privateAccess) {
        WebViewChromium wvc = new WebViewChromium(this, webView, privateAccess);
        synchronized (this.mLock) {
            if (this.mWebViewsToStart != null) {
                this.mWebViewsToStart.add(new WeakReference<>(wvc));
            }
        }
        ResourceProvider.registerResources(webView.getContext());
        return wvc;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasStarted() {
        return this.mStarted;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startYourEngines(boolean onMainThread) {
        synchronized (this.mLock) {
            ensureChromiumStartedLocked(onMainThread);
        }
    }

    @Override // android.webkit.WebViewFactoryProvider
    public GeolocationPermissions getGeolocationPermissions() {
        synchronized (this.mLock) {
            if (this.mGeolocationPermissions == null) {
                ensureChromiumStartedLocked(true);
                this.mGeolocationPermissions = new GeolocationPermissionsAdapter(getBrowserContextLocked().getGeolocationPermissions());
            }
        }
        return this.mGeolocationPermissions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AwBrowserContext getBrowserContext() {
        AwBrowserContext browserContextLocked;
        synchronized (this.mLock) {
            browserContextLocked = getBrowserContextLocked();
        }
        return browserContextLocked;
    }

    private AwBrowserContext getBrowserContextLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mLock)) {
            if ($assertionsDisabled || this.mStarted) {
                if (this.mBrowserContext == null) {
                    this.mBrowserContext = new AwBrowserContext(ActivityThread.currentApplication().getSharedPreferences(CHROMIUM_PREFS_NAME, 0));
                }
                return this.mBrowserContext;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    @Override // android.webkit.WebViewFactoryProvider
    public CookieManager getCookieManager() {
        synchronized (this.mLock) {
            if (this.mCookieManager == null) {
                if (!this.mStarted) {
                    ContentMain.initApplicationContext(ActivityThread.currentApplication());
                }
                this.mCookieManager = new CookieManagerAdapter(new AwCookieManager());
            }
        }
        return this.mCookieManager;
    }

    @Override // android.webkit.WebViewFactoryProvider
    public WebIconDatabase getWebIconDatabase() {
        synchronized (this.mLock) {
            if (this.mWebIconDatabase == null) {
                ensureChromiumStartedLocked(true);
                this.mWebIconDatabase = new WebIconDatabaseAdapter();
            }
        }
        return this.mWebIconDatabase;
    }

    @Override // android.webkit.WebViewFactoryProvider
    public WebStorage getWebStorage() {
        synchronized (this.mLock) {
            if (this.mWebStorage == null) {
                ensureChromiumStartedLocked(true);
                this.mWebStorage = new WebStorageAdapter(AwQuotaManagerBridge.getInstance());
            }
        }
        return this.mWebStorage;
    }

    @Override // android.webkit.WebViewFactoryProvider
    public WebViewDatabase getWebViewDatabase(Context context) {
        synchronized (this.mLock) {
            if (this.mWebViewDatabase == null) {
                ensureChromiumStartedLocked(true);
                AwBrowserContext browserContext = getBrowserContextLocked();
                this.mWebViewDatabase = new WebViewDatabaseAdapter(browserContext.getFormDatabase(), browserContext.getHttpAuthDatabase(context));
            }
        }
        return this.mWebViewDatabase;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWebContentsDebuggingEnabled(boolean enable) {
        if (Looper.myLooper() != ThreadUtils.getUiThreadLooper()) {
            throw new RuntimeException("Toggling of Web Contents Debugging must be done on the UI thread");
        }
        if (this.mDevToolsServer == null) {
            if (!enable) {
                return;
            }
            this.mDevToolsServer = new AwDevToolsServer();
        }
        this.mDevToolsServer.setRemoteDebuggingEnabled(enable);
    }
}