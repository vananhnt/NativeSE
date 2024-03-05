package android.net.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.impl.io.IdentityInputStream;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

/* loaded from: AndroidHttpClientConnection.class */
public class AndroidHttpClientConnection implements HttpInetConnection, org.apache.http.HttpConnection {
    private int maxHeaderCount;
    private int maxLineLength;
    private volatile boolean open;
    private SessionInputBuffer inbuffer = null;
    private SessionOutputBuffer outbuffer = null;
    private HttpMessageWriter requestWriter = null;
    private HttpConnectionMetricsImpl metrics = null;
    private Socket socket = null;
    private final EntitySerializer entityserializer = new EntitySerializer(new StrictContentLengthStrategy());

    public void bind(Socket socket, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        assertNotOpen();
        socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            socket.setSoLinger(linger > 0, linger);
        }
        this.socket = socket;
        int buffersize = HttpConnectionParams.getSocketBufferSize(params);
        this.inbuffer = new SocketInputBuffer(socket, buffersize, params);
        this.outbuffer = new SocketOutputBuffer(socket, buffersize, params);
        this.maxHeaderCount = params.getIntParameter("http.connection.max-header-count", -1);
        this.maxLineLength = params.getIntParameter("http.connection.max-line-length", -1);
        this.requestWriter = new HttpRequestWriter(this.outbuffer, null, params);
        this.metrics = new HttpConnectionMetricsImpl(this.inbuffer.getMetrics(), this.outbuffer.getMetrics());
        this.open = true;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getSimpleName()).append("[");
        if (isOpen()) {
            buffer.append(getRemotePort());
        } else {
            buffer.append("closed");
        }
        buffer.append("]");
        return buffer.toString();
    }

    private void assertNotOpen() {
        if (this.open) {
            throw new IllegalStateException("Connection is already open");
        }
    }

    private void assertOpen() {
        if (!this.open) {
            throw new IllegalStateException("Connection is not open");
        }
    }

    @Override // org.apache.http.HttpConnection
    public boolean isOpen() {
        return this.open && this.socket != null && this.socket.isConnected();
    }

    @Override // org.apache.http.HttpInetConnection
    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }

    @Override // org.apache.http.HttpInetConnection
    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }

    @Override // org.apache.http.HttpInetConnection
    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }

    @Override // org.apache.http.HttpInetConnection
    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }

    @Override // org.apache.http.HttpConnection
    public void setSocketTimeout(int timeout) {
        assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            } catch (SocketException e) {
            }
        }
    }

    @Override // org.apache.http.HttpConnection
    public int getSocketTimeout() {
        if (this.socket != null) {
            try {
                return this.socket.getSoTimeout();
            } catch (SocketException e) {
                return -1;
            }
        }
        return -1;
    }

    @Override // org.apache.http.HttpConnection
    public void shutdown() throws IOException {
        this.open = false;
        Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }

    @Override // org.apache.http.HttpConnection
    public void close() throws IOException {
        if (!this.open) {
            return;
        }
        this.open = false;
        doFlush();
        try {
            try {
                this.socket.shutdownOutput();
            } catch (UnsupportedOperationException e) {
            }
        } catch (IOException e2) {
        }
        try {
            this.socket.shutdownInput();
        } catch (IOException e3) {
        }
        this.socket.close();
    }

    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        this.requestWriter.write(request);
        this.metrics.incrementRequestCount();
    }

    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        if (request.getEntity() == null) {
            return;
        }
        this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
    }

    protected void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    public StatusLine parseResponseHeader(Headers headers) throws IOException, ParseException {
        char ch;
        assertOpen();
        CharArrayBuffer current = new CharArrayBuffer(64);
        if (this.inbuffer.readLine(current) == -1) {
            throw new NoHttpResponseException("The target server failed to respond");
        }
        StatusLine statusline = BasicLineParser.DEFAULT.parseStatusLine(current, new ParserCursor(0, current.length()));
        int statusCode = statusline.getStatusCode();
        CharArrayBuffer previous = null;
        int headerNumber = 0;
        while (true) {
            if (current == null) {
                current = new CharArrayBuffer(64);
            } else {
                current.clear();
            }
            int l = this.inbuffer.readLine(current);
            if (l == -1 || current.length() < 1) {
                break;
            }
            char first = current.charAt(0);
            if ((first == ' ' || first == '\t') && previous != null) {
                int start = 0;
                int length = current.length();
                while (start < length && ((ch = current.charAt(start)) == ' ' || ch == '\t')) {
                    start++;
                }
                if (this.maxLineLength > 0 && ((previous.length() + 1) + current.length()) - start > this.maxLineLength) {
                    throw new IOException("Maximum line length limit exceeded");
                }
                previous.append(' ');
                previous.append(current, start, current.length() - start);
            } else {
                if (previous != null) {
                    headers.parseHeader(previous);
                }
                headerNumber++;
                previous = current;
                current = null;
            }
            if (this.maxHeaderCount > 0 && headerNumber >= this.maxHeaderCount) {
                throw new IOException("Maximum header count exceeded");
            }
        }
        if (previous != null) {
            headers.parseHeader(previous);
        }
        if (statusCode >= 200) {
            this.metrics.incrementResponseCount();
        }
        return statusline;
    }

    public HttpEntity receiveResponseEntity(Headers headers) {
        assertOpen();
        BasicHttpEntity entity = new BasicHttpEntity();
        long len = determineLength(headers);
        if (len == -2) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
            entity.setContent(new ChunkedInputStream(this.inbuffer));
        } else if (len == -1) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
            entity.setContent(new IdentityInputStream(this.inbuffer));
        } else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(new ContentLengthInputStream(this.inbuffer, len));
        }
        String contentTypeHeader = headers.getContentType();
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        String contentEncodingHeader = headers.getContentEncoding();
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }

    private long determineLength(Headers headers) {
        long transferEncoding = headers.getTransferEncoding();
        if (transferEncoding < 0) {
            return transferEncoding;
        }
        long contentlen = headers.getContentLength();
        if (contentlen > -1) {
            return contentlen;
        }
        return -1L;
    }

    @Override // org.apache.http.HttpConnection
    public boolean isStale() {
        assertOpen();
        try {
            this.inbuffer.isDataAvailable(1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    @Override // org.apache.http.HttpConnection
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}