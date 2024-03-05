package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;
import android.util.Log;
import android.util.Slog;
import java.io.IOException;

/* loaded from: MasterClearReceiver.class */
public class MasterClearReceiver extends BroadcastReceiver {
    private static final String TAG = "MasterClear";

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_REMOTE_INTENT) && !"google.com".equals(intent.getStringExtra("from"))) {
            Slog.w(TAG, "Ignoring master clear request -- not from trusted server.");
            return;
        }
        Slog.w(TAG, "!!! FACTORY RESET !!!");
        Thread thr = new Thread("Reboot") { // from class: com.android.server.MasterClearReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    RecoverySystem.rebootWipeUserData(context);
                    Log.wtf(MasterClearReceiver.TAG, "Still running after master clear?!");
                } catch (IOException e) {
                    Slog.e(MasterClearReceiver.TAG, "Can't perform master clear/factory reset", e);
                }
            }
        };
        thr.start();
    }
}