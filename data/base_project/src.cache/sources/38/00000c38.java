package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: PrintDocumentInfo.class */
public final class PrintDocumentInfo implements Parcelable {
    public static final int PAGE_COUNT_UNKNOWN = -1;
    public static final int CONTENT_TYPE_UNKNOWN = -1;
    public static final int CONTENT_TYPE_DOCUMENT = 0;
    public static final int CONTENT_TYPE_PHOTO = 1;
    private String mName;
    private int mPageCount;
    private int mContentType;
    private long mDataSize;
    public static final Parcelable.Creator<PrintDocumentInfo> CREATOR = new Parcelable.Creator<PrintDocumentInfo>() { // from class: android.print.PrintDocumentInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintDocumentInfo createFromParcel(Parcel parcel) {
            return new PrintDocumentInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintDocumentInfo[] newArray(int size) {
            return new PrintDocumentInfo[size];
        }
    };

    private PrintDocumentInfo() {
    }

    private PrintDocumentInfo(PrintDocumentInfo prototype) {
        this.mName = prototype.mName;
        this.mPageCount = prototype.mPageCount;
        this.mContentType = prototype.mContentType;
        this.mDataSize = prototype.mDataSize;
    }

    private PrintDocumentInfo(Parcel parcel) {
        this.mName = parcel.readString();
        this.mPageCount = parcel.readInt();
        this.mContentType = parcel.readInt();
        this.mDataSize = parcel.readLong();
    }

    public String getName() {
        return this.mName;
    }

    public int getPageCount() {
        return this.mPageCount;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public long getDataSize() {
        return this.mDataSize;
    }

    public void setDataSize(long dataSize) {
        this.mDataSize = dataSize;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mName);
        parcel.writeInt(this.mPageCount);
        parcel.writeInt(this.mContentType);
        parcel.writeLong(this.mDataSize);
    }

    public int hashCode() {
        int result = (31 * 1) + (this.mName != null ? this.mName.hashCode() : 0);
        return ((31 * ((31 * ((31 * ((31 * result) + this.mContentType)) + this.mPageCount)) + ((int) this.mDataSize))) + ((int) this.mDataSize)) >> 32;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintDocumentInfo other = (PrintDocumentInfo) obj;
        if (!TextUtils.equals(this.mName, other.mName) || this.mContentType != other.mContentType || this.mPageCount != other.mPageCount || this.mDataSize != other.mDataSize) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintDocumentInfo{");
        builder.append("name=").append(this.mName);
        builder.append(", pageCount=").append(this.mPageCount);
        builder.append(", contentType=").append(contentTyepToString(this.mContentType));
        builder.append(", dataSize=").append(this.mDataSize);
        builder.append("}");
        return builder.toString();
    }

    private String contentTyepToString(int contentType) {
        switch (contentType) {
            case 0:
                return "CONTENT_TYPE_DOCUMENT";
            case 1:
                return "CONTENT_TYPE_PHOTO";
            default:
                return "CONTENT_TYPE_UNKNOWN";
        }
    }

    /* loaded from: PrintDocumentInfo$Builder.class */
    public static final class Builder {
        private final PrintDocumentInfo mPrototype;

        public Builder(String name) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name cannot be empty");
            }
            this.mPrototype = new PrintDocumentInfo();
            this.mPrototype.mName = name;
        }

        public Builder setPageCount(int pageCount) {
            if (pageCount >= 0 || pageCount == -1) {
                this.mPrototype.mPageCount = pageCount;
                return this;
            }
            throw new IllegalArgumentException("pageCount must be greater than or euqal to zero or DocumentInfo#PAGE_COUNT_UNKNOWN");
        }

        public Builder setContentType(int type) {
            this.mPrototype.mContentType = type;
            return this;
        }

        public PrintDocumentInfo build() {
            return new PrintDocumentInfo();
        }
    }
}