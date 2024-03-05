package java.lang.reflect;

import java.lang.annotation.Annotation;

/* loaded from: AccessibleObject.class */
public class AccessibleObject implements AnnotatedElement {
    /* JADX INFO: Access modifiers changed from: protected */
    public AccessibleObject() {
        throw new RuntimeException("Stub!");
    }

    public static void setAccessible(AccessibleObject[] objects, boolean flag) {
        throw new RuntimeException("Stub!");
    }

    public boolean isAccessible() {
        throw new RuntimeException("Stub!");
    }

    public void setAccessible(boolean flag) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getDeclaredAnnotations() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getAnnotations() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        throw new RuntimeException("Stub!");
    }
}