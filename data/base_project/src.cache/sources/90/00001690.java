package android.webkit;

import android.net.WebAddress;

/* loaded from: CookieManager.class */
public class CookieManager {
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("doesn't implement Cloneable");
    }

    public static synchronized CookieManager getInstance() {
        return WebViewFactory.getProvider().getCookieManager();
    }

    public synchronized void setAcceptCookie(boolean accept) {
        throw new MustOverrideException();
    }

    public synchronized boolean acceptCookie() {
        throw new MustOverrideException();
    }

    public void setCookie(String url, String value) {
        throw new MustOverrideException();
    }

    public String getCookie(String url) {
        throw new MustOverrideException();
    }

    public String getCookie(String url, boolean privateBrowsing) {
        throw new MustOverrideException();
    }

    public synchronized String getCookie(WebAddress uri) {
        throw new MustOverrideException();
    }

    public void removeSessionCookie() {
        throw new MustOverrideException();
    }

    public void removeAllCookie() {
        throw new MustOverrideException();
    }

    public synchronized boolean hasCookies() {
        throw new MustOverrideException();
    }

    public synchronized boolean hasCookies(boolean privateBrowsing) {
        throw new MustOverrideException();
    }

    public void removeExpiredCookie() {
        throw new MustOverrideException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flushCookieStore() {
        throw new MustOverrideException();
    }

    public static boolean allowFileSchemeCookies() {
        return getInstance().allowFileSchemeCookiesImpl();
    }

    protected boolean allowFileSchemeCookiesImpl() {
        throw new MustOverrideException();
    }

    public static void setAcceptFileSchemeCookies(boolean accept) {
        getInstance().setAcceptFileSchemeCookiesImpl(accept);
    }

    protected void setAcceptFileSchemeCookiesImpl(boolean accept) {
        throw new MustOverrideException();
    }
}