package javax.xml.parsers;

import gov.nist.core.Separators;
import javax.xml.validation.Schema;
import org.apache.harmony.xml.parsers.SAXParserFactoryImpl;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SAXParserFactory.class */
public abstract class SAXParserFactory {
    private boolean validating = false;
    private boolean namespaceAware = false;

    public abstract SAXParser newSAXParser() throws ParserConfigurationException, SAXException;

    public abstract void setFeature(String str, boolean z) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

    public abstract boolean getFeature(String str) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

    public static SAXParserFactory newInstance() {
        return new SAXParserFactoryImpl();
    }

    public static SAXParserFactory newInstance(String factoryClassName, ClassLoader classLoader) {
        if (factoryClassName == null) {
            throw new FactoryConfigurationError("factoryClassName == null");
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        try {
            Class<?> type = classLoader != null ? classLoader.loadClass(factoryClassName) : Class.forName(factoryClassName);
            return (SAXParserFactory) type.newInstance();
        } catch (ClassNotFoundException e) {
            throw new FactoryConfigurationError(e);
        } catch (IllegalAccessException e2) {
            throw new FactoryConfigurationError(e2);
        } catch (InstantiationException e3) {
            throw new FactoryConfigurationError(e3);
        }
    }

    public void setNamespaceAware(boolean awareness) {
        this.namespaceAware = awareness;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public boolean isValidating() {
        return this.validating;
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }

    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }

    public void setXIncludeAware(boolean state) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }

    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }
}