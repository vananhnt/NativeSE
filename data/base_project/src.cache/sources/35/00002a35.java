package org.apache.harmony.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/* loaded from: ExpatReader.class */
public class ExpatReader implements XMLReader {
    ContentHandler contentHandler;
    DTDHandler dtdHandler;
    EntityResolver entityResolver;
    ErrorHandler errorHandler;
    LexicalHandler lexicalHandler;
    private boolean processNamespaces = true;
    private boolean processNamespacePrefixes = false;
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatReader.parse(org.xml.sax.InputSource):void, file: ExpatReader.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // org.xml.sax.XMLReader
    public void parse(org.xml.sax.InputSource r1) throws java.io.IOException, org.xml.sax.SAXException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatReader.parse(org.xml.sax.InputSource):void, file: ExpatReader.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatReader.parse(org.xml.sax.InputSource):void");
    }

    /* loaded from: ExpatReader$Feature.class */
    private static class Feature {
        private static final String BASE_URI = "http://xml.org/sax/features/";
        private static final String VALIDATION = "http://xml.org/sax/features/validation";
        private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
        private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
        private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
        private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
        private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

        private Feature() {
        }
    }

    @Override // org.xml.sax.XMLReader
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.equals(Parser.validationFeature) || name.equals(Parser.externalGeneralEntitiesFeature) || name.equals(Parser.externalParameterEntitiesFeature)) {
            return false;
        }
        if (name.equals(Parser.namespacesFeature)) {
            return this.processNamespaces;
        }
        if (name.equals(Parser.namespacePrefixesFeature)) {
            return this.processNamespacePrefixes;
        }
        if (name.equals(Parser.stringInterningFeature)) {
            return true;
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.equals(Parser.validationFeature) || name.equals(Parser.externalGeneralEntitiesFeature) || name.equals(Parser.externalParameterEntitiesFeature)) {
            if (value) {
                throw new SAXNotSupportedException("Cannot enable " + name);
            }
        } else if (name.equals(Parser.namespacesFeature)) {
            this.processNamespaces = value;
        } else if (name.equals(Parser.namespacePrefixesFeature)) {
            this.processNamespacePrefixes = value;
        } else if (name.equals(Parser.stringInterningFeature)) {
            if (value) {
                return;
            }
            throw new SAXNotSupportedException("Cannot disable " + name);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    @Override // org.xml.sax.XMLReader
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            return this.lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            if ((value instanceof LexicalHandler) || value == null) {
                this.lexicalHandler = (LexicalHandler) value;
                return;
            }
            throw new SAXNotSupportedException("value doesn't implement org.xml.sax.ext.LexicalHandler");
        }
        throw new SAXNotRecognizedException(name);
    }

    @Override // org.xml.sax.XMLReader
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    @Override // org.xml.sax.XMLReader
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    @Override // org.xml.sax.XMLReader
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    @Override // org.xml.sax.XMLReader
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    @Override // org.xml.sax.XMLReader
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override // org.xml.sax.XMLReader
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    @Override // org.xml.sax.XMLReader
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    @Override // org.xml.sax.XMLReader
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public boolean isNamespaceProcessingEnabled() {
        return this.processNamespaces;
    }

    public void setNamespaceProcessingEnabled(boolean processNamespaces) {
        this.processNamespaces = processNamespaces;
    }

    private void parse(Reader in, String publicId, String systemId) throws IOException, SAXException {
        ExpatParser parser = new ExpatParser("UTF-16", this, this.processNamespaces, publicId, systemId);
        parser.parseDocument(in);
    }

    private void parse(InputStream in, String charsetName, String publicId, String systemId) throws IOException, SAXException {
        ExpatParser parser = new ExpatParser(charsetName, this, this.processNamespaces, publicId, systemId);
        parser.parseDocument(in);
    }

    @Override // org.xml.sax.XMLReader
    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    }
}