package android.media;

/* loaded from: MediaScannerClient.class */
public interface MediaScannerClient {
    void scanFile(String str, long j, long j2, boolean z, boolean z2);

    void handleStringTag(String str, String str2);

    void setMimeType(String str);
}