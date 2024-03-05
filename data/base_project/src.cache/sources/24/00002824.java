package javax.sip.address;

import java.text.ParseException;
import java.util.Iterator;
import javax.sip.InvalidArgumentException;
import javax.sip.header.Parameters;

/* loaded from: SipURI.class */
public interface SipURI extends URI, Parameters {
    boolean isSecure();

    void setSecure(boolean z);

    String getHeader(String str);

    void setHeader(String str, String str2);

    Iterator getHeaderNames();

    String getHost();

    void setHost(String str) throws ParseException;

    String getLrParam();

    void setLrParam();

    boolean hasLrParam();

    String getMAddrParam();

    void setMAddrParam(String str) throws ParseException;

    int getPort();

    void setPort(int i) throws InvalidArgumentException;

    int getTTLParam();

    void setTTLParam(int i);

    String getTransportParam();

    void setTransportParam(String str) throws ParseException;

    boolean hasTransport();

    String getUser();

    void setUser(String str);

    String getUserParam();

    void setUserParam(String str);

    String getUserType();

    void removeUserType();

    String getUserPassword();

    void setUserPassword(String str);

    String getUserAtHost();

    String getUserAtHostPort();

    String getMethodParam();

    void setMethodParam(String str) throws ParseException;
}