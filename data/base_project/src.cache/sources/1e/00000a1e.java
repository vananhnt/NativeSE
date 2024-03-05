package android.net.wifi.p2p;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.DhcpResults;
import android.net.DhcpStateMachine;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.wifi.WifiMonitor;
import android.net.wifi.WifiNative;
import android.net.wifi.WifiStateMachine;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimedRemoteCaller;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: WifiP2pService.class */
public class WifiP2pService extends IWifiP2pManager.Stub {
    private static final String TAG = "WifiP2pService";
    private static final boolean DBG = false;
    private static final String NETWORKTYPE = "WIFI_P2P";
    private Context mContext;
    private Notification mNotification;
    INetworkManagementService mNwService;
    private DhcpStateMachine mDhcpStateMachine;
    private P2pStateMachine mP2pStateMachine;
    private AsyncChannel mWifiChannel;
    private static final int GROUP_CREATING_WAIT_TIME_MS = 120000;
    private static final int DISABLE_P2P_WAIT_TIME_MS = 5000;
    private static final int DISCOVER_TIMEOUT_S = 120;
    private static final int GROUP_IDLE_TIME_S = 10;
    private static final int BASE = 143360;
    public static final int GROUP_CREATING_TIMED_OUT = 143361;
    private static final int PEER_CONNECTION_USER_ACCEPT = 143362;
    private static final int PEER_CONNECTION_USER_REJECT = 143363;
    private static final int DROP_WIFI_USER_ACCEPT = 143364;
    private static final int DROP_WIFI_USER_REJECT = 143365;
    public static final int DISABLE_P2P_TIMED_OUT = 143366;
    public static final int P2P_CONNECTION_CHANGED = 143371;
    public static final int DISCONNECT_WIFI_REQUEST = 143372;
    public static final int DISCONNECT_WIFI_RESPONSE = 143373;
    public static final int SET_MIRACAST_MODE = 143374;
    public static final int BLOCK_DISCOVERY = 143375;
    public static final int SET_COUNTRY_CODE = 143376;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    private final boolean mP2pSupported;
    private boolean mAutonomousGroup;
    private boolean mJoinExistingGroup;
    private boolean mDiscoveryStarted;
    private boolean mDiscoveryBlocked;
    private String mLastSetCountryCode;
    private String mServiceDiscReqId;
    private static final String SERVER_ADDRESS = "192.168.49.1";
    private static final Boolean JOIN_GROUP = true;
    private static final Boolean FORM_GROUP = false;
    private static final Boolean RELOAD = true;
    private static final Boolean NO_RELOAD = false;
    private static int mGroupCreatingTimeoutIndex = 0;
    private static int mDisableP2pTimeoutIndex = 0;
    private static final String[] DHCP_RANGE = {"192.168.49.2", "192.168.49.254"};
    private AsyncChannel mReplyChannel = new AsyncChannel();
    private WifiP2pDevice mThisDevice = new WifiP2pDevice();
    private boolean mDiscoveryPostponed = false;
    private boolean mTempoarilyDisconnectedWifi = false;
    private byte mServiceTransactionId = 0;
    private HashMap<Messenger, ClientInfo> mClientInfoList = new HashMap<>();
    private String mInterface = "p2p0";
    private NetworkInfo mNetworkInfo = new NetworkInfo(13, 0, NETWORKTYPE, "");

    static /* synthetic */ int access$1304() {
        int i = mDisableP2pTimeoutIndex + 1;
        mDisableP2pTimeoutIndex = i;
        return i;
    }

    static /* synthetic */ int access$1704() {
        int i = mGroupCreatingTimeoutIndex + 1;
        mGroupCreatingTimeoutIndex = i;
        return i;
    }

    static /* synthetic */ byte access$11904(WifiP2pService x0) {
        byte b = (byte) (x0.mServiceTransactionId + 1);
        x0.mServiceTransactionId = b;
        return b;
    }

    /* loaded from: WifiP2pService$P2pStatus.class */
    public enum P2pStatus {
        SUCCESS,
        INFORMATION_IS_CURRENTLY_UNAVAILABLE,
        INCOMPATIBLE_PARAMETERS,
        LIMIT_REACHED,
        INVALID_PARAMETER,
        UNABLE_TO_ACCOMMODATE_REQUEST,
        PREVIOUS_PROTOCOL_ERROR,
        NO_COMMON_CHANNEL,
        UNKNOWN_P2P_GROUP,
        BOTH_GO_INTENT_15,
        INCOMPATIBLE_PROVISIONING_METHOD,
        REJECTED_BY_USER,
        UNKNOWN;

        public static P2pStatus valueOf(int error) {
            switch (error) {
                case 0:
                    return SUCCESS;
                case 1:
                    return INFORMATION_IS_CURRENTLY_UNAVAILABLE;
                case 2:
                    return INCOMPATIBLE_PARAMETERS;
                case 3:
                    return LIMIT_REACHED;
                case 4:
                    return INVALID_PARAMETER;
                case 5:
                    return UNABLE_TO_ACCOMMODATE_REQUEST;
                case 6:
                    return PREVIOUS_PROTOCOL_ERROR;
                case 7:
                    return NO_COMMON_CHANNEL;
                case 8:
                    return UNKNOWN_P2P_GROUP;
                case 9:
                    return BOTH_GO_INTENT_15;
                case 10:
                    return INCOMPATIBLE_PROVISIONING_METHOD;
                case 11:
                    return REJECTED_BY_USER;
                default:
                    return UNKNOWN;
            }
        }
    }

    public WifiP2pService(Context context) {
        this.mContext = context;
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
        this.mThisDevice.primaryDeviceType = this.mContext.getResources().getString(R.string.config_wifi_p2p_device_type);
        this.mP2pStateMachine = new P2pStateMachine(TAG, this.mP2pSupported);
        this.mP2pStateMachine.start();
    }

    public void connectivityServiceReady() {
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        this.mNwService = INetworkManagementService.Stub.asInterface(b);
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE, TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_STATE, TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
    }

    @Override // android.net.wifi.p2p.IWifiP2pManager
    public Messenger getMessenger() {
        enforceAccessPermission();
        enforceChangePermission();
        return new Messenger(this.mP2pStateMachine.getHandler());
    }

    @Override // android.net.wifi.p2p.IWifiP2pManager
    public void setMiracastMode(int mode) {
        enforceConnectivityInternalPermission();
        this.mP2pStateMachine.sendMessage(SET_MIRACAST_MODE, mode);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump WifiP2pService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        this.mP2pStateMachine.dump(fd, pw, args);
        pw.println("mAutonomousGroup " + this.mAutonomousGroup);
        pw.println("mJoinExistingGroup " + this.mJoinExistingGroup);
        pw.println("mDiscoveryStarted " + this.mDiscoveryStarted);
        pw.println("mNetworkInfo " + this.mNetworkInfo);
        pw.println("mTempoarilyDisconnectedWifi " + this.mTempoarilyDisconnectedWifi);
        pw.println("mServiceDiscReqId " + this.mServiceDiscReqId);
        pw.println();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiP2pService$P2pStateMachine.class */
    public class P2pStateMachine extends StateMachine {
        private DefaultState mDefaultState;
        private P2pNotSupportedState mP2pNotSupportedState;
        private P2pDisablingState mP2pDisablingState;
        private P2pDisabledState mP2pDisabledState;
        private P2pEnablingState mP2pEnablingState;
        private P2pEnabledState mP2pEnabledState;
        private InactiveState mInactiveState;
        private GroupCreatingState mGroupCreatingState;
        private UserAuthorizingInviteRequestState mUserAuthorizingInviteRequestState;
        private UserAuthorizingNegotiationRequestState mUserAuthorizingNegotiationRequestState;
        private ProvisionDiscoveryState mProvisionDiscoveryState;
        private GroupNegotiationState mGroupNegotiationState;
        private FrequencyConflictState mFrequencyConflictState;
        private GroupCreatedState mGroupCreatedState;
        private UserAuthorizingJoinState mUserAuthorizingJoinState;
        private OngoingGroupRemovalState mOngoingGroupRemovalState;
        private WifiNative mWifiNative;
        private WifiMonitor mWifiMonitor;
        private final WifiP2pDeviceList mPeers;
        private final WifiP2pDeviceList mPeersLostDuringConnection;
        private final WifiP2pGroupList mGroups;
        private final WifiP2pInfo mWifiP2pInfo;
        private WifiP2pGroup mGroup;
        private WifiP2pConfig mSavedPeerConfig;
        private WifiP2pGroup mSavedP2pGroup;

        P2pStateMachine(String name, boolean p2pSupported) {
            super(name);
            this.mDefaultState = new DefaultState();
            this.mP2pNotSupportedState = new P2pNotSupportedState();
            this.mP2pDisablingState = new P2pDisablingState();
            this.mP2pDisabledState = new P2pDisabledState();
            this.mP2pEnablingState = new P2pEnablingState();
            this.mP2pEnabledState = new P2pEnabledState();
            this.mInactiveState = new InactiveState();
            this.mGroupCreatingState = new GroupCreatingState();
            this.mUserAuthorizingInviteRequestState = new UserAuthorizingInviteRequestState();
            this.mUserAuthorizingNegotiationRequestState = new UserAuthorizingNegotiationRequestState();
            this.mProvisionDiscoveryState = new ProvisionDiscoveryState();
            this.mGroupNegotiationState = new GroupNegotiationState();
            this.mFrequencyConflictState = new FrequencyConflictState();
            this.mGroupCreatedState = new GroupCreatedState();
            this.mUserAuthorizingJoinState = new UserAuthorizingJoinState();
            this.mOngoingGroupRemovalState = new OngoingGroupRemovalState();
            this.mWifiNative = new WifiNative(WifiP2pService.this.mInterface);
            this.mWifiMonitor = new WifiMonitor(this, this.mWifiNative);
            this.mPeers = new WifiP2pDeviceList();
            this.mPeersLostDuringConnection = new WifiP2pDeviceList();
            this.mGroups = new WifiP2pGroupList(null, new WifiP2pGroupList.GroupDeleteListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.1
                @Override // android.net.wifi.p2p.WifiP2pGroupList.GroupDeleteListener
                public void onDeleteGroup(int netId) {
                    P2pStateMachine.this.mWifiNative.removeNetwork(netId);
                    P2pStateMachine.this.mWifiNative.saveConfig();
                    P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
                }
            });
            this.mWifiP2pInfo = new WifiP2pInfo();
            this.mSavedPeerConfig = new WifiP2pConfig();
            addState(this.mDefaultState);
            addState(this.mP2pNotSupportedState, this.mDefaultState);
            addState(this.mP2pDisablingState, this.mDefaultState);
            addState(this.mP2pDisabledState, this.mDefaultState);
            addState(this.mP2pEnablingState, this.mDefaultState);
            addState(this.mP2pEnabledState, this.mDefaultState);
            addState(this.mInactiveState, this.mP2pEnabledState);
            addState(this.mGroupCreatingState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingInviteRequestState, this.mGroupCreatingState);
            addState(this.mUserAuthorizingNegotiationRequestState, this.mGroupCreatingState);
            addState(this.mProvisionDiscoveryState, this.mGroupCreatingState);
            addState(this.mGroupNegotiationState, this.mGroupCreatingState);
            addState(this.mFrequencyConflictState, this.mGroupCreatingState);
            addState(this.mGroupCreatedState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingJoinState, this.mGroupCreatedState);
            addState(this.mOngoingGroupRemovalState, this.mGroupCreatedState);
            if (p2pSupported) {
                setInitialState(this.mP2pDisabledState);
            } else {
                setInitialState(this.mP2pNotSupportedState);
            }
            setLogRecSize(50);
            setLogOnlyTransitions(true);
        }

        /* loaded from: WifiP2pService$P2pStateMachine$DefaultState.class */
        class DefaultState extends State {
            DefaultState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case 69632:
                        if (message.arg1 == 0) {
                            WifiP2pService.this.mWifiChannel = (AsyncChannel) message.obj;
                            return true;
                        }
                        P2pStateMachine.this.loge("Full connection failure, error = " + message.arg1);
                        WifiP2pService.this.mWifiChannel = null;
                        return true;
                    case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION /* 69633 */:
                        AsyncChannel ac = new AsyncChannel();
                        ac.connect(WifiP2pService.this.mContext, P2pStateMachine.this.getHandler(), message.replyTo);
                        return true;
                    case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                        if (message.arg1 == 2) {
                            P2pStateMachine.this.loge("Send failed, client connection lost");
                        } else {
                            P2pStateMachine.this.loge("Client connection lost with reason: " + message.arg1);
                        }
                        WifiP2pService.this.mWifiChannel = null;
                        return true;
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                    case WifiP2pManager.SET_CHANNEL /* 139335 */:
                    case WifiP2pService.GROUP_CREATING_TIMED_OUT /* 143361 */:
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                    case WifiP2pService.DROP_WIFI_USER_ACCEPT /* 143364 */:
                    case WifiP2pService.DROP_WIFI_USER_REJECT /* 143365 */:
                    case WifiP2pService.DISABLE_P2P_TIMED_OUT /* 143366 */:
                    case WifiP2pService.DISCONNECT_WIFI_RESPONSE /* 143373 */:
                    case WifiP2pService.SET_MIRACAST_MODE /* 143374 */:
                    case WifiP2pService.SET_COUNTRY_CODE /* 143376 */:
                    case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                    case WifiMonitor.NETWORK_CONNECTION_EVENT /* 147459 */:
                    case WifiMonitor.NETWORK_DISCONNECTION_EVENT /* 147460 */:
                    case WifiMonitor.SCAN_RESULTS_EVENT /* 147461 */:
                    case WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT /* 147462 */:
                    case WifiMonitor.AUTHENTICATION_FAILURE_EVENT /* 147463 */:
                    case WifiMonitor.WPS_SUCCESS_EVENT /* 147464 */:
                    case WifiMonitor.WPS_FAIL_EVENT /* 147465 */:
                    case WifiMonitor.WPS_OVERLAP_EVENT /* 147466 */:
                    case WifiMonitor.WPS_TIMEOUT_EVENT /* 147467 */:
                    case WifiMonitor.P2P_DEVICE_FOUND_EVENT /* 147477 */:
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                    case WifiMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT /* 147484 */:
                    case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                    case WifiMonitor.P2P_INVITATION_RESULT_EVENT /* 147488 */:
                    case WifiMonitor.P2P_FIND_STOPPED_EVENT /* 147493 */:
                    case WifiMonitor.P2P_SERV_DISC_RESP_EVENT /* 147494 */:
                    case WifiMonitor.P2P_PROV_DISC_FAILURE_EVENT /* 147495 */:
                    case DhcpStateMachine.CMD_PRE_DHCP_ACTION /* 196612 */:
                    case DhcpStateMachine.CMD_POST_DHCP_ACTION /* 196613 */:
                    case DhcpStateMachine.CMD_ON_QUIT /* 196614 */:
                        return true;
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        WifiP2pService.this.mWifiChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_RSP);
                        return true;
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_PEERS_FAILED, 2);
                        return true;
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.STOP_DISCOVERY_FAILED, 2);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CONNECT_FAILED, 2);
                        return true;
                    case WifiP2pManager.CANCEL_CONNECT /* 139274 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CANCEL_CONNECT_FAILED, 2);
                        return true;
                    case WifiP2pManager.CREATE_GROUP /* 139277 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CREATE_GROUP_FAILED, 2);
                        return true;
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_GROUP_FAILED, 2);
                        return true;
                    case WifiP2pManager.REQUEST_PEERS /* 139283 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.RESPONSE_PEERS, new WifiP2pDeviceList(P2pStateMachine.this.mPeers));
                        return true;
                    case WifiP2pManager.REQUEST_CONNECTION_INFO /* 139285 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.RESPONSE_CONNECTION_INFO, new WifiP2pInfo(P2pStateMachine.this.mWifiP2pInfo));
                        return true;
                    case WifiP2pManager.REQUEST_GROUP_INFO /* 139287 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.RESPONSE_GROUP_INFO, P2pStateMachine.this.mGroup != null ? new WifiP2pGroup(P2pStateMachine.this.mGroup) : null);
                        return true;
                    case WifiP2pManager.ADD_LOCAL_SERVICE /* 139292 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.ADD_LOCAL_SERVICE_FAILED, 2);
                        return true;
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE /* 139295 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_LOCAL_SERVICE_FAILED, 2);
                        return true;
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES /* 139298 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CLEAR_LOCAL_SERVICES_FAILED, 2);
                        return true;
                    case WifiP2pManager.ADD_SERVICE_REQUEST /* 139301 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.ADD_SERVICE_REQUEST_FAILED, 2);
                        return true;
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST /* 139304 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_SERVICE_REQUEST_FAILED, 2);
                        return true;
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS /* 139307 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CLEAR_SERVICE_REQUESTS_FAILED, 2);
                        return true;
                    case WifiP2pManager.DISCOVER_SERVICES /* 139310 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_SERVICES_FAILED, 2);
                        return true;
                    case WifiP2pManager.SET_DEVICE_NAME /* 139315 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_DEVICE_NAME_FAILED, 2);
                        return true;
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP /* 139318 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DELETE_PERSISTENT_GROUP, 2);
                        return true;
                    case WifiP2pManager.REQUEST_PERSISTENT_GROUP_INFO /* 139321 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.RESPONSE_PERSISTENT_GROUP_INFO, new WifiP2pGroupList(P2pStateMachine.this.mGroups, null));
                        return true;
                    case WifiP2pManager.SET_WFD_INFO /* 139323 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_WFD_INFO_FAILED, 2);
                        return true;
                    case WifiP2pManager.START_WPS /* 139326 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.START_WPS_FAILED, 2);
                        return true;
                    case WifiP2pService.BLOCK_DISCOVERY /* 143375 */:
                        WifiP2pService.this.mDiscoveryBlocked = message.arg1 == 1;
                        WifiP2pService.this.mDiscoveryPostponed = false;
                        if (WifiP2pService.this.mDiscoveryBlocked) {
                            try {
                                StateMachine m = (StateMachine) message.obj;
                                m.sendMessage(message.arg2);
                                return true;
                            } catch (Exception e) {
                                P2pStateMachine.this.loge("unable to send BLOCK_DISCOVERY response: " + e);
                                return true;
                            }
                        }
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                        P2pStateMachine.this.loge("Unexpected group creation, remove " + P2pStateMachine.this.mGroup);
                        P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                        return true;
                    default:
                        P2pStateMachine.this.loge("Unhandled message " + message);
                        return false;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$P2pNotSupportedState.class */
        class P2pNotSupportedState extends State {
            P2pNotSupportedState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_PEERS_FAILED, 1);
                        return true;
                    case WifiP2pManager.DISCOVER_PEERS_FAILED /* 139266 */:
                    case WifiP2pManager.DISCOVER_PEERS_SUCCEEDED /* 139267 */:
                    case WifiP2pManager.STOP_DISCOVERY_FAILED /* 139269 */:
                    case WifiP2pManager.STOP_DISCOVERY_SUCCEEDED /* 139270 */:
                    case WifiP2pManager.CONNECT_FAILED /* 139272 */:
                    case WifiP2pManager.CONNECT_SUCCEEDED /* 139273 */:
                    case WifiP2pManager.CANCEL_CONNECT_FAILED /* 139275 */:
                    case WifiP2pManager.CANCEL_CONNECT_SUCCEEDED /* 139276 */:
                    case WifiP2pManager.CREATE_GROUP_FAILED /* 139278 */:
                    case WifiP2pManager.CREATE_GROUP_SUCCEEDED /* 139279 */:
                    case WifiP2pManager.REMOVE_GROUP_FAILED /* 139281 */:
                    case WifiP2pManager.REMOVE_GROUP_SUCCEEDED /* 139282 */:
                    case WifiP2pManager.REQUEST_PEERS /* 139283 */:
                    case WifiP2pManager.RESPONSE_PEERS /* 139284 */:
                    case WifiP2pManager.REQUEST_CONNECTION_INFO /* 139285 */:
                    case WifiP2pManager.RESPONSE_CONNECTION_INFO /* 139286 */:
                    case WifiP2pManager.REQUEST_GROUP_INFO /* 139287 */:
                    case WifiP2pManager.RESPONSE_GROUP_INFO /* 139288 */:
                    case 139289:
                    case 139290:
                    case 139291:
                    case WifiP2pManager.ADD_LOCAL_SERVICE_FAILED /* 139293 */:
                    case WifiP2pManager.ADD_LOCAL_SERVICE_SUCCEEDED /* 139294 */:
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE_FAILED /* 139296 */:
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE_SUCCEEDED /* 139297 */:
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES_FAILED /* 139299 */:
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES_SUCCEEDED /* 139300 */:
                    case WifiP2pManager.ADD_SERVICE_REQUEST_FAILED /* 139302 */:
                    case WifiP2pManager.ADD_SERVICE_REQUEST_SUCCEEDED /* 139303 */:
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST_FAILED /* 139305 */:
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST_SUCCEEDED /* 139306 */:
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS_FAILED /* 139308 */:
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS_SUCCEEDED /* 139309 */:
                    case WifiP2pManager.DISCOVER_SERVICES_FAILED /* 139311 */:
                    case WifiP2pManager.DISCOVER_SERVICES_SUCCEEDED /* 139312 */:
                    case WifiP2pManager.PING /* 139313 */:
                    case WifiP2pManager.RESPONSE_SERVICE /* 139314 */:
                    case WifiP2pManager.SET_DEVICE_NAME_FAILED /* 139316 */:
                    case WifiP2pManager.SET_DEVICE_NAME_SUCCEEDED /* 139317 */:
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP_FAILED /* 139319 */:
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP_SUCCEEDED /* 139320 */:
                    case WifiP2pManager.REQUEST_PERSISTENT_GROUP_INFO /* 139321 */:
                    case WifiP2pManager.RESPONSE_PERSISTENT_GROUP_INFO /* 139322 */:
                    case WifiP2pManager.SET_WFD_INFO_FAILED /* 139324 */:
                    case WifiP2pManager.SET_WFD_INFO_SUCCEEDED /* 139325 */:
                    case WifiP2pManager.START_WPS_FAILED /* 139327 */:
                    case WifiP2pManager.START_WPS_SUCCEEDED /* 139328 */:
                    case WifiP2pManager.START_LISTEN_FAILED /* 139330 */:
                    case WifiP2pManager.START_LISTEN_SUCCEEDED /* 139331 */:
                    default:
                        return false;
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.STOP_DISCOVERY_FAILED, 1);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CONNECT_FAILED, 1);
                        return true;
                    case WifiP2pManager.CANCEL_CONNECT /* 139274 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CANCEL_CONNECT_FAILED, 1);
                        return true;
                    case WifiP2pManager.CREATE_GROUP /* 139277 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CREATE_GROUP_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_GROUP_FAILED, 1);
                        return true;
                    case WifiP2pManager.ADD_LOCAL_SERVICE /* 139292 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.ADD_LOCAL_SERVICE_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE /* 139295 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_LOCAL_SERVICE_FAILED, 1);
                        return true;
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES /* 139298 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CLEAR_LOCAL_SERVICES_FAILED, 1);
                        return true;
                    case WifiP2pManager.ADD_SERVICE_REQUEST /* 139301 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.ADD_SERVICE_REQUEST_FAILED, 1);
                        return true;
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST /* 139304 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_SERVICE_REQUEST_FAILED, 1);
                        return true;
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS /* 139307 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CLEAR_SERVICE_REQUESTS_FAILED, 1);
                        return true;
                    case WifiP2pManager.DISCOVER_SERVICES /* 139310 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_SERVICES_FAILED, 1);
                        return true;
                    case WifiP2pManager.SET_DEVICE_NAME /* 139315 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_DEVICE_NAME_FAILED, 1);
                        return true;
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP /* 139318 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DELETE_PERSISTENT_GROUP, 1);
                        return true;
                    case WifiP2pManager.SET_WFD_INFO /* 139323 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_WFD_INFO_FAILED, 1);
                        return true;
                    case WifiP2pManager.START_WPS /* 139326 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.START_WPS_FAILED, 1);
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.START_LISTEN_FAILED, 1);
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.STOP_LISTEN_FAILED, 1);
                        return true;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$P2pDisablingState.class */
        class P2pDisablingState extends State {
            P2pDisablingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.sendMessageDelayed(P2pStateMachine.this.obtainMessage(WifiP2pService.DISABLE_P2P_TIMED_OUT, WifiP2pService.access$1304(), 0), TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiP2pService.DISABLE_P2P_TIMED_OUT /* 143366 */:
                        if (WifiP2pService.mGroupCreatingTimeoutIndex == message.arg1) {
                            P2pStateMachine.this.loge("P2p disable timed out");
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pDisabledState);
                            return true;
                        }
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pDisabledState);
                        return true;
                    default:
                        return false;
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                WifiP2pService.this.mWifiChannel.sendMessage(WifiStateMachine.CMD_DISABLE_P2P_RSP);
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$P2pDisabledState.class */
        class P2pDisabledState extends State {
            P2pDisabledState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                        try {
                            WifiP2pService.this.mNwService.setInterfaceUp(WifiP2pService.this.mInterface);
                        } catch (RemoteException re) {
                            P2pStateMachine.this.loge("Unable to change interface settings: " + re);
                        } catch (IllegalStateException ie) {
                            P2pStateMachine.this.loge("Unable to change interface settings: " + ie);
                        }
                        P2pStateMachine.this.mWifiMonitor.startMonitoring();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pEnablingState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$P2pEnablingState.class */
        class P2pEnablingState extends State {
            P2pEnablingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiMonitor.SUP_CONNECTION_EVENT /* 147457 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine.this.loge("P2p socket connection failed");
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pDisabledState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$P2pEnabledState.class */
        class P2pEnabledState extends State {
            P2pEnabledState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.sendP2pStateChangedBroadcast(true);
                WifiP2pService.this.mNetworkInfo.setIsAvailable(true);
                P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                P2pStateMachine.this.initializeP2pSettings();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiStateMachine.CMD_ENABLE_P2P /* 131203 */:
                        return true;
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        if (P2pStateMachine.this.mPeers.clear()) {
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        }
                        if (P2pStateMachine.this.mGroups.clear()) {
                            P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
                        }
                        P2pStateMachine.this.mWifiMonitor.stopMonitoring();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pDisablingState);
                        return true;
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        if (WifiP2pService.this.mDiscoveryBlocked) {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_PEERS_FAILED, 2);
                            return true;
                        }
                        P2pStateMachine.this.clearSupplicantServiceRequest();
                        if (P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_PEERS_SUCCEEDED);
                            P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(true);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_PEERS_FAILED, 0);
                        return true;
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        if (P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.STOP_DISCOVERY_FAILED, 0);
                        return true;
                    case WifiP2pManager.ADD_LOCAL_SERVICE /* 139292 */:
                        WifiP2pServiceInfo servInfo = (WifiP2pServiceInfo) message.obj;
                        if (P2pStateMachine.this.addLocalService(message.replyTo, servInfo)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_LOCAL_SERVICE_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_LOCAL_SERVICE_FAILED);
                        return true;
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE /* 139295 */:
                        WifiP2pServiceInfo servInfo2 = (WifiP2pServiceInfo) message.obj;
                        P2pStateMachine.this.removeLocalService(message.replyTo, servInfo2);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_LOCAL_SERVICE_SUCCEEDED);
                        return true;
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES /* 139298 */:
                        P2pStateMachine.this.clearLocalServices(message.replyTo);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_LOCAL_SERVICES_SUCCEEDED);
                        return true;
                    case WifiP2pManager.ADD_SERVICE_REQUEST /* 139301 */:
                        if (!P2pStateMachine.this.addServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_SERVICE_REQUEST_FAILED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.ADD_SERVICE_REQUEST_SUCCEEDED);
                        return true;
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST /* 139304 */:
                        P2pStateMachine.this.removeServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_SERVICE_REQUEST_SUCCEEDED);
                        return true;
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS /* 139307 */:
                        P2pStateMachine.this.clearServiceRequests(message.replyTo);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CLEAR_SERVICE_REQUESTS_SUCCEEDED);
                        return true;
                    case WifiP2pManager.DISCOVER_SERVICES /* 139310 */:
                        if (WifiP2pService.this.mDiscoveryBlocked) {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_SERVICES_FAILED, 2);
                            return true;
                        } else if (!P2pStateMachine.this.updateSupplicantServiceRequest()) {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_SERVICES_FAILED, 3);
                            return true;
                        } else if (P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DISCOVER_SERVICES_SUCCEEDED);
                            return true;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_SERVICES_FAILED, 0);
                            return true;
                        }
                    case WifiP2pManager.SET_DEVICE_NAME /* 139315 */:
                        WifiP2pDevice d = (WifiP2pDevice) message.obj;
                        if (d == null || !P2pStateMachine.this.setAndPersistDeviceName(d.deviceName)) {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_DEVICE_NAME_FAILED, 0);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_DEVICE_NAME_SUCCEEDED);
                        return true;
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP /* 139318 */:
                        P2pStateMachine.this.mGroups.remove(message.arg1);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.DELETE_PERSISTENT_GROUP_SUCCEEDED);
                        return true;
                    case WifiP2pManager.SET_WFD_INFO /* 139323 */:
                        WifiP2pWfdInfo d2 = (WifiP2pWfdInfo) message.obj;
                        if (d2 == null || !P2pStateMachine.this.setWfdInfo(d2)) {
                            P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.SET_WFD_INFO_FAILED, 0);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_WFD_INFO_SUCCEEDED);
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_FAILED);
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_FAILED);
                        }
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        return true;
                    case WifiP2pManager.SET_CHANNEL /* 139335 */:
                        Bundle p2pChannels = (Bundle) message.obj;
                        int lc = p2pChannels.getInt("lc", 0);
                        int oc = p2pChannels.getInt("oc", 0);
                        if (P2pStateMachine.this.mWifiNative.p2pSetChannel(lc, oc)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_FAILED);
                        return true;
                    case WifiP2pService.SET_MIRACAST_MODE /* 143374 */:
                        P2pStateMachine.this.mWifiNative.setMiracastMode(message.arg1);
                        return true;
                    case WifiP2pService.BLOCK_DISCOVERY /* 143375 */:
                        boolean blocked = message.arg1 == 1;
                        if (WifiP2pService.this.mDiscoveryBlocked != blocked) {
                            WifiP2pService.this.mDiscoveryBlocked = blocked;
                            if (blocked && WifiP2pService.this.mDiscoveryStarted) {
                                P2pStateMachine.this.mWifiNative.p2pStopFind();
                                WifiP2pService.this.mDiscoveryPostponed = true;
                            }
                            if (!blocked && WifiP2pService.this.mDiscoveryPostponed) {
                                WifiP2pService.this.mDiscoveryPostponed = false;
                                P2pStateMachine.this.mWifiNative.p2pFind(120);
                            }
                            if (blocked) {
                                try {
                                    StateMachine m = (StateMachine) message.obj;
                                    m.sendMessage(message.arg2);
                                    return true;
                                } catch (Exception e) {
                                    P2pStateMachine.this.loge("unable to send BLOCK_DISCOVERY response: " + e);
                                    return true;
                                }
                            }
                            return true;
                        }
                        return true;
                    case WifiP2pService.SET_COUNTRY_CODE /* 143376 */:
                        String countryCode = ((String) message.obj).toUpperCase(Locale.ROOT);
                        if ((WifiP2pService.this.mLastSetCountryCode == null || !countryCode.equals(WifiP2pService.this.mLastSetCountryCode)) && P2pStateMachine.this.mWifiNative.setCountryCode(countryCode)) {
                            WifiP2pService.this.mLastSetCountryCode = countryCode;
                            return true;
                        }
                        return true;
                    case WifiMonitor.SUP_DISCONNECTION_EVENT /* 147458 */:
                        P2pStateMachine.this.loge("Unexpected loss of p2p socket connection");
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mP2pDisabledState);
                        return true;
                    case WifiMonitor.P2P_DEVICE_FOUND_EVENT /* 147477 */:
                        WifiP2pDevice device = (WifiP2pDevice) message.obj;
                        if (!WifiP2pService.this.mThisDevice.deviceAddress.equals(device.deviceAddress)) {
                            P2pStateMachine.this.mPeers.updateSupplicantDetails(device);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        if (P2pStateMachine.this.mPeers.remove(((WifiP2pDevice) message.obj).deviceAddress) != null) {
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_FIND_STOPPED_EVENT /* 147493 */:
                        P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(false);
                        return true;
                    case WifiMonitor.P2P_SERV_DISC_RESP_EVENT /* 147494 */:
                        List<WifiP2pServiceResponse> sdRespList = (List) message.obj;
                        for (WifiP2pServiceResponse resp : sdRespList) {
                            WifiP2pDevice dev = P2pStateMachine.this.mPeers.get(resp.getSrcDevice().deviceAddress);
                            resp.setSrcDevice(dev);
                            P2pStateMachine.this.sendServiceResponse(resp);
                        }
                        return true;
                    default:
                        return false;
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                P2pStateMachine.this.sendP2pStateChangedBroadcast(false);
                WifiP2pService.this.mNetworkInfo.setIsAvailable(false);
                WifiP2pService.this.mLastSetCountryCode = null;
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$InactiveState.class */
        class InactiveState extends State {
            InactiveState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.mSavedPeerConfig.invalidate();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                boolean ret;
                switch (message.what) {
                    case WifiP2pManager.STOP_DISCOVERY /* 139268 */:
                        if (P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                            P2pStateMachine.this.mWifiNative.p2pFlush();
                            WifiP2pService.this.mServiceDiscReqId = null;
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_DISCOVERY_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.STOP_DISCOVERY_FAILED, 0);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        WifiP2pConfig config = (WifiP2pConfig) message.obj;
                        if (!P2pStateMachine.this.isConfigInvalid(config)) {
                            WifiP2pService.this.mAutonomousGroup = false;
                            P2pStateMachine.this.mWifiNative.p2pStopFind();
                            if (P2pStateMachine.this.reinvokePersistentGroup(config)) {
                                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            } else {
                                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mProvisionDiscoveryState);
                            }
                            P2pStateMachine.this.mSavedPeerConfig = config;
                            P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.loge("Dropping connect requeset " + config);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED);
                        return true;
                    case WifiP2pManager.CREATE_GROUP /* 139277 */:
                        WifiP2pService.this.mAutonomousGroup = true;
                        if (message.arg1 == -2) {
                            int netId = P2pStateMachine.this.mGroups.getNetworkId(WifiP2pService.this.mThisDevice.deviceAddress);
                            ret = netId != -1 ? P2pStateMachine.this.mWifiNative.p2pGroupAdd(netId) : P2pStateMachine.this.mWifiNative.p2pGroupAdd(true);
                        } else {
                            ret = P2pStateMachine.this.mWifiNative.p2pGroupAdd(false);
                        }
                        if (ret) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CREATE_GROUP_SUCCEEDED);
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CREATE_GROUP_FAILED, 0);
                        return true;
                    case WifiP2pManager.START_LISTEN /* 139329 */:
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_LISTEN_FAILED);
                        return true;
                    case WifiP2pManager.STOP_LISTEN /* 139332 */:
                        if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_SUCCEEDED);
                        } else {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.STOP_LISTEN_FAILED);
                        }
                        P2pStateMachine.this.mWifiNative.p2pFlush();
                        return true;
                    case WifiP2pManager.SET_CHANNEL /* 139335 */:
                        Bundle p2pChannels = (Bundle) message.obj;
                        int lc = p2pChannels.getInt("lc", 0);
                        int oc = p2pChannels.getInt("oc", 0);
                        if (P2pStateMachine.this.mWifiNative.p2pSetChannel(lc, oc)) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.SET_CHANNEL_FAILED);
                        return true;
                    case WifiMonitor.P2P_GO_NEGOTIATION_REQUEST_EVENT /* 147479 */:
                        WifiP2pConfig config2 = (WifiP2pConfig) message.obj;
                        if (!P2pStateMachine.this.isConfigInvalid(config2)) {
                            P2pStateMachine.this.mSavedPeerConfig = config2;
                            WifiP2pService.this.mAutonomousGroup = false;
                            WifiP2pService.this.mJoinExistingGroup = false;
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mUserAuthorizingNegotiationRequestState);
                            return true;
                        }
                        P2pStateMachine.this.loge("Dropping GO neg request " + config2);
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                        if (P2pStateMachine.this.mGroup.getNetworkId() == -2) {
                            WifiP2pService.this.mAutonomousGroup = false;
                            P2pStateMachine.this.deferMessage(message);
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            return true;
                        }
                        P2pStateMachine.this.loge("Unexpected group creation, remove " + P2pStateMachine.this.mGroup);
                        P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                        return true;
                    case WifiMonitor.P2P_INVITATION_RECEIVED_EVENT /* 147487 */:
                        WifiP2pGroup group = (WifiP2pGroup) message.obj;
                        WifiP2pDevice owner = group.getOwner();
                        if (owner == null) {
                            P2pStateMachine.this.loge("Ignored invitation from null owner");
                            return true;
                        }
                        WifiP2pConfig config3 = new WifiP2pConfig();
                        config3.deviceAddress = group.getOwner().deviceAddress;
                        if (!P2pStateMachine.this.isConfigInvalid(config3)) {
                            P2pStateMachine.this.mSavedPeerConfig = config3;
                            WifiP2pDevice owner2 = P2pStateMachine.this.mPeers.get(owner.deviceAddress);
                            if (owner2 != null) {
                                if (owner2.wpsPbcSupported()) {
                                    P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                                } else if (owner2.wpsKeypadSupported()) {
                                    P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                                } else if (owner2.wpsDisplaySupported()) {
                                    P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                                }
                            }
                            WifiP2pService.this.mAutonomousGroup = false;
                            WifiP2pService.this.mJoinExistingGroup = true;
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mUserAuthorizingInviteRequestState);
                            return true;
                        }
                        P2pStateMachine.this.loge("Dropping invitation request " + config3);
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        return true;
                    default:
                        return false;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$GroupCreatingState.class */
        class GroupCreatingState extends State {
            GroupCreatingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.sendMessageDelayed(P2pStateMachine.this.obtainMessage(WifiP2pService.GROUP_CREATING_TIMED_OUT, WifiP2pService.access$1704(), 0), 120000L);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                boolean ret = true;
                switch (message.what) {
                    case WifiP2pManager.DISCOVER_PEERS /* 139265 */:
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.DISCOVER_PEERS_FAILED, 2);
                        break;
                    case WifiP2pManager.CANCEL_CONNECT /* 139274 */:
                        P2pStateMachine.this.mWifiNative.p2pCancelConnect();
                        P2pStateMachine.this.handleGroupCreationFailure();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CANCEL_CONNECT_SUCCEEDED);
                        break;
                    case WifiP2pService.GROUP_CREATING_TIMED_OUT /* 143361 */:
                        if (WifiP2pService.mGroupCreatingTimeoutIndex == message.arg1) {
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                            break;
                        }
                        break;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        WifiP2pDevice device = (WifiP2pDevice) message.obj;
                        if (P2pStateMachine.this.mSavedPeerConfig.deviceAddress.equals(device.deviceAddress)) {
                            P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(device);
                            break;
                        } else {
                            ret = false;
                            break;
                        }
                    default:
                        ret = false;
                        break;
                }
                return ret;
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$UserAuthorizingNegotiationRequestState.class */
        class UserAuthorizingNegotiationRequestState extends State {
            UserAuthorizingNegotiationRequestState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                        P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                        P2pStateMachine.this.sendPeersChangedBroadcast();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                        break;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        break;
                    default:
                        return false;
                }
                return true;
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$UserAuthorizingInviteRequestState.class */
        class UserAuthorizingInviteRequestState extends State {
            UserAuthorizingInviteRequestState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        if (!P2pStateMachine.this.reinvokePersistentGroup(P2pStateMachine.this.mSavedPeerConfig)) {
                            P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                        }
                        P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                        P2pStateMachine.this.sendPeersChangedBroadcast();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                        break;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        break;
                    default:
                        return false;
                }
                return true;
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$ProvisionDiscoveryState.class */
        class ProvisionDiscoveryState extends State {
            ProvisionDiscoveryState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.mWifiNative.p2pProvisionDiscovery(P2pStateMachine.this.mSavedPeerConfig);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiMonitor.P2P_PROV_DISC_PBC_RSP_EVENT /* 147490 */:
                        if (((WifiP2pProvDiscEvent) message.obj).device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                            P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                        if (((WifiP2pProvDiscEvent) message.obj).device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 2) {
                            if (TextUtils.isEmpty(P2pStateMachine.this.mSavedPeerConfig.wps.pin)) {
                                WifiP2pService.this.mJoinExistingGroup = false;
                                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mUserAuthorizingNegotiationRequestState);
                                return true;
                            }
                            P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        WifiP2pProvDiscEvent provDisc = (WifiP2pProvDiscEvent) message.obj;
                        WifiP2pDevice device = provDisc.device;
                        if (device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 1) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.pin = provDisc.pin;
                            P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                            P2pStateMachine.this.notifyInvitationSent(provDisc.pin, device.deviceAddress);
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_FIND_STOPPED_EVENT /* 147493 */:
                    case WifiMonitor.P2P_SERV_DISC_RESP_EVENT /* 147494 */:
                    default:
                        return false;
                    case WifiMonitor.P2P_PROV_DISC_FAILURE_EVENT /* 147495 */:
                        P2pStateMachine.this.loge("provision discovery failed");
                        P2pStateMachine.this.handleGroupCreationFailure();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        return true;
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$GroupNegotiationState.class */
        class GroupNegotiationState extends State {
            GroupNegotiationState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT /* 147481 */:
                    case WifiMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT /* 147483 */:
                        return true;
                    case WifiMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT /* 147482 */:
                        if (((P2pStatus) message.obj) == P2pStatus.NO_COMMON_CHANNEL) {
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mFrequencyConflictState);
                            return true;
                        }
                        break;
                    case WifiMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT /* 147484 */:
                        if (((P2pStatus) message.obj) == P2pStatus.NO_COMMON_CHANNEL) {
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mFrequencyConflictState);
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                        if (P2pStateMachine.this.mGroup.getNetworkId() == -2) {
                            P2pStateMachine.this.updatePersistentNetworks(WifiP2pService.NO_RELOAD.booleanValue());
                            String devAddr = P2pStateMachine.this.mGroup.getOwner().deviceAddress;
                            P2pStateMachine.this.mGroup.setNetworkId(P2pStateMachine.this.mGroups.getNetworkId(devAddr, P2pStateMachine.this.mGroup.getNetworkName()));
                        }
                        if (P2pStateMachine.this.mGroup.isGroupOwner()) {
                            if (!WifiP2pService.this.mAutonomousGroup) {
                                P2pStateMachine.this.mWifiNative.setP2pGroupIdle(P2pStateMachine.this.mGroup.getInterface(), 10);
                            }
                            P2pStateMachine.this.startDhcpServer(P2pStateMachine.this.mGroup.getInterface());
                        } else {
                            P2pStateMachine.this.mWifiNative.setP2pGroupIdle(P2pStateMachine.this.mGroup.getInterface(), 10);
                            WifiP2pService.this.mDhcpStateMachine = DhcpStateMachine.makeDhcpStateMachine(WifiP2pService.this.mContext, P2pStateMachine.this, P2pStateMachine.this.mGroup.getInterface());
                            P2pStateMachine.this.mWifiNative.setP2pPowerSave(P2pStateMachine.this.mGroup.getInterface(), false);
                            WifiP2pService.this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_START_DHCP);
                            WifiP2pDevice groupOwner = P2pStateMachine.this.mGroup.getOwner();
                            WifiP2pDevice peer = P2pStateMachine.this.mPeers.get(groupOwner.deviceAddress);
                            if (peer == null) {
                                P2pStateMachine.this.logw("Unknown group owner " + groupOwner);
                            } else {
                                groupOwner.updateSupplicantDetails(peer);
                                P2pStateMachine.this.mPeers.updateStatus(groupOwner.deviceAddress, 0);
                                P2pStateMachine.this.sendPeersChangedBroadcast();
                            }
                        }
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupCreatedState);
                        return true;
                    case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                        break;
                    case WifiMonitor.P2P_INVITATION_RECEIVED_EVENT /* 147487 */:
                    default:
                        return false;
                    case WifiMonitor.P2P_INVITATION_RESULT_EVENT /* 147488 */:
                        P2pStatus status = (P2pStatus) message.obj;
                        if (status != P2pStatus.SUCCESS) {
                            P2pStateMachine.this.loge("Invitation result " + status);
                            if (status == P2pStatus.UNKNOWN_P2P_GROUP) {
                                int netId = P2pStateMachine.this.mSavedPeerConfig.netId;
                                if (netId >= 0) {
                                    P2pStateMachine.this.removeClientFromList(netId, P2pStateMachine.this.mSavedPeerConfig.deviceAddress, true);
                                }
                                P2pStateMachine.this.mSavedPeerConfig.netId = -2;
                                P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                                return true;
                            } else if (status == P2pStatus.INFORMATION_IS_CURRENTLY_UNAVAILABLE) {
                                P2pStateMachine.this.mSavedPeerConfig.netId = -2;
                                P2pStateMachine.this.p2pConnectWithPinDisplay(P2pStateMachine.this.mSavedPeerConfig);
                                return true;
                            } else if (status == P2pStatus.NO_COMMON_CHANNEL) {
                                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mFrequencyConflictState);
                                return true;
                            } else {
                                P2pStateMachine.this.handleGroupCreationFailure();
                                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                                return true;
                            }
                        }
                        return true;
                }
                P2pStateMachine.this.handleGroupCreationFailure();
                P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                return true;
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$FrequencyConflictState.class */
        class FrequencyConflictState extends State {
            private AlertDialog mFrequencyConflictDialog;

            FrequencyConflictState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                notifyFrequencyConflict();
            }

            private void notifyFrequencyConflict() {
                P2pStateMachine.this.logd("Notify frequency conflict");
                Resources r = Resources.getSystem();
                AlertDialog dialog = new AlertDialog.Builder(WifiP2pService.this.mContext).setMessage(r.getString(R.string.wifi_p2p_frequency_conflict_message, P2pStateMachine.this.getDeviceName(P2pStateMachine.this.mSavedPeerConfig.deviceAddress))).setPositiveButton(r.getString(R.string.dlg_ok), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_ACCEPT);
                    }
                }).setNegativeButton(r.getString(R.string.decline), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog2, int which) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_REJECT);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.FrequencyConflictState.1
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface arg0) {
                        P2pStateMachine.this.sendMessage(WifiP2pService.DROP_WIFI_USER_REJECT);
                    }
                }).create();
                dialog.getWindow().setType(2003);
                dialog.show();
                this.mFrequencyConflictDialog = dialog;
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.DROP_WIFI_USER_ACCEPT /* 143364 */:
                        WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_REQUEST, 1);
                        WifiP2pService.this.mTempoarilyDisconnectedWifi = true;
                        return true;
                    case WifiP2pService.DROP_WIFI_USER_REJECT /* 143365 */:
                        P2pStateMachine.this.handleGroupCreationFailure();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        return true;
                    case WifiP2pService.DISCONNECT_WIFI_RESPONSE /* 143373 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        P2pStateMachine.this.sendMessage(WifiP2pManager.CONNECT, P2pStateMachine.this.mSavedPeerConfig);
                        return true;
                    case WifiMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT /* 147481 */:
                    case WifiMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT /* 147483 */:
                        P2pStateMachine.this.loge(getName() + "group sucess during freq conflict!");
                        return true;
                    case WifiMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT /* 147482 */:
                    case WifiMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT /* 147484 */:
                    case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                        return true;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.loge(getName() + "group started after freq conflict, handle anyway");
                        P2pStateMachine.this.deferMessage(message);
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupNegotiationState);
                        return true;
                    default:
                        return false;
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                if (this.mFrequencyConflictDialog != null) {
                    this.mFrequencyConflictDialog.dismiss();
                }
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$GroupCreatedState.class */
        class GroupCreatedState extends State {
            GroupCreatedState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.mSavedPeerConfig.invalidate();
                WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                P2pStateMachine.this.updateThisDevice(0);
                if (P2pStateMachine.this.mGroup.isGroupOwner()) {
                    P2pStateMachine.this.setWifiP2pInfoOnGroupFormation(NetworkUtils.numericToInetAddress(WifiP2pService.SERVER_ADDRESS));
                }
                if (WifiP2pService.this.mAutonomousGroup) {
                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                int netId;
                switch (message.what) {
                    case WifiStateMachine.CMD_DISABLE_P2P_REQ /* 131204 */:
                        P2pStateMachine.this.sendMessage(WifiP2pManager.REMOVE_GROUP);
                        P2pStateMachine.this.deferMessage(message);
                        return true;
                    case WifiP2pManager.CONNECT /* 139271 */:
                        WifiP2pConfig config = (WifiP2pConfig) message.obj;
                        if (P2pStateMachine.this.isConfigInvalid(config)) {
                            P2pStateMachine.this.loge("Dropping connect requeset " + config);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_FAILED);
                            return true;
                        }
                        P2pStateMachine.this.logd("Inviting device : " + config.deviceAddress);
                        P2pStateMachine.this.mSavedPeerConfig = config;
                        if (P2pStateMachine.this.mWifiNative.p2pInvite(P2pStateMachine.this.mGroup, config.deviceAddress)) {
                            P2pStateMachine.this.mPeers.updateStatus(config.deviceAddress, 1);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.CONNECT_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.CONNECT_FAILED, 0);
                        return true;
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                        if (P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface())) {
                            P2pStateMachine.this.transitionTo(P2pStateMachine.this.mOngoingGroupRemovalState);
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_SUCCEEDED);
                            return true;
                        }
                        P2pStateMachine.this.handleGroupRemoved();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        P2pStateMachine.this.replyToMessage(message, (int) WifiP2pManager.REMOVE_GROUP_FAILED, 0);
                        return true;
                    case WifiP2pManager.START_WPS /* 139326 */:
                        WpsInfo wps = (WpsInfo) message.obj;
                        if (wps == null) {
                            P2pStateMachine.this.replyToMessage(message, WifiP2pManager.START_WPS_FAILED);
                            return true;
                        }
                        boolean ret = true;
                        if (wps.setup == 0) {
                            ret = P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), null);
                        } else if (wps.pin == null) {
                            String pin = P2pStateMachine.this.mWifiNative.startWpsPinDisplay(P2pStateMachine.this.mGroup.getInterface());
                            try {
                                Integer.parseInt(pin);
                                P2pStateMachine.this.notifyInvitationSent(pin, "any");
                            } catch (NumberFormatException e) {
                                ret = false;
                            }
                        } else {
                            ret = P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), wps.pin);
                        }
                        P2pStateMachine.this.replyToMessage(message, ret ? WifiP2pManager.START_WPS_SUCCEEDED : WifiP2pManager.START_WPS_FAILED);
                        return true;
                    case WifiMonitor.P2P_DEVICE_LOST_EVENT /* 147478 */:
                        WifiP2pDevice device = (WifiP2pDevice) message.obj;
                        if (P2pStateMachine.this.mGroup.contains(device)) {
                            P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(device);
                            return true;
                        }
                        return false;
                    case WifiMonitor.P2P_GROUP_STARTED_EVENT /* 147485 */:
                        P2pStateMachine.this.loge("Duplicate group creation event notice, ignore");
                        return true;
                    case WifiMonitor.P2P_GROUP_REMOVED_EVENT /* 147486 */:
                        P2pStateMachine.this.handleGroupRemoved();
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mInactiveState);
                        return true;
                    case WifiMonitor.P2P_INVITATION_RESULT_EVENT /* 147488 */:
                        P2pStatus status = (P2pStatus) message.obj;
                        if (status != P2pStatus.SUCCESS) {
                            P2pStateMachine.this.loge("Invitation result " + status);
                            if (status == P2pStatus.UNKNOWN_P2P_GROUP && (netId = P2pStateMachine.this.mGroup.getNetworkId()) >= 0) {
                                if (P2pStateMachine.this.removeClientFromList(netId, P2pStateMachine.this.mSavedPeerConfig.deviceAddress, false)) {
                                    P2pStateMachine.this.sendMessage(WifiP2pManager.CONNECT, P2pStateMachine.this.mSavedPeerConfig);
                                    return true;
                                }
                                P2pStateMachine.this.loge("Already removed the client, ignore");
                                return true;
                            }
                            return true;
                        }
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        WifiP2pProvDiscEvent provDisc = (WifiP2pProvDiscEvent) message.obj;
                        P2pStateMachine.this.mSavedPeerConfig = new WifiP2pConfig();
                        P2pStateMachine.this.mSavedPeerConfig.deviceAddress = provDisc.device.deviceAddress;
                        if (message.what == 147491) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                        } else if (message.what == 147492) {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                            P2pStateMachine.this.mSavedPeerConfig.wps.pin = provDisc.pin;
                        } else {
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                        }
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mUserAuthorizingJoinState);
                        return true;
                    case WifiMonitor.AP_STA_DISCONNECTED_EVENT /* 147497 */:
                        WifiP2pDevice device2 = (WifiP2pDevice) message.obj;
                        String deviceAddress = device2.deviceAddress;
                        if (deviceAddress != null) {
                            P2pStateMachine.this.mPeers.updateStatus(deviceAddress, 3);
                            if (P2pStateMachine.this.mGroup.removeClient(deviceAddress)) {
                                if (WifiP2pService.this.mAutonomousGroup || !P2pStateMachine.this.mGroup.isClientListEmpty()) {
                                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                                } else {
                                    P2pStateMachine.this.logd("Client list empty, remove non-persistent p2p group");
                                    P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                                }
                            } else {
                                for (WifiP2pDevice wifiP2pDevice : P2pStateMachine.this.mGroup.getClientList()) {
                                }
                            }
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            return true;
                        }
                        P2pStateMachine.this.loge("Disconnect on unknown device: " + device2);
                        return true;
                    case WifiMonitor.AP_STA_CONNECTED_EVENT /* 147498 */:
                        String deviceAddress2 = ((WifiP2pDevice) message.obj).deviceAddress;
                        P2pStateMachine.this.mWifiNative.setP2pGroupIdle(P2pStateMachine.this.mGroup.getInterface(), 0);
                        if (deviceAddress2 != null) {
                            if (P2pStateMachine.this.mPeers.get(deviceAddress2) != null) {
                                P2pStateMachine.this.mGroup.addClient(P2pStateMachine.this.mPeers.get(deviceAddress2));
                            } else {
                                P2pStateMachine.this.mGroup.addClient(deviceAddress2);
                            }
                            P2pStateMachine.this.mPeers.updateStatus(deviceAddress2, 0);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                        } else {
                            P2pStateMachine.this.loge("Connect on null device address, ignore");
                        }
                        P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                        return true;
                    case DhcpStateMachine.CMD_POST_DHCP_ACTION /* 196613 */:
                        DhcpResults dhcpResults = (DhcpResults) message.obj;
                        if (message.arg1 == 1 && dhcpResults != null) {
                            P2pStateMachine.this.setWifiP2pInfoOnGroupFormation(dhcpResults.serverAddress);
                            P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                            P2pStateMachine.this.mWifiNative.setP2pPowerSave(P2pStateMachine.this.mGroup.getInterface(), true);
                            return true;
                        }
                        P2pStateMachine.this.loge("DHCP failed");
                        P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                        return true;
                    default:
                        return false;
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                P2pStateMachine.this.updateThisDevice(3);
                P2pStateMachine.this.resetWifiP2pInfo();
                WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
                P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$UserAuthorizingJoinState.class */
        class UserAuthorizingJoinState extends State {
            UserAuthorizingJoinState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                P2pStateMachine.this.notifyInvitationReceived();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pService.PEER_CONNECTION_USER_ACCEPT /* 143362 */:
                        P2pStateMachine.this.mWifiNative.p2pStopFind();
                        if (P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                            P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), null);
                        } else {
                            P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), P2pStateMachine.this.mSavedPeerConfig.wps.pin);
                        }
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupCreatedState);
                        return true;
                    case WifiP2pService.PEER_CONNECTION_USER_REJECT /* 143363 */:
                        P2pStateMachine.this.transitionTo(P2pStateMachine.this.mGroupCreatedState);
                        return true;
                    case WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT /* 147489 */:
                    case WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT /* 147491 */:
                    case WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT /* 147492 */:
                        return true;
                    default:
                        return false;
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
            }
        }

        /* loaded from: WifiP2pService$P2pStateMachine$OngoingGroupRemovalState.class */
        class OngoingGroupRemovalState extends State {
            OngoingGroupRemovalState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                switch (message.what) {
                    case WifiP2pManager.REMOVE_GROUP /* 139280 */:
                        P2pStateMachine.this.replyToMessage(message, WifiP2pManager.REMOVE_GROUP_SUCCEEDED);
                        return true;
                    default:
                        return false;
                }
            }
        }

        @Override // com.android.internal.util.StateMachine
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            super.dump(fd, pw, args);
            pw.println("mWifiP2pInfo " + this.mWifiP2pInfo);
            pw.println("mGroup " + this.mGroup);
            pw.println("mSavedPeerConfig " + this.mSavedPeerConfig);
            pw.println("mSavedP2pGroup " + this.mSavedP2pGroup);
            pw.println();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pStateChangedBroadcast(boolean enabled) {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            intent.addFlags(67108864);
            if (enabled) {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_STATE, 2);
            } else {
                intent.putExtra(WifiP2pManager.EXTRA_WIFI_STATE, 1);
            }
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pDiscoveryChangedBroadcast(boolean started) {
            if (WifiP2pService.this.mDiscoveryStarted == started) {
                return;
            }
            WifiP2pService.this.mDiscoveryStarted = started;
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, started ? 2 : 1);
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        private void sendThisDeviceChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            intent.addFlags(67108864);
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, new WifiP2pDevice(WifiP2pService.this.mThisDevice));
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendPeersChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            intent.putExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST, new WifiP2pDeviceList(this.mPeers));
            intent.addFlags(67108864);
            WifiP2pService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pConnectionChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            intent.addFlags(603979776);
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, new WifiP2pInfo(this.mWifiP2pInfo));
            intent.putExtra("networkInfo", new NetworkInfo(WifiP2pService.this.mNetworkInfo));
            intent.putExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP, new WifiP2pGroup(this.mGroup));
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.P2P_CONNECTION_CHANGED, new NetworkInfo(WifiP2pService.this.mNetworkInfo));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendP2pPersistentGroupsChangedBroadcast() {
            Intent intent = new Intent(WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION);
            intent.addFlags(67108864);
            WifiP2pService.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startDhcpServer(String intf) {
            try {
                InterfaceConfiguration ifcg = WifiP2pService.this.mNwService.getInterfaceConfig(intf);
                ifcg.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(WifiP2pService.SERVER_ADDRESS), 24));
                ifcg.setInterfaceUp();
                WifiP2pService.this.mNwService.setInterfaceConfig(intf, ifcg);
                WifiP2pService.this.mNwService.startTethering(WifiP2pService.DHCP_RANGE);
                logd("Started Dhcp server on " + intf);
            } catch (Exception e) {
                loge("Error configuring interface " + intf + ", :" + e);
            }
        }

        private void stopDhcpServer(String intf) {
            try {
                WifiP2pService.this.mNwService.stopTethering();
                logd("Stopped Dhcp server");
            } catch (Exception e) {
                loge("Error stopping Dhcp server" + e);
            }
        }

        private void notifyP2pEnableFailure() {
            Resources r = Resources.getSystem();
            AlertDialog dialog = new AlertDialog.Builder(WifiP2pService.this.mContext).setTitle(r.getString(R.string.wifi_p2p_dialog_title)).setMessage(r.getString(R.string.wifi_p2p_failed_message)).setPositiveButton(r.getString(17039370), (DialogInterface.OnClickListener) null).create();
            dialog.getWindow().setType(2003);
            dialog.show();
        }

        private void addRowToDialog(ViewGroup group, int stringId, String value) {
            Resources r = Resources.getSystem();
            View row = LayoutInflater.from(WifiP2pService.this.mContext).inflate(R.layout.wifi_p2p_dialog_row, group, false);
            ((TextView) row.findViewById(R.id.name)).setText(r.getString(stringId));
            ((TextView) row.findViewById(R.id.value)).setText(value);
            group.addView(row);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyInvitationSent(String pin, String peerAddress) {
            Resources r = Resources.getSystem();
            View textEntryView = LayoutInflater.from(WifiP2pService.this.mContext).inflate(R.layout.wifi_p2p_dialog, (ViewGroup) null);
            ViewGroup group = (ViewGroup) textEntryView.findViewById(R.id.info);
            addRowToDialog(group, R.string.wifi_p2p_to_message, getDeviceName(peerAddress));
            addRowToDialog(group, R.string.wifi_p2p_show_pin_message, pin);
            AlertDialog dialog = new AlertDialog.Builder(WifiP2pService.this.mContext).setTitle(r.getString(R.string.wifi_p2p_invitation_sent_title)).setView(textEntryView).setPositiveButton(r.getString(17039370), (DialogInterface.OnClickListener) null).create();
            dialog.getWindow().setType(2003);
            dialog.show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyInvitationReceived() {
            Resources r = Resources.getSystem();
            final WpsInfo wps = this.mSavedPeerConfig.wps;
            View textEntryView = LayoutInflater.from(WifiP2pService.this.mContext).inflate(R.layout.wifi_p2p_dialog, (ViewGroup) null);
            ViewGroup group = (ViewGroup) textEntryView.findViewById(R.id.info);
            addRowToDialog(group, R.string.wifi_p2p_from_message, getDeviceName(this.mSavedPeerConfig.deviceAddress));
            final EditText pin = (EditText) textEntryView.findViewById(R.id.wifi_p2p_wps_pin);
            AlertDialog dialog = new AlertDialog.Builder(WifiP2pService.this.mContext).setTitle(r.getString(R.string.wifi_p2p_invitation_to_connect_title)).setView(textEntryView).setPositiveButton(r.getString(R.string.accept), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    if (wps.setup == 2) {
                        P2pStateMachine.this.mSavedPeerConfig.wps.pin = pin.getText().toString();
                    }
                    P2pStateMachine.this.sendMessage(WifiP2pService.PEER_CONNECTION_USER_ACCEPT);
                }
            }).setNegativeButton(r.getString(R.string.decline), new DialogInterface.OnClickListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog2, int which) {
                    P2pStateMachine.this.sendMessage(WifiP2pService.PEER_CONNECTION_USER_REJECT);
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.2
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface arg0) {
                    P2pStateMachine.this.sendMessage(WifiP2pService.PEER_CONNECTION_USER_REJECT);
                }
            }).create();
            switch (wps.setup) {
                case 1:
                    addRowToDialog(group, R.string.wifi_p2p_show_pin_message, wps.pin);
                    break;
                case 2:
                    textEntryView.findViewById(R.id.enter_pin_section).setVisibility(0);
                    break;
            }
            if ((r.getConfiguration().uiMode & 5) == 5) {
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: android.net.wifi.p2p.WifiP2pService.P2pStateMachine.5
                    @Override // android.content.DialogInterface.OnKeyListener
                    public boolean onKey(DialogInterface dialog2, int keyCode, KeyEvent event) {
                        if (keyCode == 164) {
                            P2pStateMachine.this.sendMessage(WifiP2pService.PEER_CONNECTION_USER_ACCEPT);
                            dialog2.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
            }
            dialog.getWindow().setType(2003);
            dialog.show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updatePersistentNetworks(boolean reload) {
            String listStr = this.mWifiNative.listNetworks();
            if (listStr == null) {
                return;
            }
            boolean isSaveRequired = false;
            String[] lines = listStr.split(Separators.RETURN);
            if (lines == null) {
                return;
            }
            if (reload) {
                this.mGroups.clear();
            }
            for (int i = 1; i < lines.length; i++) {
                String[] result = lines[i].split(Separators.HT);
                if (result != null && result.length >= 4) {
                    String ssid = result[1];
                    String bssid = result[2];
                    String flags = result[3];
                    try {
                        int netId = Integer.parseInt(result[0]);
                        if (flags.indexOf("[CURRENT]") == -1) {
                            if (flags.indexOf("[P2P-PERSISTENT]") == -1) {
                                this.mWifiNative.removeNetwork(netId);
                                isSaveRequired = true;
                            } else if (!this.mGroups.contains(netId)) {
                                WifiP2pGroup group = new WifiP2pGroup();
                                group.setNetworkId(netId);
                                group.setNetworkName(ssid);
                                String mode = this.mWifiNative.getNetworkVariable(netId, "mode");
                                if (mode != null && mode.equals("3")) {
                                    group.setIsGroupOwner(true);
                                }
                                if (bssid.equalsIgnoreCase(WifiP2pService.this.mThisDevice.deviceAddress)) {
                                    group.setOwner(WifiP2pService.this.mThisDevice);
                                } else {
                                    WifiP2pDevice device = new WifiP2pDevice();
                                    device.deviceAddress = bssid;
                                    group.setOwner(device);
                                }
                                this.mGroups.add(group);
                                isSaveRequired = true;
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (reload || isSaveRequired) {
                this.mWifiNative.saveConfig();
                sendP2pPersistentGroupsChangedBroadcast();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isConfigInvalid(WifiP2pConfig config) {
            return config == null || TextUtils.isEmpty(config.deviceAddress) || this.mPeers.get(config.deviceAddress) == null;
        }

        private WifiP2pDevice fetchCurrentDeviceDetails(WifiP2pConfig config) {
            int gc = this.mWifiNative.getGroupCapability(config.deviceAddress);
            this.mPeers.updateGroupCapability(config.deviceAddress, gc);
            return this.mPeers.get(config.deviceAddress);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void p2pConnectWithPinDisplay(WifiP2pConfig config) {
            WifiP2pDevice dev = fetchCurrentDeviceDetails(config);
            String pin = this.mWifiNative.p2pConnect(config, dev.isGroupOwner());
            try {
                Integer.parseInt(pin);
                notifyInvitationSent(pin, config.deviceAddress);
            } catch (NumberFormatException e) {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean reinvokePersistentGroup(WifiP2pConfig config) {
            int netId;
            WifiP2pDevice dev = fetchCurrentDeviceDetails(config);
            boolean join = dev.isGroupOwner();
            String ssid = this.mWifiNative.p2pGetSsid(dev.deviceAddress);
            if (join && dev.isGroupLimit()) {
                join = false;
            } else if (join && (netId = this.mGroups.getNetworkId(dev.deviceAddress, ssid)) >= 0) {
                if (!this.mWifiNative.p2pGroupAdd(netId)) {
                    return false;
                }
                return true;
            }
            if (!join && dev.isDeviceLimit()) {
                loge("target device reaches the device limit.");
                return false;
            } else if (!join && dev.isInvitationCapable()) {
                int netId2 = -2;
                if (config.netId >= 0) {
                    if (config.deviceAddress.equals(this.mGroups.getOwnerAddr(config.netId))) {
                        netId2 = config.netId;
                    }
                } else {
                    netId2 = this.mGroups.getNetworkId(dev.deviceAddress);
                }
                if (netId2 < 0) {
                    netId2 = getNetworkIdFromClientList(dev.deviceAddress);
                }
                if (netId2 >= 0) {
                    if (this.mWifiNative.p2pReinvoke(netId2, dev.deviceAddress)) {
                        config.netId = netId2;
                        return true;
                    }
                    loge("p2pReinvoke() failed, update networks");
                    updatePersistentNetworks(WifiP2pService.RELOAD.booleanValue());
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }

        private int getNetworkIdFromClientList(String deviceAddress) {
            if (deviceAddress == null) {
                return -1;
            }
            Collection<WifiP2pGroup> groups = this.mGroups.getGroupList();
            for (WifiP2pGroup group : groups) {
                int netId = group.getNetworkId();
                String[] p2pClientList = getClientList(netId);
                if (p2pClientList != null) {
                    for (String client : p2pClientList) {
                        if (deviceAddress.equalsIgnoreCase(client)) {
                            return netId;
                        }
                    }
                    continue;
                }
            }
            return -1;
        }

        private String[] getClientList(int netId) {
            String p2pClients = this.mWifiNative.getNetworkVariable(netId, "p2p_client_list");
            if (p2pClients == null) {
                return null;
            }
            return p2pClients.split(Separators.SP);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean removeClientFromList(int netId, String addr, boolean isRemovable) {
            StringBuilder modifiedClientList = new StringBuilder();
            String[] currentClientList = getClientList(netId);
            boolean isClientRemoved = false;
            if (currentClientList != null) {
                for (String client : currentClientList) {
                    if (!client.equalsIgnoreCase(addr)) {
                        modifiedClientList.append(Separators.SP);
                        modifiedClientList.append(client);
                    } else {
                        isClientRemoved = true;
                    }
                }
            }
            if (modifiedClientList.length() == 0 && isRemovable) {
                this.mGroups.remove(netId);
                return true;
            } else if (!isClientRemoved) {
                return false;
            } else {
                if (modifiedClientList.length() == 0) {
                    modifiedClientList.append("\"\"");
                }
                this.mWifiNative.setNetworkVariable(netId, "p2p_client_list", modifiedClientList.toString());
                this.mWifiNative.saveConfig();
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWifiP2pInfoOnGroupFormation(InetAddress serverInetAddress) {
            this.mWifiP2pInfo.groupFormed = true;
            this.mWifiP2pInfo.isGroupOwner = this.mGroup.isGroupOwner();
            this.mWifiP2pInfo.groupOwnerAddress = serverInetAddress;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetWifiP2pInfo() {
            this.mWifiP2pInfo.groupFormed = false;
            this.mWifiP2pInfo.isGroupOwner = false;
            this.mWifiP2pInfo.groupOwnerAddress = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getDeviceName(String deviceAddress) {
            WifiP2pDevice d = this.mPeers.get(deviceAddress);
            if (d != null) {
                return d.deviceName;
            }
            return deviceAddress;
        }

        private String getPersistedDeviceName() {
            String deviceName = Settings.Global.getString(WifiP2pService.this.mContext.getContentResolver(), Settings.Global.WIFI_P2P_DEVICE_NAME);
            if (deviceName == null) {
                String id = Settings.Secure.getString(WifiP2pService.this.mContext.getContentResolver(), "android_id");
                return "Android_" + id.substring(0, 4);
            }
            return deviceName;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setAndPersistDeviceName(String devName) {
            if (devName == null) {
                return false;
            }
            if (this.mWifiNative.setDeviceName(devName)) {
                WifiP2pService.this.mThisDevice.deviceName = devName;
                this.mWifiNative.setP2pSsidPostfix("-" + WifiP2pService.this.mThisDevice.deviceName);
                Settings.Global.putString(WifiP2pService.this.mContext.getContentResolver(), Settings.Global.WIFI_P2P_DEVICE_NAME, devName);
                sendThisDeviceChangedBroadcast();
                return true;
            }
            loge("Failed to set device name " + devName);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setWfdInfo(WifiP2pWfdInfo wfdInfo) {
            boolean success;
            if (!wfdInfo.isWfdEnabled()) {
                success = this.mWifiNative.setWfdEnable(false);
            } else {
                success = this.mWifiNative.setWfdEnable(true) && this.mWifiNative.setWfdDeviceInfo(wfdInfo.getDeviceInfoHex());
            }
            if (success) {
                WifiP2pService.this.mThisDevice.wfdInfo = wfdInfo;
                sendThisDeviceChangedBroadcast();
                return true;
            }
            loge("Failed to set wfd properties");
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initializeP2pSettings() {
            this.mWifiNative.setPersistentReconnect(true);
            WifiP2pService.this.mThisDevice.deviceName = getPersistedDeviceName();
            this.mWifiNative.setDeviceName(WifiP2pService.this.mThisDevice.deviceName);
            this.mWifiNative.setP2pSsidPostfix("-" + WifiP2pService.this.mThisDevice.deviceName);
            this.mWifiNative.setDeviceType(WifiP2pService.this.mThisDevice.primaryDeviceType);
            this.mWifiNative.setConfigMethods("virtual_push_button physical_display keypad");
            this.mWifiNative.setConcurrencyPriority("sta");
            WifiP2pService.this.mThisDevice.deviceAddress = this.mWifiNative.p2pGetDeviceAddress();
            updateThisDevice(3);
            WifiP2pService.this.mClientInfoList.clear();
            this.mWifiNative.p2pFlush();
            this.mWifiNative.p2pServiceFlush();
            WifiP2pService.this.mServiceTransactionId = (byte) 0;
            WifiP2pService.this.mServiceDiscReqId = null;
            String countryCode = Settings.Global.getString(WifiP2pService.this.mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE);
            if (countryCode != null && !countryCode.isEmpty()) {
                WifiP2pService.this.mP2pStateMachine.sendMessage(WifiP2pService.SET_COUNTRY_CODE, countryCode);
            }
            updatePersistentNetworks(WifiP2pService.RELOAD.booleanValue());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateThisDevice(int status) {
            WifiP2pService.this.mThisDevice.status = status;
            sendThisDeviceChangedBroadcast();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleGroupCreationFailure() {
            resetWifiP2pInfo();
            WifiP2pService.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.FAILED, null, null);
            sendP2pConnectionChangedBroadcast();
            boolean peersChanged = this.mPeers.remove(this.mPeersLostDuringConnection);
            if (this.mPeers.remove(this.mSavedPeerConfig.deviceAddress) != null) {
                peersChanged = true;
            }
            if (peersChanged) {
                sendPeersChangedBroadcast();
            }
            this.mPeersLostDuringConnection.clear();
            WifiP2pService.this.mServiceDiscReqId = null;
            sendMessage(WifiP2pManager.DISCOVER_PEERS);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleGroupRemoved() {
            if (!this.mGroup.isGroupOwner()) {
                WifiP2pService.this.mDhcpStateMachine.sendMessage(DhcpStateMachine.CMD_STOP_DHCP);
                WifiP2pService.this.mDhcpStateMachine.doQuit();
                WifiP2pService.this.mDhcpStateMachine = null;
            } else {
                stopDhcpServer(this.mGroup.getInterface());
            }
            try {
                WifiP2pService.this.mNwService.clearInterfaceAddresses(this.mGroup.getInterface());
            } catch (Exception e) {
                loge("Failed to clear addresses " + e);
            }
            NetworkUtils.resetConnections(this.mGroup.getInterface(), 3);
            this.mWifiNative.setP2pGroupIdle(this.mGroup.getInterface(), 0);
            boolean peersChanged = false;
            for (WifiP2pDevice d : this.mGroup.getClientList()) {
                if (this.mPeers.remove(d)) {
                    peersChanged = true;
                }
            }
            if (this.mPeers.remove(this.mGroup.getOwner())) {
                peersChanged = true;
            }
            if (this.mPeers.remove(this.mPeersLostDuringConnection)) {
                peersChanged = true;
            }
            if (peersChanged) {
                sendPeersChangedBroadcast();
            }
            this.mGroup = null;
            this.mPeersLostDuringConnection.clear();
            WifiP2pService.this.mServiceDiscReqId = null;
            if (WifiP2pService.this.mTempoarilyDisconnectedWifi) {
                WifiP2pService.this.mWifiChannel.sendMessage(WifiP2pService.DISCONNECT_WIFI_REQUEST, 0);
                WifiP2pService.this.mTempoarilyDisconnectedWifi = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message msg, int what) {
            if (msg.replyTo == null) {
                return;
            }
            Message dstMsg = obtainMessage(msg);
            dstMsg.what = what;
            WifiP2pService.this.mReplyChannel.replyToMessage(msg, dstMsg);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message msg, int what, int arg1) {
            if (msg.replyTo == null) {
                return;
            }
            Message dstMsg = obtainMessage(msg);
            dstMsg.what = what;
            dstMsg.arg1 = arg1;
            WifiP2pService.this.mReplyChannel.replyToMessage(msg, dstMsg);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void replyToMessage(Message msg, int what, Object obj) {
            if (msg.replyTo == null) {
                return;
            }
            Message dstMsg = obtainMessage(msg);
            dstMsg.what = what;
            dstMsg.obj = obj;
            WifiP2pService.this.mReplyChannel.replyToMessage(msg, dstMsg);
        }

        private Message obtainMessage(Message srcMsg) {
            Message msg = Message.obtain();
            msg.arg2 = srcMsg.arg2;
            return msg;
        }

        @Override // com.android.internal.util.StateMachine
        protected void logd(String s) {
            Slog.d(WifiP2pService.TAG, s);
        }

        @Override // com.android.internal.util.StateMachine
        protected void loge(String s) {
            Slog.e(WifiP2pService.TAG, s);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean updateSupplicantServiceRequest() {
            clearSupplicantServiceRequest();
            StringBuffer sb = new StringBuffer();
            for (ClientInfo c : WifiP2pService.this.mClientInfoList.values()) {
                for (int i = 0; i < c.mReqList.size(); i++) {
                    WifiP2pServiceRequest req = (WifiP2pServiceRequest) c.mReqList.valueAt(i);
                    if (req != null) {
                        sb.append(req.getSupplicantQuery());
                    }
                }
            }
            if (sb.length() == 0) {
                return false;
            }
            WifiP2pService.this.mServiceDiscReqId = this.mWifiNative.p2pServDiscReq("00:00:00:00:00:00", sb.toString());
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return false;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSupplicantServiceRequest() {
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return;
            }
            this.mWifiNative.p2pServDiscCancelReq(WifiP2pService.this.mServiceDiscReqId);
            WifiP2pService.this.mServiceDiscReqId = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addServiceRequest(Messenger m, WifiP2pServiceRequest req) {
            clearClientDeadChannels();
            ClientInfo clientInfo = getClientInfo(m, true);
            if (clientInfo == null) {
                return false;
            }
            WifiP2pService.access$11904(WifiP2pService.this);
            if (WifiP2pService.this.mServiceTransactionId == 0) {
                WifiP2pService.access$11904(WifiP2pService.this);
            }
            req.setTransactionId(WifiP2pService.this.mServiceTransactionId);
            clientInfo.mReqList.put(WifiP2pService.this.mServiceTransactionId, req);
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return true;
            }
            return updateSupplicantServiceRequest();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeServiceRequest(Messenger m, WifiP2pServiceRequest req) {
            ClientInfo clientInfo = getClientInfo(m, false);
            if (clientInfo == null) {
                return;
            }
            boolean removed = false;
            int i = 0;
            while (true) {
                if (i >= clientInfo.mReqList.size()) {
                    break;
                } else if (!req.equals(clientInfo.mReqList.valueAt(i))) {
                    i++;
                } else {
                    removed = true;
                    clientInfo.mReqList.removeAt(i);
                    break;
                }
            }
            if (removed) {
                if (clientInfo.mReqList.size() == 0 && clientInfo.mServList.size() == 0) {
                    WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
                }
                if (WifiP2pService.this.mServiceDiscReqId == null) {
                    return;
                }
                updateSupplicantServiceRequest();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearServiceRequests(Messenger m) {
            ClientInfo clientInfo = getClientInfo(m, false);
            if (clientInfo == null || clientInfo.mReqList.size() == 0) {
                return;
            }
            clientInfo.mReqList.clear();
            if (clientInfo.mServList.size() == 0) {
                WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
            }
            if (WifiP2pService.this.mServiceDiscReqId == null) {
                return;
            }
            updateSupplicantServiceRequest();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean addLocalService(Messenger m, WifiP2pServiceInfo servInfo) {
            clearClientDeadChannels();
            ClientInfo clientInfo = getClientInfo(m, true);
            if (clientInfo == null || !clientInfo.mServList.add(servInfo)) {
                return false;
            }
            if (this.mWifiNative.p2pServiceAdd(servInfo)) {
                return true;
            }
            clientInfo.mServList.remove(servInfo);
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeLocalService(Messenger m, WifiP2pServiceInfo servInfo) {
            ClientInfo clientInfo = getClientInfo(m, false);
            if (clientInfo == null) {
                return;
            }
            this.mWifiNative.p2pServiceDel(servInfo);
            clientInfo.mServList.remove(servInfo);
            if (clientInfo.mReqList.size() != 0 || clientInfo.mServList.size() != 0) {
                return;
            }
            WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearLocalServices(Messenger m) {
            ClientInfo clientInfo = getClientInfo(m, false);
            if (clientInfo == null) {
                return;
            }
            for (WifiP2pServiceInfo servInfo : clientInfo.mServList) {
                this.mWifiNative.p2pServiceDel(servInfo);
            }
            clientInfo.mServList.clear();
            if (clientInfo.mReqList.size() != 0) {
                return;
            }
            WifiP2pService.this.mClientInfoList.remove(clientInfo.mMessenger);
        }

        private void clearClientInfo(Messenger m) {
            clearLocalServices(m);
            clearServiceRequests(m);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendServiceResponse(WifiP2pServiceResponse resp) {
            for (ClientInfo c : WifiP2pService.this.mClientInfoList.values()) {
                WifiP2pServiceRequest req = (WifiP2pServiceRequest) c.mReqList.get(resp.getTransactionId());
                if (req != null) {
                    Message msg = Message.obtain();
                    msg.what = WifiP2pManager.RESPONSE_SERVICE;
                    msg.arg1 = 0;
                    msg.arg2 = 0;
                    msg.obj = resp;
                    try {
                        c.mMessenger.send(msg);
                    } catch (RemoteException e) {
                        clearClientInfo(c.mMessenger);
                        return;
                    }
                }
            }
        }

        private void clearClientDeadChannels() {
            ArrayList<Messenger> deadClients = new ArrayList<>();
            for (ClientInfo c : WifiP2pService.this.mClientInfoList.values()) {
                Message msg = Message.obtain();
                msg.what = WifiP2pManager.PING;
                msg.arg1 = 0;
                msg.arg2 = 0;
                msg.obj = null;
                try {
                    c.mMessenger.send(msg);
                } catch (RemoteException e) {
                    deadClients.add(c.mMessenger);
                }
            }
            Iterator i$ = deadClients.iterator();
            while (i$.hasNext()) {
                Messenger m = i$.next();
                clearClientInfo(m);
            }
        }

        private ClientInfo getClientInfo(Messenger m, boolean createIfNotExist) {
            ClientInfo clientInfo = (ClientInfo) WifiP2pService.this.mClientInfoList.get(m);
            if (clientInfo == null && createIfNotExist) {
                clientInfo = new ClientInfo(m);
                WifiP2pService.this.mClientInfoList.put(m, clientInfo);
            }
            return clientInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiP2pService$ClientInfo.class */
    public class ClientInfo {
        private Messenger mMessenger;
        private SparseArray<WifiP2pServiceRequest> mReqList;
        private List<WifiP2pServiceInfo> mServList;

        private ClientInfo(Messenger m) {
            this.mMessenger = m;
            this.mReqList = new SparseArray<>();
            this.mServList = new ArrayList();
        }
    }
}