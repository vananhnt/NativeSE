package android.net.wifi;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SupplicantStateTracker.class */
public class SupplicantStateTracker extends StateMachine {
    private static final String TAG = "SupplicantStateTracker";
    private static final boolean DBG = false;
    private WifiStateMachine mWifiStateMachine;
    private WifiConfigStore mWifiConfigStore;
    private int mAuthenticationFailuresCount;
    private int mAssociationRejectCount;
    private boolean mAuthFailureInSupplicantBroadcast;
    private static final int MAX_RETRIES_ON_AUTHENTICATION_FAILURE = 2;
    private static final int MAX_RETRIES_ON_ASSOCIATION_REJECT = 4;
    private boolean mNetworksDisabledDuringConnect;
    private Context mContext;
    private State mUninitializedState;
    private State mDefaultState;
    private State mInactiveState;
    private State mDisconnectState;
    private State mScanState;
    private State mHandshakeState;
    private State mCompletedState;
    private State mDormantState;

    static /* synthetic */ int access$008(SupplicantStateTracker x0) {
        int i = x0.mAuthenticationFailuresCount;
        x0.mAuthenticationFailuresCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$708(SupplicantStateTracker x0) {
        int i = x0.mAssociationRejectCount;
        x0.mAssociationRejectCount = i + 1;
        return i;
    }

    public SupplicantStateTracker(Context c, WifiStateMachine wsm, WifiConfigStore wcs, Handler t) {
        super(TAG, t.getLooper());
        this.mAuthenticationFailuresCount = 0;
        this.mAssociationRejectCount = 0;
        this.mAuthFailureInSupplicantBroadcast = false;
        this.mNetworksDisabledDuringConnect = false;
        this.mUninitializedState = new UninitializedState();
        this.mDefaultState = new DefaultState();
        this.mInactiveState = new InactiveState();
        this.mDisconnectState = new DisconnectedState();
        this.mScanState = new ScanState();
        this.mHandshakeState = new HandshakeState();
        this.mCompletedState = new CompletedState();
        this.mDormantState = new DormantState();
        this.mContext = c;
        this.mWifiStateMachine = wsm;
        this.mWifiConfigStore = wcs;
        addState(this.mDefaultState);
        addState(this.mUninitializedState, this.mDefaultState);
        addState(this.mInactiveState, this.mDefaultState);
        addState(this.mDisconnectState, this.mDefaultState);
        addState(this.mScanState, this.mDefaultState);
        addState(this.mHandshakeState, this.mDefaultState);
        addState(this.mCompletedState, this.mDefaultState);
        addState(this.mDormantState, this.mDefaultState);
        setInitialState(this.mUninitializedState);
        setLogRecSize(50);
        setLogOnlyTransitions(true);
        start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNetworkConnectionFailure(int netId, int disableReason) {
        if (this.mNetworksDisabledDuringConnect) {
            this.mWifiConfigStore.enableAllNetworks();
            this.mNetworksDisabledDuringConnect = false;
        }
        this.mWifiConfigStore.disableNetwork(netId, disableReason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitionOnSupplicantStateChange(StateChangeResult stateChangeResult) {
        SupplicantState supState = stateChangeResult.state;
        switch (supState) {
            case DISCONNECTED:
                transitionTo(this.mDisconnectState);
                return;
            case INTERFACE_DISABLED:
                return;
            case SCANNING:
                transitionTo(this.mScanState);
                return;
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
                transitionTo(this.mHandshakeState);
                return;
            case COMPLETED:
                transitionTo(this.mCompletedState);
                return;
            case DORMANT:
                transitionTo(this.mDormantState);
                return;
            case INACTIVE:
                transitionTo(this.mInactiveState);
                return;
            case UNINITIALIZED:
            case INVALID:
                transitionTo(this.mUninitializedState);
                return;
            default:
                Log.e(TAG, "Unknown supplicant state " + supState);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSupplicantStateChangedBroadcast(SupplicantState state, boolean failedAuth) {
        Intent intent = new Intent(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intent.addFlags(603979776);
        intent.putExtra(WifiManager.EXTRA_NEW_STATE, (Parcelable) state);
        if (failedAuth) {
            intent.putExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 1);
        }
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* loaded from: SupplicantStateTracker$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131183:
                    SupplicantStateTracker.this.transitionTo(SupplicantStateTracker.this.mUninitializedState);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    SupplicantState state = stateChangeResult.state;
                    SupplicantStateTracker.this.sendSupplicantStateChangedBroadcast(state, SupplicantStateTracker.this.mAuthFailureInSupplicantBroadcast);
                    SupplicantStateTracker.this.mAuthFailureInSupplicantBroadcast = false;
                    SupplicantStateTracker.this.transitionOnSupplicantStateChange(stateChangeResult);
                    return true;
                case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                    SupplicantStateTracker.access$008(SupplicantStateTracker.this);
                    SupplicantStateTracker.this.mAuthFailureInSupplicantBroadcast = true;
                    return true;
                case WifiMonitor.ASSOCIATION_REJECTION_EVENT /* 147499 */:
                    SupplicantStateTracker.access$708(SupplicantStateTracker.this);
                    return true;
                case WifiManager.CONNECT_NETWORK /* 151553 */:
                    SupplicantStateTracker.this.mNetworksDisabledDuringConnect = true;
                    SupplicantStateTracker.this.mAssociationRejectCount = 0;
                    return true;
                default:
                    Log.e(SupplicantStateTracker.TAG, "Ignoring " + message);
                    return true;
            }
        }
    }

    /* loaded from: SupplicantStateTracker$UninitializedState.class */
    class UninitializedState extends State {
        UninitializedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }
    }

    /* loaded from: SupplicantStateTracker$InactiveState.class */
    class InactiveState extends State {
        InactiveState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }
    }

    /* loaded from: SupplicantStateTracker$DisconnectedState.class */
    class DisconnectedState extends State {
        DisconnectedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            Message message = SupplicantStateTracker.this.getCurrentMessage();
            StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
            if (SupplicantStateTracker.this.mAuthenticationFailuresCount < 2) {
                if (SupplicantStateTracker.this.mAssociationRejectCount >= 4) {
                    Log.d(SupplicantStateTracker.TAG, "Association getting rejected, disabling network " + stateChangeResult.networkId);
                    SupplicantStateTracker.this.handleNetworkConnectionFailure(stateChangeResult.networkId, 4);
                    SupplicantStateTracker.this.mAssociationRejectCount = 0;
                    return;
                }
                return;
            }
            Log.d(SupplicantStateTracker.TAG, "Failed to authenticate, disabling network " + stateChangeResult.networkId);
            SupplicantStateTracker.this.handleNetworkConnectionFailure(stateChangeResult.networkId, 3);
            SupplicantStateTracker.this.mAuthenticationFailuresCount = 0;
        }
    }

    /* loaded from: SupplicantStateTracker$ScanState.class */
    class ScanState extends State {
        ScanState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }
    }

    /* loaded from: SupplicantStateTracker$HandshakeState.class */
    class HandshakeState extends State {
        private static final int MAX_SUPPLICANT_LOOP_ITERATIONS = 4;
        private int mLoopDetectIndex;
        private int mLoopDetectCount;

        HandshakeState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            this.mLoopDetectIndex = 0;
            this.mLoopDetectCount = 0;
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    SupplicantState state = stateChangeResult.state;
                    if (SupplicantState.isHandshakeState(state)) {
                        if (this.mLoopDetectIndex > state.ordinal()) {
                            this.mLoopDetectCount++;
                        }
                        if (this.mLoopDetectCount > 4) {
                            Log.d(SupplicantStateTracker.TAG, "Supplicant loop detected, disabling network " + stateChangeResult.networkId);
                            SupplicantStateTracker.this.handleNetworkConnectionFailure(stateChangeResult.networkId, 3);
                        }
                        this.mLoopDetectIndex = state.ordinal();
                        SupplicantStateTracker.this.sendSupplicantStateChangedBroadcast(state, SupplicantStateTracker.this.mAuthFailureInSupplicantBroadcast);
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }
    }

    /* loaded from: SupplicantStateTracker$CompletedState.class */
    class CompletedState extends State {
        CompletedState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            SupplicantStateTracker.this.mAuthenticationFailuresCount = 0;
            SupplicantStateTracker.this.mAssociationRejectCount = 0;
            if (SupplicantStateTracker.this.mNetworksDisabledDuringConnect) {
                SupplicantStateTracker.this.mWifiConfigStore.enableAllNetworks();
                SupplicantStateTracker.this.mNetworksDisabledDuringConnect = false;
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131183:
                    SupplicantStateTracker.this.sendSupplicantStateChangedBroadcast(SupplicantState.DISCONNECTED, false);
                    SupplicantStateTracker.this.transitionTo(SupplicantStateTracker.this.mUninitializedState);
                    return true;
                case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    StateChangeResult stateChangeResult = (StateChangeResult) message.obj;
                    SupplicantState state = stateChangeResult.state;
                    SupplicantStateTracker.this.sendSupplicantStateChangedBroadcast(state, SupplicantStateTracker.this.mAuthFailureInSupplicantBroadcast);
                    if (!SupplicantState.isConnecting(state)) {
                        SupplicantStateTracker.this.transitionOnSupplicantStateChange(stateChangeResult);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: SupplicantStateTracker$DormantState.class */
    class DormantState extends State {
        DormantState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
        }
    }

    @Override // com.android.internal.util.StateMachine
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.println("mAuthenticationFailuresCount " + this.mAuthenticationFailuresCount);
        pw.println("mAuthFailureInSupplicantBroadcast " + this.mAuthFailureInSupplicantBroadcast);
        pw.println("mNetworksDisabledDuringConnect " + this.mNetworksDisabledDuringConnect);
        pw.println();
    }
}