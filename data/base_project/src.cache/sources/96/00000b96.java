package android.os;

import android.app.ActivityThread;
import android.content.Context;
import android.os.IVibratorService;
import android.util.Log;

/* loaded from: SystemVibrator.class */
public class SystemVibrator extends Vibrator {
    private static final String TAG = "Vibrator";
    private final String mPackageName;
    private final IVibratorService mService;
    private final Binder mToken;

    public SystemVibrator() {
        this.mToken = new Binder();
        this.mPackageName = ActivityThread.currentPackageName();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    public SystemVibrator(Context context) {
        this.mToken = new Binder();
        this.mPackageName = context.getOpPackageName();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        if (this.mService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return false;
        }
        try {
            return this.mService.hasVibrator();
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(long milliseconds) {
        vibrate(Process.myUid(), this.mPackageName, milliseconds);
    }

    @Override // android.os.Vibrator
    public void vibrate(long[] pattern, int repeat) {
        vibrate(Process.myUid(), this.mPackageName, pattern, repeat);
    }

    @Override // android.os.Vibrator
    public void vibrate(int owningUid, String owningPackage, long milliseconds) {
        if (this.mService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return;
        }
        try {
            this.mService.vibrate(owningUid, owningPackage, milliseconds, this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to vibrate.", e);
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int owningUid, String owningPackage, long[] pattern, int repeat) {
        if (this.mService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
        } else if (repeat < pattern.length) {
            try {
                this.mService.vibratePattern(owningUid, owningPackage, pattern, repeat, this.mToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to vibrate.", e);
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // android.os.Vibrator
    public void cancel() {
        if (this.mService == null) {
            return;
        }
        try {
            this.mService.cancelVibrate(this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to cancel vibration.", e);
        }
    }
}