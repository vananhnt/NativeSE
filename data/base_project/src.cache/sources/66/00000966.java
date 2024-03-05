package android.net.http;

import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.RequestContent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Request.class */
public class Request {
    EventHandler mEventHandler;
    private Connection mConnection;
    BasicHttpRequest mHttpRequest;
    String mPath;
    HttpHost mHost;
    HttpHost mProxyHost;
    private InputStream mBodyProvider;
    private int mBodyLength;
    private static final String HOST_HEADER = "Host";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_LENGTH_HEADER = "content-length";
    private static RequestContent requestContentProcessor = new RequestContent();
    volatile boolean mCancelled = false;
    int mFailCount = 0;
    private int mReceivedBytes = 0;
    private final Object mClientResource = new Object();
    private boolean mLoadingPaused = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Request(String method, HttpHost host, HttpHost proxyHost, String path, InputStream bodyProvider, int bodyLength, EventHandler eventHandler, Map<String, String> headers) {
        this.mEventHandler = eventHandler;
        this.mHost = host;
        this.mProxyHost = proxyHost;
        this.mPath = path;
        this.mBodyProvider = bodyProvider;
        this.mBodyLength = bodyLength;
        if (bodyProvider == null && !"POST".equalsIgnoreCase(method)) {
            this.mHttpRequest = new BasicHttpRequest(method, getUri());
        } else {
            this.mHttpRequest = new BasicHttpEntityEnclosingRequest(method, getUri());
            if (bodyProvider != null) {
                setBodyProvider(bodyProvider, bodyLength);
            }
        }
        addHeader("Host", getHostPort());
        addHeader("Accept-Encoding", "gzip");
        addHeaders(headers);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setLoadingPaused(boolean pause) {
        this.mLoadingPaused = pause;
        if (!this.mLoadingPaused) {
            notify();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConnection(Connection connection) {
        this.mConnection = connection;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventHandler getEventHandler() {
        return this.mEventHandler;
    }

    void addHeader(String name, String value) {
        if (name == null) {
            HttpLog.e("Null http header name");
            throw new NullPointerException("Null http header name");
        } else if (value == null || value.length() == 0) {
            String damage = "Null or empty value for header \"" + name + Separators.DOUBLE_QUOTE;
            HttpLog.e(damage);
            throw new RuntimeException(damage);
        } else {
            this.mHttpRequest.addHeader(name, value);
        }
    }

    void addHeaders(Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendRequest(AndroidHttpClientConnection httpClientConnection) throws HttpException, IOException {
        if (this.mCancelled) {
            return;
        }
        requestContentProcessor.process(this.mHttpRequest, this.mConnection.getHttpContext());
        httpClientConnection.sendRequestHeader(this.mHttpRequest);
        if (this.mHttpRequest instanceof HttpEntityEnclosingRequest) {
            httpClientConnection.sendRequestEntity((HttpEntityEnclosingRequest) this.mHttpRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00e5  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0178  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void readResponse(android.net.http.AndroidHttpClientConnection r7) throws java.io.IOException, org.apache.http.ParseException {
        /*
            Method dump skipped, instructions count: 523
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.Request.readResponse(android.net.http.AndroidHttpClientConnection):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void cancel() {
        this.mLoadingPaused = false;
        notify();
        this.mCancelled = true;
        if (this.mConnection != null) {
            this.mConnection.cancel();
        }
    }

    String getHostPort() {
        String myScheme = this.mHost.getSchemeName();
        int myPort = this.mHost.getPort();
        if ((myPort != 80 && myScheme.equals("http")) || (myPort != 443 && myScheme.equals("https"))) {
            return this.mHost.toHostString();
        }
        return this.mHost.getHostName();
    }

    String getUri() {
        if (this.mProxyHost == null || this.mHost.getSchemeName().equals("https")) {
            return this.mPath;
        }
        return this.mHost.getSchemeName() + "://" + getHostPort() + this.mPath;
    }

    public String toString() {
        return this.mPath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.mHttpRequest.removeHeaders("content-length");
        if (this.mBodyProvider != null) {
            try {
                this.mBodyProvider.reset();
            } catch (IOException e) {
            }
            setBodyProvider(this.mBodyProvider, this.mBodyLength);
        }
        if (this.mReceivedBytes > 0) {
            this.mFailCount = 0;
            HttpLog.v("*** Request.reset() to range:" + this.mReceivedBytes);
            this.mHttpRequest.setHeader("Range", "bytes=" + this.mReceivedBytes + "-");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitUntilComplete() {
        synchronized (this.mClientResource) {
            try {
                this.mClientResource.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void complete() {
        synchronized (this.mClientResource) {
            this.mClientResource.notifyAll();
        }
    }

    private static boolean canResponseHaveBody(HttpRequest request, int status) {
        return ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod()) || status < 200 || status == 204 || status == 304) ? false : true;
    }

    private void setBodyProvider(InputStream bodyProvider, int bodyLength) {
        if (!bodyProvider.markSupported()) {
            throw new IllegalArgumentException("bodyProvider must support mark()");
        }
        bodyProvider.mark(Integer.MAX_VALUE);
        ((BasicHttpEntityEnclosingRequest) this.mHttpRequest).setEntity(new InputStreamEntity(bodyProvider, bodyLength));
    }

    public void handleSslErrorResponse(boolean proceed) {
        HttpsConnection connection = (HttpsConnection) this.mConnection;
        if (connection != null) {
            connection.restartConnection(proceed);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void error(int errorId, int resourceId) {
        this.mEventHandler.error(errorId, this.mConnection.mContext.getText(resourceId).toString());
    }
}