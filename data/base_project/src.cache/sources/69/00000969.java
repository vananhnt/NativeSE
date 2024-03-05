package android.net.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.WebAddress;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.http.HttpHost;

/* loaded from: RequestQueue.class */
public class RequestQueue implements RequestFeeder {
    private final LinkedHashMap<HttpHost, LinkedList<Request>> mPending;
    private final Context mContext;
    private final ActivePool mActivePool;
    private final ConnectivityManager mConnectivityManager;
    private HttpHost mProxyHost;
    private BroadcastReceiver mProxyChangeReceiver;
    private static final int CONNECTION_COUNT = 4;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: RequestQueue$ConnectionManager.class */
    public interface ConnectionManager {
        HttpHost getProxyHost();

        Connection getConnection(Context context, HttpHost httpHost);

        boolean recycleConnection(Connection connection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: RequestQueue$ActivePool.class */
    public class ActivePool implements ConnectionManager {
        ConnectionThread[] mThreads;
        IdleCache mIdleCache = new IdleCache();
        private int mTotalRequest;
        private int mTotalConnection;
        private int mConnectionCount;

        static /* synthetic */ int access$408(ActivePool x0) {
            int i = x0.mTotalRequest;
            x0.mTotalRequest = i + 1;
            return i;
        }

        ActivePool(int connectionCount) {
            this.mConnectionCount = connectionCount;
            this.mThreads = new ConnectionThread[this.mConnectionCount];
            for (int i = 0; i < this.mConnectionCount; i++) {
                this.mThreads[i] = new ConnectionThread(RequestQueue.this.mContext, i, this, RequestQueue.this);
            }
        }

        void startup() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                this.mThreads[i].start();
            }
        }

        void shutdown() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                this.mThreads[i].requestStop();
            }
        }

        void startConnectionThread() {
            synchronized (RequestQueue.this) {
                RequestQueue.this.notify();
            }
        }

        public void startTiming() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                ConnectionThread rt = this.mThreads[i];
                rt.mCurrentThreadTime = -1L;
                rt.mTotalThreadTime = 0L;
            }
            this.mTotalRequest = 0;
            this.mTotalConnection = 0;
        }

        public void stopTiming() {
            int totalTime = 0;
            for (int i = 0; i < this.mConnectionCount; i++) {
                ConnectionThread rt = this.mThreads[i];
                if (rt.mCurrentThreadTime != -1) {
                    totalTime = (int) (totalTime + rt.mTotalThreadTime);
                }
                rt.mCurrentThreadTime = 0L;
            }
            Log.d("Http", "Http thread used " + totalTime + " ms  for " + this.mTotalRequest + " requests and " + this.mTotalConnection + " new connections");
        }

        void logState() {
            StringBuilder dump = new StringBuilder();
            for (int i = 0; i < this.mConnectionCount; i++) {
                dump.append(this.mThreads[i] + Separators.RETURN);
            }
            HttpLog.v(dump.toString());
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public HttpHost getProxyHost() {
            return RequestQueue.this.mProxyHost;
        }

        void disablePersistence() {
            for (int i = 0; i < this.mConnectionCount; i++) {
                Connection connection = this.mThreads[i].mConnection;
                if (connection != null) {
                    connection.setCanPersist(false);
                }
            }
            this.mIdleCache.clear();
        }

        ConnectionThread getThread(HttpHost host) {
            synchronized (RequestQueue.this) {
                for (int i = 0; i < this.mThreads.length; i++) {
                    ConnectionThread ct = this.mThreads[i];
                    Connection connection = ct.mConnection;
                    if (connection != null && connection.mHost.equals(host)) {
                        return ct;
                    }
                }
                return null;
            }
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public Connection getConnection(Context context, HttpHost host) {
            HttpHost host2 = RequestQueue.this.determineHost(host);
            Connection con = this.mIdleCache.getConnection(host2);
            if (con == null) {
                this.mTotalConnection++;
                con = Connection.getConnection(RequestQueue.this.mContext, host2, RequestQueue.this.mProxyHost, RequestQueue.this);
            }
            return con;
        }

        @Override // android.net.http.RequestQueue.ConnectionManager
        public boolean recycleConnection(Connection connection) {
            return this.mIdleCache.cacheConnection(connection.getHost(), connection);
        }
    }

    public RequestQueue(Context context) {
        this(context, 4);
    }

    public RequestQueue(Context context, int connectionCount) {
        this.mProxyHost = null;
        this.mContext = context;
        this.mPending = new LinkedHashMap<>(32);
        this.mActivePool = new ActivePool(connectionCount);
        this.mActivePool.startup();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public synchronized void enablePlatformNotifications() {
        if (this.mProxyChangeReceiver == null) {
            this.mProxyChangeReceiver = new BroadcastReceiver() { // from class: android.net.http.RequestQueue.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context ctx, Intent intent) {
                    RequestQueue.this.setProxyConfig();
                }
            };
            this.mContext.registerReceiver(this.mProxyChangeReceiver, new IntentFilter(Proxy.PROXY_CHANGE_ACTION));
        }
        setProxyConfig();
    }

    public synchronized void disablePlatformNotifications() {
        if (this.mProxyChangeReceiver != null) {
            this.mContext.unregisterReceiver(this.mProxyChangeReceiver);
            this.mProxyChangeReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setProxyConfig() {
        NetworkInfo info = this.mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.getType() == 1) {
            this.mProxyHost = null;
            return;
        }
        String host = Proxy.getHost(this.mContext);
        if (host == null) {
            this.mProxyHost = null;
            return;
        }
        this.mActivePool.disablePersistence();
        this.mProxyHost = new HttpHost(host, Proxy.getPort(this.mContext), "http");
    }

    public HttpHost getProxyHost() {
        return this.mProxyHost;
    }

    public RequestHandle queueRequest(String url, String method, Map<String, String> headers, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        WebAddress uri = new WebAddress(url);
        return queueRequest(url, uri, method, headers, eventHandler, bodyProvider, bodyLength);
    }

    public RequestHandle queueRequest(String url, WebAddress uri, String method, Map<String, String> headers, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        if (eventHandler == null) {
            eventHandler = new LoggingEventHandler();
        }
        HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        Request req = new Request(method, httpHost, this.mProxyHost, uri.getPath(), bodyProvider, bodyLength, eventHandler, headers);
        queueRequest(req, false);
        ActivePool.access$408(this.mActivePool);
        this.mActivePool.startConnectionThread();
        return new RequestHandle(this, url, uri, method, headers, bodyProvider, bodyLength, req);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RequestQueue$SyncFeeder.class */
    public static class SyncFeeder implements RequestFeeder {
        private Request mRequest;

        SyncFeeder() {
        }

        @Override // android.net.http.RequestFeeder
        public Request getRequest() {
            Request r = this.mRequest;
            this.mRequest = null;
            return r;
        }

        @Override // android.net.http.RequestFeeder
        public Request getRequest(HttpHost host) {
            return getRequest();
        }

        @Override // android.net.http.RequestFeeder
        public boolean haveRequest(HttpHost host) {
            return this.mRequest != null;
        }

        @Override // android.net.http.RequestFeeder
        public void requeueRequest(Request r) {
            this.mRequest = r;
        }
    }

    public RequestHandle queueSynchronousRequest(String url, WebAddress uri, String method, Map<String, String> headers, EventHandler eventHandler, InputStream bodyProvider, int bodyLength) {
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        Request req = new Request(method, host, this.mProxyHost, uri.getPath(), bodyProvider, bodyLength, eventHandler, headers);
        Connection conn = Connection.getConnection(this.mContext, determineHost(host), this.mProxyHost, new SyncFeeder());
        return new RequestHandle(this, url, uri, method, headers, bodyProvider, bodyLength, req, conn);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public HttpHost determineHost(HttpHost host) {
        return (this.mProxyHost == null || "https".equals(host.getSchemeName())) ? host : this.mProxyHost;
    }

    synchronized boolean requestsPending() {
        return !this.mPending.isEmpty();
    }

    synchronized void dump() {
        HttpLog.v("dump()");
        StringBuilder dump = new StringBuilder();
        int count = 0;
        if (!this.mPending.isEmpty()) {
            Iterator<Map.Entry<HttpHost, LinkedList<Request>>> iter = this.mPending.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<HttpHost, LinkedList<Request>> entry = iter.next();
                String hostName = entry.getKey().getHostName();
                int i = count;
                count++;
                StringBuilder line = new StringBuilder("p" + i + Separators.SP + hostName + Separators.SP);
                LinkedList<Request> reqList = entry.getValue();
                reqList.listIterator(0);
                while (iter.hasNext()) {
                    Request request = (Request) iter.next();
                    line.append(request + Separators.SP);
                }
                dump.append((CharSequence) line);
                dump.append(Separators.RETURN);
            }
        }
        HttpLog.v(dump.toString());
    }

    @Override // android.net.http.RequestFeeder
    public synchronized Request getRequest() {
        Request ret = null;
        if (!this.mPending.isEmpty()) {
            ret = removeFirst(this.mPending);
        }
        return ret;
    }

    @Override // android.net.http.RequestFeeder
    public synchronized Request getRequest(HttpHost host) {
        Request ret = null;
        if (this.mPending.containsKey(host)) {
            LinkedList<Request> reqList = this.mPending.get(host);
            ret = reqList.removeFirst();
            if (reqList.isEmpty()) {
                this.mPending.remove(host);
            }
        }
        return ret;
    }

    @Override // android.net.http.RequestFeeder
    public synchronized boolean haveRequest(HttpHost host) {
        return this.mPending.containsKey(host);
    }

    @Override // android.net.http.RequestFeeder
    public void requeueRequest(Request request) {
        queueRequest(request, true);
    }

    public void shutdown() {
        this.mActivePool.shutdown();
    }

    protected synchronized void queueRequest(Request request, boolean head) {
        LinkedList<Request> reqList;
        HttpHost host = request.mProxyHost == null ? request.mHost : request.mProxyHost;
        if (this.mPending.containsKey(host)) {
            reqList = this.mPending.get(host);
        } else {
            reqList = new LinkedList<>();
            this.mPending.put(host, reqList);
        }
        if (head) {
            reqList.addFirst(request);
        } else {
            reqList.add(request);
        }
    }

    public void startTiming() {
        this.mActivePool.startTiming();
    }

    public void stopTiming() {
        this.mActivePool.stopTiming();
    }

    private Request removeFirst(LinkedHashMap<HttpHost, LinkedList<Request>> requestQueue) {
        Request ret = null;
        Iterator<Map.Entry<HttpHost, LinkedList<Request>>> iter = requestQueue.entrySet().iterator();
        if (iter.hasNext()) {
            Map.Entry<HttpHost, LinkedList<Request>> entry = iter.next();
            LinkedList<Request> reqList = entry.getValue();
            ret = reqList.removeFirst();
            if (reqList.isEmpty()) {
                requestQueue.remove(entry.getKey());
            }
        }
        return ret;
    }
}