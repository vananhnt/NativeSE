package javax.sip.header;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/* loaded from: ViaHeader.class */
public interface ViaHeader extends Header, Parameters {
    public static final String NAME = "Via";

    String getBranch();

    void setBranch(String str) throws ParseException;

    String getHost();

    void setHost(String str) throws ParseException;

    String getMAddr();

    void setMAddr(String str) throws ParseException;

    int getPort();

    void setPort(int i) throws InvalidArgumentException;

    String getProtocol();

    void setProtocol(String str) throws ParseException;

    String getReceived();

    void setReceived(String str) throws ParseException;

    int getRPort();

    void setRPort() throws InvalidArgumentException;

    String getTransport();

    void setTransport(String str) throws ParseException;

    int getTTL();

    void setTTL(int i) throws InvalidArgumentException;

    String getSentByField();

    String getSentProtocolField();
}