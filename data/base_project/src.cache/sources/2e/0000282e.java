package javax.sip.header;

import java.text.ParseException;
import javax.sip.address.URI;

/* loaded from: AuthorizationHeader.class */
public interface AuthorizationHeader extends Header, Parameters {
    public static final String NAME = "Authorization";

    String getAlgorithm();

    void setAlgorithm(String str) throws ParseException;

    String getCNonce();

    void setCNonce(String str) throws ParseException;

    String getNonce();

    void setNonce(String str) throws ParseException;

    int getNonceCount();

    void setNonceCount(int i) throws ParseException;

    String getOpaque();

    void setOpaque(String str) throws ParseException;

    String getQop();

    void setQop(String str) throws ParseException;

    String getRealm();

    void setRealm(String str) throws ParseException;

    String getResponse();

    void setResponse(String str) throws ParseException;

    String getScheme();

    void setScheme(String str);

    boolean isStale();

    void setStale(boolean z);

    URI getURI();

    void setURI(URI uri);

    String getUsername();

    void setUsername(String str) throws ParseException;
}