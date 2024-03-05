package android.content.pm;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: VerificationParams.class */
public class VerificationParams implements Parcelable {
    public static final int NO_UID = -1;
    private static final String TO_STRING_PREFIX = "VerificationParams{";
    private final Uri mVerificationURI;
    private final Uri mOriginatingURI;
    private final Uri mReferrer;
    private final int mOriginatingUid;
    private int mInstallerUid;
    private final ManifestDigest mManifestDigest;
    public static final Parcelable.Creator<VerificationParams> CREATOR = new Parcelable.Creator<VerificationParams>() { // from class: android.content.pm.VerificationParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams createFromParcel(Parcel source) {
            return new VerificationParams(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams[] newArray(int size) {
            return new VerificationParams[size];
        }
    };

    public VerificationParams(Uri verificationURI, Uri originatingURI, Uri referrer, int originatingUid, ManifestDigest manifestDigest) {
        this.mVerificationURI = verificationURI;
        this.mOriginatingURI = originatingURI;
        this.mReferrer = referrer;
        this.mOriginatingUid = originatingUid;
        this.mManifestDigest = manifestDigest;
        this.mInstallerUid = -1;
    }

    public Uri getVerificationURI() {
        return this.mVerificationURI;
    }

    public Uri getOriginatingURI() {
        return this.mOriginatingURI;
    }

    public Uri getReferrer() {
        return this.mReferrer;
    }

    public int getOriginatingUid() {
        return this.mOriginatingUid;
    }

    public ManifestDigest getManifestDigest() {
        return this.mManifestDigest;
    }

    public int getInstallerUid() {
        return this.mInstallerUid;
    }

    public void setInstallerUid(int uid) {
        this.mInstallerUid = uid;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationParams)) {
            return false;
        }
        VerificationParams other = (VerificationParams) o;
        if (this.mVerificationURI == null) {
            if (other.mVerificationURI != null) {
                return false;
            }
        } else if (!this.mVerificationURI.equals(other.mVerificationURI)) {
            return false;
        }
        if (this.mOriginatingURI == null) {
            if (other.mOriginatingURI != null) {
                return false;
            }
        } else if (!this.mOriginatingURI.equals(other.mOriginatingURI)) {
            return false;
        }
        if (this.mReferrer == null) {
            if (other.mReferrer != null) {
                return false;
            }
        } else if (!this.mReferrer.equals(other.mReferrer)) {
            return false;
        }
        if (this.mOriginatingUid != other.mOriginatingUid) {
            return false;
        }
        if (this.mManifestDigest == null) {
            if (other.mManifestDigest != null) {
                return false;
            }
        } else if (!this.mManifestDigest.equals(other.mManifestDigest)) {
            return false;
        }
        if (this.mInstallerUid != other.mInstallerUid) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3 + (5 * (this.mVerificationURI == null ? 1 : this.mVerificationURI.hashCode()));
        return hash + (7 * (this.mOriginatingURI == null ? 1 : this.mOriginatingURI.hashCode())) + (11 * (this.mReferrer == null ? 1 : this.mReferrer.hashCode())) + (13 * this.mOriginatingUid) + (17 * (this.mManifestDigest == null ? 1 : this.mManifestDigest.hashCode())) + (19 * this.mInstallerUid);
    }

    public String toString() {
        return TO_STRING_PREFIX + "mVerificationURI=" + this.mVerificationURI.toString() + ",mOriginatingURI=" + this.mOriginatingURI.toString() + ",mReferrer=" + this.mReferrer.toString() + ",mOriginatingUid=" + this.mOriginatingUid + ",mManifestDigest=" + this.mManifestDigest.toString() + ",mInstallerUid=" + this.mInstallerUid + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mVerificationURI, 0);
        dest.writeParcelable(this.mOriginatingURI, 0);
        dest.writeParcelable(this.mReferrer, 0);
        dest.writeInt(this.mOriginatingUid);
        dest.writeParcelable(this.mManifestDigest, 0);
        dest.writeInt(this.mInstallerUid);
    }

    private VerificationParams(Parcel source) {
        this.mVerificationURI = (Uri) source.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingURI = (Uri) source.readParcelable(Uri.class.getClassLoader());
        this.mReferrer = (Uri) source.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingUid = source.readInt();
        this.mManifestDigest = (ManifestDigest) source.readParcelable(ManifestDigest.class.getClassLoader());
        this.mInstallerUid = source.readInt();
    }
}