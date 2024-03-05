package junit.framework;

import java.lang.reflect.Constructor;
import java.util.Enumeration;

/* loaded from: TestSuite.class */
public class TestSuite implements Test {
    public TestSuite() {
        throw new RuntimeException("Stub!");
    }

    public TestSuite(Class<?> theClass) {
        throw new RuntimeException("Stub!");
    }

    public TestSuite(Class<? extends TestCase> theClass, String name) {
        throw new RuntimeException("Stub!");
    }

    public TestSuite(String name) {
        throw new RuntimeException("Stub!");
    }

    public TestSuite(Class<?>... classes) {
        throw new RuntimeException("Stub!");
    }

    public TestSuite(Class<? extends TestCase>[] classes, String name) {
        throw new RuntimeException("Stub!");
    }

    public static Test createTest(Class<?> theClass, String name) {
        throw new RuntimeException("Stub!");
    }

    public static Constructor<?> getTestConstructor(Class<?> theClass) throws NoSuchMethodException {
        throw new RuntimeException("Stub!");
    }

    public static Test warning(String message) {
        throw new RuntimeException("Stub!");
    }

    public void addTest(Test test) {
        throw new RuntimeException("Stub!");
    }

    public void addTestSuite(Class<? extends TestCase> testClass) {
        throw new RuntimeException("Stub!");
    }

    @Override // junit.framework.Test
    public int countTestCases() {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    @Override // junit.framework.Test
    public void run(TestResult result) {
        throw new RuntimeException("Stub!");
    }

    public void runTest(Test test, TestResult result) {
        throw new RuntimeException("Stub!");
    }

    public void setName(String name) {
        throw new RuntimeException("Stub!");
    }

    public Test testAt(int index) {
        throw new RuntimeException("Stub!");
    }

    public int testCount() {
        throw new RuntimeException("Stub!");
    }

    public Enumeration<Test> tests() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}