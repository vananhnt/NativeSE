package com.android.internal.telephony;

import android.os.ServiceManager;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.uicc.AdnRecord;
import java.util.List;

/* loaded from: IccPhoneBookInterfaceManagerProxy.class */
public class IccPhoneBookInterfaceManagerProxy extends IIccPhoneBook.Stub {
    private IccPhoneBookInterfaceManager mIccPhoneBookInterfaceManager;

    public IccPhoneBookInterfaceManagerProxy(IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager) {
        this.mIccPhoneBookInterfaceManager = iccPhoneBookInterfaceManager;
        if (ServiceManager.getService("simphonebook") == null) {
            ServiceManager.addService("simphonebook", this);
        }
    }

    public void setmIccPhoneBookInterfaceManager(IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager) {
        this.mIccPhoneBookInterfaceManager = iccPhoneBookInterfaceManager;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfBySearch(int efid, String oldTag, String oldPhoneNumber, String newTag, String newPhoneNumber, String pin2) {
        return this.mIccPhoneBookInterfaceManager.updateAdnRecordsInEfBySearch(efid, oldTag, oldPhoneNumber, newTag, newPhoneNumber, pin2);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfByIndex(int efid, String newTag, String newPhoneNumber, int index, String pin2) {
        return this.mIccPhoneBookInterfaceManager.updateAdnRecordsInEfByIndex(efid, newTag, newPhoneNumber, index, pin2);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public int[] getAdnRecordsSize(int efid) {
        return this.mIccPhoneBookInterfaceManager.getAdnRecordsSize(efid);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public List<AdnRecord> getAdnRecordsInEf(int efid) {
        return this.mIccPhoneBookInterfaceManager.getAdnRecordsInEf(efid);
    }
}