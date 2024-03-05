package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.PhoneConstants;

@Deprecated
/* loaded from: PhoneStateIntentReceiver.class */
public final class PhoneStateIntentReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "PhoneStatIntentReceiver";
    private static final boolean DBG = false;
    private static final int NOTIF_PHONE = 1;
    private static final int NOTIF_SERVICE = 2;
    private static final int NOTIF_SIGNAL = 4;
    PhoneConstants.State mPhoneState;
    ServiceState mServiceState;
    SignalStrength mSignalStrength;
    private Context mContext;
    private Handler mTarget;
    private IntentFilter mFilter;
    private int mWants;
    private int mPhoneStateEventWhat;
    private int mServiceStateEventWhat;
    private int mAsuEventWhat;

    public PhoneStateIntentReceiver() {
        this.mPhoneState = PhoneConstants.State.IDLE;
        this.mServiceState = new ServiceState();
        this.mSignalStrength = new SignalStrength();
        this.mFilter = new IntentFilter();
    }

    public PhoneStateIntentReceiver(Context context, Handler target) {
        this();
        setContext(context);
        setTarget(target);
    }

    public void setContext(Context c) {
        this.mContext = c;
    }

    public void setTarget(Handler h) {
        this.mTarget = h;
    }

    public PhoneConstants.State getPhoneState() {
        if ((this.mWants & 1) == 0) {
            throw new RuntimeException("client must call notifyPhoneCallState(int)");
        }
        return this.mPhoneState;
    }

    public ServiceState getServiceState() {
        if ((this.mWants & 2) == 0) {
            throw new RuntimeException("client must call notifyServiceState(int)");
        }
        return this.mServiceState;
    }

    public int getSignalStrengthLevelAsu() {
        if ((this.mWants & 4) == 0) {
            throw new RuntimeException("client must call notifySignalStrength(int)");
        }
        return this.mSignalStrength.getAsuLevel();
    }

    public int getSignalStrengthDbm() {
        if ((this.mWants & 4) == 0) {
            throw new RuntimeException("client must call notifySignalStrength(int)");
        }
        return this.mSignalStrength.getDbm();
    }

    public void notifyPhoneCallState(int eventWhat) {
        this.mWants |= 1;
        this.mPhoneStateEventWhat = eventWhat;
        this.mFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
    }

    public boolean getNotifyPhoneCallState() {
        return (this.mWants & 1) != 0;
    }

    public void notifyServiceState(int eventWhat) {
        this.mWants |= 2;
        this.mServiceStateEventWhat = eventWhat;
        this.mFilter.addAction(TelephonyIntents.ACTION_SERVICE_STATE_CHANGED);
    }

    public boolean getNotifyServiceState() {
        return (this.mWants & 2) != 0;
    }

    public void notifySignalStrength(int eventWhat) {
        this.mWants |= 4;
        this.mAsuEventWhat = eventWhat;
        this.mFilter.addAction(TelephonyIntents.ACTION_SIGNAL_STRENGTH_CHANGED);
    }

    public boolean getNotifySignalStrength() {
        return (this.mWants & 4) != 0;
    }

    public void registerIntent() {
        this.mContext.registerReceiver(this, this.mFilter);
    }

    public void unregisterIntent() {
        this.mContext.unregisterReceiver(this);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            if (TelephonyIntents.ACTION_SIGNAL_STRENGTH_CHANGED.equals(action)) {
                this.mSignalStrength = SignalStrength.newFromBundle(intent.getExtras());
                if (this.mTarget != null && getNotifySignalStrength()) {
                    Message message = Message.obtain(this.mTarget, this.mAsuEventWhat);
                    this.mTarget.sendMessage(message);
                }
            } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
                String phoneState = intent.getStringExtra("state");
                this.mPhoneState = (PhoneConstants.State) Enum.valueOf(PhoneConstants.State.class, phoneState);
                if (this.mTarget != null && getNotifyPhoneCallState()) {
                    Message message2 = Message.obtain(this.mTarget, this.mPhoneStateEventWhat);
                    this.mTarget.sendMessage(message2);
                }
            } else if (TelephonyIntents.ACTION_SERVICE_STATE_CHANGED.equals(action)) {
                this.mServiceState = ServiceState.newFromBundle(intent.getExtras());
                if (this.mTarget != null && getNotifyServiceState()) {
                    Message message3 = Message.obtain(this.mTarget, this.mServiceStateEventWhat);
                    this.mTarget.sendMessage(message3);
                }
            }
        } catch (Exception ex) {
            Rlog.e(LOG_TAG, "[PhoneStateIntentRecv] caught " + ex);
            ex.printStackTrace();
        }
    }
}