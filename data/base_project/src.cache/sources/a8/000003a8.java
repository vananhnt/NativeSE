package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;

/* loaded from: PermissionInfo.class */
public class PermissionInfo extends PackageItemInfo implements Parcelable {
    public static final int PROTECTION_NORMAL = 0;
    public static final int PROTECTION_DANGEROUS = 1;
    public static final int PROTECTION_SIGNATURE = 2;
    public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;
    public static final int PROTECTION_FLAG_SYSTEM = 16;
    public static final int PROTECTION_FLAG_DEVELOPMENT = 32;
    public static final int PROTECTION_MASK_BASE = 15;
    public static final int PROTECTION_MASK_FLAGS = 240;
    public int protectionLevel;
    public String group;
    public static final int FLAG_COSTS_MONEY = 1;
    public int flags;
    public int descriptionRes;
    public CharSequence nonLocalizedDescription;
    public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator<PermissionInfo>() { // from class: android.content.pm.PermissionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo createFromParcel(Parcel source) {
            return new PermissionInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionInfo[] newArray(int size) {
            return new PermissionInfo[size];
        }
    };

    public static int fixProtectionLevel(int level) {
        if (level == 3) {
            level = 18;
        }
        return level;
    }

    public static String protectionToString(int level) {
        String protLevel = "????";
        switch (level & 15) {
            case 0:
                protLevel = "normal";
                break;
            case 1:
                protLevel = "dangerous";
                break;
            case 2:
                protLevel = "signature";
                break;
            case 3:
                protLevel = "signatureOrSystem";
                break;
        }
        if ((level & 16) != 0) {
            protLevel = protLevel + "|system";
        }
        if ((level & 32) != 0) {
            protLevel = protLevel + "|development";
        }
        return protLevel;
    }

    public PermissionInfo() {
    }

    public PermissionInfo(PermissionInfo orig) {
        super(orig);
        this.protectionLevel = orig.protectionLevel;
        this.flags = orig.flags;
        this.group = orig.group;
        this.descriptionRes = orig.descriptionRes;
        this.nonLocalizedDescription = orig.nonLocalizedDescription;
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
        return "PermissionInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeInt(this.protectionLevel);
        dest.writeInt(this.flags);
        dest.writeString(this.group);
        dest.writeInt(this.descriptionRes);
        TextUtils.writeToParcel(this.nonLocalizedDescription, dest, parcelableFlags);
    }

    private PermissionInfo(Parcel source) {
        super(source);
        this.protectionLevel = source.readInt();
        this.flags = source.readInt();
        this.group = source.readString();
        this.descriptionRes = source.readInt();
        this.nonLocalizedDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }
}