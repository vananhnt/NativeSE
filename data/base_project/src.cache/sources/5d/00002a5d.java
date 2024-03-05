package org.apache.harmony.xml.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;

/* loaded from: ProcessingInstructionImpl.class */
public final class ProcessingInstructionImpl extends LeafNodeImpl implements ProcessingInstruction {
    private String target;
    private String data;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProcessingInstructionImpl(DocumentImpl document, String target, String data) {
        super(document);
        this.target = target;
        this.data = data;
    }

    @Override // org.w3c.dom.ProcessingInstruction
    public String getData() {
        return this.data;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeName() {
        return this.target;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public short getNodeType() {
        return (short) 7;
    }

    @Override // org.apache.harmony.xml.dom.NodeImpl, org.w3c.dom.Node
    public String getNodeValue() {
        return this.data;
    }

    @Override // org.w3c.dom.ProcessingInstruction
    public String getTarget() {
        return this.target;
    }

    @Override // org.w3c.dom.ProcessingInstruction
    public void setData(String data) throws DOMException {
        this.data = data;
    }
}