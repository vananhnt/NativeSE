package com.android.server.updates;

/* loaded from: SmsShortCodesInstallReceiver.class */
public class SmsShortCodesInstallReceiver extends ConfigUpdateInstallReceiver {
    public SmsShortCodesInstallReceiver() {
        super("/data/misc/sms/", "codes", "metadata/", "version");
    }
}