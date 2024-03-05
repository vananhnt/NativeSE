package android.net.wifi.p2p;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceResponse;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceResponse;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: WifiP2pManager.class */
public class WifiP2pManager {
    private static final String TAG = "WifiP2pManager";
    public static final String WIFI_P2P_STATE_CHANGED_ACTION = "android.net.wifi.p2p.STATE_CHANGED";
    public static final String EXTRA_WIFI_STATE = "wifi_p2p_state";
    public static final int WIFI_P2P_STATE_DISABLED = 1;
    public static final int WIFI_P2P_STATE_ENABLED = 2;
    public static final String WIFI_P2P_CONNECTION_CHANGED_ACTION = "android.net.wifi.p2p.CONNECTION_STATE_CHANGE";
    public static final String EXTRA_WIFI_P2P_INFO = "wifiP2pInfo";
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_WIFI_P2P_GROUP = "p2pGroupInfo";
    public static final String WIFI_P2P_PEERS_CHANGED_ACTION = "android.net.wifi.p2p.PEERS_CHANGED";
    public static final String EXTRA_P2P_DEVICE_LIST = "wifiP2pDeviceList";
    public static final String WIFI_P2P_DISCOVERY_CHANGED_ACTION = "android.net.wifi.p2p.DISCOVERY_STATE_CHANGE";
    public static final String EXTRA_DISCOVERY_STATE = "discoveryState";
    public static final int WIFI_P2P_DISCOVERY_STOPPED = 1;
    public static final int WIFI_P2P_DISCOVERY_STARTED = 2;
    public static final String WIFI_P2P_THIS_DEVICE_CHANGED_ACTION = "android.net.wifi.p2p.THIS_DEVICE_CHANGED";
    public static final String EXTRA_WIFI_P2P_DEVICE = "wifiP2pDevice";
    public static final String WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION = "android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED";
    IWifiP2pManager mService;
    private static final int BASE = 139264;
    public static final int DISCOVER_PEERS = 139265;
    public static final int DISCOVER_PEERS_FAILED = 139266;
    public static final int DISCOVER_PEERS_SUCCEEDED = 139267;
    public static final int STOP_DISCOVERY = 139268;
    public static final int STOP_DISCOVERY_FAILED = 139269;
    public static final int STOP_DISCOVERY_SUCCEEDED = 139270;
    public static final int CONNECT = 139271;
    public static final int CONNECT_FAILED = 139272;
    public static final int CONNECT_SUCCEEDED = 139273;
    public static final int CANCEL_CONNECT = 139274;
    public static final int CANCEL_CONNECT_FAILED = 139275;
    public static final int CANCEL_CONNECT_SUCCEEDED = 139276;
    public static final int CREATE_GROUP = 139277;
    public static final int CREATE_GROUP_FAILED = 139278;
    public static final int CREATE_GROUP_SUCCEEDED = 139279;
    public static final int REMOVE_GROUP = 139280;
    public static final int REMOVE_GROUP_FAILED = 139281;
    public static final int REMOVE_GROUP_SUCCEEDED = 139282;
    public static final int REQUEST_PEERS = 139283;
    public static final int RESPONSE_PEERS = 139284;
    public static final int REQUEST_CONNECTION_INFO = 139285;
    public static final int RESPONSE_CONNECTION_INFO = 139286;
    public static final int REQUEST_GROUP_INFO = 139287;
    public static final int RESPONSE_GROUP_INFO = 139288;
    public static final int ADD_LOCAL_SERVICE = 139292;
    public static final int ADD_LOCAL_SERVICE_FAILED = 139293;
    public static final int ADD_LOCAL_SERVICE_SUCCEEDED = 139294;
    public static final int REMOVE_LOCAL_SERVICE = 139295;
    public static final int REMOVE_LOCAL_SERVICE_FAILED = 139296;
    public static final int REMOVE_LOCAL_SERVICE_SUCCEEDED = 139297;
    public static final int CLEAR_LOCAL_SERVICES = 139298;
    public static final int CLEAR_LOCAL_SERVICES_FAILED = 139299;
    public static final int CLEAR_LOCAL_SERVICES_SUCCEEDED = 139300;
    public static final int ADD_SERVICE_REQUEST = 139301;
    public static final int ADD_SERVICE_REQUEST_FAILED = 139302;
    public static final int ADD_SERVICE_REQUEST_SUCCEEDED = 139303;
    public static final int REMOVE_SERVICE_REQUEST = 139304;
    public static final int REMOVE_SERVICE_REQUEST_FAILED = 139305;
    public static final int REMOVE_SERVICE_REQUEST_SUCCEEDED = 139306;
    public static final int CLEAR_SERVICE_REQUESTS = 139307;
    public static final int CLEAR_SERVICE_REQUESTS_FAILED = 139308;
    public static final int CLEAR_SERVICE_REQUESTS_SUCCEEDED = 139309;
    public static final int DISCOVER_SERVICES = 139310;
    public static final int DISCOVER_SERVICES_FAILED = 139311;
    public static final int DISCOVER_SERVICES_SUCCEEDED = 139312;
    public static final int PING = 139313;
    public static final int RESPONSE_SERVICE = 139314;
    public static final int SET_DEVICE_NAME = 139315;
    public static final int SET_DEVICE_NAME_FAILED = 139316;
    public static final int SET_DEVICE_NAME_SUCCEEDED = 139317;
    public static final int DELETE_PERSISTENT_GROUP = 139318;
    public static final int DELETE_PERSISTENT_GROUP_FAILED = 139319;
    public static final int DELETE_PERSISTENT_GROUP_SUCCEEDED = 139320;
    public static final int REQUEST_PERSISTENT_GROUP_INFO = 139321;
    public static final int RESPONSE_PERSISTENT_GROUP_INFO = 139322;
    public static final int SET_WFD_INFO = 139323;
    public static final int SET_WFD_INFO_FAILED = 139324;
    public static final int SET_WFD_INFO_SUCCEEDED = 139325;
    public static final int START_WPS = 139326;
    public static final int START_WPS_FAILED = 139327;
    public static final int START_WPS_SUCCEEDED = 139328;
    public static final int START_LISTEN = 139329;
    public static final int START_LISTEN_FAILED = 139330;
    public static final int START_LISTEN_SUCCEEDED = 139331;
    public static final int STOP_LISTEN = 139332;
    public static final int STOP_LISTEN_FAILED = 139333;
    public static final int STOP_LISTEN_SUCCEEDED = 139334;
    public static final int SET_CHANNEL = 139335;
    public static final int SET_CHANNEL_FAILED = 139336;
    public static final int SET_CHANNEL_SUCCEEDED = 139337;
    public static final int ERROR = 0;
    public static final int P2P_UNSUPPORTED = 1;
    public static final int BUSY = 2;
    public static final int NO_SERVICE_REQUESTS = 3;
    public static final int MIRACAST_DISABLED = 0;
    public static final int MIRACAST_SOURCE = 1;
    public static final int MIRACAST_SINK = 2;

    /* loaded from: WifiP2pManager$ActionListener.class */
    public interface ActionListener {
        void onSuccess();

        void onFailure(int i);
    }

    /* loaded from: WifiP2pManager$ChannelListener.class */
    public interface ChannelListener {
        void onChannelDisconnected();
    }

    /* loaded from: WifiP2pManager$ConnectionInfoListener.class */
    public interface ConnectionInfoListener {
        void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo);
    }

    /* loaded from: WifiP2pManager$DnsSdServiceResponseListener.class */
    public interface DnsSdServiceResponseListener {
        void onDnsSdServiceAvailable(String str, String str2, WifiP2pDevice wifiP2pDevice);
    }

    /* loaded from: WifiP2pManager$DnsSdTxtRecordListener.class */
    public interface DnsSdTxtRecordListener {
        void onDnsSdTxtRecordAvailable(String str, Map<String, String> map, WifiP2pDevice wifiP2pDevice);
    }

    /* loaded from: WifiP2pManager$GroupInfoListener.class */
    public interface GroupInfoListener {
        void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup);
    }

    /* loaded from: WifiP2pManager$PeerListListener.class */
    public interface PeerListListener {
        void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList);
    }

    /* loaded from: WifiP2pManager$PersistentGroupInfoListener.class */
    public interface PersistentGroupInfoListener {
        void onPersistentGroupInfoAvailable(WifiP2pGroupList wifiP2pGroupList);
    }

    /* loaded from: WifiP2pManager$ServiceResponseListener.class */
    public interface ServiceResponseListener {
        void onServiceAvailable(int i, byte[] bArr, WifiP2pDevice wifiP2pDevice);
    }

    /* loaded from: WifiP2pManager$UpnpServiceResponseListener.class */
    public interface UpnpServiceResponseListener {
        void onUpnpServiceAvailable(List<String> list, WifiP2pDevice wifiP2pDevice);
    }

    public WifiP2pManager(IWifiP2pManager service) {
        this.mService = service;
    }

    /* loaded from: WifiP2pManager$Channel.class */
    public static class Channel {
        private static final int INVALID_LISTENER_KEY = 0;
        private ChannelListener mChannelListener;
        private ServiceResponseListener mServRspListener;
        private DnsSdServiceResponseListener mDnsSdServRspListener;
        private DnsSdTxtRecordListener mDnsSdTxtListener;
        private UpnpServiceResponseListener mUpnpServRspListener;
        private HashMap<Integer, Object> mListenerMap = new HashMap<>();
        private Object mListenerMapLock = new Object();
        private int mListenerKey = 0;
        private AsyncChannel mAsyncChannel = new AsyncChannel();
        private P2pHandler mHandler;
        Context mContext;

        Channel(Context context, Looper looper, ChannelListener l) {
            this.mHandler = new P2pHandler(looper);
            this.mChannelListener = l;
            this.mContext = context;
        }

        /* loaded from: WifiP2pManager$Channel$P2pHandler.class */
        class P2pHandler extends Handler {
            P2pHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Object listener = Channel.this.getListener(message.arg2);
                switch (message.what) {
                    case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                        if (Channel.this.mChannelListener != null) {
                            Channel.this.mChannelListener.onChannelDisconnected();
                            Channel.this.mChannelListener = null;
                            return;
                        }
                        return;
                    case WifiP2pManager.DISCOVER_PEERS_FAILED /* 139266 */:
                    case WifiP2pManager.STOP_DISCOVERY_FAILED /* 139269 */:
                    case WifiP2pManager.CONNECT_FAILED /* 139272 */:
                    case WifiP2pManager.CANCEL_CONNECT_FAILED /* 139275 */:
                    case WifiP2pManager.CREATE_GROUP_FAILED /* 139278 */:
                    case WifiP2pManager.REMOVE_GROUP_FAILED /* 139281 */:
                    case WifiP2pManager.ADD_LOCAL_SERVICE_FAILED /* 139293 */:
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE_FAILED /* 139296 */:
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES_FAILED /* 139299 */:
                    case WifiP2pManager.ADD_SERVICE_REQUEST_FAILED /* 139302 */:
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST_FAILED /* 139305 */:
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS_FAILED /* 139308 */:
                    case WifiP2pManager.DISCOVER_SERVICES_FAILED /* 139311 */:
                    case WifiP2pManager.SET_DEVICE_NAME_FAILED /* 139316 */:
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP_FAILED /* 139319 */:
                    case WifiP2pManager.SET_WFD_INFO_FAILED /* 139324 */:
                    case WifiP2pManager.START_WPS_FAILED /* 139327 */:
                    case WifiP2pManager.START_LISTEN_FAILED /* 139330 */:
                    case WifiP2pManager.STOP_LISTEN_FAILED /* 139333 */:
                    case WifiP2pManager.SET_CHANNEL_FAILED /* 139336 */:
                        if (listener != null) {
                            ((ActionListener) listener).onFailure(message.arg1);
                            return;
                        }
                        return;
                    case WifiP2pManager.DISCOVER_PEERS_SUCCEEDED /* 139267 */:
                    case WifiP2pManager.STOP_DISCOVERY_SUCCEEDED /* 139270 */:
                    case WifiP2pManager.CONNECT_SUCCEEDED /* 139273 */:
                    case WifiP2pManager.CANCEL_CONNECT_SUCCEEDED /* 139276 */:
                    case WifiP2pManager.CREATE_GROUP_SUCCEEDED /* 139279 */:
                    case WifiP2pManager.REMOVE_GROUP_SUCCEEDED /* 139282 */:
                    case WifiP2pManager.ADD_LOCAL_SERVICE_SUCCEEDED /* 139294 */:
                    case WifiP2pManager.REMOVE_LOCAL_SERVICE_SUCCEEDED /* 139297 */:
                    case WifiP2pManager.CLEAR_LOCAL_SERVICES_SUCCEEDED /* 139300 */:
                    case WifiP2pManager.ADD_SERVICE_REQUEST_SUCCEEDED /* 139303 */:
                    case WifiP2pManager.REMOVE_SERVICE_REQUEST_SUCCEEDED /* 139306 */:
                    case WifiP2pManager.CLEAR_SERVICE_REQUESTS_SUCCEEDED /* 139309 */:
                    case WifiP2pManager.DISCOVER_SERVICES_SUCCEEDED /* 139312 */:
                    case WifiP2pManager.SET_DEVICE_NAME_SUCCEEDED /* 139317 */:
                    case WifiP2pManager.DELETE_PERSISTENT_GROUP_SUCCEEDED /* 139320 */:
                    case WifiP2pManager.SET_WFD_INFO_SUCCEEDED /* 139325 */:
                    case WifiP2pManager.START_WPS_SUCCEEDED /* 139328 */:
                    case WifiP2pManager.START_LISTEN_SUCCEEDED /* 139331 */:
                    case WifiP2pManager.STOP_LISTEN_SUCCEEDED /* 139334 */:
                    case WifiP2pManager.SET_CHANNEL_SUCCEEDED /* 139337 */:
                        if (listener != null) {
                            ((ActionListener) listener).onSuccess();
                            return;
                        }
                        return;
                    case WifiP2pManager.RESPONSE_PEERS /* 139284 */:
                        WifiP2pDeviceList peers = (WifiP2pDeviceList) message.obj;
                        if (listener != null) {
                            ((PeerListListener) listener).onPeersAvailable(peers);
                            return;
                        }
                        return;
                    case WifiP2pManager.RESPONSE_CONNECTION_INFO /* 139286 */:
                        WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) message.obj;
                        if (listener != null) {
                            ((ConnectionInfoListener) listener).onConnectionInfoAvailable(wifiP2pInfo);
                            return;
                        }
                        return;
                    case WifiP2pManager.RESPONSE_GROUP_INFO /* 139288 */:
                        WifiP2pGroup group = (WifiP2pGroup) message.obj;
                        if (listener != null) {
                            ((GroupInfoListener) listener).onGroupInfoAvailable(group);
                            return;
                        }
                        return;
                    case WifiP2pManager.RESPONSE_SERVICE /* 139314 */:
                        WifiP2pServiceResponse resp = (WifiP2pServiceResponse) message.obj;
                        Channel.this.handleServiceResponse(resp);
                        return;
                    case WifiP2pManager.RESPONSE_PERSISTENT_GROUP_INFO /* 139322 */:
                        WifiP2pGroupList groups = (WifiP2pGroupList) message.obj;
                        if (listener != null) {
                            ((PersistentGroupInfoListener) listener).onPersistentGroupInfoAvailable(groups);
                            return;
                        }
                        return;
                    default:
                        Log.d(WifiP2pManager.TAG, "Ignored " + message);
                        return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleServiceResponse(WifiP2pServiceResponse resp) {
            if (resp instanceof WifiP2pDnsSdServiceResponse) {
                handleDnsSdServiceResponse((WifiP2pDnsSdServiceResponse) resp);
            } else if (resp instanceof WifiP2pUpnpServiceResponse) {
                if (this.mUpnpServRspListener != null) {
                    handleUpnpServiceResponse((WifiP2pUpnpServiceResponse) resp);
                }
            } else if (this.mServRspListener != null) {
                this.mServRspListener.onServiceAvailable(resp.getServiceType(), resp.getRawData(), resp.getSrcDevice());
            }
        }

        private void handleUpnpServiceResponse(WifiP2pUpnpServiceResponse resp) {
            this.mUpnpServRspListener.onUpnpServiceAvailable(resp.getUniqueServiceNames(), resp.getSrcDevice());
        }

        private void handleDnsSdServiceResponse(WifiP2pDnsSdServiceResponse resp) {
            if (resp.getDnsType() == 12) {
                if (this.mDnsSdServRspListener != null) {
                    this.mDnsSdServRspListener.onDnsSdServiceAvailable(resp.getInstanceName(), resp.getDnsQueryName(), resp.getSrcDevice());
                }
            } else if (resp.getDnsType() == 16) {
                if (this.mDnsSdTxtListener != null) {
                    this.mDnsSdTxtListener.onDnsSdTxtRecordAvailable(resp.getDnsQueryName(), resp.getTxtRecord(), resp.getSrcDevice());
                }
            } else {
                Log.e(WifiP2pManager.TAG, "Unhandled resp " + resp);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int putListener(Object listener) {
            int key;
            if (listener == null) {
                return 0;
            }
            synchronized (this.mListenerMapLock) {
                do {
                    key = this.mListenerKey;
                    this.mListenerKey = key + 1;
                } while (key == 0);
                this.mListenerMap.put(Integer.valueOf(key), listener);
            }
            return key;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Object getListener(int key) {
            Object remove;
            if (key == 0) {
                return null;
            }
            synchronized (this.mListenerMapLock) {
                remove = this.mListenerMap.remove(Integer.valueOf(key));
            }
            return remove;
        }
    }

    private static void checkChannel(Channel c) {
        if (c == null) {
            throw new IllegalArgumentException("Channel needs to be initialized");
        }
    }

    private static void checkServiceInfo(WifiP2pServiceInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("service info is null");
        }
    }

    private static void checkServiceRequest(WifiP2pServiceRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("service request is null");
        }
    }

    private static void checkP2pConfig(WifiP2pConfig c) {
        if (c == null) {
            throw new IllegalArgumentException("config cannot be null");
        }
        if (TextUtils.isEmpty(c.deviceAddress)) {
            throw new IllegalArgumentException("deviceAddress cannot be empty");
        }
    }

    public Channel initialize(Context srcContext, Looper srcLooper, ChannelListener listener) {
        Messenger messenger = getMessenger();
        if (messenger == null) {
            return null;
        }
        Channel c = new Channel(srcContext, srcLooper, listener);
        if (c.mAsyncChannel.connectSync(srcContext, c.mHandler, messenger) == 0) {
            return c;
        }
        return null;
    }

    public void discoverPeers(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(DISCOVER_PEERS, 0, c.putListener(listener));
    }

    public void stopPeerDiscovery(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(STOP_DISCOVERY, 0, c.putListener(listener));
    }

    public void connect(Channel c, WifiP2pConfig config, ActionListener listener) {
        checkChannel(c);
        checkP2pConfig(config);
        c.mAsyncChannel.sendMessage(CONNECT, 0, c.putListener(listener), config);
    }

    public void cancelConnect(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(CANCEL_CONNECT, 0, c.putListener(listener));
    }

    public void createGroup(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(CREATE_GROUP, -2, c.putListener(listener));
    }

    public void removeGroup(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REMOVE_GROUP, 0, c.putListener(listener));
    }

    public void listen(Channel c, boolean enable, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(enable ? START_LISTEN : STOP_LISTEN, 0, c.putListener(listener));
    }

    public void setWifiP2pChannels(Channel c, int lc, int oc, ActionListener listener) {
        checkChannel(c);
        Bundle p2pChannels = new Bundle();
        p2pChannels.putInt("lc", lc);
        p2pChannels.putInt("oc", oc);
        c.mAsyncChannel.sendMessage(SET_CHANNEL, 0, c.putListener(listener), p2pChannels);
    }

    public void startWps(Channel c, WpsInfo wps, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(START_WPS, 0, c.putListener(listener), wps);
    }

    public void addLocalService(Channel c, WifiP2pServiceInfo servInfo, ActionListener listener) {
        checkChannel(c);
        checkServiceInfo(servInfo);
        c.mAsyncChannel.sendMessage(ADD_LOCAL_SERVICE, 0, c.putListener(listener), servInfo);
    }

    public void removeLocalService(Channel c, WifiP2pServiceInfo servInfo, ActionListener listener) {
        checkChannel(c);
        checkServiceInfo(servInfo);
        c.mAsyncChannel.sendMessage(REMOVE_LOCAL_SERVICE, 0, c.putListener(listener), servInfo);
    }

    public void clearLocalServices(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(CLEAR_LOCAL_SERVICES, 0, c.putListener(listener));
    }

    public void setServiceResponseListener(Channel c, ServiceResponseListener listener) {
        checkChannel(c);
        c.mServRspListener = listener;
    }

    public void setDnsSdResponseListeners(Channel c, DnsSdServiceResponseListener servListener, DnsSdTxtRecordListener txtListener) {
        checkChannel(c);
        c.mDnsSdServRspListener = servListener;
        c.mDnsSdTxtListener = txtListener;
    }

    public void setUpnpServiceResponseListener(Channel c, UpnpServiceResponseListener listener) {
        checkChannel(c);
        c.mUpnpServRspListener = listener;
    }

    public void discoverServices(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(DISCOVER_SERVICES, 0, c.putListener(listener));
    }

    public void addServiceRequest(Channel c, WifiP2pServiceRequest req, ActionListener listener) {
        checkChannel(c);
        checkServiceRequest(req);
        c.mAsyncChannel.sendMessage(ADD_SERVICE_REQUEST, 0, c.putListener(listener), req);
    }

    public void removeServiceRequest(Channel c, WifiP2pServiceRequest req, ActionListener listener) {
        checkChannel(c);
        checkServiceRequest(req);
        c.mAsyncChannel.sendMessage(REMOVE_SERVICE_REQUEST, 0, c.putListener(listener), req);
    }

    public void clearServiceRequests(Channel c, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(CLEAR_SERVICE_REQUESTS, 0, c.putListener(listener));
    }

    public void requestPeers(Channel c, PeerListListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_PEERS, 0, c.putListener(listener));
    }

    public void requestConnectionInfo(Channel c, ConnectionInfoListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_CONNECTION_INFO, 0, c.putListener(listener));
    }

    public void requestGroupInfo(Channel c, GroupInfoListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_GROUP_INFO, 0, c.putListener(listener));
    }

    public void setDeviceName(Channel c, String devName, ActionListener listener) {
        checkChannel(c);
        WifiP2pDevice d = new WifiP2pDevice();
        d.deviceName = devName;
        c.mAsyncChannel.sendMessage(SET_DEVICE_NAME, 0, c.putListener(listener), d);
    }

    public void setWFDInfo(Channel c, WifiP2pWfdInfo wfdInfo, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(SET_WFD_INFO, 0, c.putListener(listener), wfdInfo);
    }

    public void deletePersistentGroup(Channel c, int netId, ActionListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(DELETE_PERSISTENT_GROUP, netId, c.putListener(listener));
    }

    public void requestPersistentGroupInfo(Channel c, PersistentGroupInfoListener listener) {
        checkChannel(c);
        c.mAsyncChannel.sendMessage(REQUEST_PERSISTENT_GROUP_INFO, 0, c.putListener(listener));
    }

    public void setMiracastMode(int mode) {
        try {
            this.mService.setMiracastMode(mode);
        } catch (RemoteException e) {
        }
    }

    public Messenger getMessenger() {
        try {
            return this.mService.getMessenger();
        } catch (RemoteException e) {
            return null;
        }
    }
}