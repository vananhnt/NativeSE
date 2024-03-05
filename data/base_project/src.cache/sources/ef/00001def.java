package com.android.server.connectivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkStatsService;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.os.Binder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.telephony.Phone;
import com.android.internal.util.IState;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.IoThread;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/* loaded from: Tethering.class */
public class Tethering extends INetworkManagementEventObserver.Stub {
    private Context mContext;
    private static final String TAG = "Tethering";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private String[] mTetherableUsbRegexs;
    private String[] mTetherableWifiRegexs;
    private String[] mTetherableBluetoothRegexs;
    private Collection<Integer> mUpstreamIfaceTypes;
    private final INetworkManagementService mNMService;
    private final INetworkStatsService mStatsService;
    private final IConnectivityManager mConnService;
    private Looper mLooper;
    private BroadcastReceiver mStateReceiver;
    private static final String USB_NEAR_IFACE_ADDR = "192.168.42.129";
    private static final int USB_PREFIX_LENGTH = 24;
    private String[] mDhcpRange;
    private String[] mDefaultDnsServers;
    private static final String DNS_DEFAULT_SERVER1 = "8.8.8.8";
    private static final String DNS_DEFAULT_SERVER2 = "8.8.4.4";
    private StateMachine mTetherMasterSM;
    private Notification mTetheredNotification;
    private boolean mRndisEnabled;
    private boolean mUsbTetherRequested;
    private static final Integer MOBILE_TYPE = new Integer(0);
    private static final Integer HIPRI_TYPE = new Integer(5);
    private static final Integer DUN_TYPE = new Integer(4);
    private static final String[] DHCP_DEFAULT_RANGE = {"192.168.42.2", "192.168.42.254", "192.168.43.2", "192.168.43.254", "192.168.44.2", "192.168.44.254", "192.168.45.2", "192.168.45.254", "192.168.46.2", "192.168.46.254", "192.168.47.2", "192.168.47.254", "192.168.48.2", "192.168.48.254"};
    private int mPreferredUpstreamMobileApn = -1;
    private Object mPublicSync = new Object();
    private HashMap<String, TetherInterfaceSM> mIfaces = new HashMap<>();

    public Tethering(Context context, INetworkManagementService nmService, INetworkStatsService statsService, IConnectivityManager connService, Looper looper) {
        this.mContext = context;
        this.mNMService = nmService;
        this.mStatsService = statsService;
        this.mConnService = connService;
        this.mLooper = looper;
        this.mLooper = IoThread.get().getLooper();
        this.mTetherMasterSM = new TetherMasterSM("TetherMaster", this.mLooper);
        this.mTetherMasterSM.start();
        this.mStateReceiver = new StateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_STATE);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        this.mContext.registerReceiver(this.mStateReceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_MEDIA_SHARED);
        filter2.addAction(Intent.ACTION_MEDIA_UNSHARED);
        filter2.addDataScheme(ContentResolver.SCHEME_FILE);
        this.mContext.registerReceiver(this.mStateReceiver, filter2);
        this.mDhcpRange = context.getResources().getStringArray(R.array.config_tether_dhcp_range);
        if (this.mDhcpRange.length == 0 || this.mDhcpRange.length % 2 == 1) {
            this.mDhcpRange = DHCP_DEFAULT_RANGE;
        }
        updateConfiguration();
        this.mDefaultDnsServers = new String[2];
        this.mDefaultDnsServers[0] = DNS_DEFAULT_SERVER1;
        this.mDefaultDnsServers[1] = DNS_DEFAULT_SERVER2;
    }

    void updateConfiguration() {
        String[] tetherableUsbRegexs = this.mContext.getResources().getStringArray(R.array.config_tether_usb_regexs);
        String[] tetherableWifiRegexs = this.mContext.getResources().getStringArray(R.array.config_tether_wifi_regexs);
        String[] tetherableBluetoothRegexs = this.mContext.getResources().getStringArray(R.array.config_tether_bluetooth_regexs);
        int[] ifaceTypes = this.mContext.getResources().getIntArray(R.array.config_tether_upstream_types);
        Collection<Integer> upstreamIfaceTypes = new ArrayList<>();
        for (int i : ifaceTypes) {
            upstreamIfaceTypes.add(new Integer(i));
        }
        synchronized (this.mPublicSync) {
            this.mTetherableUsbRegexs = tetherableUsbRegexs;
            this.mTetherableWifiRegexs = tetherableWifiRegexs;
            this.mTetherableBluetoothRegexs = tetherableBluetoothRegexs;
            this.mUpstreamIfaceTypes = upstreamIfaceTypes;
        }
        checkDunRequired();
    }

    @Override // android.net.INetworkManagementEventObserver
    public void interfaceStatusChanged(String iface, boolean up) {
        boolean found = false;
        boolean usb = false;
        synchronized (this.mPublicSync) {
            if (isWifi(iface)) {
                found = true;
            } else if (isUsb(iface)) {
                found = true;
                usb = true;
            } else if (isBluetooth(iface)) {
                found = true;
            }
            if (found) {
                TetherInterfaceSM sm = this.mIfaces.get(iface);
                if (up) {
                    if (sm == null) {
                        TetherInterfaceSM sm2 = new TetherInterfaceSM(iface, this.mLooper, usb);
                        this.mIfaces.put(iface, sm2);
                        sm2.start();
                    }
                } else if (!isUsb(iface) && sm != null) {
                    sm.sendMessage(4);
                    this.mIfaces.remove(iface);
                }
            }
        }
    }

    @Override // android.net.INetworkManagementEventObserver
    public void interfaceLinkStateChanged(String iface, boolean up) {
        interfaceStatusChanged(iface, up);
    }

    private boolean isUsb(String iface) {
        synchronized (this.mPublicSync) {
            String[] arr$ = this.mTetherableUsbRegexs;
            for (String regex : arr$) {
                if (iface.matches(regex)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isWifi(String iface) {
        synchronized (this.mPublicSync) {
            String[] arr$ = this.mTetherableWifiRegexs;
            for (String regex : arr$) {
                if (iface.matches(regex)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isBluetooth(String iface) {
        synchronized (this.mPublicSync) {
            String[] arr$ = this.mTetherableBluetoothRegexs;
            for (String regex : arr$) {
                if (iface.matches(regex)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // android.net.INetworkManagementEventObserver
    public void interfaceAdded(String iface) {
        boolean found = false;
        boolean usb = false;
        synchronized (this.mPublicSync) {
            if (isWifi(iface)) {
                found = true;
            }
            if (isUsb(iface)) {
                found = true;
                usb = true;
            }
            if (isBluetooth(iface)) {
                found = true;
            }
            if (found) {
                if (this.mIfaces.get(iface) != null) {
                    return;
                }
                TetherInterfaceSM sm = new TetherInterfaceSM(iface, this.mLooper, usb);
                this.mIfaces.put(iface, sm);
                sm.start();
            }
        }
    }

    @Override // android.net.INetworkManagementEventObserver
    public void interfaceRemoved(String iface) {
        synchronized (this.mPublicSync) {
            TetherInterfaceSM sm = this.mIfaces.get(iface);
            if (sm == null) {
                return;
            }
            sm.sendMessage(4);
            this.mIfaces.remove(iface);
        }
    }

    @Override // android.net.INetworkManagementEventObserver
    public void addressUpdated(String address, String iface, int flags, int scope) {
    }

    @Override // android.net.INetworkManagementEventObserver
    public void addressRemoved(String address, String iface, int flags, int scope) {
    }

    @Override // android.net.INetworkManagementEventObserver
    public void limitReached(String limitName, String iface) {
    }

    @Override // android.net.INetworkManagementEventObserver
    public void interfaceClassDataActivityChanged(String label, boolean active) {
    }

    public int tether(String iface) {
        TetherInterfaceSM sm;
        Log.d(TAG, "Tethering " + iface);
        synchronized (this.mPublicSync) {
            sm = this.mIfaces.get(iface);
        }
        if (sm == null) {
            Log.e(TAG, "Tried to Tether an unknown iface :" + iface + ", ignoring");
            return 1;
        } else if (!sm.isAvailable() && !sm.isErrored()) {
            Log.e(TAG, "Tried to Tether an unavailable iface :" + iface + ", ignoring");
            return 4;
        } else {
            sm.sendMessage(2);
            return 0;
        }
    }

    public int untether(String iface) {
        TetherInterfaceSM sm;
        Log.d(TAG, "Untethering " + iface);
        synchronized (this.mPublicSync) {
            sm = this.mIfaces.get(iface);
        }
        if (sm == null) {
            Log.e(TAG, "Tried to Untether an unknown iface :" + iface + ", ignoring");
            return 1;
        } else if (sm.isErrored()) {
            Log.e(TAG, "Tried to Untethered an errored iface :" + iface + ", ignoring");
            return 4;
        } else {
            sm.sendMessage(3);
            return 0;
        }
    }

    public int getLastTetherError(String iface) {
        synchronized (this.mPublicSync) {
            TetherInterfaceSM sm = this.mIfaces.get(iface);
            if (sm == null) {
                Log.e(TAG, "Tried to getLastTetherError on an unknown iface :" + iface + ", ignoring");
                return 1;
            }
            return sm.getLastError();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTetherStateChangedBroadcast() {
        try {
            if (this.mConnService.isTetheringSupported()) {
                ArrayList<String> availableList = new ArrayList<>();
                ArrayList<String> activeList = new ArrayList<>();
                ArrayList<String> erroredList = new ArrayList<>();
                boolean wifiTethered = false;
                boolean usbTethered = false;
                boolean bluetoothTethered = false;
                synchronized (this.mPublicSync) {
                    Set ifaces = this.mIfaces.keySet();
                    for (Object iface : ifaces) {
                        TetherInterfaceSM sm = this.mIfaces.get(iface);
                        if (sm != null) {
                            if (sm.isErrored()) {
                                erroredList.add((String) iface);
                            } else if (sm.isAvailable()) {
                                availableList.add((String) iface);
                            } else if (sm.isTethered()) {
                                if (isUsb((String) iface)) {
                                    usbTethered = true;
                                } else if (isWifi((String) iface)) {
                                    wifiTethered = true;
                                } else if (isBluetooth((String) iface)) {
                                    bluetoothTethered = true;
                                }
                                activeList.add((String) iface);
                            }
                        }
                    }
                }
                Intent broadcast = new Intent(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
                broadcast.addFlags(603979776);
                broadcast.putStringArrayListExtra(ConnectivityManager.EXTRA_AVAILABLE_TETHER, availableList);
                broadcast.putStringArrayListExtra(ConnectivityManager.EXTRA_ACTIVE_TETHER, activeList);
                broadcast.putStringArrayListExtra(ConnectivityManager.EXTRA_ERRORED_TETHER, erroredList);
                this.mContext.sendStickyBroadcastAsUser(broadcast, UserHandle.ALL);
                Log.d(TAG, "sendTetherStateChangedBroadcast " + availableList.size() + ", " + activeList.size() + ", " + erroredList.size());
                if (usbTethered) {
                    if (wifiTethered || bluetoothTethered) {
                        showTetheredNotification(R.drawable.stat_sys_tether_general);
                    } else {
                        showTetheredNotification(R.drawable.stat_sys_tether_usb);
                    }
                } else if (wifiTethered) {
                    if (bluetoothTethered) {
                        showTetheredNotification(R.drawable.stat_sys_tether_general);
                    } else {
                        showTetheredNotification(R.drawable.stat_sys_tether_wifi);
                    }
                } else if (bluetoothTethered) {
                    showTetheredNotification(R.drawable.stat_sys_tether_bluetooth);
                } else {
                    clearTetheredNotification();
                }
            }
        } catch (RemoteException e) {
        }
    }

    private void showTetheredNotification(int icon) {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        if (this.mTetheredNotification != null) {
            if (this.mTetheredNotification.icon == icon) {
                return;
            }
            notificationManager.cancelAsUser(null, this.mTetheredNotification.icon, UserHandle.ALL);
        }
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setFlags(1073741824);
        PendingIntent pi = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 0, null, UserHandle.CURRENT);
        Resources r = Resources.getSystem();
        CharSequence title = r.getText(R.string.tethered_notification_title);
        CharSequence message = r.getText(R.string.tethered_notification_message);
        if (this.mTetheredNotification == null) {
            this.mTetheredNotification = new Notification();
            this.mTetheredNotification.when = 0L;
        }
        this.mTetheredNotification.icon = icon;
        this.mTetheredNotification.defaults &= -2;
        this.mTetheredNotification.flags = 2;
        this.mTetheredNotification.tickerText = title;
        this.mTetheredNotification.setLatestEventInfo(this.mContext, title, message, pi);
        notificationManager.notifyAsUser(null, this.mTetheredNotification.icon, this.mTetheredNotification, UserHandle.ALL);
    }

    private void clearTetheredNotification() {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && this.mTetheredNotification != null) {
            notificationManager.cancelAsUser(null, this.mTetheredNotification.icon, UserHandle.ALL);
            this.mTetheredNotification = null;
        }
    }

    /* loaded from: Tethering$StateReceiver.class */
    private class StateReceiver extends BroadcastReceiver {
        private StateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UsbManager.ACTION_USB_STATE)) {
                synchronized (Tethering.this.mPublicSync) {
                    boolean usbConnected = intent.getBooleanExtra("connected", false);
                    Tethering.this.mRndisEnabled = intent.getBooleanExtra(UsbManager.USB_FUNCTION_RNDIS, false);
                    if (usbConnected && Tethering.this.mRndisEnabled && Tethering.this.mUsbTetherRequested) {
                        Tethering.this.tetherUsb(true);
                    }
                    Tethering.this.mUsbTetherRequested = false;
                }
            } else if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                    Tethering.this.updateConfiguration();
                }
            } else {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (networkInfo != null && networkInfo.getDetailedState() != NetworkInfo.DetailedState.FAILED) {
                    Tethering.this.mTetherMasterSM.sendMessage(3);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tetherUsb(boolean enable) {
        String[] strArr = new String[0];
        try {
            String[] ifaces = this.mNMService.listInterfaces();
            for (String iface : ifaces) {
                if (isUsb(iface)) {
                    int result = enable ? tether(iface) : untether(iface);
                    if (result == 0) {
                        return;
                    }
                }
            }
            Log.e(TAG, "unable start or stop USB tethering");
        } catch (Exception e) {
            Log.e(TAG, "Error listing Interfaces", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean configureUsbIface(boolean enabled) {
        String[] strArr = new String[0];
        try {
            String[] ifaces = this.mNMService.listInterfaces();
            for (String iface : ifaces) {
                if (isUsb(iface)) {
                    try {
                        InterfaceConfiguration ifcg = this.mNMService.getInterfaceConfig(iface);
                        if (ifcg != null) {
                            InetAddress addr = NetworkUtils.numericToInetAddress(USB_NEAR_IFACE_ADDR);
                            ifcg.setLinkAddress(new LinkAddress(addr, 24));
                            if (enabled) {
                                ifcg.setInterfaceUp();
                            } else {
                                ifcg.setInterfaceDown();
                            }
                            ifcg.clearFlag("running");
                            this.mNMService.setInterfaceConfig(iface, ifcg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error configuring interface " + iface, e);
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e2) {
            Log.e(TAG, "Error listing Interfaces", e2);
            return false;
        }
    }

    public String[] getTetherableUsbRegexs() {
        return this.mTetherableUsbRegexs;
    }

    public String[] getTetherableWifiRegexs() {
        return this.mTetherableWifiRegexs;
    }

    public String[] getTetherableBluetoothRegexs() {
        return this.mTetherableBluetoothRegexs;
    }

    public int setUsbTethering(boolean enable) {
        UsbManager usbManager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE);
        synchronized (this.mPublicSync) {
            if (enable) {
                if (this.mRndisEnabled) {
                    tetherUsb(true);
                } else {
                    this.mUsbTetherRequested = true;
                    usbManager.setCurrentFunction(UsbManager.USB_FUNCTION_RNDIS, false);
                }
            } else {
                tetherUsb(false);
                if (this.mRndisEnabled) {
                    usbManager.setCurrentFunction(null, false);
                }
                this.mUsbTetherRequested = false;
            }
        }
        return 0;
    }

    public int[] getUpstreamIfaceTypes() {
        int[] values;
        synchronized (this.mPublicSync) {
            updateConfiguration();
            values = new int[this.mUpstreamIfaceTypes.size()];
            Iterator<Integer> iterator = this.mUpstreamIfaceTypes.iterator();
            for (int i = 0; i < this.mUpstreamIfaceTypes.size(); i++) {
                values[i] = iterator.next().intValue();
            }
        }
        return values;
    }

    public void checkDunRequired() {
        int secureSetting = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.TETHER_DUN_REQUIRED, 2);
        synchronized (this.mPublicSync) {
            if (secureSetting != 2) {
                int requiredApn = secureSetting == 1 ? 4 : 5;
                if (requiredApn == 4) {
                    while (this.mUpstreamIfaceTypes.contains(MOBILE_TYPE)) {
                        this.mUpstreamIfaceTypes.remove(MOBILE_TYPE);
                    }
                    while (this.mUpstreamIfaceTypes.contains(HIPRI_TYPE)) {
                        this.mUpstreamIfaceTypes.remove(HIPRI_TYPE);
                    }
                    if (!this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {
                        this.mUpstreamIfaceTypes.add(DUN_TYPE);
                    }
                } else {
                    while (this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {
                        this.mUpstreamIfaceTypes.remove(DUN_TYPE);
                    }
                    if (!this.mUpstreamIfaceTypes.contains(MOBILE_TYPE)) {
                        this.mUpstreamIfaceTypes.add(MOBILE_TYPE);
                    }
                    if (!this.mUpstreamIfaceTypes.contains(HIPRI_TYPE)) {
                        this.mUpstreamIfaceTypes.add(HIPRI_TYPE);
                    }
                }
            }
            if (this.mUpstreamIfaceTypes.contains(DUN_TYPE)) {
                this.mPreferredUpstreamMobileApn = 4;
            } else {
                this.mPreferredUpstreamMobileApn = 5;
            }
        }
    }

    public String[] getTetheredIfaces() {
        ArrayList<String> list = new ArrayList<>();
        synchronized (this.mPublicSync) {
            Set keys = this.mIfaces.keySet();
            for (Object key : keys) {
                TetherInterfaceSM sm = this.mIfaces.get(key);
                if (sm.isTethered()) {
                    list.add((String) key);
                }
            }
        }
        String[] retVal = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retVal[i] = list.get(i);
        }
        return retVal;
    }

    public String[] getTetherableIfaces() {
        ArrayList<String> list = new ArrayList<>();
        synchronized (this.mPublicSync) {
            Set keys = this.mIfaces.keySet();
            for (Object key : keys) {
                TetherInterfaceSM sm = this.mIfaces.get(key);
                if (sm.isAvailable()) {
                    list.add((String) key);
                }
            }
        }
        String[] retVal = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retVal[i] = list.get(i);
        }
        return retVal;
    }

    public String[] getErroredIfaces() {
        ArrayList<String> list = new ArrayList<>();
        synchronized (this.mPublicSync) {
            Set keys = this.mIfaces.keySet();
            for (Object key : keys) {
                TetherInterfaceSM sm = this.mIfaces.get(key);
                if (sm.isErrored()) {
                    list.add((String) key);
                }
            }
        }
        String[] retVal = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retVal[i] = list.get(i);
        }
        return retVal;
    }

    public void handleTetherIfaceChange() {
        this.mTetherMasterSM.sendMessage(3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Tethering$TetherInterfaceSM.class */
    public class TetherInterfaceSM extends StateMachine {
        static final int CMD_TETHER_MODE_DEAD = 1;
        static final int CMD_TETHER_REQUESTED = 2;
        static final int CMD_TETHER_UNREQUESTED = 3;
        static final int CMD_INTERFACE_DOWN = 4;
        static final int CMD_INTERFACE_UP = 5;
        static final int CMD_CELL_DUN_ERROR = 6;
        static final int CMD_IP_FORWARDING_ENABLE_ERROR = 7;
        static final int CMD_IP_FORWARDING_DISABLE_ERROR = 8;
        static final int CMD_START_TETHERING_ERROR = 9;
        static final int CMD_STOP_TETHERING_ERROR = 10;
        static final int CMD_SET_DNS_FORWARDERS_ERROR = 11;
        static final int CMD_TETHER_CONNECTION_CHANGED = 12;
        private State mDefaultState;
        private State mInitialState;
        private State mStartingState;
        private State mTetheredState;
        private State mUnavailableState;
        private boolean mAvailable;
        private boolean mTethered;
        int mLastError;
        String mIfaceName;
        String mMyUpstreamIfaceName;
        boolean mUsb;

        TetherInterfaceSM(String name, Looper looper, boolean usb) {
            super(name, looper);
            this.mIfaceName = name;
            this.mUsb = usb;
            setLastError(0);
            this.mInitialState = new InitialState();
            addState(this.mInitialState);
            this.mStartingState = new StartingState();
            addState(this.mStartingState);
            this.mTetheredState = new TetheredState();
            addState(this.mTetheredState);
            this.mUnavailableState = new UnavailableState();
            addState(this.mUnavailableState);
            setInitialState(this.mInitialState);
        }

        public String toString() {
            String res = new String() + this.mIfaceName + " - ";
            IState current = getCurrentState();
            if (current == this.mInitialState) {
                res = res + "InitialState";
            }
            if (current == this.mStartingState) {
                res = res + "StartingState";
            }
            if (current == this.mTetheredState) {
                res = res + "TetheredState";
            }
            if (current == this.mUnavailableState) {
                res = res + "UnavailableState";
            }
            if (this.mAvailable) {
                res = res + " - Available";
            }
            if (this.mTethered) {
                res = res + " - Tethered";
            }
            return res + " - lastError =" + this.mLastError;
        }

        public int getLastError() {
            int i;
            synchronized (Tethering.this.mPublicSync) {
                i = this.mLastError;
            }
            return i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLastError(int error) {
            synchronized (Tethering.this.mPublicSync) {
                this.mLastError = error;
                if (isErrored() && this.mUsb) {
                    Tethering.this.configureUsbIface(false);
                }
            }
        }

        public boolean isAvailable() {
            boolean z;
            synchronized (Tethering.this.mPublicSync) {
                z = this.mAvailable;
            }
            return z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAvailable(boolean available) {
            synchronized (Tethering.this.mPublicSync) {
                this.mAvailable = available;
            }
        }

        public boolean isTethered() {
            boolean z;
            synchronized (Tethering.this.mPublicSync) {
                z = this.mTethered;
            }
            return z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTethered(boolean tethered) {
            synchronized (Tethering.this.mPublicSync) {
                this.mTethered = tethered;
            }
        }

        public boolean isErrored() {
            boolean z;
            synchronized (Tethering.this.mPublicSync) {
                z = this.mLastError != 0;
            }
            return z;
        }

        /* loaded from: Tethering$TetherInterfaceSM$InitialState.class */
        class InitialState extends State {
            InitialState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                TetherInterfaceSM.this.setAvailable(true);
                TetherInterfaceSM.this.setTethered(false);
                Tethering.this.sendTetherStateChangedBroadcast();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                Log.d(Tethering.TAG, "InitialState.processMessage what=" + message.what);
                boolean retValue = true;
                switch (message.what) {
                    case 2:
                        TetherInterfaceSM.this.setLastError(0);
                        Tethering.this.mTetherMasterSM.sendMessage(1, TetherInterfaceSM.this);
                        TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mStartingState);
                        break;
                    case 4:
                        TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mUnavailableState);
                        break;
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }
        }

        /* loaded from: Tethering$TetherInterfaceSM$StartingState.class */
        class StartingState extends State {
            StartingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                TetherInterfaceSM.this.setAvailable(false);
                if (!TetherInterfaceSM.this.mUsb || Tethering.this.configureUsbIface(true)) {
                    Tethering.this.sendTetherStateChangedBroadcast();
                    TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mTetheredState);
                    return;
                }
                Tethering.this.mTetherMasterSM.sendMessage(2, TetherInterfaceSM.this);
                TetherInterfaceSM.this.setLastError(10);
                TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mInitialState);
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                Log.d(Tethering.TAG, "StartingState.processMessage what=" + message.what);
                boolean retValue = true;
                switch (message.what) {
                    case 3:
                        Tethering.this.mTetherMasterSM.sendMessage(2, TetherInterfaceSM.this);
                        if (!TetherInterfaceSM.this.mUsb || Tethering.this.configureUsbIface(false)) {
                            TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mInitialState);
                            break;
                        } else {
                            TetherInterfaceSM.this.setLastErrorAndTransitionToInitialState(10);
                            break;
                        }
                        break;
                    case 4:
                        Tethering.this.mTetherMasterSM.sendMessage(2, TetherInterfaceSM.this);
                        TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mUnavailableState);
                        break;
                    case 5:
                    default:
                        retValue = false;
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                        TetherInterfaceSM.this.setLastErrorAndTransitionToInitialState(5);
                        break;
                }
                return retValue;
            }
        }

        /* loaded from: Tethering$TetherInterfaceSM$TetheredState.class */
        class TetheredState extends State {
            TetheredState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                try {
                    Tethering.this.mNMService.tetherInterface(TetherInterfaceSM.this.mIfaceName);
                    Log.d(Tethering.TAG, "Tethered " + TetherInterfaceSM.this.mIfaceName);
                    TetherInterfaceSM.this.setAvailable(false);
                    TetherInterfaceSM.this.setTethered(true);
                    Tethering.this.sendTetherStateChangedBroadcast();
                } catch (Exception e) {
                    Log.e(Tethering.TAG, "Error Tethering: " + e.toString());
                    TetherInterfaceSM.this.setLastError(6);
                    TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mInitialState);
                }
            }

            private void cleanupUpstream() {
                if (TetherInterfaceSM.this.mMyUpstreamIfaceName != null) {
                    try {
                        Tethering.this.mStatsService.forceUpdate();
                    } catch (Exception e) {
                    }
                    try {
                        Tethering.this.mNMService.disableNat(TetherInterfaceSM.this.mIfaceName, TetherInterfaceSM.this.mMyUpstreamIfaceName);
                    } catch (Exception e2) {
                    }
                    TetherInterfaceSM.this.mMyUpstreamIfaceName = null;
                }
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:46:0x01fe  */
            /* JADX WARN: Removed duplicated region for block: B:47:0x0209  */
            @Override // com.android.internal.util.State, com.android.internal.util.IState
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public boolean processMessage(android.os.Message r5) {
                /*
                    Method dump skipped, instructions count: 616
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.Tethering.TetherInterfaceSM.TetheredState.processMessage(android.os.Message):boolean");
            }
        }

        /* loaded from: Tethering$TetherInterfaceSM$UnavailableState.class */
        class UnavailableState extends State {
            UnavailableState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                TetherInterfaceSM.this.setAvailable(false);
                TetherInterfaceSM.this.setLastError(0);
                TetherInterfaceSM.this.setTethered(false);
                Tethering.this.sendTetherStateChangedBroadcast();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                boolean retValue = true;
                switch (message.what) {
                    case 5:
                        TetherInterfaceSM.this.transitionTo(TetherInterfaceSM.this.mInitialState);
                        break;
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }
        }

        void setLastErrorAndTransitionToInitialState(int error) {
            setLastError(error);
            transitionTo(this.mInitialState);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: Tethering$TetherMasterSM.class */
    public class TetherMasterSM extends StateMachine {
        static final int CMD_TETHER_MODE_REQUESTED = 1;
        static final int CMD_TETHER_MODE_UNREQUESTED = 2;
        static final int CMD_UPSTREAM_CHANGED = 3;
        static final int CMD_CELL_CONNECTION_RENEW = 4;
        static final int CMD_RETRY_UPSTREAM = 5;
        private int mSequenceNumber;
        private State mInitialState;
        private State mTetherModeAliveState;
        private State mSetIpForwardingEnabledErrorState;
        private State mSetIpForwardingDisabledErrorState;
        private State mStartTetheringErrorState;
        private State mStopTetheringErrorState;
        private State mSetDnsForwardersErrorState;
        private ArrayList<TetherInterfaceSM> mNotifyList;
        private int mCurrentConnectionSequence;
        private int mMobileApnReserved;
        private String mUpstreamIfaceName;
        private static final int UPSTREAM_SETTLE_TIME_MS = 10000;
        private static final int CELL_CONNECTION_RENEW_MS = 40000;

        static /* synthetic */ int access$3104(TetherMasterSM x0) {
            int i = x0.mCurrentConnectionSequence + 1;
            x0.mCurrentConnectionSequence = i;
            return i;
        }

        TetherMasterSM(String name, Looper looper) {
            super(name, looper);
            this.mMobileApnReserved = -1;
            this.mUpstreamIfaceName = null;
            this.mInitialState = new InitialState();
            addState(this.mInitialState);
            this.mTetherModeAliveState = new TetherModeAliveState();
            addState(this.mTetherModeAliveState);
            this.mSetIpForwardingEnabledErrorState = new SetIpForwardingEnabledErrorState();
            addState(this.mSetIpForwardingEnabledErrorState);
            this.mSetIpForwardingDisabledErrorState = new SetIpForwardingDisabledErrorState();
            addState(this.mSetIpForwardingDisabledErrorState);
            this.mStartTetheringErrorState = new StartTetheringErrorState();
            addState(this.mStartTetheringErrorState);
            this.mStopTetheringErrorState = new StopTetheringErrorState();
            addState(this.mStopTetheringErrorState);
            this.mSetDnsForwardersErrorState = new SetDnsForwardersErrorState();
            addState(this.mSetDnsForwardersErrorState);
            this.mNotifyList = new ArrayList<>();
            setInitialState(this.mInitialState);
        }

        /* loaded from: Tethering$TetherMasterSM$TetherMasterUtilState.class */
        class TetherMasterUtilState extends State {
            protected static final boolean TRY_TO_SETUP_MOBILE_CONNECTION = true;
            protected static final boolean WAIT_FOR_NETWORK_TO_SETTLE = false;

            TetherMasterUtilState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message m) {
                return false;
            }

            protected String enableString(int apnType) {
                switch (apnType) {
                    case 0:
                    case 5:
                        return Phone.FEATURE_ENABLE_HIPRI;
                    case 4:
                        return Phone.FEATURE_ENABLE_DUN_ALWAYS;
                    default:
                        return null;
                }
            }

            protected boolean turnOnUpstreamMobileConnection(int apnType) {
                boolean retValue = true;
                if (apnType == -1) {
                    return false;
                }
                if (apnType != TetherMasterSM.this.mMobileApnReserved) {
                    turnOffUpstreamMobileConnection();
                }
                int result = 3;
                String enableString = enableString(apnType);
                if (enableString == null) {
                    return false;
                }
                try {
                    result = Tethering.this.mConnService.startUsingNetworkFeature(0, enableString, new Binder());
                } catch (Exception e) {
                }
                switch (result) {
                    case 0:
                    case 1:
                        TetherMasterSM.this.mMobileApnReserved = apnType;
                        Message m = TetherMasterSM.this.obtainMessage(4);
                        m.arg1 = TetherMasterSM.access$3104(TetherMasterSM.this);
                        TetherMasterSM.this.sendMessageDelayed(m, 40000L);
                        break;
                    case 2:
                    case 3:
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }

            protected boolean turnOffUpstreamMobileConnection() {
                TetherMasterSM.access$3104(TetherMasterSM.this);
                if (TetherMasterSM.this.mMobileApnReserved != -1) {
                    try {
                        Tethering.this.mConnService.stopUsingNetworkFeature(0, enableString(TetherMasterSM.this.mMobileApnReserved));
                        TetherMasterSM.this.mMobileApnReserved = -1;
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return true;
            }

            protected boolean turnOnMasterTetherSettings() {
                try {
                    Tethering.this.mNMService.setIpForwardingEnabled(true);
                    try {
                        Tethering.this.mNMService.startTethering(Tethering.this.mDhcpRange);
                    } catch (Exception e) {
                        try {
                            Tethering.this.mNMService.stopTethering();
                            Tethering.this.mNMService.startTethering(Tethering.this.mDhcpRange);
                        } catch (Exception e2) {
                            TetherMasterSM.this.transitionTo(TetherMasterSM.this.mStartTetheringErrorState);
                            return false;
                        }
                    }
                    try {
                        Tethering.this.mNMService.setDnsForwarders(Tethering.this.mDefaultDnsServers);
                        return true;
                    } catch (Exception e3) {
                        TetherMasterSM.this.transitionTo(TetherMasterSM.this.mSetDnsForwardersErrorState);
                        return false;
                    }
                } catch (Exception e4) {
                    TetherMasterSM.this.transitionTo(TetherMasterSM.this.mSetIpForwardingEnabledErrorState);
                    return false;
                }
            }

            protected boolean turnOffMasterTetherSettings() {
                try {
                    Tethering.this.mNMService.stopTethering();
                    try {
                        Tethering.this.mNMService.setIpForwardingEnabled(false);
                        TetherMasterSM.this.transitionTo(TetherMasterSM.this.mInitialState);
                        return true;
                    } catch (Exception e) {
                        TetherMasterSM.this.transitionTo(TetherMasterSM.this.mSetIpForwardingDisabledErrorState);
                        return false;
                    }
                } catch (Exception e2) {
                    TetherMasterSM.this.transitionTo(TetherMasterSM.this.mStopTetheringErrorState);
                    return false;
                }
            }

            protected void chooseUpstreamType(boolean tryCell) {
                int upType = -1;
                String iface = null;
                Tethering.this.updateConfiguration();
                synchronized (Tethering.this.mPublicSync) {
                    Iterator i$ = Tethering.this.mUpstreamIfaceTypes.iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        Integer netType = (Integer) i$.next();
                        NetworkInfo info = null;
                        try {
                            info = Tethering.this.mConnService.getNetworkInfo(netType.intValue());
                        } catch (RemoteException e) {
                        }
                        if (info != null && info.isConnected()) {
                            upType = netType.intValue();
                            break;
                        }
                    }
                }
                Log.d(Tethering.TAG, "chooseUpstreamType(" + tryCell + "), preferredApn =" + Tethering.this.mPreferredUpstreamMobileApn + ", got type=" + upType);
                if (upType == 4 || upType == 5) {
                    turnOnUpstreamMobileConnection(upType);
                } else if (upType != -1) {
                    turnOffUpstreamMobileConnection();
                }
                if (upType == -1) {
                    boolean tryAgainLater = true;
                    if (tryCell && turnOnUpstreamMobileConnection(Tethering.this.mPreferredUpstreamMobileApn)) {
                        tryAgainLater = false;
                    }
                    if (tryAgainLater) {
                        TetherMasterSM.this.sendMessageDelayed(5, 10000L);
                    }
                } else {
                    LinkProperties linkProperties = null;
                    try {
                        linkProperties = Tethering.this.mConnService.getLinkProperties(upType);
                    } catch (RemoteException e2) {
                    }
                    if (linkProperties != null) {
                        Log.i(Tethering.TAG, "Finding IPv4 upstream interface on: " + linkProperties);
                        RouteInfo ipv4Default = RouteInfo.selectBestRoute(linkProperties.getAllRoutes(), Inet4Address.ANY);
                        if (ipv4Default != null) {
                            iface = ipv4Default.getInterface();
                            Log.i(Tethering.TAG, "Found interface " + ipv4Default.getInterface());
                        } else {
                            Log.i(Tethering.TAG, "No IPv4 upstream interface, giving up.");
                        }
                    }
                    if (iface != null) {
                        String[] dnsServers = Tethering.this.mDefaultDnsServers;
                        Collection<InetAddress> dnses = linkProperties.getDnses();
                        if (dnses != null) {
                            ArrayList<InetAddress> v4Dnses = new ArrayList<>(dnses.size());
                            for (InetAddress dnsAddress : dnses) {
                                if (dnsAddress instanceof Inet4Address) {
                                    v4Dnses.add(dnsAddress);
                                }
                            }
                            if (v4Dnses.size() > 0) {
                                dnsServers = NetworkUtils.makeStrings(v4Dnses);
                            }
                        }
                        try {
                            Tethering.this.mNMService.setDnsForwarders(dnsServers);
                        } catch (Exception e3) {
                            TetherMasterSM.this.transitionTo(TetherMasterSM.this.mSetDnsForwardersErrorState);
                        }
                    }
                }
                notifyTetheredOfNewUpstreamIface(iface);
            }

            protected void notifyTetheredOfNewUpstreamIface(String ifaceName) {
                Log.d(Tethering.TAG, "notifying tethered with iface =" + ifaceName);
                TetherMasterSM.this.mUpstreamIfaceName = ifaceName;
                Iterator i$ = TetherMasterSM.this.mNotifyList.iterator();
                while (i$.hasNext()) {
                    TetherInterfaceSM sm = (TetherInterfaceSM) i$.next();
                    sm.sendMessage(12, ifaceName);
                }
            }
        }

        /* loaded from: Tethering$TetherMasterSM$InitialState.class */
        class InitialState extends TetherMasterUtilState {
            InitialState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
            }

            @Override // com.android.server.connectivity.Tethering.TetherMasterSM.TetherMasterUtilState, com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                Log.d(Tethering.TAG, "MasterInitialState.processMessage what=" + message.what);
                boolean retValue = true;
                switch (message.what) {
                    case 1:
                        TetherMasterSM.this.mNotifyList.add((TetherInterfaceSM) message.obj);
                        TetherMasterSM.this.transitionTo(TetherMasterSM.this.mTetherModeAliveState);
                        break;
                    case 2:
                        TetherInterfaceSM who = (TetherInterfaceSM) message.obj;
                        int index = TetherMasterSM.this.mNotifyList.indexOf(who);
                        if (index != -1) {
                            TetherMasterSM.this.mNotifyList.remove(who);
                            break;
                        }
                        break;
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }
        }

        /* loaded from: Tethering$TetherMasterSM$TetherModeAliveState.class */
        class TetherModeAliveState extends TetherMasterUtilState {
            boolean mTryCell;

            TetherModeAliveState() {
                super();
                this.mTryCell = true;
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                turnOnMasterTetherSettings();
                this.mTryCell = true;
                chooseUpstreamType(this.mTryCell);
                this.mTryCell = !this.mTryCell;
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void exit() {
                turnOffUpstreamMobileConnection();
                notifyTetheredOfNewUpstreamIface(null);
            }

            @Override // com.android.server.connectivity.Tethering.TetherMasterSM.TetherMasterUtilState, com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                Log.d(Tethering.TAG, "TetherModeAliveState.processMessage what=" + message.what);
                boolean retValue = true;
                switch (message.what) {
                    case 1:
                        TetherInterfaceSM who = (TetherInterfaceSM) message.obj;
                        TetherMasterSM.this.mNotifyList.add(who);
                        who.sendMessage(12, TetherMasterSM.this.mUpstreamIfaceName);
                        break;
                    case 2:
                        TetherInterfaceSM who2 = (TetherInterfaceSM) message.obj;
                        int index = TetherMasterSM.this.mNotifyList.indexOf(who2);
                        if (index != -1) {
                            Log.d(Tethering.TAG, "TetherModeAlive removing notifyee " + who2);
                            TetherMasterSM.this.mNotifyList.remove(index);
                            if (TetherMasterSM.this.mNotifyList.isEmpty()) {
                                turnOffMasterTetherSettings();
                                break;
                            } else {
                                Log.d(Tethering.TAG, "TetherModeAlive still has " + TetherMasterSM.this.mNotifyList.size() + " live requests:");
                                Iterator i$ = TetherMasterSM.this.mNotifyList.iterator();
                                while (i$.hasNext()) {
                                    Object o = (TetherInterfaceSM) i$.next();
                                    Log.d(Tethering.TAG, "  " + o);
                                }
                                break;
                            }
                        } else {
                            Log.e(Tethering.TAG, "TetherModeAliveState UNREQUESTED has unknown who: " + who2);
                            break;
                        }
                    case 3:
                        this.mTryCell = true;
                        chooseUpstreamType(this.mTryCell);
                        this.mTryCell = !this.mTryCell;
                        break;
                    case 4:
                        if (TetherMasterSM.this.mCurrentConnectionSequence == message.arg1) {
                            turnOnUpstreamMobileConnection(TetherMasterSM.this.mMobileApnReserved);
                            break;
                        }
                        break;
                    case 5:
                        chooseUpstreamType(this.mTryCell);
                        this.mTryCell = !this.mTryCell;
                        break;
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }
        }

        /* loaded from: Tethering$TetherMasterSM$ErrorState.class */
        class ErrorState extends State {
            int mErrorNotification;

            ErrorState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message message) {
                boolean retValue = true;
                switch (message.what) {
                    case 1:
                        TetherInterfaceSM who = (TetherInterfaceSM) message.obj;
                        who.sendMessage(this.mErrorNotification);
                        break;
                    default:
                        retValue = false;
                        break;
                }
                return retValue;
            }

            void notify(int msgType) {
                this.mErrorNotification = msgType;
                Iterator i$ = TetherMasterSM.this.mNotifyList.iterator();
                while (i$.hasNext()) {
                    Object o = i$.next();
                    TetherInterfaceSM sm = (TetherInterfaceSM) o;
                    sm.sendMessage(msgType);
                }
            }
        }

        /* loaded from: Tethering$TetherMasterSM$SetIpForwardingEnabledErrorState.class */
        class SetIpForwardingEnabledErrorState extends ErrorState {
            SetIpForwardingEnabledErrorState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                Log.e(Tethering.TAG, "Error in setIpForwardingEnabled");
                notify(7);
            }
        }

        /* loaded from: Tethering$TetherMasterSM$SetIpForwardingDisabledErrorState.class */
        class SetIpForwardingDisabledErrorState extends ErrorState {
            SetIpForwardingDisabledErrorState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                Log.e(Tethering.TAG, "Error in setIpForwardingDisabled");
                notify(8);
            }
        }

        /* loaded from: Tethering$TetherMasterSM$StartTetheringErrorState.class */
        class StartTetheringErrorState extends ErrorState {
            StartTetheringErrorState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                Log.e(Tethering.TAG, "Error in startTethering");
                notify(9);
                try {
                    Tethering.this.mNMService.setIpForwardingEnabled(false);
                } catch (Exception e) {
                }
            }
        }

        /* loaded from: Tethering$TetherMasterSM$StopTetheringErrorState.class */
        class StopTetheringErrorState extends ErrorState {
            StopTetheringErrorState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                Log.e(Tethering.TAG, "Error in stopTethering");
                notify(10);
                try {
                    Tethering.this.mNMService.setIpForwardingEnabled(false);
                } catch (Exception e) {
                }
            }
        }

        /* loaded from: Tethering$TetherMasterSM$SetDnsForwardersErrorState.class */
        class SetDnsForwardersErrorState extends ErrorState {
            SetDnsForwardersErrorState() {
                super();
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public void enter() {
                Log.e(Tethering.TAG, "Error in setDnsForwarders");
                notify(11);
                try {
                    Tethering.this.mNMService.stopTethering();
                } catch (Exception e) {
                }
                try {
                    Tethering.this.mNMService.setIpForwardingEnabled(false);
                } catch (Exception e2) {
                }
            }
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump ConnectivityService.Tether from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mPublicSync) {
            pw.println("mUpstreamIfaceTypes: ");
            for (Integer netType : this.mUpstreamIfaceTypes) {
                pw.println(Separators.SP + netType);
            }
            pw.println();
            pw.println("Tether state:");
            for (Object o : this.mIfaces.values()) {
                pw.println(Separators.SP + o);
            }
        }
        pw.println();
    }
}