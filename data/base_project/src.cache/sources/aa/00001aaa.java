package com.android.internal.telephony;

import android.Manifest;
import android.content.Context;
import android.os.Binder;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.uicc.IsimRecords;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: PhoneSubInfo.class */
public class PhoneSubInfo extends IPhoneSubInfo.Stub {
    static final String LOG_TAG = "PhoneSubInfo";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private Phone mPhone;
    private Context mContext;
    private static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    private static final String CALL_PRIVILEGED = "android.permission.CALL_PRIVILEGED";
    private static final String READ_PRIVILEGED_PHONE_STATE = "android.permission.READ_PRIVILEGED_PHONE_STATE";

    public PhoneSubInfo(Phone phone) {
        this.mPhone = phone;
        this.mContext = phone.getContext();
    }

    public void dispose() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.Binder
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            loge("Error while finalizing:", throwable);
        }
        log("PhoneSubInfo finalized");
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getDeviceId() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getDeviceId();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getDeviceSvn() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getDeviceSvn();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getSubscriberId() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getSubscriberId();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getGroupIdLevel1() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getGroupIdLevel1();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIccSerialNumber() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getIccSerialNumber();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getLine1Number() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getLine1Number();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getLine1AlphaTag() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getLine1AlphaTag();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getMsisdn() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getMsisdn();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getVoiceMailNumber() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        String number = PhoneNumberUtils.extractNetworkPortion(this.mPhone.getVoiceMailNumber());
        return number;
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getCompleteVoiceMailNumber() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CALL_PRIVILEGED", "Requires CALL_PRIVILEGED");
        String number = this.mPhone.getVoiceMailNumber();
        return number;
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getVoiceMailAlphaTag() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "Requires READ_PHONE_STATE");
        return this.mPhone.getVoiceMailAlphaTag();
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIsimImpi() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", "Requires READ_PRIVILEGED_PHONE_STATE");
        IsimRecords isim = this.mPhone.getIsimRecords();
        if (isim != null) {
            return isim.getIsimImpi();
        }
        return null;
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String getIsimDomain() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", "Requires READ_PRIVILEGED_PHONE_STATE");
        IsimRecords isim = this.mPhone.getIsimRecords();
        if (isim != null) {
            return isim.getIsimDomain();
        }
        return null;
    }

    @Override // com.android.internal.telephony.IPhoneSubInfo
    public String[] getIsimImpu() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", "Requires READ_PRIVILEGED_PHONE_STATE");
        IsimRecords isim = this.mPhone.getIsimRecords();
        if (isim != null) {
            return isim.getIsimImpu();
        }
        return null;
    }

    private void log(String s) {
        Rlog.d(LOG_TAG, s);
    }

    private void loge(String s, Throwable e) {
        Rlog.e(LOG_TAG, s, e);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump PhoneSubInfo from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Phone Subscriber Info:");
        pw.println("  Phone Type = " + this.mPhone.getPhoneName());
        pw.println("  Device ID = " + this.mPhone.getDeviceId());
    }
}