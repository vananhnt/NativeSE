package javax.xml.validation;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ValidatorHandler.class */
public abstract class ValidatorHandler implements ContentHandler {
    public abstract void setContentHandler(ContentHandler contentHandler);

    public abstract ContentHandler getContentHandler();

    public abstract void setErrorHandler(ErrorHandler errorHandler);

    public abstract ErrorHandler getErrorHandler();

    public abstract void setResourceResolver(LSResourceResolver lSResourceResolver);

    public abstract LSResourceResolver getResourceResolver();

    public abstract TypeInfoProvider getTypeInfoProvider();

    protected ValidatorHandler() {
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        throw new SAXNotRecognizedException(name);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        throw new SAXNotRecognizedException(name);
    }
}