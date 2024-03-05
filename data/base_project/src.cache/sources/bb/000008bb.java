package android.net;

import android.content.Context;
import android.os.Binder;
import android.os.Messenger;
import android.os.RemoteException;
import com.android.internal.util.Preconditions;
import java.net.InetAddress;

/* loaded from: ConnectivityManager.class */
public class ConnectivityManager {
    private static final String TAG = "ConnectivityManager";
    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String CONNECTIVITY_ACTION_IMMEDIATE = "android.net.conn.CONNECTIVITY_CHANGE_IMMEDIATE";
    @Deprecated
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    public static final String EXTRA_NETWORK_TYPE = "networkType";
    public static final String EXTRA_IS_FAILOVER = "isFailover";
    public static final String EXTRA_OTHER_NETWORK_INFO = "otherNetwork";
    public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
    public static final String EXTRA_REASON = "reason";
    public static final String EXTRA_EXTRA_INFO = "extraInfo";
    public static final String EXTRA_INET_CONDITION = "inetCondition";
    public static final String ACTION_DATA_ACTIVITY_CHANGE = "android.net.conn.DATA_ACTIVITY_CHANGE";
    public static final String EXTRA_DEVICE_TYPE = "deviceType";
    public static final String EXTRA_IS_ACTIVE = "isActive";
    @Deprecated
    public static final String ACTION_BACKGROUND_DATA_SETTING_CHANGED = "android.net.conn.BACKGROUND_DATA_SETTING_CHANGED";
    public static final String INET_CONDITION_ACTION = "android.net.conn.INET_CONDITION_ACTION";
    public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
    public static final String EXTRA_AVAILABLE_TETHER = "availableArray";
    public static final String EXTRA_ACTIVE_TETHER = "activeArray";
    public static final String EXTRA_ERRORED_TETHER = "erroredArray";
    public static final String ACTION_CAPTIVE_PORTAL_TEST_COMPLETED = "android.net.conn.CAPTIVE_PORTAL_TEST_COMPLETED";
    public static final String EXTRA_IS_CAPTIVE_PORTAL = "captivePortal";
    public static final int TYPE_NONE = -1;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE_MMS = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_MOBILE_DUN = 4;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_WIMAX = 6;
    public static final int TYPE_BLUETOOTH = 7;
    public static final int TYPE_DUMMY = 8;
    public static final int TYPE_ETHERNET = 9;
    public static final int TYPE_MOBILE_FOTA = 10;
    public static final int TYPE_MOBILE_IMS = 11;
    public static final int TYPE_MOBILE_CBS = 12;
    public static final int TYPE_WIFI_P2P = 13;
    public static final int TYPE_MOBILE_IA = 14;
    public static final int MAX_RADIO_TYPE = 14;
    public static final int MAX_NETWORK_TYPE = 14;
    @Deprecated
    public static final int DEFAULT_NETWORK_PREFERENCE = 1;
    public static final int CONNECTIVITY_CHANGE_DELAY_DEFAULT = 3000;
    private final IConnectivityManager mService;
    public static final int TETHER_ERROR_NO_ERROR = 0;
    public static final int TETHER_ERROR_UNKNOWN_IFACE = 1;
    public static final int TETHER_ERROR_SERVICE_UNAVAIL = 2;
    public static final int TETHER_ERROR_UNSUPPORTED = 3;
    public static final int TETHER_ERROR_UNAVAIL_IFACE = 4;
    public static final int TETHER_ERROR_MASTER_ERROR = 5;
    public static final int TETHER_ERROR_TETHER_IFACE_ERROR = 6;
    public static final int TETHER_ERROR_UNTETHER_IFACE_ERROR = 7;
    public static final int TETHER_ERROR_ENABLE_NAT_ERROR = 8;
    public static final int TETHER_ERROR_DISABLE_NAT_ERROR = 9;
    public static final int TETHER_ERROR_IFACE_CFG_ERROR = 10;

    public static boolean isNetworkTypeValid(int networkType) {
        return networkType >= 0 && networkType <= 14;
    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case 0:
                return "MOBILE";
            case 1:
                return "WIFI";
            case 2:
                return "MOBILE_MMS";
            case 3:
                return "MOBILE_SUPL";
            case 4:
                return "MOBILE_DUN";
            case 5:
                return "MOBILE_HIPRI";
            case 6:
                return "WIMAX";
            case 7:
                return "BLUETOOTH";
            case 8:
                return "DUMMY";
            case 9:
                return "ETHERNET";
            case 10:
                return "MOBILE_FOTA";
            case 11:
                return "MOBILE_IMS";
            case 12:
                return "MOBILE_CBS";
            case 13:
                return "WIFI_P2P";
            case 14:
                return "MOBILE_IA";
            default:
                return Integer.toString(type);
        }
    }

    public static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 14:
                return true;
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            default:
                return false;
        }
    }

    public static boolean isNetworkTypeWifi(int networkType) {
        switch (networkType) {
            case 1:
            case 13:
                return true;
            default:
                return false;
        }
    }

    public static boolean isNetworkTypeExempt(int networkType) {
        switch (networkType) {
            case 2:
            case 3:
            case 5:
            case 14:
                return true;
            default:
                return false;
        }
    }

    public void setNetworkPreference(int preference) {
        try {
            this.mService.setNetworkPreference(preference);
        } catch (RemoteException e) {
        }
    }

    public int getNetworkPreference() {
        try {
            return this.mService.getNetworkPreference();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public NetworkInfo getActiveNetworkInfo() {
        try {
            return this.mService.getActiveNetworkInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public NetworkInfo getActiveNetworkInfoForUid(int uid) {
        try {
            return this.mService.getActiveNetworkInfoForUid(uid);
        } catch (RemoteException e) {
            return null;
        }
    }

    public NetworkInfo getNetworkInfo(int networkType) {
        try {
            return this.mService.getNetworkInfo(networkType);
        } catch (RemoteException e) {
            return null;
        }
    }

    public NetworkInfo[] getAllNetworkInfo() {
        try {
            return this.mService.getAllNetworkInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public NetworkInfo getProvisioningOrActiveNetworkInfo() {
        try {
            return this.mService.getProvisioningOrActiveNetworkInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public LinkProperties getActiveLinkProperties() {
        try {
            return this.mService.getActiveLinkProperties();
        } catch (RemoteException e) {
            return null;
        }
    }

    public LinkProperties getLinkProperties(int networkType) {
        try {
            return this.mService.getLinkProperties(networkType);
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean setRadios(boolean turnOn) {
        try {
            return this.mService.setRadios(turnOn);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setRadio(int networkType, boolean turnOn) {
        try {
            return this.mService.setRadio(networkType, turnOn);
        } catch (RemoteException e) {
            return false;
        }
    }

    public int startUsingNetworkFeature(int networkType, String feature) {
        try {
            return this.mService.startUsingNetworkFeature(networkType, feature, new Binder());
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int stopUsingNetworkFeature(int networkType, String feature) {
        try {
            return this.mService.stopUsingNetworkFeature(networkType, feature);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean requestRouteToHost(int networkType, int hostAddress) {
        InetAddress inetAddress = NetworkUtils.intToInetAddress(hostAddress);
        if (inetAddress == null) {
            return false;
        }
        return requestRouteToHostAddress(networkType, inetAddress);
    }

    public boolean requestRouteToHostAddress(int networkType, InetAddress hostAddress) {
        byte[] address = hostAddress.getAddress();
        try {
            return this.mService.requestRouteToHostAddress(networkType, address);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Deprecated
    public boolean getBackgroundDataSetting() {
        return true;
    }

    @Deprecated
    public void setBackgroundDataSetting(boolean allowBackgroundData) {
    }

    public NetworkQuotaInfo getActiveNetworkQuotaInfo() {
        try {
            return this.mService.getActiveNetworkQuotaInfo();
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean getMobileDataEnabled() {
        try {
            return this.mService.getMobileDataEnabled();
        } catch (RemoteException e) {
            return true;
        }
    }

    public void setMobileDataEnabled(boolean enabled) {
        try {
            this.mService.setMobileDataEnabled(enabled);
        } catch (RemoteException e) {
        }
    }

    public ConnectivityManager(IConnectivityManager service) {
        this.mService = (IConnectivityManager) Preconditions.checkNotNull(service, "missing IConnectivityManager");
    }

    public static ConnectivityManager from(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String[] getTetherableIfaces() {
        try {
            return this.mService.getTetherableIfaces();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public String[] getTetheredIfaces() {
        try {
            return this.mService.getTetheredIfaces();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public String[] getTetheringErroredIfaces() {
        try {
            return this.mService.getTetheringErroredIfaces();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public int tether(String iface) {
        try {
            return this.mService.tether(iface);
        } catch (RemoteException e) {
            return 2;
        }
    }

    public int untether(String iface) {
        try {
            return this.mService.untether(iface);
        } catch (RemoteException e) {
            return 2;
        }
    }

    public boolean isTetheringSupported() {
        try {
            return this.mService.isTetheringSupported();
        } catch (RemoteException e) {
            return false;
        }
    }

    public String[] getTetherableUsbRegexs() {
        try {
            return this.mService.getTetherableUsbRegexs();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public String[] getTetherableWifiRegexs() {
        try {
            return this.mService.getTetherableWifiRegexs();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public String[] getTetherableBluetoothRegexs() {
        try {
            return this.mService.getTetherableBluetoothRegexs();
        } catch (RemoteException e) {
            return new String[0];
        }
    }

    public int setUsbTethering(boolean enable) {
        try {
            return this.mService.setUsbTethering(enable);
        } catch (RemoteException e) {
            return 2;
        }
    }

    public int getLastTetherError(String iface) {
        try {
            return this.mService.getLastTetherError(iface);
        } catch (RemoteException e) {
            return 2;
        }
    }

    public boolean requestNetworkTransitionWakelock(String forWhom) {
        try {
            this.mService.requestNetworkTransitionWakelock(forWhom);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void reportInetCondition(int networkType, int percentage) {
        try {
            this.mService.reportInetCondition(networkType, percentage);
        } catch (RemoteException e) {
        }
    }

    public void setGlobalProxy(ProxyProperties p) {
        try {
            this.mService.setGlobalProxy(p);
        } catch (RemoteException e) {
        }
    }

    public ProxyProperties getGlobalProxy() {
        try {
            return this.mService.getGlobalProxy();
        } catch (RemoteException e) {
            return null;
        }
    }

    public ProxyProperties getProxy() {
        try {
            return this.mService.getProxy();
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setDataDependency(int networkType, boolean met) {
        try {
            this.mService.setDataDependency(networkType, met);
        } catch (RemoteException e) {
        }
    }

    public boolean isNetworkSupported(int networkType) {
        try {
            return this.mService.isNetworkSupported(networkType);
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean isActiveNetworkMetered() {
        try {
            return this.mService.isActiveNetworkMetered();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean updateLockdownVpn() {
        try {
            return this.mService.updateLockdownVpn();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void captivePortalCheckComplete(NetworkInfo info) {
        try {
            this.mService.captivePortalCheckComplete(info);
        } catch (RemoteException e) {
        }
    }

    public void captivePortalCheckCompleted(NetworkInfo info, boolean isCaptivePortal) {
        try {
            this.mService.captivePortalCheckCompleted(info, isCaptivePortal);
        } catch (RemoteException e) {
        }
    }

    public void supplyMessenger(int networkType, Messenger messenger) {
        try {
            this.mService.supplyMessenger(networkType, messenger);
        } catch (RemoteException e) {
        }
    }

    public int checkMobileProvisioning(int suggestedTimeOutMs) {
        int timeOutMs = -1;
        try {
            timeOutMs = this.mService.checkMobileProvisioning(suggestedTimeOutMs);
        } catch (RemoteException e) {
        }
        return timeOutMs;
    }

    public String getMobileProvisioningUrl() {
        try {
            return this.mService.getMobileProvisioningUrl();
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getMobileRedirectedProvisioningUrl() {
        try {
            return this.mService.getMobileRedirectedProvisioningUrl();
        } catch (RemoteException e) {
            return null;
        }
    }

    public LinkQualityInfo getLinkQualityInfo(int networkType) {
        try {
            LinkQualityInfo li = this.mService.getLinkQualityInfo(networkType);
            return li;
        } catch (RemoteException e) {
            return null;
        }
    }

    public LinkQualityInfo getActiveLinkQualityInfo() {
        try {
            LinkQualityInfo li = this.mService.getActiveLinkQualityInfo();
            return li;
        } catch (RemoteException e) {
            return null;
        }
    }

    public LinkQualityInfo[] getAllLinkQualityInfo() {
        try {
            LinkQualityInfo[] li = this.mService.getAllLinkQualityInfo();
            return li;
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setProvisioningNotificationVisible(boolean visible, int networkType, String extraInfo, String url) {
        try {
            this.mService.setProvisioningNotificationVisible(visible, networkType, extraInfo, url);
        } catch (RemoteException e) {
        }
    }

    public void setAirplaneMode(boolean enable) {
        try {
            this.mService.setAirplaneMode(enable);
        } catch (RemoteException e) {
        }
    }
}