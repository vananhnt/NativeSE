package com.android.internal.telephony;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* loaded from: ISms.class */
public interface ISms extends IInterface {
    List<SmsRawData> getAllMessagesFromIccEf(String str) throws RemoteException;

    boolean updateMessageOnIccEf(String str, int i, int i2, byte[] bArr) throws RemoteException;

    boolean copyMessageToIccEf(String str, int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    void sendData(String str, String str2, String str3, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) throws RemoteException;

    void sendText(String str, String str2, String str3, String str4, PendingIntent pendingIntent, PendingIntent pendingIntent2) throws RemoteException;

    void sendMultipartText(String str, String str2, String str3, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3) throws RemoteException;

    boolean enableCellBroadcast(int i) throws RemoteException;

    boolean disableCellBroadcast(int i) throws RemoteException;

    boolean enableCellBroadcastRange(int i, int i2) throws RemoteException;

    boolean disableCellBroadcastRange(int i, int i2) throws RemoteException;

    int getPremiumSmsPermission(String str) throws RemoteException;

    void setPremiumSmsPermission(String str, int i) throws RemoteException;

    boolean isImsSmsSupported() throws RemoteException;

    String getImsSmsFormat() throws RemoteException;

    /* loaded from: ISms$Stub.class */
    public static abstract class Stub extends Binder implements ISms {
        private static final String DESCRIPTOR = "com.android.internal.telephony.ISms";
        static final int TRANSACTION_getAllMessagesFromIccEf = 1;
        static final int TRANSACTION_updateMessageOnIccEf = 2;
        static final int TRANSACTION_copyMessageToIccEf = 3;
        static final int TRANSACTION_sendData = 4;
        static final int TRANSACTION_sendText = 5;
        static final int TRANSACTION_sendMultipartText = 6;
        static final int TRANSACTION_enableCellBroadcast = 7;
        static final int TRANSACTION_disableCellBroadcast = 8;
        static final int TRANSACTION_enableCellBroadcastRange = 9;
        static final int TRANSACTION_disableCellBroadcastRange = 10;
        static final int TRANSACTION_getPremiumSmsPermission = 11;
        static final int TRANSACTION_setPremiumSmsPermission = 12;
        static final int TRANSACTION_isImsSmsSupported = 13;
        static final int TRANSACTION_getImsSmsFormat = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISms asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISms)) {
                return (ISms) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            PendingIntent _arg4;
            PendingIntent _arg5;
            PendingIntent _arg52;
            PendingIntent _arg6;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    List<SmsRawData> _result = getAllMessagesFromIccEf(_arg0);
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    int _arg1 = data.readInt();
                    int _arg2 = data.readInt();
                    byte[] _arg3 = data.createByteArray();
                    boolean _result2 = updateMessageOnIccEf(_arg02, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    int _arg12 = data.readInt();
                    byte[] _arg22 = data.createByteArray();
                    byte[] _arg32 = data.createByteArray();
                    boolean _result3 = copyMessageToIccEf(_arg03, _arg12, _arg22, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    String _arg13 = data.readString();
                    String _arg23 = data.readString();
                    int _arg33 = data.readInt();
                    byte[] _arg42 = data.createByteArray();
                    if (0 != data.readInt()) {
                        _arg52 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg52 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg6 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    sendData(_arg04, _arg13, _arg23, _arg33, _arg42, _arg52, _arg6);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    String _arg14 = data.readString();
                    String _arg24 = data.readString();
                    String _arg34 = data.readString();
                    if (0 != data.readInt()) {
                        _arg4 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg5 = PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    sendText(_arg05, _arg14, _arg24, _arg34, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    String _arg15 = data.readString();
                    String _arg25 = data.readString();
                    List<String> _arg35 = data.createStringArrayList();
                    List<PendingIntent> _arg43 = data.createTypedArrayList(PendingIntent.CREATOR);
                    List<PendingIntent> _arg53 = data.createTypedArrayList(PendingIntent.CREATOR);
                    sendMultipartText(_arg06, _arg15, _arg25, _arg35, _arg43, _arg53);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    boolean _result4 = enableCellBroadcast(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    boolean _result5 = disableCellBroadcast(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _arg16 = data.readInt();
                    boolean _result6 = enableCellBroadcastRange(_arg09, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg17 = data.readInt();
                    boolean _result7 = disableCellBroadcastRange(_arg010, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    int _result8 = getPremiumSmsPermission(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    int _arg18 = data.readInt();
                    setPremiumSmsPermission(_arg012, _arg18);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result9 = isImsSmsSupported();
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _result10 = getImsSmsFormat();
                    reply.writeNoException();
                    reply.writeString(_result10);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ISms$Stub$Proxy.class */
        private static class Proxy implements ISms {
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

            @Override // com.android.internal.telephony.ISms
            public List<SmsRawData> getAllMessagesFromIccEf(String callingPkg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<SmsRawData> _result = _reply.createTypedArrayList(SmsRawData.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean updateMessageOnIccEf(String callingPkg, int messageIndex, int newStatus, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeInt(messageIndex);
                    _data.writeInt(newStatus);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean copyMessageToIccEf(String callingPkg, int status, byte[] pdu, byte[] smsc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeInt(status);
                    _data.writeByteArray(pdu);
                    _data.writeByteArray(smsc);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendData(String callingPkg, String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(destAddr);
                    _data.writeString(scAddr);
                    _data.writeInt(destPort);
                    _data.writeByteArray(data);
                    if (sentIntent != null) {
                        _data.writeInt(1);
                        sentIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (deliveryIntent != null) {
                        _data.writeInt(1);
                        deliveryIntent.writeToParcel(_data, 0);
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

            @Override // com.android.internal.telephony.ISms
            public void sendText(String callingPkg, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(destAddr);
                    _data.writeString(scAddr);
                    _data.writeString(text);
                    if (sentIntent != null) {
                        _data.writeInt(1);
                        sentIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (deliveryIntent != null) {
                        _data.writeInt(1);
                        deliveryIntent.writeToParcel(_data, 0);
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

            @Override // com.android.internal.telephony.ISms
            public void sendMultipartText(String callingPkg, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(destinationAddress);
                    _data.writeString(scAddress);
                    _data.writeStringList(parts);
                    _data.writeTypedList(sentIntents);
                    _data.writeTypedList(deliveryIntents);
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

            @Override // com.android.internal.telephony.ISms
            public boolean enableCellBroadcast(int messageIdentifier) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(messageIdentifier);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean disableCellBroadcast(int messageIdentifier) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(messageIdentifier);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean enableCellBroadcastRange(int startMessageId, int endMessageId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startMessageId);
                    _data.writeInt(endMessageId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean disableCellBroadcastRange(int startMessageId, int endMessageId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startMessageId);
                    _data.writeInt(endMessageId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int getPremiumSmsPermission(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ISms
            public void setPremiumSmsPermission(String packageName, int permission) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(permission);
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

            @Override // com.android.internal.telephony.ISms
            public boolean isImsSmsSupported() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public String getImsSmsFormat() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
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
        }
    }
}