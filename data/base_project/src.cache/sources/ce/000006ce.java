package android.location;

import android.hardware.location.GeofenceHardwareRequestParcelable;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IFusedGeofenceHardware.class */
public interface IFusedGeofenceHardware extends IInterface {
    boolean isSupported() throws RemoteException;

    void addGeofences(GeofenceHardwareRequestParcelable[] geofenceHardwareRequestParcelableArr) throws RemoteException;

    void removeGeofences(int[] iArr) throws RemoteException;

    void pauseMonitoringGeofence(int i) throws RemoteException;

    void resumeMonitoringGeofence(int i, int i2) throws RemoteException;

    void modifyGeofenceOptions(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    /* loaded from: IFusedGeofenceHardware$Stub.class */
    public static abstract class Stub extends Binder implements IFusedGeofenceHardware {
        private static final String DESCRIPTOR = "android.location.IFusedGeofenceHardware";
        static final int TRANSACTION_isSupported = 1;
        static final int TRANSACTION_addGeofences = 2;
        static final int TRANSACTION_removeGeofences = 3;
        static final int TRANSACTION_pauseMonitoringGeofence = 4;
        static final int TRANSACTION_resumeMonitoringGeofence = 5;
        static final int TRANSACTION_modifyGeofenceOptions = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IFusedGeofenceHardware asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFusedGeofenceHardware)) {
                return (IFusedGeofenceHardware) iin;
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
                    boolean _result = isSupported();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    GeofenceHardwareRequestParcelable[] _arg0 = (GeofenceHardwareRequestParcelable[]) data.createTypedArray(GeofenceHardwareRequestParcelable.CREATOR);
                    addGeofences(_arg0);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg02 = data.createIntArray();
                    removeGeofences(_arg02);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    pauseMonitoringGeofence(_arg03);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg1 = data.readInt();
                    resumeMonitoringGeofence(_arg04, _arg1);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg12 = data.readInt();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    int _arg5 = data.readInt();
                    modifyGeofenceOptions(_arg05, _arg12, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IFusedGeofenceHardware$Stub$Proxy.class */
        private static class Proxy implements IFusedGeofenceHardware {
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

            @Override // android.location.IFusedGeofenceHardware
            public boolean isSupported() throws RemoteException {
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

            @Override // android.location.IFusedGeofenceHardware
            public void addGeofences(GeofenceHardwareRequestParcelable[] geofenceRequestsArray) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(geofenceRequestsArray, 0);
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

            @Override // android.location.IFusedGeofenceHardware
            public void removeGeofences(int[] geofenceIds) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(geofenceIds);
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

            @Override // android.location.IFusedGeofenceHardware
            public void pauseMonitoringGeofence(int geofenceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
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

            @Override // android.location.IFusedGeofenceHardware
            public void resumeMonitoringGeofence(int geofenceId, int monitorTransitions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(monitorTransitions);
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

            @Override // android.location.IFusedGeofenceHardware
            public void modifyGeofenceOptions(int geofenceId, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer, int sourcesToUse) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(lastTransition);
                    _data.writeInt(monitorTransitions);
                    _data.writeInt(notificationResponsiveness);
                    _data.writeInt(unknownTimer);
                    _data.writeInt(sourcesToUse);
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
        }
    }
}