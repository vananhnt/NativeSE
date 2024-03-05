package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Entity.class */
public interface Entity extends Node {
    String getPublicId();

    String getSystemId();

    String getNotationName();

    String getInputEncoding();

    String getXmlEncoding();

    String getXmlVersion();
}