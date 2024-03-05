package com.android.internal.telephony;

/* loaded from: SmsResponse.class */
public class SmsResponse {
    int mMessageRef;
    String mAckPdu;
    int mErrorCode;

    public SmsResponse(int messageRef, String ackPdu, int errorCode) {
        this.mMessageRef = messageRef;
        this.mAckPdu = ackPdu;
        this.mErrorCode = errorCode;
    }

    public String toString() {
        String ret = "{ mMessageRef = " + this.mMessageRef + ", mErrorCode = " + this.mErrorCode + ", mAckPdu = " + this.mAckPdu + "}";
        return ret;
    }
}