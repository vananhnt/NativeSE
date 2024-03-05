package javax.net.ssl;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HostnameVerifier.class */
public interface HostnameVerifier {
    boolean verify(String str, SSLSession sSLSession);
}