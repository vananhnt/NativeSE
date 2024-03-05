package android.view.inputmethod;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: CompletionInfo.class */
public final class CompletionInfo implements Parcelable {
    private final long mId;
    private final int mPosition;
    private final CharSequence mText;
    private final CharSequence mLabel;
    public static final Parcelable.Creator<CompletionInfo> CREATOR = new Parcelable.Creator<CompletionInfo>() { // from class: android.view.inputmethod.CompletionInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompletionInfo createFromParcel(Parcel source) {
            return new CompletionInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompletionInfo[] newArray(int size) {
            return new CompletionInfo[size];
        }
    };

    public CompletionInfo(long id, int index, CharSequence text) {
        this.mId = id;
        this.mPosition = index;
        this.mText = text;
        this.mLabel = null;
    }

    public CompletionInfo(long id, int index, CharSequence text, CharSequence label) {
        this.mId = id;
        this.mPosition = index;
        this.mText = text;
        this.mLabel = label;
    }

    private CompletionInfo(Parcel source) {
        this.mId = source.readLong();
        this.mPosition = source.readInt();
        this.mText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
    }

    public long getId() {
        return this.mId;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public String toString() {
        return "CompletionInfo{#" + this.mPosition + " \"" + ((Object) this.mText) + "\" id=" + this.mId + " label=" + ((Object) this.mLabel) + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeInt(this.mPosition);
        TextUtils.writeToParcel(this.mText, dest, flags);
        TextUtils.writeToParcel(this.mLabel, dest, flags);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}