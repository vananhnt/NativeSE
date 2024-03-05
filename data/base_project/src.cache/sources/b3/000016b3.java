package android.webkit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebStorage;

/* loaded from: WebChromeClient.class */
public class WebChromeClient {

    /* loaded from: WebChromeClient$CustomViewCallback.class */
    public interface CustomViewCallback {
        void onCustomViewHidden();
    }

    public void onProgressChanged(WebView view, int newProgress) {
    }

    public void onReceivedTitle(WebView view, String title) {
    }

    public void onReceivedIcon(WebView view, Bitmap icon) {
    }

    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
    }

    public void onShowCustomView(View view, CustomViewCallback callback) {
    }

    @Deprecated
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
    }

    public void onHideCustomView() {
    }

    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false;
    }

    public void onRequestFocus(WebView view) {
    }

    public void onCloseWindow(WebView window) {
    }

    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return false;
    }

    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return false;
    }

    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return false;
    }

    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return false;
    }

    @Deprecated
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(quota);
    }

    @Deprecated
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(quota);
    }

    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
    }

    public void onGeolocationPermissionsHidePrompt() {
    }

    public boolean onJsTimeout() {
        return true;
    }

    @Deprecated
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
        return false;
    }

    public Bitmap getDefaultVideoPoster() {
        return null;
    }

    public View getVideoLoadingProgressView() {
        return null;
    }

    public void getVisitedHistory(ValueCallback<String[]> callback) {
    }

    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        uploadFile.onReceiveValue(null);
    }

    public void setupAutoFill(Message msg) {
    }
}