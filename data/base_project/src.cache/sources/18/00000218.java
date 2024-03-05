package android.app.admin;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: DeviceAdminReceiver.class */
public class DeviceAdminReceiver extends BroadcastReceiver {
    private static String TAG = "DevicePolicy";
    private static boolean localLOGV = false;
    public static final String ACTION_DEVICE_ADMIN_ENABLED = "android.app.action.DEVICE_ADMIN_ENABLED";
    public static final String ACTION_DEVICE_ADMIN_DISABLE_REQUESTED = "android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED";
    public static final String EXTRA_DISABLE_WARNING = "android.app.extra.DISABLE_WARNING";
    public static final String ACTION_DEVICE_ADMIN_DISABLED = "android.app.action.DEVICE_ADMIN_DISABLED";
    public static final String ACTION_PASSWORD_CHANGED = "android.app.action.ACTION_PASSWORD_CHANGED";
    public static final String ACTION_PASSWORD_FAILED = "android.app.action.ACTION_PASSWORD_FAILED";
    public static final String ACTION_PASSWORD_SUCCEEDED = "android.app.action.ACTION_PASSWORD_SUCCEEDED";
    public static final String ACTION_PASSWORD_EXPIRING = "android.app.action.ACTION_PASSWORD_EXPIRING";
    public static final String DEVICE_ADMIN_META_DATA = "android.app.device_admin";
    private DevicePolicyManager mManager;
    private ComponentName mWho;

    public DevicePolicyManager getManager(Context context) {
        if (this.mManager != null) {
            return this.mManager;
        }
        this.mManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return this.mManager;
    }

    public ComponentName getWho(Context context) {
        if (this.mWho != null) {
            return this.mWho;
        }
        this.mWho = new ComponentName(context, getClass());
        return this.mWho;
    }

    public void onEnabled(Context context, Intent intent) {
    }

    public CharSequence onDisableRequested(Context context, Intent intent) {
        return null;
    }

    public void onDisabled(Context context, Intent intent) {
    }

    public void onPasswordChanged(Context context, Intent intent) {
    }

    public void onPasswordFailed(Context context, Intent intent) {
    }

    public void onPasswordSucceeded(Context context, Intent intent) {
    }

    public void onPasswordExpiring(Context context, Intent intent) {
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_PASSWORD_CHANGED.equals(action)) {
            onPasswordChanged(context, intent);
        } else if (ACTION_PASSWORD_FAILED.equals(action)) {
            onPasswordFailed(context, intent);
        } else if (ACTION_PASSWORD_SUCCEEDED.equals(action)) {
            onPasswordSucceeded(context, intent);
        } else if (ACTION_DEVICE_ADMIN_ENABLED.equals(action)) {
            onEnabled(context, intent);
        } else if (ACTION_DEVICE_ADMIN_DISABLE_REQUESTED.equals(action)) {
            CharSequence res = onDisableRequested(context, intent);
            if (res != null) {
                Bundle extras = getResultExtras(true);
                extras.putCharSequence(EXTRA_DISABLE_WARNING, res);
            }
        } else if (ACTION_DEVICE_ADMIN_DISABLED.equals(action)) {
            onDisabled(context, intent);
        } else if (ACTION_PASSWORD_EXPIRING.equals(action)) {
            onPasswordExpiring(context, intent);
        }
    }
}