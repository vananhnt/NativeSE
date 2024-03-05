package com.android.webview.chromium;

import android.net.ParseException;
import android.net.WebAddress;
import android.util.Log;
import android.webkit.CookieManager;
import com.android.org.chromium.android_webview.AwCookieManager;

/* loaded from: CookieManagerAdapter.class */
public class CookieManagerAdapter extends CookieManager {
    private static final String LOGTAG = "CookieManager";
    AwCookieManager mChromeCookieManager;

    public CookieManagerAdapter(AwCookieManager chromeCookieManager) {
        this.mChromeCookieManager = chromeCookieManager;
    }

    @Override // android.webkit.CookieManager
    public synchronized void setAcceptCookie(boolean accept) {
        this.mChromeCookieManager.setAcceptCookie(accept);
    }

    @Override // android.webkit.CookieManager
    public synchronized boolean acceptCookie() {
        return this.mChromeCookieManager.acceptCookie();
    }

    @Override // android.webkit.CookieManager
    public void setCookie(String url, String value) {
        try {
            this.mChromeCookieManager.setCookie(fixupUrl(url), value);
        } catch (ParseException e) {
            Log.e(LOGTAG, "Not setting cookie due to error parsing URL: " + url, e);
        }
    }

    @Override // android.webkit.CookieManager
    public String getCookie(String url) {
        try {
            return this.mChromeCookieManager.getCookie(fixupUrl(url));
        } catch (ParseException e) {
            Log.e(LOGTAG, "Unable to get cookies due to error parsing URL: " + url, e);
            return null;
        }
    }

    @Override // android.webkit.CookieManager
    public String getCookie(String url, boolean privateBrowsing) {
        return getCookie(url);
    }

    @Override // android.webkit.CookieManager
    public synchronized String getCookie(WebAddress uri) {
        return this.mChromeCookieManager.getCookie(uri.toString());
    }

    @Override // android.webkit.CookieManager
    public void removeSessionCookie() {
        this.mChromeCookieManager.removeSessionCookie();
    }

    @Override // android.webkit.CookieManager
    public void removeAllCookie() {
        this.mChromeCookieManager.removeAllCookie();
    }

    @Override // android.webkit.CookieManager
    public synchronized boolean hasCookies() {
        return this.mChromeCookieManager.hasCookies();
    }

    @Override // android.webkit.CookieManager
    public synchronized boolean hasCookies(boolean privateBrowsing) {
        return this.mChromeCookieManager.hasCookies();
    }

    @Override // android.webkit.CookieManager
    public void removeExpiredCookie() {
        this.mChromeCookieManager.removeExpiredCookie();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.webkit.CookieManager
    public void flushCookieStore() {
        this.mChromeCookieManager.flushCookieStore();
    }

    @Override // android.webkit.CookieManager
    protected boolean allowFileSchemeCookiesImpl() {
        return this.mChromeCookieManager.allowFileSchemeCookies();
    }

    @Override // android.webkit.CookieManager
    protected void setAcceptFileSchemeCookiesImpl(boolean accept) {
        this.mChromeCookieManager.setAcceptFileSchemeCookies(accept);
    }

    private static String fixupUrl(String url) throws ParseException {
        return new WebAddress(url).toString();
    }
}