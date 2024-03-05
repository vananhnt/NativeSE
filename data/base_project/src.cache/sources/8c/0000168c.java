package android.webkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Deprecated
/* loaded from: CacheManager.class */
public final class CacheManager {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !CacheManager.class.desiredAssertionStatus();
    }

    @Deprecated
    /* loaded from: CacheManager$CacheResult.class */
    public static class CacheResult {
        int httpStatusCode;
        long contentLength;
        long expires;
        String expiresString;
        String localPath;
        String lastModified;
        String etag;
        String mimeType;
        String location;
        String encoding;
        String contentdisposition;
        String crossDomain;
        InputStream inStream;
        OutputStream outStream;
        File outFile;

        public int getHttpStatusCode() {
            return this.httpStatusCode;
        }

        public long getContentLength() {
            return this.contentLength;
        }

        public String getLocalPath() {
            return this.localPath;
        }

        public long getExpires() {
            return this.expires;
        }

        public String getExpiresString() {
            return this.expiresString;
        }

        public String getLastModified() {
            return this.lastModified;
        }

        public String getETag() {
            return this.etag;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public String getLocation() {
            return this.location;
        }

        public String getEncoding() {
            return this.encoding;
        }

        public String getContentDisposition() {
            return this.contentdisposition;
        }

        public InputStream getInputStream() {
            return this.inStream;
        }

        public OutputStream getOutputStream() {
            return this.outStream;
        }

        public void setInputStream(InputStream stream) {
            this.inStream = stream;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }
    }

    @Deprecated
    public static File getCacheFileBaseDir() {
        return null;
    }

    @Deprecated
    public static boolean cacheDisabled() {
        return false;
    }

    @Deprecated
    public static boolean startCacheTransaction() {
        return false;
    }

    @Deprecated
    public static boolean endCacheTransaction() {
        return false;
    }

    @Deprecated
    public static CacheResult getCacheFile(String url, Map<String, String> headers) {
        return null;
    }

    @Deprecated
    public static void saveCacheFile(String url, CacheResult cacheResult) {
        saveCacheFile(url, 0L, cacheResult);
    }

    static void saveCacheFile(String url, long postIdentifier, CacheResult cacheRet) {
        try {
            cacheRet.outStream.close();
            if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        } catch (IOException e) {
        }
    }
}