package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;

/* loaded from: IConnectivityManager.class */
public interface IConnectivityManager extends IInterface {
    void markSocketAsUser(ParcelFileDescriptor parcelFileDescriptor, int i) throws RemoteException;

    void setNetworkPreference(int i) throws RemoteException;

    int getNetworkPreference() throws RemoteException;

    NetworkInfo getActiveNetworkInfo() throws RemoteException;

    NetworkInfo getActiveNetworkInfoForUid(int i) throws RemoteException;

    NetworkInfo getNetworkInfo(int i) throws RemoteException;

    NetworkInfo[] getAllNetworkInfo() throws RemoteException;

    NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException;

    boolean isNetworkSupported(int i) throws RemoteException;

    LinkProperties getActiveLinkProperties() throws RemoteException;

    LinkProperties getLinkProperties(int i) throws RemoteException;

    NetworkState[] getAllNetworkState() throws RemoteException;

    NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException;

    boolean isActiveNetworkMetered() throws RemoteException;

    boolean setRadios(boolean z) throws RemoteException;

    boolean setRadio(int i, boolean z) throws RemoteException;

    int startUsingNetworkFeature(int i, String str, IBinder iBinder) throws RemoteException;

    int stopUsingNetworkFeature(int i, String str) throws RemoteException;

    boolean requestRouteToHost(int i, int i2) throws RemoteException;

    boolean requestRouteToHostAddress(int i, byte[] bArr) throws RemoteException;

    boolean getMobileDataEnabled() throws RemoteException;

    void setMobileDataEnabled(boolean z) throws RemoteException;

    void setPolicyDataEnable(int i, boolean z) throws RemoteException;

    int tether(String str) throws RemoteException;

    int untether(String str) throws RemoteException;

    int getLastTetherError(String str) throws RemoteException;

    boolean isTetheringSupported() throws RemoteException;

    String[] getTetherableIfaces() throws RemoteException;

    String[] getTetheredIfaces() throws RemoteException;

    String[] getTetheringErroredIfaces() throws RemoteException;

    String[] getTetherableUsbRegexs() throws RemoteException;

    String[] getTetherableWifiRegexs() throws RemoteException;

    String[] getTetherableBluetoothRegexs() throws RemoteException;

    int setUsbTethering(boolean z) throws RemoteException;

    void requestNetworkTransitionWakelock(String str) throws RemoteException;

    void reportInetCondition(int i, int i2) throws RemoteException;

    ProxyProperties getGlobalProxy() throws RemoteException;

    void setGlobalProxy(ProxyProperties proxyProperties) throws RemoteException;

    ProxyProperties getProxy() throws RemoteException;

    void setDataDependency(int i, boolean z) throws RemoteException;

    boolean protectVpn(ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    boolean prepareVpn(String str, String str2) throws RemoteException;

    ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) throws RemoteException;

    VpnConfig getVpnConfig() throws RemoteException;

    void startLegacyVpn(VpnProfile vpnProfile) throws RemoteException;

    LegacyVpnInfo getLegacyVpnInfo() throws RemoteException;

    boolean updateLockdownVpn() throws RemoteException;

    void captivePortalCheckComplete(NetworkInfo networkInfo) throws RemoteException;

    void captivePortalCheckCompleted(NetworkInfo networkInfo, boolean z) throws RemoteException;

    void supplyMessenger(int i, Messenger messenger) throws RemoteException;

    int findConnectionTypeForIface(String str) throws RemoteException;

    int checkMobileProvisioning(int i) throws RemoteException;

    String getMobileProvisioningUrl() throws RemoteException;

    String getMobileRedirectedProvisioningUrl() throws RemoteException;

    LinkQualityInfo getLinkQualityInfo(int i) throws RemoteException;

    LinkQualityInfo getActiveLinkQualityInfo() throws RemoteException;

    LinkQualityInfo[] getAllLinkQualityInfo() throws RemoteException;

    void setProvisioningNotificationVisible(boolean z, int i, String str, String str2) throws RemoteException;

    void setAirplaneMode(boolean z) throws RemoteException;

    /* loaded from: IConnectivityManager$Stub.class */
    public static abstract class Stub extends Binder implements IConnectivityManager {
        private static final String DESCRIPTOR = "android.net.IConnectivityManager";
        static final int TRANSACTION_markSocketAsUser = 1;
        static final int TRANSACTION_setNetworkPreference = 2;
        static final int TRANSACTION_getNetworkPreference = 3;
        static final int TRANSACTION_getActiveNetworkInfo = 4;
        static final int TRANSACTION_getActiveNetworkInfoForUid = 5;
        static final int TRANSACTION_getNetworkInfo = 6;
        static final int TRANSACTION_getAllNetworkInfo = 7;
        static final int TRANSACTION_getProvisioningOrActiveNetworkInfo = 8;
        static final int TRANSACTION_isNetworkSupported = 9;
        static final int TRANSACTION_getActiveLinkProperties = 10;
        static final int TRANSACTION_getLinkProperties = 11;
        static final int TRANSACTION_getAllNetworkState = 12;
        static final int TRANSACTION_getActiveNetworkQuotaInfo = 13;
        static final int TRANSACTION_isActiveNetworkMetered = 14;
        static final int TRANSACTION_setRadios = 15;
        static final int TRANSACTION_setRadio = 16;
        static final int TRANSACTION_startUsingNetworkFeature = 17;
        static final int TRANSACTION_stopUsingNetworkFeature = 18;
        static final int TRANSACTION_requestRouteToHost = 19;
        static final int TRANSACTION_requestRouteToHostAddress = 20;
        static final int TRANSACTION_getMobileDataEnabled = 21;
        static final int TRANSACTION_setMobileDataEnabled = 22;
        static final int TRANSACTION_setPolicyDataEnable = 23;
        static final int TRANSACTION_tether = 24;
        static final int TRANSACTION_untether = 25;
        static final int TRANSACTION_getLastTetherError = 26;
        static final int TRANSACTION_isTetheringSupported = 27;
        static final int TRANSACTION_getTetherableIfaces = 28;
        static final int TRANSACTION_getTetheredIfaces = 29;
        static final int TRANSACTION_getTetheringErroredIfaces = 30;
        static final int TRANSACTION_getTetherableUsbRegexs = 31;
        static final int TRANSACTION_getTetherableWifiRegexs = 32;
        static final int TRANSACTION_getTetherableBluetoothRegexs = 33;
        static final int TRANSACTION_setUsbTethering = 34;
        static final int TRANSACTION_requestNetworkTransitionWakelock = 35;
        static final int TRANSACTION_reportInetCondition = 36;
        static final int TRANSACTION_getGlobalProxy = 37;
        static final int TRANSACTION_setGlobalProxy = 38;
        static final int TRANSACTION_getProxy = 39;
        static final int TRANSACTION_setDataDependency = 40;
        static final int TRANSACTION_protectVpn = 41;
        static final int TRANSACTION_prepareVpn = 42;
        static final int TRANSACTION_establishVpn = 43;
        static final int TRANSACTION_getVpnConfig = 44;
        static final int TRANSACTION_startLegacyVpn = 45;
        static final int TRANSACTION_getLegacyVpnInfo = 46;
        static final int TRANSACTION_updateLockdownVpn = 47;
        static final int TRANSACTION_captivePortalCheckComplete = 48;
        static final int TRANSACTION_captivePortalCheckCompleted = 49;
        static final int TRANSACTION_supplyMessenger = 50;
        static final int TRANSACTION_findConnectionTypeForIface = 51;
        static final int TRANSACTION_checkMobileProvisioning = 52;
        static final int TRANSACTION_getMobileProvisioningUrl = 53;
        static final int TRANSACTION_getMobileRedirectedProvisioningUrl = 54;
        static final int TRANSACTION_getLinkQualityInfo = 55;
        static final int TRANSACTION_getActiveLinkQualityInfo = 56;
        static final int TRANSACTION_getAllLinkQualityInfo = 57;
        static final int TRANSACTION_setProvisioningNotificationVisible = 58;
        static final int TRANSACTION_setAirplaneMode = 59;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IConnectivityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IConnectivityManager)) {
                return (IConnectivityManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Messenger _arg1;
            NetworkInfo _arg0;
            NetworkInfo _arg02;
            VpnProfile _arg03;
            VpnConfig _arg04;
            ParcelFileDescriptor _arg05;
            ProxyProperties _arg06;
            ParcelFileDescriptor _arg07;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _arg12 = data.readInt();
                    markSocketAsUser(_arg07, _arg12);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    setNetworkPreference(_arg08);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getNetworkPreference();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkInfo _result2 = getActiveNetworkInfo();
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
                    int _arg09 = data.readInt();
                    NetworkInfo _result3 = getActiveNetworkInfoForUid(_arg09);
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    NetworkInfo _result4 = getNetworkInfo(_arg010);
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(1);
                        _result4.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkInfo[] _result5 = getAllNetworkInfo();
                    reply.writeNoException();
                    reply.writeTypedArray(_result5, 1);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkInfo _result6 = getProvisioningOrActiveNetworkInfo();
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(1);
                        _result6.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    boolean _result7 = isNetworkSupported(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    LinkProperties _result8 = getActiveLinkProperties();
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(1);
                        _result8.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    LinkProperties _result9 = getLinkProperties(_arg012);
                    reply.writeNoException();
                    if (_result9 != null) {
                        reply.writeInt(1);
                        _result9.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkState[] _result10 = getAllNetworkState();
                    reply.writeNoException();
                    reply.writeTypedArray(_result10, 1);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkQuotaInfo _result11 = getActiveNetworkQuotaInfo();
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result12 = isActiveNetworkMetered();
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg013 = 0 != data.readInt();
                    boolean _result13 = setRadios(_arg013);
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    boolean _arg13 = 0 != data.readInt();
                    boolean _result14 = setRadio(_arg014, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result14 ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    String _arg14 = data.readString();
                    IBinder _arg2 = data.readStrongBinder();
                    int _result15 = startUsingNetworkFeature(_arg015, _arg14, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    String _arg15 = data.readString();
                    int _result16 = stopUsingNetworkFeature(_arg016, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    int _arg16 = data.readInt();
                    boolean _result17 = requestRouteToHost(_arg017, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    byte[] _arg17 = data.createByteArray();
                    boolean _result18 = requestRouteToHostAddress(_arg018, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result18 ? 1 : 0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result19 = getMobileDataEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result19 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg019 = 0 != data.readInt();
                    setMobileDataEnabled(_arg019);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    boolean _arg18 = 0 != data.readInt();
                    setPolicyDataEnable(_arg020, _arg18);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg021 = data.readString();
                    int _result20 = tether(_arg021);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg022 = data.readString();
                    int _result21 = untether(_arg022);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg023 = data.readString();
                    int _result22 = getLastTetherError(_arg023);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result23 = isTetheringSupported();
                    reply.writeNoException();
                    reply.writeInt(_result23 ? 1 : 0);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result24 = getTetherableIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result24);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result25 = getTetheredIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result25);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result26 = getTetheringErroredIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result26);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result27 = getTetherableUsbRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result27);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result28 = getTetherableWifiRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result28);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result29 = getTetherableBluetoothRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result29);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg024 = 0 != data.readInt();
                    int _result30 = setUsbTethering(_arg024);
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg025 = data.readString();
                    requestNetworkTransitionWakelock(_arg025);
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    int _arg19 = data.readInt();
                    reportInetCondition(_arg026, _arg19);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    ProxyProperties _result31 = getGlobalProxy();
                    reply.writeNoException();
                    if (_result31 != null) {
                        reply.writeInt(1);
                        _result31.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = ProxyProperties.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    setGlobalProxy(_arg06);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    ProxyProperties _result32 = getProxy();
                    reply.writeNoException();
                    if (_result32 != null) {
                        reply.writeInt(1);
                        _result32.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    boolean _arg110 = 0 != data.readInt();
                    setDataDependency(_arg027, _arg110);
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    boolean _result33 = protectVpn(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result33 ? 1 : 0);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg028 = data.readString();
                    String _arg111 = data.readString();
                    boolean _result34 = prepareVpn(_arg028, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result34 ? 1 : 0);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = VpnConfig.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    ParcelFileDescriptor _result35 = establishVpn(_arg04);
                    reply.writeNoException();
                    if (_result35 != null) {
                        reply.writeInt(1);
                        _result35.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    VpnConfig _result36 = getVpnConfig();
                    reply.writeNoException();
                    if (_result36 != null) {
                        reply.writeInt(1);
                        _result36.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = VpnProfile.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    startLegacyVpn(_arg03);
                    reply.writeNoException();
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    LegacyVpnInfo _result37 = getLegacyVpnInfo();
                    reply.writeNoException();
                    if (_result37 != null) {
                        reply.writeInt(1);
                        _result37.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result38 = updateLockdownVpn();
                    reply.writeNoException();
                    reply.writeInt(_result38 ? 1 : 0);
                    return true;
                case 48:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = NetworkInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    captivePortalCheckComplete(_arg02);
                    reply.writeNoException();
                    return true;
                case 49:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = NetworkInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    boolean _arg112 = 0 != data.readInt();
                    captivePortalCheckCompleted(_arg0, _arg112);
                    reply.writeNoException();
                    return true;
                case 50:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    supplyMessenger(_arg029, _arg1);
                    reply.writeNoException();
                    return true;
                case 51:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg030 = data.readString();
                    int _result39 = findConnectionTypeForIface(_arg030);
                    reply.writeNoException();
                    reply.writeInt(_result39);
                    return true;
                case 52:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg031 = data.readInt();
                    int _result40 = checkMobileProvisioning(_arg031);
                    reply.writeNoException();
                    reply.writeInt(_result40);
                    return true;
                case 53:
                    data.enforceInterface(DESCRIPTOR);
                    String _result41 = getMobileProvisioningUrl();
                    reply.writeNoException();
                    reply.writeString(_result41);
                    return true;
                case 54:
                    data.enforceInterface(DESCRIPTOR);
                    String _result42 = getMobileRedirectedProvisioningUrl();
                    reply.writeNoException();
                    reply.writeString(_result42);
                    return true;
                case 55:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    LinkQualityInfo _result43 = getLinkQualityInfo(_arg032);
                    reply.writeNoException();
                    if (_result43 != null) {
                        reply.writeInt(1);
                        _result43.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 56:
                    data.enforceInterface(DESCRIPTOR);
                    LinkQualityInfo _result44 = getActiveLinkQualityInfo();
                    reply.writeNoException();
                    if (_result44 != null) {
                        reply.writeInt(1);
                        _result44.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 57:
                    data.enforceInterface(DESCRIPTOR);
                    LinkQualityInfo[] _result45 = getAllLinkQualityInfo();
                    reply.writeNoException();
                    reply.writeTypedArray(_result45, 1);
                    return true;
                case 58:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg033 = 0 != data.readInt();
                    int _arg113 = data.readInt();
                    String _arg22 = data.readString();
                    String _arg3 = data.readString();
                    setProvisioningNotificationVisible(_arg033, _arg113, _arg22, _arg3);
                    reply.writeNoException();
                    return true;
                case 59:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg034 = 0 != data.readInt();
                    setAirplaneMode(_arg034);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IConnectivityManager$Stub$Proxy.class */
        private static class Proxy implements IConnectivityManager {
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

            @Override // android.net.IConnectivityManager
            public void markSocketAsUser(ParcelFileDescriptor socket, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(uid);
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

            @Override // android.net.IConnectivityManager
            public void setNetworkPreference(int pref) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pref);
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

            @Override // android.net.IConnectivityManager
            public int getNetworkPreference() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public NetworkInfo getActiveNetworkInfo() throws RemoteException {
                NetworkInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getActiveNetworkInfoForUid(int uid) throws RemoteException {
                NetworkInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getNetworkInfo(int networkType) throws RemoteException {
                NetworkInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo[] getAllNetworkInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    NetworkInfo[] _result = (NetworkInfo[]) _reply.createTypedArray(NetworkInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException {
                NetworkInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean isNetworkSupported(int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkProperties getActiveLinkProperties() throws RemoteException {
                LinkProperties _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = LinkProperties.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkProperties getLinkProperties(int networkType) throws RemoteException {
                LinkProperties _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = LinkProperties.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkState[] getAllNetworkState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    NetworkState[] _result = (NetworkState[]) _reply.createTypedArray(NetworkState.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException {
                NetworkQuotaInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = NetworkQuotaInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean isActiveNetworkMetered() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean setRadios(boolean onOff) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(onOff ? 1 : 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean setRadio(int networkType, boolean turnOn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(turnOn ? 1 : 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public int startUsingNetworkFeature(int networkType, String feature, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeString(feature);
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(17, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public int stopUsingNetworkFeature(int networkType, String feature) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeString(feature);
                    this.mRemote.transact(18, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public boolean requestRouteToHost(int networkType, int hostAddress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(hostAddress);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean requestRouteToHostAddress(int networkType, byte[] hostAddress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeByteArray(hostAddress);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean getMobileDataEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setMobileDataEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
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

            @Override // android.net.IConnectivityManager
            public void setPolicyDataEnable(int networkType, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public int tether(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(24, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public int untether(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(25, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public int getLastTetherError(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(26, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public boolean isTetheringSupported() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public String[] getTetherableIfaces() throws RemoteException {
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

            @Override // android.net.IConnectivityManager
            public String[] getTetheredIfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public String[] getTetheringErroredIfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public String[] getTetherableUsbRegexs() throws RemoteException {
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

            @Override // android.net.IConnectivityManager
            public String[] getTetherableWifiRegexs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public String[] getTetherableBluetoothRegexs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public int setUsbTethering(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(34, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public void requestNetworkTransitionWakelock(String forWhom) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(forWhom);
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

            @Override // android.net.IConnectivityManager
            public void reportInetCondition(int networkType, int percentage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(percentage);
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

            @Override // android.net.IConnectivityManager
            public ProxyProperties getGlobalProxy() throws RemoteException {
                ProxyProperties _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ProxyProperties.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setGlobalProxy(ProxyProperties p) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (p != null) {
                        _data.writeInt(1);
                        p.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public ProxyProperties getProxy() throws RemoteException {
                ProxyProperties _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ProxyProperties.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void setDataDependency(int networkType, boolean met) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(met ? 1 : 0);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean protectVpn(ParcelFileDescriptor socket) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (socket != null) {
                        _data.writeInt(1);
                        socket.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean prepareVpn(String oldPackage, String newPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(oldPackage);
                    _data.writeString(newPackage);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public ParcelFileDescriptor establishVpn(VpnConfig config) throws RemoteException {
                ParcelFileDescriptor _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (config != null) {
                        _data.writeInt(1);
                        config.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public VpnConfig getVpnConfig() throws RemoteException {
                VpnConfig _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = VpnConfig.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void startLegacyVpn(VpnProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profile != null) {
                        _data.writeInt(1);
                        profile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.IConnectivityManager
            public LegacyVpnInfo getLegacyVpnInfo() throws RemoteException {
                LegacyVpnInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = LegacyVpnInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public boolean updateLockdownVpn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public void captivePortalCheckComplete(NetworkInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.IConnectivityManager
            public void captivePortalCheckCompleted(NetworkInfo info, boolean isCaptivePortal) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(isCaptivePortal ? 1 : 0);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public void supplyMessenger(int networkType, Messenger messenger) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    if (messenger != null) {
                        _data.writeInt(1);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.IConnectivityManager
            public int findConnectionTypeForIface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(51, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public int checkMobileProvisioning(int suggestedTimeOutMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(suggestedTimeOutMs);
                    this.mRemote.transact(52, _data, _reply, 0);
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

            @Override // android.net.IConnectivityManager
            public String getMobileProvisioningUrl() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public String getMobileRedirectedProvisioningUrl() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo getLinkQualityInfo(int networkType) throws RemoteException {
                LinkQualityInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = LinkQualityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo getActiveLinkQualityInfo() throws RemoteException {
                LinkQualityInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = LinkQualityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IConnectivityManager
            public LinkQualityInfo[] getAllLinkQualityInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    LinkQualityInfo[] _result = (LinkQualityInfo[]) _reply.createTypedArray(LinkQualityInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.IConnectivityManager
            public void setProvisioningNotificationVisible(boolean visible, int networkType, String extraInfo, String url) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visible ? 1 : 0);
                    _data.writeInt(networkType);
                    _data.writeString(extraInfo);
                    _data.writeString(url);
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

            @Override // android.net.IConnectivityManager
            public void setAirplaneMode(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
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
        }
    }
}