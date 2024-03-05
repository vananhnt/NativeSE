package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: DocumentHandler.class */
public interface DocumentHandler {
    void setDocumentLocator(Locator locator);

    void startDocument() throws SAXException;

    void endDocument() throws SAXException;

    void startElement(String str, AttributeList attributeList) throws SAXException;

    void endElement(String str) throws SAXException;

    void characters(char[] cArr, int i, int i2) throws SAXException;

    void ignorableWhitespace(char[] cArr, int i, int i2) throws SAXException;

    void processingInstruction(String str, String str2) throws SAXException;
}