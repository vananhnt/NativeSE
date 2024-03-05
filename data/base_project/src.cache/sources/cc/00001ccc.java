package com.android.server;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.speech.RecognitionService;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.content.PackageMonitor;
import java.util.List;

/* loaded from: RecognitionManagerService.class */
public class RecognitionManagerService extends Binder {
    static final String TAG = "RecognitionManagerService";
    private final Context mContext;
    private final IPackageManager mIPm;
    private static final boolean DEBUG = false;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.RecognitionManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int userHandle;
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action) && (userHandle = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1)) > 0) {
                RecognitionManagerService.this.initForUser(userHandle);
            }
        }
    };
    private final MyPackageMonitor mMonitor = new MyPackageMonitor();

    /* loaded from: RecognitionManagerService$MyPackageMonitor.class */
    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            ComponentName comp;
            int userHandle = getChangingUserId();
            ComponentName comp2 = RecognitionManagerService.this.getCurRecognizer(userHandle);
            if (comp2 == null) {
                if (anyPackagesAppearing() && (comp = RecognitionManagerService.this.findAvailRecognizer(null, userHandle)) != null) {
                    RecognitionManagerService.this.setCurRecognizer(comp, userHandle);
                    return;
                }
                return;
            }
            int change = isPackageDisappearing(comp2.getPackageName());
            if (change == 3 || change == 2) {
                RecognitionManagerService.this.setCurRecognizer(RecognitionManagerService.this.findAvailRecognizer(null, userHandle), userHandle);
            } else if (isPackageModified(comp2.getPackageName())) {
                RecognitionManagerService.this.setCurRecognizer(RecognitionManagerService.this.findAvailRecognizer(comp2.getPackageName(), userHandle), userHandle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecognitionManagerService(Context context) {
        this.mContext = context;
        this.mMonitor.register(context, null, UserHandle.ALL, true);
        this.mIPm = AppGlobals.getPackageManager();
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, new IntentFilter(Intent.ACTION_BOOT_COMPLETED), null, null);
    }

    public void systemReady() {
        initForUser(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initForUser(int userHandle) {
        ComponentName comp;
        ComponentName comp2 = getCurRecognizer(userHandle);
        ServiceInfo info = null;
        if (comp2 != null) {
            try {
                info = this.mIPm.getServiceInfo(comp2, 0, userHandle);
            } catch (RemoteException e) {
            }
        }
        if (info == null && (comp = findAvailRecognizer(null, userHandle)) != null) {
            setCurRecognizer(comp, userHandle);
        }
    }

    ComponentName findAvailRecognizer(String prefPackage, int userHandle) {
        List<ResolveInfo> available = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent(RecognitionService.SERVICE_INTERFACE), 0, userHandle);
        int numAvailable = available.size();
        if (numAvailable == 0) {
            Slog.w(TAG, "no available voice recognition services found for user " + userHandle);
            return null;
        }
        if (prefPackage != null) {
            for (int i = 0; i < numAvailable; i++) {
                ServiceInfo serviceInfo = available.get(i).serviceInfo;
                if (prefPackage.equals(serviceInfo.packageName)) {
                    return new ComponentName(serviceInfo.packageName, serviceInfo.name);
                }
            }
        }
        if (numAvailable > 1) {
            Slog.w(TAG, "more than one voice recognition service found, picking first");
        }
        ServiceInfo serviceInfo2 = available.get(0).serviceInfo;
        return new ComponentName(serviceInfo2.packageName, serviceInfo2.name);
    }

    ComponentName getCurRecognizer(int userHandle) {
        String curRecognizer = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.VOICE_RECOGNITION_SERVICE, userHandle);
        if (TextUtils.isEmpty(curRecognizer)) {
            return null;
        }
        return ComponentName.unflattenFromString(curRecognizer);
    }

    void setCurRecognizer(ComponentName comp, int userHandle) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), Settings.Secure.VOICE_RECOGNITION_SERVICE, comp != null ? comp.flattenToShortString() : "", userHandle);
    }
}