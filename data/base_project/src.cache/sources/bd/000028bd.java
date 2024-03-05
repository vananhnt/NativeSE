package junit.framework;

import java.util.Enumeration;
import java.util.Vector;

/* loaded from: TestResult.class */
public class TestResult {
    protected Vector<TestFailure> fFailures;
    protected Vector<TestFailure> fErrors;
    protected Vector<TestListener> fListeners;
    protected int fRunTests;

    public TestResult() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void addError(Test test, Throwable t) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void addFailure(Test test, AssertionFailedError t) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void addListener(TestListener listener) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void removeListener(TestListener listener) {
        throw new RuntimeException("Stub!");
    }

    public void endTest(Test test) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int errorCount() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Enumeration<TestFailure> errors() {
        throw new RuntimeException("Stub!");
    }

    public synchronized int failureCount() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Enumeration<TestFailure> failures() {
        throw new RuntimeException("Stub!");
    }

    protected void run(TestCase test) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int runCount() {
        throw new RuntimeException("Stub!");
    }

    public void runProtected(Test test, Protectable p) {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean shouldStop() {
        throw new RuntimeException("Stub!");
    }

    public void startTest(Test test) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void stop() {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean wasSuccessful() {
        throw new RuntimeException("Stub!");
    }
}