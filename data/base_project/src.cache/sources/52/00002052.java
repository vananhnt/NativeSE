package com.android.webview.chromium;

import android.content.ClipDescription;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.print.PrintDocumentAdapter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.HardwareCanvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.DownloadListener;
import android.webkit.FindActionModeCallback;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewProvider;
import android.widget.TextView;
import com.android.internal.R;
import com.android.org.chromium.android_webview.AwContents;
import com.android.org.chromium.android_webview.AwLayoutSizer;
import com.android.org.chromium.android_webview.AwPrintDocumentAdapter;
import com.android.org.chromium.android_webview.AwSettings;
import com.android.org.chromium.base.ThreadUtils;
import com.android.org.chromium.content.browser.LoadUrlParams;
import java.io.BufferedWriter;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WebViewChromium.class */
public class WebViewChromium implements WebViewProvider, WebViewProvider.ScrollDelegate, WebViewProvider.ViewDelegate {
    private static final String TAG;
    WebView mWebView;
    WebView.PrivateAccess mWebViewPrivate;
    private WebViewContentsClientAdapter mContentsClientAdapter;
    private ContentSettingsAdapter mWebSettings;
    private AwContents mAwContents;
    private DrawGLFunctor mGLfunctor;
    private final int mAppTargetSdkVersion;
    private WebViewChromiumFactoryProvider mFactory;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final WebView.HitTestResult mHitTestResult = new WebView.HitTestResult();
    private WebViewChromiumRunQueue mRunQueue = new WebViewChromiumRunQueue();

    static {
        $assertionsDisabled = !WebViewChromium.class.desiredAssertionStatus();
        TAG = WebViewChromium.class.getSimpleName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WebViewChromium$WebViewChromiumRunQueue.class */
    public class WebViewChromiumRunQueue {
        private Queue<Runnable> mQueue = new ConcurrentLinkedQueue();

        public WebViewChromiumRunQueue() {
        }

        public void addTask(Runnable task) {
            this.mQueue.add(task);
            if (WebViewChromium.this.mFactory.hasStarted()) {
                ThreadUtils.runOnUiThread(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.WebViewChromiumRunQueue.1
                    @Override // java.lang.Runnable
                    public void run() {
                        WebViewChromiumRunQueue.this.drainQueue();
                    }
                });
            }
        }

        public void drainQueue() {
            if (this.mQueue == null || this.mQueue.isEmpty()) {
                return;
            }
            Runnable poll = this.mQueue.poll();
            while (true) {
                Runnable task = poll;
                if (task != null) {
                    task.run();
                    poll = this.mQueue.poll();
                } else {
                    return;
                }
            }
        }
    }

    public WebViewChromium(WebViewChromiumFactoryProvider factory, WebView webView, WebView.PrivateAccess webViewPrivate) {
        this.mWebView = webView;
        this.mWebViewPrivate = webViewPrivate;
        this.mAppTargetSdkVersion = this.mWebView.getContext().getApplicationInfo().targetSdkVersion;
        this.mFactory = factory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void completeWindowCreation(WebView parent, WebView child) {
        AwContents parentContents = ((WebViewChromium) parent.getWebViewProvider()).mAwContents;
        AwContents childContents = child == null ? null : ((WebViewChromium) child.getWebViewProvider()).mAwContents;
        parentContents.supplyContentsForPopup(childContents);
    }

    private <T> T runBlockingFuture(FutureTask<T> task) {
        if (this.mFactory.hasStarted()) {
            if (ThreadUtils.runningOnUiThread()) {
                throw new IllegalStateException("This method should only be called off the UI thread");
            }
            this.mRunQueue.addTask(task);
            try {
                return task.get(4L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new RuntimeException("Probable deadlock detected due to WebView API being called on incorrect thread while the UI thread is blocked.", e);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        throw new RuntimeException("Must be started before we block!");
    }

    private void runVoidTaskOnUiThreadBlocking(Runnable r) {
        FutureTask<Void> task = new FutureTask<>(r, null);
        runBlockingFuture(task);
    }

    private <T> T runOnUiThreadBlocking(Callable<T> c) {
        return (T) runBlockingFuture(new FutureTask<>(c));
    }

    @Override // android.webkit.WebViewProvider
    public void init(Map<String, Object> javaScriptInterfaces, final boolean privateBrowsing) {
        if (privateBrowsing) {
            this.mFactory.startYourEngines(true);
            if (this.mAppTargetSdkVersion >= 19) {
                throw new IllegalArgumentException("Private browsing is not supported in WebView.");
            }
            Log.w(TAG, "Private browsing is not supported in WebView.");
            TextView warningLabel = new TextView(this.mWebView.getContext());
            warningLabel.setText(this.mWebView.getContext().getString(R.string.webviewchromium_private_browsing_warning));
            this.mWebView.addView(warningLabel);
        }
        if (this.mAppTargetSdkVersion >= 18) {
            this.mFactory.startYourEngines(false);
            checkThread();
        } else if (!this.mFactory.hasStarted() && Looper.myLooper() == Looper.getMainLooper()) {
            this.mFactory.startYourEngines(true);
        }
        boolean isAccessFromFileURLsGrantedByDefault = this.mAppTargetSdkVersion < 16;
        boolean areLegacyQuirksEnabled = this.mAppTargetSdkVersion < 19;
        this.mContentsClientAdapter = new WebViewContentsClientAdapter(this.mWebView);
        this.mWebSettings = new ContentSettingsAdapter(new AwSettings(this.mWebView.getContext(), isAccessFromFileURLsGrantedByDefault, areLegacyQuirksEnabled));
        this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.1
            @Override // java.lang.Runnable
            public void run() {
                WebViewChromium.this.initForReal();
                if (privateBrowsing) {
                    WebViewChromium.this.destroy();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initForReal() {
        this.mAwContents = new AwContents(this.mFactory.getBrowserContext(), this.mWebView, new InternalAccessAdapter(), this.mContentsClientAdapter, new AwLayoutSizer(), this.mWebSettings.getAwSettings());
        if (this.mAppTargetSdkVersion >= 19) {
            AwContents.setShouldDownloadFavicons();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startYourEngine() {
        this.mRunQueue.drainQueue();
    }

    private RuntimeException createThreadException() {
        return new IllegalStateException("Calling View methods on another thread than the UI thread.");
    }

    private boolean checkNeedsPost() {
        boolean needsPost = (this.mFactory.hasStarted() && ThreadUtils.runningOnUiThread()) ? false : true;
        if (!needsPost && this.mAwContents == null) {
            throw new IllegalStateException("AwContents must be created if we are not posting!");
        }
        return needsPost;
    }

    private void checkThread() {
        if (!ThreadUtils.runningOnUiThread()) {
            final RuntimeException threadViolation = createThreadException();
            ThreadUtils.postOnUiThread(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.2
                @Override // java.lang.Runnable
                public void run() {
                    throw threadViolation;
                }
            });
            throw createThreadException();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void setHorizontalScrollbarOverlay(final boolean overlay) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.3
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setHorizontalScrollbarOverlay(overlay);
                }
            });
        } else {
            this.mAwContents.setHorizontalScrollbarOverlay(overlay);
        }
    }

    @Override // android.webkit.WebViewProvider
    public void setVerticalScrollbarOverlay(final boolean overlay) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.4
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setVerticalScrollbarOverlay(overlay);
                }
            });
        } else {
            this.mAwContents.setVerticalScrollbarOverlay(overlay);
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean overlayHorizontalScrollbar() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.5
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.overlayHorizontalScrollbar());
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.overlayHorizontalScrollbar();
    }

    @Override // android.webkit.WebViewProvider
    public boolean overlayVerticalScrollbar() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.6
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.overlayVerticalScrollbar());
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.overlayVerticalScrollbar();
    }

    @Override // android.webkit.WebViewProvider
    public int getVisibleTitleHeight() {
        return 0;
    }

    @Override // android.webkit.WebViewProvider
    public SslCertificate getCertificate() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            SslCertificate ret = (SslCertificate) runOnUiThreadBlocking(new Callable<SslCertificate>() { // from class: com.android.webview.chromium.WebViewChromium.7
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public SslCertificate call() {
                    return WebViewChromium.this.getCertificate();
                }
            });
            return ret;
        }
        return this.mAwContents.getCertificate();
    }

    @Override // android.webkit.WebViewProvider
    public void setCertificate(SslCertificate certificate) {
    }

    @Override // android.webkit.WebViewProvider
    public void savePassword(String host, String username, String password) {
    }

    @Override // android.webkit.WebViewProvider
    public void setHttpAuthUsernamePassword(final String host, final String realm, final String username, final String password) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.8
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setHttpAuthUsernamePassword(host, realm, username, password);
                }
            });
        } else {
            this.mAwContents.setHttpAuthUsernamePassword(host, realm, username, password);
        }
    }

    @Override // android.webkit.WebViewProvider
    public String[] getHttpAuthUsernamePassword(final String host, final String realm) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            String[] ret = (String[]) runOnUiThreadBlocking(new Callable<String[]>() { // from class: com.android.webview.chromium.WebViewChromium.9
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public String[] call() {
                    return WebViewChromium.this.getHttpAuthUsernamePassword(host, realm);
                }
            });
            return ret;
        }
        return this.mAwContents.getHttpAuthUsernamePassword(host, realm);
    }

    @Override // android.webkit.WebViewProvider
    public void destroy() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.10
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.destroy();
                }
            });
            return;
        }
        this.mAwContents.destroy();
        if (this.mGLfunctor != null) {
            this.mGLfunctor.destroy();
            this.mGLfunctor = null;
        }
    }

    @Override // android.webkit.WebViewProvider
    public void setNetworkAvailable(final boolean networkUp) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.11
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setNetworkAvailable(networkUp);
                }
            });
        } else {
            this.mAwContents.setNetworkAvailable(networkUp);
        }
    }

    @Override // android.webkit.WebViewProvider
    public WebBackForwardList saveState(final Bundle outState) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            WebBackForwardList ret = (WebBackForwardList) runOnUiThreadBlocking(new Callable<WebBackForwardList>() { // from class: com.android.webview.chromium.WebViewChromium.12
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public WebBackForwardList call() {
                    return WebViewChromium.this.saveState(outState);
                }
            });
            return ret;
        } else if (outState != null && this.mAwContents.saveState(outState)) {
            return copyBackForwardList();
        } else {
            return null;
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean savePicture(Bundle b, File dest) {
        return false;
    }

    @Override // android.webkit.WebViewProvider
    public boolean restorePicture(Bundle b, File src) {
        return false;
    }

    @Override // android.webkit.WebViewProvider
    public WebBackForwardList restoreState(final Bundle inState) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            WebBackForwardList ret = (WebBackForwardList) runOnUiThreadBlocking(new Callable<WebBackForwardList>() { // from class: com.android.webview.chromium.WebViewChromium.13
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public WebBackForwardList call() {
                    return WebViewChromium.this.restoreState(inState);
                }
            });
            return ret;
        } else if (inState != null && this.mAwContents.restoreState(inState)) {
            return copyBackForwardList();
        } else {
            return null;
        }
    }

    @Override // android.webkit.WebViewProvider
    public void loadUrl(final String url, Map<String, String> additionalHttpHeaders) {
        if (this.mAppTargetSdkVersion < 19 && url != null && url.startsWith("javascript:")) {
            this.mFactory.startYourEngines(true);
            if (checkNeedsPost()) {
                this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.14
                    @Override // java.lang.Runnable
                    public void run() {
                        WebViewChromium.this.mAwContents.evaluateJavaScriptEvenIfNotYetNavigated(url.substring("javascript:".length()));
                    }
                });
                return;
            } else {
                this.mAwContents.evaluateJavaScriptEvenIfNotYetNavigated(url.substring("javascript:".length()));
                return;
            }
        }
        LoadUrlParams params = new LoadUrlParams(url);
        if (additionalHttpHeaders != null) {
            params.setExtraHeaders(additionalHttpHeaders);
        }
        loadUrlOnUiThread(params);
    }

    @Override // android.webkit.WebViewProvider
    public void loadUrl(String url) {
        if (url == null) {
            return;
        }
        loadUrl(url, null);
    }

    @Override // android.webkit.WebViewProvider
    public void postUrl(String url, byte[] postData) {
        LoadUrlParams params = LoadUrlParams.createLoadHttpPostParams(url, postData);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        params.setExtraHeaders(headers);
        loadUrlOnUiThread(params);
    }

    private static String fixupMimeType(String mimeType) {
        return TextUtils.isEmpty(mimeType) ? ClipDescription.MIMETYPE_TEXT_HTML : mimeType;
    }

    private static String fixupData(String data) {
        return TextUtils.isEmpty(data) ? "" : data;
    }

    private static String fixupBase(String url) {
        return TextUtils.isEmpty(url) ? "about:blank" : url;
    }

    private static String fixupHistory(String url) {
        return TextUtils.isEmpty(url) ? "about:blank" : url;
    }

    private static boolean isBase64Encoded(String encoding) {
        return "base64".equals(encoding);
    }

    @Override // android.webkit.WebViewProvider
    public void loadData(String data, String mimeType, String encoding) {
        loadUrlOnUiThread(LoadUrlParams.createLoadDataParams(fixupData(data), fixupMimeType(mimeType), isBase64Encoded(encoding)));
    }

    @Override // android.webkit.WebViewProvider
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        LoadUrlParams loadUrlParams;
        String data2 = fixupData(data);
        String mimeType2 = fixupMimeType(mimeType);
        String baseUrl2 = fixupBase(baseUrl);
        String historyUrl2 = fixupHistory(historyUrl);
        if (baseUrl2.startsWith("data:")) {
            boolean isBase64 = isBase64Encoded(encoding);
            loadUrlParams = LoadUrlParams.createLoadDataParamsWithBaseUrl(data2, mimeType2, isBase64, baseUrl2, historyUrl2, isBase64 ? null : encoding);
        } else {
            try {
                loadUrlParams = LoadUrlParams.createLoadDataParamsWithBaseUrl(Base64.encodeToString(data2.getBytes("utf-8"), 0), mimeType2, true, baseUrl2, historyUrl2, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.wtf(TAG, "Unable to load data string " + data2, e);
                return;
            }
        }
        loadUrlOnUiThread(loadUrlParams);
        final String finalBaseUrl = loadUrlParams.getBaseUrl();
        ThreadUtils.postOnUiThread(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.15
            @Override // java.lang.Runnable
            public void run() {
                WebViewChromium.this.mContentsClientAdapter.onPageStarted(finalBaseUrl);
            }
        });
    }

    private void loadUrlOnUiThread(final LoadUrlParams loadUrlParams) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            if (!$assertionsDisabled && this.mAppTargetSdkVersion >= 18) {
                throw new AssertionError();
            }
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.16
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.mAwContents.loadUrl(loadUrlParams);
                }
            });
            return;
        }
        this.mAwContents.loadUrl(loadUrlParams);
    }

    @Override // android.webkit.WebViewProvider
    public void evaluateJavaScript(String script, ValueCallback<String> resultCallback) {
        checkThread();
        this.mAwContents.evaluateJavaScript(script, resultCallback);
    }

    @Override // android.webkit.WebViewProvider
    public void saveWebArchive(String filename) {
        saveWebArchive(filename, false, null);
    }

    @Override // android.webkit.WebViewProvider
    public void saveWebArchive(final String basename, final boolean autoname, final ValueCallback<String> callback) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.17
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.saveWebArchive(basename, autoname, callback);
                }
            });
        } else {
            this.mAwContents.saveWebArchive(basename, autoname, callback);
        }
    }

    @Override // android.webkit.WebViewProvider
    public void stopLoading() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.18
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.stopLoading();
                }
            });
        } else {
            this.mAwContents.stopLoading();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void reload() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.19
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.reload();
                }
            });
        } else {
            this.mAwContents.reload();
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean canGoBack() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.20
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.canGoBack());
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.canGoBack();
    }

    @Override // android.webkit.WebViewProvider
    public void goBack() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.21
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.goBack();
                }
            });
        } else {
            this.mAwContents.goBack();
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean canGoForward() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.22
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.canGoForward());
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.canGoForward();
    }

    @Override // android.webkit.WebViewProvider
    public void goForward() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.23
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.goForward();
                }
            });
        } else {
            this.mAwContents.goForward();
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean canGoBackOrForward(final int steps) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.24
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.canGoBackOrForward(steps));
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.canGoBackOrForward(steps);
    }

    @Override // android.webkit.WebViewProvider
    public void goBackOrForward(final int steps) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.25
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.goBackOrForward(steps);
                }
            });
        } else {
            this.mAwContents.goBackOrForward(steps);
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean isPrivateBrowsingEnabled() {
        return false;
    }

    @Override // android.webkit.WebViewProvider
    public boolean pageUp(final boolean top) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.26
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.pageUp(top));
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.pageUp(top);
    }

    @Override // android.webkit.WebViewProvider
    public boolean pageDown(final boolean bottom) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.27
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.pageDown(bottom));
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.pageDown(bottom);
    }

    @Override // android.webkit.WebViewProvider
    public void clearView() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.28
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearView();
                }
            });
        } else {
            this.mAwContents.clearView();
        }
    }

    @Override // android.webkit.WebViewProvider
    public Picture capturePicture() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Picture ret = (Picture) runOnUiThreadBlocking(new Callable<Picture>() { // from class: com.android.webview.chromium.WebViewChromium.29
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Picture call() {
                    return WebViewChromium.this.capturePicture();
                }
            });
            return ret;
        }
        return this.mAwContents.capturePicture();
    }

    @Override // android.webkit.WebViewProvider
    public PrintDocumentAdapter createPrintDocumentAdapter() {
        checkThread();
        return new AwPrintDocumentAdapter(this.mAwContents.getPdfExporter());
    }

    @Override // android.webkit.WebViewProvider
    public float getScale() {
        this.mFactory.startYourEngines(true);
        return this.mAwContents.getScale();
    }

    @Override // android.webkit.WebViewProvider
    public void setInitialScale(int scaleInPercent) {
        this.mWebSettings.getAwSettings().setInitialPageScale(scaleInPercent);
    }

    @Override // android.webkit.WebViewProvider
    public void invokeZoomPicker() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.30
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.invokeZoomPicker();
                }
            });
        } else {
            this.mAwContents.invokeZoomPicker();
        }
    }

    @Override // android.webkit.WebViewProvider
    public WebView.HitTestResult getHitTestResult() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            WebView.HitTestResult ret = (WebView.HitTestResult) runOnUiThreadBlocking(new Callable<WebView.HitTestResult>() { // from class: com.android.webview.chromium.WebViewChromium.31
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public WebView.HitTestResult call() {
                    return WebViewChromium.this.getHitTestResult();
                }
            });
            return ret;
        }
        AwContents.HitTestData data = this.mAwContents.getLastHitTestResult();
        this.mHitTestResult.setType(data.hitTestResultType);
        this.mHitTestResult.setExtra(data.hitTestResultExtraData);
        return this.mHitTestResult;
    }

    @Override // android.webkit.WebViewProvider
    public void requestFocusNodeHref(final Message hrefMsg) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.32
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.requestFocusNodeHref(hrefMsg);
                }
            });
        } else {
            this.mAwContents.requestFocusNodeHref(hrefMsg);
        }
    }

    @Override // android.webkit.WebViewProvider
    public void requestImageRef(final Message msg) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.33
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.requestImageRef(msg);
                }
            });
        } else {
            this.mAwContents.requestImageRef(msg);
        }
    }

    @Override // android.webkit.WebViewProvider
    public String getUrl() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            String ret = (String) runOnUiThreadBlocking(new Callable<String>() { // from class: com.android.webview.chromium.WebViewChromium.34
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public String call() {
                    return WebViewChromium.this.getUrl();
                }
            });
            return ret;
        }
        String url = this.mAwContents.getUrl();
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        return url;
    }

    @Override // android.webkit.WebViewProvider
    public String getOriginalUrl() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            String ret = (String) runOnUiThreadBlocking(new Callable<String>() { // from class: com.android.webview.chromium.WebViewChromium.35
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public String call() {
                    return WebViewChromium.this.getOriginalUrl();
                }
            });
            return ret;
        }
        String url = this.mAwContents.getOriginalUrl();
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        return url;
    }

    @Override // android.webkit.WebViewProvider
    public String getTitle() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            String ret = (String) runOnUiThreadBlocking(new Callable<String>() { // from class: com.android.webview.chromium.WebViewChromium.36
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public String call() {
                    return WebViewChromium.this.getTitle();
                }
            });
            return ret;
        }
        return this.mAwContents.getTitle();
    }

    @Override // android.webkit.WebViewProvider
    public Bitmap getFavicon() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Bitmap ret = (Bitmap) runOnUiThreadBlocking(new Callable<Bitmap>() { // from class: com.android.webview.chromium.WebViewChromium.37
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Bitmap call() {
                    return WebViewChromium.this.getFavicon();
                }
            });
            return ret;
        }
        return this.mAwContents.getFavicon();
    }

    @Override // android.webkit.WebViewProvider
    public String getTouchIconUrl() {
        return null;
    }

    @Override // android.webkit.WebViewProvider
    public int getProgress() {
        if (this.mAwContents == null) {
            return 100;
        }
        return this.mAwContents.getMostRecentProgress();
    }

    @Override // android.webkit.WebViewProvider
    public int getContentHeight() {
        if (this.mAwContents == null) {
            return 0;
        }
        return this.mAwContents.getContentHeightCss();
    }

    @Override // android.webkit.WebViewProvider
    public int getContentWidth() {
        if (this.mAwContents == null) {
            return 0;
        }
        return this.mAwContents.getContentWidthCss();
    }

    @Override // android.webkit.WebViewProvider
    public void pauseTimers() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.38
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.pauseTimers();
                }
            });
        } else {
            this.mAwContents.pauseTimers();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void resumeTimers() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.39
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.resumeTimers();
                }
            });
        } else {
            this.mAwContents.resumeTimers();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void onPause() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.40
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onPause();
                }
            });
        } else {
            this.mAwContents.onPause();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void onResume() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.41
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onResume();
                }
            });
        } else {
            this.mAwContents.onResume();
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean isPaused() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            Boolean ret = (Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.42
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.isPaused());
                }
            });
            return ret.booleanValue();
        }
        return this.mAwContents.isPaused();
    }

    @Override // android.webkit.WebViewProvider
    public void freeMemory() {
    }

    @Override // android.webkit.WebViewProvider
    public void clearCache(final boolean includeDiskFiles) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.43
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearCache(includeDiskFiles);
                }
            });
        } else {
            this.mAwContents.clearCache(includeDiskFiles);
        }
    }

    @Override // android.webkit.WebViewProvider
    public void clearFormData() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.44
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearFormData();
                }
            });
        } else {
            this.mAwContents.hideAutofillPopup();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void clearHistory() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.45
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearHistory();
                }
            });
        } else {
            this.mAwContents.clearHistory();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void clearSslPreferences() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.46
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearSslPreferences();
                }
            });
        } else {
            this.mAwContents.clearSslPreferences();
        }
    }

    @Override // android.webkit.WebViewProvider
    public WebBackForwardList copyBackForwardList() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            WebBackForwardList ret = (WebBackForwardList) runOnUiThreadBlocking(new Callable<WebBackForwardList>() { // from class: com.android.webview.chromium.WebViewChromium.47
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public WebBackForwardList call() {
                    return WebViewChromium.this.copyBackForwardList();
                }
            });
            return ret;
        }
        return new WebBackForwardListChromium(this.mAwContents.getNavigationHistory());
    }

    @Override // android.webkit.WebViewProvider
    public void setFindListener(WebView.FindListener listener) {
        this.mContentsClientAdapter.setFindListener(listener);
    }

    @Override // android.webkit.WebViewProvider
    public void findNext(final boolean forwards) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.48
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.findNext(forwards);
                }
            });
        } else {
            this.mAwContents.findNext(forwards);
        }
    }

    @Override // android.webkit.WebViewProvider
    public int findAll(String searchString) {
        findAllAsync(searchString);
        return 0;
    }

    @Override // android.webkit.WebViewProvider
    public void findAllAsync(final String searchString) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.49
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.findAllAsync(searchString);
                }
            });
        } else {
            this.mAwContents.findAllAsync(searchString);
        }
    }

    @Override // android.webkit.WebViewProvider
    public boolean showFindDialog(String text, boolean showIme) {
        FindActionModeCallback findAction;
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost() || this.mWebView.getParent() == null || (findAction = new FindActionModeCallback(this.mWebView.getContext())) == null) {
            return false;
        }
        this.mWebView.startActionMode(findAction);
        findAction.setWebView(this.mWebView);
        if (showIme) {
            findAction.showSoftInput();
        }
        if (text != null) {
            findAction.setText(text);
            findAction.findAll();
            return true;
        }
        return true;
    }

    @Override // android.webkit.WebViewProvider
    public void notifyFindDialogDismissed() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.50
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.notifyFindDialogDismissed();
                }
            });
        } else {
            clearMatches();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void clearMatches() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.51
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.clearMatches();
                }
            });
        } else {
            this.mAwContents.clearMatches();
        }
    }

    @Override // android.webkit.WebViewProvider
    public void documentHasImages(final Message response) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.52
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.documentHasImages(response);
                }
            });
        } else {
            this.mAwContents.documentHasImages(response);
        }
    }

    @Override // android.webkit.WebViewProvider
    public void setWebViewClient(WebViewClient client) {
        this.mContentsClientAdapter.setWebViewClient(client);
    }

    @Override // android.webkit.WebViewProvider
    public void setDownloadListener(DownloadListener listener) {
        this.mContentsClientAdapter.setDownloadListener(listener);
    }

    @Override // android.webkit.WebViewProvider
    public void setWebChromeClient(WebChromeClient client) {
        this.mContentsClientAdapter.setWebChromeClient(client);
    }

    @Override // android.webkit.WebViewProvider
    public void setPictureListener(final WebView.PictureListener listener) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.53
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setPictureListener(listener);
                }
            });
            return;
        }
        this.mContentsClientAdapter.setPictureListener(listener);
        this.mAwContents.enableOnNewPicture(listener != null, this.mAppTargetSdkVersion >= 18);
    }

    @Override // android.webkit.WebViewProvider
    public void addJavascriptInterface(final Object obj, final String interfaceName) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.54
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.addJavascriptInterface(obj, interfaceName);
                }
            });
            return;
        }
        Class<? extends Annotation> requiredAnnotation = null;
        if (this.mAppTargetSdkVersion >= 17) {
            requiredAnnotation = JavascriptInterface.class;
        }
        this.mAwContents.addPossiblyUnsafeJavascriptInterface(obj, interfaceName, requiredAnnotation);
    }

    @Override // android.webkit.WebViewProvider
    public void removeJavascriptInterface(final String interfaceName) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.55
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.removeJavascriptInterface(interfaceName);
                }
            });
        } else {
            this.mAwContents.removeJavascriptInterface(interfaceName);
        }
    }

    @Override // android.webkit.WebViewProvider
    public WebSettings getSettings() {
        return this.mWebSettings;
    }

    @Override // android.webkit.WebViewProvider
    public void setMapTrackballToArrowKeys(boolean setMap) {
    }

    @Override // android.webkit.WebViewProvider
    public void flingScroll(final int vx, final int vy) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.56
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.flingScroll(vx, vy);
                }
            });
        } else {
            this.mAwContents.flingScroll(vx, vy);
        }
    }

    @Override // android.webkit.WebViewProvider
    public View getZoomControls() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            return null;
        }
        Log.w(TAG, "WebView doesn't support getZoomControls");
        if (this.mAwContents.getSettings().supportZoom()) {
            return new View(this.mWebView.getContext());
        }
        return null;
    }

    @Override // android.webkit.WebViewProvider
    public boolean canZoomIn() {
        if (checkNeedsPost()) {
            return false;
        }
        return this.mAwContents.canZoomIn();
    }

    @Override // android.webkit.WebViewProvider
    public boolean canZoomOut() {
        if (checkNeedsPost()) {
            return false;
        }
        return this.mAwContents.canZoomOut();
    }

    @Override // android.webkit.WebViewProvider
    public boolean zoomIn() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.57
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.zoomIn());
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.zoomIn();
    }

    @Override // android.webkit.WebViewProvider
    public boolean zoomOut() {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.58
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.zoomOut());
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.zoomOut();
    }

    @Override // android.webkit.WebViewProvider
    public void dumpViewHierarchyWithProperties(BufferedWriter out, int level) {
    }

    @Override // android.webkit.WebViewProvider
    public View findHierarchyView(String className, int hashCode) {
        return null;
    }

    @Override // android.webkit.WebViewProvider
    public WebViewProvider.ViewDelegate getViewDelegate() {
        return this;
    }

    @Override // android.webkit.WebViewProvider
    public WebViewProvider.ScrollDelegate getScrollDelegate() {
        return this;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean shouldDelayChildPressedState() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.59
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.shouldDelayChildPressedState());
                }
            })).booleanValue();
            return ret;
        }
        return true;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            AccessibilityNodeProvider ret = (AccessibilityNodeProvider) runOnUiThreadBlocking(new Callable<AccessibilityNodeProvider>() { // from class: com.android.webview.chromium.WebViewChromium.60
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public AccessibilityNodeProvider call() {
                    return WebViewChromium.this.getAccessibilityNodeProvider();
                }
            });
            return ret;
        }
        return this.mAwContents.getAccessibilityNodeProvider();
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo info) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            runVoidTaskOnUiThreadBlocking(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.61
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onInitializeAccessibilityNodeInfo(info);
                }
            });
        } else {
            this.mAwContents.onInitializeAccessibilityNodeInfo(info);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onInitializeAccessibilityEvent(final AccessibilityEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            runVoidTaskOnUiThreadBlocking(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.62
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onInitializeAccessibilityEvent(event);
                }
            });
        } else {
            this.mAwContents.onInitializeAccessibilityEvent(event);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean performAccessibilityAction(final int action, final Bundle arguments) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.63
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.performAccessibilityAction(action, arguments));
                }
            })).booleanValue();
            return ret;
        } else if (this.mAwContents.supportsAccessibilityAction(action)) {
            return this.mAwContents.performAccessibilityAction(action, arguments);
        } else {
            return this.mWebViewPrivate.super_performAccessibilityAction(action, arguments);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void setOverScrollMode(final int mode) {
        if (this.mAwContents == null) {
            return;
        }
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.64
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setOverScrollMode(mode);
                }
            });
        } else {
            this.mAwContents.setOverScrollMode(mode);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void setScrollBarStyle(final int style) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.65
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setScrollBarStyle(style);
                }
            });
        } else {
            this.mAwContents.setScrollBarStyle(style);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        this.mWebViewPrivate.super_onDrawVerticalScrollBar(canvas, scrollBar, l, t, r, b);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onOverScrolled(final int scrollX, final int scrollY, final boolean clampedX, final boolean clampedY) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.66
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
                }
            });
        } else {
            this.mAwContents.onContainerViewOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onWindowVisibilityChanged(final int visibility) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.67
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onWindowVisibilityChanged(visibility);
                }
            });
        } else {
            this.mAwContents.onWindowVisibilityChanged(visibility);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onDraw(final Canvas canvas) {
        this.mFactory.startYourEngines(true);
        if (checkNeedsPost()) {
            runVoidTaskOnUiThreadBlocking(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.68
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onDraw(canvas);
                }
            });
        } else {
            this.mAwContents.onDraw(canvas);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        this.mFactory.startYourEngines(false);
        checkThread();
        this.mWebViewPrivate.super_setLayoutParams(layoutParams);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean performLongClick() {
        if (this.mWebView.getParent() != null) {
            return this.mWebViewPrivate.super_performLongClick();
        }
        return false;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onConfigurationChanged(final Configuration newConfig) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.69
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onConfigurationChanged(newConfig);
                }
            });
        } else {
            this.mAwContents.onConfigurationChanged(newConfig);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            return null;
        }
        return this.mAwContents.onCreateInputConnection(outAttrs);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onKeyMultiple(final int keyCode, final int repeatCount, final KeyEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.70
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onKeyMultiple(keyCode, repeatCount, event));
                }
            })).booleanValue();
            return ret;
        }
        UnimplementedWebViewApi.invoke();
        return false;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.71
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onKeyDown(keyCode, event));
                }
            })).booleanValue();
            return ret;
        }
        UnimplementedWebViewApi.invoke();
        return false;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.72
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onKeyUp(keyCode, event));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.onKeyUp(keyCode, event);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onAttachedToWindow() {
        this.mFactory.startYourEngines(false);
        checkThread();
        this.mAwContents.onAttachedToWindow();
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onDetachedFromWindow() {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.73
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onDetachedFromWindow();
                }
            });
            return;
        }
        Runnable detachAwContents = new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.74
            @Override // java.lang.Runnable
            public void run() {
                WebViewChromium.this.mAwContents.onDetachedFromWindow();
            }
        };
        if (this.mGLfunctor == null || !this.mWebView.executeHardwareAction(detachAwContents)) {
            detachAwContents.run();
        }
        if (this.mGLfunctor != null) {
            this.mGLfunctor.detach();
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onVisibilityChanged(final View changedView, final int visibility) {
        if (this.mAwContents == null) {
            return;
        }
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.75
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onVisibilityChanged(changedView, visibility);
                }
            });
        } else {
            this.mAwContents.onVisibilityChanged(changedView, visibility);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onWindowFocusChanged(final boolean hasWindowFocus) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.76
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onWindowFocusChanged(hasWindowFocus);
                }
            });
        } else {
            this.mAwContents.onWindowFocusChanged(hasWindowFocus);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onFocusChanged(final boolean focused, final int direction, final Rect previouslyFocusedRect) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.77
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onFocusChanged(focused, direction, previouslyFocusedRect);
                }
            });
        } else {
            this.mAwContents.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean setFrame(int left, int top, int right, int bottom) {
        return this.mWebViewPrivate.super_setFrame(left, top, right, bottom);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onSizeChanged(final int w, final int h, final int ow, final int oh) {
        if (checkNeedsPost()) {
            this.mRunQueue.addTask(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.78
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onSizeChanged(w, h, ow, oh);
                }
            });
        } else {
            this.mAwContents.onSizeChanged(w, h, ow, oh);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean dispatchKeyEvent(final KeyEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.79
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.dispatchKeyEvent(event));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.dispatchKeyEvent(event);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onTouchEvent(final MotionEvent ev) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.80
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onTouchEvent(ev));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.onTouchEvent(ev);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onHoverEvent(final MotionEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.81
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onHoverEvent(event));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.onHoverEvent(event);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onGenericMotionEvent(final MotionEvent event) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.82
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.onGenericMotionEvent(event));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.onGenericMotionEvent(event);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean onTrackballEvent(MotionEvent ev) {
        return false;
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean requestFocus(final int direction, final Rect previouslyFocusedRect) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.83
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.requestFocus(direction, previouslyFocusedRect));
                }
            })).booleanValue();
            return ret;
        }
        this.mAwContents.requestFocus();
        return this.mWebViewPrivate.super_requestFocus(direction, previouslyFocusedRect);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            runVoidTaskOnUiThreadBlocking(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.84
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            });
        } else {
            this.mAwContents.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public boolean requestChildRectangleOnScreen(final View child, final Rect rect, final boolean immediate) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            boolean ret = ((Boolean) runOnUiThreadBlocking(new Callable<Boolean>() { // from class: com.android.webview.chromium.WebViewChromium.85
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() {
                    return Boolean.valueOf(WebViewChromium.this.requestChildRectangleOnScreen(child, rect, immediate));
                }
            })).booleanValue();
            return ret;
        }
        return this.mAwContents.requestChildRectangleOnScreen(child, rect, immediate);
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void setBackgroundColor(final int color) {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            ThreadUtils.postOnUiThread(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.86
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.setBackgroundColor(color);
                }
            });
        } else {
            this.mAwContents.setBackgroundColor(color);
        }
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void setLayerType(int layerType, Paint paint) {
    }

    @Override // android.webkit.WebViewProvider.ViewDelegate
    public void preDispatchDraw(Canvas canvas) {
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public int computeHorizontalScrollRange() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            int ret = ((Integer) runOnUiThreadBlocking(new Callable<Integer>() { // from class: com.android.webview.chromium.WebViewChromium.87
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Integer call() {
                    return Integer.valueOf(WebViewChromium.this.computeHorizontalScrollRange());
                }
            })).intValue();
            return ret;
        }
        return this.mAwContents.computeHorizontalScrollRange();
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public int computeHorizontalScrollOffset() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            int ret = ((Integer) runOnUiThreadBlocking(new Callable<Integer>() { // from class: com.android.webview.chromium.WebViewChromium.88
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Integer call() {
                    return Integer.valueOf(WebViewChromium.this.computeHorizontalScrollOffset());
                }
            })).intValue();
            return ret;
        }
        return this.mAwContents.computeHorizontalScrollOffset();
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public int computeVerticalScrollRange() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            int ret = ((Integer) runOnUiThreadBlocking(new Callable<Integer>() { // from class: com.android.webview.chromium.WebViewChromium.89
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Integer call() {
                    return Integer.valueOf(WebViewChromium.this.computeVerticalScrollRange());
                }
            })).intValue();
            return ret;
        }
        return this.mAwContents.computeVerticalScrollRange();
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public int computeVerticalScrollOffset() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            int ret = ((Integer) runOnUiThreadBlocking(new Callable<Integer>() { // from class: com.android.webview.chromium.WebViewChromium.90
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Integer call() {
                    return Integer.valueOf(WebViewChromium.this.computeVerticalScrollOffset());
                }
            })).intValue();
            return ret;
        }
        return this.mAwContents.computeVerticalScrollOffset();
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public int computeVerticalScrollExtent() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            int ret = ((Integer) runOnUiThreadBlocking(new Callable<Integer>() { // from class: com.android.webview.chromium.WebViewChromium.91
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Integer call() {
                    return Integer.valueOf(WebViewChromium.this.computeVerticalScrollExtent());
                }
            })).intValue();
            return ret;
        }
        return this.mAwContents.computeVerticalScrollExtent();
    }

    @Override // android.webkit.WebViewProvider.ScrollDelegate
    public void computeScroll() {
        this.mFactory.startYourEngines(false);
        if (checkNeedsPost()) {
            runVoidTaskOnUiThreadBlocking(new Runnable() { // from class: com.android.webview.chromium.WebViewChromium.92
                @Override // java.lang.Runnable
                public void run() {
                    WebViewChromium.this.computeScroll();
                }
            });
        } else {
            this.mAwContents.computeScroll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WebViewChromium$InternalAccessAdapter.class */
    public class InternalAccessAdapter implements AwContents.InternalAccessDelegate {
        private InternalAccessAdapter() {
        }

        public boolean drawChild(Canvas arg0, View arg1, long arg2) {
            UnimplementedWebViewApi.invoke();
            return false;
        }

        public boolean super_onKeyUp(int arg0, KeyEvent arg1) {
            return false;
        }

        public boolean super_dispatchKeyEventPreIme(KeyEvent arg0) {
            UnimplementedWebViewApi.invoke();
            return false;
        }

        public boolean super_dispatchKeyEvent(KeyEvent event) {
            return WebViewChromium.this.mWebViewPrivate.super_dispatchKeyEvent(event);
        }

        public boolean super_onGenericMotionEvent(MotionEvent arg0) {
            return WebViewChromium.this.mWebViewPrivate.super_onGenericMotionEvent(arg0);
        }

        public void super_onConfigurationChanged(Configuration arg0) {
        }

        public int super_getScrollBarStyle() {
            return WebViewChromium.this.mWebViewPrivate.super_getScrollBarStyle();
        }

        public boolean awakenScrollBars() {
            WebViewChromium.this.mWebViewPrivate.awakenScrollBars(0);
            return true;
        }

        public boolean super_awakenScrollBars(int arg0, boolean arg1) {
            UnimplementedWebViewApi.invoke();
            return false;
        }

        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            WebViewChromium.this.mWebViewPrivate.setScrollXRaw(l);
            WebViewChromium.this.mWebViewPrivate.setScrollYRaw(t);
            WebViewChromium.this.mWebViewPrivate.onScrollChanged(l, t, oldl, oldt);
        }

        public void overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            WebViewChromium.this.mWebViewPrivate.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }

        public void super_scrollTo(int scrollX, int scrollY) {
            WebViewChromium.this.mWebViewPrivate.super_scrollTo(scrollX, scrollY);
        }

        public void setMeasuredDimension(int measuredWidth, int measuredHeight) {
            WebViewChromium.this.mWebViewPrivate.setMeasuredDimension(measuredWidth, measuredHeight);
        }

        public boolean requestDrawGL(Canvas canvas) {
            if (WebViewChromium.this.mGLfunctor == null) {
                WebViewChromium.this.mGLfunctor = new DrawGLFunctor(WebViewChromium.this.mAwContents.getAwDrawGLViewContext());
            }
            return WebViewChromium.this.mGLfunctor.requestDrawGL((HardwareCanvas) canvas, WebViewChromium.this.mWebView.getViewRootImpl());
        }
    }
}