package com.android.server;

import android.Manifest;
import android.accounts.GrantCredentialsPermissionActivity;
import android.content.Context;
import android.net.INetworkManagementEventObserver;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.NetworkStats;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.app.IBatteryStats;
import com.android.internal.net.NetworkStatsFactory;
import com.android.internal.util.Preconditions;
import com.android.server.NativeDaemonConnector;
import com.android.server.Watchdog;
import com.android.server.net.LockdownVpnTracker;
import com.google.android.collect.Maps;
import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ParameterNames;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

/* loaded from: NetworkManagementService.class */
public class NetworkManagementService extends INetworkManagementService.Stub implements Watchdog.Monitor {
    private static final String TAG = "NetworkManagementService";
    private static final boolean DBG = false;
    private static final String NETD_TAG = "NetdConnector";
    private static final String NETD_SOCKET_NAME = "netd";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String ALLOW = "allow";
    private static final String DENY = "deny";
    private static final String DEFAULT = "default";
    private static final String SECONDARY = "secondary";
    public static final String LIMIT_GLOBAL_ALERT = "globalAlert";
    private Context mContext;
    private NativeDaemonConnector mConnector;
    private Thread mThread;
    private volatile boolean mBandwidthControlEnabled;
    private volatile boolean mFirewallEnabled;
    private final Handler mMainHandler = new Handler();
    private CountDownLatch mConnectedSignal = new CountDownLatch(1);
    private final RemoteCallbackList<INetworkManagementEventObserver> mObservers = new RemoteCallbackList<>();
    private final NetworkStatsFactory mStatsFactory = new NetworkStatsFactory();
    private Object mQuotaLock = new Object();
    private HashMap<String, Long> mActiveQuotas = Maps.newHashMap();
    private HashMap<String, Long> mActiveAlerts = Maps.newHashMap();
    private SparseBooleanArray mUidRejectOnQuota = new SparseBooleanArray();
    private Object mIdleTimerLock = new Object();
    private HashMap<String, IdleTimerParams> mActiveIdleTimers = Maps.newHashMap();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NetworkManagementService.readRouteList(java.lang.String):java.util.ArrayList<java.lang.String>, file: NetworkManagementService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private java.util.ArrayList<java.lang.String> readRouteList(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NetworkManagementService.readRouteList(java.lang.String):java.util.ArrayList<java.lang.String>, file: NetworkManagementService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetworkManagementService.readRouteList(java.lang.String):java.util.ArrayList");
    }

    /* loaded from: NetworkManagementService$NetdResponseCode.class */
    class NetdResponseCode {
        public static final int InterfaceListResult = 110;
        public static final int TetherInterfaceListResult = 111;
        public static final int TetherDnsFwdTgtListResult = 112;
        public static final int TtyListResult = 113;
        public static final int TetheringStatsListResult = 114;
        public static final int TetherStatusResult = 210;
        public static final int IpFwdStatusResult = 211;
        public static final int InterfaceGetCfgResult = 213;
        public static final int SoftapStatusResult = 214;
        public static final int InterfaceRxCounterResult = 216;
        public static final int InterfaceTxCounterResult = 217;
        public static final int QuotaCounterResult = 220;
        public static final int TetheringStatsResult = 221;
        public static final int DnsProxyQueryResult = 222;
        public static final int ClatdStatusResult = 223;
        public static final int GetMarkResult = 225;
        public static final int InterfaceChange = 600;
        public static final int BandwidthControl = 601;
        public static final int InterfaceClassActivity = 613;
        public static final int InterfaceAddressChange = 614;

        NetdResponseCode() {
        }
    }

    /* loaded from: NetworkManagementService$IdleTimerParams.class */
    private static class IdleTimerParams {
        public final int timeout;
        public final String label;
        public int networkCount = 1;

        IdleTimerParams(int timeout, String label) {
            this.timeout = timeout;
            this.label = label;
        }
    }

    private NetworkManagementService(Context context, String socket) {
        this.mContext = context;
        if ("simulator".equals(SystemProperties.get("ro.product.device"))) {
            return;
        }
        this.mConnector = new NativeDaemonConnector(new NetdCallbackReceiver(), socket, 10, NETD_TAG, 160);
        this.mThread = new Thread(this.mConnector, NETD_TAG);
        Watchdog.getInstance().addMonitor(this);
    }

    static NetworkManagementService create(Context context, String socket) throws InterruptedException {
        NetworkManagementService service = new NetworkManagementService(context, socket);
        CountDownLatch connectedSignal = service.mConnectedSignal;
        service.mThread.start();
        connectedSignal.await();
        return service;
    }

    public static NetworkManagementService create(Context context) throws InterruptedException {
        return create(context, NETD_SOCKET_NAME);
    }

    public void systemReady() {
        prepareNativeDaemon();
    }

    @Override // android.os.INetworkManagementService
    public void registerObserver(INetworkManagementEventObserver observer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        this.mObservers.register(observer);
    }

    @Override // android.os.INetworkManagementService
    public void unregisterObserver(INetworkManagementEventObserver observer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        this.mObservers.unregister(observer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyInterfaceStatusChanged(String iface, boolean up) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceStatusChanged(iface, up);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyInterfaceLinkStateChanged(String iface, boolean up) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceLinkStateChanged(iface, up);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyInterfaceAdded(String iface) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceAdded(iface);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyInterfaceRemoved(String iface) {
        this.mActiveAlerts.remove(iface);
        this.mActiveQuotas.remove(iface);
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceRemoved(iface);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLimitReached(String limitName, String iface) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).limitReached(limitName, iface);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyInterfaceClassActivity(String label, boolean active) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).interfaceClassDataActivityChanged(label, active);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareNativeDaemon() {
        this.mBandwidthControlEnabled = false;
        boolean hasKernelSupport = new File("/proc/net/xt_qtaguid/ctrl").exists();
        if (hasKernelSupport) {
            Slog.d(TAG, "enabling bandwidth control");
            try {
                this.mConnector.execute("bandwidth", "enable");
                this.mBandwidthControlEnabled = true;
            } catch (NativeDaemonConnectorException e) {
                Log.wtf(TAG, "problem enabling bandwidth controls", e);
            }
        } else {
            Slog.d(TAG, "not enabling bandwidth control");
        }
        SystemProperties.set(NetworkManagementSocketTagger.PROP_QTAGUID_ENABLED, this.mBandwidthControlEnabled ? "1" : "0");
        if (this.mBandwidthControlEnabled) {
            try {
                IBatteryStats.Stub.asInterface(ServiceManager.getService(BatteryStats.SERVICE_NAME)).noteNetworkStatsEnabled();
            } catch (RemoteException e2) {
            }
        }
        synchronized (this.mQuotaLock) {
            int size = this.mActiveQuotas.size();
            if (size > 0) {
                Slog.d(TAG, "pushing " + size + " active quota rules");
                HashMap<String, Long> activeQuotas = this.mActiveQuotas;
                this.mActiveQuotas = Maps.newHashMap();
                for (Map.Entry<String, Long> entry : activeQuotas.entrySet()) {
                    setInterfaceQuota(entry.getKey(), entry.getValue().longValue());
                }
            }
            int size2 = this.mActiveAlerts.size();
            if (size2 > 0) {
                Slog.d(TAG, "pushing " + size2 + " active alert rules");
                HashMap<String, Long> activeAlerts = this.mActiveAlerts;
                this.mActiveAlerts = Maps.newHashMap();
                for (Map.Entry<String, Long> entry2 : activeAlerts.entrySet()) {
                    setInterfaceAlert(entry2.getKey(), entry2.getValue().longValue());
                }
            }
            int size3 = this.mUidRejectOnQuota.size();
            if (size3 > 0) {
                Slog.d(TAG, "pushing " + size3 + " active uid rules");
                SparseBooleanArray uidRejectOnQuota = this.mUidRejectOnQuota;
                this.mUidRejectOnQuota = new SparseBooleanArray();
                for (int i = 0; i < uidRejectOnQuota.size(); i++) {
                    setUidNetworkRules(uidRejectOnQuota.keyAt(i), uidRejectOnQuota.valueAt(i));
                }
            }
        }
        setFirewallEnabled(this.mFirewallEnabled || LockdownVpnTracker.isEnabled());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAddressUpdated(String address, String iface, int flags, int scope) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).addressUpdated(address, iface, flags, scope);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAddressRemoved(String address, String iface, int flags, int scope) {
        int length = this.mObservers.beginBroadcast();
        for (int i = 0; i < length; i++) {
            try {
                this.mObservers.getBroadcastItem(i).addressRemoved(address, iface, flags, scope);
            } catch (RemoteException e) {
            } catch (RuntimeException e2) {
            }
        }
        this.mObservers.finishBroadcast();
    }

    /* loaded from: NetworkManagementService$NetdCallbackReceiver.class */
    private class NetdCallbackReceiver implements INativeDaemonConnectorCallbacks {
        private NetdCallbackReceiver() {
        }

        @Override // com.android.server.INativeDaemonConnectorCallbacks
        public void onDaemonConnected() {
            if (NetworkManagementService.this.mConnectedSignal != null) {
                NetworkManagementService.this.mConnectedSignal.countDown();
                NetworkManagementService.this.mConnectedSignal = null;
                return;
            }
            NetworkManagementService.this.mMainHandler.post(new Runnable() { // from class: com.android.server.NetworkManagementService.NetdCallbackReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    NetworkManagementService.this.prepareNativeDaemon();
                }
            });
        }

        @Override // com.android.server.INativeDaemonConnectorCallbacks
        public boolean onEvent(int code, String raw, String[] cooked) {
            switch (code) {
                case 600:
                    if (cooked.length < 4 || !cooked[1].equals("Iface")) {
                        throw new IllegalStateException(String.format("Invalid event from daemon (%s)", raw));
                    }
                    if (cooked[2].equals("added")) {
                        NetworkManagementService.this.notifyInterfaceAdded(cooked[3]);
                        return true;
                    } else if (cooked[2].equals(Environment.MEDIA_REMOVED)) {
                        NetworkManagementService.this.notifyInterfaceRemoved(cooked[3]);
                        return true;
                    } else if (cooked[2].equals("changed") && cooked.length == 5) {
                        NetworkManagementService.this.notifyInterfaceStatusChanged(cooked[3], cooked[4].equals("up"));
                        return true;
                    } else if (cooked[2].equals("linkstate") && cooked.length == 5) {
                        NetworkManagementService.this.notifyInterfaceLinkStateChanged(cooked[3], cooked[4].equals("up"));
                        return true;
                    } else {
                        throw new IllegalStateException(String.format("Invalid event from daemon (%s)", raw));
                    }
                case NetdResponseCode.BandwidthControl /* 601 */:
                    if (cooked.length < 5 || !cooked[1].equals("limit")) {
                        throw new IllegalStateException(String.format("Invalid event from daemon (%s)", raw));
                    }
                    if (cooked[2].equals(ParameterNames.ALERT)) {
                        NetworkManagementService.this.notifyLimitReached(cooked[3], cooked[4]);
                        return true;
                    }
                    throw new IllegalStateException(String.format("Invalid event from daemon (%s)", raw));
                case NetdResponseCode.InterfaceClassActivity /* 613 */:
                    if (cooked.length < 4 || !cooked[1].equals("IfaceClass")) {
                        throw new IllegalStateException(String.format("Invalid event from daemon (%s)", raw));
                    }
                    boolean isActive = cooked[2].equals("active");
                    NetworkManagementService.this.notifyInterfaceClassActivity(cooked[3], isActive);
                    return true;
                case NetdResponseCode.InterfaceAddressChange /* 614 */:
                    String msg = String.format("Invalid event from daemon (%s)", raw);
                    if (cooked.length < 6 || !cooked[1].equals("Address")) {
                        throw new IllegalStateException(msg);
                    }
                    try {
                        int flags = Integer.parseInt(cooked[5]);
                        int scope = Integer.parseInt(cooked[6]);
                        if (cooked[2].equals("updated")) {
                            NetworkManagementService.this.notifyAddressUpdated(cooked[3], cooked[4], flags, scope);
                            return true;
                        }
                        NetworkManagementService.this.notifyAddressRemoved(cooked[3], cooked[4], flags, scope);
                        return true;
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException(msg);
                    }
                default:
                    return false;
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public String[] listInterfaces() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("interface", "list"), 110);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public InterfaceConfiguration getInterfaceConfig(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("interface", "getcfg", iface);
            event.checkCode(213);
            StringTokenizer st = new StringTokenizer(event.getMessage());
            try {
                InterfaceConfiguration cfg = new InterfaceConfiguration();
                cfg.setHardwareAddress(st.nextToken(Separators.SP));
                InetAddress addr = null;
                int prefixLength = 0;
                try {
                    addr = NetworkUtils.numericToInetAddress(st.nextToken());
                } catch (IllegalArgumentException iae) {
                    Slog.e(TAG, "Failed to parse ipaddr", iae);
                }
                try {
                    prefixLength = Integer.parseInt(st.nextToken());
                } catch (NumberFormatException nfe) {
                    Slog.e(TAG, "Failed to parse prefixLength", nfe);
                }
                cfg.setLinkAddress(new LinkAddress(addr, prefixLength));
                while (st.hasMoreTokens()) {
                    cfg.setFlag(st.nextToken());
                }
                return cfg;
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("Invalid response from daemon: " + event);
            }
        } catch (NativeDaemonConnectorException e2) {
            throw e2.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceConfig(String iface, InterfaceConfiguration cfg) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        LinkAddress linkAddr = cfg.getLinkAddress();
        if (linkAddr == null || linkAddr.getAddress() == null) {
            throw new IllegalStateException("Null LinkAddress given");
        }
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("interface", "setcfg", iface, linkAddr.getAddress().getHostAddress(), Integer.valueOf(linkAddr.getNetworkPrefixLength()));
        for (String flag : cfg.getFlags()) {
            cmd.appendArg(flag);
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceDown(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        InterfaceConfiguration ifcg = getInterfaceConfig(iface);
        ifcg.setInterfaceDown();
        setInterfaceConfig(iface, ifcg);
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceUp(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        InterfaceConfiguration ifcg = getInterfaceConfig(iface);
        ifcg.setInterfaceUp();
        setInterfaceConfig(iface, ifcg);
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceIpv6PrivacyExtensions(String iface, boolean enable) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonConnector nativeDaemonConnector = this.mConnector;
            Object[] objArr = new Object[3];
            objArr[0] = "ipv6privacyextensions";
            objArr[1] = iface;
            objArr[2] = enable ? "enable" : "disable";
            nativeDaemonConnector.execute("interface", objArr);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearInterfaceAddresses(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "clearaddrs", iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void enableIpv6(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "ipv6", iface, "enable");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void disableIpv6(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "ipv6", iface, "disable");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void addRoute(String interfaceName, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        modifyRoute(interfaceName, ADD, route, "default");
    }

    @Override // android.os.INetworkManagementService
    public void removeRoute(String interfaceName, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        modifyRoute(interfaceName, REMOVE, route, "default");
    }

    @Override // android.os.INetworkManagementService
    public void addSecondaryRoute(String interfaceName, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        modifyRoute(interfaceName, ADD, route, SECONDARY);
    }

    @Override // android.os.INetworkManagementService
    public void removeSecondaryRoute(String interfaceName, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        modifyRoute(interfaceName, REMOVE, route, SECONDARY);
    }

    private void modifyRoute(String interfaceName, String action, RouteInfo route, String type) {
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("interface", "route", action, interfaceName, type);
        LinkAddress la = route.getDestination();
        cmd.appendArg(la.getAddress().getHostAddress());
        cmd.appendArg(Integer.valueOf(la.getNetworkPrefixLength()));
        if (route.getGateway() == null) {
            if (la.getAddress() instanceof Inet4Address) {
                cmd.appendArg("0.0.0.0");
            } else {
                cmd.appendArg("::0");
            }
        } else {
            cmd.appendArg(route.getGateway().getHostAddress());
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public RouteInfo[] getRoutes(String interfaceName) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        ArrayList<RouteInfo> routes = new ArrayList<>();
        Iterator i$ = readRouteList("/proc/net/route").iterator();
        while (i$.hasNext()) {
            String s = i$.next();
            String[] fields = s.split(Separators.HT);
            if (fields.length > 7) {
                String iface = fields[0];
                if (interfaceName.equals(iface)) {
                    String dest = fields[1];
                    String gate = fields[2];
                    String str = fields[3];
                    String mask = fields[7];
                    try {
                        InetAddress destAddr = NetworkUtils.intToInetAddress((int) Long.parseLong(dest, 16));
                        int prefixLength = NetworkUtils.netmaskIntToPrefixLength((int) Long.parseLong(mask, 16));
                        LinkAddress linkAddress = new LinkAddress(destAddr, prefixLength);
                        InetAddress gatewayAddr = NetworkUtils.intToInetAddress((int) Long.parseLong(gate, 16));
                        RouteInfo route = new RouteInfo(linkAddress, gatewayAddr);
                        routes.add(route);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing route " + s + " : " + e);
                    }
                }
            }
        }
        Iterator i$2 = readRouteList("/proc/net/ipv6_route").iterator();
        while (i$2.hasNext()) {
            String s2 = i$2.next();
            String[] fields2 = s2.split("\\s+");
            if (fields2.length > 9) {
                String iface2 = fields2[9].trim();
                if (interfaceName.equals(iface2)) {
                    String dest2 = fields2[0];
                    String prefix = fields2[1];
                    String gate2 = fields2[4];
                    try {
                        int prefixLength2 = Integer.parseInt(prefix, 16);
                        InetAddress destAddr2 = NetworkUtils.hexToInet6Address(dest2);
                        LinkAddress linkAddress2 = new LinkAddress(destAddr2, prefixLength2);
                        InetAddress gateAddr = NetworkUtils.hexToInet6Address(gate2);
                        RouteInfo route2 = new RouteInfo(linkAddress2, gateAddr);
                        routes.add(route2);
                    } catch (Exception e2) {
                        Log.e(TAG, "Error parsing route " + s2 + " : " + e2);
                    }
                }
            }
        }
        return (RouteInfo[]) routes.toArray(new RouteInfo[routes.size()]);
    }

    @Override // android.os.INetworkManagementService
    public void setMtu(String iface, int mtu) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "setmtu", iface, Integer.valueOf(mtu));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void shutdown() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SHUTDOWN, TAG);
        Slog.d(TAG, "Shutting down");
    }

    @Override // android.os.INetworkManagementService
    public boolean getIpForwardingEnabled() throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("ipfwd", "status");
            event.checkCode(211);
            return event.getMessage().endsWith("enabled");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setIpForwardingEnabled(boolean enable) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonConnector nativeDaemonConnector = this.mConnector;
            Object[] objArr = new Object[1];
            objArr[0] = enable ? "enable" : "disable";
            nativeDaemonConnector.execute("ipfwd", objArr);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void startTethering(String[] dhcpRange) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("tether", Telephony.BaseMmsColumns.START);
        for (String d : dhcpRange) {
            cmd.appendArg(d);
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void stopTethering() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("tether", "stop");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public boolean isTetheringStarted() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("tether", "status");
            event.checkCode(210);
            return event.getMessage().endsWith("started");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void tetherInterface(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("tether", "interface", ADD, iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void untetherInterface(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("tether", "interface", REMOVE, iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public String[] listTetheredInterfaces() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("tether", "interface", "list"), 111);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setDnsForwarders(String[] dns) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("tether", "dns", "set");
        for (String s : dns) {
            cmd.appendArg(NetworkUtils.numericToInetAddress(s).getHostAddress());
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public String[] getDnsForwarders() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("tether", "dns", "list"), 112);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    private void modifyNat(String action, String internalInterface, String externalInterface) throws SocketException {
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("nat", action, internalInterface, externalInterface);
        NetworkInterface internalNetworkInterface = NetworkInterface.getByName(internalInterface);
        if (internalNetworkInterface == null) {
            cmd.appendArg("0");
        } else {
            Collection<InterfaceAddress> interfaceAddresses = internalNetworkInterface.getInterfaceAddresses();
            cmd.appendArg(Integer.valueOf(interfaceAddresses.size()));
            for (InterfaceAddress ia : interfaceAddresses) {
                InetAddress addr = NetworkUtils.getNetworkPart(ia.getAddress(), ia.getNetworkPrefixLength());
                cmd.appendArg(addr.getHostAddress() + Separators.SLASH + ((int) ia.getNetworkPrefixLength()));
            }
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void enableNat(String internalInterface, String externalInterface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            modifyNat("enable", internalInterface, externalInterface);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public void disableNat(String internalInterface, String externalInterface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            modifyNat("disable", internalInterface, externalInterface);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public String[] listTtys() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return NativeDaemonEvent.filterMessageList(this.mConnector.executeForList("list_ttys", new Object[0]), 113);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void attachPppd(String tty, String localAddr, String remoteAddr, String dns1Addr, String dns2Addr) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("pppd", "attach", tty, NetworkUtils.numericToInetAddress(localAddr).getHostAddress(), NetworkUtils.numericToInetAddress(remoteAddr).getHostAddress(), NetworkUtils.numericToInetAddress(dns1Addr).getHostAddress(), NetworkUtils.numericToInetAddress(dns2Addr).getHostAddress());
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void detachPppd(String tty) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("pppd", "detach", tty);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void startAccessPoint(WifiConfiguration wifiConfig, String wlanIface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            wifiFirmwareReload(wlanIface, "AP");
            if (wifiConfig == null) {
                this.mConnector.execute("softap", "set", wlanIface);
            } else {
                this.mConnector.execute("softap", "set", wlanIface, wifiConfig.SSID, "broadcast", "6", getSecurityType(wifiConfig), new NativeDaemonConnector.SensitiveArg(wifiConfig.preSharedKey));
            }
            this.mConnector.execute("softap", "startap");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    private static String getSecurityType(WifiConfiguration wifiConfig) {
        switch (wifiConfig.getAuthType()) {
            case 1:
                return "wpa-psk";
            case 4:
                return "wpa2-psk";
            default:
                return "open";
        }
    }

    @Override // android.os.INetworkManagementService
    public void wifiFirmwareReload(String wlanIface, String mode) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("softap", "fwreload", wlanIface, mode);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void stopAccessPoint(String wlanIface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("softap", "stopap");
            wifiFirmwareReload(wlanIface, "STA");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setAccessPoint(WifiConfiguration wifiConfig, String wlanIface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            if (wifiConfig == null) {
                this.mConnector.execute("softap", "set", wlanIface);
            } else {
                this.mConnector.execute("softap", "set", wlanIface, wifiConfig.SSID, "broadcast", "6", getSecurityType(wifiConfig), new NativeDaemonConnector.SensitiveArg(wifiConfig.preSharedKey));
            }
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void addIdleTimer(String iface, int timeout, String label) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        synchronized (this.mIdleTimerLock) {
            IdleTimerParams params = this.mActiveIdleTimers.get(iface);
            if (params != null) {
                params.networkCount++;
                return;
            }
            try {
                this.mConnector.execute("idletimer", ADD, iface, Integer.toString(timeout), label);
                this.mActiveIdleTimers.put(iface, new IdleTimerParams(timeout, label));
            } catch (NativeDaemonConnectorException e) {
                throw e.rethrowAsParcelableException();
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void removeIdleTimer(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        synchronized (this.mIdleTimerLock) {
            IdleTimerParams params = this.mActiveIdleTimers.get(iface);
            if (params != null) {
                int i = params.networkCount - 1;
                params.networkCount = i;
                if (i <= 0) {
                    try {
                        this.mConnector.execute("idletimer", REMOVE, iface, Integer.toString(params.timeout), params.label);
                        this.mActiveIdleTimers.remove(iface);
                    } catch (NativeDaemonConnectorException e) {
                        throw e.rethrowAsParcelableException();
                    }
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public NetworkStats getNetworkStatsSummaryDev() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return this.mStatsFactory.readNetworkStatsSummaryDev();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public NetworkStats getNetworkStatsSummaryXt() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return this.mStatsFactory.readNetworkStatsSummaryXt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public NetworkStats getNetworkStatsDetail() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return this.mStatsFactory.readNetworkStatsDetail(-1);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceQuota(String iface, long quotaBytes) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            synchronized (this.mQuotaLock) {
                if (this.mActiveQuotas.containsKey(iface)) {
                    throw new IllegalStateException("iface " + iface + " already has quota");
                }
                try {
                    this.mConnector.execute("bandwidth", "setiquota", iface, Long.valueOf(quotaBytes));
                    this.mActiveQuotas.put(iface, Long.valueOf(quotaBytes));
                } catch (NativeDaemonConnectorException e) {
                    throw e.rethrowAsParcelableException();
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void removeInterfaceQuota(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            synchronized (this.mQuotaLock) {
                if (this.mActiveQuotas.containsKey(iface)) {
                    this.mActiveQuotas.remove(iface);
                    this.mActiveAlerts.remove(iface);
                    try {
                        this.mConnector.execute("bandwidth", "removeiquota", iface);
                    } catch (NativeDaemonConnectorException e) {
                        throw e.rethrowAsParcelableException();
                    }
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void setInterfaceAlert(String iface, long alertBytes) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            if (!this.mActiveQuotas.containsKey(iface)) {
                throw new IllegalStateException("setting alert requires existing quota on iface");
            }
            synchronized (this.mQuotaLock) {
                if (this.mActiveAlerts.containsKey(iface)) {
                    throw new IllegalStateException("iface " + iface + " already has alert");
                }
                try {
                    this.mConnector.execute("bandwidth", "setinterfacealert", iface, Long.valueOf(alertBytes));
                    this.mActiveAlerts.put(iface, Long.valueOf(alertBytes));
                } catch (NativeDaemonConnectorException e) {
                    throw e.rethrowAsParcelableException();
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void removeInterfaceAlert(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            synchronized (this.mQuotaLock) {
                if (this.mActiveAlerts.containsKey(iface)) {
                    try {
                        this.mConnector.execute("bandwidth", "removeinterfacealert", iface);
                        this.mActiveAlerts.remove(iface);
                    } catch (NativeDaemonConnectorException e) {
                        throw e.rethrowAsParcelableException();
                    }
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void setGlobalAlert(long alertBytes) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            try {
                this.mConnector.execute("bandwidth", "setglobalalert", Long.valueOf(alertBytes));
            } catch (NativeDaemonConnectorException e) {
                throw e.rethrowAsParcelableException();
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public void setUidNetworkRules(int uid, boolean rejectOnQuotaInterfaces) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        if (this.mBandwidthControlEnabled) {
            synchronized (this.mQuotaLock) {
                boolean oldRejectOnQuota = this.mUidRejectOnQuota.get(uid, false);
                if (oldRejectOnQuota == rejectOnQuotaInterfaces) {
                    return;
                }
                try {
                    NativeDaemonConnector nativeDaemonConnector = this.mConnector;
                    Object[] objArr = new Object[2];
                    objArr[0] = rejectOnQuotaInterfaces ? "addnaughtyapps" : "removenaughtyapps";
                    objArr[1] = Integer.valueOf(uid);
                    nativeDaemonConnector.execute("bandwidth", objArr);
                    if (rejectOnQuotaInterfaces) {
                        this.mUidRejectOnQuota.put(uid, true);
                    } else {
                        this.mUidRejectOnQuota.delete(uid);
                    }
                } catch (NativeDaemonConnectorException e) {
                    throw e.rethrowAsParcelableException();
                }
            }
        }
    }

    @Override // android.os.INetworkManagementService
    public boolean isBandwidthControlEnabled() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        return this.mBandwidthControlEnabled;
    }

    @Override // android.os.INetworkManagementService
    public NetworkStats getNetworkStatsUidDetail(int uid) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            return this.mStatsFactory.readNetworkStatsDetail(uid);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.os.INetworkManagementService
    public NetworkStats getNetworkStatsTethering() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        NetworkStats stats = new NetworkStats(SystemClock.elapsedRealtime(), 1);
        try {
            NativeDaemonEvent[] events = this.mConnector.executeForList("bandwidth", "gettetherstats");
            for (NativeDaemonEvent event : events) {
                if (event.getCode() == 114) {
                    StringTokenizer tok = new StringTokenizer(event.getMessage());
                    try {
                        tok.nextToken();
                        String ifaceOut = tok.nextToken();
                        NetworkStats.Entry entry = new NetworkStats.Entry();
                        entry.iface = ifaceOut;
                        entry.uid = -5;
                        entry.set = 0;
                        entry.tag = 0;
                        entry.rxBytes = Long.parseLong(tok.nextToken());
                        entry.rxPackets = Long.parseLong(tok.nextToken());
                        entry.txBytes = Long.parseLong(tok.nextToken());
                        entry.txPackets = Long.parseLong(tok.nextToken());
                        stats.combineValues(entry);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("problem parsing tethering stats: " + event);
                    } catch (NoSuchElementException e2) {
                        throw new IllegalStateException("problem parsing tethering stats: " + event);
                    }
                }
            }
            return stats;
        } catch (NativeDaemonConnectorException e3) {
            throw e3.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setDefaultInterfaceForDns(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "setdefaultif", iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setDnsServersForInterface(String iface, String[] servers, String domains) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        Object[] objArr = new Object[3];
        objArr[0] = "setifdns";
        objArr[1] = iface;
        objArr[2] = domains == null ? "" : domains;
        NativeDaemonConnector.Command cmd = new NativeDaemonConnector.Command("resolver", objArr);
        for (String s : servers) {
            InetAddress a = NetworkUtils.numericToInetAddress(s);
            if (!a.isAnyLocalAddress()) {
                cmd.appendArg(a.getHostAddress());
            }
        }
        try {
            this.mConnector.execute(cmd);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setUidRangeRoute(String iface, int uid_start, int uid_end) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, ADD, iface, Integer.valueOf(uid_start), Integer.valueOf(uid_end));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearUidRangeRoute(String iface, int uid_start, int uid_end) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, REMOVE, iface, Integer.valueOf(uid_start), Integer.valueOf(uid_end));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setMarkedForwarding(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", "rule", ADD, iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearMarkedForwarding(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", "rule", REMOVE, iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public int getMarkForUid(int uid) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("interface", "fwmark", "get", "mark", Integer.valueOf(uid));
            event.checkCode(225);
            return Integer.parseInt(event.getMessage());
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public int getMarkForProtect() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("interface", "fwmark", "get", "protect");
            event.checkCode(225);
            return Integer.parseInt(event.getMessage());
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setMarkedForwardingRoute(String iface, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            LinkAddress dest = route.getDestination();
            this.mConnector.execute("interface", "fwmark", "route", ADD, iface, dest.getAddress().getHostAddress(), Integer.valueOf(dest.getNetworkPrefixLength()));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearMarkedForwardingRoute(String iface, RouteInfo route) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            LinkAddress dest = route.getDestination();
            this.mConnector.execute("interface", "fwmark", "route", REMOVE, iface, dest.getAddress().getHostAddress(), Integer.valueOf(dest.getNetworkPrefixLength()));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setHostExemption(LinkAddress host) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", "exempt", ADD, host);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearHostExemption(LinkAddress host) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("interface", "fwmark", "exempt", REMOVE, host);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setDnsInterfaceForUidRange(String iface, int uid_start, int uid_end) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "setifaceforuidrange", iface, Integer.valueOf(uid_start), Integer.valueOf(uid_end));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearDnsInterfaceForUidRange(int uid_start, int uid_end) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "clearifaceforuidrange", Integer.valueOf(uid_start), Integer.valueOf(uid_end));
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearDnsInterfaceMaps() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "clearifacemapping");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void flushDefaultDnsCache() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "flushdefaultif");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void flushInterfaceDnsCache(String iface) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "flushif", iface);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setFirewallEnabled(boolean enabled) {
        enforceSystemUid();
        try {
            NativeDaemonConnector nativeDaemonConnector = this.mConnector;
            Object[] objArr = new Object[1];
            objArr[0] = enabled ? "enable" : "disable";
            nativeDaemonConnector.execute("firewall", objArr);
            this.mFirewallEnabled = enabled;
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public boolean isFirewallEnabled() {
        enforceSystemUid();
        return this.mFirewallEnabled;
    }

    @Override // android.os.INetworkManagementService
    public void setFirewallInterfaceRule(String iface, boolean allow) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        String rule = allow ? ALLOW : DENY;
        try {
            this.mConnector.execute("firewall", "set_interface_rule", iface, rule);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setFirewallEgressSourceRule(String addr, boolean allow) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        String rule = allow ? ALLOW : DENY;
        try {
            this.mConnector.execute("firewall", "set_egress_source_rule", addr, rule);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setFirewallEgressDestRule(String addr, int port, boolean allow) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        String rule = allow ? ALLOW : DENY;
        try {
            this.mConnector.execute("firewall", "set_egress_dest_rule", addr, Integer.valueOf(port), rule);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void setFirewallUidRule(int uid, boolean allow) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        String rule = allow ? ALLOW : DENY;
        try {
            this.mConnector.execute("firewall", "set_uid_rule", Integer.valueOf(uid), rule);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    private static void enforceSystemUid() {
        int uid = Binder.getCallingUid();
        if (uid != 1000) {
            throw new SecurityException("Only available to AID_SYSTEM");
        }
    }

    @Override // android.os.INetworkManagementService
    public void setDnsInterfaceForPid(String iface, int pid) throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "setifaceforpid", iface, Integer.valueOf(pid));
        } catch (NativeDaemonConnectorException e) {
            throw new IllegalStateException("Error communicating with native deamon to set interface for pid" + iface, e);
        }
    }

    @Override // android.os.INetworkManagementService
    public void clearDnsInterfaceForPid(int pid) throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("resolver", "clearifaceforpid", Integer.valueOf(pid));
        } catch (NativeDaemonConnectorException e) {
            throw new IllegalStateException("Error communicating with native deamon to clear interface for pid " + pid, e);
        }
    }

    @Override // android.os.INetworkManagementService
    public void startClatd(String interfaceName) throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("clatd", Telephony.BaseMmsColumns.START, interfaceName);
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public void stopClatd() throws IllegalStateException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            this.mConnector.execute("clatd", "stop");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // android.os.INetworkManagementService
    public boolean isClatdStarted() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.CONNECTIVITY_INTERNAL, TAG);
        try {
            NativeDaemonEvent event = this.mConnector.execute("clatd", "status");
            event.checkCode(223);
            return event.getMessage().endsWith("started");
        } catch (NativeDaemonConnectorException e) {
            throw e.rethrowAsParcelableException();
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        if (this.mConnector != null) {
            this.mConnector.monitor();
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        pw.println("NetworkManagementService NativeDaemonConnector Log:");
        this.mConnector.dump(fd, pw, args);
        pw.println();
        pw.print("Bandwidth control enabled: ");
        pw.println(this.mBandwidthControlEnabled);
        synchronized (this.mQuotaLock) {
            pw.print("Active quota ifaces: ");
            pw.println(this.mActiveQuotas.toString());
            pw.print("Active alert ifaces: ");
            pw.println(this.mActiveAlerts.toString());
        }
        synchronized (this.mUidRejectOnQuota) {
            pw.print("UID reject on quota ifaces: [");
            int size = this.mUidRejectOnQuota.size();
            for (int i = 0; i < size; i++) {
                pw.print(this.mUidRejectOnQuota.keyAt(i));
                if (i < size - 1) {
                    pw.print(Separators.COMMA);
                }
            }
            pw.println("]");
        }
        pw.print("Firewall enabled: ");
        pw.println(this.mFirewallEnabled);
    }
}