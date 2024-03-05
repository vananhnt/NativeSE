package org.kxml2.io;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import libcore.internal.StringPool;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: KXmlParser.class */
public class KXmlParser implements XmlPullParser, Closeable {
    private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
    private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
    private static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
    private static final String FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";
    private static final Map<String, String> DEFAULT_ENTITIES = new HashMap();
    private static final int ELEMENTDECL = 11;
    private static final int ENTITYDECL = 12;
    private static final int ATTLISTDECL = 13;
    private static final int NOTATIONDECL = 14;
    private static final int PARAMETER_ENTITY_REF = 15;
    private static final char[] START_COMMENT;
    private static final char[] END_COMMENT;
    private static final char[] COMMENT_DOUBLE_DASH;
    private static final char[] START_CDATA;
    private static final char[] END_CDATA;
    private static final char[] START_PROCESSING_INSTRUCTION;
    private static final char[] END_PROCESSING_INSTRUCTION;
    private static final char[] START_DOCTYPE;
    private static final char[] SYSTEM;
    private static final char[] PUBLIC;
    private static final char[] START_ELEMENT;
    private static final char[] START_ATTLIST;
    private static final char[] START_ENTITY;
    private static final char[] START_NOTATION;
    private static final char[] EMPTY;
    private static final char[] ANY;
    private static final char[] NDATA;
    private static final char[] NOTATION;
    private static final char[] REQUIRED;
    private static final char[] IMPLIED;
    private static final char[] FIXED;
    private static final String UNEXPECTED_EOF = "Unexpected EOF";
    private static final String ILLEGAL_TYPE = "Wrong event type";
    private static final int XML_DECLARATION = 998;
    private String location;
    private String version;
    private Boolean standalone;
    private String rootElementName;
    private String systemId;
    private String publicId;
    private boolean processDocDecl;
    private boolean processNsp;
    private boolean relaxed;
    private boolean keepNamespaceAttributes;
    private StringBuilder bufferCapture;
    private Map<String, char[]> documentEntities;
    private Map<String, Map<String, String>> defaultAttributes;
    private int depth;
    private Reader reader;
    private String encoding;
    private ContentSource nextContentSource;
    private int bufferStartLine;
    private int bufferStartColumn;
    private int type;
    private boolean isWhitespace;
    private String namespace;
    private String prefix;
    private String name;
    private String text;
    private boolean degenerated;
    private int attributeCount;
    private String error;
    private boolean unresolved;
    private static final char[] SINGLE_QUOTE;
    private static final char[] DOUBLE_QUOTE;
    private String[] elementStack = new String[16];
    private String[] nspStack = new String[8];
    private int[] nspCounts = new int[4];
    private char[] buffer = new char[8192];
    private int position = 0;
    private int limit = 0;
    private String[] attributes = new String[16];
    public final StringPool stringPool = new StringPool();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: KXmlParser$ValueContext.class */
    public enum ValueContext {
        ATTRIBUTE,
        TEXT,
        ENTITY_DECLARATION
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.kxml2.io.KXmlParser.readDoctype(boolean):void, file: KXmlParser.class
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
    private void readDoctype(boolean r1) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.kxml2.io.KXmlParser.readDoctype(boolean):void, file: KXmlParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.readDoctype(boolean):void");
    }

    static {
        DEFAULT_ENTITIES.put("lt", Separators.LESS_THAN);
        DEFAULT_ENTITIES.put("gt", Separators.GREATER_THAN);
        DEFAULT_ENTITIES.put("amp", Separators.AND);
        DEFAULT_ENTITIES.put("apos", Separators.QUOTE);
        DEFAULT_ENTITIES.put("quot", Separators.DOUBLE_QUOTE);
        START_COMMENT = new char[]{'<', '!', '-', '-'};
        END_COMMENT = new char[]{'-', '-', '>'};
        COMMENT_DOUBLE_DASH = new char[]{'-', '-'};
        START_CDATA = new char[]{'<', '!', '[', 'C', 'D', 'A', 'T', 'A', '['};
        END_CDATA = new char[]{']', ']', '>'};
        START_PROCESSING_INSTRUCTION = new char[]{'<', '?'};
        END_PROCESSING_INSTRUCTION = new char[]{'?', '>'};
        START_DOCTYPE = new char[]{'<', '!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
        SYSTEM = new char[]{'S', 'Y', 'S', 'T', 'E', 'M'};
        PUBLIC = new char[]{'P', 'U', 'B', 'L', 'I', 'C'};
        START_ELEMENT = new char[]{'<', '!', 'E', 'L', 'E', 'M', 'E', 'N', 'T'};
        START_ATTLIST = new char[]{'<', '!', 'A', 'T', 'T', 'L', 'I', 'S', 'T'};
        START_ENTITY = new char[]{'<', '!', 'E', 'N', 'T', 'I', 'T', 'Y'};
        START_NOTATION = new char[]{'<', '!', 'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        EMPTY = new char[]{'E', 'M', 'P', 'T', 'Y'};
        ANY = new char[]{'A', 'N', 'Y'};
        NDATA = new char[]{'N', 'D', 'A', 'T', 'A'};
        NOTATION = new char[]{'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        REQUIRED = new char[]{'R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D'};
        IMPLIED = new char[]{'I', 'M', 'P', 'L', 'I', 'E', 'D'};
        FIXED = new char[]{'F', 'I', 'X', 'E', 'D'};
        SINGLE_QUOTE = new char[]{'\''};
        DOUBLE_QUOTE = new char[]{'\"'};
    }

    public void keepNamespaceAttributes() {
        this.keepNamespaceAttributes = true;
    }

    private boolean adjustNsp() throws XmlPullParserException {
        String prefix;
        String attrName;
        boolean any = false;
        int i = 0;
        while (i < (this.attributeCount << 2)) {
            String attrName2 = this.attributes[i + 2];
            int cut = attrName2.indexOf(58);
            if (cut != -1) {
                prefix = attrName2.substring(0, cut);
                attrName = attrName2.substring(cut + 1);
            } else if (!attrName2.equals("xmlns")) {
                i += 4;
            } else {
                prefix = attrName2;
                attrName = null;
            }
            if (!prefix.equals("xmlns")) {
                any = true;
            } else {
                int[] iArr = this.nspCounts;
                int i2 = this.depth;
                int i3 = iArr[i2];
                iArr[i2] = i3 + 1;
                int j = i3 << 1;
                this.nspStack = ensureCapacity(this.nspStack, j + 2);
                this.nspStack[j] = attrName;
                this.nspStack[j + 1] = this.attributes[i + 3];
                if (attrName != null && this.attributes[i + 3].isEmpty()) {
                    checkRelaxed("illegal empty namespace");
                }
                if (this.keepNamespaceAttributes) {
                    this.attributes[i] = "http://www.w3.org/2000/xmlns/";
                    any = true;
                } else {
                    int i4 = this.attributeCount - 1;
                    this.attributeCount = i4;
                    System.arraycopy(this.attributes, i + 4, this.attributes, i, (i4 << 2) - i);
                    i -= 4;
                }
            }
            i += 4;
        }
        if (any) {
            for (int i5 = (this.attributeCount << 2) - 4; i5 >= 0; i5 -= 4) {
                String attrName3 = this.attributes[i5 + 2];
                int cut2 = attrName3.indexOf(58);
                if (cut2 == 0 && !this.relaxed) {
                    throw new RuntimeException("illegal attribute name: " + attrName3 + " at " + this);
                }
                if (cut2 != -1) {
                    String attrPrefix = attrName3.substring(0, cut2);
                    String attrName4 = attrName3.substring(cut2 + 1);
                    String attrNs = getNamespace(attrPrefix);
                    if (attrNs == null && !this.relaxed) {
                        throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this);
                    }
                    this.attributes[i5] = attrNs;
                    this.attributes[i5 + 1] = attrPrefix;
                    this.attributes[i5 + 2] = attrName4;
                }
            }
        }
        int cut3 = this.name.indexOf(58);
        if (cut3 == 0) {
            checkRelaxed("illegal tag name: " + this.name);
        }
        if (cut3 != -1) {
            this.prefix = this.name.substring(0, cut3);
            this.name = this.name.substring(cut3 + 1);
        }
        this.namespace = getNamespace(this.prefix);
        if (this.namespace == null) {
            if (this.prefix != null) {
                checkRelaxed("undefined prefix: " + this.prefix);
            }
            this.namespace = "";
        }
        return any;
    }

    private String[] ensureCapacity(String[] arr, int required) {
        if (arr.length >= required) {
            return arr;
        }
        String[] bigger = new String[required + 16];
        System.arraycopy(arr, 0, bigger, 0, arr.length);
        return bigger;
    }

    private void checkRelaxed(String errorMessage) throws XmlPullParserException {
        if (!this.relaxed) {
            throw new XmlPullParserException(errorMessage, this, null);
        }
        if (this.error == null) {
            this.error = "Error: " + errorMessage;
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int next() throws XmlPullParserException, IOException {
        return next(false);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextToken() throws XmlPullParserException, IOException {
        return next(true);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x012a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int next(boolean r8) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            Method dump skipped, instructions count: 535
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.next(boolean):int");
    }

    private String readUntil(char[] delimiter, boolean returnText) throws IOException, XmlPullParserException {
        int start = this.position;
        StringBuilder result = null;
        if (returnText && this.text != null) {
            result = new StringBuilder();
            result.append(this.text);
        }
        while (true) {
            if (this.position + delimiter.length > this.limit) {
                if (start < this.position && returnText) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                }
                if (!fillBuffer(delimiter.length)) {
                    checkRelaxed(UNEXPECTED_EOF);
                    this.type = 9;
                    return null;
                }
                start = this.position;
            }
            for (int i = 0; i < delimiter.length; i++) {
                if (this.buffer[this.position + i] != delimiter[i]) {
                    break;
                }
            }
            int end = this.position;
            this.position += delimiter.length;
            if (!returnText) {
                return null;
            }
            if (result == null) {
                return this.stringPool.get(this.buffer, start, end - start);
            }
            result.append(this.buffer, start, end - start);
            return result.toString();
            this.position++;
        }
    }

    private void readXmlDeclaration() throws IOException, XmlPullParserException {
        if (this.bufferStartLine != 0 || this.bufferStartColumn != 0 || this.position != 0) {
            checkRelaxed("processing instructions must not start with xml");
        }
        read(START_PROCESSING_INSTRUCTION);
        parseStartTag(true, true);
        if (this.attributeCount < 1 || !"version".equals(this.attributes[2])) {
            checkRelaxed("version expected");
        }
        this.version = this.attributes[3];
        int pos = 1;
        if (1 < this.attributeCount && "encoding".equals(this.attributes[6])) {
            this.encoding = this.attributes[7];
            pos = 1 + 1;
        }
        if (pos < this.attributeCount && "standalone".equals(this.attributes[(4 * pos) + 2])) {
            String st = this.attributes[3 + (4 * pos)];
            if (AuthorizationHeaderIms.YES.equals(st)) {
                this.standalone = Boolean.TRUE;
            } else if (AuthorizationHeaderIms.NO.equals(st)) {
                this.standalone = Boolean.FALSE;
            } else {
                checkRelaxed("illegal standalone value: " + st);
            }
            pos++;
        }
        if (pos != this.attributeCount) {
            checkRelaxed("unexpected attributes in XML declaration");
        }
        this.isWhitespace = true;
        this.text = null;
    }

    private String readComment(boolean returnText) throws IOException, XmlPullParserException {
        read(START_COMMENT);
        if (this.relaxed) {
            return readUntil(END_COMMENT, returnText);
        }
        String commentText = readUntil(COMMENT_DOUBLE_DASH, returnText);
        if (peekCharacter() != 62) {
            throw new XmlPullParserException("Comments may not contain --", this, null);
        }
        this.position++;
        return commentText;
    }

    private boolean readExternalId(boolean requireSystemName, boolean assignFields) throws IOException, XmlPullParserException {
        int delimiter;
        skip();
        int c = peekCharacter();
        if (c == 83) {
            read(SYSTEM);
        } else if (c == 80) {
            read(PUBLIC);
            skip();
            if (assignFields) {
                this.publicId = readQuotedId(true);
            } else {
                readQuotedId(false);
            }
        } else {
            return false;
        }
        skip();
        if (!requireSystemName && (delimiter = peekCharacter()) != 34 && delimiter != 39) {
            return true;
        }
        if (assignFields) {
            this.systemId = readQuotedId(true);
            return true;
        }
        readQuotedId(false);
        return true;
    }

    private String readQuotedId(boolean returnText) throws IOException, XmlPullParserException {
        char[] delimiter;
        int quote = peekCharacter();
        if (quote == 34) {
            delimiter = DOUBLE_QUOTE;
        } else if (quote == 39) {
            delimiter = SINGLE_QUOTE;
        } else {
            throw new XmlPullParserException("Expected a quoted string", this, null);
        }
        this.position++;
        return readUntil(delimiter, returnText);
    }

    private void readInternalSubset() throws IOException, XmlPullParserException {
        read('[');
        while (true) {
            skip();
            if (peekCharacter() == 93) {
                this.position++;
                return;
            }
            int declarationType = peekType(true);
            switch (declarationType) {
                case 8:
                    read(START_PROCESSING_INSTRUCTION);
                    readUntil(END_PROCESSING_INSTRUCTION, false);
                    break;
                case 9:
                    readComment(false);
                    break;
                case 10:
                default:
                    throw new XmlPullParserException("Unexpected token", this, null);
                case 11:
                    readElementDeclaration();
                    break;
                case 12:
                    readEntityDeclaration();
                    break;
                case 13:
                    readAttributeListDeclaration();
                    break;
                case 14:
                    readNotationDeclaration();
                    break;
                case 15:
                    throw new XmlPullParserException("Parameter entity references are not supported", this, null);
            }
        }
    }

    private void readElementDeclaration() throws IOException, XmlPullParserException {
        read(START_ELEMENT);
        skip();
        readName();
        readContentSpec();
        skip();
        read('>');
    }

    private void readContentSpec() throws IOException, XmlPullParserException {
        skip();
        int c = peekCharacter();
        if (c != 40) {
            if (c == EMPTY[0]) {
                read(EMPTY);
                return;
            } else if (c == ANY[0]) {
                read(ANY);
                return;
            } else {
                throw new XmlPullParserException("Expected element content spec", this, null);
            }
        }
        int depth = 0;
        do {
            if (c == 40) {
                depth++;
            } else if (c == 41) {
                depth--;
            } else if (c == -1) {
                throw new XmlPullParserException("Unterminated element content spec", this, null);
            }
            this.position++;
            c = peekCharacter();
        } while (depth > 0);
        if (c == 42 || c == 63 || c == 43) {
            this.position++;
        }
    }

    private void readAttributeListDeclaration() throws IOException, XmlPullParserException {
        read(START_ATTLIST);
        skip();
        String elementName = readName();
        while (true) {
            skip();
            int c = peekCharacter();
            if (c == 62) {
                this.position++;
                return;
            }
            String attributeName = readName();
            skip();
            if (this.position + 1 >= this.limit && !fillBuffer(2)) {
                throw new XmlPullParserException("Malformed attribute list", this, null);
            }
            if (this.buffer[this.position] == NOTATION[0] && this.buffer[this.position + 1] == NOTATION[1]) {
                read(NOTATION);
                skip();
            }
            int c2 = peekCharacter();
            if (c2 == 40) {
                this.position++;
                while (true) {
                    skip();
                    readName();
                    skip();
                    int c3 = peekCharacter();
                    if (c3 == 41) {
                        this.position++;
                        break;
                    } else if (c3 == 124) {
                        this.position++;
                    } else {
                        throw new XmlPullParserException("Malformed attribute type", this, null);
                    }
                }
            } else {
                readName();
            }
            skip();
            int c4 = peekCharacter();
            if (c4 == 35) {
                this.position++;
                int c5 = peekCharacter();
                if (c5 == 82) {
                    read(REQUIRED);
                } else if (c5 == 73) {
                    read(IMPLIED);
                } else if (c5 == 70) {
                    read(FIXED);
                } else {
                    throw new XmlPullParserException("Malformed attribute type", this, null);
                }
                skip();
                c4 = peekCharacter();
            }
            if (c4 == 34 || c4 == 39) {
                this.position++;
                String value = readValue((char) c4, true, true, ValueContext.ATTRIBUTE);
                if (peekCharacter() == c4) {
                    this.position++;
                }
                defineAttributeDefault(elementName, attributeName, value);
            }
        }
    }

    private void defineAttributeDefault(String elementName, String attributeName, String value) {
        if (this.defaultAttributes == null) {
            this.defaultAttributes = new HashMap();
        }
        Map<String, String> elementAttributes = this.defaultAttributes.get(elementName);
        if (elementAttributes == null) {
            elementAttributes = new HashMap<>();
            this.defaultAttributes.put(elementName, elementAttributes);
        }
        elementAttributes.put(attributeName, value);
    }

    private void readEntityDeclaration() throws IOException, XmlPullParserException {
        String entityValue;
        read(START_ENTITY);
        boolean generalEntity = true;
        skip();
        if (peekCharacter() == 37) {
            generalEntity = false;
            this.position++;
            skip();
        }
        String name = readName();
        skip();
        int quote = peekCharacter();
        if (quote == 34 || quote == 39) {
            this.position++;
            entityValue = readValue((char) quote, true, false, ValueContext.ENTITY_DECLARATION);
            if (peekCharacter() == quote) {
                this.position++;
            }
        } else if (readExternalId(true, false)) {
            entityValue = "";
            skip();
            if (peekCharacter() == NDATA[0]) {
                read(NDATA);
                skip();
                readName();
            }
        } else {
            throw new XmlPullParserException("Expected entity value or external ID", this, null);
        }
        if (generalEntity && this.processDocDecl) {
            if (this.documentEntities == null) {
                this.documentEntities = new HashMap();
            }
            this.documentEntities.put(name, entityValue.toCharArray());
        }
        skip();
        read('>');
    }

    private void readNotationDeclaration() throws IOException, XmlPullParserException {
        read(START_NOTATION);
        skip();
        readName();
        if (!readExternalId(false, false)) {
            throw new XmlPullParserException("Expected external ID or public ID for notation", this, null);
        }
        skip();
        read('>');
    }

    private void readEndTag() throws IOException, XmlPullParserException {
        read('<');
        read('/');
        this.name = readName();
        skip();
        read('>');
        int sp = (this.depth - 1) * 4;
        if (this.depth == 0) {
            checkRelaxed("read end tag " + this.name + " with no tags open");
            this.type = 9;
        } else if (this.name.equals(this.elementStack[sp + 3])) {
            this.namespace = this.elementStack[sp];
            this.prefix = this.elementStack[sp + 1];
            this.name = this.elementStack[sp + 2];
        } else if (!this.relaxed) {
            throw new XmlPullParserException("expected: /" + this.elementStack[sp + 3] + " read: " + this.name, this, null);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int peekType(boolean inDeclaration) throws IOException, XmlPullParserException {
        if (this.position >= this.limit && !fillBuffer(1)) {
            return 1;
        }
        switch (this.buffer[this.position]) {
            case '%':
                return inDeclaration ? 15 : 4;
            case '&':
                return 6;
            case '<':
                if (this.position + 3 >= this.limit && !fillBuffer(4)) {
                    throw new XmlPullParserException("Dangling <", this, null);
                }
                switch (this.buffer[this.position + 1]) {
                    case '!':
                        switch (this.buffer[this.position + 2]) {
                            case '-':
                                return 9;
                            case 'A':
                                return 13;
                            case 'D':
                                return 10;
                            case 'E':
                                switch (this.buffer[this.position + 3]) {
                                    case 'L':
                                        return 11;
                                    case 'N':
                                        return 12;
                                }
                            case 'N':
                                return 14;
                            case '[':
                                return 5;
                        }
                        throw new XmlPullParserException("Unexpected <!", this, null);
                    case '/':
                        return 3;
                    case '?':
                        if (this.position + 5 < this.limit || fillBuffer(6)) {
                            if (this.buffer[this.position + 2] == 'x' || this.buffer[this.position + 2] == 'X') {
                                if (this.buffer[this.position + 3] == 'm' || this.buffer[this.position + 3] == 'M') {
                                    if ((this.buffer[this.position + 4] == 'l' || this.buffer[this.position + 4] == 'L') && this.buffer[this.position + 5] == ' ') {
                                        return XML_DECLARATION;
                                    }
                                    return 8;
                                }
                                return 8;
                            }
                            return 8;
                        }
                        return 8;
                    default:
                        return 2;
                }
            default:
                return 4;
        }
    }

    private void parseStartTag(boolean xmldecl, boolean throwOnResolveFailure) throws IOException, XmlPullParserException {
        Map<String, String> elementDefaultAttributes;
        if (!xmldecl) {
            read('<');
        }
        this.name = readName();
        this.attributeCount = 0;
        while (true) {
            skip();
            if (this.position >= this.limit && !fillBuffer(1)) {
                checkRelaxed(UNEXPECTED_EOF);
                return;
            }
            char c = this.buffer[this.position];
            if (xmldecl) {
                if (c == '?') {
                    this.position++;
                    read('>');
                    return;
                }
            } else if (c == '/') {
                this.degenerated = true;
                this.position++;
                skip();
                read('>');
                break;
            } else if (c == '>') {
                this.position++;
                break;
            }
            String attrName = readName();
            int i = this.attributeCount;
            this.attributeCount = i + 1;
            int i2 = i * 4;
            this.attributes = ensureCapacity(this.attributes, i2 + 4);
            this.attributes[i2] = "";
            this.attributes[i2 + 1] = null;
            this.attributes[i2 + 2] = attrName;
            skip();
            if (this.position >= this.limit && !fillBuffer(1)) {
                checkRelaxed(UNEXPECTED_EOF);
                return;
            } else if (this.buffer[this.position] == '=') {
                this.position++;
                skip();
                if (this.position >= this.limit && !fillBuffer(1)) {
                    checkRelaxed(UNEXPECTED_EOF);
                    return;
                }
                char delimiter = this.buffer[this.position];
                if (delimiter == '\'' || delimiter == '\"') {
                    this.position++;
                } else if (this.relaxed) {
                    delimiter = ' ';
                } else {
                    throw new XmlPullParserException("attr value delimiter missing!", this, null);
                }
                this.attributes[i2 + 3] = readValue(delimiter, true, throwOnResolveFailure, ValueContext.ATTRIBUTE);
                if (delimiter != ' ' && peekCharacter() == delimiter) {
                    this.position++;
                }
            } else if (this.relaxed) {
                this.attributes[i2 + 3] = attrName;
            } else {
                checkRelaxed("Attr.value missing f. " + attrName);
                this.attributes[i2 + 3] = attrName;
            }
        }
        int i3 = this.depth;
        this.depth = i3 + 1;
        int sp = i3 * 4;
        this.elementStack = ensureCapacity(this.elementStack, sp + 4);
        this.elementStack[sp + 3] = this.name;
        if (this.depth >= this.nspCounts.length) {
            int[] bigger = new int[this.depth + 4];
            System.arraycopy(this.nspCounts, 0, bigger, 0, this.nspCounts.length);
            this.nspCounts = bigger;
        }
        this.nspCounts[this.depth] = this.nspCounts[this.depth - 1];
        if (this.processNsp) {
            adjustNsp();
        } else {
            this.namespace = "";
        }
        if (this.defaultAttributes != null && (elementDefaultAttributes = this.defaultAttributes.get(this.name)) != null) {
            for (Map.Entry<String, String> entry : elementDefaultAttributes.entrySet()) {
                if (getAttributeValue(null, entry.getKey()) == null) {
                    int i4 = this.attributeCount;
                    this.attributeCount = i4 + 1;
                    int i5 = i4 * 4;
                    this.attributes = ensureCapacity(this.attributes, i5 + 4);
                    this.attributes[i5] = "";
                    this.attributes[i5 + 1] = null;
                    this.attributes[i5 + 2] = entry.getKey();
                    this.attributes[i5 + 3] = entry.getValue();
                }
            }
        }
        this.elementStack[sp] = this.namespace;
        this.elementStack[sp + 1] = this.prefix;
        this.elementStack[sp + 2] = this.name;
    }

    private void readEntity(StringBuilder out, boolean isEntityToken, boolean throwOnResolveFailure, ValueContext valueContext) throws IOException, XmlPullParserException {
        char[] resolved;
        int start = out.length();
        char[] cArr = this.buffer;
        int i = this.position;
        this.position = i + 1;
        if (cArr[i] != '&') {
            throw new AssertionError();
        }
        out.append('&');
        while (true) {
            int c = peekCharacter();
            if (c == 59) {
                out.append(';');
                this.position++;
                String code = out.substring(start + 1, out.length() - 1);
                if (isEntityToken) {
                    this.name = code;
                }
                if (code.startsWith(Separators.POUND)) {
                    try {
                        int c2 = code.startsWith("#x") ? Integer.parseInt(code.substring(2), 16) : Integer.parseInt(code.substring(1));
                        out.delete(start, out.length());
                        out.appendCodePoint(c2);
                        this.unresolved = false;
                        return;
                    } catch (NumberFormatException e) {
                        throw new XmlPullParserException("Invalid character reference: &" + code);
                    } catch (IllegalArgumentException e2) {
                        throw new XmlPullParserException("Invalid character reference: &" + code);
                    }
                } else if (valueContext == ValueContext.ENTITY_DECLARATION) {
                    return;
                } else {
                    String defaultEntity = DEFAULT_ENTITIES.get(code);
                    if (defaultEntity != null) {
                        out.delete(start, out.length());
                        this.unresolved = false;
                        out.append(defaultEntity);
                        return;
                    } else if (this.documentEntities != null && (resolved = this.documentEntities.get(code)) != null) {
                        out.delete(start, out.length());
                        this.unresolved = false;
                        if (this.processDocDecl) {
                            pushContentSource(resolved);
                            return;
                        } else {
                            out.append(resolved);
                            return;
                        }
                    } else if (this.systemId != null) {
                        out.delete(start, out.length());
                        return;
                    } else {
                        this.unresolved = true;
                        if (throwOnResolveFailure) {
                            checkRelaxed("unresolved: &" + code + Separators.SEMICOLON);
                            return;
                        }
                        return;
                    }
                }
            } else if (c >= 128 || ((c >= 48 && c <= 57) || ((c >= 97 && c <= 122) || ((c >= 65 && c <= 90) || c == 95 || c == 45 || c == 35)))) {
                this.position++;
                out.append((char) c);
            } else if (this.relaxed) {
                return;
            } else {
                throw new XmlPullParserException("unterminated entity ref", this, null);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:108:0x024c, code lost:
        if (r12 != null) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x0263, code lost:
        return r6.stringPool.get(r6.buffer, r11, r6.position - r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x0264, code lost:
        r12.append(r6.buffer, r11, r6.position - r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x027c, code lost:
        return r12.toString();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String readValue(char r7, boolean r8, boolean r9, org.kxml2.io.KXmlParser.ValueContext r10) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            Method dump skipped, instructions count: 637
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.readValue(char, boolean, boolean, org.kxml2.io.KXmlParser$ValueContext):java.lang.String");
    }

    private void read(char expected) throws IOException, XmlPullParserException {
        int c = peekCharacter();
        if (c != expected) {
            checkRelaxed("expected: '" + expected + "' actual: '" + ((char) c) + Separators.QUOTE);
            if (c == -1) {
                return;
            }
        }
        this.position++;
    }

    private void read(char[] chars) throws IOException, XmlPullParserException {
        if (this.position + chars.length > this.limit && !fillBuffer(chars.length)) {
            checkRelaxed("expected: '" + new String(chars) + "' but was EOF");
            return;
        }
        for (int i = 0; i < chars.length; i++) {
            if (this.buffer[this.position + i] != chars[i]) {
                checkRelaxed("expected: \"" + new String(chars) + "\" but was \"" + new String(this.buffer, this.position, chars.length) + "...\"");
            }
        }
        this.position += chars.length;
    }

    private int peekCharacter() throws IOException, XmlPullParserException {
        if (this.position < this.limit || fillBuffer(1)) {
            return this.buffer[this.position];
        }
        return -1;
    }

    private boolean fillBuffer(int minimum) throws IOException, XmlPullParserException {
        while (this.nextContentSource != null) {
            if (this.position < this.limit) {
                throw new XmlPullParserException("Unbalanced entity!", this, null);
            }
            popContentSource();
            if (this.limit - this.position >= minimum) {
                return true;
            }
        }
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                this.bufferStartLine++;
                this.bufferStartColumn = 0;
            } else {
                this.bufferStartColumn++;
            }
        }
        if (this.bufferCapture != null) {
            this.bufferCapture.append(this.buffer, 0, this.position);
        }
        if (this.limit != this.position) {
            this.limit -= this.position;
            System.arraycopy(this.buffer, this.position, this.buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.position = 0;
        do {
            int total = this.reader.read(this.buffer, this.limit, this.buffer.length - this.limit);
            if (total == -1) {
                return false;
            }
            this.limit += total;
        } while (this.limit < minimum);
        return true;
    }

    private String readName() throws IOException, XmlPullParserException {
        if (this.position >= this.limit && !fillBuffer(1)) {
            checkRelaxed("name expected");
            return "";
        }
        int start = this.position;
        StringBuilder result = null;
        char c = this.buffer[this.position];
        if ((c >= 'a' && c <= 'z') || ((c >= 'A' && c <= 'Z') || c == '_' || c == ':' || c >= 192 || this.relaxed)) {
            this.position++;
            while (true) {
                if (this.position >= this.limit) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                    if (!fillBuffer(1)) {
                        return result.toString();
                    }
                    start = this.position;
                }
                char c2 = this.buffer[this.position];
                if ((c2 >= 'a' && c2 <= 'z') || ((c2 >= 'A' && c2 <= 'Z') || ((c2 >= '0' && c2 <= '9') || c2 == '_' || c2 == '-' || c2 == ':' || c2 == '.' || c2 >= 183))) {
                    this.position++;
                } else if (result == null) {
                    return this.stringPool.get(this.buffer, start, this.position - start);
                } else {
                    result.append(this.buffer, start, this.position - start);
                    return result.toString();
                }
            }
        } else {
            checkRelaxed("name expected");
            return "";
        }
    }

    private void skip() throws IOException, XmlPullParserException {
        while (true) {
            if ((this.position < this.limit || fillBuffer(1)) && this.buffer[this.position] <= ' ') {
                this.position++;
            } else {
                return;
            }
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(Reader reader) throws XmlPullParserException {
        this.reader = reader;
        this.type = 0;
        this.name = null;
        this.namespace = null;
        this.degenerated = false;
        this.attributeCount = -1;
        this.encoding = null;
        this.version = null;
        this.standalone = null;
        if (reader == null) {
            return;
        }
        this.position = 0;
        this.limit = 0;
        this.bufferStartLine = 0;
        this.bufferStartColumn = 0;
        this.depth = 0;
        this.documentEntities = null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(InputStream is, String charset) throws XmlPullParserException {
        int i;
        this.position = 0;
        this.limit = 0;
        boolean detectCharset = charset == null;
        if (is == null) {
            throw new IllegalArgumentException("is == null");
        }
        if (detectCharset) {
            int firstFourBytes = 0;
            while (this.limit < 4 && (i = is.read()) != -1) {
                try {
                    firstFourBytes = (firstFourBytes << 8) | i;
                    char[] cArr = this.buffer;
                    int i2 = this.limit;
                    this.limit = i2 + 1;
                    cArr[i2] = (char) i;
                } catch (Exception e) {
                    throw new XmlPullParserException("Invalid stream or encoding: " + e, this, e);
                }
            }
            if (this.limit == 4) {
                switch (firstFourBytes) {
                    case -131072:
                        charset = "UTF-32LE";
                        this.limit = 0;
                        break;
                    case 60:
                        charset = "UTF-32BE";
                        this.buffer[0] = '<';
                        this.limit = 1;
                        break;
                    case 65279:
                        charset = "UTF-32BE";
                        this.limit = 0;
                        break;
                    case 3932223:
                        charset = "UTF-16BE";
                        this.buffer[0] = '<';
                        this.buffer[1] = '?';
                        this.limit = 2;
                        break;
                    case 1006632960:
                        charset = "UTF-32LE";
                        this.buffer[0] = '<';
                        this.limit = 1;
                        break;
                    case 1006649088:
                        charset = "UTF-16LE";
                        this.buffer[0] = '<';
                        this.buffer[1] = '?';
                        this.limit = 2;
                        break;
                    case 1010792557:
                        while (true) {
                            int i3 = is.read();
                            if (i3 == -1) {
                                break;
                            } else {
                                char[] cArr2 = this.buffer;
                                int i4 = this.limit;
                                this.limit = i4 + 1;
                                cArr2[i4] = (char) i3;
                                if (i3 == 62) {
                                    String s = new String(this.buffer, 0, this.limit);
                                    int i0 = s.indexOf("encoding");
                                    if (i0 != -1) {
                                        while (s.charAt(i0) != '\"' && s.charAt(i0) != '\'') {
                                            i0++;
                                        }
                                        int i5 = i0;
                                        int i02 = i0 + 1;
                                        char deli = s.charAt(i5);
                                        int i1 = s.indexOf(deli, i02);
                                        charset = s.substring(i02, i1);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        if ((firstFourBytes & (-65536)) == -16842752) {
                            charset = "UTF-16BE";
                            this.buffer[0] = (char) ((this.buffer[2] << '\b') | this.buffer[3]);
                            this.limit = 1;
                            break;
                        } else if ((firstFourBytes & (-65536)) == -131072) {
                            charset = "UTF-16LE";
                            this.buffer[0] = (char) ((this.buffer[3] << '\b') | this.buffer[2]);
                            this.limit = 1;
                            break;
                        } else if ((firstFourBytes & (-256)) == -272908544) {
                            charset = "UTF-8";
                            this.buffer[0] = this.buffer[3];
                            this.limit = 1;
                            break;
                        }
                        break;
                }
            }
        }
        if (charset == null) {
            charset = "UTF-8";
        }
        int savedLimit = this.limit;
        setInput(new InputStreamReader(is, charset));
        this.encoding = charset;
        this.limit = savedLimit;
        if (!detectCharset && peekCharacter() == 65279) {
            this.limit--;
            System.arraycopy(this.buffer, 1, this.buffer, 0, this.limit);
        }
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean getFeature(String feature) {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(feature)) {
            return this.processNsp;
        }
        if (FEATURE_RELAXED.equals(feature)) {
            return this.relaxed;
        }
        if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(feature)) {
            return this.processDocDecl;
        }
        return false;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getInputEncoding() {
        return this.encoding;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void defineEntityReplacementText(String entity, String value) throws XmlPullParserException {
        if (this.processDocDecl) {
            throw new IllegalStateException("Entity replacement text may not be defined with DOCTYPE processing enabled.");
        }
        if (this.reader == null) {
            throw new IllegalStateException("Entity replacement text must be defined after setInput()");
        }
        if (this.documentEntities == null) {
            this.documentEntities = new HashMap();
        }
        this.documentEntities.put(entity, value.toCharArray());
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public Object getProperty(String property) {
        if (property.equals(PROPERTY_XMLDECL_VERSION)) {
            return this.version;
        }
        if (property.equals(PROPERTY_XMLDECL_STANDALONE)) {
            return this.standalone;
        }
        if (property.equals(PROPERTY_LOCATION)) {
            return this.location != null ? this.location : this.reader.toString();
        }
        return null;
    }

    public String getRootElementName() {
        return this.rootElementName;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getNamespaceCount(int depth) {
        if (depth > this.depth) {
            throw new IndexOutOfBoundsException();
        }
        return this.nspCounts[depth];
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespacePrefix(int pos) {
        return this.nspStack[pos * 2];
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespaceUri(int pos) {
        return this.nspStack[(pos * 2) + 1];
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace(String prefix) {
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        for (int i = (getNamespaceCount(this.depth) << 1) - 2; i >= 0; i -= 2) {
            if (prefix == null) {
                if (this.nspStack[i] == null) {
                    return this.nspStack[i + 1];
                }
            } else if (prefix.equals(this.nspStack[i])) {
                return this.nspStack[i + 1];
            }
        }
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getDepth() {
        return this.depth;
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getPositionDescription() {
        StringBuilder buf = new StringBuilder(this.type < TYPES.length ? TYPES[this.type] : "unknown");
        buf.append(' ');
        if (this.type == 2 || this.type == 3) {
            if (this.degenerated) {
                buf.append("(empty) ");
            }
            buf.append('<');
            if (this.type == 3) {
                buf.append('/');
            }
            if (this.prefix != null) {
                buf.append("{" + this.namespace + "}" + this.prefix + Separators.COLON);
            }
            buf.append(this.name);
            int cnt = this.attributeCount * 4;
            for (int i = 0; i < cnt; i += 4) {
                buf.append(' ');
                if (this.attributes[i + 1] != null) {
                    buf.append("{" + this.attributes[i] + "}" + this.attributes[i + 1] + Separators.COLON);
                }
                buf.append(this.attributes[i + 2] + "='" + this.attributes[i + 3] + Separators.QUOTE);
            }
            buf.append('>');
        } else if (this.type != 7) {
            if (this.type != 4) {
                buf.append(getText());
            } else if (this.isWhitespace) {
                buf.append("(whitespace)");
            } else {
                String text = getText();
                if (text.length() > 16) {
                    text = text.substring(0, 16) + "...";
                }
                buf.append(text);
            }
        }
        buf.append(Separators.AT + getLineNumber() + Separators.COLON + getColumnNumber());
        if (this.location != null) {
            buf.append(" in ");
            buf.append(this.location);
        } else if (this.reader != null) {
            buf.append(" in ");
            buf.append(this.reader.toString());
        }
        return buf.toString();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getLineNumber() {
        int result = this.bufferStartLine;
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                result++;
            }
        }
        return result + 1;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getColumnNumber() {
        int result = this.bufferStartColumn;
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                result = 0;
            } else {
                result++;
            }
        }
        return result + 1;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isWhitespace() throws XmlPullParserException {
        if (this.type != 4 && this.type != 7 && this.type != 5) {
            throw new XmlPullParserException(ILLEGAL_TYPE, this, null);
        }
        return this.isWhitespace;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getText() {
        if (this.type >= 4) {
            if (this.type == 6 && this.unresolved) {
                return null;
            }
            if (this.text == null) {
                return "";
            }
            return this.text;
        }
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public char[] getTextCharacters(int[] poslen) {
        String text = getText();
        if (text == null) {
            poslen[0] = -1;
            poslen[1] = -1;
            return null;
        }
        char[] result = text.toCharArray();
        poslen[0] = 0;
        poslen[1] = result.length;
        return result;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace() {
        return this.namespace;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getName() {
        return this.name;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPrefix() {
        return this.prefix;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isEmptyElementTag() throws XmlPullParserException {
        if (this.type != 2) {
            throw new XmlPullParserException(ILLEGAL_TYPE, this, null);
        }
        return this.degenerated;
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public int getAttributeCount() {
        return this.attributeCount;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeType(int index) {
        return "CDATA";
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isAttributeDefault(int index) {
        return false;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeNamespace(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[index * 4];
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeName(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(index * 4) + 2];
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributePrefix(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(index * 4) + 1];
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeValue(int index) {
        if (index >= this.attributeCount) {
            throw new IndexOutOfBoundsException();
        }
        return this.attributes[(index * 4) + 3];
    }

    @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
    public String getAttributeValue(String namespace, String name) {
        for (int i = (this.attributeCount * 4) - 4; i >= 0; i -= 4) {
            if (this.attributes[i + 2].equals(name) && (namespace == null || this.attributes[i].equals(namespace))) {
                return this.attributes[i + 3];
            }
        }
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getEventType() throws XmlPullParserException {
        return this.type;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextTag() throws XmlPullParserException, IOException {
        next();
        if (this.type == 4 && this.isWhitespace) {
            next();
        }
        if (this.type != 3 && this.type != 2) {
            throw new XmlPullParserException("unexpected type", this, null);
        }
        return this.type;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (type != this.type || ((namespace != null && !namespace.equals(getNamespace())) || (name != null && !name.equals(getName())))) {
            throw new XmlPullParserException("expected: " + TYPES[type] + " {" + namespace + "}" + name, this, null);
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String nextText() throws XmlPullParserException, IOException {
        String result;
        if (this.type != 2) {
            throw new XmlPullParserException("precondition: START_TAG", this, null);
        }
        next();
        if (this.type == 4) {
            result = getText();
            next();
        } else {
            result = "";
        }
        if (this.type != 3) {
            throw new XmlPullParserException("END_TAG expected", this, null);
        }
        return result;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setFeature(String feature, boolean value) throws XmlPullParserException {
        if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(feature)) {
            this.processNsp = value;
        } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(feature)) {
            this.processDocDecl = value;
        } else if (FEATURE_RELAXED.equals(feature)) {
            this.relaxed = value;
        } else {
            throw new XmlPullParserException("unsupported feature: " + feature, this, null);
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setProperty(String property, Object value) throws XmlPullParserException {
        if (property.equals(PROPERTY_LOCATION)) {
            this.location = String.valueOf(value);
            return;
        }
        throw new XmlPullParserException("unsupported property: " + property);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: KXmlParser$ContentSource.class */
    public static class ContentSource {
        private final ContentSource next;
        private final char[] buffer;
        private final int position;
        private final int limit;

        ContentSource(ContentSource next, char[] buffer, int position, int limit) {
            this.next = next;
            this.buffer = buffer;
            this.position = position;
            this.limit = limit;
        }
    }

    private void pushContentSource(char[] newBuffer) {
        this.nextContentSource = new ContentSource(this.nextContentSource, this.buffer, this.position, this.limit);
        this.buffer = newBuffer;
        this.position = 0;
        this.limit = newBuffer.length;
    }

    private void popContentSource() {
        this.buffer = this.nextContentSource.buffer;
        this.position = this.nextContentSource.position;
        this.limit = this.nextContentSource.limit;
        this.nextContentSource = this.nextContentSource.next;
    }
}