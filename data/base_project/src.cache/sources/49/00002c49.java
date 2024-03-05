package org.xml.sax.helpers;

import java.io.IOException;
import java.util.Locale;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XMLReaderAdapter.class */
public class XMLReaderAdapter implements Parser, ContentHandler {
    public XMLReaderAdapter() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public XMLReaderAdapter(XMLReader xmlReader) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void setLocale(Locale locale) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void setEntityResolver(EntityResolver resolver) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void setDTDHandler(DTDHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void setDocumentHandler(DocumentHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void setErrorHandler(ErrorHandler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void parse(String systemId) throws IOException, SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.Parser
    public void parse(InputSource input) throws IOException, SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
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
    public void startPrefixMapping(String prefix, String uri) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ContentHandler
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

    /* loaded from: XMLReaderAdapter$AttributesAdapter.class */
    static final class AttributesAdapter implements AttributeList {
        private Attributes attributes;

        AttributesAdapter() {
        }

        void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        @Override // org.xml.sax.AttributeList
        public int getLength() {
            return this.attributes.getLength();
        }

        @Override // org.xml.sax.AttributeList
        public String getName(int i) {
            return this.attributes.getQName(i);
        }

        @Override // org.xml.sax.AttributeList
        public String getType(int i) {
            return this.attributes.getType(i);
        }

        @Override // org.xml.sax.AttributeList
        public String getValue(int i) {
            return this.attributes.getValue(i);
        }

        @Override // org.xml.sax.AttributeList
        public String getType(String qName) {
            return this.attributes.getType(qName);
        }

        @Override // org.xml.sax.AttributeList
        public String getValue(String qName) {
            return this.attributes.getValue(qName);
        }
    }
}