package android.drm;

import gov.nist.core.Separators;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: DrmInfo.class */
public class DrmInfo {
    private byte[] mData;
    private final String mMimeType;
    private final int mInfoType;
    private final HashMap<String, Object> mAttributes = new HashMap<>();

    public DrmInfo(int infoType, byte[] data, String mimeType) {
        this.mInfoType = infoType;
        this.mMimeType = mimeType;
        this.mData = data;
        if (!isValid()) {
            String msg = "infoType: " + infoType + Separators.COMMA + "mimeType: " + mimeType + Separators.COMMA + "data: " + data;
            throw new IllegalArgumentException(msg);
        }
    }

    public DrmInfo(int infoType, String path, String mimeType) {
        this.mInfoType = infoType;
        this.mMimeType = mimeType;
        try {
            this.mData = DrmUtils.readBytes(path);
        } catch (IOException e) {
            this.mData = null;
        }
        if (!isValid()) {
            String str = "infoType: " + infoType + Separators.COMMA + "mimeType: " + mimeType + Separators.COMMA + "data: " + this.mData;
            throw new IllegalArgumentException();
        }
    }

    public void put(String key, Object value) {
        this.mAttributes.put(key, value);
    }

    public Object get(String key) {
        return this.mAttributes.get(key);
    }

    public Iterator<String> keyIterator() {
        return this.mAttributes.keySet().iterator();
    }

    public Iterator<Object> iterator() {
        return this.mAttributes.values().iterator();
    }

    public byte[] getData() {
        return this.mData;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public int getInfoType() {
        return this.mInfoType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValid() {
        return (null == this.mMimeType || this.mMimeType.equals("") || null == this.mData || this.mData.length <= 0 || !DrmInfoRequest.isValidType(this.mInfoType)) ? false : true;
    }
}