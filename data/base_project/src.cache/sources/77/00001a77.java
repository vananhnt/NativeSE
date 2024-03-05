package com.android.internal.telephony;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import java.util.List;

/* loaded from: ITelephony.class */
public interface ITelephony extends IInterface {
    void dial(String str) throws RemoteException;

    void call(String str, String str2) throws RemoteException;

    boolean showCallScreen() throws RemoteException;

    boolean showCallScreenWithDialpad(boolean z) throws RemoteException;

    boolean endCall() throws RemoteException;

    void answerRingingCall() throws RemoteException;

    void silenceRinger() throws RemoteException;

    boolean isOffhook() throws RemoteException;

    boolean isRinging() throws RemoteException;

    boolean isIdle() throws RemoteException;

    boolean isRadioOn() throws RemoteException;

    boolean isSimPinEnabled() throws RemoteException;

    void cancelMissedCallsNotification() throws RemoteException;

    boolean supplyPin(String str) throws RemoteException;

    boolean supplyPuk(String str, String str2) throws RemoteException;

    boolean handlePinMmi(String str) throws RemoteException;

    void toggleRadioOnOff() throws RemoteException;

    boolean setRadio(boolean z) throws RemoteException;

    boolean setRadioPower(boolean z) throws RemoteException;

    void updateServiceLocation() throws RemoteException;

    void enableLocationUpdates() throws RemoteException;

    void disableLocationUpdates() throws RemoteException;

    int enableApnType(String str) throws RemoteException;

    int disableApnType(String str) throws RemoteException;

    boolean enableDataConnectivity() throws RemoteException;

    boolean disableDataConnectivity() throws RemoteException;

    boolean isDataConnectivityPossible() throws RemoteException;

    Bundle getCellLocation() throws RemoteException;

    List<NeighboringCellInfo> getNeighboringCellInfo(String str) throws RemoteException;

    int getCallState() throws RemoteException;

    int getDataActivity() throws RemoteException;

    int getDataState() throws RemoteException;

    int getActivePhoneType() throws RemoteException;

    int getCdmaEriIconIndex() throws RemoteException;

    int getCdmaEriIconMode() throws RemoteException;

    String getCdmaEriText() throws RemoteException;

    boolean needsOtaServiceProvisioning() throws RemoteException;

    int getVoiceMessageCount() throws RemoteException;

    int getNetworkType() throws RemoteException;

    int getDataNetworkType() throws RemoteException;

    int getVoiceNetworkType() throws RemoteException;

    boolean hasIccCard() throws RemoteException;

    int getLteOnCdmaMode() throws RemoteException;

    List<CellInfo> getAllCellInfo() throws RemoteException;

    void setCellInfoListRate(int i) throws RemoteException;

    /* loaded from: ITelephony$Stub.class */
    public static abstract class Stub extends Binder implements ITelephony {
        private static final String DESCRIPTOR = "com.android.internal.telephony.ITelephony";
        static final int TRANSACTION_dial = 1;
        static final int TRANSACTION_call = 2;
        static final int TRANSACTION_showCallScreen = 3;
        static final int TRANSACTION_showCallScreenWithDialpad = 4;
        static final int TRANSACTION_endCall = 5;
        static final int TRANSACTION_answerRingingCall = 6;
        static final int TRANSACTION_silenceRinger = 7;
        static final int TRANSACTION_isOffhook = 8;
        static final int TRANSACTION_isRinging = 9;
        static final int TRANSACTION_isIdle = 10;
        static final int TRANSACTION_isRadioOn = 11;
        static final int TRANSACTION_isSimPinEnabled = 12;
        static final int TRANSACTION_cancelMissedCallsNotification = 13;
        static final int TRANSACTION_supplyPin = 14;
        static final int TRANSACTION_supplyPuk = 15;
        static final int TRANSACTION_handlePinMmi = 16;
        static final int TRANSACTION_toggleRadioOnOff = 17;
        static final int TRANSACTION_setRadio = 18;
        static final int TRANSACTION_setRadioPower = 19;
        static final int TRANSACTION_updateServiceLocation = 20;
        static final int TRANSACTION_enableLocationUpdates = 21;
        static final int TRANSACTION_disableLocationUpdates = 22;
        static final int TRANSACTION_enableApnType = 23;
        static final int TRANSACTION_disableApnType = 24;
        static final int TRANSACTION_enableDataConnectivity = 25;
        static final int TRANSACTION_disableDataConnectivity = 26;
        static final int TRANSACTION_isDataConnectivityPossible = 27;
        static final int TRANSACTION_getCellLocation = 28;
        static final int TRANSACTION_getNeighboringCellInfo = 29;
        static final int TRANSACTION_getCallState = 30;
        static final int TRANSACTION_getDataActivity = 31;
        static final int TRANSACTION_getDataState = 32;
        static final int TRANSACTION_getActivePhoneType = 33;
        static final int TRANSACTION_getCdmaEriIconIndex = 34;
        static final int TRANSACTION_getCdmaEriIconMode = 35;
        static final int TRANSACTION_getCdmaEriText = 36;
        static final int TRANSACTION_needsOtaServiceProvisioning = 37;
        static final int TRANSACTION_getVoiceMessageCount = 38;
        static final int TRANSACTION_getNetworkType = 39;
        static final int TRANSACTION_getDataNetworkType = 40;
        static final int TRANSACTION_getVoiceNetworkType = 41;
        static final int TRANSACTION_hasIccCard = 42;
        static final int TRANSACTION_getLteOnCdmaMode = 43;
        static final int TRANSACTION_getAllCellInfo = 44;
        static final int TRANSACTION_setCellInfoListRate = 45;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITelephony asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITelephony)) {
                return (ITelephony) iin;
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
                    String _arg0 = data.readString();
                    dial(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    String _arg1 = data.readString();
                    call(_arg02, _arg1);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = showCallScreen();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg03 = 0 != data.readInt();
                    boolean _result2 = showCallScreenWithDialpad(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = endCall();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    answerRingingCall();
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    silenceRinger();
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = isOffhook();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = isRinging();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result6 = isIdle();
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result7 = isRadioOn();
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result8 = isSimPinEnabled();
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    cancelMissedCallsNotification();
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    boolean _result9 = supplyPin(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    String _arg12 = data.readString();
                    boolean _result10 = supplyPuk(_arg05, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    boolean _result11 = handlePinMmi(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result11 ? 1 : 0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    toggleRadioOnOff();
                    reply.writeNoException();
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg07 = 0 != data.readInt();
                    boolean _result12 = setRadio(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result12 ? 1 : 0);
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg08 = 0 != data.readInt();
                    boolean _result13 = setRadioPower(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result13 ? 1 : 0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    updateServiceLocation();
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    enableLocationUpdates();
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    disableLocationUpdates();
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    int _result14 = enableApnType(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    int _result15 = disableApnType(_arg010);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result16 = enableDataConnectivity();
                    reply.writeNoException();
                    reply.writeInt(_result16 ? 1 : 0);
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result17 = disableDataConnectivity();
                    reply.writeNoException();
                    reply.writeInt(_result17 ? 1 : 0);
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result18 = isDataConnectivityPossible();
                    reply.writeNoException();
                    reply.writeInt(_result18 ? 1 : 0);
                    return true;
                case 28:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _result19 = getCellLocation();
                    reply.writeNoException();
                    if (_result19 != null) {
                        reply.writeInt(1);
                        _result19.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 29:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg011 = data.readString();
                    List<NeighboringCellInfo> _result20 = getNeighboringCellInfo(_arg011);
                    reply.writeNoException();
                    reply.writeTypedList(_result20);
                    return true;
                case 30:
                    data.enforceInterface(DESCRIPTOR);
                    int _result21 = getCallState();
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case 31:
                    data.enforceInterface(DESCRIPTOR);
                    int _result22 = getDataActivity();
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    return true;
                case 32:
                    data.enforceInterface(DESCRIPTOR);
                    int _result23 = getDataState();
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case 33:
                    data.enforceInterface(DESCRIPTOR);
                    int _result24 = getActivePhoneType();
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    return true;
                case 34:
                    data.enforceInterface(DESCRIPTOR);
                    int _result25 = getCdmaEriIconIndex();
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    return true;
                case 35:
                    data.enforceInterface(DESCRIPTOR);
                    int _result26 = getCdmaEriIconMode();
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    return true;
                case 36:
                    data.enforceInterface(DESCRIPTOR);
                    String _result27 = getCdmaEriText();
                    reply.writeNoException();
                    reply.writeString(_result27);
                    return true;
                case 37:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result28 = needsOtaServiceProvisioning();
                    reply.writeNoException();
                    reply.writeInt(_result28 ? 1 : 0);
                    return true;
                case 38:
                    data.enforceInterface(DESCRIPTOR);
                    int _result29 = getVoiceMessageCount();
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    return true;
                case 39:
                    data.enforceInterface(DESCRIPTOR);
                    int _result30 = getNetworkType();
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    return true;
                case 40:
                    data.enforceInterface(DESCRIPTOR);
                    int _result31 = getDataNetworkType();
                    reply.writeNoException();
                    reply.writeInt(_result31);
                    return true;
                case 41:
                    data.enforceInterface(DESCRIPTOR);
                    int _result32 = getVoiceNetworkType();
                    reply.writeNoException();
                    reply.writeInt(_result32);
                    return true;
                case 42:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result33 = hasIccCard();
                    reply.writeNoException();
                    reply.writeInt(_result33 ? 1 : 0);
                    return true;
                case 43:
                    data.enforceInterface(DESCRIPTOR);
                    int _result34 = getLteOnCdmaMode();
                    reply.writeNoException();
                    reply.writeInt(_result34);
                    return true;
                case 44:
                    data.enforceInterface(DESCRIPTOR);
                    List<CellInfo> _result35 = getAllCellInfo();
                    reply.writeNoException();
                    reply.writeTypedList(_result35);
                    return true;
                case 45:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    setCellInfoListRate(_arg012);
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
        /* loaded from: ITelephony$Stub$Proxy.class */
        public static class Proxy implements ITelephony {
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

            @Override // com.android.internal.telephony.ITelephony
            public void dial(String number) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(number);
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

            @Override // com.android.internal.telephony.ITelephony
            public void call(String callingPackage, String number) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(number);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean showCallScreen() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean showCallScreenWithDialpad(boolean showDialpad) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showDialpad ? 1 : 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean endCall() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public void answerRingingCall() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public void silenceRinger() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean isOffhook() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean isRinging() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean isIdle() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean isRadioOn() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean isSimPinEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public void cancelMissedCallsNotification() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean supplyPin(String pin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pin);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean supplyPuk(String puk, String pin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(puk);
                    _data.writeString(pin);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean handlePinMmi(String dialString) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dialString);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public void toggleRadioOnOff() throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean setRadio(boolean turnOn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(turnOn ? 1 : 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean setRadioPower(boolean turnOn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(turnOn ? 1 : 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public void updateServiceLocation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public void enableLocationUpdates() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public void disableLocationUpdates() throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephony
            public int enableApnType(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
                    this.mRemote.transact(23, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int disableApnType(String type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(type);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean enableDataConnectivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public boolean disableDataConnectivity() throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean isDataConnectivityPossible() throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephony
            public Bundle getCellLocation() throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public List<NeighboringCellInfo> getNeighboringCellInfo(String callingPkg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    List<NeighboringCellInfo> _result = _reply.createTypedArrayList(NeighboringCellInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public int getCallState() throws RemoteException {
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

            @Override // com.android.internal.telephony.ITelephony
            public int getDataActivity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getDataState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getActivePhoneType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getCdmaEriIconIndex() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getCdmaEriIconMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public String getCdmaEriText() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean needsOtaServiceProvisioning() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public int getVoiceMessageCount() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getNetworkType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(39, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getDataNetworkType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public int getVoiceNetworkType() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(41, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public boolean hasIccCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public int getLteOnCdmaMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
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

            @Override // com.android.internal.telephony.ITelephony
            public List<CellInfo> getAllCellInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    List<CellInfo> _result = _reply.createTypedArrayList(CellInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.telephony.ITelephony
            public void setCellInfoListRate(int rateInMillis) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rateInMillis);
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
        }
    }
}