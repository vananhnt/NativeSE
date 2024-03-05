package android.os;

import android.os.IBinder;
import android.os.IInterface;
import android.util.ArrayMap;

/* loaded from: RemoteCallbackList.class */
public class RemoteCallbackList<E extends IInterface> {
    private Object[] mActiveBroadcast;
    ArrayMap<IBinder, RemoteCallbackList<E>.Callback> mCallbacks = new ArrayMap<>();
    private int mBroadcastCount = -1;
    private boolean mKilled = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RemoteCallbackList$Callback.class */
    public final class Callback implements IBinder.DeathRecipient {
        final E mCallback;
        final Object mCookie;

        Callback(E callback, Object cookie) {
            this.mCallback = callback;
            this.mCookie = cookie;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (RemoteCallbackList.this.mCallbacks) {
                RemoteCallbackList.this.mCallbacks.remove(this.mCallback.asBinder());
            }
            RemoteCallbackList.this.onCallbackDied(this.mCallback, this.mCookie);
        }
    }

    public boolean register(E callback) {
        return register(callback, null);
    }

    public boolean register(E callback, Object cookie) {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return false;
            }
            IBinder binder = callback.asBinder();
            try {
                RemoteCallbackList<E>.Callback cb = new Callback(callback, cookie);
                binder.linkToDeath(cb, 0);
                this.mCallbacks.put(binder, cb);
                return true;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [E extends android.os.IInterface, android.os.IInterface] */
    public boolean unregister(E callback) {
        synchronized (this.mCallbacks) {
            RemoteCallbackList<E>.Callback cb = this.mCallbacks.remove(callback.asBinder());
            if (cb != null) {
                cb.mCallback.asBinder().unlinkToDeath(cb, 0);
                return true;
            }
            return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [E extends android.os.IInterface, android.os.IInterface] */
    public void kill() {
        synchronized (this.mCallbacks) {
            for (int cbi = this.mCallbacks.size() - 1; cbi >= 0; cbi--) {
                RemoteCallbackList<E>.Callback cb = this.mCallbacks.valueAt(cbi);
                cb.mCallback.asBinder().unlinkToDeath(cb, 0);
            }
            this.mCallbacks.clear();
            this.mKilled = true;
        }
    }

    public void onCallbackDied(E callback) {
    }

    public void onCallbackDied(E callback, Object cookie) {
        onCallbackDied(callback);
    }

    public int beginBroadcast() {
        synchronized (this.mCallbacks) {
            if (this.mBroadcastCount > 0) {
                throw new IllegalStateException("beginBroadcast() called while already in a broadcast");
            }
            int N = this.mCallbacks.size();
            this.mBroadcastCount = N;
            if (N <= 0) {
                return 0;
            }
            Object[] active = this.mActiveBroadcast;
            if (active == null || active.length < N) {
                Object[] objArr = new Object[N];
                active = objArr;
                this.mActiveBroadcast = objArr;
            }
            for (int i = 0; i < N; i++) {
                active[i] = this.mCallbacks.valueAt(i);
            }
            return N;
        }
    }

    public E getBroadcastItem(int index) {
        return ((Callback) this.mActiveBroadcast[index]).mCallback;
    }

    public Object getBroadcastCookie(int index) {
        return ((Callback) this.mActiveBroadcast[index]).mCookie;
    }

    public void finishBroadcast() {
        if (this.mBroadcastCount < 0) {
            throw new IllegalStateException("finishBroadcast() called outside of a broadcast");
        }
        Object[] active = this.mActiveBroadcast;
        if (active != null) {
            int N = this.mBroadcastCount;
            for (int i = 0; i < N; i++) {
                active[i] = null;
            }
        }
        this.mBroadcastCount = -1;
    }

    public int getRegisteredCallbackCount() {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return 0;
            }
            return this.mCallbacks.size();
        }
    }
}