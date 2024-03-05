package android.content;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: SyncInfo.class */
public class SyncInfo implements Parcelable {
    public final int authorityId;
    public final Account account;
    public final String authority;
    public final long startTime;
    public static final Parcelable.Creator<SyncInfo> CREATOR = new Parcelable.Creator<SyncInfo>() { // from class: android.content.SyncInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncInfo createFromParcel(Parcel in) {
            return new SyncInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncInfo[] newArray(int size) {
            return new SyncInfo[size];
        }
    };

    public SyncInfo(int authorityId, Account account, String authority, long startTime) {
        this.authorityId = authorityId;
        this.account = account;
        this.authority = authority;
        this.startTime = startTime;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.authorityId);
        this.account.writeToParcel(parcel, 0);
        parcel.writeString(this.authority);
        parcel.writeLong(this.startTime);
    }

    SyncInfo(Parcel parcel) {
        this.authorityId = parcel.readInt();
        this.account = new Account(parcel);
        this.authority = parcel.readString();
        this.startTime = parcel.readLong();
    }
}