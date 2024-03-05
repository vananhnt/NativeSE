package org.apache.harmony.xml.parsers;

import gov.nist.core.Separators;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import org.apache.harmony.xml.dom.CDATASectionImpl;
import org.apache.harmony.xml.dom.DOMImplementationImpl;
import org.apache.harmony.xml.dom.DocumentImpl;
import org.apache.harmony.xml.dom.DocumentTypeImpl;
import org.apache.harmony.xml.dom.TextImpl;
import org.kxml2.io.KXmlParser;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: DocumentBuilderImpl.class */
class DocumentBuilderImpl extends DocumentBuilder {
    private static DOMImplementationImpl dom = DOMImplementationImpl.getInstance();
    private boolean coalescing;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private boolean ignoreComments;
    private boolean ignoreElementContentWhitespace;
    private boolean namespaceAware;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.parsers.DocumentBuilderImpl.parse(org.xml.sax.InputSource):org.w3c.dom.Document, file: DocumentBuilderImpl.class
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
    @Override // javax.xml.parsers.DocumentBuilder
    public org.w3c.dom.Document parse(org.xml.sax.InputSource r1) throws org.xml.sax.SAXException, java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: org.apache.harmony.xml.parsers.DocumentBuilderImpl.parse(org.xml.sax.InputSource):org.w3c.dom.Document, file: DocumentBuilderImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.parsers.DocumentBuilderImpl.parse(org.xml.sax.InputSource):org.w3c.dom.Document");
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void reset() {
        this.coalescing = false;
        this.entityResolver = null;
        this.errorHandler = null;
        this.ignoreComments = false;
        this.ignoreElementContentWhitespace = false;
        this.namespaceAware = false;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public DOMImplementation getDOMImplementation() {
        return dom;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public boolean isValidating() {
        return false;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public Document newDocument() {
        return dom.createDocument(null, null, null);
    }

    private void parse(KXmlParser parser, DocumentImpl document, Node node, int endToken) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        while (true) {
            int token = eventType;
            if (token != endToken && token != 1) {
                if (token == 8) {
                    String text = parser.getText();
                    int dot = text.indexOf(32);
                    String target = dot != -1 ? text.substring(0, dot) : text;
                    String data = dot != -1 ? text.substring(dot + 1) : "";
                    node.appendChild(document.createProcessingInstruction(target, data));
                } else if (token == 10) {
                    String name = parser.getRootElementName();
                    String publicId = parser.getPublicId();
                    String systemId = parser.getSystemId();
                    document.appendChild(new DocumentTypeImpl(document, name, publicId, systemId));
                } else if (token == 9) {
                    if (!this.ignoreComments) {
                        node.appendChild(document.createComment(parser.getText()));
                    }
                } else if (token == 7) {
                    if (!this.ignoreElementContentWhitespace && document != node) {
                        appendText(document, node, token, parser.getText());
                    }
                } else if (token == 4 || token == 5) {
                    appendText(document, node, token, parser.getText());
                } else if (token == 6) {
                    String entity = parser.getName();
                    if (this.entityResolver != null) {
                    }
                    String resolved = resolvePredefinedOrCharacterEntity(entity);
                    if (resolved != null) {
                        appendText(document, node, token, resolved);
                    } else {
                        node.appendChild(document.createEntityReference(entity));
                    }
                } else if (token == 2) {
                    if (this.namespaceAware) {
                        String namespace = parser.getNamespace();
                        String name2 = parser.getName();
                        String prefix = parser.getPrefix();
                        if ("".equals(namespace)) {
                            namespace = null;
                        }
                        Element element = document.createElementNS(namespace, name2);
                        element.setPrefix(prefix);
                        node.appendChild(element);
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attrNamespace = parser.getAttributeNamespace(i);
                            String attrPrefix = parser.getAttributePrefix(i);
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if ("".equals(attrNamespace)) {
                                attrNamespace = null;
                            }
                            Attr attr = document.createAttributeNS(attrNamespace, attrName);
                            attr.setPrefix(attrPrefix);
                            attr.setValue(attrValue);
                            element.setAttributeNodeNS(attr);
                        }
                        parser.nextToken();
                        parse(parser, document, element, 3);
                        parser.require(3, namespace, name2);
                    } else {
                        String name3 = parser.getName();
                        Element element2 = document.createElement(name3);
                        node.appendChild(element2);
                        for (int i2 = 0; i2 < parser.getAttributeCount(); i2++) {
                            String attrName2 = parser.getAttributeName(i2);
                            String attrValue2 = parser.getAttributeValue(i2);
                            Attr attr2 = document.createAttribute(attrName2);
                            attr2.setValue(attrValue2);
                            element2.setAttributeNode(attr2);
                        }
                        parser.nextToken();
                        parse(parser, document, element2, 3);
                        parser.require(3, "", name3);
                    }
                }
                eventType = parser.nextToken();
            } else {
                return;
            }
        }
    }

    private void appendText(DocumentImpl document, Node parent, int token, String text) {
        Node lastChild;
        if (text.isEmpty()) {
            return;
        }
        if ((this.coalescing || token != 5) && (lastChild = parent.getLastChild()) != null && lastChild.getNodeType() == 3) {
            Text textNode = (Text) lastChild;
            textNode.appendData(text);
            return;
        }
        parent.appendChild(token == 5 ? new CDATASectionImpl(document, text) : new TextImpl(document, text));
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setEntityResolver(EntityResolver resolver) {
        this.entityResolver = resolver;
    }

    @Override // javax.xml.parsers.DocumentBuilder
    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public void setIgnoreComments(boolean value) {
        this.ignoreComments = value;
    }

    public void setCoalescing(boolean value) {
        this.coalescing = value;
    }

    public void setIgnoreElementContentWhitespace(boolean value) {
        this.ignoreElementContentWhitespace = value;
    }

    public void setNamespaceAware(boolean value) {
        this.namespaceAware = value;
    }

    private String resolvePredefinedOrCharacterEntity(String entityName) {
        if (entityName.startsWith("#x")) {
            return resolveCharacterReference(entityName.substring(2), 16);
        }
        if (entityName.startsWith(Separators.POUND)) {
            return resolveCharacterReference(entityName.substring(1), 10);
        }
        if ("lt".equals(entityName)) {
            return Separators.LESS_THAN;
        }
        if ("gt".equals(entityName)) {
            return Separators.GREATER_THAN;
        }
        if ("amp".equals(entityName)) {
            return Separators.AND;
        }
        if ("apos".equals(entityName)) {
            return Separators.QUOTE;
        }
        if ("quot".equals(entityName)) {
            return Separators.DOUBLE_QUOTE;
        }
        return null;
    }

    private String resolveCharacterReference(String value, int base) {
        try {
            int ch = Integer.parseInt(value, base);
            if (ch < 0 || ch > 65535) {
                return null;
            }
            return String.valueOf((char) ch);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}