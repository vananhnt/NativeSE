package com.android.server.am;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.HashMap;
import java.util.Map;

/* loaded from: CoreSettingsObserver.class */
final class CoreSettingsObserver extends ContentObserver {
    private static final String LOG_TAG = CoreSettingsObserver.class.getSimpleName();
    private static final Map<String, Class<?>> sCoreSettingToTypeMap = new HashMap();
    private final Bundle mCoreSettings;
    private final ActivityManagerService mActivityManagerService;

    static {
        sCoreSettingToTypeMap.put(Settings.Secure.LONG_PRESS_TIMEOUT, Integer.TYPE);
    }

    public CoreSettingsObserver(ActivityManagerService activityManagerService) {
        super(activityManagerService.mHandler);
        this.mCoreSettings = new Bundle();
        this.mActivityManagerService = activityManagerService;
        beginObserveCoreSettings();
        sendCoreSettings();
    }

    public Bundle getCoreSettingsLocked() {
        return (Bundle) this.mCoreSettings.clone();
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        synchronized (this.mActivityManagerService) {
            sendCoreSettings();
        }
    }

    private void sendCoreSettings() {
        populateCoreSettings(this.mCoreSettings);
        this.mActivityManagerService.onCoreSettingsChange(this.mCoreSettings);
    }

    private void beginObserveCoreSettings() {
        for (String setting : sCoreSettingToTypeMap.keySet()) {
            Uri uri = Settings.Secure.getUriFor(setting);
            this.mActivityManagerService.mContext.getContentResolver().registerContentObserver(uri, false, this);
        }
    }

    private void populateCoreSettings(Bundle snapshot) {
        Context context = this.mActivityManagerService.mContext;
        for (Map.Entry<String, Class<?>> entry : sCoreSettingToTypeMap.entrySet()) {
            String setting = entry.getKey();
            Class<?> type = entry.getValue();
            if (type == String.class) {
                try {
                    String value = Settings.Secure.getString(context.getContentResolver(), setting);
                    snapshot.putString(setting, value);
                } catch (Settings.SettingNotFoundException snfe) {
                    Log.w(LOG_TAG, "Cannot find setting \"" + setting + Separators.DOUBLE_QUOTE, snfe);
                }
            } else if (type == Integer.TYPE) {
                int value2 = Settings.Secure.getInt(context.getContentResolver(), setting);
                snapshot.putInt(setting, value2);
            } else if (type == Float.TYPE) {
                float value3 = Settings.Secure.getFloat(context.getContentResolver(), setting);
                snapshot.putFloat(setting, value3);
            } else if (type == Long.TYPE) {
                long value4 = Settings.Secure.getLong(context.getContentResolver(), setting);
                snapshot.putLong(setting, value4);
            }
        }
    }
}