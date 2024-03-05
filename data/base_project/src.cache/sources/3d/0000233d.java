package java.lang.reflect;

import java.lang.annotation.Annotation;

/* loaded from: AnnotatedElement.class */
public interface AnnotatedElement {
    <T extends Annotation> T getAnnotation(Class<T> cls);

    Annotation[] getAnnotations();

    Annotation[] getDeclaredAnnotations();

    boolean isAnnotationPresent(Class<? extends Annotation> cls);
}