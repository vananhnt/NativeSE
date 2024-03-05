package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import gov.nist.core.Separators;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/* loaded from: MediaDrm.class */
public final class MediaDrm {
    private static final String TAG = "MediaDrm";
    private EventHandler mEventHandler;
    private OnEventListener mOnEventListener;
    private int mNativeContext;
    public static final int EVENT_PROVISION_REQUIRED = 1;
    public static final int EVENT_KEY_REQUIRED = 2;
    public static final int EVENT_KEY_EXPIRED = 3;
    public static final int EVENT_VENDOR_DEFINED = 4;
    private static final int DRM_EVENT = 200;
    public static final int KEY_TYPE_STREAMING = 1;
    public static final int KEY_TYPE_OFFLINE = 2;
    public static final int KEY_TYPE_RELEASE = 3;
    public static final String PROPERTY_VENDOR = "vendor";
    public static final String PROPERTY_VERSION = "version";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_ALGORITHMS = "algorithms";
    public static final String PROPERTY_DEVICE_UNIQUE_ID = "deviceUniqueId";

    /* loaded from: MediaDrm$OnEventListener.class */
    public interface OnEventListener {
        void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2);
    }

    private static final native boolean isCryptoSchemeSupportedNative(byte[] bArr, String str);

    public native byte[] openSession() throws NotProvisionedException;

    public native void closeSession(byte[] bArr);

    public native KeyRequest getKeyRequest(byte[] bArr, byte[] bArr2, String str, int i, HashMap<String, String> hashMap) throws NotProvisionedException;

    public native byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) throws NotProvisionedException, DeniedByServerException;

    public native void restoreKeys(byte[] bArr, byte[] bArr2);

    public native void removeKeys(byte[] bArr);

    public native HashMap<String, String> queryKeyStatus(byte[] bArr);

    public native ProvisionRequest getProvisionRequest();

    public native void provideProvisionResponse(byte[] bArr) throws DeniedByServerException;

    public native List<byte[]> getSecureStops();

    public native void releaseSecureStops(byte[] bArr);

    public native String getPropertyString(String str);

    public native byte[] getPropertyByteArray(String str);

    public native void setPropertyString(String str, String str2);

    public native void setPropertyByteArray(String str, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void setCipherAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void setMacAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] encryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] decryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native byte[] signNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native boolean verifyNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    public final native void release();

    private static final native void native_init();

    private final native void native_setup(Object obj, byte[] bArr);

    private final native void native_finalize();

    public static final boolean isCryptoSchemeSupported(UUID uuid) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), null);
    }

    public static final boolean isCryptoSchemeSupported(UUID uuid, String mimeType) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), mimeType);
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

    public MediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper looper2 = Looper.getMainLooper();
            if (looper2 != null) {
                this.mEventHandler = new EventHandler(this, looper2);
            } else {
                this.mEventHandler = null;
            }
        }
        native_setup(new WeakReference(this), getByteArrayFromUUID(uuid));
    }

    public void setOnEventListener(OnEventListener listener) {
        this.mOnEventListener = listener;
    }

    /* loaded from: MediaDrm$EventHandler.class */
    private class EventHandler extends Handler {
        private MediaDrm mMediaDrm;

        public EventHandler(MediaDrm md, Looper looper) {
            super(looper);
            this.mMediaDrm = md;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (this.mMediaDrm.mNativeContext == 0) {
                Log.w(MediaDrm.TAG, "MediaDrm went away with unhandled events");
                return;
            }
            switch (msg.what) {
                case 200:
                    Log.i(MediaDrm.TAG, "Drm event (" + msg.arg1 + Separators.COMMA + msg.arg2 + Separators.RPAREN);
                    if (MediaDrm.this.mOnEventListener != null && msg.obj != null && (msg.obj instanceof Parcel)) {
                        Parcel parcel = (Parcel) msg.obj;
                        byte[] sessionId = parcel.createByteArray();
                        if (sessionId.length == 0) {
                            sessionId = null;
                        }
                        byte[] data = parcel.createByteArray();
                        if (data.length == 0) {
                            data = null;
                        }
                        MediaDrm.this.mOnEventListener.onEvent(this.mMediaDrm, sessionId, msg.arg1, msg.arg2, data);
                        return;
                    }
                    return;
                default:
                    Log.e(MediaDrm.TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    private static void postEventFromNative(Object mediadrm_ref, int eventType, int extra, Object obj) {
        MediaDrm md = (MediaDrm) ((WeakReference) mediadrm_ref).get();
        if (md != null && md.mEventHandler != null) {
            Message m = md.mEventHandler.obtainMessage(200, eventType, extra, obj);
            md.mEventHandler.sendMessage(m);
        }
    }

    /* loaded from: MediaDrm$KeyRequest.class */
    public static final class KeyRequest {
        private byte[] mData;
        private String mDefaultUrl;

        KeyRequest() {
        }

        public byte[] getData() {
            return this.mData;
        }

        public String getDefaultUrl() {
            return this.mDefaultUrl;
        }
    }

    /* loaded from: MediaDrm$ProvisionRequest.class */
    public static final class ProvisionRequest {
        private byte[] mData;
        private String mDefaultUrl;

        ProvisionRequest() {
        }

        public byte[] getData() {
            return this.mData;
        }

        public String getDefaultUrl() {
            return this.mDefaultUrl;
        }
    }

    /* loaded from: MediaDrm$CryptoSession.class */
    public final class CryptoSession {
        private MediaDrm mDrm;
        private byte[] mSessionId;

        CryptoSession(MediaDrm drm, byte[] sessionId, String cipherAlgorithm, String macAlgorithm) {
            this.mSessionId = sessionId;
            this.mDrm = drm;
            MediaDrm.setCipherAlgorithmNative(drm, sessionId, cipherAlgorithm);
            MediaDrm.setMacAlgorithmNative(drm, sessionId, macAlgorithm);
        }

        public byte[] encrypt(byte[] keyid, byte[] input, byte[] iv) {
            return MediaDrm.encryptNative(this.mDrm, this.mSessionId, keyid, input, iv);
        }

        public byte[] decrypt(byte[] keyid, byte[] input, byte[] iv) {
            return MediaDrm.decryptNative(this.mDrm, this.mSessionId, keyid, input, iv);
        }

        public byte[] sign(byte[] keyid, byte[] message) {
            return MediaDrm.signNative(this.mDrm, this.mSessionId, keyid, message);
        }

        public boolean verify(byte[] keyid, byte[] message, byte[] signature) {
            return MediaDrm.verifyNative(this.mDrm, this.mSessionId, keyid, message, signature);
        }
    }

    public CryptoSession getCryptoSession(byte[] sessionId, String cipherAlgorithm, String macAlgorithm) {
        return new CryptoSession(this, sessionId, cipherAlgorithm, macAlgorithm);
    }

    protected void finalize() {
        native_finalize();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}