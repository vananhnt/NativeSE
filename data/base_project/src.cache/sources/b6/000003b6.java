package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Printer;
import gov.nist.core.Separators;

/* loaded from: ServiceInfo.class */
public class ServiceInfo extends ComponentInfo implements Parcelable {
    public String permission;
    public static final int FLAG_STOP_WITH_TASK = 1;
    public static final int FLAG_ISOLATED_PROCESS = 2;
    public static final int FLAG_SINGLE_USER = 1073741824;
    public int flags;
    public static final Parcelable.Creator<ServiceInfo> CREATOR = new Parcelable.Creator<ServiceInfo>() { // from class: android.content.pm.ServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceInfo createFromParcel(Parcel source) {
            return new ServiceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceInfo[] newArray(int size) {
            return new ServiceInfo[size];
        }
    };

    public ServiceInfo() {
    }

    public ServiceInfo(ServiceInfo orig) {
        super(orig);
        this.permission = orig.permission;
        this.flags = orig.flags;
    }

    public void dump(Printer pw, String prefix) {
        super.dumpFront(pw, prefix);
        pw.println(prefix + "permission=" + this.permission);
        pw.println(prefix + "flags=0x" + Integer.toHexString(this.flags));
    }

    public String toString() {
        return "ServiceInfo{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.name + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.pm.ComponentInfo, android.content.pm.PackageItemInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.permission);
        dest.writeInt(this.flags);
    }

    private ServiceInfo(Parcel source) {
        super(source);
        this.permission = source.readString();
        this.flags = source.readInt();
    }
}