package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: PackageCleanItem.class */
public class PackageCleanItem {
    public final int userId;
    public final String packageName;
    public final boolean andCode;
    public static final Parcelable.Creator<PackageCleanItem> CREATOR = new Parcelable.Creator<PackageCleanItem>() { // from class: android.content.pm.PackageCleanItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageCleanItem createFromParcel(Parcel source) {
            return new PackageCleanItem(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageCleanItem[] newArray(int size) {
            return new PackageCleanItem[size];
        }
    };

    public PackageCleanItem(int userId, String packageName, boolean andCode) {
        this.userId = userId;
        this.packageName = packageName;
        this.andCode = andCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            try {
                PackageCleanItem other = (PackageCleanItem) obj;
                if (this.userId == other.userId && this.packageName.equals(other.packageName)) {
                    if (this.andCode == other.andCode) {
                        return true;
                    }
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = (31 * 17) + this.userId;
        return (31 * ((31 * result) + this.packageName.hashCode())) + (this.andCode ? 1 : 0);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.userId);
        dest.writeString(this.packageName);
        dest.writeInt(this.andCode ? 1 : 0);
    }

    private PackageCleanItem(Parcel source) {
        this.userId = source.readInt();
        this.packageName = source.readString();
        this.andCode = source.readInt() != 0;
    }
}