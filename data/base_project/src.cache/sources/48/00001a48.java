package com.android.internal.telephony;

import android.telephony.PhoneNumberUtils;

/* loaded from: CallForwardInfo.class */
public class CallForwardInfo {
    public int status;
    public int reason;
    public int serviceClass;
    public int toa;
    public String number;
    public int timeSeconds;

    public String toString() {
        return super.toString() + (this.status == 0 ? " not active " : " active ") + " reason: " + this.reason + " serviceClass: " + this.serviceClass + " \"" + PhoneNumberUtils.stringFromStringAndTOA(this.number, this.toa) + "\" " + this.timeSeconds + " seconds";
    }
}