package android.webkit;

import java.util.Set;

/* loaded from: GeolocationPermissions.class */
public class GeolocationPermissions {

    /* loaded from: GeolocationPermissions$Callback.class */
    public interface Callback {
        void invoke(String str, boolean z, boolean z2);
    }

    public static GeolocationPermissions getInstance() {
        return WebViewFactory.getProvider().getGeolocationPermissions();
    }

    public void getOrigins(ValueCallback<Set<String>> callback) {
    }

    public void getAllowed(String origin, ValueCallback<Boolean> callback) {
    }

    public void clear(String origin) {
    }

    public void allow(String origin) {
    }

    public void clearAll() {
    }
}