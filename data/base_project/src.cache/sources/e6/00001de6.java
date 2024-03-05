package com.android.server.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.TelephonyIntents;
import com.android.server.am.BatteryStatsService;

/* loaded from: DataConnectionStats.class */
public class DataConnectionStats extends BroadcastReceiver {
    private static final String TAG = "DataConnectionStats";
    private static final boolean DEBUG = false;
    private final Context mContext;
    private SignalStrength mSignalStrength;
    private ServiceState mServiceState;
    private IccCardConstants.State mSimState = IccCardConstants.State.READY;
    private int mDataState = 0;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.server.connectivity.DataConnectionStats.1
        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            DataConnectionStats.this.mSignalStrength = signalStrength;
        }

        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState state) {
            DataConnectionStats.this.mServiceState = state;
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int state, int networkType) {
            DataConnectionStats.this.mDataState = state;
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataActivity(int direction) {
            DataConnectionStats.this.notePhoneDataConnectionState();
        }
    };
    private final IBatteryStats mBatteryStats = BatteryStatsService.getService();

    public DataConnectionStats(Context context) {
        this.mContext = context;
    }

    public void startMonitoring() {
        TelephonyManager phone = (TelephonyManager) this.mContext.getSystemService("phone");
        phone.listen(this.mPhoneStateListener, 449);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.INET_CONDITION_ACTION);
        this.mContext.registerReceiver(this, filter);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(TelephonyIntents.ACTION_SIM_STATE_CHANGED)) {
            updateSimState(intent);
            notePhoneDataConnectionState();
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION) || action.equals(ConnectivityManager.INET_CONDITION_ACTION)) {
            notePhoneDataConnectionState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notePhoneDataConnectionState() {
        if (this.mServiceState == null) {
            return;
        }
        boolean simReadyOrUnknown = this.mSimState == IccCardConstants.State.READY || this.mSimState == IccCardConstants.State.UNKNOWN;
        boolean visible = (simReadyOrUnknown || isCdma()) && hasService() && this.mDataState == 2;
        int networkType = this.mServiceState.getDataNetworkType();
        try {
            this.mBatteryStats.notePhoneDataConnectionState(networkType, visible);
        } catch (RemoteException e) {
            Log.w(TAG, "Error noting data connection state", e);
        }
    }

    private final void updateSimState(Intent intent) {
        String stateExtra = intent.getStringExtra(IccCardConstants.INTENT_KEY_ICC_STATE);
        if (IccCardConstants.INTENT_VALUE_ICC_ABSENT.equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.ABSENT;
        } else if (IccCardConstants.INTENT_VALUE_ICC_READY.equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.READY;
        } else if (IccCardConstants.INTENT_VALUE_ICC_LOCKED.equals(stateExtra)) {
            String lockedReason = intent.getStringExtra("reason");
            if (IccCardConstants.INTENT_VALUE_LOCKED_ON_PIN.equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PIN_REQUIRED;
            } else if (IccCardConstants.INTENT_VALUE_LOCKED_ON_PUK.equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PUK_REQUIRED;
            } else {
                this.mSimState = IccCardConstants.State.NETWORK_LOCKED;
            }
        } else {
            this.mSimState = IccCardConstants.State.UNKNOWN;
        }
    }

    private boolean isCdma() {
        return (this.mSignalStrength == null || this.mSignalStrength.isGsm()) ? false : true;
    }

    private boolean hasService() {
        return (this.mServiceState == null || this.mServiceState.getState() == 1 || this.mServiceState.getState() == 3) ? false : true;
    }
}