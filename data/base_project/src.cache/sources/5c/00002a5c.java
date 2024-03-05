package org.apache.harmony.xml.dom;

import org.w3c.dom.Notation;

/* loaded from: NotationImpl.class */
public class NotationImpl extends LeafNodeImpl implements Notation {
    private String notationName;
    private String publicID;
    private String systemID;

    NotationImpl(DocumentImpl document, String notationName, String publicID, String systemID) {
        super(document);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return this.notationName;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 12;
    }

    @Override // org.w3c.dom.Notation
    public String getPublicId() {
        return this.publicID;
    }

    @Override // org.w3c.dom.Notation
    public String getSystemId() {
        return this.systemID;
    }
}