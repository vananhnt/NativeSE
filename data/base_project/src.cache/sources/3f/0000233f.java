package java.lang.reflect;

import java.lang.annotation.Annotation;

/* loaded from: Constructor.class */
public final class Constructor<T> extends AccessibleObject implements GenericDeclaration, Member {
    Constructor() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.GenericDeclaration
    public TypeVariable<Constructor<T>>[] getTypeParameters() {
        throw new RuntimeException("Stub!");
    }

    public String toGenericString() {
        throw new RuntimeException("Stub!");
    }

    public Type[] getGenericParameterTypes() {
        throw new RuntimeException("Stub!");
    }

    public Type[] getGenericExceptionTypes() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AccessibleObject, java.lang.reflect.AnnotatedElement
    public Annotation[] getDeclaredAnnotations() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AccessibleObject, java.lang.reflect.AnnotatedElement
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AccessibleObject, java.lang.reflect.AnnotatedElement
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        throw new RuntimeException("Stub!");
    }

    public Annotation[][] getParameterAnnotations() {
        throw new RuntimeException("Stub!");
    }

    public boolean isVarArgs() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.Member
    public boolean isSynthetic() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.Member
    public Class<T> getDeclaringClass() {
        throw new RuntimeException("Stub!");
    }

    public Class<?>[] getExceptionTypes() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.Member
    public int getModifiers() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.Member
    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public Class<?>[] getParameterTypes() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public T newInstance(Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}