package javax.xml.validation;

import org.w3c.dom.TypeInfo;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TypeInfoProvider.class */
public abstract class TypeInfoProvider {
    public abstract TypeInfo getElementTypeInfo();

    public abstract TypeInfo getAttributeTypeInfo(int i);

    public abstract boolean isIdAttribute(int i);

    public abstract boolean isSpecified(int i);

    protected TypeInfoProvider() {
    }
}