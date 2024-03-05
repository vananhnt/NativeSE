package android.net.wifi;

import android.bluetooth.BluetoothInputDevice;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.text.TextUtils;
import android.util.LocalLog;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: WifiNative.class */
public class WifiNative {
    private static final boolean DBG = false;
    private static final boolean VDBG = false;
    private final String mTAG;
    private static final int DEFAULT_GROUP_OWNER_INTENT = 6;
    static final int BLUETOOTH_COEXISTENCE_MODE_ENABLED = 0;
    static final int BLUETOOTH_COEXISTENCE_MODE_DISABLED = 1;
    static final int BLUETOOTH_COEXISTENCE_MODE_SENSE = 2;
    static final int SCAN_WITHOUT_CONNECTION_SETUP = 1;
    static final int SCAN_WITH_CONNECTION_SETUP = 2;
    public final String mInterfaceName;
    public final String mInterfacePrefix;
    private boolean mSuspendOptEnabled = false;
    private int mCmdId;
    static final Object mLock = new Object();
    private static final LocalLog mLocalLog = new LocalLog(1024);

    public static native boolean loadDriver();

    public static native boolean isDriverLoaded();

    public static native boolean unloadDriver();

    public static native boolean startSupplicant(boolean z);

    public static native boolean killSupplicant(boolean z);

    private native boolean connectToSupplicantNative();

    private native void closeSupplicantConnectionNative();

    private native String waitForEventNative();

    private native boolean doBooleanCommandNative(String str);

    private native int doIntCommandNative(String str);

    private native String doStringCommandNative(String str);

    public WifiNative(String interfaceName) {
        this.mInterfaceName = interfaceName;
        this.mTAG = "WifiNative-" + interfaceName;
        if (!interfaceName.equals("p2p0")) {
            this.mInterfacePrefix = "IFNAME=" + interfaceName + Separators.SP;
        } else {
            this.mInterfacePrefix = "";
        }
    }

    public LocalLog getLocalLog() {
        return mLocalLog;
    }

    private int getNewCmdIdLocked() {
        int i = this.mCmdId;
        this.mCmdId = i + 1;
        return i;
    }

    private void localLog(String s) {
        if (mLocalLog != null) {
            mLocalLog.log(this.mInterfaceName + ": " + s);
        }
    }

    public boolean connectToSupplicant() {
        return connectToSupplicantNative();
    }

    public void closeSupplicantConnection() {
        closeSupplicantConnectionNative();
    }

    public String waitForEvent() {
        return waitForEventNative();
    }

    private boolean doBooleanCommand(String command) {
        boolean result;
        synchronized (mLock) {
            getNewCmdIdLocked();
            result = doBooleanCommandNative(this.mInterfacePrefix + command);
        }
        return result;
    }

    private int doIntCommand(String command) {
        int result;
        synchronized (mLock) {
            getNewCmdIdLocked();
            result = doIntCommandNative(this.mInterfacePrefix + command);
        }
        return result;
    }

    private String doStringCommand(String command) {
        String result;
        synchronized (mLock) {
            getNewCmdIdLocked();
            result = doStringCommandNative(this.mInterfacePrefix + command);
        }
        return result;
    }

    private String doStringCommandWithoutLogging(String command) {
        String doStringCommandNative;
        synchronized (mLock) {
            doStringCommandNative = doStringCommandNative(this.mInterfacePrefix + command);
        }
        return doStringCommandNative;
    }

    public boolean ping() {
        String pong = doStringCommand("PING");
        return pong != null && pong.equals("PONG");
    }

    public boolean scan(int type) {
        if (type == 1) {
            return doBooleanCommand("SCAN TYPE=ONLY");
        }
        if (type == 2) {
            return doBooleanCommand("SCAN");
        }
        throw new IllegalArgumentException("Invalid scan type");
    }

    public boolean stopSupplicant() {
        return doBooleanCommand("TERMINATE");
    }

    public String listNetworks() {
        return doStringCommand("LIST_NETWORKS");
    }

    public int addNetwork() {
        return doIntCommand("ADD_NETWORK");
    }

    public boolean setNetworkVariable(int netId, String name, String value) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
            return false;
        }
        return doBooleanCommand("SET_NETWORK " + netId + Separators.SP + name + Separators.SP + value);
    }

    public String getNetworkVariable(int netId, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return doStringCommandWithoutLogging("GET_NETWORK " + netId + Separators.SP + name);
    }

    public boolean removeNetwork(int netId) {
        return doBooleanCommand("REMOVE_NETWORK " + netId);
    }

    public boolean enableNetwork(int netId, boolean disableOthers) {
        if (disableOthers) {
            return doBooleanCommand("SELECT_NETWORK " + netId);
        }
        return doBooleanCommand("ENABLE_NETWORK " + netId);
    }

    public boolean disableNetwork(int netId) {
        return doBooleanCommand("DISABLE_NETWORK " + netId);
    }

    public boolean reconnect() {
        return doBooleanCommand("RECONNECT");
    }

    public boolean reassociate() {
        return doBooleanCommand("REASSOCIATE");
    }

    public boolean disconnect() {
        return doBooleanCommand("DISCONNECT");
    }

    public String status() {
        return doStringCommand("STATUS");
    }

    public String getMacAddress() {
        String ret = doStringCommand("DRIVER MACADDR");
        if (!TextUtils.isEmpty(ret)) {
            String[] tokens = ret.split(" = ");
            if (tokens.length == 2) {
                return tokens[1];
            }
            return null;
        }
        return null;
    }

    public String scanResults(int sid) {
        return doStringCommandWithoutLogging("BSS RANGE=" + sid + "- MASK=0x21987");
    }

    public String setBatchedScanSettings(BatchedScanSettings settings) {
        if (settings == null) {
            return doStringCommand("DRIVER WLS_BATCHING STOP");
        }
        String cmd = ("DRIVER WLS_BATCHING SET SCANFREQ=" + settings.scanIntervalSec) + " MSCAN=" + settings.maxScansPerBatch;
        if (settings.maxApPerScan != Integer.MAX_VALUE) {
            cmd = cmd + " BESTN=" + settings.maxApPerScan;
        }
        if (settings.channelSet != null && !settings.channelSet.isEmpty()) {
            String cmd2 = cmd + " CHANNEL=<";
            int i = 0;
            for (String channel : settings.channelSet) {
                cmd2 = cmd2 + (i > 0 ? Separators.COMMA : "") + channel;
                i++;
            }
            cmd = cmd2 + Separators.GREATER_THAN;
        }
        if (settings.maxApForDistance != Integer.MAX_VALUE) {
            cmd = cmd + " RTT=" + settings.maxApForDistance;
        }
        return doStringCommand(cmd);
    }

    public String getBatchedScanResults() {
        return doStringCommand("DRIVER WLS_BATCHING GET");
    }

    public boolean startDriver() {
        return doBooleanCommand("DRIVER START");
    }

    public boolean stopDriver() {
        return doBooleanCommand("DRIVER STOP");
    }

    public boolean startFilteringMulticastV4Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-REMOVE 2") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean stopFilteringMulticastV4Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-ADD 2") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean startFilteringMulticastV6Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-REMOVE 3") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public boolean stopFilteringMulticastV6Packets() {
        return doBooleanCommand("DRIVER RXFILTER-STOP") && doBooleanCommand("DRIVER RXFILTER-ADD 3") && doBooleanCommand("DRIVER RXFILTER-START");
    }

    public int getBand() {
        String ret = doStringCommand("DRIVER GETBAND");
        if (!TextUtils.isEmpty(ret)) {
            String[] tokens = ret.split(Separators.SP);
            try {
                if (tokens.length == 2) {
                    return Integer.parseInt(tokens[1]);
                }
                return -1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public boolean setBand(int band) {
        return doBooleanCommand("DRIVER SETBAND " + band);
    }

    public boolean setBluetoothCoexistenceMode(int mode) {
        return doBooleanCommand("DRIVER BTCOEXMODE " + mode);
    }

    public boolean setBluetoothCoexistenceScanMode(boolean setCoexScanMode) {
        if (setCoexScanMode) {
            return doBooleanCommand("DRIVER BTCOEXSCAN-START");
        }
        return doBooleanCommand("DRIVER BTCOEXSCAN-STOP");
    }

    public boolean saveConfig() {
        return doBooleanCommand("SAVE_CONFIG");
    }

    public boolean addToBlacklist(String bssid) {
        if (TextUtils.isEmpty(bssid)) {
            return false;
        }
        return doBooleanCommand("BLACKLIST " + bssid);
    }

    public boolean clearBlacklist() {
        return doBooleanCommand("BLACKLIST clear");
    }

    public boolean setSuspendOptimizations(boolean enabled) {
        if (this.mSuspendOptEnabled == enabled) {
            return true;
        }
        this.mSuspendOptEnabled = enabled;
        if (enabled) {
            return doBooleanCommand("DRIVER SETSUSPENDMODE 1");
        }
        return doBooleanCommand("DRIVER SETSUSPENDMODE 0");
    }

    public boolean setCountryCode(String countryCode) {
        return doBooleanCommand("DRIVER COUNTRY " + countryCode.toUpperCase(Locale.ROOT));
    }

    public void enableBackgroundScan(boolean enable) {
        if (enable) {
            doBooleanCommand("SET pno 1");
        } else {
            doBooleanCommand("SET pno 0");
        }
    }

    public void setScanInterval(int scanInterval) {
        doBooleanCommand("SCAN_INTERVAL " + scanInterval);
    }

    public void startTdls(String macAddr, boolean enable) {
        if (enable) {
            doBooleanCommand("TDLS_DISCOVER " + macAddr);
            doBooleanCommand("TDLS_SETUP " + macAddr);
            return;
        }
        doBooleanCommand("TDLS_TEARDOWN " + macAddr);
    }

    public String signalPoll() {
        return doStringCommandWithoutLogging("SIGNAL_POLL");
    }

    public String pktcntPoll() {
        return doStringCommand("PKTCNT_POLL");
    }

    public void bssFlush() {
        doBooleanCommand("BSS_FLUSH 0");
    }

    public boolean startWpsPbc(String bssid) {
        if (TextUtils.isEmpty(bssid)) {
            return doBooleanCommand("WPS_PBC");
        }
        return doBooleanCommand("WPS_PBC " + bssid);
    }

    public boolean startWpsPbc(String iface, String bssid) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(bssid)) {
                return doBooleanCommandNative("IFNAME=" + iface + " WPS_PBC");
            }
            return doBooleanCommandNative("IFNAME=" + iface + " WPS_PBC " + bssid);
        }
    }

    public boolean startWpsPinKeypad(String pin) {
        if (TextUtils.isEmpty(pin)) {
            return false;
        }
        return doBooleanCommand("WPS_PIN any " + pin);
    }

    public boolean startWpsPinKeypad(String iface, String pin) {
        boolean doBooleanCommandNative;
        if (TextUtils.isEmpty(pin)) {
            return false;
        }
        synchronized (mLock) {
            doBooleanCommandNative = doBooleanCommandNative("IFNAME=" + iface + " WPS_PIN any " + pin);
        }
        return doBooleanCommandNative;
    }

    public String startWpsPinDisplay(String bssid) {
        if (TextUtils.isEmpty(bssid)) {
            return doStringCommand("WPS_PIN any");
        }
        return doStringCommand("WPS_PIN " + bssid);
    }

    public String startWpsPinDisplay(String iface, String bssid) {
        synchronized (mLock) {
            if (TextUtils.isEmpty(bssid)) {
                return doStringCommandNative("IFNAME=" + iface + " WPS_PIN any");
            }
            return doStringCommandNative("IFNAME=" + iface + " WPS_PIN " + bssid);
        }
    }

    public boolean startWpsRegistrar(String bssid, String pin) {
        if (TextUtils.isEmpty(bssid) || TextUtils.isEmpty(pin)) {
            return false;
        }
        return doBooleanCommand("WPS_REG " + bssid + Separators.SP + pin);
    }

    public boolean cancelWps() {
        return doBooleanCommand("WPS_CANCEL");
    }

    public boolean setPersistentReconnect(boolean enabled) {
        int value = enabled ? 1 : 0;
        return doBooleanCommand("SET persistent_reconnect " + value);
    }

    public boolean setDeviceName(String name) {
        return doBooleanCommand("SET device_name " + name);
    }

    public boolean setDeviceType(String type) {
        return doBooleanCommand("SET device_type " + type);
    }

    public boolean setConfigMethods(String cfg) {
        return doBooleanCommand("SET config_methods " + cfg);
    }

    public boolean setManufacturer(String value) {
        return doBooleanCommand("SET manufacturer " + value);
    }

    public boolean setModelName(String value) {
        return doBooleanCommand("SET model_name " + value);
    }

    public boolean setModelNumber(String value) {
        return doBooleanCommand("SET model_number " + value);
    }

    public boolean setSerialNumber(String value) {
        return doBooleanCommand("SET serial_number " + value);
    }

    public boolean setP2pSsidPostfix(String postfix) {
        return doBooleanCommand("SET p2p_ssid_postfix " + postfix);
    }

    public boolean setP2pGroupIdle(String iface, int time) {
        boolean doBooleanCommandNative;
        synchronized (mLock) {
            doBooleanCommandNative = doBooleanCommandNative("IFNAME=" + iface + " SET p2p_group_idle " + time);
        }
        return doBooleanCommandNative;
    }

    public void setPowerSave(boolean enabled) {
        if (enabled) {
            doBooleanCommand("SET ps 1");
        } else {
            doBooleanCommand("SET ps 0");
        }
    }

    public boolean setP2pPowerSave(String iface, boolean enabled) {
        synchronized (mLock) {
            if (enabled) {
                return doBooleanCommandNative("IFNAME=" + iface + " P2P_SET ps 1");
            }
            return doBooleanCommandNative("IFNAME=" + iface + " P2P_SET ps 0");
        }
    }

    public boolean setWfdEnable(boolean enable) {
        return doBooleanCommand("SET wifi_display " + (enable ? "1" : "0"));
    }

    public boolean setWfdDeviceInfo(String hex) {
        return doBooleanCommand("WFD_SUBELEM_SET 0 " + hex);
    }

    public boolean setConcurrencyPriority(String s) {
        return doBooleanCommand("P2P_SET conc_pref " + s);
    }

    public boolean p2pFind() {
        return doBooleanCommand("P2P_FIND");
    }

    public boolean p2pFind(int timeout) {
        if (timeout <= 0) {
            return p2pFind();
        }
        return doBooleanCommand("P2P_FIND " + timeout);
    }

    public boolean p2pStopFind() {
        return doBooleanCommand("P2P_STOP_FIND");
    }

    public boolean p2pListen() {
        return doBooleanCommand("P2P_LISTEN");
    }

    public boolean p2pListen(int timeout) {
        if (timeout <= 0) {
            return p2pListen();
        }
        return doBooleanCommand("P2P_LISTEN " + timeout);
    }

    public boolean p2pExtListen(boolean enable, int period, int interval) {
        if (enable && interval < period) {
            return false;
        }
        return doBooleanCommand("P2P_EXT_LISTEN" + (enable ? Separators.SP + period + Separators.SP + interval : ""));
    }

    public boolean p2pSetChannel(int lc, int oc) {
        if (lc >= 1 && lc <= 11) {
            if (!doBooleanCommand("P2P_SET listen_channel " + lc)) {
                return false;
            }
        } else if (lc != 0) {
            return false;
        }
        if (oc >= 1 && oc <= 165) {
            int freq = (oc <= 14 ? 2407 : BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED) + (oc * 5);
            return doBooleanCommand("P2P_SET disallow_freq 1000-" + (freq - 5) + Separators.COMMA + (freq + 5) + "-6000");
        } else if (oc == 0) {
            return doBooleanCommand("P2P_SET disallow_freq \"\"");
        } else {
            return false;
        }
    }

    public boolean p2pFlush() {
        return doBooleanCommand("P2P_FLUSH");
    }

    public String p2pConnect(WifiP2pConfig config, boolean joinExistingGroup) {
        if (config == null) {
            return null;
        }
        List<String> args = new ArrayList<>();
        WpsInfo wps = config.wps;
        args.add(config.deviceAddress);
        switch (wps.setup) {
            case 0:
                args.add("pbc");
                break;
            case 1:
                if (TextUtils.isEmpty(wps.pin)) {
                    args.add("pin");
                } else {
                    args.add(wps.pin);
                }
                args.add(Context.DISPLAY_SERVICE);
                break;
            case 2:
                args.add(wps.pin);
                args.add("keypad");
                break;
            case 3:
                args.add(wps.pin);
                args.add("label");
                break;
        }
        if (config.netId == -2) {
            args.add("persistent");
        }
        if (joinExistingGroup) {
            args.add("join");
        } else {
            int groupOwnerIntent = config.groupOwnerIntent;
            groupOwnerIntent = (groupOwnerIntent < 0 || groupOwnerIntent > 15) ? 6 : 6;
            args.add("go_intent=" + groupOwnerIntent);
        }
        String command = "P2P_CONNECT ";
        for (String s : args) {
            command = command + s + Separators.SP;
        }
        return doStringCommand(command);
    }

    public boolean p2pCancelConnect() {
        return doBooleanCommand("P2P_CANCEL");
    }

    public boolean p2pProvisionDiscovery(WifiP2pConfig config) {
        if (config == null) {
            return false;
        }
        switch (config.wps.setup) {
            case 0:
                return doBooleanCommand("P2P_PROV_DISC " + config.deviceAddress + " pbc");
            case 1:
                return doBooleanCommand("P2P_PROV_DISC " + config.deviceAddress + " keypad");
            case 2:
                return doBooleanCommand("P2P_PROV_DISC " + config.deviceAddress + " display");
            default:
                return false;
        }
    }

    public boolean p2pGroupAdd(boolean persistent) {
        if (persistent) {
            return doBooleanCommand("P2P_GROUP_ADD persistent");
        }
        return doBooleanCommand("P2P_GROUP_ADD");
    }

    public boolean p2pGroupAdd(int netId) {
        return doBooleanCommand("P2P_GROUP_ADD persistent=" + netId);
    }

    public boolean p2pGroupRemove(String iface) {
        boolean doBooleanCommandNative;
        if (TextUtils.isEmpty(iface)) {
            return false;
        }
        synchronized (mLock) {
            doBooleanCommandNative = doBooleanCommandNative("IFNAME=" + iface + " P2P_GROUP_REMOVE " + iface);
        }
        return doBooleanCommandNative;
    }

    public boolean p2pReject(String deviceAddress) {
        return doBooleanCommand("P2P_REJECT " + deviceAddress);
    }

    public boolean p2pInvite(WifiP2pGroup group, String deviceAddress) {
        if (TextUtils.isEmpty(deviceAddress)) {
            return false;
        }
        if (group == null) {
            return doBooleanCommand("P2P_INVITE peer=" + deviceAddress);
        }
        return doBooleanCommand("P2P_INVITE group=" + group.getInterface() + " peer=" + deviceAddress + " go_dev_addr=" + group.getOwner().deviceAddress);
    }

    public boolean p2pReinvoke(int netId, String deviceAddress) {
        if (TextUtils.isEmpty(deviceAddress) || netId < 0) {
            return false;
        }
        return doBooleanCommand("P2P_INVITE persistent=" + netId + " peer=" + deviceAddress);
    }

    public String p2pGetSsid(String deviceAddress) {
        return p2pGetParam(deviceAddress, "oper_ssid");
    }

    public String p2pGetDeviceAddress() {
        String status = status();
        if (status == null) {
            return "";
        }
        String[] tokens = status.split(Separators.RETURN);
        for (String token : tokens) {
            if (token.startsWith("p2p_device_address=")) {
                String[] nameValue = token.split(Separators.EQUALS);
                if (nameValue.length == 2) {
                    return nameValue[1];
                }
                return "";
            }
        }
        return "";
    }

    public int getGroupCapability(String deviceAddress) {
        if (TextUtils.isEmpty(deviceAddress)) {
            return 0;
        }
        String peerInfo = p2pPeer(deviceAddress);
        if (TextUtils.isEmpty(peerInfo)) {
            return 0;
        }
        String[] tokens = peerInfo.split(Separators.RETURN);
        int len$ = tokens.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            String token = tokens[i$];
            if (!token.startsWith("group_capab=")) {
                i$++;
            } else {
                String[] nameValue = token.split(Separators.EQUALS);
                if (nameValue.length == 2) {
                    try {
                        return Integer.decode(nameValue[1]).intValue();
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    public String p2pPeer(String deviceAddress) {
        return doStringCommand("P2P_PEER " + deviceAddress);
    }

    private String p2pGetParam(String deviceAddress, String key) {
        String peerInfo;
        if (deviceAddress == null || (peerInfo = p2pPeer(deviceAddress)) == null) {
            return null;
        }
        String[] tokens = peerInfo.split(Separators.RETURN);
        String key2 = key + Separators.EQUALS;
        for (String token : tokens) {
            if (token.startsWith(key2)) {
                String[] nameValue = token.split(Separators.EQUALS);
                if (nameValue.length == 2) {
                    return nameValue[1];
                }
                return null;
            }
        }
        return null;
    }

    public boolean p2pServiceAdd(WifiP2pServiceInfo servInfo) {
        for (String s : servInfo.getSupplicantQueryList()) {
            String command = "P2P_SERVICE_ADD" + Separators.SP + s;
            if (!doBooleanCommand(command)) {
                return false;
            }
        }
        return true;
    }

    public boolean p2pServiceDel(WifiP2pServiceInfo servInfo) {
        String command;
        for (String s : servInfo.getSupplicantQueryList()) {
            String[] data = s.split(Separators.SP);
            if (data.length < 2) {
                return false;
            }
            if ("upnp".equals(data[0])) {
                command = "P2P_SERVICE_DEL " + s;
            } else if ("bonjour".equals(data[0])) {
                String command2 = "P2P_SERVICE_DEL " + data[0];
                command = command2 + Separators.SP + data[1];
            } else {
                return false;
            }
            if (!doBooleanCommand(command)) {
                return false;
            }
        }
        return true;
    }

    public boolean p2pServiceFlush() {
        return doBooleanCommand("P2P_SERVICE_FLUSH");
    }

    public String p2pServDiscReq(String addr, String query) {
        String command = "P2P_SERV_DISC_REQ" + Separators.SP + addr;
        return doStringCommand(command + Separators.SP + query);
    }

    public boolean p2pServDiscCancelReq(String id) {
        return doBooleanCommand("P2P_SERV_DISC_CANCEL_REQ " + id);
    }

    public void setMiracastMode(int mode) {
        doBooleanCommand("DRIVER MIRACAST " + mode);
    }
}