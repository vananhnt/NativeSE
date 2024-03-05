package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: ExtractedTextRequest.class */
public class ExtractedTextRequest implements Parcelable {
    public int token;
    public int flags;
    public int hintMaxLines;
    public int hintMaxChars;
    public static final Parcelable.Creator<ExtractedTextRequest> CREATOR = new Parcelable.Creator<ExtractedTextRequest>() { // from class: android.view.inputmethod.ExtractedTextRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ExtractedTextRequest createFromParcel(Parcel source) {
            ExtractedTextRequest res = new ExtractedTextRequest();
            res.token = source.readInt();
            res.flags = source.readInt();
            res.hintMaxLines = source.readInt();
            res.hintMaxChars = source.readInt();
            return res;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ExtractedTextRequest[] newArray(int size) {
            return new ExtractedTextRequest[size];
        }
    };

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.token);
        dest.writeInt(this.flags);
        dest.writeInt(this.hintMaxLines);
        dest.writeInt(this.hintMaxChars);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}