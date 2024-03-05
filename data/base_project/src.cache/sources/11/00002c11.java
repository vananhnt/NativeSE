package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NameList.class */
public interface NameList {
    String getName(int i);

    String getNamespaceURI(int i);

    int getLength();

    boolean contains(String str);

    boolean containsNS(String str, String str2);
}