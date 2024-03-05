package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: WifiDisplaySessionInfo.class */
public final class WifiDisplaySessionInfo implements Parcelable {
    private final boolean mClient;
    private final int mSessionId;
    private final String mGroupId;
    private final String mPassphrase;
    private final String mIP;
    public static final Parcelable.Creator<WifiDisplaySessionInfo> CREATOR = new Parcelable.Creator<WifiDisplaySessionInfo>() { // from class: android.hardware.display.WifiDisplaySessionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDisplaySessionInfo createFromParcel(Parcel in) {
            boolean client = in.readInt() != 0;
            int session = in.readInt();
            String group = in.readString();
            String pp = in.readString();
            String ip = in.readString();
            return new WifiDisplaySessionInfo(client, session, group, pp, ip);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDisplaySessionInfo[] newArray(int size) {
            return new WifiDisplaySessionInfo[size];
        }
    };

    public WifiDisplaySessionInfo() {
        this(true, 0, "", "", "");
    }

    public WifiDisplaySessionInfo(boolean client, int session, String group, String pp, String ip) {
        this.mClient = client;
        this.mSessionId = session;
        this.mGroupId = group;
        this.mPassphrase = pp;
        this.mIP = ip;
    }

    public boolean isClient() {
        return this.mClient;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public String getGroupId() {
        return this.mGroupId;
    }

    public String getPassphrase() {
        return this.mPassphrase;
    }

    public String getIP() {
        return this.mIP;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mClient ? 1 : 0);
        dest.writeInt(this.mSessionId);
        dest.writeString(this.mGroupId);
        dest.writeString(this.mPassphrase);
        dest.writeString(this.mIP);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "WifiDisplaySessionInfo:\n    Client/Owner: " + (this.mClient ? "Client" : "Owner") + "\n    GroupId: " + this.mGroupId + "\n    Passphrase: " + this.mPassphrase + "\n    SessionId: " + this.mSessionId + "\n    IP Address: " + this.mIP;
    }
}