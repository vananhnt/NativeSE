package android.net;

import android.content.Context;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.HttpContext;

/* loaded from: Proxy.class */
public final class Proxy {
    private static final boolean DEBUG = false;
    private static final String TAG = "Proxy";
    public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
    public static final String EXTRA_PROXY_INFO = "proxy";
    private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
    private static ConnectivityManager sConnectivityManager = null;
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
    private static final String EXCLLIST_REGEXP = "$|^(.?[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*)+(,(.?[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*))*$";
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile(EXCLLIST_REGEXP);
    private static final ProxySelector sDefaultProxySelector = ProxySelector.getDefault();

    public static final java.net.Proxy getProxy(Context ctx, String url) {
        String host = "";
        if (url != null) {
            URI uri = URI.create(url);
            host = uri.getHost();
        }
        if (!isLocalHost(host)) {
            if (sConnectivityManager == null) {
                sConnectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            if (sConnectivityManager == null) {
                return java.net.Proxy.NO_PROXY;
            }
            ProxyProperties proxyProperties = sConnectivityManager.getProxy();
            if (proxyProperties != null && !proxyProperties.isExcluded(host)) {
                return proxyProperties.makeProxy();
            }
        }
        return java.net.Proxy.NO_PROXY;
    }

    public static final String getHost(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return null;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    public static final int getPort(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return -1;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getPort();
        } catch (Exception e) {
            return -1;
        }
    }

    public static final String getDefaultHost() {
        String host = System.getProperty("http.proxyHost");
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        return host;
    }

    public static final int getDefaultPort() {
        if (getDefaultHost() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(System.getProperty("http.proxyPort"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static final HttpHost getPreferredHttpHost(Context context, String url) {
        java.net.Proxy prefProxy = getProxy(context, url);
        if (prefProxy.equals(java.net.Proxy.NO_PROXY)) {
            return null;
        }
        InetSocketAddress sa = (InetSocketAddress) prefProxy.address();
        return new HttpHost(sa.getHostName(), sa.getPort(), "http");
    }

    private static final boolean isLocalHost(String host) {
        if (host != null && host != null) {
            try {
                if (host.equalsIgnoreCase(ProxyProperties.LOCAL_HOST)) {
                    return true;
                }
                if (NetworkUtils.numericToInetAddress(host).isLoopbackAddress()) {
                    return true;
                }
                return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    public static void validate(String hostname, String port, String exclList) {
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        Matcher listMatch = EXCLLIST_PATTERN.matcher(exclList);
        if (!match.matches()) {
            throw new IllegalArgumentException();
        }
        if (!listMatch.matches()) {
            throw new IllegalArgumentException();
        }
        if (hostname.length() > 0 && port.length() == 0) {
            throw new IllegalArgumentException();
        }
        if (port.length() > 0) {
            if (hostname.length() == 0) {
                throw new IllegalArgumentException();
            }
            try {
                int portVal = Integer.parseInt(port);
                if (portVal <= 0 || portVal > 65535) {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        }
    }

    /* loaded from: Proxy$AndroidProxySelectorRoutePlanner.class */
    static class AndroidProxySelectorRoutePlanner extends ProxySelectorRoutePlanner {
        private Context mContext;

        public AndroidProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel, Context context) {
            super(schreg, prosel);
            this.mContext = context;
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner
        protected java.net.Proxy chooseProxy(List<java.net.Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context) {
            return Proxy.getProxy(this.mContext, target.getHostName());
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner
        protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) {
            return Proxy.getPreferredHttpHost(this.mContext, target.getHostName());
        }

        @Override // org.apache.http.impl.conn.ProxySelectorRoutePlanner, org.apache.http.conn.routing.HttpRoutePlanner
        public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) {
            HttpHost proxy = Proxy.getPreferredHttpHost(this.mContext, target.getHostName());
            if (proxy == null) {
                return new HttpRoute(target);
            }
            return new HttpRoute(target, null, proxy, false);
        }
    }

    public static final HttpRoutePlanner getAndroidProxySelectorRoutePlanner(Context context) {
        AndroidProxySelectorRoutePlanner ret = new AndroidProxySelectorRoutePlanner(new SchemeRegistry(), ProxySelector.getDefault(), context);
        return ret;
    }

    public static final void setHttpProxySystemProperty(ProxyProperties p) {
        String host = null;
        String port = null;
        String exclList = null;
        String pacFileUrl = null;
        if (p != null) {
            host = p.getHost();
            port = Integer.toString(p.getPort());
            exclList = p.getExclusionList();
            pacFileUrl = p.getPacFileUrl();
        }
        setHttpProxySystemProperty(host, port, exclList, pacFileUrl);
    }

    public static final void setHttpProxySystemProperty(String host, String port, String exclList, String pacFileUrl) {
        if (exclList != null) {
            exclList = exclList.replace(Separators.COMMA, "|");
        }
        if (host != null) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("https.proxyHost", host);
        } else {
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
        }
        if (port != null) {
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyPort", port);
        } else {
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        if (exclList != null) {
            System.setProperty("http.nonProxyHosts", exclList);
            System.setProperty("https.nonProxyHosts", exclList);
        } else {
            System.clearProperty("http.nonProxyHosts");
            System.clearProperty("https.nonProxyHosts");
        }
        if (!TextUtils.isEmpty(pacFileUrl)) {
            ProxySelector.setDefault(new PacProxySelector());
        } else {
            ProxySelector.setDefault(sDefaultProxySelector);
        }
    }
}