package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: HandlerBase.class */
public class HandlerBase implements EntityResolver, DTDHandler, DocumentHandler, ErrorHandler {
    public HandlerBase() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DTDHandler
    public void notationDecl(String name, String publicId, String systemId) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DTDHandler
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void setDocumentLocator(Locator locator) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void startDocument() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void endDocument() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void startElement(String name, AttributeList attributes) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void endElement(String name) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException e) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException e) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException e) throws SAXException {
        throw new RuntimeException("Stub!");
    }
}