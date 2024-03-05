package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: PackageInfo.class */
public class PackageInfo implements Parcelable {
    public String packageName;
    public int versionCode;
    public String versionName;
    public String sharedUserId;
    public int sharedUserLabel;
    public ApplicationInfo applicationInfo;
    public long firstInstallTime;
    public long lastUpdateTime;
    public int[] gids;
    public ActivityInfo[] activities;
    public ActivityInfo[] receivers;
    public ServiceInfo[] services;
    public ProviderInfo[] providers;
    public InstrumentationInfo[] instrumentation;
    public PermissionInfo[] permissions;
    public String[] requestedPermissions;
    public int[] requestedPermissionsFlags;
    public static final int REQUESTED_PERMISSION_REQUIRED = 1;
    public static final int REQUESTED_PERMISSION_GRANTED = 2;
    public Signature[] signatures;
    public ConfigurationInfo[] configPreferences;
    public FeatureInfo[] reqFeatures;
    public static final int INSTALL_LOCATION_UNSPECIFIED = -1;
    public static final int INSTALL_LOCATION_AUTO = 0;
    public static final int INSTALL_LOCATION_INTERNAL_ONLY = 1;
    public static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;
    public int installLocation;
    public boolean requiredForAllUsers;
    public String restrictedAccountType;
    public String requiredAccountType;
    public static final Parcelable.Creator<PackageInfo> CREATOR = new Parcelable.Creator<PackageInfo>() { // from class: android.content.pm.PackageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfo createFromParcel(Parcel source) {
            return new PackageInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfo[] newArray(int size) {
            return new PackageInfo[size];
        }
    };

    public PackageInfo() {
        this.installLocation = 1;
    }

    public String toString() {
        return "PackageInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.packageName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeString(this.sharedUserId);
        dest.writeInt(this.sharedUserLabel);
        if (this.applicationInfo != null) {
            dest.writeInt(1);
            this.applicationInfo.writeToParcel(dest, parcelableFlags);
        } else {
            dest.writeInt(0);
        }
        dest.writeLong(this.firstInstallTime);
        dest.writeLong(this.lastUpdateTime);
        dest.writeIntArray(this.gids);
        dest.writeTypedArray(this.activities, parcelableFlags);
        dest.writeTypedArray(this.receivers, parcelableFlags);
        dest.writeTypedArray(this.services, parcelableFlags);
        dest.writeTypedArray(this.providers, parcelableFlags);
        dest.writeTypedArray(this.instrumentation, parcelableFlags);
        dest.writeTypedArray(this.permissions, parcelableFlags);
        dest.writeStringArray(this.requestedPermissions);
        dest.writeIntArray(this.requestedPermissionsFlags);
        dest.writeTypedArray(this.signatures, parcelableFlags);
        dest.writeTypedArray(this.configPreferences, parcelableFlags);
        dest.writeTypedArray(this.reqFeatures, parcelableFlags);
        dest.writeInt(this.installLocation);
        dest.writeInt(this.requiredForAllUsers ? 1 : 0);
        dest.writeString(this.restrictedAccountType);
        dest.writeString(this.requiredAccountType);
    }

    private PackageInfo(Parcel source) {
        this.installLocation = 1;
        this.packageName = source.readString();
        this.versionCode = source.readInt();
        this.versionName = source.readString();
        this.sharedUserId = source.readString();
        this.sharedUserLabel = source.readInt();
        int hasApp = source.readInt();
        if (hasApp != 0) {
            this.applicationInfo = ApplicationInfo.CREATOR.createFromParcel(source);
        }
        this.firstInstallTime = source.readLong();
        this.lastUpdateTime = source.readLong();
        this.gids = source.createIntArray();
        this.activities = (ActivityInfo[]) source.createTypedArray(ActivityInfo.CREATOR);
        this.receivers = (ActivityInfo[]) source.createTypedArray(ActivityInfo.CREATOR);
        this.services = (ServiceInfo[]) source.createTypedArray(ServiceInfo.CREATOR);
        this.providers = (ProviderInfo[]) source.createTypedArray(ProviderInfo.CREATOR);
        this.instrumentation = (InstrumentationInfo[]) source.createTypedArray(InstrumentationInfo.CREATOR);
        this.permissions = (PermissionInfo[]) source.createTypedArray(PermissionInfo.CREATOR);
        this.requestedPermissions = source.createStringArray();
        this.requestedPermissionsFlags = source.createIntArray();
        this.signatures = (Signature[]) source.createTypedArray(Signature.CREATOR);
        this.configPreferences = (ConfigurationInfo[]) source.createTypedArray(ConfigurationInfo.CREATOR);
        this.reqFeatures = (FeatureInfo[]) source.createTypedArray(FeatureInfo.CREATOR);
        this.installLocation = source.readInt();
        this.requiredForAllUsers = source.readInt() != 0;
        this.restrictedAccountType = source.readString();
        this.requiredAccountType = source.readString();
    }
}