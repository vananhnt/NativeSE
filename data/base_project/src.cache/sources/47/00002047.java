package com.android.webview.chromium;

import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import com.android.org.chromium.android_webview.AwGeolocationPermissions;
import java.util.Set;

/* loaded from: GeolocationPermissionsAdapter.class */
final class GeolocationPermissionsAdapter extends GeolocationPermissions {
    private AwGeolocationPermissions mChromeGeolocationPermissions;

    public GeolocationPermissionsAdapter(AwGeolocationPermissions chromeGeolocationPermissions) {
        this.mChromeGeolocationPermissions = chromeGeolocationPermissions;
    }

    @Override // android.webkit.GeolocationPermissions
    public void allow(String origin) {
        this.mChromeGeolocationPermissions.allow(origin);
    }

    @Override // android.webkit.GeolocationPermissions
    public void clear(String origin) {
        this.mChromeGeolocationPermissions.clear(origin);
    }

    @Override // android.webkit.GeolocationPermissions
    public void clearAll() {
        this.mChromeGeolocationPermissions.clearAll();
    }

    @Override // android.webkit.GeolocationPermissions
    public void getAllowed(String origin, ValueCallback<Boolean> callback) {
        this.mChromeGeolocationPermissions.getAllowed(origin, callback);
    }

    @Override // android.webkit.GeolocationPermissions
    public void getOrigins(ValueCallback<Set<String>> callback) {
        this.mChromeGeolocationPermissions.getOrigins(callback);
    }
}