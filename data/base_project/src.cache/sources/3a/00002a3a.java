package org.apache.harmony.xml.dom;

import org.w3c.dom.Comment;

/* loaded from: CommentImpl.class */
public final class CommentImpl extends CharacterDataImpl implements Comment {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CommentImpl(DocumentImpl document, String data) {
        super(document, data);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return "#comment";
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 8;
    }

    public boolean containsDashDash() {
        return this.buffer.indexOf("--") != -1;
    }
}