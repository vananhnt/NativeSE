package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.UUID;

/* loaded from: PrintJobId.class */
public final class PrintJobId implements Parcelable {
    private final String mValue;
    public static final Parcelable.Creator<PrintJobId> CREATOR = new Parcelable.Creator<PrintJobId>() { // from class: android.print.PrintJobId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobId createFromParcel(Parcel parcel) {
            return new PrintJobId(parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobId[] newArray(int size) {
            return new PrintJobId[size];
        }
    };

    public PrintJobId() {
        this(UUID.randomUUID().toString());
    }

    public PrintJobId(String value) {
        this.mValue = value;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.mValue != null ? this.mValue.hashCode() : 0);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintJobId other = (PrintJobId) obj;
        if (!TextUtils.equals(this.mValue, other.mValue)) {
            return false;
        }
        return true;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mValue);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String flattenToString() {
        return this.mValue;
    }

    public static PrintJobId unflattenFromString(String string) {
        return new PrintJobId(string);
    }
}