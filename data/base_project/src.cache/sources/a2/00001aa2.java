package com.android.internal.telephony;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.wifi.WifiManager;
import android.os.AsyncResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RegistrantList;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CellIdentityCdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.dataconnection.DcTrackerBase;
import com.android.internal.telephony.test.SimulatedRadioControl;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IsimRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UsimServiceTable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: PhoneBase.class */
public abstract class PhoneBase extends Handler implements Phone {
    private static final String LOG_TAG = "PhoneBase";
    public static final String NETWORK_SELECTION_KEY = "network_selection_key";
    public static final String NETWORK_SELECTION_NAME_KEY = "network_selection_name_key";
    public static final String DATA_DISABLED_ON_BOOT_KEY = "disabled_on_boot_key";
    protected static final int EVENT_RADIO_AVAILABLE = 1;
    protected static final int EVENT_SSN = 2;
    protected static final int EVENT_SIM_RECORDS_LOADED = 3;
    protected static final int EVENT_MMI_DONE = 4;
    protected static final int EVENT_RADIO_ON = 5;
    protected static final int EVENT_GET_BASEBAND_VERSION_DONE = 6;
    protected static final int EVENT_USSD = 7;
    protected static final int EVENT_RADIO_OFF_OR_NOT_AVAILABLE = 8;
    protected static final int EVENT_GET_IMEI_DONE = 9;
    protected static final int EVENT_GET_IMEISV_DONE = 10;
    protected static final int EVENT_GET_SIM_STATUS_DONE = 11;
    protected static final int EVENT_SET_CALL_FORWARD_DONE = 12;
    protected static final int EVENT_GET_CALL_FORWARD_DONE = 13;
    protected static final int EVENT_CALL_RING = 14;
    protected static final int EVENT_CALL_RING_CONTINUE = 15;
    protected static final int EVENT_SET_NETWORK_MANUAL_COMPLETE = 16;
    protected static final int EVENT_SET_NETWORK_AUTOMATIC_COMPLETE = 17;
    protected static final int EVENT_SET_CLIR_COMPLETE = 18;
    protected static final int EVENT_REGISTERED_TO_NETWORK = 19;
    protected static final int EVENT_SET_VM_NUMBER_DONE = 20;
    protected static final int EVENT_GET_DEVICE_IDENTITY_DONE = 21;
    protected static final int EVENT_RUIM_RECORDS_LOADED = 22;
    protected static final int EVENT_NV_READY = 23;
    protected static final int EVENT_SET_ENHANCED_VP = 24;
    protected static final int EVENT_EMERGENCY_CALLBACK_MODE_ENTER = 25;
    protected static final int EVENT_EXIT_EMERGENCY_CALLBACK_RESPONSE = 26;
    protected static final int EVENT_CDMA_SUBSCRIPTION_SOURCE_CHANGED = 27;
    protected static final int EVENT_SET_NETWORK_AUTOMATIC = 28;
    protected static final int EVENT_ICC_RECORD_EVENTS = 29;
    protected static final int EVENT_ICC_CHANGED = 30;
    public static final String CLIR_KEY = "clir_key";
    public static final String DNS_SERVER_CHECK_DISABLED_KEY = "dns_server_check_disabled_key";
    public CommandsInterface mCi;
    boolean mDnsCheckDisabled;
    public DcTrackerBase mDcTracker;
    boolean mDoesRilSendMultipleCallRing;
    int mCallRingContinueToken;
    int mCallRingDelay;
    public boolean mIsTheCurrentActivePhone;
    boolean mIsVoiceCapable;
    protected UiccController mUiccController;
    public AtomicReference<IccRecords> mIccRecords;
    public SmsStorageMonitor mSmsStorageMonitor;
    public SmsUsageMonitor mSmsUsageMonitor;
    protected AtomicReference<UiccCardApplication> mUiccApplication;
    private TelephonyTester mTelephonyTester;
    private final String mName;
    private final String mActionDetached;
    private final String mActionAttached;
    protected final RegistrantList mPreciseCallStateRegistrants;
    protected final RegistrantList mNewRingingConnectionRegistrants;
    protected final RegistrantList mIncomingRingRegistrants;
    protected final RegistrantList mDisconnectRegistrants;
    protected final RegistrantList mServiceStateRegistrants;
    protected final RegistrantList mMmiCompleteRegistrants;
    protected final RegistrantList mMmiRegistrants;
    protected final RegistrantList mUnknownConnectionRegistrants;
    protected final RegistrantList mSuppServiceFailedRegistrants;
    protected Looper mLooper;
    protected final Context mContext;
    protected PhoneNotifier mNotifier;
    protected SimulatedRadioControl mSimulatedRadioControl;
    boolean mUnitTestMode;

    protected abstract void onUpdateIccAvailability();

    @Override // com.android.internal.telephony.Phone
    public abstract PhoneConstants.State getState();

    @Override // com.android.internal.telephony.Phone
    public abstract int getPhoneType();

    @Override // com.android.internal.telephony.Phone
    public String getPhoneName() {
        return this.mName;
    }

    public String getActionDetached() {
        return this.mActionDetached;
    }

    public String getActionAttached() {
        return this.mActionAttached;
    }

    public void setSystemProperty(String property, String value) {
        if (getUnitTestMode()) {
            return;
        }
        SystemProperties.set(property, value);
    }

    protected PhoneBase(String name, PhoneNotifier notifier, Context context, CommandsInterface ci) {
        this(name, notifier, context, ci, false);
    }

    protected PhoneBase(String name, PhoneNotifier notifier, Context context, CommandsInterface ci, boolean unitTestMode) {
        this.mIsTheCurrentActivePhone = true;
        this.mIsVoiceCapable = true;
        this.mUiccController = null;
        this.mIccRecords = new AtomicReference<>();
        this.mUiccApplication = new AtomicReference<>();
        this.mPreciseCallStateRegistrants = new RegistrantList();
        this.mNewRingingConnectionRegistrants = new RegistrantList();
        this.mIncomingRingRegistrants = new RegistrantList();
        this.mDisconnectRegistrants = new RegistrantList();
        this.mServiceStateRegistrants = new RegistrantList();
        this.mMmiCompleteRegistrants = new RegistrantList();
        this.mMmiRegistrants = new RegistrantList();
        this.mUnknownConnectionRegistrants = new RegistrantList();
        this.mSuppServiceFailedRegistrants = new RegistrantList();
        this.mName = name;
        this.mNotifier = notifier;
        this.mContext = context;
        this.mLooper = Looper.myLooper();
        this.mCi = ci;
        this.mActionDetached = getClass().getPackage().getName() + ".action_detached";
        this.mActionAttached = getClass().getPackage().getName() + ".action_attached";
        if (Build.IS_DEBUGGABLE) {
            this.mTelephonyTester = new TelephonyTester(this);
        }
        setPropertiesByCarrier();
        setUnitTestMode(unitTestMode);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.mDnsCheckDisabled = sp.getBoolean(DNS_SERVER_CHECK_DISABLED_KEY, false);
        this.mCi.setOnCallRing(this, 14, null);
        this.mIsVoiceCapable = this.mContext.getResources().getBoolean(R.bool.config_voice_capable);
        this.mDoesRilSendMultipleCallRing = SystemProperties.getBoolean(TelephonyProperties.PROPERTY_RIL_SENDS_MULTIPLE_CALL_RING, true);
        Rlog.d(LOG_TAG, "mDoesRilSendMultipleCallRing=" + this.mDoesRilSendMultipleCallRing);
        this.mCallRingDelay = SystemProperties.getInt(TelephonyProperties.PROPERTY_CALL_RING_DELAY, ConnectivityManager.CONNECTIVITY_CHANGE_DELAY_DEFAULT);
        Rlog.d(LOG_TAG, "mCallRingDelay=" + this.mCallRingDelay);
        this.mSmsStorageMonitor = new SmsStorageMonitor(this);
        this.mSmsUsageMonitor = new SmsUsageMonitor(context);
        this.mUiccController = UiccController.getInstance();
        this.mUiccController.registerForIccChanged(this, 30, (Object) null);
    }

    @Override // com.android.internal.telephony.Phone
    public void dispose() {
        synchronized (PhoneProxy.lockForRadioTechnologyChange) {
            this.mCi.unSetOnCallRing(this);
            this.mDcTracker.cleanUpAllConnections((String) null);
            this.mIsTheCurrentActivePhone = false;
            this.mSmsStorageMonitor.dispose();
            this.mSmsUsageMonitor.dispose();
            this.mUiccController.unregisterForIccChanged(this);
            if (this.mTelephonyTester != null) {
                this.mTelephonyTester.dispose();
            }
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void removeReferences() {
        this.mSmsStorageMonitor = null;
        this.mSmsUsageMonitor = null;
        this.mIccRecords.set(null);
        this.mUiccApplication.set(null);
        this.mDcTracker = null;
        this.mUiccController = null;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (!this.mIsTheCurrentActivePhone) {
            Rlog.e(LOG_TAG, "Received message " + msg + "[" + msg.what + "] while being destroyed. Ignoring.");
            return;
        }
        switch (msg.what) {
            case 14:
                Rlog.d(LOG_TAG, "Event EVENT_CALL_RING Received state=" + getState());
                AsyncResult ar = (AsyncResult) msg.obj;
                if (ar.exception == null) {
                    PhoneConstants.State state = getState();
                    if (!this.mDoesRilSendMultipleCallRing && (state == PhoneConstants.State.RINGING || state == PhoneConstants.State.IDLE)) {
                        this.mCallRingContinueToken++;
                        sendIncomingCallRingNotification(this.mCallRingContinueToken);
                        return;
                    }
                    notifyIncomingRing();
                    return;
                }
                return;
            case 15:
                Rlog.d(LOG_TAG, "Event EVENT_CALL_RING_CONTINUE Received stat=" + getState());
                if (getState() == PhoneConstants.State.RINGING) {
                    sendIncomingCallRingNotification(msg.arg1);
                    return;
                }
                return;
            case 30:
                onUpdateIccAvailability();
                return;
            default:
                throw new RuntimeException("unexpected event not handled");
        }
    }

    @Override // com.android.internal.telephony.Phone
    public Context getContext() {
        return this.mContext;
    }

    @Override // com.android.internal.telephony.Phone
    public void disableDnsCheck(boolean b) {
        this.mDnsCheckDisabled = b;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(DNS_SERVER_CHECK_DISABLED_KEY, b);
        editor.apply();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isDnsCheckDisabled() {
        return this.mDnsCheckDisabled;
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForPreciseCallStateChanged(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mPreciseCallStateRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForPreciseCallStateChanged(Handler h) {
        this.mPreciseCallStateRegistrants.remove(h);
    }

    protected void notifyPreciseCallStateChangedP() {
        AsyncResult ar = new AsyncResult(null, this, null);
        this.mPreciseCallStateRegistrants.notifyRegistrants(ar);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForUnknownConnection(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mUnknownConnectionRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForUnknownConnection(Handler h) {
        this.mUnknownConnectionRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForNewRingingConnection(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mNewRingingConnectionRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForNewRingingConnection(Handler h) {
        this.mNewRingingConnectionRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForInCallVoicePrivacyOn(Handler h, int what, Object obj) {
        this.mCi.registerForInCallVoicePrivacyOn(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForInCallVoicePrivacyOn(Handler h) {
        this.mCi.unregisterForInCallVoicePrivacyOn(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForInCallVoicePrivacyOff(Handler h, int what, Object obj) {
        this.mCi.registerForInCallVoicePrivacyOff(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForInCallVoicePrivacyOff(Handler h) {
        this.mCi.unregisterForInCallVoicePrivacyOff(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForIncomingRing(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mIncomingRingRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForIncomingRing(Handler h) {
        this.mIncomingRingRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForDisconnect(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mDisconnectRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForDisconnect(Handler h) {
        this.mDisconnectRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSuppServiceFailed(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mSuppServiceFailedRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSuppServiceFailed(Handler h) {
        this.mSuppServiceFailedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForMmiInitiate(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mMmiRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForMmiInitiate(Handler h) {
        this.mMmiRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForMmiComplete(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mMmiCompleteRegistrants.addUnique(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForMmiComplete(Handler h) {
        checkCorrectThread(h);
        this.mMmiCompleteRegistrants.remove(h);
    }

    private String getSavedNetworkSelection() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sp.getString(NETWORK_SELECTION_KEY, "");
    }

    public void restoreSavedNetworkSelection(Message response) {
        String networkSelection = getSavedNetworkSelection();
        if (TextUtils.isEmpty(networkSelection)) {
            this.mCi.setNetworkSelectionModeAutomatic(response);
        } else {
            this.mCi.setNetworkSelectionModeManual(networkSelection, response);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void setUnitTestMode(boolean f) {
        this.mUnitTestMode = f;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean getUnitTestMode() {
        return this.mUnitTestMode;
    }

    protected void notifyDisconnectP(Connection cn) {
        AsyncResult ar = new AsyncResult(null, cn, null);
        this.mDisconnectRegistrants.notifyRegistrants(ar);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForServiceStateChanged(Handler h, int what, Object obj) {
        checkCorrectThread(h);
        this.mServiceStateRegistrants.add(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForServiceStateChanged(Handler h) {
        this.mServiceStateRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForRingbackTone(Handler h, int what, Object obj) {
        this.mCi.registerForRingbackTone(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForRingbackTone(Handler h) {
        this.mCi.unregisterForRingbackTone(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForResendIncallMute(Handler h, int what, Object obj) {
        this.mCi.registerForResendIncallMute(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForResendIncallMute(Handler h) {
        this.mCi.unregisterForResendIncallMute(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void setEchoSuppressionEnabled(boolean enabled) {
    }

    protected void notifyServiceStateChangedP(ServiceState ss) {
        AsyncResult ar = new AsyncResult(null, ss, null);
        this.mServiceStateRegistrants.notifyRegistrants(ar);
        this.mNotifier.notifyServiceState(this);
    }

    @Override // com.android.internal.telephony.Phone
    public SimulatedRadioControl getSimulatedRadioControl() {
        return this.mSimulatedRadioControl;
    }

    private void checkCorrectThread(Handler h) {
        if (h.getLooper() != this.mLooper) {
            throw new RuntimeException("com.android.internal.telephony.Phone must be used from within one thread");
        }
    }

    private void setPropertiesByCarrier() {
        String carrier = SystemProperties.get("ro.carrier");
        if (null == carrier || 0 == carrier.length() || "unknown".equals(carrier)) {
            return;
        }
        CharSequence[] carrierLocales = this.mContext.getResources().getTextArray(R.array.carrier_properties);
        for (int i = 0; i < carrierLocales.length; i += 3) {
            String c = carrierLocales[i].toString();
            if (carrier.equals(c)) {
                String l = carrierLocales[i + 1].toString();
                String language = l.substring(0, 2);
                String country = "";
                if (l.length() >= 5) {
                    country = l.substring(3, 5);
                }
                MccTable.setSystemLocale(this.mContext, language, country);
                if (!country.isEmpty()) {
                    try {
                        Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE);
                        return;
                    } catch (Settings.SettingNotFoundException e) {
                        WifiManager wM = (WifiManager) this.mContext.getSystemService("wifi");
                        wM.setCountryCode(country, false);
                        return;
                    }
                }
                return;
            }
        }
    }

    public IccFileHandler getIccFileHandler() {
        UiccCardApplication uiccApplication = this.mUiccApplication.get();
        if (uiccApplication == null) {
            return null;
        }
        return uiccApplication.getIccFileHandler();
    }

    public Handler getHandler() {
        return this;
    }

    @Override // com.android.internal.telephony.Phone
    public void updatePhoneObject(int voiceRadioTech) {
        PhoneFactory.getDefaultPhone().updatePhoneObject(voiceRadioTech);
    }

    public ServiceStateTracker getServiceStateTracker() {
        return null;
    }

    public CallTracker getCallTracker() {
        return null;
    }

    public IccCardApplicationStatus.AppType getCurrentUiccAppType() {
        UiccCardApplication currentApp = this.mUiccApplication.get();
        if (currentApp != null) {
            return currentApp.getType();
        }
        return IccCardApplicationStatus.AppType.APPTYPE_UNKNOWN;
    }

    @Override // com.android.internal.telephony.Phone
    public IccCard getIccCard() {
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public String getIccSerialNumber() {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            return r.getIccId();
        }
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean getIccRecordsLoaded() {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            return r.getRecordsLoaded();
        }
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public List<CellInfo> getAllCellInfo() {
        List<CellInfo> cellInfoList = getServiceStateTracker().getAllCellInfo();
        return privatizeCellInfoList(cellInfoList);
    }

    private List<CellInfo> privatizeCellInfoList(List<CellInfo> cellInfoList) {
        int mode = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE, 0);
        if (mode == 0) {
            ArrayList<CellInfo> privateCellInfoList = new ArrayList<>(cellInfoList.size());
            for (CellInfo c : cellInfoList) {
                if (c instanceof CellInfoCdma) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) c;
                    CellIdentityCdma cellIdentity = cellInfoCdma.getCellIdentity();
                    CellIdentityCdma maskedCellIdentity = new CellIdentityCdma(cellIdentity.getNetworkId(), cellIdentity.getSystemId(), cellIdentity.getBasestationId(), Integer.MAX_VALUE, Integer.MAX_VALUE);
                    CellInfoCdma privateCellInfoCdma = new CellInfoCdma(cellInfoCdma);
                    privateCellInfoCdma.setCellIdentity(maskedCellIdentity);
                    privateCellInfoList.add(privateCellInfoCdma);
                } else {
                    privateCellInfoList.add(c);
                }
            }
            cellInfoList = privateCellInfoList;
        }
        return cellInfoList;
    }

    @Override // com.android.internal.telephony.Phone
    public void setCellInfoListRate(int rateInMillis) {
        this.mCi.setCellInfoListRate(rateInMillis, null);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean getMessageWaitingIndicator() {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            return r.getVoiceMessageWaiting();
        }
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean getCallForwardingIndicator() {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            return r.getVoiceCallForwardingFlag();
        }
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public void queryCdmaRoamingPreference(Message response) {
        this.mCi.queryCdmaRoamingPreference(response);
    }

    @Override // com.android.internal.telephony.Phone
    public SignalStrength getSignalStrength() {
        ServiceStateTracker sst = getServiceStateTracker();
        if (sst == null) {
            return new SignalStrength();
        }
        return sst.getSignalStrength();
    }

    @Override // com.android.internal.telephony.Phone
    public void setCdmaRoamingPreference(int cdmaRoamingType, Message response) {
        this.mCi.setCdmaRoamingPreference(cdmaRoamingType, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void setCdmaSubscription(int cdmaSubscriptionType, Message response) {
        this.mCi.setCdmaSubscriptionSource(cdmaSubscriptionType, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void setPreferredNetworkType(int networkType, Message response) {
        this.mCi.setPreferredNetworkType(networkType, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void getPreferredNetworkType(Message response) {
        this.mCi.getPreferredNetworkType(response);
    }

    @Override // com.android.internal.telephony.Phone
    public void getSmscAddress(Message result) {
        this.mCi.getSmscAddress(result);
    }

    @Override // com.android.internal.telephony.Phone
    public void setSmscAddress(String address, Message result) {
        this.mCi.setSmscAddress(address, result);
    }

    @Override // com.android.internal.telephony.Phone
    public void setTTYMode(int ttyMode, Message onComplete) {
        this.mCi.setTTYMode(ttyMode, onComplete);
    }

    @Override // com.android.internal.telephony.Phone
    public void queryTTYMode(Message onComplete) {
        this.mCi.queryTTYMode(onComplete);
    }

    @Override // com.android.internal.telephony.Phone
    public void enableEnhancedVoicePrivacy(boolean enable, Message onComplete) {
        logUnexpectedCdmaMethodCall("enableEnhancedVoicePrivacy");
    }

    @Override // com.android.internal.telephony.Phone
    public void getEnhancedVoicePrivacy(Message onComplete) {
        logUnexpectedCdmaMethodCall("getEnhancedVoicePrivacy");
    }

    @Override // com.android.internal.telephony.Phone
    public void setBandMode(int bandMode, Message response) {
        this.mCi.setBandMode(bandMode, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void queryAvailableBandMode(Message response) {
        this.mCi.queryAvailableBandMode(response);
    }

    @Override // com.android.internal.telephony.Phone
    public void invokeOemRilRequestRaw(byte[] data, Message response) {
        this.mCi.invokeOemRilRequestRaw(data, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void invokeOemRilRequestStrings(String[] strings, Message response) {
        this.mCi.invokeOemRilRequestStrings(strings, response);
    }

    @Override // com.android.internal.telephony.Phone
    public void notifyDataActivity() {
        this.mNotifier.notifyDataActivity(this);
    }

    public void notifyMessageWaitingIndicator() {
        if (!this.mIsVoiceCapable) {
            return;
        }
        this.mNotifier.notifyMessageWaitingChanged(this);
    }

    public void notifyDataConnection(String reason, String apnType, PhoneConstants.DataState state) {
        this.mNotifier.notifyDataConnection(this, reason, apnType, state);
    }

    public void notifyDataConnection(String reason, String apnType) {
        this.mNotifier.notifyDataConnection(this, reason, apnType, getDataConnectionState(apnType));
    }

    public void notifyDataConnection(String reason) {
        String[] types = getActiveApnTypes();
        for (String apnType : types) {
            this.mNotifier.notifyDataConnection(this, reason, apnType, getDataConnectionState(apnType));
        }
    }

    public void notifyOtaspChanged(int otaspMode) {
        this.mNotifier.notifyOtaspChanged(this, otaspMode);
    }

    public void notifySignalStrength() {
        this.mNotifier.notifySignalStrength(this);
    }

    public void notifyCellInfo(List<CellInfo> cellInfo) {
        this.mNotifier.notifyCellInfo(this, privatizeCellInfoList(cellInfo));
    }

    public boolean isInEmergencyCall() {
        return false;
    }

    public boolean isInEcm() {
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public int getVoiceMessageCount() {
        return 0;
    }

    @Override // com.android.internal.telephony.Phone
    public int getCdmaEriIconIndex() {
        logUnexpectedCdmaMethodCall("getCdmaEriIconIndex");
        return -1;
    }

    @Override // com.android.internal.telephony.Phone
    public int getCdmaEriIconMode() {
        logUnexpectedCdmaMethodCall("getCdmaEriIconMode");
        return -1;
    }

    @Override // com.android.internal.telephony.Phone
    public String getCdmaEriText() {
        logUnexpectedCdmaMethodCall("getCdmaEriText");
        return "GSM nw, no ERI";
    }

    @Override // com.android.internal.telephony.Phone
    public String getCdmaMin() {
        logUnexpectedCdmaMethodCall("getCdmaMin");
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isMinInfoReady() {
        logUnexpectedCdmaMethodCall("isMinInfoReady");
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public String getCdmaPrlVersion() {
        logUnexpectedCdmaMethodCall("getCdmaPrlVersion");
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public void sendBurstDtmf(String dtmfString, int on, int off, Message onComplete) {
        logUnexpectedCdmaMethodCall("sendBurstDtmf");
    }

    @Override // com.android.internal.telephony.Phone
    public void exitEmergencyCallbackMode() {
        logUnexpectedCdmaMethodCall("exitEmergencyCallbackMode");
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForCdmaOtaStatusChange(Handler h, int what, Object obj) {
        logUnexpectedCdmaMethodCall("registerForCdmaOtaStatusChange");
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForCdmaOtaStatusChange(Handler h) {
        logUnexpectedCdmaMethodCall("unregisterForCdmaOtaStatusChange");
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSubscriptionInfoReady(Handler h, int what, Object obj) {
        logUnexpectedCdmaMethodCall("registerForSubscriptionInfoReady");
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSubscriptionInfoReady(Handler h) {
        logUnexpectedCdmaMethodCall("unregisterForSubscriptionInfoReady");
    }

    @Override // com.android.internal.telephony.Phone
    public boolean needsOtaServiceProvisioning() {
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isOtaSpNumber(String dialStr) {
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForCallWaiting(Handler h, int what, Object obj) {
        logUnexpectedCdmaMethodCall("registerForCallWaiting");
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForCallWaiting(Handler h) {
        logUnexpectedCdmaMethodCall("unregisterForCallWaiting");
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForEcmTimerReset(Handler h, int what, Object obj) {
        logUnexpectedCdmaMethodCall("registerForEcmTimerReset");
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForEcmTimerReset(Handler h) {
        logUnexpectedCdmaMethodCall("unregisterForEcmTimerReset");
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSignalInfo(Handler h, int what, Object obj) {
        this.mCi.registerForSignalInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSignalInfo(Handler h) {
        this.mCi.unregisterForSignalInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForDisplayInfo(Handler h, int what, Object obj) {
        this.mCi.registerForDisplayInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForDisplayInfo(Handler h) {
        this.mCi.unregisterForDisplayInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForNumberInfo(Handler h, int what, Object obj) {
        this.mCi.registerForNumberInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForNumberInfo(Handler h) {
        this.mCi.unregisterForNumberInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForRedirectedNumberInfo(Handler h, int what, Object obj) {
        this.mCi.registerForRedirectedNumberInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForRedirectedNumberInfo(Handler h) {
        this.mCi.unregisterForRedirectedNumberInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForLineControlInfo(Handler h, int what, Object obj) {
        this.mCi.registerForLineControlInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForLineControlInfo(Handler h) {
        this.mCi.unregisterForLineControlInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerFoT53ClirlInfo(Handler h, int what, Object obj) {
        this.mCi.registerFoT53ClirlInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForT53ClirInfo(Handler h) {
        this.mCi.unregisterForT53ClirInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForT53AudioControlInfo(Handler h, int what, Object obj) {
        this.mCi.registerForT53AudioControlInfo(h, what, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForT53AudioControlInfo(Handler h) {
        this.mCi.unregisterForT53AudioControlInfo(h);
    }

    @Override // com.android.internal.telephony.Phone
    public void setOnEcbModeExitResponse(Handler h, int what, Object obj) {
        logUnexpectedCdmaMethodCall("setOnEcbModeExitResponse");
    }

    @Override // com.android.internal.telephony.Phone
    public void unsetOnEcbModeExitResponse(Handler h) {
        logUnexpectedCdmaMethodCall("unsetOnEcbModeExitResponse");
    }

    @Override // com.android.internal.telephony.Phone
    public String[] getActiveApnTypes() {
        return this.mDcTracker.getActiveApnTypes();
    }

    @Override // com.android.internal.telephony.Phone
    public String getActiveApnHost(String apnType) {
        return this.mDcTracker.getActiveApnString(apnType);
    }

    @Override // com.android.internal.telephony.Phone
    public LinkProperties getLinkProperties(String apnType) {
        return this.mDcTracker.getLinkProperties(apnType);
    }

    @Override // com.android.internal.telephony.Phone
    public LinkCapabilities getLinkCapabilities(String apnType) {
        return this.mDcTracker.getLinkCapabilities(apnType);
    }

    @Override // com.android.internal.telephony.Phone
    public int enableApnType(String type) {
        return this.mDcTracker.enableApnType(type);
    }

    @Override // com.android.internal.telephony.Phone
    public int disableApnType(String type) {
        return this.mDcTracker.disableApnType(type);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isDataConnectivityPossible() {
        return isDataConnectivityPossible("default");
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isDataConnectivityPossible(String apnType) {
        return this.mDcTracker != null && this.mDcTracker.isDataPossible(apnType);
    }

    protected void notifyNewRingingConnectionP(Connection cn) {
        if (!this.mIsVoiceCapable) {
            return;
        }
        AsyncResult ar = new AsyncResult(null, cn, null);
        this.mNewRingingConnectionRegistrants.notifyRegistrants(ar);
    }

    private void notifyIncomingRing() {
        if (!this.mIsVoiceCapable) {
            return;
        }
        AsyncResult ar = new AsyncResult(null, this, null);
        this.mIncomingRingRegistrants.notifyRegistrants(ar);
    }

    private void sendIncomingCallRingNotification(int token) {
        if (this.mIsVoiceCapable && !this.mDoesRilSendMultipleCallRing && token == this.mCallRingContinueToken) {
            Rlog.d(LOG_TAG, "Sending notifyIncomingRing");
            notifyIncomingRing();
            sendMessageDelayed(obtainMessage(15, token, 0), this.mCallRingDelay);
            return;
        }
        Rlog.d(LOG_TAG, "Ignoring ring notification request, mDoesRilSendMultipleCallRing=" + this.mDoesRilSendMultipleCallRing + " token=" + token + " mCallRingContinueToken=" + this.mCallRingContinueToken + " mIsVoiceCapable=" + this.mIsVoiceCapable);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isCspPlmnEnabled() {
        logUnexpectedGsmMethodCall("isCspPlmnEnabled");
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public IsimRecords getIsimRecords() {
        Rlog.e(LOG_TAG, "getIsimRecords() is only supported on LTE devices");
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public void requestIsimAuthentication(String nonce, Message result) {
        Rlog.e(LOG_TAG, "requestIsimAuthentication() is only supported on LTE devices");
    }

    @Override // com.android.internal.telephony.Phone
    public String getMsisdn() {
        logUnexpectedGsmMethodCall("getMsisdn");
        return null;
    }

    private static void logUnexpectedCdmaMethodCall(String name) {
        Rlog.e(LOG_TAG, "Error! " + name + "() in PhoneBase should not be called, CDMAPhone inactive.");
    }

    @Override // com.android.internal.telephony.Phone
    public PhoneConstants.DataState getDataConnectionState() {
        return getDataConnectionState("default");
    }

    private static void logUnexpectedGsmMethodCall(String name) {
        Rlog.e(LOG_TAG, "Error! " + name + "() in PhoneBase should not be called, GSMPhone inactive.");
    }

    public void notifyCallForwardingIndicator() {
        Rlog.e(LOG_TAG, "Error! This function should never be executed, inactive CDMAPhone.");
    }

    public void notifyDataConnectionFailed(String reason, String apnType) {
        this.mNotifier.notifyDataConnectionFailed(this, reason, apnType);
    }

    @Override // com.android.internal.telephony.Phone
    public int getLteOnCdmaMode() {
        return this.mCi.getLteOnCdmaMode();
    }

    @Override // com.android.internal.telephony.Phone
    public void setVoiceMessageWaiting(int line, int countWaiting) {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            r.setVoiceMessageWaiting(line, countWaiting);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public UsimServiceTable getUsimServiceTable() {
        IccRecords r = this.mIccRecords.get();
        if (r != null) {
            return r.getUsimServiceTable();
        }
        return null;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("PhoneBase:");
        pw.println(" mCi=" + this.mCi);
        pw.println(" mDnsCheckDisabled=" + this.mDnsCheckDisabled);
        pw.println(" mDcTracker=" + this.mDcTracker);
        pw.println(" mDoesRilSendMultipleCallRing=" + this.mDoesRilSendMultipleCallRing);
        pw.println(" mCallRingContinueToken=" + this.mCallRingContinueToken);
        pw.println(" mCallRingDelay=" + this.mCallRingDelay);
        pw.println(" mIsTheCurrentActivePhone=" + this.mIsTheCurrentActivePhone);
        pw.println(" mIsVoiceCapable=" + this.mIsVoiceCapable);
        pw.println(" mIccRecords=" + this.mIccRecords.get());
        pw.println(" mUiccApplication=" + this.mUiccApplication.get());
        pw.println(" mSmsStorageMonitor=" + this.mSmsStorageMonitor);
        pw.println(" mSmsUsageMonitor=" + this.mSmsUsageMonitor);
        pw.flush();
        pw.println(" mLooper=" + this.mLooper);
        pw.println(" mContext=" + this.mContext);
        pw.println(" mNotifier=" + this.mNotifier);
        pw.println(" mSimulatedRadioControl=" + this.mSimulatedRadioControl);
        pw.println(" mUnitTestMode=" + this.mUnitTestMode);
        pw.println(" isDnsCheckDisabled()=" + isDnsCheckDisabled());
        pw.println(" getUnitTestMode()=" + getUnitTestMode());
        pw.println(" getState()=" + getState());
        pw.println(" getIccSerialNumber()=" + getIccSerialNumber());
        pw.println(" getIccRecordsLoaded()=" + getIccRecordsLoaded());
        pw.println(" getMessageWaitingIndicator()=" + getMessageWaitingIndicator());
        pw.println(" getCallForwardingIndicator()=" + getCallForwardingIndicator());
        pw.println(" isInEmergencyCall()=" + isInEmergencyCall());
        pw.flush();
        pw.println(" isInEcm()=" + isInEcm());
        pw.println(" getPhoneName()=" + getPhoneName());
        pw.println(" getPhoneType()=" + getPhoneType());
        pw.println(" getVoiceMessageCount()=" + getVoiceMessageCount());
        pw.println(" getActiveApnTypes()=" + getActiveApnTypes());
        pw.println(" isDataConnectivityPossible()=" + isDataConnectivityPossible());
        pw.println(" needsOtaServiceProvisioning=" + needsOtaServiceProvisioning());
    }
}