package java.lang;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.util.Map;
import java.util.Properties;

/* loaded from: System.class */
public final class System {
    public static final InputStream in = null;
    public static final PrintStream out = null;
    public static final PrintStream err = null;

    public static native void arraycopy(Object obj, int i, Object obj2, int i2, int i3);

    public static native long currentTimeMillis();

    public static native long nanoTime();

    public static native int identityHashCode(Object obj);

    public static native String mapLibraryName(String str);

    System() {
        throw new RuntimeException("Stub!");
    }

    public static void setIn(InputStream newIn) {
        throw new RuntimeException("Stub!");
    }

    public static void setOut(PrintStream newOut) {
        throw new RuntimeException("Stub!");
    }

    public static void setErr(PrintStream newErr) {
        throw new RuntimeException("Stub!");
    }

    public static void exit(int code) {
        throw new RuntimeException("Stub!");
    }

    public static void gc() {
        throw new RuntimeException("Stub!");
    }

    public static String getenv(String name) {
        throw new RuntimeException("Stub!");
    }

    public static Map<String, String> getenv() {
        throw new RuntimeException("Stub!");
    }

    public static Channel inheritedChannel() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public static Properties getProperties() {
        throw new RuntimeException("Stub!");
    }

    public static String getProperty(String propertyName) {
        throw new RuntimeException("Stub!");
    }

    public static String getProperty(String name, String defaultValue) {
        throw new RuntimeException("Stub!");
    }

    public static String setProperty(String name, String value) {
        throw new RuntimeException("Stub!");
    }

    public static String clearProperty(String name) {
        throw new RuntimeException("Stub!");
    }

    public static Console console() {
        throw new RuntimeException("Stub!");
    }

    public static SecurityManager getSecurityManager() {
        throw new RuntimeException("Stub!");
    }

    public static void load(String pathName) {
        throw new RuntimeException("Stub!");
    }

    public static void loadLibrary(String libName) {
        throw new RuntimeException("Stub!");
    }

    public static void runFinalization() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static void runFinalizersOnExit(boolean flag) {
        throw new RuntimeException("Stub!");
    }

    public static void setProperties(Properties p) {
        throw new RuntimeException("Stub!");
    }

    public static void setSecurityManager(SecurityManager sm) {
        throw new RuntimeException("Stub!");
    }
}