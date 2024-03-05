package com.android.internal.telephony;

import android.telephony.Rlog;
import com.android.internal.R;

/* loaded from: TelephonyCapabilities.class */
public class TelephonyCapabilities {
    private static final String LOG_TAG = "TelephonyCapabilities";

    private TelephonyCapabilities() {
    }

    public static boolean supportsEcm(Phone phone) {
        return phone.getPhoneType() == 2;
    }

    public static boolean supportsOtasp(Phone phone) {
        return phone.getPhoneType() == 2;
    }

    public static boolean supportsVoiceMessageCount(Phone phone) {
        return phone.getPhoneType() == 2;
    }

    public static boolean supportsNetworkSelection(Phone phone) {
        return phone.getPhoneType() == 1;
    }

    public static int getDeviceIdLabel(Phone phone) {
        if (phone.getPhoneType() == 1) {
            return R.string.imei;
        }
        if (phone.getPhoneType() == 2) {
            return R.string.meid;
        }
        Rlog.w(LOG_TAG, "getDeviceIdLabel: no known label for phone " + phone.getPhoneName());
        return 0;
    }

    public static boolean supportsConferenceCallManagement(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3;
    }

    public static boolean supportsHoldAndUnhold(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3;
    }

    public static boolean supportsAnswerAndHold(Phone phone) {
        return phone.getPhoneType() == 1 || phone.getPhoneType() == 3;
    }

    public static boolean supportsAdn(int phoneType) {
        return phoneType == 1;
    }

    public static boolean canDistinguishDialingAndConnected(int phoneType) {
        return phoneType == 1;
    }
}