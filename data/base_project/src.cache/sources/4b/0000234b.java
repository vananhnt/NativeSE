package java.lang.reflect;

import java.io.Serializable;

/* loaded from: Proxy.class */
public class Proxy implements Serializable {
    protected InvocationHandler h;

    protected Proxy(InvocationHandler h) {
        throw new RuntimeException("Stub!");
    }

    public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public static boolean isProxyClass(Class<?> cl) {
        throw new RuntimeException("Stub!");
    }

    public static InvocationHandler getInvocationHandler(Object proxy) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }
}