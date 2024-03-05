package android.net;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: NetworkQuotaInfo.class */
public class NetworkQuotaInfo implements Parcelable {
    private final long mEstimatedBytes;
    private final long mSoftLimitBytes;
    private final long mHardLimitBytes;
    public static final long NO_LIMIT = -1;
    public static final Parcelable.Creator<NetworkQuotaInfo> CREATOR = new Parcelable.Creator<NetworkQuotaInfo>() { // from class: android.net.NetworkQuotaInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkQuotaInfo createFromParcel(Parcel in) {
            return new NetworkQuotaInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkQuotaInfo[] newArray(int size) {
            return new NetworkQuotaInfo[size];
        }
    };

    public NetworkQuotaInfo(long estimatedBytes, long softLimitBytes, long hardLimitBytes) {
        this.mEstimatedBytes = estimatedBytes;
        this.mSoftLimitBytes = softLimitBytes;
        this.mHardLimitBytes = hardLimitBytes;
    }

    public NetworkQuotaInfo(Parcel in) {
        this.mEstimatedBytes = in.readLong();
        this.mSoftLimitBytes = in.readLong();
        this.mHardLimitBytes = in.readLong();
    }

    public long getEstimatedBytes() {
        return this.mEstimatedBytes;
    }

    public long getSoftLimitBytes() {
        return this.mSoftLimitBytes;
    }

    public long getHardLimitBytes() {
        return this.mHardLimitBytes;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mEstimatedBytes);
        out.writeLong(this.mSoftLimitBytes);
        out.writeLong(this.mHardLimitBytes);
    }
}