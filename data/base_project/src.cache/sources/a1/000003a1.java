package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: ParceledListSlice.class */
public class ParceledListSlice<T extends Parcelable> implements Parcelable {
    private static final int MAX_IPC_SIZE = 262144;
    private static final int MAX_FIRST_IPC_SIZE = 131072;
    private final List<T> mList;
    private static String TAG = "ParceledListSlice";
    private static boolean DEBUG = false;
    public static final Parcelable.ClassLoaderCreator<ParceledListSlice> CREATOR = new Parcelable.ClassLoaderCreator<ParceledListSlice>() { // from class: android.content.pm.ParceledListSlice.2
        @Override // android.os.Parcelable.Creator
        public ParceledListSlice createFromParcel(Parcel in) {
            return new ParceledListSlice(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.ClassLoaderCreator
        public ParceledListSlice createFromParcel(Parcel in, ClassLoader loader) {
            return new ParceledListSlice(in, loader);
        }

        @Override // android.os.Parcelable.Creator
        public ParceledListSlice[] newArray(int size) {
            return new ParceledListSlice[size];
        }
    };

    public ParceledListSlice(List<T> list) {
        this.mList = list;
    }

    private ParceledListSlice(Parcel p, ClassLoader loader) {
        int N = p.readInt();
        this.mList = new ArrayList(N);
        if (DEBUG) {
            Log.d(TAG, "Retrieving " + N + " items");
        }
        if (N <= 0) {
            return;
        }
        Parcelable.Creator<T> creator = p.readParcelableCreator(loader);
        int i = 0;
        while (i < N && p.readInt() != 0) {
            ((List<T>) this.mList).add(p.readCreator(creator, loader));
            if (DEBUG) {
                Log.d(TAG, "Read inline #" + i + ": " + this.mList.get(this.mList.size() - 1));
            }
            i++;
        }
        if (i >= N) {
            return;
        }
        IBinder retriever = p.readStrongBinder();
        while (i < N) {
            if (DEBUG) {
                Log.d(TAG, "Reading more @" + i + " of " + N + ": retriever=" + retriever);
            }
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(i);
            try {
                retriever.transact(1, data, reply, 0);
                while (i < N && reply.readInt() != 0) {
                    ((List<T>) this.mList).add(reply.readCreator(creator, loader));
                    if (DEBUG) {
                        Log.d(TAG, "Read extra #" + i + ": " + this.mList.get(this.mList.size() - 1));
                    }
                    i++;
                }
                reply.recycle();
                data.recycle();
            } catch (RemoteException e) {
                Log.w(TAG, "Failure retrieving array; only received " + i + " of " + N, e);
                return;
            }
        }
    }

    public List<T> getList() {
        return this.mList;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        int contents = 0;
        for (int i = 0; i < this.mList.size(); i++) {
            contents |= this.mList.get(i).describeContents();
        }
        return contents;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, final int flags) {
        final int N = this.mList.size();
        dest.writeInt(N);
        if (DEBUG) {
            Log.d(TAG, "Writing " + N + " items");
        }
        if (N > 0) {
            dest.writeParcelableCreator(this.mList.get(0));
            int i = 0;
            while (i < N && dest.dataSize() < 131072) {
                dest.writeInt(1);
                this.mList.get(i).writeToParcel(dest, flags);
                if (DEBUG) {
                    Log.d(TAG, "Wrote inline #" + i + ": " + this.mList.get(i));
                }
                i++;
            }
            if (i < N) {
                dest.writeInt(0);
                Binder retriever = new Binder() { // from class: android.content.pm.ParceledListSlice.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.Binder
                    public boolean onTransact(int code, Parcel data, Parcel reply, int flags2) throws RemoteException {
                        if (code != 1) {
                            return super.onTransact(code, data, reply, flags2);
                        }
                        int i2 = data.readInt();
                        if (ParceledListSlice.DEBUG) {
                            Log.d(ParceledListSlice.TAG, "Writing more @" + i2 + " of " + N);
                        }
                        while (i2 < N && reply.dataSize() < 262144) {
                            reply.writeInt(1);
                            ((Parcelable) ParceledListSlice.this.mList.get(i2)).writeToParcel(reply, flags);
                            if (ParceledListSlice.DEBUG) {
                                Log.d(ParceledListSlice.TAG, "Wrote extra #" + i2 + ": " + ParceledListSlice.this.mList.get(i2));
                            }
                            i2++;
                        }
                        if (i2 < N) {
                            if (ParceledListSlice.DEBUG) {
                                Log.d(ParceledListSlice.TAG, "Breaking @" + i2 + " of " + N);
                            }
                            reply.writeInt(0);
                            return true;
                        }
                        return true;
                    }
                };
                if (DEBUG) {
                    Log.d(TAG, "Breaking @" + i + " of " + N + ": retriever=" + retriever);
                }
                dest.writeStrongBinder(retriever);
            }
        }
    }
}