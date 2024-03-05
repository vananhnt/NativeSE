package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharacterData.class */
public interface CharacterData extends Node {
    String getData() throws DOMException;

    void setData(String str) throws DOMException;

    int getLength();

    String substringData(int i, int i2) throws DOMException;

    void appendData(String str) throws DOMException;

    void insertData(int i, String str) throws DOMException;

    void deleteData(int i, int i2) throws DOMException;

    void replaceData(int i, int i2, String str) throws DOMException;
}