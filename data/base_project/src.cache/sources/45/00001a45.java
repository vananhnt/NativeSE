package com.android.internal.telephony;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Registrant;
import android.os.RegistrantList;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.CommandsInterface;

/* loaded from: BaseCommands.class */
public abstract class BaseCommands implements CommandsInterface {
    protected Context mContext;
    protected Registrant mUnsolOemHookRawRegistrant;
    protected Registrant mGsmSmsRegistrant;
    protected Registrant mCdmaSmsRegistrant;
    protected Registrant mNITZTimeRegistrant;
    protected Registrant mSignalStrengthRegistrant;
    protected Registrant mUSSDRegistrant;
    protected Registrant mSmsOnSimRegistrant;
    protected Registrant mSmsStatusRegistrant;
    protected Registrant mSsnRegistrant;
    protected Registrant mCatSessionEndRegistrant;
    protected Registrant mCatProCmdRegistrant;
    protected Registrant mCatEventRegistrant;
    protected Registrant mCatCallSetUpRegistrant;
    protected Registrant mIccSmsFullRegistrant;
    protected Registrant mEmergencyCallbackModeRegistrant;
    protected Registrant mRingRegistrant;
    protected Registrant mRestrictedStateRegistrant;
    protected Registrant mGsmBroadcastSmsRegistrant;
    protected int mPreferredNetworkType;
    protected int mCdmaSubscription;
    protected int mPhoneType;
    protected CommandsInterface.RadioState mState = CommandsInterface.RadioState.RADIO_UNAVAILABLE;
    protected Object mStateMonitor = new Object();
    protected RegistrantList mRadioStateChangedRegistrants = new RegistrantList();
    protected RegistrantList mOnRegistrants = new RegistrantList();
    protected RegistrantList mAvailRegistrants = new RegistrantList();
    protected RegistrantList mOffOrNotAvailRegistrants = new RegistrantList();
    protected RegistrantList mNotAvailRegistrants = new RegistrantList();
    protected RegistrantList mCallStateRegistrants = new RegistrantList();
    protected RegistrantList mVoiceNetworkStateRegistrants = new RegistrantList();
    protected RegistrantList mDataNetworkStateRegistrants = new RegistrantList();
    protected RegistrantList mVoiceRadioTechChangedRegistrants = new RegistrantList();
    protected RegistrantList mImsNetworkStateChangedRegistrants = new RegistrantList();
    protected RegistrantList mIccStatusChangedRegistrants = new RegistrantList();
    protected RegistrantList mVoicePrivacyOnRegistrants = new RegistrantList();
    protected RegistrantList mVoicePrivacyOffRegistrants = new RegistrantList();
    protected RegistrantList mOtaProvisionRegistrants = new RegistrantList();
    protected RegistrantList mCallWaitingInfoRegistrants = new RegistrantList();
    protected RegistrantList mDisplayInfoRegistrants = new RegistrantList();
    protected RegistrantList mSignalInfoRegistrants = new RegistrantList();
    protected RegistrantList mNumberInfoRegistrants = new RegistrantList();
    protected RegistrantList mRedirNumInfoRegistrants = new RegistrantList();
    protected RegistrantList mLineControlInfoRegistrants = new RegistrantList();
    protected RegistrantList mT53ClirInfoRegistrants = new RegistrantList();
    protected RegistrantList mT53AudCntrlInfoRegistrants = new RegistrantList();
    protected RegistrantList mRingbackToneRegistrants = new RegistrantList();
    protected RegistrantList mResendIncallMuteRegistrants = new RegistrantList();
    protected RegistrantList mCdmaSubscriptionChangedRegistrants = new RegistrantList();
    protected RegistrantList mCdmaPrlChangedRegistrants = new RegistrantList();
    protected RegistrantList mExitEmergencyCallbackModeRegistrants = new RegistrantList();
    protected RegistrantList mRilConnectedRegistrants = new RegistrantList();
    protected RegistrantList mIccRefreshRegistrants = new RegistrantList();
    protected RegistrantList mRilCellInfoListRegistrants = new RegistrantList();
    protected int mRilVersion = -1;

    public BaseCommands(Context context) {
        this.mContext = context;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public CommandsInterface.RadioState getRadioState() {
        return this.mState;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRadioStateChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        synchronized (this.mStateMonitor) {
            this.mRadioStateChangedRegistrants.add(r);
            r.notifyRegistrant();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRadioStateChanged(Handler h) {
        synchronized (this.mStateMonitor) {
            this.mRadioStateChangedRegistrants.remove(h);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForImsNetworkStateChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mImsNetworkStateChangedRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForImsNetworkStateChanged(Handler h) {
        this.mImsNetworkStateChangedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForOn(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        synchronized (this.mStateMonitor) {
            this.mOnRegistrants.add(r);
            if (this.mState.isOn()) {
                r.notifyRegistrant(new AsyncResult(null, null, null));
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForOn(Handler h) {
        synchronized (this.mStateMonitor) {
            this.mOnRegistrants.remove(h);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForAvailable(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        synchronized (this.mStateMonitor) {
            this.mAvailRegistrants.add(r);
            if (this.mState.isAvailable()) {
                r.notifyRegistrant(new AsyncResult(null, null, null));
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForAvailable(Handler h) {
        synchronized (this.mStateMonitor) {
            this.mAvailRegistrants.remove(h);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNotAvailable(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        synchronized (this.mStateMonitor) {
            this.mNotAvailRegistrants.add(r);
            if (!this.mState.isAvailable()) {
                r.notifyRegistrant(new AsyncResult(null, null, null));
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNotAvailable(Handler h) {
        synchronized (this.mStateMonitor) {
            this.mNotAvailRegistrants.remove(h);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForOffOrNotAvailable(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        synchronized (this.mStateMonitor) {
            this.mOffOrNotAvailRegistrants.add(r);
            if (this.mState == CommandsInterface.RadioState.RADIO_OFF || !this.mState.isAvailable()) {
                r.notifyRegistrant(new AsyncResult(null, null, null));
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForOffOrNotAvailable(Handler h) {
        synchronized (this.mStateMonitor) {
            this.mOffOrNotAvailRegistrants.remove(h);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCallStateChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mCallStateRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCallStateChanged(Handler h) {
        this.mCallStateRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForVoiceNetworkStateChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mVoiceNetworkStateRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForVoiceNetworkStateChanged(Handler h) {
        this.mVoiceNetworkStateRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForDataNetworkStateChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mDataNetworkStateRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForDataNetworkStateChanged(Handler h) {
        this.mDataNetworkStateRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForVoiceRadioTechChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mVoiceRadioTechChangedRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForVoiceRadioTechChanged(Handler h) {
        this.mVoiceRadioTechChangedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForIccStatusChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mIccStatusChangedRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForIccStatusChanged(Handler h) {
        this.mIccStatusChangedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewGsmSms(Handler h, int what, Object obj) {
        this.mGsmSmsRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewGsmSms(Handler h) {
        this.mGsmSmsRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewCdmaSms(Handler h, int what, Object obj) {
        this.mCdmaSmsRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewCdmaSms(Handler h) {
        this.mCdmaSmsRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNewGsmBroadcastSms(Handler h, int what, Object obj) {
        this.mGsmBroadcastSmsRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNewGsmBroadcastSms(Handler h) {
        this.mGsmBroadcastSmsRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSmsOnSim(Handler h, int what, Object obj) {
        this.mSmsOnSimRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSmsOnSim(Handler h) {
        this.mSmsOnSimRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSmsStatus(Handler h, int what, Object obj) {
        this.mSmsStatusRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSmsStatus(Handler h) {
        this.mSmsStatusRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSignalStrengthUpdate(Handler h, int what, Object obj) {
        this.mSignalStrengthRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSignalStrengthUpdate(Handler h) {
        this.mSignalStrengthRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnNITZTime(Handler h, int what, Object obj) {
        this.mNITZTimeRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnNITZTime(Handler h) {
        this.mNITZTimeRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnUSSD(Handler h, int what, Object obj) {
        this.mUSSDRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnUSSD(Handler h) {
        this.mUSSDRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnSuppServiceNotification(Handler h, int what, Object obj) {
        this.mSsnRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnSuppServiceNotification(Handler h) {
        this.mSsnRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatSessionEnd(Handler h, int what, Object obj) {
        this.mCatSessionEndRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatSessionEnd(Handler h) {
        this.mCatSessionEndRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatProactiveCmd(Handler h, int what, Object obj) {
        this.mCatProCmdRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatProactiveCmd(Handler h) {
        this.mCatProCmdRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatEvent(Handler h, int what, Object obj) {
        this.mCatEventRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatEvent(Handler h) {
        this.mCatEventRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCatCallSetUp(Handler h, int what, Object obj) {
        this.mCatCallSetUpRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCatCallSetUp(Handler h) {
        this.mCatCallSetUpRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnIccSmsFull(Handler h, int what, Object obj) {
        this.mIccSmsFullRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnIccSmsFull(Handler h) {
        this.mIccSmsFullRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForIccRefresh(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mIccRefreshRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnIccRefresh(Handler h, int what, Object obj) {
        registerForIccRefresh(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setEmergencyCallbackMode(Handler h, int what, Object obj) {
        this.mEmergencyCallbackModeRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForIccRefresh(Handler h) {
        this.mIccRefreshRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unsetOnIccRefresh(Handler h) {
        unregisterForIccRefresh(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnCallRing(Handler h, int what, Object obj) {
        this.mRingRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnCallRing(Handler h) {
        this.mRingRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForInCallVoicePrivacyOn(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mVoicePrivacyOnRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForInCallVoicePrivacyOn(Handler h) {
        this.mVoicePrivacyOnRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForInCallVoicePrivacyOff(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mVoicePrivacyOffRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForInCallVoicePrivacyOff(Handler h) {
        this.mVoicePrivacyOffRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setOnRestrictedStateChanged(Handler h, int what, Object obj) {
        this.mRestrictedStateRegistrant = new Registrant(h, what, obj);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unSetOnRestrictedStateChanged(Handler h) {
        this.mRestrictedStateRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForDisplayInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mDisplayInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForDisplayInfo(Handler h) {
        this.mDisplayInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCallWaitingInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mCallWaitingInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCallWaitingInfo(Handler h) {
        this.mCallWaitingInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForSignalInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mSignalInfoRegistrants.add(r);
    }

    public void setOnUnsolOemHookRaw(Handler h, int what, Object obj) {
        this.mUnsolOemHookRawRegistrant = new Registrant(h, what, obj);
    }

    public void unSetOnUnsolOemHookRaw(Handler h) {
        this.mUnsolOemHookRawRegistrant.clear();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForSignalInfo(Handler h) {
        this.mSignalInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaOtaProvision(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mOtaProvisionRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaOtaProvision(Handler h) {
        this.mOtaProvisionRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForNumberInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mNumberInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForNumberInfo(Handler h) {
        this.mNumberInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRedirectedNumberInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mRedirNumInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRedirectedNumberInfo(Handler h) {
        this.mRedirNumInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForLineControlInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mLineControlInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForLineControlInfo(Handler h) {
        this.mLineControlInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerFoT53ClirlInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mT53ClirInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForT53ClirInfo(Handler h) {
        this.mT53ClirInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForT53AudioControlInfo(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mT53AudCntrlInfoRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForT53AudioControlInfo(Handler h) {
        this.mT53AudCntrlInfoRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRingbackTone(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mRingbackToneRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRingbackTone(Handler h) {
        this.mRingbackToneRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForResendIncallMute(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mResendIncallMuteRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForResendIncallMute(Handler h) {
        this.mResendIncallMuteRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaSubscriptionChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mCdmaSubscriptionChangedRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaSubscriptionChanged(Handler h) {
        this.mCdmaSubscriptionChangedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCdmaPrlChanged(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mCdmaPrlChangedRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCdmaPrlChanged(Handler h) {
        this.mCdmaPrlChangedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForExitEmergencyCallbackMode(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mExitEmergencyCallbackModeRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForExitEmergencyCallbackMode(Handler h) {
        this.mExitEmergencyCallbackModeRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForRilConnected(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mRilConnectedRegistrants.add(r);
        if (this.mRilVersion != -1) {
            r.notifyRegistrant(new AsyncResult(null, new Integer(this.mRilVersion), null));
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForRilConnected(Handler h) {
        this.mRilConnectedRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCurrentPreferredNetworkType() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRadioState(CommandsInterface.RadioState newState) {
        synchronized (this.mStateMonitor) {
            CommandsInterface.RadioState oldState = this.mState;
            this.mState = newState;
            if (oldState == this.mState) {
                return;
            }
            this.mRadioStateChangedRegistrants.notifyRegistrants();
            if (this.mState.isAvailable() && !oldState.isAvailable()) {
                this.mAvailRegistrants.notifyRegistrants();
                onRadioAvailable();
            }
            if (!this.mState.isAvailable() && oldState.isAvailable()) {
                this.mNotAvailRegistrants.notifyRegistrants();
            }
            if (this.mState.isOn() && !oldState.isOn()) {
                this.mOnRegistrants.notifyRegistrants();
            }
            if ((!this.mState.isOn() || !this.mState.isAvailable()) && oldState.isOn() && oldState.isAvailable()) {
                this.mOffOrNotAvailRegistrants.notifyRegistrants();
            }
        }
    }

    protected void onRadioAvailable() {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public int getLteOnCdmaMode() {
        return TelephonyManager.getLteOnCdmaModeStatic();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void registerForCellInfoList(Handler h, int what, Object obj) {
        Registrant r = new Registrant(h, what, obj);
        this.mRilCellInfoListRegistrants.add(r);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void unregisterForCellInfoList(Handler h) {
        this.mRilCellInfoListRegistrants.remove(h);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void testingEmergencyCall() {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public int getRilVersion() {
        return this.mRilVersion;
    }
}