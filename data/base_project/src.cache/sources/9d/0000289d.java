package javax.xml.transform.sax;

import javax.xml.transform.Templates;
import org.xml.sax.ContentHandler;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TemplatesHandler.class */
public interface TemplatesHandler extends ContentHandler {
    Templates getTemplates();

    void setSystemId(String str);

    String getSystemId();
}