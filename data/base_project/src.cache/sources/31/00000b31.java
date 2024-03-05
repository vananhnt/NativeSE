package android.os;

/* loaded from: IVibratorService.class */
public interface IVibratorService extends IInterface {
    boolean hasVibrator() throws RemoteException;

    void vibrate(int i, String str, long j, IBinder iBinder) throws RemoteException;

    void vibratePattern(int i, String str, long[] jArr, int i2, IBinder iBinder) throws RemoteException;

    void cancelVibrate(IBinder iBinder) throws RemoteException;

    /* loaded from: IVibratorService$Stub.class */
    public static abstract class Stub extends Binder implements IVibratorService {
        private static final String DESCRIPTOR = "android.os.IVibratorService";
        static final int TRANSACTION_hasVibrator = 1;
        static final int TRANSACTION_vibrate = 2;
        static final int TRANSACTION_vibratePattern = 3;
        static final int TRANSACTION_cancelVibrate = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVibratorService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVibratorService)) {
                return (IVibratorService) iin;
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
                    boolean _result = hasVibrator();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    String _arg1 = data.readString();
                    long _arg2 = data.readLong();
                    IBinder _arg3 = data.readStrongBinder();
                    vibrate(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    String _arg12 = data.readString();
                    long[] _arg22 = data.createLongArray();
                    int _arg32 = data.readInt();
                    IBinder _arg4 = data.readStrongBinder();
                    vibratePattern(_arg02, _arg12, _arg22, _arg32, _arg4);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    cancelVibrate(_arg03);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IVibratorService$Stub$Proxy.class */
        private static class Proxy implements IVibratorService {
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

            @Override // android.os.IVibratorService
            public boolean hasVibrator() throws RemoteException {
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

            @Override // android.os.IVibratorService
            public void vibrate(int uid, String packageName, long milliseconds, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeLong(milliseconds);
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

            @Override // android.os.IVibratorService
            public void vibratePattern(int uid, String packageName, long[] pattern, int repeat, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeLongArray(pattern);
                    _data.writeInt(repeat);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.os.IVibratorService
            public void cancelVibrate(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
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
        }
    }
}