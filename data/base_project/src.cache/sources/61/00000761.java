package android.media;

import java.util.UUID;

/* loaded from: MediaCrypto.class */
public final class MediaCrypto {
    private int mNativeContext;

    private static final native boolean isCryptoSchemeSupportedNative(byte[] bArr);

    public final native boolean requiresSecureDecoderComponent(String str);

    public final native void release();

    private static final native void native_init();

    private final native void native_setup(byte[] bArr, byte[] bArr2) throws MediaCryptoException;

    private final native void native_finalize();

    public static final boolean isCryptoSchemeSupported(UUID uuid) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid));
    }

    private static final byte[] getByteArrayFromUUID(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] uuidBytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (msb >>> (8 * (7 - i)));
            uuidBytes[8 + i] = (byte) (lsb >>> (8 * (7 - i)));
        }
        return uuidBytes;
    }

    public MediaCrypto(UUID uuid, byte[] initData) throws MediaCryptoException {
        native_setup(getByteArrayFromUUID(uuid), initData);
    }

    protected void finalize() {
        native_finalize();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}