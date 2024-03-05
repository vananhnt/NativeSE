package android.hardware.camera2.utils;

/* loaded from: UncheckedThrow.class */
public class UncheckedThrow {
    public static void throwAnyException(Exception e) {
        throwAnyImpl(e);
    }

    private static <T extends Exception> void throwAnyImpl(Exception e) throws Exception {
        throw e;
    }
}