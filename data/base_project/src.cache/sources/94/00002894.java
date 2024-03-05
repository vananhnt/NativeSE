package javax.xml.transform;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TransformerFactory.class */
public abstract class TransformerFactory {
    public abstract Transformer newTransformer(Source source) throws TransformerConfigurationException;

    public abstract Transformer newTransformer() throws TransformerConfigurationException;

    public abstract Templates newTemplates(Source source) throws TransformerConfigurationException;

    public abstract Source getAssociatedStylesheet(Source source, String str, String str2, String str3) throws TransformerConfigurationException;

    public abstract void setURIResolver(URIResolver uRIResolver);

    public abstract URIResolver getURIResolver();

    public abstract void setFeature(String str, boolean z) throws TransformerConfigurationException;

    public abstract boolean getFeature(String str);

    public abstract void setAttribute(String str, Object obj);

    public abstract Object getAttribute(String str);

    public abstract void setErrorListener(ErrorListener errorListener);

    public abstract ErrorListener getErrorListener();

    public static TransformerFactory newInstance() throws TransformerFactoryConfigurationError {
        try {
            return (TransformerFactory) Class.forName("org.apache.xalan.processor.TransformerFactoryImpl").newInstance();
        } catch (Exception e) {
            throw new NoClassDefFoundError("org.apache.xalan.processor.TransformerFactoryImpl");
        }
    }

    public static TransformerFactory newInstance(String factoryClassName, ClassLoader classLoader) throws TransformerFactoryConfigurationError {
        if (factoryClassName == null) {
            throw new TransformerFactoryConfigurationError("factoryClassName == null");
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        try {
            Class<?> type = classLoader != null ? classLoader.loadClass(factoryClassName) : Class.forName(factoryClassName);
            return (TransformerFactory) type.newInstance();
        } catch (ClassNotFoundException e) {
            throw new TransformerFactoryConfigurationError(e);
        } catch (IllegalAccessException e2) {
            throw new TransformerFactoryConfigurationError(e2);
        } catch (InstantiationException e3) {
            throw new TransformerFactoryConfigurationError(e3);
        }
    }
}