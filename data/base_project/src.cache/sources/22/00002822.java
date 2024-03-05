package javax.sip.address;

/* loaded from: Hop.class */
public interface Hop {
    String getHost();

    int getPort();

    String getTransport();

    boolean isURIRoute();

    void setURIRouteFlag();

    String toString();
}