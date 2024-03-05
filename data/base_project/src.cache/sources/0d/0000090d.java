package android.net;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: NetworkState.class */
public class NetworkState implements Parcelable {
    public final NetworkInfo networkInfo;
    public final LinkProperties linkProperties;
    public final LinkCapabilities linkCapabilities;
    public final String subscriberId;
    public final String networkId;
    public static final Parcelable.Creator<NetworkState> CREATOR = new Parcelable.Creator<NetworkState>() { // from class: android.net.NetworkState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkState createFromParcel(Parcel in) {
            return new NetworkState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkState[] newArray(int size) {
            return new NetworkState[size];
        }
    };

    public NetworkState(NetworkInfo networkInfo, LinkProperties linkProperties, LinkCapabilities linkCapabilities) {
        this(networkInfo, linkProperties, linkCapabilities, null, null);
    }

    public NetworkState(NetworkInfo networkInfo, LinkProperties linkProperties, LinkCapabilities linkCapabilities, String subscriberId, String networkId) {
        this.networkInfo = networkInfo;
        this.linkProperties = linkProperties;
        this.linkCapabilities = linkCapabilities;
        this.subscriberId = subscriberId;
        this.networkId = networkId;
    }

    public NetworkState(Parcel in) {
        this.networkInfo = (NetworkInfo) in.readParcelable(null);
        this.linkProperties = (LinkProperties) in.readParcelable(null);
        this.linkCapabilities = (LinkCapabilities) in.readParcelable(null);
        this.subscriberId = in.readString();
        this.networkId = in.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.networkInfo, flags);
        out.writeParcelable(this.linkProperties, flags);
        out.writeParcelable(this.linkCapabilities, flags);
        out.writeString(this.subscriberId);
        out.writeString(this.networkId);
    }
}