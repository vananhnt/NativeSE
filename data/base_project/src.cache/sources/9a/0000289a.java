package javax.xml.transform.sax;

import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SAXResult.class */
public class SAXResult implements Result {
    public static final String FEATURE = "http://javax.xml.transform.sax.SAXResult/feature";
    private ContentHandler handler;
    private LexicalHandler lexhandler;
    private String systemId;

    public SAXResult() {
    }

    public SAXResult(ContentHandler handler) {
        setHandler(handler);
    }

    public void setHandler(ContentHandler handler) {
        this.handler = handler;
    }

    public ContentHandler getHandler() {
        return this.handler;
    }

    public void setLexicalHandler(LexicalHandler handler) {
        this.lexhandler = handler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexhandler;
    }

    @Override // javax.xml.transform.Result
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override // javax.xml.transform.Result
    public String getSystemId() {
        return this.systemId;
    }
}