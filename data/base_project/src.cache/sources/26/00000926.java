package android.net;

import android.content.Context;
import android.util.Log;
import com.android.org.conscrypt.FileClientSessionCache;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.File;
import java.io.IOException;

/* loaded from: SSLSessionCache.class */
public final class SSLSessionCache {
    private static final String TAG = "SSLSessionCache";
    final SSLClientSessionCache mSessionCache;

    public SSLSessionCache(File dir) throws IOException {
        this.mSessionCache = FileClientSessionCache.usingDirectory(dir);
    }

    public SSLSessionCache(Context context) {
        File dir = context.getDir("sslcache", 0);
        SSLClientSessionCache cache = null;
        try {
            cache = FileClientSessionCache.usingDirectory(dir);
        } catch (IOException e) {
            Log.w(TAG, "Unable to create SSL session cache in " + dir, e);
        }
        this.mSessionCache = cache;
    }
}