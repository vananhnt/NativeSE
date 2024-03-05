package android.hardware.location;

import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IGeofenceHardwareMonitorCallback.class */
public interface IGeofenceHardwareMonitorCallback extends IInterface {
    void onMonitoringSystemChange(int i, boolean z, Location location) throws RemoteException;

    /* loaded from: IGeofenceHardwareMonitorCallback$Stub.class */
    public static abstract class Stub extends Binder implements IGeofenceHardwareMonitorCallback {
        private static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardwareMonitorCallback";
        static final int TRANSACTION_onMonitoringSystemChange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGeofenceHardwareMonitorCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGeofenceHardwareMonitorCallback)) {
                return (IGeofenceHardwareMonitorCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Location _arg2;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    boolean _arg1 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg2 = Location.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    onMonitoringSystemChange(_arg0, _arg1, _arg2);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IGeofenceHardwareMonitorCallback$Stub$Proxy.class */
        private static class Proxy implements IGeofenceHardwareMonitorCallback {
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

            @Override // android.hardware.location.IGeofenceHardwareMonitorCallback
            public void onMonitoringSystemChange(int monitoringType, boolean available, Location location) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeInt(available ? 1 : 0);
                    if (location != null) {
                        _data.writeInt(1);
                        location.writeToParcel(_data, 0);
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