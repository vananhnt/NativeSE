package javax.xml.xpath;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPathFactory.class */
public abstract class XPathFactory {
    public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
    public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";

    public abstract boolean isObjectModelSupported(String str);

    public abstract void setFeature(String str, boolean z) throws XPathFactoryConfigurationException;

    public abstract boolean getFeature(String str) throws XPathFactoryConfigurationException;

    public abstract void setXPathVariableResolver(XPathVariableResolver xPathVariableResolver);

    public abstract void setXPathFunctionResolver(XPathFunctionResolver xPathFunctionResolver);

    public abstract XPath newXPath();

    protected XPathFactory() {
    }

    public static final XPathFactory newInstance() {
        try {
            return newInstance("http://java.sun.com/jaxp/xpath/dom");
        } catch (XPathFactoryConfigurationException xpathFactoryConfigurationException) {
            throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + xpathFactoryConfigurationException.toString());
        }
    }

    public static final XPathFactory newInstance(String uri) throws XPathFactoryConfigurationException {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (uri.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = XPathFactory.class.getClassLoader();
        }
        XPathFactory xpathFactory = new XPathFactoryFinder(classLoader).newFactory(uri);
        if (xpathFactory == null) {
            throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + uri);
        }
        return xpathFactory;
    }

    public static XPathFactory newInstance(String uri, String factoryClassName, ClassLoader classLoader) throws XPathFactoryConfigurationException {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (uri.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        if (factoryClassName == null) {
            throw new XPathFactoryConfigurationException("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        XPathFactory xpathFactory = new XPathFactoryFinder(classLoader).createInstance(factoryClassName);
        if (xpathFactory == null || !xpathFactory.isObjectModelSupported(uri)) {
            throw new XPathFactoryConfigurationException("No XPathFactory implementation found for the object model: " + uri);
        }
        return xpathFactory;
    }
}