package javax.xml.transform;

import gov.nist.core.Separators;
import java.util.Properties;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Transformer.class */
public abstract class Transformer {
    public abstract void transform(Source source, Result result) throws TransformerException;

    public abstract void setParameter(String str, Object obj);

    public abstract Object getParameter(String str);

    public abstract void clearParameters();

    public abstract void setURIResolver(URIResolver uRIResolver);

    public abstract URIResolver getURIResolver();

    public abstract void setOutputProperties(Properties properties);

    public abstract Properties getOutputProperties();

    public abstract void setOutputProperty(String str, String str2) throws IllegalArgumentException;

    public abstract String getOutputProperty(String str) throws IllegalArgumentException;

    public abstract void setErrorListener(ErrorListener errorListener) throws IllegalArgumentException;

    public abstract ErrorListener getErrorListener();

    protected Transformer() {
    }

    public void reset() {
        throw new UnsupportedOperationException("This Transformer, \"" + getClass().getName() + "\", does not support the reset functionality.  Specification \"" + getClass().getPackage().getSpecificationTitle() + Separators.DOUBLE_QUOTE + " version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }
}