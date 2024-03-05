package libcore.io;

/* loaded from: StructPasswd.class */
public final class StructPasswd {
    public String pw_name;
    public int pw_uid;
    public int pw_gid;
    public String pw_dir;
    public String pw_shell;

    public StructPasswd(String pw_name, int pw_uid, int pw_gid, String pw_dir, String pw_shell) {
        this.pw_name = pw_name;
        this.pw_uid = pw_uid;
        this.pw_gid = pw_gid;
        this.pw_dir = pw_dir;
        this.pw_shell = pw_shell;
    }
}