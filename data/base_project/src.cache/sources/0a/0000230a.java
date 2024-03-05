package java.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;

/* loaded from: Package.class */
public class Package implements AnnotatedElement {
    Package() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getAnnotations() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public Annotation[] getDeclaredAnnotations() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        throw new RuntimeException("Stub!");
    }

    public String getImplementationTitle() {
        throw new RuntimeException("Stub!");
    }

    public String getImplementationVendor() {
        throw new RuntimeException("Stub!");
    }

    public String getImplementationVersion() {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public static Package getPackage(String packageName) {
        throw new RuntimeException("Stub!");
    }

    public static Package[] getPackages() {
        throw new RuntimeException("Stub!");
    }

    public String getSpecificationTitle() {
        throw new RuntimeException("Stub!");
    }

    public String getSpecificationVendor() {
        throw new RuntimeException("Stub!");
    }

    public String getSpecificationVersion() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isCompatibleWith(String version) throws NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public boolean isSealed() {
        throw new RuntimeException("Stub!");
    }

    public boolean isSealed(URL url) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}