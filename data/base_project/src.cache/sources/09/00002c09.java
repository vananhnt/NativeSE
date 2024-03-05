package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DOMLocator.class */
public interface DOMLocator {
    int getLineNumber();

    int getColumnNumber();

    int getByteOffset();

    int getUtf16Offset();

    Node getRelatedNode();

    String getUri();
}