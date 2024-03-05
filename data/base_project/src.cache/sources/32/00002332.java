package java.lang.annotation;

/* loaded from: IncompleteAnnotationException.class */
public class IncompleteAnnotationException extends RuntimeException {
    public IncompleteAnnotationException(Class<? extends Annotation> annotationType, String elementName) {
        throw new RuntimeException("Stub!");
    }

    public Class<? extends Annotation> annotationType() {
        throw new RuntimeException("Stub!");
    }

    public String elementName() {
        throw new RuntimeException("Stub!");
    }
}