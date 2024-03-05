package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* loaded from: PrintJobInfo.class */
public final class PrintJobInfo implements Parcelable {
    public static final int STATE_ANY = -1;
    public static final int STATE_ANY_VISIBLE_TO_CLIENTS = -2;
    public static final int STATE_ANY_ACTIVE = -3;
    public static final int STATE_ANY_SCHEDULED = -4;
    public static final int STATE_CREATED = 1;
    public static final int STATE_QUEUED = 2;
    public static final int STATE_STARTED = 3;
    public static final int STATE_BLOCKED = 4;
    public static final int STATE_COMPLETED = 5;
    public static final int STATE_FAILED = 6;
    public static final int STATE_CANCELED = 7;
    private PrintJobId mId;
    private String mLabel;
    private PrinterId mPrinterId;
    private String mPrinterName;
    private int mState;
    private int mAppId;
    private String mTag;
    private long mCreationTime;
    private int mCopies;
    private String mStateReason;
    private PageRange[] mPageRanges;
    private PrintAttributes mAttributes;
    private PrintDocumentInfo mDocumentInfo;
    private boolean mCanceling;
    public static final Parcelable.Creator<PrintJobInfo> CREATOR = new Parcelable.Creator<PrintJobInfo>() { // from class: android.print.PrintJobInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo createFromParcel(Parcel parcel) {
            return new PrintJobInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintJobInfo[] newArray(int size) {
            return new PrintJobInfo[size];
        }
    };

    public PrintJobInfo() {
    }

    public PrintJobInfo(PrintJobInfo other) {
        this.mId = other.mId;
        this.mLabel = other.mLabel;
        this.mPrinterId = other.mPrinterId;
        this.mPrinterName = other.mPrinterName;
        this.mState = other.mState;
        this.mAppId = other.mAppId;
        this.mTag = other.mTag;
        this.mCreationTime = other.mCreationTime;
        this.mCopies = other.mCopies;
        this.mStateReason = other.mStateReason;
        this.mPageRanges = other.mPageRanges;
        this.mAttributes = other.mAttributes;
        this.mDocumentInfo = other.mDocumentInfo;
        this.mCanceling = other.mCanceling;
    }

    private PrintJobInfo(Parcel parcel) {
        this.mId = (PrintJobId) parcel.readParcelable(null);
        this.mLabel = parcel.readString();
        this.mPrinterId = (PrinterId) parcel.readParcelable(null);
        this.mPrinterName = parcel.readString();
        this.mState = parcel.readInt();
        this.mAppId = parcel.readInt();
        this.mTag = parcel.readString();
        this.mCreationTime = parcel.readLong();
        this.mCopies = parcel.readInt();
        this.mStateReason = parcel.readString();
        if (parcel.readInt() == 1) {
            Parcelable[] parcelables = parcel.readParcelableArray(null);
            this.mPageRanges = new PageRange[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                this.mPageRanges[i] = (PageRange) parcelables[i];
            }
        }
        if (parcel.readInt() == 1) {
            this.mAttributes = PrintAttributes.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() == 1) {
            this.mDocumentInfo = PrintDocumentInfo.CREATOR.createFromParcel(parcel);
        }
        this.mCanceling = parcel.readInt() == 1;
    }

    public PrintJobId getId() {
        return this.mId;
    }

    public void setId(PrintJobId id) {
        this.mId = id;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public PrinterId getPrinterId() {
        return this.mPrinterId;
    }

    public void setPrinterId(PrinterId printerId) {
        this.mPrinterId = printerId;
    }

    public String getPrinterName() {
        return this.mPrinterName;
    }

    public void setPrinterName(String printerName) {
        this.mPrinterName = printerName;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public int getAppId() {
        return this.mAppId;
    }

    public void setAppId(int appId) {
        this.mAppId = appId;
    }

    public String getTag() {
        return this.mTag;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    public long getCreationTime() {
        return this.mCreationTime;
    }

    public void setCreationTime(long creationTime) {
        if (creationTime < 0) {
            throw new IllegalArgumentException("creationTime must be non-negative.");
        }
        this.mCreationTime = creationTime;
    }

    public int getCopies() {
        return this.mCopies;
    }

    public void setCopies(int copyCount) {
        if (copyCount < 1) {
            throw new IllegalArgumentException("Copies must be more than one.");
        }
        this.mCopies = copyCount;
    }

    public String getStateReason() {
        return this.mStateReason;
    }

    public void setStateReason(String stateReason) {
        this.mStateReason = stateReason;
    }

    public PageRange[] getPages() {
        return this.mPageRanges;
    }

    public void setPages(PageRange[] pageRanges) {
        this.mPageRanges = pageRanges;
    }

    public PrintAttributes getAttributes() {
        return this.mAttributes;
    }

    public void setAttributes(PrintAttributes attributes) {
        this.mAttributes = attributes;
    }

    public PrintDocumentInfo getDocumentInfo() {
        return this.mDocumentInfo;
    }

    public void setDocumentInfo(PrintDocumentInfo info) {
        this.mDocumentInfo = info;
    }

    public boolean isCancelling() {
        return this.mCanceling;
    }

    public void setCancelling(boolean cancelling) {
        this.mCanceling = cancelling;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mId, flags);
        parcel.writeString(this.mLabel);
        parcel.writeParcelable(this.mPrinterId, flags);
        parcel.writeString(this.mPrinterName);
        parcel.writeInt(this.mState);
        parcel.writeInt(this.mAppId);
        parcel.writeString(this.mTag);
        parcel.writeLong(this.mCreationTime);
        parcel.writeInt(this.mCopies);
        parcel.writeString(this.mStateReason);
        if (this.mPageRanges != null) {
            parcel.writeInt(1);
            parcel.writeParcelableArray(this.mPageRanges, flags);
        } else {
            parcel.writeInt(0);
        }
        if (this.mAttributes != null) {
            parcel.writeInt(1);
            this.mAttributes.writeToParcel(parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        if (this.mDocumentInfo != null) {
            parcel.writeInt(1);
            this.mDocumentInfo.writeToParcel(parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mCanceling ? 1 : 0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintJobInfo{");
        builder.append("label: ").append(this.mLabel);
        builder.append(", id: ").append(this.mId);
        builder.append(", state: ").append(stateToString(this.mState));
        builder.append(", printer: " + this.mPrinterId);
        builder.append(", tag: ").append(this.mTag);
        builder.append(", creationTime: " + this.mCreationTime);
        builder.append(", copies: ").append(this.mCopies);
        builder.append(", attributes: " + (this.mAttributes != null ? this.mAttributes.toString() : null));
        builder.append(", documentInfo: " + (this.mDocumentInfo != null ? this.mDocumentInfo.toString() : null));
        builder.append(", cancelling: " + this.mCanceling);
        builder.append(", pages: " + (this.mPageRanges != null ? Arrays.toString(this.mPageRanges) : null));
        builder.append("}");
        return builder.toString();
    }

    public static String stateToString(int state) {
        switch (state) {
            case 1:
                return "STATE_CREATED";
            case 2:
                return "STATE_QUEUED";
            case 3:
                return "STATE_STARTED";
            case 4:
                return "STATE_BLOCKED";
            case 5:
                return "STATE_COMPLETED";
            case 6:
                return "STATE_FAILED";
            case 7:
                return "STATE_CANCELED";
            default:
                return "STATE_UNKNOWN";
        }
    }

    /* loaded from: PrintJobInfo$Builder.class */
    public static final class Builder {
        private final PrintJobInfo mPrototype;

        public Builder(PrintJobInfo prototype) {
            this.mPrototype = prototype != null ? new PrintJobInfo(prototype) : new PrintJobInfo();
        }

        public void setCopies(int copies) {
            this.mPrototype.mCopies = copies;
        }

        public void setAttributes(PrintAttributes attributes) {
            this.mPrototype.mAttributes = attributes;
        }

        public void setPages(PageRange[] pages) {
            this.mPrototype.mPageRanges = pages;
        }

        public void putAdvancedOption(String key, String value) {
        }

        public void putAdvancedOption(String key, int value) {
        }

        public PrintJobInfo build() {
            return this.mPrototype;
        }
    }
}