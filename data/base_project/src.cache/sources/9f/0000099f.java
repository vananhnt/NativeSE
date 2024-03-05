package android.net.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WpsResult;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WifiConfigStore.class */
public class WifiConfigStore {
    private Context mContext;
    private static final String TAG = "WifiConfigStore";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private static final String SUPPLICANT_CONFIG_FILE = "/data/misc/wifi/wpa_supplicant.conf";
    private static final String ipConfigFile = Environment.getDataDirectory() + "/misc/wifi/ipconfig.txt";
    private static final int IPCONFIG_FILE_VERSION = 2;
    private static final String ID_KEY = "id";
    private static final String IP_ASSIGNMENT_KEY = "ipAssignment";
    private static final String LINK_ADDRESS_KEY = "linkAddress";
    private static final String GATEWAY_KEY = "gateway";
    private static final String DNS_KEY = "dns";
    private static final String PROXY_SETTINGS_KEY = "proxySettings";
    private static final String PROXY_HOST_KEY = "proxyHost";
    private static final String PROXY_PORT_KEY = "proxyPort";
    private static final String PROXY_PAC_FILE = "proxyPac";
    private static final String EXCLUSION_LIST_KEY = "exclusionList";
    private static final String EOS = "eos";
    private WifiNative mWifiNative;
    private HashMap<Integer, WifiConfiguration> mConfiguredNetworks = new HashMap<>();
    private HashMap<Integer, Integer> mNetworkIds = new HashMap<>();
    private int mLastPriority = -1;
    private final KeyStore mKeyStore = KeyStore.getInstance();
    private final LocalLog mLocalLog = null;
    private final WpaConfigFileObserver mFileObserver = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiConfigStore(Context c, WifiNative wn) {
        this.mContext = c;
        this.mWifiNative = wn;
    }

    /* loaded from: WifiConfigStore$WpaConfigFileObserver.class */
    class WpaConfigFileObserver extends FileObserver {
        public WpaConfigFileObserver() {
            super(WifiConfigStore.SUPPLICANT_CONFIG_FILE, 8);
        }

        @Override // android.os.FileObserver
        public void onEvent(int event, String path) {
            if (event == 8) {
                new File(WifiConfigStore.SUPPLICANT_CONFIG_FILE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadAndEnableAllNetworks() {
        log("Loading config and enabling all networks");
        loadConfiguredNetworks();
        enableAllNetworks();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<WifiConfiguration> getConfiguredNetworks() {
        List<WifiConfiguration> networks = new ArrayList<>();
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            networks.add(new WifiConfiguration(config));
        }
        return networks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableAllNetworks() {
        boolean networkEnabledStateChanged = false;
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            if (config != null && config.status == 1) {
                if (this.mWifiNative.enableNetwork(config.networkId, false)) {
                    networkEnabledStateChanged = true;
                    config.status = 2;
                } else {
                    loge("Enable network failed on " + config.networkId);
                }
            }
        }
        if (networkEnabledStateChanged) {
            this.mWifiNative.saveConfig();
            sendConfiguredNetworksChangedBroadcast();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean selectNetwork(int netId) {
        if (netId == -1) {
            return false;
        }
        if (this.mLastPriority == -1 || this.mLastPriority > 1000000) {
            for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
                if (config.networkId != -1) {
                    config.priority = 0;
                    addOrUpdateNetworkNative(config);
                }
            }
            this.mLastPriority = 0;
        }
        WifiConfiguration config2 = new WifiConfiguration();
        config2.networkId = netId;
        int i = this.mLastPriority + 1;
        this.mLastPriority = i;
        config2.priority = i;
        addOrUpdateNetworkNative(config2);
        this.mWifiNative.saveConfig();
        enableNetworkWithoutBroadcast(netId, true);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NetworkUpdateResult saveNetwork(WifiConfiguration config) {
        if (config == null || (config.networkId == -1 && config.SSID == null)) {
            return new NetworkUpdateResult(-1);
        }
        boolean newNetwork = config.networkId == -1;
        NetworkUpdateResult result = addOrUpdateNetworkNative(config);
        int netId = result.getNetworkId();
        if (newNetwork && netId != -1) {
            this.mWifiNative.enableNetwork(netId, false);
            this.mConfiguredNetworks.get(Integer.valueOf(netId)).status = 2;
        }
        this.mWifiNative.saveConfig();
        sendConfiguredNetworksChangedBroadcast(config, result.isNewNetwork() ? 0 : 2);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateStatus(int netId, NetworkInfo.DetailedState state) {
        WifiConfiguration config;
        if (netId == -1 || (config = this.mConfiguredNetworks.get(Integer.valueOf(netId))) == null) {
            return;
        }
        switch (state) {
            case CONNECTED:
                config.status = 0;
                return;
            case DISCONNECTED:
                if (config.status == 0) {
                    config.status = 2;
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean forgetNetwork(int netId) {
        if (this.mWifiNative.removeNetwork(netId)) {
            this.mWifiNative.saveConfig();
            removeConfigAndSendBroadcastIfNeeded(netId);
            return true;
        }
        loge("Failed to remove network " + netId);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addOrUpdateNetwork(WifiConfiguration config) {
        NetworkUpdateResult result = addOrUpdateNetworkNative(config);
        if (result.getNetworkId() != -1) {
            sendConfiguredNetworksChangedBroadcast(this.mConfiguredNetworks.get(Integer.valueOf(result.getNetworkId())), result.isNewNetwork ? 0 : 2);
        }
        return result.getNetworkId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeNetwork(int netId) {
        boolean ret = this.mWifiNative.removeNetwork(netId);
        if (ret) {
            removeConfigAndSendBroadcastIfNeeded(netId);
        }
        return ret;
    }

    private void removeConfigAndSendBroadcastIfNeeded(int netId) {
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null) {
            if (config.enterpriseConfig != null) {
                config.enterpriseConfig.removeKeys(this.mKeyStore);
            }
            this.mConfiguredNetworks.remove(Integer.valueOf(netId));
            this.mNetworkIds.remove(Integer.valueOf(configKey(config)));
            writeIpAndProxyConfigurations();
            sendConfiguredNetworksChangedBroadcast(config, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean enableNetwork(int netId, boolean disableOthers) {
        WifiConfiguration enabledNetwork;
        boolean ret = enableNetworkWithoutBroadcast(netId, disableOthers);
        if (disableOthers) {
            sendConfiguredNetworksChangedBroadcast();
        } else {
            synchronized (this.mConfiguredNetworks) {
                enabledNetwork = this.mConfiguredNetworks.get(Integer.valueOf(netId));
            }
            if (enabledNetwork != null) {
                sendConfiguredNetworksChangedBroadcast(enabledNetwork, 2);
            }
        }
        return ret;
    }

    boolean enableNetworkWithoutBroadcast(int netId, boolean disableOthers) {
        boolean ret = this.mWifiNative.enableNetwork(netId, disableOthers);
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null) {
            config.status = 2;
        }
        if (disableOthers) {
            markAllNetworksDisabledExcept(netId);
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableAllNetworks() {
        boolean networkDisabled = false;
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            if (config != null && config.status != 1) {
                if (this.mWifiNative.disableNetwork(config.networkId)) {
                    networkDisabled = true;
                    config.status = 1;
                } else {
                    loge("Disable network failed on " + config.networkId);
                }
            }
        }
        if (networkDisabled) {
            sendConfiguredNetworksChangedBroadcast();
        }
    }

    boolean disableNetwork(int netId) {
        return disableNetwork(netId, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean disableNetwork(int netId, int reason) {
        boolean ret = this.mWifiNative.disableNetwork(netId);
        WifiConfiguration network = null;
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null && config.status != 1) {
            config.status = 1;
            config.disableReason = reason;
            network = config;
        }
        if (network != null) {
            sendConfiguredNetworksChangedBroadcast(network, 2);
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean saveConfig() {
        return this.mWifiNative.saveConfig();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WpsResult startWpsWithPinFromAccessPoint(WpsInfo config) {
        WpsResult result = new WpsResult();
        if (this.mWifiNative.startWpsRegistrar(config.BSSID, config.pin)) {
            markAllNetworksDisabled();
            result.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS pin method configuration");
            result.status = WpsResult.Status.FAILURE;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WpsResult startWpsWithPinFromDevice(WpsInfo config) {
        WpsResult result = new WpsResult();
        result.pin = this.mWifiNative.startWpsPinDisplay(config.BSSID);
        if (!TextUtils.isEmpty(result.pin)) {
            markAllNetworksDisabled();
            result.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS pin method configuration");
            result.status = WpsResult.Status.FAILURE;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WpsResult startWpsPbc(WpsInfo config) {
        WpsResult result = new WpsResult();
        if (this.mWifiNative.startWpsPbc(config.BSSID)) {
            markAllNetworksDisabled();
            result.status = WpsResult.Status.SUCCESS;
        } else {
            loge("Failed to start WPS push button configuration");
            result.status = WpsResult.Status.FAILURE;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LinkProperties getLinkProperties(int netId) {
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null) {
            return new LinkProperties(config.linkProperties);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLinkProperties(int netId, LinkProperties linkProperties) {
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null) {
            if (config.linkProperties != null) {
                linkProperties.setHttpProxy(config.linkProperties.getHttpProxy());
            }
            config.linkProperties = linkProperties;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearLinkProperties(int netId) {
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null && config.linkProperties != null) {
            ProxyProperties proxy = config.linkProperties.getHttpProxy();
            config.linkProperties.clear();
            config.linkProperties.setHttpProxy(proxy);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProxyProperties getProxyProperties(int netId) {
        LinkProperties linkProperties = getLinkProperties(netId);
        if (linkProperties != null) {
            return new ProxyProperties(linkProperties.getHttpProxy());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUsingStaticIp(int netId) {
        WifiConfiguration config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (config != null && config.ipAssignment == WifiConfiguration.IpAssignment.STATIC) {
            return true;
        }
        return false;
    }

    private void sendConfiguredNetworksChangedBroadcast(WifiConfiguration network, int reason) {
        Intent intent = new Intent(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_MULTIPLE_NETWORKS_CHANGED, false);
        intent.putExtra(WifiManager.EXTRA_WIFI_CONFIGURATION, network);
        intent.putExtra(WifiManager.EXTRA_CHANGE_REASON, reason);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void sendConfiguredNetworksChangedBroadcast() {
        Intent intent = new Intent(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        intent.addFlags(67108864);
        intent.putExtra(WifiManager.EXTRA_MULTIPLE_NETWORKS_CHANGED, true);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadConfiguredNetworks() {
        String listStr = this.mWifiNative.listNetworks();
        this.mLastPriority = 0;
        this.mConfiguredNetworks.clear();
        this.mNetworkIds.clear();
        if (listStr == null) {
            return;
        }
        String[] lines = listStr.split(Separators.RETURN);
        for (int i = 1; i < lines.length; i++) {
            String[] result = lines[i].split(Separators.HT);
            WifiConfiguration config = new WifiConfiguration();
            try {
                config.networkId = Integer.parseInt(result[0]);
                if (result.length <= 3) {
                    config.status = 2;
                } else if (result[3].indexOf("[CURRENT]") != -1) {
                    config.status = 0;
                } else if (result[3].indexOf("[DISABLED]") != -1) {
                    config.status = 1;
                } else {
                    config.status = 2;
                }
                readNetworkVariables(config);
                if (config.priority > this.mLastPriority) {
                    this.mLastPriority = config.priority;
                }
                config.ipAssignment = WifiConfiguration.IpAssignment.DHCP;
                config.proxySettings = WifiConfiguration.ProxySettings.NONE;
                if (!this.mNetworkIds.containsKey(Integer.valueOf(configKey(config)))) {
                    this.mConfiguredNetworks.put(Integer.valueOf(config.networkId), config);
                    this.mNetworkIds.put(Integer.valueOf(configKey(config)), Integer.valueOf(config.networkId));
                }
            } catch (NumberFormatException e) {
                loge("Failed to read network-id '" + result[0] + Separators.QUOTE);
            }
        }
        readIpAndProxyConfigurations();
        sendConfiguredNetworksChangedBroadcast();
        if (this.mNetworkIds.size() == 0) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(SUPPLICANT_CONFIG_FILE));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (FileNotFoundException e3) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e4) {
                    }
                }
            } catch (IOException e5) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e6) {
                    }
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e7) {
                        throw th;
                    }
                }
                throw th;
            }
        }
    }

    private void markAllNetworksDisabledExcept(int netId) {
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            if (config != null && config.networkId != netId && config.status != 1) {
                config.status = 1;
                config.disableReason = 0;
            }
        }
    }

    private void markAllNetworksDisabled() {
        markAllNetworksDisabledExcept(-1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needsUnlockedKeyStore() {
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            if (config.allowedKeyManagement.get(2) && config.allowedKeyManagement.get(3) && config.enterpriseConfig.needsSoftwareBackedKeyStore()) {
                return true;
            }
        }
        return false;
    }

    private void writeIpAndProxyConfigurations() {
        List<WifiConfiguration> networks = new ArrayList<>();
        for (WifiConfiguration config : this.mConfiguredNetworks.values()) {
            networks.add(new WifiConfiguration(config));
        }
        DelayedDiskWrite.write(networks);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiConfigStore$DelayedDiskWrite.class */
    public static class DelayedDiskWrite {
        private static HandlerThread sDiskWriteHandlerThread;
        private static Handler sDiskWriteHandler;
        private static int sWriteSequence = 0;
        private static final String TAG = "DelayedDiskWrite";

        private DelayedDiskWrite() {
        }

        static void write(final List<WifiConfiguration> networks) {
            synchronized (DelayedDiskWrite.class) {
                int i = sWriteSequence + 1;
                sWriteSequence = i;
                if (i == 1) {
                    sDiskWriteHandlerThread = new HandlerThread("WifiConfigThread");
                    sDiskWriteHandlerThread.start();
                    sDiskWriteHandler = new Handler(sDiskWriteHandlerThread.getLooper());
                }
            }
            sDiskWriteHandler.post(new Runnable() { // from class: android.net.wifi.WifiConfigStore.DelayedDiskWrite.1
                @Override // java.lang.Runnable
                public void run() {
                    DelayedDiskWrite.onWriteCalled(List.this);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:35:0x01b4 A[Catch: NullPointerException -> 0x0265, IOException -> 0x02cc, all -> 0x0310, TryCatch #8 {IOException -> 0x02cc, blocks: (B:3:0x0002, B:4:0x0027, B:6:0x0030, B:7:0x003d, B:8:0x004e, B:9:0x0068, B:10:0x0085, B:12:0x008f, B:13:0x00b9, B:14:0x00c5, B:16:0x00cf, B:18:0x00ed, B:20:0x010f, B:22:0x0117, B:23:0x012b, B:19:0x010a, B:25:0x0133, B:26:0x013f, B:28:0x0149, B:33:0x018c, B:34:0x0197, B:35:0x01b4, B:42:0x0254, B:46:0x0285, B:36:0x0203, B:37:0x0230, B:39:0x024a, B:30:0x016d, B:32:0x0187, B:45:0x0267), top: B:105:0x0002, outer: #0 }] */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0203 A[Catch: NullPointerException -> 0x0265, IOException -> 0x02cc, all -> 0x0310, TryCatch #8 {IOException -> 0x02cc, blocks: (B:3:0x0002, B:4:0x0027, B:6:0x0030, B:7:0x003d, B:8:0x004e, B:9:0x0068, B:10:0x0085, B:12:0x008f, B:13:0x00b9, B:14:0x00c5, B:16:0x00cf, B:18:0x00ed, B:20:0x010f, B:22:0x0117, B:23:0x012b, B:19:0x010a, B:25:0x0133, B:26:0x013f, B:28:0x0149, B:33:0x018c, B:34:0x0197, B:35:0x01b4, B:42:0x0254, B:46:0x0285, B:36:0x0203, B:37:0x0230, B:39:0x024a, B:30:0x016d, B:32:0x0187, B:45:0x0267), top: B:105:0x0002, outer: #0 }] */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0230 A[Catch: NullPointerException -> 0x0265, IOException -> 0x02cc, all -> 0x0310, TryCatch #8 {IOException -> 0x02cc, blocks: (B:3:0x0002, B:4:0x0027, B:6:0x0030, B:7:0x003d, B:8:0x004e, B:9:0x0068, B:10:0x0085, B:12:0x008f, B:13:0x00b9, B:14:0x00c5, B:16:0x00cf, B:18:0x00ed, B:20:0x010f, B:22:0x0117, B:23:0x012b, B:19:0x010a, B:25:0x0133, B:26:0x013f, B:28:0x0149, B:33:0x018c, B:34:0x0197, B:35:0x01b4, B:42:0x0254, B:46:0x0285, B:36:0x0203, B:37:0x0230, B:39:0x024a, B:30:0x016d, B:32:0x0187, B:45:0x0267), top: B:105:0x0002, outer: #0 }] */
        /* JADX WARN: Removed duplicated region for block: B:38:0x0247  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x024a A[Catch: NullPointerException -> 0x0265, IOException -> 0x02cc, all -> 0x0310, TryCatch #8 {IOException -> 0x02cc, blocks: (B:3:0x0002, B:4:0x0027, B:6:0x0030, B:7:0x003d, B:8:0x004e, B:9:0x0068, B:10:0x0085, B:12:0x008f, B:13:0x00b9, B:14:0x00c5, B:16:0x00cf, B:18:0x00ed, B:20:0x010f, B:22:0x0117, B:23:0x012b, B:19:0x010a, B:25:0x0133, B:26:0x013f, B:28:0x0149, B:33:0x018c, B:34:0x0197, B:35:0x01b4, B:42:0x0254, B:46:0x0285, B:36:0x0203, B:37:0x0230, B:39:0x024a, B:30:0x016d, B:32:0x0187, B:45:0x0267), top: B:105:0x0002, outer: #0 }] */
        /* JADX WARN: Removed duplicated region for block: B:42:0x0254 A[Catch: NullPointerException -> 0x0265, IOException -> 0x02cc, all -> 0x0310, TryCatch #8 {IOException -> 0x02cc, blocks: (B:3:0x0002, B:4:0x0027, B:6:0x0030, B:7:0x003d, B:8:0x004e, B:9:0x0068, B:10:0x0085, B:12:0x008f, B:13:0x00b9, B:14:0x00c5, B:16:0x00cf, B:18:0x00ed, B:20:0x010f, B:22:0x0117, B:23:0x012b, B:19:0x010a, B:25:0x0133, B:26:0x013f, B:28:0x0149, B:33:0x018c, B:34:0x0197, B:35:0x01b4, B:42:0x0254, B:46:0x0285, B:36:0x0203, B:37:0x0230, B:39:0x024a, B:30:0x016d, B:32:0x0187, B:45:0x0267), top: B:105:0x0002, outer: #0 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public static void onWriteCalled(java.util.List<android.net.wifi.WifiConfiguration> r8) {
            /*
                Method dump skipped, instructions count: 853
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiConfigStore.DelayedDiskWrite.onWriteCalled(java.util.List):void");
        }

        private static void loge(String s) {
            Log.e(TAG, s);
        }
    }

    private void readIpAndProxyConfigurations() {
        DataInputStream in = null;
        try {
            try {
                DataInputStream in2 = new DataInputStream(new BufferedInputStream(new FileInputStream(ipConfigFile)));
                int version = in2.readInt();
                if (version != 2 && version != 1) {
                    loge("Bad version on IP configuration file, ignore read");
                    if (in2 != null) {
                        try {
                            in2.close();
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    return;
                }
                while (true) {
                    int id = -1;
                    WifiConfiguration.IpAssignment ipAssignment = WifiConfiguration.IpAssignment.DHCP;
                    WifiConfiguration.ProxySettings proxySettings = WifiConfiguration.ProxySettings.NONE;
                    LinkProperties linkProperties = new LinkProperties();
                    String proxyHost = null;
                    String pacFileUrl = null;
                    int proxyPort = -1;
                    String exclusionList = null;
                    while (true) {
                        String key = in2.readUTF();
                        try {
                            if (key.equals("id")) {
                                id = in2.readInt();
                            } else if (key.equals(IP_ASSIGNMENT_KEY)) {
                                ipAssignment = WifiConfiguration.IpAssignment.valueOf(in2.readUTF());
                            } else if (key.equals(LINK_ADDRESS_KEY)) {
                                LinkAddress linkAddr = new LinkAddress(NetworkUtils.numericToInetAddress(in2.readUTF()), in2.readInt());
                                linkProperties.addLinkAddress(linkAddr);
                            } else if (key.equals(GATEWAY_KEY)) {
                                LinkAddress dest = null;
                                InetAddress gateway = null;
                                if (version == 1) {
                                    gateway = NetworkUtils.numericToInetAddress(in2.readUTF());
                                } else {
                                    if (in2.readInt() == 1) {
                                        dest = new LinkAddress(NetworkUtils.numericToInetAddress(in2.readUTF()), in2.readInt());
                                    }
                                    if (in2.readInt() == 1) {
                                        gateway = NetworkUtils.numericToInetAddress(in2.readUTF());
                                    }
                                }
                                linkProperties.addRoute(new RouteInfo(dest, gateway));
                            } else if (key.equals(DNS_KEY)) {
                                linkProperties.addDns(NetworkUtils.numericToInetAddress(in2.readUTF()));
                            } else if (key.equals(PROXY_SETTINGS_KEY)) {
                                proxySettings = WifiConfiguration.ProxySettings.valueOf(in2.readUTF());
                            } else if (key.equals(PROXY_HOST_KEY)) {
                                proxyHost = in2.readUTF();
                            } else if (key.equals(PROXY_PORT_KEY)) {
                                proxyPort = in2.readInt();
                            } else if (key.equals(PROXY_PAC_FILE)) {
                                pacFileUrl = in2.readUTF();
                            } else if (key.equals(EXCLUSION_LIST_KEY)) {
                                exclusionList = in2.readUTF();
                            } else if (!key.equals(EOS)) {
                                loge("Ignore unknown key " + key + "while reading");
                            } else if (id != -1) {
                                WifiConfiguration config = this.mConfiguredNetworks.get(this.mNetworkIds.get(Integer.valueOf(id)));
                                if (config != null) {
                                    config.linkProperties = linkProperties;
                                    switch (ipAssignment) {
                                        case STATIC:
                                        case DHCP:
                                            config.ipAssignment = ipAssignment;
                                            break;
                                        case UNASSIGNED:
                                            loge("BUG: Found UNASSIGNED IP on file, use DHCP");
                                            config.ipAssignment = WifiConfiguration.IpAssignment.DHCP;
                                            break;
                                        default:
                                            loge("Ignore invalid ip assignment while reading");
                                            break;
                                    }
                                    switch (proxySettings) {
                                        case STATIC:
                                            config.proxySettings = proxySettings;
                                            ProxyProperties proxyProperties = new ProxyProperties(proxyHost, proxyPort, exclusionList);
                                            linkProperties.setHttpProxy(proxyProperties);
                                            break;
                                        case PAC:
                                            config.proxySettings = proxySettings;
                                            ProxyProperties proxyPacProperties = new ProxyProperties(pacFileUrl);
                                            linkProperties.setHttpProxy(proxyPacProperties);
                                            break;
                                        case NONE:
                                            config.proxySettings = proxySettings;
                                            break;
                                        case UNASSIGNED:
                                            loge("BUG: Found UNASSIGNED proxy on file, use NONE");
                                            config.proxySettings = WifiConfiguration.ProxySettings.NONE;
                                            break;
                                        default:
                                            loge("Ignore invalid proxy settings while reading");
                                            break;
                                    }
                                } else {
                                    loge("configuration found for missing network, ignored");
                                }
                            } else {
                                log("Missing id while parsing configuration");
                            }
                        } catch (IllegalArgumentException e2) {
                            loge("Ignore invalid address while reading" + e2);
                        }
                    }
                }
            } catch (EOFException e3) {
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (Exception e4) {
                    }
                }
            } catch (IOException e5) {
                loge("Error parsing configuration" + e5);
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (Exception e6) {
                    }
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    in.close();
                } catch (Exception e7) {
                }
            }
            throw th;
        }
    }

    private NetworkUpdateResult addOrUpdateNetworkNative(WifiConfiguration config) {
        int netId = config.networkId;
        boolean newNetwork = false;
        if (netId == -1) {
            Integer savedNetId = this.mNetworkIds.get(Integer.valueOf(configKey(config)));
            if (savedNetId != null) {
                netId = savedNetId.intValue();
            } else {
                newNetwork = true;
                netId = this.mWifiNative.addNetwork();
                if (netId < 0) {
                    loge("Failed to add a network!");
                    return new NetworkUpdateResult(-1);
                }
            }
        }
        boolean updateFailed = true;
        if (config.SSID != null && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.ssidVarName, config.SSID)) {
            loge("failed to set SSID: " + config.SSID);
        } else if (config.BSSID != null && !this.mWifiNative.setNetworkVariable(netId, "bssid", config.BSSID)) {
            loge("failed to set BSSID: " + config.BSSID);
        } else {
            String allowedKeyManagementString = makeString(config.allowedKeyManagement, WifiConfiguration.KeyMgmt.strings);
            if (config.allowedKeyManagement.cardinality() != 0 && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.KeyMgmt.varName, allowedKeyManagementString)) {
                loge("failed to set key_mgmt: " + allowedKeyManagementString);
            } else {
                String allowedProtocolsString = makeString(config.allowedProtocols, WifiConfiguration.Protocol.strings);
                if (config.allowedProtocols.cardinality() != 0 && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.Protocol.varName, allowedProtocolsString)) {
                    loge("failed to set proto: " + allowedProtocolsString);
                } else {
                    String allowedAuthAlgorithmsString = makeString(config.allowedAuthAlgorithms, WifiConfiguration.AuthAlgorithm.strings);
                    if (config.allowedAuthAlgorithms.cardinality() != 0 && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.AuthAlgorithm.varName, allowedAuthAlgorithmsString)) {
                        loge("failed to set auth_alg: " + allowedAuthAlgorithmsString);
                    } else {
                        String allowedPairwiseCiphersString = makeString(config.allowedPairwiseCiphers, WifiConfiguration.PairwiseCipher.strings);
                        if (config.allowedPairwiseCiphers.cardinality() != 0 && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.PairwiseCipher.varName, allowedPairwiseCiphersString)) {
                            loge("failed to set pairwise: " + allowedPairwiseCiphersString);
                        } else {
                            String allowedGroupCiphersString = makeString(config.allowedGroupCiphers, WifiConfiguration.GroupCipher.strings);
                            if (config.allowedGroupCiphers.cardinality() != 0 && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.GroupCipher.varName, allowedGroupCiphersString)) {
                                loge("failed to set group: " + allowedGroupCiphersString);
                            } else if (config.preSharedKey != null && !config.preSharedKey.equals("*") && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.pskVarName, config.preSharedKey)) {
                                loge("failed to set psk");
                            } else {
                                boolean hasSetKey = false;
                                if (config.wepKeys != null) {
                                    for (int i = 0; i < config.wepKeys.length; i++) {
                                        if (config.wepKeys[i] != null && !config.wepKeys[i].equals("*")) {
                                            if (!this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.wepKeyVarNames[i], config.wepKeys[i])) {
                                                loge("failed to set wep_key" + i + ": " + config.wepKeys[i]);
                                                break;
                                            }
                                            hasSetKey = true;
                                        }
                                    }
                                }
                                if (hasSetKey && !this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.wepTxKeyIdxVarName, Integer.toString(config.wepTxKeyIndex))) {
                                    loge("failed to set wep_tx_keyidx: " + config.wepTxKeyIndex);
                                } else if (!this.mWifiNative.setNetworkVariable(netId, "priority", Integer.toString(config.priority))) {
                                    loge(config.SSID + ": failed to set priority: " + config.priority);
                                } else {
                                    if (config.hiddenSSID) {
                                        if (!this.mWifiNative.setNetworkVariable(netId, WifiConfiguration.hiddenSSIDVarName, Integer.toString(config.hiddenSSID ? 1 : 0))) {
                                            loge(config.SSID + ": failed to set hiddenSSID: " + config.hiddenSSID);
                                        }
                                    }
                                    if (config.enterpriseConfig != null && config.enterpriseConfig.getEapMethod() != -1) {
                                        WifiEnterpriseConfig enterpriseConfig = config.enterpriseConfig;
                                        if (enterpriseConfig.needsKeyStore()) {
                                            if (this.mKeyStore.state() != KeyStore.State.UNLOCKED) {
                                                loge(config.SSID + ": key store is locked");
                                            } else {
                                                try {
                                                    String keyId = config.getKeyIdForCredentials(this.mConfiguredNetworks.get(Integer.valueOf(netId)));
                                                    if (!enterpriseConfig.installKeys(this.mKeyStore, keyId)) {
                                                        loge(config.SSID + ": failed to install keys");
                                                    }
                                                } catch (IllegalStateException e) {
                                                    loge(config.SSID + " invalid config for key installation");
                                                }
                                            }
                                        }
                                        HashMap<String, String> enterpriseFields = enterpriseConfig.getFields();
                                        for (String key : enterpriseFields.keySet()) {
                                            String value = enterpriseFields.get(key);
                                            if (!this.mWifiNative.setNetworkVariable(netId, key, value)) {
                                                enterpriseConfig.removeKeys(this.mKeyStore);
                                                loge(config.SSID + ": failed to set " + key + ": " + value);
                                                break;
                                            }
                                        }
                                    }
                                    updateFailed = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (updateFailed) {
            if (newNetwork) {
                this.mWifiNative.removeNetwork(netId);
                loge("Failed to set a network variable, removed network: " + netId);
            }
            return new NetworkUpdateResult(-1);
        }
        WifiConfiguration currentConfig = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        if (currentConfig == null) {
            currentConfig = new WifiConfiguration();
            currentConfig.ipAssignment = WifiConfiguration.IpAssignment.DHCP;
            currentConfig.proxySettings = WifiConfiguration.ProxySettings.NONE;
            currentConfig.networkId = netId;
        }
        readNetworkVariables(currentConfig);
        this.mConfiguredNetworks.put(Integer.valueOf(netId), currentConfig);
        this.mNetworkIds.put(Integer.valueOf(configKey(currentConfig)), Integer.valueOf(netId));
        NetworkUpdateResult result = writeIpAndProxyConfigurationsOnChange(currentConfig, config);
        result.setIsNewNetwork(newNetwork);
        result.setNetworkId(netId);
        return result;
    }

    private NetworkUpdateResult writeIpAndProxyConfigurationsOnChange(WifiConfiguration currentConfig, WifiConfiguration newConfig) {
        LinkProperties linkProperties;
        boolean ipChanged = false;
        boolean proxyChanged = false;
        switch (newConfig.ipAssignment) {
            case STATIC:
                Collection<LinkAddress> currentLinkAddresses = currentConfig.linkProperties.getLinkAddresses();
                Collection<LinkAddress> newLinkAddresses = newConfig.linkProperties.getLinkAddresses();
                Collection<InetAddress> currentDnses = currentConfig.linkProperties.getDnses();
                Collection<InetAddress> newDnses = newConfig.linkProperties.getDnses();
                Collection<RouteInfo> currentRoutes = currentConfig.linkProperties.getRoutes();
                Collection<RouteInfo> newRoutes = newConfig.linkProperties.getRoutes();
                boolean linkAddressesDiffer = (currentLinkAddresses.size() == newLinkAddresses.size() && currentLinkAddresses.containsAll(newLinkAddresses)) ? false : true;
                boolean dnsesDiffer = (currentDnses.size() == newDnses.size() && currentDnses.containsAll(newDnses)) ? false : true;
                boolean routesDiffer = (currentRoutes.size() == newRoutes.size() && currentRoutes.containsAll(newRoutes)) ? false : true;
                if (currentConfig.ipAssignment != newConfig.ipAssignment || linkAddressesDiffer || dnsesDiffer || routesDiffer) {
                    ipChanged = true;
                    break;
                }
                break;
            case DHCP:
                if (currentConfig.ipAssignment != newConfig.ipAssignment) {
                    ipChanged = true;
                    break;
                }
                break;
            case UNASSIGNED:
                break;
            default:
                loge("Ignore invalid ip assignment during write");
                break;
        }
        switch (newConfig.proxySettings) {
            case STATIC:
            case PAC:
                ProxyProperties newHttpProxy = newConfig.linkProperties.getHttpProxy();
                ProxyProperties currentHttpProxy = currentConfig.linkProperties.getHttpProxy();
                if (newHttpProxy != null) {
                    proxyChanged = !newHttpProxy.equals(currentHttpProxy);
                    break;
                } else {
                    proxyChanged = currentHttpProxy != null;
                    break;
                }
            case NONE:
                if (currentConfig.proxySettings != newConfig.proxySettings) {
                    proxyChanged = true;
                    break;
                }
                break;
            case UNASSIGNED:
                break;
            default:
                loge("Ignore invalid proxy configuration during write");
                break;
        }
        if (!ipChanged) {
            linkProperties = copyIpSettingsFromConfig(currentConfig);
        } else {
            currentConfig.ipAssignment = newConfig.ipAssignment;
            linkProperties = copyIpSettingsFromConfig(newConfig);
            log("IP config changed SSID = " + currentConfig.SSID + " linkProperties: " + linkProperties.toString());
        }
        if (!proxyChanged) {
            linkProperties.setHttpProxy(currentConfig.linkProperties.getHttpProxy());
        } else {
            currentConfig.proxySettings = newConfig.proxySettings;
            linkProperties.setHttpProxy(newConfig.linkProperties.getHttpProxy());
            log("proxy changed SSID = " + currentConfig.SSID);
            if (linkProperties.getHttpProxy() != null) {
                log(" proxyProperties: " + linkProperties.getHttpProxy().toString());
            }
        }
        if (ipChanged || proxyChanged) {
            currentConfig.linkProperties = linkProperties;
            writeIpAndProxyConfigurations();
            sendConfiguredNetworksChangedBroadcast(currentConfig, 2);
        }
        return new NetworkUpdateResult(ipChanged, proxyChanged);
    }

    private LinkProperties copyIpSettingsFromConfig(WifiConfiguration config) {
        LinkProperties linkProperties = new LinkProperties();
        linkProperties.setInterfaceName(config.linkProperties.getInterfaceName());
        for (LinkAddress linkAddr : config.linkProperties.getLinkAddresses()) {
            linkProperties.addLinkAddress(linkAddr);
        }
        for (RouteInfo route : config.linkProperties.getRoutes()) {
            linkProperties.addRoute(route);
        }
        for (InetAddress dns : config.linkProperties.getDnses()) {
            linkProperties.addDns(dns);
        }
        return linkProperties;
    }

    private void readNetworkVariables(WifiConfiguration config) {
        int netId = config.networkId;
        if (netId < 0) {
            return;
        }
        String value = this.mWifiNative.getNetworkVariable(netId, WifiConfiguration.ssidVarName);
        if (!TextUtils.isEmpty(value)) {
            if (value.charAt(0) != '\"') {
                config.SSID = Separators.DOUBLE_QUOTE + WifiSsid.createFromHex(value).toString() + Separators.DOUBLE_QUOTE;
            } else {
                config.SSID = value;
            }
        } else {
            config.SSID = null;
        }
        String value2 = this.mWifiNative.getNetworkVariable(netId, "bssid");
        if (!TextUtils.isEmpty(value2)) {
            config.BSSID = value2;
        } else {
            config.BSSID = null;
        }
        String value3 = this.mWifiNative.getNetworkVariable(netId, "priority");
        config.priority = -1;
        if (!TextUtils.isEmpty(value3)) {
            try {
                config.priority = Integer.parseInt(value3);
            } catch (NumberFormatException e) {
            }
        }
        String value4 = this.mWifiNative.getNetworkVariable(netId, WifiConfiguration.hiddenSSIDVarName);
        config.hiddenSSID = false;
        if (!TextUtils.isEmpty(value4)) {
            try {
                config.hiddenSSID = Integer.parseInt(value4) != 0;
            } catch (NumberFormatException e2) {
            }
        }
        String value5 = this.mWifiNative.getNetworkVariable(netId, WifiConfiguration.wepTxKeyIdxVarName);
        config.wepTxKeyIndex = -1;
        if (!TextUtils.isEmpty(value5)) {
            try {
                config.wepTxKeyIndex = Integer.parseInt(value5);
            } catch (NumberFormatException e3) {
            }
        }
        for (int i = 0; i < 4; i++) {
            String value6 = this.mWifiNative.getNetworkVariable(netId, WifiConfiguration.wepKeyVarNames[i]);
            if (!TextUtils.isEmpty(value6)) {
                config.wepKeys[i] = value6;
            } else {
                config.wepKeys[i] = null;
            }
        }
        String value7 = this.mWifiNative.getNetworkVariable(netId, WifiConfiguration.pskVarName);
        if (!TextUtils.isEmpty(value7)) {
            config.preSharedKey = value7;
        } else {
            config.preSharedKey = null;
        }
        String value8 = this.mWifiNative.getNetworkVariable(config.networkId, WifiConfiguration.Protocol.varName);
        if (!TextUtils.isEmpty(value8)) {
            String[] vals = value8.split(Separators.SP);
            for (String val : vals) {
                int index = lookupString(val, WifiConfiguration.Protocol.strings);
                if (0 <= index) {
                    config.allowedProtocols.set(index);
                }
            }
        }
        String value9 = this.mWifiNative.getNetworkVariable(config.networkId, WifiConfiguration.KeyMgmt.varName);
        if (!TextUtils.isEmpty(value9)) {
            String[] vals2 = value9.split(Separators.SP);
            for (String val2 : vals2) {
                int index2 = lookupString(val2, WifiConfiguration.KeyMgmt.strings);
                if (0 <= index2) {
                    config.allowedKeyManagement.set(index2);
                }
            }
        }
        String value10 = this.mWifiNative.getNetworkVariable(config.networkId, WifiConfiguration.AuthAlgorithm.varName);
        if (!TextUtils.isEmpty(value10)) {
            String[] vals3 = value10.split(Separators.SP);
            for (String val3 : vals3) {
                int index3 = lookupString(val3, WifiConfiguration.AuthAlgorithm.strings);
                if (0 <= index3) {
                    config.allowedAuthAlgorithms.set(index3);
                }
            }
        }
        String value11 = this.mWifiNative.getNetworkVariable(config.networkId, WifiConfiguration.PairwiseCipher.varName);
        if (!TextUtils.isEmpty(value11)) {
            String[] vals4 = value11.split(Separators.SP);
            for (String val4 : vals4) {
                int index4 = lookupString(val4, WifiConfiguration.PairwiseCipher.strings);
                if (0 <= index4) {
                    config.allowedPairwiseCiphers.set(index4);
                }
            }
        }
        String value12 = this.mWifiNative.getNetworkVariable(config.networkId, WifiConfiguration.GroupCipher.varName);
        if (!TextUtils.isEmpty(value12)) {
            String[] vals5 = value12.split(Separators.SP);
            for (String val5 : vals5) {
                int index5 = lookupString(val5, WifiConfiguration.GroupCipher.strings);
                if (0 <= index5) {
                    config.allowedGroupCiphers.set(index5);
                }
            }
        }
        if (config.enterpriseConfig == null) {
            config.enterpriseConfig = new WifiEnterpriseConfig();
        }
        HashMap<String, String> enterpriseFields = config.enterpriseConfig.getFields();
        String[] arr$ = WifiEnterpriseConfig.getSupplicantKeys();
        for (String key : arr$) {
            String value13 = this.mWifiNative.getNetworkVariable(netId, key);
            if (!TextUtils.isEmpty(value13)) {
                enterpriseFields.put(key, removeDoubleQuotes(value13));
            } else {
                enterpriseFields.put(key, "NULL");
            }
        }
        if (config.enterpriseConfig.migrateOldEapTlsNative(this.mWifiNative, netId)) {
            saveConfig();
        }
        config.enterpriseConfig.migrateCerts(this.mKeyStore);
        config.enterpriseConfig.initializeSoftwareKeystoreFlag(this.mKeyStore);
    }

    private String removeDoubleQuotes(String string) {
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private String convertToQuotedString(String string) {
        return Separators.DOUBLE_QUOTE + string + Separators.DOUBLE_QUOTE;
    }

    private String makeString(BitSet set, String[] strings) {
        StringBuffer buf = new StringBuffer();
        int nextSetBit = -1;
        BitSet set2 = set.get(0, strings.length);
        while (true) {
            int nextSetBit2 = set2.nextSetBit(nextSetBit + 1);
            nextSetBit = nextSetBit2;
            if (nextSetBit2 == -1) {
                break;
            }
            buf.append(strings[nextSetBit].replace('_', '-')).append(' ');
        }
        if (set2.cardinality() > 0) {
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }

    private int lookupString(String string, String[] strings) {
        int size = strings.length;
        String string2 = string.replace('-', '_');
        for (int i = 0; i < size; i++) {
            if (string2.equals(strings[i])) {
                return i;
            }
        }
        loge("Failed to look-up a string: " + string2);
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int configKey(WifiConfiguration config) {
        String key;
        if (config.allowedKeyManagement.get(1)) {
            key = config.SSID + WifiConfiguration.KeyMgmt.strings[1];
        } else if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
            key = config.SSID + WifiConfiguration.KeyMgmt.strings[2];
        } else if (config.wepKeys[0] != null) {
            key = config.SSID + "WEP";
        } else {
            key = config.SSID + WifiConfiguration.KeyMgmt.strings[0];
        }
        return key.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(TAG);
        pw.println("mLastPriority " + this.mLastPriority);
        pw.println("Configured networks");
        for (WifiConfiguration conf : getConfiguredNetworks()) {
            pw.println(conf);
        }
        pw.println();
        if (this.mLocalLog != null) {
            pw.println("WifiConfigStore - Log Begin ----");
            this.mLocalLog.dump(fd, pw, args);
            pw.println("WifiConfigStore - Log End ----");
        }
    }

    public String getConfigFile() {
        return ipConfigFile;
    }

    private void loge(String s) {
        Log.e(TAG, s);
    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    private void localLog(String s) {
        if (this.mLocalLog != null) {
            this.mLocalLog.log(s);
        }
    }

    private void localLog(String s, int netId) {
        WifiConfiguration config;
        if (this.mLocalLog == null) {
            return;
        }
        synchronized (this.mConfiguredNetworks) {
            config = this.mConfiguredNetworks.get(Integer.valueOf(netId));
        }
        if (config != null) {
            this.mLocalLog.log(s + Separators.SP + config.getPrintableSsid());
        } else {
            this.mLocalLog.log(s + Separators.SP + netId);
        }
    }
}