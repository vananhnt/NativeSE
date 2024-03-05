package com.android.internal.telephony;

import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;

/* loaded from: IccCard.class */
public interface IccCard {
    IccCardConstants.State getState();

    IccRecords getIccRecords();

    IccFileHandler getIccFileHandler();

    void registerForAbsent(Handler handler, int i, Object obj);

    void unregisterForAbsent(Handler handler);

    void registerForNetworkLocked(Handler handler, int i, Object obj);

    void unregisterForNetworkLocked(Handler handler);

    void registerForLocked(Handler handler, int i, Object obj);

    void unregisterForLocked(Handler handler);

    void supplyPin(String str, Message message);

    void supplyPuk(String str, String str2, Message message);

    void supplyPin2(String str, Message message);

    void supplyPuk2(String str, String str2, Message message);

    void supplyNetworkDepersonalization(String str, Message message);

    boolean getIccLockEnabled();

    boolean getIccFdnEnabled();

    void setIccLockEnabled(boolean z, String str, Message message);

    void setIccFdnEnabled(boolean z, String str, Message message);

    void changeIccLockPassword(String str, String str2, Message message);

    void changeIccFdnPassword(String str, String str2, Message message);

    String getServiceProviderName();

    boolean isApplicationOnIcc(IccCardApplicationStatus.AppType appType);

    boolean hasIccCard();
}