package com.android.internal.telephony;

import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;

/* loaded from: CommandsInterface.class */
public interface CommandsInterface {
    public static final int CLIR_DEFAULT = 0;
    public static final int CLIR_INVOCATION = 1;
    public static final int CLIR_SUPPRESSION = 2;
    public static final int CF_ACTION_DISABLE = 0;
    public static final int CF_ACTION_ENABLE = 1;
    public static final int CF_ACTION_REGISTRATION = 3;
    public static final int CF_ACTION_ERASURE = 4;
    public static final int CF_REASON_UNCONDITIONAL = 0;
    public static final int CF_REASON_BUSY = 1;
    public static final int CF_REASON_NO_REPLY = 2;
    public static final int CF_REASON_NOT_REACHABLE = 3;
    public static final int CF_REASON_ALL = 4;
    public static final int CF_REASON_ALL_CONDITIONAL = 5;
    public static final String CB_FACILITY_BAOC = "AO";
    public static final String CB_FACILITY_BAOIC = "OI";
    public static final String CB_FACILITY_BAOICxH = "OX";
    public static final String CB_FACILITY_BAIC = "AI";
    public static final String CB_FACILITY_BAICr = "IR";
    public static final String CB_FACILITY_BA_ALL = "AB";
    public static final String CB_FACILITY_BA_MO = "AG";
    public static final String CB_FACILITY_BA_MT = "AC";
    public static final String CB_FACILITY_BA_SIM = "SC";
    public static final String CB_FACILITY_BA_FD = "FD";
    public static final int SERVICE_CLASS_NONE = 0;
    public static final int SERVICE_CLASS_VOICE = 1;
    public static final int SERVICE_CLASS_DATA = 2;
    public static final int SERVICE_CLASS_FAX = 4;
    public static final int SERVICE_CLASS_SMS = 8;
    public static final int SERVICE_CLASS_DATA_SYNC = 16;
    public static final int SERVICE_CLASS_DATA_ASYNC = 32;
    public static final int SERVICE_CLASS_PACKET = 64;
    public static final int SERVICE_CLASS_PAD = 128;
    public static final int SERVICE_CLASS_MAX = 128;
    public static final int USSD_MODE_NOTIFY = 0;
    public static final int USSD_MODE_REQUEST = 1;
    public static final int GSM_SMS_FAIL_CAUSE_MEMORY_CAPACITY_EXCEEDED = 211;
    public static final int GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY = 212;
    public static final int GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR = 213;
    public static final int GSM_SMS_FAIL_CAUSE_UNSPECIFIED_ERROR = 255;
    public static final int CDMA_SMS_FAIL_CAUSE_INVALID_TELESERVICE_ID = 4;
    public static final int CDMA_SMS_FAIL_CAUSE_RESOURCE_SHORTAGE = 35;
    public static final int CDMA_SMS_FAIL_CAUSE_OTHER_TERMINAL_PROBLEM = 39;
    public static final int CDMA_SMS_FAIL_CAUSE_ENCODING_PROBLEM = 96;

    RadioState getRadioState();

    void getImsRegistrationState(Message message);

    void registerForRadioStateChanged(Handler handler, int i, Object obj);

    void unregisterForRadioStateChanged(Handler handler);

    void registerForVoiceRadioTechChanged(Handler handler, int i, Object obj);

    void unregisterForVoiceRadioTechChanged(Handler handler);

    void registerForImsNetworkStateChanged(Handler handler, int i, Object obj);

    void unregisterForImsNetworkStateChanged(Handler handler);

    void registerForOn(Handler handler, int i, Object obj);

    void unregisterForOn(Handler handler);

    void registerForAvailable(Handler handler, int i, Object obj);

    void unregisterForAvailable(Handler handler);

    void registerForNotAvailable(Handler handler, int i, Object obj);

    void unregisterForNotAvailable(Handler handler);

    void registerForOffOrNotAvailable(Handler handler, int i, Object obj);

    void unregisterForOffOrNotAvailable(Handler handler);

    void registerForIccStatusChanged(Handler handler, int i, Object obj);

    void unregisterForIccStatusChanged(Handler handler);

    void registerForCallStateChanged(Handler handler, int i, Object obj);

    void unregisterForCallStateChanged(Handler handler);

    void registerForVoiceNetworkStateChanged(Handler handler, int i, Object obj);

    void unregisterForVoiceNetworkStateChanged(Handler handler);

    void registerForDataNetworkStateChanged(Handler handler, int i, Object obj);

    void unregisterForDataNetworkStateChanged(Handler handler);

    void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj);

    void unregisterForInCallVoicePrivacyOn(Handler handler);

    void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj);

    void unregisterForInCallVoicePrivacyOff(Handler handler);

    void setOnNewGsmSms(Handler handler, int i, Object obj);

    void unSetOnNewGsmSms(Handler handler);

    void setOnNewCdmaSms(Handler handler, int i, Object obj);

    void unSetOnNewCdmaSms(Handler handler);

    void setOnNewGsmBroadcastSms(Handler handler, int i, Object obj);

    void unSetOnNewGsmBroadcastSms(Handler handler);

    void setOnSmsOnSim(Handler handler, int i, Object obj);

    void unSetOnSmsOnSim(Handler handler);

    void setOnSmsStatus(Handler handler, int i, Object obj);

    void unSetOnSmsStatus(Handler handler);

    void setOnNITZTime(Handler handler, int i, Object obj);

    void unSetOnNITZTime(Handler handler);

    void setOnUSSD(Handler handler, int i, Object obj);

    void unSetOnUSSD(Handler handler);

    void setOnSignalStrengthUpdate(Handler handler, int i, Object obj);

    void unSetOnSignalStrengthUpdate(Handler handler);

    void setOnIccSmsFull(Handler handler, int i, Object obj);

    void unSetOnIccSmsFull(Handler handler);

    void registerForIccRefresh(Handler handler, int i, Object obj);

    void unregisterForIccRefresh(Handler handler);

    void setOnIccRefresh(Handler handler, int i, Object obj);

    void unsetOnIccRefresh(Handler handler);

    void setOnCallRing(Handler handler, int i, Object obj);

    void unSetOnCallRing(Handler handler);

    void setOnRestrictedStateChanged(Handler handler, int i, Object obj);

    void unSetOnRestrictedStateChanged(Handler handler);

    void setOnSuppServiceNotification(Handler handler, int i, Object obj);

    void unSetOnSuppServiceNotification(Handler handler);

    void setOnCatSessionEnd(Handler handler, int i, Object obj);

    void unSetOnCatSessionEnd(Handler handler);

    void setOnCatProactiveCmd(Handler handler, int i, Object obj);

    void unSetOnCatProactiveCmd(Handler handler);

    void setOnCatEvent(Handler handler, int i, Object obj);

    void unSetOnCatEvent(Handler handler);

    void setOnCatCallSetUp(Handler handler, int i, Object obj);

    void unSetOnCatCallSetUp(Handler handler);

    void setSuppServiceNotifications(boolean z, Message message);

    void registerForDisplayInfo(Handler handler, int i, Object obj);

    void unregisterForDisplayInfo(Handler handler);

    void registerForCallWaitingInfo(Handler handler, int i, Object obj);

    void unregisterForCallWaitingInfo(Handler handler);

    void registerForSignalInfo(Handler handler, int i, Object obj);

    void unregisterForSignalInfo(Handler handler);

    void registerForNumberInfo(Handler handler, int i, Object obj);

    void unregisterForNumberInfo(Handler handler);

    void registerForRedirectedNumberInfo(Handler handler, int i, Object obj);

    void unregisterForRedirectedNumberInfo(Handler handler);

    void registerForLineControlInfo(Handler handler, int i, Object obj);

    void unregisterForLineControlInfo(Handler handler);

    void registerFoT53ClirlInfo(Handler handler, int i, Object obj);

    void unregisterForT53ClirInfo(Handler handler);

    void registerForT53AudioControlInfo(Handler handler, int i, Object obj);

    void unregisterForT53AudioControlInfo(Handler handler);

    void setEmergencyCallbackMode(Handler handler, int i, Object obj);

    void registerForCdmaOtaProvision(Handler handler, int i, Object obj);

    void unregisterForCdmaOtaProvision(Handler handler);

    void registerForRingbackTone(Handler handler, int i, Object obj);

    void unregisterForRingbackTone(Handler handler);

    void registerForResendIncallMute(Handler handler, int i, Object obj);

    void unregisterForResendIncallMute(Handler handler);

    void registerForCdmaSubscriptionChanged(Handler handler, int i, Object obj);

    void unregisterForCdmaSubscriptionChanged(Handler handler);

    void registerForCdmaPrlChanged(Handler handler, int i, Object obj);

    void unregisterForCdmaPrlChanged(Handler handler);

    void registerForExitEmergencyCallbackMode(Handler handler, int i, Object obj);

    void unregisterForExitEmergencyCallbackMode(Handler handler);

    void registerForRilConnected(Handler handler, int i, Object obj);

    void unregisterForRilConnected(Handler handler);

    void supplyIccPin(String str, Message message);

    void supplyIccPinForApp(String str, String str2, Message message);

    void supplyIccPuk(String str, String str2, Message message);

    void supplyIccPukForApp(String str, String str2, String str3, Message message);

    void supplyIccPin2(String str, Message message);

    void supplyIccPin2ForApp(String str, String str2, Message message);

    void supplyIccPuk2(String str, String str2, Message message);

    void supplyIccPuk2ForApp(String str, String str2, String str3, Message message);

    void changeIccPin(String str, String str2, Message message);

    void changeIccPinForApp(String str, String str2, String str3, Message message);

    void changeIccPin2(String str, String str2, Message message);

    void changeIccPin2ForApp(String str, String str2, String str3, Message message);

    void changeBarringPassword(String str, String str2, String str3, Message message);

    void supplyNetworkDepersonalization(String str, Message message);

    void getCurrentCalls(Message message);

    @Deprecated
    void getPDPContextList(Message message);

    void getDataCallList(Message message);

    void dial(String str, int i, Message message);

    void dial(String str, int i, UUSInfo uUSInfo, Message message);

    void getIMSI(Message message);

    void getIMSIForApp(String str, Message message);

    void getIMEI(Message message);

    void getIMEISV(Message message);

    void hangupConnection(int i, Message message);

    void hangupWaitingOrBackground(Message message);

    void hangupForegroundResumeBackground(Message message);

    void switchWaitingOrHoldingAndActive(Message message);

    void conference(Message message);

    void setPreferredVoicePrivacy(boolean z, Message message);

    void getPreferredVoicePrivacy(Message message);

    void separateConnection(int i, Message message);

    void acceptCall(Message message);

    void rejectCall(Message message);

    void explicitCallTransfer(Message message);

    void getLastCallFailCause(Message message);

    @Deprecated
    void getLastPdpFailCause(Message message);

    void getLastDataCallFailCause(Message message);

    void setMute(boolean z, Message message);

    void getMute(Message message);

    void getSignalStrength(Message message);

    void getVoiceRegistrationState(Message message);

    void getDataRegistrationState(Message message);

    void getOperator(Message message);

    void sendDtmf(char c, Message message);

    void startDtmf(char c, Message message);

    void stopDtmf(Message message);

    void sendBurstDtmf(String str, int i, int i2, Message message);

    void sendSMS(String str, String str2, Message message);

    void sendCdmaSms(byte[] bArr, Message message);

    void sendImsGsmSms(String str, String str2, int i, int i2, Message message);

    void sendImsCdmaSms(byte[] bArr, int i, int i2, Message message);

    void deleteSmsOnSim(int i, Message message);

    void deleteSmsOnRuim(int i, Message message);

    void writeSmsToSim(int i, String str, String str2, Message message);

    void writeSmsToRuim(int i, String str, Message message);

    void setRadioPower(boolean z, Message message);

    void acknowledgeLastIncomingGsmSms(boolean z, int i, Message message);

    void acknowledgeLastIncomingCdmaSms(boolean z, int i, Message message);

    void acknowledgeIncomingGsmSmsWithPdu(boolean z, String str, Message message);

    void iccIO(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, Message message);

    void iccIOForApp(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, String str4, Message message);

    void queryCLIP(Message message);

    void getCLIR(Message message);

    void setCLIR(int i, Message message);

    void queryCallWaiting(int i, Message message);

    void setCallWaiting(boolean z, int i, Message message);

    void setCallForward(int i, int i2, int i3, String str, int i4, Message message);

    void queryCallForwardStatus(int i, int i2, String str, Message message);

    void setNetworkSelectionModeAutomatic(Message message);

    void setNetworkSelectionModeManual(String str, Message message);

    void getNetworkSelectionMode(Message message);

    void getAvailableNetworks(Message message);

    void getBasebandVersion(Message message);

    void queryFacilityLock(String str, String str2, int i, Message message);

    void queryFacilityLockForApp(String str, String str2, int i, String str3, Message message);

    void setFacilityLock(String str, boolean z, String str2, int i, Message message);

    void setFacilityLockForApp(String str, boolean z, String str2, int i, String str3, Message message);

    void sendUSSD(String str, Message message);

    void cancelPendingUssd(Message message);

    void resetRadio(Message message);

    void setBandMode(int i, Message message);

    void queryAvailableBandMode(Message message);

    void setCurrentPreferredNetworkType();

    void setPreferredNetworkType(int i, Message message);

    void getPreferredNetworkType(Message message);

    void getNeighboringCids(Message message);

    void setLocationUpdates(boolean z, Message message);

    void getSmscAddress(Message message);

    void setSmscAddress(String str, Message message);

    void reportSmsMemoryStatus(boolean z, Message message);

    void reportStkServiceIsRunning(Message message);

    void invokeOemRilRequestRaw(byte[] bArr, Message message);

    void invokeOemRilRequestStrings(String[] strArr, Message message);

    void sendTerminalResponse(String str, Message message);

    void sendEnvelope(String str, Message message);

    void sendEnvelopeWithStatus(String str, Message message);

    void handleCallSetupRequestFromSim(boolean z, Message message);

    void setGsmBroadcastActivation(boolean z, Message message);

    void setGsmBroadcastConfig(SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr, Message message);

    void getGsmBroadcastConfig(Message message);

    void getDeviceIdentity(Message message);

    void getCDMASubscription(Message message);

    void sendCDMAFeatureCode(String str, Message message);

    void setPhoneType(int i);

    void queryCdmaRoamingPreference(Message message);

    void setCdmaRoamingPreference(int i, Message message);

    void setCdmaSubscriptionSource(int i, Message message);

    void getCdmaSubscriptionSource(Message message);

    void setTTYMode(int i, Message message);

    void queryTTYMode(Message message);

    void setupDataCall(String str, String str2, String str3, String str4, String str5, String str6, String str7, Message message);

    void deactivateDataCall(int i, int i2, Message message);

    void setCdmaBroadcastActivation(boolean z, Message message);

    void setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr, Message message);

    void getCdmaBroadcastConfig(Message message);

    void exitEmergencyCallbackMode(Message message);

    void getIccCardStatus(Message message);

    int getLteOnCdmaMode();

    void requestIsimAuthentication(String str, Message message);

    void getVoiceRadioTechnology(Message message);

    void getCellInfoList(Message message);

    void setCellInfoListRate(int i, Message message);

    void registerForCellInfoList(Handler handler, int i, Object obj);

    void unregisterForCellInfoList(Handler handler);

    void setInitialAttachApn(String str, String str2, int i, String str3, String str4, Message message);

    void testingEmergencyCall();

    int getRilVersion();

    /* loaded from: CommandsInterface$RadioState.class */
    public enum RadioState {
        RADIO_OFF,
        RADIO_UNAVAILABLE,
        RADIO_ON;

        public boolean isOn() {
            return this == RADIO_ON;
        }

        public boolean isAvailable() {
            return this != RADIO_UNAVAILABLE;
        }
    }
}