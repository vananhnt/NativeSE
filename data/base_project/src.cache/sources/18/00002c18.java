package org.w3c.dom;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TypeInfo.class */
public interface TypeInfo {
    public static final int DERIVATION_RESTRICTION = 1;
    public static final int DERIVATION_EXTENSION = 2;
    public static final int DERIVATION_UNION = 4;
    public static final int DERIVATION_LIST = 8;

    String getTypeName();

    String getTypeNamespace();

    boolean isDerivedFrom(String str, String str2, int i);
}