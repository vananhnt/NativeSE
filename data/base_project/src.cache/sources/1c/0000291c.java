package libcore.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/* loaded from: TypeVariableImpl.class */
public final class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D> {
    private TypeVariableImpl<D> formalVar;
    private final GenericDeclaration declOfVarUser;
    private final String name;
    private D genericDeclaration;
    private ListOfTypes bounds;

    public boolean equals(Object o) {
        if (!(o instanceof TypeVariable)) {
            return false;
        }
        TypeVariable<?> that = (TypeVariable) o;
        return getName().equals(that.getName()) && getGenericDeclaration().equals(that.getGenericDeclaration());
    }

    public int hashCode() {
        return (31 * getName().hashCode()) + getGenericDeclaration().hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeVariableImpl(D genericDecl, String name, ListOfTypes bounds) {
        this.genericDeclaration = genericDecl;
        this.name = name;
        this.bounds = bounds;
        this.formalVar = this;
        this.declOfVarUser = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeVariableImpl(D genericDecl, String name) {
        this.name = name;
        this.declOfVarUser = genericDecl;
    }

    static TypeVariable findFormalVar(GenericDeclaration layer, String name) {
        TypeVariable[] formalVars = layer.getTypeParameters();
        for (TypeVariable var : formalVars) {
            if (name.equals(var.getName())) {
                return var;
            }
        }
        return null;
    }

    private static GenericDeclaration nextLayer(GenericDeclaration decl) {
        if (decl instanceof Class) {
            Class cl = (Class) decl;
            GenericDeclaration decl2 = (GenericDeclaration) AnnotationAccess.getEnclosingMethodOrConstructor(cl);
            if (decl2 != null) {
                return decl2;
            }
            return cl.getEnclosingClass();
        } else if (decl instanceof Method) {
            return ((Method) decl).getDeclaringClass();
        } else {
            if (decl instanceof Constructor) {
                return ((Constructor) decl).getDeclaringClass();
            }
            throw new AssertionError();
        }
    }

    void resolve() {
        if (this.formalVar != null) {
            return;
        }
        GenericDeclaration curLayer = this.declOfVarUser;
        do {
            TypeVariable var = findFormalVar(curLayer, this.name);
            if (var == null) {
                curLayer = nextLayer(curLayer);
            } else {
                this.formalVar = (TypeVariableImpl) var;
                this.genericDeclaration = this.formalVar.genericDeclaration;
                this.bounds = this.formalVar.bounds;
                return;
            }
        } while (curLayer != null);
        throw new AssertionError("illegal type variable reference");
    }

    @Override // java.lang.reflect.TypeVariable
    public Type[] getBounds() {
        resolve();
        return (Type[]) this.bounds.getResolvedTypes().clone();
    }

    @Override // java.lang.reflect.TypeVariable
    public D getGenericDeclaration() {
        resolve();
        return this.genericDeclaration;
    }

    @Override // java.lang.reflect.TypeVariable
    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}