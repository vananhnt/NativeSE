package javax.xml.parsers;

import gov.nist.core.Separators;
import javax.xml.validation.Schema;
import org.apache.harmony.xml.parsers.DocumentBuilderFactoryImpl;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DocumentBuilderFactory.class */
public abstract class DocumentBuilderFactory {
    private boolean validating = false;
    private boolean namespaceAware = false;
    private boolean whitespace = false;
    private boolean expandEntityRef = true;
    private boolean ignoreComments = false;
    private boolean coalescing = false;

    public abstract DocumentBuilder newDocumentBuilder() throws ParserConfigurationException;

    public abstract void setAttribute(String str, Object obj) throws IllegalArgumentException;

    public abstract Object getAttribute(String str) throws IllegalArgumentException;

    public abstract void setFeature(String str, boolean z) throws ParserConfigurationException;

    public abstract boolean getFeature(String str) throws ParserConfigurationException;

    public static DocumentBuilderFactory newInstance() {
        return new DocumentBuilderFactoryImpl();
    }

    public static DocumentBuilderFactory newInstance(String factoryClassName, ClassLoader classLoader) {
        if (factoryClassName == null) {
            throw new FactoryConfigurationError("factoryClassName == null");
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        try {
            Class<?> type = classLoader != null ? classLoader.loadClass(factoryClassName) : Class.forName(factoryClassName);
            return (DocumentBuilderFactory) type.newInstance();
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

    public void setIgnoringElementContentWhitespace(boolean whitespace) {
        this.whitespace = whitespace;
    }

    public void setExpandEntityReferences(boolean expandEntityRef) {
        this.expandEntityRef = expandEntityRef;
    }

    public void setIgnoringComments(boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }

    public void setCoalescing(boolean coalescing) {
        this.coalescing = coalescing;
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public boolean isValidating() {
        return this.validating;
    }

    public boolean isIgnoringElementContentWhitespace() {
        return this.whitespace;
    }

    public boolean isExpandEntityReferences() {
        return this.expandEntityRef;
    }

    public boolean isIgnoringComments() {
        return this.ignoreComments;
    }

    public boolean isCoalescing() {
        return this.coalescing;
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