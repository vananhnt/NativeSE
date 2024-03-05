package android.print;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: PageRange.class */
public final class PageRange implements Parcelable {
    private final int mStart;
    private final int mEnd;
    public static final PageRange ALL_PAGES = new PageRange(0, Integer.MAX_VALUE);
    public static final Parcelable.Creator<PageRange> CREATOR = new Parcelable.Creator<PageRange>() { // from class: android.print.PageRange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PageRange createFromParcel(Parcel parcel) {
            return new PageRange(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PageRange[] newArray(int size) {
            return new PageRange[size];
        }
    };

    public PageRange(int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException("start cannot be less than zero.");
        }
        if (end < 0) {
            throw new IllegalArgumentException("end cannot be less than zero.");
        }
        if (start > end) {
            throw new IllegalArgumentException("start must be lesser than end.");
        }
        this.mStart = start;
        this.mEnd = end;
    }

    private PageRange(Parcel parcel) {
        this(parcel.readInt(), parcel.readInt());
    }

    public int getStart() {
        return this.mStart;
    }

    public int getEnd() {
        return this.mEnd;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mStart);
        parcel.writeInt(this.mEnd);
    }

    public int hashCode() {
        int result = (31 * 1) + this.mEnd;
        return (31 * result) + this.mStart;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PageRange other = (PageRange) obj;
        if (this.mEnd != other.mEnd || this.mStart != other.mStart) {
            return false;
        }
        return true;
    }

    public String toString() {
        if (this.mStart == 0 && this.mEnd == Integer.MAX_VALUE) {
            return "PageRange[<all pages>]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("PageRange[").append(this.mStart).append(" - ").append(this.mEnd).append("]");
        return builder.toString();
    }
}