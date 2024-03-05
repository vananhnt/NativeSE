package com.android.internal.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.inputmethod.ExtractedText;

/* loaded from: IInputContextCallback.class */
public interface IInputContextCallback extends IInterface {
    void setTextBeforeCursor(CharSequence charSequence, int i) throws RemoteException;

    void setTextAfterCursor(CharSequence charSequence, int i) throws RemoteException;

    void setCursorCapsMode(int i, int i2) throws RemoteException;

    void setExtractedText(ExtractedText extractedText, int i) throws RemoteException;

    void setSelectedText(CharSequence charSequence, int i) throws RemoteException;

    /* loaded from: IInputContextCallback$Stub.class */
    public static abstract class Stub extends Binder implements IInputContextCallback {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputContextCallback";
        static final int TRANSACTION_setTextBeforeCursor = 1;
        static final int TRANSACTION_setTextAfterCursor = 2;
        static final int TRANSACTION_setCursorCapsMode = 3;
        static final int TRANSACTION_setExtractedText = 4;
        static final int TRANSACTION_setSelectedText = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputContextCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputContextCallback)) {
                return (IInputContextCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CharSequence _arg0;
            ExtractedText _arg02;
            CharSequence _arg03;
            CharSequence _arg04;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    int _arg1 = data.readInt();
                    setTextBeforeCursor(_arg04, _arg1);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _arg12 = data.readInt();
                    setTextAfterCursor(_arg03, _arg12);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg13 = data.readInt();
                    setCursorCapsMode(_arg05, _arg13);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ExtractedText.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg14 = data.readInt();
                    setExtractedText(_arg02, _arg14);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg15 = data.readInt();
                    setSelectedText(_arg0, _arg15);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IInputContextCallback$Stub$Proxy.class */
        public static class Proxy implements IInputContextCallback {
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

            @Override // com.android.internal.view.IInputContextCallback
            public void setTextBeforeCursor(CharSequence textBeforeCursor, int seq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (textBeforeCursor != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(textBeforeCursor, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(seq);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputContextCallback
            public void setTextAfterCursor(CharSequence textAfterCursor, int seq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (textAfterCursor != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(textAfterCursor, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(seq);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputContextCallback
            public void setCursorCapsMode(int capsMode, int seq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(capsMode);
                    _data.writeInt(seq);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputContextCallback
            public void setExtractedText(ExtractedText extractedText, int seq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (extractedText != null) {
                        _data.writeInt(1);
                        extractedText.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(seq);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.view.IInputContextCallback
            public void setSelectedText(CharSequence selectedText, int seq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (selectedText != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(selectedText, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(seq);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}