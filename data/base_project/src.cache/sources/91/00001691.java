package android.webkit;

import android.content.Context;

/* loaded from: CookieSyncManager.class */
public final class CookieSyncManager extends WebSyncManager {
    private static CookieSyncManager sRef;
    private static boolean sGetInstanceAllowed = false;

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void stopSync() {
        super.stopSync();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void startSync() {
        super.startSync();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void resetSync() {
        super.resetSync();
    }

    @Override // android.webkit.WebSyncManager
    public /* bridge */ /* synthetic */ void sync() {
        super.sync();
    }

    @Override // android.webkit.WebSyncManager, java.lang.Runnable
    public /* bridge */ /* synthetic */ void run() {
        super.run();
    }

    private CookieSyncManager() {
        super("CookieSyncManager");
    }

    public static synchronized CookieSyncManager getInstance() {
        checkInstanceIsAllowed();
        if (sRef == null) {
            sRef = new CookieSyncManager();
        }
        return sRef;
    }

    public static synchronized CookieSyncManager createInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }
        setGetInstanceIsAllowed();
        return getInstance();
    }

    @Override // android.webkit.WebSyncManager
    protected void syncFromRamToFlash() {
        CookieManager manager = CookieManager.getInstance();
        if (!manager.acceptCookie()) {
            return;
        }
        manager.flushCookieStore();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setGetInstanceIsAllowed() {
        sGetInstanceAllowed = true;
    }

    private static void checkInstanceIsAllowed() {
        if (!sGetInstanceAllowed) {
            throw new IllegalStateException("CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()");
        }
    }
}