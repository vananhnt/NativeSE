package libcore.io;

/* loaded from: StructTimeval.class */
public final class StructTimeval {
    public final long tv_sec;
    public final long tv_usec;

    private StructTimeval(long tv_sec, long tv_usec) {
        this.tv_sec = tv_sec;
        this.tv_usec = tv_usec;
    }

    public static StructTimeval fromMillis(long millis) {
        long tv_sec = millis / 1000;
        long tv_usec = (millis - (tv_sec * 1000)) * 1000;
        return new StructTimeval(tv_sec, tv_usec);
    }

    public long toMillis() {
        return (this.tv_sec * 1000) + (this.tv_usec / 1000);
    }

    public String toString() {
        return "StructTimeval[tv_sec=" + this.tv_sec + ",tv_usec=" + this.tv_usec + "]";
    }
}