package libcore.io;

/* loaded from: Libcore.class */
public final class Libcore {
    public static Os os = new BlockGuardOs(new Posix());

    private Libcore() {
    }
}