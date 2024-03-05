package android.view.textservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* loaded from: TextInfo.class */
public final class TextInfo implements Parcelable {
    private final String mText;
    private final int mCookie;
    private final int mSequence;
    public static final Parcelable.Creator<TextInfo> CREATOR = new Parcelable.Creator<TextInfo>() { // from class: android.view.textservice.TextInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextInfo createFromParcel(Parcel source) {
            return new TextInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextInfo[] newArray(int size) {
            return new TextInfo[size];
        }
    };

    public TextInfo(String text) {
        this(text, 0, 0);
    }

    public TextInfo(String text, int cookie, int sequence) {
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException(text);
        }
        this.mText = text;
        this.mCookie = cookie;
        this.mSequence = sequence;
    }

    public TextInfo(Parcel source) {
        this.mText = source.readString();
        this.mCookie = source.readInt();
        this.mSequence = source.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mText);
        dest.writeInt(this.mCookie);
        dest.writeInt(this.mSequence);
    }

    public String getText() {
        return this.mText;
    }

    public int getCookie() {
        return this.mCookie;
    }

    public int getSequence() {
        return this.mSequence;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}