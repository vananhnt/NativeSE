package com.android.internal.textservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SuggestionsInfo;

/* loaded from: ISpellCheckerSessionListener.class */
public interface ISpellCheckerSessionListener extends IInterface {
    void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr) throws RemoteException;

    void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr) throws RemoteException;

    /* loaded from: ISpellCheckerSessionListener$Stub.class */
    public static abstract class Stub extends Binder implements ISpellCheckerSessionListener {
        private static final String DESCRIPTOR = "com.android.internal.textservice.ISpellCheckerSessionListener";
        static final int TRANSACTION_onGetSuggestions = 1;
        static final int TRANSACTION_onGetSentenceSuggestions = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISpellCheckerSessionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISpellCheckerSessionListener)) {
                return (ISpellCheckerSessionListener) iin;
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
                    SuggestionsInfo[] _arg0 = (SuggestionsInfo[]) data.createTypedArray(SuggestionsInfo.CREATOR);
                    onGetSuggestions(_arg0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    SentenceSuggestionsInfo[] _arg02 = (SentenceSuggestionsInfo[]) data.createTypedArray(SentenceSuggestionsInfo.CREATOR);
                    onGetSentenceSuggestions(_arg02);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: ISpellCheckerSessionListener$Stub$Proxy.class */
        private static class Proxy implements ISpellCheckerSessionListener {
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

            @Override // com.android.internal.textservice.ISpellCheckerSessionListener
            public void onGetSuggestions(SuggestionsInfo[] results) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(results, 0);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.textservice.ISpellCheckerSessionListener
            public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] result) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(result, 0);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}