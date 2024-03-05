package org.xml.sax.helpers;

import java.io.IOException;
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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ParserAdapter.class */
public class ParserAdapter implements XMLReader, DocumentHandler {
    public ParserAdapter() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    public ParserAdapter(Parser parser) {
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
    public void parse(String systemId) throws IOException, SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.XMLReader
    public void parse(InputSource input) throws IOException, SAXException {
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
    public void startElement(String qName, AttributeList qAtts) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.DocumentHandler
    public void endElement(String qName) throws SAXException {
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

    /* loaded from: ParserAdapter$AttributeListAdapter.class */
    final class AttributeListAdapter implements Attributes {
        private AttributeList qAtts;

        AttributeListAdapter() {
        }

        void setAttributeList(AttributeList qAtts) {
            this.qAtts = qAtts;
        }

        @Override // org.xml.sax.Attributes
        public int getLength() {
            return this.qAtts.getLength();
        }

        @Override // org.xml.sax.Attributes
        public String getURI(int i) {
            return "";
        }

        @Override // org.xml.sax.Attributes
        public String getLocalName(int i) {
            return "";
        }

        @Override // org.xml.sax.Attributes
        public String getQName(int i) {
            return this.qAtts.getName(i).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getType(int i) {
            return this.qAtts.getType(i).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getValue(int i) {
            return this.qAtts.getValue(i);
        }

        @Override // org.xml.sax.Attributes
        public int getIndex(String uri, String localName) {
            return -1;
        }

        @Override // org.xml.sax.Attributes
        public int getIndex(String qName) {
            int max = ParserAdapter.access$000(ParserAdapter.this).getLength();
            for (int i = 0; i < max; i++) {
                if (this.qAtts.getName(i).equals(qName)) {
                    return i;
                }
            }
            return -1;
        }

        @Override // org.xml.sax.Attributes
        public String getType(String uri, String localName) {
            return null;
        }

        @Override // org.xml.sax.Attributes
        public String getType(String qName) {
            return this.qAtts.getType(qName).intern();
        }

        @Override // org.xml.sax.Attributes
        public String getValue(String uri, String localName) {
            return null;
        }

        @Override // org.xml.sax.Attributes
        public String getValue(String qName) {
            return this.qAtts.getValue(qName);
        }
    }
}