package org.apache.harmony.xml.dom;

import org.w3c.dom.DocumentFragment;

/* loaded from: DocumentFragmentImpl.class */
public class DocumentFragmentImpl extends InnerNodeImpl implements DocumentFragment {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DocumentFragmentImpl(DocumentImpl document) {
        super(document);
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 11;
    }
}