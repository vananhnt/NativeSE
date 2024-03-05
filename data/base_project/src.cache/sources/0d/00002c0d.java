package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DocumentType.class */
public interface DocumentType extends Node {
    String getName();

    NamedNodeMap getEntities();

    NamedNodeMap getNotations();

    String getPublicId();

    String getSystemId();

    String getInternalSubset();
}