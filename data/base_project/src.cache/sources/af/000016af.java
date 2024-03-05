package android.webkit;

import android.webkit.CacheManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@Deprecated
/* loaded from: UrlInterceptRegistry.class */
public final class UrlInterceptRegistry {
    private static final String LOGTAG = "intercept";
    private static boolean mDisabled = false;
    private static LinkedList mHandlerList;

    private static synchronized LinkedList getHandlers() {
        if (mHandlerList == null) {
            mHandlerList = new LinkedList();
        }
        return mHandlerList;
    }

    @Deprecated
    public static synchronized void setUrlInterceptDisabled(boolean disabled) {
        mDisabled = disabled;
    }

    @Deprecated
    public static synchronized boolean urlInterceptDisabled() {
        return mDisabled;
    }

    @Deprecated
    public static synchronized boolean registerHandler(UrlInterceptHandler handler) {
        if (!getHandlers().contains(handler)) {
            getHandlers().addFirst(handler);
            return true;
        }
        return false;
    }

    @Deprecated
    public static synchronized boolean unregisterHandler(UrlInterceptHandler handler) {
        return getHandlers().remove(handler);
    }

    @Deprecated
    public static synchronized CacheManager.CacheResult getSurrogate(String url, Map<String, String> headers) {
        if (urlInterceptDisabled()) {
            return null;
        }
        Iterator iter = getHandlers().listIterator();
        while (iter.hasNext()) {
            UrlInterceptHandler handler = (UrlInterceptHandler) iter.next();
            CacheManager.CacheResult result = handler.service(url, headers);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Deprecated
    public static synchronized PluginData getPluginData(String url, Map<String, String> headers) {
        if (urlInterceptDisabled()) {
            return null;
        }
        Iterator iter = getHandlers().listIterator();
        while (iter.hasNext()) {
            UrlInterceptHandler handler = (UrlInterceptHandler) iter.next();
            PluginData data = handler.getPluginData(url, headers);
            if (data != null) {
                return data;
            }
        }
        return null;
    }
}