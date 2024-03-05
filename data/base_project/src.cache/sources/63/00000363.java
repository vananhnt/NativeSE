package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Slog;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: ContainerEncryptionParams.class */
public class ContainerEncryptionParams implements Parcelable {
    protected static final String TAG = "ContainerEncryptionParams";
    private static final String TO_STRING_PREFIX = "ContainerEncryptionParams{";
    private static final int ENC_PARAMS_IV_PARAMETERS = 1;
    private static final int MAC_PARAMS_NONE = 1;
    private final String mEncryptionAlgorithm;
    private final IvParameterSpec mEncryptionSpec;
    private final SecretKey mEncryptionKey;
    private final String mMacAlgorithm;
    private final AlgorithmParameterSpec mMacSpec;
    private final SecretKey mMacKey;
    private final byte[] mMacTag;
    private final long mAuthenticatedDataStart;
    private final long mEncryptedDataStart;
    private final long mDataEnd;
    public static final Parcelable.Creator<ContainerEncryptionParams> CREATOR = new Parcelable.Creator<ContainerEncryptionParams>() { // from class: android.content.pm.ContainerEncryptionParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContainerEncryptionParams createFromParcel(Parcel source) {
            try {
                return new ContainerEncryptionParams(source);
            } catch (InvalidAlgorithmParameterException e) {
                Slog.e(ContainerEncryptionParams.TAG, "Invalid algorithm parameters specified", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ContainerEncryptionParams[] newArray(int size) {
            return new ContainerEncryptionParams[size];
        }
    };

    public ContainerEncryptionParams(String encryptionAlgorithm, AlgorithmParameterSpec encryptionSpec, SecretKey encryptionKey) throws InvalidAlgorithmParameterException {
        this(encryptionAlgorithm, encryptionSpec, encryptionKey, null, null, null, null, -1L, -1L, -1L);
    }

    public ContainerEncryptionParams(String encryptionAlgorithm, AlgorithmParameterSpec encryptionSpec, SecretKey encryptionKey, String macAlgorithm, AlgorithmParameterSpec macSpec, SecretKey macKey, byte[] macTag, long authenticatedDataStart, long encryptedDataStart, long dataEnd) throws InvalidAlgorithmParameterException {
        if (TextUtils.isEmpty(encryptionAlgorithm)) {
            throw new NullPointerException("algorithm == null");
        }
        if (encryptionSpec == null) {
            throw new NullPointerException("encryptionSpec == null");
        }
        if (encryptionKey == null) {
            throw new NullPointerException("encryptionKey == null");
        }
        if (!TextUtils.isEmpty(macAlgorithm) && macKey == null) {
            throw new NullPointerException("macKey == null");
        }
        if (!(encryptionSpec instanceof IvParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Unknown parameter spec class; must be IvParameters");
        }
        this.mEncryptionAlgorithm = encryptionAlgorithm;
        this.mEncryptionSpec = (IvParameterSpec) encryptionSpec;
        this.mEncryptionKey = encryptionKey;
        this.mMacAlgorithm = macAlgorithm;
        this.mMacSpec = macSpec;
        this.mMacKey = macKey;
        this.mMacTag = macTag;
        this.mAuthenticatedDataStart = authenticatedDataStart;
        this.mEncryptedDataStart = encryptedDataStart;
        this.mDataEnd = dataEnd;
    }

    public String getEncryptionAlgorithm() {
        return this.mEncryptionAlgorithm;
    }

    public AlgorithmParameterSpec getEncryptionSpec() {
        return this.mEncryptionSpec;
    }

    public SecretKey getEncryptionKey() {
        return this.mEncryptionKey;
    }

    public String getMacAlgorithm() {
        return this.mMacAlgorithm;
    }

    public AlgorithmParameterSpec getMacSpec() {
        return this.mMacSpec;
    }

    public SecretKey getMacKey() {
        return this.mMacKey;
    }

    public byte[] getMacTag() {
        return this.mMacTag;
    }

    public long getAuthenticatedDataStart() {
        return this.mAuthenticatedDataStart;
    }

    public long getEncryptedDataStart() {
        return this.mEncryptedDataStart;
    }

    public long getDataEnd() {
        return this.mDataEnd;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContainerEncryptionParams)) {
            return false;
        }
        ContainerEncryptionParams other = (ContainerEncryptionParams) o;
        if (this.mAuthenticatedDataStart != other.mAuthenticatedDataStart || this.mEncryptedDataStart != other.mEncryptedDataStart || this.mDataEnd != other.mDataEnd || !this.mEncryptionAlgorithm.equals(other.mEncryptionAlgorithm) || !this.mMacAlgorithm.equals(other.mMacAlgorithm) || !isSecretKeyEqual(this.mEncryptionKey, other.mEncryptionKey) || !isSecretKeyEqual(this.mMacKey, other.mMacKey) || !Arrays.equals(this.mEncryptionSpec.getIV(), other.mEncryptionSpec.getIV()) || !Arrays.equals(this.mMacTag, other.mMacTag) || this.mMacSpec != other.mMacSpec) {
            return false;
        }
        return true;
    }

    private static final boolean isSecretKeyEqual(SecretKey key1, SecretKey key2) {
        String keyFormat = key1.getFormat();
        String otherKeyFormat = key2.getFormat();
        if (keyFormat == null) {
            if (keyFormat != otherKeyFormat || key1.getEncoded() != key2.getEncoded()) {
                return false;
            }
            return true;
        } else if (!keyFormat.equals(key2.getFormat()) || !Arrays.equals(key1.getEncoded(), key2.getEncoded())) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        int hash = 3 + (5 * this.mEncryptionAlgorithm.hashCode());
        return (int) (((int) (((int) (hash + (7 * Arrays.hashCode(this.mEncryptionSpec.getIV())) + (11 * this.mEncryptionKey.hashCode()) + (13 * this.mMacAlgorithm.hashCode()) + (17 * this.mMacKey.hashCode()) + (19 * Arrays.hashCode(this.mMacTag)) + (23 * this.mAuthenticatedDataStart))) + (29 * this.mEncryptedDataStart))) + (31 * this.mDataEnd));
    }

    public String toString() {
        return TO_STRING_PREFIX + "mEncryptionAlgorithm=\"" + this.mEncryptionAlgorithm + "\",mEncryptionSpec=" + this.mEncryptionSpec.toString() + "mEncryptionKey=" + this.mEncryptionKey.toString() + "mMacAlgorithm=\"" + this.mMacAlgorithm + "\",mMacSpec=" + this.mMacSpec.toString() + "mMacKey=" + this.mMacKey.toString() + ",mAuthenticatedDataStart=" + this.mAuthenticatedDataStart + ",mEncryptedDataStart=" + this.mEncryptedDataStart + ",mDataEnd=" + this.mDataEnd + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mEncryptionAlgorithm);
        dest.writeInt(1);
        dest.writeByteArray(this.mEncryptionSpec.getIV());
        dest.writeSerializable(this.mEncryptionKey);
        dest.writeString(this.mMacAlgorithm);
        dest.writeInt(1);
        dest.writeByteArray(new byte[0]);
        dest.writeSerializable(this.mMacKey);
        dest.writeByteArray(this.mMacTag);
        dest.writeLong(this.mAuthenticatedDataStart);
        dest.writeLong(this.mEncryptedDataStart);
        dest.writeLong(this.mDataEnd);
    }

    private ContainerEncryptionParams(Parcel source) throws InvalidAlgorithmParameterException {
        this.mEncryptionAlgorithm = source.readString();
        int encParamType = source.readInt();
        byte[] encParamsEncoded = source.createByteArray();
        this.mEncryptionKey = (SecretKey) source.readSerializable();
        this.mMacAlgorithm = source.readString();
        int macParamType = source.readInt();
        source.createByteArray();
        this.mMacKey = (SecretKey) source.readSerializable();
        this.mMacTag = source.createByteArray();
        this.mAuthenticatedDataStart = source.readLong();
        this.mEncryptedDataStart = source.readLong();
        this.mDataEnd = source.readLong();
        switch (encParamType) {
            case 1:
                this.mEncryptionSpec = new IvParameterSpec(encParamsEncoded);
                switch (macParamType) {
                    case 1:
                        this.mMacSpec = null;
                        if (this.mEncryptionKey == null) {
                            throw new NullPointerException("encryptionKey == null");
                        }
                        return;
                    default:
                        throw new InvalidAlgorithmParameterException("Unknown parameter type " + macParamType);
                }
            default:
                throw new InvalidAlgorithmParameterException("Unknown parameter type " + encParamType);
        }
    }
}