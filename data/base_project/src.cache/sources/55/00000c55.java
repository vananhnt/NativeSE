package android.print;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: PrinterId.class */
public final class PrinterId implements Parcelable {
    private final ComponentName mServiceName;
    private final String mLocalId;
    public static final Parcelable.Creator<PrinterId> CREATOR = new Parcelable.Creator<PrinterId>() { // from class: android.print.PrinterId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterId createFromParcel(Parcel parcel) {
            return new PrinterId(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterId[] newArray(int size) {
            return new PrinterId[size];
        }
    };

    public PrinterId(ComponentName serviceName, String localId) {
        this.mServiceName = serviceName;
        this.mLocalId = localId;
    }

    private PrinterId(Parcel parcel) {
        this.mServiceName = (ComponentName) parcel.readParcelable(null);
        this.mLocalId = parcel.readString();
    }

    public ComponentName getServiceName() {
        return this.mServiceName;
    }

    public String getLocalId() {
        return this.mLocalId;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mServiceName, flags);
        parcel.writeString(this.mLocalId);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PrinterId other = (PrinterId) object;
        if (this.mServiceName == null) {
            if (other.mServiceName != null) {
                return false;
            }
        } else if (!this.mServiceName.equals(other.mServiceName)) {
            return false;
        }
        if (!TextUtils.equals(this.mLocalId, other.mLocalId)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = (31 * 1) + (this.mServiceName != null ? this.mServiceName.hashCode() : 1);
        return (31 * hashCode) + this.mLocalId.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrinterId{");
        builder.append("serviceName=").append(this.mServiceName.flattenToString());
        builder.append(", localId=").append(this.mLocalId);
        builder.append('}');
        return builder.toString();
    }
}