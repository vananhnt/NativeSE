package com.android.internal.telephony;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import java.util.List;

/* loaded from: IPhoneStateListener.class */
public interface IPhoneStateListener extends IInterface {
    void onServiceStateChanged(ServiceState serviceState) throws RemoteException;

    void onSignalStrengthChanged(int i) throws RemoteException;

    void onMessageWaitingIndicatorChanged(boolean z) throws RemoteException;

    void onCallForwardingIndicatorChanged(boolean z) throws RemoteException;

    void onCellLocationChanged(Bundle bundle) throws RemoteException;

    void onCallStateChanged(int i, String str) throws RemoteException;

    void onDataConnectionStateChanged(int i, int i2) throws RemoteException;

    void onDataActivity(int i) throws RemoteException;

    void onSignalStrengthsChanged(SignalStrength signalStrength) throws RemoteException;

    void onOtaspChanged(int i) throws RemoteException;

    void onCellInfoChanged(List<CellInfo> list) throws RemoteException;

    /* loaded from: IPhoneStateListener$Stub.class */
    public static abstract class Stub extends Binder implements IPhoneStateListener {
        private static final String DESCRIPTOR = "com.android.internal.telephony.IPhoneStateListener";
        static final int TRANSACTION_onServiceStateChanged = 1;
        static final int TRANSACTION_onSignalStrengthChanged = 2;
        static final int TRANSACTION_onMessageWaitingIndicatorChanged = 3;
        static final int TRANSACTION_onCallForwardingIndicatorChanged = 4;
        static final int TRANSACTION_onCellLocationChanged = 5;
        static final int TRANSACTION_onCallStateChanged = 6;
        static final int TRANSACTION_onDataConnectionStateChanged = 7;
        static final int TRANSACTION_onDataActivity = 8;
        static final int TRANSACTION_onSignalStrengthsChanged = 9;
        static final int TRANSACTION_onOtaspChanged = 10;
        static final int TRANSACTION_onCellInfoChanged = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPhoneStateListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPhoneStateListener)) {
                return (IPhoneStateListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            SignalStrength _arg0;
            Bundle _arg02;
            ServiceState _arg03;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = ServiceState.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    onServiceStateChanged(_arg03);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    onSignalStrengthChanged(_arg04);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg05 = 0 != data.readInt();
                    onMessageWaitingIndicatorChanged(_arg05);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg06 = 0 != data.readInt();
                    onCallForwardingIndicatorChanged(_arg06);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    onCellLocationChanged(_arg02);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    String _arg1 = data.readString();
                    onCallStateChanged(_arg07, _arg1);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    int _arg12 = data.readInt();
                    onDataConnectionStateChanged(_arg08, _arg12);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    onDataActivity(_arg09);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = SignalStrength.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onSignalStrengthsChanged(_arg0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    onOtaspChanged(_arg010);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    List<CellInfo> _arg011 = data.createTypedArrayList(CellInfo.CREATOR);
                    onCellInfoChanged(_arg011);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IPhoneStateListener$Stub$Proxy.class */
        private static class Proxy implements IPhoneStateListener {
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

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onServiceStateChanged(ServiceState serviceState) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (serviceState != null) {
                        _data.writeInt(1);
                        serviceState.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onSignalStrengthChanged(int asu) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(asu);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onMessageWaitingIndicatorChanged(boolean mwi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mwi ? 1 : 0);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallForwardingIndicatorChanged(boolean cfi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cfi ? 1 : 0);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCellLocationChanged(Bundle location) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (location != null) {
                        _data.writeInt(1);
                        location.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCallStateChanged(int state, String incomingNumber) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeString(incomingNumber);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataConnectionStateChanged(int state, int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(state);
                    _data.writeInt(networkType);
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onDataActivity(int direction) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onSignalStrengthsChanged(SignalStrength signalStrength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (signalStrength != null) {
                        _data.writeInt(1);
                        signalStrength.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(9, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onOtaspChanged(int otaspMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(otaspMode);
                    this.mRemote.transact(10, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.IPhoneStateListener
            public void onCellInfoChanged(List<CellInfo> cellInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(cellInfo);
                    this.mRemote.transact(11, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}