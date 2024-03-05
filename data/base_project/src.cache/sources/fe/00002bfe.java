package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Attr.class */
public interface Attr extends Node {
    String getName();

    boolean getSpecified();

    String getValue();

    void setValue(String str) throws DOMException;

    Element getOwnerElement();

    TypeInfo getSchemaTypeInfo();

    boolean isId();
}