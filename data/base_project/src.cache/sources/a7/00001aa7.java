package com.android.internal.telephony;

import android.telephony.CellInfo;
import com.android.internal.telephony.PhoneConstants;
import java.util.List;

/* loaded from: PhoneNotifier.class */
public interface PhoneNotifier {
    void notifyPhoneState(Phone phone);

    void notifyServiceState(Phone phone);

    void notifyCellLocation(Phone phone);

    void notifySignalStrength(Phone phone);

    void notifyMessageWaitingChanged(Phone phone);

    void notifyCallForwardingChanged(Phone phone);

    void notifyDataConnection(Phone phone, String str, String str2, PhoneConstants.DataState dataState);

    void notifyDataConnectionFailed(Phone phone, String str, String str2);

    void notifyDataActivity(Phone phone);

    void notifyOtaspChanged(Phone phone, int i);

    void notifyCellInfo(Phone phone, List<CellInfo> list);
}