package java.lang.annotation;

import java.lang.reflect.Method;

/* loaded from: AnnotationTypeMismatchException.class */
public class AnnotationTypeMismatchException extends RuntimeException {
    public AnnotationTypeMismatchException(Method element, String foundType) {
        throw new RuntimeException("Stub!");
    }

    public Method element() {
        throw new RuntimeException("Stub!");
    }

    public String foundType() {
        throw new RuntimeException("Stub!");
    }
}