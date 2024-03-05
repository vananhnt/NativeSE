package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.xml.sax.XMLFilter;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SAXTransformerFactory.class */
public abstract class SAXTransformerFactory extends TransformerFactory {
    public static final String FEATURE = "http://javax.xml.transform.sax.SAXTransformerFactory/feature";
    public static final String FEATURE_XMLFILTER = "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter";

    public abstract TransformerHandler newTransformerHandler(Source source) throws TransformerConfigurationException;

    public abstract TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException;

    public abstract TransformerHandler newTransformerHandler() throws TransformerConfigurationException;

    public abstract TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException;

    public abstract XMLFilter newXMLFilter(Source source) throws TransformerConfigurationException;

    public abstract XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException;

    protected SAXTransformerFactory() {
    }
}