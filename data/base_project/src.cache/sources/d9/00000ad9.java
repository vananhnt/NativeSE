package android.os;

import android.os.IBinder;
import android.util.Log;
import java.io.FileDescriptor;
import java.lang.ref.WeakReference;

/* compiled from: Binder.java */
/* loaded from: BinderProxy.class */
final class BinderProxy implements IBinder {
    private final WeakReference mSelf = new WeakReference(this);
    private int mObject;
    private int mOrgue;

    @Override // android.os.IBinder
    public native boolean pingBinder();

    @Override // android.os.IBinder
    public native boolean isBinderAlive();

    @Override // android.os.IBinder
    public native String getInterfaceDescriptor() throws RemoteException;

    @Override // android.os.IBinder
    public native boolean transact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException;

    @Override // android.os.IBinder
    public native void linkToDeath(IBinder.DeathRecipient deathRecipient, int i) throws RemoteException;

    @Override // android.os.IBinder
    public native boolean unlinkToDeath(IBinder.DeathRecipient deathRecipient, int i);

    private final native void destroy();

    @Override // android.os.IBinder
    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    @Override // android.os.IBinder
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        try {
            transact(IBinder.DUMP_TRANSACTION, data, reply, 0);
            reply.readException();
            data.recycle();
            reply.recycle();
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.os.IBinder
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        try {
            transact(IBinder.DUMP_TRANSACTION, data, reply, 1);
            data.recycle();
            reply.recycle();
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    BinderProxy() {
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private static final void sendDeathNotice(IBinder.DeathRecipient recipient) {
        try {
            recipient.binderDied();
        } catch (RuntimeException exc) {
            Log.w("BinderNative", "Uncaught exception from death notification", exc);
        }
    }
}