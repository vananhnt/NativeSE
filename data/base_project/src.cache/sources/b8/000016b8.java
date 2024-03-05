package android.webkit;

import java.io.InputStream;

/* loaded from: WebResourceResponse.class */
public class WebResourceResponse {
    private String mMimeType;
    private String mEncoding;
    private InputStream mInputStream;

    public WebResourceResponse(String mimeType, String encoding, InputStream data) {
        this.mMimeType = mimeType;
        this.mEncoding = encoding;
        this.mInputStream = data;
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public void setEncoding(String encoding) {
        this.mEncoding = encoding;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void setData(InputStream data) {
        this.mInputStream = data;
    }

    public InputStream getData() {
        return this.mInputStream;
    }
}