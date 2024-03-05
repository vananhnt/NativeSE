package com.android.server.wifi;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: WifiSettingsStore.class */
final class WifiSettingsStore {
    private static final int WIFI_DISABLED = 0;
    private static final int WIFI_ENABLED = 1;
    private static final int WIFI_ENABLED_AIRPLANE_OVERRIDE = 2;
    private static final int WIFI_DISABLED_AIRPLANE_ON = 3;
    private int mPersistWifiState;
    private boolean mAirplaneModeOn;
    private final Context mContext;
    private boolean mCheckSavedStateAtBoot = false;
    private boolean mScanAlwaysAvailable = getPersistedScanAlwaysAvailable();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiSettingsStore(Context context) {
        this.mPersistWifiState = 0;
        this.mAirplaneModeOn = false;
        this.mContext = context;
        this.mAirplaneModeOn = getPersistedAirplaneModeOn();
        this.mPersistWifiState = getPersistedWifiState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isWifiToggleEnabled() {
        if (!this.mCheckSavedStateAtBoot) {
            this.mCheckSavedStateAtBoot = true;
            if (testAndClearWifiSavedState()) {
                return true;
            }
        }
        return this.mAirplaneModeOn ? this.mPersistWifiState == 2 : this.mPersistWifiState != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isAirplaneModeOn() {
        return this.mAirplaneModeOn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isScanAlwaysAvailable() {
        return this.mScanAlwaysAvailable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean handleWifiToggled(boolean wifiEnabled) {
        if (this.mAirplaneModeOn && !isAirplaneToggleable()) {
            return false;
        }
        if (wifiEnabled) {
            if (this.mAirplaneModeOn) {
                persistWifiState(2);
                return true;
            }
            persistWifiState(1);
            return true;
        }
        persistWifiState(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean handleAirplaneModeToggled() {
        if (!isAirplaneSensitive()) {
            return false;
        }
        this.mAirplaneModeOn = getPersistedAirplaneModeOn();
        if (this.mAirplaneModeOn) {
            if (this.mPersistWifiState == 1) {
                persistWifiState(3);
                return true;
            }
            return true;
        } else if (testAndClearWifiSavedState() || this.mPersistWifiState == 2) {
            persistWifiState(1);
            return true;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void handleWifiScanAlwaysAvailableToggled() {
        this.mScanAlwaysAvailable = getPersistedScanAlwaysAvailable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("mPersistWifiState " + this.mPersistWifiState);
        pw.println("mAirplaneModeOn " + this.mAirplaneModeOn);
    }

    private void persistWifiState(int state) {
        ContentResolver cr = this.mContext.getContentResolver();
        this.mPersistWifiState = state;
        Settings.Global.putInt(cr, "wifi_on", state);
    }

    private boolean isAirplaneSensitive() {
        String airplaneModeRadios = Settings.Global.getString(this.mContext.getContentResolver(), "airplane_mode_radios");
        return airplaneModeRadios == null || airplaneModeRadios.contains("wifi");
    }

    private boolean isAirplaneToggleable() {
        String toggleableRadios = Settings.Global.getString(this.mContext.getContentResolver(), "airplane_mode_toggleable_radios");
        return toggleableRadios != null && toggleableRadios.contains("wifi");
    }

    private boolean testAndClearWifiSavedState() {
        ContentResolver cr = this.mContext.getContentResolver();
        int wifiSavedState = 0;
        try {
            wifiSavedState = Settings.Global.getInt(cr, Settings.Global.WIFI_SAVED_STATE);
            if (wifiSavedState == 1) {
                Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
            }
        } catch (Settings.SettingNotFoundException e) {
        }
        return wifiSavedState == 1;
    }

    private int getPersistedWifiState() {
        ContentResolver cr = this.mContext.getContentResolver();
        try {
            return Settings.Global.getInt(cr, "wifi_on");
        } catch (Settings.SettingNotFoundException e) {
            Settings.Global.putInt(cr, "wifi_on", 0);
            return 0;
        }
    }

    private boolean getPersistedAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private boolean getPersistedScanAlwaysAvailable() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, 0) == 1;
    }
}