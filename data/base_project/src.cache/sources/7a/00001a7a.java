package com.android.internal.telephony;

import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import com.android.internal.telephony.IPhoneStateListener;
import java.util.List;

/* loaded from: ITelephonyRegistry.class */
public interface ITelephonyRegistry extends IInterface {
    void listen(String str, IPhoneStateListener iPhoneStateListener, int i, boolean z) throws RemoteException;

    void notifyCallState(int i, String str) throws RemoteException;

    void notifyServiceState(ServiceState serviceState) throws RemoteException;

    void notifySignalStrength(SignalStrength signalStrength) throws RemoteException;

    void notifyMessageWaitingChanged(boolean z) throws RemoteException;

    void notifyCallForwardingChanged(boolean z) throws RemoteException;

    void notifyDataActivity(int i) throws RemoteException;

    void notifyDataConnection(int i, boolean z, String str, String str2, String str3, LinkProperties linkProperties, LinkCapabilities linkCapabilities, int i2, boolean z2) throws RemoteException;

    void notifyDataConnectionFailed(String str, String str2) throws RemoteException;

    void notifyCellLocation(Bundle bundle) throws RemoteException;

    void notifyOtaspChanged(int i) throws RemoteException;

    void notifyCellInfo(List<CellInfo> list) throws RemoteException;

    /* loaded from: ITelephonyRegistry$Stub.class */
    public static abstract class Stub extends Binder implements ITelephonyRegistry {
        private static final String DESCRIPTOR = "com.android.internal.telephony.ITelephonyRegistry";
        static final int TRANSACTION_listen = 1;
        static final int TRANSACTION_notifyCallState = 2;
        static final int TRANSACTION_notifyServiceState = 3;
        static final int TRANSACTION_notifySignalStrength = 4;
        static final int TRANSACTION_notifyMessageWaitingChanged = 5;
        static final int TRANSACTION_notifyCallForwardingChanged = 6;
        static final int TRANSACTION_notifyDataActivity = 7;
        static final int TRANSACTION_notifyDataConnection = 8;
        static final int TRANSACTION_notifyDataConnectionFailed = 9;
        static final int TRANSACTION_notifyCellLocation = 10;
        static final int TRANSACTION_notifyOtaspChanged = 11;
        static final int TRANSACTION_notifyCellInfo = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITelephonyRegistry asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITelephonyRegistry)) {
                return (ITelephonyRegistry) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg0;
            LinkProperties _arg5;
            LinkCapabilities _arg6;
            SignalStrength _arg02;
            ServiceState _arg03;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    IPhoneStateListener _arg1 = IPhoneStateListener.Stub.asInterface(data.readStrongBinder());
                    int _arg2 = data.readInt();
                    boolean _arg3 = 0 != data.readInt();
                    listen(_arg04, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg12 = data.readString();
                    notifyCallState(_arg05, _arg12);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = ServiceState.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    notifyServiceState(_arg03);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = SignalStrength.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    notifySignalStrength(_arg02);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg06 = 0 != data.readInt();
                    notifyMessageWaitingChanged(_arg06);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg07 = 0 != data.readInt();
                    notifyCallForwardingChanged(_arg07);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    notifyDataActivity(_arg08);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    boolean _arg13 = 0 != data.readInt();
                    String _arg22 = data.readString();
                    String _arg32 = data.readString();
                    String _arg4 = data.readString();
                    if (0 != data.readInt()) {
                        _arg5 = LinkProperties.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg6 = LinkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    int _arg7 = data.readInt();
                    boolean _arg8 = 0 != data.readInt();
                    notifyDataConnection(_arg09, _arg13, _arg22, _arg32, _arg4, _arg5, _arg6, _arg7, _arg8);
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    String _arg14 = data.readString();
                    notifyDataConnectionFailed(_arg010, _arg14);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    notifyCellLocation(_arg0);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    notifyOtaspChanged(_arg011);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    List<CellInfo> _arg012 = data.createTypedArrayList(CellInfo.CREATOR);
                    notifyCellInfo(_arg012);
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
        /* loaded from: ITelephonyRegistry$Stub$Proxy.class */
        public static class Proxy implements ITelephonyRegistry {
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void listen(String pkg, IPhoneStateListener callback, int events, boolean notifyNow) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(events);
                    _data.writeInt(notifyNow ? 1 : 0);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallState(int state, String incomingNumber) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeString(incomingNumber);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyServiceState(ServiceState state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (state != null) {
                        _data.writeInt(1);
                        state.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifySignalStrength(SignalStrength signalStrength) throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyMessageWaitingChanged(boolean mwi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mwi ? 1 : 0);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCallForwardingChanged(boolean cfi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cfi ? 1 : 0);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataActivity(int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataConnection(int state, boolean isDataConnectivityPossible, String reason, String apn, String apnType, LinkProperties linkProperties, LinkCapabilities linkCapabilities, int networkType, boolean roaming) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(isDataConnectivityPossible ? 1 : 0);
                    _data.writeString(reason);
                    _data.writeString(apn);
                    _data.writeString(apnType);
                    if (linkProperties != null) {
                        _data.writeInt(1);
                        linkProperties.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (linkCapabilities != null) {
                        _data.writeInt(1);
                        linkCapabilities.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(networkType);
                    _data.writeInt(roaming ? 1 : 0);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyDataConnectionFailed(String reason, String apnType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    _data.writeString(apnType);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCellLocation(Bundle cellLocation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cellLocation != null) {
                        _data.writeInt(1);
                        cellLocation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyOtaspChanged(int otaspMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(otaspMode);
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

            @Override // com.android.internal.telephony.ITelephonyRegistry
            public void notifyCellInfo(List<CellInfo> cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(cellInfo);
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
        }
    }
}