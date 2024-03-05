package libcore.net.url;

import android.net.ProxyProperties;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/* loaded from: FileHandler.class */
public class FileHandler extends URLStreamHandler {
    @Override // java.net.URLStreamHandler
    public URLConnection openConnection(URL url) throws IOException {
        return openConnection(url, null);
    }

    @Override // java.net.URLStreamHandler
    public URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        String host = url.getHost();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(ProxyProperties.LOCAL_HOST)) {
            return new FileURLConnection(url);
        }
        URL ftpURL = new URL("ftp", host, url.getFile());
        return proxy == null ? ftpURL.openConnection() : ftpURL.openConnection(proxy);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.net.URLStreamHandler
    public void parseURL(URL url, String spec, int start, int end) {
        if (end < start) {
            return;
        }
        String parseString = "";
        if (start < end) {
            parseString = spec.substring(start, end).replace('\\', '/');
        }
        super.parseURL(url, parseString, 0, parseString.length());
    }
}