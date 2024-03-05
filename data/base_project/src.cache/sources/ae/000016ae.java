package android.webkit;

import android.webkit.CacheManager;
import java.util.Map;

@Deprecated
/* loaded from: UrlInterceptHandler.class */
public interface UrlInterceptHandler {
    @Deprecated
    CacheManager.CacheResult service(String str, Map<String, String> map);

    @Deprecated
    PluginData getPluginData(String str, Map<String, String> map);
}