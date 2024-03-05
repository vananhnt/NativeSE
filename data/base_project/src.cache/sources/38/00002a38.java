package org.apache.harmony.xml.dom;

import gov.nist.core.Separators;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

/* loaded from: CDATASectionImpl.class */
public final class CDATASectionImpl extends TextImpl implements CDATASection {
    public CDATASectionImpl(DocumentImpl document, String data) {
        super(document, data);
    }

    @Override // org.apache.harmony.xml.dom.TextImpl, org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return "#cdata-section";
    }

    @Override // org.apache.harmony.xml.dom.TextImpl, org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 4;
    }

    public void split() {
        if (!needsSplitting()) {
            return;
        }
        Node parent = getParentNode();
        String[] parts = getData().split("\\]\\]>");
        parent.insertBefore(new CDATASectionImpl(this.document, parts[0] + "]]"), this);
        for (int p = 1; p < parts.length - 1; p++) {
            parent.insertBefore(new CDATASectionImpl(this.document, Separators.GREATER_THAN + parts[p] + "]]"), this);
        }
        setData(Separators.GREATER_THAN + parts[parts.length - 1]);
    }

    public boolean needsSplitting() {
        return this.buffer.indexOf("]]>") != -1;
    }

    public TextImpl replaceWithText() {
        TextImpl replacement = new TextImpl(this.document, getData());
        this.parent.insertBefore(replacement, this);
        this.parent.removeChild(this);
        return replacement;
    }
}