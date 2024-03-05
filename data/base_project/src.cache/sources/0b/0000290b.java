package libcore.net.url;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/* loaded from: FtpHandler.class */
public class FtpHandler extends URLStreamHandler {
    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL u) throws IOException {
        return new FtpURLConnection(u);
    }

    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url == null || proxy == null) {
            throw new IllegalArgumentException("url == null || proxy == null");
        }
        return new FtpURLConnection(url, proxy);
    }

    @Override // java.net.URLStreamHandler
    protected int getDefaultPort() {
        return 21;
    }
}