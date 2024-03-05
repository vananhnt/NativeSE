package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Message;
import android.os.Parcel;
import android.telephony.Rlog;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: RIL.java */
/* loaded from: RILRequest.class */
public class RILRequest {
    static final String LOG_TAG = "RilRequest";
    static Random sRandom = new Random();
    static AtomicInteger sNextSerial = new AtomicInteger(0);
    private static Object sPoolSync = new Object();
    private static RILRequest sPool = null;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 4;
    int mSerial;
    int mRequest;
    Message mResult;
    Parcel mParcel;
    RILRequest mNext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RILRequest obtain(int request, Message result) {
        RILRequest rr = null;
        synchronized (sPoolSync) {
            if (sPool != null) {
                rr = sPool;
                sPool = rr.mNext;
                rr.mNext = null;
                sPoolSize--;
            }
        }
        if (rr == null) {
            rr = new RILRequest();
        }
        rr.mSerial = sNextSerial.getAndIncrement();
        rr.mRequest = request;
        rr.mResult = result;
        rr.mParcel = Parcel.obtain();
        if (result != null && result.getTarget() == null) {
            throw new NullPointerException("Message target must not be null");
        }
        rr.mParcel.writeInt(request);
        rr.mParcel.writeInt(rr.mSerial);
        return rr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        synchronized (sPoolSync) {
            if (sPoolSize < 4) {
                this.mNext = sPool;
                sPool = this;
                sPoolSize++;
                this.mResult = null;
            }
        }
    }

    private RILRequest() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void resetSerial() {
        sNextSerial.set(sRandom.nextInt());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String serialString() {
        StringBuilder sb = new StringBuilder(8);
        long adjustedSerial = (this.mSerial - (-2147483648L)) % 10000;
        String sn = Long.toString(adjustedSerial);
        sb.append('[');
        int s = sn.length();
        for (int i = 0; i < 4 - s; i++) {
            sb.append('0');
        }
        sb.append(sn);
        sb.append(']');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onError(int error, Object ret) {
        CommandException ex = CommandException.fromRilErrno(error);
        Rlog.d(LOG_TAG, serialString() + "< " + RIL.requestToString(this.mRequest) + " error: " + ex);
        if (this.mResult != null) {
            AsyncResult.forMessage(this.mResult, ret, ex);
            this.mResult.sendToTarget();
        }
        if (this.mParcel != null) {
            this.mParcel.recycle();
            this.mParcel = null;
        }
    }
}