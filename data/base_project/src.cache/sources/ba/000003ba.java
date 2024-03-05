package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import gov.nist.core.Separators;

/* loaded from: UserInfo.class */
public class UserInfo implements Parcelable {
    public static final int FLAG_MASK_USER_TYPE = 63;
    public static final int FLAG_PRIMARY = 1;
    public static final int FLAG_ADMIN = 2;
    public static final int FLAG_GUEST = 4;
    public static final int FLAG_RESTRICTED = 8;
    public static final int FLAG_INITIALIZED = 16;
    public int id;
    public int serialNumber;
    public String name;
    public String iconPath;
    public int flags;
    public long creationTime;
    public long lastLoggedInTime;
    public boolean partial;
    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() { // from class: android.content.pm.UserInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public UserInfo(int id, String name, int flags) {
        this(id, name, null, flags);
    }

    public UserInfo(int id, String name, String iconPath, int flags) {
        this.id = id;
        this.name = name;
        this.flags = flags;
        this.iconPath = iconPath;
    }

    public boolean isPrimary() {
        return (this.flags & 1) == 1;
    }

    public boolean isAdmin() {
        return (this.flags & 2) == 2;
    }

    public boolean isGuest() {
        return (this.flags & 4) == 4;
    }

    public boolean isRestricted() {
        return (this.flags & 8) == 8;
    }

    public UserInfo() {
    }

    public UserInfo(UserInfo orig) {
        this.name = orig.name;
        this.iconPath = orig.iconPath;
        this.id = orig.id;
        this.flags = orig.flags;
        this.serialNumber = orig.serialNumber;
        this.creationTime = orig.creationTime;
        this.lastLoggedInTime = orig.lastLoggedInTime;
        this.partial = orig.partial;
    }

    public UserHandle getUserHandle() {
        return new UserHandle(this.id);
    }

    public String toString() {
        return "UserInfo{" + this.id + Separators.COLON + this.name + Separators.COLON + Integer.toHexString(this.flags) + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.iconPath);
        dest.writeInt(this.flags);
        dest.writeInt(this.serialNumber);
        dest.writeLong(this.creationTime);
        dest.writeLong(this.lastLoggedInTime);
        dest.writeInt(this.partial ? 1 : 0);
    }

    private UserInfo(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.iconPath = source.readString();
        this.flags = source.readInt();
        this.serialNumber = source.readInt();
        this.creationTime = source.readLong();
        this.lastLoggedInTime = source.readLong();
        this.partial = source.readInt() != 0;
    }
}