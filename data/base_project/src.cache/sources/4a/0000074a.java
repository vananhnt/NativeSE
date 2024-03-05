package android.media;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserHandle;

/* loaded from: IRingtonePlayer.class */
public interface IRingtonePlayer extends IInterface {
    void play(IBinder iBinder, Uri uri, int i) throws RemoteException;

    void stop(IBinder iBinder) throws RemoteException;

    boolean isPlaying(IBinder iBinder) throws RemoteException;

    void playAsync(Uri uri, UserHandle userHandle, boolean z, int i) throws RemoteException;

    void stopAsync() throws RemoteException;

    /* loaded from: IRingtonePlayer$Stub.class */
    public static abstract class Stub extends Binder implements IRingtonePlayer {
        private static final String DESCRIPTOR = "android.media.IRingtonePlayer";
        static final int TRANSACTION_play = 1;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_isPlaying = 3;
        static final int TRANSACTION_playAsync = 4;
        static final int TRANSACTION_stopAsync = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRingtonePlayer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRingtonePlayer)) {
                return (IRingtonePlayer) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Uri _arg0;
            UserHandle _arg1;
            Uri _arg12;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg02 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg12 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    int _arg2 = data.readInt();
                    play(_arg02, _arg12, _arg2);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    stop(_arg03);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg04 = data.readStrongBinder();
                    boolean _result = isPlaying(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = Uri.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = UserHandle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    boolean _arg22 = 0 != data.readInt();
                    int _arg3 = data.readInt();
                    playAsync(_arg0, _arg1, _arg22, _arg3);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    stopAsync();
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
        /* loaded from: IRingtonePlayer$Stub$Proxy.class */
        public static class Proxy implements IRingtonePlayer {
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

            @Override // android.media.IRingtonePlayer
            public void play(IBinder token, Uri uri, int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(streamType);
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

            @Override // android.media.IRingtonePlayer
            public void stop(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
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

            @Override // android.media.IRingtonePlayer
            public boolean isPlaying(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IRingtonePlayer
            public void playAsync(Uri uri, UserHandle user, boolean looping, int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (user != null) {
                        _data.writeInt(1);
                        user.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(looping ? 1 : 0);
                    _data.writeInt(streamType);
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

            @Override // android.media.IRingtonePlayer
            public void stopAsync() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
        }
    }
}