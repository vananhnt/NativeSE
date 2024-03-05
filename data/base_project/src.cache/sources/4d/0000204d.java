package com.android.webview.chromium;

import android.graphics.Bitmap;
import android.webkit.WebHistoryItem;
import com.android.org.chromium.content.browser.NavigationEntry;

/* loaded from: WebHistoryItemChromium.class */
public class WebHistoryItemChromium extends WebHistoryItem {
    private final String mUrl;
    private final String mOriginalUrl;
    private final String mTitle;
    private final Bitmap mFavicon;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebHistoryItemChromium(NavigationEntry entry) {
        this.mUrl = entry.getUrl();
        this.mOriginalUrl = entry.getOriginalUrl();
        this.mTitle = entry.getTitle();
        this.mFavicon = entry.getFavicon();
    }

    @Override // android.webkit.WebHistoryItem
    public int getId() {
        return -1;
    }

    @Override // android.webkit.WebHistoryItem
    public String getUrl() {
        return this.mUrl;
    }

    @Override // android.webkit.WebHistoryItem
    public String getOriginalUrl() {
        return this.mOriginalUrl;
    }

    @Override // android.webkit.WebHistoryItem
    public String getTitle() {
        return this.mTitle;
    }

    @Override // android.webkit.WebHistoryItem
    public Bitmap getFavicon() {
        return this.mFavicon;
    }

    private WebHistoryItemChromium(String url, String originalUrl, String title, Bitmap favicon) {
        this.mUrl = url;
        this.mOriginalUrl = originalUrl;
        this.mTitle = title;
        this.mFavicon = favicon;
    }

    @Override // android.webkit.WebHistoryItem
    /* renamed from: clone */
    public synchronized WebHistoryItemChromium mo997clone() {
        return new WebHistoryItemChromium(this.mUrl, this.mOriginalUrl, this.mTitle, this.mFavicon);
    }
}