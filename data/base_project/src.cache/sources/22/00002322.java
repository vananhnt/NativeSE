package java.lang;

import java.lang.Thread;

/* loaded from: ThreadGroup.class */
public class ThreadGroup implements Thread.UncaughtExceptionHandler {
    public ThreadGroup(String name) {
        throw new RuntimeException("Stub!");
    }

    public ThreadGroup(ThreadGroup parent, String name) {
        throw new RuntimeException("Stub!");
    }

    public int activeCount() {
        throw new RuntimeException("Stub!");
    }

    public int activeGroupCount() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public boolean allowThreadSuspension(boolean b) {
        throw new RuntimeException("Stub!");
    }

    public final void checkAccess() {
        throw new RuntimeException("Stub!");
    }

    public final void destroy() {
        throw new RuntimeException("Stub!");
    }

    public int enumerate(Thread[] threads) {
        throw new RuntimeException("Stub!");
    }

    public int enumerate(Thread[] threads, boolean recurse) {
        throw new RuntimeException("Stub!");
    }

    public int enumerate(ThreadGroup[] groups) {
        throw new RuntimeException("Stub!");
    }

    public int enumerate(ThreadGroup[] groups, boolean recurse) {
        throw new RuntimeException("Stub!");
    }

    public final int getMaxPriority() {
        throw new RuntimeException("Stub!");
    }

    public final String getName() {
        throw new RuntimeException("Stub!");
    }

    public final ThreadGroup getParent() {
        throw new RuntimeException("Stub!");
    }

    public final void interrupt() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isDaemon() {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean isDestroyed() {
        throw new RuntimeException("Stub!");
    }

    public void list() {
        throw new RuntimeException("Stub!");
    }

    public final boolean parentOf(ThreadGroup g) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void resume() {
        throw new RuntimeException("Stub!");
    }

    public final void setDaemon(boolean isDaemon) {
        throw new RuntimeException("Stub!");
    }

    public final void setMaxPriority(int newMax) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void stop() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void suspend() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread t, Throwable e) {
        throw new RuntimeException("Stub!");
    }
}