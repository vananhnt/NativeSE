package javax.sip.header;

import java.text.ParseException;

/* loaded from: ContentDispositionHeader.class */
public interface ContentDispositionHeader extends Header, Parameters {
    public static final String NAME = "Content-Disposition";
    public static final String RENDER = "Render";
    public static final String SESSION = "Session";
    public static final String ICON = "Icon";
    public static final String ALERT = "Alert";

    String getDispositionType();

    void setDispositionType(String str) throws ParseException;

    String getHandling();

    void setHandling(String str) throws ParseException;
}