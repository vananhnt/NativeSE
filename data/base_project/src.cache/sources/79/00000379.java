package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: InstrumentationInfo.class */
public class InstrumentationInfo extends PackageItemInfo implements Parcelable {
    public String targetPackage;
    public String sourceDir;
    public String publicSourceDir;
    public String dataDir;
    public String nativeLibraryDir;
    public boolean handleProfiling;
    public boolean functionalTest;
    public static final Parcelable.Creator<InstrumentationInfo> CREATOR = new Parcelable.Creator<InstrumentationInfo>() { // from class: android.content.pm.InstrumentationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InstrumentationInfo createFromParcel(Parcel source) {
            return new InstrumentationInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InstrumentationInfo[] newArray(int size) {
            return new InstrumentationInfo[size];
        }
    };

    public InstrumentationInfo() {
    }

    public InstrumentationInfo(InstrumentationInfo orig) {
        super(orig);
        this.targetPackage = orig.targetPackage;
        this.sourceDir = orig.sourceDir;
        this.publicSourceDir = orig.publicSourceDir;
        this.dataDir = orig.dataDir;
        this.nativeLibraryDir = orig.nativeLibraryDir;
        this.handleProfiling = orig.handleProfiling;
        this.functionalTest = orig.functionalTest;
    }

    public String toString() {
        return "InstrumentationInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.targetPackage);
        dest.writeString(this.sourceDir);
        dest.writeString(this.publicSourceDir);
        dest.writeString(this.dataDir);
        dest.writeString(this.nativeLibraryDir);
        dest.writeInt(!this.handleProfiling ? 0 : 1);
        dest.writeInt(!this.functionalTest ? 0 : 1);
    }

    private InstrumentationInfo(Parcel source) {
        super(source);
        this.targetPackage = source.readString();
        this.sourceDir = source.readString();
        this.publicSourceDir = source.readString();
        this.dataDir = source.readString();
        this.nativeLibraryDir = source.readString();
        this.handleProfiling = source.readInt() != 0;
        this.functionalTest = source.readInt() != 0;
    }
}