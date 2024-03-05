package android.os;

/* loaded from: IBatteryPropertiesListener.class */
public interface IBatteryPropertiesListener extends IInterface {
    void batteryPropertiesChanged(BatteryProperties batteryProperties) throws RemoteException;

    /* loaded from: IBatteryPropertiesListener$Stub.class */
    public static abstract class Stub extends Binder implements IBatteryPropertiesListener {
        private static final String DESCRIPTOR = "android.os.IBatteryPropertiesListener";
        static final int TRANSACTION_batteryPropertiesChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBatteryPropertiesListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBatteryPropertiesListener)) {
                return (IBatteryPropertiesListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            BatteryProperties _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = BatteryProperties.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    batteryPropertiesChanged(_arg0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IBatteryPropertiesListener$Stub$Proxy.class */
        private static class Proxy implements IBatteryPropertiesListener {
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

            @Override // android.os.IBatteryPropertiesListener
            public void batteryPropertiesChanged(BatteryProperties props) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (props != null) {
                        _data.writeInt(1);
                        props.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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