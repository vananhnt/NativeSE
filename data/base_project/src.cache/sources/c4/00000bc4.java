package android.os.storage;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import java.io.File;

/* loaded from: StorageVolume.class */
public class StorageVolume implements Parcelable {
    private int mStorageId;
    private final File mPath;
    private final int mDescriptionId;
    private final boolean mPrimary;
    private final boolean mRemovable;
    private final boolean mEmulated;
    private final int mMtpReserveSpace;
    private final boolean mAllowMassStorage;
    private final long mMaxFileSize;
    private final UserHandle mOwner;
    public static final String EXTRA_STORAGE_VOLUME = "storage_volume";
    public static final Parcelable.Creator<StorageVolume> CREATOR = new Parcelable.Creator<StorageVolume>() { // from class: android.os.storage.StorageVolume.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageVolume createFromParcel(Parcel in) {
            return new StorageVolume(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StorageVolume[] newArray(int size) {
            return new StorageVolume[size];
        }
    };

    public StorageVolume(File path, int descriptionId, boolean primary, boolean removable, boolean emulated, int mtpReserveSpace, boolean allowMassStorage, long maxFileSize, UserHandle owner) {
        this.mPath = path;
        this.mDescriptionId = descriptionId;
        this.mPrimary = primary;
        this.mRemovable = removable;
        this.mEmulated = emulated;
        this.mMtpReserveSpace = mtpReserveSpace;
        this.mAllowMassStorage = allowMassStorage;
        this.mMaxFileSize = maxFileSize;
        this.mOwner = owner;
    }

    private StorageVolume(Parcel in) {
        this.mStorageId = in.readInt();
        this.mPath = new File(in.readString());
        this.mDescriptionId = in.readInt();
        this.mPrimary = in.readInt() != 0;
        this.mRemovable = in.readInt() != 0;
        this.mEmulated = in.readInt() != 0;
        this.mMtpReserveSpace = in.readInt();
        this.mAllowMassStorage = in.readInt() != 0;
        this.mMaxFileSize = in.readLong();
        this.mOwner = (UserHandle) in.readParcelable(null);
    }

    public static StorageVolume fromTemplate(StorageVolume template, File path, UserHandle owner) {
        return new StorageVolume(path, template.mDescriptionId, template.mPrimary, template.mRemovable, template.mEmulated, template.mMtpReserveSpace, template.mAllowMassStorage, template.mMaxFileSize, owner);
    }

    public String getPath() {
        return this.mPath.toString();
    }

    public File getPathFile() {
        return this.mPath;
    }

    public String getDescription(Context context) {
        return context.getResources().getString(this.mDescriptionId);
    }

    public int getDescriptionId() {
        return this.mDescriptionId;
    }

    public boolean isPrimary() {
        return this.mPrimary;
    }

    public boolean isRemovable() {
        return this.mRemovable;
    }

    public boolean isEmulated() {
        return this.mEmulated;
    }

    public int getStorageId() {
        return this.mStorageId;
    }

    public void setStorageId(int index) {
        this.mStorageId = ((index + 1) << 16) + 1;
    }

    public int getMtpReserveSpace() {
        return this.mMtpReserveSpace;
    }

    public boolean allowMassStorage() {
        return this.mAllowMassStorage;
    }

    public long getMaxFileSize() {
        return this.mMaxFileSize;
    }

    public UserHandle getOwner() {
        return this.mOwner;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof StorageVolume) && this.mPath != null) {
            StorageVolume volume = (StorageVolume) obj;
            return this.mPath.equals(volume.mPath);
        }
        return false;
    }

    public int hashCode() {
        return this.mPath.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StorageVolume [");
        builder.append("mStorageId=").append(this.mStorageId);
        builder.append(" mPath=").append(this.mPath);
        builder.append(" mDescriptionId=").append(this.mDescriptionId);
        builder.append(" mPrimary=").append(this.mPrimary);
        builder.append(" mRemovable=").append(this.mRemovable);
        builder.append(" mEmulated=").append(this.mEmulated);
        builder.append(" mMtpReserveSpace=").append(this.mMtpReserveSpace);
        builder.append(" mAllowMassStorage=").append(this.mAllowMassStorage);
        builder.append(" mMaxFileSize=").append(this.mMaxFileSize);
        builder.append(" mOwner=").append(this.mOwner);
        builder.append("]");
        return builder.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mStorageId);
        parcel.writeString(this.mPath.toString());
        parcel.writeInt(this.mDescriptionId);
        parcel.writeInt(this.mPrimary ? 1 : 0);
        parcel.writeInt(this.mRemovable ? 1 : 0);
        parcel.writeInt(this.mEmulated ? 1 : 0);
        parcel.writeInt(this.mMtpReserveSpace);
        parcel.writeInt(this.mAllowMassStorage ? 1 : 0);
        parcel.writeLong(this.mMaxFileSize);
        parcel.writeParcelable(this.mOwner, flags);
    }
}