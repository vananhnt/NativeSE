package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import libcore.io.IoUtils;

/* loaded from: ManifestDigest.class */
public class ManifestDigest implements Parcelable {
    private static final String TAG = "ManifestDigest";
    private final byte[] mDigest;
    private static final String TO_STRING_PREFIX = "ManifestDigest {mDigest=";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    public static final Parcelable.Creator<ManifestDigest> CREATOR = new Parcelable.Creator<ManifestDigest>() { // from class: android.content.pm.ManifestDigest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ManifestDigest createFromParcel(Parcel source) {
            return new ManifestDigest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ManifestDigest[] newArray(int size) {
            return new ManifestDigest[size];
        }
    };

    ManifestDigest(byte[] digest) {
        this.mDigest = digest;
    }

    private ManifestDigest(Parcel source) {
        this.mDigest = source.createByteArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v4, types: [java.security.DigestInputStream, java.lang.AutoCloseable] */
    public static ManifestDigest fromInputStream(InputStream fileIs) {
        if (fileIs == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            ?? digestInputStream = new DigestInputStream(new BufferedInputStream(fileIs), md);
            try {
                try {
                    byte[] readBuffer = new byte[8192];
                    while (digestInputStream.read(readBuffer, 0, readBuffer.length) != -1) {
                    }
                    byte[] digest = md.digest();
                    return new ManifestDigest(digest);
                } catch (IOException e) {
                    Slog.w(TAG, "Could not read manifest");
                    IoUtils.closeQuietly((AutoCloseable) digestInputStream);
                    return null;
                }
            } finally {
                IoUtils.closeQuietly((AutoCloseable) digestInputStream);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException("SHA-256 must be available", e2);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ManifestDigest)) {
            return false;
        }
        ManifestDigest other = (ManifestDigest) o;
        return this == other || Arrays.equals(this.mDigest, other.mDigest);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mDigest);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(TO_STRING_PREFIX.length() + (this.mDigest.length * 3) + 1);
        sb.append(TO_STRING_PREFIX);
        int N = this.mDigest.length;
        for (int i = 0; i < N; i++) {
            byte b = this.mDigest[i];
            IntegralToString.appendByteAsHex(sb, b, false);
            sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mDigest);
    }
}