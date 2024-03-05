package libcore.reflect;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

/* loaded from: ListOfVariables.class */
final class ListOfVariables {
    final ArrayList<TypeVariable<?>> array = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(TypeVariable<?> elem) {
        this.array.add(elem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeVariable<?>[] getArray() {
        TypeVariable<?>[] a = new TypeVariable[this.array.size()];
        return (TypeVariable[]) this.array.toArray(a);
    }
}