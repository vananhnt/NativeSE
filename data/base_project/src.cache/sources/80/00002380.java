package java.net;

import java.nio.charset.StandardCharsets;
import libcore.io.OsConstants;

/* loaded from: InetUnixAddress.class */
public final class InetUnixAddress extends InetAddress {
    public InetUnixAddress(String path) {
        this(path.getBytes(StandardCharsets.UTF_8));
    }

    public InetUnixAddress(byte[] path) {
        super(OsConstants.AF_UNIX, path, null);
    }

    @Override // java.net.InetAddress
    public String toString() {
        return "InetUnixAddress[" + new String(this.ipaddress, StandardCharsets.UTF_8) + "]";
    }
}