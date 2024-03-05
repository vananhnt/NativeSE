package android.os;

import android.net.INetworkManagementEventObserver;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.NetworkStats;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;

/* loaded from: INetworkManagementService.class */
public interface INetworkManagementService extends IInterface {
    void registerObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;

    void unregisterObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;

    String[] listInterfaces() throws RemoteException;

    InterfaceConfiguration getInterfaceConfig(String str) throws RemoteException;

    void setInterfaceConfig(String str, InterfaceConfiguration interfaceConfiguration) throws RemoteException;

    void clearInterfaceAddresses(String str) throws RemoteException;

    void setInterfaceDown(String str) throws RemoteException;

    void setInterfaceUp(String str) throws RemoteException;

    void setInterfaceIpv6PrivacyExtensions(String str, boolean z) throws RemoteException;

    void disableIpv6(String str) throws RemoteException;

    void enableIpv6(String str) throws RemoteException;

    RouteInfo[] getRoutes(String str) throws RemoteException;

    void addRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void removeRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void addSecondaryRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void removeSecondaryRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void setMtu(String str, int i) throws RemoteException;

    void shutdown() throws RemoteException;

    boolean getIpForwardingEnabled() throws RemoteException;

    void setIpForwardingEnabled(boolean z) throws RemoteException;

    void startTethering(String[] strArr) throws RemoteException;

    void stopTethering() throws RemoteException;

    boolean isTetheringStarted() throws RemoteException;

    void tetherInterface(String str) throws RemoteException;

    void untetherInterface(String str) throws RemoteException;

    String[] listTetheredInterfaces() throws RemoteException;

    void setDnsForwarders(String[] strArr) throws RemoteException;

    String[] getDnsForwarders() throws RemoteException;

    void enableNat(String str, String str2) throws RemoteException;

    void disableNat(String str, String str2) throws RemoteException;

    String[] listTtys() throws RemoteException;

    void attachPppd(String str, String str2, String str3, String str4, String str5) throws RemoteException;

    void detachPppd(String str) throws RemoteException;

    void wifiFirmwareReload(String str, String str2) throws RemoteException;

    void startAccessPoint(WifiConfiguration wifiConfiguration, String str) throws RemoteException;

    void stopAccessPoint(String str) throws RemoteException;

    void setAccessPoint(WifiConfiguration wifiConfiguration, String str) throws RemoteException;

    NetworkStats getNetworkStatsSummaryDev() throws RemoteException;

    NetworkStats getNetworkStatsSummaryXt() throws RemoteException;

    NetworkStats getNetworkStatsDetail() throws RemoteException;

    NetworkStats getNetworkStatsUidDetail(int i) throws RemoteException;

    NetworkStats getNetworkStatsTethering() throws RemoteException;

    void setInterfaceQuota(String str, long j) throws RemoteException;

    void removeInterfaceQuota(String str) throws RemoteException;

    void setInterfaceAlert(String str, long j) throws RemoteException;

    void removeInterfaceAlert(String str) throws RemoteException;

    void setGlobalAlert(long j) throws RemoteException;

    void setUidNetworkRules(int i, boolean z) throws RemoteException;

    boolean isBandwidthControlEnabled() throws RemoteException;

    void addIdleTimer(String str, int i, String str2) throws RemoteException;

    void removeIdleTimer(String str) throws RemoteException;

    void setDefaultInterfaceForDns(String str) throws RemoteException;

    void setDnsServersForInterface(String str, String[] strArr, String str2) throws RemoteException;

    void flushDefaultDnsCache() throws RemoteException;

    void flushInterfaceDnsCache(String str) throws RemoteException;

    void setFirewallEnabled(boolean z) throws RemoteException;

    boolean isFirewallEnabled() throws RemoteException;

    void setFirewallInterfaceRule(String str, boolean z) throws RemoteException;

    void setFirewallEgressSourceRule(String str, boolean z) throws RemoteException;

    void setFirewallEgressDestRule(String str, int i, boolean z) throws RemoteException;

    void setFirewallUidRule(int i, boolean z) throws RemoteException;

    void setUidRangeRoute(String str, int i, int i2) throws RemoteException;

    void clearUidRangeRoute(String str, int i, int i2) throws RemoteException;

    void setMarkedForwarding(String str) throws RemoteException;

    void clearMarkedForwarding(String str) throws RemoteException;

    int getMarkForUid(int i) throws RemoteException;

    int getMarkForProtect() throws RemoteException;

    void setMarkedForwardingRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void clearMarkedForwardingRoute(String str, RouteInfo routeInfo) throws RemoteException;

    void setHostExemption(LinkAddress linkAddress) throws RemoteException;

    void clearHostExemption(LinkAddress linkAddress) throws RemoteException;

    void setDnsInterfaceForPid(String str, int i) throws RemoteException;

    void clearDnsInterfaceForPid(int i) throws RemoteException;

    void setDnsInterfaceForUidRange(String str, int i, int i2) throws RemoteException;

    void clearDnsInterfaceForUidRange(int i, int i2) throws RemoteException;

    void clearDnsInterfaceMaps() throws RemoteException;

    void startClatd(String str) throws RemoteException;

    void stopClatd() throws RemoteException;

    boolean isClatdStarted() throws RemoteException;

    /* loaded from: INetworkManagementService$Stub.class */
    public static abstract class Stub extends Binder implements INetworkManagementService {
        private static final String DESCRIPTOR = "android.os.INetworkManagementService";
        static final int TRANSACTION_registerObserver = 1;
        static final int TRANSACTION_unregisterObserver = 2;
        static final int TRANSACTION_listInterfaces = 3;
        static final int TRANSACTION_getInterfaceConfig = 4;
        static final int TRANSACTION_setInterfaceConfig = 5;
        static final int TRANSACTION_clearInterfaceAddresses = 6;
        static final int TRANSACTION_setInterfaceDown = 7;
        static final int TRANSACTION_setInterfaceUp = 8;
        static final int TRANSACTION_setInterfaceIpv6PrivacyExtensions = 9;
        static final int TRANSACTION_disableIpv6 = 10;
        static final int TRANSACTION_enableIpv6 = 11;
        static final int TRANSACTION_getRoutes = 12;
        static final int TRANSACTION_addRoute = 13;
        static final int TRANSACTION_removeRoute = 14;
        static final int TRANSACTION_addSecondaryRoute = 15;
        static final int TRANSACTION_removeSecondaryRoute = 16;
        static final int TRANSACTION_setMtu = 17;
        static final int TRANSACTION_shutdown = 18;
        static final int TRANSACTION_getIpForwardingEnabled = 19;
        static final int TRANSACTION_setIpForwardingEnabled = 20;
        static final int TRANSACTION_startTethering = 21;
        static final int TRANSACTION_stopTethering = 22;
        static final int TRANSACTION_isTetheringStarted = 23;
        static final int TRANSACTION_tetherInterface = 24;
        static final int TRANSACTION_untetherInterface = 25;
        static final int TRANSACTION_listTetheredInterfaces = 26;
        static final int TRANSACTION_setDnsForwarders = 27;
        static final int TRANSACTION_getDnsForwarders = 28;
        static final int TRANSACTION_enableNat = 29;
        static final int TRANSACTION_disableNat = 30;
        static final int TRANSACTION_listTtys = 31;
        static final int TRANSACTION_attachPppd = 32;
        static final int TRANSACTION_detachPppd = 33;
        static final int TRANSACTION_wifiFirmwareReload = 34;
        static final int TRANSACTION_startAccessPoint = 35;
        static final int TRANSACTION_stopAccessPoint = 36;
        static final int TRANSACTION_setAccessPoint = 37;
        static final int TRANSACTION_getNetworkStatsSummaryDev = 38;
        static final int TRANSACTION_getNetworkStatsSummaryXt = 39;
        static final int TRANSACTION_getNetworkStatsDetail = 40;
        static final int TRANSACTION_getNetworkStatsUidDetail = 41;
        static final int TRANSACTION_getNetworkStatsTethering = 42;
        static final int TRANSACTION_setInterfaceQuota = 43;
        static final int TRANSACTION_removeInterfaceQuota = 44;
        static final int TRANSACTION_setInterfaceAlert = 45;
        static final int TRANSACTION_removeInterfaceAlert = 46;
        static final int TRANSACTION_setGlobalAlert = 47;
        static final int TRANSACTION_setUidNetworkRules = 48;
        static final int TRANSACTION_isBandwidthControlEnabled = 49;
        static final int TRANSACTION_addIdleTimer = 50;
        static final int TRANSACTION_removeIdleTimer = 51;
        static final int TRANSACTION_setDefaultInterfaceForDns = 52;
        static final int TRANSACTION_setDnsServersForInterface = 53;
        static final int TRANSACTION_flushDefaultDnsCache = 54;
        static final int TRANSACTION_flushInterfaceDnsCache = 55;
        static final int TRANSACTION_setFirewallEnabled = 56;
        static final int TRANSACTION_isFirewallEnabled = 57;
        static final int TRANSACTION_setFirewallInterfaceRule = 58;
        static final int TRANSACTION_setFirewallEgressSourceRule = 59;
        static final int TRANSACTION_setFirewallEgressDestRule = 60;
        static final int TRANSACTION_setFirewallUidRule = 61;
        static final int TRANSACTION_setUidRangeRoute = 62;
        static final int TRANSACTION_clearUidRangeRoute = 63;
        static final int TRANSACTION_setMarkedForwarding = 64;
        static final int TRANSACTION_clearMarkedForwarding = 65;
        static final int TRANSACTION_getMarkForUid = 66;
        static final int TRANSACTION_getMarkForProtect = 67;
        static final int TRANSACTION_setMarkedForwardingRoute = 68;
        static final int TRANSACTION_clearMarkedForwardingRoute = 69;
        static final int TRANSACTION_setHostExemption = 70;
        static final int TRANSACTION_clearHostExemption = 71;
        static final int TRANSACTION_setDnsInterfaceForPid = 72;
        static final int TRANSACTION_clearDnsInterfaceForPid = 73;
        static final int TRANSACTION_setDnsInterfaceForUidRange = 74;
        static final int TRANSACTION_clearDnsInterfaceForUidRange = 75;
        static final int TRANSACTION_clearDnsInterfaceMaps = 76;
        static final int TRANSACTION_startClatd = 77;
        static final int TRANSACTION_stopClatd = 78;
        static final int TRANSACTION_isClatdStarted = 79;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkManagementService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INetworkManagementService)) {
                return (INetworkManagementService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            LinkAddress _arg0;
            LinkAddress _arg02;
            RouteInfo _arg1;
            RouteInfo _arg12;
            WifiConfiguration _arg03;
            WifiConfiguration _arg04;
            RouteInfo _arg13;
            RouteInfo _arg14;
            RouteInfo _arg15;
            RouteInfo _arg16;
            InterfaceConfiguration _arg17;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    INetworkManagementEventObserver _arg05 = INetworkManagementEventObserver.Stub.asInterface(data.readStrongBinder());
                    registerObserver(_arg05);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    INetworkManagementEventObserver _arg06 = INetworkManagementEventObserver.Stub.asInterface(data.readStrongBinder());
                    unregisterObserver(_arg06);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result = listInterfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    InterfaceConfiguration _result2 = getInterfaceConfig(_arg07);
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    if (0 != data.readInt()) {
                        _arg17 = InterfaceConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg17 = null;
                    }
                    setInterfaceConfig(_arg08, _arg17);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    clearInterfaceAddresses(_arg09);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    setInterfaceDown(_arg010);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    setInterfaceUp(_arg011);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    boolean _arg18 = 0 != data.readInt();
                    setInterfaceIpv6PrivacyExtensions(_arg012, _arg18);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    disableIpv6(_arg013);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    enableIpv6(_arg014);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    RouteInfo[] _result3 = getRoutes(_arg015);
                    reply.writeNoException();
                    reply.writeTypedArray(_result3, 1);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    if (0 != data.readInt()) {
                        _arg16 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg16 = null;
                    }
                    addRoute(_arg016, _arg16);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg017 = data.readString();
                    if (0 != data.readInt()) {
                        _arg15 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg15 = null;
                    }
                    removeRoute(_arg017, _arg15);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    if (0 != data.readInt()) {
                        _arg14 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    addSecondaryRoute(_arg018, _arg14);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    if (0 != data.readInt()) {
                        _arg13 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    removeSecondaryRoute(_arg019, _arg13);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg020 = data.readString();
                    int _arg19 = data.readInt();
                    setMtu(_arg020, _arg19);
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    shutdown();
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = getIpForwardingEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg021 = 0 != data.readInt();
                    setIpForwardingEnabled(_arg021);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg022 = data.createStringArray();
                    startTethering(_arg022);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    stopTethering();
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = isTetheringStarted();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg023 = data.readString();
                    tetherInterface(_arg023);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg024 = data.readString();
                    untetherInterface(_arg024);
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result6 = listTetheredInterfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result6);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg025 = data.createStringArray();
                    setDnsForwarders(_arg025);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result7 = getDnsForwarders();
                    reply.writeNoException();
                    reply.writeStringArray(_result7);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg026 = data.readString();
                    String _arg110 = data.readString();
                    enableNat(_arg026, _arg110);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg027 = data.readString();
                    String _arg111 = data.readString();
                    disableNat(_arg027, _arg111);
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result8 = listTtys();
                    reply.writeNoException();
                    reply.writeStringArray(_result8);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg028 = data.readString();
                    String _arg112 = data.readString();
                    String _arg2 = data.readString();
                    String _arg3 = data.readString();
                    String _arg4 = data.readString();
                    attachPppd(_arg028, _arg112, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg029 = data.readString();
                    detachPppd(_arg029);
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg030 = data.readString();
                    String _arg113 = data.readString();
                    wifiFirmwareReload(_arg030, _arg113);
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = WifiConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    String _arg114 = data.readString();
                    startAccessPoint(_arg04, _arg114);
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg031 = data.readString();
                    stopAccessPoint(_arg031);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = WifiConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    String _arg115 = data.readString();
                    setAccessPoint(_arg03, _arg115);
                    reply.writeNoException();
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkStats _result9 = getNetworkStatsSummaryDev();
                    reply.writeNoException();
                    if (_result9 != null) {
                        reply.writeInt(1);
                        _result9.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkStats _result10 = getNetworkStatsSummaryXt();
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(1);
                        _result10.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkStats _result11 = getNetworkStatsDetail();
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    NetworkStats _result12 = getNetworkStatsUidDetail(_arg032);
                    reply.writeNoException();
                    if (_result12 != null) {
                        reply.writeInt(1);
                        _result12.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkStats _result13 = getNetworkStatsTethering();
                    reply.writeNoException();
                    if (_result13 != null) {
                        reply.writeInt(1);
                        _result13.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg033 = data.readString();
                    long _arg116 = data.readLong();
                    setInterfaceQuota(_arg033, _arg116);
                    reply.writeNoException();
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg034 = data.readString();
                    removeInterfaceQuota(_arg034);
                    reply.writeNoException();
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg035 = data.readString();
                    long _arg117 = data.readLong();
                    setInterfaceAlert(_arg035, _arg117);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg036 = data.readString();
                    removeInterfaceAlert(_arg036);
                    reply.writeNoException();
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg037 = data.readLong();
                    setGlobalAlert(_arg037);
                    reply.writeNoException();
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    boolean _arg118 = 0 != data.readInt();
                    setUidNetworkRules(_arg038, _arg118);
                    reply.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result14 = isBandwidthControlEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result14 ? 1 : 0);
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg039 = data.readString();
                    int _arg119 = data.readInt();
                    String _arg22 = data.readString();
                    addIdleTimer(_arg039, _arg119, _arg22);
                    reply.writeNoException();
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg040 = data.readString();
                    removeIdleTimer(_arg040);
                    reply.writeNoException();
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg041 = data.readString();
                    setDefaultInterfaceForDns(_arg041);
                    reply.writeNoException();
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg042 = data.readString();
                    String[] _arg120 = data.createStringArray();
                    String _arg23 = data.readString();
                    setDnsServersForInterface(_arg042, _arg120, _arg23);
                    reply.writeNoException();
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    flushDefaultDnsCache();
                    reply.writeNoException();
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg043 = data.readString();
                    flushInterfaceDnsCache(_arg043);
                    reply.writeNoException();
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg044 = 0 != data.readInt();
                    setFirewallEnabled(_arg044);
                    reply.writeNoException();
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = isFirewallEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 58:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg045 = data.readString();
                    boolean _arg121 = 0 != data.readInt();
                    setFirewallInterfaceRule(_arg045, _arg121);
                    reply.writeNoException();
                    return true;
                case 59:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg046 = data.readString();
                    boolean _arg122 = 0 != data.readInt();
                    setFirewallEgressSourceRule(_arg046, _arg122);
                    reply.writeNoException();
                    return true;
                case 60:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg047 = data.readString();
                    int _arg123 = data.readInt();
                    boolean _arg24 = 0 != data.readInt();
                    setFirewallEgressDestRule(_arg047, _arg123, _arg24);
                    reply.writeNoException();
                    return true;
                case 61:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg048 = data.readInt();
                    boolean _arg124 = 0 != data.readInt();
                    setFirewallUidRule(_arg048, _arg124);
                    reply.writeNoException();
                    return true;
                case 62:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg049 = data.readString();
                    int _arg125 = data.readInt();
                    int _arg25 = data.readInt();
                    setUidRangeRoute(_arg049, _arg125, _arg25);
                    reply.writeNoException();
                    return true;
                case 63:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg050 = data.readString();
                    int _arg126 = data.readInt();
                    int _arg26 = data.readInt();
                    clearUidRangeRoute(_arg050, _arg126, _arg26);
                    reply.writeNoException();
                    return true;
                case 64:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg051 = data.readString();
                    setMarkedForwarding(_arg051);
                    reply.writeNoException();
                    return true;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg052 = data.readString();
                    clearMarkedForwarding(_arg052);
                    reply.writeNoException();
                    return true;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg053 = data.readInt();
                    int _result16 = getMarkForUid(_arg053);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    int _result17 = getMarkForProtect();
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    return true;
                case 68:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg054 = data.readString();
                    if (0 != data.readInt()) {
                        _arg12 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    setMarkedForwardingRoute(_arg054, _arg12);
                    reply.writeNoException();
                    return true;
                case 69:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg055 = data.readString();
                    if (0 != data.readInt()) {
                        _arg1 = RouteInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    clearMarkedForwardingRoute(_arg055, _arg1);
                    reply.writeNoException();
                    return true;
                case 70:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = LinkAddress.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    setHostExemption(_arg02);
                    reply.writeNoException();
                    return true;
                case 71:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = LinkAddress.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    clearHostExemption(_arg0);
                    reply.writeNoException();
                    return true;
                case 72:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg056 = data.readString();
                    int _arg127 = data.readInt();
                    setDnsInterfaceForPid(_arg056, _arg127);
                    reply.writeNoException();
                    return true;
                case 73:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg057 = data.readInt();
                    clearDnsInterfaceForPid(_arg057);
                    reply.writeNoException();
                    return true;
                case 74:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg058 = data.readString();
                    int _arg128 = data.readInt();
                    int _arg27 = data.readInt();
                    setDnsInterfaceForUidRange(_arg058, _arg128, _arg27);
                    reply.writeNoException();
                    return true;
                case 75:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg059 = data.readInt();
                    int _arg129 = data.readInt();
                    clearDnsInterfaceForUidRange(_arg059, _arg129);
                    reply.writeNoException();
                    return true;
                case 76:
                    data.enforceInterface(DESCRIPTOR);
                    clearDnsInterfaceMaps();
                    reply.writeNoException();
                    return true;
                case 77:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg060 = data.readString();
                    startClatd(_arg060);
                    reply.writeNoException();
                    return true;
                case 78:
                    data.enforceInterface(DESCRIPTOR);
                    stopClatd();
                    reply.writeNoException();
                    return true;
                case 79:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result18 = isClatdStarted();
                    reply.writeNoException();
                    reply.writeInt(_result18 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: INetworkManagementService$Stub$Proxy.class */
        private static class Proxy implements INetworkManagementService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.os.INetworkManagementService
            public void registerObserver(INetworkManagementEventObserver obs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(obs != null ? obs.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void unregisterObserver(INetworkManagementEventObserver obs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(obs != null ? obs.asBinder() : null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public String[] listInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public InterfaceConfiguration getInterfaceConfig(String iface) throws RemoteException {
                InterfaceConfiguration _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = InterfaceConfiguration.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceConfig(String iface, InterfaceConfiguration cfg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (cfg != null) {
                        _data.writeInt(1);
                        cfg.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearInterfaceAddresses(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceDown(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceUp(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceIpv6PrivacyExtensions(String iface, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void disableIpv6(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void enableIpv6(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public RouteInfo[] getRoutes(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    RouteInfo[] _result = (RouteInfo[]) _reply.createTypedArray(RouteInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void addRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void removeRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void addSecondaryRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void removeSecondaryRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setMtu(String iface, int mtu) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(mtu);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void shutdown() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public boolean getIpForwardingEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void setIpForwardingEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void startTethering(String[] dhcpRanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(dhcpRanges);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void stopTethering() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public boolean isTetheringStarted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void tetherInterface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void untetherInterface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public String[] listTetheredInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setDnsForwarders(String[] dns) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(dns);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public String[] getDnsForwarders() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void enableNat(String internalInterface, String externalInterface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(internalInterface);
                    _data.writeString(externalInterface);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void disableNat(String internalInterface, String externalInterface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(internalInterface);
                    _data.writeString(externalInterface);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public String[] listTtys() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void attachPppd(String tty, String localAddr, String remoteAddr, String dns1Addr, String dns2Addr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tty);
                    _data.writeString(localAddr);
                    _data.writeString(remoteAddr);
                    _data.writeString(dns1Addr);
                    _data.writeString(dns2Addr);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void detachPppd(String tty) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tty);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void wifiFirmwareReload(String wlanIface, String mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(wlanIface);
                    _data.writeString(mode);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void startAccessPoint(WifiConfiguration wifiConfig, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (wifiConfig != null) {
                        _data.writeInt(1);
                        wifiConfig.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(iface);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void stopAccessPoint(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setAccessPoint(WifiConfiguration wifiConfig, String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (wifiConfig != null) {
                        _data.writeInt(1);
                        wifiConfig.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(iface);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public NetworkStats getNetworkStatsSummaryDev() throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public NetworkStats getNetworkStatsSummaryXt() throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public NetworkStats getNetworkStatsDetail() throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public NetworkStats getNetworkStatsUidDetail(int uid) throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public NetworkStats getNetworkStatsTethering() throws RemoteException {
                NetworkStats _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkStats.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceQuota(String iface, long quotaBytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeLong(quotaBytes);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void removeInterfaceQuota(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setInterfaceAlert(String iface, long alertBytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeLong(alertBytes);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void removeInterfaceAlert(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setGlobalAlert(long alertBytes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(alertBytes);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setUidNetworkRules(int uid, boolean rejectOnQuotaInterfaces) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(rejectOnQuotaInterfaces ? 1 : 0);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public boolean isBandwidthControlEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void addIdleTimer(String iface, int timeout, String label) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(timeout);
                    _data.writeString(label);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void removeIdleTimer(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setDefaultInterfaceForDns(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setDnsServersForInterface(String iface, String[] servers, String domains) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeStringArray(servers);
                    _data.writeString(domains);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void flushDefaultDnsCache() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void flushInterfaceDnsCache(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setFirewallEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public boolean isFirewallEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.os.INetworkManagementService
            public void setFirewallInterfaceRule(String iface, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(allow ? 1 : 0);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setFirewallEgressSourceRule(String addr, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(addr);
                    _data.writeInt(allow ? 1 : 0);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setFirewallEgressDestRule(String addr, int port, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(addr);
                    _data.writeInt(port);
                    _data.writeInt(allow ? 1 : 0);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setFirewallUidRule(int uid, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(allow ? 1 : 0);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setUidRangeRoute(String iface, int uid_start, int uid_end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(uid_start);
                    _data.writeInt(uid_end);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearUidRangeRoute(String iface, int uid_start, int uid_end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(uid_start);
                    _data.writeInt(uid_end);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setMarkedForwarding(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearMarkedForwarding(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public int getMarkForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public int getMarkForProtect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setMarkedForwardingRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearMarkedForwardingRoute(String iface, RouteInfo route) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    if (route != null) {
                        _data.writeInt(1);
                        route.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setHostExemption(LinkAddress host) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (host != null) {
                        _data.writeInt(1);
                        host.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearHostExemption(LinkAddress host) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (host != null) {
                        _data.writeInt(1);
                        host.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setDnsInterfaceForPid(String iface, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(pid);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearDnsInterfaceForPid(int pid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pid);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void setDnsInterfaceForUidRange(String iface, int uid_start, int uid_end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(uid_start);
                    _data.writeInt(uid_end);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearDnsInterfaceForUidRange(int uid_start, int uid_end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid_start);
                    _data.writeInt(uid_end);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void clearDnsInterfaceMaps() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void startClatd(String interfaceName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(interfaceName);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public void stopClatd() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.INetworkManagementService
            public boolean isClatdStarted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}