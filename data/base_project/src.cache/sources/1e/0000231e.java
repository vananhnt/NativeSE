package java.lang;

import java.util.Map;

/* loaded from: Thread.class */
public class Thread implements Runnable {
    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 1;
    public static final int NORM_PRIORITY = 5;

    /* loaded from: Thread$State.class */
    public enum State {
        BLOCKED,
        NEW,
        RUNNABLE,
        TERMINATED,
        TIMED_WAITING,
        WAITING
    }

    /* loaded from: Thread$UncaughtExceptionHandler.class */
    public interface UncaughtExceptionHandler {
        void uncaughtException(Thread thread, Throwable th);
    }

    public Thread() {
        throw new RuntimeException("Stub!");
    }

    public Thread(Runnable runnable) {
        throw new RuntimeException("Stub!");
    }

    public Thread(Runnable runnable, String threadName) {
        throw new RuntimeException("Stub!");
    }

    public Thread(String threadName) {
        throw new RuntimeException("Stub!");
    }

    public Thread(ThreadGroup group, Runnable runnable) {
        throw new RuntimeException("Stub!");
    }

    public Thread(ThreadGroup group, Runnable runnable, String threadName) {
        throw new RuntimeException("Stub!");
    }

    public Thread(ThreadGroup group, String threadName) {
        throw new RuntimeException("Stub!");
    }

    public Thread(ThreadGroup group, Runnable runnable, String threadName, long stackSize) {
        throw new RuntimeException("Stub!");
    }

    public static int activeCount() {
        throw new RuntimeException("Stub!");
    }

    public final void checkAccess() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public int countStackFrames() {
        throw new RuntimeException("Stub!");
    }

    public static Thread currentThread() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public void destroy() {
        throw new RuntimeException("Stub!");
    }

    public static void dumpStack() {
        throw new RuntimeException("Stub!");
    }

    public static int enumerate(Thread[] threads) {
        throw new RuntimeException("Stub!");
    }

    public static Map<Thread, StackTraceElement[]> getAllStackTraces() {
        throw new RuntimeException("Stub!");
    }

    public ClassLoader getContextClassLoader() {
        throw new RuntimeException("Stub!");
    }

    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        throw new RuntimeException("Stub!");
    }

    public long getId() {
        throw new RuntimeException("Stub!");
    }

    public final String getName() {
        throw new RuntimeException("Stub!");
    }

    public final int getPriority() {
        throw new RuntimeException("Stub!");
    }

    public StackTraceElement[] getStackTrace() {
        throw new RuntimeException("Stub!");
    }

    public State getState() {
        throw new RuntimeException("Stub!");
    }

    public final ThreadGroup getThreadGroup() {
        throw new RuntimeException("Stub!");
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        throw new RuntimeException("Stub!");
    }

    public void interrupt() {
        throw new RuntimeException("Stub!");
    }

    public static boolean interrupted() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isAlive() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isDaemon() {
        throw new RuntimeException("Stub!");
    }

    public boolean isInterrupted() {
        throw new RuntimeException("Stub!");
    }

    public final void join() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public final void join(long millis) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public final void join(long millis, int nanos) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void resume() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Runnable
    public void run() {
        throw new RuntimeException("Stub!");
    }

    public void setContextClassLoader(ClassLoader cl) {
        throw new RuntimeException("Stub!");
    }

    public final void setDaemon(boolean isDaemon) {
        throw new RuntimeException("Stub!");
    }

    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        throw new RuntimeException("Stub!");
    }

    public final void setName(String threadName) {
        throw new RuntimeException("Stub!");
    }

    public final void setPriority(int priority) {
        throw new RuntimeException("Stub!");
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        throw new RuntimeException("Stub!");
    }

    public static void sleep(long time) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public static void sleep(long millis, int nanos) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void start() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void stop() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final synchronized void stop(Throwable throwable) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public final void suspend() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public static void yield() {
        throw new RuntimeException("Stub!");
    }

    public static boolean holdsLock(Object object) {
        throw new RuntimeException("Stub!");
    }
}