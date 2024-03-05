package libcore.reflect;

import gov.nist.core.Separators;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: ParameterizedTypeImpl.class */
public final class ParameterizedTypeImpl implements ParameterizedType {
    private final ListOfTypes args;
    private final ParameterizedTypeImpl ownerType0;
    private Type ownerTypeRes;
    private Class rawType;
    private final String rawTypeName;
    private ClassLoader loader;

    public ParameterizedTypeImpl(ParameterizedTypeImpl ownerType, String rawTypeName, ListOfTypes args, ClassLoader loader) {
        this.ownerType0 = ownerType;
        this.rawTypeName = rawTypeName;
        this.args = args;
        this.loader = loader;
    }

    @Override // java.lang.reflect.ParameterizedType
    public Type[] getActualTypeArguments() {
        return (Type[]) this.args.getResolvedTypes().clone();
    }

    @Override // java.lang.reflect.ParameterizedType
    public Type getOwnerType() {
        if (this.ownerTypeRes == null) {
            if (this.ownerType0 != null) {
                this.ownerTypeRes = this.ownerType0.getResolvedType();
            } else {
                this.ownerTypeRes = getRawType().getDeclaringClass();
            }
        }
        return this.ownerTypeRes;
    }

    @Override // java.lang.reflect.ParameterizedType
    public Class getRawType() {
        if (this.rawType == null) {
            try {
                this.rawType = Class.forName(this.rawTypeName, false, this.loader);
            } catch (ClassNotFoundException e) {
                throw new TypeNotPresentException(this.rawTypeName, e);
            }
        }
        return this.rawType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Type getResolvedType() {
        if (this.args.getResolvedTypes().length == 0) {
            return getRawType();
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.rawTypeName);
        if (this.args.length() > 0) {
            sb.append(Separators.LESS_THAN).append(this.args).append(Separators.GREATER_THAN);
        }
        return sb.toString();
    }
}