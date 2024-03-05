package org.xml.sax;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: AttributeList.class */
public interface AttributeList {
    int getLength();

    String getName(int i);

    String getType(int i);

    String getValue(int i);

    String getType(String str);

    String getValue(String str);
}