package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;
import java.util.EnumMap;

/* loaded from: NetworkInfo.class */
public class NetworkInfo implements Parcelable {
    private static final EnumMap<DetailedState, State> stateMap = new EnumMap<>(DetailedState.class);
    private int mNetworkType;
    private int mSubtype;
    private String mTypeName;
    private String mSubtypeName;
    private State mState;
    private DetailedState mDetailedState;
    private String mReason;
    private String mExtraInfo;
    private boolean mIsFailover;
    private boolean mIsRoaming;
    private boolean mIsConnectedToProvisioningNetwork;
    private boolean mIsAvailable;
    public static final Parcelable.Creator<NetworkInfo> CREATOR;

    /* loaded from: NetworkInfo$DetailedState.class */
    public enum DetailedState {
        IDLE,
        SCANNING,
        CONNECTING,
        AUTHENTICATING,
        OBTAINING_IPADDR,
        CONNECTED,
        SUSPENDED,
        DISCONNECTING,
        DISCONNECTED,
        FAILED,
        BLOCKED,
        VERIFYING_POOR_LINK,
        CAPTIVE_PORTAL_CHECK
    }

    /* loaded from: NetworkInfo$State.class */
    public enum State {
        CONNECTING,
        CONNECTED,
        SUSPENDED,
        DISCONNECTING,
        DISCONNECTED,
        UNKNOWN
    }

    static {
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.IDLE, (DetailedState) State.DISCONNECTED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.SCANNING, (DetailedState) State.DISCONNECTED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.CONNECTING, (DetailedState) State.CONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.AUTHENTICATING, (DetailedState) State.CONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.OBTAINING_IPADDR, (DetailedState) State.CONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.VERIFYING_POOR_LINK, (DetailedState) State.CONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.CAPTIVE_PORTAL_CHECK, (DetailedState) State.CONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.CONNECTED, (DetailedState) State.CONNECTED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.SUSPENDED, (DetailedState) State.SUSPENDED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.DISCONNECTING, (DetailedState) State.DISCONNECTING);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.DISCONNECTED, (DetailedState) State.DISCONNECTED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.FAILED, (DetailedState) State.DISCONNECTED);
        stateMap.put((EnumMap<DetailedState, State>) DetailedState.BLOCKED, (DetailedState) State.DISCONNECTED);
        CREATOR = new Parcelable.Creator<NetworkInfo>() { // from class: android.net.NetworkInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public NetworkInfo createFromParcel(Parcel in) {
                int netType = in.readInt();
                int subtype = in.readInt();
                String typeName = in.readString();
                String subtypeName = in.readString();
                NetworkInfo netInfo = new NetworkInfo(netType, subtype, typeName, subtypeName);
                netInfo.mState = State.valueOf(in.readString());
                netInfo.mDetailedState = DetailedState.valueOf(in.readString());
                netInfo.mIsFailover = in.readInt() != 0;
                netInfo.mIsAvailable = in.readInt() != 0;
                netInfo.mIsRoaming = in.readInt() != 0;
                netInfo.mIsConnectedToProvisioningNetwork = in.readInt() != 0;
                netInfo.mReason = in.readString();
                netInfo.mExtraInfo = in.readString();
                return netInfo;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public NetworkInfo[] newArray(int size) {
                return new NetworkInfo[size];
            }
        };
    }

    public NetworkInfo(int type) {
    }

    public NetworkInfo(int type, int subtype, String typeName, String subtypeName) {
        if (!ConnectivityManager.isNetworkTypeValid(type)) {
            throw new IllegalArgumentException("Invalid network type: " + type);
        }
        this.mNetworkType = type;
        this.mSubtype = subtype;
        this.mTypeName = typeName;
        this.mSubtypeName = subtypeName;
        setDetailedState(DetailedState.IDLE, null, null);
        this.mState = State.UNKNOWN;
        this.mIsAvailable = false;
        this.mIsRoaming = false;
        this.mIsConnectedToProvisioningNetwork = false;
    }

    public NetworkInfo(NetworkInfo source) {
        if (source != null) {
            this.mNetworkType = source.mNetworkType;
            this.mSubtype = source.mSubtype;
            this.mTypeName = source.mTypeName;
            this.mSubtypeName = source.mSubtypeName;
            this.mState = source.mState;
            this.mDetailedState = source.mDetailedState;
            this.mReason = source.mReason;
            this.mExtraInfo = source.mExtraInfo;
            this.mIsFailover = source.mIsFailover;
            this.mIsRoaming = source.mIsRoaming;
            this.mIsAvailable = source.mIsAvailable;
            this.mIsConnectedToProvisioningNetwork = source.mIsConnectedToProvisioningNetwork;
        }
    }

    public int getType() {
        int i;
        synchronized (this) {
            i = this.mNetworkType;
        }
        return i;
    }

    public int getSubtype() {
        int i;
        synchronized (this) {
            i = this.mSubtype;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSubtype(int subtype, String subtypeName) {
        synchronized (this) {
            this.mSubtype = subtype;
            this.mSubtypeName = subtypeName;
        }
    }

    public String getTypeName() {
        String str;
        synchronized (this) {
            str = this.mTypeName;
        }
        return str;
    }

    public String getSubtypeName() {
        String str;
        synchronized (this) {
            str = this.mSubtypeName;
        }
        return str;
    }

    public boolean isConnectedOrConnecting() {
        boolean z;
        synchronized (this) {
            z = this.mState == State.CONNECTED || this.mState == State.CONNECTING;
        }
        return z;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this) {
            z = this.mState == State.CONNECTED;
        }
        return z;
    }

    public boolean isAvailable() {
        boolean z;
        synchronized (this) {
            z = this.mIsAvailable;
        }
        return z;
    }

    public void setIsAvailable(boolean isAvailable) {
        synchronized (this) {
            this.mIsAvailable = isAvailable;
        }
    }

    public boolean isFailover() {
        boolean z;
        synchronized (this) {
            z = this.mIsFailover;
        }
        return z;
    }

    public void setFailover(boolean isFailover) {
        synchronized (this) {
            this.mIsFailover = isFailover;
        }
    }

    public boolean isRoaming() {
        boolean z;
        synchronized (this) {
            z = this.mIsRoaming;
        }
        return z;
    }

    public void setRoaming(boolean isRoaming) {
        synchronized (this) {
            this.mIsRoaming = isRoaming;
        }
    }

    public boolean isConnectedToProvisioningNetwork() {
        boolean z;
        synchronized (this) {
            z = this.mIsConnectedToProvisioningNetwork;
        }
        return z;
    }

    public void setIsConnectedToProvisioningNetwork(boolean val) {
        synchronized (this) {
            this.mIsConnectedToProvisioningNetwork = val;
        }
    }

    public State getState() {
        State state;
        synchronized (this) {
            state = this.mState;
        }
        return state;
    }

    public DetailedState getDetailedState() {
        DetailedState detailedState;
        synchronized (this) {
            detailedState = this.mDetailedState;
        }
        return detailedState;
    }

    public void setDetailedState(DetailedState detailedState, String reason, String extraInfo) {
        synchronized (this) {
            this.mDetailedState = detailedState;
            this.mState = stateMap.get(detailedState);
            this.mReason = reason;
            this.mExtraInfo = extraInfo;
        }
    }

    public void setExtraInfo(String extraInfo) {
        synchronized (this) {
            this.mExtraInfo = extraInfo;
        }
    }

    public String getReason() {
        String str;
        synchronized (this) {
            str = this.mReason;
        }
        return str;
    }

    public String getExtraInfo() {
        String str;
        synchronized (this) {
            str = this.mExtraInfo;
        }
        return str;
    }

    public String toString() {
        String sb;
        synchronized (this) {
            StringBuilder builder = new StringBuilder("NetworkInfo: ");
            builder.append("type: ").append(getTypeName()).append("[").append(getSubtypeName()).append("], state: ").append(this.mState).append(Separators.SLASH).append(this.mDetailedState).append(", reason: ").append(this.mReason == null ? "(unspecified)" : this.mReason).append(", extra: ").append(this.mExtraInfo == null ? "(none)" : this.mExtraInfo).append(", roaming: ").append(this.mIsRoaming).append(", failover: ").append(this.mIsFailover).append(", isAvailable: ").append(this.mIsAvailable).append(", isConnectedToProvisioningNetwork: ").append(this.mIsConnectedToProvisioningNetwork);
            sb = builder.toString();
        }
        return sb;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        synchronized (this) {
            dest.writeInt(this.mNetworkType);
            dest.writeInt(this.mSubtype);
            dest.writeString(this.mTypeName);
            dest.writeString(this.mSubtypeName);
            dest.writeString(this.mState.name());
            dest.writeString(this.mDetailedState.name());
            dest.writeInt(this.mIsFailover ? 1 : 0);
            dest.writeInt(this.mIsAvailable ? 1 : 0);
            dest.writeInt(this.mIsRoaming ? 1 : 0);
            dest.writeInt(this.mIsConnectedToProvisioningNetwork ? 1 : 0);
            dest.writeString(this.mReason);
            dest.writeString(this.mExtraInfo);
        }
    }
}