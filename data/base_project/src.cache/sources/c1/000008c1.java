package android.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

/* loaded from: DhcpStateMachine.class */
public class DhcpStateMachine extends StateMachine {
    private static final String TAG = "DhcpStateMachine";
    private static final boolean DBG = false;
    private StateMachine mController;
    private Context mContext;
    private BroadcastReceiver mBroadcastReceiver;
    private AlarmManager mAlarmManager;
    private PendingIntent mDhcpRenewalIntent;
    private PowerManager.WakeLock mDhcpRenewWakeLock;
    private static final String WAKELOCK_TAG = "DHCP";
    private DhcpResults mDhcpResults;
    private static final int DHCP_RENEW = 0;
    private static final String ACTION_DHCP_RENEW = "android.net.wifi.DHCP_RENEW";
    private static final int MIN_RENEWAL_TIME_SECS = 300;
    private final String mInterfaceName;
    private boolean mRegisteredForPreDhcpNotification;
    private static final int BASE = 196608;
    public static final int CMD_START_DHCP = 196609;
    public static final int CMD_STOP_DHCP = 196610;
    public static final int CMD_RENEW_DHCP = 196611;
    public static final int CMD_PRE_DHCP_ACTION = 196612;
    public static final int CMD_POST_DHCP_ACTION = 196613;
    public static final int CMD_ON_QUIT = 196614;
    public static final int CMD_PRE_DHCP_ACTION_COMPLETE = 196615;
    public static final int DHCP_SUCCESS = 1;
    public static final int DHCP_FAILURE = 2;
    private State mDefaultState;
    private State mStoppedState;
    private State mWaitBeforeStartState;
    private State mRunningState;
    private State mWaitBeforeRenewalState;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DhcpStateMachine$DhcpAction.class */
    public enum DhcpAction {
        START,
        RENEW
    }

    private DhcpStateMachine(Context context, StateMachine controller, String intf) {
        super(TAG);
        this.mRegisteredForPreDhcpNotification = false;
        this.mDefaultState = new DefaultState();
        this.mStoppedState = new StoppedState();
        this.mWaitBeforeStartState = new WaitBeforeStartState();
        this.mRunningState = new RunningState();
        this.mWaitBeforeRenewalState = new WaitBeforeRenewalState();
        this.mContext = context;
        this.mController = controller;
        this.mInterfaceName = intf;
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent dhcpRenewalIntent = new Intent(ACTION_DHCP_RENEW, (Uri) null);
        this.mDhcpRenewalIntent = PendingIntent.getBroadcast(this.mContext, 0, dhcpRenewalIntent, 0);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mDhcpRenewWakeLock = powerManager.newWakeLock(1, WAKELOCK_TAG);
        this.mDhcpRenewWakeLock.setReferenceCounted(false);
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: android.net.DhcpStateMachine.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                DhcpStateMachine.this.mDhcpRenewWakeLock.acquire(40000L);
                DhcpStateMachine.this.sendMessage(DhcpStateMachine.CMD_RENEW_DHCP);
            }
        };
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter(ACTION_DHCP_RENEW));
        addState(this.mDefaultState);
        addState(this.mStoppedState, this.mDefaultState);
        addState(this.mWaitBeforeStartState, this.mDefaultState);
        addState(this.mRunningState, this.mDefaultState);
        addState(this.mWaitBeforeRenewalState, this.mDefaultState);
        setInitialState(this.mStoppedState);
    }

    public static DhcpStateMachine makeDhcpStateMachine(Context context, StateMachine controller, String intf) {
        DhcpStateMachine dsm = new DhcpStateMachine(context, controller, intf);
        dsm.start();
        return dsm;
    }

    public void registerForPreDhcpNotification() {
        this.mRegisteredForPreDhcpNotification = true;
    }

    public void doQuit() {
        quit();
    }

    @Override // com.android.internal.util.StateMachine
    protected void onQuitting() {
        this.mController.sendMessage(CMD_ON_QUIT);
    }

    /* loaded from: DhcpStateMachine$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            DhcpStateMachine.this.mContext.unregisterReceiver(DhcpStateMachine.this.mBroadcastReceiver);
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case DhcpStateMachine.CMD_RENEW_DHCP /* 196611 */:
                    Log.e(DhcpStateMachine.TAG, "Error! Failed to handle a DHCP renewal on " + DhcpStateMachine.this.mInterfaceName);
                    DhcpStateMachine.this.mDhcpRenewWakeLock.release();
                    return true;
                default:
                    Log.e(DhcpStateMachine.TAG, "Error! unhandled message  " + message);
                    return true;
            }
        }
    }

    /* loaded from: DhcpStateMachine$StoppedState.class */
    class StoppedState extends State {
        StoppedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            boolean retValue = true;
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    if (DhcpStateMachine.this.mRegisteredForPreDhcpNotification) {
                        DhcpStateMachine.this.mController.sendMessage(DhcpStateMachine.CMD_PRE_DHCP_ACTION);
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mWaitBeforeStartState);
                        break;
                    } else if (DhcpStateMachine.this.runDhcp(DhcpAction.START)) {
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mRunningState);
                        break;
                    }
                    break;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    break;
                default:
                    retValue = false;
                    break;
            }
            return retValue;
        }
    }

    /* loaded from: DhcpStateMachine$WaitBeforeStartState.class */
    class WaitBeforeStartState extends State {
        WaitBeforeStartState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            boolean retValue = true;
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    break;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                    break;
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE /* 196615 */:
                    if (DhcpStateMachine.this.runDhcp(DhcpAction.START)) {
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mRunningState);
                        break;
                    } else {
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                        break;
                    }
                default:
                    retValue = false;
                    break;
            }
            return retValue;
        }
    }

    /* loaded from: DhcpStateMachine$RunningState.class */
    class RunningState extends State {
        RunningState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            boolean retValue = true;
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    break;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine.this.mAlarmManager.cancel(DhcpStateMachine.this.mDhcpRenewalIntent);
                    if (!NetworkUtils.stopDhcp(DhcpStateMachine.this.mInterfaceName)) {
                        Log.e(DhcpStateMachine.TAG, "Failed to stop Dhcp on " + DhcpStateMachine.this.mInterfaceName);
                    }
                    DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                    break;
                case DhcpStateMachine.CMD_RENEW_DHCP /* 196611 */:
                    if (DhcpStateMachine.this.mRegisteredForPreDhcpNotification) {
                        DhcpStateMachine.this.mController.sendMessage(DhcpStateMachine.CMD_PRE_DHCP_ACTION);
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mWaitBeforeRenewalState);
                        break;
                    } else {
                        if (!DhcpStateMachine.this.runDhcp(DhcpAction.RENEW)) {
                            DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                        }
                        DhcpStateMachine.this.mDhcpRenewWakeLock.release();
                        break;
                    }
                default:
                    retValue = false;
                    break;
            }
            return retValue;
        }
    }

    /* loaded from: DhcpStateMachine$WaitBeforeRenewalState.class */
    class WaitBeforeRenewalState extends State {
        WaitBeforeRenewalState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            boolean retValue = true;
            switch (message.what) {
                case DhcpStateMachine.CMD_START_DHCP /* 196609 */:
                    break;
                case DhcpStateMachine.CMD_STOP_DHCP /* 196610 */:
                    DhcpStateMachine.this.mAlarmManager.cancel(DhcpStateMachine.this.mDhcpRenewalIntent);
                    if (!NetworkUtils.stopDhcp(DhcpStateMachine.this.mInterfaceName)) {
                        Log.e(DhcpStateMachine.TAG, "Failed to stop Dhcp on " + DhcpStateMachine.this.mInterfaceName);
                    }
                    DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                    break;
                case DhcpStateMachine.CMD_PRE_DHCP_ACTION_COMPLETE /* 196615 */:
                    if (DhcpStateMachine.this.runDhcp(DhcpAction.RENEW)) {
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mRunningState);
                        break;
                    } else {
                        DhcpStateMachine.this.transitionTo(DhcpStateMachine.this.mStoppedState);
                        break;
                    }
                default:
                    retValue = false;
                    break;
            }
            return retValue;
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void exit() {
            DhcpStateMachine.this.mDhcpRenewWakeLock.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runDhcp(DhcpAction dhcpAction) {
        boolean success = false;
        DhcpResults dhcpResults = new DhcpResults();
        if (dhcpAction == DhcpAction.START) {
            NetworkUtils.stopDhcp(this.mInterfaceName);
            success = NetworkUtils.runDhcp(this.mInterfaceName, dhcpResults);
        } else if (dhcpAction == DhcpAction.RENEW) {
            success = NetworkUtils.runDhcpRenew(this.mInterfaceName, dhcpResults);
            if (success) {
                dhcpResults.updateFromDhcpRequest(this.mDhcpResults);
            }
        }
        if (success) {
            long leaseDuration = dhcpResults.leaseDuration;
            if (leaseDuration >= 0) {
                if (leaseDuration < 300) {
                    leaseDuration = 300;
                }
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + (leaseDuration * 480), this.mDhcpRenewalIntent);
            }
            this.mDhcpResults = dhcpResults;
            this.mController.obtainMessage(CMD_POST_DHCP_ACTION, 1, 0, dhcpResults).sendToTarget();
        } else {
            Log.e(TAG, "DHCP failed on " + this.mInterfaceName + ": " + NetworkUtils.getDhcpError());
            NetworkUtils.stopDhcp(this.mInterfaceName);
            this.mController.obtainMessage(CMD_POST_DHCP_ACTION, 2, 0).sendToTarget();
        }
        return success;
    }
}