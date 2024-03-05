package android.mtp;

/* loaded from: MtpStorageInfo.class */
public final class MtpStorageInfo {
    private int mStorageId;
    private long mMaxCapacity;
    private long mFreeSpace;
    private String mDescription;
    private String mVolumeIdentifier;

    private MtpStorageInfo() {
    }

    public final int getStorageId() {
        return this.mStorageId;
    }

    public final long getMaxCapacity() {
        return this.mMaxCapacity;
    }

    public final long getFreeSpace() {
        return this.mFreeSpace;
    }

    public final String getDescription() {
        return this.mDescription;
    }

    public final String getVolumeIdentifier() {
        return this.mVolumeIdentifier;
    }
}