package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: FeatureInfo.class */
public class FeatureInfo implements Parcelable {
    public String name;
    public static final int GL_ES_VERSION_UNDEFINED = 0;
    public int reqGlEsVersion;
    public static final int FLAG_REQUIRED = 1;
    public int flags;
    public static final Parcelable.Creator<FeatureInfo> CREATOR = new Parcelable.Creator<FeatureInfo>() { // from class: android.content.pm.FeatureInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FeatureInfo createFromParcel(Parcel source) {
            return new FeatureInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FeatureInfo[] newArray(int size) {
            return new FeatureInfo[size];
        }
    };

    public FeatureInfo() {
    }

    public FeatureInfo(FeatureInfo orig) {
        this.name = orig.name;
        this.reqGlEsVersion = orig.reqGlEsVersion;
        this.flags = orig.flags;
    }

    public String toString() {
        if (this.name != null) {
            return "FeatureInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + " fl=0x" + Integer.toHexString(this.flags) + "}";
        }
        return "FeatureInfo{" + Integer.toHexString(System.identityHashCode(this)) + " glEsVers=" + getGlEsVersion() + " fl=0x" + Integer.toHexString(this.flags) + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.name);
        dest.writeInt(this.reqGlEsVersion);
        dest.writeInt(this.flags);
    }

    private FeatureInfo(Parcel source) {
        this.name = source.readString();
        this.reqGlEsVersion = source.readInt();
        this.flags = source.readInt();
    }

    public String getGlEsVersion() {
        int major = (this.reqGlEsVersion & (-65536)) >> 16;
        int minor = this.reqGlEsVersion & 65535;
        return String.valueOf(major) + Separators.DOT + String.valueOf(minor);
    }
}