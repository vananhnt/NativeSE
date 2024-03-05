package junit.runner;

/* loaded from: TestSuiteLoader.class */
public interface TestSuiteLoader {
    Class load(String str) throws ClassNotFoundException;

    Class reload(Class cls) throws ClassNotFoundException;
}