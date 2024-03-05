package java.net;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Authenticator.class */
public abstract class Authenticator {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Authenticator$RequestorType.class */
    public enum RequestorType {
        PROXY,
        SERVER
    }

    public Authenticator() {
        throw new RuntimeException("Stub!");
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        throw new RuntimeException("Stub!");
    }

    protected final int getRequestingPort() {
        throw new RuntimeException("Stub!");
    }

    protected final InetAddress getRequestingSite() {
        throw new RuntimeException("Stub!");
    }

    protected final String getRequestingPrompt() {
        throw new RuntimeException("Stub!");
    }

    protected final String getRequestingProtocol() {
        throw new RuntimeException("Stub!");
    }

    protected final String getRequestingScheme() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized PasswordAuthentication requestPasswordAuthentication(InetAddress rAddr, int rPort, String rProtocol, String rPrompt, String rScheme) {
        throw new RuntimeException("Stub!");
    }

    public static void setDefault(Authenticator a) {
        throw new RuntimeException("Stub!");
    }

    public static synchronized PasswordAuthentication requestPasswordAuthentication(String rHost, InetAddress rAddr, int rPort, String rProtocol, String rPrompt, String rScheme) {
        throw new RuntimeException("Stub!");
    }

    protected final String getRequestingHost() {
        throw new RuntimeException("Stub!");
    }

    public static PasswordAuthentication requestPasswordAuthentication(String rHost, InetAddress rAddr, int rPort, String rProtocol, String rPrompt, String rScheme, URL rURL, RequestorType reqType) {
        throw new RuntimeException("Stub!");
    }

    protected URL getRequestingURL() {
        throw new RuntimeException("Stub!");
    }

    protected RequestorType getRequestorType() {
        throw new RuntimeException("Stub!");
    }
}