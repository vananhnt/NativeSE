package javax.sip;

import java.io.IOException;
import java.text.ParseException;
import javax.sip.header.ContactHeader;

/* loaded from: ListeningPoint.class */
public interface ListeningPoint extends Cloneable {
    public static final String TCP = "TCP";
    public static final String UDP = "UDP";
    public static final String SCTP = "SCTP";
    public static final String TLS = "TLS";
    public static final int PORT_5060 = 5060;
    public static final int PORT_5061 = 5061;

    String getIPAddress();

    int getPort();

    String getTransport();

    String getSentBy();

    void setSentBy(String str) throws ParseException;

    ContactHeader createContactHeader();

    void sendHeartbeat(String str, int i) throws IOException;
}