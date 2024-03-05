package com.android.webview.chromium;

import android.webkit.WebViewDatabase;
import com.android.org.chromium.android_webview.AwFormDatabase;
import com.android.org.chromium.android_webview.HttpAuthDatabase;

/* loaded from: WebViewDatabaseAdapter.class */
final class WebViewDatabaseAdapter extends WebViewDatabase {
    private AwFormDatabase mFormDatabase;
    private HttpAuthDatabase mHttpAuthDatabase;

    public WebViewDatabaseAdapter(AwFormDatabase formDatabase, HttpAuthDatabase httpAuthDatabase) {
        this.mFormDatabase = formDatabase;
        this.mHttpAuthDatabase = httpAuthDatabase;
    }

    @Override // android.webkit.WebViewDatabase
    public boolean hasUsernamePassword() {
        return false;
    }

    @Override // android.webkit.WebViewDatabase
    public void clearUsernamePassword() {
    }

    @Override // android.webkit.WebViewDatabase
    public boolean hasHttpAuthUsernamePassword() {
        return this.mHttpAuthDatabase.hasHttpAuthUsernamePassword();
    }

    @Override // android.webkit.WebViewDatabase
    public void clearHttpAuthUsernamePassword() {
        this.mHttpAuthDatabase.clearHttpAuthUsernamePassword();
    }

    @Override // android.webkit.WebViewDatabase
    public boolean hasFormData() {
        AwFormDatabase awFormDatabase = this.mFormDatabase;
        return AwFormDatabase.hasFormData();
    }

    @Override // android.webkit.WebViewDatabase
    public void clearFormData() {
        AwFormDatabase awFormDatabase = this.mFormDatabase;
        AwFormDatabase.clearFormData();
    }
}