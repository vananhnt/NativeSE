package android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.SntpClient;
import android.os.SystemClock;
import android.provider.Settings;
import com.android.internal.R;

/* loaded from: NtpTrustedTime.class */
public class NtpTrustedTime implements TrustedTime {
    private static final String TAG = "NtpTrustedTime";
    private static final boolean LOGD = false;
    private static NtpTrustedTime sSingleton;
    private final String mServer;
    private final long mTimeout;
    private boolean mHasCache;
    private long mCachedNtpTime;
    private long mCachedNtpElapsedRealtime;
    private long mCachedNtpCertainty;

    private NtpTrustedTime(String server, long timeout) {
        this.mServer = server;
        this.mTimeout = timeout;
    }

    public static synchronized NtpTrustedTime getInstance(Context context) {
        if (sSingleton == null) {
            Resources res = context.getResources();
            ContentResolver resolver = context.getContentResolver();
            String defaultServer = res.getString(R.string.config_ntpServer);
            long defaultTimeout = res.getInteger(R.integer.config_ntpTimeout);
            String secureServer = Settings.Global.getString(resolver, Settings.Global.NTP_SERVER);
            long timeout = Settings.Global.getLong(resolver, Settings.Global.NTP_TIMEOUT, defaultTimeout);
            String server = secureServer != null ? secureServer : defaultServer;
            sSingleton = new NtpTrustedTime(server, timeout);
        }
        return sSingleton;
    }

    @Override // android.util.TrustedTime
    public boolean forceRefresh() {
        if (this.mServer == null) {
            return false;
        }
        SntpClient client = new SntpClient();
        if (client.requestTime(this.mServer, (int) this.mTimeout)) {
            this.mHasCache = true;
            this.mCachedNtpTime = client.getNtpTime();
            this.mCachedNtpElapsedRealtime = client.getNtpTimeReference();
            this.mCachedNtpCertainty = client.getRoundTripTime() / 2;
            return true;
        }
        return false;
    }

    @Override // android.util.TrustedTime
    public boolean hasCache() {
        return this.mHasCache;
    }

    @Override // android.util.TrustedTime
    public long getCacheAge() {
        if (this.mHasCache) {
            return SystemClock.elapsedRealtime() - this.mCachedNtpElapsedRealtime;
        }
        return Long.MAX_VALUE;
    }

    @Override // android.util.TrustedTime
    public long getCacheCertainty() {
        if (this.mHasCache) {
            return this.mCachedNtpCertainty;
        }
        return Long.MAX_VALUE;
    }

    @Override // android.util.TrustedTime
    public long currentTimeMillis() {
        if (!this.mHasCache) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        return this.mCachedNtpTime + getCacheAge();
    }

    public long getCachedNtpTime() {
        return this.mCachedNtpTime;
    }

    public long getCachedNtpTimeReference() {
        return this.mCachedNtpElapsedRealtime;
    }
}