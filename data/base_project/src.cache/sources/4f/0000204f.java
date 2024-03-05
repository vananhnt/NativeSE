package com.android.webview.chromium;

import android.webkit.ValueCallback;
import android.webkit.WebStorage;
import com.android.org.chromium.android_webview.AwQuotaManagerBridge;
import java.util.HashMap;
import java.util.Map;

/* loaded from: WebStorageAdapter.class */
final class WebStorageAdapter extends WebStorage {
    private final AwQuotaManagerBridge mQuotaManagerBridge;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebStorageAdapter(AwQuotaManagerBridge quotaManagerBridge) {
        this.mQuotaManagerBridge = quotaManagerBridge;
    }

    @Override // android.webkit.WebStorage
    public void getOrigins(final ValueCallback<Map> callback) {
        this.mQuotaManagerBridge.getOrigins(new ValueCallback<AwQuotaManagerBridge.Origins>() { // from class: com.android.webview.chromium.WebStorageAdapter.1
            @Override // android.webkit.ValueCallback
            public void onReceiveValue(AwQuotaManagerBridge.Origins origins) {
                HashMap hashMap = new HashMap();
                for (int i = 0; i < origins.mOrigins.length; i++) {
                    WebStorage.Origin origin = new WebStorage.Origin(origins.mOrigins[i], origins.mQuotas[i], origins.mUsages[i]) { // from class: com.android.webview.chromium.WebStorageAdapter.1.1
                    };
                    hashMap.put(origins.mOrigins[i], origin);
                }
                callback.onReceiveValue(hashMap);
            }
        });
    }

    @Override // android.webkit.WebStorage
    public void getUsageForOrigin(String origin, ValueCallback<Long> callback) {
        this.mQuotaManagerBridge.getUsageForOrigin(origin, callback);
    }

    @Override // android.webkit.WebStorage
    public void getQuotaForOrigin(String origin, ValueCallback<Long> callback) {
        this.mQuotaManagerBridge.getQuotaForOrigin(origin, callback);
    }

    @Override // android.webkit.WebStorage
    public void setQuotaForOrigin(String origin, long quota) {
    }

    @Override // android.webkit.WebStorage
    public void deleteOrigin(String origin) {
        this.mQuotaManagerBridge.deleteOrigin(origin);
    }

    @Override // android.webkit.WebStorage
    public void deleteAllData() {
        this.mQuotaManagerBridge.deleteAllData();
    }
}