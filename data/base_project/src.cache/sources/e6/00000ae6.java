package android.os;

import android.os.IBinder;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

/* loaded from: CommonClock.class */
public class CommonClock {
    public static final long TIME_NOT_SYNCED = -1;
    public static final long INVALID_TIMELINE_ID = 0;
    public static final int ERROR_ESTIMATE_UNKNOWN = Integer.MAX_VALUE;
    public static final int STATE_INVALID = -1;
    public static final int STATE_INITIAL = 0;
    public static final int STATE_CLIENT = 1;
    public static final int STATE_MASTER = 2;
    public static final int STATE_RONIN = 3;
    public static final int STATE_WAIT_FOR_ELECTION = 4;
    public static final String SERVICE_NAME = "common_time.clock";
    private IBinder mRemote;
    private String mInterfaceDesc;
    private CommonTimeUtils mUtils;
    private static final int METHOD_IS_COMMON_TIME_VALID = 1;
    private static final int METHOD_COMMON_TIME_TO_LOCAL_TIME = 2;
    private static final int METHOD_LOCAL_TIME_TO_COMMON_TIME = 3;
    private static final int METHOD_GET_COMMON_TIME = 4;
    private static final int METHOD_GET_COMMON_FREQ = 5;
    private static final int METHOD_GET_LOCAL_TIME = 6;
    private static final int METHOD_GET_LOCAL_FREQ = 7;
    private static final int METHOD_GET_ESTIMATED_ERROR = 8;
    private static final int METHOD_GET_TIMELINE_ID = 9;
    private static final int METHOD_GET_STATE = 10;
    private static final int METHOD_GET_MASTER_ADDRESS = 11;
    private static final int METHOD_REGISTER_LISTENER = 12;
    private static final int METHOD_UNREGISTER_LISTENER = 13;
    private static final int METHOD_CBK_ON_TIMELINE_CHANGED = 1;
    private final Object mListenerLock = new Object();
    private OnTimelineChangedListener mTimelineChangedListener = null;
    private OnServerDiedListener mServerDiedListener = null;
    private IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient() { // from class: android.os.CommonClock.1
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (CommonClock.this.mListenerLock) {
                if (null != CommonClock.this.mServerDiedListener) {
                    CommonClock.this.mServerDiedListener.onServerDied();
                }
            }
        }
    };
    private TimelineChangedListener mCallbackTgt = null;

    /* loaded from: CommonClock$OnServerDiedListener.class */
    public interface OnServerDiedListener {
        void onServerDied();
    }

    /* loaded from: CommonClock$OnTimelineChangedListener.class */
    public interface OnTimelineChangedListener {
        void onTimelineChanged(long j);
    }

    public CommonClock() throws RemoteException {
        this.mRemote = null;
        this.mInterfaceDesc = "";
        this.mRemote = ServiceManager.getService(SERVICE_NAME);
        if (null == this.mRemote) {
            throw new RemoteException();
        }
        this.mInterfaceDesc = this.mRemote.getInterfaceDescriptor();
        this.mUtils = new CommonTimeUtils(this.mRemote, this.mInterfaceDesc);
        this.mRemote.linkToDeath(this.mDeathHandler, 0);
        registerTimelineChangeListener();
    }

    public static CommonClock create() {
        CommonClock retVal;
        try {
            retVal = new CommonClock();
        } catch (RemoteException e) {
            retVal = null;
        }
        return retVal;
    }

    public void release() {
        unregisterTimelineChangeListener();
        if (null != this.mRemote) {
            try {
                this.mRemote.unlinkToDeath(this.mDeathHandler, 0);
            } catch (NoSuchElementException e) {
            }
            this.mRemote = null;
        }
        this.mUtils = null;
    }

    public long getTime() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetLong(4, -1L);
    }

    public int getEstimatedError() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetInt(8, Integer.MAX_VALUE);
    }

    public long getTimelineId() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetLong(9, 0L);
    }

    public int getState() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetInt(10, -1);
    }

    public InetSocketAddress getMasterAddr() throws RemoteException {
        throwOnDeadServer();
        return this.mUtils.transactGetSockaddr(11);
    }

    public void setTimelineChangedListener(OnTimelineChangedListener listener) {
        synchronized (this.mListenerLock) {
            this.mTimelineChangedListener = listener;
        }
    }

    public void setServerDiedListener(OnServerDiedListener listener) {
        synchronized (this.mListenerLock) {
            this.mServerDiedListener = listener;
        }
    }

    protected void finalize() throws Throwable {
        release();
    }

    private void throwOnDeadServer() throws RemoteException {
        if (null == this.mRemote || null == this.mUtils) {
            throw new RemoteException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CommonClock$TimelineChangedListener.class */
    public class TimelineChangedListener extends Binder {
        private static final String DESCRIPTOR = "android.os.ICommonClockListener";

        private TimelineChangedListener() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    long timelineId = data.readLong();
                    synchronized (CommonClock.this.mListenerLock) {
                        if (null != CommonClock.this.mTimelineChangedListener) {
                            CommonClock.this.mTimelineChangedListener.onTimelineChanged(timelineId);
                        }
                    }
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    private void registerTimelineChangeListener() throws RemoteException {
        boolean success;
        if (null != this.mCallbackTgt) {
            return;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        this.mCallbackTgt = new TimelineChangedListener();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            data.writeStrongBinder(this.mCallbackTgt);
            this.mRemote.transact(12, data, reply, 0);
            success = 0 == reply.readInt();
            reply.recycle();
            data.recycle();
        } catch (RemoteException e) {
            success = false;
            reply.recycle();
            data.recycle();
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
        if (!success) {
            this.mCallbackTgt = null;
            this.mRemote = null;
            this.mUtils = null;
        }
    }

    private void unregisterTimelineChangeListener() {
        if (null == this.mCallbackTgt) {
            return;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(this.mInterfaceDesc);
            data.writeStrongBinder(this.mCallbackTgt);
            this.mRemote.transact(13, data, reply, 0);
            reply.recycle();
            data.recycle();
            this.mCallbackTgt = null;
        } catch (RemoteException e) {
            reply.recycle();
            data.recycle();
            this.mCallbackTgt = null;
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            this.mCallbackTgt = null;
            throw th;
        }
    }
}