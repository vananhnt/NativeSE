package android.content;

import android.app.ActivityManagerNative;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.AndroidException;

/* loaded from: IntentSender.class */
public class IntentSender implements Parcelable {
    private final IIntentSender mTarget;
    public static final Parcelable.Creator<IntentSender> CREATOR = new Parcelable.Creator<IntentSender>() { // from class: android.content.IntentSender.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentSender createFromParcel(Parcel in) {
            IBinder target = in.readStrongBinder();
            if (target != null) {
                return new IntentSender(target);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentSender[] newArray(int size) {
            return new IntentSender[size];
        }
    };

    /* loaded from: IntentSender$OnFinished.class */
    public interface OnFinished {
        void onSendFinished(IntentSender intentSender, Intent intent, int i, String str, Bundle bundle);
    }

    /* loaded from: IntentSender$SendIntentException.class */
    public static class SendIntentException extends AndroidException {
        public SendIntentException() {
        }

        public SendIntentException(String name) {
            super(name);
        }

        public SendIntentException(Exception cause) {
            super(cause);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: IntentSender$FinishedDispatcher.class */
    public static class FinishedDispatcher extends IIntentReceiver.Stub implements Runnable {
        private final IntentSender mIntentSender;
        private final OnFinished mWho;
        private final Handler mHandler;
        private Intent mIntent;
        private int mResultCode;
        private String mResultData;
        private Bundle mResultExtras;

        FinishedDispatcher(IntentSender pi, OnFinished who, Handler handler) {
            this.mIntentSender = pi;
            this.mWho = who;
            this.mHandler = handler;
        }

        @Override // android.content.IIntentReceiver
        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean serialized, boolean sticky, int sendingUser) {
            this.mIntent = intent;
            this.mResultCode = resultCode;
            this.mResultData = data;
            this.mResultExtras = extras;
            if (this.mHandler == null) {
                run();
            } else {
                this.mHandler.post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mWho.onSendFinished(this.mIntentSender, this.mIntent, this.mResultCode, this.mResultData, this.mResultExtras);
        }
    }

    public void sendIntent(Context context, int code, Intent intent, OnFinished onFinished, Handler handler) throws SendIntentException {
        sendIntent(context, code, intent, onFinished, handler, null);
    }

    public void sendIntent(Context context, int code, Intent intent, OnFinished onFinished, Handler handler, String requiredPermission) throws SendIntentException {
        String resolveTypeIfNeeded;
        if (intent != null) {
            try {
                resolveTypeIfNeeded = intent.resolveTypeIfNeeded(context.getContentResolver());
            } catch (RemoteException e) {
                throw new SendIntentException();
            }
        } else {
            resolveTypeIfNeeded = null;
        }
        String resolvedType = resolveTypeIfNeeded;
        int res = this.mTarget.send(code, intent, resolvedType, onFinished != null ? new FinishedDispatcher(this, onFinished, handler) : null, requiredPermission);
        if (res < 0) {
            throw new SendIntentException();
        }
    }

    @Deprecated
    public String getTargetPackage() {
        try {
            return ActivityManagerNative.getDefault().getPackageForIntentSender(this.mTarget);
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getCreatorPackage() {
        try {
            return ActivityManagerNative.getDefault().getPackageForIntentSender(this.mTarget);
        } catch (RemoteException e) {
            return null;
        }
    }

    public int getCreatorUid() {
        try {
            return ActivityManagerNative.getDefault().getUidForIntentSender(this.mTarget);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public UserHandle getCreatorUserHandle() {
        try {
            int uid = ActivityManagerNative.getDefault().getUidForIntentSender(this.mTarget);
            if (uid > 0) {
                return new UserHandle(UserHandle.getUserId(uid));
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean equals(Object otherObj) {
        if (otherObj instanceof IntentSender) {
            return this.mTarget.asBinder().equals(((IntentSender) otherObj).mTarget.asBinder());
        }
        return false;
    }

    public int hashCode() {
        return this.mTarget.asBinder().hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("IntentSender{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(": ");
        sb.append(this.mTarget != null ? this.mTarget.asBinder() : null);
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.mTarget.asBinder());
    }

    public static void writeIntentSenderOrNullToParcel(IntentSender sender, Parcel out) {
        out.writeStrongBinder(sender != null ? sender.mTarget.asBinder() : null);
    }

    public static IntentSender readIntentSenderOrNullFromParcel(Parcel in) {
        IBinder b = in.readStrongBinder();
        if (b != null) {
            return new IntentSender(b);
        }
        return null;
    }

    public IIntentSender getTarget() {
        return this.mTarget;
    }

    public IntentSender(IIntentSender target) {
        this.mTarget = target;
    }

    public IntentSender(IBinder target) {
        this.mTarget = IIntentSender.Stub.asInterface(target);
    }
}