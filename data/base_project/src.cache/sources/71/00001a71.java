package com.android.internal.telephony;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IPhoneSubInfo.class */
public interface IPhoneSubInfo extends IInterface {
    String getDeviceId() throws RemoteException;

    String getDeviceSvn() throws RemoteException;

    String getSubscriberId() throws RemoteException;

    String getGroupIdLevel1() throws RemoteException;

    String getIccSerialNumber() throws RemoteException;

    String getLine1Number() throws RemoteException;

    String getLine1AlphaTag() throws RemoteException;

    String getMsisdn() throws RemoteException;

    String getVoiceMailNumber() throws RemoteException;

    String getCompleteVoiceMailNumber() throws RemoteException;

    String getVoiceMailAlphaTag() throws RemoteException;

    String getIsimImpi() throws RemoteException;

    String getIsimDomain() throws RemoteException;

    String[] getIsimImpu() throws RemoteException;

    /* loaded from: IPhoneSubInfo$Stub.class */
    public static abstract class Stub extends Binder implements IPhoneSubInfo {
        private static final String DESCRIPTOR = "com.android.internal.telephony.IPhoneSubInfo";
        static final int TRANSACTION_getDeviceId = 1;
        static final int TRANSACTION_getDeviceSvn = 2;
        static final int TRANSACTION_getSubscriberId = 3;
        static final int TRANSACTION_getGroupIdLevel1 = 4;
        static final int TRANSACTION_getIccSerialNumber = 5;
        static final int TRANSACTION_getLine1Number = 6;
        static final int TRANSACTION_getLine1AlphaTag = 7;
        static final int TRANSACTION_getMsisdn = 8;
        static final int TRANSACTION_getVoiceMailNumber = 9;
        static final int TRANSACTION_getCompleteVoiceMailNumber = 10;
        static final int TRANSACTION_getVoiceMailAlphaTag = 11;
        static final int TRANSACTION_getIsimImpi = 12;
        static final int TRANSACTION_getIsimDomain = 13;
        static final int TRANSACTION_getIsimImpu = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPhoneSubInfo asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPhoneSubInfo)) {
                return (IPhoneSubInfo) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _result = getDeviceId();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _result2 = getDeviceSvn();
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _result3 = getSubscriberId();
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _result4 = getGroupIdLevel1();
                    reply.writeNoException();
                    reply.writeString(_result4);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _result5 = getIccSerialNumber();
                    reply.writeNoException();
                    reply.writeString(_result5);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _result6 = getLine1Number();
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _result7 = getLine1AlphaTag();
                    reply.writeNoException();
                    reply.writeString(_result7);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _result8 = getMsisdn();
                    reply.writeNoException();
                    reply.writeString(_result8);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _result9 = getVoiceMailNumber();
                    reply.writeNoException();
                    reply.writeString(_result9);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _result10 = getCompleteVoiceMailNumber();
                    reply.writeNoException();
                    reply.writeString(_result10);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _result11 = getVoiceMailAlphaTag();
                    reply.writeNoException();
                    reply.writeString(_result11);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _result12 = getIsimImpi();
                    reply.writeNoException();
                    reply.writeString(_result12);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _result13 = getIsimDomain();
                    reply.writeNoException();
                    reply.writeString(_result13);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _result14 = getIsimImpu();
                    reply.writeNoException();
                    reply.writeStringArray(_result14);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IPhoneSubInfo$Stub$Proxy.class */
        public static class Proxy implements IPhoneSubInfo {
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getDeviceId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getDeviceSvn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getSubscriberId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getGroupIdLevel1() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getIccSerialNumber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getLine1Number() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getLine1AlphaTag() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getMsisdn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getVoiceMailNumber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getCompleteVoiceMailNumber() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getVoiceMailAlphaTag() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getIsimImpi() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String getIsimDomain() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.IPhoneSubInfo
            public String[] getIsimImpu() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
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
        }
    }
}