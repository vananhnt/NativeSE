package java.lang;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.ProtectionDomain;

/* loaded from: Class.class */
public final class Class<T> implements Serializable, AnnotatedElement, GenericDeclaration, Type {
    public native Class<?> getComponentType();

    @Override // java.lang.reflect.AnnotatedElement
    public native Annotation[] getDeclaredAnnotations();

    public native Class<?> getDeclaringClass();

    public native Class<?> getEnclosingClass();

    public native Constructor<?> getEnclosingConstructor();

    public native Method getEnclosingMethod();

    public native Class<?>[] getInterfaces();

    public native Class<? super T> getSuperclass();

    public native boolean isAnonymousClass();

    public native boolean isAssignableFrom(Class<?> cls);

    public native boolean isInstance(Object obj);

    public native boolean isInterface();

    public native boolean isPrimitive();

    public native boolean desiredAssertionStatus();

    Class() {
        throw new RuntimeException("Stub!");
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public static Class<?> forName(String className, boolean shouldInitialize, ClassLoader classLoader) throws ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public Class<?>[] getClasses() {
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

    public String getCanonicalName() {
        throw new RuntimeException("Stub!");
    }

    public ClassLoader getClassLoader() {
        throw new RuntimeException("Stub!");
    }

    public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        throw new RuntimeException("Stub!");
    }

    public Constructor<?>[] getConstructors() {
        throw new RuntimeException("Stub!");
    }

    public Class<?>[] getDeclaredClasses() {
        throw new RuntimeException("Stub!");
    }

    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        throw new RuntimeException("Stub!");
    }

    public Constructor<?>[] getDeclaredConstructors() {
        throw new RuntimeException("Stub!");
    }

    public Field getDeclaredField(String name) throws NoSuchFieldException {
        throw new RuntimeException("Stub!");
    }

    public Field[] getDeclaredFields() {
        throw new RuntimeException("Stub!");
    }

    public Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        throw new RuntimeException("Stub!");
    }

    public Method[] getDeclaredMethods() {
        throw new RuntimeException("Stub!");
    }

    public T[] getEnumConstants() {
        throw new RuntimeException("Stub!");
    }

    public Field getField(String name) throws NoSuchFieldException {
        throw new RuntimeException("Stub!");
    }

    public Field[] getFields() {
        throw new RuntimeException("Stub!");
    }

    public Type[] getGenericInterfaces() {
        throw new RuntimeException("Stub!");
    }

    public Type getGenericSuperclass() {
        throw new RuntimeException("Stub!");
    }

    public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        throw new RuntimeException("Stub!");
    }

    public Method[] getMethods() {
        throw new RuntimeException("Stub!");
    }

    public int getModifiers() {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public String getSimpleName() {
        throw new RuntimeException("Stub!");
    }

    public ProtectionDomain getProtectionDomain() {
        throw new RuntimeException("Stub!");
    }

    public URL getResource(String resourceName) {
        throw new RuntimeException("Stub!");
    }

    public InputStream getResourceAsStream(String resourceName) {
        throw new RuntimeException("Stub!");
    }

    public Object[] getSigners() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.GenericDeclaration
    public synchronized TypeVariable<Class<T>>[] getTypeParameters() {
        throw new RuntimeException("Stub!");
    }

    public boolean isAnnotation() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.reflect.AnnotatedElement
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        throw new RuntimeException("Stub!");
    }

    public boolean isArray() {
        throw new RuntimeException("Stub!");
    }

    public boolean isEnum() {
        throw new RuntimeException("Stub!");
    }

    public boolean isLocalClass() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMemberClass() {
        throw new RuntimeException("Stub!");
    }

    public boolean isSynthetic() {
        throw new RuntimeException("Stub!");
    }

    public T newInstance() throws InstantiationException, IllegalAccessException {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public Package getPackage() {
        throw new RuntimeException("Stub!");
    }

    public <U> Class<? extends U> asSubclass(Class<U> c) {
        throw new RuntimeException("Stub!");
    }

    public T cast(Object obj) {
        throw new RuntimeException("Stub!");
    }
}