package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: InterfaceConfiguration.class */
public class InterfaceConfiguration implements Parcelable {
    private String mHwAddr;
    private LinkAddress mAddr;
    private HashSet<String> mFlags = Sets.newHashSet();
    private static final String FLAG_UP = "up";
    private static final String FLAG_DOWN = "down";
    public static final Parcelable.Creator<InterfaceConfiguration> CREATOR = new Parcelable.Creator<InterfaceConfiguration>() { // from class: android.net.InterfaceConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InterfaceConfiguration createFromParcel(Parcel in) {
            InterfaceConfiguration info = new InterfaceConfiguration();
            info.mHwAddr = in.readString();
            if (in.readByte() == 1) {
                info.mAddr = (LinkAddress) in.readParcelable(null);
            }
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                info.mFlags.add(in.readString());
            }
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InterfaceConfiguration[] newArray(int size) {
            return new InterfaceConfiguration[size];
        }
    };

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("mHwAddr=").append(this.mHwAddr);
        builder.append(" mAddr=").append(String.valueOf(this.mAddr));
        builder.append(" mFlags=").append(getFlags());
        return builder.toString();
    }

    public Iterable<String> getFlags() {
        return this.mFlags;
    }

    public boolean hasFlag(String flag) {
        validateFlag(flag);
        return this.mFlags.contains(flag);
    }

    public void clearFlag(String flag) {
        validateFlag(flag);
        this.mFlags.remove(flag);
    }

    public void setFlag(String flag) {
        validateFlag(flag);
        this.mFlags.add(flag);
    }

    public void setInterfaceUp() {
        this.mFlags.remove(FLAG_DOWN);
        this.mFlags.add(FLAG_UP);
    }

    public void setInterfaceDown() {
        this.mFlags.remove(FLAG_UP);
        this.mFlags.add(FLAG_DOWN);
    }

    public LinkAddress getLinkAddress() {
        return this.mAddr;
    }

    public void setLinkAddress(LinkAddress addr) {
        this.mAddr = addr;
    }

    public String getHardwareAddress() {
        return this.mHwAddr;
    }

    public void setHardwareAddress(String hwAddr) {
        this.mHwAddr = hwAddr;
    }

    public boolean isActive() {
        try {
            if (hasFlag(FLAG_UP)) {
                byte[] arr$ = this.mAddr.getAddress().getAddress();
                for (byte b : arr$) {
                    if (b != 0) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mHwAddr);
        if (this.mAddr != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(this.mAddr, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mFlags.size());
        Iterator i$ = this.mFlags.iterator();
        while (i$.hasNext()) {
            String flag = i$.next();
            dest.writeString(flag);
        }
    }

    private static void validateFlag(String flag) {
        if (flag.indexOf(32) >= 0) {
            throw new IllegalArgumentException("flag contains space: " + flag);
        }
    }
}