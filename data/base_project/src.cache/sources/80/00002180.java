package gov.nist.javax.sip.header.extensions;

import java.text.ParseException;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: JoinHeader.class */
public interface JoinHeader extends Parameters, Header {
    public static final String NAME = "Join";

    void setToTag(String str) throws ParseException;

    void setFromTag(String str) throws ParseException;

    String getToTag();

    String getFromTag();

    void setCallId(String str) throws ParseException;

    String getCallId();
}