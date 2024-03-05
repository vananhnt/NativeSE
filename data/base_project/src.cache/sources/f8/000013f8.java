package android.text.style;

import android.app.PendingIntent;
import android.os.Parcel;
import android.text.ParcelableSpan;

/* loaded from: EasyEditSpan.class */
public class EasyEditSpan implements ParcelableSpan {
    public static final String EXTRA_TEXT_CHANGED_TYPE = "android.text.style.EXTRA_TEXT_CHANGED_TYPE";
    public static final int TEXT_DELETED = 1;
    public static final int TEXT_MODIFIED = 2;
    private final PendingIntent mPendingIntent;
    private boolean mDeleteEnabled;

    public EasyEditSpan() {
        this.mPendingIntent = null;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(PendingIntent pendingIntent) {
        this.mPendingIntent = pendingIntent;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(Parcel source) {
        this.mPendingIntent = (PendingIntent) source.readParcelable(null);
        this.mDeleteEnabled = source.readByte() == 1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mPendingIntent, 0);
        dest.writeByte((byte) (this.mDeleteEnabled ? 1 : 0));
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 22;
    }

    public boolean isDeleteEnabled() {
        return this.mDeleteEnabled;
    }

    public void setDeleteEnabled(boolean value) {
        this.mDeleteEnabled = value;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }
}