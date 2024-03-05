package android.net.wifi;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pProvDiscEvent;
import android.net.wifi.p2p.WifiP2pService;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import android.os.Message;
import android.util.Log;
import com.android.internal.util.StateMachine;
import gov.nist.core.Separators;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: WifiMonitor.class */
public class WifiMonitor {
    private static final boolean DBG = false;
    private static final String TAG = "WifiMonitor";
    private static final int CONNECTED = 1;
    private static final int DISCONNECTED = 2;
    private static final int STATE_CHANGE = 3;
    private static final int SCAN_RESULTS = 4;
    private static final int LINK_SPEED = 5;
    private static final int TERMINATING = 6;
    private static final int DRIVER_STATE = 7;
    private static final int EAP_FAILURE = 8;
    private static final int ASSOC_REJECT = 9;
    private static final int UNKNOWN = 10;
    private static final String WPA_EVENT_PREFIX_STR = "WPA:";
    private static final String PASSWORD_MAY_BE_INCORRECT_STR = "pre-shared key may be incorrect";
    private static final String WPS_SUCCESS_STR = "WPS-SUCCESS";
    private static final String WPS_FAIL_STR = "WPS-FAIL";
    private static final String WPS_FAIL_PATTERN = "WPS-FAIL msg=\\d+(?: config_error=(\\d+))?(?: reason=(\\d+))?";
    private static final int CONFIG_MULTIPLE_PBC_DETECTED = 12;
    private static final int CONFIG_AUTH_FAILURE = 18;
    private static final int REASON_TKIP_ONLY_PROHIBITED = 1;
    private static final int REASON_WEP_PROHIBITED = 2;
    private static final String WPS_OVERLAP_STR = "WPS-OVERLAP-DETECTED";
    private static final String WPS_TIMEOUT_STR = "WPS-TIMEOUT";
    private static final String CONNECTED_STR = "CONNECTED";
    private static final String DISCONNECTED_STR = "DISCONNECTED";
    private static final String STATE_CHANGE_STR = "STATE-CHANGE";
    private static final String SCAN_RESULTS_STR = "SCAN-RESULTS";
    private static final String LINK_SPEED_STR = "LINK-SPEED";
    private static final String TERMINATING_STR = "TERMINATING";
    private static final String DRIVER_STATE_STR = "DRIVER-STATE";
    private static final String EAP_FAILURE_STR = "EAP-FAILURE";
    private static final String EAP_AUTH_FAILURE_STR = "EAP authentication failed";
    private static final String ASSOC_REJECT_STR = "ASSOC-REJECT";
    private static final String P2P_EVENT_PREFIX_STR = "P2P";
    private static final String P2P_DEVICE_FOUND_STR = "P2P-DEVICE-FOUND";
    private static final String P2P_DEVICE_LOST_STR = "P2P-DEVICE-LOST";
    private static final String P2P_FIND_STOPPED_STR = "P2P-FIND-STOPPED";
    private static final String P2P_GO_NEG_REQUEST_STR = "P2P-GO-NEG-REQUEST";
    private static final String P2P_GO_NEG_SUCCESS_STR = "P2P-GO-NEG-SUCCESS";
    private static final String P2P_GO_NEG_FAILURE_STR = "P2P-GO-NEG-FAILURE";
    private static final String P2P_GROUP_FORMATION_SUCCESS_STR = "P2P-GROUP-FORMATION-SUCCESS";
    private static final String P2P_GROUP_FORMATION_FAILURE_STR = "P2P-GROUP-FORMATION-FAILURE";
    private static final String P2P_GROUP_STARTED_STR = "P2P-GROUP-STARTED";
    private static final String P2P_GROUP_REMOVED_STR = "P2P-GROUP-REMOVED";
    private static final String P2P_INVITATION_RECEIVED_STR = "P2P-INVITATION-RECEIVED";
    private static final String P2P_INVITATION_RESULT_STR = "P2P-INVITATION-RESULT";
    private static final String P2P_PROV_DISC_PBC_REQ_STR = "P2P-PROV-DISC-PBC-REQ";
    private static final String P2P_PROV_DISC_PBC_RSP_STR = "P2P-PROV-DISC-PBC-RESP";
    private static final String P2P_PROV_DISC_ENTER_PIN_STR = "P2P-PROV-DISC-ENTER-PIN";
    private static final String P2P_PROV_DISC_SHOW_PIN_STR = "P2P-PROV-DISC-SHOW-PIN";
    private static final String P2P_PROV_DISC_FAILURE_STR = "P2P-PROV-DISC-FAILURE";
    private static final String P2P_SERV_DISC_RESP_STR = "P2P-SERV-DISC-RESP";
    private static final String HOST_AP_EVENT_PREFIX_STR = "AP";
    private static final String AP_STA_CONNECTED_STR = "AP-STA-CONNECTED";
    private static final String AP_STA_DISCONNECTED_STR = "AP-STA-DISCONNECTED";
    private static final int BASE = 147456;
    public static final int SUP_CONNECTION_EVENT = 147457;
    public static final int SUP_DISCONNECTION_EVENT = 147458;
    public static final int NETWORK_CONNECTION_EVENT = 147459;
    public static final int NETWORK_DISCONNECTION_EVENT = 147460;
    public static final int SCAN_RESULTS_EVENT = 147461;
    public static final int SUPPLICANT_STATE_CHANGE_EVENT = 147462;
    public static final int AUTHENTICATION_FAILURE_EVENT = 147463;
    public static final int WPS_SUCCESS_EVENT = 147464;
    public static final int WPS_FAIL_EVENT = 147465;
    public static final int WPS_OVERLAP_EVENT = 147466;
    public static final int WPS_TIMEOUT_EVENT = 147467;
    public static final int DRIVER_HUNG_EVENT = 147468;
    public static final int P2P_DEVICE_FOUND_EVENT = 147477;
    public static final int P2P_DEVICE_LOST_EVENT = 147478;
    public static final int P2P_GO_NEGOTIATION_REQUEST_EVENT = 147479;
    public static final int P2P_GO_NEGOTIATION_SUCCESS_EVENT = 147481;
    public static final int P2P_GO_NEGOTIATION_FAILURE_EVENT = 147482;
    public static final int P2P_GROUP_FORMATION_SUCCESS_EVENT = 147483;
    public static final int P2P_GROUP_FORMATION_FAILURE_EVENT = 147484;
    public static final int P2P_GROUP_STARTED_EVENT = 147485;
    public static final int P2P_GROUP_REMOVED_EVENT = 147486;
    public static final int P2P_INVITATION_RECEIVED_EVENT = 147487;
    public static final int P2P_INVITATION_RESULT_EVENT = 147488;
    public static final int P2P_PROV_DISC_PBC_REQ_EVENT = 147489;
    public static final int P2P_PROV_DISC_PBC_RSP_EVENT = 147490;
    public static final int P2P_PROV_DISC_ENTER_PIN_EVENT = 147491;
    public static final int P2P_PROV_DISC_SHOW_PIN_EVENT = 147492;
    public static final int P2P_FIND_STOPPED_EVENT = 147493;
    public static final int P2P_SERV_DISC_RESP_EVENT = 147494;
    public static final int P2P_PROV_DISC_FAILURE_EVENT = 147495;
    public static final int AP_STA_DISCONNECTED_EVENT = 147497;
    public static final int AP_STA_CONNECTED_EVENT = 147498;
    public static final int ASSOCIATION_REJECTION_EVENT = 147499;
    private static final String MONITOR_SOCKET_CLOSED_STR = "connection closed";
    private static final String WPA_RECV_ERROR_STR = "recv error";
    private static final int MAX_RECV_ERRORS = 10;
    private final String mInterfaceName;
    private final WifiNative mWifiNative;
    private final StateMachine mWifiStateMachine;
    private boolean mMonitoring = false;
    private static final String EVENT_PREFIX_STR = "CTRL-EVENT-";
    private static final int EVENT_PREFIX_LEN_STR = EVENT_PREFIX_STR.length();
    private static Pattern mConnectedEventPattern = Pattern.compile("((?:[0-9a-f]{2}:){5}[0-9a-f]{2}) .* \\[id=([0-9]+) ");

    public WifiMonitor(StateMachine wifiStateMachine, WifiNative wifiNative) {
        this.mWifiNative = wifiNative;
        this.mInterfaceName = wifiNative.mInterfaceName;
        this.mWifiStateMachine = wifiStateMachine;
        WifiMonitorSingleton.getMonitor().registerInterfaceMonitor(this.mInterfaceName, this);
    }

    public void startMonitoring() {
        WifiMonitorSingleton.getMonitor().startMonitoring(this.mInterfaceName);
    }

    public void stopMonitoring() {
        WifiMonitorSingleton.getMonitor().stopMonitoring(this.mInterfaceName);
    }

    public void stopSupplicant() {
        WifiMonitorSingleton.getMonitor().stopSupplicant();
    }

    public void killSupplicant(boolean p2pSupported) {
        WifiMonitorSingleton.getMonitor().killSupplicant(p2pSupported);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiMonitor$WifiMonitorSingleton.class */
    public static class WifiMonitorSingleton {
        private static Object sSingletonLock = new Object();
        private static WifiMonitorSingleton sWifiMonitorSingleton = null;
        private HashMap<String, WifiMonitor> mIfaceMap = new HashMap<>();
        private boolean mConnected = false;
        private WifiNative mWifiNative;

        private WifiMonitorSingleton() {
        }

        static WifiMonitorSingleton getMonitor() {
            synchronized (sSingletonLock) {
                if (sWifiMonitorSingleton == null) {
                    sWifiMonitorSingleton = new WifiMonitorSingleton();
                }
            }
            return sWifiMonitorSingleton;
        }

        public synchronized void startMonitoring(String iface) {
            WifiMonitor m = this.mIfaceMap.get(iface);
            if (m == null) {
                Log.e(WifiMonitor.TAG, "startMonitor called with unknown iface=" + iface);
                return;
            }
            Log.d(WifiMonitor.TAG, "startMonitoring(" + iface + ") with mConnected = " + this.mConnected);
            if (this.mConnected) {
                m.mMonitoring = true;
                m.mWifiStateMachine.sendMessage(WifiMonitor.SUP_CONNECTION_EVENT);
                return;
            }
            int connectTries = 0;
            while (!this.mWifiNative.connectToSupplicant()) {
                int i = connectTries;
                connectTries++;
                if (i < 5) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    }
                } else {
                    this.mIfaceMap.remove(iface);
                    m.mWifiStateMachine.sendMessage(WifiMonitor.SUP_DISCONNECTION_EVENT);
                    Log.e(WifiMonitor.TAG, "startMonitoring(" + iface + ") failed!");
                    return;
                }
            }
            m.mMonitoring = true;
            m.mWifiStateMachine.sendMessage(WifiMonitor.SUP_CONNECTION_EVENT);
            new MonitorThread(this.mWifiNative, this).start();
            this.mConnected = true;
        }

        public synchronized void stopMonitoring(String iface) {
            WifiMonitor m = this.mIfaceMap.get(iface);
            m.mMonitoring = false;
            m.mWifiStateMachine.sendMessage(WifiMonitor.SUP_DISCONNECTION_EVENT);
        }

        public synchronized void registerInterfaceMonitor(String iface, WifiMonitor m) {
            this.mIfaceMap.put(iface, m);
            if (this.mWifiNative == null) {
                this.mWifiNative = m.mWifiNative;
            }
        }

        public synchronized void unregisterInterfaceMonitor(String iface) {
            this.mIfaceMap.remove(iface);
        }

        public synchronized void stopSupplicant() {
            this.mWifiNative.stopSupplicant();
        }

        public synchronized void killSupplicant(boolean p2pSupported) {
            WifiNative wifiNative = this.mWifiNative;
            WifiNative.killSupplicant(p2pSupported);
            this.mConnected = false;
            for (Map.Entry<String, WifiMonitor> e : this.mIfaceMap.entrySet()) {
                WifiMonitor m = e.getValue();
                m.mMonitoring = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized WifiMonitor getMonitor(String iface) {
            return this.mIfaceMap.get(iface);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WifiMonitor$MonitorThread.class */
    public static class MonitorThread extends Thread {
        private final WifiNative mWifiNative;
        private final WifiMonitorSingleton mWifiMonitorSingleton;
        private int mRecvErrors;
        private StateMachine mStateMachine;

        public MonitorThread(WifiNative wifiNative, WifiMonitorSingleton wifiMonitorSingleton) {
            super(WifiMonitor.TAG);
            this.mRecvErrors = 0;
            this.mStateMachine = null;
            this.mWifiNative = wifiNative;
            this.mWifiMonitorSingleton = wifiMonitorSingleton;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                String eventStr = this.mWifiNative.waitForEvent();
                WifiMonitor m = null;
                this.mStateMachine = null;
                if (!eventStr.startsWith("IFNAME=")) {
                    m = this.mWifiMonitorSingleton.getMonitor("p2p0");
                } else {
                    int space = eventStr.indexOf(32);
                    if (space != -1) {
                        String iface = eventStr.substring(7, space);
                        m = this.mWifiMonitorSingleton.getMonitor(iface);
                        if (m == null && iface.startsWith("p2p-")) {
                            m = this.mWifiMonitorSingleton.getMonitor("p2p0");
                        }
                        eventStr = eventStr.substring(space + 1);
                    }
                }
                if (m != null) {
                    if (m.mMonitoring) {
                        this.mStateMachine = m.mWifiStateMachine;
                    } else {
                        continue;
                    }
                }
                if (this.mStateMachine != null) {
                    if (dispatchEvent(eventStr)) {
                        return;
                    }
                } else {
                    boolean done = false;
                    for (Map.Entry<String, WifiMonitor> e : this.mWifiMonitorSingleton.mIfaceMap.entrySet()) {
                        WifiMonitor m2 = e.getValue();
                        this.mStateMachine = m2.mWifiStateMachine;
                        if (dispatchEvent(eventStr)) {
                            done = true;
                        }
                    }
                    if (done) {
                        this.mWifiMonitorSingleton.mConnected = false;
                        return;
                    }
                }
            }
        }

        private boolean dispatchEvent(String eventStr) {
            int event;
            if (!eventStr.startsWith(WifiMonitor.EVENT_PREFIX_STR)) {
                if (eventStr.startsWith(WifiMonitor.WPA_EVENT_PREFIX_STR) && 0 < eventStr.indexOf(WifiMonitor.PASSWORD_MAY_BE_INCORRECT_STR)) {
                    this.mStateMachine.sendMessage(WifiMonitor.AUTHENTICATION_FAILURE_EVENT);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.WPS_SUCCESS_STR)) {
                    this.mStateMachine.sendMessage(WifiMonitor.WPS_SUCCESS_EVENT);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.WPS_FAIL_STR)) {
                    handleWpsFailEvent(eventStr);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.WPS_OVERLAP_STR)) {
                    this.mStateMachine.sendMessage(WifiMonitor.WPS_OVERLAP_EVENT);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.WPS_TIMEOUT_STR)) {
                    this.mStateMachine.sendMessage(WifiMonitor.WPS_TIMEOUT_EVENT);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.P2P_EVENT_PREFIX_STR)) {
                    handleP2pEvents(eventStr);
                    return false;
                } else if (eventStr.startsWith(WifiMonitor.HOST_AP_EVENT_PREFIX_STR)) {
                    handleHostApEvents(eventStr);
                    return false;
                } else {
                    return false;
                }
            }
            String eventName = eventStr.substring(WifiMonitor.EVENT_PREFIX_LEN_STR);
            int nameEnd = eventName.indexOf(32);
            if (nameEnd != -1) {
                eventName = eventName.substring(0, nameEnd);
            }
            if (eventName.length() == 0) {
                return false;
            }
            if (eventName.equals(WifiMonitor.CONNECTED_STR)) {
                event = 1;
            } else if (eventName.equals(WifiMonitor.DISCONNECTED_STR)) {
                event = 2;
            } else if (eventName.equals(WifiMonitor.STATE_CHANGE_STR)) {
                event = 3;
            } else if (eventName.equals(WifiMonitor.SCAN_RESULTS_STR)) {
                event = 4;
            } else if (eventName.equals(WifiMonitor.LINK_SPEED_STR)) {
                event = 5;
            } else if (eventName.equals(WifiMonitor.TERMINATING_STR)) {
                event = 6;
            } else if (eventName.equals(WifiMonitor.DRIVER_STATE_STR)) {
                event = 7;
            } else if (eventName.equals(WifiMonitor.EAP_FAILURE_STR)) {
                event = 8;
            } else if (eventName.equals(WifiMonitor.ASSOC_REJECT_STR)) {
                event = 9;
            } else {
                event = 10;
            }
            String eventData = eventStr;
            if (event == 7 || event == 5) {
                eventData = eventData.split(Separators.SP)[1];
            } else if (event == 3 || event == 8) {
                int ind = eventStr.indexOf(Separators.SP);
                if (ind != -1) {
                    eventData = eventStr.substring(ind + 1);
                }
            } else {
                int ind2 = eventStr.indexOf(" - ");
                if (ind2 != -1) {
                    eventData = eventStr.substring(ind2 + 3);
                }
            }
            if (event == 3) {
                handleSupplicantStateChange(eventData);
            } else if (event == 7) {
                handleDriverEvent(eventData);
            } else if (event == 6) {
                if (eventData.startsWith(WifiMonitor.WPA_RECV_ERROR_STR)) {
                    int i = this.mRecvErrors + 1;
                    this.mRecvErrors = i;
                    if (i <= 10) {
                        return false;
                    }
                }
                this.mStateMachine.sendMessage(WifiMonitor.SUP_DISCONNECTION_EVENT);
                return true;
            } else if (event == 8) {
                if (eventData.startsWith(WifiMonitor.EAP_AUTH_FAILURE_STR)) {
                    this.mStateMachine.sendMessage(WifiMonitor.AUTHENTICATION_FAILURE_EVENT);
                }
            } else if (event == 9) {
                this.mStateMachine.sendMessage(WifiMonitor.ASSOCIATION_REJECTION_EVENT);
            } else {
                handleEvent(event, eventData);
            }
            this.mRecvErrors = 0;
            return false;
        }

        private void handleDriverEvent(String state) {
            if (state != null && state.equals("HANGED")) {
                this.mStateMachine.sendMessage(WifiMonitor.DRIVER_HUNG_EVENT);
            }
        }

        void handleEvent(int event, String remainder) {
            switch (event) {
                case 1:
                    handleNetworkStateChange(NetworkInfo.DetailedState.CONNECTED, remainder);
                    return;
                case 2:
                    handleNetworkStateChange(NetworkInfo.DetailedState.DISCONNECTED, remainder);
                    return;
                case 3:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    return;
                case 4:
                    this.mStateMachine.sendMessage(WifiMonitor.SCAN_RESULTS_EVENT);
                    return;
            }
        }

        private void handleWpsFailEvent(String dataString) {
            Pattern p = Pattern.compile(WifiMonitor.WPS_FAIL_PATTERN);
            Matcher match = p.matcher(dataString);
            if (match.find()) {
                String cfgErr = match.group(1);
                String reason = match.group(2);
                if (reason != null) {
                    switch (Integer.parseInt(reason)) {
                        case 1:
                            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.WPS_FAIL_EVENT, 5, 0));
                            return;
                        case 2:
                            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.WPS_FAIL_EVENT, 4, 0));
                            return;
                    }
                }
                if (cfgErr != null) {
                    switch (Integer.parseInt(cfgErr)) {
                        case 12:
                            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.WPS_FAIL_EVENT, 3, 0));
                            return;
                        case 18:
                            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.WPS_FAIL_EVENT, 6, 0));
                            return;
                    }
                }
            }
            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.WPS_FAIL_EVENT, 0, 0));
        }

        private WifiP2pService.P2pStatus p2pError(String dataString) {
            WifiP2pService.P2pStatus err = WifiP2pService.P2pStatus.UNKNOWN;
            String[] tokens = dataString.split(Separators.SP);
            if (tokens.length < 2) {
                return err;
            }
            String[] nameValue = tokens[1].split(Separators.EQUALS);
            if (nameValue.length != 2) {
                return err;
            }
            if (nameValue[1].equals("FREQ_CONFLICT")) {
                return WifiP2pService.P2pStatus.NO_COMMON_CHANNEL;
            }
            try {
                err = WifiP2pService.P2pStatus.valueOf(Integer.parseInt(nameValue[1]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return err;
        }

        private void handleP2pEvents(String dataString) {
            if (dataString.startsWith(WifiMonitor.P2P_DEVICE_FOUND_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_DEVICE_FOUND_EVENT, new WifiP2pDevice(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_DEVICE_LOST_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_DEVICE_LOST_EVENT, new WifiP2pDevice(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_FIND_STOPPED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_FIND_STOPPED_EVENT);
            } else if (dataString.startsWith(WifiMonitor.P2P_GO_NEG_REQUEST_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GO_NEGOTIATION_REQUEST_EVENT, new WifiP2pConfig(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_GO_NEG_SUCCESS_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT);
            } else if (dataString.startsWith(WifiMonitor.P2P_GO_NEG_FAILURE_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT, p2pError(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_GROUP_FORMATION_SUCCESS_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT);
            } else if (dataString.startsWith(WifiMonitor.P2P_GROUP_FORMATION_FAILURE_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT, p2pError(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_GROUP_STARTED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GROUP_STARTED_EVENT, new WifiP2pGroup(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_GROUP_REMOVED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_GROUP_REMOVED_EVENT, new WifiP2pGroup(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_INVITATION_RECEIVED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_INVITATION_RECEIVED_EVENT, new WifiP2pGroup(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_INVITATION_RESULT_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_INVITATION_RESULT_EVENT, p2pError(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_PROV_DISC_PBC_REQ_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_PROV_DISC_PBC_REQ_EVENT, new WifiP2pProvDiscEvent(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_PROV_DISC_PBC_RSP_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_PROV_DISC_PBC_RSP_EVENT, new WifiP2pProvDiscEvent(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_PROV_DISC_ENTER_PIN_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT, new WifiP2pProvDiscEvent(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_PROV_DISC_SHOW_PIN_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT, new WifiP2pProvDiscEvent(dataString));
            } else if (dataString.startsWith(WifiMonitor.P2P_PROV_DISC_FAILURE_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.P2P_PROV_DISC_FAILURE_EVENT);
            } else if (dataString.startsWith(WifiMonitor.P2P_SERV_DISC_RESP_STR)) {
                List<WifiP2pServiceResponse> list = WifiP2pServiceResponse.newInstance(dataString);
                if (list != null) {
                    this.mStateMachine.sendMessage(WifiMonitor.P2P_SERV_DISC_RESP_EVENT, list);
                } else {
                    Log.e(WifiMonitor.TAG, "Null service resp " + dataString);
                }
            }
        }

        private void handleHostApEvents(String dataString) {
            String[] tokens = dataString.split(Separators.SP);
            if (tokens[0].equals(WifiMonitor.AP_STA_CONNECTED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.AP_STA_CONNECTED_EVENT, new WifiP2pDevice(dataString));
            } else if (tokens[0].equals(WifiMonitor.AP_STA_DISCONNECTED_STR)) {
                this.mStateMachine.sendMessage(WifiMonitor.AP_STA_DISCONNECTED_EVENT, new WifiP2pDevice(dataString));
            }
        }

        private void handleSupplicantStateChange(String dataString) {
            WifiSsid wifiSsid = null;
            int index = dataString.lastIndexOf("SSID=");
            if (index != -1) {
                wifiSsid = WifiSsid.createFromAsciiEncoded(dataString.substring(index + 5));
            }
            String[] dataTokens = dataString.split(Separators.SP);
            String BSSID = null;
            int networkId = -1;
            int newState = -1;
            for (String token : dataTokens) {
                String[] nameValue = token.split(Separators.EQUALS);
                if (nameValue.length == 2) {
                    if (nameValue[0].equals("BSSID")) {
                        BSSID = nameValue[1];
                    } else {
                        try {
                            int value = Integer.parseInt(nameValue[1]);
                            if (nameValue[0].equals("id")) {
                                networkId = value;
                            } else if (nameValue[0].equals("state")) {
                                newState = value;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
            if (newState == -1) {
                return;
            }
            SupplicantState newSupplicantState = SupplicantState.INVALID;
            SupplicantState[] arr$ = SupplicantState.values();
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                SupplicantState state = arr$[i$];
                if (state.ordinal() != newState) {
                    i$++;
                } else {
                    newSupplicantState = state;
                    break;
                }
            }
            if (newSupplicantState == SupplicantState.INVALID) {
                Log.w(WifiMonitor.TAG, "Invalid supplicant state: " + newState);
            }
            notifySupplicantStateChange(networkId, wifiSsid, BSSID, newSupplicantState);
        }

        private void handleNetworkStateChange(NetworkInfo.DetailedState newState, String data) {
            String BSSID = null;
            int networkId = -1;
            if (newState == NetworkInfo.DetailedState.CONNECTED) {
                Matcher match = WifiMonitor.mConnectedEventPattern.matcher(data);
                if (match.find()) {
                    BSSID = match.group(1);
                    try {
                        networkId = Integer.parseInt(match.group(2));
                    } catch (NumberFormatException e) {
                        networkId = -1;
                    }
                }
                notifyNetworkStateChange(newState, BSSID, networkId);
            }
        }

        void notifyNetworkStateChange(NetworkInfo.DetailedState newState, String BSSID, int netId) {
            if (newState == NetworkInfo.DetailedState.CONNECTED) {
                Message m = this.mStateMachine.obtainMessage(WifiMonitor.NETWORK_CONNECTION_EVENT, netId, 0, BSSID);
                this.mStateMachine.sendMessage(m);
                return;
            }
            Message m2 = this.mStateMachine.obtainMessage(WifiMonitor.NETWORK_DISCONNECTION_EVENT, netId, 0, BSSID);
            this.mStateMachine.sendMessage(m2);
        }

        void notifySupplicantStateChange(int networkId, WifiSsid wifiSsid, String BSSID, SupplicantState newState) {
            this.mStateMachine.sendMessage(this.mStateMachine.obtainMessage(WifiMonitor.SUPPLICANT_STATE_CHANGE_EVENT, new StateChangeResult(networkId, wifiSsid, BSSID, newState)));
        }
    }
}