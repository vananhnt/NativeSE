package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: SyncRequest.class */
public class SyncRequest implements Parcelable {
    private static final String TAG = "SyncRequest";
    private final Account mAccountToSync;
    private final String mAuthority;
    private final ComponentName mComponentInfo;
    private final Bundle mExtras;
    private final boolean mDisallowMetered;
    private final long mTxBytes;
    private final long mRxBytes;
    private final long mSyncFlexTimeSecs;
    private final long mSyncRunTimeSecs;
    private final boolean mIsPeriodic;
    private final boolean mIsAuthority;
    private final boolean mIsExpedited;
    public static final Parcelable.Creator<SyncRequest> CREATOR = new Parcelable.Creator<SyncRequest>() { // from class: android.content.SyncRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncRequest createFromParcel(Parcel in) {
            return new SyncRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncRequest[] newArray(int size) {
            return new SyncRequest[size];
        }
    };

    public boolean isPeriodic() {
        return this.mIsPeriodic;
    }

    public boolean isExpedited() {
        return this.mIsExpedited;
    }

    public boolean hasAuthority() {
        return this.mIsAuthority;
    }

    public Account getAccount() {
        if (!hasAuthority()) {
            throw new IllegalArgumentException("Cannot getAccount() for a sync that does notspecify an authority.");
        }
        return this.mAccountToSync;
    }

    public String getProvider() {
        if (!hasAuthority()) {
            throw new IllegalArgumentException("Cannot getProvider() for a sync that does notspecify a provider.");
        }
        return this.mAuthority;
    }

    public Bundle getBundle() {
        return this.mExtras;
    }

    public long getSyncFlexTime() {
        return this.mSyncFlexTimeSecs;
    }

    public long getSyncRunTime() {
        return this.mSyncRunTimeSecs;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBundle(this.mExtras);
        parcel.writeLong(this.mSyncFlexTimeSecs);
        parcel.writeLong(this.mSyncRunTimeSecs);
        parcel.writeInt(this.mIsPeriodic ? 1 : 0);
        parcel.writeInt(this.mDisallowMetered ? 1 : 0);
        parcel.writeLong(this.mTxBytes);
        parcel.writeLong(this.mRxBytes);
        parcel.writeInt(this.mIsAuthority ? 1 : 0);
        parcel.writeInt(this.mIsExpedited ? 1 : 0);
        if (this.mIsAuthority) {
            parcel.writeParcelable(this.mAccountToSync, flags);
            parcel.writeString(this.mAuthority);
            return;
        }
        parcel.writeParcelable(this.mComponentInfo, flags);
    }

    private SyncRequest(Parcel in) {
        this.mExtras = in.readBundle();
        this.mSyncFlexTimeSecs = in.readLong();
        this.mSyncRunTimeSecs = in.readLong();
        this.mIsPeriodic = in.readInt() != 0;
        this.mDisallowMetered = in.readInt() != 0;
        this.mTxBytes = in.readLong();
        this.mRxBytes = in.readLong();
        this.mIsAuthority = in.readInt() != 0;
        this.mIsExpedited = in.readInt() != 0;
        if (this.mIsAuthority) {
            this.mComponentInfo = null;
            this.mAccountToSync = (Account) in.readParcelable(null);
            this.mAuthority = in.readString();
            return;
        }
        this.mComponentInfo = (ComponentName) in.readParcelable(null);
        this.mAccountToSync = null;
        this.mAuthority = null;
    }

    protected SyncRequest(Builder b) {
        this.mSyncFlexTimeSecs = b.mSyncFlexTimeSecs;
        this.mSyncRunTimeSecs = b.mSyncRunTimeSecs;
        this.mAccountToSync = b.mAccount;
        this.mAuthority = b.mAuthority;
        this.mComponentInfo = b.mComponentName;
        this.mIsPeriodic = b.mSyncType == 1;
        this.mIsAuthority = b.mSyncTarget == 2;
        this.mIsExpedited = b.mExpedited;
        this.mExtras = new Bundle(b.mCustomExtras);
        this.mExtras.putAll(b.mSyncConfigExtras);
        this.mDisallowMetered = b.mDisallowMetered;
        this.mTxBytes = b.mTxBytes;
        this.mRxBytes = b.mRxBytes;
    }

    /* loaded from: SyncRequest$Builder.class */
    public static class Builder {
        private static final int SYNC_TYPE_UNKNOWN = 0;
        private static final int SYNC_TYPE_PERIODIC = 1;
        private static final int SYNC_TYPE_ONCE = 2;
        private static final int SYNC_TARGET_UNKNOWN = 0;
        private static final int SYNC_TARGET_SERVICE = 1;
        private static final int SYNC_TARGET_ADAPTER = 2;
        private long mSyncFlexTimeSecs;
        private long mSyncRunTimeSecs;
        private Bundle mCustomExtras;
        private Bundle mSyncConfigExtras;
        private boolean mDisallowMetered;
        private boolean mIsManual;
        private boolean mNoRetry;
        private boolean mIgnoreBackoff;
        private boolean mIgnoreSettings;
        private boolean mExpedited;
        private ComponentName mComponentName;
        private Account mAccount;
        private String mAuthority;
        private long mTxBytes = -1;
        private long mRxBytes = -1;
        private int mPriority = 0;
        private int mSyncType = 0;
        private int mSyncTarget = 0;

        public Builder syncOnce() {
            if (this.mSyncType != 0) {
                throw new IllegalArgumentException("Sync type has already been defined.");
            }
            this.mSyncType = 2;
            setupInterval(0L, 0L);
            return this;
        }

        public Builder syncPeriodic(long pollFrequency, long beforeSeconds) {
            if (this.mSyncType != 0) {
                throw new IllegalArgumentException("Sync type has already been defined.");
            }
            this.mSyncType = 1;
            setupInterval(pollFrequency, beforeSeconds);
            return this;
        }

        private void setupInterval(long at, long before) {
            if (before > at) {
                throw new IllegalArgumentException("Specified run time for the sync must be after the specified flex time.");
            }
            this.mSyncRunTimeSecs = at;
            this.mSyncFlexTimeSecs = before;
        }

        public Builder setTransferSize(long rxBytes, long txBytes) {
            this.mRxBytes = rxBytes;
            this.mTxBytes = txBytes;
            return this;
        }

        public Builder setDisallowMetered(boolean disallow) {
            this.mDisallowMetered = disallow;
            return this;
        }

        public Builder setSyncAdapter(Account account, String authority) {
            if (this.mSyncTarget != 0) {
                throw new IllegalArgumentException("Sync target has already been defined.");
            }
            if (authority != null && authority.length() == 0) {
                throw new IllegalArgumentException("Authority must be non-empty");
            }
            this.mSyncTarget = 2;
            this.mAccount = account;
            this.mAuthority = authority;
            this.mComponentName = null;
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.mCustomExtras = bundle;
            return this;
        }

        public Builder setNoRetry(boolean noRetry) {
            this.mNoRetry = noRetry;
            return this;
        }

        public Builder setIgnoreSettings(boolean ignoreSettings) {
            this.mIgnoreSettings = ignoreSettings;
            return this;
        }

        public Builder setIgnoreBackoff(boolean ignoreBackoff) {
            this.mIgnoreBackoff = ignoreBackoff;
            return this;
        }

        public Builder setManual(boolean isManual) {
            this.mIsManual = isManual;
            return this;
        }

        public Builder setExpedited(boolean expedited) {
            this.mExpedited = expedited;
            return this;
        }

        public Builder setPriority(int priority) {
            if (priority < -2 || priority > 2) {
                throw new IllegalArgumentException("Priority must be within range [-2, 2]");
            }
            this.mPriority = priority;
            return this;
        }

        public SyncRequest build() {
            if (this.mCustomExtras == null) {
                this.mCustomExtras = new Bundle();
            }
            ContentResolver.validateSyncExtrasBundle(this.mCustomExtras);
            this.mSyncConfigExtras = new Bundle();
            if (this.mIgnoreBackoff) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
            }
            if (this.mDisallowMetered) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_DISALLOW_METERED, true);
            }
            if (this.mIgnoreSettings) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
            }
            if (this.mNoRetry) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
            }
            if (this.mExpedited) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            }
            if (this.mIsManual) {
                this.mSyncConfigExtras.putBoolean("force", true);
            }
            this.mSyncConfigExtras.putLong(ContentResolver.SYNC_EXTRAS_EXPECTED_UPLOAD, this.mTxBytes);
            this.mSyncConfigExtras.putLong(ContentResolver.SYNC_EXTRAS_EXPECTED_DOWNLOAD, this.mRxBytes);
            this.mSyncConfigExtras.putInt(ContentResolver.SYNC_EXTRAS_PRIORITY, this.mPriority);
            if (this.mSyncType == 1) {
                validatePeriodicExtras(this.mCustomExtras);
                validatePeriodicExtras(this.mSyncConfigExtras);
                if (this.mAccount == null) {
                    throw new IllegalArgumentException("Account must not be null for periodic sync.");
                }
                if (this.mAuthority == null) {
                    throw new IllegalArgumentException("Authority must not be null for periodic sync.");
                }
            } else if (this.mSyncType == 0) {
                throw new IllegalArgumentException("Must call either syncOnce() or syncPeriodic()");
            }
            if (this.mSyncTarget == 0) {
                throw new IllegalArgumentException("Must specify an adapter with setSyncAdapter(Account, String");
            }
            return new SyncRequest(this);
        }

        private void validatePeriodicExtras(Bundle extras) {
            if (extras.getBoolean("force", false) || extras.getBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false) || extras.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, false) || extras.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, false) || extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false) || extras.getBoolean("force", false) || extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false)) {
                throw new IllegalArgumentException("Illegal extras were set");
            }
        }
    }
}