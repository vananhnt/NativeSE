package android.drm;

/* loaded from: DrmInfoStatus.class */
public class DrmInfoStatus {
    public static final int STATUS_OK = 1;
    public static final int STATUS_ERROR = 2;
    public final int statusCode;
    public final int infoType;
    public final String mimeType;
    public final ProcessedData data;

    public DrmInfoStatus(int statusCode, int infoType, ProcessedData data, String mimeType) {
        if (!DrmInfoRequest.isValidType(infoType)) {
            throw new IllegalArgumentException("infoType: " + infoType);
        }
        if (!isValidStatusCode(statusCode)) {
            throw new IllegalArgumentException("Unsupported status code: " + statusCode);
        }
        if (mimeType == null || mimeType == "") {
            throw new IllegalArgumentException("mimeType is null or an empty string");
        }
        this.statusCode = statusCode;
        this.infoType = infoType;
        this.data = data;
        this.mimeType = mimeType;
    }

    private boolean isValidStatusCode(int statusCode) {
        return statusCode == 1 || statusCode == 2;
    }
}