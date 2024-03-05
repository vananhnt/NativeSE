package org.xml.sax.ext;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultHandler2.class */
public class DefaultHandler2 extends DefaultHandler implements LexicalHandler, DeclHandler, EntityResolver2 {
    public DefaultHandler2() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startCDATA() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endCDATA() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endDTD() throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startEntity(String name) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endEntity(String name) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void comment(char[] ch, int start, int length) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void elementDecl(String name, String model) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void internalEntityDecl(String name, String value) throws SAXException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.EntityResolver2
    public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.ext.EntityResolver2
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        throw new RuntimeException("Stub!");
    }
}