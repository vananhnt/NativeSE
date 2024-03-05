package org.xml.sax.helpers;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultHandler.class */
public class DefaultHandler implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler {
    public DefaultHandler() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DTDHandler
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DTDHandler
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void setDocumentLocator(Locator locator) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
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