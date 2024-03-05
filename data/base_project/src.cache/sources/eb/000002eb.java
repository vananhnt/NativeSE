package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: ContentProviderResult.class */
public class ContentProviderResult implements Parcelable {
    public final Uri uri;
    public final Integer count;
    public static final Parcelable.Creator<ContentProviderResult> CREATOR = new Parcelable.Creator<ContentProviderResult>() { // from class: android.content.ContentProviderResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContentProviderResult createFromParcel(Parcel source) {
            return new ContentProviderResult(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContentProviderResult[] newArray(int size) {
            return new ContentProviderResult[size];
        }
    };

    public ContentProviderResult(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null");
        }
        this.uri = uri;
        this.count = null;
    }

    public ContentProviderResult(int count) {
        this.count = Integer.valueOf(count);
        this.uri = null;
    }

    public ContentProviderResult(Parcel source) {
        int type = source.readInt();
        if (type == 1) {
            this.count = Integer.valueOf(source.readInt());
            this.uri = null;
            return;
        }
        this.count = null;
        this.uri = Uri.CREATOR.createFromParcel(source);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.uri == null) {
            dest.writeInt(1);
            dest.writeInt(this.count.intValue());
            return;
        }
        dest.writeInt(2);
        this.uri.writeToParcel(dest, 0);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        if (this.uri != null) {
            return "ContentProviderResult(uri=" + this.uri.toString() + Separators.RPAREN;
        }
        return "ContentProviderResult(count=" + this.count + Separators.RPAREN;
    }
}