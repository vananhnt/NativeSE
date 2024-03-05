package com.android.webview.chromium;

import android.content.ContentResolver;
import android.webkit.WebIconDatabase;
import com.android.org.chromium.android_webview.AwContents;

/* loaded from: WebIconDatabaseAdapter.class */
final class WebIconDatabaseAdapter extends WebIconDatabase {
    @Override // android.webkit.WebIconDatabase
    public void open(String path) {
        AwContents.setShouldDownloadFavicons();
    }

    @Override // android.webkit.WebIconDatabase
    public void close() {
    }

    @Override // android.webkit.WebIconDatabase
    public void removeAllIcons() {
    }

    @Override // android.webkit.WebIconDatabase
    public void requestIconForPageUrl(String url, WebIconDatabase.IconListener listener) {
    }

    @Override // android.webkit.WebIconDatabase
    public void bulkRequestIconForPageUrl(ContentResolver cr, String where, WebIconDatabase.IconListener listener) {
    }

    @Override // android.webkit.WebIconDatabase
    public void retainIconForPageUrl(String url) {
    }

    @Override // android.webkit.WebIconDatabase
    public void releaseIconForPageUrl(String url) {
    }
}