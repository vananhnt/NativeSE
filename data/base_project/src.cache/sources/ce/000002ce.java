package android.content;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.QueuedWork;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: BroadcastReceiver.class */
public abstract class BroadcastReceiver {
    private PendingResult mPendingResult;
    private boolean mDebugUnregister;

    public abstract void onReceive(Context context, Intent intent);

    /* loaded from: BroadcastReceiver$PendingResult.class */
    public static class PendingResult {
        public static final int TYPE_COMPONENT = 0;
        public static final int TYPE_REGISTERED = 1;
        public static final int TYPE_UNREGISTERED = 2;
        final int mType;
        final boolean mOrderedHint;
        final boolean mInitialStickyHint;
        final IBinder mToken;
        final int mSendingUser;
        int mResultCode;
        String mResultData;
        Bundle mResultExtras;
        boolean mAbortBroadcast;
        boolean mFinished;

        public PendingResult(int resultCode, String resultData, Bundle resultExtras, int type, boolean ordered, boolean sticky, IBinder token, int userId) {
            this.mResultCode = resultCode;
            this.mResultData = resultData;
            this.mResultExtras = resultExtras;
            this.mType = type;
            this.mOrderedHint = ordered;
            this.mInitialStickyHint = sticky;
            this.mToken = token;
            this.mSendingUser = userId;
        }

        public final void setResultCode(int code) {
            checkSynchronousHint();
            this.mResultCode = code;
        }

        public final int getResultCode() {
            return this.mResultCode;
        }

        public final void setResultData(String data) {
            checkSynchronousHint();
            this.mResultData = data;
        }

        public final String getResultData() {
            return this.mResultData;
        }

        public final void setResultExtras(Bundle extras) {
            checkSynchronousHint();
            this.mResultExtras = extras;
        }

        public final Bundle getResultExtras(boolean makeMap) {
            Bundle e = this.mResultExtras;
            if (makeMap) {
                if (e == null) {
                    Bundle bundle = new Bundle();
                    e = bundle;
                    this.mResultExtras = bundle;
                }
                return e;
            }
            return e;
        }

        public final void setResult(int code, String data, Bundle extras) {
            checkSynchronousHint();
            this.mResultCode = code;
            this.mResultData = data;
            this.mResultExtras = extras;
        }

        public final boolean getAbortBroadcast() {
            return this.mAbortBroadcast;
        }

        public final void abortBroadcast() {
            checkSynchronousHint();
            this.mAbortBroadcast = true;
        }

        public final void clearAbortBroadcast() {
            this.mAbortBroadcast = false;
        }

        public final void finish() {
            if (this.mType != 0) {
                if (this.mOrderedHint && this.mType != 2) {
                    sendFinished(ActivityManagerNative.getDefault());
                    return;
                }
                return;
            }
            final IActivityManager mgr = ActivityManagerNative.getDefault();
            if (QueuedWork.hasPendingWork()) {
                QueuedWork.singleThreadExecutor().execute(new Runnable() { // from class: android.content.BroadcastReceiver.PendingResult.1
                    @Override // java.lang.Runnable
                    public void run() {
                        PendingResult.this.sendFinished(mgr);
                    }
                });
            } else {
                sendFinished(mgr);
            }
        }

        public void setExtrasClassLoader(ClassLoader cl) {
            if (this.mResultExtras != null) {
                this.mResultExtras.setClassLoader(cl);
            }
        }

        public void sendFinished(IActivityManager am) {
            synchronized (this) {
                if (this.mFinished) {
                    throw new IllegalStateException("Broadcast already finished");
                }
                this.mFinished = true;
                try {
                    if (this.mResultExtras != null) {
                        this.mResultExtras.setAllowFds(false);
                    }
                    if (this.mOrderedHint) {
                        am.finishReceiver(this.mToken, this.mResultCode, this.mResultData, this.mResultExtras, this.mAbortBroadcast);
                    } else {
                        am.finishReceiver(this.mToken, 0, null, null, false);
                    }
                } catch (RemoteException e) {
                }
            }
        }

        public int getSendingUserId() {
            return this.mSendingUser;
        }

        void checkSynchronousHint() {
            if (this.mOrderedHint || this.mInitialStickyHint) {
                return;
            }
            RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
            e.fillInStackTrace();
            Log.e("BroadcastReceiver", e.getMessage(), e);
        }
    }

    public final PendingResult goAsync() {
        PendingResult res = this.mPendingResult;
        this.mPendingResult = null;
        return res;
    }

    public IBinder peekService(Context myContext, Intent service) {
        IActivityManager am = ActivityManagerNative.getDefault();
        IBinder binder = null;
        try {
            service.prepareToLeaveProcess();
            binder = am.peekService(service, service.resolveTypeIfNeeded(myContext.getContentResolver()));
        } catch (RemoteException e) {
        }
        return binder;
    }

    public final void setResultCode(int code) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
    }

    public final int getResultCode() {
        if (this.mPendingResult != null) {
            return this.mPendingResult.mResultCode;
        }
        return 0;
    }

    public final void setResultData(String data) {
        checkSynchronousHint();
        this.mPendingResult.mResultData = data;
    }

    public final String getResultData() {
        if (this.mPendingResult != null) {
            return this.mPendingResult.mResultData;
        }
        return null;
    }

    public final void setResultExtras(Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultExtras = extras;
    }

    public final Bundle getResultExtras(boolean makeMap) {
        if (this.mPendingResult == null) {
            return null;
        }
        Bundle e = this.mPendingResult.mResultExtras;
        if (makeMap) {
            if (e == null) {
                PendingResult pendingResult = this.mPendingResult;
                Bundle bundle = new Bundle();
                e = bundle;
                pendingResult.mResultExtras = bundle;
            }
            return e;
        }
        return e;
    }

    public final void setResult(int code, String data, Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
        this.mPendingResult.mResultData = data;
        this.mPendingResult.mResultExtras = extras;
    }

    public final boolean getAbortBroadcast() {
        if (this.mPendingResult != null) {
            return this.mPendingResult.mAbortBroadcast;
        }
        return false;
    }

    public final void abortBroadcast() {
        checkSynchronousHint();
        this.mPendingResult.mAbortBroadcast = true;
    }

    public final void clearAbortBroadcast() {
        if (this.mPendingResult != null) {
            this.mPendingResult.mAbortBroadcast = false;
        }
    }

    public final boolean isOrderedBroadcast() {
        if (this.mPendingResult != null) {
            return this.mPendingResult.mOrderedHint;
        }
        return false;
    }

    public final boolean isInitialStickyBroadcast() {
        if (this.mPendingResult != null) {
            return this.mPendingResult.mInitialStickyHint;
        }
        return false;
    }

    public final void setOrderedHint(boolean isOrdered) {
    }

    public final void setPendingResult(PendingResult result) {
        this.mPendingResult = result;
    }

    public final PendingResult getPendingResult() {
        return this.mPendingResult;
    }

    public int getSendingUserId() {
        return this.mPendingResult.mSendingUser;
    }

    public final void setDebugUnregister(boolean debug) {
        this.mDebugUnregister = debug;
    }

    public final boolean getDebugUnregister() {
        return this.mDebugUnregister;
    }

    void checkSynchronousHint() {
        if (this.mPendingResult == null) {
            throw new IllegalStateException("Call while result is not pending");
        }
        if (this.mPendingResult.mOrderedHint || this.mPendingResult.mInitialStickyHint) {
            return;
        }
        RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
        e.fillInStackTrace();
        Log.e("BroadcastReceiver", e.getMessage(), e);
    }
}