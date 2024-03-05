package android.net.wifi;

import android.net.DhcpInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.WorkSource;
import java.util.List;

/* loaded from: IWifiManager.class */
public interface IWifiManager extends IInterface {
    List<WifiConfiguration> getConfiguredNetworks() throws RemoteException;

    int addOrUpdateNetwork(WifiConfiguration wifiConfiguration) throws RemoteException;

    boolean removeNetwork(int i) throws RemoteException;

    boolean enableNetwork(int i, boolean z) throws RemoteException;

    boolean disableNetwork(int i) throws RemoteException;

    boolean pingSupplicant() throws RemoteException;

    void startScan(WorkSource workSource) throws RemoteException;

    List<ScanResult> getScanResults(String str) throws RemoteException;

    void disconnect() throws RemoteException;

    void reconnect() throws RemoteException;

    void reassociate() throws RemoteException;

    WifiInfo getConnectionInfo() throws RemoteException;

    boolean setWifiEnabled(boolean z) throws RemoteException;

    int getWifiEnabledState() throws RemoteException;

    void setCountryCode(String str, boolean z) throws RemoteException;

    void setFrequencyBand(int i, boolean z) throws RemoteException;

    int getFrequencyBand() throws RemoteException;

    boolean isDualBandSupported() throws RemoteException;

    boolean saveConfiguration() throws RemoteException;

    DhcpInfo getDhcpInfo() throws RemoteException;

    boolean isScanAlwaysAvailable() throws RemoteException;

    boolean acquireWifiLock(IBinder iBinder, int i, String str, WorkSource workSource) throws RemoteException;

    void updateWifiLockWorkSource(IBinder iBinder, WorkSource workSource) throws RemoteException;

    boolean releaseWifiLock(IBinder iBinder) throws RemoteException;

    void initializeMulticastFiltering() throws RemoteException;

    boolean isMulticastEnabled() throws RemoteException;

    void acquireMulticastLock(IBinder iBinder, String str) throws RemoteException;

    void releaseMulticastLock() throws RemoteException;

    void setWifiApEnabled(WifiConfiguration wifiConfiguration, boolean z) throws RemoteException;

    int getWifiApEnabledState() throws RemoteException;

    WifiConfiguration getWifiApConfiguration() throws RemoteException;

    void setWifiApConfiguration(WifiConfiguration wifiConfiguration) throws RemoteException;

    void startWifi() throws RemoteException;

    void stopWifi() throws RemoteException;

    void addToBlacklist(String str) throws RemoteException;

    void clearBlacklist() throws RemoteException;

    Messenger getWifiServiceMessenger() throws RemoteException;

    Messenger getWifiStateMachineMessenger() throws RemoteException;

    String getConfigFile() throws RemoteException;

    void captivePortalCheckComplete() throws RemoteException;

    void enableTdls(String str, boolean z) throws RemoteException;

    void enableTdlsWithMacAddress(String str, boolean z) throws RemoteException;

    boolean requestBatchedScan(BatchedScanSettings batchedScanSettings, IBinder iBinder) throws RemoteException;

    void stopBatchedScan(BatchedScanSettings batchedScanSettings) throws RemoteException;

    List<BatchedScanResult> getBatchedScanResults(String str) throws RemoteException;

    boolean isBatchedScanSupported() throws RemoteException;

    void pollBatchedScan() throws RemoteException;

    /* loaded from: IWifiManager$Stub.class */
    public static abstract class Stub extends Binder implements IWifiManager {
        private static final String DESCRIPTOR = "android.net.wifi.IWifiManager";
        static final int TRANSACTION_getConfiguredNetworks = 1;
        static final int TRANSACTION_addOrUpdateNetwork = 2;
        static final int TRANSACTION_removeNetwork = 3;
        static final int TRANSACTION_enableNetwork = 4;
        static final int TRANSACTION_disableNetwork = 5;
        static final int TRANSACTION_pingSupplicant = 6;
        static final int TRANSACTION_startScan = 7;
        static final int TRANSACTION_getScanResults = 8;
        static final int TRANSACTION_disconnect = 9;
        static final int TRANSACTION_reconnect = 10;
        static final int TRANSACTION_reassociate = 11;
        static final int TRANSACTION_getConnectionInfo = 12;
        static final int TRANSACTION_setWifiEnabled = 13;
        static final int TRANSACTION_getWifiEnabledState = 14;
        static final int TRANSACTION_setCountryCode = 15;
        static final int TRANSACTION_setFrequencyBand = 16;
        static final int TRANSACTION_getFrequencyBand = 17;
        static final int TRANSACTION_isDualBandSupported = 18;
        static final int TRANSACTION_saveConfiguration = 19;
        static final int TRANSACTION_getDhcpInfo = 20;
        static final int TRANSACTION_isScanAlwaysAvailable = 21;
        static final int TRANSACTION_acquireWifiLock = 22;
        static final int TRANSACTION_updateWifiLockWorkSource = 23;
        static final int TRANSACTION_releaseWifiLock = 24;
        static final int TRANSACTION_initializeMulticastFiltering = 25;
        static final int TRANSACTION_isMulticastEnabled = 26;
        static final int TRANSACTION_acquireMulticastLock = 27;
        static final int TRANSACTION_releaseMulticastLock = 28;
        static final int TRANSACTION_setWifiApEnabled = 29;
        static final int TRANSACTION_getWifiApEnabledState = 30;
        static final int TRANSACTION_getWifiApConfiguration = 31;
        static final int TRANSACTION_setWifiApConfiguration = 32;
        static final int TRANSACTION_startWifi = 33;
        static final int TRANSACTION_stopWifi = 34;
        static final int TRANSACTION_addToBlacklist = 35;
        static final int TRANSACTION_clearBlacklist = 36;
        static final int TRANSACTION_getWifiServiceMessenger = 37;
        static final int TRANSACTION_getWifiStateMachineMessenger = 38;
        static final int TRANSACTION_getConfigFile = 39;
        static final int TRANSACTION_captivePortalCheckComplete = 40;
        static final int TRANSACTION_enableTdls = 41;
        static final int TRANSACTION_enableTdlsWithMacAddress = 42;
        static final int TRANSACTION_requestBatchedScan = 43;
        static final int TRANSACTION_stopBatchedScan = 44;
        static final int TRANSACTION_getBatchedScanResults = 45;
        static final int TRANSACTION_isBatchedScanSupported = 46;
        static final int TRANSACTION_pollBatchedScan = 47;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWifiManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWifiManager)) {
                return (IWifiManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            BatchedScanSettings _arg0;
            BatchedScanSettings _arg02;
            WifiConfiguration _arg03;
            WifiConfiguration _arg04;
            WorkSource _arg1;
            WorkSource _arg3;
            WorkSource _arg05;
            WifiConfiguration _arg06;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    List<WifiConfiguration> _result = getConfiguredNetworks();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = WifiConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _result2 = addOrUpdateNetwork(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    boolean _result3 = removeNetwork(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    boolean _arg12 = 0 != data.readInt();
                    boolean _result4 = enableNetwork(_arg08, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    boolean _result5 = disableNetwork(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result6 = pingSupplicant();
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    startScan(_arg05);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    List<ScanResult> _result7 = getScanResults(_arg010);
                    reply.writeNoException();
                    reply.writeTypedList(_result7);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    disconnect();
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    reconnect();
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    reassociate();
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    WifiInfo _result8 = getConnectionInfo();
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(1);
                        _result8.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg011 = 0 != data.readInt();
                    boolean _result9 = setWifiEnabled(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _result10 = getWifiEnabledState();
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    boolean _arg13 = 0 != data.readInt();
                    setCountryCode(_arg012, _arg13);
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    boolean _arg14 = 0 != data.readInt();
                    setFrequencyBand(_arg013, _arg14);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = getFrequencyBand();
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result12 = isDualBandSupported();
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result13 = saveConfiguration();
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    DhcpInfo _result14 = getDhcpInfo();
                    reply.writeNoException();
                    if (_result14 != null) {
                        reply.writeInt(1);
                        _result14.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result15 = isScanAlwaysAvailable();
                    reply.writeNoException();
                    reply.writeInt(_result15 ? 1 : 0);
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg014 = data.readStrongBinder();
                    int _arg15 = data.readInt();
                    String _arg2 = data.readString();
                    if (0 != data.readInt()) {
                        _arg3 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    boolean _result16 = acquireWifiLock(_arg014, _arg15, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result16 ? 1 : 0);
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg015 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg1 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    updateWifiLockWorkSource(_arg015, _arg1);
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg016 = data.readStrongBinder();
                    boolean _result17 = releaseWifiLock(_arg016);
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    initializeMulticastFiltering();
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result18 = isMulticastEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result18 ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg017 = data.readStrongBinder();
                    String _arg16 = data.readString();
                    acquireMulticastLock(_arg017, _arg16);
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    releaseMulticastLock();
                    reply.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = WifiConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    boolean _arg17 = 0 != data.readInt();
                    setWifiApEnabled(_arg04, _arg17);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    int _result19 = getWifiApEnabledState();
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    WifiConfiguration _result20 = getWifiApConfiguration();
                    reply.writeNoException();
                    if (_result20 != null) {
                        reply.writeInt(1);
                        _result20.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = WifiConfiguration.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    setWifiApConfiguration(_arg03);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    startWifi();
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    stopWifi();
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    addToBlacklist(_arg018);
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    clearBlacklist();
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    Messenger _result21 = getWifiServiceMessenger();
                    reply.writeNoException();
                    if (_result21 != null) {
                        reply.writeInt(1);
                        _result21.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    Messenger _result22 = getWifiStateMachineMessenger();
                    reply.writeNoException();
                    if (_result22 != null) {
                        reply.writeInt(1);
                        _result22.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    String _result23 = getConfigFile();
                    reply.writeNoException();
                    reply.writeString(_result23);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    captivePortalCheckComplete();
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    boolean _arg18 = 0 != data.readInt();
                    enableTdls(_arg019, _arg18);
                    reply.writeNoException();
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg020 = data.readString();
                    boolean _arg19 = 0 != data.readInt();
                    enableTdlsWithMacAddress(_arg020, _arg19);
                    reply.writeNoException();
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = BatchedScanSettings.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    IBinder _arg110 = data.readStrongBinder();
                    boolean _result24 = requestBatchedScan(_arg02, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result24 ? 1 : 0);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = BatchedScanSettings.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    stopBatchedScan(_arg0);
                    reply.writeNoException();
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg021 = data.readString();
                    List<BatchedScanResult> _result25 = getBatchedScanResults(_arg021);
                    reply.writeNoException();
                    reply.writeTypedList(_result25);
                    return true;
                case 46:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result26 = isBatchedScanSupported();
                    reply.writeNoException();
                    reply.writeInt(_result26 ? 1 : 0);
                    return true;
                case 47:
                    data.enforceInterface(DESCRIPTOR);
                    pollBatchedScan();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IWifiManager$Stub$Proxy.class */
        public static class Proxy implements IWifiManager {
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

            @Override // android.net.wifi.IWifiManager
            public List<WifiConfiguration> getConfiguredNetworks() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<WifiConfiguration> _result = _reply.createTypedArrayList(WifiConfiguration.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int addOrUpdateNetwork(WifiConfiguration config) throws RemoteException {
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
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // android.net.wifi.IWifiManager
            public boolean removeNetwork(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean enableNetwork(int netId, boolean disableOthers) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    _data.writeInt(disableOthers ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean disableNetwork(int netId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(netId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean pingSupplicant() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void startScan(WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ws != null) {
                        _data.writeInt(1);
                        ws.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.wifi.IWifiManager
            public List<ScanResult> getScanResults(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<ScanResult> _result = _reply.createTypedArrayList(ScanResult.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public void reconnect() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public void reassociate() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public WifiInfo getConnectionInfo() throws RemoteException {
                WifiInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = WifiInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean setWifiEnabled(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public int getWifiEnabledState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
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

            @Override // android.net.wifi.IWifiManager
            public void setCountryCode(String country, boolean persist) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(country);
                    _data.writeInt(persist ? 1 : 0);
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

            @Override // android.net.wifi.IWifiManager
            public void setFrequencyBand(int band, boolean persist) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(band);
                    _data.writeInt(persist ? 1 : 0);
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

            @Override // android.net.wifi.IWifiManager
            public int getFrequencyBand() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public boolean isDualBandSupported() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean saveConfiguration() throws RemoteException {
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

            @Override // android.net.wifi.IWifiManager
            public DhcpInfo getDhcpInfo() throws RemoteException {
                DhcpInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = DhcpInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isScanAlwaysAvailable() throws RemoteException {
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

            @Override // android.net.wifi.IWifiManager
            public boolean acquireWifiLock(IBinder lock, int lockType, String tag, WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    _data.writeInt(lockType);
                    _data.writeString(tag);
                    if (ws != null) {
                        _data.writeInt(1);
                        ws.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void updateWifiLockWorkSource(IBinder lock, WorkSource ws) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    if (ws != null) {
                        _data.writeInt(1);
                        ws.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.wifi.IWifiManager
            public boolean releaseWifiLock(IBinder lock) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(lock);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void initializeMulticastFiltering() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public boolean isMulticastEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void acquireMulticastLock(IBinder binder, String tag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeString(tag);
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

            @Override // android.net.wifi.IWifiManager
            public void releaseMulticastLock() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setWifiApEnabled(WifiConfiguration wifiConfig, boolean enable) throws RemoteException {
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
                    _data.writeInt(enable ? 1 : 0);
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

            @Override // android.net.wifi.IWifiManager
            public int getWifiApEnabledState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
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

            @Override // android.net.wifi.IWifiManager
            public WifiConfiguration getWifiApConfiguration() throws RemoteException {
                WifiConfiguration _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = WifiConfiguration.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void setWifiApConfiguration(WifiConfiguration wifiConfig) throws RemoteException {
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

            @Override // android.net.wifi.IWifiManager
            public void startWifi() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public void stopWifi() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public void addToBlacklist(String bssid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(bssid);
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

            @Override // android.net.wifi.IWifiManager
            public void clearBlacklist() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public Messenger getWifiServiceMessenger() throws RemoteException {
                Messenger _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Messenger.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public Messenger getWifiStateMachineMessenger() throws RemoteException {
                Messenger _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Messenger.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public String getConfigFile() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
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

            @Override // android.net.wifi.IWifiManager
            public void captivePortalCheckComplete() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.net.wifi.IWifiManager
            public void enableTdls(String remoteIPAddress, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(remoteIPAddress);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void enableTdlsWithMacAddress(String remoteMacAddress, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(remoteMacAddress);
                    _data.writeInt(enable ? 1 : 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean requestBatchedScan(BatchedScanSettings requested, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (requested != null) {
                        _data.writeInt(1);
                        requested.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void stopBatchedScan(BatchedScanSettings requested) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (requested != null) {
                        _data.writeInt(1);
                        requested.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.net.wifi.IWifiManager
            public List<BatchedScanResult> getBatchedScanResults(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    List<BatchedScanResult> _result = _reply.createTypedArrayList(BatchedScanResult.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.net.wifi.IWifiManager
            public boolean isBatchedScanSupported() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.IWifiManager
            public void pollBatchedScan() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
        }
    }
}