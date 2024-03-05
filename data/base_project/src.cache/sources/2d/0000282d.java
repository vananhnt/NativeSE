package javax.sip.header;

import java.text.ParseException;

/* loaded from: AuthenticationInfoHeader.class */
public interface AuthenticationInfoHeader extends Header, Parameters {
    public static final String NAME = "Authentication-Info";

    String getCNonce();

    void setCNonce(String str) throws ParseException;

    String getNextNonce();

    void setNextNonce(String str) throws ParseException;

    int getNonceCount();

    void setNonceCount(int i) throws ParseException;

    String getQop();

    void setQop(String str) throws ParseException;

    String getResponse();

    void setResponse(String str) throws ParseException;
}