package android.os;

import android.os.Parcelable;
import com.android.internal.os.IResultReceiver;

/* loaded from: ResultReceiver.class */
public class ResultReceiver implements Parcelable {
    final boolean mLocal;
    final Handler mHandler;
    IResultReceiver mReceiver;
    public static final Parcelable.Creator<ResultReceiver> CREATOR = new Parcelable.Creator<ResultReceiver>() { // from class: android.os.ResultReceiver.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResultReceiver createFromParcel(Parcel in) {
            return new ResultReceiver(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResultReceiver[] newArray(int size) {
            return new ResultReceiver[size];
        }
    };

    /* loaded from: ResultReceiver$MyRunnable.class */
    class MyRunnable implements Runnable {
        final int mResultCode;
        final Bundle mResultData;

        MyRunnable(int resultCode, Bundle resultData) {
            this.mResultCode = resultCode;
            this.mResultData = resultData;
        }

        @Override // java.lang.Runnable
        public void run() {
            ResultReceiver.this.onReceiveResult(this.mResultCode, this.mResultData);
        }
    }

    /* loaded from: ResultReceiver$MyResultReceiver.class */
    class MyResultReceiver extends IResultReceiver.Stub {
        MyResultReceiver() {
        }

        @Override // com.android.internal.os.IResultReceiver
        public void send(int resultCode, Bundle resultData) {
            if (ResultReceiver.this.mHandler != null) {
                ResultReceiver.this.mHandler.post(new MyRunnable(resultCode, resultData));
            } else {
                ResultReceiver.this.onReceiveResult(resultCode, resultData);
            }
        }
    }

    public ResultReceiver(Handler handler) {
        this.mLocal = true;
        this.mHandler = handler;
    }

    public void send(int resultCode, Bundle resultData) {
        if (this.mLocal) {
            if (this.mHandler != null) {
                this.mHandler.post(new MyRunnable(resultCode, resultData));
            } else {
                onReceiveResult(resultCode, resultData);
            }
        } else if (this.mReceiver != null) {
            try {
                this.mReceiver.send(resultCode, resultData);
            } catch (RemoteException e) {
            }
        }
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        synchronized (this) {
            if (this.mReceiver == null) {
                this.mReceiver = new MyResultReceiver();
            }
            out.writeStrongBinder(this.mReceiver.asBinder());
        }
    }

    ResultReceiver(Parcel in) {
        this.mLocal = false;
        this.mHandler = null;
        this.mReceiver = IResultReceiver.Stub.asInterface(in.readStrongBinder());
    }
}