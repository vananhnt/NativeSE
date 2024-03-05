package java.lang.annotation;

/* loaded from: Annotation.class */
public interface Annotation {
    Class<? extends Annotation> annotationType();

    boolean equals(Object obj);

    int hashCode();

    String toString();
}