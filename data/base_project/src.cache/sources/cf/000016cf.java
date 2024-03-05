package android.webkit;

import android.os.StrictMode;
import android.util.AndroidRuntimeException;
import android.util.Log;

/* loaded from: WebViewFactory.class */
public final class WebViewFactory {
    private static final String CHROMIUM_WEBVIEW_FACTORY = "com.android.webview.chromium.WebViewChromiumFactoryProvider";
    private static final String LOGTAG = "WebViewFactory";
    private static final boolean DEBUG = false;
    private static WebViewFactoryProvider sProviderInstance;
    private static final Object sProviderLock = new Object();

    static /* synthetic */ Class access$000() throws ClassNotFoundException {
        return getFactoryClass();
    }

    /* loaded from: WebViewFactory$Preloader.class */
    private static class Preloader {
        static WebViewFactoryProvider sPreloadedProvider;

        private Preloader() {
        }

        static {
            try {
                sPreloadedProvider = (WebViewFactoryProvider) WebViewFactory.access$000().newInstance();
            } catch (Exception e) {
                Log.w(WebViewFactory.LOGTAG, "error preloading provider", e);
            }
        }
    }

    public static boolean isExperimentalWebViewAvailable() {
        return false;
    }

    public static void setUseExperimentalWebView(boolean enable) {
    }

    public static boolean useExperimentalWebView() {
        return true;
    }

    public static boolean isUseExperimentalWebViewSet() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WebViewFactoryProvider getProvider() {
        synchronized (sProviderLock) {
            if (sProviderInstance != null) {
                return sProviderInstance;
            }
            try {
                Class<WebViewFactoryProvider> providerClass = getFactoryClass();
                if (Preloader.sPreloadedProvider != null && Preloader.sPreloadedProvider.getClass() == providerClass) {
                    sProviderInstance = Preloader.sPreloadedProvider;
                    return sProviderInstance;
                }
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
                try {
                    sProviderInstance = providerClass.newInstance();
                    WebViewFactoryProvider webViewFactoryProvider = sProviderInstance;
                    StrictMode.setThreadPolicy(oldPolicy);
                    return webViewFactoryProvider;
                } catch (Exception e) {
                    Log.e(LOGTAG, "error instantiating provider", e);
                    throw new AndroidRuntimeException(e);
                }
            } catch (ClassNotFoundException e2) {
                Log.e(LOGTAG, "error loading provider", e2);
                throw new AndroidRuntimeException(e2);
            }
        }
    }

    private static Class<WebViewFactoryProvider> getFactoryClass() throws ClassNotFoundException {
        return Class.forName(CHROMIUM_WEBVIEW_FACTORY);
    }
}