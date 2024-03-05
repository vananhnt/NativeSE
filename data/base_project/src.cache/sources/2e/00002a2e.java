package org.apache.harmony.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ExpatParser.class */
public class ExpatParser {
    private static final int BUFFER_SIZE = 8096;
    private long pointer;
    private boolean inStartElement;
    private int attributeCount;
    private int attributePointer;
    private final Locator locator;
    private final ExpatReader xmlReader;
    private final String publicId;
    private final String systemId;
    private final String encoding;
    private final ExpatAttributes attributes;
    private static final String OUTSIDE_START_ELEMENT = "Attributes can only be used within the scope of startElement().";
    private static final String DEFAULT_ENCODING = "UTF-8";
    static final String CHARACTER_ENCODING = "UTF-16";
    private static final int TIMEOUT = 20000;

    private native long initialize(String str, boolean z);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.startElement(java.lang.String, java.lang.String, java.lang.String, int, int):void, file: ExpatParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void startElement(java.lang.String r1, java.lang.String r2, java.lang.String r3, int r4, int r5) throws org.xml.sax.SAXException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.startElement(java.lang.String, java.lang.String, java.lang.String, int, int):void, file: ExpatParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatParser.startElement(java.lang.String, java.lang.String, java.lang.String, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.handleExternalEntity(java.lang.String, java.lang.String, java.lang.String):void, file: ExpatParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    void handleExternalEntity(java.lang.String r1, java.lang.String r2, java.lang.String r3) throws org.xml.sax.SAXException, java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.handleExternalEntity(java.lang.String, java.lang.String, java.lang.String):void, file: ExpatParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatParser.handleExternalEntity(java.lang.String, java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.parseExternalEntity(org.apache.harmony.xml.ExpatParser, org.xml.sax.InputSource):void, file: ExpatParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void parseExternalEntity(org.apache.harmony.xml.ExpatParser r1, org.xml.sax.InputSource r2) throws java.io.IOException, org.xml.sax.SAXException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.parseExternalEntity(org.apache.harmony.xml.ExpatParser, org.xml.sax.InputSource):void, file: ExpatParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatParser.parseExternalEntity(org.apache.harmony.xml.ExpatParser, org.xml.sax.InputSource):void");
    }

    private static native long createEntityParser(long j, String str);

    private native void appendString(long j, String str, boolean z) throws SAXException, ExpatException;

    private native void appendChars(long j, char[] cArr, int i, int i2) throws SAXException, ExpatException;

    private native void appendBytes(long j, byte[] bArr, int i, int i2) throws SAXException, ExpatException;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.finalize():void, file: ExpatParser.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    protected synchronized void finalize() throws java.lang.Throwable {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.finalize():void, file: ExpatParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatParser.finalize():void");
    }

    private native void release(long j);

    private static native void releaseParser(long j);

    private static native void staticInitialize(String str);

    private static native int line(long j);

    private static native int column(long j);

    private static native long cloneAttributes(long j, int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExpatParser(String encoding, ExpatReader xmlReader, boolean processNamespaces, String publicId, String systemId) {
        this.inStartElement = false;
        this.attributeCount = -1;
        this.attributePointer = 0;
        this.locator = new ExpatLocator();
        this.attributes = new CurrentAttributes();
        this.publicId = publicId;
        this.systemId = systemId;
        this.xmlReader = xmlReader;
        this.encoding = encoding == null ? "UTF-8" : encoding;
        this.pointer = initialize(this.encoding, processNamespaces);
    }

    private ExpatParser(String encoding, ExpatReader xmlReader, long pointer, String publicId, String systemId) {
        this.inStartElement = false;
        this.attributeCount = -1;
        this.attributePointer = 0;
        this.locator = new ExpatLocator();
        this.attributes = new CurrentAttributes();
        this.encoding = encoding;
        this.xmlReader = xmlReader;
        this.pointer = pointer;
        this.systemId = systemId;
        this.publicId = publicId;
    }

    void endElement(String uri, String localName, String qName) throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.endElement(uri, localName, qName);
        }
    }

    void text(char[] text, int length) throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.characters(text, 0, length);
        }
    }

    void comment(char[] text, int length) throws SAXException {
        LexicalHandler lexicalHandler = this.xmlReader.lexicalHandler;
        if (lexicalHandler != null) {
            lexicalHandler.comment(text, 0, length);
        }
    }

    void startCdata() throws SAXException {
        LexicalHandler lexicalHandler = this.xmlReader.lexicalHandler;
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    void endCdata() throws SAXException {
        LexicalHandler lexicalHandler = this.xmlReader.lexicalHandler;
        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    void startNamespace(String prefix, String uri) throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.startPrefixMapping(prefix, uri);
        }
    }

    void endNamespace(String prefix) throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.endPrefixMapping(prefix);
        }
    }

    void startDtd(String name, String publicId, String systemId) throws SAXException {
        LexicalHandler lexicalHandler = this.xmlReader.lexicalHandler;
        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    void endDtd() throws SAXException {
        LexicalHandler lexicalHandler = this.xmlReader.lexicalHandler;
        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    void processingInstruction(String target, String data) throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.processingInstruction(target, data);
        }
    }

    void notationDecl(String name, String publicId, String systemId) throws SAXException {
        DTDHandler dtdHandler = this.xmlReader.dtdHandler;
        if (dtdHandler != null) {
            dtdHandler.notationDecl(name, publicId, systemId);
        }
    }

    void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        DTDHandler dtdHandler = this.xmlReader.dtdHandler;
        if (dtdHandler != null) {
            dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }

    private String pickEncoding(InputSource inputSource) {
        Reader reader = inputSource.getCharacterStream();
        if (reader != null) {
            return "UTF-16";
        }
        String encoding = inputSource.getEncoding();
        return encoding == null ? "UTF-8" : encoding;
    }

    void append(String xml) throws SAXException {
        try {
            appendString(this.pointer, xml, false);
        } catch (ExpatException e) {
            throw new ParseException(e.getMessage(), this.locator);
        }
    }

    void append(char[] xml, int offset, int length) throws SAXException {
        try {
            appendChars(this.pointer, xml, offset, length);
        } catch (ExpatException e) {
            throw new ParseException(e.getMessage(), this.locator);
        }
    }

    void append(byte[] xml) throws SAXException {
        append(xml, 0, xml.length);
    }

    void append(byte[] xml, int offset, int length) throws SAXException {
        try {
            appendBytes(this.pointer, xml, offset, length);
        } catch (ExpatException e) {
            throw new ParseException(e.getMessage(), this.locator);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void parseDocument(InputStream in) throws IOException, SAXException {
        startDocument();
        parseFragment(in);
        finish();
        endDocument();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void parseDocument(Reader in) throws IOException, SAXException {
        startDocument();
        parseFragment(in);
        finish();
        endDocument();
    }

    private void parseFragment(Reader in) throws IOException, SAXException {
        char[] buffer = new char[4048];
        while (true) {
            int length = in.read(buffer);
            if (length != -1) {
                try {
                    appendChars(this.pointer, buffer, 0, length);
                } catch (ExpatException e) {
                    throw new ParseException(e.getMessage(), this.locator);
                }
            } else {
                return;
            }
        }
    }

    private void parseFragment(InputStream in) throws IOException, SAXException {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int length = in.read(buffer);
            if (length != -1) {
                try {
                    appendBytes(this.pointer, buffer, 0, length);
                } catch (ExpatException e) {
                    throw new ParseException(e.getMessage(), this.locator);
                }
            } else {
                return;
            }
        }
    }

    private void startDocument() throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.setDocumentLocator(this.locator);
            contentHandler.startDocument();
        }
    }

    private void endDocument() throws SAXException {
        ContentHandler contentHandler = this.xmlReader.contentHandler;
        if (contentHandler != null) {
            contentHandler.endDocument();
        }
    }

    void finish() throws SAXException {
        try {
            appendString(this.pointer, "", true);
        } catch (ExpatException e) {
            throw new ParseException(e.getMessage(), this.locator);
        }
    }

    static {
        staticInitialize("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int line() {
        return line(this.pointer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int column() {
        return column(this.pointer);
    }

    Attributes cloneAttributes() {
        if (!this.inStartElement) {
            throw new IllegalStateException(OUTSIDE_START_ELEMENT);
        }
        if (this.attributeCount != 0) {
            long clonePointer = cloneAttributes(this.attributePointer, this.attributeCount);
            return new ClonedAttributes(this.pointer, clonePointer, this.attributeCount);
        }
        return ClonedAttributes.EMPTY;
    }

    /* loaded from: ExpatParser$ClonedAttributes.class */
    private static class ClonedAttributes extends ExpatAttributes {
        private static final Attributes EMPTY = new ClonedAttributes(0, 0, 0);
        private final long parserPointer;
        private long pointer;
        private final int length;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.ClonedAttributes.finalize():void, file: ExpatParser$ClonedAttributes.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        protected synchronized void finalize() throws java.lang.Throwable {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.ExpatParser.ClonedAttributes.finalize():void, file: ExpatParser$ClonedAttributes.class
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.ExpatParser.ClonedAttributes.finalize():void");
        }

        private ClonedAttributes(long parserPointer, long pointer, int length) {
            this.parserPointer = parserPointer;
            this.pointer = pointer;
            this.length = length;
        }

        @Override // org.apache.harmony.xml.ExpatAttributes
        public long getParserPointer() {
            return this.parserPointer;
        }

        @Override // org.apache.harmony.xml.ExpatAttributes
        public long getPointer() {
            return this.pointer;
        }

        @Override // org.apache.harmony.xml.ExpatAttributes, org.xml.sax.Attributes
        public int getLength() {
            return this.length;
        }
    }

    /* loaded from: ExpatParser$ExpatLocator.class */
    private class ExpatLocator implements Locator {
        private ExpatLocator() {
        }

        @Override // org.xml.sax.Locator
        public String getPublicId() {
            return ExpatParser.this.publicId;
        }

        @Override // org.xml.sax.Locator
        public String getSystemId() {
            return ExpatParser.this.systemId;
        }

        @Override // org.xml.sax.Locator
        public int getLineNumber() {
            return ExpatParser.this.line();
        }

        @Override // org.xml.sax.Locator
        public int getColumnNumber() {
            return ExpatParser.this.column();
        }

        public String toString() {
            return "Locator[publicId: " + ExpatParser.this.publicId + ", systemId: " + ExpatParser.this.systemId + ", line: " + getLineNumber() + ", column: " + getColumnNumber() + "]";
        }
    }

    /* loaded from: ExpatParser$CurrentAttributes.class */
    private class CurrentAttributes extends ExpatAttributes {
        private CurrentAttributes() {
        }

        @Override // org.apache.harmony.xml.ExpatAttributes
        public long getParserPointer() {
            return ExpatParser.this.pointer;
        }

        @Override // org.apache.harmony.xml.ExpatAttributes
        public long getPointer() {
            if (ExpatParser.this.inStartElement) {
                return ExpatParser.this.attributePointer;
            }
            throw new IllegalStateException(ExpatParser.OUTSIDE_START_ELEMENT);
        }

        @Override // org.apache.harmony.xml.ExpatAttributes, org.xml.sax.Attributes
        public int getLength() {
            if (ExpatParser.this.inStartElement) {
                return ExpatParser.this.attributeCount;
            }
            throw new IllegalStateException(ExpatParser.OUTSIDE_START_ELEMENT);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ExpatParser$ParseException.class */
    public static class ParseException extends SAXParseException {
        private ParseException(String message, Locator locator) {
            super(makeMessage(message, locator), locator);
        }

        private static String makeMessage(String message, Locator locator) {
            return makeMessage(message, locator.getLineNumber(), locator.getColumnNumber());
        }

        private static String makeMessage(String message, int line, int column) {
            return "At line " + line + ", column " + column + ": " + message;
        }
    }

    static InputStream openUrl(String url) throws IOException {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            return urlConnection.getInputStream();
        } catch (Exception e) {
            IOException ioe = new IOException("Couldn't open " + url);
            ioe.initCause(e);
            throw ioe;
        }
    }

    /* loaded from: ExpatParser$EntityParser.class */
    private static class EntityParser extends ExpatParser {
        private int depth;

        private EntityParser(String encoding, ExpatReader xmlReader, long pointer, String publicId, String systemId) {
            super(encoding, xmlReader, pointer, publicId, systemId);
            this.depth = 0;
        }

        @Override // org.apache.harmony.xml.ExpatParser
        void startElement(String uri, String localName, String qName, int attributePointer, int attributeCount) throws SAXException {
            int i = this.depth;
            this.depth = i + 1;
            if (i > 0) {
                super.startElement(uri, localName, qName, attributePointer, attributeCount);
            }
        }

        @Override // org.apache.harmony.xml.ExpatParser
        void endElement(String uri, String localName, String qName) throws SAXException {
            int i = this.depth - 1;
            this.depth = i;
            if (i > 0) {
                super.endElement(uri, localName, qName);
            }
        }

        @Override // org.apache.harmony.xml.ExpatParser
        protected synchronized void finalize() throws Throwable {
        }
    }
}