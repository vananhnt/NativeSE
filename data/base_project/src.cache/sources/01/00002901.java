package libcore.io;

/* loaded from: StructUcred.class */
public final class StructUcred {
    public final int pid;
    public final int uid;
    public final int gid;

    private StructUcred(int pid, int uid, int gid) {
        this.pid = pid;
        this.uid = uid;
        this.gid = gid;
    }

    public String toString() {
        return "StructUcred[pid=" + this.pid + ",uid=" + this.uid + ",gid=" + this.gid + "]";
    }
}