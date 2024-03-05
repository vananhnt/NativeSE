package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.IOnKeyguardExitResult;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;

/* loaded from: KeyguardManager.class */
public class KeyguardManager {
    private IWindowManager mWM = WindowManagerGlobal.getWindowManagerService();

    /* loaded from: KeyguardManager$OnKeyguardExitResult.class */
    public interface OnKeyguardExitResult {
        void onKeyguardExitResult(boolean z);
    }

    /* loaded from: KeyguardManager$KeyguardLock.class */
    public class KeyguardLock {
        private IBinder mToken = new Binder();
        private String mTag;

        KeyguardLock(String tag) {
            this.mTag = tag;
        }

        public void disableKeyguard() {
            try {
                KeyguardManager.this.mWM.disableKeyguard(this.mToken, this.mTag);
            } catch (RemoteException e) {
            }
        }

        public void reenableKeyguard() {
            try {
                KeyguardManager.this.mWM.reenableKeyguard(this.mToken);
            } catch (RemoteException e) {
            }
        }
    }

    @Deprecated
    public KeyguardLock newKeyguardLock(String tag) {
        return new KeyguardLock(tag);
    }

    public boolean isKeyguardLocked() {
        try {
            return this.mWM.isKeyguardLocked();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isKeyguardSecure() {
        try {
            return this.mWM.isKeyguardSecure();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean inKeyguardRestrictedInputMode() {
        try {
            return this.mWM.inKeyguardRestrictedInputMode();
        } catch (RemoteException e) {
            return false;
        }
    }

    @Deprecated
    public void exitKeyguardSecurely(final OnKeyguardExitResult callback) {
        try {
            this.mWM.exitKeyguardSecurely(new IOnKeyguardExitResult.Stub() { // from class: android.app.KeyguardManager.1
                @Override // android.view.IOnKeyguardExitResult
                public void onKeyguardExitResult(boolean success) throws RemoteException {
                    callback.onKeyguardExitResult(success);
                }
            });
        } catch (RemoteException e) {
        }
    }
}