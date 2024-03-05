package android.webkit;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Message;
import android.print.PrintDocumentAdapter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;

/* loaded from: WebViewProvider.class */
public interface WebViewProvider {

    /* loaded from: WebViewProvider$ScrollDelegate.class */
    public interface ScrollDelegate {
        int computeHorizontalScrollRange();

        int computeHorizontalScrollOffset();

        int computeVerticalScrollRange();

        int computeVerticalScrollOffset();

        int computeVerticalScrollExtent();

        void computeScroll();
    }

    /* loaded from: WebViewProvider$ViewDelegate.class */
    public interface ViewDelegate {
        boolean shouldDelayChildPressedState();

        AccessibilityNodeProvider getAccessibilityNodeProvider();

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo);

        void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        boolean performAccessibilityAction(int i, Bundle bundle);

        void setOverScrollMode(int i);

        void setScrollBarStyle(int i);

        void onDrawVerticalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4);

        void onOverScrolled(int i, int i2, boolean z, boolean z2);

        void onWindowVisibilityChanged(int i);

        void onDraw(Canvas canvas);

        void setLayoutParams(ViewGroup.LayoutParams layoutParams);

        boolean performLongClick();

        void onConfigurationChanged(Configuration configuration);

        InputConnection onCreateInputConnection(EditorInfo editorInfo);

        boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent);

        boolean onKeyDown(int i, KeyEvent keyEvent);

        boolean onKeyUp(int i, KeyEvent keyEvent);

        void onAttachedToWindow();

        void onDetachedFromWindow();

        void onVisibilityChanged(View view, int i);

        void onWindowFocusChanged(boolean z);

        void onFocusChanged(boolean z, int i, Rect rect);

        boolean setFrame(int i, int i2, int i3, int i4);

        void onSizeChanged(int i, int i2, int i3, int i4);

        void onScrollChanged(int i, int i2, int i3, int i4);

        boolean dispatchKeyEvent(KeyEvent keyEvent);

        boolean onTouchEvent(MotionEvent motionEvent);

        boolean onHoverEvent(MotionEvent motionEvent);

        boolean onGenericMotionEvent(MotionEvent motionEvent);

        boolean onTrackballEvent(MotionEvent motionEvent);

        boolean requestFocus(int i, Rect rect);

        void onMeasure(int i, int i2);

        boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z);

        void setBackgroundColor(int i);

        void setLayerType(int i, Paint paint);

        void preDispatchDraw(Canvas canvas);
    }

    void init(Map<String, Object> map, boolean z);

    void setHorizontalScrollbarOverlay(boolean z);

    void setVerticalScrollbarOverlay(boolean z);

    boolean overlayHorizontalScrollbar();

    boolean overlayVerticalScrollbar();

    int getVisibleTitleHeight();

    SslCertificate getCertificate();

    void setCertificate(SslCertificate sslCertificate);

    void savePassword(String str, String str2, String str3);

    void setHttpAuthUsernamePassword(String str, String str2, String str3, String str4);

    String[] getHttpAuthUsernamePassword(String str, String str2);

    void destroy();

    void setNetworkAvailable(boolean z);

    WebBackForwardList saveState(Bundle bundle);

    boolean savePicture(Bundle bundle, File file);

    boolean restorePicture(Bundle bundle, File file);

    WebBackForwardList restoreState(Bundle bundle);

    void loadUrl(String str, Map<String, String> map);

    void loadUrl(String str);

    void postUrl(String str, byte[] bArr);

    void loadData(String str, String str2, String str3);

    void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5);

    void evaluateJavaScript(String str, ValueCallback<String> valueCallback);

    void saveWebArchive(String str);

    void saveWebArchive(String str, boolean z, ValueCallback<String> valueCallback);

    void stopLoading();

    void reload();

    boolean canGoBack();

    void goBack();

    boolean canGoForward();

    void goForward();

    boolean canGoBackOrForward(int i);

    void goBackOrForward(int i);

    boolean isPrivateBrowsingEnabled();

    boolean pageUp(boolean z);

    boolean pageDown(boolean z);

    void clearView();

    Picture capturePicture();

    PrintDocumentAdapter createPrintDocumentAdapter();

    float getScale();

    void setInitialScale(int i);

    void invokeZoomPicker();

    WebView.HitTestResult getHitTestResult();

    void requestFocusNodeHref(Message message);

    void requestImageRef(Message message);

    String getUrl();

    String getOriginalUrl();

    String getTitle();

    Bitmap getFavicon();

    String getTouchIconUrl();

    int getProgress();

    int getContentHeight();

    int getContentWidth();

    void pauseTimers();

    void resumeTimers();

    void onPause();

    void onResume();

    boolean isPaused();

    void freeMemory();

    void clearCache(boolean z);

    void clearFormData();

    void clearHistory();

    void clearSslPreferences();

    WebBackForwardList copyBackForwardList();

    void setFindListener(WebView.FindListener findListener);

    void findNext(boolean z);

    int findAll(String str);

    void findAllAsync(String str);

    boolean showFindDialog(String str, boolean z);

    void clearMatches();

    void documentHasImages(Message message);

    void setWebViewClient(WebViewClient webViewClient);

    void setDownloadListener(DownloadListener downloadListener);

    void setWebChromeClient(WebChromeClient webChromeClient);

    void setPictureListener(WebView.PictureListener pictureListener);

    void addJavascriptInterface(Object obj, String str);

    void removeJavascriptInterface(String str);

    WebSettings getSettings();

    void setMapTrackballToArrowKeys(boolean z);

    void flingScroll(int i, int i2);

    View getZoomControls();

    boolean canZoomIn();

    boolean canZoomOut();

    boolean zoomIn();

    boolean zoomOut();

    void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i);

    View findHierarchyView(String str, int i);

    ViewDelegate getViewDelegate();

    ScrollDelegate getScrollDelegate();

    void notifyFindDialogDismissed();
}