package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ErrorHandler.class */
public interface ErrorHandler {
    void warning(SAXParseException sAXParseException) throws SAXException;

    void error(SAXParseException sAXParseException) throws SAXException;

    void fatalError(SAXParseException sAXParseException) throws SAXException;
}