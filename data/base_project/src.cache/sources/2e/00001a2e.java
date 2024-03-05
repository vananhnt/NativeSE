package com.android.internal.policy;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardShowCallback;

/* loaded from: IKeyguardService.class */
public interface IKeyguardService extends IInterface {
    boolean isShowing() throws RemoteException;

    boolean isSecure() throws RemoteException;

    boolean isShowingAndNotHidden() throws RemoteException;

    boolean isInputRestricted() throws RemoteException;

    boolean isDismissable() throws RemoteException;

    void verifyUnlock(IKeyguardExitCallback iKeyguardExitCallback) throws RemoteException;

    void keyguardDone(boolean z, boolean z2) throws RemoteException;

    void setHidden(boolean z) throws RemoteException;

    void dismiss() throws RemoteException;

    void onDreamingStarted() throws RemoteException;

    void onDreamingStopped() throws RemoteException;

    void onScreenTurnedOff(int i) throws RemoteException;

    void onScreenTurnedOn(IKeyguardShowCallback iKeyguardShowCallback) throws RemoteException;

    void setKeyguardEnabled(boolean z) throws RemoteException;

    void onSystemReady() throws RemoteException;

    void doKeyguardTimeout(Bundle bundle) throws RemoteException;

    void setCurrentUser(int i) throws RemoteException;

    void showAssistant() throws RemoteException;

    void dispatch(MotionEvent motionEvent) throws RemoteException;

    void launchCamera() throws RemoteException;

    void onBootCompleted() throws RemoteException;

    /* loaded from: IKeyguardService$Stub.class */
    public static abstract class Stub extends Binder implements IKeyguardService {
        private static final String DESCRIPTOR = "com.android.internal.policy.IKeyguardService";
        static final int TRANSACTION_isShowing = 1;
        static final int TRANSACTION_isSecure = 2;
        static final int TRANSACTION_isShowingAndNotHidden = 3;
        static final int TRANSACTION_isInputRestricted = 4;
        static final int TRANSACTION_isDismissable = 5;
        static final int TRANSACTION_verifyUnlock = 6;
        static final int TRANSACTION_keyguardDone = 7;
        static final int TRANSACTION_setHidden = 8;
        static final int TRANSACTION_dismiss = 9;
        static final int TRANSACTION_onDreamingStarted = 10;
        static final int TRANSACTION_onDreamingStopped = 11;
        static final int TRANSACTION_onScreenTurnedOff = 12;
        static final int TRANSACTION_onScreenTurnedOn = 13;
        static final int TRANSACTION_setKeyguardEnabled = 14;
        static final int TRANSACTION_onSystemReady = 15;
        static final int TRANSACTION_doKeyguardTimeout = 16;
        static final int TRANSACTION_setCurrentUser = 17;
        static final int TRANSACTION_showAssistant = 18;
        static final int TRANSACTION_dispatch = 19;
        static final int TRANSACTION_launchCamera = 20;
        static final int TRANSACTION_onBootCompleted = 21;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyguardService)) {
                return (IKeyguardService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            MotionEvent _arg0;
            Bundle _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isShowing();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result2 = isSecure();
                    reply.writeNoException();
                    reply.writeInt(_result2 ? 1 : 0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = isShowingAndNotHidden();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = isInputRestricted();
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result5 = isDismissable();
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IKeyguardExitCallback _arg03 = IKeyguardExitCallback.Stub.asInterface(data.readStrongBinder());
                    verifyUnlock(_arg03);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg04 = 0 != data.readInt();
                    boolean _arg1 = 0 != data.readInt();
                    keyguardDone(_arg04, _arg1);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg05 = 0 != data.readInt();
                    setHidden(_arg05);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    dismiss();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    onDreamingStarted();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    onDreamingStopped();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    onScreenTurnedOff(_arg06);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    IKeyguardShowCallback _arg07 = IKeyguardShowCallback.Stub.asInterface(data.readStrongBinder());
                    onScreenTurnedOn(_arg07);
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg08 = 0 != data.readInt();
                    setKeyguardEnabled(_arg08);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    onSystemReady();
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    doKeyguardTimeout(_arg02);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    setCurrentUser(_arg09);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    showAssistant();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = MotionEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    dispatch(_arg0);
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    launchCamera();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    onBootCompleted();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IKeyguardService$Stub$Proxy.class */
        private static class Proxy implements IKeyguardService {
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

            @Override // com.android.internal.policy.IKeyguardService
            public boolean isShowing() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public boolean isSecure() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public boolean isShowingAndNotHidden() throws RemoteException {
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

            @Override // com.android.internal.policy.IKeyguardService
            public boolean isInputRestricted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public boolean isDismissable() throws RemoteException {
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

            @Override // com.android.internal.policy.IKeyguardService
            public void verifyUnlock(IKeyguardExitCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void keyguardDone(boolean authenticated, boolean wakeup) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(authenticated ? 1 : 0);
                    _data.writeInt(wakeup ? 1 : 0);
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setHidden(boolean isHidden) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(isHidden ? 1 : 0);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void dismiss() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onDreamingStarted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onDreamingStopped() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurnedOff(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(12, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onScreenTurnedOn(IKeyguardShowCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(13, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setKeyguardEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enabled ? 1 : 0);
                    this.mRemote.transact(14, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onSystemReady() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void doKeyguardTimeout(Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(16, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void setCurrentUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void showAssistant() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void dispatch(MotionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(19, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void launchCamera() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.policy.IKeyguardService
            public void onBootCompleted() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(21, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}