package libcore.io;

import java.net.InetAddress;

/* loaded from: StructGroupReq.class */
public final class StructGroupReq {
    public final int gr_interface;
    public final InetAddress gr_group;

    public StructGroupReq(int gr_interface, InetAddress gr_group) {
        this.gr_interface = gr_interface;
        this.gr_group = gr_group;
    }

    public String toString() {
        return "StructGroupReq[gr_interface=" + this.gr_interface + ",gr_group=" + this.gr_group + "]";
    }
}