package com.android.internal.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.WorkSource;
import android.telephony.SignalStrength;

/* loaded from: IBatteryStats.class */
public interface IBatteryStats extends IInterface {
    byte[] getStatistics() throws RemoteException;

    void noteStartWakelock(int i, int i2, String str, int i3) throws RemoteException;

    void noteStopWakelock(int i, int i2, String str, int i3) throws RemoteException;

    void noteStartSensor(int i, int i2) throws RemoteException;

    void noteStopSensor(int i, int i2) throws RemoteException;

    void noteStartWakelockFromSource(WorkSource workSource, int i, String str, int i2) throws RemoteException;

    void noteStopWakelockFromSource(WorkSource workSource, int i, String str, int i2) throws RemoteException;

    void noteVibratorOn(int i, long j) throws RemoteException;

    void noteVibratorOff(int i) throws RemoteException;

    void noteStartGps(int i) throws RemoteException;

    void noteStopGps(int i) throws RemoteException;

    void noteScreenOn() throws RemoteException;

    void noteScreenBrightness(int i) throws RemoteException;

    void noteScreenOff() throws RemoteException;

    void noteInputEvent() throws RemoteException;

    void noteUserActivity(int i, int i2) throws RemoteException;

    void notePhoneOn() throws RemoteException;

    void notePhoneOff() throws RemoteException;

    void notePhoneSignalStrength(SignalStrength signalStrength) throws RemoteException;

    void notePhoneDataConnectionState(int i, boolean z) throws RemoteException;

    void notePhoneState(int i) throws RemoteException;

    void noteWifiOn() throws RemoteException;

    void noteWifiOff() throws RemoteException;

    void noteWifiRunning(WorkSource workSource) throws RemoteException;

    void noteWifiRunningChanged(WorkSource workSource, WorkSource workSource2) throws RemoteException;

    void noteWifiStopped(WorkSource workSource) throws RemoteException;

    void noteBluetoothOn() throws RemoteException;

    void noteBluetoothOff() throws RemoteException;

    void noteFullWifiLockAcquired(int i) throws RemoteException;

    void noteFullWifiLockReleased(int i) throws RemoteException;

    void noteWifiScanStarted(int i) throws RemoteException;

    void noteWifiScanStopped(int i) throws RemoteException;

    void noteWifiMulticastEnabled(int i) throws RemoteException;

    void noteWifiMulticastDisabled(int i) throws RemoteException;

    void noteFullWifiLockAcquiredFromSource(WorkSource workSource) throws RemoteException;

    void noteFullWifiLockReleasedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiScanStartedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiScanStoppedFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiMulticastEnabledFromSource(WorkSource workSource) throws RemoteException;

    void noteWifiMulticastDisabledFromSource(WorkSource workSource) throws RemoteException;

    void noteNetworkInterfaceType(String str, int i) throws RemoteException;

    void noteNetworkStatsEnabled() throws RemoteException;

    void setBatteryState(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    long getAwakeTimeBattery() throws RemoteException;

    long getAwakeTimePlugged() throws RemoteException;

    /* loaded from: IBatteryStats$Stub.class */
    public static abstract class Stub extends Binder implements IBatteryStats {
        private static final String DESCRIPTOR = "com.android.internal.app.IBatteryStats";
        static final int TRANSACTION_getStatistics = 1;
        static final int TRANSACTION_noteStartWakelock = 2;
        static final int TRANSACTION_noteStopWakelock = 3;
        static final int TRANSACTION_noteStartSensor = 4;
        static final int TRANSACTION_noteStopSensor = 5;
        static final int TRANSACTION_noteStartWakelockFromSource = 6;
        static final int TRANSACTION_noteStopWakelockFromSource = 7;
        static final int TRANSACTION_noteVibratorOn = 8;
        static final int TRANSACTION_noteVibratorOff = 9;
        static final int TRANSACTION_noteStartGps = 10;
        static final int TRANSACTION_noteStopGps = 11;
        static final int TRANSACTION_noteScreenOn = 12;
        static final int TRANSACTION_noteScreenBrightness = 13;
        static final int TRANSACTION_noteScreenOff = 14;
        static final int TRANSACTION_noteInputEvent = 15;
        static final int TRANSACTION_noteUserActivity = 16;
        static final int TRANSACTION_notePhoneOn = 17;
        static final int TRANSACTION_notePhoneOff = 18;
        static final int TRANSACTION_notePhoneSignalStrength = 19;
        static final int TRANSACTION_notePhoneDataConnectionState = 20;
        static final int TRANSACTION_notePhoneState = 21;
        static final int TRANSACTION_noteWifiOn = 22;
        static final int TRANSACTION_noteWifiOff = 23;
        static final int TRANSACTION_noteWifiRunning = 24;
        static final int TRANSACTION_noteWifiRunningChanged = 25;
        static final int TRANSACTION_noteWifiStopped = 26;
        static final int TRANSACTION_noteBluetoothOn = 27;
        static final int TRANSACTION_noteBluetoothOff = 28;
        static final int TRANSACTION_noteFullWifiLockAcquired = 29;
        static final int TRANSACTION_noteFullWifiLockReleased = 30;
        static final int TRANSACTION_noteWifiScanStarted = 31;
        static final int TRANSACTION_noteWifiScanStopped = 32;
        static final int TRANSACTION_noteWifiMulticastEnabled = 33;
        static final int TRANSACTION_noteWifiMulticastDisabled = 34;
        static final int TRANSACTION_noteFullWifiLockAcquiredFromSource = 35;
        static final int TRANSACTION_noteFullWifiLockReleasedFromSource = 36;
        static final int TRANSACTION_noteWifiScanStartedFromSource = 37;
        static final int TRANSACTION_noteWifiScanStoppedFromSource = 38;
        static final int TRANSACTION_noteWifiMulticastEnabledFromSource = 39;
        static final int TRANSACTION_noteWifiMulticastDisabledFromSource = 40;
        static final int TRANSACTION_noteNetworkInterfaceType = 41;
        static final int TRANSACTION_noteNetworkStatsEnabled = 42;
        static final int TRANSACTION_setBatteryState = 43;
        static final int TRANSACTION_getAwakeTimeBattery = 44;
        static final int TRANSACTION_getAwakeTimePlugged = 45;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBatteryStats asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBatteryStats)) {
                return (IBatteryStats) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            WorkSource _arg0;
            WorkSource _arg02;
            WorkSource _arg03;
            WorkSource _arg04;
            WorkSource _arg05;
            WorkSource _arg06;
            WorkSource _arg07;
            WorkSource _arg08;
            WorkSource _arg1;
            WorkSource _arg09;
            SignalStrength _arg010;
            WorkSource _arg011;
            WorkSource _arg012;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _result = getStatistics();
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _arg12 = data.readInt();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    noteStartWakelock(_arg013, _arg12, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    int _arg13 = data.readInt();
                    String _arg22 = data.readString();
                    int _arg32 = data.readInt();
                    noteStopWakelock(_arg014, _arg13, _arg22, _arg32);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    int _arg14 = data.readInt();
                    noteStartSensor(_arg015, _arg14);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    int _arg15 = data.readInt();
                    noteStopSensor(_arg016, _arg15);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    int _arg16 = data.readInt();
                    String _arg23 = data.readString();
                    int _arg33 = data.readInt();
                    noteStartWakelockFromSource(_arg012, _arg16, _arg23, _arg33);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    int _arg17 = data.readInt();
                    String _arg24 = data.readString();
                    int _arg34 = data.readInt();
                    noteStopWakelockFromSource(_arg011, _arg17, _arg24, _arg34);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    long _arg18 = data.readLong();
                    noteVibratorOn(_arg017, _arg18);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    noteVibratorOff(_arg018);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    noteStartGps(_arg019);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    noteStopGps(_arg020);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    noteScreenOn();
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    noteScreenBrightness(_arg021);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    noteScreenOff();
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    noteInputEvent();
                    reply.writeNoException();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    int _arg19 = data.readInt();
                    noteUserActivity(_arg022, _arg19);
                    reply.writeNoException();
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    notePhoneOn();
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    notePhoneOff();
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = SignalStrength.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    notePhoneSignalStrength(_arg010);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    boolean _arg110 = 0 != data.readInt();
                    notePhoneDataConnectionState(_arg023, _arg110);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    notePhoneState(_arg024);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    noteWifiOn();
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    noteWifiOff();
                    reply.writeNoException();
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    noteWifiRunning(_arg09);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    noteWifiRunningChanged(_arg08, _arg1);
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    noteWifiStopped(_arg07);
                    reply.writeNoException();
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    noteBluetoothOn();
                    reply.writeNoException();
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    noteBluetoothOff();
                    reply.writeNoException();
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    noteFullWifiLockAcquired(_arg025);
                    reply.writeNoException();
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    noteFullWifiLockReleased(_arg026);
                    reply.writeNoException();
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    noteWifiScanStarted(_arg027);
                    reply.writeNoException();
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    noteWifiScanStopped(_arg028);
                    reply.writeNoException();
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    noteWifiMulticastEnabled(_arg029);
                    reply.writeNoException();
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    noteWifiMulticastDisabled(_arg030);
                    reply.writeNoException();
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    noteFullWifiLockAcquiredFromSource(_arg06);
                    reply.writeNoException();
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    noteFullWifiLockReleasedFromSource(_arg05);
                    reply.writeNoException();
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    noteWifiScanStartedFromSource(_arg04);
                    reply.writeNoException();
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    noteWifiScanStoppedFromSource(_arg03);
                    reply.writeNoException();
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    noteWifiMulticastEnabledFromSource(_arg02);
                    reply.writeNoException();
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = WorkSource.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    noteWifiMulticastDisabledFromSource(_arg0);
                    reply.writeNoException();
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg031 = data.readString();
                    int _arg111 = data.readInt();
                    noteNetworkInterfaceType(_arg031, _arg111);
                    reply.writeNoException();
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    noteNetworkStatsEnabled();
                    reply.writeNoException();
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    int _arg112 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg35 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    setBatteryState(_arg032, _arg112, _arg25, _arg35, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    long _result2 = getAwakeTimeBattery();
                    reply.writeNoException();
                    reply.writeLong(_result2);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    long _result3 = getAwakeTimePlugged();
                    reply.writeNoException();
                    reply.writeLong(_result3);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBatteryStats$Stub$Proxy.class */
        private static class Proxy implements IBatteryStats {
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

            @Override // com.android.internal.app.IBatteryStats
            public byte[] getStatistics() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartWakelock(int uid, int pid, String name, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeInt(type);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopWakelock(int uid, int pid, String name, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeInt(type);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartSensor(int uid, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(sensor);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopSensor(int uid, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(sensor);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartWakelockFromSource(WorkSource ws, int pid, String name, int type) throws RemoteException {
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
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeInt(type);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopWakelockFromSource(WorkSource ws, int pid, String name, int type) throws RemoteException {
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
                    _data.writeInt(pid);
                    _data.writeString(name);
                    _data.writeInt(type);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteVibratorOn(int uid, long durationMillis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeLong(durationMillis);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteVibratorOff(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteStartGps(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteStopGps(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteScreenOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteScreenBrightness(int brightness) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(brightness);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteScreenOff() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteInputEvent() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteUserActivity(int uid, int event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeInt(event);
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

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneOff() throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneSignalStrength(SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (signalStrength != null) {
                        _data.writeInt(1);
                        signalStrength.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneDataConnectionState(int dataType, boolean hasData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(dataType);
                    _data.writeInt(hasData ? 1 : 0);
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

            @Override // com.android.internal.app.IBatteryStats
            public void notePhoneState(int phoneState) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneState);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiOn() throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiOff() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRunning(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiRunningChanged(WorkSource oldWs, WorkSource newWs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (oldWs != null) {
                        _data.writeInt(1);
                        oldWs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (newWs != null) {
                        _data.writeInt(1);
                        newWs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiStopped(WorkSource ws) throws RemoteException {
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
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteBluetoothOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteBluetoothOff() throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockAcquired(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockReleased(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStarted(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStopped(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastEnabled(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastDisabled(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockAcquiredFromSource(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteFullWifiLockReleasedFromSource(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStartedFromSource(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiScanStoppedFromSource(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastEnabledFromSource(WorkSource ws) throws RemoteException {
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
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public void noteWifiMulticastDisabledFromSource(WorkSource ws) throws RemoteException {
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteNetworkInterfaceType(String iface, int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    _data.writeInt(type);
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

            @Override // com.android.internal.app.IBatteryStats
            public void noteNetworkStatsEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.app.IBatteryStats
            public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(health);
                    _data.writeInt(plugType);
                    _data.writeInt(level);
                    _data.writeInt(temp);
                    _data.writeInt(volt);
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

            @Override // com.android.internal.app.IBatteryStats
            public long getAwakeTimeBattery() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IBatteryStats
            public long getAwakeTimePlugged() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}