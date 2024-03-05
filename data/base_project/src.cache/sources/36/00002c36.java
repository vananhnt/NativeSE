package org.xml.sax.ext;

import org.xml.sax.Attributes;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Attributes2.class */
public interface Attributes2 extends Attributes {
    boolean isDeclared(int i);

    boolean isDeclared(String str);

    boolean isDeclared(String str, String str2);

    boolean isSpecified(int i);

    boolean isSpecified(String str, String str2);

    boolean isSpecified(String str);
}