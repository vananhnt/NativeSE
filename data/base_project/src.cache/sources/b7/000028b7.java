package junit.framework;

/* loaded from: ComparisonFailure.class */
public class ComparisonFailure extends AssertionFailedError {
    public ComparisonFailure(String message, String expected, String actual) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        throw new RuntimeException("Stub!");
    }

    public String getActual() {
        throw new RuntimeException("Stub!");
    }

    public String getExpected() {
        throw new RuntimeException("Stub!");
    }
}