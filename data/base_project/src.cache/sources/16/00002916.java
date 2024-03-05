package libcore.reflect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/* loaded from: GenericArrayTypeImpl.class */
public final class GenericArrayTypeImpl implements GenericArrayType {
    private final Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.componentType = componentType;
    }

    @Override // java.lang.reflect.GenericArrayType
    public Type getGenericComponentType() {
        try {
            return ((ParameterizedTypeImpl) this.componentType).getResolvedType();
        } catch (ClassCastException e) {
            return this.componentType;
        }
    }

    public String toString() {
        return this.componentType.toString() + "[]";
    }
}