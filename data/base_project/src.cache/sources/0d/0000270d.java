package java.util.concurrent.atomic;

/* loaded from: Fences.class */
public class Fences {
    private static volatile int theVolatile;

    private Fences() {
    }

    public static <T> T orderReads(T ref) {
        int i = theVolatile;
        return ref;
    }

    public static <T> T orderWrites(T ref) {
        theVolatile = 0;
        return ref;
    }

    public static <T> T orderAccesses(T ref) {
        theVolatile = 0;
        return ref;
    }

    public static void reachabilityFence(Object ref) {
        if (ref != null) {
            synchronized (ref) {
            }
        }
    }
}