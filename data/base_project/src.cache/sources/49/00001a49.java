package com.android.internal.telephony;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.Registrant;
import android.os.RegistrantList;
import android.telephony.Rlog;
import com.android.internal.R;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.sip.SipPhone;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: CallManager.class */
public final class CallManager {
    private static final String LOG_TAG = "CallManager";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private static final int EVENT_DISCONNECT = 100;
    private static final int EVENT_PRECISE_CALL_STATE_CHANGED = 101;
    private static final int EVENT_NEW_RINGING_CONNECTION = 102;
    private static final int EVENT_UNKNOWN_CONNECTION = 103;
    private static final int EVENT_INCOMING_RING = 104;
    private static final int EVENT_RINGBACK_TONE = 105;
    private static final int EVENT_IN_CALL_VOICE_PRIVACY_ON = 106;
    private static final int EVENT_IN_CALL_VOICE_PRIVACY_OFF = 107;
    private static final int EVENT_CALL_WAITING = 108;
    private static final int EVENT_DISPLAY_INFO = 109;
    private static final int EVENT_SIGNAL_INFO = 110;
    private static final int EVENT_CDMA_OTA_STATUS_CHANGE = 111;
    private static final int EVENT_RESEND_INCALL_MUTE = 112;
    private static final int EVENT_MMI_INITIATE = 113;
    private static final int EVENT_MMI_COMPLETE = 114;
    private static final int EVENT_ECM_TIMER_RESET = 115;
    private static final int EVENT_SUBSCRIPTION_INFO_READY = 116;
    private static final int EVENT_SUPP_SERVICE_FAILED = 117;
    private static final int EVENT_SERVICE_STATE_CHANGED = 118;
    private static final int EVENT_POST_DIAL_CHARACTER = 119;
    private static final CallManager INSTANCE = new CallManager();
    private final ArrayList<Connection> mEmptyConnections = new ArrayList<>();
    private boolean mSpeedUpAudioForMtCall = false;
    protected final RegistrantList mPreciseCallStateRegistrants = new RegistrantList();
    protected final RegistrantList mNewRingingConnectionRegistrants = new RegistrantList();
    protected final RegistrantList mIncomingRingRegistrants = new RegistrantList();
    protected final RegistrantList mDisconnectRegistrants = new RegistrantList();
    protected final RegistrantList mMmiRegistrants = new RegistrantList();
    protected final RegistrantList mUnknownConnectionRegistrants = new RegistrantList();
    protected final RegistrantList mRingbackToneRegistrants = new RegistrantList();
    protected final RegistrantList mInCallVoicePrivacyOnRegistrants = new RegistrantList();
    protected final RegistrantList mInCallVoicePrivacyOffRegistrants = new RegistrantList();
    protected final RegistrantList mCallWaitingRegistrants = new RegistrantList();
    protected final RegistrantList mDisplayInfoRegistrants = new RegistrantList();
    protected final RegistrantList mSignalInfoRegistrants = new RegistrantList();
    protected final RegistrantList mCdmaOtaStatusChangeRegistrants = new RegistrantList();
    protected final RegistrantList mResendIncallMuteRegistrants = new RegistrantList();
    protected final RegistrantList mMmiInitiateRegistrants = new RegistrantList();
    protected final RegistrantList mMmiCompleteRegistrants = new RegistrantList();
    protected final RegistrantList mEcmTimerResetRegistrants = new RegistrantList();
    protected final RegistrantList mSubscriptionInfoReadyRegistrants = new RegistrantList();
    protected final RegistrantList mSuppServiceFailedRegistrants = new RegistrantList();
    protected final RegistrantList mServiceStateChangedRegistrants = new RegistrantList();
    protected final RegistrantList mPostDialCharacterRegistrants = new RegistrantList();
    private Handler mHandler = new Handler() { // from class: com.android.internal.telephony.CallManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    CallManager.this.mDisconnectRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 101:
                    CallManager.this.mPreciseCallStateRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 102:
                    if (CallManager.this.getActiveFgCallState().isDialing() || CallManager.this.hasMoreThanOneRingingCall()) {
                        Connection c = (Connection) ((AsyncResult) msg.obj).result;
                        try {
                            Rlog.d(CallManager.LOG_TAG, "silently drop incoming call: " + c.getCall());
                            c.getCall().hangup();
                            return;
                        } catch (CallStateException e) {
                            Rlog.w(CallManager.LOG_TAG, "new ringing connection", e);
                            return;
                        }
                    }
                    CallManager.this.mNewRingingConnectionRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 103:
                    CallManager.this.mUnknownConnectionRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 104:
                    if (!CallManager.this.hasActiveFgCall()) {
                        CallManager.this.mIncomingRingRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                        return;
                    }
                    return;
                case 105:
                    CallManager.this.mRingbackToneRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 106:
                    CallManager.this.mInCallVoicePrivacyOnRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 107:
                    CallManager.this.mInCallVoicePrivacyOffRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 108:
                    CallManager.this.mCallWaitingRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 109:
                    CallManager.this.mDisplayInfoRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 110:
                    CallManager.this.mSignalInfoRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 111:
                    CallManager.this.mCdmaOtaStatusChangeRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 112:
                    CallManager.this.mResendIncallMuteRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 113:
                    CallManager.this.mMmiInitiateRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 114:
                    CallManager.this.mMmiCompleteRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 115:
                    CallManager.this.mEcmTimerResetRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 116:
                    CallManager.this.mSubscriptionInfoReadyRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 117:
                    CallManager.this.mSuppServiceFailedRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 118:
                    CallManager.this.mServiceStateChangedRegistrants.notifyRegistrants((AsyncResult) msg.obj);
                    return;
                case 119:
                    for (int i = 0; i < CallManager.this.mPostDialCharacterRegistrants.size(); i++) {
                        Message notifyMsg = ((Registrant) CallManager.this.mPostDialCharacterRegistrants.get(i)).messageForRegistrant();
                        notifyMsg.obj = msg.obj;
                        notifyMsg.arg1 = msg.arg1;
                        notifyMsg.sendToTarget();
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private final ArrayList<Phone> mPhones = new ArrayList<>();
    private final ArrayList<Call> mRingingCalls = new ArrayList<>();
    private final ArrayList<Call> mBackgroundCalls = new ArrayList<>();
    private final ArrayList<Call> mForegroundCalls = new ArrayList<>();
    private Phone mDefaultPhone = null;

    private CallManager() {
    }

    public static CallManager getInstance() {
        return INSTANCE;
    }

    private static Phone getPhoneBase(Phone phone) {
        if (phone instanceof PhoneProxy) {
            return phone.getForegroundCall().getPhone();
        }
        return phone;
    }

    public static boolean isSamePhone(Phone p1, Phone p2) {
        return getPhoneBase(p1) == getPhoneBase(p2);
    }

    public List<Phone> getAllPhones() {
        return Collections.unmodifiableList(this.mPhones);
    }

    public PhoneConstants.State getState() {
        PhoneConstants.State s = PhoneConstants.State.IDLE;
        Iterator i$ = this.mPhones.iterator();
        while (i$.hasNext()) {
            Phone phone = i$.next();
            if (phone.getState() == PhoneConstants.State.RINGING) {
                s = PhoneConstants.State.RINGING;
            } else if (phone.getState() == PhoneConstants.State.OFFHOOK && s == PhoneConstants.State.IDLE) {
                s = PhoneConstants.State.OFFHOOK;
            }
        }
        return s;
    }

    public int getServiceState() {
        int resultState = 1;
        Iterator i$ = this.mPhones.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            Phone phone = i$.next();
            int serviceState = phone.getServiceState().getState();
            if (serviceState == 0) {
                resultState = serviceState;
                break;
            } else if (serviceState == 1) {
                if (resultState == 2 || resultState == 3) {
                    resultState = serviceState;
                }
            } else if (serviceState == 2 && resultState == 3) {
                resultState = serviceState;
            }
        }
        return resultState;
    }

    public boolean registerPhone(Phone phone) {
        Phone basePhone = getPhoneBase(phone);
        if (basePhone != null && !this.mPhones.contains(basePhone)) {
            Rlog.d(LOG_TAG, "registerPhone(" + phone.getPhoneName() + Separators.SP + phone + Separators.RPAREN);
            if (this.mPhones.isEmpty()) {
                this.mDefaultPhone = basePhone;
            }
            this.mPhones.add(basePhone);
            this.mRingingCalls.add(basePhone.getRingingCall());
            this.mBackgroundCalls.add(basePhone.getBackgroundCall());
            this.mForegroundCalls.add(basePhone.getForegroundCall());
            registerForPhoneStates(basePhone);
            return true;
        }
        return false;
    }

    public void unregisterPhone(Phone phone) {
        Phone basePhone = getPhoneBase(phone);
        if (basePhone != null && this.mPhones.contains(basePhone)) {
            Rlog.d(LOG_TAG, "unregisterPhone(" + phone.getPhoneName() + Separators.SP + phone + Separators.RPAREN);
            this.mPhones.remove(basePhone);
            this.mRingingCalls.remove(basePhone.getRingingCall());
            this.mBackgroundCalls.remove(basePhone.getBackgroundCall());
            this.mForegroundCalls.remove(basePhone.getForegroundCall());
            unregisterForPhoneStates(basePhone);
            if (basePhone == this.mDefaultPhone) {
                if (this.mPhones.isEmpty()) {
                    this.mDefaultPhone = null;
                } else {
                    this.mDefaultPhone = this.mPhones.get(0);
                }
            }
        }
    }

    public Phone getDefaultPhone() {
        return this.mDefaultPhone;
    }

    public Phone getFgPhone() {
        return getActiveFgCall().getPhone();
    }

    public Phone getBgPhone() {
        return getFirstActiveBgCall().getPhone();
    }

    public Phone getRingingPhone() {
        return getFirstActiveRingingCall().getPhone();
    }

    public void setAudioMode() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (getState()) {
            case RINGING:
                int curAudioMode = audioManager.getMode();
                if (curAudioMode != 1) {
                    if (audioManager.getStreamVolume(2) > 0) {
                        audioManager.requestAudioFocusForCall(2, 2);
                    }
                    if (!this.mSpeedUpAudioForMtCall) {
                        audioManager.setMode(1);
                    }
                }
                if (this.mSpeedUpAudioForMtCall && curAudioMode != 2) {
                    audioManager.setMode(2);
                    return;
                }
                return;
            case OFFHOOK:
                Phone offhookPhone = getFgPhone();
                if (getActiveFgCallState() == Call.State.IDLE) {
                    offhookPhone = getBgPhone();
                }
                int newAudioMode = 2;
                if (offhookPhone instanceof SipPhone) {
                    newAudioMode = 3;
                }
                if (audioManager.getMode() != newAudioMode || this.mSpeedUpAudioForMtCall) {
                    audioManager.requestAudioFocusForCall(0, 2);
                    audioManager.setMode(newAudioMode);
                }
                this.mSpeedUpAudioForMtCall = false;
                return;
            case IDLE:
                if (audioManager.getMode() != 0) {
                    audioManager.setMode(0);
                    audioManager.abandonAudioFocusForCall();
                }
                this.mSpeedUpAudioForMtCall = false;
                return;
            default:
                return;
        }
    }

    private Context getContext() {
        Phone defaultPhone = getDefaultPhone();
        if (defaultPhone == null) {
            return null;
        }
        return defaultPhone.getContext();
    }

    private void registerForPhoneStates(Phone phone) {
        phone.registerForPreciseCallStateChanged(this.mHandler, 101, null);
        phone.registerForDisconnect(this.mHandler, 100, null);
        phone.registerForNewRingingConnection(this.mHandler, 102, null);
        phone.registerForUnknownConnection(this.mHandler, 103, null);
        phone.registerForIncomingRing(this.mHandler, 104, null);
        phone.registerForRingbackTone(this.mHandler, 105, null);
        phone.registerForInCallVoicePrivacyOn(this.mHandler, 106, null);
        phone.registerForInCallVoicePrivacyOff(this.mHandler, 107, null);
        phone.registerForDisplayInfo(this.mHandler, 109, null);
        phone.registerForSignalInfo(this.mHandler, 110, null);
        phone.registerForResendIncallMute(this.mHandler, 112, null);
        phone.registerForMmiInitiate(this.mHandler, 113, null);
        phone.registerForMmiComplete(this.mHandler, 114, null);
        phone.registerForSuppServiceFailed(this.mHandler, 117, null);
        phone.registerForServiceStateChanged(this.mHandler, 118, null);
        if (phone.getPhoneType() == 1 || phone.getPhoneType() == 2) {
            phone.setOnPostDialCharacter(this.mHandler, 119, null);
        }
        if (phone.getPhoneType() == 2) {
            phone.registerForCdmaOtaStatusChange(this.mHandler, 111, null);
            phone.registerForSubscriptionInfoReady(this.mHandler, 116, null);
            phone.registerForCallWaiting(this.mHandler, 108, null);
            phone.registerForEcmTimerReset(this.mHandler, 115, null);
        }
    }

    private void unregisterForPhoneStates(Phone phone) {
        phone.unregisterForPreciseCallStateChanged(this.mHandler);
        phone.unregisterForDisconnect(this.mHandler);
        phone.unregisterForNewRingingConnection(this.mHandler);
        phone.unregisterForUnknownConnection(this.mHandler);
        phone.unregisterForIncomingRing(this.mHandler);
        phone.unregisterForRingbackTone(this.mHandler);
        phone.unregisterForInCallVoicePrivacyOn(this.mHandler);
        phone.unregisterForInCallVoicePrivacyOff(this.mHandler);
        phone.unregisterForDisplayInfo(this.mHandler);
        phone.unregisterForSignalInfo(this.mHandler);
        phone.unregisterForResendIncallMute(this.mHandler);
        phone.unregisterForMmiInitiate(this.mHandler);
        phone.unregisterForMmiComplete(this.mHandler);
        phone.unregisterForSuppServiceFailed(this.mHandler);
        phone.unregisterForServiceStateChanged(this.mHandler);
        if (phone.getPhoneType() == 1 || phone.getPhoneType() == 2) {
            phone.setOnPostDialCharacter(null, 119, null);
        }
        if (phone.getPhoneType() == 2) {
            phone.unregisterForCdmaOtaStatusChange(this.mHandler);
            phone.unregisterForSubscriptionInfoReady(this.mHandler);
            phone.unregisterForCallWaiting(this.mHandler);
            phone.unregisterForEcmTimerReset(this.mHandler);
        }
    }

    public void acceptCall(Call ringingCall) throws CallStateException {
        Phone ringingPhone = ringingCall.getPhone();
        if (hasActiveFgCall()) {
            Phone activePhone = getActiveFgCall().getPhone();
            boolean hasBgCall = !activePhone.getBackgroundCall().isIdle();
            boolean sameChannel = activePhone == ringingPhone;
            if (sameChannel && hasBgCall) {
                getActiveFgCall().hangup();
            } else if (!sameChannel && !hasBgCall) {
                activePhone.switchHoldingAndActive();
            } else if (!sameChannel && hasBgCall) {
                getActiveFgCall().hangup();
            }
        }
        Context context = getContext();
        if (context == null) {
            Rlog.d(LOG_TAG, "Speedup Audio Path enhancement: Context is null");
        } else if (context.getResources().getBoolean(R.bool.config_speed_up_audio_on_mt_calls)) {
            Rlog.d(LOG_TAG, "Speedup Audio Path enhancement");
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currMode = audioManager.getMode();
            if (currMode != 2 && !(ringingPhone instanceof SipPhone)) {
                Rlog.d(LOG_TAG, "setAudioMode Setting audio mode from " + currMode + " to 2");
                audioManager.setMode(2);
                this.mSpeedUpAudioForMtCall = true;
            }
        }
        ringingPhone.acceptCall();
    }

    public void rejectCall(Call ringingCall) throws CallStateException {
        Phone ringingPhone = ringingCall.getPhone();
        ringingPhone.rejectCall();
    }

    public void switchHoldingAndActive(Call heldCall) throws CallStateException {
        Phone activePhone = null;
        Phone heldPhone = null;
        if (hasActiveFgCall()) {
            activePhone = getActiveFgCall().getPhone();
        }
        if (heldCall != null) {
            heldPhone = heldCall.getPhone();
        }
        if (activePhone != null) {
            activePhone.switchHoldingAndActive();
        }
        if (heldPhone != null && heldPhone != activePhone) {
            heldPhone.switchHoldingAndActive();
        }
    }

    public void hangupForegroundResumeBackground(Call heldCall) throws CallStateException {
        if (hasActiveFgCall()) {
            Phone foregroundPhone = getFgPhone();
            if (heldCall != null) {
                Phone backgroundPhone = heldCall.getPhone();
                if (foregroundPhone == backgroundPhone) {
                    getActiveFgCall().hangup();
                    return;
                }
                getActiveFgCall().hangup();
                switchHoldingAndActive(heldCall);
            }
        }
    }

    public boolean canConference(Call heldCall) {
        Phone activePhone = null;
        Phone heldPhone = null;
        if (hasActiveFgCall()) {
            activePhone = getActiveFgCall().getPhone();
        }
        if (heldCall != null) {
            heldPhone = heldCall.getPhone();
        }
        return heldPhone.getClass().equals(activePhone.getClass());
    }

    public void conference(Call heldCall) throws CallStateException {
        SipPhone fgPhone = getFgPhone();
        if (fgPhone instanceof SipPhone) {
            fgPhone.conference(heldCall);
        } else if (canConference(heldCall)) {
            fgPhone.conference();
        } else {
            throw new CallStateException("Can't conference foreground and selected background call");
        }
    }

    public Connection dial(Phone phone, String dialString) throws CallStateException {
        Phone basePhone = getPhoneBase(phone);
        if (!canDial(phone)) {
            throw new CallStateException("cannot dial in current state");
        }
        if (hasActiveFgCall()) {
            Phone activePhone = getActiveFgCall().getPhone();
            boolean hasBgCall = !activePhone.getBackgroundCall().isIdle();
            Rlog.d(LOG_TAG, "hasBgCall: " + hasBgCall + " sameChannel:" + (activePhone == basePhone));
            if (activePhone != basePhone) {
                if (hasBgCall) {
                    Rlog.d(LOG_TAG, "Hangup");
                    getActiveFgCall().hangup();
                } else {
                    Rlog.d(LOG_TAG, "Switch");
                    activePhone.switchHoldingAndActive();
                }
            }
        }
        Connection result = basePhone.dial(dialString);
        return result;
    }

    public Connection dial(Phone phone, String dialString, UUSInfo uusInfo) throws CallStateException {
        return phone.dial(dialString, uusInfo);
    }

    public void clearDisconnected() {
        Iterator i$ = this.mPhones.iterator();
        while (i$.hasNext()) {
            Phone phone = i$.next();
            phone.clearDisconnected();
        }
    }

    private boolean canDial(Phone phone) {
        int serviceState = phone.getServiceState().getState();
        boolean hasRingingCall = hasActiveRingingCall();
        Call.State fgCallState = getActiveFgCallState();
        boolean result = (serviceState == 3 || hasRingingCall || (fgCallState != Call.State.ACTIVE && fgCallState != Call.State.IDLE && fgCallState != Call.State.DISCONNECTED)) ? false : true;
        if (!result) {
            Rlog.d(LOG_TAG, "canDial serviceState=" + serviceState + " hasRingingCall=" + hasRingingCall + " fgCallState=" + fgCallState);
        }
        return result;
    }

    public boolean canTransfer(Call heldCall) {
        Phone activePhone = null;
        Phone heldPhone = null;
        if (hasActiveFgCall()) {
            activePhone = getActiveFgCall().getPhone();
        }
        if (heldCall != null) {
            heldPhone = heldCall.getPhone();
        }
        return heldPhone == activePhone && activePhone.canTransfer();
    }

    public void explicitCallTransfer(Call heldCall) throws CallStateException {
        if (canTransfer(heldCall)) {
            heldCall.getPhone().explicitCallTransfer();
        }
    }

    public List<? extends MmiCode> getPendingMmiCodes(Phone phone) {
        Rlog.e(LOG_TAG, "getPendingMmiCodes not implemented");
        return null;
    }

    public boolean sendUssdResponse(Phone phone, String ussdMessge) {
        Rlog.e(LOG_TAG, "sendUssdResponse not implemented");
        return false;
    }

    public void setMute(boolean muted) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().setMute(muted);
        }
    }

    public boolean getMute() {
        if (hasActiveFgCall()) {
            return getActiveFgCall().getPhone().getMute();
        }
        if (hasActiveBgCall()) {
            return getFirstActiveBgCall().getPhone().getMute();
        }
        return false;
    }

    public void setEchoSuppressionEnabled(boolean enabled) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().setEchoSuppressionEnabled(enabled);
        }
    }

    public boolean sendDtmf(char c) {
        boolean result = false;
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().sendDtmf(c);
            result = true;
        }
        return result;
    }

    public boolean startDtmf(char c) {
        boolean result = false;
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().startDtmf(c);
            result = true;
        }
        return result;
    }

    public void stopDtmf() {
        if (hasActiveFgCall()) {
            getFgPhone().stopDtmf();
        }
    }

    public boolean sendBurstDtmf(String dtmfString, int on, int off, Message onComplete) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().sendBurstDtmf(dtmfString, on, off, onComplete);
            return true;
        }
        return false;
    }

    public void registerForDisconnect(Handler h, int what, Object obj) {
        this.mDisconnectRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForDisconnect(Handler h) {
        this.mDisconnectRegistrants.remove(h);
    }

    public void registerForPreciseCallStateChanged(Handler h, int what, Object obj) {
        this.mPreciseCallStateRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForPreciseCallStateChanged(Handler h) {
        this.mPreciseCallStateRegistrants.remove(h);
    }

    public void registerForUnknownConnection(Handler h, int what, Object obj) {
        this.mUnknownConnectionRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForUnknownConnection(Handler h) {
        this.mUnknownConnectionRegistrants.remove(h);
    }

    public void registerForNewRingingConnection(Handler h, int what, Object obj) {
        this.mNewRingingConnectionRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForNewRingingConnection(Handler h) {
        this.mNewRingingConnectionRegistrants.remove(h);
    }

    public void registerForIncomingRing(Handler h, int what, Object obj) {
        this.mIncomingRingRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForIncomingRing(Handler h) {
        this.mIncomingRingRegistrants.remove(h);
    }

    public void registerForRingbackTone(Handler h, int what, Object obj) {
        this.mRingbackToneRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForRingbackTone(Handler h) {
        this.mRingbackToneRegistrants.remove(h);
    }

    public void registerForResendIncallMute(Handler h, int what, Object obj) {
        this.mResendIncallMuteRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForResendIncallMute(Handler h) {
        this.mResendIncallMuteRegistrants.remove(h);
    }

    public void registerForMmiInitiate(Handler h, int what, Object obj) {
        this.mMmiInitiateRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForMmiInitiate(Handler h) {
        this.mMmiInitiateRegistrants.remove(h);
    }

    public void registerForMmiComplete(Handler h, int what, Object obj) {
        this.mMmiCompleteRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForMmiComplete(Handler h) {
        this.mMmiCompleteRegistrants.remove(h);
    }

    public void registerForEcmTimerReset(Handler h, int what, Object obj) {
        this.mEcmTimerResetRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForEcmTimerReset(Handler h) {
        this.mEcmTimerResetRegistrants.remove(h);
    }

    public void registerForServiceStateChanged(Handler h, int what, Object obj) {
        this.mServiceStateChangedRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForServiceStateChanged(Handler h) {
        this.mServiceStateChangedRegistrants.remove(h);
    }

    public void registerForSuppServiceFailed(Handler h, int what, Object obj) {
        this.mSuppServiceFailedRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForSuppServiceFailed(Handler h) {
        this.mSuppServiceFailedRegistrants.remove(h);
    }

    public void registerForInCallVoicePrivacyOn(Handler h, int what, Object obj) {
        this.mInCallVoicePrivacyOnRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForInCallVoicePrivacyOn(Handler h) {
        this.mInCallVoicePrivacyOnRegistrants.remove(h);
    }

    public void registerForInCallVoicePrivacyOff(Handler h, int what, Object obj) {
        this.mInCallVoicePrivacyOffRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForInCallVoicePrivacyOff(Handler h) {
        this.mInCallVoicePrivacyOffRegistrants.remove(h);
    }

    public void registerForCallWaiting(Handler h, int what, Object obj) {
        this.mCallWaitingRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForCallWaiting(Handler h) {
        this.mCallWaitingRegistrants.remove(h);
    }

    public void registerForSignalInfo(Handler h, int what, Object obj) {
        this.mSignalInfoRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForSignalInfo(Handler h) {
        this.mSignalInfoRegistrants.remove(h);
    }

    public void registerForDisplayInfo(Handler h, int what, Object obj) {
        this.mDisplayInfoRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForDisplayInfo(Handler h) {
        this.mDisplayInfoRegistrants.remove(h);
    }

    public void registerForCdmaOtaStatusChange(Handler h, int what, Object obj) {
        this.mCdmaOtaStatusChangeRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForCdmaOtaStatusChange(Handler h) {
        this.mCdmaOtaStatusChangeRegistrants.remove(h);
    }

    public void registerForSubscriptionInfoReady(Handler h, int what, Object obj) {
        this.mSubscriptionInfoReadyRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForSubscriptionInfoReady(Handler h) {
        this.mSubscriptionInfoReadyRegistrants.remove(h);
    }

    public void registerForPostDialCharacter(Handler h, int what, Object obj) {
        this.mPostDialCharacterRegistrants.addUnique(h, what, obj);
    }

    public void unregisterForPostDialCharacter(Handler h) {
        this.mPostDialCharacterRegistrants.remove(h);
    }

    public List<Call> getRingingCalls() {
        return Collections.unmodifiableList(this.mRingingCalls);
    }

    public List<Call> getForegroundCalls() {
        return Collections.unmodifiableList(this.mForegroundCalls);
    }

    public List<Call> getBackgroundCalls() {
        return Collections.unmodifiableList(this.mBackgroundCalls);
    }

    public boolean hasActiveFgCall() {
        return getFirstActiveCall(this.mForegroundCalls) != null;
    }

    public boolean hasActiveBgCall() {
        return getFirstActiveCall(this.mBackgroundCalls) != null;
    }

    public boolean hasActiveRingingCall() {
        return getFirstActiveCall(this.mRingingCalls) != null;
    }

    public Call getActiveFgCall() {
        Call call = getFirstNonIdleCall(this.mForegroundCalls);
        if (call == null) {
            call = this.mDefaultPhone == null ? null : this.mDefaultPhone.getForegroundCall();
        }
        return call;
    }

    private Call getFirstNonIdleCall(List<Call> calls) {
        Call result = null;
        for (Call call : calls) {
            if (!call.isIdle()) {
                return call;
            }
            if (call.getState() != Call.State.IDLE && result == null) {
                result = call;
            }
        }
        return result;
    }

    public Call getFirstActiveBgCall() {
        Call call = getFirstNonIdleCall(this.mBackgroundCalls);
        if (call == null) {
            call = this.mDefaultPhone == null ? null : this.mDefaultPhone.getBackgroundCall();
        }
        return call;
    }

    public Call getFirstActiveRingingCall() {
        Call call = getFirstNonIdleCall(this.mRingingCalls);
        if (call == null) {
            call = this.mDefaultPhone == null ? null : this.mDefaultPhone.getRingingCall();
        }
        return call;
    }

    public Call.State getActiveFgCallState() {
        Call fgCall = getActiveFgCall();
        if (fgCall != null) {
            return fgCall.getState();
        }
        return Call.State.IDLE;
    }

    public List<Connection> getFgCallConnections() {
        Call fgCall = getActiveFgCall();
        if (fgCall != null) {
            return fgCall.getConnections();
        }
        return this.mEmptyConnections;
    }

    public List<Connection> getBgCallConnections() {
        Call bgCall = getFirstActiveBgCall();
        if (bgCall != null) {
            return bgCall.getConnections();
        }
        return this.mEmptyConnections;
    }

    public Connection getFgCallLatestConnection() {
        Call fgCall = getActiveFgCall();
        if (fgCall != null) {
            return fgCall.getLatestConnection();
        }
        return null;
    }

    public boolean hasDisconnectedFgCall() {
        return getFirstCallOfState(this.mForegroundCalls, Call.State.DISCONNECTED) != null;
    }

    public boolean hasDisconnectedBgCall() {
        return getFirstCallOfState(this.mBackgroundCalls, Call.State.DISCONNECTED) != null;
    }

    private Call getFirstActiveCall(ArrayList<Call> calls) {
        Iterator i$ = calls.iterator();
        while (i$.hasNext()) {
            Call call = i$.next();
            if (!call.isIdle()) {
                return call;
            }
        }
        return null;
    }

    private Call getFirstCallOfState(ArrayList<Call> calls, Call.State state) {
        Iterator i$ = calls.iterator();
        while (i$.hasNext()) {
            Call call = i$.next();
            if (call.getState() == state) {
                return call;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasMoreThanOneRingingCall() {
        int count = 0;
        Iterator i$ = this.mRingingCalls.iterator();
        while (i$.hasNext()) {
            Call call = i$.next();
            if (call.getState().isRinging()) {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("CallManager {");
        b.append("\nstate = " + getState());
        Call call = getActiveFgCall();
        b.append("\n- Foreground: " + getActiveFgCallState());
        b.append(" from " + call.getPhone());
        b.append("\n  Conn: ").append(getFgCallConnections());
        Call call2 = getFirstActiveBgCall();
        b.append("\n- Background: " + call2.getState());
        b.append(" from " + call2.getPhone());
        b.append("\n  Conn: ").append(getBgCallConnections());
        Call call3 = getFirstActiveRingingCall();
        b.append("\n- Ringing: " + call3.getState());
        b.append(" from " + call3.getPhone());
        for (Phone phone : getAllPhones()) {
            if (phone != null) {
                b.append("\nPhone: " + phone + ", name = " + phone.getPhoneName() + ", state = " + phone.getState());
                Call call4 = phone.getForegroundCall();
                b.append("\n- Foreground: ").append(call4);
                Call call5 = phone.getBackgroundCall();
                b.append(" Background: ").append(call5);
                Call call6 = phone.getRingingCall();
                b.append(" Ringing: ").append(call6);
            }
        }
        b.append("\n}");
        return b.toString();
    }
}