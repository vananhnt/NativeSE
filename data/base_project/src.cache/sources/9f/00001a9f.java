package com.android.internal.telephony;

import android.content.Context;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.test.SimulatedRadioControl;
import com.android.internal.telephony.uicc.IsimRecords;
import com.android.internal.telephony.uicc.UsimServiceTable;
import java.util.List;

/* loaded from: Phone.class */
public interface Phone {
    public static final boolean DEBUG_PHONE = true;
    public static final String FEATURE_ENABLE_MMS = "enableMMS";
    public static final String FEATURE_ENABLE_SUPL = "enableSUPL";
    public static final String FEATURE_ENABLE_DUN = "enableDUN";
    public static final String FEATURE_ENABLE_HIPRI = "enableHIPRI";
    public static final String FEATURE_ENABLE_DUN_ALWAYS = "enableDUNAlways";
    public static final String FEATURE_ENABLE_FOTA = "enableFOTA";
    public static final String FEATURE_ENABLE_IMS = "enableIMS";
    public static final String FEATURE_ENABLE_CBS = "enableCBS";
    public static final String REASON_ROAMING_ON = "roamingOn";
    public static final String REASON_ROAMING_OFF = "roamingOff";
    public static final String REASON_DATA_DISABLED = "dataDisabled";
    public static final String REASON_DATA_ENABLED = "dataEnabled";
    public static final String REASON_DATA_ATTACHED = "dataAttached";
    public static final String REASON_DATA_DETACHED = "dataDetached";
    public static final String REASON_CDMA_DATA_ATTACHED = "cdmaDataAttached";
    public static final String REASON_CDMA_DATA_DETACHED = "cdmaDataDetached";
    public static final String REASON_APN_CHANGED = "apnChanged";
    public static final String REASON_APN_SWITCHED = "apnSwitched";
    public static final String REASON_APN_FAILED = "apnFailed";
    public static final String REASON_RESTORE_DEFAULT_APN = "restoreDefaultApn";
    public static final String REASON_RADIO_TURNED_OFF = "radioTurnedOff";
    public static final String REASON_PDP_RESET = "pdpReset";
    public static final String REASON_VOICE_CALL_ENDED = "2GVoiceCallEnded";
    public static final String REASON_VOICE_CALL_STARTED = "2GVoiceCallStarted";
    public static final String REASON_PS_RESTRICT_ENABLED = "psRestrictEnabled";
    public static final String REASON_PS_RESTRICT_DISABLED = "psRestrictDisabled";
    public static final String REASON_SIM_LOADED = "simLoaded";
    public static final String REASON_NW_TYPE_CHANGED = "nwTypeChanged";
    public static final String REASON_DATA_DEPENDENCY_MET = "dependencyMet";
    public static final String REASON_DATA_DEPENDENCY_UNMET = "dependencyUnmet";
    public static final String REASON_LOST_DATA_CONNECTION = "lostDataConnection";
    public static final String REASON_CONNECTED = "connected";
    public static final int BM_UNSPECIFIED = 0;
    public static final int BM_EURO_BAND = 1;
    public static final int BM_US_BAND = 2;
    public static final int BM_JPN_BAND = 3;
    public static final int BM_AUS_BAND = 4;
    public static final int BM_AUS2_BAND = 5;
    public static final int BM_BOUNDARY = 6;
    public static final int NT_MODE_WCDMA_PREF = 0;
    public static final int NT_MODE_GSM_ONLY = 1;
    public static final int NT_MODE_WCDMA_ONLY = 2;
    public static final int NT_MODE_GSM_UMTS = 3;
    public static final int NT_MODE_CDMA = 4;
    public static final int NT_MODE_CDMA_NO_EVDO = 5;
    public static final int NT_MODE_EVDO_NO_CDMA = 6;
    public static final int NT_MODE_GLOBAL = 7;
    public static final int NT_MODE_LTE_CDMA_AND_EVDO = 8;
    public static final int NT_MODE_LTE_GSM_WCDMA = 9;
    public static final int NT_MODE_LTE_CMDA_EVDO_GSM_WCDMA = 10;
    public static final int NT_MODE_LTE_ONLY = 11;
    public static final int NT_MODE_LTE_WCDMA = 12;
    public static final int PREFERRED_NT_MODE = 0;
    public static final int CDMA_RM_HOME = 0;
    public static final int CDMA_RM_AFFILIATED = 1;
    public static final int CDMA_RM_ANY = 2;
    public static final int CDMA_SUBSCRIPTION_UNKNOWN = -1;
    public static final int CDMA_SUBSCRIPTION_RUIM_SIM = 0;
    public static final int CDMA_SUBSCRIPTION_NV = 1;
    public static final int PREFERRED_CDMA_SUBSCRIPTION = 1;
    public static final int TTY_MODE_OFF = 0;
    public static final int TTY_MODE_FULL = 1;
    public static final int TTY_MODE_HCO = 2;
    public static final int TTY_MODE_VCO = 3;
    public static final int CDMA_OTA_PROVISION_STATUS_SPL_UNLOCKED = 0;
    public static final int CDMA_OTA_PROVISION_STATUS_SPC_RETRIES_EXCEEDED = 1;
    public static final int CDMA_OTA_PROVISION_STATUS_A_KEY_EXCHANGED = 2;
    public static final int CDMA_OTA_PROVISION_STATUS_SSD_UPDATED = 3;
    public static final int CDMA_OTA_PROVISION_STATUS_NAM_DOWNLOADED = 4;
    public static final int CDMA_OTA_PROVISION_STATUS_MDN_DOWNLOADED = 5;
    public static final int CDMA_OTA_PROVISION_STATUS_IMSI_DOWNLOADED = 6;
    public static final int CDMA_OTA_PROVISION_STATUS_PRL_DOWNLOADED = 7;
    public static final int CDMA_OTA_PROVISION_STATUS_COMMITTED = 8;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_STARTED = 9;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_STOPPED = 10;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_ABORTED = 11;

    /* loaded from: Phone$DataActivityState.class */
    public enum DataActivityState {
        NONE,
        DATAIN,
        DATAOUT,
        DATAINANDOUT,
        DORMANT
    }

    /* loaded from: Phone$SuppService.class */
    public enum SuppService {
        UNKNOWN,
        SWITCH,
        SEPARATE,
        TRANSFER,
        CONFERENCE,
        REJECT,
        HANGUP
    }

    ServiceState getServiceState();

    CellLocation getCellLocation();

    List<CellInfo> getAllCellInfo();

    void setCellInfoListRate(int i);

    PhoneConstants.DataState getDataConnectionState();

    PhoneConstants.DataState getDataConnectionState(String str);

    DataActivityState getDataActivityState();

    Context getContext();

    void disableDnsCheck(boolean z);

    boolean isDnsCheckDisabled();

    PhoneConstants.State getState();

    String getPhoneName();

    int getPhoneType();

    String[] getActiveApnTypes();

    String getActiveApnHost(String str);

    LinkProperties getLinkProperties(String str);

    LinkCapabilities getLinkCapabilities(String str);

    SignalStrength getSignalStrength();

    void registerForUnknownConnection(Handler handler, int i, Object obj);

    void unregisterForUnknownConnection(Handler handler);

    void registerForPreciseCallStateChanged(Handler handler, int i, Object obj);

    void unregisterForPreciseCallStateChanged(Handler handler);

    void registerForNewRingingConnection(Handler handler, int i, Object obj);

    void unregisterForNewRingingConnection(Handler handler);

    void registerForIncomingRing(Handler handler, int i, Object obj);

    void unregisterForIncomingRing(Handler handler);

    void registerForRingbackTone(Handler handler, int i, Object obj);

    void unregisterForRingbackTone(Handler handler);

    void registerForResendIncallMute(Handler handler, int i, Object obj);

    void unregisterForResendIncallMute(Handler handler);

    void registerForDisconnect(Handler handler, int i, Object obj);

    void unregisterForDisconnect(Handler handler);

    void registerForMmiInitiate(Handler handler, int i, Object obj);

    void unregisterForMmiInitiate(Handler handler);

    void registerForMmiComplete(Handler handler, int i, Object obj);

    void unregisterForMmiComplete(Handler handler);

    void registerForEcmTimerReset(Handler handler, int i, Object obj);

    void unregisterForEcmTimerReset(Handler handler);

    List<? extends MmiCode> getPendingMmiCodes();

    void sendUssdResponse(String str);

    void registerForServiceStateChanged(Handler handler, int i, Object obj);

    void unregisterForServiceStateChanged(Handler handler);

    void registerForSuppServiceNotification(Handler handler, int i, Object obj);

    void unregisterForSuppServiceNotification(Handler handler);

    void registerForSuppServiceFailed(Handler handler, int i, Object obj);

    void unregisterForSuppServiceFailed(Handler handler);

    void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj);

    void unregisterForInCallVoicePrivacyOn(Handler handler);

    void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj);

    void unregisterForInCallVoicePrivacyOff(Handler handler);

    void registerForCdmaOtaStatusChange(Handler handler, int i, Object obj);

    void unregisterForCdmaOtaStatusChange(Handler handler);

    void registerForSubscriptionInfoReady(Handler handler, int i, Object obj);

    void unregisterForSubscriptionInfoReady(Handler handler);

    boolean getIccRecordsLoaded();

    IccCard getIccCard();

    void acceptCall() throws CallStateException;

    void rejectCall() throws CallStateException;

    void switchHoldingAndActive() throws CallStateException;

    boolean canConference();

    void conference() throws CallStateException;

    void enableEnhancedVoicePrivacy(boolean z, Message message);

    void getEnhancedVoicePrivacy(Message message);

    boolean canTransfer();

    void explicitCallTransfer() throws CallStateException;

    void clearDisconnected();

    Call getForegroundCall();

    Call getBackgroundCall();

    Call getRingingCall();

    Connection dial(String str) throws CallStateException;

    Connection dial(String str, UUSInfo uUSInfo) throws CallStateException;

    boolean handlePinMmi(String str);

    boolean handleInCallMmiCommands(String str) throws CallStateException;

    void sendDtmf(char c);

    void startDtmf(char c);

    void stopDtmf();

    void sendBurstDtmf(String str, int i, int i2, Message message);

    void setRadioPower(boolean z);

    boolean getMessageWaitingIndicator();

    boolean getCallForwardingIndicator();

    String getLine1Number();

    String getLine1AlphaTag();

    void setLine1Number(String str, String str2, Message message);

    String getVoiceMailNumber();

    int getVoiceMessageCount();

    String getVoiceMailAlphaTag();

    void setVoiceMailNumber(String str, String str2, Message message);

    void getCallForwardingOption(int i, Message message);

    void setCallForwardingOption(int i, int i2, String str, int i3, Message message);

    void getOutgoingCallerIdDisplay(Message message);

    void setOutgoingCallerIdDisplay(int i, Message message);

    void getCallWaiting(Message message);

    void setCallWaiting(boolean z, Message message);

    void getAvailableNetworks(Message message);

    void setNetworkSelectionModeAutomatic(Message message);

    void selectNetworkManually(OperatorInfo operatorInfo, Message message);

    void setPreferredNetworkType(int i, Message message);

    void getPreferredNetworkType(Message message);

    void getSmscAddress(Message message);

    void setSmscAddress(String str, Message message);

    void getNeighboringCids(Message message);

    void setOnPostDialCharacter(Handler handler, int i, Object obj);

    void setMute(boolean z);

    boolean getMute();

    void setEchoSuppressionEnabled(boolean z);

    void invokeOemRilRequestRaw(byte[] bArr, Message message);

    void invokeOemRilRequestStrings(String[] strArr, Message message);

    void getDataCallList(Message message);

    void updateServiceLocation();

    void enableLocationUpdates();

    void disableLocationUpdates();

    void setUnitTestMode(boolean z);

    boolean getUnitTestMode();

    void setBandMode(int i, Message message);

    void queryAvailableBandMode(Message message);

    boolean getDataRoamingEnabled();

    void setDataRoamingEnabled(boolean z);

    void queryCdmaRoamingPreference(Message message);

    void setCdmaRoamingPreference(int i, Message message);

    void setCdmaSubscription(int i, Message message);

    SimulatedRadioControl getSimulatedRadioControl();

    int enableApnType(String str);

    int disableApnType(String str);

    boolean isDataConnectivityPossible();

    boolean isDataConnectivityPossible(String str);

    String getDeviceId();

    String getDeviceSvn();

    String getSubscriberId();

    String getGroupIdLevel1();

    String getIccSerialNumber();

    String getCdmaMin();

    boolean isMinInfoReady();

    String getCdmaPrlVersion();

    String getEsn();

    String getMeid();

    String getMsisdn();

    String getImei();

    PhoneSubInfo getPhoneSubInfo();

    IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager();

    void setTTYMode(int i, Message message);

    void queryTTYMode(Message message);

    void activateCellBroadcastSms(int i, Message message);

    void getCellBroadcastSmsConfig(Message message);

    void setCellBroadcastSmsConfig(int[] iArr, Message message);

    void notifyDataActivity();

    int getCdmaEriIconIndex();

    int getCdmaEriIconMode();

    String getCdmaEriText();

    void exitEmergencyCallbackMode();

    boolean isOtaSpNumber(String str);

    boolean needsOtaServiceProvisioning();

    void registerForCallWaiting(Handler handler, int i, Object obj);

    void unregisterForCallWaiting(Handler handler);

    void registerForSignalInfo(Handler handler, int i, Object obj);

    void unregisterForSignalInfo(Handler handler);

    void registerForDisplayInfo(Handler handler, int i, Object obj);

    void unregisterForDisplayInfo(Handler handler);

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

    void setOnEcbModeExitResponse(Handler handler, int i, Object obj);

    void unsetOnEcbModeExitResponse(Handler handler);

    int getLteOnCdmaMode();

    boolean isCspPlmnEnabled();

    IsimRecords getIsimRecords();

    void requestIsimAuthentication(String str, Message message);

    void setVoiceMessageWaiting(int i, int i2);

    UsimServiceTable getUsimServiceTable();

    void dispose();

    void removeReferences();

    void updatePhoneObject(int i);
}