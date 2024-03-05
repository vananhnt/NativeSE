package javax.sip.header;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/* loaded from: ReasonHeader.class */
public interface ReasonHeader extends Header, Parameters {
    public static final String NAME = "Reason";

    int getCause();

    void setCause(int i) throws InvalidArgumentException;

    String getProtocol();

    void setProtocol(String str) throws ParseException;

    String getText();

    void setText(String str) throws ParseException;
}