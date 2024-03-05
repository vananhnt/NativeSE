package libcore.io;

/* loaded from: StructUtsname.class */
public final class StructUtsname {
    public final String sysname;
    public final String nodename;
    public final String release;
    public final String version;
    public final String machine;

    StructUtsname(String sysname, String nodename, String release, String version, String machine) {
        this.sysname = sysname;
        this.nodename = nodename;
        this.release = release;
        this.version = version;
        this.machine = machine;
    }
}