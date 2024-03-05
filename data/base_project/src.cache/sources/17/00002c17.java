package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Text.class */
public interface Text extends CharacterData {
    Text splitText(int i) throws DOMException;

    boolean isElementContentWhitespace();

    String getWholeText();

    Text replaceWholeText(String str) throws DOMException;
}