package javax.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.net.ServerSocketFactory;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLServerSocketFactory.class */
public abstract class SSLServerSocketFactory extends ServerSocketFactory {
    private static ServerSocketFactory defaultServerSocketFactory;
    private static String defaultName;

    public abstract String[] getDefaultCipherSuites();

    public abstract String[] getSupportedCipherSuites();

    public static synchronized ServerSocketFactory getDefault() {
        SSLContext context;
        if (defaultServerSocketFactory != null) {
            return defaultServerSocketFactory;
        }
        if (defaultName == null) {
            defaultName = Security.getProperty("ssl.ServerSocketFactory.provider");
            if (defaultName != null) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }
                try {
                    Class<?> ssfc = Class.forName(defaultName, true, cl);
                    defaultServerSocketFactory = (ServerSocketFactory) ssfc.newInstance();
                } catch (Exception e) {
                }
            }
        }
        if (defaultServerSocketFactory == null) {
            try {
                context = SSLContext.getDefault();
            } catch (NoSuchAlgorithmException e2) {
                context = null;
            }
            if (context != null) {
                defaultServerSocketFactory = context.getServerSocketFactory();
            }
        }
        if (defaultServerSocketFactory == null) {
            defaultServerSocketFactory = new DefaultSSLServerSocketFactory("No ServerSocketFactory installed");
        }
        return defaultServerSocketFactory;
    }
}