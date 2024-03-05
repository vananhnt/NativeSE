package javax.net.ssl;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.net.SocketFactory;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLSocketFactory.class */
public abstract class SSLSocketFactory extends SocketFactory {
    private static SocketFactory defaultSocketFactory;
    private static String defaultName;

    public abstract String[] getDefaultCipherSuites();

    public abstract String[] getSupportedCipherSuites();

    public abstract Socket createSocket(Socket socket, String str, int i, boolean z) throws IOException;

    public static synchronized SocketFactory getDefault() {
        SSLContext context;
        if (defaultSocketFactory != null) {
            return defaultSocketFactory;
        }
        if (defaultName == null) {
            defaultName = Security.getProperty("ssl.SocketFactory.provider");
            if (defaultName != null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }
                try {
                    Class<?> sfc = Class.forName(defaultName, true, cl);
                    defaultSocketFactory = (SocketFactory) sfc.newInstance();
                } catch (Exception e) {
                    System.logE("Problem creating " + defaultName, e);
                }
            }
        }
        if (defaultSocketFactory == null) {
            try {
                context = SSLContext.getDefault();
            } catch (NoSuchAlgorithmException e2) {
                context = null;
            }
            if (context != null) {
                defaultSocketFactory = context.getSocketFactory();
            }
        }
        if (defaultSocketFactory == null) {
            defaultSocketFactory = new DefaultSSLSocketFactory("No SSLSocketFactory installed");
        }
        return defaultSocketFactory;
    }
}