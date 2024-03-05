package javax.xml.parsers;

import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DocumentBuilder.class */
public abstract class DocumentBuilder {
    private static final boolean DEBUG = false;

    public abstract Document parse(InputSource inputSource) throws SAXException, IOException;

    public abstract boolean isNamespaceAware();

    public abstract boolean isValidating();

    public abstract void setEntityResolver(EntityResolver entityResolver);

    public abstract void setErrorHandler(ErrorHandler errorHandler);

    public abstract Document newDocument();

    public abstract DOMImplementation getDOMImplementation();

    public void reset() {
        throw new UnsupportedOperationException("This DocumentBuilder, \"" + getClass().getName() + "\", does not support the reset functionality.  Specification \"" + getClass().getPackage().getSpecificationTitle() + Separators.DOUBLE_QUOTE + " version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }

    public Document parse(InputStream is) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource in = new InputSource(is);
        return parse(in);
    }

    public Document parse(InputStream is, String systemId) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource in = new InputSource(is);
        in.setSystemId(systemId);
        return parse(in);
    }

    public Document parse(String uri) throws SAXException, IOException {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        InputSource in = new InputSource(uri);
        return parse(in);
    }

    public Document parse(File f) throws SAXException, IOException {
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        String escapedURI = FilePathToURI.filepath2URI(f.getAbsolutePath());
        InputSource in = new InputSource(escapedURI);
        return parse(in);
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }

    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + Separators.DOUBLE_QUOTE);
    }
}