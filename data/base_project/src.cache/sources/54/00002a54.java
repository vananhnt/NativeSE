package org.apache.harmony.xml.dom;

import org.w3c.dom.Entity;

/* loaded from: EntityImpl.class */
public class EntityImpl extends NodeImpl implements Entity {
    private String notationName;
    private String publicID;
    private String systemID;

    EntityImpl(DocumentImpl document, String notationName, String publicID, String systemID) {
        super(document);
        this.notationName = notationName;
        this.publicID = publicID;
        this.systemID = systemID;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return getNotationName();
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 6;
    }

    @Override // org.w3c.dom.Entity
    public String getNotationName() {
        return this.notationName;
    }

    @Override // org.w3c.dom.Entity
    public String getPublicId() {
        return this.publicID;
    }

    @Override // org.w3c.dom.Entity
    public String getSystemId() {
        return this.systemID;
    }

    @Override // org.w3c.dom.Entity
    public String getInputEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.Entity
    public String getXmlEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override // org.w3c.dom.Entity
    public String getXmlVersion() {
        throw new UnsupportedOperationException();
    }
}