package com.android.server.location;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.server.LocationManagerService;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: LocationBlacklist.class */
public final class LocationBlacklist extends ContentObserver {
    private static final String TAG = "LocationBlacklist";
    private static final boolean D = LocationManagerService.D;
    private static final String BLACKLIST_CONFIG_NAME = "locationPackagePrefixBlacklist";
    private static final String WHITELIST_CONFIG_NAME = "locationPackagePrefixWhitelist";
    private final Context mContext;
    private final Object mLock;
    private String[] mWhitelist;
    private String[] mBlacklist;
    private int mCurrentUserId;

    public LocationBlacklist(Context context, Handler handler) {
        super(handler);
        this.mLock = new Object();
        this.mWhitelist = new String[0];
        this.mBlacklist = new String[0];
        this.mCurrentUserId = 0;
        this.mContext = context;
    }

    public void init() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(BLACKLIST_CONFIG_NAME), false, this, -1);
        reloadBlacklist();
    }

    private void reloadBlacklistLocked() {
        this.mWhitelist = getStringArrayLocked(WHITELIST_CONFIG_NAME);
        if (D) {
            Slog.d(TAG, "whitelist: " + Arrays.toString(this.mWhitelist));
        }
        this.mBlacklist = getStringArrayLocked(BLACKLIST_CONFIG_NAME);
        if (D) {
            Slog.d(TAG, "blacklist: " + Arrays.toString(this.mBlacklist));
        }
    }

    private void reloadBlacklist() {
        synchronized (this.mLock) {
            reloadBlacklistLocked();
        }
    }

    public boolean isBlacklisted(String packageName) {
        synchronized (this.mLock) {
            String[] arr$ = this.mBlacklist;
            for (String black : arr$) {
                if (packageName.startsWith(black) && !inWhitelist(packageName)) {
                    if (D) {
                        Log.d(TAG, "dropping location (blacklisted): " + packageName + " matches " + black);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private boolean inWhitelist(String pkg) {
        synchronized (this.mLock) {
            String[] arr$ = this.mWhitelist;
            for (String white : arr$) {
                if (pkg.startsWith(white)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        reloadBlacklist();
    }

    public void switchUser(int userId) {
        synchronized (this.mLock) {
            this.mCurrentUserId = userId;
            reloadBlacklistLocked();
        }
    }

    private String[] getStringArrayLocked(String key) {
        String flatString;
        synchronized (this.mLock) {
            flatString = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), key, this.mCurrentUserId);
        }
        if (flatString == null) {
            return new String[0];
        }
        String[] splitStrings = flatString.split(Separators.COMMA);
        ArrayList<String> result = new ArrayList<>();
        for (String pkg : splitStrings) {
            String pkg2 = pkg.trim();
            if (!pkg2.isEmpty()) {
                result.add(pkg2);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public void dump(PrintWriter pw) {
        pw.println("mWhitelist=" + Arrays.toString(this.mWhitelist) + " mBlacklist=" + Arrays.toString(this.mBlacklist));
    }
}