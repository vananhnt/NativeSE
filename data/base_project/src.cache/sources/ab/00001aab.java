package com.android.internal.telephony;

import android.os.ServiceManager;
import com.android.internal.telephony.IPhoneSubInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: PhoneSubInfoProxy.class */
public class PhoneSubInfoProxy extends IPhoneSubInfo.Stub {
    private PhoneSubInfo mPhoneSubInfo;

    public PhoneSubInfoProxy(PhoneSubInfo phoneSubInfo) {
        this.mPhoneSubInfo = phoneSubInfo;
        if (ServiceManager.getService("iphonesubinfo") == null) {
            ServiceManager.addService("iphonesubinfo", this);
        }
    }

    public void setmPhoneSubInfo(PhoneSubInfo phoneSubInfo) {
        this.mPhoneSubInfo = phoneSubInfo;
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getDeviceId() {
        return this.mPhoneSubInfo.getDeviceId();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getDeviceSvn() {
        return this.mPhoneSubInfo.getDeviceSvn();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getSubscriberId() {
        return this.mPhoneSubInfo.getSubscriberId();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getGroupIdLevel1() {
        return this.mPhoneSubInfo.getGroupIdLevel1();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIccSerialNumber() {
        return this.mPhoneSubInfo.getIccSerialNumber();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getLine1Number() {
        return this.mPhoneSubInfo.getLine1Number();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getLine1AlphaTag() {
        return this.mPhoneSubInfo.getLine1AlphaTag();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getMsisdn() {
        return this.mPhoneSubInfo.getMsisdn();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getVoiceMailNumber() {
        return this.mPhoneSubInfo.getVoiceMailNumber();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getCompleteVoiceMailNumber() {
        return this.mPhoneSubInfo.getCompleteVoiceMailNumber();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getVoiceMailAlphaTag() {
        return this.mPhoneSubInfo.getVoiceMailAlphaTag();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIsimImpi() {
        return this.mPhoneSubInfo.getIsimImpi();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIsimDomain() {
        return this.mPhoneSubInfo.getIsimDomain();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String[] getIsimImpu() {
        return this.mPhoneSubInfo.getIsimImpu();
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mPhoneSubInfo.dump(fd, pw, args);
    }
}