package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: BluetoothHealthAppConfiguration.class */
public final class BluetoothHealthAppConfiguration implements Parcelable {
    private final String mName;
    private final int mDataType;
    private final int mRole;
    private final int mChannelType;
    public static final Parcelable.Creator<BluetoothHealthAppConfiguration> CREATOR = new Parcelable.Creator<BluetoothHealthAppConfiguration>() { // from class: android.bluetooth.BluetoothHealthAppConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothHealthAppConfiguration createFromParcel(Parcel in) {
            String name = in.readString();
            int type = in.readInt();
            int role = in.readInt();
            int channelType = in.readInt();
            return new BluetoothHealthAppConfiguration(name, type, role, channelType);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothHealthAppConfiguration[] newArray(int size) {
            return new BluetoothHealthAppConfiguration[size];
        }
    };

    BluetoothHealthAppConfiguration(String name, int dataType) {
        this.mName = name;
        this.mDataType = dataType;
        this.mRole = 2;
        this.mChannelType = 12;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothHealthAppConfiguration(String name, int dataType, int role, int channelType) {
        this.mName = name;
        this.mDataType = dataType;
        this.mRole = role;
        this.mChannelType = channelType;
    }

    public boolean equals(Object o) {
        if (o instanceof BluetoothHealthAppConfiguration) {
            BluetoothHealthAppConfiguration config = (BluetoothHealthAppConfiguration) o;
            return this.mName.equals(config.getName()) && this.mDataType == config.getDataType() && this.mRole == config.getRole() && this.mChannelType == config.getChannelType();
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 17) + (this.mName != null ? this.mName.hashCode() : 0);
        return (31 * ((31 * ((31 * result) + this.mDataType)) + this.mRole)) + this.mChannelType;
    }

    public String toString() {
        return "BluetoothHealthAppConfiguration [mName = " + this.mName + ",mDataType = " + this.mDataType + ", mRole = " + this.mRole + ",mChannelType = " + this.mChannelType + "]";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getDataType() {
        return this.mDataType;
    }

    public String getName() {
        return this.mName;
    }

    public int getRole() {
        return this.mRole;
    }

    public int getChannelType() {
        return this.mChannelType;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mName);
        out.writeInt(this.mDataType);
        out.writeInt(this.mRole);
        out.writeInt(this.mChannelType);
    }
}