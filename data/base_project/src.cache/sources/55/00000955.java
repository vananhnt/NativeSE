package android.net.http;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Connection.class */
public abstract class Connection {
    static final int SOCKET_TIMEOUT = 60000;
    private static final int SEND = 0;
    private static final int READ = 1;
    private static final int DRAIN = 2;
    private static final int DONE = 3;
    Context mContext;
    HttpHost mHost;
    private static final int RETRY_REQUEST_LIMIT = 2;
    private static final int MIN_PIPE = 2;
    private static final int MAX_PIPE = 3;
    private static final String HTTP_CONNECTION = "http.connection";
    RequestFeeder mRequestFeeder;
    private byte[] mBuf;
    private static final String[] states = {"SEND", "READ", "DRAIN", "DONE"};
    private static int STATE_NORMAL = 0;
    private static int STATE_CANCEL_REQUESTED = 1;
    protected AndroidHttpClientConnection mHttpClientConnection = null;
    protected SslCertificate mCertificate = null;
    private int mActive = STATE_NORMAL;
    private boolean mCanPersist = false;
    private HttpContext mHttpContext = new BasicHttpContext(null);

    abstract String getScheme();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void closeConnection();

    abstract AndroidHttpClientConnection openConnection(Request request) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public Connection(Context context, HttpHost host, RequestFeeder requestFeeder) {
        this.mContext = context;
        this.mHost = host;
        this.mRequestFeeder = requestFeeder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpHost getHost() {
        return this.mHost;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Connection getConnection(Context context, HttpHost host, HttpHost proxy, RequestFeeder requestFeeder) {
        if (host.getSchemeName().equals("http")) {
            return new HttpConnection(context, host, requestFeeder);
        }
        return new HttpsConnection(context, host, proxy, requestFeeder);
    }

    SslCertificate getCertificate() {
        return this.mCertificate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        this.mActive = STATE_CANCEL_REQUESTED;
        closeConnection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processRequests(Request firstRequest) {
        Request req;
        int error = 0;
        Exception exception = null;
        LinkedList<Request> pipe = new LinkedList<>();
        int minPipe = 2;
        int maxPipe = 3;
        int state = 0;
        while (state != 3) {
            if (this.mActive == STATE_CANCEL_REQUESTED) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
                this.mActive = STATE_NORMAL;
            }
            switch (state) {
                case 0:
                    if (pipe.size() == maxPipe) {
                        state = 1;
                        break;
                    } else {
                        if (firstRequest == null) {
                            req = this.mRequestFeeder.getRequest(this.mHost);
                        } else {
                            req = firstRequest;
                            firstRequest = null;
                        }
                        if (req == null) {
                            state = 2;
                            break;
                        } else {
                            req.setConnection(this);
                            if (req.mCancelled) {
                                req.complete();
                                break;
                            } else if ((this.mHttpClientConnection == null || !this.mHttpClientConnection.isOpen()) && !openHttpConnection(req)) {
                                state = 3;
                                break;
                            } else {
                                req.mEventHandler.certificate(this.mCertificate);
                                try {
                                    req.sendRequest(this.mHttpClientConnection);
                                } catch (IOException e2) {
                                    exception = e2;
                                    error = -7;
                                } catch (IllegalStateException e3) {
                                    exception = e3;
                                    error = -7;
                                } catch (HttpException e4) {
                                    exception = e4;
                                    error = -1;
                                }
                                if (exception != null) {
                                    if (httpFailure(req, error, exception) && !req.mCancelled) {
                                        pipe.addLast(req);
                                    }
                                    exception = null;
                                    state = clearPipe(pipe) ? 3 : 0;
                                    maxPipe = 1;
                                    minPipe = 1;
                                    break;
                                } else {
                                    pipe.addLast(req);
                                    if (!this.mCanPersist) {
                                        state = 1;
                                        break;
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 1:
                case 2:
                    boolean empty = !this.mRequestFeeder.haveRequest(this.mHost);
                    int pipeSize = pipe.size();
                    if (state != 2 && pipeSize < minPipe && !empty && this.mCanPersist) {
                        state = 0;
                        break;
                    } else if (pipeSize == 0) {
                        state = empty ? 3 : 0;
                        break;
                    } else {
                        Request req2 = pipe.removeFirst();
                        try {
                            req2.readResponse(this.mHttpClientConnection);
                        } catch (IOException e5) {
                            exception = e5;
                            error = -7;
                        } catch (IllegalStateException e6) {
                            exception = e6;
                            error = -7;
                        } catch (ParseException e7) {
                            exception = e7;
                            error = -7;
                        }
                        if (exception != null) {
                            if (httpFailure(req2, error, exception) && !req2.mCancelled) {
                                req2.reset();
                                pipe.addFirst(req2);
                            }
                            exception = null;
                            this.mCanPersist = false;
                        }
                        if (!this.mCanPersist) {
                            closeConnection();
                            this.mHttpContext.removeAttribute("http.connection");
                            clearPipe(pipe);
                            maxPipe = 1;
                            minPipe = 1;
                            state = 0;
                            break;
                        } else {
                            break;
                        }
                    }
                    break;
            }
        }
    }

    private boolean clearPipe(LinkedList<Request> pipe) {
        boolean empty = true;
        synchronized (this.mRequestFeeder) {
            while (!pipe.isEmpty()) {
                Request tReq = pipe.removeLast();
                this.mRequestFeeder.requeueRequest(tReq);
                empty = false;
            }
            if (empty) {
                empty = !this.mRequestFeeder.haveRequest(this.mHost);
            }
        }
        return empty;
    }

    private boolean openHttpConnection(Request req) {
        SystemClock.uptimeMillis();
        int error = 0;
        Exception exception = null;
        try {
            this.mCertificate = null;
            this.mHttpClientConnection = openConnection(req);
        } catch (SSLConnectionClosedByUserException e) {
            req.mFailCount = 2;
            return false;
        } catch (IOException e2) {
            error = -6;
            exception = e2;
        } catch (IllegalArgumentException e3) {
            error = -6;
            req.mFailCount = 2;
            exception = e3;
        } catch (UnknownHostException e4) {
            error = -2;
            exception = e4;
        } catch (SSLHandshakeException e5) {
            req.mFailCount = 2;
            error = -11;
            exception = e5;
        }
        if (this.mHttpClientConnection == null) {
            req.mFailCount = 2;
            return false;
        }
        this.mHttpClientConnection.setSocketTimeout(60000);
        this.mHttpContext.setAttribute("http.connection", this.mHttpClientConnection);
        if (error == 0) {
            return true;
        }
        if (req.mFailCount < 2) {
            this.mRequestFeeder.requeueRequest(req);
            req.mFailCount++;
        } else {
            httpFailure(req, error, exception);
        }
        return error == 0;
    }

    private boolean httpFailure(Request req, int errorId, Exception e) {
        String error;
        boolean ret = true;
        int i = req.mFailCount + 1;
        req.mFailCount = i;
        if (i >= 2) {
            ret = false;
            if (errorId < 0) {
                error = ErrorStrings.getString(errorId, this.mContext);
            } else {
                Throwable cause = e.getCause();
                error = cause != null ? cause.toString() : e.getMessage();
            }
            req.mEventHandler.error(errorId, error);
            req.complete();
        }
        closeConnection();
        this.mHttpContext.removeAttribute("http.connection");
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpContext getHttpContext() {
        return this.mHttpContext;
    }

    private boolean keepAlive(HttpEntity entity, ProtocolVersion ver, int connType, HttpContext context) {
        org.apache.http.HttpConnection conn = (org.apache.http.HttpConnection) context.getAttribute("http.connection");
        if (conn != null && !conn.isOpen()) {
            return false;
        }
        if ((entity == null || entity.getContentLength() >= 0 || (entity.isChunked() && !ver.lessEquals(HttpVersion.HTTP_1_0))) && connType != 1) {
            return connType == 2 || !ver.lessEquals(HttpVersion.HTTP_1_0);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanPersist(HttpEntity entity, ProtocolVersion ver, int connType) {
        this.mCanPersist = keepAlive(entity, ver, connType, this.mHttpContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanPersist(boolean canPersist) {
        this.mCanPersist = canPersist;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getCanPersist() {
        return this.mCanPersist;
    }

    public synchronized String toString() {
        return this.mHost.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getBuf() {
        if (this.mBuf == null) {
            this.mBuf = new byte[8192];
        }
        return this.mBuf;
    }
}