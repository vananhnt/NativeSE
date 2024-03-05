package android.content;

import android.content.res.Configuration;

/* loaded from: ComponentCallbacks.class */
public interface ComponentCallbacks {
    void onConfigurationChanged(Configuration configuration);

    void onLowMemory();
}