package libcore.reflect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import libcore.util.EmptyArray;

/* loaded from: ListOfTypes.class */
public final class ListOfTypes {
    public static final ListOfTypes EMPTY = new ListOfTypes(0);
    private final ArrayList<Type> types;
    private Type[] resolvedTypes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListOfTypes(int capacity) {
        this.types = new ArrayList<>(capacity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ListOfTypes(Type[] types) {
        this.types = new ArrayList<>(types.length);
        for (Type type : types) {
            this.types.add(type);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        this.types.add(type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int length() {
        return this.types.size();
    }

    public Type[] getResolvedTypes() {
        Type[] result = this.resolvedTypes;
        if (result == null) {
            result = resolveTypes(this.types);
            this.resolvedTypes = result;
        }
        return result;
    }

    private Type[] resolveTypes(List<Type> unresolved) {
        int size = unresolved.size();
        if (size == 0) {
            return EmptyArray.TYPE;
        }
        Type[] result = new Type[size];
        for (int i = 0; i < size; i++) {
            Type type = unresolved.get(i);
            try {
                result[i] = ((ParameterizedTypeImpl) type).getResolvedType();
            } catch (ClassCastException e) {
                result[i] = type;
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.types.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(this.types.get(i));
        }
        return result.toString();
    }
}