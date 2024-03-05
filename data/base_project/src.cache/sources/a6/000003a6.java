package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;

/* loaded from: PermissionGroupInfo.class */
public class PermissionGroupInfo extends PackageItemInfo implements Parcelable {
    public int descriptionRes;
    public CharSequence nonLocalizedDescription;
    public static final int FLAG_PERSONAL_INFO = 1;
    public int flags;
    public int priority;
    public static final Parcelable.Creator<PermissionGroupInfo> CREATOR = new Parcelable.Creator<PermissionGroupInfo>() { // from class: android.content.pm.PermissionGroupInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionGroupInfo createFromParcel(Parcel source) {
            return new PermissionGroupInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionGroupInfo[] newArray(int size) {
            return new PermissionGroupInfo[size];
        }
    };

    public PermissionGroupInfo() {
    }

    public PermissionGroupInfo(PermissionGroupInfo orig) {
        super(orig);
        this.descriptionRes = orig.descriptionRes;
        this.nonLocalizedDescription = orig.nonLocalizedDescription;
        this.flags = orig.flags;
        this.priority = orig.priority;
    }

    public CharSequence loadDescription(PackageManager pm) {
        CharSequence label;
        if (this.nonLocalizedDescription != null) {
            return this.nonLocalizedDescription;
        }
        if (this.descriptionRes != 0 && (label = pm.getText(this.packageName, this.descriptionRes, null)) != null) {
            return label;
        }
        return null;
    }

    public String toString() {
        return "PermissionGroupInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + " flgs=0x" + Integer.toHexString(this.flags) + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.descriptionRes);
        TextUtils.writeToParcel(this.nonLocalizedDescription, dest, parcelableFlags);
        dest.writeInt(this.flags);
        dest.writeInt(this.priority);
    }

    private PermissionGroupInfo(Parcel source) {
        super(source);
        this.descriptionRes = source.readInt();
        this.nonLocalizedDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.flags = source.readInt();
        this.priority = source.readInt();
    }
}