package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: Runtime.class */
public class Runtime {
    public native void gc();

    public native long freeMemory();

    public native long totalMemory();

    public native long maxMemory();

    Runtime() {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String[] progArray) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String[] progArray, String[] envp) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String[] progArray, String[] envp, File directory) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String prog) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String prog, String[] envp) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public Process exec(String prog, String[] envp, File directory) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void exit(int code) {
        throw new RuntimeException("Stub!");
    }

    public static Runtime getRuntime() {
        throw new RuntimeException("Stub!");
    }

    public void load(String pathName) {
        throw new RuntimeException("Stub!");
    }

    public void loadLibrary(String libName) {
        throw new RuntimeException("Stub!");
    }

    public void runFinalization() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static void runFinalizersOnExit(boolean run) {
        throw new RuntimeException("Stub!");
    }

    public void traceInstructions(boolean enable) {
        throw new RuntimeException("Stub!");
    }

    public void traceMethodCalls(boolean enable) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public InputStream getLocalizedInputStream(InputStream stream) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public OutputStream getLocalizedOutputStream(OutputStream stream) {
        throw new RuntimeException("Stub!");
    }

    public void addShutdownHook(Thread hook) {
        throw new RuntimeException("Stub!");
    }

    public boolean removeShutdownHook(Thread hook) {
        throw new RuntimeException("Stub!");
    }

    public void halt(int code) {
        throw new RuntimeException("Stub!");
    }

    public int availableProcessors() {
        throw new RuntimeException("Stub!");
    }
}