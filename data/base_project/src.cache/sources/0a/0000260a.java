package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Properties.class */
public class Properties extends Hashtable<Object, Object> {
    protected Properties defaults;

    public Properties() {
        throw new RuntimeException("Stub!");
    }

    public Properties(Properties properties) {
        throw new RuntimeException("Stub!");
    }

    public String getProperty(String name) {
        throw new RuntimeException("Stub!");
    }

    public String getProperty(String name, String defaultValue) {
        throw new RuntimeException("Stub!");
    }

    public void list(PrintStream out) {
        throw new RuntimeException("Stub!");
    }

    public void list(PrintWriter out) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void load(InputStream in) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void load(Reader in) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Enumeration<?> propertyNames() {
        throw new RuntimeException("Stub!");
    }

    public Set<String> stringPropertyNames() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public void save(OutputStream out, String comment) {
        throw new RuntimeException("Stub!");
    }

    public Object setProperty(String name, String value) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void store(OutputStream out, String comment) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void store(Writer writer, String comment) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        throw new RuntimeException("Stub!");
    }

    public void storeToXML(OutputStream os, String comment) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.Properties$1  reason: invalid class name */
    /* loaded from: Properties$1.class */
    class AnonymousClass1 implements ErrorHandler {
        AnonymousClass1() {
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }

    /* renamed from: java.util.Properties$2  reason: invalid class name */
    /* loaded from: Properties$2.class */
    class AnonymousClass2 implements EntityResolver {
        AnonymousClass2() {
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId.equals(Properties.PROP_DTD_NAME)) {
                InputSource result = new InputSource(new StringReader(Properties.PROP_DTD));
                result.setSystemId(Properties.PROP_DTD_NAME);
                return result;
            }
            throw new SAXException("Invalid DOCTYPE declaration: " + systemId);
        }
    }
}