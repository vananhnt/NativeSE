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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XMLFilterImpl.class */
public class XMLFilterImpl implements XMLFilter, EntityResolver, DTDHandler, ContentHandler, ErrorHandler {
    public XMLFilterImpl() {
        throw new RuntimeException("Stub!");
    }

    public XMLFilterImpl(XMLReader parent) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLFilter
    public void setParent(XMLReader parent) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLFilter
    public XMLReader getParent() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setEntityResolver(EntityResolver resolver) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public EntityResolver getEntityResolver() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setDTDHandler(DTDHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public DTDHandler getDTDHandler() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setContentHandler(ContentHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public ContentHandler getContentHandler() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void setErrorHandler(ErrorHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public ErrorHandler getErrorHandler() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void parse(InputSource input) throws SAXException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void parse(String systemId) throws SAXException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
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

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        throw new RuntimeException("Stub!");
    }

    public void startDocument() throws SAXException {
        throw new RuntimeException("Stub!");
    }

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

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

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