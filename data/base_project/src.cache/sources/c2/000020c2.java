package dalvik.system;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/* loaded from: BaseDexClassLoader.class */
public class BaseDexClassLoader extends ClassLoader {
    public BaseDexClassLoader(String dexPath, File optimizedDirectory, String libraryPath, ClassLoader parent) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    protected URL findResource(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    protected Enumeration<URL> findResources(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    public String findLibrary(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ClassLoader
    protected synchronized Package getPackage(String name) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}