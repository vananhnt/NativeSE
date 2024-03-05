package android.nfc;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: BeamShareData.class */
public final class BeamShareData implements Parcelable {
    public final NdefMessage ndefMessage;
    public final Uri[] uris;
    public final int flags;
    public static final Parcelable.Creator<BeamShareData> CREATOR = new Parcelable.Creator<BeamShareData>() { // from class: android.nfc.BeamShareData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BeamShareData createFromParcel(Parcel source) {
            Uri[] uris = null;
            NdefMessage msg = (NdefMessage) source.readParcelable(NdefMessage.class.getClassLoader());
            int numUris = source.readInt();
            if (numUris > 0) {
                uris = new Uri[numUris];
                source.readTypedArray(uris, Uri.CREATOR);
            }
            int flags = source.readInt();
            return new BeamShareData(msg, uris, flags);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BeamShareData[] newArray(int size) {
            return new BeamShareData[size];
        }
    };

    public BeamShareData(NdefMessage msg, Uri[] uris, int flags) {
        this.ndefMessage = msg;
        this.uris = uris;
        this.flags = flags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int urisLength = this.uris != null ? this.uris.length : 0;
        dest.writeParcelable(this.ndefMessage, 0);
        dest.writeInt(urisLength);
        if (urisLength > 0) {
            dest.writeTypedArray(this.uris, 0);
        }
        dest.writeInt(this.flags);
    }
}