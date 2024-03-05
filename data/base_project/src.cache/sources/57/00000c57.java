package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: PrinterInfo.class */
public final class PrinterInfo implements Parcelable {
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_BUSY = 2;
    public static final int STATUS_UNAVAILABLE = 3;
    private PrinterId mId;
    private String mName;
    private int mStatus;
    private String mDescription;
    private PrinterCapabilitiesInfo mCapabilities;
    public static final Parcelable.Creator<PrinterInfo> CREATOR = new Parcelable.Creator<PrinterInfo>() { // from class: android.print.PrinterInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterInfo createFromParcel(Parcel parcel) {
            return new PrinterInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrinterInfo[] newArray(int size) {
            return new PrinterInfo[size];
        }
    };

    private PrinterInfo() {
    }

    private PrinterInfo(PrinterInfo prototype) {
        copyFrom(prototype);
    }

    public void copyFrom(PrinterInfo other) {
        if (this == other) {
            return;
        }
        this.mId = other.mId;
        this.mName = other.mName;
        this.mStatus = other.mStatus;
        this.mDescription = other.mDescription;
        if (other.mCapabilities != null) {
            if (this.mCapabilities != null) {
                this.mCapabilities.copyFrom(other.mCapabilities);
                return;
            } else {
                this.mCapabilities = new PrinterCapabilitiesInfo(other.mCapabilities);
                return;
            }
        }
        this.mCapabilities = null;
    }

    public PrinterId getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public PrinterCapabilitiesInfo getCapabilities() {
        return this.mCapabilities;
    }

    private PrinterInfo(Parcel parcel) {
        this.mId = (PrinterId) parcel.readParcelable(null);
        this.mName = parcel.readString();
        this.mStatus = parcel.readInt();
        this.mDescription = parcel.readString();
        this.mCapabilities = (PrinterCapabilitiesInfo) parcel.readParcelable(null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mId, flags);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mStatus);
        parcel.writeString(this.mDescription);
        parcel.writeParcelable(this.mCapabilities, flags);
    }

    public int hashCode() {
        int result = (31 * 1) + (this.mId != null ? this.mId.hashCode() : 0);
        return (31 * ((31 * ((31 * ((31 * result) + (this.mName != null ? this.mName.hashCode() : 0))) + this.mStatus)) + (this.mDescription != null ? this.mDescription.hashCode() : 0))) + (this.mCapabilities != null ? this.mCapabilities.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrinterInfo other = (PrinterInfo) obj;
        if (this.mId == null) {
            if (other.mId != null) {
                return false;
            }
        } else if (!this.mId.equals(other.mId)) {
            return false;
        }
        if (!TextUtils.equals(this.mName, other.mName) || this.mStatus != other.mStatus || !TextUtils.equals(this.mDescription, other.mDescription)) {
            return false;
        }
        if (this.mCapabilities == null) {
            if (other.mCapabilities != null) {
                return false;
            }
            return true;
        } else if (!this.mCapabilities.equals(other.mCapabilities)) {
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrinterInfo{");
        builder.append("id=").append(this.mId);
        builder.append(", name=").append(this.mName);
        builder.append(", status=").append(this.mStatus);
        builder.append(", description=").append(this.mDescription);
        builder.append(", capabilities=").append(this.mCapabilities);
        builder.append("\"}");
        return builder.toString();
    }

    /* loaded from: PrinterInfo$Builder.class */
    public static final class Builder {
        private final PrinterInfo mPrototype;

        public Builder(PrinterId printerId, String name, int status) {
            if (printerId == null) {
                throw new IllegalArgumentException("printerId cannot be null.");
            }
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name cannot be empty.");
            }
            if (!isValidStatus(status)) {
                throw new IllegalArgumentException("status is invalid.");
            }
            this.mPrototype = new PrinterInfo();
            this.mPrototype.mId = printerId;
            this.mPrototype.mName = name;
            this.mPrototype.mStatus = status;
        }

        public Builder(PrinterInfo other) {
            this.mPrototype = new PrinterInfo();
            this.mPrototype.copyFrom(other);
        }

        public Builder setStatus(int status) {
            this.mPrototype.mStatus = status;
            return this;
        }

        public Builder setName(String name) {
            this.mPrototype.mName = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.mPrototype.mDescription = description;
            return this;
        }

        public Builder setCapabilities(PrinterCapabilitiesInfo capabilities) {
            this.mPrototype.mCapabilities = capabilities;
            return this;
        }

        public PrinterInfo build() {
            return new PrinterInfo();
        }

        private boolean isValidStatus(int status) {
            return status == 1 || status == 2 || status == 3;
        }
    }
}