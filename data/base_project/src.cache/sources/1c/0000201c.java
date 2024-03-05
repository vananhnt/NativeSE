package com.android.server.wm;

import android.app.ActivityManagerNative;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.TokenWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManagerPolicy;

/* loaded from: KeyguardDisableHandler.class */
public class KeyguardDisableHandler extends Handler {
    private static final String TAG = "KeyguardDisableHandler";
    private static final int ALLOW_DISABLE_YES = 1;
    private static final int ALLOW_DISABLE_NO = 0;
    private static final int ALLOW_DISABLE_UNKNOWN = -1;
    private int mAllowDisableKeyguard = -1;
    static final int KEYGUARD_DISABLE = 1;
    static final int KEYGUARD_REENABLE = 2;
    static final int KEYGUARD_POLICY_CHANGED = 3;
    final Context mContext;
    final WindowManagerPolicy mPolicy;
    KeyguardTokenWatcher mKeyguardTokenWatcher;

    public KeyguardDisableHandler(Context context, WindowManagerPolicy policy) {
        this.mContext = context;
        this.mPolicy = policy;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (this.mKeyguardTokenWatcher == null) {
            this.mKeyguardTokenWatcher = new KeyguardTokenWatcher(this);
        }
        switch (msg.what) {
            case 1:
                Pair<IBinder, String> pair = (Pair) msg.obj;
                this.mKeyguardTokenWatcher.acquire((IBinder) pair.first, (String) pair.second);
                return;
            case 2:
                this.mKeyguardTokenWatcher.release((IBinder) msg.obj);
                return;
            case 3:
                this.mPolicy.enableKeyguard(true);
                this.mAllowDisableKeyguard = -1;
                return;
            default:
                return;
        }
    }

    /* loaded from: KeyguardDisableHandler$KeyguardTokenWatcher.class */
    class KeyguardTokenWatcher extends TokenWatcher {
        public KeyguardTokenWatcher(Handler handler) {
            super(handler, KeyguardDisableHandler.TAG);
        }

        @Override // android.os.TokenWatcher
        public void acquired() {
            DevicePolicyManager dpm;
            if (KeyguardDisableHandler.this.mAllowDisableKeyguard == -1 && (dpm = (DevicePolicyManager) KeyguardDisableHandler.this.mContext.getSystemService(Context.DEVICE_POLICY_SERVICE)) != null) {
                try {
                    KeyguardDisableHandler.this.mAllowDisableKeyguard = dpm.getPasswordQuality(null, ActivityManagerNative.getDefault().getCurrentUser().id) == 0 ? 1 : 0;
                } catch (RemoteException e) {
                }
            }
            if (KeyguardDisableHandler.this.mAllowDisableKeyguard == 1) {
                KeyguardDisableHandler.this.mPolicy.enableKeyguard(false);
            } else {
                Log.v(KeyguardDisableHandler.TAG, "Not disabling keyguard since device policy is enforced");
            }
        }

        @Override // android.os.TokenWatcher
        public void released() {
            KeyguardDisableHandler.this.mPolicy.enableKeyguard(true);
        }
    }
}