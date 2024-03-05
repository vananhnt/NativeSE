package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.ArrayUtils;
import java.io.ByteArrayInputStream;
import java.lang.ref.SoftReference;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

/* loaded from: Signature.class */
public class Signature implements Parcelable {
    private final byte[] mSignature;
    private int mHashCode;
    private boolean mHaveHashCode;
    private SoftReference<String> mStringRef;
    public static final Parcelable.Creator<Signature> CREATOR = new Parcelable.Creator<Signature>() { // from class: android.content.pm.Signature.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Signature createFromParcel(Parcel source) {
            return new Signature(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Signature[] newArray(int size) {
            return new Signature[size];
        }
    };

    public Signature(byte[] signature) {
        this.mSignature = (byte[]) signature.clone();
    }

    private static final int parseHexDigit(int nibble) {
        if (48 <= nibble && nibble <= 57) {
            return nibble - 48;
        }
        if (97 <= nibble && nibble <= 102) {
            return (nibble - 97) + 10;
        }
        if (65 <= nibble && nibble <= 70) {
            return (nibble - 65) + 10;
        }
        throw new IllegalArgumentException("Invalid character " + nibble + " in hex string");
    }

    public Signature(String text) {
        byte[] input = text.getBytes();
        int N = input.length;
        if (N % 2 != 0) {
            throw new IllegalArgumentException("text size " + N + " is not even");
        }
        byte[] sig = new byte[N / 2];
        int sigIndex = 0;
        int i = 0;
        while (i < N) {
            int i2 = i;
            int i3 = i + 1;
            int hi = parseHexDigit(input[i2]);
            i = i3 + 1;
            int lo = parseHexDigit(input[i3]);
            int i4 = sigIndex;
            sigIndex++;
            sig[i4] = (byte) ((hi << 4) | lo);
        }
        this.mSignature = sig;
    }

    public char[] toChars() {
        return toChars(null, null);
    }

    public char[] toChars(char[] existingArray, int[] outLen) {
        byte[] sig = this.mSignature;
        int N = sig.length;
        int N2 = N * 2;
        char[] text = (existingArray == null || N2 > existingArray.length) ? new char[N2] : existingArray;
        for (int j = 0; j < N; j++) {
            byte v = sig[j];
            int d = (v >> 4) & 15;
            text[j * 2] = (char) (d >= 10 ? (97 + d) - 10 : 48 + d);
            int d2 = v & 15;
            text[(j * 2) + 1] = (char) (d2 >= 10 ? (97 + d2) - 10 : 48 + d2);
        }
        if (outLen != null) {
            outLen[0] = N;
        }
        return text;
    }

    public String toCharsString() {
        String str = this.mStringRef == null ? null : this.mStringRef.get();
        if (str != null) {
            return str;
        }
        String str2 = new String(toChars());
        this.mStringRef = new SoftReference<>(str2);
        return str2;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[this.mSignature.length];
        System.arraycopy(this.mSignature, 0, bytes, 0, this.mSignature.length);
        return bytes;
    }

    public PublicKey getPublicKey() throws CertificateException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(this.mSignature);
        Certificate cert = certFactory.generateCertificate(bais);
        return cert.getPublicKey();
    }

    public boolean equals(Object obj) {
        if (obj != null) {
            try {
                Signature other = (Signature) obj;
                if (this != other) {
                    if (!Arrays.equals(this.mSignature, other.mSignature)) {
                        return false;
                    }
                }
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        if (this.mHaveHashCode) {
            return this.mHashCode;
        }
        this.mHashCode = Arrays.hashCode(this.mSignature);
        this.mHaveHashCode = true;
        return this.mHashCode;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeByteArray(this.mSignature);
    }

    private Signature(Parcel source) {
        this.mSignature = source.createByteArray();
    }

    public static boolean areExactMatch(Signature[] a, Signature[] b) {
        return ArrayUtils.containsAll(a, b) && ArrayUtils.containsAll(b, a);
    }
}