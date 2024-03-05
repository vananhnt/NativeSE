package android.net;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.net.IProxyService;
import com.google.android.collect.Lists;
import gov.nist.core.Separators;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/* loaded from: PacProxySelector.class */
public class PacProxySelector extends ProxySelector {
    private static final String TAG = "PacProxySelector";
    public static final String PROXY_SERVICE = "com.android.net.IProxyService";
    private IProxyService mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService("com.android.net.IProxyService"));
    private final List<java.net.Proxy> mDefaultList;

    public PacProxySelector() {
        if (this.mProxyService == null) {
            Log.e(TAG, "PacManager: no proxy service");
        }
        this.mDefaultList = Lists.newArrayList(new java.net.Proxy[]{java.net.Proxy.NO_PROXY});
    }

    @Override // java.net.ProxySelector
    public List<java.net.Proxy> select(URI uri) {
        String urlString;
        if (this.mProxyService == null) {
            this.mProxyService = IProxyService.Stub.asInterface(ServiceManager.getService("com.android.net.IProxyService"));
        }
        if (this.mProxyService == null) {
            Log.e(TAG, "select: no proxy service return NO_PROXY");
            return Lists.newArrayList(new java.net.Proxy[]{java.net.Proxy.NO_PROXY});
        }
        String response = null;
        try {
            urlString = uri.toURL().toString();
        } catch (MalformedURLException e) {
            urlString = uri.getHost();
        }
        try {
            response = this.mProxyService.resolvePacFile(uri.getHost(), urlString);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
        if (response == null) {
            return this.mDefaultList;
        }
        return parseResponse(response);
    }

    private static List<java.net.Proxy> parseResponse(String response) {
        int port;
        String[] split = response.split(Separators.SEMICOLON);
        List<java.net.Proxy> ret = Lists.newArrayList();
        for (String s : split) {
            String trimmed = s.trim();
            if (trimmed.equals("DIRECT")) {
                ret.add(java.net.Proxy.NO_PROXY);
            } else if (trimmed.startsWith("PROXY ")) {
                String[] hostPort = trimmed.substring(6).split(Separators.COLON);
                String host = hostPort[0];
                try {
                    port = Integer.parseInt(hostPort[1]);
                } catch (Exception e) {
                    port = 8080;
                }
                ret.add(new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
            }
        }
        if (ret.size() == 0) {
            ret.add(java.net.Proxy.NO_PROXY);
        }
        return ret;
    }

    @Override // java.net.ProxySelector
    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
    }
}