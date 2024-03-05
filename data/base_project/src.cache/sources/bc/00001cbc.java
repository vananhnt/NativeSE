package com.android.server;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.nsd.DnsSdTxtRecord;
import android.net.nsd.INsdManager;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Binder;
import android.os.Message;
import android.os.Messenger;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/* loaded from: NsdService.class */
public class NsdService extends INsdManager.Stub {
    private static final String TAG = "NsdService";
    private static final String MDNS_TAG = "mDnsConnector";
    private static final boolean DBG = true;
    private Context mContext;
    private ContentResolver mContentResolver;
    private static final int BASE = 393216;
    private static final int CMD_TO_STRING_COUNT = 19;
    private static String[] sCmdToString = new String[19];
    private HashMap<Messenger, ClientInfo> mClients = new HashMap<>();
    private SparseArray<ClientInfo> mIdToClientInfoMap = new SparseArray<>();
    private AsyncChannel mReplyChannel = new AsyncChannel();
    private int INVALID_ID = 0;
    private int mUniqueId = 1;
    private final CountDownLatch mNativeDaemonConnected = new CountDownLatch(1);
    private NativeDaemonConnector mNativeConnector = new NativeDaemonConnector(new NativeCallbackReceiver(), "mdns", 10, MDNS_TAG, 25);
    private NsdStateMachine mNsdStateMachine = new NsdStateMachine(TAG);

    static {
        sCmdToString[1] = "DISCOVER";
        sCmdToString[6] = "STOP-DISCOVER";
        sCmdToString[9] = "REGISTER";
        sCmdToString[12] = "UNREGISTER";
        sCmdToString[18] = "RESOLVE";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String cmdToString(int cmd) {
        int cmd2 = cmd - 393216;
        if (cmd2 >= 0 && cmd2 < sCmdToString.length) {
            return sCmdToString[cmd2];
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NsdService$NsdStateMachine.class */
    public class NsdStateMachine extends StateMachine {
        private final DefaultState mDefaultState;
        private final DisabledState mDisabledState;
        private final EnabledState mEnabledState;

        @Override // com.android.internal.util.StateMachine
        protected String getWhatToString(int what) {
            return NsdService.cmdToString(what);
        }

        private void registerForNsdSetting() {
            ContentObserver contentObserver = new ContentObserver(getHandler()) { // from class: com.android.server.NsdService.NsdStateMachine.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    if (NsdService.this.isNsdEnabled()) {
                        NsdService.this.mNsdStateMachine.sendMessage(NsdManager.ENABLE);
                    } else {
                        NsdService.this.mNsdStateMachine.sendMessage(NsdManager.DISABLE);
                    }
                }
            };
            NsdService.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.NSD_ON), false, contentObserver);
        }

        NsdStateMachine(String name) {
            super(name);
            this.mDefaultState = new DefaultState();
            this.mDisabledState = new DisabledState();
            this.mEnabledState = new EnabledState();
            addState(this.mDefaultState);
            addState(this.mDisabledState, this.mDefaultState);
            addState(this.mEnabledState, this.mDefaultState);
            if (NsdService.this.isNsdEnabled()) {
                setInitialState(this.mEnabledState);
            } else {
                setInitialState(this.mDisabledState);
            }
            setLogRecSize(25);
            registerForNsdSetting();
        }

        /* loaded from: NsdService$NsdStateMachine$DefaultState.class */
        class DefaultState extends State {
            DefaultState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case 69632:
                        if (msg.arg1 == 0) {
                            AsyncChannel c = (AsyncChannel) msg.obj;
                            Slog.d(NsdService.TAG, "New client listening to asynchronous messages");
                            c.sendMessage(AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED);
                            ClientInfo cInfo = new ClientInfo(c, msg.replyTo);
                            NsdService.this.mClients.put(msg.replyTo, cInfo);
                            return true;
                        }
                        Slog.e(NsdService.TAG, "Client connection failure, error=" + msg.arg1);
                        return true;
                    case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION /* 69633 */:
                        AsyncChannel ac = new AsyncChannel();
                        ac.connect(NsdService.this.mContext, NsdStateMachine.this.getHandler(), msg.replyTo);
                        return true;
                    case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                        if (msg.arg1 == 2) {
                            Slog.e(NsdService.TAG, "Send failed, client connection lost");
                        } else {
                            Slog.d(NsdService.TAG, "Client connection lost with reason: " + msg.arg1);
                        }
                        NsdService.this.mClients.remove(msg.replyTo);
                        return true;
                    case NsdManager.DISCOVER_SERVICES /* 393217 */:
                        NsdService.this.replyToMessage(msg, (int) NsdManager.DISCOVER_SERVICES_FAILED, 0);
                        return true;
                    case NsdManager.STOP_DISCOVERY /* 393222 */:
                        NsdService.this.replyToMessage(msg, (int) NsdManager.STOP_DISCOVERY_FAILED, 0);
                        return true;
                    case NsdManager.REGISTER_SERVICE /* 393225 */:
                        NsdService.this.replyToMessage(msg, (int) NsdManager.REGISTER_SERVICE_FAILED, 0);
                        return true;
                    case NsdManager.UNREGISTER_SERVICE /* 393228 */:
                        NsdService.this.replyToMessage(msg, (int) NsdManager.UNREGISTER_SERVICE_FAILED, 0);
                        return true;
                    case NsdManager.RESOLVE_SERVICE /* 393234 */:
                        NsdService.this.replyToMessage(msg, (int) NsdManager.RESOLVE_SERVICE_FAILED, 0);
                        return true;
                    case NsdManager.NATIVE_DAEMON_EVENT /* 393242 */:
                    default:
                        Slog.e(NsdService.TAG, "Unhandled " + msg);
                        return false;
                }
            }
        }

        /* loaded from: NsdService$NsdStateMachine$DisabledState.class */
        class DisabledState extends State {
            DisabledState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                NsdService.this.sendNsdStateChangeBroadcast(false);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                switch (msg.what) {
                    case NsdManager.ENABLE /* 393240 */:
                        NsdStateMachine.this.transitionTo(NsdStateMachine.this.mEnabledState);
                        return true;
                    default:
                        return false;
                }
            }
        }

        /* loaded from: NsdService$NsdStateMachine$EnabledState.class */
        class EnabledState extends State {
            EnabledState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                NsdService.this.sendNsdStateChangeBroadcast(true);
                if (NsdService.this.mClients.size() > 0) {
                    NsdService.this.startMDnsDaemon();
                }
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                if (NsdService.this.mClients.size() > 0) {
                    NsdService.this.stopMDnsDaemon();
                }
            }

            private boolean requestLimitReached(ClientInfo clientInfo) {
                if (clientInfo.mClientIds.size() >= 10) {
                    Slog.d(NsdService.TAG, "Exceeded max outstanding requests " + clientInfo);
                    return true;
                }
                return false;
            }

            private void storeRequestMap(int clientId, int globalId, ClientInfo clientInfo) {
                clientInfo.mClientIds.put(clientId, Integer.valueOf(globalId));
                NsdService.this.mIdToClientInfoMap.put(globalId, clientInfo);
            }

            private void removeRequestMap(int clientId, int globalId, ClientInfo clientInfo) {
                clientInfo.mClientIds.remove(clientId);
                NsdService.this.mIdToClientInfoMap.remove(globalId);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                boolean result = true;
                switch (msg.what) {
                    case 69632:
                        if (msg.arg1 == 0 && NsdService.this.mClients.size() == 0) {
                            NsdService.this.startMDnsDaemon();
                        }
                        result = false;
                        break;
                    case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                        if (NsdService.this.mClients.size() == 1) {
                            NsdService.this.stopMDnsDaemon();
                        }
                        result = false;
                        break;
                    case NsdManager.DISCOVER_SERVICES /* 393217 */:
                        Slog.d(NsdService.TAG, "Discover services");
                        NsdServiceInfo servInfo = (NsdServiceInfo) msg.obj;
                        ClientInfo clientInfo = (ClientInfo) NsdService.this.mClients.get(msg.replyTo);
                        if (requestLimitReached(clientInfo)) {
                            NsdService.this.replyToMessage(msg, (int) NsdManager.DISCOVER_SERVICES_FAILED, 4);
                            break;
                        } else {
                            int id = NsdService.this.getUniqueId();
                            if (!NsdService.this.discoverServices(id, servInfo.getServiceType())) {
                                NsdService.this.stopServiceDiscovery(id);
                                NsdService.this.replyToMessage(msg, (int) NsdManager.DISCOVER_SERVICES_FAILED, 0);
                                break;
                            } else {
                                Slog.d(NsdService.TAG, "Discover " + msg.arg2 + Separators.SP + id + servInfo.getServiceType());
                                storeRequestMap(msg.arg2, id, clientInfo);
                                NsdService.this.replyToMessage(msg, (int) NsdManager.DISCOVER_SERVICES_STARTED, servInfo);
                                break;
                            }
                        }
                    case NsdManager.STOP_DISCOVERY /* 393222 */:
                        Slog.d(NsdService.TAG, "Stop service discovery");
                        ClientInfo clientInfo2 = (ClientInfo) NsdService.this.mClients.get(msg.replyTo);
                        try {
                            int id2 = ((Integer) clientInfo2.mClientIds.get(msg.arg2)).intValue();
                            removeRequestMap(msg.arg2, id2, clientInfo2);
                            if (NsdService.this.stopServiceDiscovery(id2)) {
                                NsdService.this.replyToMessage(msg, NsdManager.STOP_DISCOVERY_SUCCEEDED);
                                break;
                            } else {
                                NsdService.this.replyToMessage(msg, (int) NsdManager.STOP_DISCOVERY_FAILED, 0);
                                break;
                            }
                        } catch (NullPointerException e) {
                            NsdService.this.replyToMessage(msg, (int) NsdManager.STOP_DISCOVERY_FAILED, 0);
                            break;
                        }
                    case NsdManager.REGISTER_SERVICE /* 393225 */:
                        Slog.d(NsdService.TAG, "Register service");
                        ClientInfo clientInfo3 = (ClientInfo) NsdService.this.mClients.get(msg.replyTo);
                        if (requestLimitReached(clientInfo3)) {
                            NsdService.this.replyToMessage(msg, (int) NsdManager.REGISTER_SERVICE_FAILED, 4);
                            break;
                        } else {
                            int id3 = NsdService.this.getUniqueId();
                            if (!NsdService.this.registerService(id3, (NsdServiceInfo) msg.obj)) {
                                NsdService.this.unregisterService(id3);
                                NsdService.this.replyToMessage(msg, (int) NsdManager.REGISTER_SERVICE_FAILED, 0);
                                break;
                            } else {
                                Slog.d(NsdService.TAG, "Register " + msg.arg2 + Separators.SP + id3);
                                storeRequestMap(msg.arg2, id3, clientInfo3);
                                break;
                            }
                        }
                    case NsdManager.UNREGISTER_SERVICE /* 393228 */:
                        Slog.d(NsdService.TAG, "unregister service");
                        ClientInfo clientInfo4 = (ClientInfo) NsdService.this.mClients.get(msg.replyTo);
                        try {
                            int id4 = ((Integer) clientInfo4.mClientIds.get(msg.arg2)).intValue();
                            removeRequestMap(msg.arg2, id4, clientInfo4);
                            if (NsdService.this.unregisterService(id4)) {
                                NsdService.this.replyToMessage(msg, NsdManager.UNREGISTER_SERVICE_SUCCEEDED);
                                break;
                            } else {
                                NsdService.this.replyToMessage(msg, (int) NsdManager.UNREGISTER_SERVICE_FAILED, 0);
                                break;
                            }
                        } catch (NullPointerException e2) {
                            NsdService.this.replyToMessage(msg, (int) NsdManager.UNREGISTER_SERVICE_FAILED, 0);
                            break;
                        }
                    case NsdManager.RESOLVE_SERVICE /* 393234 */:
                        Slog.d(NsdService.TAG, "Resolve service");
                        NsdServiceInfo servInfo2 = (NsdServiceInfo) msg.obj;
                        ClientInfo clientInfo5 = (ClientInfo) NsdService.this.mClients.get(msg.replyTo);
                        if (clientInfo5.mResolvedService != null) {
                            NsdService.this.replyToMessage(msg, (int) NsdManager.RESOLVE_SERVICE_FAILED, 3);
                            break;
                        } else {
                            int id5 = NsdService.this.getUniqueId();
                            if (!NsdService.this.resolveService(id5, servInfo2)) {
                                NsdService.this.replyToMessage(msg, (int) NsdManager.RESOLVE_SERVICE_FAILED, 0);
                                break;
                            } else {
                                clientInfo5.mResolvedService = new NsdServiceInfo();
                                storeRequestMap(msg.arg2, id5, clientInfo5);
                                break;
                            }
                        }
                    case NsdManager.DISABLE /* 393241 */:
                        NsdStateMachine.this.transitionTo(NsdStateMachine.this.mDisabledState);
                        break;
                    case NsdManager.NATIVE_DAEMON_EVENT /* 393242 */:
                        NativeEvent event = (NativeEvent) msg.obj;
                        if (!handleNativeEvent(event.code, event.raw, NativeDaemonEvent.unescapeArgs(event.raw))) {
                            result = false;
                            break;
                        }
                        break;
                    default:
                        result = false;
                        break;
                }
                return result;
            }

            private boolean handleNativeEvent(int code, String raw, String[] cooked) {
                boolean handled = true;
                int id = Integer.parseInt(cooked[1]);
                ClientInfo clientInfo = (ClientInfo) NsdService.this.mIdToClientInfoMap.get(id);
                if (clientInfo == null) {
                    Slog.e(NsdService.TAG, "Unique id with no client mapping: " + id);
                    return false;
                }
                int keyId = clientInfo.mClientIds.indexOfValue(Integer.valueOf(id));
                if (keyId == -1) {
                    Slog.d(NsdService.TAG, "Notification for a listener that is no longer active: " + id);
                    return false;
                }
                int clientId = clientInfo.mClientIds.keyAt(keyId);
                switch (code) {
                    case NativeResponseCode.SERVICE_DISCOVERY_FAILED /* 602 */:
                        Slog.d(NsdService.TAG, "SERVICE_DISC_FAILED Raw: " + raw);
                        clientInfo.mChannel.sendMessage(NsdManager.DISCOVER_SERVICES_FAILED, 0, clientId);
                        break;
                    case 603:
                        Slog.d(NsdService.TAG, "SERVICE_FOUND Raw: " + raw);
                        NsdServiceInfo servInfo = new NsdServiceInfo(cooked[2], cooked[3], null);
                        clientInfo.mChannel.sendMessage(NsdManager.SERVICE_FOUND, 0, clientId, servInfo);
                        break;
                    case 604:
                        Slog.d(NsdService.TAG, "SERVICE_LOST Raw: " + raw);
                        NsdServiceInfo servInfo2 = new NsdServiceInfo(cooked[2], cooked[3], null);
                        clientInfo.mChannel.sendMessage(NsdManager.SERVICE_LOST, 0, clientId, servInfo2);
                        break;
                    case 605:
                        Slog.d(NsdService.TAG, "SERVICE_REGISTER_FAILED Raw: " + raw);
                        clientInfo.mChannel.sendMessage(NsdManager.REGISTER_SERVICE_FAILED, 0, clientId);
                        break;
                    case 606:
                        Slog.d(NsdService.TAG, "SERVICE_REGISTERED Raw: " + raw);
                        NsdServiceInfo servInfo3 = new NsdServiceInfo(cooked[2], null, null);
                        clientInfo.mChannel.sendMessage(NsdManager.REGISTER_SERVICE_SUCCEEDED, id, clientId, servInfo3);
                        break;
                    case NativeResponseCode.SERVICE_RESOLUTION_FAILED /* 607 */:
                        Slog.d(NsdService.TAG, "SERVICE_RESOLVE_FAILED Raw: " + raw);
                        NsdService.this.stopResolveService(id);
                        removeRequestMap(clientId, id, clientInfo);
                        clientInfo.mResolvedService = null;
                        clientInfo.mChannel.sendMessage(NsdManager.RESOLVE_SERVICE_FAILED, 0, clientId);
                        break;
                    case NativeResponseCode.SERVICE_RESOLVED /* 608 */:
                        Slog.d(NsdService.TAG, "SERVICE_RESOLVED Raw: " + raw);
                        int index = cooked[2].indexOf(Separators.DOT);
                        if (index == -1) {
                            Slog.e(NsdService.TAG, "Invalid service found " + raw);
                            break;
                        } else {
                            String name = cooked[2].substring(0, index);
                            String rest = cooked[2].substring(index);
                            String type = rest.replace(".local.", "");
                            clientInfo.mResolvedService.setServiceName(name);
                            clientInfo.mResolvedService.setServiceType(type);
                            clientInfo.mResolvedService.setPort(Integer.parseInt(cooked[4]));
                            NsdService.this.stopResolveService(id);
                            if (!NsdService.this.getAddrInfo(id, cooked[3])) {
                                clientInfo.mChannel.sendMessage(NsdManager.RESOLVE_SERVICE_FAILED, 0, clientId);
                                removeRequestMap(clientId, id, clientInfo);
                                clientInfo.mResolvedService = null;
                                break;
                            }
                        }
                        break;
                    case NativeResponseCode.SERVICE_UPDATED /* 609 */:
                    case NativeResponseCode.SERVICE_UPDATE_FAILED /* 610 */:
                        break;
                    case NativeResponseCode.SERVICE_GET_ADDR_FAILED /* 611 */:
                        NsdService.this.stopGetAddrInfo(id);
                        removeRequestMap(clientId, id, clientInfo);
                        clientInfo.mResolvedService = null;
                        Slog.d(NsdService.TAG, "SERVICE_RESOLVE_FAILED Raw: " + raw);
                        clientInfo.mChannel.sendMessage(NsdManager.RESOLVE_SERVICE_FAILED, 0, clientId);
                        break;
                    case NativeResponseCode.SERVICE_GET_ADDR_SUCCESS /* 612 */:
                        Slog.d(NsdService.TAG, "SERVICE_GET_ADDR_SUCCESS Raw: " + raw);
                        try {
                            clientInfo.mResolvedService.setHost(InetAddress.getByName(cooked[4]));
                            clientInfo.mChannel.sendMessage(NsdManager.RESOLVE_SERVICE_SUCCEEDED, 0, clientId, clientInfo.mResolvedService);
                        } catch (UnknownHostException e) {
                            clientInfo.mChannel.sendMessage(NsdManager.RESOLVE_SERVICE_FAILED, 0, clientId);
                        }
                        NsdService.this.stopGetAddrInfo(id);
                        removeRequestMap(clientId, id, clientInfo);
                        clientInfo.mResolvedService = null;
                        break;
                    default:
                        handled = false;
                        break;
                }
                return handled;
            }
        }
    }

    private NsdService(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mNsdStateMachine.start();
        Thread th = new Thread(this.mNativeConnector, MDNS_TAG);
        th.start();
    }

    public static NsdService create(Context context) throws InterruptedException {
        NsdService service = new NsdService(context);
        service.mNativeDaemonConnected.await();
        return service;
    }

    @Override // android.net.nsd.INsdManager
    public Messenger getMessenger() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERNET, TAG);
        return new Messenger(this.mNsdStateMachine.getHandler());
    }

    @Override // android.net.nsd.INsdManager
    public void setEnabled(boolean enable) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        Settings.Global.putInt(this.mContentResolver, Settings.Global.NSD_ON, enable ? 1 : 0);
        if (enable) {
            this.mNsdStateMachine.sendMessage(NsdManager.ENABLE);
        } else {
            this.mNsdStateMachine.sendMessage(NsdManager.DISABLE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendNsdStateChangeBroadcast(boolean enabled) {
        Intent intent = new Intent(NsdManager.ACTION_NSD_STATE_CHANGED);
        intent.addFlags(67108864);
        if (enabled) {
            intent.putExtra(NsdManager.EXTRA_NSD_STATE, 2);
        } else {
            intent.putExtra(NsdManager.EXTRA_NSD_STATE, 1);
        }
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNsdEnabled() {
        boolean ret = Settings.Global.getInt(this.mContentResolver, Settings.Global.NSD_ON, 1) == 1;
        Slog.d(TAG, "Network service discovery enabled " + ret);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getUniqueId() {
        int i = this.mUniqueId + 1;
        this.mUniqueId = i;
        if (i == this.INVALID_ID) {
            int i2 = this.mUniqueId + 1;
            this.mUniqueId = i2;
            return i2;
        }
        return this.mUniqueId;
    }

    /* loaded from: NsdService$NativeResponseCode.class */
    class NativeResponseCode {
        public static final int SERVICE_DISCOVERY_FAILED = 602;
        public static final int SERVICE_FOUND = 603;
        public static final int SERVICE_LOST = 604;
        public static final int SERVICE_REGISTRATION_FAILED = 605;
        public static final int SERVICE_REGISTERED = 606;
        public static final int SERVICE_RESOLUTION_FAILED = 607;
        public static final int SERVICE_RESOLVED = 608;
        public static final int SERVICE_UPDATED = 609;
        public static final int SERVICE_UPDATE_FAILED = 610;
        public static final int SERVICE_GET_ADDR_FAILED = 611;
        public static final int SERVICE_GET_ADDR_SUCCESS = 612;

        NativeResponseCode() {
        }
    }

    /* loaded from: NsdService$NativeEvent.class */
    private class NativeEvent {
        final int code;
        final String raw;

        NativeEvent(int code, String raw) {
            this.code = code;
            this.raw = raw;
        }
    }

    /* loaded from: NsdService$NativeCallbackReceiver.class */
    class NativeCallbackReceiver implements INativeDaemonConnectorCallbacks {
        NativeCallbackReceiver() {
        }

        @Override // com.android.server.INativeDaemonConnectorCallbacks
        public void onDaemonConnected() {
            NsdService.this.mNativeDaemonConnected.countDown();
        }

        @Override // com.android.server.INativeDaemonConnectorCallbacks
        public boolean onEvent(int code, String raw, String[] cooked) {
            NativeEvent event = new NativeEvent(code, raw);
            NsdService.this.mNsdStateMachine.sendMessage(NsdManager.NATIVE_DAEMON_EVENT, event);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean startMDnsDaemon() {
        Slog.d(TAG, "startMDnsDaemon");
        try {
            this.mNativeConnector.execute("mdnssd", "start-service");
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to start daemon" + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean stopMDnsDaemon() {
        Slog.d(TAG, "stopMDnsDaemon");
        try {
            this.mNativeConnector.execute("mdnssd", "stop-service");
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to start daemon" + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean registerService(int regId, NsdServiceInfo service) {
        Slog.d(TAG, "registerService: " + regId + Separators.SP + service);
        try {
            this.mNativeConnector.execute("mdnssd", "register", Integer.valueOf(regId), service.getServiceName(), service.getServiceType(), Integer.valueOf(service.getPort()));
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to execute registerService " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean unregisterService(int regId) {
        Slog.d(TAG, "unregisterService: " + regId);
        try {
            this.mNativeConnector.execute("mdnssd", "stop-register", Integer.valueOf(regId));
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to execute unregisterService " + e);
            return false;
        }
    }

    private boolean updateService(int regId, DnsSdTxtRecord t) {
        Slog.d(TAG, "updateService: " + regId + Separators.SP + t);
        if (t == null) {
            return false;
        }
        try {
            this.mNativeConnector.execute("mdnssd", "update", Integer.valueOf(regId), Integer.valueOf(t.size()), t.getRawData());
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to updateServices " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean discoverServices(int discoveryId, String serviceType) {
        Slog.d(TAG, "discoverServices: " + discoveryId + Separators.SP + serviceType);
        try {
            this.mNativeConnector.execute("mdnssd", "discover", Integer.valueOf(discoveryId), serviceType);
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to discoverServices " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean stopServiceDiscovery(int discoveryId) {
        Slog.d(TAG, "stopServiceDiscovery: " + discoveryId);
        try {
            this.mNativeConnector.execute("mdnssd", "stop-discover", Integer.valueOf(discoveryId));
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to stopServiceDiscovery " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean resolveService(int resolveId, NsdServiceInfo service) {
        Slog.d(TAG, "resolveService: " + resolveId + Separators.SP + service);
        try {
            this.mNativeConnector.execute("mdnssd", "resolve", Integer.valueOf(resolveId), service.getServiceName(), service.getServiceType(), "local.");
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to resolveService " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean stopResolveService(int resolveId) {
        Slog.d(TAG, "stopResolveService: " + resolveId);
        try {
            this.mNativeConnector.execute("mdnssd", "stop-resolve", Integer.valueOf(resolveId));
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to stop resolve " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getAddrInfo(int resolveId, String hostname) {
        Slog.d(TAG, "getAdddrInfo: " + resolveId);
        try {
            this.mNativeConnector.execute("mdnssd", "getaddrinfo", Integer.valueOf(resolveId), hostname);
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to getAddrInfo " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean stopGetAddrInfo(int resolveId) {
        Slog.d(TAG, "stopGetAdddrInfo: " + resolveId);
        try {
            this.mNativeConnector.execute("mdnssd", "stop-getaddrinfo", Integer.valueOf(resolveId));
            return true;
        } catch (NativeDaemonConnectorException e) {
            Slog.e(TAG, "Failed to stopGetAddrInfo " + e);
            return false;
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump ServiceDiscoverService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        for (ClientInfo client : this.mClients.values()) {
            pw.println("Client Info");
            pw.println(client);
        }
        this.mNsdStateMachine.dump(fd, pw, args);
    }

    private Message obtainMessage(Message srcMsg) {
        Message msg = Message.obtain();
        msg.arg2 = srcMsg.arg2;
        return msg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessage(msg);
        dstMsg.what = what;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, int arg1) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessage(msg);
        dstMsg.what = what;
        dstMsg.arg1 = arg1;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyToMessage(Message msg, int what, Object obj) {
        if (msg.replyTo == null) {
            return;
        }
        Message dstMsg = obtainMessage(msg);
        dstMsg.what = what;
        dstMsg.obj = obj;
        this.mReplyChannel.replyToMessage(msg, dstMsg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NsdService$ClientInfo.class */
    public class ClientInfo {
        private static final int MAX_LIMIT = 10;
        private final AsyncChannel mChannel;
        private final Messenger mMessenger;
        private NsdServiceInfo mResolvedService;
        private SparseArray<Integer> mClientIds;

        private ClientInfo(AsyncChannel c, Messenger m) {
            this.mClientIds = new SparseArray<>();
            this.mChannel = c;
            this.mMessenger = m;
            Slog.d(NsdService.TAG, "New client, channel: " + c + " messenger: " + m);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("mChannel ").append(this.mChannel).append(Separators.RETURN);
            sb.append("mMessenger ").append(this.mMessenger).append(Separators.RETURN);
            sb.append("mResolvedService ").append(this.mResolvedService).append(Separators.RETURN);
            for (int i = 0; i < this.mClientIds.size(); i++) {
                sb.append("clientId ").append(this.mClientIds.keyAt(i));
                sb.append(" mDnsId ").append(this.mClientIds.valueAt(i)).append(Separators.RETURN);
            }
            return sb.toString();
        }
    }
}