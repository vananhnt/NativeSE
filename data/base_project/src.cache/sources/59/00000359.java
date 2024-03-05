package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: UriPermission.class */
public final class UriPermission implements Parcelable {
    private final Uri mUri;
    private final int mModeFlags;
    private final long mPersistedTime;
    public static final long INVALID_TIME = Long.MIN_VALUE;
    public static final Parcelable.Creator<UriPermission> CREATOR = new Parcelable.Creator<UriPermission>() { // from class: android.content.UriPermission.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UriPermission createFromParcel(Parcel source) {
            return new UriPermission(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UriPermission[] newArray(int size) {
            return new UriPermission[size];
        }
    };

    public UriPermission(Uri uri, int modeFlags, long persistedTime) {
        this.mUri = uri;
        this.mModeFlags = modeFlags;
        this.mPersistedTime = persistedTime;
    }

    public UriPermission(Parcel in) {
        this.mUri = (Uri) in.readParcelable(null);
        this.mModeFlags = in.readInt();
        this.mPersistedTime = in.readLong();
    }

    public Uri getUri() {
        return this.mUri;
    }

    public boolean isReadPermission() {
        return (this.mModeFlags & 1) != 0;
    }

    public boolean isWritePermission() {
        return (this.mModeFlags & 2) != 0;
    }

    public long getPersistedTime() {
        return this.mPersistedTime;
    }

    public String toString() {
        return "UriPermission {uri=" + this.mUri + ", modeFlags=" + this.mModeFlags + ", persistedTime=" + this.mPersistedTime + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mUri, flags);
        dest.writeInt(this.mModeFlags);
        dest.writeLong(this.mPersistedTime);
    }
}