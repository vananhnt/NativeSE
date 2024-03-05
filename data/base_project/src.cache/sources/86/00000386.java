package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: PackageInfoLite.class */
public class PackageInfoLite implements Parcelable {
    public String packageName;
    public int versionCode;
    public int recommendedInstallLocation;
    public int installLocation;
    public VerifierInfo[] verifiers;
    public static final Parcelable.Creator<PackageInfoLite> CREATOR = new Parcelable.Creator<PackageInfoLite>() { // from class: android.content.pm.PackageInfoLite.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfoLite createFromParcel(Parcel source) {
            return new PackageInfoLite(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfoLite[] newArray(int size) {
            return new PackageInfoLite[size];
        }
    };

    public PackageInfoLite() {
    }

    public String toString() {
        return "PackageInfoLite{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.packageName);
        dest.writeInt(this.versionCode);
        dest.writeInt(this.recommendedInstallLocation);
        dest.writeInt(this.installLocation);
        if (this.verifiers == null || this.verifiers.length == 0) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(this.verifiers.length);
        dest.writeTypedArray(this.verifiers, parcelableFlags);
    }

    private PackageInfoLite(Parcel source) {
        this.packageName = source.readString();
        this.versionCode = source.readInt();
        this.recommendedInstallLocation = source.readInt();
        this.installLocation = source.readInt();
        int verifiersLength = source.readInt();
        if (verifiersLength == 0) {
            this.verifiers = new VerifierInfo[0];
            return;
        }
        this.verifiers = new VerifierInfo[verifiersLength];
        source.readTypedArray(this.verifiers, VerifierInfo.CREATOR);
    }
}