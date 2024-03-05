package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IAudioFocusDispatcher.class */
public interface IAudioFocusDispatcher extends IInterface {
    void dispatchAudioFocusChange(int i, String str) throws RemoteException;

    /* loaded from: IAudioFocusDispatcher$Stub.class */
    public static abstract class Stub extends Binder implements IAudioFocusDispatcher {
        private static final String DESCRIPTOR = "android.media.IAudioFocusDispatcher";
        static final int TRANSACTION_dispatchAudioFocusChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioFocusDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioFocusDispatcher)) {
                return (IAudioFocusDispatcher) iin;
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
                    int _arg0 = data.readInt();
                    String _arg1 = data.readString();
                    dispatchAudioFocusChange(_arg0, _arg1);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IAudioFocusDispatcher$Stub$Proxy.class */
        public static class Proxy implements IAudioFocusDispatcher {
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

            @Override // android.media.IAudioFocusDispatcher
            public void dispatchAudioFocusChange(int focusChange, String clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(focusChange);
                    _data.writeString(clientId);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}