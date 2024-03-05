package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ProcessingInstruction.class */
public interface ProcessingInstruction extends Node {
    String getTarget();

    String getData();

    void setData(String str) throws DOMException;
}