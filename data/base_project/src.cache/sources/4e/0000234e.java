package java.lang.reflect;

import java.lang.reflect.GenericDeclaration;

/* loaded from: TypeVariable.class */
public interface TypeVariable<D extends GenericDeclaration> extends Type {
    Type[] getBounds();

    D getGenericDeclaration();

    String getName();
}