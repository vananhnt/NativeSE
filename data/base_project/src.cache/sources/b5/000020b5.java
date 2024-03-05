package com.android.webview.chromium;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.net.http.ErrorStrings;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsDialogHelper;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.internal.R;
import com.android.org.chromium.android_webview.AwContentsClient;
import com.android.org.chromium.android_webview.AwHttpAuthHandler;
import com.android.org.chromium.android_webview.InterceptedRequestData;
import com.android.org.chromium.android_webview.JsPromptResultReceiver;
import com.android.org.chromium.android_webview.JsResultReceiver;
import com.android.org.chromium.base.ThreadUtils;
import com.android.org.chromium.content.common.TraceEvent;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;

/* loaded from: WebViewContentsClientAdapter.class */
public class WebViewContentsClientAdapter extends AwContentsClient {
    private static final String TAG = "WebViewCallback";
    private static final boolean TRACE = false;
    private final WebView mWebView;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;
    private WebView.FindListener mFindListener;
    private WebView.PictureListener mPictureListener;
    private DownloadListener mDownloadListener;
    private Handler mUiThreadHandler;
    private SoftReference<Bitmap> mCachedDefaultVideoPoster;
    private static final int NEW_WEBVIEW_CREATED = 100;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebViewContentsClientAdapter(WebView webView) {
        if (webView == null) {
            throw new IllegalArgumentException("webView can't be null");
        }
        this.mWebView = webView;
        setWebViewClient(null);
        this.mUiThreadHandler = new Handler() { // from class: com.android.webview.chromium.WebViewContentsClientAdapter.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 100:
                        WebView.WebViewTransport t = (WebView.WebViewTransport) msg.obj;
                        WebView newWebView = t.getWebView();
                        if (newWebView == WebViewContentsClientAdapter.this.mWebView) {
                            throw new IllegalArgumentException("Parent WebView cannot host it's own popup window. Please use WebSettings.setSupportMultipleWindows(false)");
                        }
                        if (newWebView == null || newWebView.copyBackForwardList().getSize() == 0) {
                            WebViewChromium.completeWindowCreation(WebViewContentsClientAdapter.this.mWebView, newWebView);
                            return;
                        }
                        throw new IllegalArgumentException("New WebView for popup window must not have been previously navigated.");
                    default:
                        throw new IllegalStateException();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WebViewContentsClientAdapter$NullWebViewClient.class */
    public static class NullWebViewClient extends WebViewClient {
        NullWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode == 82 || keyCode == 3 || keyCode == 4 || keyCode == 5 || keyCode == 6 || keyCode == 26 || keyCode == 79 || keyCode == 27 || keyCode == 80 || keyCode == 25 || keyCode == 164 || keyCode == 24) {
                return true;
            }
            return false;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                Intent intent = Intent.parseUri(url, 1);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, view.getContext().getPackageName());
                try {
                    view.getContext().startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Log.w(WebViewContentsClientAdapter.TAG, "No application can handle " + url);
                    return false;
                }
            } catch (URISyntaxException ex) {
                Log.w(WebViewContentsClientAdapter.TAG, "Bad URI " + url + ": " + ex.getMessage());
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWebViewClient(WebViewClient client) {
        if (client != null) {
            this.mWebViewClient = client;
        } else {
            this.mWebViewClient = new NullWebViewClient();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWebChromeClient(WebChromeClient client) {
        this.mWebChromeClient = client;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDownloadListener(DownloadListener listener) {
        this.mDownloadListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFindListener(WebView.FindListener listener) {
        this.mFindListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPictureListener(WebView.PictureListener listener) {
        this.mPictureListener = listener;
    }

    public void getVisitedHistory(ValueCallback<String[]> callback) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.getVisitedHistory(callback);
        }
        TraceEvent.end();
    }

    public void doUpdateVisitedHistory(String url, boolean isReload) {
        TraceEvent.begin();
        this.mWebViewClient.doUpdateVisitedHistory(this.mWebView, url, isReload);
        TraceEvent.end();
    }

    public void onProgressChanged(int progress) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onProgressChanged(this.mWebView, progress);
        }
        TraceEvent.end();
    }

    public InterceptedRequestData shouldInterceptRequest(String url) {
        TraceEvent.begin();
        WebResourceResponse response = this.mWebViewClient.shouldInterceptRequest(this.mWebView, url);
        TraceEvent.end();
        if (response == null) {
            return null;
        }
        return new InterceptedRequestData(response.getMimeType(), response.getEncoding(), response.getData());
    }

    public boolean shouldOverrideUrlLoading(String url) {
        TraceEvent.begin();
        boolean result = this.mWebViewClient.shouldOverrideUrlLoading(this.mWebView, url);
        TraceEvent.end();
        return result;
    }

    public void onUnhandledKeyEvent(KeyEvent event) {
        TraceEvent.begin();
        this.mWebViewClient.onUnhandledKeyEvent(this.mWebView, event);
        TraceEvent.end();
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        boolean result;
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            result = this.mWebChromeClient.onConsoleMessage(consoleMessage);
            String message = consoleMessage.message();
            if (result && message != null && message.startsWith("[blocked]")) {
                Log.e(TAG, "Blocked URL: " + message);
            }
        } else {
            result = false;
        }
        TraceEvent.end();
        return result;
    }

    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        if (this.mFindListener == null) {
            return;
        }
        TraceEvent.begin();
        this.mFindListener.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting);
        TraceEvent.end();
    }

    public void onNewPicture(Picture picture) {
        if (this.mPictureListener == null) {
            return;
        }
        TraceEvent.begin();
        this.mPictureListener.onNewPicture(this.mWebView, picture);
        TraceEvent.end();
    }

    public void onLoadResource(String url) {
        TraceEvent.begin();
        this.mWebViewClient.onLoadResource(this.mWebView, url);
        TraceEvent.end();
    }

    public boolean onCreateWindow(boolean isDialog, boolean isUserGesture) {
        boolean result;
        Handler handler = this.mUiThreadHandler;
        WebView webView = this.mWebView;
        webView.getClass();
        Message m = handler.obtainMessage(100, new WebView.WebViewTransport());
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            result = this.mWebChromeClient.onCreateWindow(this.mWebView, isDialog, isUserGesture, m);
        } else {
            result = false;
        }
        TraceEvent.end();
        return result;
    }

    public void onCloseWindow() {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onCloseWindow(this.mWebView);
        }
        TraceEvent.end();
    }

    public void onRequestFocus() {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onRequestFocus(this.mWebView);
        }
        TraceEvent.end();
    }

    public void onReceivedTouchIconUrl(String url, boolean precomposed) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onReceivedTouchIconUrl(this.mWebView, url, precomposed);
        }
        TraceEvent.end();
    }

    public void onReceivedIcon(Bitmap bitmap) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onReceivedIcon(this.mWebView, bitmap);
        }
        TraceEvent.end();
    }

    public void onPageStarted(String url) {
        TraceEvent.begin();
        this.mWebViewClient.onPageStarted(this.mWebView, url, this.mWebView.getFavicon());
        TraceEvent.end();
    }

    public void onPageFinished(String url) {
        TraceEvent.begin();
        this.mWebViewClient.onPageFinished(this.mWebView, url);
        TraceEvent.end();
        if (this.mPictureListener != null) {
            ThreadUtils.postOnUiThreadDelayed(new Runnable() { // from class: com.android.webview.chromium.WebViewContentsClientAdapter.2
                @Override // java.lang.Runnable
                public void run() {
                    UnimplementedWebViewApi.invoke();
                    if (WebViewContentsClientAdapter.this.mPictureListener != null) {
                        WebViewContentsClientAdapter.this.mPictureListener.onNewPicture(WebViewContentsClientAdapter.this.mWebView, new Picture());
                    }
                }
            }, 100L);
        }
    }

    public void onReceivedError(int errorCode, String description, String failingUrl) {
        if (description == null || description.isEmpty()) {
            description = ErrorStrings.getString(errorCode, this.mWebView.getContext());
        }
        TraceEvent.begin();
        this.mWebViewClient.onReceivedError(this.mWebView, errorCode, description, failingUrl);
        TraceEvent.end();
    }

    public void onReceivedTitle(String title) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onReceivedTitle(this.mWebView, title);
        }
        TraceEvent.end();
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        if (event.isSystem()) {
            return true;
        }
        TraceEvent.begin();
        boolean result = this.mWebViewClient.shouldOverrideKeyEvent(this.mWebView, event);
        TraceEvent.end();
        return result;
    }

    public void onStartContentIntent(Context context, String contentUrl) {
        TraceEvent.begin();
        this.mWebViewClient.shouldOverrideUrlLoading(this.mWebView, contentUrl);
        TraceEvent.end();
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        TraceEvent.end();
    }

    public void onGeolocationPermissionsHidePrompt() {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onGeolocationPermissionsHidePrompt();
        }
        TraceEvent.end();
    }

    /* loaded from: WebViewContentsClientAdapter$JsPromptResultReceiverAdapter.class */
    private static class JsPromptResultReceiverAdapter implements JsResult.ResultReceiver {
        private JsPromptResultReceiver mChromePromptResultReceiver;
        private JsResultReceiver mChromeResultReceiver;
        private final JsPromptResult mPromptResult = new JsPromptResult(this);

        public JsPromptResultReceiverAdapter(JsPromptResultReceiver receiver) {
            this.mChromePromptResultReceiver = receiver;
        }

        public JsPromptResultReceiverAdapter(JsResultReceiver receiver) {
            this.mChromeResultReceiver = receiver;
        }

        public JsPromptResult getPromptResult() {
            return this.mPromptResult;
        }

        @Override // android.webkit.JsResult.ResultReceiver
        public void onJsResultComplete(JsResult result) {
            if (this.mChromePromptResultReceiver != null) {
                if (this.mPromptResult.getResult()) {
                    this.mChromePromptResultReceiver.confirm(this.mPromptResult.getStringResult());
                } else {
                    this.mChromePromptResultReceiver.cancel();
                }
            } else if (this.mPromptResult.getResult()) {
                this.mChromeResultReceiver.confirm();
            } else {
                this.mChromeResultReceiver.cancel();
            }
        }
    }

    public void handleJsAlert(String url, String message, JsResultReceiver receiver) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            JsPromptResult res = new JsPromptResultReceiverAdapter(receiver).getPromptResult();
            if (!this.mWebChromeClient.onJsAlert(this.mWebView, url, message, res)) {
                new JsDialogHelper(res, 1, null, message, url).showDialog(this.mWebView.getContext());
            }
        } else {
            receiver.cancel();
        }
        TraceEvent.end();
    }

    public void handleJsBeforeUnload(String url, String message, JsResultReceiver receiver) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            JsPromptResult res = new JsPromptResultReceiverAdapter(receiver).getPromptResult();
            if (!this.mWebChromeClient.onJsBeforeUnload(this.mWebView, url, message, res)) {
                new JsDialogHelper(res, 4, null, message, url).showDialog(this.mWebView.getContext());
            }
        } else {
            receiver.cancel();
        }
        TraceEvent.end();
    }

    public void handleJsConfirm(String url, String message, JsResultReceiver receiver) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            JsPromptResult res = new JsPromptResultReceiverAdapter(receiver).getPromptResult();
            if (!this.mWebChromeClient.onJsConfirm(this.mWebView, url, message, res)) {
                new JsDialogHelper(res, 2, null, message, url).showDialog(this.mWebView.getContext());
            }
        } else {
            receiver.cancel();
        }
        TraceEvent.end();
    }

    public void handleJsPrompt(String url, String message, String defaultValue, JsPromptResultReceiver receiver) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            JsPromptResult res = new JsPromptResultReceiverAdapter(receiver).getPromptResult();
            if (!this.mWebChromeClient.onJsPrompt(this.mWebView, url, message, defaultValue, res)) {
                new JsDialogHelper(res, 3, defaultValue, message, url).showDialog(this.mWebView.getContext());
            }
        } else {
            receiver.cancel();
        }
        TraceEvent.end();
    }

    public void onReceivedHttpAuthRequest(AwHttpAuthHandler handler, String host, String realm) {
        TraceEvent.begin();
        this.mWebViewClient.onReceivedHttpAuthRequest(this.mWebView, new AwHttpAuthHandlerAdapter(handler), host, realm);
        TraceEvent.end();
    }

    public void onReceivedSslError(final ValueCallback<Boolean> callback, SslError error) {
        SslErrorHandler handler = new SslErrorHandler() { // from class: com.android.webview.chromium.WebViewContentsClientAdapter.3
            @Override // android.webkit.SslErrorHandler
            public void proceed() {
                postProceed(true);
            }

            @Override // android.webkit.SslErrorHandler
            public void cancel() {
                postProceed(false);
            }

            private void postProceed(final boolean proceed) {
                post(new Runnable() { // from class: com.android.webview.chromium.WebViewContentsClientAdapter.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        callback.onReceiveValue(Boolean.valueOf(proceed));
                    }
                });
            }
        };
        TraceEvent.begin();
        this.mWebViewClient.onReceivedSslError(this.mWebView, handler, error);
        TraceEvent.end();
    }

    public void onReceivedLoginRequest(String realm, String account, String args) {
        TraceEvent.begin();
        this.mWebViewClient.onReceivedLoginRequest(this.mWebView, realm, account, args);
        TraceEvent.end();
    }

    public void onFormResubmission(Message dontResend, Message resend) {
        TraceEvent.begin();
        this.mWebViewClient.onFormResubmission(this.mWebView, dontResend, resend);
        TraceEvent.end();
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        if (this.mDownloadListener != null) {
            TraceEvent.begin();
            this.mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
            TraceEvent.end();
        }
    }

    public void onScaleChangedScaled(float oldScale, float newScale) {
        TraceEvent.begin();
        this.mWebViewClient.onScaleChanged(this.mWebView, oldScale, newScale);
        TraceEvent.end();
    }

    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback cb) {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onShowCustomView(view, cb);
        }
        TraceEvent.end();
    }

    public void onHideCustomView() {
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            this.mWebChromeClient.onHideCustomView();
        }
        TraceEvent.end();
    }

    protected View getVideoLoadingProgressView() {
        View result;
        TraceEvent.begin();
        if (this.mWebChromeClient != null) {
            result = this.mWebChromeClient.getVideoLoadingProgressView();
        } else {
            result = null;
        }
        TraceEvent.end();
        return result;
    }

    public Bitmap getDefaultVideoPoster() {
        TraceEvent.begin();
        Bitmap result = null;
        if (this.mWebChromeClient != null) {
            result = this.mWebChromeClient.getDefaultVideoPoster();
        }
        if (result == null) {
            if (this.mCachedDefaultVideoPoster != null) {
                result = this.mCachedDefaultVideoPoster.get();
            }
            if (result == null) {
                Bitmap poster = BitmapFactory.decodeResource(this.mWebView.getContext().getResources(), R.drawable.ic_media_video_poster);
                result = Bitmap.createBitmap(poster.getWidth(), poster.getHeight(), poster.getConfig());
                result.eraseColor(Color.GRAY);
                Canvas canvas = new Canvas(result);
                canvas.drawBitmap(poster, 0.0f, 0.0f, (Paint) null);
                this.mCachedDefaultVideoPoster = new SoftReference<>(result);
            }
        }
        TraceEvent.end();
        return result;
    }

    /* loaded from: WebViewContentsClientAdapter$AwHttpAuthHandlerAdapter.class */
    private static class AwHttpAuthHandlerAdapter extends HttpAuthHandler {
        private AwHttpAuthHandler mAwHandler;

        public AwHttpAuthHandlerAdapter(AwHttpAuthHandler awHandler) {
            this.mAwHandler = awHandler;
        }

        @Override // android.webkit.HttpAuthHandler
        public void proceed(String username, String password) {
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }
            this.mAwHandler.proceed(username, password);
        }

        @Override // android.webkit.HttpAuthHandler
        public void cancel() {
            this.mAwHandler.cancel();
        }

        @Override // android.webkit.HttpAuthHandler
        public boolean useHttpAuthUsernamePassword() {
            return this.mAwHandler.isFirstAttempt();
        }
    }
}