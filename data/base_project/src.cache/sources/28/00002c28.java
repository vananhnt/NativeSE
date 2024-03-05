package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DTDHandler.class */
public interface DTDHandler {
    void notationDecl(String str, String str2, String str3) throws SAXException;

    void unparsedEntityDecl(String str, String str2, String str3, String str4) throws SAXException;
}