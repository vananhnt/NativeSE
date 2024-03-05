package javax.sip.header;

import java.text.ParseException;
import javax.sip.address.URI;

/* loaded from: ErrorInfoHeader.class */
public interface ErrorInfoHeader extends Header, Parameters {
    public static final String NAME = "Error-Info";

    URI getErrorInfo();

    void setErrorInfo(URI uri);

    String getErrorMessage();

    void setErrorMessage(String str) throws ParseException;
}