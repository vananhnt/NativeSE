package gov.nist.javax.sip.header.extensions;

import java.text.ParseException;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: ReplacesHeader.class */
public interface ReplacesHeader extends Header, Parameters {
    public static final String NAME = "Replaces";

    String getToTag();

    void setToTag(String str) throws ParseException;

    String getFromTag();

    void setFromTag(String str) throws ParseException;

    String getCallId();

    void setCallId(String str) throws ParseException;
}