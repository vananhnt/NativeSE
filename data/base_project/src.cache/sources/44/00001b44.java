package com.android.internal.view;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;

/* loaded from: IInputMethodSession.class */
public interface IInputMethodSession extends IInterface {
    void finishInput() throws RemoteException;

    void updateExtractedText(int i, ExtractedText extractedText) throws RemoteException;

    void updateSelection(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void viewClicked(boolean z) throws RemoteException;

    void updateCursor(Rect rect) throws RemoteException;

    void displayCompletions(CompletionInfo[] completionInfoArr) throws RemoteException;

    void appPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void toggleSoftInput(int i, int i2) throws RemoteException;

    void finishSession() throws RemoteException;

    /* loaded from: IInputMethodSession$Stub.class */
    public static abstract class Stub extends Binder implements IInputMethodSession {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputMethodSession";
        static final int TRANSACTION_finishInput = 1;
        static final int TRANSACTION_updateExtractedText = 2;
        static final int TRANSACTION_updateSelection = 3;
        static final int TRANSACTION_viewClicked = 4;
        static final int TRANSACTION_updateCursor = 5;
        static final int TRANSACTION_displayCompletions = 6;
        static final int TRANSACTION_appPrivateCommand = 7;
        static final int TRANSACTION_toggleSoftInput = 8;
        static final int TRANSACTION_finishSession = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputMethodSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodSession)) {
                return (IInputMethodSession) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            Rect _arg0;
            ExtractedText _arg12;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    finishInput();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = ExtractedText.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    updateExtractedText(_arg02, _arg12);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _arg13 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    updateSelection(_arg03, _arg13, _arg2, _arg3, _arg4, _arg5);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg04 = 0 != data.readInt();
                    viewClicked(_arg04);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    updateCursor(_arg0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    CompletionInfo[] _arg05 = (CompletionInfo[]) data.createTypedArray(CompletionInfo.CREATOR);
                    displayCompletions(_arg05);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    if (0 != data.readInt()) {
                        _arg1 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    appPrivateCommand(_arg06, _arg1);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg14 = data.readInt();
                    toggleSoftInput(_arg07, _arg14);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    finishSession();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IInputMethodSession$Stub$Proxy.class */
        private static class Proxy implements IInputMethodSession {
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

            @Override // com.android.internal.view.IInputMethodSession
            public void finishInput() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void updateExtractedText(int token, ExtractedText text) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
                    if (text != null) {
                        _data.writeInt(1);
                        text.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(oldSelStart);
                    _data.writeInt(oldSelEnd);
                    _data.writeInt(newSelStart);
                    _data.writeInt(newSelEnd);
                    _data.writeInt(candidatesStart);
                    _data.writeInt(candidatesEnd);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void viewClicked(boolean focusChanged) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(focusChanged ? 1 : 0);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void updateCursor(Rect newCursor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (newCursor != null) {
                        _data.writeInt(1);
                        newCursor.writeToParcel(_data, 0);
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

            @Override // com.android.internal.view.IInputMethodSession
            public void displayCompletions(CompletionInfo[] completions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(completions, 0);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void appPrivateCommand(String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void toggleSoftInput(int showFlags, int hideFlags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showFlags);
                    _data.writeInt(hideFlags);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputMethodSession
            public void finishSession() throws RemoteException {
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
        }
    }
}