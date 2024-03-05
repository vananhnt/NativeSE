package org.apache.harmony.xml.dom;

import gov.nist.core.Separators;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

/* loaded from: DocumentTypeImpl.class */
public final class DocumentTypeImpl extends LeafNodeImpl implements DocumentType {
    private String qualifiedName;
    private String publicId;
    private String systemId;

    public DocumentTypeImpl(DocumentImpl document, String qualifiedName, String publicId, String systemId) {
        super(document);
        if (qualifiedName == null || "".equals(qualifiedName)) {
            throw new DOMException((short) 14, qualifiedName);
        }
        int prefixSeparator = qualifiedName.lastIndexOf(Separators.COLON);
        if (prefixSeparator != -1) {
            String prefix = qualifiedName.substring(0, prefixSeparator);
            String localName = qualifiedName.substring(prefixSeparator + 1);
            if (!DocumentImpl.isXMLIdentifier(prefix)) {
                throw new DOMException((short) 14, qualifiedName);
            }
            if (!DocumentImpl.isXMLIdentifier(localName)) {
                throw new DOMException((short) 5, qualifiedName);
            }
        } else if (!DocumentImpl.isXMLIdentifier(qualifiedName)) {
            throw new DOMException((short) 5, qualifiedName);
        }
        this.qualifiedName = qualifiedName;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return this.qualifiedName;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 10;
    }

    @Override // org.w3c.dom.DocumentType
    public NamedNodeMap getEntities() {
        return null;
    }

    @Override // org.w3c.dom.DocumentType
    public String getInternalSubset() {
        return null;
    }

    @Override // org.w3c.dom.DocumentType
    public String getName() {
        return this.qualifiedName;
    }

    @Override // org.w3c.dom.DocumentType
    public NamedNodeMap getNotations() {
        return null;
    }

    @Override // org.w3c.dom.DocumentType
    public String getPublicId() {
        return this.publicId;
    }

    @Override // org.w3c.dom.DocumentType
    public String getSystemId() {
        return this.systemId;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getTextContent() throws DOMException {
        return null;
    }
}