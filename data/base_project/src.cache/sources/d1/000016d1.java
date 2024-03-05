package android.webkit;

import android.content.Context;
import android.webkit.WebView;

/* loaded from: WebViewFactoryProvider.class */
public interface WebViewFactoryProvider {

    /* loaded from: WebViewFactoryProvider$Statics.class */
    public interface Statics {
        String findAddress(String str);

        void setPlatformNotificationsEnabled(boolean z);

        String getDefaultUserAgent(Context context);

        void setWebContentsDebuggingEnabled(boolean z);
    }

    Statics getStatics();

    WebViewProvider createWebView(WebView webView, WebView.PrivateAccess privateAccess);

    GeolocationPermissions getGeolocationPermissions();

    CookieManager getCookieManager();

    WebIconDatabase getWebIconDatabase();

    WebStorage getWebStorage();

    WebViewDatabase getWebViewDatabase(Context context);
}