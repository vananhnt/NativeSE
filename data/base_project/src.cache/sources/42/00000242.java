package android.app.backup;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: RestoreSet.class */
public class RestoreSet implements Parcelable {
    public String name;
    public String device;
    public long token;
    public static final Parcelable.Creator<RestoreSet> CREATOR = new Parcelable.Creator<RestoreSet>() { // from class: android.app.backup.RestoreSet.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestoreSet createFromParcel(Parcel in) {
            return new RestoreSet(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestoreSet[] newArray(int size) {
            return new RestoreSet[size];
        }
    };

    public RestoreSet() {
    }

    public RestoreSet(String _name, String _dev, long _token) {
        this.name = _name;
        this.device = _dev;
        this.token = _token;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.device);
        out.writeLong(this.token);
    }

    private RestoreSet(Parcel in) {
        this.name = in.readString();
        this.device = in.readString();
        this.token = in.readLong();
    }
}