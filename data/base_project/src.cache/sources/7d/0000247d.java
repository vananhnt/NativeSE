package java.security;

import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SecureClassLoader.class */
public class SecureClassLoader extends ClassLoader {
    /* JADX INFO: Access modifiers changed from: protected */
    public SecureClassLoader() {
        throw new RuntimeException("Stub!");
    }

    protected SecureClassLoader(ClassLoader parent) {
        throw new RuntimeException("Stub!");
    }

    protected PermissionCollection getPermissions(CodeSource codesource) {
        throw new RuntimeException("Stub!");
    }

    protected final Class<?> defineClass(String name, byte[] b, int off, int len, CodeSource cs) {
        throw new RuntimeException("Stub!");
    }

    protected final Class<?> defineClass(String name, ByteBuffer b, CodeSource cs) {
        throw new RuntimeException("Stub!");
    }
}