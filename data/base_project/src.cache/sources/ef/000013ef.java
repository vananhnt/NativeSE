package android.text.style;

import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;

/* loaded from: AlignmentSpan.class */
public interface AlignmentSpan extends ParagraphStyle {
    Layout.Alignment getAlignment();

    /* loaded from: AlignmentSpan$Standard.class */
    public static class Standard implements AlignmentSpan, ParcelableSpan {
        private final Layout.Alignment mAlignment;

        public Standard(Layout.Alignment align) {
            this.mAlignment = align;
        }

        public Standard(Parcel src) {
            this.mAlignment = Layout.Alignment.valueOf(src.readString());
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeId() {
            return 1;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAlignment.name());
        }

        @Override // android.text.style.AlignmentSpan
        public Layout.Alignment getAlignment() {
            return this.mAlignment;
        }
    }
}